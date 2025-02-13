
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
import static tekgenesis.common.service.Status.GATEWAY_TIMEOUT;

/**
 * 504 Gateway Timeout.
 */
@SuppressWarnings("WeakerAccess")
public class GatewayTimeoutRestException extends RestInvocationException {

    //~ Constructors .................................................................................................................................

    /** Construct {@link GatewayTimeoutRestException} with given message. */
    GatewayTimeoutRestException(@Nullable String msg) {
        super(GATEWAY_TIMEOUT, REST_MSGS.timeout(), msg);
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 6361638935661710412L;
}
