
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
 * This class represents 7 elements tuples.
 *
 * @see  Tuple
 */
public class Tuple7<T, U, V, W, X, Y, Z> extends Tuple6<T, U, V, W, X, Y> {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final Z seventh;

    //~ Constructors .................................................................................................................................

    /** constructor.* */
    Tuple7() {
        // noinspection ConstantConditions
        seventh = null;
    }

    @SuppressWarnings({ "WeakerAccess", "ConstructorWithTooManyParameters" })
    protected Tuple7(@NotNull T first, @NotNull U second, @NotNull V third, @NotNull W fourth, @NotNull X fifth, @NotNull Y sixth,
                     @NotNull Z seventh) {
        super(first, second, third, fourth, fifth, sixth);
        this.seventh = seventh;
    }

    //~ Methods ......................................................................................................................................

    /**
     * Returns the fourth element in the <code>Tuple</code>.
     *
     * @return  The fourth element in the <code>Tuple</code>.
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    public Z _7() {
        return seventh;
    }

    @Override public int arity() {
        return 7;
    }

    /** Returns the Tuple as a list of its elements . */
    @NotNull public ImmutableList<?> asList() {
        // noinspection unchecked
        return listOf(first(), second(), third(), fourth(), fifth(), sixth(), seventh());
    }

    @SuppressWarnings({ "unchecked", "EqualsBetweenInconvertibleTypes" })
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Tuple7 && super.equals(obj) && equal(seventh, ((Tuple7<T, U, V, W, X, Y, Z>) obj).seventh);
    }

    @Override public int hashCode() {
        return hashCodeAll(first(), second(), third(), fourth(), fifth(), sixth(), seventh());
    }

    /**
     * Returns the fourth element in the <code>Tuple</code>.
     *
     * @return  The fourth element in the <code>Tuple</code>.
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    public Z seventh() {
        return seventh;
    }

    @NotNull @Override public Object getLast() {
        return seventh();
    }

    @Override protected ToStringBuilder createToStringBuilder() {
        return super.createToStringBuilder().add(seventh());
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 7714536646731608944L;
}  // end class Tuple7
