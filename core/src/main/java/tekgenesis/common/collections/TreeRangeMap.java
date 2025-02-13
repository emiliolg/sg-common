
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.collections;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.Predefined.ensureNotNull;
import static tekgenesis.common.Predefined.notImplemented;
import static tekgenesis.common.collections.Range.Cut;

/**
 * An implementation of {@code RangeMap} based on a {@code TreeMap}.
 *
 * <p>As defined by {@code RangeMap}, this supports neither null keys nor null values.</p>
 */
public final class TreeRangeMap<K extends Comparable<? super K>, V> implements RangeMap<K, V> {

    //~ Instance Fields ..............................................................................................................................

    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    private final NavigableMap<Cut<K>, RangeMapEntry<K, V>> entriesByLowerBound;

    //~ Constructors .................................................................................................................................

    private TreeRangeMap() {
        entriesByLowerBound = new TreeMap<>();
    }

    //~ Methods ......................................................................................................................................

    @NotNull @Override public Map<Range<K>, V> asMapOfRanges() {
        return new AsMapOfRanges();
    }

    @Override public void clear() {
        entriesByLowerBound.clear();
    }

    @Override public boolean equals(@Nullable Object map) {
        if (map instanceof RangeMap) {
            final RangeMap<K, V> other = cast(map);
            return asMapOfRanges().equals(other.asMapOfRanges());
        }
        return false;
    }

    @Nullable @Override public V get(@NotNull final K key) {
        final Map.Entry<Range<K>, V> entry = getEntry(key);
        return (entry == null) ? null : entry.getValue();
    }

    @Override public int hashCode() {
        return asMapOfRanges().hashCode();
    }

    @Override public void put(@NotNull final Range<K> range, @NotNull final V value) {
        if (!range.isEmpty()) {
            remove(range);
            entriesByLowerBound.put(range.getLowerBound(), new RangeMapEntry<>(range, value));
        }
    }

    @Override public void putAll(@NotNull final RangeMap<K, V> rangeMap) {
        for (final Map.Entry<Range<K>, V> entry : rangeMap.asMapOfRanges().entrySet())
            put(entry.getKey(), entry.getValue());
    }

    @Override public void remove(@NotNull final Range<K> range) {
        if (range.isEmpty()) return;

        /* The comments for this method will use [ ] to indicate the bounds of range and ( ) to
         * indicate the bounds of ranges in the range map. */
        final Map.Entry<Cut<K>, RangeMapEntry<K, V>> mapEntryBelowToTruncate = entriesByLowerBound.lowerEntry(range.getLowerBound());
        if (mapEntryBelowToTruncate != null) {
            // we know ( [
            final RangeMapEntry<K, V> rangeMapEntry = mapEntryBelowToTruncate.getValue();
            if (rangeMapEntry.getUpperBound().compareTo(range.getLowerBound()) > 0) {
                // we know ( [ )
                if (rangeMapEntry.getUpperBound().compareTo(range.getUpperBound()) > 0)
                    // we know ( [ ] ), so insert the range ] ) back into the map --
                    // it's being split apart
                    putRangeMapEntry(range.getUpperBound(), rangeMapEntry.getUpperBound(), mapEntryBelowToTruncate.getValue().getValue());
                // overwrite mapEntryToTruncateBelow with a truncated range
                putRangeMapEntry(rangeMapEntry.getLowerBound(), range.getLowerBound(), mapEntryBelowToTruncate.getValue().getValue());
            }
        }

        final Map.Entry<Cut<K>, RangeMapEntry<K, V>> mapEntryAboveToTruncate = entriesByLowerBound.lowerEntry(range.getUpperBound());
        if (mapEntryAboveToTruncate != null) {
            // we know ( ]
            final RangeMapEntry<K, V> rangeMapEntry = mapEntryAboveToTruncate.getValue();
            if (rangeMapEntry.getUpperBound().compareTo(range.getUpperBound()) > 0) {
                // we know ( ] ), and since we dealt with truncating below already,
                // we know [ ( ] )
                putRangeMapEntry(range.getUpperBound(), rangeMapEntry.getUpperBound(), mapEntryAboveToTruncate.getValue().getValue());
                entriesByLowerBound.remove(range.getLowerBound());
            }
        }
        entriesByLowerBound.subMap(range.getLowerBound(), range.getUpperBound()).clear();
    }

