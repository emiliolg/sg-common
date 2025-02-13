
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
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.core.Option;

import static tekgenesis.common.Predefined.equalElements;
import static tekgenesis.common.collections.Colls.immutable;
import static tekgenesis.common.core.Option.some;

/**
 * A {@link Set} that is guaranteed to leave the underlying data unmodified.
 */
@SuppressWarnings("EqualsAndHashcode")  // view super
public abstract class ImmutableSet<E> extends ImmutableCollection<E> implements Set<E> {

    //~ Constructors .................................................................................................................................

    /** Default Constructor. */
    protected ImmutableSet() {}

    //~ Methods ......................................................................................................................................

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        return o == this || o instanceof Set && equalElements(this, (Set<Object>) o);
    }

    @Override public ImmutableSet<E> toSet() {
        return this;
    }

    //~ Inner Classes ................................................................................................................................

    static final class Wrapper<T> extends ImmutableSet<T> {
        private final Set<T> original;

        public Wrapper(@NotNull final Set<T> c) {
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
}  // end class ImmutableSet
