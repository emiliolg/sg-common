
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.jmx;

import javax.management.MBeanServerConnection;

@FunctionalInterface interface JmxFunction<R> {

    //~ Methods ......................................................................................................................................

    R apply(MBeanServerConnection t)
        throws Exception;
}
