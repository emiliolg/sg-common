
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static java.lang.Math.abs;
import static java.util.Calendar.*;
import static java.util.TimeZone.getTimeZone;

/**
 * Class that must have a GWT specific implementation.
 */
@SuppressWarnings("NonJREEmulationClassesInClientCode")  // Not included in GWT
public class CalendarUtils {

    //~ Constructors .................................................................................................................................

    private CalendarUtils() {}

    //~ Methods ......................................................................................................................................

    /** return the milliseconds for a date with the specified parts. */
    public static long makeDate(boolean utc, int year, int month, int day) {
        final Calendar cal = new GregorianCalendar(utc ? UTC : timeZone.get());
        cal.clear();
        cal.set(ERA, year < 0 ? GregorianCalendar.BC : GregorianCalendar.AD);
        cal.set(YEAR, abs(year));
        cal.set(MONTH, month - 1);
        cal.set(DAY_OF_MONTH, day);

        try {
            return cal.getTimeInMillis();
        }
        catch (final Exception e) {
            return 0;
        }
    }

    /** return the milliseconds for a date with the specified parts. */
    public static long makeDateFromWeekOfYear(boolean utc, int year, int week, int dayOfWeek) {
        final Calendar cal = new GregorianCalendar(utc ? UTC : timeZone.get());
        cal.clear();
        cal.set(ERA, year < 0 ? GregorianCalendar.BC : GregorianCalendar.AD);
        cal.set(YEAR, abs(year));
        cal.set(WEEK_OF_YEAR, week);
        cal.set(DAY_OF_WEEK, dayOfWeek);
        return cal.getTimeInMillis();
    }

    /** from the specified date (interpreted as milliseconds) split the the calendar parts. */
    public static int[] splitDateParts(long date, boolean utc) {
        final Calendar cal = new GregorianCalendar(utc ? UTC : timeZone.get());
        cal.setTimeInMillis(date);

        final int era = cal.get(ERA);
        return new int[] {
                   cal.get(YEAR) * (era == GregorianCalendar.BC ? -1 : 1),
                   cal.get(MONTH) + 1,
                   cal.get(DAY_OF_MONTH),
                   cal.get(HOUR_OF_DAY),
                   cal.get(MINUTE),
                   cal.get(SECOND),
                   cal.get(MILLISECOND),
                   cal.get(DAY_OF_WEEK),
               };
    }

    /** Sets session timezone. */
    public static void setSessionTimeZone(TimeZone tz) {
        timeZone.set(tz);
    }

    /** Sets session timezone offset. */
    public static void setSessionTimeZoneOffset(int offset) {
        setSessionTimeZone(getTimeZone((offset > 0 ? "GMT+" : "GMT") + offset));
    }

    //~ Static Fields ................................................................................................................................

    private static final ThreadLocal<TimeZone> timeZone = ThreadLocal.withInitial(TimeZone::getDefault);

    /** The GMT Timezone. */
    private static final TimeZone UTC = getTimeZone("GMT");
}  // end class CalendarUtils
