
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
 * This class represents 8 elements tuples.
 *
 * @see  Tuple
 */
@SuppressWarnings("ClassTooDeepInInheritanceTree")
public class Tuple8<S, T, U, V, W, X, Y, Z> extends Tuple7<S, T, U, V, W, X, Y> {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final Z eighth;

    //~ Constructors .................................................................................................................................

    /** constructor.* */
    Tuple8() {
        // noinspection ConstantConditions
        eighth = null;
    }

    @SuppressWarnings({ "WeakerAccess", "ConstructorWithTooManyParameters" })
    protected Tuple8(@NotNull S first, @NotNull T second, @NotNull U third, @NotNull V fourth, @NotNull W fifth, @NotNull X sixth, @NotNull Y seventh,
                     @NotNull Z eighth) {
        super(first, second, third, fourth, fifth, sixth, seventh);
        this.eighth = eighth;
    }

    //~ Methods ......................................................................................................................................

    /**
     * Returns the fourth element in the <code>Tuple</code>.
     *
     * @return  The fourth element in the <code>Tuple</code>.
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    public Z _8() {
        return eighth;
    }

    @Override public int arity() {
        return 8;
    }

    /** Returns the Tuple as a list of its elements . */
    @NotNull public ImmutableList<?> asList() {
        // noinspection unchecked
        return listOf(first(), second(), third(), fourth(), fifth(), sixth(), seventh(), eighth);
    }

    /**
     * Returns the fourth element in the <code>Tuple</code>.
     *
     * @return  The fourth element in the <code>Tuple</code>.
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    public Z eighth() {
        return eighth;
    }

    @SuppressWarnings({ "unchecked", "EqualsBetweenInconvertibleTypes" })
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Tuple8 && super.equals(obj) && equal(eighth, ((Tuple8<S, T, U, V, W, X, Y, Z>) obj).eighth);
    }

    @Override public int hashCode() {
        return hashCodeAll(first(), second(), third(), fourth(), fifth(), sixth(), seventh(), eighth);
    }

    @NotNull @Override public Object getLast() {
        return eighth();
    }

    @Override protected ToStringBuilder createToStringBuilder() {
        return super.createToStringBuilder().add(eighth());
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = -4669933669333577328L;
}  // end class Tuple8
