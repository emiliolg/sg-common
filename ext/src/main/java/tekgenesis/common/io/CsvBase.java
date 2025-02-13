
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.io;

import java.io.Closeable;
import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static tekgenesis.common.Predefined.notNull;

/**
 * Base class for CSV IO classes.
 */
@SuppressWarnings("WeakerAccess")
public abstract class CsvBase {

    //~ Instance Fields ..............................................................................................................................

    @Nullable protected String       fieldSeparator;
    protected final char             quoteChar;
    @NotNull protected final String  recordSeparator;
    @NotNull final String            nullString;
    @NotNull private final Closeable closeable;

    //~ Constructors .................................................................................................................................

    CsvBase(@NotNull Closeable closeable, @Nullable String fieldSeparator, @Nullable String recordSeparator, @Nullable String nullString,
            char quoteChar) {
        this.closeable       = closeable;
        this.fieldSeparator  = fieldSeparator;
        this.recordSeparator = notNull(recordSeparator, System.getProperty("line.separator"));
        this.nullString      = notNull(nullString, "NULL");
        this.quoteChar       = quoteChar;
    }

    //~ Methods ......................................................................................................................................

    /** Performs a close() operation. */
    public void close() {
        try {
            closeable.close();
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Returns 'null' string. */
    @NotNull public String getNullString() {
        return nullString;
    }

    @NotNull String getFieldSeparator() {
        return notNull(fieldSeparator, ",");
    }
}  // end class CsvBase
