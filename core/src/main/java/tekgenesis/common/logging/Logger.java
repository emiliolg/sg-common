
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.logging;

import java.util.function.Supplier;

import org.intellij.lang.annotations.PrintFormat;
import org.jetbrains.annotations.NotNull;

import tekgenesis.common.annotation.GwtIncompatible;

import static java.lang.String.format;

import static tekgenesis.common.logging.Logger.Level.*;

/**
 * java.util.logging.Logger facade.
 */
@SuppressWarnings("WeakerAccess")  // Public API
public class Logger {

    //~ Instance Fields ..............................................................................................................................

    private final java.util.logging.Logger logger;

    //~ Constructors .................................................................................................................................

    protected Logger(java.util.logging.Logger logger) {
        this.logger = logger;
    }

    //~ Methods ......................................................................................................................................

    /** Logs with debug level. */
    public void debug(String msg) {
        log(DEBUG, msg);
    }

    /** Logs an exception with debug level and msg. */
    public void debug(Throwable t) {
        log(DEBUG, t);
    }

    /** Logs with debug level using the specified supplier. */
    public void debug(@NotNull final Supplier<String> msgSupplier) {
        log(DEBUG, msgSupplier);
    }

    /** Logs an exception with debug level using the specified supplier for message. */
    public void debug(@NotNull final Supplier<String> msgSupplier, final Throwable t) {
        log(DEBUG, msgSupplier, t);
    }

    /** Logs an exception with debug level and msg. */
    public void debug(String msg, Throwable caught) {
        log(DEBUG, msg, caught);
    }

    /** Logs a message with format with debug level. */
    @GwtIncompatible public void debug(@PrintFormat String msg, Object... args) {
        log(DEBUG, msg, args);
    }

    /** Logs with error level. */
    public void error(String msg) {
        log(ERROR, msg);
    }

    /** Logs an exception with error level and msg. */
    public void error(Throwable t) {
        log(ERROR, t);
    }

    /** Logs with error level using the specified supplier. */
    public void error(@NotNull final Supplier<String> msgSupplier) {
        log(ERROR, msgSupplier);
    }

    /** Logs an exception with error level using the specified supplier for message. */
    public void error(@NotNull final Supplier<String> msgSupplier, final Throwable t) {
        log(ERROR, msgSupplier, t);
    }

    /** Logs an exception with error level and msg. */
    public void error(String msg, Throwable caught) {
        log(ERROR, msg, caught);
    }

    /** Logs a message with format with debug level. */
    @GwtIncompatible public void error(@PrintFormat String msg, Object... args) {
        log(ERROR, msg, args);
    }

    /** Logs with info level. */
    public void info(String msg) {
        log(INFO, msg);
    }

    /** Logs an exception with info level and msg. */
    public void info(Throwable t) {
        log(INFO, t);
    }

    /** Logs with info level using the specified supplier. */
    public void info(@NotNull final Supplier<String> msgSupplier) {
        log(INFO, msgSupplier);
    }

    /** Logs an exception with info level using the specified supplier for message. */
    public void info(@NotNull final Supplier<String> msgSupplier, final Throwable t) {
        log(INFO, msgSupplier, t);
    }

    /** Logs an exception with info level and msg. */
    public void info(String msg, Throwable caught) {
        log(INFO, msg, caught);
    }

    /** Logs a message with format with info level. */
    @GwtIncompatible public void info(@PrintFormat String msg, Object... args) {
        log(INFO, msg, args);
    }

    /** Logs with the specified level. */
    public void log(Level level, String msg) {
        logger.log(level.jLevel, msg);
    }

    /** Logs with the specified level and the specified throwable. */
    public void log(Level level, Throwable throwable) {
        logger.log(level.jLevel, throwable.getMessage(), throwable);
    }

    /** Logs with the specified level. */
    public void log(Level level, Supplier<String> msg) {
        logger.log(level.jLevel, msg);
    }

    /** Logs with the specified level and the specified throwable. */
    public void log(Level level, String msg, Throwable throwable) {
        logger.log(level.jLevel, msg, throwable);
    }

    /** Logs with the specified level and the specified throwable. */
    public void log(Level level, Supplier<String> msgSupplier, Throwable throwable) {
        logger.log(level.jLevel, throwable, msgSupplier);
    }

    /** Logs with the specified level. */

    @GwtIncompatible public void log(Level level, @PrintFormat String formatString, Object... args) {
        logger.log(level.jLevel, format(formatString, args));
    }

    /** Logs with warning level. */
    public void warning(String msg) {
        log(WARNING, msg);
    }

    /** Logs an exception with warning level and msg. */
    public void warning(Throwable t) {
        log(WARNING, t);
    }

    /** Logs with warning level using the specified supplier. */
    public void warning(@NotNull final Supplier<String> msgSupplier) {
        log(WARNING, msgSupplier);
    }

    /** Logs an exception with warning level using the specified supplier for message. */
    public void warning(@NotNull final Supplier<String> msgSupplier, final Throwable t) {
        log(WARNING, msgSupplier, t);
    }

    /** Logs an exception with warning level and msg. */
    public void warning(String msg, Throwable caught) {
        log(WARNING, msg, caught);
    }

    /** Logs a message with format with warning level. */
    @GwtIncompatible public void warning(@PrintFormat String msg, Object... args) {
        log(WARNING, msg, args);
    }

    /** Returns true if the Logger is logging at the specified level. */
    public boolean isLoggable(Level level) {
        return logger.isLoggable(level.jLevel);
    }

    /** Get the log level. */
    public Level getLevel() {
        return from(logger.getLevel());
    }

    /** Set the log level. */
    public void setLevel(Level level) {
        logger.setLevel(level.jLevel);
    }

    /** Return the logger Name. */
    public String getName() {
        return logger.getName();
    }

    /** Return the underlying logger. */
    public java.util.logging.Logger getTargetLogger() {
        return logger;
    }

    //~ Methods ......................................................................................................................................

    /** Returns a Logger for the given class. */
    public static Logger getLogger(Class<?> clazz) {
        return new Logger(java.util.logging.Logger.getLogger(clazz.getName()));
    }

    //~ Enums ........................................................................................................................................

    public enum Level {
        DEBUG(java.util.logging.Level.FINE), INFO(java.util.logging.Level.INFO), WARNING(java.util.logging.Level.WARNING),

        ERROR(java.util.logging.Level.SEVERE), OFF(java.util.logging.Level.OFF);
        private final java.util.logging.Level jLevel;

        Level(java.util.logging.Level level) {
            jLevel = level;
        }

        /** Creates a Level from {@link java.util.logging.Level}. */
        public static Level from(java.util.logging.Level level) {
            for (final Level l : values()) {
                if (l.jLevel.equals(level)) return l;
            }
            return DEBUG;
        }
    }
}  // end class Logger
