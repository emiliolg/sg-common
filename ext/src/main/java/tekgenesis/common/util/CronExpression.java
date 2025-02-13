
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.ImmutableSet;
import tekgenesis.common.core.DateTime;
import tekgenesis.common.core.Option;

import static java.lang.Character.isDigit;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;

import static tekgenesis.common.Predefined.notNull;
import static tekgenesis.common.collections.Colls.immutable;
import static tekgenesis.common.core.Option.some;
import static tekgenesis.common.core.Times.*;
import static tekgenesis.common.util.CronExpression.Exception.conflictingSpec;
import static tekgenesis.common.util.CronExpression.Exception.lastAndValue;
import static tekgenesis.common.util.CronExpression.Exception.multipleNth;

/**
 * Provides a parser and evaluator for unix-like cron expressions.
 */
public class CronExpression {

    //~ Instance Fields ..............................................................................................................................

    private final String                    cronExpression;
    private final EnumMap<FieldType, Field> fields;
    private final TimeZone                  timeZone;

    //~ Constructors .................................................................................................................................

    /** Constructs a new CronExpression. */
    public CronExpression(@NotNull String str) {
        this(str, TimeZone.getDefault());
    }

    /** Constructs a new CronExpression with a specified TimeZone . */
    public CronExpression(@NotNull String cronExpression, TimeZone timeZone) {
        this.cronExpression = cronExpression;
        this.timeZone       = timeZone;
        fields              = new EnumMap<>(FieldType.class);

        final String[] fieldStrings = notNull(cronExpression).split("\\s+");

        for (final FieldType type : FieldType.values()) {
            final Field field = new Field(type);
            fields.put(type, field);
            for (final String v : type.get(fieldStrings).split(","))
                field.parse(v);
        }
    }

    //~ Methods ......................................................................................................................................

    /** Returns the string representation of the CronExpression. */
    @Override public String toString() {
        return cronExpression;
    }

    /** Returns the string representation of the CronExpression. */
    public String getCronExpression() {
        return cronExpression;
    }

    /** Get Days of week. */
    public EnumSet<DayOfWeek> getDaysOfWeek() {
        final Field dow = fields.get(FieldType.DAY_OF_WEEK);
        if (dow.includeAll()) return EnumSet.allOf(DayOfWeek.class);
        final EnumSet<DayOfWeek> result = EnumSet.noneOf(DayOfWeek.class);
        for (final int v : dow.values())
            result.add(DayOfWeek.of(v == 1 ? 7 : v - 1));
        return result;
    }

    /** Get the next time this expression is valid. */
    @Nullable public Long getTimeAfter(long millis) {
        return getValidTime(millis + MILLIS_SECOND);
    }

    /** Get the next time this expression is valid. */
    @Nullable public DateTime getTimeAfter(final DateTime dateTime) {
        final Long timeAfter = getTimeAfter(dateTime.toMilliseconds());
        return timeAfter == null ? null : DateTime.fromMilliseconds(timeAfter);
    }

    /** Returns the timeZone for which this expression will be resolved. */
    public TimeZone getTimeZone() {
        return timeZone;
    }
    /**
     * Get the a time (in milliseconds) greater or equal to the specified one (truncated to seconds)
     * where this expression is valid.
     */
    @Nullable public Long getValidTime(long millis) {
        final Calendar cl = new GregorianCalendar(timeZone);
        cl.setTimeInMillis(millis - millis % MILLIS_SECOND);  // truncate to second precision

        while (!adjust(cl))
            if (cl.get(Calendar.YEAR) > TOP_YEAR) return null;
        return cl.getTimeInMillis();
    }

    /**
     * Indicates whether the given date satisfies the cron expression. (Time is truncated to
     * seconds)
     */
    public boolean isSatisfiedBy(long millis) {
        final long ms = millis - millis % MILLIS_SECOND;
        return ms == notNull(getValidTime(ms), ms - 1);
    }

    /** Returns true if the CronExpression is satisfied by the specified DateTime. */
    public boolean isSatisfiedBy(final DateTime dt) {
        return isSatisfiedBy(dt.toMilliseconds());
    }

    Field getField(FieldType t) {
        return fields.get(t);
    }

    private boolean adjust(Calendar cl) {
        for (final FieldType type : FieldType.values()) {
            if (!getField(type).adjust(cl)) return false;
        }
        return true;
    }

    //~ Methods ......................................................................................................................................

    /** Try to parse the Exception and returns and Option with none if it is not valid. */
    public static Option<CronExpression> parse(@Nullable String cronExpression) {
        try {
            if (cronExpression != null && !cronExpression.isEmpty()) return some(new CronExpression(cronExpression));
        }
        catch (final Exception ignore) {}
        return Option.empty();
    }

