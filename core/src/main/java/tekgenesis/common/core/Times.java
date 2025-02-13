
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.util.Date;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.Character.isDigit;
import static java.lang.String.valueOf;

import static tekgenesis.common.core.DateTimeBase.D;
import static tekgenesis.common.core.DateTimeBase.MO;
import static tekgenesis.common.core.DateTimeBase.Y;
import static tekgenesis.common.core.Tuple.tuple;
import static tekgenesis.common.util.CalendarUtils.makeDate;
import static tekgenesis.common.util.CalendarUtils.makeDateFromWeekOfYear;
import static tekgenesis.common.util.CalendarUtils.splitDateParts;

/**
 * Time (& Date) related utilities.
 */
public class Times {

    //~ Constructors .................................................................................................................................

    private Times() {}

    //~ Methods ......................................................................................................................................

    /** Add days to an instant. */
    public static long addDays(long time, int days) {
        return time + days * MILLIS_DAY;
    }

    /** Add seconds to an instant. */
    public static long addSeconds(long time, int seconds) {
        return time + seconds * MILLIS_SECOND;
    }

    /** Days between the times as milliseconds. */
    public static int daysBetween(long time1, long time2) {
        return (int) ((time1 - time2) / MILLIS_DAY);
    }

    /**
     * Generate a ISO date.
     *
     * @param   date  the number of milliseconds since the EPOCH
     *
     * @return  a string representing the date in the ISO 8601 format
     */
    public static String isoDate(long date) {
        final int[] fields = splitDateParts(date, true);

        final StringBuilder result = datePart(fields);
        return result.toString();
    }

    /**
     * Generate a ISO 8601 date.
     *
     * @param   date  the number of milliseconds since the EPOCH
     *
     * @return  a string representing the datetime in the ISO 8601 format
     */
    public static String isoDateTime(long date) {
        final int[] fields = splitDateParts(date, true);

        final StringBuilder result = datePart(fields);
        result.append("T");
        append2Digits(result, fields[3]);
        result.append(":");
        append2Digits(result, fields[4]);
        result.append(":");
        append2Digits(result, fields[5]);
        if (fields[6] != 0) {
            result.append(".");
            appendMillis(result, fields[6]);
        }
        result.append("Z");
        return result.toString();
    }  // end method isoDateTime

    /**
     * Reverse operation of {@link #toMidnight(Date)} gets the milliseconds and returns a Date.
     * Fixing the timezone to UTC if true
     */
    public static Date millisToDate(long time, boolean utc) {
        return utc ? DateOnly.fromMilliseconds(time).toDate() : DateTime.fromMilliseconds(time).toDate();
    }
    /**
     * Parses a String for a Date Only object.
     *
     * <p>The string can the following formats (using lex-like syntax): <b>1. Date-time
     * specification:</b> <i>-?y{1,6}/M{1,2}/d{1,2}</i></p>
     *
     * <p>If years are positive (AD), more than three digits <i>must</i> be specified (use leading
     * zeroes to pad if necessary).</p>
     *
     * @param   text  The string from which the time object should be parsed.
     *
     * @return  The number of milliseconds since the EPOCH (or zero if the text is null or empty)
     *
     * @throws  IllegalArgumentException  if the format is invalid
     */
    public static long parseDate(@Nullable final String text)
        throws IllegalArgumentException
    {
        if (text == null) return 0;
        final TimeParser timeParser = new TimeParser(text, true);
        if (!timeParser.hasText()) return 0;

        timeParser.parseDate();

        return timeParser.compute();
    }

    /**
     * Parses a String for a time object.
     *
     * <p>The string can the following formats (using lex-like syntax): <b>1. Date-time
     * specification:</b> <i>-?y{1,6}/M{1,2}/d{1,2}(Th{1,2}:m{1,2}(:s{1,2}(.ms)?)?)?</i></p>
     *
     * <p>If years are positive (AD), more than three digits <i>must</i> be specified (use leading
     * zeroes to pad if necessary).</p>
     *
     * @param   text  The string from which the time object should be parsed.
     *
     * @return  The number of milliseconds since the EPOCH (or zero if the text is null or empty)
     *
     * @throws  IllegalArgumentException  if the format is invalid
     */
    public static long parseDateTime(@Nullable final String text)
        throws IllegalArgumentException
    {
        if (text == null) return 0;
        else {
            final TimeParser timeParser = new TimeParser(text, false);
            return timeParser.hasText() ? timeParser.parseDateTime() : 0;
        }
    }  // end method parseDateTime

    /** Seconds between the times as milliseconds. */
    public static long secondsBetween(long time1, long time2) {
        return ((time1 - time2) / MILLIS_SECOND);
    }

    /** Stringify an hour/minute part. */
    public static String stringify(int i) {
        return i < 10 ? "0" + i : valueOf(i);
    }

