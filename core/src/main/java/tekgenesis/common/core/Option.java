
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.annotation.GwtIncompatible;
import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.collections.Seq;

import static tekgenesis.common.Predefined.ensureNotNull;
import static tekgenesis.common.collections.Colls.emptyIterator;
import static tekgenesis.common.collections.Colls.emptyList;
import static tekgenesis.common.collections.Colls.listOf;
import static tekgenesis.common.util.GwtReplaceable.createRuntimeException;
import static tekgenesis.common.util.GwtReplaceable.isInstance;

/**
 * Represents optional values. Instances of Option are either an instance of Some or the object
 * None. This is a replacement for using <code>null</code> as a way to represent an absent value.
 */
public abstract class Option<T> implements Iterable<T> {

    //~ Constructors .................................................................................................................................

    private Option() {}

    //~ Methods ......................................................................................................................................

    /**
     * Returns an Option of the specified type or none if the contained value is not an instance of
     * that class.
     */
    @GwtIncompatible public abstract <E> Option<E> castTo(Class<E> target);

    /**
     * Returns this Option if it is nonempty and applying the predicate p to this Option's value
     * returns true. Otherwise, return None.
     */
    @NotNull public abstract Option<T> filter(@NotNull Predicate<? super T> predicate);
    /**
     * Returns the option's flatted mapped value as an option if its nonempty (The mapping function
     * returns an Option itself). Otherwise return an empty one.
     */
    @NotNull public abstract <U> Option<U> flatMap(@NotNull final Function<? super T, Option<U>> f);

    /**
     * Returns the contained instance, which must be present.
     *
     * @throws  NoSuchElementException  if the instance is not defined
     */
    @NotNull public abstract T get();

    /** If a value is empty, invoke the specified runnable, otherwise do nothing. */
    public abstract Option<T> ifEmpty(Runnable runnable);
    /**
     * If a value is defined, invoke the specified consumer with the value, otherwise do nothing.
     */
    public abstract Option<T> ifPresent(Consumer<? super T> consumer);

