
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.tools.test;

import org.junit.runners.model.Statement;

/**
 * Statement Builder.
 */
public final class StatementBuilder {

    //~ Constructors .................................................................................................................................

    private StatementBuilder() {}

    //~ Methods ......................................................................................................................................

    /** Creates a new statement. */
    public static Statement create(final Statement statement, final StatementStep after, final StatementStep before) {
        return new Statement() {
            @Override public void evaluate()
                throws Throwable
            {
                before.execute();
                try {
                    statement.evaluate();
                }
                finally {
                    after.execute();
                }
            }
        };
    }
}
