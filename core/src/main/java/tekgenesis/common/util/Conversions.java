
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.math.BigDecimal;
import java.util.*;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.collections.Seq;
import tekgenesis.common.core.DateOnly;
import tekgenesis.common.core.DateTime;
import tekgenesis.common.core.Enumeration;
import tekgenesis.common.core.Times;
import tekgenesis.common.core.enumeration.Enumerations;

import static java.text.DateFormat.DEFAULT;
import static java.text.DateFormat.SHORT;

import static tekgenesis.common.Predefined.*;
import static tekgenesis.common.core.Constants.VALUE_OF;
import static tekgenesis.common.core.Times.isoDate;
import static tekgenesis.common.core.Times.isoDateTime;
import static tekgenesis.common.util.Reflection.findMethod;
import static tekgenesis.common.util.Reflection.invokeStatic;

/**
 * Common conversions.
 */
@SuppressWarnings("WeakerAccess")
public class Conversions {

    //~ Constructors .................................................................................................................................

    private Conversions() {}

    //~ Methods ......................................................................................................................................

    /** Converts a date time to a string. */
    public static String dateTimeToString(@Nullable Date date) {
        return date == null ? "" : isoDateTime(date.getTime());
    }

    /** Converts a date only to a string. */
    public static String dateToString(@Nullable Date date) {
        return date == null ? "" : isoDate(date.getTime());
    }

    /** Format a specific type object to an String. */
    @NotNull public static String format(@Nullable Object value) {
        if (value == null) return "";
        if (value instanceof Enumeration) return ((Enumeration<?, ?>) value).label();
        if (value instanceof DateTime) return ((DateTime) value).format(DEFAULT, SHORT);
        if (value instanceof DateOnly) return (((DateOnly) value).format(DEFAULT));
        return toString(value);
    }

    /** Builds an immutable list of the format of the given Object. */
    @NotNull public static ImmutableList<String> formatList(Object obj) {
        return obj instanceof Seq ? formatList((Seq<?>) obj) : ImmutableList.of(format(obj));
    }

    /** Builds an immutable list of the format of the given Seq. */
    @NotNull public static ImmutableList<String> formatList(Seq<?> objs) {
        return objs.flatMap(Conversions::formatList).toList();
    }

    /**
     * Builds an immutable list of the String.valueOf of the given Objects, or their label if an
     * Enumeration is found but putting empty Strings if they are null.
     */
    @NotNull public static ImmutableList<String> formatList(Object first, Object... rest) {
        final ImmutableList.Builder<String> result = ImmutableList.builder(rest.length + 1);
        result.addAll(formatList(first));
        for (final Object o : rest)
            result.addAll(formatList(o));
        return result.build();
    }

    /** Try to convert a String to a value of the specified type. */
    @Contract("!null, _ -> !null")
    @Nullable public static <T> T fromString(@Nullable String value, @NotNull Class<T> targetType) {
        if (value == null) return null;
        return cast(fromStr(value, targetType));
    }

    /** Convert numbers between them. */
    @Nullable public static <T extends Number> T numberTo(@Nullable Number value, @NotNull Class<T> requiredType) {
        if (value == null) return null;
        if (requiredType.isInstance(value)) return cast(value);

        if (requiredType.isAssignableFrom(BigDecimal.class))
            return cast(floats.contains(value.getClass()) ? BigDecimal.valueOf(value.doubleValue()) : BigDecimal.valueOf(value.longValue()));

        if (integers.contains(requiredType)) {
            if (requiredType == byte.class || requiredType == Byte.class) return cast(value.byteValue());
            if (requiredType == short.class || requiredType == Short.class) return cast(value.shortValue());
            if (requiredType == int.class || requiredType == Integer.class) return cast(value.intValue());
            return cast(value.longValue());
        }

        if (floats.contains(requiredType)) {
            if (requiredType == float.class || requiredType == Float.class) return cast(value.floatValue());
            return cast(value.doubleValue());
        }
        throw getConversionException(value, requiredType);
    }

    /** Converts to a Boolean. */
    public static boolean toBoolean(@Nullable String str) {
        return Boolean.parseBoolean(str);
    }

    /** Convert to a Date parsing the Date and time part. */
    public static Date toDateTime(@Nullable String str) {
        return new Date(Times.parseDateTime(str));
    }

    /** Converts to a BigDecimal. */
    public static BigDecimal toDecimal(@Nullable String str) {
        return str == null || str.isEmpty() ? BigDecimal.ZERO : new BigDecimal(str.trim());
    }
    /** Converts to a BigDecimal with an specified number of decimals. */
    public static BigDecimal toDecimal(@Nullable String str, int decimals) {
        return (str == null || str.isEmpty() ? BigDecimal.ZERO : new BigDecimal(str.trim())).setScale(decimals, BigDecimal.ROUND_UNNECESSARY);
    }

