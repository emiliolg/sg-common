
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service.exception;

/**
 * Message conversion exception.
 */
public class MessageConversionException extends RuntimeException {

    //~ Constructors .................................................................................................................................

    /** Message conversion exception constructor with cause. */
    public MessageConversionException(Throwable cause) {
        super(cause);
    }

    /** Message conversion exception constructor with message. */
    public MessageConversionException(String message) {
        super(message);
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 998371496631072016L;
}
