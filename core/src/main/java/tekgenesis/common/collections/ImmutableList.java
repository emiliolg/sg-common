
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.collections;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.Predefined;
import tekgenesis.common.core.Constants;
import tekgenesis.common.core.Mutable;
import tekgenesis.common.core.Option;
import tekgenesis.common.core.StepResult;
import tekgenesis.common.core.Tuple;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.Predefined.checkArgument;
import static tekgenesis.common.Predefined.ensureNotNull;
import static tekgenesis.common.Predefined.equal;
import static tekgenesis.common.collections.Colls.emptyIterator;
import static tekgenesis.common.collections.Colls.immutable;
import static tekgenesis.common.collections.Colls.listOf;
import static tekgenesis.common.core.Constants.EMPTY_OBJECT_ARRAY;
import static tekgenesis.common.core.Option.option;
import static tekgenesis.common.core.Option.some;

/**
 * * An {@link Collection} that is guaranteed to leave the underlying data unmodified.
 */
public abstract class ImmutableList<E> extends ImmutableCollection<E> implements List<E>, Serializable {

    //~ Constructors .................................................................................................................................

    /** Default Constructor. */
    protected ImmutableList() {}

    //~ Methods ......................................................................................................................................

    @Override public final void add(final int n, final E e) {
        throw new UnsupportedOperationException();
    }

