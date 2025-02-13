
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.service.HeaderNames;
import tekgenesis.common.service.Headers;

import static tekgenesis.common.service.HeaderNames.*;

/**
 * Modifier that adapts response to unzip stream, and modifies request to add accept encoding
 * header.
 */
class GzipDecompressingMessageModifier implements MessageModifier {

    //~ Methods ......................................................................................................................................

    @Override public void modify(@NotNull HttpConnectionRequest request) {
        final Headers headers = request.getHeaders();
        if (headers.getFirst(ACCEPT_ENCODING).isEmpty()) headers.set(ACCEPT_ENCODING, gzip_codec);
    }

    @Override public void modify(@NotNull HttpConnectionResponse response) {
        final Headers headers = response.getHeaders();
        for (final String codec : headers.getFirst(HeaderNames.CONTENT_ENCODING)) {
            if (gzip_codec.equals(codec)) {
                try {
                    response.setBody(new GZIPInputStream(response.getContent()));
                }
                catch (final IOException e) {
                    throw new UncheckedIOException(e);
                }
                final Map<String, Collection<String>> mutable = headers.asMap();
                mutable.remove(CONTENT_ENCODING);
                mutable.remove(CONTENT_LENGTH);
                mutable.remove(CONTENT_MD5);
            }
        }
    }

    //~ Static Fields ................................................................................................................................

    private static final String gzip_codec = "gzip";
}
