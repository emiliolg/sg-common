
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
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.MultiMap;
import tekgenesis.common.core.Option;
import tekgenesis.common.core.Times;
import tekgenesis.common.invoker.error.InvokerErrorHandler;
import tekgenesis.common.invoker.exception.InvokerInvocationException;
import tekgenesis.common.invoker.metric.InvocationKeyGenerator;
import tekgenesis.common.invoker.metric.InvocationKeyGenerator.CamelCaseKeyGenerator;
import tekgenesis.common.media.MediaType;
import tekgenesis.common.service.*;
import tekgenesis.common.service.cookie.Cookie;
import tekgenesis.common.service.cookie.Cookies;
import tekgenesis.common.service.etl.HtmlReadMessageConverter;
import tekgenesis.common.service.etl.MessageConverter;

import static tekgenesis.common.Predefined.isNotEmpty;
import static tekgenesis.common.core.Option.some;
import static tekgenesis.common.service.Method.GET;
import static tekgenesis.common.service.Parameters.mapToQueryString;
import static tekgenesis.common.service.Status.REQUEST_URI_TOO_LONG;
import static tekgenesis.common.service.etl.MessageConverters.*;

/**
 * Invoker over HTTP implementation.
 */
class HttpInvokerImpl implements HttpInvoker, InvokerCommandFactory {

    //~ Instance Fields ..............................................................................................................................

    String serverUrl;

    private int connectTimeout;

    private final List<MessageConverter<?>> converters   = new ArrayList<>();
    @NotNull private final List<Cookie>     cookies;
    private Option<InvokerErrorHandler>     errorHandler;
    @NotNull private InvokerCommandFactory  factory;

    @NotNull private final Headers          headers;
    @NotNull private InvocationKeyGenerator keyGenerator = NO_KEY_GENERATOR;
    private final List<MessageModifier>     modifiers    = new ArrayList<>();
    private int                             readTimeout;
    private Option<String>                  threadPoolKey;

    //~ Constructors .................................................................................................................................

    HttpInvokerImpl() {
        serverUrl      = "";
        connectTimeout = (int) Times.MILLIS_MINUTE;
        // noinspection MagicNumber
        readTimeout = (int) (5 * Times.MILLIS_MINUTE);

        headers = new Headers();
        cookies = new ArrayList<>();

        // Add default converters
        withByteConverter(this::withConverter);
        withXmlConverter(this::withConverter);
        withStringConverter(this::withConverter);
        withFormConverter(this::withConverter);
        withBasicTypeConverter(this::withConverter);
        withJsonConverter(this::withConverter);

        // Add static html read only message converter
        withConverter(new HtmlReadMessageConverter());

        // Use default InvokerCommand pool
        threadPoolKey = Option.empty();

        // No error handler by default
        errorHandler = Option.empty();

        // Specify self as default command factory
        factory = this;
    }

    //~ Methods ......................................................................................................................................

    @NotNull @Override public HttpInvoker accept(@NotNull MediaType... mimes) {
        headers.setAccept(mimes);
        return this;
    }

    @NotNull @Override public HttpInvoker acceptLanguage(@NotNull Locale locale) {
        headers.setAcceptLanguage(locale);
        return this;
    }

    @Override public <T> InvokerCommandImpl<T> command(Invocation<T> invocation) {
        return new InvokerCommandImpl<T>(invocation) {
            @Override protected String getThreadPoolKey() {
                return threadPoolKey.orElse(super.getThreadPoolKey());
            }
        };
    }

    @NotNull @Override public HttpInvoker contentType(@NotNull MediaType mime) {
        headers.setContentType(mime);
        return this;
    }

    @NotNull @Override public HttpInvoker header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    /** Return a new resource to be retrieved in specified path. */
    @Override public PathResource<?> resource(@NotNull String path) {
        return createHttpResource(path, GET);
    }

    /** Return a new resource to be retrieved in specified path, with specified method. */
    @Override public CallResource<?> resource(@NotNull Call call) {
        return createHttpResource(call.getUrl(), call.getMethod());
    }

    @Override public String toString() {
        return serverUrl;
    }

    /**
     * Specify command pool for given invoker. Invoker will use default InvokerCommand pool if not
     * specified.
     */
    @Override public HttpInvoker withCommandPool(@NotNull String commandPoolName) {
        threadPoolKey = some(commandPoolName);
        return this;
    }

    /**
     * Timeout value, in milliseconds, to be used when opening a communications link to a resource.
     * Zero value implies that the option is disabled (i.e., timeout of infinity). Default is one
     * minute.
     */
    @Override public HttpInvoker withConnectTimeout(int timeout) {
        connectTimeout = timeout;
        return this;
    }

    /** Add given {@link MessageConverter converter} to invoker. */
    @Override public HttpInvoker withConverter(@NotNull final MessageConverter<?> converter) {
        converters.add(0, converter);
        return this;
    }

