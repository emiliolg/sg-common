
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

import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;

/**
 * Instance that supports logging.
 */
public interface LoggableInstance {

    //~ Methods ......................................................................................................................................

    /** Log the message as an debug. */
    default void logDebug(@NotNull final Supplier<String> supplier) {
        logger().debug(supplier);
    }

    /** Log the message as an debug. */
    default void logDebug(final String msg, Object... args) {
        logger().debug(format(msg, args));
    }

    /** Log the message as an error. */
    default void logError(@NotNull Throwable e) {
        logger().error(e);
    }

    /** Log the message as an error. */
    default void logError(final String msg, Object... args) {
        logger().error(format(msg, args));
    }

    /** Returns the logger. */
    Logger logger();

    /** Log the message as an info. */
    default void logInfo(@NotNull final Supplier<String> supplier) {
        logger().info(supplier);
    }

    /** Log the message as an info. */
    default void logInfo(@NotNull final String msg) {
        logger().info(msg);
    }

    /** Log the message as an info. */
    default void logInfo(final String msg, Object... args) {
        logger().info(format(msg, args));
    }

    /** Log the message as an warning. */
    default void logWarning(@NotNull Throwable e) {
        logger().warning(e);
    }

    /** Log the message as an warning. */
    default void logWarning(@NotNull String msg) {
        logger().warning(msg);
    }

    /** Log the message as an warning. */
    default void logWarning(@NotNull final Supplier<String> supplier) {
        logger().warning(supplier);
    }

    /** Log the message as an warning. */
    default void logWarning(final String msg, Object... args) {
        logger().warning(format(msg, args));
    }
}  // end interface LoggableInstance
