
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.*;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.MultiMap;
import tekgenesis.common.collections.Seq;
import tekgenesis.common.command.exception.CommandInvocationException;
import tekgenesis.common.core.Option;
import tekgenesis.common.core.enumeration.Enumerations;
import tekgenesis.common.invoker.error.InvokerErrorHandler;
import tekgenesis.common.invoker.exception.InvokerApplicationException;
import tekgenesis.common.invoker.exception.InvokerConnectionException;
import tekgenesis.common.invoker.exception.InvokerInvocationException;
import tekgenesis.common.invoker.metric.InvocationKeyGenerator;
import tekgenesis.common.invoker.metric.InvocationMetrics;
import tekgenesis.common.service.*;
import tekgenesis.common.service.cookie.Cookie;
import tekgenesis.common.util.Files;

import static java.lang.System.currentTimeMillis;

import static tekgenesis.common.Predefined.option;
import static tekgenesis.common.core.Option.some;
import static tekgenesis.common.json.JsonMapping.shared;
import static tekgenesis.common.service.HeaderNames.X_APPLICATION_EXCEPTION;
import static tekgenesis.common.service.Method.*;

/**
 * Represents an invocation to be eventually executed on an {@link HttpInvoker invoker}.
 */
class Invocation<T> {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private Option<Integer>     connectTimeout;
    @NotNull private final List<Cookie>  cookies;

    @Nullable private final InboundMessageReader<T> extractor;
    @NotNull private final Headers                  headers;
    @NotNull private Option<String>                 key;
    @NotNull private final InvocationKeyGenerator   keyGenerator;
    @NotNull private final Method                   method;
    @NotNull private final MultiMap<String, String> parameters;

    @NotNull private final String    path;
    @Nullable private final Object   payload;
    @NotNull private Option<Integer> readTimeout;

    //~ Constructors .................................................................................................................................

    @SuppressWarnings("ConstructorWithTooManyParameters")
    private Invocation(@NotNull String path, @NotNull Method method, @NotNull Headers headers, @NotNull List<Cookie> cookies,
                       @NotNull MultiMap<String, String> parameters, @Nullable Object payload, @Nullable InboundMessageReader<T> extractor,
                       @NotNull InvocationKeyGenerator keyGenerator) {
        this.path         = path;
        this.method       = method;
        this.headers      = headers;
        this.cookies      = cookies;
        this.parameters   = parameters;
        this.payload      = payload;
        this.extractor    = extractor;
        this.keyGenerator = keyGenerator;
        connectTimeout    = Option.empty();
        readTimeout       = Option.empty();
        key               = Option.empty();
    }

    //~ Methods ......................................................................................................................................

    /** Execute invocation with given invoker. */
    HttpInvokerResult<T> invokeUsing(@NotNull HttpInvokerImpl invoker) {
        return invoke(invoker);
    }

    Invocation<T> withConnectTimeout(int t) {
        connectTimeout = some(t);
        return this;
    }

    Invocation<T> withReadTimeout(int t) {
        readTimeout = some(t);
        return this;
    }

    void setKey(@NotNull String key) {
        this.key = some(key);
    }

    @NotNull String getPath() {
        return path;
    }

