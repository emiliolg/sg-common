
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import java.util.function.Consumer;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.command.Command;
import tekgenesis.common.command.FallbackCommand;
import tekgenesis.common.invoker.exception.InvokerApplicationException;
import tekgenesis.common.invoker.exception.InvokerConnectionException;
import tekgenesis.common.invoker.exception.InvokerInvocationException;
import tekgenesis.common.service.Headers;
import tekgenesis.common.service.Status;

/**
 * Invoker command wrap services calls code that executes potentially risky functionality. This
 * command is essentially a blocking command but provides an Observable facade if used with
 * observe()
 */
public interface InvokerCommand<T> extends FallbackCommand<T>, Command<HttpInvokerResult<T>> {

    //~ Methods ......................................................................................................................................

    /**
     * Execute result or throwable block when either a success completion with a valid T result, or
     * a {@link InvokerApplicationException exception} thrown during execution must be accepted.
     */
    void acceptEither(@NotNull Consumer<T> result, @NotNull Consumer<InvokerApplicationException> exception);

    /**
     * Used for synchronous execution of command.
     *
     * @return  HttpInvokerResult<T> result of {@link Command} execution
     */
    @Override HttpInvokerResult<T> execute()
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException;

    /**
     * Used for synchronous execution of invocation.
     *
     * @return  T result of {@link InvokerCommand<T>} execution
     */
    T get()
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException;

    /**
     * Map execution result or throwable when either a success completion with a valid T result or a
     * {@link InvokerApplicationException exception} is thrown during execution.
     */
    <R> R mapEither(@NotNull Function<T, R> mapResult, @NotNull Function<InvokerApplicationException, R> mapException);

    /**
     * If a {@link Command command} method fails in any way then the specified function will be
     * invoked to provide an opportunity to return a fallback response. This should be a static or
     * cached result that can immediately be returned upon failure.
     */
    @Override InvokerCommand<T> onErrorFallback(@NotNull Function<Throwable, T> fallback);

    /**
     * If a {@link InvokerCommand command} method fails with an
     * {@link InvokerApplicationException exception} then the specified function will be invoked to
     * provide an opportunity to return a fallback response. This should be a static or cached
     * result that can immediately be returned upon failure.
     */
    InvokerCommand<T> onExceptionFallback(@NotNull Function<InvokerApplicationException, T> fallback);

    /**
     * Timeout value, in milliseconds, to be used when opening a communications link to a resource.
     * Zero value implies that the option is disabled (i.e., timeout of infinity). Value is
     * inherited from {@link HttpInvoker#withConnectTimeout} connect timeout but can be overridden
     * for each command.
     */
    InvokerCommand<T> withConnectTimeout(int timeout);

    /** Set command metrics invocation key. */
    InvokerCommand<T> withInvocationKey(String key);

    /**
     * Timeout value, in milliseconds, for reading from input stream when a connection is
     * established. Zero value implies that the option is disabled (i.e., timeout of infinity).
     * Value is inherited from {@link HttpInvoker#withReadTimeout} read timeout but can be
     * overridden for each command.
     */
    InvokerCommand<T> withReadTimeout(int timeout);

    /**
     * Used for synchronous execution of invocation, suppressing InvokerApplicationException and
     * InvokerInvocationException.
     *
     * @return  {@link Headers headers} of {@link InvokerCommand<T>} execution
     */
    Headers getHeaders()
        throws InvokerConnectionException;

    /**
     * Used for synchronous execution of invocation, suppressing InvokerApplicationException and
     * InvokerInvocationException.
     *
     * @return  {@link Status status} of {@link InvokerCommand<T>} execution
     */
    Status getStatus()
        throws InvokerConnectionException;
}  // end interface InvokerCommand
