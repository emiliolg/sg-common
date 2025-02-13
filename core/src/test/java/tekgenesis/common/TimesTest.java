
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.time.DayOfWeek;
import java.util.*;
import java.util.function.LongSupplier;

import org.junit.Before;
import org.junit.Test;

import tekgenesis.common.collections.Maps;
import tekgenesis.common.core.DateOnly;
import tekgenesis.common.core.DateTime;
import tekgenesis.common.core.Times;
import tekgenesis.common.core.Tuple5;
import tekgenesis.common.util.CalendarUtils;

import static java.util.Arrays.asList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;

import static tekgenesis.common.collections.Colls.listOf;
import static tekgenesis.common.core.DateOnly.date;
import static tekgenesis.common.core.Times.millisToDate;
import static tekgenesis.common.core.Tuple.tuple;
import static tekgenesis.common.util.CalendarUtils.makeDate;

/**
 * User: emilio; Date: 12/16/11; Time: 12:08 PM;
 */

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber", "NonJREEmulationClassesInClientCode" })
public class TimesTest {

    //~ Methods ......................................................................................................................................

    @Before public void assignTimeZone() {
        final TimeZone timeZone = TimeZone.getTimeZone("America/Argentina/Buenos_Aires");  // TimeZone.getTimeZone("GMT-3:00");
        CalendarUtils.setSessionTimeZone(timeZone);
        TimeZone.setDefault(timeZone);
    }

    @Test public void betweenTest() {
        final DateOnly d0 = DateOnly.fromString("19590401");
        final DateOnly d1 = DateOnly.fromString("19590402");
        final DateOnly d2 = DateOnly.fromString("19590403");
        assertThat(d1.between(d0, d2)).isTrue();
        assertThat(d1.between(d1, d1)).isTrue();
        assertThat(d0.between(d1, d2)).isFalse();

        final DateTime t0 = DateTime.fromString("19590401");
        final DateTime t1 = DateTime.fromString("19590402");
        final DateTime t2 = DateTime.fromString("19590403");
        assertThat(t1.between(t0, t2)).isTrue();
        assertThat(t1.between(t1, t1)).isTrue();
        assertThat(t0.between(t1, t2)).isFalse();
    }

    public void checkToJavaAndBack(DateOnly d1) {
        final Date     date1 = new Date(d1.toDate().getTime());
        final DateOnly d2    = DateOnly.fromDate(date1);
        assertThat(d2).isEqualTo(d1);

        final Date     date = millisToDate(d1.toMilliseconds(), true);
        final DateOnly d3   = DateOnly.fromDate(date);
        assertThat(d3).isEqualTo(d1);
    }

    @Test public void compareTest() {
        final DateOnly d0 = DateOnly.fromString("19590401");
        final DateOnly d1 = DateOnly.fromString("19590402");
        final DateOnly d2 = DateOnly.fromString("19590403");
        assertThat(d1.isGreaterOrEqualTo(d0)).isTrue();
        assertThat(d1.isLessThan(d2)).isTrue();
        assertThat(d0.isGreaterOrEqualTo(d0)).isTrue();

        final DateTime t0 = DateTime.fromString("19590401");
        final DateTime t1 = DateTime.fromString("19590402");
        final DateTime t2 = DateTime.fromString("19590403");
        assertThat(t1.isGreaterOrEqualTo(t0)).isTrue();
        assertThat(t1.isLessThan(t2)).isTrue();
        assertThat(t0.isGreaterOrEqualTo(t0)).isTrue();
        assertThat(t0.isLessOrEqualTo(t0)).isTrue();
        assertThat(t2.isLessOrEqualTo(t0)).isFalse();
    }

