
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core.enumeration;

import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.annotation.GwtIncompatible;
import tekgenesis.common.core.Enumeration;
import tekgenesis.common.core.StrBuilder;
import tekgenesis.common.core.Strings;
import tekgenesis.common.util.Reflection;

import static java.util.Collections.unmodifiableMap;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.Predefined.ensureNotNull;
import static tekgenesis.common.Predefined.notNull;

/**
 * Utility methods for Enumeration classes. Excluded from GWT.
 */
@GwtIncompatible
@SuppressWarnings({ "NonJREEmulationClassesInClientCode", "WeakerAccess" })
public class Enumerations {

    //~ Constructors .................................................................................................................................

    private Enumerations() {}

    //~ Methods ......................................................................................................................................

    /** Return the given class as an enum one. */
    public static <T extends Enum<T>> Class<T> asEnumClass(Class<?> anyClass) {
        return cast(anyClass);
    }

    /** Returns a long from an EnumSet. */
    public static <T extends Enum<T>> long asLong(@Nullable EnumSet<T> enumSet) {
        if (enumSet == null || enumSet.isEmpty()) return 0;
        long result = 0;
        for (final T t : enumSet) {
            final int n = t instanceof Enumeration ? ((Enumeration<?, ?>) t).index() : t.ordinal();
            result |= 1 << n;
        }
        return result;
    }

    /**
     * Return a String separated by ',' from an Iterable of Enums (Reverse operation of #enumSet).
     */
    public static String asString(Iterable<? extends Enum<?>> enums) {
        final StrBuilder result = new StrBuilder();
        for (final Enum<?> e : enums)
            result.appendElement(e.name());
        return result.toString();
    }

    /** Build the map of values. */
    @NotNull public static <K, T extends Enum<T> & Enumeration<T, K>> Map<K, T> buildMap(@Nullable final T[] values) {
        final Map<K, T> r = new LinkedHashMap<>();
        if (values != null) {
            for (final T v : values)
                r.put(v.key(), v);
        }
        return unmodifiableMap(r);
    }

    /** Returns the Enumeration value. */
    @Nullable public static <T extends Enum<T> & Enumeration<T, K>, K> T enumerationValueOf(Class<?> requiredType, K key) {
        if (!Enumeration.class.isAssignableFrom(requiredType)) return null;
        final Class<T>  et  = cast(requiredType);
        final Map<K, T> map = Enumeration.mapFor(et);
        final T         t   = map.get(key);
        if (t != null) return t;
        if (key instanceof Number) {
            final K k = cast(((Number) key).intValue());
            return map.get(k);
        }
        return null;
    }

    /** Return an EnumSet from an String of enum constants separated by ','. */
    @NotNull public static <T extends Enum<T>> EnumSet<T> enumSet(Class<T> enumClass, String enumConstants) {
        String enums = notNull(enumConstants);
        if (enums.startsWith("[") && enums.endsWith("]")) enums = enums.substring(1, enums.length() - 1);
        return enumSet(enumClass, Strings.split(enums, ',').map(String::trim));
    }

    /** Return an EnumSet from a Collection of enum constants. */
    @NotNull public static <T extends Enum<T>> EnumSet<T> enumSet(Class<T> enumClass, Iterable<String> strings) {
        final EnumSet<T> r = EnumSet.noneOf(enumClass);
        for (final String s : strings)
            r.add(valueOf(enumClass, s));
        return r;
    }

    /** Return an EnumSet from a collection of enums. */
    public static <E extends Enum<E>> EnumSet<E> enumSet(Class<E> clazz, Collection<E> c) {
        return c.isEmpty() ? EnumSet.noneOf(clazz) : EnumSet.copyOf(c);
    }

    /** Returns a set of the values of the enum that matches the given mask. */
    public static <E extends Enum<E>> EnumSet<E> longToSet(Class<E> enumType, @Nullable Long elements) {
        final EnumSet<E> result = EnumSet.noneOf(enumType);
        if (elements == null) return result;
        final long v = elements;
        for (final E e : enumType.getEnumConstants()) {
            final int n = e instanceof Enumeration ? ((Enumeration<?, ?>) e).index() : e.ordinal();
            if ((v & (1 << n)) != 0) result.add(e);
        }
        return result;
    }

    /** Returns the enum type of the given EnumSet. */
    public static <E extends Enum<E>> Class<E> typeOfSet(EnumSet<E> set) {
        return ensureNotNull(Reflection.getPrivateField(set, "elementType"));
    }

    /** Call {@link Enum#valueOf(Class, String)} avoid problems with Generics. */
    public static <T extends Enum<T>> T valueOf(Class<T> enumClass, String enumConstant) {
        final Class<Void> c = cast(enumClass);
        return cast(Enum.valueOf(c, enumConstant));
    }

    /** Call {@link Enum#valueOf(Class, String)} avoid problems with Generics. */
    public static <T extends Enum<T>> T valueOf(String enumClassName, String enumConstant) {
        final Class<Void> c = Reflection.findClass(enumClassName);
        return cast(Enum.valueOf(c, enumConstant));
    }

    /** Return the set of values for any Enumeration. */
    public static Collection<? extends Enumeration<?, ?>> getValuesFor(String className) {
        final Class<Void>       aClass = Reflection.findClass(className);
        final Map<Object, Void> map    = Enumeration.mapFor(aClass);
        return map.values();
    }

    //~ Enums ........................................................................................................................................

    public enum Void implements Enumeration<Void, Object> {
        ;
        @Override public int index() {
            return ordinal();
        }
        @Override public Object key() {
            return "";
        }
    }
}  // end class Enumerations
