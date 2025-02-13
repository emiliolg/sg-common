
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import tekgenesis.common.service.Status;

/**
 * Exception thrown by MultiHostStrategy for handling.
 */
public class StrategyException extends RuntimeException {

    //~ Instance Fields ..............................................................................................................................

    private final String message;
    private final Status status;

    //~ Constructors .................................................................................................................................

    /** Constructor with message. */
    public StrategyException(Status status, String message) {
        this.message = message;
        this.status  = status;
    }

    //~ Methods ......................................................................................................................................

    @Override public String getMessage() {
        return "Request failed with status: " + status + " " + message;
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = -8966803168494149716L;
}
