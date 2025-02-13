
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker.error;

import java.io.InputStream;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.service.Headers;
import tekgenesis.common.service.Status;

/**
 * Handler for code error responses on {@link tekgenesis.common.invoker.HttpInvoker invoker}.
 */
public interface InvokerErrorHandler {

    //~ Methods ......................................................................................................................................

    /** Handle given error status. */
    void handle(@NotNull Status status, @NotNull Headers headers, @NotNull InputStream data);
}
