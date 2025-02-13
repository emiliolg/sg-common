
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service.etl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.charset.Charset;

import org.jetbrains.annotations.Nullable;

import tekgenesis.common.core.DateOnly;
import tekgenesis.common.core.DateTime;
import tekgenesis.common.media.MediaType;
import tekgenesis.common.util.Conversions;

import static tekgenesis.common.util.Files.readInput;

/**
 * Message converter for reading and writing basic types objects (except string).
 */
public class BasicTypeMessageConverter extends AbstractMessageConverter<Object> {

    //~ Instance Fields ..............................................................................................................................

    private final Charset charset;

    //~ Constructors .................................................................................................................................

    /** Default constructor that uses {@link #DEFAULT_CHARSET} as default charset. */
    public BasicTypeMessageConverter() {
        this(DEFAULT_CHARSET);
    }

    /** Constructor accepting a default charset to use if content type does not specifies one. */
    public BasicTypeMessageConverter(Charset charset) {
        super(MediaType.TEXT_PLAIN);
        this.charset = charset;
    }

    //~ Methods ......................................................................................................................................

    @Override public Object read(Class<?> type, Type genericType, @Nullable MediaType contentType, InputStream stream)
        throws IOException
    {
        final Charset c       = getContentTypeCharsetOrDefault(contentType, charset);
        final String  content = readInput(new InputStreamReader(stream, c));
        return Conversions.fromString(content, type);
    }

    @Override public void write(Object content, @Nullable MediaType contentType, OutputStream stream)
        throws IOException
    {
        final Charset c = getContentTypeCharsetOrDefault(contentType, charset);
        stream.write(Conversions.toString(content).getBytes(c));
    }

    @Override protected boolean supports(Class<?> clazz) {
        return Boolean.class.equals(clazz) || Integer.class.equals(clazz) || Double.class.equals(clazz) || BigDecimal.class.equals(clazz) ||
               DateTime.class.equals(clazz) || DateOnly.class.equals(clazz) || clazz.isEnum();
    }
}  // end class BasicTypeMessageConverter
