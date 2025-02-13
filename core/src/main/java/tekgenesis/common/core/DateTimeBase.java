
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Date;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.annotation.GwtIncompatible;

import static tekgenesis.common.util.CalendarUtils.makeDate;
import static tekgenesis.common.util.CalendarUtils.splitDateParts;

/**
 * The base implementation of {@link DateOnly} and {@link DateTime}.
 */
public abstract class DateTimeBase<This extends DateTimeBase<This>> implements Serializable, RichComparable<This> {

    //~ Instance Fields ..............................................................................................................................

    /**
     * The currently set time for this Date expressed in milliseconds after January 1, 1970, 0:00:00
     * GMT.
     *
     * @serial
     */
    final long time;

    //~ Constructors .................................................................................................................................

    DateTimeBase() {
        time = System.currentTimeMillis();
    }

    DateTimeBase(long time) {
        this.time = time;
    }

    //~ Methods ......................................................................................................................................

    /**
     * Returns a copy of this date plus the specified number of days.
     *
     * <p>This date instance is immutable and unaffected by this method call.</p>
     *
     * @param  days  the amount of days to add, may be negative
     */
    @SuppressWarnings("WeakerAccess")
    public final This addDays(int days) {
        return create(Times.addDays(time, days));
    }

    /**
     * Returns a copy of this date plus the specified number of months.
     *
     * <p>This date instance is immutable and unaffected by this method call.</p>
     */
    public final This addMonths(int months) {
        final int[] parts = splitParts();
        final int   month = parts[MO] - MO + months;    // Rolling month in base 0
        final int   years = month / Times.MONTHS_YEAR;  // Number of years to increment
        parts[Y]  += years;
        parts[MO] = month - years * Times.MONTHS_YEAR + MO;
        return makeFromParts(parts);
    }

    /**
     * Returns a copy of this date plus the specified number of weeks.
     *
     * <p>This date instance is immutable and unaffected by this method call.</p>
     */
    public final This addWeeks(int weeks) {
        return addDays(weeks * Times.DAYS_WEEK);
    }

    /**
     * Returns a copy of this date plus the specified number of years.
     *
     * <p>This date instance is immutable and unaffected by this method call.</p>
     */
    public final This addYears(int years) {
        final int[] parts = splitParts();
        parts[Y] += years;
        return makeFromParts(parts);
    }

    /** Check that the Object is between the specified values (inclusive). */
    public boolean between(@NotNull This low, @NotNull This high) {
        return compareTo(low) >= 0 && compareTo(high) <= 0;
    }

    @Override public int compareTo(@NotNull This o) {
        return (time > o.time) ? 1 : (time == o.time) ? 0 : -1;
    }

    /** Returns the number of days between this date and the one specified as a parameter. */
    @SuppressWarnings("WeakerAccess")
    public int daysFrom(This other) {
        return Times.daysBetween(time, other.time);
    }

    @Override public boolean equals(Object o) {
        return o instanceof DateTimeBase && ((DateTimeBase<?>) o).time == time;
    }

    /**
     * Format the DateTime using the specified DateFormat as described in
     * {@link DateFormat#getDateTimeInstance()}.
     */
    @GwtIncompatible
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public String format() {
        return DateFormat.getDateTimeInstance().format(toDate());
    }

    /**
     * Format the DateTime using the specified mask formatter as described in
     * {@link SimpleDateFormat}.
     */
    @GwtIncompatible
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public final String format(String mask) {
        final SimpleDateFormat sf = new SimpleDateFormat(mask);
        return sf.format(toDate());
    }
    /** Format the DateTime using the specified DateFormat as described in {@link DateFormat}. */
    @GwtIncompatible
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public final String format(DateFormat dateFormat) {
        return dateFormat.format(toDate());
    }

    @Override public int hashCode() {
        return (int) (time ^ (time >>> 32));
    }

