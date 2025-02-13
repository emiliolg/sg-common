
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

import tekgenesis.common.invoker.exception.InvokerConnectionException;
import tekgenesis.common.invoker.exception.InvokerInvocationException;
import tekgenesis.common.service.Headers;
import tekgenesis.common.service.Method;

/**
 * Represents an HTTP resource with a set path for retrieval.
 */
public interface PathResource<U extends PathResource<U>> extends HttpResource<U> {

    //~ Methods ......................................................................................................................................

    /**
     * Invoke the {@link Method#DELETE} method with no request payload or response. If a
     * representation is present then that representation is ignored.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    void delete()
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke the {@link Method#DELETE} method with no request payload that returns a response.
     *
     * @param   responseType  the type of the returned response.
     *
     * @return  an instance of type responseType.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    <T> T delete(@NotNull Class<T> responseType)
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke the {@link Method#DELETE} method with no request payload that returns a response.
     *
     * @param   genericType  the type of the returned response.
     *
     * @return  an instance of type responseType.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    <T> T delete(@NotNull GenericType<T> genericType)
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke the {@link Method#GET} method.
     *
     * @param   responseType  the type of the returned response.
     *
     * @return  an instance of type responseType.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    <T> T get(@NotNull Class<T> responseType)
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke the {@link Method#GET} method.
     *
     * @param   genericType  the type of the returned response.
     *
     * @return  a sequence of type multipleType.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    <T> T get(@NotNull GenericType<T> genericType)
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke the {@link Method#HEAD} method.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    Headers head()
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke a {@link Method method} with no request payload or response. If a representation is
     * present then that representation is ignored.
     *
     * @param   method  the desired {@link Method method}.
     *
     * @return  an invoker typed result. See {@link HttpInvokerResult}.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    InvokerCommand<?> invoke(@NotNull Method method)
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke a {@link Method method} with a request payload but no response. If a representation is
     * present then that representation is ignored.
     *
     * @param   method   the desired {@link Method method}.
     * @param   payload  the request payload.
     *
     * @return  an invoker typed result. See {@link HttpInvokerResult}.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    InvokerCommand<?> invoke(@NotNull Method method, @NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke a {@link Method method} with no request payload that returns a response.
     *
     * @param   method        the desired {@link Method method}.
     * @param   responseType  the type of the returned response.
     *
     * @return  an invoker typed result. See {@link HttpInvokerResult}.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    <T> InvokerCommand<T> invoke(@NotNull Method method, @NotNull Class<T> responseType)
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke a {@link Method method} with no request payload that returns a response.
     *
     * @param   method       the desired {@link Method method}.
     * @param   genericType  the type of the multiple returned response.
     *
     * @return  an invoker typed result. See {@link HttpInvokerResult}.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    <T> InvokerCommand<T> invoke(@NotNull Method method, @NotNull GenericType<T> genericType)
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke a {@link Method method} with a request payload that returns a response.
     *
     * @param   method        the desired {@link Method method}.
     * @param   responseType  the type of the returned response.
     * @param   payload       the request payload.
     *
     * @return  an invoker typed result. See {@link HttpInvokerResult}.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    <T> InvokerCommand<T> invoke(@NotNull Method method, @NotNull Class<T> responseType, @NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke a {@link Method method} with a request payload that returns a response.
     *
     * @param   method       the desired {@link Method method}.
     * @param   genericType  the type of the returned response.
     * @param   payload      the request payload.
     *
     * @return  an invoker typed result. See {@link HttpInvokerResult}.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    <T> InvokerCommand<T> invoke(@NotNull Method method, @NotNull GenericType<T> genericType, @NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke the {@link Method#POST} method with no request payload or response. If a
     * representation is present then that representation is ignored.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    void post()
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke the {@link Method#POST} method with a request payload but no response. If a
     * representation is present then that representation is ignored.
     *
     * @param   payload  the request payload.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    void post(@NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke the {@link Method#POST} method with no request payload that returns a response.
     *
     * @param   responseType  the type of the returned response.
     *
     * @return  an instance of type responseType.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    <T> T post(@NotNull Class<T> responseType)
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke the {@link Method#POST} method with no request payload that returns a response.
     *
     * @param   genericType  the type of the returned response.
     *
     * @return  an instance of type responseType.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    <T> T post(@NotNull GenericType<T> genericType)
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke the {@link Method#POST} method with a request payload that returns a response.
     *
     * @param   responseType  the type of the returned response.
     * @param   payload       the request payload.
     *
     * @return  an instance of type responseType.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    <T> T post(@NotNull Class<T> responseType, @NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke the {@link Method#POST} method with a request payload that returns a response.
     *
     * @param   genericType  the type of the returned response.
     * @param   payload      the request payload.
     *
     * @return  an instance of type responseType.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    <T> T post(@NotNull GenericType<T> genericType, @NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke the {@link Method#PUT} method with no request payload or response. If a representation
     * is present then that representation is ignored.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    void put()
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke the {@link Method#PUT} method with a request payload but no response. If a
     * representation is present then that representation is ignored.
     *
     * @param   payload  the request payload.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    void put(@NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke the {@link Method#PUT} method with no request payload that returns a response.
     *
     * @param   responseType  the type of the returned response.
     *
     * @return  an instance of type responseType.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    <T> T put(@NotNull Class<T> responseType)
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke the {@link Method#PUT} method with no request payload that returns a response.
     *
     * @param   genericType  the type of the returned response.
     *
     * @return  an instance of type responseType.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    <T> T put(@NotNull GenericType<T> genericType)
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke the {@link Method#PUT} method with a request payload that returns a response.
     *
     * @param   responseType  the type of the returned response.
     * @param   payload       the request payload.
     *
     * @return  an instance of type responseType.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    <T> T put(@NotNull Class<T> responseType, @NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException;

    /**
     * Invoke the {@link Method#PUT} method with a request payload that returns a response.
     *
     * @param   genericType  the type of the returned response.
     * @param   payload      the request payload.
     *
     * @return  an instance of type responseType.
     *
     * @throws  InvokerConnectionException  if the invoker fails to process the request or response.
     * @throws  InvokerInvocationException  if status of response is >= 300 and the default error
     *                                      handler is being used.
     */
    <T> T put(@NotNull GenericType<T> genericType, @NotNull Object payload)
        throws InvokerConnectionException, InvokerInvocationException;
}  // end interface PathResource
