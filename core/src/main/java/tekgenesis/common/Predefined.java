
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.annotation.GwtIncompatible;
import tekgenesis.common.collections.Colls;
import tekgenesis.common.core.Constants;
import tekgenesis.common.core.Enumeration;
import tekgenesis.common.core.Option;
import tekgenesis.common.core.ToStringBuilder;
import tekgenesis.common.exception.ApplicationException;

import static java.lang.Integer.toHexString;

/**
 * This class contains common static methods It is intended to be statically import in every
 * tekgenesis java source file.
 */
@SuppressWarnings("ClassWithTooManyMethods")
public final class Predefined {

    //~ Constructors .................................................................................................................................

    private Predefined() {}

    //~ Methods ......................................................................................................................................

    /** Makes an unchecked cast. Useful for Generics due to type erasure */
    @Contract("!null -> !null; null -> null")
    @Nullable
    @SuppressWarnings("unchecked")
    public static <A, B> A cast(@Nullable B b) {
        return (A) b;
    }

    /** Check an argument and throw IllegalArgumentException if the check fails. */
    public static void checkArgument(boolean expression, @NotNull String errorMessage) {
        if (!expression) throw new IllegalArgumentException(errorMessage);
    }

    /**
     * <p>Null safe comparison of Comparables.</p>
     *
     * @param   a  the first comparable, may be null
     * @param   b  the second comparable, may be null
     *
     * @return  a negative value if a < b, zero if a = b and a positive value if a > b
     *
     * @see     Comparator#compare(Object, Object)
     */
    public static <T extends Comparable<T>> int compare(@Nullable Comparable<T> a, @Nullable T b) {
        return a == b ? 0 : a == null ? -1 : b == null ? 1 : a.compareTo(b);
    }

    /**
     * Creates an instance of a Builder class used to implement {@link Object#toString()}.
     *
     * <p>This is intended to be called in the following way:</p>
     *
     * <pre>   {@code
     * <p/>
     *   createToStringBuilder("Point")
     *       .add("x", x)
     *       .add("y", y)
     *       .build();
     *   // Will return something like Point(x=1, y=10)
     * <p/>
     *   createToStringBuilder("Point").add(x).add(y).build();
     *   // Will return something like Point(1, 10)
     *   }}</pre>
     *
     * @param   name  the object to generate the string for.
     *
     * @return  A Builder used to build the {@link Object#toString()} method.
     */
    public static ToStringBuilder createToStringBuilder(@NotNull String name) {
        return new ToStringBuilder(name);
    }

    /** If the Value is null throw a {@link NullPointerException} with the specified Message. */
    @Contract("null -> fail; !null -> !null")
    public static <T> T ensureNotNull(@Nullable final T value) {
        if (value == null) throw new NullPointerException();
        return value;
    }

    /** If the Value is null throw the exception returned by the supplier. */
    @Contract("null, _ -> fail; !null,_ -> !null")
    public static <T> T ensureNotNull(@Nullable final T value, Supplier<? extends RuntimeException> exception) {
        if (value == null) throw exception.get();
        return value;
    }

    /** If the Value is null throw a {@link NullPointerException} with the specified Message. */
    @Contract("null, _ -> fail; !null, _ -> !null")
    public static <T> T ensureNotNull(@Nullable final T value, @NotNull final String msg) {
        if (value == null) throw new NullPointerException(msg);
        return value;
    }

    /** If the Value is null throw a {@link ApplicationException} with the specified Message. */
    @Contract("null, _ -> fail; !null, _ -> !null")
    @GwtIncompatible public static <T> T ensureNotNull(@Nullable final T value, @NotNull Enumeration<?, String> msg) {
        if (value == null) throw new ApplicationException(msg);
        return value;
    }

    /**
     * If the Value is null throw a {@link ApplicationException} with the specified Message and
     * parameters.
     */
    @Contract("null, _, _-> fail; !null, _, _ -> !null")
    @GwtIncompatible public static <T> T ensureNotNull(@Nullable final T value, @NotNull Enumeration<?, String> msg, @NotNull Object... parameters) {
        if (value == null) throw new ApplicationException(msg, parameters);
        return value;
    }

    /**
     * <p>Compares two objects for equality, where either one or both objects may be {@code null}.
     * </p>
     */
    public static boolean equal(@Nullable Object a, @Nullable Object b) {
        return a == b || (a != null && a.equals(b));
    }

