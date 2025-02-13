
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
 * Exceptions handling resources. GWT only version.
 */
public class FieldValueException extends RuntimeException {

    //~ Constructors .................................................................................................................................

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
        final ImmutableList.Builder<String> errors = ImmutableList.builder();
        r.run();
        return errors.build();
    }

    public static void negativeError(String fieldName, Object value) {
        dataError(fieldName, value, "cannot be negative.");
    }

    public static void precisionError(String fieldName, Object value, int valuePrecision, int fieldPrecision) {
        dataError(fieldName, value, "precision " + valuePrecision + " exceeds " + fieldPrecision + ".");
    }

    private static void dataError(String fieldName, Object value, String msgTail) {
        final String message = "Field '" + fieldName + "' value '" + value + "' " + msgTail;
        throw new FieldValueException(message);
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = -830253029346947607L;
}  // end class FieldValueException
