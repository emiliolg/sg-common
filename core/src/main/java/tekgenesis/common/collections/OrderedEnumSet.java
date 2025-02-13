
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.collections;

import java.util.*;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import static java.util.Arrays.asList;

/**
 * An Immutable EnumSet that returns elements in the way they have been specified in the
 * constructor.
 */
public class OrderedEnumSet<E extends Enum<E>> extends AbstractSet<E> {

    //~ Instance Fields ..............................................................................................................................

    private final EnumSet<E> enumSet;
    private final List<E>    list;

    //~ Constructors .................................................................................................................................

    /** Construct an ordered EnumSet. */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public OrderedEnumSet(@NotNull E... enums) {
        enumSet = EnumSet.noneOf(enums[0].getDeclaringClass());
        Collections.addAll(enumSet, enums);
        list = asList(enums);
    }

    //~ Methods ......................................................................................................................................

    @Override public boolean contains(final Object o) {
        return enumSet.contains(o);
    }

    @Override public boolean containsAll(@NotNull final Collection<?> c) {
        return enumSet.containsAll(c);
    }

    @NotNull @Override public Iterator<E> iterator() {
        return list.iterator();
    }

    @Override public int size() {
        return list.size();
    }

    @Override public Spliterator<E> spliterator() {
        return list.spliterator();
    }

    @Override public Stream<E> stream() {
        return list.stream();
    }
}  // end class OrderedEnumSet
