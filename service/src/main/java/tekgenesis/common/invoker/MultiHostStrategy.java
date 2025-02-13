
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.core.Option;
import tekgenesis.common.logging.Logger;

/**
 * Strategy for multi host invoker.
 */
interface MultiHostStrategy {

    //~ Instance Fields ..............................................................................................................................

    String EXECUTION_FAILED_ON_SERVER    = "Execution '%s' failed on server '%s' (#%s out of %s)";
    String NO_FURTHER_SERVER_TO_RETRIEVE = "No further server to retrieve.";
    String WILL_RETRY_ON_SERVER          = "Will retry on server '%s' (#%s out of %s)";

    //~ Methods ......................................................................................................................................

    /** Pick next invoker for retrying a failed invocation. */
    Option<HttpInvoker> next(@NotNull Logger logger, @NotNull HttpInvoker last, @NotNull Invocation<?> failed);

    /** Pick an invoker for a new resource invocation. */
    Option<HttpInvoker> pick();
}
