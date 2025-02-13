
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

import static tekgenesis.common.core.Constants.ISO_8859_1;

/**
 * A Property writer that handles comments and formatting with wrapping on properties values.
 */
@SuppressWarnings("WeakerAccess")
public class PropertyWriter {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final PrintWriter output;

    //~ Constructors .................................................................................................................................

    /** Create the writer to write to the specified file. */
    public PropertyWriter(final File file) {
        this(file, false);
    }
    /** Create the writer to write to the specified file. */

    public PropertyWriter(final File file, boolean append) {
        // Ensure parent directory exists
        file.getParentFile().mkdirs();
        try {
            output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), ISO_8859_1)), false);
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    //~ Methods ......................................................................................................................................

    /** Close the writer. */
    public void close() {
        output.close();
    }
    /** jumps a line. */
    public void skipLine() {
        output.println();
    }

    /** Write an empty comment line. */
    public void writeComment() {
        output.println("#");
    }

    /** Write a comment line. */
    public void writeComment(String comment) {
        if (comment != null) output.println("# " + comment.replace("\n", "\n# "));
    }

    /** Write a property to the file. */
    public void writeProperty(String code, String msg) {
        outputKey(code, false);
        outputValue(msg, false);
        output.println();
    }

    private void outputEscapedChar(char chr) {
        output.print('\\');
        output.print(chr);
    }

    private void outputKey(String key, boolean comment) {
        output.print(key);
        output.print(':');

        int len = key.length() + 1;

        if (len > KEY_FIELD_SIZE) wrap(comment);

        while (len++ < KEY_FIELD_SIZE)
            output.print(' ');
    }

    private void outputUnicode(char chr) {
        if (chr <= ISO_HIGH && !Character.isISOControl(chr)) output.print(chr);
        else {
            outputEscapedChar('u');
            output.print(toHex((chr >> FIRST_NIBBLE) & NIBBLE_MASK));
            output.print(toHex((chr >> SECOND_NIBBLE) & NIBBLE_MASK));
            output.print(toHex((chr >> THIRD_NIBBLE) & NIBBLE_MASK));
            output.print(toHex(chr & NIBBLE_MASK));
        }
    }  // end method outputUnicode

    private void outputValue(String value, boolean comment) {
        final int len = value.length();

        boolean begin = true;

        for (int i = 0; i < len; i++) {
            final char chr = value.charAt(i);

            if (begin && chr == ' ') outputEscapedChar(' ');
            else {
                begin = false;
                switch (chr) {
                case '\n':
                    outputEscapedChar('n');
                    if (i < len - 1) wrap(comment);
                    begin = true;
                    break;

                case '\t':
                    outputEscapedChar('t');
                    break;
                case '\r':
                    outputEscapedChar('r');
                    break;
                case '\f':
                    outputEscapedChar('f');
                    break;

                case '\\':
                case '=':
                case ':':
                case '#':
                case '!':
                    outputEscapedChar(chr);
                    break;

                default:
                    outputUnicode(chr);
                }
            }
        }
    }

    private void wrap(boolean comment) {
        int len;

        if (comment) {
            output.println();
            output.print('#');
            len = 1;
        }
        else {
            output.print('\\');
            output.println();
            len = 0;
        }

        while (len++ < KEY_FIELD_SIZE)
            output.print(' ');
    }

    //~ Methods ......................................................................................................................................

    private static char toHex(final int nibble) {
        return hexDigit[(nibble & NIBBLE_MASK)];
    }

    //~ Static Fields ................................................................................................................................

    private static final int ISO_HIGH = 0xFF;

    private static final int FIRST_NIBBLE  = 12;
    private static final int SECOND_NIBBLE = 8;
    private static final int THIRD_NIBBLE  = 4;

    private static final int NIBBLE_MASK = 0xF;

    private static final char[] hexDigit       = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    private static final int    KEY_FIELD_SIZE = 25;
}
