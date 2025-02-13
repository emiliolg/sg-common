
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.serializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tekgenesis.common.core.DateOnly;

import static java.util.Collections.emptyList;

import static tekgenesis.common.core.DateOnly.fromMilliseconds;

/**
 * An interface for reading values from a stream.
 */
public interface StreamReader {

    //~ Methods ......................................................................................................................................

    /** Read a boolean from the Stream. */
    boolean readBoolean();

    /** Read a List&lt;Boolean&gt; from the Stream. */
    List<Boolean> readBooleans();
    /** Read a byte from the Stream. */
    byte readByte();
    /** Read a char from the Stream. */
    char readChar();
    /** Read a List&lt;DateOnly&gt; from the Stream. */
    List<DateOnly> readDates();
    /** Read a Map&lt;DateOnly, String&gt; from the Stream. */
    Map<DateOnly, String> readDatesMap();
    /** Read a double from the Stream. */
    double readDouble();
    /** Read a float from the Stream. */
    float readFloat();
    /** Read an int from the Stream. */
    int readInt();
    /** Read an long from the Stream. */
    long readLong();
    /** Read an Object from the Stream. */
    Object readObject();
    /** Read a Constant Object from the Stream. */
    Object readObjectConst();
    /** Read a short from the Stream. */
    short readShort();
    /** Read a String from the Stream. */
    String readString();
    /** Read a List&lt;String&gt; from the Stream. */
    List<String> readStrings();

    //~ Inner Classes ................................................................................................................................

    abstract class Default implements StreamReader {
        @Override public List<Boolean> readBooleans() {
            final int sz = readInt();
            if (sz == 0) return emptyList();
            final List<Boolean> list = new ArrayList<>(sz);
            for (int i = 0; i < sz; i++)
                list.add(readBoolean());
            return list;
        }

        @Override public List<DateOnly> readDates() {
            final int sz = readInt();
            if (sz == 0) return emptyList();
            final List<DateOnly> list = new ArrayList<>(sz);
            for (int i = 0; i < sz; i++)
                list.add(fromMilliseconds(readLong()));
            return list;
        }

        @Override public Map<DateOnly, String> readDatesMap() {
            final int sz = readInt();
            if (sz == 0) return new HashMap<>();
            final Map<DateOnly, String> map = new HashMap<>(sz);
            for (int i = 0; i < sz; i++)
                map.put(fromMilliseconds(readLong()), readString());
            return map;
        }
        @Override public Object readObject() {
            throw new UnsupportedOperationException("StreamReader.Default.readObject");
        }

        @Override public Object readObjectConst() {
            throw new UnsupportedOperationException("StreamReader.Default.readObjectConst");
        }

        @Override public List<String> readStrings() {
            final int sz = readInt();
            if (sz == 0) return emptyList();
            final List<String> list = new ArrayList<>(sz);
            for (int i = 0; i < sz; i++)
                list.add(readString());
            return list;
        }
    }  // end class Default
}  // end interface StreamReader
