
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

import tekgenesis.common.rest.RestMessages;

import static tekgenesis.common.service.Status.SERVICE_UNAVAILABLE;

/**
 * 503 Service Unavailable.
 */
@SuppressWarnings("WeakerAccess")
public class ServiceUnavailableRestException extends RestInvocationException {

    //~ Constructors .................................................................................................................................

    /** Construct {@link ServiceUnavailableRestException} with given message. */
    ServiceUnavailableRestException(@Nullable String msg) {
        super(SERVICE_UNAVAILABLE, RestMessages.REST_MSGS.serviceUnavailable(), msg);
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = -2058550068214092207L;
}
