
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import org.jetbrains.annotations.NotNull;

/**
 * This class represents a {@link CharSequence} backed up by a <code>char</code> array.
 */
public final class CharArraySequence implements CharSequence {

    //~ Instance Fields ..............................................................................................................................

    /** The character array. */
    private final char[] data;

    /** The length of char sequence. */
    private final int length;

    /** The index of the first character. */
    private int offset;

    //~ Constructors .................................................................................................................................

    /**
     * Creates a CharArraySequence with the specified data.
     *
     * @param  data  the new underlying data.
     */
    public CharArraySequence(char[] data) {
        this.data = data;
        offset    = 0;
        length    = data.length;
    }

    /**
     * Creates a character array from the specified CharSequence.
     *
     * @param  sequence  the sequence source.
     */
    public CharArraySequence(CharSequence sequence) {
        final int len = sequence.length();
        data = new char[len];
        for (int i = 0; i < len; i++)
            data[i] = sequence.charAt(i);
        length = len;
    }

    /**
     * Creates a CharArraySequence with the specified data.
     *
     * @param  data    the new underlying data.
     * @param  offset  the offset.
     * @param  length  the length.
     */
    public CharArraySequence(char[] data, int offset, int length) {
        this.data   = data;
        this.offset = offset;
        this.length = length;
    }

    //~ Methods ......................................................................................................................................

    public char charAt(int index) {
        if ((index < 0) || (index >= length)) throw new IndexOutOfBoundsException("index: " + index);
        return data[offset + index];
    }

    /**
     * Compares this character sequence against the specified object (<code>String</code> or <code>
     * CharSequence</code>).
     *
     * @param   o  the object to compare with.
     *
     * @return  <code>true</code> if both objects represent the same sequence; <code>false</code>
     *          otherwise.
     */
    public boolean equals(Object o) {
        if (o instanceof CharSequence) {
            final CharSequence that = (CharSequence) o;
            if (length == that.length()) {
                int j = offset + length;
                for (int i = length; --i >= 0;) {
                    if (data[--j] != that.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the hash code for this {@link CharArraySequence}.
     *
     * <p>Note: Returns the same hashCode as <code>java.lang.String</code> (consistent with
     * {@link #equals})</p>
     *
     * @return  the hash code value.
     */
    public int hashCode() {
        int h = 0;
        for (int i = 0, j = offset; i < length; i++, j++)
            h = 31 * h + data[j];
        return h;
    }

    /**
     * Returns the length of this character sequence.
     *
     * @return  the number of characters
     */
    public int length() {
        return length;
    }

    public CharSequence subSequence(int start, int end) {
        if (start < 0 || start > end || end > length()) throw new IndexOutOfBoundsException();
        return new CharArraySequence(data, offset + start, end - start);
    }

    /**
     * Returns the <code>String <code>corresponding to this character sequence. The <code>
     * String</code> returned is always allocated on the heap and can safely be referenced
     * elsewhere.</code></code>
     *
     * @return  the <code>java.lang.String</code> for this character sequence.
     */
    @NotNull public String toString() {
        return new String(data, offset, length);
    }
}  // end class CharArraySequence
