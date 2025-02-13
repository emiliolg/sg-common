
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
class NoCommandException extends JCommandException {

    //~ Constructors .................................................................................................................................

    public NoCommandException() {
        super("No command specified.");
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 1780192930447839282L;
}