    /** Add cookie to client invocation. */
    @NotNull @Override public HttpInvoker withCookie(@NotNull String name, @NotNull String value) {
        cookies.add(Cookies.create(name, value));
        return this;
    }

    /** Set global {@link InvokerErrorHandler error handler}. May be overridden by resource. */
    @Override public HttpInvoker withErrorHandler(@NotNull final InvokerErrorHandler handler) {
        errorHandler = some(handler);
        return this;
    }

    /** Add Gzip decompression {@link MessageModifier modifier} to invoker. */
    @Override public HttpInvoker withGzipDecompression() {
        withModifier(new GzipDecompressingMessageModifier());
        return this;
    }

    @Override public HttpInvoker withMetrics() {
        return withMetrics(new CamelCaseKeyGenerator());
    }

    @Override public HttpInvoker withMetrics(@NotNull InvocationKeyGenerator generator) {
        keyGenerator = generator;
        return this;
    }

    /** Add given {@link MessageModifier modifier} to invoker. */
    @Override public HttpInvoker withModifier(@NotNull final MessageModifier modifier) {
        modifiers.add(modifier);
        return this;
    }

    /**
     * Timeout value, in milliseconds, for reading from input stream when a connection is
     * established. Zero value implies that the option is disabled (i.e., timeout of infinity).
     * Default is five minutes.
     */
    @Override public HttpInvoker withReadTimeout(int timeout) {
        readTimeout = timeout;
        return this;
    }

    /** Application token for X-Tek-app-Token. */
    @Override public HttpInvoker withSgAppToken(@NotNull String token) {
        header(HeaderNames.TEK_APP_TOKEN, token);
        return this;
    }

    @Override public HttpInvoker withSurrogate(@NotNull String surrogate) {
        header(HeaderNames.TEK_APP_TOKEN_SURROGATE, surrogate);
        return this;
    }

    void applyModifiers(HttpConnectionRequest request) {
        for (final MessageModifier modifier : modifiers)
            modifier.modify(request);
    }

    void applyModifiers(HttpConnectionResponse response) {
        for (final MessageModifier modifier : modifiers)
            modifier.modify(response);
    }

    URI createUri(@NotNull String path, @NotNull MultiMap<String, String> parameters)
        throws URISyntaxException
    {
        final String queryString = mapToQueryString(parameters);
        String       result      = isNotEmpty(serverUrl) ? serverUrl + URL_SEPARATOR : "";
        result += path;
        result = result + (isNotEmpty(queryString) ? "?" + queryString : "");

        if (result.length() > MAX_URL_SIZE)
            throw new InvokerInvocationException(REQUEST_URI_TOO_LONG, headers, String.format("Requested uri is too large: '%s'", result));

        return new URI(result).normalize();
    }

    @Nullable <T> T doExtract(@Nullable InboundMessageReader<T> extractor, @NotNull HttpConnectionResponse response) {
        return extractor != null ? extractor.read(response, converters) : null;
    }

    void doPrepare(@Nullable Object payload, @NotNull HttpConnectionRequest request)
        throws IOException
    {
        if (payload != null) new OutboundMessageWriter().write(request, converters, payload);
        else request.writeHeaders();
    }

    HttpConnectionResponse execute(@NotNull HttpConnectionRequest request)
        throws IOException
    {
        final HttpURLConnection connection = request.getConnection();
        connection.connect();
        return new HttpConnectionResponse(connection);
    }

    /** Set internal {@link InvokerCommandFactory command factory}. */
    HttpInvoker withCommandFactory(@NotNull final InvokerCommandFactory f) {
        factory = f;
        return this;
    }

    /** Specify default server. The server url -> protocol://host[:port] */
    HttpInvoker withServer(@NotNull String server) {
        serverUrl = server;
        return this;
    }

    int getConnectTimeout() {
        return connectTimeout;
    }

    boolean isMetricsEnabled() {
        return keyGenerator != NO_KEY_GENERATOR;
    }

    Option<InvokerErrorHandler> getErrorHandler() {
        return errorHandler;
    }

    @NotNull InvocationKeyGenerator getInvocationKeyGenerator() {
        return keyGenerator;
    }

    Iterable<MessageConverter<?>> getMessageConverters() {
        return converters;
    }

    int getReadTimeout() {
        return readTimeout;
    }

    /** Create resource copying all headers specified on {@link HttpInvoker}. */
    private HttpResourceImpl createHttpResource(@NotNull String path, @NotNull Method method) {
        final HttpResourceImpl resource = new HttpResourceImpl(this, factory, keyGenerator, path, method);
        resource.getHeaders().putAll(headers);
        resource.withCookies(cookies);
        return resource;
    }

    //~ Static Fields ................................................................................................................................

    private static final char URL_SEPARATOR = '/';

    private static final int MAX_URL_SIZE = 2048;

    private static final InvocationKeyGenerator NO_KEY_GENERATOR = (server, path, method) -> "";
}  // end class HttpInvokerImpl
