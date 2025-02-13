
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.media.MediaType;

import static tekgenesis.common.util.Files.readInput;

/**
 * Message converter for reading and writing String objects.
 */
public class StringMessageConverter extends AbstractMessageConverter<String> {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final Charset charset;

    //~ Constructors .................................................................................................................................

    /** Default constructor that uses {@link #DEFAULT_CHARSET} as default charset. */
    public StringMessageConverter() {
        this(DEFAULT_CHARSET);
    }

    /** Constructor accepting a default charset to use if content type does not specifies one. */
    public StringMessageConverter(@NotNull Charset charset) {
        super(MediaType.TEXT_PLAIN);
        this.charset = charset;
    }

    //~ Methods ......................................................................................................................................

    @Override public String read(Class<? extends String> type, Type genericType, @Nullable MediaType contentType, InputStream stream)
        throws IOException
    {
        final Charset c = getContentTypeCharsetOrDefault(contentType, charset);
        return readInput(new InputStreamReader(stream, c));
    }

    @Override public void write(String content, MediaType mediaType, OutputStream stream)
        throws IOException
    {
        final Charset c      = getContentTypeCharsetOrDefault(mediaType, charset);
        final Writer  writer = new OutputStreamWriter(stream, c);
        writer.write(content);
        writer.flush();
    }

    @Override protected boolean supports(Class<?> clazz) {
        return String.class.equals(clazz);
    }
}  // end class StringMessageConverter
