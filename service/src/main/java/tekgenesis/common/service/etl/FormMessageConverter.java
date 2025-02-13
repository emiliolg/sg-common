
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service.etl;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.collections.MultiMap;
import tekgenesis.common.media.MediaType;

import static java.net.URLEncoder.encode;

import static tekgenesis.common.collections.Colls.map;

/**
 * Message converter for writing http form data.
 */
public class FormMessageConverter extends AbstractMessageConverter<MultiMap<String, String>> {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final Charset charset;

    //~ Constructors .................................................................................................................................

    /** Default constructor that uses {@link #DEFAULT_CHARSET} as default charset. */
    public FormMessageConverter() {
        this(DEFAULT_CHARSET);
    }

    /**
     * Constructor accepting a default charset to use if the message content type does not specify
     * one.
     */
    public FormMessageConverter(@NotNull Charset charset) {
        super(MediaType.APPLICATION_FORM_URLENCODED);
        this.charset = charset;
    }

    //~ Methods ......................................................................................................................................

    @Override public MultiMap<String, String> read(Class<? extends MultiMap<String, String>> type, Type genericType, MediaType contentType,
                                                   InputStream stream)
        throws IOException
    {
        throw new IllegalStateException("FormMessageConverter is a write only converter!");
    }

    @Override public void write(MultiMap<String, String> content, MediaType contentType, OutputStream stream)
        throws IOException
    {
        final Charset c = getContentTypeCharsetOrDefault(contentType, charset);

        final StringBuilder            builder = new StringBuilder();
        final Function<String, String> encoder = createEncoder(c);

        for (final Map.Entry<String, Collection<String>> entry : content.asMap().entrySet()) {
            if (builder.length() != 0) builder.append("&");
            final String name      = encoder.apply(entry.getKey());
            final String parameter = map(entry.getValue(), encoder).map(value -> name + "=" + value).mkString("&");
            builder.append(parameter);
        }

        final Writer writer = new OutputStreamWriter(stream, c);
        writer.write(builder.toString());
        writer.flush();
    }

    /** Does not knows how to read form data, only writes!. */
    @Override protected boolean canRead(MediaType mediaType) {
        return false;
    }

    @Override protected boolean supports(Class<?> clazz) {
        return MultiMap.class.equals(clazz);
    }

    private Function<String, String> createEncoder(final Charset c) {
        return value -> {
                   try {
                       return encode(value, c.name());
                   }
                   catch (final UnsupportedEncodingException ignored) {}
                   return "";
               };
    }
}  // end class FormMessageConverter
