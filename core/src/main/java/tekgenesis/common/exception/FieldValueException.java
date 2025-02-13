
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.exception;

import tekgenesis.common.collections.ImmutableList;

/**
 * Exceptions handling resources.
 */
public class FieldValueException extends RuntimeException {

    //~ Constructors .................................................................................................................................

    /** Creates a FieldValueException with an empty message. */
    public FieldValueException() {
        this("");
    }

    private FieldValueException(String message) {
        super(message);
    }

    //~ Methods ......................................................................................................................................

    /**
     * Run the lambda capturing all Errors instead of throwing FieldValueException and then return
     * the list of errors captured.
     */
    public static ImmutableList<String> captureErrors(Runnable r) {
        try {
            final ImmutableList.Builder<String> errors = ImmutableList.builder();
            currentCapture.set(errors);
            r.run();
            return errors.build();
        }
        finally {
            currentCapture.remove();
        }
    }

    /** Creates a negative error on given field with the given value. */
    public static void negativeError(String fieldName, Object value) {
        dataError(fieldName, value, "cannot be negative.");
    }

    /** Creates a precision error on given field with the given value. */
    public static void precisionError(String fieldName, Object value, int valuePrecision, int fieldPrecision) {
        dataError(fieldName, value, "precision " + valuePrecision + " exceeds " + fieldPrecision + ".");
    }

    private static void dataError(String fieldName, Object value, String msgTail) {
        final String                        message = "Field '" + fieldName + "' value '" + value + "' " + msgTail;
        final ImmutableList.Builder<String> capture = currentCapture.get();
        if (capture != null) capture.add(message);
        else throw new FieldValueException(message);
    }

    //~ Static Fields ................................................................................................................................

    private static final transient ThreadLocal<ImmutableList.Builder<String>> currentCapture = new ThreadLocal<>();

    private static final long serialVersionUID = -830253029346947607L;
}  // end class FieldValueException
