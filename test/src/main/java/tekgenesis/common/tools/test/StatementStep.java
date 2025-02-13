
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.tools.test;

/**
 * Statement execution step.
 */
@FunctionalInterface public interface StatementStep {

    //~ Methods ......................................................................................................................................

    /** Execute it. */
    void execute()
        throws Exception;
}
