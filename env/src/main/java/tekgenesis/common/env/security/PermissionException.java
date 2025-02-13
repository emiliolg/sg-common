
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.security;

import static tekgenesis.common.env.security.SecurityMessages.MSGS;

/**
 * Exception thrown when a user does not have a permission.
 */
public class PermissionException extends RuntimeException {

    //~ Constructors .................................................................................................................................

    /** Default form. */
    public PermissionException(String user, String formClass, String permission) {
        super(MSGS.dontHavePermission(user, permission, formClass));
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = -4454462472633364892L;
}
