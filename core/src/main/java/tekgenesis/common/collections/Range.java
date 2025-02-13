
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.Predefined;
import tekgenesis.common.core.Functions;
import tekgenesis.common.core.Mutable;
import tekgenesis.common.core.RichComparable;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.Predefined.checkArgument;
import static tekgenesis.common.Predefined.ensureNotNull;
import static tekgenesis.common.collections.Seq.createSeq;

/**
 * A range (or "interval") defines the boundaries around a contiguous span of values of some
 * {@code Comparable} type; for example, "integers from 1 to 100 inclusive." Note that it is not
 * possible to iterate over these contained values.
 *
 * <p>Types of ranges:</p>
 *
 * <p>Each end of the range may be bounded or unbounded. If bounded, there is an associated endpoint
 * value, and the range is considered to be either open (does not include the endpoint) or closed
 * (includes the endpoint) on that side. With three possibilities on each side, this yields nine
 * basic types of ranges, enumerated below. (Notation: a square bracket ({@code [ ]}) indicates that
 * the range is closed on that side; a parenthesis ({@code ( )}) means it is either open or
 * unbounded. The construct {@code {x | statement}} is read "the set of all x such that statement.")
 * </p>
 *
 * <p>Notation / Definition / Factory method (a..b) {x | a < x < b}} {@link Range#open open} [a..b]
 * {x | a <= x <= b}} {@link Range#closed closed} (a..b] {x | a < x <= b}}
 * {@link Range#openClosed openClosed} [a..b) {x | a <= x < b}} {@link Range#closedOpen closedOpen}
 * (a..+∞) {x | x > a}} {@link Range#greaterThan greaterThan} [a..+∞) {x | x >= a}}
 * {@link Range#atLeast atLeast} (-∞..b) {x | x < b}} {@link Range#lessThan lessThan} (-∞..b] {x | x
 * <= b}} {@link Range#atMost atMost} (-∞..+∞) {x}} {@link Range#all all}</p>
 *
 * <p>When both endpoints exist, the upper endpoint may not be less than the lower. The endpoints
 * may be equal only if at least one of the bounds is closed:</p>
 *
 * <p>1- {@code [a..a]} : a singleton range 2- {@code [a..a); (a..a]} : {@linkplain #isEmpty empty}
 * ranges; also valid 3- {@code (a..a)} : invalid; an exception will be thrown</p>
 */
@SuppressWarnings("JavaDoc")
public class Range<C extends Comparable<? super C>> implements Predicate<C> {

    //~ Instance Fields ..............................................................................................................................

    private final Cut<C> lowerBound;
    private final Cut<C> upperBound;

    //~ Constructors .................................................................................................................................

