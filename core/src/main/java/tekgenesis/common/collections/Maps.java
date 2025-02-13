
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.collections;

import java.util.*;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.Predefined;
import tekgenesis.common.core.Tuple;

/**
 * Maps Factories and utilities.
 */
public class Maps {

    //~ Constructors .................................................................................................................................

    private Maps() {}

    //~ Methods ......................................................................................................................................

    /** Creates a {@link EnumMap} with the specified initial entries. */
    @NotNull public static <K extends Enum<K>, V> EnumMap<K, V> enumMap(Tuple<K, V> e1, Tuple<?, ?>... elements) {
        final Class<K> c = e1.first().getDeclaringClass();
        return fill(new EnumMap<>(c), e1, elements);
    }

    /** Creates a {@link EnumMap} with the specified initial entry. */
    @NotNull public static <K extends Enum<K>, V> EnumMap<K, V> enumMap(K key, V value) {
        final Class<K>      c   = key.getDeclaringClass();
        final EnumMap<K, V> map = new EnumMap<>(c);
        map.put(key, value);
        return map;
    }

    /** Creates a {@link HashMap} with the specified initial elements. */
    @NotNull public static <K, V> HashMap<K, V> hashMap(Tuple<K, V> e1, Tuple<?, ?>... elements) {
        return fill(new HashMap<>(), e1, elements);
    }

    /** Creates an identity {@link HashMap} with the specified elements as key and value. */
    public static <E> Map<E, E> identity(Iterable<E> elements) {
        final Map<E, E> result = new LinkedHashMap<>();
        for (final E e : elements)
            result.put(e, e);
        return result;
    }

    /** Creates an immutable map entry. */
    public static <K, V> Map.Entry<K, V> immutableEntry(@Nullable final K key, @Nullable final V value) {
        return new ImmutableEntry<>(key, value);
    }

    /** Creates a {@link LinkedHashMap} with the specified initial elements. */
    @NotNull @SafeVarargs
    @SuppressWarnings("varargs")
    public static <K, V> LinkedHashMap<K, V> linkedHashMap(Tuple<K, V> e1, Tuple<K, V>... elements) {
        return fill(new LinkedHashMap<>(), e1, elements);
    }

    /**
     * Creates a {@link LinkedHashMap} with elements taken from the the specified list through the
     * application of the specified function.
     */
    @NotNull public static <T, K, V> Map<K, V> map(Iterable<T> elements, Function<T, Tuple<K, V>> function) {
        return fill(new LinkedHashMap<>(), elements, function);
    }

    /** Creates a {@link TreeMap} with the specified initial elements. */
    @NotNull public static <K, V> TreeMap<K, V> treeMap(Tuple<K, V> e1, Tuple<?, ?>... elements) {
        return fill(new TreeMap<>(), e1, elements);
    }

    @SuppressWarnings("unchecked")
    private static <K, V, M extends Map<K, V>> M fill(M result, Tuple<K, V> e1, Tuple<?, ?>[] elements) {
        result.put(e1.first(), e1.second());
        if (elements.length > 0) {
            for (final Tuple<?, ?> tuple : elements)
                result.put((K) tuple.first(), (V) tuple.second());
        }
        return result;
    }

    private static <E, K, V, M extends Map<K, V>> M fill(M result, Iterable<E> elements, Function<E, Tuple<K, V>> function) {
        for (final E e : elements) {
            final Tuple<K, V> tuple = function.apply(e);
            result.put(tuple.first(), tuple.second());
        }
        return result;
    }

    //~ Inner Classes ................................................................................................................................

    /**
     * A Map.Entry that is guaranteed to leave the underlying data unmodified.
     */
    static class ImmutableEntry<K, V> implements Map.Entry<K, V> {
        @Nullable private final K key;
        @Nullable private final V value;

        ImmutableEntry(@Nullable K key, @Nullable V value) {
            this.key   = key;
            this.value = value;
        }

        @Override public boolean equals(@Nullable Object object) {
            if (object instanceof Map.Entry) {
                final Map.Entry<?, ?> that = (Map.Entry<?, ?>) object;
                return Predefined.equal(getKey(), that.getKey()) && Predefined.equal(getValue(), that.getValue());
            }
            return false;
        }

        @Override public int hashCode() {
            final K k = getKey();
            final V v = getValue();
            return ((k == null) ? 0 : k.hashCode()) ^ ((v == null) ? 0 : v.hashCode());
        }

        /** Returns a string representation of the form {@code {key}={value}}. */
        @Override public String toString() {
            return getKey() + "=" + getValue();
        }

        @Nullable @Override public K getKey() {
            return key;
        }

        @Nullable @Override public V getValue() {
            return value;
        }

        @Override public V setValue(V v) {
            throw new UnsupportedOperationException();
        }
    }  // end class ImmutableEntry
}  // end class Maps