    private InvokerApplicationException applicationException(@NotNull Status status, @NotNull Headers responseHeaders, @Nullable InputStream data) {
        try {
            final ApplicationExceptionResult result = shared().readValue(data, ApplicationExceptionResult.class);
            final Enum<?>                    e      = getEnumFromResult(result);
            return new InvokerApplicationException(status, responseHeaders, e, result.getMsg());
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private HttpURLConnection createConnection(@NotNull URI uri, @NotNull HttpInvokerImpl invoker)
        throws IOException
    {
        final HttpURLConnection connection = (HttpURLConnection) toUrl(uri).openConnection();

        final int ct = connectTimeout.orElse(invoker.getConnectTimeout());
        if (ct >= 0) connection.setConnectTimeout(ct);
        final int rt = readTimeout.orElse(invoker.getReadTimeout());
        if (rt >= 0) connection.setReadTimeout(rt);

        connection.setDoInput(true);

        if (GET == method) connection.setInstanceFollowRedirects(true);
        else connection.setInstanceFollowRedirects(false);

        if (PUT == method || POST == method) connection.setDoOutput(true);
        else connection.setDoOutput(false);

        connection.setRequestMethod(method.name());

        return connection;
    }

    /**
     * Exception creation that returns: -> {@link InvokerApplicationException application exception}
     * for handler exception results -> {@link InvokerInvocationException invocation exception} for
     * handler unsuccessful results
     */
    private CommandInvocationException createInvocationException(HttpInvokerImpl invoker, HttpConnectionResponse response, Status status)
        throws IOException
    {
        for (final InvokerErrorHandler handler : invoker.getErrorHandler())
            handler.handle(status, response.getHeaders(), response.getContent());

        final Option<String> header = response.getHeaders().getFirst(X_APPLICATION_EXCEPTION);
        if (header.isPresent()) return applicationException(status, response.getHeaders(), response.getContent());
        else return invocationException(status, response.getHeaders(), response.getContent());
    }

    /** Create http url connection request for given uri and method. */
    private HttpConnectionRequest createRequest(@NotNull URI uri, @NotNull HttpInvokerImpl invoker)
        throws IOException
    {
        final HttpURLConnection connection = createConnection(uri, invoker);
        return new HttpConnectionRequest(uri, method, headers, cookies, connection);
    }

    private InvokerInvocationException invocationException(@NotNull Status status, @NotNull Headers responseHeaders, @NotNull InputStream data) {
        return new InvokerInvocationException(status, responseHeaders, data);
    }

    private HttpInvokerResult<T> invoke(HttpInvokerImpl invoker) {
        HttpConnectionResponse response = null;

        final Option<InvocationMetrics> metrics        = option(getMetricsInstance(invoker));
        final boolean                   metricsEnabled = invoker.isMetricsEnabled();
        try {
            final URI                   uri     = invoker.createUri(path, parameters);
            final HttpConnectionRequest request = createRequest(uri, invoker);

            invoker.applyModifiers(request);

            invoker.doPrepare(payload, request);

            final long invocationTime = currentTimeMillis();

            response = invoker.execute(request);

            invoker.applyModifiers(response);

            final Status status = response.getStatus();

            if (status.isSuccessful()) {
                if (metricsEnabled) registerSuccessExecution(metrics.get(), invocationTime);
                final T result = invoker.doExtract(extractor, response);
                if (result != null) return new SomeInvocationResult<>(invoker, response, result);
                else return new EmptyInvocationResult<>(invoker, response);
            }
            else {
                if (metricsEnabled) registerErrorExecution(metrics.get());
                throw createInvocationException(invoker, response, status);
            }
        }
        catch (IOException | URISyntaxException exception) {
            if (metricsEnabled) registerExceptionExecution(metrics.get(), exception);
            throw new InvokerConnectionException(exception);
        }
        finally {
            if (response != null) Files.close(response);
        }
    }  // end method invoke

    private void registerErrorExecution(@NotNull InvocationMetrics metrics) {
        metrics.markError();
    }

    private void registerExceptionExecution(@NotNull InvocationMetrics metrics, Exception exception) {
        if (exception instanceof SocketTimeoutException) metrics.markTimeout();
        else metrics.markFailure();
    }

    private void registerSuccessExecution(@NotNull InvocationMetrics metrics, long invocationStartTime) {
        metrics.addInvocationExecutionTime(currentTimeMillis() - invocationStartTime);
        metrics.markSuccess();
    }

    private URL toUrl(URI requestURI) {
        try {
            return requestURI.toURL();
        }
        catch (MalformedURLException | IllegalArgumentException e) {
            throw new InvokerConnectionException(e);
        }
    }

    private <E extends Enum<E>> Enum<?> getEnumFromResult(ApplicationExceptionResult result) {
        return Enumerations.<E>valueOf(result.getEnumClass(), result.getEnumName());
    }

    @Nullable private InvocationMetrics getMetricsInstance(@NotNull HttpInvokerImpl invoker) {
        if (invoker.isMetricsEnabled()) {
            final String metricsKey = key.orElse(keyGenerator.key(invoker.serverUrl, path, method));
            return InvocationMetrics.getOrCreateInstance(metricsKey, invoker.serverUrl);
        }
        return null;
    }

    //~ Methods ......................................................................................................................................

    static <T> Invocation<T> invocation(@NotNull String path, @NotNull Method method, @NotNull Headers headers, @NotNull List<Cookie> cookies,
                                        @NotNull MultiMap<String, String> parameters, @Nullable Object payload,
                                        @Nullable InboundMessageReader<T> extractor, @NotNull InvocationKeyGenerator keyGenerator) {
        return new Invocation<>(path, method, headers, cookies, parameters, payload, extractor, keyGenerator);
    }

    //~ Inner Classes ................................................................................................................................

    private abstract static class AbstractInvocationResult<T> implements HttpInvokerResult<T> {
        private final HttpInvoker            invoker;
        private final HttpConnectionResponse response;

        private AbstractInvocationResult(HttpInvoker invoker, HttpConnectionResponse response) {
            this.invoker  = invoker;
            this.response = response;
        }

        @Override public Seq<Cookie> getCookies() {
            return response.getCookies();
        }

        @Override public Headers getHeaders() {
            return response.getHeaders();
        }

        @Override public HttpInvoker getInvoker() {
            return invoker;
        }

        @Override public Status getStatus() {
            try {
                return response.getStatus();
            }
            catch (final IOException e) {
                throw new InvokerConnectionException(e);
            }
        }
    }

    private static class EmptyInvocationResult<T> extends AbstractInvocationResult<T> {
        private EmptyInvocationResult(HttpInvoker invoker, @NotNull HttpConnectionResponse response) {
            super(invoker, response);
        }

        @Nullable @Override public T get() {
            return null;
        }
    }

    private static class SomeInvocationResult<T> extends AbstractInvocationResult<T> {
        @NotNull private final T body;

        private SomeInvocationResult(@NotNull HttpInvokerImpl invoker, @NotNull HttpConnectionResponse response, @NotNull T body) {
            super(invoker, response);
            this.body = body;
        }

        @Override public T get() {
            return body;
        }
    }
}  // end class Invocation
