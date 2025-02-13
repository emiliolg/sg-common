
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.math.BigDecimal;
import java.util.List;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.Seq;

import static java.math.BigDecimal.ROUND_HALF_EVEN;

import static tekgenesis.common.collections.ImmutableList.fromIterable;
import static tekgenesis.common.exception.FieldValueException.negativeError;
import static tekgenesis.common.exception.FieldValueException.precisionError;

/**
 * Some utility functions to operate over {@link BigDecimal} values.
 */
public interface Decimals {

    //~ Methods ......................................................................................................................................

    /** Logical comparison of 2 decimals. */
    @SuppressWarnings("NumberEquality")
    static boolean equal(@Nullable BigDecimal a, @Nullable BigDecimal b) {
        return a == b || a != null && b != null && a.compareTo(b) == 0;
    }

    /** Create a Decimal from an Double. */
    @NotNull static BigDecimal fromDouble(double f) {
        final long l = (long) f;
        if ((double) l == f) return BigDecimal.valueOf(l);
        return new BigDecimal(f);
    }

    /** Create a Decimal from an Integer. */
    @NotNull static BigDecimal fromInt(int n) {
        return BigDecimal.valueOf(n);
    }

    /** Create a Decimal from an String. */
    @NotNull static BigDecimal fromString(String s) {
        return new BigDecimal(s);
    }

    /** Returns true if a Decimal has a non zero not null value. */
    static boolean hasValue(@Nullable BigDecimal a) {
        return a != null && a.compareTo(BigDecimal.ZERO) != 0;
    }

    /**
     * Multiply a BigDecimal by a double and returns a new BigDecimal with the scale of the first
     * one.
     */
    @NotNull static BigDecimal multiply(@NotNull BigDecimal a, double factor) {
        return (factor == 0 ? BigDecimal.ZERO : a.multiply(BigDecimal.valueOf(factor))).setScale(a.scale(), ROUND_HALF_EVEN);
    }

    /** Returns 0 if the BigDecimal is null, the actual value otherwise. */
    @NotNull static BigDecimal notNull(@Nullable BigDecimal a) {
        return a == null ? BigDecimal.ZERO : a;
    }

    /** Scale numbers to the specified number of decimals and validate precision. */
    @Nullable static List<BigDecimal> scaleAndCheck(@NotNull List<BigDecimal> values, final boolean signed, final int precision, final int decimals) {
        for (int i = 0; i < values.size(); i++)
            values.set(i, scaleAndCheck("", values.get(i), signed, precision, decimals));
        return values;
    }

    /** Scale numbers to the specified number of decimals and validate precision. */
    @Nullable static Seq<BigDecimal> scaleAndCheck(String fieldName, @NotNull Seq<BigDecimal> values, final boolean signed, final int precision,
                                                   final int decimals) {
        return values.map(value -> scaleAndCheck(fieldName, value, signed, precision, decimals));
    }

    /** Scale number to the specified number of decimals and validate precision. */
    @Contract("_, !null, _, _, _ -> !null")
    @Nullable static BigDecimal scaleAndCheck(String name, @Nullable BigDecimal value, boolean signed, int precision, int decimals) {
        if (value == null) return null;
        final BigDecimal val = value.setScale(decimals, ROUND_HALF_EVEN);

        if (!signed && val.signum() == -1) negativeError(name, value.toPlainString());
        if (val.precision() > precision) precisionError(name, value.toPlainString(), val.precision(), precision);
        return val;
    }

    /** Scale numbers to the specified number of decimals and validate precision. */
    @Nullable static List<BigDecimal> scaleAndCheck(String fieldName, @NotNull Iterable<BigDecimal> values, final boolean signed, final int precision,
                                                    final int decimals) {        //
        return fromIterable(values).map(value -> scaleAndCheck(fieldName, value, signed, precision, decimals)).toList();
    }

    /** Check if a BigDecimal is zero . */
    static boolean isZero(@Nullable BigDecimal a) {
        return a != null && a.compareTo(BigDecimal.ZERO) == 0;
    }
}  // end class Decimals
