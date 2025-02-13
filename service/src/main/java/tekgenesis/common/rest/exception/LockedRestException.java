
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
import static tekgenesis.common.service.Status.LOCKED;

/**
 * 423 Locked.
 */
@SuppressWarnings("WeakerAccess")
public class LockedRestException extends RestInvocationException {

    //~ Constructors .................................................................................................................................

    /** Construct {@link LockedRestException} with given message. */
    LockedRestException(@Nullable String msg) {
        super(LOCKED, REST_MSGS.httpLock(), msg);
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = -8174349930574541280L;
}
