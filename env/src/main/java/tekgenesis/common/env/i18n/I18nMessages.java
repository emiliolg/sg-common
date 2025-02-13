
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.i18n;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.text.MessageFormat;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * I18n mark interface. All messages classes should implement this.
 */
public interface I18nMessages {

    //~ Annotations ..................................................................................................................................

    /**
     * Default text to be used if no translation is found (and also used as the source for
     * translation). Format should be that expected by {@link MessageFormat}
     */
    @Documented
    @Retention(RUNTIME)
    @Target(ElementType.METHOD)
    @interface DefaultMessage {
        /**
         * The value of the message.
         */
        String value();
    }
}
