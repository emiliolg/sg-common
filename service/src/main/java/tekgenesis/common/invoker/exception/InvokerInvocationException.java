
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker.exception;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.media.MediaType;
import tekgenesis.common.service.Headers;
import tekgenesis.common.service.Status;
import tekgenesis.common.util.Files;

import static tekgenesis.common.Predefined.equal;
import static tekgenesis.common.Predefined.isNotEmpty;
import static tekgenesis.common.core.Constants.UTF8;

/**
 * Exception raised when the status code of the http response indicates a response that is not
 * handled nor expected.
 */
public class InvokerInvocationException extends InvokerResponseException {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final byte[] content;
    @NotNull private final String data;

    //~ Constructors .................................................................................................................................

    /** Exception constructor. */
    public InvokerInvocationException(@NotNull Status status, @NotNull Headers headers, @NotNull String message) {
        this(status, headers, message.getBytes());
    }

    /** Exception constructor. */
    public InvokerInvocationException(@NotNull Status status, @NotNull Headers headers, @NotNull InputStream content) {
        this(status, headers, Files.toByteArray(content));
    }

    /** Exception constructor. */
    private InvokerInvocationException(@NotNull Status status, @NotNull Headers headers, @NotNull byte[] content) {
        super(status, headers);
        this.content = content;
        data         = new String(content, Charset.forName(UTF8));
    }

    //~ Methods ......................................................................................................................................

    /** Return response error data stream as byte array. */
    @NotNull public byte[] getContent() {
        return content;
    }

    /** Return response error data stream as utf-8 string. */
    @NotNull public String getData() {
        return data;
    }

    @Override
    @SuppressWarnings("DuplicateStringLiteralInspection")
    public String getMessage() {
        return equal(getHeaders().getContentType(), MediaType.APPLICATION_JSON)
               ? "{ \"status\":\"" + status + (isNotEmpty(data) ? "\", \"data\":\"" + data : "") + "\" }"
               : "{ status=" + status + (isNotEmpty(data) ? ", data=" + data : "") + " }";
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = -5937574015805122011L;
}  // end class InvokerInvocationException
