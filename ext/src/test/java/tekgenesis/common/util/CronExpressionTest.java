
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.junit.Test;

import tekgenesis.common.core.DateTime;
import tekgenesis.common.core.Option;
import tekgenesis.common.core.Tuple;
import tekgenesis.common.core.Tuple3;

import static java.time.DayOfWeek.*;
import static java.util.Arrays.asList;

import static org.assertj.core.api.Assertions.*;

import static tekgenesis.common.core.Tuple.tuple;
import static tekgenesis.common.tools.test.Tests.assertNotNull;
import static tekgenesis.common.util.CronExpression.*;
import static tekgenesis.common.util.CronExpression.FieldType.*;

/**
 * Test Cron Expressions.
 */
@SuppressWarnings({ "DuplicateStringLiteralInspection", "OverlyLongMethod, JavaDoc" })
public class CronExpressionTest {

    //~ Methods ......................................................................................................................................

    @Test public void testDayOfWeek()
        throws CronExpression.Exception
    {
        final List<Tuple3<String, String, List<String>>> tests =  //
                                                                 asList(
                tuple("0 0 0 ? * MON", "2014-04-25", asList("2014-04-28", "2014-05-05", "2014-05-12", "2014-05-19", "2014-05-26", "2014-06-02")),
                tuple("0 0 0 ? * L-5", "2014-04-25", asList("2014-04-28", "2014-05-05", "2014-05-12", "2014-05-19", "2014-05-26", "2014-06-02")),
                tuple("0 0 0 ? * MONL", "2014-04-25", asList("2014-04-28", "2014-05-26", "2014-06-30", "2014-07-28")),
                tuple("0 0 0 ? * MON#2", "2014-04-25", asList("2014-05-12", "2014-06-09", "2014-07-14", "2014-08-11")),
                tuple("0 30 7 ? * MON-FRI",
                    "2014-06-30",
                    asList("2014-06-30", "2014-07-01", "2014-07-02", "2014-07-03", "2014-07-04", "2014-07-07", "2014-07-08")));

        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        testList(tests, format);
    }  // end method testDayOfWeek

    @Test public void testDays()
        throws CronExpression.Exception
    {
        final List<Tuple3<String, String, List<String>>> tests =  //
                                                                 asList(
                tuple("0 0 0 1",
                    "2014-04-25",
                    asList("2014-05-01 00:00:00",
                        "2014-06-01 00:00:00",
                        "2014-07-01 00:00:00",
                        "2014-08-01 00:00:00",
                        "2014-09-01 00:00:00",
                        "2014-10-01 00:00:00",
                        "2014-11-01 00:00:00",
                        "2014-12-01 00:00:00",
                        "2015-01-01 00:00:00")),
                tuple("0 0 0 1W",
                    "2014-04-25",
                    asList("2014-05-01 00:00:00",
                        "2014-06-02 00:00:00",
                        "2014-07-01 00:00:00",
                        "2014-08-01 00:00:00",
                        "2014-09-01 00:00:00",
                        "2014-10-01 00:00:00",
                        "2014-11-03 00:00:00",
                        "2014-12-01 00:00:00",
                        "2015-01-01 00:00:00")),
                tuple("0 30 13 L",
                    "2014-10-25",
                    asList("2014-10-31 13:30:00", "2014-11-30 13:30:00", "2014-12-31 13:30:00", "2015-01-31 13:30:00", "2015-02-28 13:30:00")),
                tuple("0 30 13 L-1",
                    "2014-10-25",
                    asList("2014-10-30 13:30:00", "2014-11-29 13:30:00", "2014-12-30 13:30:00", "2015-01-30 13:30:00", "2015-02-27 13:30:00")),
                tuple("0 30 13 LW",
                    "2014-10-25",
                    asList("2014-10-31 13:30:00", "2014-11-28 13:30:00", "2014-12-31 13:30:00", "2015-01-30 13:30:00", "2015-02-27 13:30:00")),
                tuple("0 30 13 L-1W",
                    "2014-10-25",
                    asList("2014-10-30 13:30:00", "2014-11-28 13:30:00", "2014-12-30 13:30:00", "2015-01-30 13:30:00", "2015-02-27 13:30:00")));

        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        testList(tests, format);
    }  // end method testDays

    @Test public void testFieldParsing() {
        final EnumSet<DayOfWeek> mondayToFriday = EnumSet.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY);
        final EnumSet<DayOfWeek> all            = EnumSet.allOf(DayOfWeek.class);
        final EnumSet<DayOfWeek> friday         = EnumSet.of(FRIDAY);
        final EnumSet<DayOfWeek> fridayToMonday = EnumSet.of(FRIDAY, SATURDAY, SUNDAY, MONDAY);

