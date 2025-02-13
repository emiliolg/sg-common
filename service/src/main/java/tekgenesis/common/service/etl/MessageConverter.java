
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

import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.Seq;
import tekgenesis.common.media.MediaType;
import tekgenesis.common.media.Mime;
import tekgenesis.common.service.Headers;

/**
 * Interface that specifies a converter that can convert from and to inbound and outbound messages.
 */
public interface MessageConverter<T> {

    //~ Methods ......................................................................................................................................

    /** Indicates whether the given class can be read by this converter. */
    boolean canRead(Class<?> type, Type genericType, @Nullable MediaType contentType);

    /** Indicates whether the given class can be written by this converter. */
    boolean canWrite(Class<?> type, Type genericType, @Nullable MediaType contentType);

    /** Read an object of given type from message input stream. */
    T read(Class<? extends T> type, Type genericType, @Nullable MediaType contentType, InputStream stream)
        throws IOException;

    /** Write given object to message output stream. */
    void write(T content, @Nullable MediaType contentType, OutputStream stream)
        throws IOException;

    /** Write any headers. */
    void write(T content, @Nullable MediaType contentType, Headers headers);

    /** Return the list of {@link Mime} objects supported by this converter. */
    Seq<MediaType> getSupportedMediaTypes();
}
