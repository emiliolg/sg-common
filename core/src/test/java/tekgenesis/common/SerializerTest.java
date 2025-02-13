
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import tekgenesis.common.serializer.StreamReader;
import tekgenesis.common.serializer.StreamWriter;

import static java.util.Arrays.asList;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class SerializerTest {

    //~ Methods ......................................................................................................................................

    @Test public void reader() {
        final List<String>     strings = asList("Hello", " ", "World");
        final Iterator<String> it      = strings.iterator();

        final StreamReader reader = new MyStreamReader() {
                @Override public int readInt() {
                    return 3;
                }
                @Override public String readString() {
                    return it.next();
                }
            };

        assertThat(reader.readStrings()).isEqualTo(strings);
    }

    @Test public void writer() {
        final int[]        intVal  = { -1 };
        final List<String> strings = new ArrayList<>();

        final StreamWriter writer = new SerializerTest.MyStreamWriter() {
                @Override public StreamWriter writeInt(int value) {
                    intVal[0] = value;
                    return this;
                }

                @Override public StreamWriter writeString(String value) {
                    strings.add(value);
                    return this;
                }
            };

        final List<String> values = asList("Hello", " ", "World");
        writer.writeStrings(values);
        assertThat(intVal[0]).isEqualTo(3);
        assertThat(strings).isEqualTo(values);
    }

    //~ Inner Classes ................................................................................................................................

    class MyStreamReader extends StreamReader.Default {
        @Override public boolean readBoolean() {
            return false;
        }
        @Override public byte readByte() {
            return 0;
        }
        @Override public char readChar() {
            return 0;
        }
        @Override public double readDouble() {
            return 0;
        }
        @Override public float readFloat() {
            return 0;
        }
        @Override public int readInt() {
            return 0;
        }
        @Override public long readLong() {
            return 0;
        }
        @Override public short readShort() {
            return 0;
        }
        @Override public String readString() {
            return null;
        }
    }

    class MyStreamWriter extends StreamWriter.Default {
        @Override public StreamWriter writeBoolean(boolean value) {
            return null;
        }
        @Override public StreamWriter writeByte(byte value) {
            return null;
        }
        @Override public StreamWriter writeChar(char value) {
            return null;
        }
        @Override public StreamWriter writeDouble(double value) {
            return null;
        }
        @Override public StreamWriter writeFloat(float value) {
            return null;
        }
        @Override public StreamWriter writeInt(int value) {
            return null;
        }
        @Override public StreamWriter writeLong(long value) {
            return null;
        }
        @Override public StreamWriter writeShort(short value) {
            return null;
        }
        @Override public StreamWriter writeString(String value) {
            return null;
        }
    }
}  // end class SerializerTest