    @NotNull @Override public Range<K> span() {
        final Map.Entry<Cut<K>, RangeMapEntry<K, V>> first = entriesByLowerBound.firstEntry();
        final Map.Entry<Cut<K>, RangeMapEntry<K, V>> last  = entriesByLowerBound.lastEntry();
        if (first == null) throw new NoSuchElementException();
        return Range.create(first.getValue().getKey().getLowerBound(), last.getValue().getKey().getUpperBound());
    }

    @NotNull @Override public RangeMap<K, V> subRangeMap(@NotNull final Range<K> subRange) {
        return Range.<K>all().equals(subRange) ? this : new SubRangeMap(subRange);
    }

    @Override public String toString() {
        return entriesByLowerBound.values().toString();
    }

    @Nullable @Override public Map.Entry<Range<K>, V> getEntry(@NotNull final K key) {
        final Map.Entry<Cut<K>, RangeMapEntry<K, V>> entry = entriesByLowerBound.floorEntry(Cut.below(key));

        return entry != null && entry.getValue().contains(key) ? entry.getValue() : null;
    }

    private RangeMap<K, V> emptySubRangeMap() {
        return cast(EMPTY_SUB_RANGE_MAP);
    }

    private void putRangeMapEntry(Cut<K> lowerBound, Cut<K> upperBound, @Nullable V value) {
        if (value != null) entriesByLowerBound.put(lowerBound, new RangeMapEntry<>(lowerBound, upperBound, value));
    }

    //~ Methods ......................................................................................................................................

    /** Tree Range Map creation. */
    public static <K extends Comparable<? super K>, V> TreeRangeMap<K, V> create() {
        return new TreeRangeMap<>();
    }

    //~ Static Fields ................................................................................................................................

    @SuppressWarnings("rawtypes")
    private static final RangeMap EMPTY_SUB_RANGE_MAP = new RangeMap() {
            @Nullable @Override public Object get(@NotNull final Comparable key) {
                return null;
            }

            @Nullable @Override public Map.Entry<Range, Object> getEntry(@NotNull final Comparable key) {
                return null;
            }

            @NotNull @Override public Map<Range, Object> asMapOfRanges() {
                return Collections.emptyMap();
            }

            @NotNull @Override public Range span() {
                throw new NoSuchElementException();
            }

            @Override public void put(@NotNull Range range, @NotNull Object value) {
                throw emptyMap();
            }

            @Override public void putAll(@NotNull RangeMap map) {
                throw emptyMap();
            }

            @Override public void remove(@NotNull Range range) {
                throw emptyMap();
            }

            @Override public void clear() {
                throw emptyMap();
            }

            @NotNull @Override public RangeMap subRangeMap(Range range) {
                throw emptyMap();
            }

            private IllegalArgumentException emptyMap() {
                return new IllegalArgumentException("Empty map.");
            }
        };

    //~ Inner Classes ................................................................................................................................

    private final class AsMapOfRanges extends AbstractMap<Range<K>, V> {
        @Override public boolean containsKey(@Nullable Object key) {
            return get(key) != null;
        }

        @NotNull @Override public Set<Entry<Range<K>, V>> entrySet() {
            return new AbstractSet<Entry<Range<K>, V>>() {
                @NotNull @Override public Iterator<Entry<Range<K>, V>> iterator() {
                    return cast(entriesByLowerBound.values().iterator());
                }

                @Override public int size() {
                    return entriesByLowerBound.size();
                }
            };
        }

        @Nullable @Override public V get(@Nullable Object key) {
            if (key instanceof Range) {
                final Range<?>            range = (Range<?>) key;
                final RangeMapEntry<K, V> entry = entriesByLowerBound.get(range.getLowerBound());
                if (entry != null && entry.getKey().equals(range)) return entry.getValue();
            }
            return null;
        }
    }

    private static final class RangeMapEntry<K extends Comparable<? super K>, V> extends Maps.ImmutableEntry<Range<K>, V> {
        RangeMapEntry(@NotNull final Range<K> range, @NotNull final V value) {
            super(range, value);
        }

