
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Outbound message exchange. Common for client and server.
 */
public interface OutboundMessage extends Message {

    //~ Methods ......................................................................................................................................

    /**
     * Return message content as stream. Message will write headers before returning stream if
     * necessary!
     */
    OutputStream getContent()
        throws IOException;
}
