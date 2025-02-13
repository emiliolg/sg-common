
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.Test;

import tekgenesis.common.env.context.Context;
import tekgenesis.common.env.logging.LogConfig;
import tekgenesis.common.env.properties.LoggingProps;
import tekgenesis.common.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

import static tekgenesis.common.logging.Logger.Level.*;

public class LoggingTest {

    //~ Instance Fields ..............................................................................................................................

    final AtomicInteger counter = new AtomicInteger();

    //~ Methods ......................................................................................................................................

    @Test public void testLog()
        throws IOException
    {
        init();

        assertThat(logTest.isLoggable(DEBUG)).isFalse();

        shouldNotLog(DEBUG, "Debug message");

        shouldLog(INFO, "Info message");

        assertThat(logHandler.logs.get(0).getMessage()).isEqualTo("Info message");

        logTest.info("Info message 2");

        assertThat(logHandler.logs.size()).isEqualTo(2);
        assertThat(logHandler.logs.get(1).getMessage()).isEqualTo("Info message 2");

        shouldLog(WARNING, "Warning message");
        assertThat(logHandler.logs.get(2).getMessage()).isEqualTo("Warning message");

        logTest.getTargetLogger().setLevel(Level.WARNING);
        assertThat(logTest.isLoggable(WARNING)).isTrue();

        shouldNotLog(INFO, "This log shouldn't be logged.");

        shouldNotLog(DEBUG, "Debug message not logged");

        shouldLog(WARNING, "Warning message 2");

        logTest.getTargetLogger().setLevel(Level.FINE);
        assertThat(logTest.isLoggable(DEBUG));

        shouldLog(DEBUG, "Debug message logged");
        assertThat(logHandler.logs.get(4).getMessage()).isEqualTo("Debug message logged");

        assertThat(logTest.isLoggable(WARNING)).isTrue();
        logTest.getTargetLogger().setLevel(Level.OFF);
        assertThat(logTest.isLoggable(WARNING)).isFalse();
        assertThat(logTest.isLoggable(ERROR)).isFalse();

        shouldNotLog(ERROR, "Error message");

        logTest.info(() -> "Info message not logged");
        assertThat(logHandler.logs.size()).isEqualTo(5);

        logTest.warning("Warning message not logged");
        assertThat(logHandler.logs.size()).isEqualTo(5);

        logTest.warning("Debug message not logged");
        assertThat(logHandler.logs.size()).isEqualTo(5);
    }  // end method testLog

    private void init() {
        final LoggingProps props = new LoggingProps();
        props.rootLoggerLevel = ch.qos.logback.classic.Level.INFO;
        Context.getEnvironment().put(props);
        logTest.getTargetLogger().addHandler(logHandler);
        LogConfig.start();
    }

    private void log(Logger.Level level, String message) {
        switch (level) {
        case DEBUG:
            logTest.debug(() -> {
                counter.incrementAndGet();
                return message;
            });
            break;
        case INFO:
            logTest.info(() -> {
                counter.incrementAndGet();
                return message;
            });
            break;
        case WARNING:
            logTest.warning(() -> {
                counter.incrementAndGet();
                return message;
            });
            break;
        case ERROR:
            logTest.error(() -> {
                counter.incrementAndGet();
                return message;
            });
            break;
        case OFF:
            break;
        }
    }

    private void shouldLog(Logger.Level level, String message) {
        final int before        = logHandler.logs.size();
        final int beforeCounter = counter.get();
        log(level, message);
        assertThat(logHandler.logs.size()).isEqualTo(before + 1);
        assertThat(counter.get()).isEqualTo(beforeCounter + 1);
    }

    private void shouldNotLog(Logger.Level level, String message) {
        final int before        = logHandler.logs.size();
        final int beforeCounter = counter.get();
        log(level, message);
        assertThat(logHandler.logs.size()).isEqualTo(before);
        assertThat(counter.get()).isEqualTo(beforeCounter);
    }

    //~ Static Fields ................................................................................................................................

    private static final MyHandler logHandler = new MyHandler();
    private static final Logger    logTest    = Logger.getLogger(LoggingTest.class);

    //~ Inner Classes ................................................................................................................................

    static class MyHandler extends Handler {
        List<LogRecord> logs = new ArrayList<>();

        @Override public void close() {}

        @Override public void flush() {}

        @Override public void publish(LogRecord record) {
            logs.add(record);
        }
    }
}  // end class LoggingTest
