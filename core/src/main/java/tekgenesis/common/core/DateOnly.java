
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.text.DateFormat;
import java.util.Date;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.annotation.GwtIncompatible;

import static tekgenesis.common.util.CalendarUtils.makeDate;

/**
 * DateOnly defines a date where the time component is fixed at midnight. This class does not uses a
 * timezone, thus midnight is defined at UTC.
 *
 * <p>Date is thread-safe and immutable.</p>
 *
 * <p>Internally it just holds the number of milliseconds since EPOCH (January 1, 1970, 0:00:00 GMT)
 * </p>
 */
public class DateOnly extends DateTimeBase<DateOnly> {

    //~ Constructors .................................................................................................................................

    /** Default contructor. */
    public DateOnly() {}

    private DateOnly(long time) {
        super(time);
    }

    //~ Methods ......................................................................................................................................

    /**
     * Format the DateTime using the specified DateFormat as described in
     * {@link DateFormat#getDateInstance()}.
     */
    @GwtIncompatible
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public String format() {
        return DateFormat.getDateInstance().format(toDate());
    }

    /**
     * Format the DateOnly using the specified DateFormat as described in
     * {@link DateFormat#getDateInstance(int style)}.
     */
    @GwtIncompatible
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public String format(int style) {
        return DateFormat.getDateInstance(style).format(toDate());
    }

    @NotNull @Override public DateOnly toDateOnly() {
        return this;
    }

    @Override public String toString() {
        return Times.isoDate(time);
    }

    @Override DateOnly create(long t) {
        return fromMilliseconds(t);
    }

    boolean fixedToUtc() {
        return true;
    }

    //~ Methods ......................................................................................................................................

    /** Creates a DateOnly object with the current System time. */
    @NotNull public static DateOnly current() {
        return fromDate(new Date(DateTime.currentTimeMillis()));
    }

    /**
     * Parses a String for a Date Only object.
     *
     * @see  #fromString
     */
    @NotNull public static DateOnly date(@Nullable String text) {
        return fromString(text);
    }

    /** Creates a DateOnly object, from it parts (Year, Month(1-12) and Day). */
    @NotNull public static DateOnly date(int year, int month, int day) {
        return fromMilliseconds(makeDate(true, year, month, day));
    }

    /** Creates a DateOnly object from a {@link Date} it correct the timezone to UTC. */
    @NotNull public static DateOnly fromDate(@Nullable Date date) {
        return date == null ? EPOCH : fromMilliseconds(Times.toMidnight(date));
    }

    /** Creates a DateOnly object from a number of milliseconds since EPOCH. Fix to UTC midnight */
    @NotNull public static DateOnly fromMilliseconds(long milliseconds) {
        final long x = Times.toMidnight(milliseconds);
        if (x == 0) return EPOCH;
        final int      index = hashTime(x);
        final DateOnly p     = CACHED_ELEMENTS[index];
        return p != null && p.time == x ? p : (CACHED_ELEMENTS[index] = new DateOnly(x));
    }

    /** Creates a DateOnly object, from it parts (Year, Month(1-12) and Day). */
    @NotNull public static DateOnly fromParts(int year, int month, int day) {
        return date(year, month, day);
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
    @NotNull public static DateOnly fromString(@Nullable String text) {
        return fromMilliseconds(Times.parseDate(text));
    }

    /**
     * Parses a String for a Date Only object.
     *
     * @see  #fromString
     */
    @NotNull public static DateOnly valueOf(@Nullable String text) {
        return fromString(text);
    }

    //~ Static Fields ................................................................................................................................

    public static final DateOnly    EPOCH           = new DateOnly(0);
    private static final DateOnly[] CACHED_ELEMENTS = new DateOnly[CACHE_SIZE];

    private static final long serialVersionUID = -671902790527885944L;
}  // end class DateOnly
