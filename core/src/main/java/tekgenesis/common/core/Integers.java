
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
import org.jetbrains.annotations.Nullable;

import static tekgenesis.common.exception.FieldValueException.negativeError;
import static tekgenesis.common.exception.FieldValueException.precisionError;

/**
 * Some utility functions to operate over {@link Integer} values.
 */
public interface Integers {

    //~ Methods ......................................................................................................................................

    /** Checks Integer sign and length. */
    @Contract("_, !null, _,_ -> !null")
    @Nullable static Integer checkSignedLength(String fieldName, @Nullable Integer value, boolean signed, int length) {
        return value == null ? null : checkSignedLength(fieldName, value.intValue(), signed, length);
    }

    /** Checks Integer sign and length. */
    @Contract("_, !null, _,_ -> !null")
    @Nullable static Long checkSignedLength(String fieldName, @Nullable Long value, boolean signed, int length) {
        return value == null ? null : checkSignedLength(fieldName, value.longValue(), signed, length);
    }

    /** Checks long sign and length. */
    static long checkSignedLength(String fieldName, final long value, final boolean signed, final int length) {
        if (!signed && value < 0) negativeError(fieldName, value);
        final int valueLength = getLength(value);
        if (valueLength > length) precisionError(fieldName, value, valueLength, length);
        return value;
    }

    /** Checks Integer sign and length. */
    static int checkSignedLength(String fieldName, final int value, final boolean signed, final int length) {
        // todo... do only for ideafix
        if (value == Integer.MAX_VALUE) return Integer.MAX_VALUE;
        if (!signed && value < 0) negativeError(fieldName, value);
        final int valueLength = getLength(value);
        if (valueLength > length) precisionError(fieldName, value, valueLength, length);
        return value;
    }
    /** Checks Integers sign and length. */
    static <T extends Iterable<Integer>> T checkSignedLength(String fieldName, final T values, final boolean signed, final int length) {
        for (final Integer value : values)
            checkSignedLength(fieldName, value, signed, length);
        return values;
    }

    /** Return the number of decimal digits of a long. */
    static int getLength(long value) {
        if (value == 0) return 1;
        int l = 0;
        for (long v = value; v != 0; v /= 10)
            l++;
        return l;
    }
}  // end class Integers
