
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.tools.test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A Rule to mute specified loggers.
 */
public class LoggerCollectRule implements TestRule {

    //~ Instance Fields ..............................................................................................................................

    private final Class<?>[] initialLoggers;

    private final List<String> loggerErrors = new ArrayList<>();

    private final Handler collectorHandler = new Handler() {
            @Override public void publish(final LogRecord record) {
                loggerErrors.add(record.getMessage());
            }

            @Override public void flush() {}

            @Override public void close()
                throws SecurityException {}
        };

    private final List<Logger> loggers = new ArrayList<>();

    //~ Constructors .................................................................................................................................

    /** Create the Rule with the specified loggers initially muted. */
    public LoggerCollectRule(Class<?>... loggers) {
        initialLoggers = loggers;
    }

    //~ Methods ......................................................................................................................................

    @Override public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override public void evaluate()
                throws Throwable
            {
                for (final Class<?> l : initialLoggers)
                    collect(l);
                try {
                    base.evaluate();
                }
                finally {
                    /* Restore loggers */
                    for (final Logger l : loggers) {
                        l.setUseParentHandlers(true);
                        l.removeHandler(collectorHandler);
                    }
                }
            }
        };
    }

    /** Collect the output of the specified logger. */
    public void collect(Class<?> c) {
        final Logger l = tekgenesis.common.logging.Logger.getLogger(c).getTargetLogger();
        l.setUseParentHandlers(false);
        l.addHandler(collectorHandler);
        loggers.add(l);
    }

    /** Return the logger errors. */
    public List<String> getLoggerErrors() {
        return loggerErrors;
    }
}  // end class LoggerCollectRule
