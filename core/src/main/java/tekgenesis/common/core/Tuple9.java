
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
 * This class represents 9 elements tuples.
 *
 * @see  Tuple
 */
@SuppressWarnings("ClassTooDeepInInheritanceTree")
public class Tuple9<R, S, T, U, V, W, X, Y, Z> extends Tuple8<R, S, T, U, V, W, X, Y> {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final Z ninth;

    //~ Constructors .................................................................................................................................

    /** constructor.* */
    Tuple9() {
        // noinspection ConstantConditions
        ninth = null;
    }

    @SuppressWarnings({ "WeakerAccess", "ConstructorWithTooManyParameters" })
    protected Tuple9(@NotNull R first, @NotNull S second, @NotNull T third, @NotNull U fourth, @NotNull V fifth, @NotNull W sixth, @NotNull X seventh,
                     @NotNull Y eighth, @NotNull Z ninth) {
        super(first, second, third, fourth, fifth, sixth, seventh, eighth);
        this.ninth = ninth;
    }

    //~ Methods ......................................................................................................................................

    /**
     * Returns the fourth element in the <code>Tuple</code>.
     *
     * @return  The fourth element in the <code>Tuple</code>.
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    public Z _9() {
        return ninth;
    }

    @Override public int arity() {
        return 9;
    }

    /** Returns the Tuple as a list of its elements . */
    @NotNull public ImmutableList<?> asList() {
        // noinspection unchecked
        return listOf(first(), second(), third(), fourth(), fifth(), sixth(), seventh(), eighth(), ninth);
    }

    @SuppressWarnings({ "unchecked", "EqualsBetweenInconvertibleTypes" })
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Tuple9 && super.equals(obj) && equal(ninth, ((Tuple9<R, S, T, U, V, W, X, Y, Z>) obj).ninth);
    }

    @Override public int hashCode() {
        return hashCodeAll(first(), second(), third(), fourth(), fifth(), sixth(), seventh(), eighth(), ninth);
    }

    /**
     * Returns the fourth element in the <code>Tuple</code>.
     *
     * @return  The fourth element in the <code>Tuple</code>.
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    public Z ninth() {
        return ninth;
    }

    @NotNull @Override public Object getLast() {
        return ninth();
    }

    @Override protected ToStringBuilder createToStringBuilder() {
        return super.createToStringBuilder().add(ninth());
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = -2623499060427650281L;
}  // end class Tuple9
