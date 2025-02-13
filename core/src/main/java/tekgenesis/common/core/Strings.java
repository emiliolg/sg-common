
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.function.Function;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.annotation.GwtIncompatible;
import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.collections.Seq;
import tekgenesis.common.collections.Traversable;
import tekgenesis.common.util.GwtReplaceable;

import static java.lang.Character.isLetterOrDigit;
import static java.lang.Character.isWhitespace;
import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;
import static java.util.Arrays.asList;

import static tekgenesis.common.Characters.isAscii;
import static tekgenesis.common.Characters.isLatin;
import static tekgenesis.common.Characters.toAscii;
import static tekgenesis.common.Predefined.notNull;
import static tekgenesis.common.collections.Colls.emptyList;
import static tekgenesis.common.collections.Colls.listOf;
import static tekgenesis.common.collections.Colls.map;
import static tekgenesis.common.core.Tuple.tuple;

/**
 * Common utility methods to deal with Strings.
 */
@SuppressWarnings({ "ClassWithTooManyMethods", "OverlyComplexClass" })
public class Strings {

    //~ Constructors .................................................................................................................................

    private Strings() {}

    //~ Methods ......................................................................................................................................

    /** Return the collection of Strings interpreted as a set of lines. */
    public static String asLines(String[] lines) {
        return asLines(asList(lines));
    }

    /** Return the collection of Strings interpreted as a set of lines. */
    public static String asLines(Iterable<String> lines) {
        final StringBuilder result = new StringBuilder();
        for (final String s : lines)
            result.append(s).append("\n");
        return result.toString();
    }

    /** Capitalize the first character of a String. */
    @NotNull public static String capitalizeFirst(@NotNull String name) {
        return name.isEmpty() ? name : toUpperCase(name.charAt(0)) + name.substring(1);
    }

    /** Returns true if given string contains a whitespace character. */
    public static boolean containsWhiteSpace(@NotNull final String text) {
        for (int i = 0; i < text.length(); i++) {
            if (isWhitespace(text.charAt(i))) return true;
        }
        return false;
    }

