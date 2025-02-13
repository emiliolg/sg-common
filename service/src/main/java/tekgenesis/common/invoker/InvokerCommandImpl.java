
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.Colls;
import tekgenesis.common.collections.Seq;
import tekgenesis.common.command.AbstractCommand;
import tekgenesis.common.core.Option;
import tekgenesis.common.invoker.exception.InvokerApplicationException;
import tekgenesis.common.invoker.exception.InvokerResponseException;
import tekgenesis.common.service.Headers;
import tekgenesis.common.service.Status;
import tekgenesis.common.service.cookie.Cookie;

import static tekgenesis.common.core.Option.*;
import static tekgenesis.common.core.Option.some;
import static tekgenesis.common.service.Status.OK;

/**
 * Wraps {@link Invocation} into {@link InvokerCommand}.
 */
class InvokerCommandImpl<T> extends AbstractCommand<HttpInvokerResult<T>> implements InvokerCommand<T> {

    //~ Instance Fields ..............................................................................................................................

    @Nullable private FallbackFunction fallback;

    @NotNull private final Invocation<T> invocation;

    @Nullable private HttpInvoker invoker;

    //~ Constructors .................................................................................................................................

    InvokerCommandImpl(@NotNull final Invocation<T> invocation) {
        this.invocation = invocation;
        fallback        = null;
        invoker         = null;
    }

    //~ Methods ......................................................................................................................................

    @Override public void acceptEither(@NotNull Consumer<T> result, @NotNull Consumer<InvokerApplicationException> exception) {
        try {
            result.accept(get());
        }
        catch (final InvokerApplicationException e) {
            exception.accept(e);
        }
    }

    @Override public T get() {
        return execute().get();
    }

    @Override public <R> R mapEither(@NotNull Function<T, R> mapResult, @NotNull Function<InvokerApplicationException, R> mapException) {
        try {
            return mapResult.apply(get());
        }
        catch (final InvokerApplicationException e) {
            return mapException.apply(e);
        }
    }

    @Override public InvokerCommand<T> onErrorFallback(@NotNull final Function<Throwable, T> f) {
        getFallback().onError(f);
        return this;
    }

    @Override public InvokerCommand<T> onExceptionFallback(@NotNull final Function<InvokerApplicationException, T> f) {
        getFallback().onException(f);
        return this;
    }

    @Override public InvokerCommand<T> withConnectTimeout(int t) {
        invocation.withConnectTimeout(t);
        return this;
    }

    @Override public InvokerCommand<T> withInvocationKey(String key) {
        invocation.setKey(key);
        return this;
    }

    @Override public InvokerCommand<T> withReadTimeout(int t) {
        invocation.withReadTimeout(t);
        return this;
    }

    @Override public Headers getHeaders() {
        return silentExecute().getHeaders();
    }

    @Override public Status getStatus() {
        return silentExecute().getStatus();
    }

    @Override protected HttpInvokerResult<T> run() {
        return invocation.invokeUsing((HttpInvokerImpl) getInvoker());
    }

    @Override protected String getThreadPoolKey() {
        return "InvokerCommand";
    }

    /** Specify {@link HttpInvoker invoker} to perform invocation command with. */
    InvokerCommand<T> withInvoker(@NotNull HttpInvoker i) {
        invoker = i;
        return this;
    }

    @NotNull
    @SuppressWarnings("DuplicateStringLiteralInspection")
    HttpInvoker getInvoker() {
        if (invoker == null) throw new IllegalStateException("Invoker hasn't been specified!");
        return invoker;
    }

    private HttpInvokerResult<T> silentExecute() {
        try {
            return execute();
        }
        catch (final InvokerResponseException e) {
            return new StubHttpResult<>(e, getInvoker());
        }
    }

    @NotNull private FallbackFunction getFallback() {
        if (fallback == null) {
            fallback = new FallbackFunction();
            withFallback(fallback);
        }
        return fallback;
    }

    //~ Inner Classes ................................................................................................................................

    private class FallbackFunction implements Function<Throwable, HttpInvokerResult<T>> {
        Option<Function<Throwable, T>>                   errors;
        Option<Function<InvokerApplicationException, T>> exceptions;

        private FallbackFunction() {
            errors     = empty();
            exceptions = empty();
        }

        @Override public HttpInvokerResult<T> apply(Throwable throwable) {
            final T value;
            if (exceptions.isPresent() && throwable instanceof InvokerApplicationException)
                value = exceptions.get().apply((InvokerApplicationException) throwable);
            else if (errors.isPresent()) value = errors.get().apply(throwable);
            else throw decompose(asException(throwable));
            return new FallbackHttpResult<>(value, getInvoker());
        }

        private Exception asException(Throwable throwable) {
            return throwable instanceof Exception ? ((Exception) throwable) : new RuntimeException(throwable);
        }

        private void onError(Function<Throwable, T> f) {
            errors = some(f);
        }

        private void onException(Function<InvokerApplicationException, T> f) {
            exceptions = some(f);
        }
    }

    private static class FallbackHttpResult<T> implements HttpInvokerResult<T> {
        @NotNull private final HttpInvoker invoker;

        @NotNull private final T value;

        private FallbackHttpResult(@NotNull final T value, @NotNull HttpInvoker invoker) {
            this.value   = value;
            this.invoker = invoker;
        }

        @Override public T get() {
            return value;
        }

        @Override public Seq<Cookie> getCookies() {
            throw unsupported();
        }

        @Override public Headers getHeaders() {
            throw unsupported();
        }

        @NotNull @Override public HttpInvoker getInvoker() {
            return invoker;
        }

        @Override public Status getStatus() {
            return OK;
        }

        private UnsupportedOperationException unsupported() {
            return new UnsupportedOperationException("Fallback result implementation.");
        }
    }

    private static class StubHttpResult<T> implements HttpInvokerResult<T> {
        private final Headers headers;

        private final HttpInvoker invoker;
        private final Status      status;

        private StubHttpResult(@NotNull InvokerResponseException e, @NotNull HttpInvoker invoker) {
            this.invoker = invoker;
            headers      = e.getHeaders();
            status       = e.getStatus();
        }

        @Override public T get() {
            throw new NoSuchElementException("Stub result implementation for status: " + status);
        }

        @Override public Seq<Cookie> getCookies() {
            return Colls.emptyIterable();
        }

        @Override public Headers getHeaders() {
            return headers;
        }

        @Override public HttpInvoker getInvoker() {
            return invoker;
        }

        @Override public Status getStatus() {
            return status;
        }
    }
}  // end class InvokerCommandImpl
