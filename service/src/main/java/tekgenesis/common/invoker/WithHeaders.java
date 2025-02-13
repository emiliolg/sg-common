
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import java.util.Locale;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.media.MediaType;

/**
 * Append headers to underlying object.
 */
public interface WithHeaders<T> {

    //~ Methods ......................................................................................................................................

    /** Set the accept media types. */
    @NotNull T accept(@NotNull MediaType... mimes);

    /** Set the accept language. */
    @NotNull T acceptLanguage(@NotNull Locale locale);

    /** Set the content-type media type. */
    @NotNull T contentType(@NotNull MediaType mime);

    /** Add header value associated with given header name. */
    @NotNull T header(String name, String value);

    /** Add cookie to client invocation. */
    @NotNull T withCookie(@NotNull final String name, @NotNull final String value);
}
