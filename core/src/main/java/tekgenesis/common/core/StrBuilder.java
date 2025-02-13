
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.annotation.GwtIncompatible;

import static java.lang.String.format;

import static tekgenesis.common.core.Strings.quoted;

/**
 * A simplified {@link StringBuilder} with some added functionality to handle collections.
 */
public class StrBuilder implements Serializable, CharSequence, Builder<String> {

    //~ Instance Fields ..............................................................................................................................

    private final StringBuilder implementation;

    private boolean insideCollection;
    private String  sep = DEFAULT_SEPARATOR;

    //~ Constructors .................................................................................................................................

    /** Create a Builder. */
    public StrBuilder() {
        implementation = new StringBuilder();
    }

    /** Create a Builder and initialize it with the specified String. */
    public StrBuilder(String init) {
        implementation = new StringBuilder(init);
    }

    //~ Methods ......................................................................................................................................

    /** Appends the specified string to this character sequence. */
    public StrBuilder append(String str) {
        implementation.append(str);
        return this;
    }

    /** Appends the specified Sequence to this character sequence. */
    @SuppressWarnings("UnusedReturnValue")
    public StrBuilder append(CharSequence s) {
        implementation.append(s);
        return this;
    }

    /** Appends the specified Object to this character sequence. */
    public StrBuilder append(Object o) {
        implementation.append(o);
        return this;
    }

    /** Appends the specified {@link Iterable} to this character sequence. using the default sep */
    @SuppressWarnings("UnusedReturnValue")
    public StrBuilder append(@NotNull Iterable<?> items) {
        return append(items, sep);
    }

    /**
     * Appends the specified {@link Iterable} to this character sequence, using the specified sep.
     */
    public StrBuilder append(@NotNull Iterable<?> items, @NotNull String separator) {
        boolean useSep = false;
        for (final Object item : items) {
            if (useSep) implementation.append(separator);
            useSep = true;
            implementation.append(item);
        }
        return this;
    }

    /**
     * Appends the specified element of a collection to this character sequence, using the default
     * sep.
     */
    public StrBuilder appendElement(@NotNull Object item) {
        return appendElement(item, sep);
    }
    /**
     * Appends the specified element of a collection to this character sequence, using the specified
     * separator.
     */
    public StrBuilder appendElement(@NotNull Object item, @NotNull String separator) {
        if (insideCollection) implementation.append(separator);
        insideCollection = true;
        implementation.append(item);
        return this;
    }

    /**
     * Appends the specified element of a collection to this character sequence, using the specified
     * separator. And escaping occurrences of the separator using "\\"
     */
    @SuppressWarnings("UnusedReturnValue")
    public StrBuilder appendEscapedElement(@NotNull Object item, char separator) {
        if (insideCollection) implementation.append(separator);
        insideCollection = true;
        final String e = String.valueOf(item);
        for (int i = 0; i < e.length(); i++) {
            final char c = e.charAt(i);
            if (c == separator) implementation.append('\\');
            implementation.append(c);
        }
        return this;
    }

    /** Appends the specified formatted string to this character sequence. */
    @GwtIncompatible
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public StrBuilder appendFormat(String format, Object... args) {
        return appendElement(format(format, args));
    }

    @Override public String build() {
        return implementation.toString();
    }

    @Override public char charAt(int index) {
        return implementation.charAt(index);
    }

    @Override public int length() {
        return implementation.length();
    }

    /** Quotes and appends the specified string to this character sequence. */
    public StrBuilder quote(String str) {
        implementation.append(quoted(str, '\\'));
        return this;
    }

    /**
     * Start the appending of a collection, using the DEFAULT_SEPARATOR (Only needed when you append
     * more than one collection to a Builder).
     */
    @SuppressWarnings("UnusedReturnValue")
    public StrBuilder startCollection() {
        return startCollection(DEFAULT_SEPARATOR);
    }

    /** Start the appending of a collection and set the sep to the specified one. */
    public StrBuilder startCollection(String useSeparator) {
        sep              = useSeparator;
        insideCollection = false;
        return this;
    }

    @Override public CharSequence subSequence(int start, int end) {
        return implementation.subSequence(start, end);
    }

    @NotNull @Override public String toString() {
        return implementation.toString();
    }

    /** Returns true if this builder is empty. */
    public boolean isEmpty() {
        return implementation.length() == 0;
    }

    //~ Static Fields ................................................................................................................................

    private static final String DEFAULT_SEPARATOR = ",";

    private static final long serialVersionUID = -1679160174687397038L;
}  // end class StrBuilder
