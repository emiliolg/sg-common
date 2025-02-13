
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
 * Internal command factory.
 */
interface InvokerCommandFactory {

    //~ Methods ......................................................................................................................................

    /** Create new internal invoker command implementation. */
    <T> InvokerCommandImpl<T> command(Invocation<T> invocation);
}
