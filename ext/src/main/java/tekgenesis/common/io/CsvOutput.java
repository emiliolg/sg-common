
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.core.Constants;
import tekgenesis.common.core.StrBuilder;

/**
 * Generic Csv Output.
 */
@SuppressWarnings("WeakerAccess")
public class CsvOutput extends CsvBase {

    //~ Instance Fields ..............................................................................................................................

    private StrBuilder currentLine;

    private final Writer writer;

    //~ Constructors .................................................................................................................................

    /** Create a CsvOutput with an input stream with default values and iso-8859-1 encoding. */
    public CsvOutput(@NotNull OutputStream outputStream) {
        this(outputStream, Constants.ISO_8859_1);
    }

    /** Create a CsvOutput with default values. */
    public CsvOutput(@NotNull Writer writer) {
        this(writer, null, null, null, '"');
    }

    /** Create a CsvOutput with an input stream with default values. */
    public CsvOutput(@NotNull OutputStream outputStream, @NotNull String charsetName) {
        this(getWriter(outputStream, charsetName), null, null, null, '"');
    }

    /** Create a CsvOutput with default recordSeparator and Null String. */
    public CsvOutput(@NotNull Writer writer, @Nullable String fieldSeparator) {
        this(writer, fieldSeparator, null, null, '"');
    }

    /** Create a CsvOutput. */
    public CsvOutput(@NotNull Writer writer, @Nullable String fieldSeparator, @Nullable String recordSeparator, @Nullable String nullString,
                     char quoteChar) {
        super(writer, fieldSeparator, recordSeparator, nullString, quoteChar);
        this.writer = writer;
        currentLine = null;
    }

    //~ Methods ......................................................................................................................................

    /** Output a quoted String field. */
    public CsvOutput outField(String value) {
        return outField(value, quoteChar != 0);
    }

    /** Output a field with quoting sensitive to the type. */
    public CsvOutput outField(Object value) {
        return outField(String.valueOf(value), value instanceof String);
    }

    /** Flush the current line. */
    public void writeLine() {
        try {
            if (currentLine != null) {
                writer.append(currentLine);
                writer.write(super.recordSeparator);
                currentLine = null;
            }
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Output a field to the Writer. */
    protected CsvOutput outField(@Nullable String value, boolean quoted) {
        if (currentLine == null) {
            currentLine = new StrBuilder();
            currentLine.startCollection(getFieldSeparator());
        }
        currentLine.appendElement(value == null ? getNullString() : quoted ? quote(value) : value);
        return this;
    }

    private void appendQuote(StringBuilder result) {
        if (quoteChar != 0) result.append(quoteChar);
    }

    private CharSequence quote(@NotNull String str) {
        final StringBuilder result = new StringBuilder();
        appendQuote(result);
        for (int i = 0; i < str.length(); i++) {
            final char c = str.charAt(i);
            result.append(c);
            if (c == quoteChar) result.append(c);
        }
        appendQuote(result);
        return result;
    }

    //~ Methods ......................................................................................................................................

    private static OutputStreamWriter getWriter(@NotNull OutputStream outputStream, @NotNull String charsetName) {
        try {
            return new OutputStreamWriter(outputStream, charsetName);
        }
        catch (final UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}  // end class CsvOutput
