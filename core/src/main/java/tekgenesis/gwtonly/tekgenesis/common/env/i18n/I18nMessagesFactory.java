
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.i18n;

import com.google.gwt.core.client.GWT;

import tekgenesis.common.core.Instantiator;
import tekgenesis.common.env.i18n.I18nMessages;

/**
 * Factory class to create instances of the given class on GWT code.
 */
public class I18nMessagesFactory {

    //~ Methods ......................................................................................................................................

    /** Creates an instance of the given class on GWT code. */
    public static <T extends I18nMessages> T create(Class<T> clazz) {
        final Instantiator i = GWT.create(Instantiator.class);
        return i.create(clazz.getName());
    }
}
