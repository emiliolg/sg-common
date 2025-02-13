
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.exception;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.core.Enumeration;
import tekgenesis.common.core.Option;
import tekgenesis.common.util.Message;

/**
 * Return application exception with message as localizable enum.
 */
public class ApplicationException extends RuntimeException {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final Message message;

    //~ Constructors .................................................................................................................................

    /** Application exception constructor with enum message. */
    public ApplicationException(@NotNull Enumeration<?, String> msg) {
        this(msg, EMPTY_PARAMETERS);
    }

    /** Application exception constructor with enum message and parameters. */
    public ApplicationException(@NotNull Message message) {
        this.message = message;
    }

    /** Application exception constructor with enum message and parameters. */
    public ApplicationException(@NotNull Enumeration<?, ?> msg, @NotNull Object... parameters) {
        message = Message.create(msg, parameters);
    }

    //~ Methods ......................................................................................................................................

    /** Return exception enum arguments. */
    @NotNull public Option<Object[]> getArguments() {
        return message.getArguments();
    }

    /** Return exception enum message. */
    @NotNull public Enumeration<?, ?> getEnumeration() {
        return message.getEnumeration();
    }

    /** Return exception label message. */
    @Override public String getMessage() {
        return message.label();
    }

    //~ Static Fields ................................................................................................................................

    private static final Object[] EMPTY_PARAMETERS = {};

    private static final long serialVersionUID = 4866895806105092015L;
}  // end class ApplicationException