    /** Returns the number of months between this date and the one specified as a parameter. */
    @SuppressWarnings("WeakerAccess")
    public final int monthsFrom(This other) {
        final int[] thisDate = splitParts();
        final int[] thatDate = other.splitParts();
        final int   result   = Times.MONTHS_YEAR * (thisDate[Y] - thatDate[Y]) + (thisDate[MO] - thatDate[MO]);
        return dayPlusTime(thisDate) < dayPlusTime(thatDate) ? result - 1 : result;
    }

    /** Converts this Object to a {@link Date} in the current timezone. */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    public final Date toDate() {
        final int[] parts = splitParts();
        return fixedToUtc() ? Times.dateFromParts(parts) : new Date(time);
    }

    /** Return the {@link DateOnly} part of this. */
    @NotNull public DateOnly toDateOnly() {
        return DateOnly.fromDate(toDate());
    }

    /** Converts this Object to a {@link DateTime} in the current timezone. */
    @NotNull public DateTime toDateTime() {
        return DateTime.fromDate(toDate());
    }

    /**
     * Returns this time value in milliseconds.
     *
     * @return  the current time as UTC milliseconds from the epoch.
     */
    public final long toMilliseconds() {
        return time;
    }

    /** Returns the number of weeks between this date and the one specified as a parameter. */
    public final int weeksFrom(This other) {
        return daysFrom(other) / Times.DAYS_WEEK;
    }

    /** Return a new date with the day set to the specified value. */
    public final This withDay(int day) {
        return withPart(D, day);
    }

    /** Return a new date with the month set to the specified value. */
    public final This withMonth(int month) {
        return withPart(MO, month);
    }

    /** Return a new date with the year set to the specified value. */
    public final This withYear(int year) {
        return withPart(Y, year);
    }

    /** Returns the number of months between this date and the one specified as a parameter. */
    public final int yearsFrom(This other) {
        return monthsFrom(other) / Times.MONTHS_YEAR;
    }

    /** Return the day for this date. */
    public final int getDay() {
        return splitParts()[D];
    }

    /** Return the day of week for this date. */
    @GwtIncompatible public final DayOfWeek getDayOfWeek() {
        final int dow = splitParts()[DOW];
        return DayOfWeek.of(dow == 1 ? 7 : dow - 1);
    }

    /** Return the month for this date. */
    public final int getMonth() {
        return splitParts()[MO];
    }

    /** Return the year for this date. */
    public final int getYear() {
        return splitParts()[Y];
    }

    /** 'Constructor' used to be overridden in subclasses. */
    abstract This create(long t);

    /** Whether is timezone sensible or not. */
    abstract boolean fixedToUtc();

    /** Split into parts to be overridden in subclasses (So it can control. */
    int[] splitParts() {
        return splitDateParts(time, fixedToUtc());
    }

    This withPart(int part, int value) {
        final int[] parts = splitParts();
        parts[part] = value;
        return makeFromParts(parts);
    }

    private long dayPlusTime(int[] thisDate) {
        return thisDate[D] * Times.MILLIS_DAY + makeTimeOnly(thisDate);
    }

    private This makeFromParts(int[] parts) {
        return create(makeDate(fixedToUtc(), parts[Y], parts[MO], parts[D]) + makeTimeOnly(parts));
    }

    private long makeTimeOnly(int[] parts) {
        return Times.makeTimeOnly(parts[H], parts[M], parts[S], parts[MS]);
    }

    //~ Methods ......................................................................................................................................

    static int hashTime(long x) {
        return (Long.hashCode(x) & MAX_VALUE) % CACHE_SIZE;
    }

    //~ Static Fields ................................................................................................................................

    private static final int MAX_VALUE = 0x7fffffff;

    static final int CACHE_SIZE = 20479;

    // Parts Index
    static final int Y   = 0;
    static final int MO  = 1;
    static final int D   = 2;
    static final int H   = 3;
    static final int M   = 4;
    static final int S   = 5;
    static final int MS  = 6;
    static final int DOW = 7;

    private static final long serialVersionUID = -8275923764086971689L;
}  // end class DateTimeBase