    /** Verify that 2 iterables have the same elements. */
    public static boolean equalElements(@Nullable Iterable<?> it1, @Nullable Iterable<?> it2) {
        return it1 == it2 || it1 != null && it2 != null && eq(it1, it2);
    }

    /** Create a hash code with all the elements of an {@link Iterable}. */
    public static <E> int hashCodeAll(@Nullable final Iterable<E> iterable) {
        if (iterable == null) return 0;
        int hashCode = 1;
        for (final E e : iterable)
            hashCode = Constants.HASH_SALT * hashCode + (e == null ? 0 : e.hashCode());
        return hashCode;
    }

    /**
     * Generates a hash code for multiple values.
     *
     * <p>This is useful for implementing {@link Object#hashCode()}. For example, in an object that
     * has three fields, {@code x}, {@code y}, and {@code z}, one could write:</p>
     *
     * <pre>
       public int hashCodeAll() {
         return hashCodeAll(x, y, z);
       }</pre>
     *
     * @param   first  The first object to hash
     * @param   rest   The other objects to hash
     *
     * @return  The composed hash code of the objects, or zero if null
     */
    public static int hashCodeAll(@Nullable Object first, @NotNull Object... rest) {
        int result = first == null ? 0 : first instanceof Enum ? ((Enum<?>) first).ordinal() : first.hashCode();
        for (final Object element : rest)
            result = Constants.HASH_SALT * result +
                     (element == null ? 0 : element instanceof Enum ? ((Enum<?>) element).ordinal() : element.hashCode());
        return result;
    }

    /**
     * <p>Gets an string that identifies the obj. The string is the one that would be produced by
     * the {@link Object#toString()} method if a class did not override toString itself.
     * {@code null} will return the String "null".</p>
     *
     * @param   obj  the obj to create a toString for, may be {@code null}
     *
     * @return  the default toString text, or "null" if {@code null} is passed in
     */
    @NotNull public static String identityToString(@Nullable Object obj) {
        return obj == null ? Constants.NULL_TO_STRING : obj.getClass().getName() + "@" + toHexString(System.identityHashCode(obj));
    }

    /** Execute the lambda if the value is not null. */
    public static <T> void ifPresent(@Nullable T value, Consumer<T> consumer) {
        if (value != null) consumer.accept(value);
    }

    /** Maps a possible null value. */
    @Contract("null,_ -> null")
    @Nullable
    @SuppressWarnings("unchecked")
    public static <B, A> B mapNullable(@Nullable A a, @NotNull Function<A, B> mapper) {
        return a == null ? null : mapper.apply(a);
    }

    /**
     * Returns the maximum of the given 2 elements. All elements must implement the <tt>
     * Comparable</tt> interface.
     *
     * @param   first   The first object to compare
     * @param   second  The other object to compare
     *
     * @return  the maximum of the given elements
     *
     * @see     Comparable
     */
    @NotNull public static <T extends Comparable<T>> T max(@NotNull T first, @NotNull T second) {
        T result = first;
        if (compare(second, first) > 0) result = second;
        return result;
    }

    /**
     * Returns the minimum of the given 2 elements. All elements must implement the <tt>
     * Comparable</tt> interface.
     *
     * @param   first   The first object to compare
     * @param   second  The other object to compare
     *
     * @return  the minimum of the given elements
     *
     * @see     Comparable
     */
    @NotNull public static <T extends Comparable<T>> T min(@NotNull T first, @NotNull T second) {
        T result = first;
        if (compare(second, first) < 0) result = second;
        return result;
    }

    /** If the String is not null returns the String, else returns the default String value. */
    @NotNull public static String notEmpty(@Nullable String value, @NotNull String defaultValue) {
        return isEmpty(value) ? defaultValue : value;
    }

    /** If the String is not null returns the String, else calculates the default String value. */
    @NotNull public static String notEmpty(@Nullable String value, @NotNull Supplier<String> defaultValue) {
        return isEmpty(value) ? defaultValue.get() : value;
    }

    /** Build an UnsupportedOperationException with a 'Not Implemented' Message. */
    public static UnsupportedOperationException notImplemented(String name) {
        return new UnsupportedOperationException("Not Implemented " + name);
    }

    /** If the String is not null returns the String, else returns "". */
    @NotNull public static String notNull(@Nullable String value) {
        return value != null ? value : "";
    }

