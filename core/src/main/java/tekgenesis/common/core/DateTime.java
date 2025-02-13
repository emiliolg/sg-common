
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
import java.util.function.LongSupplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.annotation.GwtIncompatible;

import static tekgenesis.common.core.Times.*;
import static tekgenesis.common.util.CalendarUtils.makeDate;

/**
 * DateTime is the standard implementation of an unmodifiable datetime class. It represents an exact
 * point on the time-line, limited to the precision of milliseconds. A <code>DateTime</code>
 * calculates its fields with respect to a given time zone. (By Default the current one) Internally,
 * it holds the datetime as milliseconds from 1970-01-01T00:00:00Z.
 *
 * <p>DateTime is thread-safe and immutable.</p>
 */
@SuppressWarnings("WeakerAccess ")
public final class DateTime extends DateTimeBase<DateTime> {

    //~ Constructors .................................................................................................................................

    @SuppressWarnings("UnusedDeclaration")
    private DateTime() {}

    private DateTime(long time) {
        super(time);
    }

    //~ Methods ......................................................................................................................................

    /**
     * Returns a copy of this time plus the specified number of hours.
     *
     * <p>This instance is immutable and unaffected by this method call.</p>
     */
    public DateTime addHours(int hours) {
        return addMinutes(hours * MINUTES_HOUR);
    }

    /**
     * Returns a copy of this time plus the specified number of seconds.
     *
     * <p>This instance is immutable and unaffected by this method call.</p>
     */
    public DateTime addMilliseconds(long milliseconds) {
        return create(time + milliseconds);
    }
    /**
     * Returns a copy of this time plus the specified number of minutes.
     *
     * <p>This instance is immutable and unaffected by this method call.</p>
     */
    public DateTime addMinutes(int minutes) {
        final int seconds = minutes * SECONDS_MINUTE;
        return addSeconds(seconds);
    }

    /**
     * Returns a copy of this time plus the specified number of seconds.
     *
     * <p>This instance is immutable and unaffected by this method call.</p>
     */
    public DateTime addSeconds(int seconds) {
        final long milliseconds = seconds * MILLIS_SECOND;
        return addMilliseconds(milliseconds);
    }

    /**
     * Format the DateTime using the specified DateFormat as described in
     * {@link DateFormat#getDateTimeInstance(int dateStyle, int timeStyle)}.
     */
    @GwtIncompatible public String format(int dateStyle, int timeStyle) {
        return DateFormat.getDateTimeInstance(dateStyle, timeStyle).format(toDate());
    }

    /** Returns the number of hours between this time and the one specified as a parameter. */
    public int hoursFrom(DateTime other) {
        return (int) ((time - other.time) / (MINUTES_HOUR * MILLIS_MINUTE));
    }

    /** Returns the number of minutes between this time and the one specified as a parameter. */
    public int minutesFrom(DateTime other) {
        return (int) ((time - other.time) / Times.MILLIS_MINUTE);
    }

    /** Returns the number of seconds between this time and the one specified as a parameter. */
    public int secondsFrom(DateTime other) {
        return (int) ((time - other.time) / MILLIS_SECOND);
    }

    /** Converts this Object to a {@link DateTime} in the current timezone. */
    @NotNull @Override public DateTime toDateTime() {
        return this;
    }

    /** Returns the number of months between this date and the one specified as a parameter. */
    @Override public String toString() {
        return Times.isoDateTime(time);
    }

    /** Return a new time with the hours set to the specified value. */
    public DateTime withHours(int hours) {
        return withPart(H, hours);
    }

    /** Return a new time with the milliseconds set to the specified value. */
    public DateTime withMilliseconds(int milliseconds) {
        return withPart(MS, milliseconds);
    }

    /** Return a new time with the minutes set to the specified value. */
    public DateTime withMinutes(int minutes) {
        return withPart(M, minutes);
    }

    /** Return a new time with the minutes set to the specified value. */
    public DateTime withSeconds(int seconds) {
        return withPart(S, seconds);
    }

    /** Return the seconds since midNight. */
    public int getDaySeconds() {
        final int[] parts = splitParts();
        return (parts[H] * MINUTES_HOUR + parts[M]) * SECONDS_MINUTE + parts[S];
    }

    /** Return the seconds part in a fractional way (Containing milliseconds) for this time. */
    public double getFractionalSeconds() {
        final int[] ints = splitParts();
        return ints[S] + ints[MS] / THOUSAND;
    }

