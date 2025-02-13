
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.jmx;

/**
 * JmxInvoker Interface.
 */
public interface JmxInvoker extends Invoker<JmxInvoker> {

    //~ Methods ......................................................................................................................................

    /** Return the managed bean with the specified name. */
    MBeanResourceImpl mbean(String objectName);
}
