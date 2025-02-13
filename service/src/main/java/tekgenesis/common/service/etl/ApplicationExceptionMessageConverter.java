
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
import java.io.OutputStream;
import java.lang.reflect.Type;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.jetbrains.annotations.Nullable;

import tekgenesis.common.exception.ApplicationException;
import tekgenesis.common.json.JsonMapping;
import tekgenesis.common.media.MediaType;
import tekgenesis.common.service.ApplicationExceptionResult;
import tekgenesis.common.service.Headers;

import static java.lang.Boolean.TRUE;

import static tekgenesis.common.service.HeaderNames.X_APPLICATION_EXCEPTION;

/**
 * Message converter for writing {@link ApplicationException} results.
 */
public class ApplicationExceptionMessageConverter extends AbstractMessageConverter<ApplicationExceptionResult> {

    //~ Instance Fields ..............................................................................................................................

    private final ObjectMapper mapper;

    //~ Constructors .................................................................................................................................

    /** Create default {@link ApplicationExceptionMessageConverter}. */
    public ApplicationExceptionMessageConverter() {
        this(JsonMapping.shared());
    }

    /** Create {@link ApplicationExceptionMessageConverter} with specified {@link ObjectMapper}. */
    public ApplicationExceptionMessageConverter(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    //~ Methods ......................................................................................................................................

    @Override public ApplicationExceptionResult read(Class<? extends ApplicationExceptionResult> type, Type genericType,
                                                     @Nullable MediaType contentType, InputStream stream)
        throws IOException
    {
        return mapper.readValue(stream, type);
    }

    @Override public void write(ApplicationExceptionResult content, MediaType contentType, Headers headers) {
        headers.put(X_APPLICATION_EXCEPTION, TRUE.toString());
        // Fixed content type for application exception messages
        super.write(content, MediaType.APPLICATION_JSON, headers);
    }

    @Override public void write(ApplicationExceptionResult content, @Nullable MediaType contentType, OutputStream stream)
        throws IOException
    {
        mapper.writeValue(stream, content);
    }

    @Override protected boolean canWrite(@Nullable MediaType mediaType) {
        return true;
    }

    @Override protected boolean supports(Class<?> clazz) {
        return ApplicationExceptionResult.class.isAssignableFrom(clazz);
    }
}  // end class ApplicationExceptionMessageConverter
