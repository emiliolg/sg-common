
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

/**
 * Add handy operation to a Comparable If you implements this methods instead of implementing
 * Comparable you get all the additional methods.
 */
public interface RichComparable<T> extends Comparable<T> {

    //~ Methods ......................................................................................................................................

    /** isGreaterThan than that. */
    default boolean isGreaterThan(T that) {
        return compareTo(that) > 0;
    }

    /** isLessThan than that. */
    default boolean isLessThan(T that) {
        return compareTo(that) < 0;
    }

    /** isGreaterOrEqualTo that. */
    default boolean isGreaterOrEqualTo(T that) {
        return this == that || compareTo(that) >= 0;
    }

    /** isLessOrEqualTo that. */
    default boolean isLessOrEqualTo(T that) {
        return this == that || compareTo(that) <= 0;
    }
}
