
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.assertj.core.api.Fail;
import org.junit.Test;

import tekgenesis.common.collections.Range;
import tekgenesis.common.collections.RangeMap;
import tekgenesis.common.collections.TreeRangeMap;
import tekgenesis.common.core.DateOnly;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import static tekgenesis.common.collections.Range.*;
import static tekgenesis.common.collections.TreeRangeMap.create;

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class RangeTreeMapTest {

    //~ Methods ......................................................................................................................................

    @Test public void putAllThreeRanges() {
        for (final Range<Integer> range1 : ranges) {
            for (final Range<Integer> range2 : ranges) {
                for (final Range<Integer> range3 : ranges) {
                    final TreeRangeMap<Integer, Integer> extra = create();
                    extra.put(range2, 2);
                    extra.put(range3, 3);

                    final TreeRangeMap<Integer, Integer> map = create();
                    map.put(range1, 1);
                    map.putAll(extra);

                    final Map<Integer, Integer> model = new HashMap<>();
                    addValuesToModel(model, range1, 1);
                    addValuesToModel(model, range2, 2);
                    addValuesToModel(model, range3, 3);

                    verify(model, map);
                }
            }
        }
    }

    @Test public void putAndRemove() {
        for (final Range<Integer> rangeToPut : ranges) {
            for (final Range<Integer> rangeToRemove : ranges) {
                final TreeRangeMap<Integer, Integer> map = create();
                map.put(rangeToPut, 1);
                map.remove(rangeToRemove);

                final Map<Integer, Integer> model = new HashMap<>();
                addValuesToModel(model, rangeToPut, 1);
                removeValuesFromModel(model, rangeToRemove);

                verify(model, map);
            }
        }
    }

    @Test public void putGetSingleRange() {
        for (final Range<Integer> range : ranges) {
            final TreeRangeMap<Integer, Integer> map = create();
            map.put(range, 1);

            final Map<Integer, Integer> model = new HashMap<>();
            addValuesToModel(model, range, 1);

            verify(model, map);
        }
    }

    @Test public void putGetTwoRanges() {
        for (final Range<Integer> range1 : ranges) {
            for (final Range<Integer> range2 : ranges) {
                final TreeRangeMap<Integer, Integer> map = create();
                map.put(range1, 1);
                map.put(range2, 2);

                final Map<Integer, Integer> model = new HashMap<>();
                addValuesToModel(model, range1, 1);
                addValuesToModel(model, range2, 2);

                verify(model, map);
            }
        }
    }

    @Test public void spanSingleRange() {
        for (final Range<Integer> range : ranges) {
            final TreeRangeMap<Integer, Integer> map = create();
            map.put(range, 1);

            if (range.isEmpty()) {
                try {
                    map.span();
                    failBecauseExceptionWasNotThrown(NoSuchElementException.class);
                }
                catch (final NoSuchElementException ignore) {}
            }
            else assertThat(map.span()).isEqualTo(range);
        }
    }

    @Test public void spanTwoRanges() {
        for (final Range<Integer> range1 : ranges) {
            for (final Range<Integer> range2 : ranges) {
                final TreeRangeMap<Integer, Integer> map = create();
                map.put(range1, 1);
                map.put(range2, 2);

                final Range<Integer> expected = range1.isEmpty() ? (range2.isEmpty() ? null : range2)
                                                                 : range2.isEmpty() ? range1 : range1.span(range2);

                if (expected != null) assertThat(map.span()).isEqualTo(expected);
                else {
                    try {
                        map.span();
                        failBecauseExceptionWasNotThrown(NoSuchElementException.class);
                    }
                    catch (final NoSuchElementException ignore) {}
                }
            }
        }
    }

    @SuppressWarnings("OverlyLongMethod")
    @Test public void subRangeMap() {
        final TreeRangeMap<DateOnly, String> map = create();

        final DateOnly first = DateOnly.date(2013, 1, 1);
        final DateOnly apr   = DateOnly.date(2013, 4, 1);
        final DateOnly jun   = DateOnly.date(2013, 6, 1);
        final DateOnly jul   = DateOnly.date(2013, 7, 1);
        final DateOnly oct   = DateOnly.date(2013, 10, 1);
        final DateOnly last  = DateOnly.date(2013, 12, 31);

        final Range<DateOnly> q1 = closedOpen(first, apr);
        final Range<DateOnly> q2 = closedOpen(apr, jul);
        final Range<DateOnly> q3 = closedOpen(jul, oct);
        final Range<DateOnly> q4 = closed(oct, last);

        map.put(q1, "q1");
        map.put(q2, "q2");
        map.put(q3, "q3");
        map.put(q4, "q4");

        final RangeMap<DateOnly, String> sub = map.subRangeMap(closed(first, last));

        assertThat(sub.get(first)).isEqualTo("q1");
        assertThat(sub.get(apr)).isEqualTo("q2");
        assertThat(sub.get(jun)).isEqualTo("q2");
        assertThat(sub.get(jul)).isEqualTo("q3");
        assertThat(sub.get(oct)).isEqualTo("q4");
        assertThat(sub.get(last)).isEqualTo("q4");

        // Remove June;
        sub.remove(closedOpen(jun, jul));

        assertThat(sub.get(jun)).isEqualTo(null);
        assertThat(map.get(jun)).isEqualTo(null);
        assertThat(sub.get(jul)).isEqualTo("q3");
        assertThat(map.get(jul)).isEqualTo("q3");

        final RangeMap<DateOnly, String> firstSemester = map.subRangeMap(closedOpen(first, jul));

        assertThat(firstSemester.get(first)).isEqualTo("q1");
        assertThat(firstSemester.get(apr)).isEqualTo("q2");
        assertThat(firstSemester.get(jun)).isEqualTo(null);
        assertThat(firstSemester.get(jul)).isEqualTo(null);
        assertThat(firstSemester.get(oct)).isEqualTo(null);

        final RangeMap<DateOnly, String> secondSemester = map.subRangeMap(closed(jul, last));

        assertThat(secondSemester.get(apr)).isEqualTo(null);
        assertThat(secondSemester.get(jun)).isEqualTo(null);
        assertThat(secondSemester.get(jul)).isEqualTo("q3");
        assertThat(secondSemester.get(oct)).isEqualTo("q4");
        assertThat(secondSemester.get(last)).isEqualTo("q4");

        secondSemester.put(closed(DateOnly.date(2013, 9, 21), DateOnly.date(2013, 12, 21)), "spring");

        assertThat(secondSemester.get(jul)).isEqualTo("q3");
        assertThat(secondSemester.get(oct)).isEqualTo("spring");
        assertThat(secondSemester.get(last)).isEqualTo("q4");

        assertThat(map.asMapOfRanges().toString()).isEqualTo(
            "{" +
            "[2013-01-01..2013-04-01)=q1, " +
            "[2013-04-01..2013-06-01)=q2, " +
            "[2013-07-01..2013-09-21)=q3, " +
            "[2013-09-21..2013-12-21]=spring, " +
            "(2013-12-21..2013-12-31]=q4" +
            "}");

        try {
            secondSemester.asMapOfRanges();
            Fail.failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException ignore) {}
    }  // end method subRangeMap

    private void addValuesToModel(Map<Integer, Integer> model, Range<Integer> range, int value) {
        for (int i = -2; i < 2; i++) {
            if (range.contains(i)) model.put(i, value);
        }
    }
    private void removeValuesFromModel(Map<Integer, Integer> model, Range<Integer> range) {
        for (int i = -2; i < 2; i++) {
            if (range.contains(i)) model.remove(i);
        }
    }
    private void verify(Map<Integer, Integer> model, TreeRangeMap<Integer, Integer> test) {
        for (int i = -2; i < 2; i++) {
            final Integer actual = model.get(i);
            assertThat(actual).isEqualTo(test.get(i));

            final Map<Range<Integer>, Integer> mapOfRanges = test.asMapOfRanges();

            if (actual != null) assertThat(mapOfRanges.values().contains(actual)).isTrue();

            for (final Range<Integer> range : mapOfRanges.keySet())
                assertThat(range.isEmpty()).isFalse();
        }
    }

    //~ Static Fields ................................................................................................................................

    private static final ArrayList<Range<Integer>> ranges = new ArrayList<>();

    static {
        final Range<Integer> all = Range.all();
        ranges.add(all);

        for (int i = -1; i < 1; i++) {
            ranges.add(lessThan(i));
            ranges.add(atMost(i));
            ranges.add(greaterThan(i));
            ranges.add(atLeast(i));
        }

        for (int i = -1; i < 1; i++) {
            for (int j = i; j < 1; j++) {
                if (i != j) {
                    ranges.add(closed(i, j));
                    ranges.add(closedOpen(i, j));
                    ranges.add(openClosed(i, j));
                    ranges.add(open(i, j));
                }
            }
        }
    }
}  // end class RangeTreeMapTest
