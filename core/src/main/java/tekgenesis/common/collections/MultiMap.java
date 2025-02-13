
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

import tekgenesis.common.core.Option;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.collections.Colls.emptyIterable;

/**
 * A map from a Key to a list of values.
 */
public class MultiMap<K, V> {

    //~ Instance Fields ..............................................................................................................................

    private final Comparator<V> comparator;

    private final Map<K, Collection<V>> implementation;
    private final boolean               sorted;
    private final boolean               unique;

    //~ Constructors .................................................................................................................................

    private MultiMap(Map<K, Collection<V>> impl, boolean unique, boolean sorted, @Nullable Comparator<V> comparator) {
        implementation  = impl;
        this.unique     = unique;
        this.sorted     = sorted;
        this.comparator = cast(comparator == null ? DEFAULT_COMPARATOR : comparator);
    }

    //~ Methods ......................................................................................................................................

    /** Return All individual Values. */
    public Seq<V> allValues() {
        Seq<V> result = emptyIterable();
        for (final Collection<V> vs : implementation.values())
            result = result.append(vs);
        return result;
    }

    /**
     * Returns a map view that associates each key with the corresponding values in the multi-map.
     */
    public Map<K, Collection<V>> asMap() {
        return implementation;
    }

    /** Returns true if the Map contains the specified key. */
    public boolean containsKey(K key) {
        return implementation.containsKey(key);
    }

    /**
     * Returns a collection view of all values associated with a key. It returns the
     * {@link Colls#emptyList() } if no entry is present
     */
    @NotNull public ImmutableCollection<V> get(K key) {
        return Colls.immutable(implementation.get(key));
    }

    /** Returns a Collection of all keys, each appearing once in the returned set. */
    public ImmutableCollection<K> keys() {
        return Colls.immutable(implementation.keySet());
    }

    /** Ensures there is an entry (at least an empty one) for the specified key. */
    public Collection<V> put(K key) {
        return implementation.computeIfAbsent(key, k -> sorted ? new TreeSet<>(comparator) : unique ? new LinkedHashSet<>() : new ArrayList<>(2));
    }

    /** Stores a key-value pair in the multi-map. */
    public void put(K key, V value) {
        put(key).add(value);
    }

    /** Copies all key-values from given instance into the multi-map. */
    public void putAll(@NotNull final MultiMap<K, V> other) {
        for (final Map.Entry<K, Collection<V>> entry : other.implementation.entrySet())
            putAll(entry.getKey(), entry.getValue());
    }

    /** Stores a key-values pair in the multi-map. */
    public void putAll(K key, Iterable<V> values) {
        final Collection<V> c = put(key);
        for (final V v : values)
            c.add(v);
    }

    /** Stores a key-value pair in the multi-map, if the entry is empty. */
    public void putIfEmpty(K key, V value) {
        final Collection<V> vs = put(key);
        if (vs.isEmpty()) vs.add(value);
    }

    /** Removes a key-value pair from the multi map. */
    public void remove(K key, V value) {
        final Collection<V> values = implementation.get(key);
        if (values != null) values.remove(value);
    }

    /** Removes all key-value pair with the specified key from the multi map. */
    public void removeAll(K key) {
        implementation.remove(key);
    }

    @Override public String toString() {
        return asMap().toString();
    }

    /** Return Values. */
    public Collection<Collection<V>> values() {
        return implementation.values();
    }

    /** Get optional first element in collection of specified key. */
    public Option<V> getFirst(K key) {
        return get(key).getFirst();
    }

    /** Returns true if the MultiMap is Empty. */
    public boolean isEmpty() {
        return implementation.isEmpty();
    }

    //~ Methods ......................................................................................................................................

    /** Creates a MultiMap backed by a LinkedHashMap. */
    public static <K, V> MultiMap<K, V> createLinkedMultiMap() {
        return new MultiMap<>(new LinkedHashMap<K, Collection<V>>(), true, false, null);
    }

    /** Creates a MultiMap.backed by a HashMap. */
    public static <K, V> MultiMap<K, V> createMultiMap() {
        return new Builder<K, V>().build();
    }

    /** Creates a MultiMap backed by a LinkedHashMap. The list of values for each key is ordered */
    public static <K, V> MultiMap<K, V> createSortedMultiMap() {
        return new Builder<K, V>().withSortedValues().build();
    }

    /** Creates a MultiMap backed by a LinkedHashMap. The list of values for each key is ordered */
    public static <K, V> MultiMap<K, V> createSortedMultiMap(Comparator<V> comparator) {
        return new Builder<K, V>().withSortedValues(comparator).build();
    }

    /**
     * Creates a MultiMap backed by a LinkedHashMap. The list of values for each key is guaranteed
     * to be unique
     */
    public static <K, V> MultiMap<K, V> createUniqueMultiMap() {
        return new Builder<K, V>().withUniqueValues().build();
    }

    //~ Static Fields ................................................................................................................................

    @SuppressWarnings("rawtypes")
    private static final Comparator DEFAULT_COMPARATOR = (o1, o2) -> {
                                                             if (o1 instanceof Comparable) {
                                                                 final Comparable<Object> c = cast(o1);
                                                                 return c.compareTo(o2);
                                                             }
                                                             return o1.toString().compareTo(o2.toString());
                                                         };

    //~ Inner Classes ................................................................................................................................

    /**
     * Builder to create a Multi Map.
     */
    public static class Builder<K, V> {
        private Comparator<K> keyComparator   = null;
        private boolean       sortedKeys      = false;
        private boolean       sortedValues    = false;
        private boolean       uniqueValues    = false;
        private Comparator<V> valueComparator = null;

        /** Build it. */
        public MultiMap<K, V> build() {
            final Map<K, Collection<V>> impl = sortedKeys ? keyComparator == null ? new TreeMap<>() : new TreeMap<>(keyComparator)
                                                          : new LinkedHashMap<>();
            return new MultiMap<>(impl, uniqueValues, sortedValues, valueComparator);
        }

        /** Build it from any iterable and a function that acts as a keyextractor. */
        public MultiMap<K, V> buildFrom(Traversable<V> elements, Function<V, K> keyExtractor) {
            final MultiMap<K, V> m = build();
            elements.forEach(e -> m.put(keyExtractor.apply(e), e));
            return m;
        }

        /** Keys will be sorted using the natural order. */
        public Builder<K, V> withSortedKeys() {
            sortedKeys = true;
            return this;
        }

        /** Keys will be sorted using the specified comparator. */
        public Builder<K, V> withSortedKeys(Comparator<K> cmp) {
            keyComparator = cmp;
            sortedKeys    = true;
            return this;
        }

        /** Values will be sorted using the natural order. */
        public Builder<K, V> withSortedValues() {
            sortedValues = true;
            return this;
        }

        /** Values will be sorted using the specified comparator. */
        public Builder<K, V> withSortedValues(Comparator<V> cmp) {
            valueComparator = cmp;
            sortedValues    = true;
            return this;
        }

        /** Values must be unique inside each key. */
        public Builder<K, V> withUniqueValues() {
            uniqueValues = true;
            return this;
        }
    }  // end class Builder
}  // end class MultiMap
