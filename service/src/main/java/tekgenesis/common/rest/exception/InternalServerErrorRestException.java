
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

import static tekgenesis.common.service.Status.INTERNAL_SERVER_ERROR;

/**
 * 500 Internal Server Error.
 */
@SuppressWarnings("WeakerAccess")
public class InternalServerErrorRestException extends RestInvocationException {

    //~ Constructors .................................................................................................................................

    /** Construct {@link InternalServerErrorRestException} with given message. */
    InternalServerErrorRestException(@Nullable String msg) {
        super(INTERNAL_SERVER_ERROR, RestMessages.REST_MSGS.internalServerError(), msg);
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 3285102877283758030L;
}
