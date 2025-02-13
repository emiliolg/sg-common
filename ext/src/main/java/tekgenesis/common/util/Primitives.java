
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static tekgenesis.common.Predefined.notNull;
import static tekgenesis.common.core.QName.qualify;

/**
 * Some utility to deal with types.
 */
public class Primitives {

    //~ Constructors .................................................................................................................................

    private Primitives() {}

    //~ Methods ......................................................................................................................................

    /** Returns the primitive for a wrapper class. */
    public static String primitiveFor(String typeName) {
        final Class<?> c = primitive.get(qualify(JAVA_LANG, typeName));
        return c != null ? c.getName() : "";
    }

    /** Returns the primitive for a wrapper class. */
    @Nullable public static Class<?> primitiveFor(Class<?> clazz) {
        return primitive.get(clazz.getName());
    }

    /**
     * Returns the wrapper for a primitive type as an String if the type is a primitive one, if not
     * returns the argument.
     */
    public static String wrapIfNeeded(String typeName) {
        final Class<?> w = wrapper.get(typeName);
        return w == null ? typeName : w.getName();
    }

    /** Returns the wrapper for a primitive type. */
    @Nullable public static Class<?> wrapperFor(String typeName) {
        return wrapper.get(typeName);
    }
    /** Returns the wrapper for a primitive type, ot the class itself if it is not a primitive. */
    @NotNull public static Class<?> wrapperFor(Class<?> c) {
        return notNull(wrapper.get(c.getSimpleName()), c);
    }

    /** Returns true if typeName is a primitive one. */
    public static boolean isPrimitive(String typeName) {
        return wrapper.containsKey(typeName);
    }

    /** Returns true if typeName is a primitive one. */
    public static boolean isPrimitive(Class<?> typeName) {
        return wrapper.containsKey(typeName.getName());
    }

    /** Returns true if typeName is a primitive Wrapper. */
    public static boolean isWrapper(Class<?> typeName) {
        return primitive.containsKey(typeName.getName());
    }

    //~ Static Fields ................................................................................................................................

    private static final String JAVA_LANG = "java.lang";

    private static final Class<?>[] wrapperClasses = {
        Byte.class, Short.class, Integer.class, Long.class, Character.class, Boolean.class, Float.class, Double.class, Void.class
    };

    private static final String TYPE_FIELD = "TYPE";

    private static final Map<String, Class<?>> wrapper = new HashMap<>();

    private static final Map<String, Class<?>> primitive = new HashMap<>();

    static {
        for (final Class<?> c : wrapperClasses) {
            try {
                final Class<?> p = (Class<?>) c.getField(TYPE_FIELD).get(null);
                wrapper.put(p.getName(), c);
                primitive.put(c.getName(), p);
            }
            catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}  // end class Primitives
