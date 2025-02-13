
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
import java.util.function.*;
import java.util.stream.Collector;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.core.Mutable;
import tekgenesis.common.core.Option;
import tekgenesis.common.core.StepResult;
import tekgenesis.common.core.StrBuilder;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.Predefined.ensureNotNull;
import static tekgenesis.common.collections.Colls.emptyList;
import static tekgenesis.common.collections.Colls.immutable;
import static tekgenesis.common.core.StepResult.done;
import static tekgenesis.common.core.StepResult.next;

/**
 * Interface that Based on a forEach implementation implements a number of collection operations.
 */
public interface Traversable<E> {

    //~ Methods ......................................................................................................................................

    /**
     * Performs a mutable reduction operation on the elements of this Traversable. See
     * {@link Collector}
     */
    default <A, R> R collect(Collector<? super E, A, R> collector) {
        return collect(collector.supplier(), collector.accumulator(), collector.finisher());
    }
    /**
     * Performs a mutable reduction operation on the elements of this Traversable. See
     * {@link Collector}
     */
    default <A, R> R collect(Supplier<A> initializer, BiConsumer<A, ? super E> accumulator, Function<A, R> finisher) {
        final A container = initializer.get();
        forEach(e -> accumulator.accept(container, e));
        return finisher.apply(container);
    }

    /** Returns <tt>true</tt> if the specified element is contained. */
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    default boolean contains(@NotNull final Object toFind) {
        return forEachReturning(doneWhen(toFind::equals, true)).isPresent();
    }

    /**
     * Tests whether a predicate holds for any of the elements of this Traversable. For all use
     * {@link #forAll(Predicate)}.
     */
    default boolean exists(@NotNull final Predicate<? super E> p) {
        return forEachReturning(doneWhen(p, true)).isPresent();
    }

    /** Returns an {@link Traversable} with all the elements that satisfy a predicate. */
    default Traversable<E> filter(Predicate<? super E> predicate) {
        final Traversable<E> original = this;
        return new Traversable<E>() {
            @Override public <R> Option<R> forEachReturning(@NotNull Function<? super E, StepResult<R>> step, Option<R> finalValue) {
                return original.forEachReturning(e -> {
                        if (predicate.test(e)) return step.apply(e);
                        return StepResult.next();
                    },
                    finalValue);
            }
        };
    }

    /**
     * Returns a Traversable consisting of the results of replacing each element of this Traversable
     * with the contents of a mapped Iterable produced by applying the provided mapping function to
     * each element. If a mapped Iterable is {@code null} an empty iterable is used, instead.
     */
    default <R> Traversable<R> flatMap(Function<? super E, ? extends Iterable<? extends R>> mapper) {
        final Traversable<E> original = this;
        return new Traversable<R>() {
            @Override public <S> Option<S> forEachReturning(@NotNull Function<? super R, StepResult<S>> step, Option<S> finalValue) {
                return original.forEachReturning(e -> {
                        for (final R r : mapper.apply(e)) {
                            final StepResult<S> stepResult = step.apply(r);
                            if (stepResult.isDone()) return stepResult.getValue().map(StepResult::done).orElse(StepResult.done());
                        }
                        return StepResult.next();
                    },
                    finalValue);
            }
        };
    }

    /**
     * Applies a binary operator to a start value and all elements of this traversable or iterator,
     * going left to right. op(...op(initialValue, x_1), x_2, ..., x_n)
     */
    @Contract("!null,_ -> !null")
    @Nullable default <T> T foldLeft(@Nullable T initialValue, BiFunction<T, E, T> op) {
        final Mutable<T> result = new Mutable.Object<>(initialValue);
        forEach(e -> result.setValue(op.apply(result.getValue(), e)));
        return result.getValue();
    }

    /**
     * Tests whether a predicate holds for all elements of this sequence. For any use
     * {@link #exists(Predicate)}.
     */
    default boolean forAll(@NotNull final Predicate<? super E> p) {
        return forEachReturning(doneWhen(p.negate(), false)).isEmpty();
    }

    /** Performs an action for each element of this traversable. */
    default void forEach(@NotNull Consumer<? super E> consumer) {  //
        forEachReturning(e -> {                                    //
            consumer.accept(e);                                    //
            return next();                                         //
        });
    }

    /** Performs an action for each element of this Traversable. And returns a result when done */
    default <R> Option<R> forEachReturning(@NotNull Function<? super E, StepResult<R>> step) {
        return forEachReturning(step, Option.empty());
    }

    /** Performs an action for each element of this Traversable. And returns a result when done */
    <R> Option<R> forEachReturning(@NotNull Function<? super E, StepResult<R>> step, Option<R> finalValue);

    /** Performs an action for each element of this Traversable. */
    default void forEachWhile(@NotNull Predicate<? super E> predicate) {
        forEachReturning(e -> predicate.test(e) ? next() : done());
    }

    /**
     * Partitions this collection into a MultiMap according to some discriminator function.
     *
     * @see  MultiMap.Builder#buildFrom if you want to create a MultiMap with special
     *       characteristics
     */
    default <K> MultiMap<K, E> groupBy(Function<E, K> keyExtractor) {
        return new MultiMap.Builder<K, E>().buildFrom(this, keyExtractor);
    }

