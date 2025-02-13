
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.collections.Seq;
import tekgenesis.common.core.DateOnly;
import tekgenesis.common.core.DateTime;
import tekgenesis.common.util.Conversions;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.json.JsonEscapes.escapeHtml;

/**
 * Json Mapping utility class.
 */
public final class JsonMapping {

    //~ Constructors .................................................................................................................................

    private JsonMapping() {}

    //~ Methods ......................................................................................................................................

    /** Attempt to construct a type instance from an InputStream. */
    @NotNull public static <T> T fromJson(@NotNull final InputStream value, @NotNull final Class<T> type) {
        try {
            return sharedMapper.readValue(value, type);
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    /** Attempt to construct a type instance from an InputStream. */
    @NotNull public static <T> T fromJson(@NotNull final byte[] value, @NotNull final Class<T> type) {
        try {
            return sharedMapper.readValue(value, type);
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Attempt to construct a type instance from a String. */
    @NotNull public static <T> T fromJson(@NotNull final String value, @NotNull final Class<T> type) {
        try {
            return sharedMapper.readValue(value, type);
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Return a new sg json mapper. */
    public static ObjectMapper json() {
        final ObjectMapper result = new ObjectMapper();
        registerMapping(result);
        return result;
    }

    /** Return a new sg json mapper with html escaping. */
    public static ObjectMapper jsonHtmlSafe() {
        return escapeHtml(json());
    }

    /**
     * Register Sui Generis module to extend mapper functionality to custom types (such as
     * DateTime/DateOnly).
     */
    public static void registerMapping(@NotNull final ObjectMapper m) {
        m.registerModule(sgModule);
    }

    /** Return a shared sg json mapper. */
    public static ObjectMapper shared() {
        return sharedMapper;
    }

    /** Attempt to serialize given value to a json string. */
    @NotNull public static String toJson(@NotNull final Object object) {
        try {
            return sharedMapper.writeValueAsString(object);
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Attempt to serialize given value to a json string into given output stream. */
    public static void toJson(@NotNull final OutputStream stream, @NotNull final Object object) {
        try {
            sharedMapper.writeValue(stream, object);
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    /** Attempt to serialize given value to a json byte array. */
    @NotNull public static byte[] toJsonBytes(@NotNull final Object object) {
        try {
            return sharedMapper.writeValueAsBytes(object);
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    //~ Static Fields ................................................................................................................................

    private static final Class<Seq<Object>> SEQ_CLASS = cast(Seq.class);

    //J-
    private static final Module sgModule = new SimpleModule("SuiGenerisModule", new Version(1, 0, 0, null,null,null))
            .addSerializer(DateOnly.class, new DateOnlySerializer())
            .addDeserializer(DateOnly.class, new DateOnlyDeserializer())
            .addSerializer(DateTime.class, new DateTimeSerializer())
            .addDeserializer(DateTime.class, new DateTimeDeserializer())
            .addSerializer(SEQ_CLASS, new SeqSerializer());
    //J+

    private static final ObjectMapper sharedMapper = json();

    //~ Inner Classes ................................................................................................................................

    /**
     * Deserializer For DateOnly.
     */
    private static final class DateOnlyDeserializer extends JsonDeserializer<DateOnly> {
        @Override public DateOnly deserialize(JsonParser jp, DeserializationContext ctx)
            throws IOException
        {
            return Conversions.fromString(jp.getText(), DateOnly.class);
        }
    }

    /**
     * Serializer For DateOnly.
     */
    private static final class DateOnlySerializer extends JsonSerializer<DateOnly> {
        @Override public void serialize(DateOnly value, JsonGenerator jg, SerializerProvider provider)
            throws IOException
        {
            jg.writeString(value.toString());
        }
    }

    /**
     * Deserializer For DateTime.
     */
    private static final class DateTimeDeserializer extends JsonDeserializer<DateTime> {
        @Override public DateTime deserialize(JsonParser jp, DeserializationContext ctx)
            throws IOException
        {
            return Conversions.fromString(jp.getText(), DateTime.class);
        }
    }

    /**
     * Serializer For DateTime.
     */
    private static final class DateTimeSerializer extends JsonSerializer<DateTime> {
        @Override public void serialize(DateTime value, JsonGenerator jg, SerializerProvider provider)
            throws IOException
        {
            jg.writeString(value.toString());
        }
    }

    /**
     * Serializer For Seq.
     */
    private static final class SeqSerializer extends JsonSerializer<Seq<Object>> {
        @Override public void serialize(Seq<Object> seq, JsonGenerator jg, SerializerProvider provider)
            throws IOException
        {
            provider.defaultSerializeValue(seq.into(new ArrayList<>()), jg);
        }
    }
}  // end class JsonMapping
