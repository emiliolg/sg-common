
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import rx.Observable;
import rx.functions.Func1;

import tekgenesis.common.core.Option;
import tekgenesis.common.invoker.error.InvokerErrorHandler;
import tekgenesis.common.invoker.exception.InvokerConnectionException;
import tekgenesis.common.invoker.exception.InvokerInvocationException;
import tekgenesis.common.invoker.exception.NoInvokerAvailable;
import tekgenesis.common.invoker.metric.InvocationKeyGenerator;
import tekgenesis.common.logging.Logger;
import tekgenesis.common.media.MediaType;
import tekgenesis.common.service.Call;
import tekgenesis.common.service.cookie.Cookie;
import tekgenesis.common.service.cookie.Cookies;
import tekgenesis.common.service.cookie.MutableCookie;
import tekgenesis.common.service.etl.MessageConverter;

import static java.lang.String.format;

import static tekgenesis.common.core.Option.some;
import static tekgenesis.common.invoker.HttpInvokers.invoker;
import static tekgenesis.common.service.cookie.Cookies.hash;

/**
 * Multi host http invoker supporting basic strategies.
 */
class MultiHostHttpInvoker implements HttpInvoker, InvokerCommandFactory {

    //~ Instance Fields ..............................................................................................................................

    private final List<HttpInvoker> invokers;
    private final MultiHostStrategy strategy;
    private Option<String>          threadPoolKey;

    //~ Constructors .................................................................................................................................

    MultiHostHttpInvoker(Strategy strategy, @NotNull final String... servers) {
        invokers      = new ArrayList<>(servers.length);
        threadPoolKey = Option.empty();
        // Create and configure invokers for given servers.
        for (final String s : servers) {
            final HttpInvokerImpl host = (HttpInvokerImpl) invoker(s);
            host.withCommandFactory(this);
            invokers.add(host);
        }
        this.strategy = strategy.create(invokers);
    }

    //~ Methods ......................................................................................................................................

    @NotNull @Override public HttpInvoker accept(@NotNull final MediaType... mimes) {
        forEach(invoker -> invoker.accept(mimes));
        return this;
    }

    @NotNull @Override public HttpInvoker acceptLanguage(@NotNull final Locale locale) {
        forEach(invoker -> invoker.acceptLanguage(locale));
        return this;
    }

    @Override public <T> InvokerCommandImpl<T> command(Invocation<T> invocation) {
        return new MultiHostInvokerCommandImpl<>(invocation);
    }

    @NotNull @Override public HttpInvoker contentType(@NotNull final MediaType mime) {
        forEach(invoker -> invoker.contentType(mime));
        return this;
    }

    @NotNull @Override public HttpInvoker header(final String name, final String value) {
        forEach(invoker -> invoker.header(name, value));
        return this;
    }

    @Override public PathResource<?> resource(@NotNull String path) {
        return pickInvoker().resource(path);
    }

    @Override public CallResource<?> resource(@NotNull Call call) {
        return pickInvoker().resource(call);
    }

    @Override public HttpInvoker withCommandPool(@NotNull final String commandPoolName) {
        threadPoolKey = some(commandPoolName);
        return this;
    }

    @Override public MultiHostHttpInvoker withConnectTimeout(final int timeout) {
        forEach(invoker -> invoker.withConnectTimeout(timeout));
        return this;
    }

    @Override public MultiHostHttpInvoker withConverter(@NotNull final MessageConverter<?> converter) {
        forEach(invoker -> invoker.withConverter(converter));
        return this;
    }

    @NotNull @Override public HttpInvoker withCookie(@NotNull String name, @NotNull String value) {
        forEach(invoker -> invoker.withCookie(name, value));
        return this;
    }

    @Override public MultiHostHttpInvoker withErrorHandler(@NotNull final InvokerErrorHandler handler) {
        forEach(invoker -> invoker.withErrorHandler(handler));
        return this;
    }

    @Override public MultiHostHttpInvoker withGzipDecompression() {
        forEach(HttpInvoker::withGzipDecompression);
        return this;
    }

    @Override public HttpInvoker withMetrics() {
        forEach(HttpInvoker::withMetrics);
        return this;
    }

    @Override public HttpInvoker withMetrics(@NotNull final InvocationKeyGenerator keyGenerator) {
        forEach(invoker -> invoker.withMetrics(keyGenerator));
        return this;
    }

    @Override public MultiHostHttpInvoker withModifier(@NotNull final MessageModifier modifier) {
        forEach(invoker -> invoker.withModifier(modifier));
        return this;
    }

    @Override public MultiHostHttpInvoker withReadTimeout(final int timeout) {
        forEach(invoker -> invoker.withReadTimeout(timeout));
        return this;
    }

    @Override public HttpInvoker withSgAppToken(@NotNull final String sgAppToken) {
        forEach(invoker -> invoker.withSgAppToken(sgAppToken));
        return this;
    }

