
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.rest.exception;

import org.jetbrains.annotations.Nullable;

import static tekgenesis.common.rest.RestMessages.REST_MSGS;
import static tekgenesis.common.service.Status.BAD_GATEWAY;

/**
 * 502 Bad Gateway.
 */
@SuppressWarnings("WeakerAccess")
public class BadGatewayRestException extends RestInvocationException {

    //~ Constructors .................................................................................................................................

    /** Construct {@link BadGatewayRestException} with given message. */
    BadGatewayRestException(@Nullable String msg) {
        super(BAD_GATEWAY, REST_MSGS.badGateway(), msg);
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = -3173100891156672275L;
}
