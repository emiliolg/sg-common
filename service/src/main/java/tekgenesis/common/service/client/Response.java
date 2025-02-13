
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service.client;

import java.io.IOException;

import tekgenesis.common.service.InboundMessage;
import tekgenesis.common.service.Status;

/**
 * Represents a client response.
 */
public interface Response extends InboundMessage {

    //~ Methods ......................................................................................................................................

    /** Get the status code. */
    Status getStatus()
        throws IOException;
}