    /**
     * If a value is present, performs the given action with the value, otherwise performs the given
     * empty-based action.
     */
    public abstract void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction);
    /**
     * Returns the option's mapped value as an option if its nonempty, otherwise return an empty
     * one.
     */
    @NotNull public abstract <U> Option<U> map(@NotNull final Function<? super T, ? extends U> f);

    /** Returns this option if it is nonempty, otherwise return alternative supplier. */
    @NotNull public abstract Option<T> or(@NotNull Supplier<Option<T>> alternative);

    /** Returns the option's value if the option is nonempty, otherwise return defaultValue. */
    @Contract("!null -> !null")
    @Nullable public abstract T orElse(@Nullable T defaultValue);

    /**
     * Returns the option's value if the option is nonempty, otherwise return the value provided by
     * the supplier.
     */
    @NotNull public abstract T orElseGet(@NotNull Supplier<T> supplier);

    /**
     * Returns the contained instance, which must be present. Otherwise throws supplier exception
     * (handly method for exception factories).
     */
    @NotNull public abstract <X extends Throwable> T orElseThrow(@NotNull final Supplier<? extends X> supplier)
        throws X;

    /** Convert the option to an Immutable List. */
    public abstract ImmutableList<T> toList();
    @Override public abstract String toString();

    /**
     * Returns the contained instance, which must be present. Otherwise throws an
     * IllegalStateException todo @deprecate use orElseThrow
     */
    @NotNull public abstract T getOrFail(String errorMsg);

    /**
     * Returns the contained instance, which must be present. Otherwise it constructs an Exception
     * and throws it \ todo @deprecate use orElseThrow
     */
    @Deprecated @NotNull public abstract T getOrFail(Class<? extends RuntimeException> e, Object... args);

    /**
     * Returns the option's value if the option is nonempty, otherwise returns <code>null.</code>
     */
    @Nullable public abstract T getOrNull();

    /** Returns {@code true} if there is a value present, otherwise {@code false}. */
    public abstract boolean isPresent();

    /** Returns true if the instance is {@link #empty()}. */
    public abstract boolean isEmpty();

    //~ Methods ......................................................................................................................................

    /** Returns the empty Option. */
    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> Option<T> empty() {
        return (Option<T>) EMPTY;
    }

    /** Returns an option from a java.util.Optional. */
    @NotNull
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Option<T> fromOptional(@NotNull Optional<T> optional) {
        return optional.isPresent() ? of(optional.get()) : empty();
    }

    /**
     * Returns the "empty" <code>none</code> Option.
     *
     * <p>todo mark as @deprecated use empty</p>
     */
    @NotNull public static <T> Option<T> none() {
        return empty();
    }

    /** Returns a "non empty" Option. */
    @NotNull public static <T> Option<T> of(@NotNull T value) {
        return new Some<>(value);
    }
    /**
     * If {@code value} is non-null, returns an {@link Option} instance containing that value
     * otherwise returns {@link #empty()} }.
     */
    @NotNull public static <T> Option<T> ofNullable(@Nullable T value) {
        return value == null ? empty() : of(value);
    }

    /**
     * If {@code value} is non-null, returns an {@link Option} instance containing that value
     * otherwise returns {@link #empty()} }.* todo @deprecate
     */
    @NotNull public static <T> Option<T> option(@Nullable T value) {
        return ofNullable(value);
    }

    /** Returns a "non empty" Option. todo @deprecate */
    @NotNull public static <T> Option<T> some(@NotNull T value) {
        return of(value);
    }

    //~ Static Fields ................................................................................................................................

    @NonNls public static final String OPTION_EMPTY = "Option.empty";

    private static final Option<Object> EMPTY = new Option<Object>() {
            @Override public boolean isPresent() {
                return false;
            }

            @NotNull @Override public Object get() {
                throw new NoSuchElementException();
            }

            @Override public Option<Object> ifPresent(final Consumer<? super Object> consumer) {
                return this;
            }

            @Override public void ifPresentOrElse(Consumer<? super Object> action, Runnable emptyAction) {
                emptyAction.run();
            }

            @Override public Option<Object> ifEmpty(Runnable r) {
                r.run();
                return this;
            }

            @Override public ImmutableList<Object> toList() {
                return emptyList();
            }

            @NotNull public <U> Seq<U> flatMapToSeq(@NotNull Function<? super Object, Iterable<U>> f) {
                return emptyList();
            }

            /**
             * Returns the option's mapped value as an option if its nonempty, otherwise return an
             * empty one.
             */
            @NotNull public final <U> Option<U> map(@NotNull final Function<? super Object, ? extends U> f) {
                return empty();
            }

            @NotNull @Override public <U> Option<U> flatMap(@NotNull Function<? super Object, Option<U>> f) {
                return empty();
            }

            @NotNull @Override public Object getOrFail(Class<? extends RuntimeException> e, Object... args) {
                throw createRuntimeException(e, args);
            }

            @NotNull @Override
            @SuppressWarnings("RedundantThrowsDeclaration")
            public <X extends Throwable> Object orElseThrow(@NotNull Supplier<? extends X> supplier)
                throws X
            {
                throw supplier.get();
            }

            @Nullable @Override public Object getOrNull() {
                return null;
            }

            @Override public boolean isEmpty() {
                return true;
            }

            @NotNull @Override public Option<Object> filter(@NotNull Predicate<? super Object> predicate) {
                return this;
            }

            @NotNull public Option<Object> orElse(@NotNull Option<Object> alternative) {
                return alternative;
            }

            @NotNull @Override public Option<Object> or(@NotNull Supplier<Option<Object>> alternative) {
                return alternative.get();
            }

            @Nullable @Override public Object orElse(@Nullable Object defaultValue) {
                return defaultValue;
            }

            @NotNull @Override public Object orElseGet(@NotNull Supplier<Object> supplier) {
                return supplier.get();
            }

            @NotNull @Override public Object getOrFail(String errorMsg) {
                throw new IllegalStateException(errorMsg);
            }

            @NotNull public Iterator<Object> iterator() {
                return emptyIterator();
            }

            @Override public String toString() {
                return OPTION_EMPTY;
            }

            @GwtIncompatible @Override
            @SuppressWarnings("unchecked")
            public <E> Option<E> castTo(Class<E> target) {
                return (Option<E>) EMPTY;
            }
        };

    //~ Inner Classes ................................................................................................................................

    private static class Some<T> extends Option<T> {
        @NotNull final T value;

        public Some(@NotNull T value) {
            this.value = ensureNotNull(value);
        }

        @GwtIncompatible @Override
        @SuppressWarnings("unchecked")
        public <E> Option<E> castTo(Class<E> target) {
            return (Option<E>) (isInstance(target, value) ? this : EMPTY);
        }

        @Override
        @SuppressWarnings({ "unchecked", "NonJREEmulationClassesInClientCode" })
        public boolean equals(final Object obj) {
            return obj instanceof Some && ((Some<T>) obj).value.equals(value);
        }

        @NotNull @Override public Option<T> filter(@NotNull Predicate<? super T> predicate) {
            return ensureNotNull(predicate.test(value)) ? this : empty();
        }

        @NotNull @Override public <U> Option<U> flatMap(@NotNull Function<? super T, Option<U>> f) {
            return ensureNotNull(f).apply(value);
        }

        @NotNull @Override public T get() {
            return value;
        }

        @Override public int hashCode() {
            return value.hashCode();
        }

        @Override public Option<T> ifEmpty(Runnable runnable) {
            return this;
        }

        @Override public Option<T> ifPresent(final Consumer<? super T> consumer) {
            consumer.accept(value);
            return this;
        }

        @Override public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
            action.accept(value);
        }

        @NotNull public Iterator<T> iterator() {
            return Collections.singleton(value).iterator();
        }
        /**
         * Returns the option's mapped value as an option if its nonempty, otherwise return an empty
         * one.
         */
        @NotNull @Override public <U> Option<U> map(@NotNull final Function<? super T, ? extends U> f) {
            return ofNullable(f.apply(value));
        }

        @NotNull @Override public Option<T> or(@NotNull Supplier<Option<T>> alternative) {
            return this;
        }

        @NotNull public Option<T> orElse(@NotNull Option<T> alternative) {
            return this;
        }

        @NotNull @Override public T orElse(@Nullable T defaultValue) {
            return value;
        }

        @NotNull @Override public T orElseGet(@NotNull Supplier<T> supplier) {
            return value;
        }

        @NotNull @Override public <X extends Throwable> T orElseThrow(@NotNull Supplier<? extends X> supplier) {
            return value;
        }

        @Override public ImmutableList<T> toList() {
            return listOf(value);
        }

        @Override public String toString() {
            return "some(" + value + ")";
        }

        @NotNull @Override public T getOrFail(String errorMsg) {
            return value;
        }

        @NotNull @Override public T getOrFail(Class<? extends RuntimeException> e, Object... args) {
            return value;
        }

        @Override public T getOrNull() {
            return value;
        }

        @Override public boolean isPresent() {
            return true;
        }

        @Override public boolean isEmpty() {
            return false;
        }
    }  // end class Some
}  // end class Option
