
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.test;

import java.util.Locale;

import org.junit.Test;

import tekgenesis.common.env.context.Context;
import tekgenesis.common.env.i18n.I18nBundle;
import tekgenesis.common.env.i18n.I18nMessages;
import tekgenesis.common.env.i18n.I18nMessagesFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * I18n Test;
 */
@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class I18nTest {

    //~ Methods ......................................................................................................................................

    @Test public void bundleTest() {
        final I18nBundle bundle = I18nBundle.getBundle(I18nTest.class);

        assertThat(bundle.getString("r1", locale)).isEqualTo("Resource 1");

        assertThat(bundle.getString("r1", es)).isEqualTo("Recurso 1");

        assertThat(bundle.getString("r2", "default", locale)).isEqualTo("default");
    }

    @Test public void messagesTest() {
        final A a = I18nMessagesFactory.create(A.class);

        assertThat(a.a()).isEqualTo("Some a");
        assertThat(a.b(4)).isEqualTo("One B with '4' legs");
        assertThat(a.b(6)).isEqualTo("One B with '6' legs");

        Context.getContext().setLocale(es);
        assertThat(a.a()).isEqualTo("Un a");
        assertThat(a.b(4)).isEqualTo("Un B con '4' patas");
        assertThat(a.b(6)).isEqualTo("Un B con '6' patas");
    }

    //~ Static Fields ................................................................................................................................

    private static final Locale locale = Locale.getDefault();
    private static final Locale es     = new Locale("es");

    //~ Inner Interfaces .............................................................................................................................

    interface A extends I18nMessages {
        @DefaultMessage("Some a")
        String a();
        @DefaultMessage("One B with ''{0}'' legs")
        String b(int c);
    }
}  // end class I18nTest