    /** Return the hour for this time. */
    public int getHours() {
        return splitParts()[H];
    }

    /** Return the minutes for this time. */
    public int getMinutes() {
        return splitParts()[M];
    }

    /** Return the seconds for this time. */
    public int getSeconds() {
        return splitParts()[S];
    }

    @Override DateTime create(long t) {
        return fromMilliseconds(t);
    }

    @Override boolean fixedToUtc() {
        return false;
    }

    //~ Methods ......................................................................................................................................

    /** Creates a DateTime object with the current System time. */
    @NotNull public static DateTime current() {
        return fromMilliseconds(currentTimeMillis());
    }

    /** Returns the current time in milliseconds. */
    public static long currentTimeMillis() {
        return timeSupplier.getAsLong();
    }

    /**
     * Parses a String for a DateTime object.
     *
     * @see  #fromString
     */
    @NotNull public static DateTime date(@Nullable String text) {
        return fromString(text);
    }
    /** Creates a DateTime object, from it parts (Year, Month(1-12), Day). */
    @NotNull public static DateTime dateTime(int year, int month, int day) {
        return dateTime(year, month, day, 0, 0, 0, 0);
    }
    /**
     * Creates a DateTime object, from it parts (Year, Month(1-12), Day, hours, minutes, seconds).
     */
    @NotNull public static DateTime dateTime(int year, int month, int day, int hours, int minutes) {
        return dateTime(year, month, day, hours, minutes, 0, 0);
    }
    /**
     * Creates a DateTime object, from it parts (Year, Month(1-12), Day, hours, minutes, seconds).
     */
    @NotNull public static DateTime dateTime(int year, int month, int day, int hours, int minutes, int seconds) {
        return dateTime(year, month, day, hours, minutes, seconds, 0);
    }

    /**
     * Creates a DateTime object, from it parts (Year, Month(1-12), Day, hours, minutes, seconds and
     * milliseconds).
     */
    @NotNull public static DateTime dateTime(int year, int month, int day, int hours, int minutes, int seconds, int millis) {
        return fromMilliseconds(makeDate(false, year, month, day) + Times.makeTimeOnly(hours, minutes, seconds, millis));
    }

    /** Creates a DateTime object from a {@link Date}. */
    @NotNull public static DateTime fromDate(@Nullable Date date) {
        return fromMilliseconds(date == null ? 0 : date.getTime());
    }

    /** Creates a DateTime object from a number of milliseconds since EPOCH. */
    @NotNull public static DateTime fromMilliseconds(long milliseconds) {
        if (milliseconds == 0) return EPOCH;
        final int      index = hashTime(milliseconds);
        final DateTime p     = CACHED_ELEMENTS[index];
        return p != null && p.time == milliseconds ? p : (CACHED_ELEMENTS[index] = new DateTime(milliseconds));
    }

    /**
     * Parses a String for a DateTime object.
     *
     * @param   text  The string from which the time object should be parsed.
     *
     * @return  The number of milliseconds since the EPOCH (or zero if the text is null or empty)
     *
     * @throws  IllegalArgumentException  if the format is invalid
     */
    @NotNull public static DateTime fromString(@Nullable String text) {
        return fromMilliseconds(Times.parseDateTime(text));
    }

    /**
     * Parses a String for a DateTime object.
     *
     * @see  #fromString
     */
    @NotNull public static DateTime valueOf(@Nullable String text) {
        return fromString(text);
    }

    /** Set the active instance of the CurrentTimeProvider. returns the previous value */
    public static LongSupplier setTimeSupplier(LongSupplier newValue) {
        final LongSupplier previous = timeSupplier;
        timeSupplier = newValue;
        return previous;
    }

    //~ Static Fields ................................................................................................................................

    private static transient LongSupplier timeSupplier = System::currentTimeMillis;

    public static final DateTime EPOCH     = new DateTime(0);
    public static final DateTime MAX_VALUE = new DateTime(253402200799999L);   // 30-12-9999
    public static final DateTime MIN_VALUE = new DateTime(-210835180800000L);  // 01-01-4712 BC
    public static final DateTime ZERO      = new DateTime(-62135758800000L);   // 01-01-01 AD

    private static final long serialVersionUID = 3519429639450700127L;

    private static final DateTime[] CACHED_ELEMENTS = new DateTime[CACHE_SIZE];
}  // end class DateTime
