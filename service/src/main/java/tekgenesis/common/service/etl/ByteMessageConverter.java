
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

import tekgenesis.common.media.MediaType;
import tekgenesis.common.service.Headers;

import static tekgenesis.common.Predefined.notNull;
import static tekgenesis.common.util.Files.toByteArray;

/**
 * Message converter for reading and writing raw bytes.
 */
public class ByteMessageConverter extends AbstractMessageConverter<Object> {

    //~ Methods ......................................................................................................................................

    @Override public byte[] read(Class<?> type, Type genericType, MediaType contentType, InputStream stream) {
        return toByteArray(stream);
    }

    @Override public void write(Object payload, MediaType contentType, Headers headers) {
        super.write(payload, notNull(contentType, MediaType.APPLICATION_OCTET_STREAM), headers);
    }

    @Override public void write(Object content, MediaType contentType, OutputStream stream)
        throws IOException
    {
        try {
            stream.write((byte[]) content);
        }
        catch (final IOException e) {
            throw new IOException(e);
        }
    }

    @Override protected boolean supports(Class<?> clazz) {
        return byte[].class.equals(clazz);
    }
}