    /** Returns the number of occurrences of a given char into an {@link String}. */
    @SuppressWarnings("WeakerAccess")
    public static int count(@Nullable String str, char c) {
        final String s      = notNull(str);
        int          result = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) result++;
        }
        return result;
    }

    /** Adds cover delimiter char to given String. */
    public static String cover(String str) {
        return COVER_DELIMITER_CHAR + str + COVER_DELIMITER_CHAR;
    }

    /**
     * Given an string that may contain cover delimiting chars, returns an String with enclosing
     * text covered (hidden).
     */
    @NotNull public static String coverText(@Nullable final String str) {
        final String        text   = notNull(str);
        final StringBuilder result = new StringBuilder();
        boolean             read   = true;
        for (int i = 0; i < text.length(); i++) {
            final char current = text.charAt(i);
            if (current != COVER_DELIMITER_CHAR && read) result.append(current);
            else if (current == COVER_DELIMITER_CHAR) read = !read;
        }
        return result.toString();
    }

    /** De Capitalize the first character of a String. */
    @NotNull public static String deCapitalizeFirst(@NotNull String name) {
        return name.isEmpty() ? name : Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    /** Parse a String with escape sequences and (Optionally) enclosed in quotes THe. */
    @SuppressWarnings("NonJREEmulationClassesInClientCode")  // C'mon is not that complex! :-)
    public static String decode(String str) {
        int length = str.length();
        if (length == 0) return str;
        int i = 0;
        if (str.charAt(0) == '\"') {
            if (str.startsWith(Q3) && str.endsWith(Q3) && str.length() >= 6) {
                i      =  3;
                length -= 3;
            }
            else {
                i = 1;
                length--;
            }
        }

        final StringBuilder buffer = new StringBuilder(length);
        while (i < length) {
            char chr = str.charAt(i++);
            if (chr == '\\' && i < length) {
                chr = str.charAt(i++);
                switch (chr) {
                case 'u':
                    chr =  decodeUnicodeChar(str.substring(i, i + 4));
                    i   += 4;
                    break;
                case 't':
                    chr = '\t';
                    break;
                case 'r':
                    chr = '\r';
                    break;
                case 'n':
                    chr = '\n';
                    break;
                case 'f':
                    chr = '\f';
                    break;
                }
            }
            buffer.append(chr);
        }
        return buffer.toString();
    }  // end method decode

    /** Encode a character using an escape sequence. */
    @SuppressWarnings("MethodWithMultipleReturnPoints")
    public static String encodeChar(char c) {
        // If is and ISO, no control character return it unchanged
        if (c <= MAX_ISO && !GwtReplaceable.isISOControl(c)) return String.valueOf(c);
        switch (c) {
        case '\n':
            return "\\n";
        case '\t':
            return "\\t";
        case '\r':
            return "\\r";
        case '\f':
            return "\\f";
        default:
            final String s = Integer.toHexString(c);
            return "\\u" + nChars(' ', 4 - s.length()) + s;
        }
    }

    /** Escape a given char in an String. */
    @NotNull public static String escapeCharOn(@NotNull String string, char charToEscape) {
        final int first = string.indexOf(charToEscape);
        if (first == -1) return string;
        final StringBuilder result = new StringBuilder(string.substring(0, first));

        for (int i = first; i < string.length(); i++) {
            final char c = string.charAt(i);
            if (c == charToEscape) result.append('\\');
            result.append(c);
        }
        return result.toString();
    }
    /** Extract digits from an String. */
    @NotNull public static String extractDigits(@Nullable String str, int limit) {
        if (str == null) return "";
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length() && result.length() < limit; i++) {
            final char ch = str.charAt(i);
            if (Character.isDigit(ch)) result.append(ch);
        }
        return result.toString();
    }

    /**
     * Returns a tuple(beginIndex, finishIndex) to be used with a substring over str to find query.
     */
    @Nullable public static IntIntTuple findSubstring(@NotNull final String str, @NotNull final String query) {
        int begin  = -1;
        int finish = 0;

        int i = 0;
        while (i < str.length() && finish < query.length()) {
            final char chs = toLowerCase(toAscii(str.charAt(i)));
            final char chq = toLowerCase(toAscii(query.charAt(finish)));
            if (isLetterDigitOrSpace(chs) && isLetterDigitOrSpace(chq)) {
                if (chs == chq) {
                    if (finish == query.length() - 1 && finish < i) finish = i;
                    else if (finish == query.length() - 1 && finish > i) break;
                    else if (begin == -1) begin = i;
                    finish++;
                    i++;
                }
                else {
                    if (begin == -1) i++;
                    else begin = -1;
                    finish = 0;
                }
            }

            if (!isLetterDigitOrSpace(chs)) i++;
            if (!isLetterDigitOrSpace(chq)) finish++;
        }

        return begin != -1 && begin < finish ? tuple(begin, finish) : null;
    }

    /**
     * From a String with Camel Case format returns a String with underscores.
     *
     * <pre>
     For example : fromCamelCase("DateTime") -> DATE_TIME
     For example : fromCamelCase("fromURL") -> FROM_URL
     * </pre>
     */
    public static String fromCamelCase(@NotNull String camelCaseString) {
        /// Check that I'm not passing an underscored string
        if (camelCaseString.indexOf('_') != -1) return camelCaseString.toUpperCase();

        boolean             inWord = false;
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelCaseString.length(); i++) {
            char c = camelCaseString.charAt(i);
            if (!Character.isUpperCase(c)) {
                c      = toUpperCase(c);
                inWord = true;
            }
            else if (inWord) {
                result.append('_');
                inWord = false;
            }

            result.append(c);
        }
        return result.toString();
    }

    /** Transforms name into a getter name. */
    public static String getterName(@NotNull final String name) {
        return getterName(name, "");
    }

    /** Generate getter name. */
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public static String getterName(@NotNull final String name, @NotNull final String type) {
        return (type.equalsIgnoreCase(Constants.BOOLEAN) || type.equals(Boolean.class.getName()) ? "is" : "get") + capitalizeFirst(name);
    }

    /** Generate getter name. */
    public static String getterName(@NotNull final String name, final boolean isBoolean) {
        return getterName(name, isBoolean ? Boolean.class.getName() : "");
    }

    /**
     * Create an String from an Iterable. Separated by the specified character escaping it. It is
     * the symmetrical of {@link #splitNotEscaped}
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    public static String join(@Nullable Iterable<String> elements, char sep) {
        if (elements == null) return "";

        final StrBuilder result = new StrBuilder();
        for (final Object e : elements)
            result.appendEscapedElement(e == null ? Constants.NULL_TO_STRING : e, sep);
        return result.toString();
    }

    /** Split an String into lines. */
    @NotNull public static List<String> lines(@Nullable String str) {
        return split(str, '\n');
    }

    /** return the max length of a set of strings. */
    public static int maxLength(@Nullable Iterable<String> strings) {
        return maxLength(strings, Integer.MAX_VALUE);
    }

    /**
     * Returns the max length of a set of strings. Discarding the strings longer than the specified
     * cutoff argument
     */

    public static int maxLength(@Nullable Iterable<String> strings, int cutoff) {
        int maxLength = 0;
        if (strings != null) {
            for (final String s : strings) {
                final int length = s.length();
                if (length <= cutoff) maxLength = Math.max(maxLength, length);
            }
        }
        return maxLength;
    }

    /**
     * Create an md5 hash code of the String and return it as an hexadecimal digit truncated to the
     * specified length.
     */
    @SuppressWarnings("WeakerAccess")
    public static String md5(String str, int length) {
        try {
            final MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes());
            final BigInteger bigInt = new BigInteger(1, md5.digest());
            final String     hash   = bigInt.toString(HEX_RADIX);

            return length < hash.length() ? hash.substring(0, length) : nChars('0', length - hash.length()) + hash;
        }
        catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /** Returns an String composed by <code>n</code> repetitions of the specified character. */
    @NotNull public static String nChars(char c, int n) {
        if (n < 0) throw new IllegalArgumentException("n = " + n);
        final StringBuilder b = new StringBuilder(n);
        for (int i = 0; i < n; i++)
            b.append(c);
        return b.toString();
    }

    /** If the String is null or empty returns Option.empty() else returns Option.of(value). */
    public static Option<String> nonEmpty(@Nullable String value) {
        return value == null || value.isEmpty() ? Option.empty() : Option.of(value);
    }

    /** Converts to a String an Object not preserving nulls. */
    @NotNull public static String notNullValueOf(@Nullable Object value) {
        return notNull(valueOf(value));
    }

    /**
     * try to parse an String to an Integer. If the format is wrong returns the specified
     * defaultValue
     */
    public static int parseAsInt(String s, int defaultValue) {
        try {
            return Integer.parseInt(s);
        }
        catch (final NumberFormatException e) {
            return defaultValue;
        }
    }

    /** From a String returns its plural. */
    @SuppressWarnings({ "NonJREEmulationClassesInClientCode", "DuplicateStringLiteralInspection" })
    public static String pluralize(@NotNull String str) {
        if (str.endsWith("Child") || str.endsWith("child")) return str + "ren";

        if ("this".equals(str)) return "these";
        if ("This".equals(str)) return "These";

        if (endsWithIgnoreCase(str, "s") || endsWithIgnoreCase(str, "x") || endsWithIgnoreCase(str, "ch")) return str + "es";

        final int len = str.length();
        if (endsWithIgnoreCase(str, "y") && len > 1 && !isVowel(Character.toLowerCase(str.charAt(len - 2)))) return str.substring(0, len - 1) + "ies";

        return str + "s";
    }

    /** Builds a "quoted" string, escaping inner quotes if necessary. */
    public static String quoted(@Nullable final String str) {
        // noinspection NonJREEmulationClassesInClientCode
        return str == null ? "\"\"" : quoted(str, '\\');
    }

    /**
     * Builds a "quoted" string, escaping inner quotes if necessary. The escape char is explicitly
     * defined (Usual values are '\\' and '"')
     */
    public static String quoted(@NotNull final String str, char escapeChar) {
        return quoted('\"', str, escapeChar);
    }

    /**
     * Builds a "quoted" string, with speficied quote char, escaping inner quotes if necessary. The
     * escape char is explicitly defined (Usual values are '\\' and '"')
     */
    public static String quoted(char quoteChar, @NotNull final String str, char escapeChar) {
        final int           len    = str.length();
        final StringBuilder result = new StringBuilder(len + 2);
        result.append(quoteChar);
        for (int i = 0; i < len; i++) {
            final char c = str.charAt(i);
            if (c == quoteChar) result.append(escapeChar);
            result.append(c);
        }
        result.append(quoteChar);
        return result.toString();
    }

    /** Replace last occurrence of substring in string. */
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public static String replaceLast(@NotNull String string, @NotNull String substring, @NotNull String replacement) {
        final int index = string.lastIndexOf(substring);
        if (index == -1) return string;
        return string.substring(0, index) + replacement + string.substring(index + substring.length());
    }

    /** Generate setter name. */
    public static String setterName(final String name) {
        return "set" + capitalizeFirst(name);
    }

    /**
     * Builds a 'quoted' string (with single quotation marks), escaping inner quotes if necessary.
     */
    public static String singleQuoted(@NotNull final String str) {
        return quoted('\'', str, '\\');
    }

    /** From a String returns its singular. */
    @SuppressWarnings({ "NonJREEmulationClassesInClientCode", "DuplicateStringLiteralInspection" })
    public static String singularize(@NotNull String str) {
        if ("these".equals(str)) return "this";
        if ("These".equals(str)) return "This";

        final int len = str.length();
        if (str.endsWith("Children") || str.endsWith("children")) return str.substring(0, len - 3);
        if (endsWithIgnoreCase(str, "ies")) return str.substring(0, len - 3) + "y";
        if (endsWithIgnoreCase(str, "es")) return str.substring(0, len - 2);
        if (endsWithIgnoreCase(str, "s")) return str.substring(0, len - 1);

        return str;
    }
    /** Returns an String composed by <code>n</code> spaces. */
    @NotNull public static String spaces(int n) {
        return nChars(' ', n);
    }

    /** Split an String into chunks separated by the specified character. */
    @NotNull public static ImmutableList<String> split(@Nullable String str, char c) {
        final String s = notNull(str);

        if (s.isEmpty()) return emptyList();

        final int n = count(s, c) + 1;
        if (n == 1) return listOf(s);

        final ImmutableList.Builder<String> result = ImmutableList.builder(n);

        int prev = 0;
        for (int i = 0; i < s.length(); i++) {
            final char c1 = s.charAt(i);
            if (c1 == c) {
                result.add(s.substring(prev, i));
                prev = i + 1;
            }
        }
        result.add(s.substring(prev));
        return result.build();
    }

    /**
     * Split an String into chunks separated by not escaped occurrences of the specified character.
     */
    @NotNull
    @SuppressWarnings("WeakerAccess")
    public static ImmutableList<String> splitNotEscaped(@Nullable String str, char c) {
        final ImmutableList<String> original = split(str, c);
        for (final String s : original) {
            if (!s.isEmpty() && s.charAt(s.length() - 1) == '\\') {
                // Must consolidate;
                final int                           n    = original.size();
                final ImmutableList.Builder<String> a    = ImmutableList.builder(n - 1);
                String                              curr = "";
                for (final String s1 : original) {
                    final int len = s1.length() - 1;
                    if (len >= 0 && s1.charAt(len) == '\\') curr += s1.substring(0, len) + c;
                    else {
                        a.add(curr + s1);
                        curr = "";
                    }
                }
                return a.build();
            }
        }
        return original;
    }  // end method splitNotEscaped

    /**
     * Split an String into chunks separated by not escaped occurrences of the specified character.
     * Returns an array of the specified size Only sep of length 1 is supported
     */
    @NotNull public static String[] splitToArray(@Nullable String string, int size) {
        return splitToArray(string, "", ":", "", size);
    }
    /**
     * Split an String into chunks separated by not escaped occurrences of the specified character.
     * Remove the start and end strings if present Returns an array of the specified size Only sep
     * of length 1 is supported
     */
    @NotNull
    @SuppressWarnings({ "NonJREEmulationClassesInClientCode", "WeakerAccess" })
    public static String[] splitToArray(@Nullable String string, String start, String sep, String end, int size) {
        String str = string == null ? "" : string.startsWith(start) ? string.substring(start.length()) : string;
        if (str.endsWith(end)) str = str.substring(0, str.length() - end.length());
        final List<String> strings = splitNotEscaped(str, sep.charAt(0));
        final String[]     result  = new String[size];
        int                i       = 0;
        for (final String s : strings) {
            if (i < size) result[i++] = s;
        }
        while (i < size)
            result[i++] = "";
        return result;
    }

    /** Splits an String based on a delimiter to a tuple with 2 values. */
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public static Tuple<String, String> splitToTuple(final String str, final String delimiter) {
        final int pos = str.indexOf(delimiter);
        return pos == -1 ? tuple(str, "") : tuple(str.substring(0, pos), str.substring(pos + 1));
    }

    /** Strip accents and other diacritical marks. */
    public static String stripAccents(String s) {
        final int length = s.length();
        int       i      = 0;
        do {
            if (i == length) return s;
        }
        while (isAscii(s.charAt(i++)));

        final StringBuilder result = new StringBuilder(s.substring(0, --i));
        while (i < length) {
            final char c = s.charAt(i++);
            if (isAscii(c)) result.append(c);
            else if (isLatin(c)) result.append(toAscii(c));
        }
        return result.toString();
    }

    /**
     * Returns a String with Camel Case format based on an String with underscores.
     *
     * <pre>
     For example :
     toCamelCase("DATE_TIME") -> DateTime
     toCamelCase("check_box") -> checkBox
     * </pre>
     */
    public static String toCamelCase(@NotNull String underscoredString) {
        return fromUnderscoredString(underscoredString, false);
    }

    /** Return iterable of objects as iterable of strings. */
    public static <T> Iterable<String> toStringIterable(Iterable<T> objects) {
        return map(objects, String::valueOf);
    }

    /**
     * From a String with underscores to a list of words.
     *
     * <pre>
     For example : toWords("DATE_TIME") -> Date Time
     For example : toWords("FROM_URL")  -> From Url
     * </pre>
     */
    public static String toWords(@NotNull String underscoreString) {
        return fromUnderscoredString(underscoreString, true);
    }

    /** Traverse an String. */
    @NotNull public static Traversable<Character> traverse(String str) {
        if (str == null || str.isEmpty()) return emptyList();
        return new Traversable<Character>() {
            @Override public <R> Option<R> forEachReturning(@NotNull Function<? super Character, StepResult<R>> step, Option<R> finalValue) {
                final int l = str.length();
                for (int i = 0; i < l; i++) {
                    final StepResult<R> r = step.apply(str.charAt(i));
                    if (r.isDone()) return r.getValue();
                }
                return finalValue;
            }
        };
    }

    /** Truncate String to given length . */
    @Contract("!null,_ -> !null; null,_ -> null")
    @Nullable public static String truncate(@Nullable String str, int limit) {
        return str == null ? null : str.length() < limit ? str : str.substring(0, limit);
    }

    /** Truncate Strings to a given length . */
    @Nullable public static List<String> truncate(@Nullable List<String> values, final int limit) {
        if (values == null) return null;
        if (values instanceof Seq)  // noinspection unchecked
            return truncate((Seq<String>) values, limit).toList();
        for (int i = 0; i < values.size(); i++)
            values.set(i, truncate(values.get(i), limit));
        return values;
    }

    /** Truncate Strings to a given length . */
    public static Seq<String> truncate(@NotNull Seq<String> values, final int limit) {
        return values.map(s -> truncate(s, limit));
    }

    /** Truncate an String to a given length adding a hash to avoid duplications. */
    @NotNull public static String truncate(@NotNull String str, String sep, int limit) {
        final int length = str.length();
        if (length <= limit) return str;
        final String hash = sep + Integer.toHexString(str.hashCode()).toUpperCase();
        return str.substring(0, limit - hash.length()) + hash;
    }
    /**
     * Truncate an String to a given length adding a hash to avoid duplications. It appends the
     * suffix at the ends, separated by the specified char and try to keep it Try to keep the last
     * part of the string determined by using the specified suffix as a separator. If the length of
     * the residual prefix is less than 4 truncate the suffix
     */
    @NotNull public static String truncate(@NotNull String str, String suffix, String sep, int limit) {
        final String full = str + sep + suffix;
        if (full.length() <= limit) return full;
        final String hash = md5(full, 6);

        final int prefix = Math.max(limit - (hash.length() + suffix.length() + 2 * sep.length()), 4);
        return (str.substring(0, prefix) + sep + hash + sep + suffix).substring(0, limit);
    }

    /**
     * Removes bar and stars from a comment exactly like this one describing, leaving only the inner
     * text.
     */
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public static String unCommentText(@Nullable String text) {
        if (text == null || text.isEmpty()) return "";
        String    newText  = text.replaceFirst("/\\*+", "");
        final int endIndex = newText.lastIndexOf("*/");
        newText = endIndex > 0 ? newText.substring(0, endIndex) : "";
        return newText.replaceAll("\\*", "").trim();
    }

    /**
     * Removed the quotes from a quoted string.
     *
     * @param   str  String to be unquoted
     *
     * @return  unquoted string
     */
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public static String unquote(String str) {
        String result = str;
        if (str != null && ((str.startsWith("\"") && str.endsWith("\"")) || (str.startsWith("'") && str.endsWith("'"))))
            result = str.substring(1, str.length() - 1);
        return result;
    }

    /** Converts to a String an Object preserving nulls. */
    @Nullable public static String valueOf(@Nullable Object value) {
        if (value == null) return null;
        return value.toString();
    }

    /**
     * Returns a boolean, result from the comparison between the given string and a pattern of
     * hexadecimal color.
     */
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public static boolean verifyHexColor(@NotNull String color) {
        return color.matches(HEX_COLOR_PATTERN);
    }

    /** Returns true if an String contains only digits. */
    public static boolean isNumeric(String s) {
        final int length = s.length();
        if (length == 0) return false;
        for (int i = 0; i < length; i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }
    /** Returns true if there are not upper case characters in the String. */
    public static boolean isLowerCase(String word) {
        for (int i = 0; i < word.length(); i++) {
            if (Character.isUpperCase(word.charAt(i))) return false;
        }
        return true;
    }

    /** Returns true if there are not lower case characters in the String. */
    public static boolean isUpperCase(String word) {
        for (int i = 0; i < word.length(); i++) {
            if (Character.isLowerCase(word.charAt(i))) return false;
        }
        return true;
    }

    /** Returns true if the string is empty or all whitespaces. */
    @GwtIncompatible
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public static boolean isBlank(@Nullable String word) {
        if (word == null || word.isEmpty()) return true;

        for (int i = 0; i < word.length(); i++) {
            final char ch = word.charAt(i);
            if (!isWhitespace(ch) && ch != '\u00A0') return false;
        }
        return true;
    }
    /** Returns true if the string is not empty or all whitespaces. */
    @GwtIncompatible
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public static boolean isNotBlank(@Nullable String word) {
        return !isBlank(word);
    }

    private static char decodeUnicodeChar(String str) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            final int n = Character.digit(str.charAt(i), Constants.HEXADECIMAL_RADIX);
            if (n == -1) throw new IllegalArgumentException("Malformed unicode encoding: " + str);
            value = (value << 4) | n;
        }
        return (char) value;
    }

    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    private static boolean endsWithIgnoreCase(@NonNls @NotNull String str, @NonNls @NotNull String suffix) {
        final int stringLength = str.length();
        final int suffixLength = suffix.length();
        return stringLength >= suffixLength && str.regionMatches(true, stringLength - suffixLength, suffix, 0, suffixLength);
    }

    private static String fromUnderscoredString(String underscoredString, boolean whitespace) {
        // A variable that defines what to do with the next character: -1 = lowercase it, +1 = uppercase it, 0 = copy as is
        int                 action = 0;
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < underscoredString.length(); i++) {
            final char c = underscoredString.charAt(i);
            if (c == '_') {
                action = 1;
                if (whitespace) result.append(' ');
            }
            else {
                result.append(action == -1 ? Character.toLowerCase(c) : action == 1 ? Character.toUpperCase(c) : c);
                action = -1;
            }
        }
        return result.toString();
    }

    private static boolean isLetterDigitOrSpace(char c) {
        return isLetterOrDigit(c) || c == ' ';
    }

    private static boolean isVowel(char c) {
        return "aeiouy".indexOf(c) >= 0;
    }

    //~ Static Fields ................................................................................................................................

    private static final String Q3 = "\"\"\"";

    private static final char COVER_DELIMITER_CHAR = '\u2009';

    private static final int HEX_RADIX = 16;

    private static final int MAX_ISO = 0xFF;

    private static final String HEX_COLOR_PATTERN = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";

    // TODO private static final String CHRONOLOGY_PATTERN = "^(1){1}$";

}  // end class Strings
