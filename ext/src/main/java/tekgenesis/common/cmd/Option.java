
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.cmd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tags an attribute as an option.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Option {
    /**
     * The name or (names) of this option. Defaults to the name of the field + the first letter of
     * the field.
     */
    String[] name() default {};

    /**
     * The regexp that the values of this option must match.
     */
    String pattern() default ".*";

    /**
     * A description for this option.
     */
    String[] description() default {};

    /**
     * The default value if none is specified.
     */
    String[] defaultValue() default {};

    /**
     * Option is not displayed in help messages.
     */
    boolean hidden() default false;
    /**
     * Option is required.
     */
    boolean required() default false;

    /**
     * Main Arguments.
     */
    boolean main() default false;

    /**
     * Arity of the option. Use it when : - The element is a list and you want a specified number of
     * elements (If not variable arity will be assumed). - Use arity 1 with boolean if you want to
     * explicitly specify the boolean value. (i.e. --debug true vs --debug)
     */
    int arity() default 0;

    /**
     * The type for a multiple option.
     */
    Class<?> elementType() default String.class;
}
