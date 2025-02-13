
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.cluster;

import java.io.Serializable;

/**
 * Interface to handle messages sent to cluster.
 */
public interface MessageHandler<T extends Serializable> {

    //~ Instance Fields ..............................................................................................................................

    short CACHE_SCOPE       = 1001;
    short CLEAR_CACHE_SCOPE = 1003;

    short INDEX_SCOPE         = 1000;
    short REBUILD_CACHE_SCOPE = 1002;

    short SEND_MAILS = 1004;

    //~ Methods ......................................................................................................................................

    /** Handle message. */
    void handle(T message);

    /** Message Scope. */
    Short getScope();
}
