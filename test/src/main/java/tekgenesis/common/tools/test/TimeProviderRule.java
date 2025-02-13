
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.tools.test;

import java.util.function.LongSupplier;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import tekgenesis.common.core.DateTime;

/**
 * A Rule to mute specified loggers.
 */
public class TimeProviderRule implements TestRule {

    //~ Instance Fields ..............................................................................................................................

    private long currentTime;

    //~ Constructors .................................................................................................................................

    /** Create the Rule with 0 as current time. */
    public TimeProviderRule() {
        currentTime = 0;
    }

    /** Create the Rule with the specified DateTime. */
    public TimeProviderRule(DateTime currentTime) {
        this.currentTime = currentTime.toMilliseconds();
    }

    //~ Methods ......................................................................................................................................

    @Override public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override public void evaluate()
                throws Throwable
            {
                final LongSupplier oldProvider = DateTime.setTimeSupplier(TimeProviderRule.this::getCurrentTime);
                try {
                    base.evaluate();
                }
                finally {
                    DateTime.setTimeSupplier(oldProvider);
                }
            }
        };
    }

    /** Increment current time. */
    public void increment(final long inc) {
        currentTime += inc;
    }

    /** Get rule current time. */
    public long getCurrentTime() {
        return currentTime;
    }

    /** Set rule current time. */
    public void setCurrentTime(final long currentTIme) {
        currentTime = currentTIme;
    }
}  // end class TimeProviderRule
