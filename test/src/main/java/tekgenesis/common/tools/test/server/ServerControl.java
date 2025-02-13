
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.tools.test.server;

/**
 * Control server lifecycle.
 */
public interface ServerControl {

    //~ Methods ......................................................................................................................................

    /** Shutdown server. */
    void shutdown();

    /** Start server. */
    void start();
}
