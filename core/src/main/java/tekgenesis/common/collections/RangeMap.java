
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.collections;

import java.util.Map;
import java.util.NoSuchElementException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A mapping from disjoint nonempty ranges to non-null values. Queries look up the value associated
 * with the range (if any) that contains a specified key.
 *
 * <p>No "coalescing" is done of {@linkplain Range#isConnected(Range) connected} ranges, even if
 * they are mapped to the same value.</p>
 */
public interface RangeMap<K extends Comparable<? super K>, V> {

    //~ Methods ......................................................................................................................................

    /**
     * Returns a view of this range map as an unmodifiable Map<Range<K>, V>. Modifications to this
     * range map are guaranteed to read through to the returned {@code Map}.
     *
     * <p>It is guaranteed that no empty ranges will be in the returned {@code Map}.</p>
     */
    @NotNull Map<Range<K>, V> asMapOfRanges();

    /** Removes all associations from this range map. */
    void clear();

    /**
     * Returns {@code true} if {@code obj} is another {@code RangeMap} that has an equivalent
     * {@link #asMapOfRanges()}.
     */
    @Override boolean equals(@Nullable Object o);

    /**
     * Returns the value associated with the specified key, or {@code null} if there is no such
     * value.
     *
     * <p>Specifically, if any range in this range map contains the specified key, the value
     * associated with that range is returned.</p>
     */
    @Nullable V get(@NotNull final K key);

    /** Returns {@code asMapOfRanges().hashCode()}. */
    @Override int hashCode();

    /**
     * Maps a range to a specified value.
     *
     * <p>Specifically, after a call to {@code put(range, value)}, if
     * {@link Range#contains(Comparable) range.contains(k)}, then {@link #get(Comparable) get(k)}
     * will return {@code value}.</p>
     *
     * <p>If range {@linkplain Range#isEmpty() is empty}, then this is a no-op.</p>
     */
    void put(@NotNull final Range<K> range, @NotNull final V value);

    /** Puts all the associations from {@code rangeMap} into this range map. */
    void putAll(@NotNull final RangeMap<K, V> rangeMap);

    /**
     * Removes all associations from this range map in the specified range.
     *
     * <p>If {@code !range.contains(k)}, {@link #get(Comparable) get(k)} will return the same result
     * before and after a call to {@code remove(range)}. If {@code range.contains(k)}, then after a
     * call to {@code remove(range)}, {@code get(k)} will return {@code null}.</p>
     */
    void remove(@NotNull final Range<K> range);

    /**
     * Returns the minimal range {@linkplain Range#encloses(Range) enclosing} the ranges in this
     * {@code RangeMap}.
     *
     * @throws  NoSuchElementException  if this range map is empty
     */
    @NotNull Range<K> span();

    /**
     * Returns a view of the part of this range map that intersects with {@code range}.
     *
     * <p>For example, if rangeMap had the entries [1, 5] => "foo", (6, 8) => "bar", (10, \u2025) =>
     * "baz" then rangeMap.subRangeMap(Range.open(3, 12)) would return a range map with the entries
     * (3, 5) => "foo", (6, 8) => "bar", (10, 12) => "baz".</p>
     *
     * <p>The returned range map supports all optional operations that this range map supports.</p>
     *
     * <p>The returned range map will throw an {@link IllegalArgumentException} on an attempt to
     * insert a range not {@linkplain Range#encloses(Range) enclosed} by {@code range}.</p>
     */
    @NotNull RangeMap<K, V> subRangeMap(Range<K> range);

    /**
     * Returns the range containing this key and its associated value, if such a range is present in
     * the range map, or {@code null} otherwise.
     */
    @Nullable Map.Entry<Range<K>, V> getEntry(@NotNull final K key);
}  // end interface RangeMap
