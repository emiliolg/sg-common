
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.core.Option;
import tekgenesis.common.core.Tuple;
import tekgenesis.common.util.GwtReplaceable;

import static java.util.Arrays.asList;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.collections.Seq.createSeq;
import static tekgenesis.common.core.Option.some;
import static tekgenesis.common.core.Predicates.alwaysTrue;

/**
 * This class consists exclusively of static methods that operate on or return collection, in a
 * generic way, including {@link Collection}, {@link Iterable}, {@link Iterator}, etc.
 */
@SuppressWarnings("ClassWithTooManyMethods")
public interface Colls {

    //~ Methods ......................................................................................................................................

    /** Returns a Seq with the specified iterables concateneted. */
    static <T> Seq<T> append(Iterable<? extends T> a, Iterable<? extends T> b) {
        return new BaseSeq.SeqSet<T>(a).append(b);
    }

    /** Returns a Seq with the specified iterables concateneted. */
    @SafeVarargs static <T> Seq<T> append(Iterable<? extends T> a, Iterable<? extends T>... rest) {
        final Seq<T> ts = new BaseSeq.SeqSet<>(a);
        for (final Iterable<? extends T> i : rest)
            ts.append(i);
        return ts;
    }

    /** Returns true if the iterable contains the given element. */
    static <T> boolean contains(@Nullable final Iterable<T> input, @NotNull final Object e) {
        if (input != null) {
            for (final T t : input) {
                if (e.equals(t)) return true;
            }
        }
        return false;
    }
    /**
     * Creates a {@link Seq} based on a tree structure. Each node of the tree must implements
     * {@link Iterable}
     *
     * @param  elements  the tree to get the input from
     */
    @NotNull static <T extends Iterable<T>> Seq<T> deepSeq(@NotNull final Iterable<T> elements) {
        //J-
        return createSeq(() ->
            new IteratorBase<T>() {
                private final Stack<Iterator<T>> stack = Stack.createStack();
                private Iterator<T>              it    = elements.iterator();
                @Override public boolean hasNext() {
                    if (nextExists) return true;
                    // noinspection LoopConditionNotUpdatedInsideLoop
                    while (!it.hasNext()) {
                        if (stack.isEmpty()) { nextExists = false; return false; }
                        it = stack.pop();
                    }
                    nextValue = it.next();
                    stack.push(it);
                    it = nextValue.iterator();
                    nextExists = true;
                    return true;
                }
            });
        //J+
    }

    /**
     * Drops the first n elements of the given input and returns a {@link Seq}. If the
     * {@link Iterable input} contains less elements no exception is thrown.
     */
    @NotNull static <T> Seq<T> drop(@Nullable final Iterable<T> input, final int n) {  //
        return input == null ? emptyList() : createSeq(() -> slice(input.iterator(), n, Integer.MAX_VALUE));
    }

    /** Returns the empty Iterable. */
    @NotNull static <T> Seq<T> emptyIterable() {
        return emptySeq();
    }

    /** Returns the empty Iterator. */
    @NotNull static <T> ImmutableIterator<T> emptyIterator() {
        return cast(Immutables.EMPTY_ITERATOR);
    }

    /** Returns the empty List (immutable). */
    @NotNull static <T> ImmutableList<T> emptyList() {
        return ImmutableList.empty();
    }

    /** Returns the empty Seq. */
    @NotNull static <T> Seq<T> emptySeq() {
        return cast(Immutables.EMPTY_ITERABLE);
    }

    /**
     * Tests whether the predicate holds for any of the elements of given input. For 'all' use
     * {@link #forAll(Iterable, Predicate)}.
     */
    static <T> boolean exists(@Nullable final Iterable<T> input, @NotNull final Predicate<? super T> f) {
        return input != null && immutable(input).exists(f);
    }

    /** Returns an {@link Seq} with all the elements that are instances of the specified class. */
    @NotNull static <E, T extends E> Seq<T> filter(@NotNull final Iterable<E> it, @NotNull final Class<T> c) {
        return filter(it, c, alwaysTrue());
    }

