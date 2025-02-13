
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static tekgenesis.common.exception.FieldValueException.negativeError;

/**
 * Some utility functions to operate over {@link Double} values.
 */
public interface Reals {

    //~ Methods ......................................................................................................................................

    /** Checks Double sign. */
    @Contract("_,!null, _ -> !null")
    @Nullable static Double checkSigned(String fieldName, @Nullable Double value, boolean signed) {
        if (value != null && !signed && value < 0) negativeError(fieldName, value);
        return value;
    }

    /** Checks Doubles sign. */
    static <T extends Iterable<Double>> T checkSigned(String fieldName, @NotNull T values, final boolean signed) {
        for (final Double value : values)
            checkSigned(fieldName, value, signed);
        return values;
    }
}
