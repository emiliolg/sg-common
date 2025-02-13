
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

import tekgenesis.common.collections.ImmutableList;

import static tekgenesis.common.collections.Colls.listOf;
import static tekgenesis.common.core.Constants.HASH_SALT;

/**
 * This class implements and IntIntTuple.
 *
 * @see  Tuple
 */
public class IntIntTuple implements Tuple<Integer, Integer>, Serializable {

    //~ Instance Fields ..............................................................................................................................

    private final int first;
    private final int second;

    //~ Constructors .................................................................................................................................

    /** constructor.* */
    IntIntTuple() {
        first  = 0;
        second = 0;
    }

    protected IntIntTuple(int first, int second) {
        this.first  = first;
        this.second = second;
    }

    //~ Methods ......................................................................................................................................

    /** Returns the first element in the <code>Tuple</code>. */
    @NotNull public Integer _1() {
        return first;
    }
    /** Returns the second element in the <code>Tuple</code>. */
    @NotNull public Integer _2() {
        return second;
    }

    @Override public Tuple<Integer, Integer> append(Object third) {
        return Tuple.tuple(first, second, third);
    }

    @Override public int arity() {
        return 2;
    }

    /** Returns the Tuple as a list of its elements . */
    @NotNull public ImmutableList<?> asList() {
        return listOf(first, second);
    }

    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        return this == obj || obj instanceof IntIntTuple && first == ((IntIntTuple) obj).first && second == ((IntIntTuple) obj).second;
    }

    /** Returns the first element in the <code>Tuple</code>. */
    @NotNull public Integer first() {
        return first;
    }

    /**
     * Returns a hash code for this <code>Tuple</code>. This implementation uses the same algorithm
     * in the documentation for the <tt>List.hashCode</tt> method.
     *
     * @return  a hash code value for this object.
     */
    public int hashCode() {
        return first + HASH_SALT * second;
    }

    /** Returns the second element in the <code>Tuple</code>. */
    @NotNull public Integer second() {
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
        return "(" + first + "," + second + ")";
    }

    /** Return the last element in the tuple. */
    @NotNull public Integer getLast() {
        return second;
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 7439627913276511204L;
}  // end class IntIntTuple
