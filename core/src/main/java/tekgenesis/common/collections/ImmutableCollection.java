
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.collections;

import java.util.Collection;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.core.Option;

import static tekgenesis.common.collections.Colls.immutable;
import static tekgenesis.common.core.Arrays.newArray;
import static tekgenesis.common.core.Option.some;

/**
 * * An {@link Collection} that is guaranteed to leave the underlying data unmodified.
 */
public abstract class ImmutableCollection<E> extends BaseSeq<E> implements Collection<E> {

    //~ Constructors .................................................................................................................................

    ImmutableCollection() {}

    //~ Methods ......................................................................................................................................

    // Every method that tries to modify the collection must throw {@link UnsupportedOperationException}

    @Override public final boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override public final boolean addAll(@NotNull Collection<? extends E> newElements) {
        throw new UnsupportedOperationException();
    }

    @Override public final void clear() {
        throw new UnsupportedOperationException();
    }
    @Override public boolean containsAll(@NotNull Collection<?> c) {
        for (final Object e : c)
            if (!contains(e)) return false;
        return true;
    }

    @Override public final boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override public final boolean removeAll(@NotNull Collection<?> oldElements) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean removeIf(Predicate<? super E> filter) {
        throw new UnsupportedOperationException();
    }

    @Override public final boolean retainAll(@NotNull Collection<?> elementsToKeep) {
        throw new UnsupportedOperationException();
    }

    @NotNull @Override public Object[] toArray() {
        final Object[] r = new Object[size()];
        copyToArray(r);
        return r;
    }

    @NotNull @Override public <T> T[] toArray(@NotNull T[] a) {
        final T[] r = a.length >= size() ? a : newArray(a.getClass().getComponentType(), size());
        copyToArray(r);
        if (a.length > size())  // noinspection AssignmentToNull
            r[size()] = null;
        return r;
    }

    @NotNull @Override public Option<E> getFirst()
    {
        return isEmpty() ? Option.empty() : super.getFirst();
    }

    protected void copyToArray(Object[] r) {
        int i = 0;
        for (final E e : this)
            r[i++] = e;
    }

    //~ Inner Classes ................................................................................................................................

    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    static final class Wrapper<T> extends ImmutableCollection<T> {
        private final Collection<T> original;

        public Wrapper(@NotNull final Collection<T> c) {
            original = c;
        }

        public boolean contains(@NotNull Object o) {
            return original.contains(o);
        }

        public boolean containsAll(@NotNull Collection<?> c) {
            return original.containsAll(c);
        }

        @NotNull @Override public ImmutableIterator<T> iterator() {
            return immutable(original.iterator());
        }

        public int size() {
            return original.size();
        }

        @Override public Option<Integer> getSize() {
            return some(original.size());
        }

        public boolean isEmpty() {
            return original.isEmpty();
        }
    }
}  // end class ImmutableCollection
