
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.collections;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A descriptor for a discrete comparable domain such as all Integer instances. A discrete domain is
 * one that supports the three basic operations: {@link #next}, {@link #previous} and
 * {@link #distance}.
 */
public abstract class DiscreteDomain<T extends Comparable<? super T>> {

    //~ Methods ......................................................................................................................................

    /**
     * Returns a signed value indicating how many nested invocations of {@link #next} (if positive)
     * or {@link #previous} (if negative) are needed to reach {@code end} starting from
     * {@code start}.
     */
    public abstract long distance(@NotNull final T start, @NotNull final T end);

    /**
     * Returns the unique least value of type T that is greater than given value}, or null if none
     * exists. Inverse operation to {@link #previous}.
     */
    public abstract T next(@NotNull final T value);

    /**
     * Returns the unique greatest value of type T that is less than value, or null if none exists.
     * Inverse operation to {@link #next}.
     */
    public abstract T previous(@NotNull T value);

    /** Returns the maximum value of type T, if it has one. */
    T maxValue() {
        throw new NoSuchElementException();
    }

    /** Returns the minimum value of type T, if it has one. */
    T minValue() {
        throw new NoSuchElementException();
    }

    //~ Methods ......................................................................................................................................

    /** Returns the discrete domain for values of type BigDecimal. */
    public static DiscreteDomain<BigDecimal> decimals() {
        return BigDecimalDomain.INSTANCE;
    }

    /** Returns the discrete domain for values of type Double. */
    public static DiscreteDomain<Double> doubles() {
        return DoubleDomain.INSTANCE;
    }

    /** Returns the discrete domain for values of type Integer. */
    public static DiscreteDomain<Integer> integers() {
        return IntegerDomain.INSTANCE;
    }

    /** Returns the discrete domain for values of type Long. */
    public static DiscreteDomain<Long> longs() {
        return LongDomain.INSTANCE;
    }

    //~ Inner Classes ................................................................................................................................

    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    private static final class BigDecimalDomain extends DiscreteDomain<BigDecimal> {
        @Override public long distance(@NotNull BigDecimal start, @NotNull BigDecimal end) {
            return end.subtract(start).max(MIN_LONG).min(MAX_LONG).longValue();
        }

        @Override public BigDecimal next(@NotNull BigDecimal value) {
            return value.add(BigDecimal.ONE);
        }

        @Override public BigDecimal previous(@NotNull BigDecimal value) {
            return value.subtract(BigDecimal.ONE);
        }

        private static final BigDecimalDomain INSTANCE = new BigDecimalDomain();
        private static final BigDecimal       MIN_LONG = BigDecimal.valueOf(Long.MIN_VALUE);
        private static final BigDecimal       MAX_LONG = BigDecimal.valueOf(Long.MAX_VALUE);
    }

    private static final class DoubleDomain extends DiscreteDomain<Double> {
        @Override public long distance(@NotNull Double start, @NotNull Double end) {
            return (long) (end - start);
        }

        @Override public Double maxValue() {
            return Double.MAX_VALUE;
        }

        @Override public Double minValue() {
            return Double.MIN_VALUE;
        }

        @Nullable @Override public Double next(@NotNull Double value) {
            return value == Double.MAX_VALUE ? null : value + 1;
        }

        @Nullable @Override public Double previous(@NotNull Double value) {
            return value == Double.MIN_VALUE ? null : value - 1;
        }

        private static final DoubleDomain INSTANCE = new DoubleDomain();
    }

    private static final class IntegerDomain extends DiscreteDomain<Integer> {
        @Override public long distance(@NotNull Integer start, @NotNull Integer end) {
            return (long) end - start;
        }

        @Override public Integer maxValue() {
            return Integer.MAX_VALUE;
        }

        @Override public Integer minValue() {
            return Integer.MIN_VALUE;
        }

        @Nullable @Override public Integer next(@NotNull Integer value) {
            return value == Integer.MAX_VALUE ? null : value + 1;
        }

        @Nullable @Override public Integer previous(@NotNull Integer value) {
            return value == Integer.MIN_VALUE ? null : value - 1;
        }

        private static final IntegerDomain INSTANCE = new IntegerDomain();
    }

    private static final class LongDomain extends DiscreteDomain<Long> {
        @Override public long distance(@NotNull Long start, @NotNull Long end) {
            final long result = end - start;

            if (end > start && result < 0) {  // overflow
                return Long.MAX_VALUE;
            }

            if (end < start && result > 0) {  // underflow
                return Long.MIN_VALUE;
            }

            return result;
        }

        @Override public Long maxValue() {
            return Long.MAX_VALUE;
        }

        @Override public Long minValue() {
            return Long.MIN_VALUE;
        }

        @Nullable @Override public Long next(@NotNull Long value) {
            return value == Long.MAX_VALUE ? null : value + 1;
        }

        @Nullable @Override public Long previous(@NotNull Long value) {
            return value == Long.MIN_VALUE ? null : value - 1;
        }

        private static final LongDomain INSTANCE = new LongDomain();
    }
}  // end class DiscreteDomain