    /** Returns an {@link Seq} with all the elements that are complies with the given Predicate. */
    @NotNull static <T> Seq<T> filter(@Nullable final Iterable<T> iterable, @NotNull final Predicate<? super T> p) {  //
        return iterable == null ? emptyIterable() : createSeq(() ->
                new IteratorBase<T>() {
                    final Iterator<T> iter = iterable.iterator();

                    @Override public boolean hasNext() {
                        while (!nextExists && iter.hasNext()) {
                            nextValue  = iter.next();
                            nextExists = p.test(nextValue);
                        }
                        return nextExists;
                    }
                });
    }

    /**
     * Returns an {@link Seq} with all the elements that are instances of the specified class, and
     * satisfies a predicate.
     */
    @NotNull static <E, T extends E> Seq<T> filter(@NotNull final Iterable<E> iterable, @NotNull final Class<T> c, final Predicate<? super T> p) {
        return map(iterable, t -> GwtReplaceable.isInstance(c, t) && p.test(cast(t)) ? cast(t) : null, true);
    }

    /** Get the First element of the Iterable or none. */
    static <E> Option<E> first(Iterable<E> iterable) {
        final Iterator<E> iterator = iterable.iterator();
        return iterator.hasNext() ? some(iterator.next()) : Option.empty();
    }

    /** Get the First element of the Iterable that fulfils the condition or none. */
    static <E> Option<E> first(Iterable<E> iterable, Predicate<? super E> predicate) {
        for (final E e : iterable) {
            if (predicate.test(e)) return some(e);
        }
        return Option.empty();
    }

    /**
     * Returns a Seq consisting of the results of replacing each element of this Iterable with the
     * contents of a mapped Iterable produced by applying the provided mapping function to each
     * element. If a mapped Seq is {@code null} an empty iterable is used, instead.
     */
    @NotNull static <T, U> Seq<U> flatMap(@Nullable final Iterable<T>                                         input,
                                          @NotNull final Function<? super T, ? extends Iterable<? extends U>> mapper) {
        return input == null ? emptyList() : createSeq(() ->
                new IteratorBase<U>() {
                    final Iterator<T>     outer = input.iterator();
                    Iterator<? extends U> inner = nextIterator();

                    @Nullable private Iterator<? extends U> nextIterator() {
                        while (outer.hasNext()) {
                            final Iterable<? extends U> iter = mapper.apply(outer.next());
                            if (iter != null) return iter.iterator();
                        }
                        return null;
                    }
                    @Override public boolean hasNext() {
                        while (!nextExists && inner != null) {
                            while (!nextExists && inner.hasNext()) {
                                nextValue  = inner.next();
                                nextExists = nextValue != null;
                            }
                            if (!nextExists) inner = nextIterator();
                        }
                        return nextExists;
                    }
                });
    }

    /**
     * Tests whether the predicate holds for all elements of given input. For 'any' use
     * {@link #exists(Iterable, Predicate)}.
     */
    static <T> boolean forAll(@Nullable final Iterable<T> input, @NotNull final Predicate<? super T> f) {
        if (input != null) {
            for (final T t : input) {
                if (!f.test(t)) return false;
            }
        }
        return true;
    }

    /** Returns an unmodifiable view of an {@link Iterator}. */
    @NotNull static <T> ImmutableIterator<T> immutable(@NotNull final Iterator<T> it) {
        return it instanceof ImmutableIterator ? (ImmutableIterator<T>) it : new ImmutableIterator<T>() {
                @Override public boolean hasNext() {
                    return it.hasNext();
                }
                @Override public T next() {
                    return it.next();
                }
            };
    }

    /** Returns an unmodifiable view of an {@link Iterator}. */
    @NotNull static <T> ImmutableListIterator<T> immutable(@NotNull ListIterator<T> it) {
        return it instanceof ImmutableListIterator ? (ImmutableListIterator<T>) it : new ImmutableList.ListIteratorWrapper<>(it);
    }

    /** Returns an unmodifiable view of an {@link Iterable}. */
    @NotNull static <T> Seq<T> immutable(@NotNull Iterable<T> it) {  //
        return it instanceof Seq ? (Seq<T>) it : createSeq(() -> immutable(it.iterator()));
    }

