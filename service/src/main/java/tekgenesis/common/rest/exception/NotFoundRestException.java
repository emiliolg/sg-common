
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

import static tekgenesis.common.service.Status.NOT_FOUND;

/**
 * 404 Internal Server Error.
 */
@SuppressWarnings("WeakerAccess")
public class NotFoundRestException extends RestInvocationException {

    //~ Constructors .................................................................................................................................

    /** Construct {@link NotFoundRestException} with given message. */
    NotFoundRestException(@Nullable String msg) {
        super(NOT_FOUND, RestMessages.REST_MSGS.documentNotFound(), msg);
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = -8288404934268834264L;
}
