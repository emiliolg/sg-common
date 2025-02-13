
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
 * Constants for command.
 */
@SuppressWarnings("DuplicateStringLiteralInspection")
class CmdConstants {

    //~ Constructors .................................................................................................................................

    private CmdConstants() {}

    //~ Static Fields ................................................................................................................................

    static final String   COMMANDS             = "Commands:";
    static final String   OPTIONS              = "Options:";
    static final String   SPACES_6             = "      ";
    static final String   USAGE                = "Usage: ";
    static final String   HELP_REQUESTED_FIELD = "helpRequested";
    static final String[] HELP_DESCRIPTION     = { "Print Help Information and exit" };
    static final String[] DEFAULT_HELP         = { "help", "h" };
}
