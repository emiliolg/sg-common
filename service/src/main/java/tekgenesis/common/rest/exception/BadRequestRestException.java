
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
import static tekgenesis.common.service.Status.BAD_REQUEST;

/**
 * 400 Bad Request.
 */
@SuppressWarnings("WeakerAccess")
public class BadRequestRestException extends RestInvocationException {

    //~ Constructors .................................................................................................................................

    /** Construct {@link BadRequestRestException} with given message. */
    BadRequestRestException(@Nullable String msg) {
        super(BAD_REQUEST, REST_MSGS.badRequest(), msg);
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = -3032796152490511011L;
}