        RangeMapEntry(@NotNull final Cut<K> lowerBound, @NotNull final Cut<K> upperBound, @NotNull final V value) {
            this(Range.create(lowerBound, upperBound), value);
        }

        public boolean contains(@NotNull final K v) {
            return getKey().contains(v);
        }

        @NotNull @Override public Range<K> getKey() {
            return ensureNotNull(super.getKey(), "Null Key.");
        }

        @NotNull private Cut<K> getLowerBound() {
            return getKey().getLowerBound();
        }

        @NotNull private Cut<K> getUpperBound() {
            return getKey().getUpperBound();
        }
    }

    private class SubRangeMap implements RangeMap<K, V> {
        @NotNull private final Range<K> subRange;

        SubRangeMap(@NotNull final Range<K> subRange) {
            this.subRange = subRange;
        }

        @NotNull @Override public Map<Range<K>, V> asMapOfRanges() {
            throw notImplemented("SubRangeMap.asMapOfRanges");
        }

        @Override public void clear() {
            TreeRangeMap.this.remove(subRange);
        }

        @Override
        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        public boolean equals(@Nullable Object o) {
            throw notImplemented("SubRangeMap.equals");
        }

        @Nullable @Override public V get(@NotNull final K key) {
            return subRange.contains(key) ? TreeRangeMap.this.get(key) : null;
        }

        @Override public int hashCode() {
            throw notImplemented("SubRangeMap.hashCode");
        }

        @Override public void put(@NotNull final Range<K> range, @NotNull final V value) {
            checkEnclose(range, "Cannot put range " + range + " into a subRangeMap: " + subRange);
            TreeRangeMap.this.put(range, value);
        }

        @Override public void putAll(@NotNull final RangeMap<K, V> rangeMap) {
            if (!rangeMap.asMapOfRanges().isEmpty()) {
                final Range<K> span = rangeMap.span();
                checkEnclose(span, "Cannot putAll rangeMap with span " + span + " into a subRangeMap " + subRange);
                TreeRangeMap.this.putAll(rangeMap);
            }
        }

        @Override public void remove(@NotNull final Range<K> range) {
            if (range.isConnected(subRange)) TreeRangeMap.this.remove(range.intersection(subRange));
        }

        @NotNull @Override public Range<K> span() {
            final Cut<K> lowerBound;

            final Map.Entry<Cut<K>, RangeMapEntry<K, V>> lowerEntry = entriesByLowerBound.floorEntry(subRange.getLowerBound());
            if (lowerEntry != null && lowerEntry.getValue().getUpperBound().compareTo(subRange.getLowerBound()) > 0)
                lowerBound = subRange.getLowerBound();
            else {
                lowerBound = entriesByLowerBound.ceilingKey(subRange.getLowerBound());
                if (lowerBound == null || lowerBound.compareTo(subRange.getUpperBound()) >= 0) throw new NoSuchElementException();
            }

            final Map.Entry<Cut<K>, RangeMapEntry<K, V>> upperEntry = entriesByLowerBound.lowerEntry(subRange.getUpperBound());

            if (upperEntry == null) throw new NoSuchElementException();

            final Cut<K> upperBound;

            if (upperEntry.getValue().getUpperBound().compareTo(subRange.getUpperBound()) >= 0) upperBound = subRange.getUpperBound();
            else upperBound = upperEntry.getValue().getUpperBound();
            return Range.create(lowerBound, upperBound);
        }  // end method span

        @NotNull @Override public RangeMap<K, V> subRangeMap(Range<K> range) {
            return !range.isConnected(subRange) ? emptySubRangeMap() : TreeRangeMap.this.subRangeMap(range.intersection(subRange));
        }

        @Nullable @Override public Map.Entry<Range<K>, V> getEntry(@NotNull final K key) {
            if (subRange.contains(key)) {
                final Map.Entry<Range<K>, V> entry = TreeRangeMap.this.getEntry(key);
                if (entry != null) return Maps.immutableEntry(entry.getKey().intersection(subRange), entry.getValue());
            }
            return null;
        }

        private void checkEnclose(@NotNull final Range<K> range, @NotNull final String message) {
            if (!subRange.encloses(range)) throw new IllegalArgumentException(message);
        }
    }  // end class SubRangeMap
}
