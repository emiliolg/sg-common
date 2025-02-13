
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.Predefined;

import static java.util.Arrays.asList;

import static tekgenesis.common.Predefined.equalElements;

/**
 * An Abstract Base Sequence that implements {@link Object} methods, and other non default methods.
 */
public abstract class BaseSeq<E> implements Seq<E> {

    //~ Methods ......................................................................................................................................

    /** Appends given elements. */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public final Seq<E> append(final E... elements) {
        return append(asList(elements));
    }

    @Override
    @SuppressWarnings({ "RedundantMethodOverride", "EmptyMethod" })
    public boolean contains(@NotNull Object toFind) {
        return Seq.super.contains(toFind);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        return obj instanceof Iterable && equalElements(this, (Iterable<E>) obj);
    }

    @Override public int hashCode() {
        return Predefined.hashCodeAll(this);
    }

    @Override
    @SuppressWarnings({ "RedundantMethodOverride", "EmptyMethod" })
    public int size() {
        return Seq.super.size();
    }

    @Override public String toString() {
        return mkString();
    }

    @Override
    @SuppressWarnings({ "RedundantMethodOverride", "EmptyMethod" })
    public boolean isEmpty() {
        return Seq.super.isEmpty();
    }

    //~ Methods ......................................................................................................................................

    static <E> void tsort(E e, Set<E> elements, Set<E> visited, Function<E, Iterable<E>> dependenciesFor) {
        for (final E dependency : dependenciesFor.apply(e)) {
            if (!visited.contains(dependency)) {
                visited.add(dependency);
                tsort(dependency, elements, visited, dependenciesFor);
            }
            // If some day loop detection is important this can be enabled with a flag
            // else if (!elements.contains(dependency)){
            // throw loop
            // }
        }
        elements.add(e);
    }

    //~ Inner Classes ................................................................................................................................

    static class SeqSet<T> extends BaseSeq<T> {
        @NotNull private final List<Iterable<? extends T>> iterables;

        SeqSet() {
            iterables = new ArrayList<>();
        }

        /** Create a SeqSet. */
        SeqSet(final Iterable<? extends T> es) {
            this();
            append(es);
        }

        public SeqSet<T> append(@NotNull Iterable<? extends T> iterable) {
            iterables.add(iterable);
            return this;
        }

        @NotNull @Override public ImmutableIterator<T> iterator() {
            return new IteratorBase<T>() {
                @Override public boolean hasNext() {
                    if (!nextExists && currentIterator.hasNext()) {
                        nextValue  = currentIterator.next();
                        nextExists = true;
                    }
                    if (nextExists) return true;
                    if (iterablesIterator.hasNext()) {
                        currentIterator = iterablesIterator.next().iterator();
                        return hasNext();
                    }
                    return false;
                }

                final Iterable<Iterable<? extends T>>         its               = iterables;
                private Iterator<? extends T>                 currentIterator   = Colls.emptyIterator();
                private final Iterator<Iterable<? extends T>> iterablesIterator = its.iterator();
            };
        }

        @Override public int size() {
            int result = 0;
            for (final Iterable<? extends T> it : iterables) {
                if (it != null) result += Colls.size(it);
            }
            return result;
        }
    }  // end class SeqSet
}  // end class BaseSeq
