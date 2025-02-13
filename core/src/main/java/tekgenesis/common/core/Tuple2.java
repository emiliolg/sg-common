
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.Predefined;
import tekgenesis.common.collections.ImmutableList;

import static tekgenesis.common.Predefined.hashCodeAll;
import static tekgenesis.common.collections.Colls.listOf;

/**
 * This class represents 2 elements tuples.
 *
 * @see  Tuple
 */
@SuppressWarnings("WeakerAccess")
public class Tuple2<T, U> implements Tuple<T, U>, Serializable {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final T first;
    @NotNull private final U second;

    //~ Constructors .................................................................................................................................

    /** constructor.* */
    @SuppressWarnings("ConstantConditions")
    Tuple2() {
        first  = null;
        second = null;
    }

    protected Tuple2(@NotNull T first, @NotNull U second) {
        this.first  = first;
        this.second = second;
    }

    //~ Methods ......................................................................................................................................

    @NotNull @Override public T _1() {
        return first;
    }
    @NotNull @Override public U _2() {
        return second;
    }

    @Override public int arity() {
        return 2;
    }

    @NotNull @Override public ImmutableList<?> asList() {
        // noinspection unchecked
        return listOf(first, second);
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Tuple) {
            final Tuple<?, ?> that = (Tuple<?, ?>) obj;
            return arity() == that.arity() && _1().equals(that._1()) && _2().equals(that._2());
        }
        return false;
    }

    @NotNull @Override public T first() {
        return first;
    }

    /**
     * Returns a hash code for this <code>Tuple</code>. This implementation uses the same algorithm
     * in the documentation for the <tt>List.hashCode</tt> method.
     *
     * @return  a hash code value for this object.
     */
    public int hashCode() {
        return hashCodeAll(first(), second());
    }

    @NotNull @Override public U second() {
        return second;
    }

    /**
     * Returns a string representation of this <code>Tuple</code> consisting of the n elements of
     * the tuple enclosed by parenthesis (<tt>"()"</tt>) and separated by <tt>", "</tt> (comma and
     * space). Elements are converted to strings as by <tt>String.valueOf(Object)</tt>.
     *
     * @return  a string representation of this tuple.
     */
    public final String toString() {
        return createToStringBuilder().build();
    }

    @NotNull @Override public Object getLast() {
        return second();
    }

    ToStringBuilder createToStringBuilder() {
        return Predefined.createToStringBuilder("Tuple").add(first()).add(second());
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 200705311430L;
}  // end class Tuple2
