
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.MultiMap;
import tekgenesis.common.invoker.exception.InvokerConnectionException;
import tekgenesis.common.invoker.exception.InvokerInvocationException;
import tekgenesis.common.invoker.metric.InvocationKeyGenerator;
import tekgenesis.common.media.MediaType;
import tekgenesis.common.service.Headers;
import tekgenesis.common.service.InboundMessageReader;
import tekgenesis.common.service.Method;
import tekgenesis.common.service.cookie.Cookie;
import tekgenesis.common.service.cookie.Cookies;

import static java.util.Map.Entry;

import static tekgenesis.common.invoker.Invocation.invocation;
import static tekgenesis.common.service.Method.*;

/**
 * Represents an HTTP resource to be retrieved.
 */
class HttpResourceImpl implements PathResource<HttpResourceImpl>, CallResource<HttpResourceImpl> {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final List<Cookie> cookies;

    @NotNull private final InvokerCommandFactory factory;

    @NotNull private final Headers         headers;
    @NotNull private final HttpInvokerImpl invoker;

    @NotNull private final InvocationKeyGenerator   keyGenerator;
    @NotNull private final Method                   method;
    @NotNull private final MultiMap<String, String> parameters;
    @NotNull private final String                   path;

    //~ Constructors .................................................................................................................................

    HttpResourceImpl(@NotNull HttpInvokerImpl invoker, @NotNull InvokerCommandFactory factory, @NotNull InvocationKeyGenerator keyGenerator,
                     @NotNull String path, @NotNull Method method) {
        this.invoker      = invoker;
        this.factory      = factory;
        this.path         = path;
        this.method       = method;
        headers           = new Headers();
        cookies           = new ArrayList<>();
        parameters        = MultiMap.createLinkedMultiMap();
        this.keyGenerator = keyGenerator;
    }

    //~ Methods ......................................................................................................................................

    @NotNull @Override public HttpResourceImpl accept(@NotNull MediaType... mimes) {
        headers.setAccept(mimes);
        return this;
    }

    @NotNull @Override public HttpResourceImpl acceptLanguage(@NotNull Locale locale) {
        headers.setAcceptLanguage(locale);
        return this;
    }

    @NotNull @Override public HttpResourceImpl contentType(@NotNull MediaType mime) {
        headers.setContentType(mime);
        return this;
    }

    @Override public void delete()
        throws InvokerConnectionException, InvokerInvocationException
    {
        invoke(DELETE).execute();
    }

