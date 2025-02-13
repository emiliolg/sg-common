
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.cmd;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An annotation to provide info for a Command.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {
    /**
     * Command Name.
     */
    String name();
    /**
     * Description for the Command, you can specify several description lines.
     */
    String[] description() default {};
    /**
     * Help option names. Default defined in {@link Command#DEFAULT_HELP } if you want no help use
     * help = ""
     */
    String[] help() default {};
}