    /**
     * Indicates whether the specified cron expression can be parsed into a valid cron expression.
     */
    public static boolean isValidExpression(String cronExpression) {
        return parse(cronExpression).isPresent();
    }

    //~ Static Fields ................................................................................................................................

    private static final int TOP_YEAR = 2999;
    private static final int MIN_YEAR = 2000;

    private static final int DAYS_MONTH = 31;

    private static final DateFormatSymbols dateSymbols = new DateFormatSymbols(Locale.US);

    public static final int MAX_YEAR = 2200;

    //~ Enums ........................................................................................................................................

    enum FieldType {
        SECOND(0, SECONDS_MINUTE - 1, Calendar.SECOND),

        MINUTE(0, MINUTES_HOUR - 1, Calendar.MINUTE),

        HOUR(0, HOURS_DAY - 1, Calendar.HOUR_OF_DAY),

        DAY_OF_MONTH(1, DAYS_MONTH, Calendar.DAY_OF_MONTH) {
            @Override void last(final Field field, final int offset, final String w) {
                field.weekday    = w != null;
                field.last       = true;
                field.lastOffset = offset;
            }

            @Override void noSpec(final Field field) {
                field.addAll();
                field.noSpec = true;
            }

            @Override public void nearestWeekday(final Field field, final int day) {
                field.weekday = true;
                field.values.add(day);
            }

            @Override int nextValue(final Field field, Calendar cl, final int v) {
                final int result;
                if (field.last) {
                    final int day = cl.getActualMaximum(Calendar.DAY_OF_MONTH) - field.lastOffset;
                    result = field.weekday ? field.adjustToWeekday(cl, day) : day;
                }
                else result = field.weekday ? field.adjustToWeekday(cl, field.values.first()) : super.nextValue(field, cl, v);
                // If new day is smaller than previous one, we need to increment the month.
                // Then we set 1 as the new day to force recalculation.
                return result < v ? 1 : result;
            }},

        MONTH(1, MONTHS_YEAR, Calendar.MONTH) {
            @Override public int parseName(final String name) {
                final String[] shortMonths = dateSymbols.getShortMonths();
                for (int i = 0; i < shortMonths.length; i++) {
                    if (name.equalsIgnoreCase(shortMonths[i])) return i + 1;
                }
                return super.parseName(name);
            }},

        DAY_OF_WEEK(1, DAYS_WEEK, Calendar.DAY_OF_WEEK) {
            @Override public void lastDowOfMonth(final Field field, final int i) {
                field.last = true;
                field.values.add(i);
            }

            @Override void dayOfWeekNth(Field field, final int dow, final int nth) {
                if (field.weekDayNth != 0) throw multipleNth();
                field.values.add(dow);
                field.weekDayNth = nth;
            }

            @Override void noSpec(final Field field) { field.addAll(); }

            @Override public int parseName(final String name) {
                final String[] shortWeekdays = dateSymbols.getShortWeekdays();
                for (int i = 1; i < shortWeekdays.length; i++) {
                    if (name.equalsIgnoreCase(shortWeekdays[i])) return i;
                }
                return super.parseName(name);
            }

            @Override int nextValue(final Field field, final Calendar cl, final int v) {
                final int current  = get(cl);
                final int expected = super.nextValue(field, cl, current);

                // Add to correct to the expected day of the week
                int       day     = v + (expected + 7 - current) % 7;
                final int lastDay = cl.getActualMaximum(Calendar.DAY_OF_MONTH);

                if (field.last) {
                    // Move to the last occurrence in the month
                    while (day <= lastDay - 7)
                        day += 7;
                }
                else if (field.weekDayNth != 0) {
                    final int weekOfMonth = ((day - 1) / 7) + 1;
                    final int offset      = (field.weekDayNth - weekOfMonth) * 7;
                    day = offset >= 0 ? day + offset : 1;
                }
                return day > lastDay ? 1 : day;
            }@Override boolean adjust(final Field field, final Calendar cl) { return doAdjust(field, DAY_OF_MONTH, cl); }

            @Override void validate(final Field field) {
                super.validate(field);

                final Field dom = field.getField(DAY_OF_MONTH);

                /* If both DoM and DoW has '?' assume '*' for DoW */
                if (field.noSpec && dom.noSpec && !dom.last) field.noSpec = false;

                /* If DoW has '*' and DoM has value assume '?' for DoW */
                else if (field.all && !dom.noSpec) field.noSpec = true;

                /* If DoW has value and DoM has '*' assume '?' for DoM */
                else if (dom.all && !field.noSpec) dom.noSpec = true;

                /* If both have value it is an error */
                else if (!field.noSpec && !dom.noSpec) throw conflictingSpec();
            }},

