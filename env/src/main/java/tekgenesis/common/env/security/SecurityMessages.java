
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.security;

import tekgenesis.common.env.i18n.I18nMessages;
import tekgenesis.common.env.i18n.I18nMessagesFactory;

/**
 * Metadata Form Messages class.
 */
public interface SecurityMessages extends I18nMessages {

    //~ Instance Fields ..............................................................................................................................

    SecurityMessages MSGS = I18nMessagesFactory.create(SecurityMessages.class);

    //~ Methods ......................................................................................................................................

    @DefaultMessage("User {0} does not have permission {1} for form {2}")
    @SuppressWarnings("JavaDoc")
    String dontHavePermission(String user, String permission, String form);
}
