
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.collections.ImmutableList;

import static tekgenesis.common.Predefined.equal;
import static tekgenesis.common.Predefined.hashCodeAll;
import static tekgenesis.common.collections.Colls.listOf;

/**
 * This class represents 3 elements tuples.
 *
 * @see  Tuple
 */
public class Tuple3<T, U, V> extends Tuple2<T, U> {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final V third;

    //~ Constructors .................................................................................................................................

    /** constructor.* */
    Tuple3() {
        // noinspection ConstantConditions
        third = null;
    }

    Tuple3(@NotNull T first, @NotNull U second, @NotNull V third) {
        super(first, second);
        this.third = third;
    }

    //~ Methods ......................................................................................................................................

    /** Returns the third element in the <code>Tuple</code>. */
    @NotNull public V _3() {
        return third;
    }

    @Override public int arity() {
        return 3;
    }

    /** Returns the Tuple as a list of its elements . */
    @NotNull public ImmutableList<?> asList() {
        // noinspection unchecked
        return listOf(first(), second(), third);
    }

    @SuppressWarnings({ "unchecked", "EqualsBetweenInconvertibleTypes" })
    public boolean equals(Object obj) {
        return this == obj || super.equals(obj) && equal(third, ((Tuple3<T, U, V>) obj).third);
    }

    public int hashCode() {
        return hashCodeAll(first(), second(), third());
    }

    /** Returns the third element in the <code>Tuple</code>. */
    @NotNull public V third() {
        return third;
    }

    @NotNull @Override public Object getLast() {
        return third();
    }

    @Override ToStringBuilder createToStringBuilder() {
        return super.createToStringBuilder().add(third());
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 1009450617304769215L;
}  // end class Tuple3
