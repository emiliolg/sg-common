
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.cmd;

import tekgenesis.common.core.Constants;

/**
 * InvalidOption.
 */
class MissingArgumentException extends JCommandException {

    //~ Constructors .................................................................................................................................

    public MissingArgumentException(final Opt opt, String name) {
        super(
            opt.isMain() ? "Missing " + (opt.getDescription().isEmpty() ? Constants.ARGUMENT : opt.getDescription().get(0))
                         : "Missing Argument for Option '" + name + "'");
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 2698536139483309645L;
}
