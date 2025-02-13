
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;

import org.jetbrains.annotations.Nullable;

import tekgenesis.common.logging.Logger;

import static tekgenesis.common.core.DateTime.currentTimeMillis;

/**
 * Each Map Entry.
 */
public class MemoEntry<V> {

    //~ Instance Fields ..............................................................................................................................

    private final AtomicLong lastTime = new AtomicLong(0);
    private V                value    = null;

    //~ Methods ......................................................................................................................................

    /** Clear value and force calculation on next get(). */
    public void clear() {
        value = null;
        force();
    }

    /** Force calculation next time. */
    public void force() {
        lastTime.set(0);
    }

    /** Get the value. Recalculate if necessary */
    public V get(long duration, BiFunction<Long, V, V> calculation) {
        return get(duration, calculation, null);
    }

    /** Get the value. Recalculate if necessary */
    public V get(long duration, BiFunction<Long, V, V> calculation, @Nullable ExecutorService executor) {
        final long now = currentTimeMillis();
        final long lt  = lastTime.get();

        // If no value calculate synchronizing
        if (value == null) {
            synchronized (this) {
                if (value == null) {
                    lastTime.set(now);
                    tryToCalculate(lt, calculation);
                }
            }
        }
        // The AtomicLong manages the concurrency. So if 2 threads try to recalculate the value. Only the first one will do it
        else if ((lt == 0L || duration < 0 || now - lt >= duration) && lastTime.compareAndSet(lt, now)) {
            if (executor != null) calculateAsync(executor, lt, calculation);
            else tryToCalculate(lt, calculation);
        }
        return value;
    }

    private void calculateAsync(ExecutorService executor, long lt, BiFunction<Long, V, V> calculation) {
        try {
            executor.submit(() -> tryToCalculate(lt, calculation)).get(1, TimeUnit.SECONDS);
        }
        catch (final TimeoutException ignore) {}
        catch (final Exception e) {
            lastTime.set(lt);
            logger.error(e);
        }
    }

    private void tryToCalculate(long lt, BiFunction<Long, V, V> calculation) {
        try {
            value = calculation.apply(lt, value);
        }
        catch (final Exception e) {
            logger.error(e);
            lastTime.set(lt);
        }
    }

    //~ Static Fields ................................................................................................................................

    private static final Logger logger = Logger.getLogger(MemoEntry.class);
}  // end class MemoEntry