    /** Insert all the items of this Seq into the specified collection. */
    @NotNull default <C extends Collection<E>> C into(@NotNull C target) {
        forEach(target::add);
        return target;
    }
    /** Map each element using a {@link Function} and returns a new {@link Traversable}. */
    default <R> Traversable<R> map(Function<? super E, ? extends R> mapper) {
        final Traversable<E> original = this;
        return new Traversable<R>() {
            @Override public <S> Option<S> forEachReturning(@NotNull Function<? super R, StepResult<S>> step, Option<S> finalValue) {
                return original.forEachReturning(e -> step.apply(mapper.apply(e)), finalValue);
            }
        };
    }

    /**
     * Returns the maximum element of the given collection or none() if the collection is empty,
     * according to the order induced by the specified comparator.
     */
    @NotNull default Option<E> max(@NotNull final Comparator<? super E> f) {
        return Option.ofNullable(foldLeft(null, (a, b) -> a == null || f.compare(a, b) < 0 ? b : a));
    }
    /**
     * Returns the minimum of the given collection or none() if the collection is empty, according
     * to the order induced by the specified comparator.
     */
    @NotNull default Option<E> min(@NotNull final Comparator<? super E> f) {
        return Option.ofNullable(foldLeft(null, (a, b) -> a == null || f.compare(a, b) > 0 ? b : a));
    }
    /** Returns a string representation. Using the specified separator strings. */
    @NotNull default String mkString() {
        return mkString("(", ", ", ")");
    }

    /** Returns a string representation. Using the specified separator strings. */
    @NotNull default String mkString(String sep) {
        return mkString("", sep, "");
    }

    /** Returns a string representation of an {@link Iterator}. */
    default String mkString(String start, String sep, String end) {
        final StrBuilder builder = new StrBuilder(start);
        builder.startCollection(sep);
        forEach(builder::appendElement);
        return builder.append(end).toString();
    }
    /**
     * Applies a binary operator to all elements of this traversable or iterator, going left to
     * right. op(...op(x_1, x_2), ..., x_n)
     */
    @NotNull default E reduce(BiFunction<E, E, E> op) {
        final Mutable<E> result = new Mutable.Object<>();
        forEach(e -> result.setValue(result.getValue() == null ? e : op.apply(result.getValue(), e)));
        return ensureNotNull(result.getValue(), () -> new IllegalStateException("Empty Collection"));
    }

    /** Revert the Traversable. */
    default ImmutableList<E> revert() {
        return ImmutableList.buildReversed(this);
    }

    /** Returns the size of the Traversable. */
    default int size() {
        final Mutable.Int accumulator = new Mutable.Int();
        forEach(e -> accumulator.increment());
        return accumulator.value();
    }
    /** Returns an {@link ImmutableList} sorted using the specified Comparator. */
    @NotNull default ImmutableList<E> sorted(@NotNull final Comparator<E> c) {
        final ImmutableList.Builder<E> builder = ImmutableList.builder(getSize().orElse(ImmutableList.EXPECTED_SIZE));
        forEach(builder::add);
        return builder.sortAndBuild(c);
    }
    /** Takes elements of this traversable while the condition is satisfied. */
    @NotNull default Traversable<E> takeWhile(Predicate<E> condition) {
        final Traversable<E> original = this;
        return new Traversable<E>() {
            @Override public <R> Option<R> forEachReturning(@NotNull Function<? super E, StepResult<R>> step, Option<R> finalValue) {
                return original.forEachReturning(e -> {
                        if (!condition.test(e)) return finalValue.map(StepResult::done).orElse(done());
                        step.apply(e);
                        return next();
                    },
                    finalValue);
            }
        };
    }

    /** Convert to an array of the specified type. */
    default <T> T[] toArray(Function<Integer, T[]> arrayConstructor) {
        final T[]         array = arrayConstructor.apply(size());
        final Mutable.Int index = new Mutable.Int();
        forEach(e -> array[index.getAndIncrement()] = cast(e));
        return array;
    }

    /** Creates a {@link List}. */
    @NotNull default ImmutableList<E> toList() {
        // noinspection InstanceofThis
        if (this instanceof ImmutableList) return cast(this);
        final int sz = getSize().orElse(ImmutableList.EXPECTED_SIZE);
        if (sz == 0) return emptyList();
        if (sz == 1) return getFirst().toList();
        final ImmutableList.Builder<E> builder = ImmutableList.builder(sz);
        forEach(builder::add);
        return builder.build();
    }

    /** Convert the Traversable to a ImmutableSet. */
    default ImmutableSet<E> toSet() {
        return immutable(into(new HashSet<>()));
    }

    /** Get the First element in the Seq. */
    @NotNull default Option<E> getFirst() {
        return forEachReturning(StepResult::done);
    }

    /** Get the First element in the Seq that fulfills the condition. */
    @NotNull default Option<E> getFirst(@NotNull final Predicate<? super E> p) {
        return forEachReturning(e -> p.test(e) ? done(e) : next());
    }
    /**
     * Get the size as an Option. If the size can be determined without iterating over the elements
     * it will be returned If not {@link Option#empty()} will be return
     */
    default Option<Integer> getSize() {
        return Option.empty();
    }

    /** Returns true if the collection does not have elements. */
    default boolean isEmpty() {
        return forEachReturning(e -> done(true), Option.empty()).isEmpty();
    }

    //~ Methods ......................................................................................................................................

    /** Test the predicate over E and return done or next. */
    static <T, E> Function<E, StepResult<T>> doneWhen(Predicate<? super E> p, T value) {
        return e -> p.test(e) ? done(value) : next();
    }
}  // end interface Traversable
