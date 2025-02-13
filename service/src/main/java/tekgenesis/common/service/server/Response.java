
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service.server;

import java.nio.charset.Charset;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.media.MediaType;
import tekgenesis.common.media.Mime;
import tekgenesis.common.service.OutboundMessage;
import tekgenesis.common.service.Status;
import tekgenesis.common.service.cookie.MutableCookie;

import static tekgenesis.common.collections.Maps.hashMap;
import static tekgenesis.common.core.Tuple.tuple;
import static tekgenesis.common.media.MediaType.CHARSET_PARAMETER;

/**
 * Represents a server response.
 */
public interface Response extends OutboundMessage {

    //~ Methods ......................................................................................................................................

    /** Set the content-type media type. */
    @NotNull default Response withContentType(@NotNull MediaType mime) {
        getHeaders().setContentType(mime);
        return this;
    }

    /** Set the content-type media type. */
    @NotNull default Response withContentType(@NotNull Mime mime, @NotNull Charset charset) {
        return withContentType(new MediaType(mime, hashMap(tuple(CHARSET_PARAMETER, charset.name()))));
    }

    /** Set cookie on response. Returns {@link MutableCookie} for customization. */
    MutableCookie withCookie(@NotNull String name, @NotNull String value);

    /** Add header value associated with given header name. */
    @NotNull default Response withHeader(String name, String value) {
        getHeaders().put(name, value);
        return this;
    }

    /** Return result status. */
    @NotNull Status getStatus();

    /** Set response status. */
    void setStatus(@NotNull Status status);
}