    /** A string with the time only part. */
    public static String toHoursMinSecondsString(final long millis) {
        final int[] fields = splitDateParts(millis, true);

        final StringBuilder result = new StringBuilder();
        result.append("T");
        append2Digits(result, fields[3]);
        result.append(":");
        append2Digits(result, fields[4]);
        result.append(":");
        append2Digits(result, fields[5]);
        if (fields[6] != 0) {
            result.append(".");
            appendMillis(result, fields[6]);
        }
        result.append("Z");
        return result.toString();
    }

    /**
     * Takes seconds and returns a tuple where the first element is the hour part and the second one
     * is the minutes part. For 3600 -> (1, 0) is returned.
     */
    public static IntIntTuple toHoursMinutes(int seconds) {
        final int hs = seconds / (MINUTES_HOUR * SECONDS_MINUTE);
        final int ms = (seconds % (SECONDS_MINUTE * MINUTES_HOUR)) / SECONDS_MINUTE;
        return tuple(hs, ms);
    }

    /**
     * Takes seconds and returns a tuple where the first element is the hour part and the second one
     * is the minutes part, but in String format. For 3600 -> ("01", "00") is returned.
     */
    public static Tuple<String, String> toHoursMinutesString(int seconds) {
        final IntIntTuple t = toHoursMinutes(seconds);
        return tuple(stringify(t.first()), stringify(t.second()));
    }

    /** Get the Milliseconds of a {@link Date} adjusted to GMT midnight. */
    @SuppressWarnings("deprecation")
    public static long toMidnight(Date date) {
        return date.getTime() - minutesToMillis(date.getTimezoneOffset());
    }

    /** Adjust the Milliseconds to GMT midnight. Using canonical modulus. */
    public static long toMidnight(long time) {
        return time - ((time % MILLIS_DAY) + MILLIS_DAY) % MILLIS_DAY;
    }

    /** Returns if java Dates are equal in terms of year, month and day only. */
    @SuppressWarnings("deprecation")
    public static boolean isEqualDateOnly(final Date date, final Date otherDate) {
        return date.getYear() == otherDate.getYear() && date.getDate() == otherDate.getDate() && date.getMonth() == otherDate.getMonth();
    }

    @NotNull
    @SuppressWarnings("deprecation")
    static Date dateFromParts(int[] parts) {
        return new Date(parts[Y] - ONE_THOUSAND_NINE_HUNDRED, parts[MO] - 1, parts[D]);
    }

    static long makeTimeOnly(int hours, int minutes, int seconds, int millis) {
        return millis + MILLIS_SECOND * (seconds + SECONDS_MINUTE * (minutes + MINUTES_HOUR * hours));
    }

    private static void append2Digits(StringBuilder result, int value) {
        final int n = value % 100;
        result.append((char) (n / 10 + '0'));
        result.append((char) (n % 10 + '0'));
    }

    private static void appendMillis(StringBuilder result, int value) {
        if (value < 100) result.append(0);
        if (value < 10) result.append(0);
        result.append(value);
    }

    private static StringBuilder datePart(int[] fields) {
        final StringBuilder result = new StringBuilder();
        int                 year   = fields[0];
        if (year < 0) {
            result.append('-');
            year = -year;
        }
        // pad
        if (year > FOUR_DIGITS) result.append(year);
        else {
            append2Digits(result, year / 100);
            append2Digits(result, year % 100);
        }
        result.append("-");
        append2Digits(result, fields[1]);
        result.append("-");
        append2Digits(result, fields[2]);
        return result;
    }

    private static long minutesToMillis(int offsetInMinutes) {
        return offsetInMinutes * SECONDS_MINUTE * MILLIS_SECOND;
    }

    //~ Static Fields ................................................................................................................................

    private static final int ONE_THOUSAND_NINE_HUNDRED = 1900;

    // Constants
    public static final int MONTHS_YEAR = 12;
    public static final int DAYS_WEEK   = 7;

    public static final int  HOURS_DAY      = 24;
    public static final int  MINUTES_HOUR   = 60;
    public static final int  SECONDS_MINUTE = 60;
    public static final int  SECONDS_HOUR   = MINUTES_HOUR * SECONDS_MINUTE;
    public static final int  SECONDS_DAY    = HOURS_DAY * SECONDS_HOUR;
    public static final int  SECONDS_YEAR   = SECONDS_DAY * 365;
    public static final long MILLIS_SECOND  = 1000L;

    static final double THOUSAND = 1_000.0;

    public static final long MILLIS_MINUTE = SECONDS_MINUTE * MILLIS_SECOND;
    public static final long MILLIS_DAY    = HOURS_DAY * MINUTES_HOUR * MILLIS_MINUTE;

    private static final int FOUR_DIGITS = 9999;

    //~ Inner Classes ................................................................................................................................

    private static class TimeParser {
        int          dayOfMonth;
        int          dayOfWeek;
        int          dayOfYear;
        int          index;
        final int    length;
        int          month;
        final String text;
        boolean      utc;
        int          week;
        int          year;

        private TimeParser(@NotNull String text, boolean utc) {
            this.text = text;
            this.utc  = utc;
            length    = text.length();
            index     = 0;
            skipWhiteSpace();
            dayOfMonth = 1;
            dayOfWeek  = 1;
            dayOfYear  = 0;
            week       = 0;
            month      = 1;
        }

