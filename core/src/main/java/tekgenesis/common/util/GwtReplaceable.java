
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.core.Constants;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.collections.Colls.first;

/**
 * A class with static methods with an implementation different in GWT This is the NON GWT
 * implementation.
 */
@SuppressWarnings("NonJREEmulationClassesInClientCode")  // excluded from GWT
public class GwtReplaceable {

    //~ Constructors .................................................................................................................................

    private GwtReplaceable() {}

    //~ Methods ......................................................................................................................................

    /**
     * Appends an String representation of an object to a StringBuilder This implementation handles
     * Arrays on any type. It is included here so it can have a different implementation for GWT
     */
    public static void appendValue(StringBuilder builder, @Nullable final Object value) {
        if (value == null) builder.append(Constants.NULL_TO_STRING);
        else if (!value.getClass().isArray()) builder.append(value);
        else {
            builder.append("[");
            for (int i = 0; i < Array.getLength(value); i++) {
                if (i > 0) builder.append(", ");
                appendValue(builder, Array.get(value, i));
            }
            builder.append("]");
        }
    }  // end method appendValue

    /** Cast the Object to the specified class. */
    public static <T> T classCast(final Class<T> target, final Object value) {
        return target.cast(value);
    }

    /** Creates an Exception object using reflection. */
    public static RuntimeException createRuntimeException(Class<? extends RuntimeException> e, Object[] args) {
        return Reflection.construct(e, args);
    }

    /** New array of the given class. */
    @NotNull public static <T> T[] newArray(Class<?> t, int size) {
        return cast(Array.newInstance(t, size));
    }

    /** Check if given value is instance of target class. */
    public static boolean isInstance(final Class<?> target, final Object value) {
        return target.isInstance(value);
    }

    /** Implementation to be replaced in Gwt. */
    public static boolean isInstanceOf(final Object o,
                                       @SuppressWarnings("rawtypes") final Class<? extends Iterable> collectionType, final Class<?> elementType) {
        return !collectionType.isInstance(o) ? false : first((Iterable<?>) o).map(elementType::isInstance).orElse(true);
    }

    /** Determines if the specified character is an ISO control character. */
    public static boolean isISOControl(char c) {
        return Character.isISOControl(c);
    }

    /** Check if given value is assignable to the target class. */
    public static boolean isAssignableFrom(final Class<?> target, final Class<?> cls) {
        return target.isAssignableFrom(cls);
    }

    /** Returns true if running on the client side (browser). */
    public static boolean isClient() {
        return false;
    }
}
