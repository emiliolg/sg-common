
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static tekgenesis.common.Predefined.cast;

/**
 * MemoMap.
 */
public abstract class MemoMap<K, V, M extends MemoMap<K, V, M>> extends Memo<V, M> {

    //~ Instance Fields ..............................................................................................................................

    private final ConcurrentHashMap<K, MemoEntry<V>> map;

    //~ Constructors .................................................................................................................................

    /** MemoMap constructor. */
    protected MemoMap(long duration, TimeUnit unit) {
        super(duration, unit);
        map = new ConcurrentHashMap<>();
    }

    //~ Methods ......................................................................................................................................

    @Override public void clear() {
        force();
    }
    /** clear value of a particular key. */
    public void clear(K key) {
        final MemoEntry<V> entry = map.get(key);
        if (entry != null) entry.clear();
    }

    @Override public void force() {
        map.clear();
    }

    /** Force recalculation of a particular key. */
    public void force(K key) {
        final MemoEntry<V> entry = map.get(key);
        if (entry != null) entry.force();
    }

    /** Get the memoized value. */
    public V get(K key) {                                        //
        return map.computeIfAbsent(key, k -> createMemoEntry())  //
               .get(getDuration(), (lt, old) -> calculate(key, lt, old), null);
    }

    protected abstract V calculate(K key, long lastRefreshTime, @Nullable V oldValue);

    @NotNull protected MemoEntry<V> createMemoEntry() {
        return new MemoEntry<>();
    }

    //~ Methods ......................................................................................................................................

    /** Force recalculation of a particular key for the specified map. */
    public static void forceRecalculation(String memoName, Object key) {  //
        getInstanceByName(memoName).castTo(MemoMap.class).ifPresent(m -> {
            final MemoMap<Object, ?, ?> mm = cast(m);
            mm.force(key);
        });
    }
}  // end class MemoMap
