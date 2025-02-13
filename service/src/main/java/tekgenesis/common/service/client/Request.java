
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service.client;

import java.net.URI;

import tekgenesis.common.service.Method;
import tekgenesis.common.service.OutboundMessage;

/**
 * Represents a client request.
 */
public interface Request extends OutboundMessage {

    //~ Methods ......................................................................................................................................

    /** Return request method. */
    Method getMethod();

    /** Return request uri. */
    URI getURI();
}
