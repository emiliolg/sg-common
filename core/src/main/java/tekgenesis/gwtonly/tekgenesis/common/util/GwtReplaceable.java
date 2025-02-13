
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.core.Constants;

import static tekgenesis.common.Predefined.cast;

public class GwtReplaceable {

    //~ Methods ......................................................................................................................................

    public static void appendValue(StringBuilder builder, final Object value) {
        if (value == null) builder.append(Constants.NULL_TO_STRING);
        else builder.append(value);
    }  // end method appendValue

    public static <T> T classCast(final Class<T> target, final Object value) {
        return (T) value;
    }

    public static RuntimeException createRuntimeException(Class<? extends RuntimeException> e, Object[] args) {
        return new IllegalStateException(e.getClass() + ": " + Arrays.toString(args));
    }

    @NotNull public static <T> T[] newArray(Class<?> t, int size) {
        return cast(new Object[size]);
    }

    public static boolean isInstance(final Class<?> target, final Object value) {
        throw new UnsupportedOperationException("Gwt isInstance invocation");
    }
    /** Returns true if the Object is an instance of an Collection class of a particular type. */
    public static boolean isInstanceOf(Object o, Class<? extends Iterable> collectionType, Class<?> elementType) {
        try {
            final Iterable it = (Iterable) o;
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("MagicNumber")
    public static boolean isISOControl(char c) {
        final int codePoint = (int) c;
        return (codePoint >= 0x0000 && codePoint <= 0x001F) || (codePoint >= 0x007F && codePoint <= 0x009F);
    }

    public static boolean isAssignableFrom(final Class<?> target, final Class<?> cls) {
        throw new UnsupportedOperationException("Gwt isAssignableFrom invocation");
    }

    public static boolean isClient() {
        return true;
    }
}  // end class GwtReplaceable
