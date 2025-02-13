
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.cmd;

import java.util.List;

/**
 * InvalidOption.
 */
class ExtraArgumentsException extends JCommandException {

    //~ Constructors .................................................................................................................................

    public ExtraArgumentsException(List<String> args) {
        super("Extra Arguments '" + args + "'");
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 8320795469964913402L;
}
