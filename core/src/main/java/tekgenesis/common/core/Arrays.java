
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

import tekgenesis.common.util.GwtReplaceable;

/**
 * Utility Class to manage Array related operations.
 */
public interface Arrays {

    //~ Methods ......................................................................................................................................

    /** Return true if all elements of an array are null. */
    static boolean allNull(@Nullable Object[] array) {
        if (array != null) {
            for (final Object o : array) {
                if (o != null) return false;
            }
        }
        return true;
    }

    /** Create a generic one element array based on an Object. */
    @NotNull static <T> T[] arrayOf(T t) {
        final T[] r = newArray(t.getClass(), 1);
        r[0] = t;
        return r;
    }

    /** Create a generic two element array. */
    @NotNull static <T> T[] arrayOf(T first, T second) {
        final T[] r = newArray(first.getClass(), 2);
        r[0] = first;
        r[1] = second;
        return r;
    }

    /** Prepend first to the rest array. */
    @NotNull @SafeVarargs
    @SuppressWarnings("varargs")
    static <T> T[] arrayOf(final T first, final T... rest) {
        final int size = rest == null ? 1 : rest.length + 1;
        final T[] e    = newArray(first.getClass(), size);
        e[0] = first;
        if (rest != null) System.arraycopy(rest, 0, e, 1, rest.length);
        return e;
    }

    /** Create a generic one element array based on an Object. */
    @NotNull static <T> T[] newArray(Class<?> t, int size) {
        return GwtReplaceable.newArray(t, size);
    }
}  // end interface Arrays