        YEAR(MIN_YEAR, MAX_YEAR, Calendar.YEAR) {
            @Override int nextValue(final Field field, final Calendar cl, final int v) {
                final Integer nxt = field.values.ceiling(v);
                return nxt != null ? nxt : TOP_YEAR + 2;
            }};

        protected final int calendarField;
        protected final int end;
        protected final int start;

        FieldType(final int start, final int end, final int calendarField) {
            this.start         = start;
            this.end           = end;
            this.calendarField = calendarField;
        }

        public void lastDowOfMonth(final Field field, final int i) {
            throw Exception.invalidValue(this, i + "L");
        }

        public void nearestWeekday(final Field field, final int day) {
            throw Exception.invalidValue(this, day + "W");
        }

        public int parseName(final String name) {
            throw Exception.invalidValue(this, name);
        }

        protected boolean doAdjust(final Field field, final FieldType type, final Calendar cl) {
            final int prev = type.get(cl);
            final int curr = nextValue(field, cl, prev);
            type.set(cl, curr);
            if (curr == prev) return true;
            if (curr < prev) type.incrementNext(cl);
            FieldType f = type.prev();
            if (f == null) return true;
            do {
                f.set(cl, cl.getMinimum(f.calendarField));
                f = f.prev();
            }
            while (f != null);
            return false;
        }

        boolean adjust(final Field field, final Calendar cl) {
            return doAdjust(field, this, cl);
        }

        void dayOfWeekNth(final Field field, final int dow, final int nth) {
            throw Exception.invalidValue(this, dow + "#" + nth);
        }

        final String get(String[] str) {
            final int n = ordinal();
            return n < str.length ? str[n] : "*";
        }
        final int get(final Calendar cl) {
            final int v = cl.get(calendarField);
            return this == MONTH ? v + 1 : v;
        }

        void last(final Field field, final int offset, final String w) {
            if (w != null) throw Exception.invalidValue(this, w);
            field.values.add(end - offset);
        }

        int nextValue(final Field field, final Calendar cl, final int v) {
            return field.next(v);
        }

        void noSpec(final Field field) {
            throw Exception.invalidValue(this, "?");
        }
        @Nullable final FieldType prev() {
            final int i = ordinal() - 1;
            return i < 0 ? null : values()[i];
        }
        final void set(final Calendar cl, final int curr) {
            cl.set(calendarField, this == MONTH ? curr - 1 : curr);
        }

        void validate(final Field field) {
            if (field.last && field.values.size() > 1) throw lastAndValue(this);
        }

        private void incrementNext(Calendar cl) {
            if (this != YEAR) {
                final FieldType nextField = this == MONTH ? YEAR : values()[ordinal() + 1];
                cl.add(nextField.calendarField, 1);
            }
        }
    }

    //J-
    private static final String VAL = "(\\d+|[a-zA-Z]{3})";
    private static final String OFFSET = "/([1-9]\\d*)";
    private static final String OPT_OFFSET = "(" + OFFSET + ")?";
    //J+

    enum Format {
        NO_SPEC("\\?"),                       //
        ALL("\\*" + OPT_OFFSET),              //
        VALUE(VAL),                           //
        RANGE(VAL + "-" + VAL + OPT_OFFSET),  //
        STEP(VAL + OFFSET),                   //
        WEEKDAY_NTH(VAL + "#([1-5])"),        //
        LAST_DOW_OF_MONTH(VAL + "[Ll]"),      //
        NEAREST_WEEKDAY("(\\d+)W"),           //
        LAST("[Ll](-(\\d+))?([Ww])?"),        //
        ;

        private final Pattern pattern;

        Format(final String regex) {
            pattern = Pattern.compile(regex);
        }

        public Matcher matcher(final String str) {
            return pattern.matcher(str);
        }
    }

    //~ Inner Classes ................................................................................................................................

    public static class Exception extends RuntimeException {
        private Exception(final String message) {
            super(message);
        }

        static Exception conflictingSpec() {
            return new Exception(format("Cannot specify a value for '%s' and '%s'", FieldType.DAY_OF_MONTH, FieldType.DAY_OF_WEEK));
        }

        static Exception invalidValue(FieldType field, String value) {
            return new Exception(format("Invalid value '%s' for field '%s'", value, field));
        }
        static Exception lastAndValue(final FieldType field) {
            return new Exception(format("Cannot specify 'L' and other values for '%s'", field));
        }

        static Exception multipleNth() {
            return new Exception(format("Cannot specifying multiple 'nth' days for '%s'.", FieldType.DAY_OF_MONTH));
        }

