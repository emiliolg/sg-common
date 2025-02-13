
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

import tekgenesis.common.invoker.error.InvokerErrorHandler;
import tekgenesis.common.invoker.metric.InvocationKeyGenerator;
import tekgenesis.common.service.Call;
import tekgenesis.common.service.etl.MessageConverter;

/**
 * Invoker over HTTP.
 */
public interface HttpInvoker extends WithHeaders<HttpInvoker> {

    //~ Methods ......................................................................................................................................

    /** Return a new resource to be retrieved in specified path. */
    PathResource<?> resource(@NotNull String path);

    /** Return a new resource to be retrieved in specified path, with specified method. */
    CallResource<?> resource(@NotNull Call call);

    /**
     * Specify command pool for given invoker. Invoker will use default InvokerCommand pool if not
     * specified.
     */
    HttpInvoker withCommandPool(@NotNull String commandPoolName);

    /**
     * Timeout value, in milliseconds, to be used when opening a communications link to a resource.
     * Zero value implies that the option is disabled (i.e., timeout of infinity).
     */
    HttpInvoker withConnectTimeout(int timeout);

    /** Add given {@link MessageConverter converter} to invoker. */
    HttpInvoker withConverter(@NotNull MessageConverter<?> converter);

    /** Set {@link InvokerErrorHandler error handler}. Allows throwing specific exceptions. */
    HttpInvoker withErrorHandler(@NotNull InvokerErrorHandler handler);

    /** Add Gzip decompression {@link MessageModifier modifier} to invoker. */
    HttpInvoker withGzipDecompression();

    /** Allow metrics recording on invoker. */
    HttpInvoker withMetrics();

    /** Allow metrics recording on invoker specifying custom {@link InvocationKeyGenerator}. */
    HttpInvoker withMetrics(@NotNull InvocationKeyGenerator keyGenerator);

    /** Add given {@link MessageModifier modifier} to invoker. */
    HttpInvoker withModifier(@NotNull MessageModifier modifier);

    /**
     * Timeout value, in milliseconds, for reading from input stream when a connection is
     * established. Zero value implies that the option is disabled (i.e., timeout of infinity).
     */
    HttpInvoker withReadTimeout(int timeout);

    /** Application token for X-Tek-app-Token. */
    HttpInvoker withSgAppToken(@NotNull String sgAppToken);

    /** Application surrogate user. */
    HttpInvoker withSurrogate(@NotNull String surrogate);
}  // end interface HttpInvoker
