
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.jmx;

import tekgenesis.common.command.Command;
import tekgenesis.common.command.FallbackCommand;
import tekgenesis.common.invoker.exception.InvokerApplicationException;
import tekgenesis.common.invoker.exception.InvokerConnectionException;
import tekgenesis.common.invoker.exception.InvokerInvocationException;

/**
 * Jmx Invoker command.
 */
public interface JmxInvokerCommand<T> extends FallbackCommand<T>, Command<T> {

    //~ Methods ......................................................................................................................................

    /**
     * Used for synchronous execution of invocation.
     *
     * @return  T result of {@link JmxInvokerCommand<T>} execution
     */
    T get()
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException;
}
