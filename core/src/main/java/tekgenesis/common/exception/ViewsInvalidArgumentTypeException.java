
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception thrown when arguments for an xhtml view are invalid.
 */
public class ViewsInvalidArgumentTypeException extends RuntimeException {

    //~ Instance Fields ..............................................................................................................................

    private final int          columnNumber;
    private final List<String> errors;
    private final String       fullPath;
    private final int          lineNumber;

    //~ Constructors .................................................................................................................................

    /** Default Constructor. */
    public ViewsInvalidArgumentTypeException() {
        fullPath     = null;
        errors       = new ArrayList<>();
        columnNumber = 0;
        lineNumber   = 0;
    }

    /** With cause message constructor. */
    public ViewsInvalidArgumentTypeException(String cause, List<String> errors, String fullPath, int columnNumber, int lineNumber) {
        super(cause);
        this.errors       = errors;
        this.fullPath     = fullPath;
        this.columnNumber = columnNumber;
        this.lineNumber   = lineNumber;
    }

    //~ Methods ......................................................................................................................................

    /** Get column number. */
    public int getColumnNumber() {
        return columnNumber;
    }

    /** Get all errors in params attribute. */
    public List<String> getErrors() {
        return errors;
    }

    /** Get full path of views file. */
    public String getFullPath() {
        return fullPath;
    }

    /** Get line number. */
    public int getLineNumber() {
        return lineNumber;
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = -2535120325766416280L;
}  // end class ViewsInvalidArgumentTypeException
