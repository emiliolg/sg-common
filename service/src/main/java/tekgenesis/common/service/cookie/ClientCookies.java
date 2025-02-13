
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
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.service.HeaderNames;

import static tekgenesis.common.core.Strings.unquote;
import static tekgenesis.common.service.cookie.Cookies.appendQuotedIfWhitespace;

/**
 * A RFC6265 most-compliant cookie encoder/decoder to be used client side, so only name=value pairs
 * are sent. Note that multiple cookies are supposed to be sent at once in a single
 * {@link HeaderNames#COOKIE} header.
 */
class ClientCookies {

    //~ Constructors .................................................................................................................................

    private ClientCookies() {}

    //~ Methods ......................................................................................................................................

    @Nullable
    @SuppressWarnings("DuplicateStringLiteralInspection")
    static Cookie decode(@NotNull final String header) {
        final String[] bites  = header.split("[;]");
        MutableCookie  cookie = null;
        for (final String bite : bites) {
            final String[] crumbs = bite.split("=", 2);
            final String   name   = crumbs.length > 0 ? crumbs[0].trim() : "";
            final String   value  = unquote(crumbs.length > 1 ? crumbs[1].trim() : "");
            if (cookie == null) cookie = Cookies.create(name, value);
            else {
                final String key = name.toLowerCase();
                // noinspection IfStatementWithTooManyBranches
                if (key.startsWith("domain")) cookie.withDomain(value);
                else if (key.startsWith("max-age")) cookie.withMaxAge(Integer.parseInt(value));
                else if (key.startsWith("path")) cookie.withPath(value);
                else if (key.startsWith("secure")) cookie.withSecure(true);
                else if (key.startsWith("httponly")) cookie.withHttpOnly(true);
            }
        }
        return cookie;
    }

    /** Encode given cookie. */
    @NotNull static String encode(@NotNull final Cookie cookie) {
        final StringBuilder builder = new StringBuilder();
        builder.append(cookie.getName()).append('=');
        appendQuotedIfWhitespace(builder, cookie.getValue());
        return builder.toString();
    }
}