        private long compute() {
            if (week != 0) return makeDateFromWeekOfYear(utc, year, week, dayOfWeek);

            return dayOfYear == 0 ? makeDate(utc, year, month, dayOfMonth) : makeDateFromDayOfYear(utc, year, dayOfYear);
        }

        private char currentChar() {
            return hasText() ? text.charAt(index) : 0;
        }

        private int digitAt(int i) {
            return Character.digit(text.charAt(i), 10);
        }

        /** Count the number of digits in the current position. */
        private int digits() {
            int i = index;

            while (i < text.length() && isDigit(text.charAt(i)))
                i++;

            return i - index;
        }

        private boolean hasText() {
            return index < length;
        }

        private RuntimeException invalid() {
            return new IllegalArgumentException(text);
        }

        /** Parse a number that have at most 2 digits. */
        private int parse2Digits(int digits) {
            if (digits == 0) return 0;
            int result = digitAt(index++);
            if (digits > 1) result = result * 10 + digitAt(index++);
            return result;
        }

        private void parseDate() {
            year = parseSign() * parseYear();

            if (isDatePartSeparator(0)) index++;
            if (currentChar() == 'W') {
                index++;
                week = parse2Digits(digits());
                if (isDatePartSeparator(0)) index++;
                dayOfWeek = parse2Digits(digits()) % 7 + 1;
            }
            else {
                final int digits = digits();
                if (digits == 3)  // special case Day of the year
                    dayOfYear = parseNumber(3);
                else if (digits > 0) {
                    month = parse2Digits(digits);
                    if (isDatePartSeparator(0)) index++;
                    dayOfMonth = parse2Digits(digits());
                    if (dayOfMonth == 0) dayOfMonth = 1;
                }
            }
        }                         // end method parseDate

        private long parseDateTime() {
            parseDate();
            // Do not Inline calls methods modify the UTC field
            final int millis     = parseTimePart();
            final int zoneOffset = parseZoneOffset();
            return compute() + millis - zoneOffset;
        }  // end method parseDateTime

        private int parseMilliseconds() {
            final char c = currentChar();
            if (c != '.' && c != ',') return 0;
            index++;
            final int digits = digits();
            if (digits < 3) return parseNumber(digits) * (digits == 1 ? 100 : 10);
            final int ms = parseNumber(3);
            index += digits - 3;
            return ms;
        }  // end method parseMilliseconds

        /** Parse a number in the current position with the specified number of digits. */
        private int parseNumber(int digits) {
            int result = 0;
            for (int i = 0; i < digits; i++)
                result = result * 10 + digitAt(index + i);
            index += digits;
            return result;
        }

        private int parseSign() {
            final char c = currentChar();
            if (c == '+' || c == '-') index++;
            return c == '-' ? -1 : 1;
        }

        private int parseTime() {
            //J-
            return (
                    (parse2Digits(digits()) * MINUTES_HOUR +
                            parseTimeSegment()) * SECONDS_MINUTE +
                            parseTimeSegment()
            ) * 1000 + parseMilliseconds();
            //J+
        }

        private int parseTimePart() {
            final char c = currentChar();
            if (c != ' ' && c != 'T') return 0;
            utc = false;
            index++;
            return parseTime();
        }

        private int parseTimeSegment() {
            if (!hasText()) return 0;
            if (currentChar() == ':') index++;
            return parse2Digits(digits());
        }

        private int parseYear() {
            final int digits = digits();
            if (digits < 2 || digits == 3) throw invalid();

            int centuryMultiplier = 1;
            int yearDigits        = digits;
            if (digits != 4) {
                // See if what follows is a date part separator
                // If so then all digits are considered part of the year
                if (!isDatePartSeparator(digits)) {
                    if (digits != 2)  // Only 4 digits are considered
                        yearDigits = 4;
                    else              // It is a century
                        centuryMultiplier = 100;
                }
            }
            return parseNumber(yearDigits) * centuryMultiplier;
        }

        private int parseZoneOffset()
        {
            final char c = currentChar();
            if (c == '+' || c == '-') {
                utc = true;
                return parseSign() * parseTime();
            }
            if (c == 'Z') utc = true;
            return 0;
        }

        /** skipWhiteSpace. */
        private void skipWhiteSpace() {
            while (hasText() && text.charAt(index) == ' ')
                index++;
        }

        /** Check to see if there is a Date part separator at position (current+offset). */
        private boolean isDatePartSeparator(int offset) {
            final int nextPos = index + offset;
            return nextPos < length && "-/ ".indexOf(text.charAt(nextPos)) != -1;
        }

        private static long makeDateFromDayOfYear(boolean utc, int year, int dayOfYear) {
            return makeDate(utc, year, 1, 1) + (dayOfYear - 1L) * MILLIS_DAY;
        }

        static final int END = -1;
    }  // end class TimeParser
}  // end class Times
