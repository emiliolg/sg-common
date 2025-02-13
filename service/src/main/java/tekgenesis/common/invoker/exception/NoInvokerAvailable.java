
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
import tekgenesis.common.invoker.InvokerCommand;

/**
 * No invoker available exception. Returned when no healthy invoker was available.
 */
public class NoInvokerAvailable extends CommandInvocationException {

    //~ Constructors .................................................................................................................................

    /** Default constructor. */
    public NoInvokerAvailable() {
        super(InvokerCommand.class, NO_INVOKER_AVAILABLE);
    }

    /** Default constructor with throwable cause. */
    public NoInvokerAvailable(@NotNull Throwable t) {
        super(InvokerCommand.class, NO_INVOKER_AVAILABLE, t);
    }

    //~ Static Fields ................................................................................................................................

    private static final String NO_INVOKER_AVAILABLE = "No invoker available! Strategy #pick an empty option. " +
                                                       "Probably there was no healthy invoker available at a time.";

    private static final long serialVersionUID = -2399197465929062015L;
}
