
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
class InvalidOptionException extends JCommandException {

    //~ Constructors .................................................................................................................................

    public InvalidOptionException(String option) {
        super("Invalid Option '" + option + "'");
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 6738380126194031572L;
}
