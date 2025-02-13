
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
 * This class represents 4 elements tuples.
 *
 * @see  Tuple
 */
public class Tuple5<T, U, V, W, X> extends Tuple4<T, U, V, W> {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final X fifth;

    //~ Constructors .................................................................................................................................

    /** constructor.* */
    Tuple5() {
        // noinspection ConstantConditions
        fifth = null;
    }

    @SuppressWarnings("WeakerAccess")
    protected Tuple5(@NotNull T first, @NotNull U second, @NotNull V third, @NotNull W fourth, @NotNull X fifth) {
        super(first, second, third, fourth);
        this.fifth = fifth;
    }

    //~ Methods ......................................................................................................................................

    /**
     * Returns the fourth element in the <code>Tuple</code>.
     *
     * @return  The fourth element in the <code>Tuple</code>.
     */
    @NotNull public X _5() {
        return fifth;
    }

    @Override public int arity() {
        return 5;
    }

    /** Returns the Tuple as a list of its elements . */
    @NotNull public ImmutableList<?> asList() {
        // noinspection unchecked
        return listOf(first(), second(), third(), fourth(), fifth());
    }

    @SuppressWarnings({ "unchecked", "EqualsBetweenInconvertibleTypes" })
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Tuple5 && super.equals(obj) && equal(fifth, ((Tuple5<T, U, V, W, X>) obj).fifth);
    }

    /**
     * Returns the fourth element in the <code>Tuple</code>.
     *
     * @return  The fourth element in the <code>Tuple</code>.
     */
    @NotNull public X fifth() {
        return fifth;
    }

    @Override public int hashCode() {
        return hashCodeAll(first(), second(), third(), fourth(), fifth());
    }

    @NotNull @Override public Object getLast() {
        return fifth();
    }

    @Override protected ToStringBuilder createToStringBuilder() {
        return super.createToStringBuilder().add(fifth());
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 7714536646731608944L;
}  // end class Tuple5
