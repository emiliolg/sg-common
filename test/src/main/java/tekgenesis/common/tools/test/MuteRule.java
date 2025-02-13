
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.tools.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import tekgenesis.common.core.Option;
import tekgenesis.common.core.Tuple;
import tekgenesis.common.logging.Logger;

import static tekgenesis.common.Predefined.option;
import static tekgenesis.common.core.Tuple.tuple;

/**
 * A Rule to mute specified loggers.
 */
public class MuteRule implements TestRule {

    //~ Instance Fields ..............................................................................................................................

    private final Class<?>[] initialLoggers;

    private final Map<String, Tuple<Logger, Option<Logger.Level>>> mutedLoggers = new HashMap<>();

    //~ Constructors .................................................................................................................................

    /** Create the Rule with the specified loggers initially muted. */
    public MuteRule(Class<?>... loggers) {
        initialLoggers = loggers;
    }

    //~ Methods ......................................................................................................................................

    @Override public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override public void evaluate()
                throws Throwable
            {
                mutedLoggers.clear();
                for (final Class<?> l : initialLoggers)
                    mute(l);
                try {
                    base.evaluate();
                }
                finally {
                    // Restore loggers
                    mutedLoggers.values().forEach(tekgenesis.common.tools.test.MuteRule::restore);
                }
            }
        };
    }

    /** Mute the specified logger. */
    public void mute(Class<?> logger) {
        final Logger       l     = Logger.getLogger(logger);
        final Logger.Level level = l.getLevel();
        l.setLevel(Logger.Level.OFF);
        mutedLoggers.put(logger.getName(), tuple(l, option(level)));
    }
    /** Restore the specified logger. */
    @SuppressWarnings("UnusedDeclaration")
    public void restore(Class<?> logger) {
        restore(mutedLoggers.remove(logger.getName()));
    }

    //~ Methods ......................................................................................................................................

    private static void restore(Tuple<Logger, Option<Logger.Level>> e) {
        if (e != null) e.second().ifPresent(level -> e.first().setLevel(level));
    }
}  // end class MuteRule
