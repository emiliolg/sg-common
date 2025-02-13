
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.command.exception;

import tekgenesis.common.command.AbstractCommand;

/**
 * RuntimeException that is thrown when an {@link AbstractCommand} fails with timeout.
 */
public class CommandTimeoutException extends RuntimeException {

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = -6332928660955489990L;
}
