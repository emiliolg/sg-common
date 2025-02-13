
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.invoker.exception.InvokerApplicationException;
import tekgenesis.common.invoker.exception.InvokerConnectionException;
import tekgenesis.common.invoker.exception.InvokerInvocationException;
import tekgenesis.common.service.Call;

/**
 * Represents an HTTP resource with a set path and method for retrieval.
 */
public interface CallResource<U extends CallResource<U>> extends HttpResource<U> {

    //~ Methods ......................................................................................................................................

    /**
     * Invoke {@link Call call} with no request payload or response. If a representation is present
     * then that representation is ignored.
     *
     * @return  an invoker typed result. See {@link HttpInvokerResult}.
     *
     * @throws  InvokerApplicationException  if the invoker fails to process the request or
     *                                       response.
     * @throws  InvokerConnectionException   if the invoker fails to process the request or
     *                                       response.
     * @throws  InvokerInvocationException   if status of response is >= 300 and the default error
     *                                       handler is being used.
     */
    InvokerCommand<?> invoke()
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException;

    /**
     * Invoke {@link Call call} with a request payload but no response. If a representation is
     * present then that representation is ignored.
     *
     * @param   payload  the request payload.
     *
     * @return  an invoker typed result. See {@link HttpInvokerResult}.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    InvokerCommand<?> invoke(@NotNull Object payload)
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException;

    /**
     * Invoke {@link Call call} with no request payload that returns a response.
     *
     * @param   responseType  the type of the returned response.
     *
     * @return  an invoker typed result. See {@link HttpInvokerResult}.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    <T> InvokerCommand<T> invoke(@NotNull Class<T> responseType)
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException;

    /**
     * Invoke {@link Call call} with no request payload that returns a response.
     *
     * @param   genericType  the type of the returned response.
     *
     * @return  an invoker typed result. See {@link HttpInvokerResult}.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    <T> InvokerCommand<T> invoke(@NotNull GenericType<T> genericType)
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException;

    /**
     * Invoke {@link Call call} with a request payload that returns a response.
     *
     * @param   responseType  the type of the returned response.
     * @param   payload       the request payload.
     *
     * @return  an invoker typed result. See {@link HttpInvokerResult}.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    <T> InvokerCommand<T> invoke(@NotNull Class<T> responseType, @NotNull Object payload)
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException;

    /**
     * Invoke {@link Call call} with a request payload that returns a response.
     *
     * @param   genericType  the type of the returned response.
     * @param   payload      the request payload.
     *
     * @return  an invoker typed result. See {@link HttpInvokerResult}.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    <T> InvokerCommand<T> invoke(@NotNull GenericType<T> genericType, @NotNull Object payload)
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException;
}  // end interface CallResource
