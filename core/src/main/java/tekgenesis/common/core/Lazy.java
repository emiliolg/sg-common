
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static tekgenesis.common.Predefined.*;

/**
 * Represents Lazy values. A lazy value is instantiate with a function that will be invoked only
 * once. to calculate the value.
 */
public class Lazy<T> implements Supplier<T> {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final Supplier<T> supplier;

    @Nullable private volatile T value;

    //~ Constructors .................................................................................................................................

    /** Create a Lazy value based on a Supplier. */
    public Lazy(@NotNull Supplier<T> supplier) {
        this.supplier = supplier;
        value         = null;
    }

    //~ Methods ......................................................................................................................................

    @Override
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public boolean equals(Object obj) {
        if (obj == null) return false;
        final T thisValue = get();
        if (obj instanceof Supplier) {
            final Supplier<T> s2 = cast(obj);
            return thisValue.equals(s2.get());
        }
        return thisValue.equals(obj);
    }

    /** Returns the value. Calculate it if necessary. */
    @NotNull public T get() {
        T result = value;
        if (result == null) {
            synchronized (this) {
                result = value;
                if (result == null) {
                    result = supplier.get();
                    if (result == null) throw new IllegalStateException("Null value calculated");
                    value = result;
                }
            }
        }
        return result;
    }

    @Override public int hashCode() {
        return get().hashCode();
    }

    @Override public String toString() {
        return get().toString();
    }

    /** Returns true if the instance is already defined. */
    public boolean isDefined() {
        return value != null;
    }
}  // end class Lazy
