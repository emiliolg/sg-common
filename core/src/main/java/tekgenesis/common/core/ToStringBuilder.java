
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.Predefined;

import static tekgenesis.common.util.GwtReplaceable.appendValue;

/**
 * A Class used to build a {@link Object#toString()} method.
 */
public final class ToStringBuilder implements Builder<String> {

    //~ Instance Fields ..............................................................................................................................

    private boolean             appendSeparator;
    private final StringBuilder builder;

    //~ Constructors .................................................................................................................................

    /** Constructor for the Object. Used from {@link Predefined#createToStringBuilder(String)} */

    public ToStringBuilder(@NotNull String name) {
        builder = new StringBuilder(name).append('(');
    }

    //~ Methods ......................................................................................................................................

    /** Adds a value to the builder. */
    public ToStringBuilder add(@Nullable Object value) {
        appendSeparator();
        appendValue(builder, value);
        return this;
    }

    /** Adds a pair (field name, value) to the builder. */
    public ToStringBuilder add(@NotNull String name, @Nullable Object value) {
        appendSeparator();
        builder.append(name).append('=');
        appendValue(builder, value);
        return this;
    }

    @Override public String build() {
        return builder.append(')').toString();
    }

    private void appendSeparator() {
        if (appendSeparator) builder.append(", ");
        appendSeparator = true;
    }
}  // end class ToStringBuilder
