
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service.cookie;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.service.HeaderNames;

import static tekgenesis.common.service.cookie.Cookies.appendQuotedIfWhitespace;

/**
 * A RFC6265 most-compliant cookie encoder/decoder to be used server side. Multiple cookies are
 * supposed to be sent using multiple {@link HeaderNames#SET_COOKIE} headers.
 */
class ServerCookies {

    //~ Constructors .................................................................................................................................

    private ServerCookies() {}

    //~ Methods ......................................................................................................................................

    /** Encode given {@link Cookie}. */
    @NotNull static String encode(@NotNull final Cookie cookie) {
        final StringBuilder builder = new StringBuilder();

        builder.append(cookie.getName()).append('=');
        appendQuotedIfWhitespace(builder, cookie.getValue());

        if (cookie.getDomain() != null) {
            builder.append(";Domain=");
            appendQuotedIfWhitespace(builder, cookie.getDomain());
        }
        if (cookie.getPath() != null) {
            builder.append(";Path=");
            appendQuotedIfWhitespace(builder, cookie.getPath());
        }
        if (cookie.getMaxAge() != -1) {
            builder.append(";Max-Age=");
            builder.append(cookie.getMaxAge());
        }
        if (cookie.isSecure()) builder.append(";Secure");
        if (cookie.isHttpOnly()) builder.append(";HTTPOnly");

        return builder.toString();
    }
}  // end class ServerCookies