    /** Returns an unmodifiable view of an {@link Collection}. */
    @NotNull static <T> ImmutableCollection<T> immutable(@Nullable Collection<T> c) {
        if (c instanceof ImmutableCollection) return (ImmutableCollection<T>) c;
        if (c == null) return emptyList();
        return new ImmutableCollection.Wrapper<>(c);
    }
    /** Returns an unmodifiable view of an {@link Set}. */
    @NotNull static <T> ImmutableSet<T> immutable(@Nullable Set<T> c) {
        if (c instanceof ImmutableSet) return (ImmutableSet<T>) c;
        return new ImmutableSet.Wrapper<>(c == null ? Collections.emptySet() : c);
    }

    /** Returns an unmodifiable view of an {@link Iterable}. */
    @NotNull static <T> ImmutableList<T> immutable(@Nullable List<T> c) {
        if (c instanceof ImmutableList) return (ImmutableList<T>) c;
        if (c == null) return emptyList();
        // TOdo enable optimizations once checked
        // final int size = c.size();
        // if (size == 0) return emptyList();
        // if (size == 1) return singletonList(c.get(0));
        // if (size < 10) {
        // return new ImmutableList.Array<T>(cast(c.toArray()), size);
        // }
        return new ImmutableList.Wrapper<>(c);
    }

    /** Get the index of the List that fulfils the condition or -1. */
    static <E> int indexOf(List<E> list, Predicate<? super E> predicate) {
        for (int i = 0; i < list.size(); i++) {
            if (predicate.test(list.get(i))) return i;
        }
        return -1;
    }

    /** Insert all the items of this Iterable the specified collection. */
    static <E, C extends Collection<E>> C into(@Nullable Iterable<E> iterable, C target) {
        if (iterable != null) {
            for (final E e : iterable)
                target.add(e);
        }
        return target;
    }

    /** Returns an Immutable List with the specified elements. */
    @Deprecated @NotNull static <T> ImmutableList<T> list(@Nullable T e) {
        return listOf(e);
    }

    /** Returns an Immutable List with the specified elements. */
    @Deprecated @NotNull static <T> ImmutableList<T> list(T[] rest) {
        return ImmutableList.fromArray(rest);
    }

    /** Returns an Immutable List with the specified elements. */
    @Deprecated @NotNull static <T> ImmutableList<T> list(@Nullable T e1, T e2) {
        return listOf(e1, e2);
    }

    /** Returns an Immutable List with the specified elements. */
    @Deprecated @NotNull static <T> ImmutableList<T> list(@Nullable T e1, T e2, T e3) {
        return listOf(e1, e2, e3);
    }

    /** Returns an Immutable List with the specified elements. */
    @Deprecated @NotNull static <T> ImmutableList<T> list(@Nullable T e1, T e2, T e3, T e4) {
        return listOf(e1, e2, e3, e4);
    }
    /** Returns an Immutable List with the specified elements. */
    @Deprecated @NotNull static <T> ImmutableList<T> list(@Nullable T e1, T e2, T e3, T e4, T e5) {
        return listOf(e1, e2, e3, e4, e5);
    }
    /** Returns an Immutable List with the specified elements. */
    @Deprecated @NotNull static <T> ImmutableList<T> list(@Nullable T e1, T e2, T e3, T e4, T e5, T e6) {
        return listOf(e1, e2, e3, e4, e5, e6);
    }

    /** Returns an Immutable List with the specified elements. */
    @Deprecated @NotNull static <T> ImmutableList<T> list(@Nullable T e1, T e2, T e3, T e4, T e5, T e6, T e7) {
        return listOf(e1, e2, e3, e4, e5, e6, e7);
    }
    /** Returns an Immutable List with the specified elements. */
    @Deprecated @NotNull static <T> ImmutableList<T> list(@Nullable T e1, T e2, T e3, T e4, T e5, T e6, T e7, T e8) {
        return listOf(e1, e2, e3, e4, e5, e6, e7, e8);
    }

