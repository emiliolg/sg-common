
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.util.Objects;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.Predefined;

/**
 * Some utility methods to adapt, create, or operate with {@link Predicate} objects.
 */
public class Predicates {

    //~ Constructors .................................................................................................................................

    private Predicates() {}

    //~ Methods ......................................................................................................................................

    /** Always false predicate. */
    public static <E> Predicate<E> alwaysFalse() {
        return o -> false;
    }

    /** Always true predicate. */
    public static <E> Predicate<E> alwaysTrue() {
        return o -> true;
    }

    /** A predicate that matches Strings ending with a given suffix. */
    public static Predicate<String> endsWith(final String suffix) {
        return s -> s != null && s.endsWith(suffix);
    }

    /** A predicate that check that the object is equal to the specified one. */
    public static <T> Predicate<T> equal(@Nullable final T value) {
        return o -> Predefined.equal(o, value);
    }

    /** A predicate that matches a String length. */
    public static Predicate<String> hasLength(final int length) {
        return s -> s != null && s.length() == length;
    }

    /**
     * Returns a java.util.function.Predicate that checks true if element is contained in iterable
     * (using equals()).
     */
    public static <E> Predicate<E> in(@NotNull final Iterable<E> iterable) {
        return e -> {
                   for (final E next : iterable)
                       if (next.equals(e)) return true;
                   return false;
               };
    }

    /** A predicate that matches a give regular expression. */
    public static Predicate<String> matches(final String regex) {
        return s -> s != null && s.matches(regex);
    }

    /** Negates a given predicate. */
    public static <E> Predicate<E> not(@NotNull final Predicate<E> predicate) {
        return predicate.negate();
    }

    /** A predicate that matches Strings starting with a given prefix. */
    public static Predicate<String> startsWith(final String prefix) {
        return s -> s != null && s.startsWith(prefix);
    }

    /** Returns a predicate that checks if a given element is not null. */
    public static <E> Predicate<E> isNotNull() {
        return Objects::nonNull;
    }

    /** Returns a predicate that checks if a given element is null. */
    public static <E> Predicate<E> isNull() {
        return Objects::isNull;
    }

    /** Returns a predicate that checks if a given string element is not null or empty. */
    public static Predicate<String> isNotEmpty() {
        return Predefined::isNotEmpty;
    }
}  // end class Predicates