        for (final FieldType f : FieldType.values())
            assertParsesForField("0 15 10 * * ? 2005", f, all);
        assertParsesForField("58-4 5 21 ? * MON-FRI", SECOND, mondayToFriday);
        assertParsesForField("0 58-4 21 ? * MON-FRI", MINUTE, mondayToFriday);
        assertParsesForField("0 0/5 21-3 ? * MON-FRI", HOUR, mondayToFriday);
        assertParsesForField("58 5 21 ? * 6-2", DAY_OF_WEEK, fridayToMonday);
        assertParsesForField("58 5 21 28-5 1 ?", DAY_OF_MONTH, all);
        assertParsesForField("58 5 21 ? 11-2 FRI", MONTH, friday);
        assertParsesForField("58 5 21 ? NOV-FEB FRI", MONTH, friday);
        assertParsesForField("0 0 14-6 ? * FRI-MON", HOUR, fridayToMonday);
        assertParsesForField("0 0 14-6 ? * FRI-MON", DAY_OF_WEEK, fridayToMonday);
        assertParsesForField("55-3 56-2 6 ? * FRI", SECOND, friday);
        assertParsesForField("55-3 56-2 6 ? * FRI", MINUTE, friday);
    }

    @Test public void testIsSatisfiedBy()
        throws ParseException
    {
        final CronExpression cronExpression = new CronExpression("0 15 10 * * ? 2005");

        final List<Tuple<String, Boolean>> values =  //
                                                    asList(tuple("2006-6-1 10:15:00", false),
                tuple("2005-6-1 10:15:00", true),
                tuple("2005-6-1 10:16:00", false),
                tuple("2005-6-1 10:14:00", false));

        for (final Tuple<String, Boolean> v : values) {
            final String time = v.first();
            assertThat(cronExpression.isSatisfiedBy(DateTime.valueOf(time))).as(time).isEqualTo(v.second());
        }
    }

    @Test public void testLastDayOffset()
        throws CronExpression.Exception
    {
        final List<Tuple3<String, String, Boolean>> values =  //
                                                             asList(tuple("0 15 10 L-1W * ? 2010", "2010-10-29 10:15:00", true),
                tuple("0 15 10 L-2 * ? 2010", "2010-10-29 10:15:00", true),
                tuple("0 15 10 L-2 * ? 2010", "2010-10-28 10:15:00", false),
                tuple("0 15 10 L-5W * ? 2010", "2010-10-26 10:15:00", true),
                tuple("0 15 10 L-1 * ? 2010", "2010-10-30 10:15:00", true));

        for (final Tuple3<String, String, Boolean> v : values) {
            final CronExpression c    = new CronExpression(v.first());
            final String         time = v.second();
            assertThat(c.isSatisfiedBy(DateTime.valueOf(time))).as(c + ": " + time).isEqualTo(v.third());
        }
    }

    @Test public void testMoreCases()
        throws CronExpression.Exception
    {
        final List<Tuple3<String, String, Boolean>> values =  //

            asList(tuple("0 0 0 1 * ?", "2010-10-1 0:00:00", true),
                tuple("0 0 0/1", "2010-10-30 1:00:00", true),
                tuple("0/5 * * * * ?", "2010-10-30 10:15:00", true),
                tuple("0/5 * * * * ?", "2010-10-30 10:15:01", false),
                tuple("0/5", "2010-10-30 10:15:00", true),
                tuple("0/5", "2010-10-30 10:15:01", false));

        for (final Tuple3<String, String, Boolean> v : values) {
            final CronExpression c    = new CronExpression(v.first());
            final String         time = v.second();
            assertThat(isValidExpression(v.first())).isEqualTo(true);
            assertThat(c.isSatisfiedBy(DateTime.valueOf(time))).as(time).isEqualTo(v.third());
        }
    }

    @Test public void testParsing() {
        assertThat(parse("* * * * Foo ? ")).isSameAs(Option.empty());
        assertThat(parse(null)).isSameAs(Option.empty());

        mustProduceException("* * * * Foo ? ", "Invalid value 'Foo' for field 'MONTH'");
        mustProduceException("* * * * Jan-Foo ? ", "Invalid value 'Foo' for field 'MONTH'");
        mustProduceException("0 0 * 4 * 1", "Cannot specify a value for 'DAY_OF_MONTH' and 'DAY_OF_WEEK'");
        mustProduceException("0 43 9 1,5,29,L * ?", "Cannot specify 'L' and other values for 'DAY_OF_MONTH'");
        mustProduceException("? 43 9 * * *", "Invalid value '?' for field 'SECOND'");
        mustProduceException("0/5 * * 32W 1 ?", "Value '32' out of range for field 'DAY_OF_MONTH'");
        mustProduceException("LW", "Invalid value 'W' for field 'SECOND'");
        mustProduceException("1W", "Invalid value '1W' for field 'SECOND'");
        mustProduceException("4L", "Invalid value '4L' for field 'SECOND'");
        mustProduceException("1#5", "Invalid value '1#5' for field 'SECOND'");
        mustProduceException("Pepe", "Invalid value 'Pepe' for field 'SECOND'");
        mustProduceException("0 0 0 * * Pep", "Invalid value 'Pep' for field 'DAY_OF_WEEK'");
        mustProduceException("0 0 0 * * 1#5,2#4", "Cannot specifying multiple 'nth' days for 'DAY_OF_MONTH'.");

        try {
            final String         str = "*/30 43-45 1-13/3,14 ? NOV-FEB 5L *";
            final CronExpression ce  = new CronExpression(str);
            assertThat(ce.getCronExpression()).isEqualTo(str);
            assertThat(ce.getCronExpression()).isEqualTo(ce.toString());
            assertThat(ce.getTimeZone()).isEqualTo(TimeZone.getDefault());
            assertThat(ce.getField(SECOND).toString()).isEqualTo("[0, 30]");
            assertThat(ce.getField(MINUTE).toString()).isEqualTo("[43, 44, 45]");
            assertThat(ce.getField(HOUR).toString()).isEqualTo("[1, 4, 7, 10, 13, 14]");
            assertThat(ce.getField(DAY_OF_MONTH).toString()).isEqualTo("?");
            assertThat(ce.getField(MONTH).toString()).isEqualTo("[1, 2, 11, 12]");
            assertThat(ce.getField(DAY_OF_WEEK).toString()).isEqualTo("[5]");
            assertThat(ce.getField(YEAR).toString()).isEqualTo("*");
        }
        catch (final CronExpression.Exception pe) {
            fail("Unexpected Exception: " + pe.getMessage());
        }
    }  // end method testParsing

    @Test public void testSecondsMinutesHours()
        throws CronExpression.Exception
    {
        final List<String> values = asList("00:00:30",
                "00:30:00",
                "00:30:30",
                "06:00:00",
                "06:00:30",
                "06:30:00",
                "06:30:30",
                "12:00:00",
                "12:00:30",
                "12:30:00",
                "12:30:30");

        final List<Tuple3<String, String, List<String>>> tests =  //
                                                                 asList(tuple("0/30 0/30 0/6", "00:00:00", values),
                tuple("0,30 0,30 0,6,12", "00:00:00", values),
                tuple("0-30/5", "00:00:00", asList("00:00:05", "00:00:10", "00:00:15", "00:00:20", "00:00:25", "00:00:30", "00:01:00", "00:01:05")),
                tuple("0-30/5 0 0",
                    "00:00:00",
                    asList("00:00:05", "00:00:10", "00:00:15", "00:00:20", "00:00:25", "00:00:30", "00:00:00", "00:00:05")));

        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        for (final Tuple3<String, String, List<String>> test : tests) {
            final CronExpression c    = new CronExpression(test.first());
            long                 date = DateTime.valueOf(test.second()).toMilliseconds();
            for (final String value : test.third()) {
                date = assertNotNull(c.getTimeAfter(DateTime.fromMilliseconds(date))).toMilliseconds();
                assertThat(format.format(date)).as(test.first() + " " + test.second()).isEqualTo(value);
            }
        }
    }

    private void assertParsesForField(String expression, FieldType constant, EnumSet<DayOfWeek> values) {
        try {
            final CronExpression       cronExpression = new CronExpression(expression);
            final CronExpression.Field field          = cronExpression.getField(constant);
            if (!field.all) {
                final Set<Integer> set = field.values();
                assertThat(set).isNotEmpty();
            }
            assertThat(cronExpression.getDaysOfWeek()).containsAll(values);
        }
        catch (final CronExpression.Exception pe) {
            fail(pe.getMessage());
        }
    }

    private void testList(List<Tuple3<String, String, List<String>>> tests, SimpleDateFormat format) {
        for (final Tuple3<String, String, List<String>> test : tests) {
            final CronExpression c    = new CronExpression(test.first());
            long                 date = DateTime.valueOf(test.second()).toMilliseconds();
            for (final String value : test.third()) {
                date = assertNotNull(c.getTimeAfter(date));
                assertThat(format.format(date)).as(test.first() + " " + test.second()).isEqualTo(value);
            }
        }
    }

    //~ Methods ......................................................................................................................................

    private static void mustProduceException(final String cronExpression, final String message) {
        try {
            new CronExpression(cronExpression);
            failBecauseExceptionWasNotThrown(CronExpression.Exception.class);
        }
        catch (final CronExpression.Exception pe) {
            assertThat(pe.getMessage()).startsWith(message);
        }
    }
}  // end class CronExpressionTest
