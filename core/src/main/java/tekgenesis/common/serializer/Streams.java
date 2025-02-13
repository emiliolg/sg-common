
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.serializer;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.core.Tuple;

/**
 * Streams utility class.
 */
public class Streams {

    //~ Constructors .................................................................................................................................

    private Streams() {}

    //~ Methods ......................................................................................................................................

    /** Read fixed list of values from the stream. */
    @NotNull public static <T, U extends List<T>> U readList(@NotNull final StreamReader r, @NotNull final U result,
                                                             @NotNull final Function<StreamReader, T> reader) {
        final int n = r.readInt();
        for (int i = 0; i < n; i++)
            result.add(reader.apply(r));
        return result;
    }

    /** Reads a nullable Double from the Stream. */
    @Nullable public static Double readNullableDouble(@NotNull final StreamReader r) {
        final boolean defined = r.readBoolean();
        return defined ? r.readDouble() : null;
    }

    /** Reads a nullable Integer from the Stream. */
    @Nullable public static Integer readNullableInteger(@NotNull final StreamReader r) {
        final boolean defined = r.readBoolean();
        return defined ? r.readInt() : null;
    }

    /** Write list of values to the stream. */
    public static <T> void writeList(@NotNull final StreamWriter w, @NotNull final List<T> values,
                                     @NotNull final Consumer<Tuple<StreamWriter, T>> writer) {
        w.writeInt(values.size());
        for (final T value : values)
            writer.accept(Tuple.tuple(w, value));
    }

    /** Writes a nullable Double to the Stream. */
    public static void writeNullableDouble(@NotNull final StreamWriter w, @Nullable Double value) {
        final boolean defined = value != null;
        w.writeBoolean(defined);
        if (defined) w.writeDouble(value);
    }

    /** Writes a nullable Double to the Stream. */
    public static void writeNullableInteger(@NotNull final StreamWriter w, @Nullable Integer value) {
        final boolean defined = value != null;
        w.writeBoolean(defined);
        if (defined) w.writeInt(value);
    }
}  // end class Streams
