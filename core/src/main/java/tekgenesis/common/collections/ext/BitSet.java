
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.collections.ext;

import java.util.*;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.Predefined;
import tekgenesis.common.collections.ImmutableIterator;
import tekgenesis.common.core.IntIntTuple;
import tekgenesis.common.core.Strings;
import tekgenesis.common.core.Tuple;

import static java.lang.Integer.parseInt;

/**
 * A Bit Set that implements a {@link Set}.
 */
public class BitSet extends AbstractSet<Integer> {

    //~ Instance Fields ..............................................................................................................................

    private final java.util.BitSet data;

    //~ Constructors .................................................................................................................................

    /** Create a BitSet. */
    @SuppressWarnings("WeakerAccess")
    public BitSet() {
        data = new java.util.BitSet();
    }

    /** Create a BitSet. */
    public BitSet(int size) {
        data = new java.util.BitSet(size);
    }

    //~ Methods ......................................................................................................................................

    @Override public boolean add(Integer index) {
        if (data.get(index)) return false;
        data.set(index);
        return true;
    }

    @Override public void clear() {
        data.clear();
    }

    @Override public boolean contains(Object o) {
        return o instanceof Integer && data.get((Integer) o);
    }

    @Override public boolean equals(Object o) {
        return o instanceof BitSet && data.equals(((BitSet) o).data);
    }

    /** Returns the value of the bit with the specified index. The value */
    public boolean get(int index) {
        return data.get(index);
    }

    @Override public int hashCode() {
        return data.hashCode();
    }

    /** Returns true if this Set and the one specified as a parameter have an element in common. */
    public boolean intersects(BitSet other) {
        return data.intersects(other.data);
    }

    @NotNull @Override public Iterator<Integer> iterator() {
        return new ImmutableIterator<Integer>() {
            int next = data.nextSetBit(0);

            @Override public boolean hasNext() {
                return next != -1;
            }

            @Override public Integer next() {
                final int result = next;
                next = data.nextSetBit(next + 1);
                return result;
            }
        };
    }

    /** Return a view of this set as a set of the specified type of elements. */
    public <E> Set<E> mapToSet(Function<E, Integer> in, Function<Integer, E> out) {
        return new MappedSet<>(this, in, out);
    }

    @Override public boolean remove(Object o) {
        final int index = (Integer) o;
        if (!data.get(index)) return false;
        data.clear(index);
        return true;
    }

    /** Sets the bit at the specified index to {@code true}. */
    public void set(int index) {
        data.set(index);
    }

    /** Sets the bit at the specified index to the specified value. */
    public void set(int index, boolean value) {
        data.set(index, value);
    }

    /** Set all bits in the specified range. */
    @SuppressWarnings("WeakerAccess")
    public void set(int from, int to, boolean value) {
        data.set(from, to, value);
    }

    @Override public int size() {
        return data.size();
    }

    /** If the BitSet is representable by a range returns the range, else returns null. */
    @Nullable public IntIntTuple toRange() {
        int from = -1;
        int prev = -1;
        for (final Integer i : this) {
            if (prev == -1) from = i;
            else if (prev != i - 1) return null;
            prev = i;
        }
        return from != -1 ? Tuple.tuple(from, prev) : null;
    }

    @Override public String toString() {
        final StringBuilder result = new StringBuilder();
        int                 from   = -1;
        int                 prev   = -1;
        for (final Integer i : this) {
            if (prev == -1) from = i;
            else if (prev != i - 1) {
                emit(result, from, prev);
                from = i;
            }
            prev = i;
        }
        if (prev != -1) emit(result, from, prev);

        return result.toString();
    }  // end method toString

    /** Sets all the bits to the specified value. */
    public void setAll(boolean value) {
        data.set(0, size(), value);
    }

    @Override public boolean isEmpty() {
        return data.isEmpty();
    }

    private void emit(StringBuilder result, int from, int prev) {
        if (result.length() != 0) result.append(',');
        if (from != prev) result.append(from).append('-').append(prev);
        else result.append(prev);
    }

    //~ Methods ......................................................................................................................................

    /**
     * An utility function that will cluster a group of {@link List<String>} and create a new list
     * where all the elements in an specified column are replaces by a BitSet.
     */
    public static List<List<String>> cluster(Iterable<? extends List<String>> input, final int column) {
        final ArrayList<List<String>> result = new ArrayList<>();

        final Map<List<String>, BitSet> map = new LinkedHashMap<>();
        for (final List<String> line : input) {
            final String col = line.get(column);
            if (col.isEmpty()) result.add(line);
            else {
                final int n = parseInt(col);
                line.set(column, "");

                BitSet set = map.get(line);
                if (set == null) set = new BitSet();
                set.add(n);
                map.put(line, set);
            }
        }

        result.ensureCapacity(map.size());
        for (final Map.Entry<List<String>, BitSet> e : map.entrySet()) {
            final List<String> list = e.getKey();
            list.set(column, e.getValue().toString());
            result.add(list);
        }
        return result;
    }

    /** Creates a BitSet based on an String. */
    public static BitSet valueOf(String str) {
        final BitSet result = new BitSet();
        if (Predefined.isEmpty(str)) return result;
        final List<String> parts = Strings.split(str, ',');
        for (final String part : parts) {
            final int dash = part.indexOf('-');
            if (dash == -1) result.add(Integer.valueOf(part));
            else {
                final int from = Integer.valueOf(part.substring(0, dash));
                final int to   = Integer.valueOf(part.substring(dash + 1));
                result.set(from, to + 1, true);
            }
        }
        return result;
    }
}  // end class BitSet
