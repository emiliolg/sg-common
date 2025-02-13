
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
import java.util.concurrent.TimeUnit;

import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.core.Option;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.Predefined.option;
import static tekgenesis.common.collections.Colls.toList;

/**
 * Base class for Memo (2 subclasses SingletonMemo & MemoMap).
 */
public abstract class Memo<V, M extends Memo<V, M>> {

    //~ Instance Fields ..............................................................................................................................

    private long duration;  // Duration in milliseconds

    //~ Constructors .................................................................................................................................

    Memo(long duration, TimeUnit unit) {
        this.duration = unit.toMillis(duration);
    }

    //~ Methods ......................................................................................................................................

    /** Clear value of this Memo. */
    public abstract void clear();

    /** Force the recalculation of this Memo. */
    public abstract void force();

    /**
     * Set the duration (TTL) of the SingletonMemo. If value is negative, the memo is always
     * calculated
     */
    public M withDuration(long newDuration, TimeUnit unit) {
        duration = unit.toMillis(newDuration);
        return cast(this);
    }

    /** Get the duration of the SingletonMemo in milliseconds. */
    public long getDuration() {
        return duration;
    }

    //~ Methods ......................................................................................................................................

    /** Return all Memo Names. */
    public static synchronized ImmutableList<String> allMemoNames() {
        return toList(instances.keySet());
    }
    /** Return all Memos. */
    public static synchronized ImmutableList<Memo<?, ?>> allMemos() {
        return toList(instances.values());
    }

    /** Force the recalculation of the specified Memo (if existent). */
    public static synchronized void forceRecalculation(String memoName) {
        getInstanceByName(memoName).ifPresent(Memo::force);
    }

    /** Register the specified Memo. */
    public static <M extends Memo<T, M>, T> M register(String memoName, M memo) {
        instances.put(memoName, memo);
        return memo;
    }

    /** Get Memo instance. */
    public static synchronized <M extends Memo<T, M>, T> M getInstance(Class<M> myMemoClass) {
        return cast(instances.computeIfAbsent(myMemoClass.getCanonicalName(), k -> Reflection.construct(myMemoClass)));
    }
    static Option<Memo<?, ?>> getInstanceByName(String memoName) {
        return option(instances.get(memoName));
    }

    //~ Static Fields ................................................................................................................................

    private static final Map<String, Memo<?, ?>> instances = new HashMap<>();
}  // end class Memo
