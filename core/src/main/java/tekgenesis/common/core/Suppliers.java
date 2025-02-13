
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

import tekgenesis.common.annotation.GwtIncompatible;

/**
 * Some utility methods to adapt, create, or operate with {@link Supplier} objects.
 */
public interface Suppliers {

    //~ Methods ......................................................................................................................................

    /** An Empty Supplier. */
    static <T> Supplier<T> empty() {
        return () -> null;
    }

    /** Creates a retriever from a {@link Builder}. */
    @NotNull static <T> Supplier<T> fromBuilder(@NotNull final Builder<T> b) {
        return b::build;
    }

    /** A Simple Retriever that returns always the same Object. */
    static <T> Supplier<T> fromObject(final T object) {
        return () -> object;
    }

    /** Creates a retriever from a {@link Runnable}. */
    static Supplier<Void> fromRunnable(Runnable r) {
        return () -> {
                   r.run();
                   return null;
               };
    }

    /** Memoize the invocation of a Supplier for a given lapse of time. */
    @GwtIncompatible static <T> MemoizingSupplier<T> memoize(final Supplier<T> delegate, long duration, TimeUnit unit) {
        return new MemoizingSupplier<>(delegate, duration, unit);
    }
}  // end class Suppliers
