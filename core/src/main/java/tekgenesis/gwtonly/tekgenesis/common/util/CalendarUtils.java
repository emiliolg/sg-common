
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.util.Date;

/**
 * Class that must have a GWT specific implementation.
 */
public class CalendarUtils {

    //~ Constructors .................................................................................................................................

    private CalendarUtils() {}

    //~ Methods ......................................................................................................................................

    /** return the milliseconds for a date with the specified parts. */
    public static long makeDate(boolean utc, int year, int month, int day) {
        return utc ? Date.UTC(year - BASE_YEAR, month - 1, day, 0, 0, 0) : new Date(year - BASE_YEAR, month - 1, day).getTime();
    }

    /** return the milliseconds for a date with the specified parts. */
    public static long makeDateFromWeekOfYear(boolean utc, int year, int week, int dayOfWeek) {
        throw new UnsupportedOperationException("Gwt makeDateFromWeekOfYear invocation");
    }

    /** from the specified date (interpreted as milliseconds) split the the calendar parts. */
    public static int[] splitDateParts(long date, boolean utc) {
        // Get a Date on UTC (Shift the TimeZone)
        final long offset = utc ? new Date(0).getTimezoneOffset() * 60 * 1000L : 0L;
        final Date d      = new Date(offset + date);

        return new int[] {
                   d.getYear() + BASE_YEAR,
                   d.getMonth() + 1,
                   d.getDate(),
                   d.getHours(),
                   d.getMinutes(),
                   d.getSeconds(),
                   0,
                   d.getDay(),
               };
    }

    //~ Static Fields ................................................................................................................................

    public static final int BASE_YEAR = 1900;
}  // end class CalendarUtils
