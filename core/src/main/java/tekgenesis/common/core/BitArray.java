
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.serializer.StreamReader;
import tekgenesis.common.serializer.StreamWriter;

/**
 * An Array of bits.
 */

public class BitArray implements Serializable {

    //~ Instance Fields ..............................................................................................................................

    private final int[] bits;

    //~ Constructors .................................................................................................................................

    /** Creates a BitArray of the default size, initialized to zeros. */
    public BitArray() {
        this(BITS_PER_UNIT);
    }

    /** Creates a BitArray of the specified size, initialized to zeros. */
    public BitArray(int size)
        throws IllegalArgumentException
    {
        if (size < 0) throw new IllegalArgumentException("Illegal Size: " + size);

        // Round up to the next multiple of BITS_PER_UNIT
        final int l = (size + BITS_PER_UNIT - 1) / BITS_PER_UNIT;
        bits = new int[l];
    }

    /** Creates a BitArray with the given internal bits. */
    private BitArray(int[] bits) {
        this.bits = bits;
    }

    //~ Methods ......................................................................................................................................

    /**
     * Returns a new BitArray, performing the bitwise AND operation between the current and the
     * given BitArray.
     */
    public BitArray and(@NotNull final BitArray other) {
        final BitArray result = new BitArray(size());
        for (int i = 0; i < bits.length; i++)
            result.bits[i] = other.bits[i] & bits[i];
        return result;
    }

    /** Clear the Array. */
    public void clear() {
        setAll(false);
    }

    /** Returns a new BitArray copy. */
    public BitArray copy() {
        final int[] copy = new int[bits.length];
        System.arraycopy(bits, 0, copy, 0, bits.length);
        return new BitArray(copy);
    }

    /** Returns true if current and given BitArray are bitwise equal. */
    public boolean eq(@NotNull final BitArray other) {
        for (int i = 0; i < bits.length; i++) {
            if (other.bits[i] != bits[i]) return false;
        }
        return true;
    }

    /** Returns the value of the element. */
    public boolean get(int index)
        throws ArrayIndexOutOfBoundsException
    {
        if (index < 0 || index >= size()) throw new ArrayIndexOutOfBoundsException(index);
        return (bits[index(index)] & mask(index)) != 0;
    }

    /** Serialize the BitArray. */
    public void serialize(StreamWriter w) {
        w.writeInt(bits.length);
        for (final int bit : bits)
            w.writeInt(bit);
    }

    /** Sets the value of the element. */
    public void set(int n, boolean value)
        throws ArrayIndexOutOfBoundsException
    {
        if (n < 0 || n >= size()) throw new ArrayIndexOutOfBoundsException(n);
        final int index = index(n);
        final int mask  = mask(n);

        if (value) bits[index] |= mask;
        else bits[index] &= ~mask;
    }

    /** Returns the size of the Array. */
    @SuppressWarnings("WeakerAccess")
    public int size() {
        return bits.length * BITS_PER_UNIT;
    }

    @Override public String toString() {
        final StrBuilder result = new StrBuilder();
        final int        size   = size();
        int              prev   = -1;
        for (int i = 0; i <= size; i++) {
            if (i < size && get(i)) {
                if (prev == -1) {
                    if (!result.isEmpty()) result.append(',');
                    result.append(i);
                    prev = i;
                }
            }
            else if (prev != -1) {
                if (prev != i - 1) result.append('-').append(i - 1);
                prev = -1;
            }
        }
        return result.toString();
    }

    /** Set all elements to the specified value. */
    public void setAll(boolean value) {
        for (int i = 0; i < bits.length; i++)
            bits[i] = value ? ALL_BITS : 0;
    }

    /** Return the backing int array. */
    public int[] getBits() {
        return bits;
    }

    /** Set all bits. */
    public void setBits(int... bits) {
        System.arraycopy(bits, 0, this.bits, 0, bits.length);
    }

    /** Returns true if All elements are false. */
    public boolean isEmpty() {
        for (final int b : bits) {
            if (b != 0) return false;
        }
        return true;
    }

    //~ Methods ......................................................................................................................................

    /** Initialize a serialized BitArray. */
    public static BitArray initialize(StreamReader r) {
        final int   length = r.readInt();
        final int[] bits   = new int[length];
        for (int i = 0; i < length; i++)
            bits[i] = r.readInt();
        return new BitArray(bits);
    }

    private static int index(int i) {
        return i / BITS_PER_UNIT;
    }

    private static int mask(int i) {
        return 1 << (i % BITS_PER_UNIT);
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = -7166972021731746906L;

    private static final int BITS_PER_UNIT = 32;
    private static final int ALL_BITS      = 0xFFFFFFFF;
}  // end class BitArray
