
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.cmd;

/**
 * Base class of CommandExecutor Exceptions.
 */
class JCommandException extends RuntimeException {

    //~ Constructors .................................................................................................................................

    JCommandException(String message) {
        super(message);
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 6213140747655047556L;
}