    @Override public HttpInvoker withSurrogate(@NotNull final String surrogate) {
        forEach(invoker -> invoker.withSurrogate(surrogate));
        return this;
    }

    /** Visible for testing. */
    MultiHostStrategy getStrategy() {
        return strategy;
    }

    /** Apply given block for each invoker. */
    private void forEach(@NotNull final Consumer<HttpInvoker> command) {
        invokers.forEach(command);
    }

    private HttpInvoker pickInvoker() {
        return strategy.pick().orElseThrow(NoInvokerAvailable::new);
    }

    //~ Static Fields ................................................................................................................................

    private static final Logger logger = Logger.getLogger(MultiHostHttpInvoker.class);

    //~ Inner Classes ................................................................................................................................

    private class MultiHostCookie implements MutableCookie {
        private final List<MutableCookie> cookies;

        private MultiHostCookie() {
            cookies = new ArrayList<>();
        }

        @Override public boolean equals(Object o) {
            return this == o || o instanceof Cookie && Cookies.equal(this, (Cookie) o);
        }

        @Override public int hashCode() {
            return hash(this);
        }

        @Override public String toString() {
            return Cookies.toString(any());
        }

        @Override public MutableCookie withDomain(@Nullable String domain) {
            cookies.forEach(c -> c.withDomain(domain));
            return this;
        }

        @Override public MutableCookie withHttpOnly(boolean flag) {
            cookies.forEach(c -> c.withHttpOnly(flag));
            return this;
        }

        @Override public MutableCookie withMaxAge(long seconds) {
            cookies.forEach(c -> c.withMaxAge(seconds));
            return this;
        }

        @Override public MutableCookie withPath(@Nullable String uri) {
            cookies.forEach(c -> c.withPath(uri));
            return this;
        }

        @Override public MutableCookie withSecure(boolean flag) {
            cookies.forEach(c -> c.withSecure(flag));
            return this;
        }

        @Override public MutableCookie withValue(@NotNull String newValue) {
            cookies.forEach(c -> c.withValue(newValue));
            return this;
        }

        @Nullable @Override public String getDomain() {
            return any().getDomain();
        }

        @Override public boolean isSecure() {
            return any().isSecure();
        }

        @Override public long getMaxAge() {
            return any().getMaxAge();
        }

        @NotNull @Override public String getName() {
            return any().getName();
        }

        @Nullable @Override public String getPath() {
            return any().getPath();
        }

        @NotNull @Override public String getValue() {
            return any().getValue();
        }

        @Override public boolean isHttpOnly() {
            return any().isHttpOnly();
        }

        private MutableCookie any() {
            return cookies.get(0);
        }

        private void withCookie(MutableCookie cookie) {
            cookies.add(cookie);
        }
    }  // end class MultiHostCookie

    private class MultiHostInvokerCommandImpl<T> extends InvokerCommandImpl<T>
        implements Func1<Throwable, Observable<? extends HttpInvokerResult<T>>>
    {
        private final Invocation<T> invocation;
        private Throwable           previous;

        MultiHostInvokerCommandImpl(Invocation<T> invocation) {
            super(invocation);
            this.invocation = invocation;
            previous        = null;
        }

        @Override public Observable<? extends HttpInvokerResult<T>> call(Throwable t) {
            if (isConnectionException(t) || isServerError(t)) {
                logger.error(format("Cannot invoke '%s' with error '%s'", invocation.getPath(), t));
                previous = chainWithPreviousExceptions(t);
                final HttpInvoker                    next   = strategy.next(logger, getInvoker(), invocation).orElseThrow(() ->
                            new NoInvokerAvailable(previous));
                final MultiHostInvokerCommandImpl<T> result = new MultiHostInvokerCommandImpl<>(invocation);
                result.previous = previous;
                return result.withInvoker(next).observe();
            }
            return Observable.error(t);
        }

        @Override protected Observable<HttpInvokerResult<T>> getDecoratedObservable() {
            return super.getDecoratedObservable().onErrorResumeNext(this);
        }

        @Override protected String getThreadPoolKey() {
            return threadPoolKey.orElse(super.getThreadPoolKey());
        }

        private Throwable chainWithPreviousExceptions(Throwable t) {
            final Throwable cause = t instanceof InvokerConnectionException ? t.getCause() : t;
            if (previous == null) previous = cause;
            else if (cause.getCause() == null) previous = cause.initCause(previous);
            return previous;
        }

        private boolean isConnectionException(Throwable t) {
            return t instanceof InvokerConnectionException;
        }

        private boolean isServerError(Throwable t) {
            return t instanceof InvokerInvocationException && ((InvokerInvocationException) t).getStatus().isServerError();
        }
    }  // end class MultiHostInvokerCommandImpl
}  // end class MultiHostHttpInvoker
