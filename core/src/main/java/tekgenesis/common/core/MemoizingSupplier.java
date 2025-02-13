
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import static tekgenesis.common.Predefined.ensureNotNull;

/**
 * A Supplier that memoize the call for a given time. Use
 * {@link Suppliers#memoize(Supplier, long, TimeUnit)} to create one.
 */
public class MemoizingSupplier<T> implements Supplier<T> {

    //~ Instance Fields ..............................................................................................................................

    transient volatile long   expiration;
    transient volatile T      value;
    private final Supplier<T> delegate;
    private final long        durationNanoseconds;

    //~ Constructors .................................................................................................................................

    MemoizingSupplier(@NotNull Supplier<T> delegate, long duration, TimeUnit unit) {
        ensureNotNull(delegate);
        if (duration < 0) throw new IllegalArgumentException("Duration '" + duration + "'must be greater than 0");
        this.delegate       = delegate;
        durationNanoseconds = unit.toNanos(duration);
        value               = null;
        expiration          = 0L;
    }

    //~ Methods ......................................................................................................................................

    public T get() {
        final long exp = expiration;
        final long now = durationNanoseconds == 0 ? 0L : System.nanoTime();
        if (exp != 0L && now - exp < 0L) return value;

        synchronized (this) {
            if (exp != expiration) return value;
            final T t = delegate.get();
            value = t;
            final long l = now + durationNanoseconds;
            expiration = l == 0L ? 1L : l;
            return t;
        }
    }
    /** Reset the Supplier, force recalculation next time is invoked. */
    public void reset() {
        expiration = 0L;
    }
}  // end class MemoizingSupplier
