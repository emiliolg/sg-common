
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.core.DateOnly;
import tekgenesis.common.core.DateTime;
import tekgenesis.common.util.CalendarUtils;

import static org.assertj.core.api.Assertions.assertThat;

import static tekgenesis.common.EnumerationsTest.Color.BLUE;
import static tekgenesis.common.util.Conversions.*;

/**
 * User: emilio; Date: 12/16/11; Time: 12:08 PM;
 */

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class ConversionsTest {

    //~ Methods ......................................................................................................................................

    @Before public void assignTimeZone() {
        final TimeZone timeZone = TimeZone.getTimeZone("America/Argentina/Buenos_Aires");  // TimeZone.getTimeZone("GMT-3:00");
        CalendarUtils.setSessionTimeZone(timeZone);
        TimeZone.setDefault(timeZone);
        Locale.setDefault(Locale.ENGLISH);
    }
    @Test public void commonValues() {
        assertThat(toBoolean("true")).isTrue();
        assertThat(toBoolean("false")).isFalse();

        assertThat(toInt("10")).isEqualTo(10);
        assertThat(toLong("10")).isEqualTo(10);

        assertThat(toDouble("10.12")).isEqualTo(10.12);

        assertThat(toDecimal("0")).isZero();

        assertThat(toDecimal("100")).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test public void emptyTests() {
        assertThat(toBoolean("")).isFalse();
        assertThat(toInt("")).isZero();
        assertThat(toLong("")).isZero();
        assertThat(toDouble("")).isZero();
        assertThat(toDecimal("")).isZero();
    }

    @Test public void formatTest() {
        assertThat(format(10)).isEqualTo("10");
        assertThat(format(DateTime.dateTime(2000, 1, 1, 10, 20))).isEqualTo("Jan 1, 2000 10:20 AM");
        assertThat(format(DateOnly.date(2000, 1, 1))).isEqualTo("Jan 1, 2000");
        assertThat(format(BLUE)).isEqualTo("blue");
        assertThat(formatList(10)).containsExactly("10");
        assertThat(formatList(10, ImmutableList.of(BLUE, DateTime.dateTime(2000, 1, 1, 10, 20)), 1.0)).containsExactly("10",
            "blue",
            "Jan 1, 2000 10:20 AM",
            "1");
    }

    @Test public void fromStringTest() {
        assertThat(fromString("1", Long.class)).isEqualTo(1L);
        assertThat(fromString("1", Long.class)).isEqualTo(1L);
        assertThat(fromString("1", Double.class)).isEqualTo(1L);
        assertThat(fromString("1", Double.class)).isEqualTo(1);
        assertThat(fromString("1", BigDecimal.class)).isEqualTo(new BigDecimal(1));
        final Date date = new Date();
        fromString(dateToString(date), Date.class);
        final DateTime dateTime = DateTime.fromMilliseconds(System.currentTimeMillis()).withMilliseconds(0);
        assertThat(fromString(dateTimeToString(dateTime.toDate()), DateTime.class)).isEqualTo(dateTime);
        final DateOnly dateOnly = DateOnly.fromMilliseconds(System.currentTimeMillis());
        assertThat(fromString(dateTimeToString(dateOnly.toDate()), DateOnly.class)).isEqualTo(dateOnly);
    }

    @Test public void notSoRightValues() {
        assertThat(toBoolean("xxx")).isFalse();

        assertThat(toInt(" 10 ")).isEqualTo(10);
        assertThat(toLong(" 10 ")).isEqualTo(10);

        assertThat(toDouble(" 10.12 ")).isEqualTo(10.12);

        assertThat(toDecimal(" 0 ")).isZero();

        assertThat(toDecimal(" 100 ")).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test public void nullTests() {
        assertThat(toBoolean(null)).isFalse();
        assertThat(toInt(null)).isZero();
        assertThat(toLong(null)).isZero();
        assertThat(toDouble(null)).isZero();
        assertThat(toDecimal(null)).isZero();
    }

    @Test public void numberConversions() {
        assertThat(numberTo(null, Long.class)).isNull();

        assertThat(numberTo(10, Long.class)).isEqualTo(10L);

        final Byte v = numberTo(1110L, Byte.TYPE);
        assertThat(v).isEqualTo((byte) 86);
        assertThat(numberTo(v, Long.class)).isEqualTo(86L);

        assertThat(numberTo(1110990L, BigDecimal.class)).isEqualTo(BigDecimal.valueOf(1110990));
        assertThat(numberTo(10.20, BigDecimal.class)).isEqualTo(new BigDecimal("10.2"));
    }

    @Test public void toDecimalTest() {
        assertThat(toDecimal("0", 0)).isEqualTo(BigDecimal.ZERO);
        assertThat(toDecimal("1", 0)).isEqualTo(BigDecimal.ONE);
        assertThat(toDecimal("10", 0)).isEqualTo(BigDecimal.TEN);
        assertThat(toDecimal("1.2", 2)).isEqualTo(new BigDecimal(1.2).setScale(2, BigDecimal.ROUND_HALF_UP));
    }
}  // end class ConversionsTest