    /** If the Integer is not null returns it else returns 0. */
    public static int notNull(@Nullable Integer value) {
        return value != null ? value : 0;
    }

    /** If the value is not null returns the value, else returns the default value. */
    @NotNull public static <T> Iterable<T> notNull(@Nullable Collection<T> collection) {
        return collection == null ? Colls.emptyIterable() : collection;
    }

    /** If the Integer is not null returns it else returns the defaultValue. */
    public static int notNull(@Nullable Integer value, int defaultValue) {
        return value != null ? value : defaultValue;
    }

    /** If the value is not null returns the value, else returns the default value. */
    @NotNull public static <T> T notNull(@Nullable T value, @NotNull T defaultValue) {
        return value != null ? value : defaultValue;
    }

    /** If the value is not null returns the value, else returns the suppliers result. */
    @NotNull public static <T> T notNull(@Nullable T value, @NotNull Supplier<T> supplier) {
        return value != null ? value : supplier.get();
    }

    /**
     * If {@code value} is non-null, returns an {@link Option} instance containing that value
     * otherwise returns {@link Option#empty()}.
     */
    @NotNull public static <T> Option<T> option(@Nullable T value) {
        return Option.option(value);
    }

    /** Create an Unreachable Exception. */
    @SuppressWarnings("DuplicateStringLiteralInspection")
    public static IllegalStateException unreachable() {
        return new IllegalStateException("Unreachable");
    }

    /** Create an Unreachable Exception. */
    public static IllegalStateException unreachable(@NotNull String message) {
        return new IllegalStateException("Unreachable " + message);
    }

    /**
     * .
     *
     * <p>Determine whether object is null or not.</p>
     */
    public static boolean isDefined(@Nullable Object object) {
        return object != null;
    }

    /**
     * Returns true if the String (Interpreted as a boolean) is equals to "true". false otherwise
     */
    public static boolean isTrue(String str) {
        return Boolean.TRUE.toString().equals(str);
    }

    /**
     * Returns {@code true} if the given string is null or is the empty string.
     *
     * @param   string  a string reference to check
     *
     * @return  {@code true} if the string is null or is the empty string
     */
    @Contract("null -> true")
    public static boolean isEmpty(@Nullable String string) {
        return string == null || string.isEmpty();
    }

    /**
     * Returns {@code true} if the given collection is null or is empty.
     *
     * @param   collection  a collection reference to check
     *
     * @return  {@code true} if the collection is null or it has no elements
     */
    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Returns {@code true} if the given iterable is null or is empty.
     *
     * @param   iterable  a iterable reference to check
     *
     * @return  {@code true} if the iterable is null or it has no elements
     */
    public static boolean isEmpty(@Nullable Iterable<?> iterable) {
        return iterable == null || iterable instanceof Collection && ((Collection<?>) iterable).isEmpty() || !iterable.iterator().hasNext();
    }

    /**
     * Returns {@code true} if the given string is not empty and not null.
     *
     * @param   string  a string reference to check
     *
     * @return  {@code true} if the string is not null and it is not the empty string
     */
    @Contract("null -> false")
    public static boolean isNotEmpty(@Nullable String string) {
        return string != null && !string.isEmpty();
    }

    /**
     * Returns {@code true} if the given array is not empty and not null.
     *
     * @param   array  a array reference to check
     *
     * @return  {@code true} if the array is not null and it is not the empty string
     */
    public static <T> boolean isNotEmpty(@Nullable T[] array) {
        return array != null && array.length > 0;
    }

    /**
     * Returns {@code true} if the given collection is not empty and not null.
     *
     * @param   collection  a collection reference to check
     *
     * @return  {@code true} if the collection is not null and is not empty
     */
    public static boolean isNotEmpty(@Nullable Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * Returns {@code true} if the given iterable is not empty and not null.
     *
     * @param   iterable  a iterable reference to check
     *
     * @return  {@code true} if the iterable is not null and is not empty
     */
    public static boolean isNotEmpty(@Nullable Iterable<?> iterable) {
        return !isEmpty(iterable);
    }

    private static boolean eq(Iterable<?> it1, Iterable<?> it2) {
        final Iterator<?> e2 = it2.iterator();
        for (final Object e : it1) {
            if (!e2.hasNext() || !equal(e, e2.next())) return false;
        }
        return !e2.hasNext();
    }
}  // end class Predefined
