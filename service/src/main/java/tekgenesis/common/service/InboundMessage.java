
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import tekgenesis.common.collections.Seq;
import tekgenesis.common.service.cookie.Cookie;

/**
 * Inbound message exchange. Common for client and server.
 */
public interface InboundMessage extends Message, Closeable {

    //~ Methods ......................................................................................................................................

    /** Close message content stream. */
    void close();

    /** Return message content as stream. */
    InputStream getContent()
        throws IOException;

    /** Return message cookies. */
    Seq<Cookie> getCookies();
}
