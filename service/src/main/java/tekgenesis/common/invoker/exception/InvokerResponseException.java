
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker.exception;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.command.exception.CommandInvocationException;
import tekgenesis.common.service.Headers;
import tekgenesis.common.service.Status;

/**
 * Invoker exception including status and headers.
 */
public class InvokerResponseException extends CommandInvocationException {

    //~ Instance Fields ..............................................................................................................................

    @NotNull final Headers headers;

    @NotNull final Status status;

    //~ Constructors .................................................................................................................................

    InvokerResponseException(@NotNull final Status status, @NotNull final Headers headers) {
        this.status  = status;
        this.headers = headers;
    }

    //~ Methods ......................................................................................................................................

    /** Return exception response headers. */
    @NotNull public Headers getHeaders() {
        return headers;
    }

    /** Return exception response status. */
    @NotNull public Status getStatus() {
        return status;
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = -6112680120329062015L;
}
