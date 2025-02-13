
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import tekgenesis.common.annotation.GwtIncompatible;
import tekgenesis.common.core.DateOnly;

/**
 * An interface for writing values into a stream.
 */
@SuppressWarnings("UnusedReturnValue")
public interface StreamWriter {

    //~ Methods ......................................................................................................................................

    /**
     * Serializes the contents of this stream into a string.
     *
     * @return  a string that is the serialization of the contents of this stream
     */
    String toString();
    /** Writes a boolean to the Stream. */
    StreamWriter writeBoolean(boolean value);
    /** Writes a Collection<Boolean> to the Stream. */
    StreamWriter writeBooleans(Collection<Boolean> values);
    /** Writes a byte to the Stream. */
    StreamWriter writeByte(byte value);
    /** Writes a char to the Stream. */
    StreamWriter writeChar(char value);
    /** Writes a Collection<DateOnly> to the Stream. */
    StreamWriter writeDates(Collection<DateOnly> values);
    /** Writes a Map<DateOnly, String> to the Stream. */
    StreamWriter writeDatesMap(Map<DateOnly, String> values);
    /** Writes a double to the Stream. */
    StreamWriter writeDouble(double value);
    /** Writes a float to the Stream. */
    StreamWriter writeFloat(float value);
    /** Writes an integer to the Stream. */
    StreamWriter writeInt(int value);
    /** Writes a long to the Stream. */
    StreamWriter writeLong(long value);
    /** Writes an Object to the Stream. */
    StreamWriter writeObject(Object value);
    /** Writes a Constant (Using the kind) to the Stream. */
    StreamWriter writeObjectConst(Object object);
    /** Writes a short to the Stream. */
    StreamWriter writeShort(short value);
    /** Writes a String to the Stream. */
    StreamWriter writeString(String value);
    /** Writes a Collection<String> to the Stream. */
    StreamWriter writeStrings(Collection<String> values);

    //~ Inner Classes ................................................................................................................................

    /**
     * Some default implementations.
     */
    abstract class Default implements StreamWriter {
        @Override public StreamWriter writeBooleans(final Collection<Boolean> values) {
            writeInt(values.size());
            for (final Boolean value : values)
                writeBoolean(value);
            return this;
        }
        @Override public StreamWriter writeDates(final Collection<DateOnly> values) {
            writeInt(values.size());
            for (final DateOnly value : values)
                writeLong(value.toMilliseconds());
            return this;
        }
        @Override public StreamWriter writeDatesMap(final Map<DateOnly, String> values) {
            writeInt(values.size());
            for (final Map.Entry<DateOnly, String> value : values.entrySet()) {
                writeLong(value.getKey().toMilliseconds());
                writeString(value.getValue());
            }
            return this;
        }

        @GwtIncompatible @Override public StreamWriter writeObject(final Object value) {
            if (!(value instanceof Serializable)) throw new UnsupportedOperationException("StreamWriter.Default.writeObject for " + value);
            try(final ByteArrayOutputStream out = new ByteArrayOutputStream();
                final ObjectOutputStream stream = new ObjectOutputStream(out))
            {
                stream.writeObject(value);
                stream.flush();
                final byte[] bytes = out.toByteArray();
                writeInt(bytes.length);
                writeBytes(bytes);
                return this;
            }
            catch (final IOException e) {
                throw new SerializerException(e);
            }
        }

        @Override public StreamWriter writeObjectConst(Object object) {
            throw new UnsupportedOperationException("StreamWriter.Default.writeObjectConst for " + object);
        }
        @Override public StreamWriter writeStrings(final Collection<String> values) {
            writeInt(values.size());
            for (final String value : values)
                writeString(value);
            return this;
        }

        @SuppressWarnings({ "EmptyMethod", "RedundantThrows" })
        protected void writeBytes(byte[] bytes)
            throws IOException {}
    }  // end class Default
}  // end interface StreamWriter
