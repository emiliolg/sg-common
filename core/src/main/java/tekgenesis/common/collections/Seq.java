
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.collections;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.core.Mutable;
import tekgenesis.common.core.Option;
import tekgenesis.common.core.StepResult;
import tekgenesis.common.core.Tuple;

import static tekgenesis.common.collections.Colls.emptySeq;
import static tekgenesis.common.collections.Colls.listOf;
import static tekgenesis.common.core.Predicates.alwaysTrue;

/**
 * An extension to {@link Traversable} that also implements an {@link Iterable } guaranteed to
 * return an ImmutableIterator It also adds some utility methods.
 */
@FunctionalInterface public interface Seq<E> extends Iterable<E>, Traversable<E> {

    //~ Methods ......................................................................................................................................

    /** Append to the current Seq the specified elements and returns a new Seq. */
    default Seq<E> append(final Iterable<? extends E> elements) {
        return Colls.append(this, elements);
    }

    /** Append to the current Seq the specified element and returns a new Seq. */
    default Seq<E> append(final E element) {
        return append(listOf(element));
    }

    /** Append to the current Seq the specified elements and returns a new Seq. */
    default Seq<E> append(final E e1, final E e2) {
        return append(listOf(e1, e2));
    }

    /** Append to the current Seq the specified elements and returns a new Seq. */
    default Seq<E> append(final E e1, final E e2, final E e3) {
        return append(listOf(e1, e2, e3));
    }

    /** Drops the first n elements of the sequence and returns a {@link Seq}. */
    @NotNull default Seq<E> drop(int n) {
        return Colls.drop(this, n);
    }

    /** Returns an {@link Seq} with all the elements that satisfy a predicate. */
    @NotNull default Seq<E> filter(@NotNull final Predicate<? super E> p) {
        return Colls.filter(this, p);
    }

    /** Returns an {@link Seq} with all the elements that are instances of the specified class. */
    @NotNull default <T extends E> Seq<T> filter(@NotNull final Class<T> c) {
        return Colls.filter(this, c, alwaysTrue());
    }

    /**
     * Returns an {@link Seq} with all the elements that are instances of the specified class, and
     * satisfies a predicate.
     */
    @NotNull default <T extends E> Seq<T> filter(@NotNull final Class<T> c, @NotNull final Predicate<? super T> p) {
        return Colls.filter(this, c, p);
    }
    /**
     * Returns a Seq consisting of the results of replacing each element of this Iterable with the
     * contents of a mapped Iterable produced by applying the provided mapping function to each
     * element. If a mapped Seq is {@code null} an empty iterable is used, instead.
     */
    @NotNull default <U> Seq<U> flatMap(@NotNull final Function<? super E, ? extends Iterable<? extends U>> f) {
        return Colls.flatMap(this, f);
    }
    @Override default void forEach(@NotNull Consumer<? super E> action) {
        for (final E t : this)
            action.accept(t);
    }
    @Override default <R> Option<R> forEachReturning(@NotNull Function<? super E, StepResult<R>> step, Option<R> finalValue) {
        for (final E t : this) {
            final StepResult<R> result = step.apply(t);
            if (result.isDone()) return result.getValue();
        }
        return finalValue;
    }

    /** Group a Seq in chunks of the specified size. */
    default Seq<ImmutableList<E>> grouped(int groupSize) {
        final Iterator<E> it = iterator();
        return createSeq(() ->
                new ImmutableIterator<ImmutableList<E>>() {
                    @Override public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override public ImmutableList<E> next() {
                        return ImmutableList.build(b -> {
                            int i = 0;             //
                            while (i < groupSize && it.hasNext()) {
                                b.add(it.next());  //
                                i++;
                            }
                        });
                    }
                });
    }
    @NotNull @Override ImmutableIterator<E> iterator();

    /** Map each element using a {@link Function} and returns a {@link Seq}. */
    @NotNull default <T> Seq<T> map(@NotNull final Function<? super E, ? extends T> f) {
        return Colls.map(this, f);
    }

    /** Prepend to the current Seq the specified element and returns a new Seq. */
    default Seq<E> prepend(final E element) {
        return listOf(element).append(this);
    }

    /** Returns an {@link Seq} with all the elements in the specified range. */
    @NotNull default Seq<E> slice(final int from, final int to) {
        return Colls.slice(this, from, to);
    }

    /** Takes the first n elements of the sequence and returns a {@link Seq}. */
    @NotNull default Seq<E> take(int n) {
        return slice(0, n);
    }

    /** Topological Sort the sequence using the specified dependency function Loops are avoided ! */
    default Set<E> topologicalSort(Function<E, Iterable<E>> dependenciesFor) {
        final Set<E>     result  = new LinkedHashSet<>();
        final HashSet<E> visited = new HashSet<>();
        for (final E e : this)
            BaseSeq.tsort(e, result, visited, dependenciesFor);
        return result;
    }

    /** Convert the Seq to a Seq of Strings. */
    @NotNull default Seq<String> toStrings() {
        return map(String::valueOf);
    }

    /** Zip this seq with an Iterable and return a Seq with a tuple with the zipped elements. */
    default <U> Seq<Tuple<E, U>> zip(Seq<U> that) {
        return Colls.zip(this, that);
    }

    /** Zip this seq with an Iterable and return a Seq with a tuple with the zipped elements. */
    default <U, R> Seq<R> zipWith(BiFunction<E, U, R> function, Seq<U> that) {
        return Colls.zipWith(function, this, that);
    }
    @Override default boolean isEmpty() {
        return !iterator().hasNext();
    }

    //~ Methods ......................................................................................................................................

    /** Return a view of the Array as a Seq. */
    @NotNull static <T> Seq<T> asSeq(@Nullable T[] elements) {
        if (elements == null) return emptySeq();
        return createSeq(() ->
                new ImmutableIterator<T>() {
                    int i = 0;

                    @Override public boolean hasNext() {
                        return i < elements.length;
                    }
                    @Override public T next() {
                        return elements[i++];
                    }
                });
    }

    /** Creates a Seq with the given Iterator as supplier. */
    static <T> Seq<T> createSeq(final Supplier<ImmutableIterator<T>> iteratorSupplier) {
        return new BaseSeq<T>() {
            @NotNull @Override public ImmutableIterator<T> iterator() {
                return iteratorSupplier.get();
            }
        };
    }

    /** Returns a Seq with all the internal Iterables concatened. */
    static <E> Seq<E> flatten(Iterable<? extends Iterable<E>> iterables) {
        final BaseSeq.SeqSet<E> result = new BaseSeq.SeqSet<>();
        iterables.forEach(result::append);
        return result;
    }

    /** Create a sequence with all integers staring from the specified value. */
    static Seq<Integer> from(int from) {
        return fromTo(from, Integer.MAX_VALUE);
    }

    /** Create a sequence with all integers in the specified range. */
    static Seq<Integer> fromTo(int from, int to) {
        return createSeq(() ->
                new ImmutableIterator<Integer>() {
                    private final Mutable.Int next = new Mutable.Int(from);

                    public boolean hasNext() {
                        return next.value() <= to;
                    }
                    @Override public Integer next() {
                        return next.getAndIncrement();
                    }
                });
    }

    /** Create a sequence with infinit repetitions of the specified element. */
    static <E> Seq<E> repeat(E e) {
        return createSeq(() ->
                new ImmutableIterator<E>() {
                    @Override public boolean hasNext() {
                        return true;
                    }
                    @Override public E next() {
                        return e;
                    }
                });
    }
}  // end class Seq
