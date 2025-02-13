
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
import java.nio.charset.Charset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.media.MediaType;
import tekgenesis.common.util.Files;
import tekgenesis.service.html.Html;
import tekgenesis.service.html.HtmlBuilder;

import static tekgenesis.common.env.context.Context.getContext;

/**
 * Message converter for reading html.
 */
public class HtmlReadMessageConverter extends AbstractMessageConverter<Html> {

    //~ Instance Fields ..............................................................................................................................

    @NotNull protected final Charset charset;

    //~ Constructors .................................................................................................................................

    /** Default constructor that uses {@link #DEFAULT_CHARSET} as default charset. */
    public HtmlReadMessageConverter() {
        this(DEFAULT_CHARSET);
    }

    /** Constructor accepting a default charset to use if content type does not specifies one. */
    public HtmlReadMessageConverter(@NotNull Charset charset) {
        super(MediaType.TEXT_HTML);
        this.charset = charset;
    }

    //~ Methods ......................................................................................................................................

    @Override public boolean canWrite(Class<?> type, Type genericType, MediaType contentType) {
        return !isReadOnly() && super.canWrite(type, genericType, contentType);
    }

    @Override public Html read(Class<? extends Html> type, Type genericType, MediaType contentType, InputStream stream)
        throws IOException
    {
        final Charset     c       = getContentTypeCharsetOrDefault(contentType, charset);
        final String      content = Files.readInput(new InputStreamReader(stream, c));
        final HtmlBuilder builder = getContext().getSingleton(HtmlBuilder.class);
        return builder.staticSource(content).build();
    }

    @Override public void write(Html content, @Nullable MediaType contentType, OutputStream stream)
        throws IOException
    {
        throw new IllegalStateException("HtmlReadMessageConverter is a read only html converter!");
    }

    @Override protected boolean supports(Class<?> clazz) {
        return Html.class.isAssignableFrom(clazz);
    }

    protected boolean isReadOnly() {
        return true;
    }
}  // end class HtmlReadMessageConverter
