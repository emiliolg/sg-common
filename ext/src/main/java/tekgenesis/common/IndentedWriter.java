
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.io.*;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link PrintWriter} that manages indentation.
 */
@SuppressWarnings("UnusedReturnValue")  // Writer.
public class IndentedWriter implements Closeable, Flushable {

    //~ Instance Fields ..............................................................................................................................

    private int                 column;
    private int                 indentation;
    private final MyPrintWriter w;

    //~ Constructors .................................................................................................................................

    /** Creates an IndentedWriter. */
    public IndentedWriter(File file)
        throws FileNotFoundException
    {
        w           = new MyPrintWriter(file);
        indentation = 0;
        column      = 0;
    }

    /** Creates an IndentedWriter. */
    public IndentedWriter(Writer writer) {
        w           = new MyPrintWriter(writer);
        indentation = 0;
        column      = 0;
    }

    //~ Methods ......................................................................................................................................

    @Override public void close()
        throws IOException
    {
        w.close();
    }

    public void flush() {
        w.flush();
    }
    /** indent the lines to be printed. */
    public IndentedWriter indent() {
        indentation++;
        return this;
    }

    /** Terminates the current line by writing the line separator string. */
    public IndentedWriter newLine() {
        w.println();
        column = 0;
        return this;
    }

    /** print a newLine and spaces up to the specified margin. */
    public IndentedWriter newLine(int margin) {
        newLine();
        while (column < margin) {
            w.directWrite(' ');
            column++;
        }
        return this;
    }

    /** Prints a string. */

    public IndentedWriter print(String string) {
        w.print(string);
        return this;
    }

    /** Print a character. */
    public void print(char c) {
        w.write(c);
    }

    /** Prints a formatted string using the specified format string and arguments. */
    public IndentedWriter printf(String fmt, Object... args) {
        return print(String.format(fmt, args));
    }

    /** Prints a String and then terminates the line. */
    public IndentedWriter println(String s) {
        return print(s).newLine();
    }

    /** Print an String quoted. */
    public IndentedWriter printQuoted(String s) {
        w.print('"');
        w.print(s);
        w.print('"');
        return this;
    }

    /** Print an String and return the writer. */
    public IndentedWriter prints(String s) {
        w.print(s);
        return this;
    }

    /** un-indent the lines to be printed. */
    public IndentedWriter unIndent() {
        indentation--;
        if (indentation < 0) throw new IllegalStateException("Negative indentation");
        return this;
    }

    /** Return the current column number. */
    public int getColumn() {
        return column;
    }

    //~ Static Fields ................................................................................................................................

    private static final int INDENT_SPACES = 4;

    //~ Inner Classes ................................................................................................................................

    private class MyPrintWriter extends PrintWriter {
        public MyPrintWriter(File file)
            throws FileNotFoundException
        {
            super(file);
        }

        public MyPrintWriter(Writer writer) {
            super(writer);
        }

        @Override public void println() {
            super.println();
            column = 0;
        }

        @Override public void write(int c) {
            if (c == '\n') println();
            else {
                printMargin();
                super.write(c);
                column++;
            }
        }

        public void write(@NotNull String s, int off, int len) {
            if (len <= 0) return;
            printMargin();
            super.write(s, off, len);
            if (s.charAt(off + len - 1) == '\n') column = 0;
            else column += len;
        }

        private void directWrite(int c) {
            super.write(c);
        }

        private void printMargin() {
            if (column == 0) {
                int n = indentation * INDENT_SPACES;
                column += n;
                while (n-- > 0)
                    w.directWrite(' ');
            }
        }
    }  // end class MyPrintWriter
}  // end class IndentedWriter