    /** Returns an Immutable List with the specified elements. */
    @NotNull static <T> ImmutableList<T> listOf(@Nullable T e) {
        return ImmutableList.of(e);
    }

    /** Returns an Immutable List with the specified elements. */
    @NotNull static <T> ImmutableList<T> listOf(@Nullable T e1, @Nullable T e2) {
        return ImmutableList.of(e1, e2);
    }

    /** Returns an Immutable List with the specified elements. */
    @NotNull @SafeVarargs
    @SuppressWarnings("varargs")
    static <T> ImmutableList<T> listOf(@Nullable T first, T... rest) {
        return ImmutableList.of(first, rest);
    }

    /** Returns an Immutable List with the specified elements. */
    @NotNull static <T> ImmutableList<T> listOf(@Nullable T e1, @Nullable T e2, @Nullable T e3) {
        return ImmutableList.of(e1, e2, e3);
    }

    /** Returns an Immutable List with the specified elements. */
    @NotNull static <T> ImmutableList<T> listOf(@Nullable T e1, @Nullable T e2, @Nullable T e3, @Nullable T e4) {
        return ImmutableList.of(e1, e2, e3, e4);
    }

    /** Returns an Immutable List with the specified elements. */
    @NotNull static <T> ImmutableList<T> listOf(@Nullable T e1, @Nullable T e2, @Nullable T e3, @Nullable T e4, @Nullable T e5) {
        return ImmutableList.of(e1, e2, e3, e4, e5);
    }

    /**
     * Map each element from an Iterable using a {@link Function} and returns a {@link Seq}. When
     * the map function returns <code>null</code> the element will be skipped
     */
    @NotNull static <T, U> Seq<U> map(@Nullable final Iterable<T> input, @NotNull final Function<? super T, ? extends U> f) {
        return map(input, f, true);
    }

    /**
     * Map each element from an Iterable using a {@link Function} and returns a {@link Seq}. Allows
     * the inclusion of null elements in the result
     */
    @NotNull static <T, U> Seq<U> map(@Nullable final Iterable<T> input, @NotNull final Function<? super T, ? extends U> f, boolean skipNulls) {
        if (input == null) return emptyIterable();
        return createSeq(() -> {
            final Iterator<T> iter = input.iterator();

            if (!skipNulls) return new ImmutableIterator<U>() {
                @Override public boolean hasNext() {
                    return iter.hasNext();}
                @Override public U next() {
                    return f.apply(iter.next());}
            };
            return new IteratorBase<U>() {
                @Override public boolean hasNext() {
                    while (!nextExists && iter.hasNext()) {
                        nextValue  = f.apply(iter.next());
                        nextExists = nextValue != null;
                    }
                    return nextExists;
                }
            };
        });
    }

    /** Returns a string representation of an {@link Iterable}. */
    @NotNull static String mkString(Iterable<?> iterable) {
        return immutable(iterable).mkString();
    }

    /** Returns a string representation of an {@link Iterable}. */
    @NotNull static String mkString(Iterable<?> iterable, String sep) {
        return immutable(iterable).mkString(sep);
    }

    /**
     * Returns a string representation of an {@link Iterable}, Using start, end, and separator
     * strings.
     */
    @NotNull static String mkString(Iterable<?> iterable, String start, String sep, String end) {
        return immutable(iterable).mkString(start, sep, end);
    }

    /** Creates a new {@link Seq} from an {@link Iterable}. */
    @NotNull static <T> Seq<T> seq(Iterable<T> iterable) {
        return immutable(iterable);
    }

    /** Returns a Set with the specified elements. */
    @NotNull @SafeVarargs
    @SuppressWarnings("varargs")
    static <T> Set<T> set(T... elements) {
        return new LinkedHashSet<>(asList(elements));
    }

    /** Returns a ImmutableSet with only one element. */
    @Deprecated static <T> ImmutableList<T> singleton(final T element) {
        return listOf(element);
    }

