
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.util.NoSuchElementException;

import org.junit.Test;

import tekgenesis.common.collections.Immutables;
import tekgenesis.common.collections.Range;
import tekgenesis.common.collections.Seq;
import tekgenesis.common.core.DateOnly;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;

import static tekgenesis.common.collections.Colls.emptyIterable;
import static tekgenesis.common.collections.Colls.listOf;
import static tekgenesis.common.collections.Range.*;
import static tekgenesis.common.core.DateOnly.date;

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class RangeTest {

    //~ Methods ......................................................................................................................................

    @Test public void allTest() {
        final Range<Integer> range = Range.all();

        assertThat(range.contains(Integer.MIN_VALUE)).isTrue();
        assertThat(range.contains(0)).isTrue();
        assertThat(range.contains(Integer.MAX_VALUE)).isTrue();
        assertThat(range.isEmpty()).isFalse();
        assertThat(range.toString()).isEqualTo("(-\u221e..+\u221e)");
        try {
            assertThat(range.enumerate(this::inc).take(5)).containsExactly(6, 7, 8, 9, 10);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo(LOWER_BOUND_ERROR);
        }
    }

    @Test public void atLeastTest() {
        final Range<Integer> range = Range.atLeast(6);

        assertThat(range.contains(Integer.MIN_VALUE)).isFalse();
        assertThat(range.contains(5)).isFalse();
        assertThat(range.contains(6)).isTrue();
        assertThat(range.contains(Integer.MAX_VALUE)).isTrue();
        assertThat(range.isEmpty()).isFalse();
        assertThat(range.toString()).isEqualTo("[6..+\u221e)");

        assertThat(range.enumerate(this::inc).take(5)).containsExactly(6, 7, 8, 9, 10);
    }

    @Test public void atMostTest() {
        final Range<Integer> range = Range.atMost(4);

        assertThat(range.contains(Integer.MIN_VALUE)).isTrue();
        assertThat(range.contains(4)).isTrue();
        assertThat(range.contains(5)).isFalse();
        assertThat(range.contains(Integer.MAX_VALUE)).isFalse();
        assertThat(range.isEmpty()).isFalse();
        assertThat(range.toString()).isEqualTo("(-\u221e..4]");
        try {
            assertThat(range.enumerate(this::inc).take(5)).containsExactly(6, 7, 8, 9, 10);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo(LOWER_BOUND_ERROR);
        }
    }

    @Test public void closedOpenTest() {
        final Range<Integer> range = closedOpen(5, 8);

        assertThat(range.contains(4)).isFalse();
        assertThat(range.contains(5)).isTrue();
        assertThat(range.contains(7)).isTrue();
        assertThat(range.contains(8)).isFalse();
        assertThat(range.toString()).isEqualTo("[5..8)");
        assertThat(range.enumerate(this::inc)).containsExactly(5, 6, 7);
    }

    @Test public void closedTest() {
        final Range<Integer> range = closed(5, 7);

        assertThat(range.contains(4)).isFalse();
        assertThat(range.contains(5)).isTrue();
        assertThat(range.contains(7)).isTrue();
        assertThat(range.contains(8)).isFalse();
        assertThat(range.isEmpty()).isFalse();
        assertThat(range.toString()).isEqualTo("[5..7]");
        assertThat(range.enumerate(this::inc)).containsExactly(5, 6, 7);

        try {
            closed(4, 3);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Invalid range: [4..3]");
        }
    }

    @Test public void containsAll() {
        final Range<Integer> range = closed(3, 5);
        final Seq<Integer>   empty = emptyIterable();
        assertThat(range.containsAll(listOf(3, 3, 4, 5))).isTrue();
        assertThat(range.containsAll(listOf(3, 3, 4, 5, 6))).isFalse();
        assertThat(range.containsAll(empty)).isTrue();

        assertThat(openClosed(3, 3).containsAll(empty)).isTrue();
    }

    @Test public void dates() {
        final DateOnly april     = date(2011, 4, 1);
        final DateOnly september = date(2011, 9, 30);

        final Range<DateOnly> range = closed(april, september);
        assertThat(range.contains(date(2011, 4, 1))).isTrue();
        assertThat(range.isEmpty()).isFalse();
        assertThat(range.enumerate(d -> d.addMonths(1))).containsExactly(april,
            date(2011, 5, 1),
            date(2011, 6, 1),
            date(2011, 7, 1),
            date(2011, 8, 1),
            date(2011, 9, 1));

        final DateOnly july     = date(2011, 7, 1);
        final DateOnly december = date(2011, 12, 1);

        final Range<DateOnly> other = open(july, december);

        assertThat(range.isConnected((other))).isTrue();
        assertThat(range.intersection(other)).isEqualTo(openClosed(july, september));
        assertThat(range.encloses(closed(april, july))).isTrue();
        assertThat(range.span((other))).isEqualTo(closedOpen(april, december));
    }

    @Test public void emptyClosedOpen() {
        final Range<Integer> range = closedOpen(4, 4);

        assertThat(range.contains(3)).isFalse();
        assertThat(range.contains(4)).isFalse();
        assertThat(range.contains(5)).isFalse();
        assertThat(range.isEmpty()).isTrue();
        assertThat(range.toString()).isEqualTo("[4..4)");
        assertThat(range.enumerate(this::inc)).isEmpty();
    }

    @Test public void emptyOpenClosed() {
        final Range<Integer> range = openClosed(4, 4);

        assertThat(range.contains(3)).isFalse();
        assertThat(range.contains(4)).isFalse();
        assertThat(range.contains(5)).isFalse();
        assertThat(range.isEmpty()).isTrue();
        assertThat(range.toString()).isEqualTo("(4..4]");
        assertThat(range.enumerate(this::inc)).isEmpty();
    }

    @Test public void encloseAllTest() {
        assertThat(encloseAll(listOf(0))).isEqualTo(closed(0, 0));
        assertThat(encloseAll(listOf(5, -3))).isEqualTo(closed(-3, 5));
        assertThat(encloseAll(listOf(1, 2, 2, 2, 5, -3, 0, -1))).isEqualTo(closed(-3, 5));

        try {
            final Seq<Integer> values = emptyIterable();
            encloseAll(values);
            failBecauseExceptionWasNotThrown(NoSuchElementException.class);
        }
        catch (final NoSuchElementException e) {
            assertThat(e).hasMessage(Immutables.EMPTY_ITERATOR_MSG);
        }

        try {
            encloseAll(listOf(1, 2, null, 4));
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        }
        catch (final NullPointerException e) {
            assertThat(e).hasMessage("Null enclosing value!");
        }
    }

    @Test public void enclosesClosedTest() {
        final Range<Integer> range = closed(2, 5);
        assertThat(range.encloses(range)).isTrue();
        assertThat(range.encloses(open(2, 5))).isTrue();
        assertThat(range.encloses(openClosed(2, 5))).isTrue();
        assertThat(range.encloses(closedOpen(2, 5))).isTrue();
        assertThat(range.encloses(closed(3, 5))).isTrue();
        assertThat(range.encloses(closed(2, 4))).isTrue();

        assertThat(range.encloses(open(1, 6))).isFalse();
        assertThat(range.encloses(greaterThan(3))).isFalse();
        assertThat(range.encloses(lessThan(3))).isFalse();
        assertThat(range.encloses(Range.atLeast(3))).isFalse();
        assertThat(range.encloses(Range.atMost(3))).isFalse();
        assertThat(range.encloses(Range.all())).isFalse();
    }

    @Test public void enclosesOpen() {
        final Range<Integer> range = open(2, 5);
        assertThat(range.encloses(range)).isTrue();
        assertThat(range.encloses(open(2, 4))).isTrue();
        assertThat(range.encloses(open(3, 5))).isTrue();
        assertThat(range.encloses(closed(3, 4))).isTrue();

        assertThat(range.encloses(openClosed(2, 5))).isFalse();
        assertThat(range.encloses(closedOpen(2, 5))).isFalse();
        assertThat(range.encloses(closed(1, 4))).isFalse();
        assertThat(range.encloses(closed(3, 6))).isFalse();
        assertThat(range.encloses(greaterThan(3))).isFalse();
        assertThat(range.encloses(lessThan(3))).isFalse();
        assertThat(range.encloses(Range.atLeast(3))).isFalse();
        assertThat(range.encloses(Range.atMost(3))).isFalse();
        assertThat(range.encloses(Range.all())).isFalse();
    }

    @Test public void greaterThanTest() {
        final Range<Integer> range = greaterThan(5);

        assertThat(range.contains(Integer.MIN_VALUE)).isFalse();
        assertThat(range.contains(5)).isFalse();
        assertThat(range.contains(6)).isTrue();
        assertThat(range.contains(Integer.MAX_VALUE)).isTrue();
        assertThat(range.isEmpty()).isFalse();
        assertThat(range.toString()).isEqualTo("(5..+\u221e)");
    }

    @Test public void intersectionDeFactoEmpty() {
        final Range<Integer> range = open(3, 4);
        assertThat(range.intersection(range)).isEqualTo(range);

        assertThat(openClosed(3, 3)).isEqualTo(range.intersection(Range.atMost(3)));
        assertThat(closedOpen(4, 4)).isEqualTo(range.intersection(Range.atLeast(4)));

        try {
            range.intersection(lessThan(3));
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Invalid range: (3..3)");
        }

        try {
            range.intersection(greaterThan(4));
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Invalid range: (4..4)");
        }

        assertThat(openClosed(4, 4)).isEqualTo(closed(3, 4).intersection(greaterThan(4)));
    }

    @Test public void intersectionEmpty() {
        final Range<Integer> range = closedOpen(3, 3);
        assertThat(range.intersection(range)).isEqualTo(range);

        try {
            range.intersection(open(3, 5));
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Invalid range: (3..3)");
        }

        try {
            range.intersection(closed(0, 2));
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Invalid range: [3..2]");
        }
    }

    @Test public void intersectionSingleton() {
        final Range<Integer> range = closed(3, 3);
        assertThat(range.intersection(range)).isEqualTo(range);

        assertThat(range.intersection(Range.atMost(4))).isEqualTo(range);
        assertThat(range.intersection(Range.atMost(3))).isEqualTo(range);
        assertThat(range.intersection(Range.atLeast(3))).isEqualTo(range);
        assertThat(range.intersection(Range.atLeast(2))).isEqualTo(range);

        assertThat(range.intersection(lessThan(3))).isEqualTo(closedOpen(3, 3));
        assertThat(range.intersection(greaterThan(3))).isEqualTo(openClosed(3, 3));

        try {
            range.intersection(Range.atLeast(4));
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Invalid range: [4..3]");
        }

        try {
            range.intersection(Range.atMost(2));
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Invalid range: [3..2]");
        }
    }

    @Test public void intersectionTest() {
        final Range<Integer> range = closed(4, 8);

        // adjacent below;
        assertThat(range.intersection(closedOpen(2, 4))).isEqualTo(closedOpen(4, 4));

        // overlap below;
        assertThat(range.intersection(closed(2, 6))).isEqualTo(closed(4, 6));

        // enclosed with same start;
        assertThat(range.intersection(closed(4, 6))).isEqualTo(closed(4, 6));

        // enclosed, interior;
        assertThat(range.intersection(closed(5, 7))).isEqualTo(closed(5, 7));

        // enclosed with same end;
        assertThat(range.intersection(closed(6, 8))).isEqualTo(closed(6, 8));

        // equal;
        assertThat(range.intersection(range)).isEqualTo(range);

        // enclosing with same start;
        assertThat(range.intersection(closed(4, 10))).isEqualTo(range);

        // enclosing with same end;
        assertThat(range.intersection(closed(2, 8))).isEqualTo(range);

        // enclosing, exterior;
        assertThat(range.intersection(closed(2, 10))).isEqualTo(range);

        // overlap above;
        assertThat(range.intersection(closed(6, 10))).isEqualTo(closed(6, 8));

        // adjacent above;
        assertThat(range.intersection(openClosed(8, 10))).isEqualTo(openClosed(8, 8));

        // separate below;
        try {
            range.intersection(closed(0, 2));
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Invalid range: [4..2]");
        }

        // separate above;
        try {
            range.intersection(closed(10, 12));
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Invalid range: [10..8]");
        }
    }  // end method intersectionTest

    @Test public void lessThanTest() {
        final Range<Integer> range = lessThan(5);

        assertThat(range.contains(Integer.MIN_VALUE)).isTrue();
        assertThat(range.contains(4)).isTrue();
        assertThat(range.contains(5)).isFalse();
        assertThat(range.contains(Integer.MAX_VALUE)).isFalse();
        assertThat(range.isEmpty()).isFalse();
        assertThat(range.toString()).isEqualTo("(-\u221e..5)");
    }

    @Test public void openClosedTest() {
        final Range<Integer> range = openClosed(4, 7);

        assertThat(range.contains(4)).isFalse();
        assertThat(range.contains(5)).isTrue();
        assertThat(range.contains(7)).isTrue();
        assertThat(range.contains(8)).isFalse();
        assertThat(range.isEmpty()).isFalse();
        assertThat(range.toString()).isEqualTo("(4..7]");
    }

    @Test public void openTest() {
        final Range<Integer> range = open(4, 8);

        assertThat(range.contains(4)).isFalse();
        assertThat(range.contains(5)).isTrue();
        assertThat(range.contains(7)).isTrue();
        assertThat(range.contains(8)).isFalse();
        assertThat(range.isEmpty()).isFalse();
        assertThat(range.toString()).isEqualTo("(4..8)");

        assertThat(range.toString(value -> value == 4 ? "Four" : value + "")).isEqualTo("(Four..8)");

        try {
            open(4, 3);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Invalid range: (4..3)");
        }
        try {
            open(3, 3);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Invalid range: (3..3)");
        }
    }

    @Test public void singletonTest() {
        final Range<Integer> range = singleton(4);

        assertThat(range.contains(3)).isFalse();
        assertThat(range.contains(4)).isTrue();
        assertThat(range.contains(5)).isFalse();
        assertThat(range.isEmpty()).isFalse();
        assertThat(range.toString()).isEqualTo("[4..4]");
    }

    @Test public void spanTest() {
        final Range<Integer> range = closed(4, 8);

        // separate below;
        assertThat(range.span(closed(0, 2))).isEqualTo(closed(0, 8));
        assertThat(range.span(Range.atMost(2))).isEqualTo(Range.atMost(8));

        // adjacent below;
        assertThat(range.span(closedOpen(2, 4))).isEqualTo(closed(2, 8));
        assertThat(range.span(lessThan(4))).isEqualTo(Range.atMost(8));

        // overlap below;
        assertThat(range.span(closed(2, 6))).isEqualTo(closed(2, 8));
        assertThat(range.span(Range.atMost(6))).isEqualTo(Range.atMost(8));

        // enclosed with same start;
        assertThat(range.span(closed(4, 6))).isEqualTo(range);

        // enclosed, interior;
        assertThat(range.span(closed(5, 7))).isEqualTo(range);

        // enclosed with same end;
        assertThat(range.span(closed(6, 8))).isEqualTo(range);

        // equal;
        assertThat(range.span(range)).isEqualTo(range);

        // enclosing with same start;
        assertThat(range.span(closed(4, 10))).isEqualTo(closed(4, 10));
        assertThat(range.span(Range.atLeast(4))).isEqualTo(Range.atLeast(4));

        // enclosing with same end;
        assertThat(range.span(closed(2, 8))).isEqualTo(closed(2, 8));
        assertThat(range.span(Range.atMost(8))).isEqualTo(Range.atMost(8));

        // enclosing, exterior;
        assertThat(range.span(closed(2, 10))).isEqualTo(closed(2, 10));
        assertThat(range.span(Range.all())).isEqualTo(Range.all());

        // overlap above;
        assertThat(range.span(closed(6, 10))).isEqualTo(closed(4, 10));
        assertThat(range.span(Range.atLeast(6))).isEqualTo(Range.atLeast(4));

        // adjacent above;
        assertThat(range.span(openClosed(8, 10))).isEqualTo(closed(4, 10));
        assertThat(range.span(greaterThan(8))).isEqualTo(Range.atLeast(4));

        // separate above;
        assertThat(range.span(closed(10, 12))).isEqualTo(closed(4, 12));
        assertThat(range.span(Range.atLeast(10))).isEqualTo(Range.atLeast(4));
    }  // end method spanTest

    @Test public void isConnectedTest() {
        assertThat(closed(3, 5).isConnected(open(5, 6))).isTrue();
        assertThat(closed(3, 5).isConnected(openClosed(5, 5))).isTrue();
        assertThat(open(3, 5).isConnected(closed(5, 6))).isTrue();
        assertThat(closed(3, 7).isConnected(open(6, 8))).isTrue();
        assertThat(open(3, 7).isConnected(closed(5, 6))).isTrue();
        assertThat(closed(3, 5).isConnected(closed(7, 8))).isFalse();
        assertThat(closed(3, 5).isConnected(closedOpen(7, 7))).isFalse();
    }

    private int inc(Integer n) {
        return n + 1;
    }
}  // end class RangeTest
