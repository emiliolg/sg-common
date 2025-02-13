
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

/**
 * Handler for invoker exceptions.
 */
interface InvokerExceptionHandler {

    //~ Methods ......................................................................................................................................

    /** Handle exception on given execution. */
    <T> HttpInvokerResult<T> handle(HttpInvoker invoker, Invocation<T> execution, Exception exception);
}