        static Exception valueOutOfRange(final FieldType field, final int inc) {
            return new Exception(format("Value '%d' out of range for field '%s'", inc, field));
        }

        private static final long serialVersionUID = 6293406828226410460L;
    }

    class Field {
        public boolean                 all;
        private boolean                last;
        private int                    lastOffset;
        private boolean                noSpec;
        private final FieldType        type;
        private final TreeSet<Integer> values;
        private boolean                weekday;
        private int                    weekDayNth;

        Field(final FieldType type) {
            this.type = type;
            last      = false;
            values    = new TreeSet<>();
        }

        /** Include all values. */
        public boolean includeAll() {
            return all;
        }

        @Override public String toString() {
            return noSpec ? "?" : all ? "*" : values.toString();
        }

        public Field getField(final FieldType t) {
            return fields.get(t);
        }

        /** Return the values considered. */
        public ImmutableSet<Integer> getValues() {
            return immutable(values);
        }

        void addAll() {
            addAll(1);
        }

        void addAll(int inc) {
            if (inc == 1) all = true;
            else {
                for (int i = type.start; i <= type.end; i += inc)
                    values.add(i);
            }
        }  // end method addAll

        /** Adjust the value to the first valid (wrapping). */
        int next(int value) {
            final Integer v = values.ceiling(value);
            return v != null ? v : values.first();
        }

        void parse(final String v) {
            final int len = v.length();
            if (len == 0) return;
            for (final Format f : Format.values()) {
                final Matcher m = f.matcher(v);
                if (m.matches()) {
                    parse(f, m);
                    return;
                }
            }
            throw Exception.invalidValue(type, v);
        }  // end method parse

        final TreeSet<Integer> values() {
            return values;
        }

        final TreeSet<Integer> valuesIncludingAll() {
            if (!all) return values;
            final TreeSet<Integer> result = new TreeSet<>();
            for (int i = type.start; i <= type.end; i++)
                result.add(i);
            return result;
        }

        private void addRange(final int from, final int to, final int step) {
            if (step == 1 && from == type.start && to == type.end) all = true;
            else if (from <= to) for (int i = from; i <= to; i += step)
                values.add(i);
            else {
                addRange(from, type.end, step);
                addRange(type.start, to, step);
            }
        }  // end method addRange

        /**
         * Adjust the calendar to meet the specified field request If the adjustment implies a
         * reprocessing of previous fields return false.
         */
        private boolean adjust(final Calendar cl) {
            return all || noSpec || type.adjust(this, cl);
        }

        private int adjustToWeekday(final Calendar cl, final int day) {
            final Calendar c = (Calendar) cl.clone();
            c.set(Calendar.DAY_OF_MONTH, day);

            final int dow = c.get(Calendar.DAY_OF_WEEK);

            if (dow == Calendar.SATURDAY) return day == 1 ? 3 : day - 1;
            if (dow == Calendar.SUNDAY) {
                final int lastDay = cl.getActualMaximum(Calendar.DAY_OF_MONTH);
                return lastDay == day ? day - 2 : day + 1;
            }
            return day;
        }

        private void parse(final Format f, final Matcher m) {
            switch (f) {
            case NO_SPEC:
                type.noSpec(this);
                break;
            case ALL:
                addAll(parseOffset(m, 2));
                break;
            case LAST:
                type.last(this, m.group(2) == null ? 0 : parseOffset(m, 2), m.group(3));
                break;
            case VALUE:
                values.add(parseValue(m, 1));
                break;
            case STEP:
                addRange(parseValue(m, 1), type.end, parseOffset(m, 2));
                break;
            case RANGE:
                addRange(parseValue(m, 1), parseValue(m, 2), parseOffset(m, 4));
                break;
            case WEEKDAY_NTH:
                type.dayOfWeekNth(this, parseValue(m, 1), parseInt(m.group(2)));
                break;
            case NEAREST_WEEKDAY:
                type.nearestWeekday(this, parseValue(m, 1));
                break;
            case LAST_DOW_OF_MONTH:
                type.lastDowOfMonth(this, parseValue(m, 1));
                break;
            }
            type.validate(this);
        }

        private int parseOffset(Matcher m, int group) {
            final String v     = m.group(group);
            final int    value = v == null ? 1 : parseInt(v);
            if (value < 1 || value > type.end - type.start) throw Exception.valueOutOfRange(type, value);
            return value;
        }

        private int parseValue(final Matcher m, final int group) {
            final String str   = m.group(group);
            final int    value = isDigit(str.charAt(0)) ? parseInt(str) : type.parseName(str);
            if (value < type.start || value > type.end) throw Exception.valueOutOfRange(type, value);
            return value;
        }
    }  // end class Field
}  // end class CronExpression
