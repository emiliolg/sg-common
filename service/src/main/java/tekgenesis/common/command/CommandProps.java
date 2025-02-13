
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.command;

import javax.inject.Named;

import tekgenesis.common.env.Mutable;
import tekgenesis.common.env.Properties;

/**
 * Command properties.
 */
@Mutable
@Named("command")
public class CommandProps implements Properties {

    //~ Instance Fields ..............................................................................................................................

    /** Blocking queue size for command thread pool. */
    public int poolThreadQueueSize = 5;

    /**
     * Amount of threads per core to keep in the command thread pool (may be overridden be
     * poolTotalThreads property).
     */
    public int poolThreadsPerCore = 5;

    /**
     * Total amount of threads to be used in the command thread pool (overrides poolThreadsPerCore
     * property). Zero means undefined, and poolThreadsPerCore will be used.
     */
    public int poolTotalThreads = 0;
}