    /** Returns an ImmutableIterator with only one element. */
    static <T> ImmutableListIterator<T> singletonIteraror(@Nullable final T element) {
        return new ImmutableListIterator<T>() {
            private boolean hasNext = true;

            @Override public boolean hasNext() {
                return hasNext;
            }
            @Override public boolean hasPrevious() {
                return !hasNext;
            }
            @Override public int nextIndex() {
                return hasNext ? 0 : 1;
            }
            @Nullable @Override public T next() {
                if (!hasNext) throw new NoSuchElementException();
                hasNext = false;
                return element;
            }
            @Nullable @Override public T previous() {
                if (hasNext) throw new NoSuchElementException();
                hasNext = true;
                return element;
            }
        };
    }

    /** Returns the size of any Iterable. */
    static int size(@Nullable Iterable<?> it) {
        if (it == null) return 0;
        if (it instanceof Collection) return ((Collection<?>) it).size();
        int size = 0;
        for (final Object o : it)
            size++;
        return size;
    }

    /** Returns an {@link Seq} with all the elements in the specified range. */
    @NotNull static <T> Seq<T> slice(@Nullable final Iterable<T> it, final int from, final int to) {
        return it == null ? emptyIterable() : createSeq(() -> slice(it.iterator(), from, to));
    }

    /** Returns an {@link ImmutableIterator} with all the elements in the specified range. */
    static <E> ImmutableIterator<E> slice(@NotNull final Iterator<E> it, final int from, final int to) {
        return new IteratorBase<E>() {
            int i = 0;

            @Override public boolean hasNext() {
                while (!nextExists && it.hasNext() && i < to) {
                    nextValue  = it.next();
                    nextExists = i++ >= from;
                }
                return nextExists;
            }
        };
    }

    /** Returns an {@link ImmutableList} sorted using the specified Comparator. */
    static <E> ImmutableList<E> sorted(Iterable<E> iterable, Comparator<E> c) {
        return immutable(iterable).sorted(c);
    }

    /**
     * Takes the first n elements of the given input and returns a {@link Seq}. If the
     * {@link Iterable input} contains less elements no exception is thrown.
     */
    @NotNull static <T> Seq<T> take(@Nullable final Iterable<T> input, int n) {
        return slice(input, 0, n);
    }

    /** Creates a {@link List} from a given {@link Iterable}. */
    @NotNull static <T> ImmutableList<T> toList(Iterable<T> iterable) {
        return ImmutableList.fromIterable(iterable);
    }

    /** Zip two iterables into a new Iterable that returns a tuple foreach element zipped. */
    @NotNull static <T1, T2> Seq<Tuple<T1, T2>> zip(@NotNull final Iterable<T1> seq1, @NotNull final Iterable<T2> seq2) {
        //J-
        return createSeq( () -> new ImmutableIterator<Tuple<T1, T2>>() {
            final Iterator<T1> it1 = seq1.iterator();
            final Iterator<T2> it2 = seq2.iterator();
            @Override public boolean hasNext() { return it1.hasNext() && it2.hasNext(); }
            @Override public Tuple<T1, T2> next() { return Tuple.tuple(it1.next(), it2.next()); }
        });
        //J+
    }

    /**
     * Zip two iterables into a new Iterable that returns a tuple foreach element zipped and the
     * function applied.
     */
    static <R, T1, T2> Seq<R> zipWith(BiFunction<T1, T2, R> function, Seq<T1> seq1, Seq<T2> seq2) {
        //J-
        return createSeq( () -> new ImmutableIterator<R>() {
            final Iterator<T1> it1 = seq1.iterator();
            final Iterator<T2> it2 = seq2.iterator();
            @Override public boolean hasNext() { return it1.hasNext() && it2.hasNext(); }
            @Override public R next() { return function.apply(it1.next(), it2.next()); }
        });
        //J+
    }

    /** Returns true if the Object is an instance of an Collection class of a particular type. */
    static boolean isInstanceOf(Object o,
                                @SuppressWarnings("rawtypes") Class<? extends Iterable> collectionType, Class<?> elementType) {
        return GwtReplaceable.isInstanceOf(o, collectionType, elementType);
    }
}  // end class Colls
