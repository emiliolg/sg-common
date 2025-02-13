
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.io;

import java.io.*;
import java.util.*;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.AbstractIterator;
import tekgenesis.common.collections.Colls;

import static java.util.Arrays.asList;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.Predefined.notNull;

/**
 * Generic CSV Input.
 */
@SuppressWarnings({ "WeakerAccess", "UnusedDeclaration" })  // Used from Scala tests.
public class CsvInput<T> extends CsvBase implements Iterable<T> {

    //~ Instance Fields ..............................................................................................................................

    @NotNull protected final BufferedReader reader;

    private int                             lineNumber;
    private final Function<List<String>, T> mapper;

    //~ Constructors .................................................................................................................................

    /** Create a CsvInput. */
    protected CsvInput(@NotNull Reader reader, @NotNull Function<List<String>, T> mapper, @Nullable String fieldSeparator,
                       @Nullable String recordSeparator, @Nullable String nullString, char quoteChar) {
        super(reader, fieldSeparator, recordSeparator, nullString, quoteChar);
        this.reader = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
        this.mapper = mapper;
        lineNumber  = 0;
    }

    //~ Methods ......................................................................................................................................

    /**
     * Return a CsvInput that will return map of values using as fields the ones read from the first
     * line of the input.
     */
    public CsvInput<Map<String, String>> asMap() {
        return withMapper(new ListMapFunction(doReadLine()));
    }

    /** Return a CsvInput that will return map of values using the specified list of field names. */
    public CsvInput<Map<String, String>> asMap(final List<String> fieldNames) {
        return withMapper(new ListMapFunction(fieldNames));
    }

    /** Return a CsvInput that will return map of values using the specified list of field names. */
    public CsvInput<Map<String, String>> asMap(String... fieldNames) {
        return withMapper(new ListMapFunction(asList(fieldNames)));
    }

    @Override public Iterator<T> iterator() {
        return new AbstractIterator<T>() {
            @Override protected boolean advance() {
                List<String> strings;
                while (!(strings = doReadLine()).isEmpty()) {
                    next = mapper.apply(strings);
                    if (next == null) break;
                    if (isValid(next)) return true;
                }
                return false;
            }
        };
    }

    /** Read a line from the CSV file. */
    @NotNull public T readLine() {
        return mapper.apply(doReadLine());
    }

    /** Return a CsvInput that will use the specified function as a mapper for input lines. */
    public <E> CsvInput<E> withMapper(final Function<List<String>, E> m) {
        return new CsvInput<>(reader, m, fieldSeparator, recordSeparator, nullString, quoteChar);
    }

    /** Use as null string the specified one. */
    public CsvInput<T> withNullString(@NotNull String str) {
        return new CsvInput<>(reader, mapper, fieldSeparator, recordSeparator, str, quoteChar);
    }
    /** Use as Quote character the specified one. */
    public CsvInput<T> withQuoteCharacter(char chr) {
        return new CsvInput<>(reader, mapper, fieldSeparator, recordSeparator, nullString, chr);
    }

    /** Use as Record separator the specified one. */
    public CsvInput<T> withRecordSeparator(@NotNull String str) {
        return new CsvInput<>(reader, mapper, fieldSeparator, str, nullString, quoteChar);
    }

    /** Use as Field separator the specified one. */
    public CsvInput<T> withSeparator(@NotNull String str) {
        return new CsvInput<>(reader, mapper, str, recordSeparator, nullString, quoteChar);
    }

    /** Return the current line number. */
    @SuppressWarnings("UnusedDeclaration")
    public int getLineNumber() {
        return lineNumber;
    }

    protected boolean isEndField(int chr)
        throws IOException
    {
        return matches(chr, getFieldSeparator());
    }

    protected boolean isEndRecord(int chr)
        throws IOException
    {
        return matches(chr, super.recordSeparator);
    }

    /** Give a chance to validate a record. */
    protected boolean isValid(final T record) {
        return true;
    }

    private void addCurrent(List<String> result, StringBuilder current, boolean checkNull) {
        final String s = current.toString();
        result.add(checkNull && s.equals(getNullString()) ? null : s);
        current.setLength(0);
    }
    @NotNull private List<String> doReadLine() {
        try {
            int chr = reader.read();
            if (chr == -1) return Colls.emptyList();

            State               state   = State.NONE;
            final List<String>  result  = new ArrayList<>();
            final StringBuilder current = new StringBuilder();
            while (chr != -1 && !isEndRecord(chr)) {
                switch (state) {
                case NONE:
                    if (chr == quoteChar && current.length() == 0) state = State.INSIDE_QUOTES;
                    else if (isEndField(chr)) addCurrent(result, current, true);
                    else current.append((char) chr);
                    break;
                case INSIDE_QUOTES:
                    if (chr == quoteChar) state = State.QUOTE;
                    else current.append((char) chr);
                    break;
                case QUOTE:
                    if (chr == quoteChar) {
                        state = State.INSIDE_QUOTES;
                        current.append(quoteChar);
                    }
                    else {
                        state = State.NONE;
                        if (isEndField(chr)) addCurrent(result, current, false);
                        else current.append((char) chr);
                    }
                    break;
                }
                chr = reader.read();
            }
            addCurrent(result, current, state == State.NONE);
            lineNumber++;
            return result;
        }
        catch (final IOException e) {
            return Colls.emptyList();
        }
    }  // end method doReadLine

    private boolean matches(int chr, @NotNull String separator)
        throws IOException
    {
        if (separator.charAt(0) != chr) return false;
        final int length = separator.length();
        if (length != 1) {
            reader.mark(length);
            for (int i = 1; i < length; i++)
                if (reader.read() != separator.charAt(i)) {
                    reader.reset();
                    return false;
                }
        }
        return true;
    }

    //~ Methods ......................................................................................................................................

    /** Create a CsvInput with default values. */
    public static CsvInput<List<String>> createCsvInput(@NotNull Reader reader) {
        final Function<List<String>, List<String>> identity = Function.identity();
        return new CsvInput<>(reader, identity, null, null, null, '"');
    }

    /** Create a CsvInput with default values to read from a file. */
    public static CsvInput<List<String>> createCsvInput(@NotNull File file) {
        try {
            return createCsvInput(new FileReader(file));
        }
        catch (final FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    //~ Enums ........................................................................................................................................

    protected enum State { NONE, INSIDE_QUOTES, QUOTE }

    //~ Inner Classes ................................................................................................................................

    private static class ListMapFunction implements Function<List<String>, Map<String, String>> {
        private final LinkedHashMap<String, String> map;

        public ListMapFunction(List<String> fieldNames) {
            map = new LinkedHashMap<>(fieldNames.size());
            for (final String fieldName : fieldNames)
                map.put(fieldName, "");
        }

        @Override public Map<String, String> apply(List<String> values) {
            int i = 0;
            for (final String fieldName : map.keySet()) {
                final String value = i < values.size() ? notNull(values.get(i), "") : "";
                map.put(fieldName, value);
                i++;
            }
            return cast(map.clone());
        }
    }
}  // end class CsvInput
