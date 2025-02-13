
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
@SuppressWarnings("WeakerAccess")
public class Tuple4<T, U, V, W> extends Tuple3<T, U, V> {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final W fourth;

    //~ Constructors .................................................................................................................................

    /** constructor.* */
    Tuple4() {
        // noinspection ConstantConditions
        fourth = null;
    }

    protected Tuple4(@NotNull T first, @NotNull U second, @NotNull V third, @NotNull W fourth) {
        super(first, second, third);
        this.fourth = fourth;
    }

    //~ Methods ......................................................................................................................................

    /** Returns the fourth element in the <code>Tuple</code>. */
    public W _4() {
        return fourth;
    }
    @Override public int arity() {
        return 4;
    }

    /** Returns the Tuple as a list of its elements . */
    @NotNull public ImmutableList<?> asList() {
        // noinspection unchecked
        return listOf(first(), second(), third(), fourth());
    }

    @SuppressWarnings({ "unchecked", "EqualsBetweenInconvertibleTypes" })
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Tuple4 && super.equals(obj) && equal(fourth, ((Tuple4<T, U, V, W>) obj).fourth);
    }

    /**
     * Returns the fourth element in the <code>Tuple</code>.
     *
     * @return  The fourth element in the <code>Tuple</code>.
     */
    @NotNull public W fourth() {
        return fourth;
    }

    @Override public int hashCode() {
        return hashCodeAll(first(), second(), third(), fourth());
    }

    @NotNull @Override public Object getLast() {
        return fourth();
    }

    @Override protected ToStringBuilder createToStringBuilder() {
        return super.createToStringBuilder().add(fourth());
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 7714536646731608944L;
}  // end class Tuple4
