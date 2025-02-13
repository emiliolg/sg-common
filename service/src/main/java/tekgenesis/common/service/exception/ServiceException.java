
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service.exception;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.service.Status;

import static tekgenesis.common.collections.Colls.mkString;
import static tekgenesis.common.service.Status.BAD_REQUEST;
import static tekgenesis.common.service.Status.LENGTH_REQUIRED;

/**
 * Generic Service Exception.
 */
public class ServiceException extends RuntimeException {

    //~ Instance Fields ..............................................................................................................................

    private final Status status;

    //~ Constructors .................................................................................................................................

    private ServiceException(Status status, String msg) {
        super(msg);
        this.status = status;
    }

    //~ Methods ......................................................................................................................................

    /** Return service exception status. */
    public Status getStatus() {
        return status;
    }

    //~ Methods ......................................................................................................................................

    /** Service exception caused by invalid parameter. */
    public static ServiceException invalidParameter(String name, String kind, String value) {
        return new ServiceException(BAD_REQUEST, String.format("Invalid parameter '%s' of type '%s' with value: %s", name, kind, value));
    }

    /** Service exception caused by unspecified parameters. */
    public static ServiceException requiredParameters(@NotNull List<String> parameters) {
        final String s = "Missing required " + (parameters.size() > 1 ? "parameters: " : "parameter: ");
        return new ServiceException(BAD_REQUEST, s + mkString(parameters, ","));
    }

    /** Service exception caused by unspecified content length. */
    public static ServiceException unspecifiedContentLength() {
        return new ServiceException(LENGTH_REQUIRED, "Unspecified Content-Length. Expected Content-Length for POST/PUT methods");
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 2938773822301041984L;
}  // end class ServiceException