    @Override public final boolean addAll(final int n, @NotNull final Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @NotNull @Override public ImmutableList<E> drop(final int n) {
        return slice(n, size());
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        return o == this || o instanceof List && Predefined.equalElements(this, (List<Object>) o);
    }

    @Override
    @SuppressWarnings("RedundantMethodOverride")
    public int hashCode() {
        return Predefined.hashCodeAll(this);
    }
    @NotNull @Override public ImmutableIterator<E> iterator() {
        return originalListIterator(0);
    }

    @NotNull @Override public final ImmutableListIterator<E> listIterator() {
        return originalListIterator(0);
    }

    @NotNull @Override public final ImmutableListIterator<E> listIterator(final int index) {
        return originalListIterator(index);
    }

    @Override public final E remove(final int n) {
        throw new UnsupportedOperationException();
    }
    @Override public void replaceAll(UnaryOperator<E> operator) {
        throw new UnsupportedOperationException();
    }

    @Override public final E set(final int n, final E e) {
        throw new UnsupportedOperationException();
    }

    @NotNull @Override public ImmutableList<E> slice(final int fromIndex, final int toIndex) {
        if (!checkRange(fromIndex, toIndex)) return empty();
        final int fence = Math.min(toIndex, size());
        return fence - fromIndex == 1 ? listOf(get(fromIndex)) : sublistOf(fromIndex, fence);
    }

    @Override public void sort(Comparator<? super E> c) {
        throw new UnsupportedOperationException();
    }

    @NotNull @Override public ImmutableList<E> subList(int fromIndex, int toIndex) {
        if (!checkRange(fromIndex, toIndex) || toIndex > size())
            throw new IndexOutOfBoundsException("subList(" + fromIndex + ", " + toIndex + ") applied to list of size " + size());
        return toIndex == fromIndex ? empty() : toIndex - fromIndex == 1 ? listOf(get(fromIndex)) : sublistOf(fromIndex, toIndex);
    }

    @NotNull @Override public ImmutableList<E> toList() {
        return this;
    }

    @NotNull @Override public Option<E> getFirst() {
        return isEmpty() ? Option.empty() : Option.some(get(0));
    }
    @Override public Option<Integer> getSize() {
        return some(size());
    }

    protected abstract ImmutableListIterator<E> originalListIterator(final int index);

    boolean checkRange(int from, int to) {
        return from >= 0 && from <= to && from <= size();
    }

    @NotNull abstract ImmutableList<E> sublistOf(int from, int fence);

    //~ Methods ......................................................................................................................................

    /** Build it calling the accumulator function. */
    public static <E> ImmutableList<E> build(Consumer<ImmutableList.Builder<E>> accumulator) {
        final Builder<E> builder = builder(EXPECTED_SIZE);
        accumulator.accept(builder);
        return builder.build();
    }

    /** Returns array new builder. */
    public static <E> Builder<E> builder() {
        return builder(EXPECTED_SIZE);
    }
    /** Returns array new builder. */
    public static <E> Builder<E> builder(int expectedSize) {
        return new Builder<>(expectedSize);
    }

    /** Build an empty Immutable list. */
    public static <E> ImmutableList<E> empty() {
        return cast(Immutables.EMPTY_LIST);
    }

    /** Immutable list build with the elements of the array. */
    public static <T> ImmutableList<T> fromArray(@Nullable T[] array) {
        if (array == null) return empty();
        final Builder<T> builder = builder(array.length);
        builder.addAll(array);
        return builder.build();
    }

    /** Immutable list build with the elements of the Iterable. */
    public static <E> ImmutableList<E> fromIterable(Iterable<? extends E> iterable) {
        final ImmutableList.Builder<E> builder = builder(iterable instanceof Collection ? ((Collection<?>) iterable).size() : EXPECTED_SIZE);
        for (final E t : iterable)
            builder.add(t);
        return builder.build();
    }

    /** Build an Immutable list of one element. */
    public static <E> ImmutableList<E> of(@Nullable E e) {
        return new Singleton<>(e);
    }

    /** Build an Immutable list of 2 elements. */
    public static <E> ImmutableList<E> of(@Nullable E e1, @Nullable E e2) {
        return ImmutableList.<E>builder(2).add(e1).add(e2).build();
    }

    /** Build an Immutable list of n elements. */
    @SafeVarargs public static <E> ImmutableList<E> of(@Nullable E first, E... rest) {
        final Builder<E> builder = builder(rest.length + 1);
        builder.add(first);
        for (final E e : rest)
            builder.add(e);
        return builder.build();
    }

    /** Build an Immutable list of 3 elements. */
    public static <E> ImmutableList<E> of(@Nullable E e1, @Nullable E e2, @Nullable E e3) {
        return ImmutableList.<E>builder(3).add(e1).add(e2).add(e3).build();
    }

    /** Build an Immutable list of 4 elements. */
    public static <E> ImmutableList<E> of(@Nullable E e1, @Nullable E e2, @Nullable E e3, @Nullable E e4) {
        return ImmutableList.<E>builder(4).add(e1).add(e2).add(e3).add(e4).build();
    }

    /** Build an Immutable list of 5 elements. */
    public static <E> ImmutableList<E> of(@Nullable E e1, @Nullable E e2, @Nullable E e3, @Nullable E e4, @Nullable E e5) {
        return ImmutableList.<E>builder(4).add(e1).add(e2).add(e3).add(e4).add(e5).build();
    }

    static <E> ImmutableList<E> buildReversed(Traversable<E> t) {
        return t.getSize().map(sz -> {
                if (sz <= 1) return t.getFirst().map(ImmutableList::of).orElse(empty());
                final Object[]    content = new Object[sz];
                final Mutable.Int index   = new Mutable.Int(sz);
                t.forEach(e -> content[index.decrement()] = e);
                return new Array<E>(cast(content));
            }).orElseGet(() -> {
            final LinkedList<E> reverted = new LinkedList<>();
            t.forEach(reverted::addFirst);
            if (reverted.isEmpty()) return empty();
            if (reverted.size() == 1) return listOf(reverted.getFirst());
            return new ImmutableList.Wrapper<>(reverted);
        });
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 2250424769803549568L;

    static final int EXPECTED_SIZE = 4;

    //~ Inner Classes ................................................................................................................................

    static class Array<E> extends ImmutableList<E> implements RandomAccess, Serializable {
        @NotNull private final E[] array;
        private final int          from;
        private final int          to;

        Array() {
            array = cast(Constants.EMPTY_OBJECT_ARRAY);
            from  = to = 0;
        }
        Array(E[] array) {
            this(array, 0, array.length);
        }
        Array(E[] array, int from, int to) {
            this.array = ensureNotNull(array);
            this.from  = from;
            this.to    = to;
        }

        @Override public boolean contains(@NotNull Object o) {
            return indexOf(o) != -1;
        }

        @Override public void forEach(@NotNull Consumer<? super E> action) {
            ensureNotNull(action);
            for (int i = from; i < to; i++)
                action.accept(array[i]);
        }

        @Override public E get(int index) {
            return array[index + from];
        }

        @Override public int indexOf(Object o) {
            for (int i = from; i < to; i++) {
                final E obj = array[i];
                if (o == obj || o != null && o.equals(obj)) return i - from;
            }
            return -1;
        }

        @Override public int lastIndexOf(Object o) {
            for (int i = to - 1; i >= from; i--) {
                final E obj = array[i];
                if (o == obj || o != null && o.equals(obj)) return i - from;
            }
            return -1;
        }

        @Override public int size() {
            return to - from;
        }

        @Override public Spliterator<E> spliterator() {
            return Spliterators.spliterator(array, from, to, Spliterator.ORDERED);
        }

        @Override public boolean isEmpty() {
            return to <= from;
        }

        @Override protected void copyToArray(Object[] r) {
            System.arraycopy(array, from, r, 0, to - from);
        }

        @Override protected ImmutableListIterator<E> originalListIterator(int index) {
            return new Iter(index);
        }

        @NotNull @Override ImmutableList<E> sublistOf(int fromIndex, int toIndex) {
            return new Array<>(array, fromIndex + from, toIndex + from);
        }

        private static final long serialVersionUID = -2764017481108945198L;

        private class Iter extends ImmutableListIterator<E> {
            private int position;

            public Iter(int index) {
                position = index + from;
            }

            @Override public boolean hasNext() {
                return position < to;
            }

            @Override public boolean hasPrevious() {
                return position > 0;
            }

            @Override public E next() {
                if (position < to) return array[position++];
                throw new NoSuchElementException();
            }

            @Override public int nextIndex() {
                return position - from;
            }

            @Override public E previous() {
                if (position > 0) return array[--position];
                throw new NoSuchElementException();
            }
        }
    }  // end class Array

    /**
     * Class to manage building of ImmutableLists.
     */
    public static class Builder<E> {
        private Object[] content;
        private int      size;

        private Builder(int expectedSize) {
            content = new Object[expectedSize <= 0 ? EXPECTED_SIZE : expectedSize];
            size    = 0;
        }

        /** Add en element. */
        public final Builder<E> add(@Nullable E element) {
            ensureCapacity(size + 1);
            content[size++] = element;
            return this;
        }

        /** Add several elements. */
        public Builder<E> addAll(Iterable<? extends E> elements) {
            if (elements instanceof Collection) {
                final Collection<?> collection = (Collection<?>) elements;
                ensureCapacity(size + collection.size());
            }
            for (final E element : elements)
                add(element);
            return this;
        }

        /** Builds the Immutable list. */
        public ImmutableList<E> build() {
            return doBuild(null);
        }

        /** The number of elements. */
        public int size() {
            return size;
        }

        /** Builds the Immutable list sorted. */
        public ImmutableList<E> sortAndBuild(@NotNull Comparator<E> comparator) {
            return doBuild(comparator);
        }

        private void addAll(@NotNull Object[] array) {
            ensureCapacity(size + array.length);
            System.arraycopy(array, 0, content, size, array.length);
            size += array.length;
        }

        @NotNull private ImmutableList<E> doBuild(@Nullable Comparator<E> comparator) {
            if (size == 0) return empty();
            final E[] r = cast(content);
            if (size == 1) return listOf(r[0]);
            if (comparator != null) Arrays.sort(r, 0, size, comparator);
            return new Array<>(r, 0, size);
        }

        /**
         * Expand the absolute capacity of the builder so it can accept at least the specified
         * number of elements without being resized.
         */
        private void ensureCapacity(int minCapacity) {
            if (content.length >= minCapacity) return;
            content = Arrays.copyOf(content, newCapacity(minCapacity));
        }

        private int newCapacity(int minCapacity) {
            checkArgument(minCapacity >= 0, "Cannot store more than MAX_VALUE elements");
            // careful of overflow!
            final int oldCapacity = content.length;
            int       newCapacity = oldCapacity + (oldCapacity >> 1) + 1;
            if (newCapacity < minCapacity) newCapacity = Integer.highestOneBit(minCapacity - 1) << 1;
            if (newCapacity < 0) newCapacity = Integer.MAX_VALUE;
            return newCapacity;
        }
    }  // end class Builder

    static class EmptyList<E> extends ImmutableList<E> {
        @Override public Seq<E> append(Iterable<? extends E> elements) {
            return cast(immutable(elements));
        }
        @Override public boolean contains(@NotNull final Object o) {
            return false;
        }

        @Override public boolean containsAll(@NotNull final Collection<?> c) {
            return c.isEmpty();
        }

        @NotNull @Override public ImmutableList<E> drop(int n) {
            return this;
        }

        public boolean equals(Object o) {
            return (o instanceof List) && ((List<?>) o).isEmpty();
        }

        @Override public boolean exists(@NotNull Predicate<? super E> p) {
            return false;
        }

        @NotNull @Override public Seq<E> filter(@NotNull Predicate<? super E> p) {
            return this;
        }

        @NotNull @Override public <T extends E> Seq<T> filter(@NotNull Class<T> c) {
            return empty();
        }

        @NotNull @Override public <T extends E> Seq<T> filter(@NotNull Class<T> c, @NotNull Predicate<? super T> p) {
            return empty();
        }

        @NotNull @Override public <U> Seq<U> flatMap(@NotNull Function<? super E, ? extends Iterable<? extends U>> f) {
            return empty();
        }

        @Nullable @Override public <T> T foldLeft(T initialValue, BiFunction<T, E, T> op) {
            return initialValue;
        }

        @Override public boolean forAll(@NotNull Predicate<? super E> p) {
            return true;
        }

        @Override public void forEach(@NotNull Consumer<? super E> action) {}

        @Override public <R> Option<R> forEachReturning(@NotNull Function<? super E, StepResult<R>> step, Option<R> finalValue) {
            return finalValue;
        }

        @Override public E get(final int index) {
            throw new IndexOutOfBoundsException(EMPTY_LIST_ERROR);
        }

        @Override public int hashCode() {
            return 1;
        }

        @Override public int indexOf(final Object o) {
            return -1;
        }

        @NotNull @Override public ImmutableIterator<E> iterator() {
            return emptyIterator();
        }

        @Override public int lastIndexOf(final Object o) {
            return -1;
        }

        @NotNull @Override public <T> Seq<T> map(@NotNull Function<? super E, ? extends T> f) {
            return empty();
        }

        @Override public boolean removeIf(Predicate<? super E> filter) {
            return false;
        }

        @Override public void replaceAll(UnaryOperator<E> operator) {}

        @Override public int size() {
            return 0;
        }

        @NotNull @Override public ImmutableList<E> slice(final int fromIndex, final int toIndex) {
            return this;
        }

        @Override public void sort(Comparator<? super E> c) {}

        @Override public Spliterator<E> spliterator() {
            return Spliterators.emptySpliterator();
        }

        @NotNull @Override public Seq<E> take(int n) {
            return empty();
        }

        @NotNull @Override public Object[] toArray() {
            return EMPTY_OBJECT_ARRAY;
        }

        @NotNull @Override
        @SuppressWarnings("AssignmentToNull")
        public <T> T[] toArray(@NotNull final T[] a) {
            if (a.length > 0) a[0] = null;
            return a;
        }

        @Override public <U> Seq<Tuple<E, U>> zip(Seq<U> that) {
            return empty();
        }

        @Override public <U, R> Seq<R> zipWith(BiFunction<E, U, R> function, Seq<U> that) {
            return empty();
        }

        @NotNull @Override public Option<E> getFirst() {
            return Option.empty();
        }

        @Override public boolean isEmpty() {
            return true;
        }

        @Override protected ImmutableListIterator<E> originalListIterator(final int index) {
            return cast(Immutables.EMPTY_LIST_ITERATOR);
        }

        @NotNull @Override ImmutableList<E> sublistOf(int from, int fence) {
            return this;
        }
        // Preserves singleton property
        private Object readResolve() {
            return Immutables.EMPTY_LIST;
        }

        private static final long serialVersionUID = -4748699130275173164L;

        @SuppressWarnings("DuplicateStringLiteralInspection")
        private static final String EMPTY_LIST_ERROR = "Empty List";
    }  // end class EmptyList

    static final class ListIteratorWrapper<T> extends ImmutableListIterator<T> {
        private final ListIterator<T> original;

        ListIteratorWrapper(@NotNull final ListIterator<T> it) {
            original = it;
        }

        @Override public boolean hasNext() {
            return original.hasNext();
        }

        @Override public boolean hasPrevious() {
            return original.hasPrevious();
        }

        @Override public T next() {
            return original.next();
        }

        @Override public int nextIndex() {
            return original.nextIndex();
        }

        @Override public T previous() {
            return original.previous();
        }

        @Override public int previousIndex() {
            return original.previousIndex();
        }
    }

    private static class Singleton<E> extends ImmutableList<E> implements RandomAccess, Serializable {
        private final E element;

        private Singleton() {
            element = null;
        }

        Singleton(E obj) {
            element = obj;
        }

        public boolean contains(@NotNull Object obj) {
            return equal(obj, element);
        }

        @Override public void forEach(@NotNull Consumer<? super E> action) {
            action.accept(element);
        }

        @Nullable public E get(int index) {
            if (index != 0) throw new IndexOutOfBoundsException("Index: " + index + ", Size: 1");
            return element;
        }

        @Override public int indexOf(Object o) {
            return contains(o) ? 0 : -1;
        }

        @Override public int lastIndexOf(Object o) {
            return indexOf(o);
        }

        public int size() {
            return 1;
        }

        @NotNull @Override public ImmutableList<E> slice(int fromIndex, int toIndex) {
            return checkRange(fromIndex, toIndex) && fromIndex == 0 && toIndex > 0 ? this : empty();
        }

        @Override public void sort(Comparator<? super E> c) {}

        @Override public Spliterator<E> spliterator() {
            return Collections.singletonList(element).spliterator();
        }

        @NotNull @Override public Option<E> getFirst() {
            return option(element);
        }

        @Override public boolean isEmpty() {
            return false;
        }

        @Override protected ImmutableListIterator<E> originalListIterator(int index) {
            return Colls.singletonIteraror(element);
        }

        @NotNull @Override ImmutableList<E> sublistOf(int from, int fence) {
            return this;
        }

        private static final long serialVersionUID = 3093736618740652951L;
    }  // end class Singleton

    static class Wrapper<T> extends ImmutableList<T> {
        private final List<T> original;

        /** Default constructor. */
        Wrapper() {
            original = Collections.emptyList();
        }

        /** Create a ImmutableList Wrapper. */
        Wrapper(@NotNull final List<T> c) {
            original = c;
        }

        public boolean contains(@NotNull Object o) {
            return original.contains(o);
        }

        public boolean containsAll(@NotNull Collection<?> c) {
            return original.containsAll(c);
        }

        @Override public void forEach(@NotNull Consumer<? super T> action) {
            original.forEach(action);
        }

        @Override public T get(final int index) {
            return original.get(index);
        }

        @Override public int indexOf(final Object o) {
            return original.indexOf(o);
        }

        @NotNull @Override public ImmutableIterator<T> iterator() {
            return immutable(original.iterator());
        }

        @Override public int lastIndexOf(final Object o) {
            return original.lastIndexOf(o);
        }

        public int size() {
            return original.size();
        }

        public boolean isEmpty() {
            return original.isEmpty();
        }

        @Override protected ImmutableListIterator<T> originalListIterator(final int index) {
            return immutable(original.listIterator(index));
        }

        @NotNull @Override ImmutableList<T> sublistOf(int fromIndex, int toIndex) {
            final List<T> c    = original.subList(fromIndex, toIndex);
            final int     size = c.size();
            return size == 0 ? empty() : size == 1 ? listOf(c.get(0)) : new ImmutableList.Wrapper<>(c);
        }

        private static final long serialVersionUID = -5929172189165401852L;
    }  // end class Wrapper
}  // end class ImmutableList