    @Override public <T> T delete(@NotNull Class<T> responseType)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return invoke(DELETE, responseType).execute().get();
    }

    @Override public <T> T delete(@NotNull GenericType<T> genericType)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return invoke(DELETE, genericType).execute().get();
    }

    @Override public <T> T get(@NotNull Class<T> responseType)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return invoke(GET, responseType).execute().get();
    }

    @Override public <T> T get(@NotNull GenericType<T> genericType)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return invoke(GET, genericType).execute().get();
    }

    @Override public Headers head()
        throws InvokerConnectionException, InvokerInvocationException
    {
        return invoke(HEAD).execute().getHeaders();
    }

    @NotNull @Override public HttpResourceImpl header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    @Override public InvokerCommand<?> invoke()
        throws InvokerConnectionException, InvokerInvocationException
    {
        return invoke(method);
    }

    @Override public InvokerCommand<?> invoke(@NotNull Method m)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return handle(m, null, null);
    }

    @Override public InvokerCommand<?> invoke(@NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return invoke(method, payload);
    }

    @Override public <T> InvokerCommand<T> invoke(@NotNull Class<T> responseType)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return invoke(method, responseType);
    }

    @Override public <T> InvokerCommand<T> invoke(@NotNull GenericType<T> genericType)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return invoke(method, genericType);
    }

    @Override public InvokerCommand<?> invoke(@NotNull Method m, @NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return handle(m, payload, null);
    }

    @Override public <T> InvokerCommand<T> invoke(@NotNull Method m, @NotNull Class<T> responseType)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return handle(m, null, new InboundMessageReader<>(responseType));
    }

    @Override public <T> InvokerCommand<T> invoke(@NotNull Method m, @NotNull GenericType<T> genericType)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return handle(m, null, extractorFor(genericType));
    }

    @Override public <T> InvokerCommand<T> invoke(@NotNull Class<T> responseType, @NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return invoke(method, responseType, payload);
    }

    @Override public <T> InvokerCommand<T> invoke(@NotNull GenericType<T> genericType, @NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return invoke(method, genericType, payload);
    }

    @Override public <T> InvokerCommand<T> invoke(@NotNull Method m, @NotNull Class<T> responseType, @NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return handle(m, payload, new InboundMessageReader<>(responseType));
    }

    @Override public <T> InvokerCommand<T> invoke(@NotNull Method m, @NotNull GenericType<T> genericType, @NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return handle(m, payload, extractorFor(genericType));
    }

    @Override public HttpResourceImpl param(@NotNull String parameter, @NotNull String value) {
        parameters.put(parameter, value);
        return this;
    }

    @Override public HttpResourceImpl param(@NotNull String parameter, @NotNull Iterable<String> values) {
        parameters.putAll(parameter, values);
        return this;
    }

    @Override public HttpResourceImpl params(@NotNull Map<String, Iterable<String>> params) {
        for (final Entry<String, Iterable<String>> entry : params.entrySet())
            param(entry.getKey(), entry.getValue());
        return this;
    }

    @Override public HttpResourceImpl params(@NotNull MultiMap<String, String> params) {
        for (final Entry<String, Collection<String>> entry : params.asMap().entrySet())
            param(entry.getKey(), entry.getValue());
        return this;
    }

    @Override public void post()
        throws InvokerConnectionException, InvokerInvocationException
    {
        invoke(POST).execute();
    }

    @Override public void post(@NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException
    {
        invoke(POST, payload).execute();
    }

    @Override public <T> T post(@NotNull Class<T> responseType)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return invoke(POST, responseType).execute().get();
    }

    @Override public <T> T post(@NotNull GenericType<T> genericType)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return invoke(POST, genericType).execute().get();
    }

    @Override public <T> T post(@NotNull Class<T> responseType, @NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return invoke(POST, responseType, payload).execute().get();
    }

    @Override public <T> T post(@NotNull GenericType<T> genericType, @NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return invoke(POST, genericType, payload).execute().get();
    }

    @Override public void put()
        throws InvokerConnectionException, InvokerInvocationException
    {
        invoke(PUT).execute();
    }

    @Override public void put(@NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException
    {
        invoke(PUT, payload).execute();
    }

    @Override public <T> T put(@NotNull Class<T> responseType)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return invoke(PUT, responseType).execute().get();
    }

    @Override public <T> T put(@NotNull GenericType<T> genericType)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return invoke(PUT, genericType).execute().get();
    }

    @Override public <T> T put(@NotNull Class<T> responseType, @NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return invoke(PUT, responseType, payload).execute().get();
    }

    @Override public <T> T put(@NotNull GenericType<T> genericType, @NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException
    {
        return invoke(PUT, genericType, payload).execute().get();
    }

    @NotNull @Override public HttpResourceImpl withCookie(@NotNull String name, @NotNull String value) {
        cookies.add(Cookies.create(name, value));
        return this;
    }

    @NotNull @Override public Headers getHeaders() {
        return headers;
    }

    void withCookies(@NotNull final List<Cookie> cs) {
        cookies.addAll(cs);
    }

    private <T> InboundMessageReader<T> extractorFor(GenericType<T> genericType) {
        return new InboundMessageReader<>(genericType.getRaw(), genericType.getType());
    }

    private <T> InvokerCommand<T> handle(@NotNull Method m, @Nullable Object payload, @Nullable InboundMessageReader<T> extractor) {
        final Invocation<T> invocation = invocation(path, m, headers, cookies, parameters, payload, extractor, keyGenerator);
        return factory.command(invocation).withInvoker(invoker);
    }
}  // end class HttpResourceImpl
