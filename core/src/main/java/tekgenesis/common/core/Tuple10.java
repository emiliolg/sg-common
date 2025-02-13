
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
 * This class represents 10 elements tuples.
 *
 * @see  Tuple
 */
@SuppressWarnings("ClassTooDeepInInheritanceTree")
public class Tuple10<Q, R, S, T, U, V, W, X, Y, Z> extends Tuple9<Q, R, S, T, U, V, W, X, Y> {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final Z tenth;

    //~ Constructors .................................................................................................................................

    Tuple10() {
        // noinspection ConstantConditions
        tenth = null;
    }

    @SuppressWarnings({ "WeakerAccess", "ConstructorWithTooManyParameters" })
    protected Tuple10(@NotNull Q first, @NotNull R second, @NotNull S third, @NotNull T fourth, @NotNull U fifth, @NotNull V sixth,
                      @NotNull W seventh, @NotNull X eighth, @NotNull Y ninth, @NotNull Z tenth) {
        super(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth);
        this.tenth = tenth;
    }

    //~ Methods ......................................................................................................................................

    /**
     * Returns the fourth element in the <code>Tuple</code>.
     *
     * @return  The fourth element in the <code>Tuple</code>.
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    public Z _10() {
        return tenth;
    }

    @Override public int arity() {
        return 10;
    }

    /** Returns the Tuple as a list of its elements . */
    @NotNull public ImmutableList<?> asList() {
        // noinspection unchecked
        return listOf(first(), second(), third(), fourth(), fifth(), sixth(), seventh(), eighth(), ninth(), tenth);
    }

    @SuppressWarnings({ "unchecked", "EqualsBetweenInconvertibleTypes" })
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Tuple10 && super.equals(obj) && equal(tenth, ((Tuple10<Q, R, S, T, U, V, W, X, Y, Z>) obj).tenth);
    }

    @Override public int hashCode() {
        return hashCodeAll(first(), second(), third(), fourth(), fifth(), sixth(), seventh(), eighth(), ninth(), tenth);
    }

    /**
     * Returns the tenth element in the <code>Tuple</code>.
     *
     * @return  The tenth element in the <code>Tuple</code>.
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    public Z tenth() {
        return tenth;
    }

    @NotNull @Override public Object getLast() {
        return tenth();
    }

    @Override protected ToStringBuilder createToStringBuilder() {
        return super.createToStringBuilder().add(tenth());
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 6258509074103239181L;
}  // end class Tuple10