    @SuppressWarnings("MagicConstant")
    @Test public void currentDate() {
        final List<Tuple5<Integer, Integer, Integer, Integer, String>> cases = asList(tuple(2000, 1, 1, 22, "GMT-3:00"),
                tuple(2000, 1, 1, 22, "GMT"),
                tuple(2000, 1, 1, 1, "GMT-3:00"),
                tuple(2000, 1, 1, 1, "GMT"),
                tuple(1999, 12, 31, 22, "GMT-3:00"),
                tuple(1999, 12, 31, 22, "GMT"),
                tuple(1999, 12, 31, 0, "GMT-3:00"),
                tuple(1999, 12, 31, 0, "GMT"));

        final TimeZone oldTz = TimeZone.getDefault();
        for (final Tuple5<Integer, Integer, Integer, Integer, String> c : cases) {
            final int    year  = c.first();
            final int    month = c.second();
            final int    day   = c.third();
            final int    hour  = c.fourth();
            final String tz    = c.fifth();

            final GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone(tz));
            calendar.set(year, month, day, hour, 0, 0);
            calendar.getTimeInMillis();
            final LongSupplier oldProvider = DateTime.setTimeSupplier(createTimeProvider(year, month, day, hour, tz));

            TimeZone.setDefault(TimeZone.getTimeZone(tz));
            final DateTime currentTime = DateTime.current();
            final DateOnly currentDate = DateOnly.current();
            DateTime.setTimeSupplier(oldProvider);

            assertThat(currentDate.getDay()).isEqualTo(day);
            assertThat(currentDate.getMonth()).isEqualTo(month);
            assertThat(currentDate.getYear()).isEqualTo(year);

            assertThat(DateOnly.fromDate(currentTime.toDate())).isEqualTo(currentDate);
            assertThat(currentTime.toDateOnly()).isEqualTo(currentDate);
        }
        TimeZone.setDefault(oldTz);
    }

    @SuppressWarnings("OverlyLongMethod")
    @Test public void dateOnly() {
        final Date d = javaDate(77, 9, 27);

        final DateOnly dOnly  = DateOnly.fromDate(d);
        final DateOnly dOnly2 = DateOnly.fromMilliseconds(d.getTime());

        assertThat(dOnly.toString()).isEqualTo(dOnly2.toString());

        assertThat(dOnly).isEqualTo(dOnly2);

        final DateOnly d1 = DateOnly.fromString("19590402");
        assertThat(d1.toString()).isEqualTo("1959-04-02");
        assertThat(d1.getYear()).isEqualTo(1959);
        assertThat(d1.getMonth()).isEqualTo(4);
        assertThat(d1.getDay()).isEqualTo(2);
        assertThat(d1.getDayOfWeek()).isEqualTo(DayOfWeek.THURSDAY);
        assertThat(d1).isEqualTo(date(1959, 4, 2));
        assertThat(DateOnly.fromMilliseconds(d1.toMilliseconds())).isEqualTo(d1);

        assertThat(d1.toDate().toString()).startsWith("Thu Apr 02").endsWith("1959");
        final Date makeDate1 = javaDate(59, 4, 2);
        assertThat(DateOnly.fromDate(makeDate1)).isEqualTo(d1);

        final DateOnly d2 = d1.withYear(1950);
        assertThat(d1.monthsFrom(d2)).isEqualTo(108);
        assertThat(d2.toString()).isEqualTo("1950-04-02");
        assertThat(d2.format("dd/MM/yy")).isEqualTo("02/04/50");

        final DateOnly d3 = d1.addDays(60);
        assertThat(d3.toString()).isEqualTo("1959-06-01");
        assertThat(d3.compareTo(d1)).isEqualTo(1);
        assertThat(d1.compareTo(d3)).isEqualTo(-1);
        assertThat(d3.weeksFrom(d1)).isEqualTo(8);
        assertThat(d3.monthsFrom(d1)).isEqualTo(1);
        assertThat(d3.yearsFrom(d1)).isZero();
        assertThat(d1.withMonth(6).withDay(1)).isEqualTo(d3);

        final DateOnly d4 = d1.withYear(1960);
        assertThat(d4.toString()).isEqualTo("1960-04-02");
        assertThat(d4.daysFrom(d1)).isEqualTo(366);
        assertThat(d4.weeksFrom(d1)).isEqualTo(52);
        assertThat(d4.monthsFrom(d1)).isEqualTo(12);
        assertThat(d4.yearsFrom(d1)).isEqualTo(1);

        assertThat(d1.daysFrom(d4)).isEqualTo(-366);
        assertThat(d1.weeksFrom(d4)).isEqualTo(-52);
        assertThat(d1.monthsFrom(d4)).isEqualTo(-12);
        assertThat(d1.yearsFrom(d4)).isEqualTo(-1);

        assertThat(d1.addYears(1)).isEqualTo(d4);
        assertThat(d1.addMonths(12)).isEqualTo(d4);
        assertThat(d1.addDays(366)).isEqualTo(d4);

        assertThat(d1.addYears(1).compareTo(d4)).isZero();

        final DateOnly d5 = d4.withDay(1);
        assertThat(d5.daysFrom(d1)).isEqualTo(365);
        assertThat(d5.weeksFrom(d1)).isEqualTo(52);
        assertThat(d5.monthsFrom(d1)).isEqualTo(11);
        assertThat(d5.yearsFrom(d1)).isZero();

        assertThat(DateOnly.fromDate(null).toMilliseconds()).isZero();
    }  // end method dateOnly

    @SuppressWarnings("OverlyLongMethod")
    @Test public void dateTime() {
        final DateTime d1 = DateTime.fromString("19590402");

        assertThat(d1).isNotEqualTo("a");
        assertThat(d1.hashCode()).isEqualTo(Long.valueOf(d1.toMilliseconds()).hashCode());

        assertThat(d1.toString()).isEqualTo("1959-04-02T03:00:00Z");
        assertThat(d1.getYear()).isEqualTo(1959);
        assertThat(d1.getMonth()).isEqualTo(4);
        assertThat(d1.getDay()).isEqualTo(2);
        assertThat(d1.getDayOfWeek()).isEqualTo(DayOfWeek.THURSDAY);
        assertThat(d1).isEqualTo(DateTime.dateTime(1959, 4, 2));

        assertThat(DateTime.fromMilliseconds(d1.toMilliseconds())).isEqualTo(d1);

        assertThat(d1.toDate().toString()).startsWith("Thu Apr 02").endsWith("1959");
        assertThat(DateTime.fromDate(javaDate(59, 4, 2))).isEqualTo(d1);

        final DateTime d2 = d1.withYear(1950);
        assertThat(d1.monthsFrom(d2)).isEqualTo(108);
        assertThat(d2.toString()).isEqualTo("1950-04-02T03:00:00Z");
        assertThat(d1).isNotEqualTo(d2);

        final DateTime d3 = d1.addDays(60);
        assertThat(d3.toString()).isEqualTo("1959-06-01T03:00:00Z");
        assertThat(d3.compareTo(d1)).isEqualTo(1);
        assertThat(d3.daysFrom(d1)).isEqualTo(60);
        assertThat(d3.weeksFrom(d1)).isEqualTo(8);
        assertThat(d3.monthsFrom(d1)).isEqualTo(1);
        assertThat(d3.yearsFrom(d1)).isZero();

        assertThat(d1.addWeeks(8).addDays(4)).isEqualTo(d3);

        final DateTime d4 = d1.withYear(1960);
        assertThat(d4.toString()).isEqualTo("1960-04-02T03:00:00Z");
        assertThat(d4.daysFrom(d1)).isEqualTo(366);
        assertThat(d4.weeksFrom(d1)).isEqualTo(52);
        assertThat(d4.monthsFrom(d1)).isEqualTo(12);
        assertThat(d4.yearsFrom(d1)).isEqualTo(1);

        assertThat(d1.daysFrom(d4)).isEqualTo(-366);
        assertThat(d1.weeksFrom(d4)).isEqualTo(-52);
        assertThat(d1.monthsFrom(d4)).isEqualTo(-12);
        assertThat(d1.yearsFrom(d4)).isEqualTo(-1);

        assertThat(d1.addYears(1)).isEqualTo(d4);
        assertThat(d1.addMonths(12)).isEqualTo(d4);
        assertThat(d1.addDays(366)).isEqualTo(d4);

        final DateTime d5 = d1.withHours(18);
        assertThat(d5.toString()).isEqualTo("1959-04-02T21:00:00Z");

        assertThat(d1.addHours(18)).isEqualTo(d5);

        assertThat(DateTime.fromDate(null).toMilliseconds()).isZero();

        final DateTime d6 = DateTime.fromMilliseconds(d1.toMilliseconds() + 123456);
        assertThat(d6.toString()).isEqualTo("1959-04-02T03:02:03.456Z");

        assertThat(d6.getHours()).isZero();
        assertThat(d6.getMinutes()).isEqualTo(2);
        assertThat(d6.getSeconds()).isEqualTo(3);
        assertThat(d6.getFractionalSeconds()).isEqualTo(3.456);

        assertThat(d6.hoursFrom(d1)).isZero();
        assertThat(d6.minutesFrom(d1)).isEqualTo(2);
        assertThat(d6.secondsFrom(d1)).isEqualTo(123);

        final DateTime d7 = d6.withSeconds(50).withMinutes(50).withMilliseconds(50);
        assertThat(d7.toString()).isEqualTo("1959-04-02T03:50:50.050Z");

        final TimeZone tz = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-1"));
        assertThat(d7.format("dd/MM/yyyy hh:mm")).isEqualTo("02/04/1959 02:50");
        TimeZone.setDefault(tz);
    }  // end method dateTime

    @Test public void dateToJavaDateAndBack() {
        listOf(date(2016, 10, 6), date(2008, 10, 19), date(1963, 10, 1), date(1967, 4, 2)).forEach(this::checkToJavaAndBack);
    }

    @Test public void parseIllegalDates() {
        for (final String date : asList("193", "1")) {
            try {
                Times.parseDateTime(date);
                failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
            }
            catch (final IllegalArgumentException e) {
                assertThat(e).hasMessage(date);
            }
        }
    }

    @Test public void parseValidDates() {
        final Map<String, String> values = Maps.hashMap(tuple("   ", EPOCH),
                tuple("19Z", "1900-01-01T00:00:00Z"),
                tuple("  19590402Z", "1959-04-02T00:00:00Z"),
                tuple("19590402Z", "1959-04-02T00:00:00Z"),
                tuple("+19590402Z", "1959-04-02T00:00:00Z"),
                tuple("1959-04-02Z", "1959-04-02T00:00:00Z"),
                tuple("1959 04 02Z", "1959-04-02T00:00:00Z"),
                tuple("1959-4-2Z", "1959-04-02T00:00:00Z"),
                tuple("1959-04Z", "1959-04-01T00:00:00Z"),
                tuple("001959-04-02Z", "1959-04-02T00:00:00Z"),
                tuple("1959-092Z", "1959-04-02T00:00:00Z"),
                tuple("1959-W14-4Z", "1959-04-02T00:00:00Z"),
                tuple("1959-W144Z", "1959-04-02T00:00:00Z"),
                tuple("1959-04-02T00:15", "1959-04-02T03:15:00Z"),
                tuple("1959-04-02T0015", "1959-04-02T03:15:00Z"),
                tuple("1959-04-02T00:15:30", "1959-04-02T03:15:30Z"),
                tuple("1959-04-02T001530", "1959-04-02T03:15:30Z"),
                tuple("1959-04-02T00:15:30.12", "1959-04-02T03:15:30.120Z"),
                tuple("1959-04-02 00:15:30.12", "1959-04-02T03:15:30.120Z"),
                tuple("1959-04-02T00:15:30,1", "1959-04-02T03:15:30.100Z"),
                tuple("1959-04-02T00:15:30.10", "1959-04-02T03:15:30.100Z"),
                tuple("1959-04-02T00:15:30.121", "1959-04-02T03:15:30.121Z"),
                tuple("1959-04-02T001530.12", "1959-04-02T03:15:30.120Z"),
                tuple("1959-04-02T0315Z", "1959-04-02T03:15:00Z"),
                tuple("1959-04-02T00:15:30-0300", "1959-04-02T03:15:30Z"),
                tuple("1959-04-02T00:15:30-3", "1959-04-02T03:15:30Z"),
                tuple("1959-04-02T06:15:30+3", "1959-04-02T03:15:30Z"),
                tuple("1959-04-02T00:15:30-3:00", "1959-04-02T03:15:30Z"),
                tuple("-33-01-01Z", "-0033-01-01T00:00:00Z"),
                tuple("-33-W144Z", "-0033-03-30T00:00:00Z"),
                tuple("21959-04-02T00:15:30-3:00", "21959-04-02T03:15:30Z"));
        for (final Map.Entry<String, String> e : values.entrySet()) {
            final long t = Times.parseDateTime(e.getKey());
            assertThat(Times.isoDateTime(t)).isEqualTo(e.getValue());
        }

        assertThat(Times.isoDateTime(Times.parseDateTime(null))).isEqualTo(EPOCH);
        assertThat(Times.isoDateTime(Times.parseDate("19590402"))).isEqualTo("1959-04-02T00:00:00Z");
        assertThat(Times.isoDateTime(Times.parseDate("19590402T00:15:30-0300"))).isEqualTo("1959-04-02T00:00:00Z");
        assertThat(Times.parseDate(null)).isZero();
        assertThat(Times.parseDate("   ")).isZero();
    }  // end method parseValidDates

    @Test public void toHoursMinSecondsString() {
        final DateTime current      = DateTime.current();
        final DateTime dateTime     = current.addHours(3).addMinutes(5).addSeconds(22);
        final long     diffInMillis = dateTime.toMilliseconds() - current.toMilliseconds();
        assertThat(Times.toHoursMinSecondsString(diffInMillis)).isEqualTo("T03:05:22Z");
    }

    //~ Methods ......................................................................................................................................

    private static LongSupplier createTimeProvider(final int year, final int month, final int day, final int hour, final String tz) {
        return () -> {
                   final GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone(tz));
                   // noinspection MagicConstant
                   calendar.set(year, month - 1, day, hour, 0, 0);
                   return calendar.getTimeInMillis();
               };
    }

    private static Date javaDate(int year, int month, int day) {
        return new Date(makeDate(false, 1900 + year, month, day));
    }

    //~ Static Fields ................................................................................................................................

    private static final String EPOCH = Times.isoDateTime(0);
}  // end class TimesTest
