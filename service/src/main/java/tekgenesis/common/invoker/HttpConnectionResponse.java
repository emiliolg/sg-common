
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.collections.Seq;
import tekgenesis.common.invoker.exception.InvokerConnectionException;
import tekgenesis.common.service.Headers;
import tekgenesis.common.service.Status;
import tekgenesis.common.service.client.Response;
import tekgenesis.common.service.cookie.Cookie;
import tekgenesis.common.util.Files;

import static java.lang.String.format;

import static tekgenesis.common.Predefined.isNotEmpty;
import static tekgenesis.common.collections.Colls.seq;
import static tekgenesis.common.service.cookie.Cookies.decodeClientCookies;

/**
 * Response based on http url connection.
 */
class HttpConnectionResponse implements Response {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final HttpURLConnection connection;
    private Seq<Cookie>                      cookies;
    private Headers                          headers;
    private Status                           status;
    private InputStream                      stream;

    //~ Constructors .................................................................................................................................

    HttpConnectionResponse(@NotNull HttpURLConnection connection) {
        this.connection = connection;
        headers         = null;
        cookies         = null;
        stream          = null;
        status          = null;
    }

    //~ Methods ......................................................................................................................................

    @Override public void close() {
        Files.close(stream);
    }

    public void setBody(@NotNull InputStream wrapper) {
        stream = wrapper;
    }

    @Override public InputStream getContent()
        throws IOException
    {
        if (stream == null) {
            if (getStatus().code() < Status.BAD_REQUEST.code()) stream = connection.getInputStream();
            else stream = getErrorInputStream();
        }
        return stream;
    }

    @Override public Seq<Cookie> getCookies() {
        return readCookies();
    }

    @NotNull @Override public Headers getHeaders() {
        return readHeaders();
    }

    @Override public Status getStatus()
        throws IOException
    {
        if (status == null) {
            final int responseCode = connection.getResponseCode();
            status = Status.fromCode(responseCode);
            if (status == null) {
                final InputStream errorInputStream = getErrorInputStream();
                final String      errMsg           = errorInputStream != null ? Files.readInput(new InputStreamReader(errorInputStream)) : "";
                throw new InvokerConnectionException(
                    new IllegalStateException(format("Response code not defined %d' (errorMsg: %s)", responseCode, errMsg)));
            }
        }

        return status;
    }

    private Seq<Cookie> readCookies() {
        if (cookies == null) {
            readHeaders();
            cookies = seq(decodeClientCookies(headers));
        }
        return cookies;
    }

    private Headers readHeaders() {
        if (headers == null) {
            headers = new Headers();
            for (final Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
                final String key = header.getKey();
                if (isNotEmpty(key)) headers.putAll(key, header.getValue());
            }
        }
        return headers;
    }

    private InputStream getErrorInputStream() {
        final InputStream error = connection.getErrorStream();
        return error != null ? error : new ByteArrayInputStream(empty_byte_array);
    }

    //~ Static Fields ................................................................................................................................

    private static final byte[] empty_byte_array = new byte[0];
}  // end class HttpConnectionResponse
