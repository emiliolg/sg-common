
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Locale;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.env.context.Context;

/**
 * Utility methods to work with {@link DayOfWeek }.
 */
public interface Days {

    //~ Methods ......................................................................................................................................

    /** Return the {@link Calendar#DAY_OF_WEEK}. */
    static int calendarNumber(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
        case MONDAY:
            return Calendar.MONDAY;
        case TUESDAY:
            return Calendar.TUESDAY;
        case WEDNESDAY:
            return Calendar.WEDNESDAY;
        case THURSDAY:
            return Calendar.THURSDAY;
        case FRIDAY:
            return Calendar.FRIDAY;
        case SATURDAY:
            return Calendar.SATURDAY;
        default:
            return Calendar.SUNDAY;
        }
    }

    /** Return the CronValue String for a given DayOfWeek. */
    static String cronValue(DayOfWeek day) {
        switch (day) {
        case MONDAY:
            return "MON";
        case TUESDAY:
            return "TUE";
        case WEDNESDAY:
            return "WED";
        case THURSDAY:
            return "THU";
        case FRIDAY:
            return "FRI";
        case SATURDAY:
            return "SAT";
        case SUNDAY:
            return "SUN";
        }
        return "";
    }
    /** Returns the description for the current Locale. */
    static String description(@Nullable DayOfWeek dow) {
        return description(dow, Context.getContext().getLocale());
    }
    /** Returns the description for the specified Locale. */
    static String description(@Nullable DayOfWeek dow, Locale locale) {
        return dow == null ? "" : dow.getDisplayName(TextStyle.FULL, locale);
    }

    /** Get standard java DayOfWeek from l {@link Calendar#DAY_OF_WEEK}. */
    static DayOfWeek fromCalendarNumber(int dayOfWeek) {
        final int dow = dayOfWeek % 7;

        switch (dow) {
        case Calendar.SUNDAY:
            return DayOfWeek.SUNDAY;
        case Calendar.MONDAY:
            return DayOfWeek.MONDAY;
        case Calendar.TUESDAY:
            return DayOfWeek.TUESDAY;
        case Calendar.WEDNESDAY:
            return DayOfWeek.WEDNESDAY;
        case Calendar.THURSDAY:
            return DayOfWeek.THURSDAY;
        case Calendar.FRIDAY:
            return DayOfWeek.FRIDAY;
        default:
            return DayOfWeek.SATURDAY;
        }
    }

    /** Create a DayOfWeek based on the Description. */
    @Nullable static DayOfWeek fromDescription(@NotNull final String description) {
        for (final DayOfWeek dayOfWeek : DayOfWeek.values()) {
            if (description(dayOfWeek).equalsIgnoreCase(description)) return dayOfWeek;
        }
        return null;
    }
    /** Returns the short description for the current Locale. */
    static String shortDescription(@Nullable DayOfWeek dow) {
        return shortDescription(dow, Context.getContext().getLocale());
    }

    /** Returns the short description for the specified Locale. */
    static String shortDescription(@Nullable DayOfWeek dow, Locale locale) {
        return dow == null ? "" : dow.getDisplayName(TextStyle.SHORT, locale);
    }
}  // end interface Days
