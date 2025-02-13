
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.*;
import java.util.function.Function;
import java.util.function.LongSupplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.core.*;

/**
 * A Simple LruCache.
 */
public class LruCache<K, V> {

    //~ Instance Fields ..............................................................................................................................

    //J-
    // Stat Fields
    private transient int loadedCount;
    private transient int missCount;
    private transient int putCount;
    private transient int evictionCount;
    private transient int hitCount;
    // Functions
    private final LongSupplier ticker;
    private final ToIntFunction<V> weigher;
    private final ToLongFunction<V> expiration;
    //J+

    private final LinkedHashMap<K, V> map;
    private final int                 maxWeight;
    private int                       weight;  // Weight of this cache in units. Not necessarily the number of elements.

    //~ Constructors .................................................................................................................................

    private LruCache(int maxWeight, ToIntFunction<V> weigher, @Nullable ToLongFunction<V> expiration, final LongSupplier ticker) {
        this.maxWeight  = maxWeight;
        map             = new LinkedHashMap<>(0, LOAD_FACTOR, true);
        this.weigher    = weigher;
        this.expiration = expiration;
        this.ticker     = ticker;
    }

    //~ Methods ......................................................................................................................................

    /** Clear the cache, calling {@link #entryRemoved} on each removed entry. */
    public final void evictAll() {
        trimToWeight(-1);  // -1 will evict 0-sized elements
    }

    /** Returns the number of values that have been evicted. */
    public final synchronized int evictionCount() {
        return evictionCount;
    }

    /**
     * Returns the value for {@code key} if it exists in the cache or can be loaded by {@code #load}.
     */
    @Nullable public final V find(@NotNull K key, @NotNull Function<K, V> load) {
        V mapValue = get(key);
        if (mapValue != null) return mapValue;

        /*
         * Attempt to load a value.
         */
        final V value = load.apply(key);
        if (value == null) return null;

        synchronized (this) {
            loadedCount++;
            mapValue = map.put(key, value);

            if (mapValue == null) weight += weigher.applyAsInt(value);
            else map.put(key, mapValue);  // There was a conflict so undo that last put
        }

        if (mapValue != null) return mapValue;

        trimToWeight(maxWeight);
        return value;
    }

    /** Returns the value for {@code key} if it exists in the cache. */
    @Nullable public synchronized V get(@NotNull final K key) {
        final V mapValue = map.get(key);
        if (mapValue != null && (expiration == null || expiration.applyAsLong(mapValue) > ticker.getAsLong())) {
            hitCount++;
            return mapValue;
        }
        missCount++;
        return null;
    }

    /** Returns the number of times {@link #find} returned a value. */
    public final synchronized int hitCount() {
        return hitCount;
    }

    /** Returns the number of times load is invoked and return a value. */
    public final synchronized int loadedCount() {
        return loadedCount;
    }

    /**
     * Returns the number of times {@link #find} returned null or required a new value to be loaded.
     */
    public final synchronized int missCount() {
        return missCount;
    }

    /**
     * Caches {@code value} for {@code key}. The value is moved to the head of the queue.
     *
     * @return  the previous value mapped by {@code key}.
     */
    @Nullable public final V put(@NotNull K key, @NotNull V value) {
        final V previous;
        synchronized (this) {
            putCount++;
            weight   += weigher.applyAsInt(value);
            previous = map.put(key, value);
            if (previous != null) weight -= weigher.applyAsInt(previous);
        }
        trimToWeight(maxWeight);
        return previous;
    }

    /** Returns the number of times {@link #put} was called. */
    public final synchronized int putCount() {
        return putCount;
    }

    /**
     * Removes the entry for {@code key} if it exists.
     *
     * @return  the previous value mapped by {@code key}.
     */
    @Nullable public final V remove(@NotNull K key) {
        final V previous;
        synchronized (this) {
            previous = map.remove(key);
            if (previous != null) weight -= weigher.applyAsInt(previous);
        }
        return previous;
    }

    /** The number of entries. */
    public final synchronized int size() {
        return map.size();
    }

    /**
     * Returns a copy of the current contents of the cache, ordered from least recently accessed to
     * most recently accessed.
     */
    public final synchronized Map<K, V> snapshot() {
        return new LinkedHashMap<>(map);
    }

    /** return an string with cache stats. */
    public String stats() {
        final int accesses   = hitCount + missCount;
        final int hitPercent = accesses != 0 ? (100 * hitCount / accesses) : 0;
        return String.format("maxSize=%d,hits=%d,misses=%d,hitRate=%d%%", maxWeight, hitCount, missCount, hitPercent);
    }

    @Override public final synchronized String toString() {
        return "LruCache[" + stats() + "]";
    }

    /**
     * Remove the eldest entries until the total of remaining entries is at or below the requested
     * size.
     *
     * @param  max  the maximum size of the cache before returning. May be -1 to evict even 0-sized
     *              elements.
     */
    public synchronized void trimToWeight(int max) {
        if (weight <= max) return;
        if (expiration != null) {
            final long current = ticker.getAsLong();
            map.values().removeIf(v -> expiration.applyAsLong(v) <= current);
        }
        while (weight > max && !map.isEmpty()) {
            final Map.Entry<K, V> toEvict = map.entrySet().iterator().next();
            final K               key     = toEvict.getKey();
            final V               value   = toEvict.getValue();
            map.remove(key);
            weight -= weigher.applyAsInt(value);
            evictionCount++;
        }
    }

    /** Returns the maximum weight of the cache. */
    public final int getMaxWeight() {
        return maxWeight;
    }

    /** Returns the current cache weight. */
    public final synchronized int getWeight() {
        return weight;
    }

    //~ Methods ......................................................................................................................................

    /** Create an LRU cache. */
    public static <K, V> LruCache<K, V> createLruCache(int maxSize) {
        return new Builder<K, V>().maxWeight(maxSize).build();
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 1118368051004454450L;

    private static final float LOAD_FACTOR = 0.75f;

    //~ Inner Classes ................................................................................................................................

    public static class Builder<K, V> {
        private ToLongFunction<V> expiration = null;

        private int              maxWeight = Integer.MAX_VALUE;
        private LongSupplier     ticker    = DateTime::currentTimeMillis;
        private ToIntFunction<V> weigher   = (v) -> 1;

        /** Build the cache. */
        public LruCache<K, V> build() {
            return new LruCache<>(maxWeight, weigher, expiration, ticker);
        }

        /**
         * Specifies the maximum weight of entries the cache may contain. either using the function
         * specified with {@link #weigher}, or 1 per entry
         */
        public Builder<K, V> maxWeight(final int max) {
            maxWeight = max;
            return this;
        }

        /**
         * Specifies the ticker used to calculate time. By default it will use
         * {@link System#currentTimeMillis()}
         */
        public Builder<K, V> ticker(LongSupplier tickerSupplier) {
            ticker = tickerSupplier;
            return this;
        }

        /** Specifies how to weight each entry. */
        public Builder<K, V> weigher(ToIntFunction<V> weigherFunction) {
            weigher = weigherFunction;
            return this;
        }

        /** Specifies how to calculate the expiration for a given entry. */
        public Builder<K, V> withExpiration(ToLongFunction<V> expirationFunction) {
            expiration = expirationFunction;
            return this;
        }
    }  // end class Builder
}  // end class LruCache
