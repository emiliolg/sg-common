
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
 * InvalidOption.
 */
class InvalidCommandException extends JCommandException {

    //~ Constructors .................................................................................................................................

    public InvalidCommandException(final String cmdName) {
        super("Invalid command: '" + cmdName + "'.");
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 6042773597100015405L;
}