    /** Converts to a double. */
    public static double toDouble(@Nullable String str) {
        return str == null || str.isEmpty() ? 0 : Double.parseDouble(str.trim());
    }

    /** Converts to an int. */
    public static int toInt(@Nullable String str) {
        return str == null || str.isEmpty() ? 0 : Integer.parseInt(str.trim());
    }
    /** Converts to a long. */
    public static long toLong(@Nullable String str) {
        return str == null || str.isEmpty() ? 0 : Long.parseLong(str.trim());
    }

    /** Convert specific type objects to String. See {@link #fromString(String, Class)} */
    @Nullable public static Seq<String> toString(@NotNull Seq<?> values) {
        return values.map(Conversions::toString);
    }

    /** Convert a specific type object to String. See {@link #fromString(String, Class)} */
    @Contract("!null -> !null")
    @Nullable public static String toString(@Nullable Object value) {
        if (value == null) return null;
        if (value instanceof Double) {  // Hack to format 2.0 as 2
            final Double d = (Double) value;
            if (d == Math.rint(d)) return ((Integer) d.intValue()).toString();
        }
        return value.toString();
    }

    /** Return the conversion method for the specified Type. */
    public static String getConversionFor(String t) {
        return notNull(conversions.get(t));
    }

    /** Returns true if objects of the class can be converted from an String. */
    public static boolean isConvertible(Class<?> c) {
        return convertibles.contains(c) || c.isEnum();
    }

    private static void add(Class<?> type, String conversion) {
        conversions.put(type.getName(), conversion);
        conversions.put(type.getSimpleName(), conversion);
    }

    private static Object fromStr(String value, Class<?> targetType) {
        if (targetType == String.class) return value;
        if (targetType == boolean.class || targetType == Boolean.class) return toBoolean(value);
        if (targetType == int.class || targetType == Integer.class || targetType == short.class || targetType == Short.class) return toInt(value);
        if (targetType == long.class || targetType == Long.class) return toLong(value);
        if (targetType == double.class || targetType == Double.class) return toDouble(value);
        if (targetType == BigDecimal.class) return toDecimal(value);
        if (targetType == Date.class) return new Date(Times.parseDate(value));
        if (targetType == DateTime.class) return DateTime.fromString(value);
        if (targetType == DateOnly.class) return DateOnly.fromString(value);

        if (targetType.isEnum()) return Enumerations.valueOf(Enumerations.asEnumClass(targetType).getName(), value);

        return findMethod(targetType, VALUE_OF, String.class)  //
               .map(m -> ensureNotNull(invokeStatic(m, value)))  //
               .orElseThrow(() -> getConversionException(value, targetType));
    }

    private static IllegalArgumentException getConversionException(Object value, Class<?> targetType) {
        return new IllegalArgumentException("Cannot create a '" + targetType + "' from '" + value + "'");
    }

    //~ Static Fields ................................................................................................................................

    private static final Map<String, String> conversions = new HashMap<>();

    @NonNls private static final String TO_INT     = "toInt";
    @NonNls private static final String TO_LONG    = "toLong";
    @NonNls private static final String TO_DOUBLE  = "toDouble";
    @NonNls private static final String TO_BOOLEAN = "toBoolean";
    @NonNls private static final String TO_DECIMAL = "toDecimal";
    @NonNls private static final String TO_DATE    = "toDate";

    static {
        add(Integer.TYPE, TO_INT);
        add(Integer.class, TO_INT);

        add(Long.TYPE, TO_LONG);
        add(Long.class, TO_LONG);

        add(Double.TYPE, TO_DOUBLE);
        add(Double.class, TO_DOUBLE);

        add(Boolean.TYPE, TO_BOOLEAN);
        add(Boolean.class, TO_BOOLEAN);

        add(BigDecimal.class, TO_DECIMAL);
        add(Date.class, TO_DATE);
    }

    private static final Set<Class<?>> integers = new HashSet<>();

    static {
        integers.add(Byte.class);
        integers.add(Integer.class);
        integers.add(Short.class);
        integers.add(Long.class);
        integers.add(byte.class);
        integers.add(int.class);
        integers.add(short.class);
        integers.add(long.class);
    }

    private static final Set<Class<?>> floats = new HashSet<>();

    static {
        floats.add(Float.class);
        floats.add(Double.class);
        floats.add(float.class);
        floats.add(double.class);
    }

    private static final Set<Class<?>> convertibles = new HashSet<>();

    static {
        convertibles.addAll(integers);
        convertibles.addAll(floats);
        convertibles.add(Boolean.class);
        convertibles.add(Date.class);
        convertibles.add(DateTime.class);
        convertibles.add(DateOnly.class);
    }
}  // end class Conversions