    private Range(@NotNull final Cut<C> lowerBound, @NotNull final Cut<C> upperBound) {
        if (lowerBound.compareTo(upperBound) > 0 || lowerBound == Cut.<C>aboveAll() || upperBound == Cut.<C>belowAll())
            throw new IllegalArgumentException("Invalid range: " + toString(lowerBound, upperBound));
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    //~ Methods ......................................................................................................................................

    /**
     * Returns true if value is within the bounds of this range. For example, on the range [0..2),
     * contains(1) returns true, while contains(2) returns false.
     */
    public boolean contains(@NotNull final C value) {
        return lowerBound.isLessThan(value) && !upperBound.isLessThan(value);
    }

    /** Returns true if every element in values contained in this range. */
    public boolean containsAll(Iterable<? extends C> values) {
        if (Predefined.isEmpty(values)) return true;

        for (final C value : values) {
            if (!contains(value)) return false;
        }
        return true;
    }

    /**
     * Returns true if the bounds of other do not extend outside the bounds of this range. Examples:
     *
     * <ul>
     *   <li>{@code [3..6]} encloses {@code [4..5]}</li>
     *   <li>{@code (3..6)} encloses {@code (3..6)}</li>
     *   <li>{@code [3..6]} encloses {@code [4..4)} (even though the latter is empty)</li>
     *   <li>{@code (3..6]} does not enclose {@code [3..6]}</li>
     *   <li>{@code [4..5]} does not enclose {@code (3..6)} (even though it contains every value
     *     contained by the latter range)</li>
     *   <li>{@code [3..6]} does not enclose {@code (1..1]} (even though it contains every value
     *     contained by the latter range)</li>
     * </ul>
     *
     * <p>Note that if {@code a.encloses(b)}, then {@code b.contains(v)} implies
     * {@code a.contains(v)}, but as the last two examples illustrate, the converse is not always
     * true.</p>
     *
     * <p>Being reflexive, antisymmetric and transitive, the {@code encloses} relation defines a <i>
     * partial order</i> over ranges.</p>
     */
    public boolean encloses(Range<C> other) {
        return lowerBound.compareTo(other.lowerBound) <= 0 && upperBound.compareTo(other.upperBound) >= 0;
    }

    /** Enumerate the Range. */
    public Seq<C> enumerate(Function<C, C> calculateNext) {
        checkArgument(lowerBound != Cut.belowAll(), LOWER_BOUND_ERROR);
        final C lb    = lowerBound.getEndpoint();
        final C first = lowerBound.isLessThan(lb) ? lb : calculateNext.apply(lb);

        return createSeq(() ->
                new ImmutableIterator<C>() {
                    final Mutable<C> next = new Mutable.Object<>(first);

                    C nextValue() {
                        return ensureNotNull(next.getValue());
                    }
                    @Override public boolean hasNext() {
                        return !upperBound.isLessThan(nextValue());
                    }
                    @Override public C next() {
                        final C result = nextValue();
                        next.setValue(calculateNext.apply(result));
                        return result;
                    }
                });
    }

    /**
     * Returns true if object is a range having the same endpoints and bound types as this range.
     * Note that discrete ranges such as {@code (1..4)} and {@code [2..3]} are <b>not</b> equal to
     * one another, despite the fact that they each contain precisely the same set of values.
     * Similarly, empty ranges are not equal unless they have exactly the same representation, so
     * {@code [3..3)}, {@code (3..3]}, {@code (4..4]} are all unequal.
     */
    @Override public boolean equals(@Nullable Object object) {
        if (object instanceof Range) {
            final Range<?> other = (Range<?>) object;
            return lowerBound.equals(other.lowerBound) && upperBound.equals(other.upperBound);
        }
        return false;
    }

    /** Returns a hash code for this range. */
    @Override public int hashCode() {
        return lowerBound.hashCode() * 31 + upperBound.hashCode();
    }

    /**
     * Returns the maximal range enclosed by both this range and connectedRange, if such a range
     * exists.
     *
     * <p>For example, the intersection of {@code [1..5]} and {@code (3..7)} is {@code (3..5]}. The
     * resulting range may be empty: for example, {@code [1..5)} intersected with {@code [5..7)}
     * yields the empty range {@code [5..5)}.</p>
     *
     * <p>The intersection exists if and only if the two ranges are connected.</p>
     *
     * <p>The intersection operation is commutative, associative and idempotent, and its identity
     * element is {@link Range#all}).</p>
     *
     * @throws  IllegalArgumentException  if {@code isConnected(connectedRange)} is false
     */
    @NotNull public Range<C> intersection(Range<C> connectedRange) {
        final int lower = lowerBound.compareTo(connectedRange.lowerBound);
        final int upper = upperBound.compareTo(connectedRange.upperBound);
        if (lower >= 0 && upper <= 0) return this;
        else if (lower <= 0 && upper >= 0) return connectedRange;
        else return create((lower >= 0) ? lowerBound : connectedRange.lowerBound, (upper <= 0) ? upperBound : connectedRange.upperBound);
    }

    /**
     * Returns the minimal range that encloses both this range and other. For example, the span of
     * {@code [1..3]} and {@code (5..7)} is {@code [1..7)}.
     *
     * <p>If the input ranges are {@linkplain #isConnected connected}, the returned range can also
     * be called their union. If they are not, note that the span might contain values that are not
     * contained in either input range.</p>
     *
     * <p>Like {@link #intersection(Range) intersection}, this operation is commutative, associative
     * and idempotent. Unlike it, it is always well-defined for any two input ranges.</p>
     */
    @NotNull public Range<C> span(Range<C> other) {
        final int lower = lowerBound.compareTo(other.lowerBound);
        final int upper = upperBound.compareTo(other.upperBound);
        if (lower <= 0 && upper >= 0) return this;
        else if (lower >= 0 && upper <= 0) return other;
        else return create((lower <= 0) ? lowerBound : other.lowerBound, (upper >= 0) ? upperBound : other.upperBound);
    }

    /** Equivalent to {@link #contains}; provided to satisfy the {@link Predicate} interface. */
    @Override public boolean test(C input) {
        return contains(input);
    }

    /** Returns a string representation of this range, such as {@code "[3..5)"}. */
    @Override public String toString() {
        return toString(Functions.mkString());
    }

    /**
     * Returns a string representation of this range, such as {@code "[3..5)"}, using given function
     * to format bound elements.
     */
    @SuppressWarnings("WeakerAccess")
    public String toString(@NotNull final Function<C, String> formatter) {
        return toString(lowerBound, upperBound, formatter);
    }

    /**
     * Returns {@code true} if there exists a (possibly empty) range which is
     * {@linkplain #encloses enclosed} by both this range and {@code other}.
     *
     * <p>For example,</p>
     *
     * <ul>
     *   <li>{@code [2, 4)} and {@code [5, 7)} are not connected</li>
     *   <li>{@code [2, 4)} and {@code [3, 5)} are connected, because both enclose {@code [3, 4)}
     *   </li>
     *   <li>{@code [2, 4)} and {@code [4, 6)} are connected, because both enclose the empty range
     *     {@code [4, 4)}</li>
     * </ul>
     *
     * <p>Note that this range and other have a well-defined union and intersection (as a single,
     * possibly-empty range) if and only if this method returns true.</p>
     *
     * <p>The connectedness relation is both reflexive and symmetric, but does not form an as it is
     * not transitive.</p>
     */
    public boolean isConnected(Range<C> other) {
        return lowerBound.compareTo(other.upperBound) <= 0 && other.lowerBound.compareTo(upperBound) <= 0;
    }

    /**
     * Returns {@code true} if this range is of the form {@code [v..v)} or {@code (v..v]}. (This
     * does not encompass ranges of the form {@code (v..v)}, because such ranges are <i>invalid</i>
     * and can't be constructed at all.)
     */
    public boolean isEmpty() {
        return lowerBound.equals(upperBound);
    }

    /** Package protected lower bound getter. */
    @NotNull Cut<C> getLowerBound() {
        return lowerBound;
    }

    /** Package protected upper bound getter. */
    @NotNull Cut<C> getUpperBound() {
        return upperBound;
    }

    //~ Methods ......................................................................................................................................

    /** Returns a range that contains every value of type {@code C}. */
    public static <C extends Comparable<? super C>> Range<C> all() {
        return cast(ALL);
    }

    /** Returns a range that contains all values greater than or equal to {@code endpoint}. */
    public static <C extends Comparable<? super C>> Range<C> atLeast(@NotNull final C endpoint) {
        return create(Cut.below(endpoint), Cut.aboveAll());
    }

    /** Returns a range that contains all values less than or equal to {@code endpoint}. */
    public static <C extends Comparable<? super C>> Range<C> atMost(@NotNull final C endpoint) {
        return create(Cut.belowAll(), Cut.above(endpoint));
    }

    /**
     * Returns a range that contains all values greater than or equal to {@code lower} and less than
     * or equal to {@code upper}.
     *
     * @throws  IllegalArgumentException  if {@code lower} is greater than {@code upper}
     */
    @SuppressWarnings("WeakerAccess")
    public static <C extends Comparable<? super C>> Range<C> closed(@NotNull final C lower, @NotNull final C upper) {
        return create(Cut.below(lower), Cut.above(upper));
    }

    /**
     * Returns a range that contains all values greater than or equal to {@code lower} and strictly
     * less than {@code upper}.
     *
     * @throws  IllegalArgumentException  if {@code lower} is greater than {@code upper}
     */
    public static <C extends Comparable<? super C>> Range<C> closedOpen(@NotNull final C lower, @NotNull final C upper) {
        return create(Cut.below(lower), Cut.below(upper));
    }

    /**
     * Returns the minimal range that {@linkplain Range#contains(Comparable) contains} all of the
     * given values. The returned range is closed on both ends.
     *
     * @throws  NoSuchElementException  if {@code values} is empty
     * @throws  NullPointerException    if any of {@code values} is null
     */
    @SuppressWarnings("DuplicateStringLiteralInspection")
    public static <C extends Comparable<? super C>> Range<C> encloseAll(@NotNull final Iterable<C> values) {
        final Iterator<C> valueIterator = values.iterator();
        C                 min           = ensureNotNull(valueIterator.next(), "Empty enclosing values!");
        C                 max           = min;
        while (valueIterator.hasNext()) {
            final C value = ensureNotNull(valueIterator.next(), "Null enclosing value!");
            min = min(min, value);
            max = max(max, value);
        }
        return closed(min, max);
    }

    /** Returns a range that contains all values strictly greater than {@code endpoint}. */
    public static <C extends Comparable<? super C>> Range<C> greaterThan(@NotNull final C endpoint) {
        return create(Cut.above(endpoint), Cut.aboveAll());
    }

    /** Returns a range that contains all values strictly less than {@code endpoint}. */
    public static <C extends Comparable<? super C>> Range<C> lessThan(@NotNull final C endpoint) {
        return create(Cut.belowAll(), Cut.below(endpoint));
    }

    /**
     * Returns a range that contains all values strictly greater than {@code lower} and strictly
     * less than {@code upper}.
     *
     * @throws  IllegalArgumentException  if {@code lower} is greater than <i>or equal to</i>
     *                                    {@code upper}
     */
    public static <C extends Comparable<? super C>> Range<C> open(@NotNull final C lower, @NotNull final C upper) {
        return create(Cut.above(lower), Cut.below(upper));
    }

    /**
     * Returns a range that contains all values strictly greater than {@code lower} and less than or
     * equal to {@code upper}.
     *
     * @throws  IllegalArgumentException  if {@code lower} is greater than {@code upper}
     */
    public static <C extends Comparable<? super C>> Range<C> openClosed(C lower, C upper) {
        return create(Cut.above(lower), Cut.above(upper));
    }

    /**
     * Returns a range that {@linkplain Range#contains(Comparable) contains} only the given value.
     * The returned range is closed on both ends.
     */
    public static <C extends Comparable<? super C>> Range<C> singleton(@NotNull final C value) {
        return closed(value, value);
    }

    static <C extends Comparable<? super C>> Range<C> create(@NotNull final Cut<C> lowerBound, @NotNull final Cut<C> upperBound) {
        return new Range<>(lowerBound, upperBound);
    }

    private static <C extends Comparable<? super C>> C max(@NotNull final C hansel, @NotNull final C gretel) {
        return hansel.compareTo(gretel) >= 0 ? hansel : gretel;
    }

    private static <C extends Comparable<? super C>> C min(@NotNull final C hansel, @NotNull final C gretel) {
        return hansel.compareTo(gretel) <= 0 ? hansel : gretel;
    }

    private static <T extends Comparable<? super T>> String toString(@NotNull final Cut<T> lowerBound, @NotNull final Cut<T> upperBound) {
        return toString(lowerBound, upperBound, Functions.mkString());
    }

    private static <T extends Comparable<? super T>> String toString(@NotNull final Cut<T> lowerBound, @NotNull final Cut<T> upperBound,
                                                                     @NotNull final Function<T, String> formatter) {
        final StringBuilder sb = new StringBuilder();
        lowerBound.describeAsLowerBound(sb, formatter);
        sb.append("..");
        upperBound.describeAsUpperBound(sb, formatter);
        return sb.toString();
    }

    //~ Static Fields ................................................................................................................................

    @NonNls public static final String LOWER_BOUND_ERROR = "Need defined lower bound";

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final Range<?> ALL = new Range(Cut.belowAll(), Cut.aboveAll());

    //~ Inner Classes ................................................................................................................................

    /**
     * Implementation detail for the internal structure of {@link Range} instances. Represents a
     * unique way of "cutting" instances of type {@code C} into two sections; this can be done below
     * a certain value, above a certain value, below all values or above all values. With this
     * object defined in this way, an interval can always be represented by a pair of {@code Cut}
     * instances.
     */
    abstract static class Cut<C extends Comparable<? super C>> implements RichComparable<Cut<C>> {
        private final C endpoint;

        private Cut(@Nullable final C endpoint) {
            this.endpoint = endpoint;
        }

        @Override
        @SuppressWarnings({ "NonJREEmulationClassesInClientCode", "InstanceofThis" })
        public int compareTo(@NotNull Cut<C> that) {
            if (that == Cut.<C>belowAll()) return 1;
            if (that == Cut.<C>aboveAll()) return -1;

            assert that.endpoint != null;
            final int result = getEndpoint().compareTo(that.endpoint);

            return result != 0 ? result :  // Same value. Below comes before above.
                                         Boolean.valueOf(this instanceof AboveValue).compareTo(that instanceof AboveValue);
        }

        @Override public boolean equals(Object obj) {
            if (obj instanceof Cut) {
                final Cut<C> that = cast(obj);
                return compareTo(that) == 0;
            }
            return false;
        }

        @Override public int hashCode() {
            return endpoint != null ? endpoint.hashCode() : 0;
        }

        abstract void describeAsLowerBound(@NotNull final StringBuilder builder, @NotNull final Function<C, String> formatter);
        abstract void describeAsUpperBound(@NotNull final StringBuilder builder, @NotNull final Function<C, String> formatter);

        @NotNull C getEndpoint() {
            return ensureNotNull(endpoint, "Null endpoint!");
        }

        abstract boolean isLessThan(@NotNull final C value);

        static <C extends Comparable<? super C>> Cut<C> below(@NotNull final C endpoint) {
            return new BelowValue<>(endpoint);
        }

        private static <C extends Comparable<? super C>> Cut<C> above(@NotNull final C endpoint) {
            return new AboveValue<>(endpoint);
        }

        private static <C extends Comparable<? super C>> Cut<C> aboveAll() {
            return cast(AboveAll.INSTANCE);
        }

        private static <C extends Comparable<? super C>> Cut<C> belowAll() {
            return cast(BelowAll.INSTANCE);
        }

        @SuppressWarnings("ComparableImplementedButEqualsNotOverridden")
        private static final class AboveAll<C extends Comparable<? super C>> extends Cut<C> {
            private AboveAll() {
                super(null);
            }

            @Override public int compareTo(@NotNull Cut<C> that) {
                return (this == that) ? 0 : 1;
            }

            @Override public boolean isLessThan(@NotNull C value) {
                return false;
            }

            @Override void describeAsLowerBound(@NotNull StringBuilder b, @NotNull Function<C, String> f) {
                throw new IllegalStateException();
            }

            @Override void describeAsUpperBound(@NotNull StringBuilder b, @NotNull Function<C, String> f) {
                b.append("+\u221e)");
            }

            @SuppressWarnings("rawtypes")
            private static final AboveAll INSTANCE = new AboveAll();
        }

        private static class AboveValue<C extends Comparable<? super C>> extends Cut<C> {
            private AboveValue(@NotNull C endpoint) {
                super(endpoint);
            }

            @Override public boolean isLessThan(@NotNull C value) {
                return getEndpoint().compareTo(value) < 0;
            }

            @Override void describeAsLowerBound(@NotNull StringBuilder b, @NotNull Function<C, String> f) {
                b.append('(').append(f.apply(getEndpoint()));
            }

            @Override void describeAsUpperBound(@NotNull StringBuilder b, @NotNull Function<C, String> f) {
                b.append(f.apply(getEndpoint())).append(']');
            }
        }

        @SuppressWarnings("ComparableImplementedButEqualsNotOverridden")
        private static final class BelowAll<C extends Comparable<? super C>> extends Cut<C> {
            private BelowAll() {
                super(null);
            }

            @Override public int compareTo(@NotNull Cut<C> that) {
                return (this == that) ? 0 : -1;
            }

            @Override public boolean isLessThan(@NotNull C value) {
                return true;
            }

            @Override void describeAsLowerBound(@NotNull StringBuilder b, @NotNull Function<C, String> f) {
                b.append("(-\u221e");
            }

            @Override void describeAsUpperBound(@NotNull StringBuilder b, @NotNull Function<C, String> f) {
                throw new IllegalStateException();
            }

            @SuppressWarnings("rawtypes")
            private static final BelowAll INSTANCE = new BelowAll();
        }

        private static class BelowValue<C extends Comparable<? super C>> extends Cut<C> {
            private BelowValue(@NotNull C endpoint) {
                super(endpoint);
            }

            @Override public boolean isLessThan(@NotNull C value) {
                return getEndpoint().compareTo(value) <= 0;
            }

            @Override void describeAsLowerBound(@NotNull StringBuilder b, @NotNull Function<C, String> f) {
                b.append('[').append(f.apply(getEndpoint()));
            }

            @Override void describeAsUpperBound(@NotNull StringBuilder b, @NotNull Function<C, String> f) {
                b.append(f.apply(getEndpoint())).append(')');
            }
        }
    }  // end class Cut
}  // end class Range
