
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.service.Headers;
import tekgenesis.common.service.Method;
import tekgenesis.common.service.client.Request;
import tekgenesis.common.service.cookie.Cookie;

import static tekgenesis.common.service.cookie.Cookies.encodeClientCookies;

/**
 * Request based on http url connection.
 */
class HttpConnectionRequest implements Request {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final HttpURLConnection connection;
    @NotNull private final List<Cookie>      cookies;
    @NotNull private final Headers           headers;
    @NotNull private final Method            method;
    @NotNull private final URI               uri;

    //~ Constructors .................................................................................................................................

    HttpConnectionRequest(@NotNull URI uri, @NotNull Method method, @NotNull Headers headers, @NotNull List<Cookie> cookies,
                          @NotNull HttpURLConnection connection) {
        this.uri        = uri;
        this.method     = method;
        this.connection = connection;
        this.headers    = headers;
        this.cookies    = cookies;
    }

    //~ Methods ......................................................................................................................................

    @Override public OutputStream getContent()
        throws IOException
    {
        writeHeaders();
        return connection.getOutputStream();
    }

    @NotNull @Override public Headers getHeaders() {
        return headers;
    }

    @NotNull @Override public Method getMethod() {
        return method;
    }

    @Override public URI getURI() {
        return uri;
    }

    /**
     * Write request headers, must be called before writing content or explicitly if no content is
     * to be written.
     */
    void writeHeaders() {
        encodeClientCookies(headers, cookies);
        for (final Map.Entry<String, Collection<String>> entry : headers.asMap().entrySet()) {
            final String name = entry.getKey();
            for (final String headerValue : entry.getValue())
                connection.addRequestProperty(name, headerValue);
        }
    }

    @NotNull HttpURLConnection getConnection() {
        return connection;
    }
}  // end class HttpConnectionRequest
