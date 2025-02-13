
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.jmx;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.invoker.error.InvokerErrorHandler;
import tekgenesis.common.invoker.metric.InvocationKeyGenerator;

/**
 * Invoker interface.
 */
public interface Invoker<T> {

    //~ Methods ......................................................................................................................................

    /**
     * Timeout value, in milliseconds, to be used when opening a communications link to a resource.
     * Zero value implies that the option is disabled (i.e., timeout of infinity).
     */
    T withConnectTimeout(int timeout);

    /** Set {@link InvokerErrorHandler error handler}. Allows throwing specific exceptions. */
    T withErrorHandler(@NotNull InvokerErrorHandler handler);

    /** Allow metrics recording on invoker. */
    T withMetrics();

    /** Allow metrics recording on invoker specifying custom {@link InvocationKeyGenerator}. */
    T withMetrics(@NotNull InvocationKeyGenerator keyGenerator);

    /**
     * Timeout value, in milliseconds, for reading from input stream when a connection is
     * established. Zero value implies that the option is disabled (i.e., timeout of infinity).
     */
    T withReadTimeout(int timeout);
}
