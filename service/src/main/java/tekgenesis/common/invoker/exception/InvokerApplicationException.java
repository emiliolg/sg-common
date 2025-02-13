
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker.exception;

import tekgenesis.common.media.MediaType;
import tekgenesis.common.service.Headers;
import tekgenesis.common.service.Status;

import static tekgenesis.common.Predefined.equal;

/**
 * Return application exception with message as localizable enum.
 */
public class InvokerApplicationException extends InvokerResponseException {

    //~ Instance Fields ..............................................................................................................................

    private final Enum<?> enumeration;
    private final String  msg;

    //~ Constructors .................................................................................................................................

    /**  */
    public InvokerApplicationException(Status status, Headers headers, Enum<?> enumeration, String msg) {
        super(status, headers);
        this.enumeration = enumeration;
        this.msg         = msg;
    }

    //~ Methods ......................................................................................................................................

    /** Return exception enumeration constant. */
    public Enum<?> getEnum() {
        return enumeration;
    }

    @Override
    @SuppressWarnings("DuplicateStringLiteralInspection")
    public String getMessage() {
        return equal(getHeaders().getContentType(), MediaType.APPLICATION_JSON)
               ? "{ \"status\":\"" + status + "\", \"enumeration\":\"" + enumeration + "\", \"msg\":\"" + msg + "\" }"
               : "{ status=" + status + ", enumeration=" + enumeration + ", msg='" + msg + '\'' + " }";
    }

    /** Return exception enumeration message. */
    public String getMsg() {
        return msg;
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 4866895806105092015L;
}  // end class InvokerApplicationException
