
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.serializer;

/**
 * An Exception to use when Serialization has Problems.
 */
public class SerializerException extends RuntimeException {

    //~ Constructors .................................................................................................................................

    /** Default contructor. */
    public SerializerException() {}

    /** Construct the Expression from a message. */
    public SerializerException(final String message) {
        super(message);
    }

    /** Construct the Expression from another Exception. */
    public SerializerException(final Throwable cause) {
        super(cause);
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 2724952206035818156L;
}
