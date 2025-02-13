
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.command.exception;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.command.Command;

/**
 * RuntimeException that is thrown when an {@link Command} fails.
 */
@SuppressWarnings("rawtypes")
public class CommandInvocationException extends RuntimeException {

    //~ Instance Fields ..............................................................................................................................

    private final Class<? extends Command> commandClass;

    //~ Constructors .................................................................................................................................

    /** Constructs a new runtime exception with {@code null} as its detail message. */
    public CommandInvocationException() {
        commandClass = null;
    }

    /** Constructs a new runtime exception with the specified detail cause. */
    public CommandInvocationException(Class<? extends Command> commandClass, String message) {
        super(message);
        this.commandClass = commandClass;
    }

    /** Constructs a new runtime exception with the specified detail cause. */
    public CommandInvocationException(Class<? extends Command> commandClass, @NotNull Throwable cause) {
        super(cause);
        this.commandClass = commandClass;
    }

    /** Constructs a new runtime exception with the specified detail cause and message. */
    public CommandInvocationException(Class<? extends Command> commandClass, String message, @NotNull Throwable cause) {
        super(message, cause);
        this.commandClass = commandClass;
    }

    //~ Methods ......................................................................................................................................

    /** The implementing class of the {@link Command}. */
    public Class<? extends Command> getImplementingClass() {
        return commandClass;
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = -1612730373824062015L;
}  // end class CommandInvocationException
