
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.rest.exception;

import java.net.HttpURLConnection;

import org.jetbrains.annotations.Nullable;

import tekgenesis.common.command.exception.CommandInvocationException;
import tekgenesis.common.invoker.InvokerCommand;
import tekgenesis.common.rest.RestMessages;
import tekgenesis.common.service.Status;

import static tekgenesis.common.Predefined.notEmpty;

/**
 * REST Invocation Exception.
 */
@SuppressWarnings("WeakerAccess")
public class RestInvocationException extends CommandInvocationException {

    //~ Instance Fields ..............................................................................................................................

    private final int    errorCode;
    private final String errorMsg;

    //~ Constructors .................................................................................................................................

    /** Construct {@link RestInvocationException} with given code, message, and error. */
    public RestInvocationException(Status errorCode, String msg, @Nullable String errorMsg) {
        this(errorCode.code(), msg, errorMsg);
    }

    /** Construct {@link RestInvocationException} with given code, message, and error. */
    @SuppressWarnings("WeakerAccess")
    public RestInvocationException(int errorCode, String msg, @Nullable String error) {
        super(InvokerCommand.class, RestMessages.REST_MSGS.httpInvocationError(msg, errorCode, notEmpty(error, "")));
        this.errorCode = errorCode;
        errorMsg       = error;
    }

    /** Construct {@link RestInvocationException} with given code, message, error, and cause. */
    @SuppressWarnings("WeakerAccess")
    public RestInvocationException(Status errorCode, String msg, @Nullable String error, @Nullable Throwable ex) {
        this(errorCode.code(), msg, error);
    }

    //~ Methods ......................................................................................................................................

    /** @return  The http error code */
    public int getErrorCode() {
        return errorCode;
    }

    /** @return  the error msg */
    @Nullable public String getErrorMsg() {
        return errorMsg;
    }

    //~ Methods ......................................................................................................................................

    /**
     * Build a REST Exception based on the http error code.
     *
     * @param   errorCode  the http error code
     * @param   errorMsg   the error message
     *
     * @return  a RESTException
     */
    public static RestInvocationException createException(int errorCode, String errorMsg) {
        switch (errorCode) {
        case HttpURLConnection.HTTP_INTERNAL_ERROR:
            return new InternalServerErrorRestException(errorMsg);
        case HttpURLConnection.HTTP_BAD_GATEWAY:
            return new BadGatewayRestException(MSG);
        case HttpURLConnection.HTTP_UNAVAILABLE:
            return new ServiceUnavailableRestException(errorMsg);
        case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
            return new GatewayTimeoutRestException(MSG);
        case HttpURLConnection.HTTP_BAD_REQUEST:
            return new BadRequestRestException(errorMsg);
        // noinspection MagicNumber
        case 423:
            return new LockedRestException(errorMsg);
        case HttpURLConnection.HTTP_NOT_FOUND:
            return new NotFoundRestException(errorMsg);
        default:
            return new RestInvocationException(errorCode, "Invocation Failed", errorMsg);
        }
    }

    //~ Static Fields ................................................................................................................................

    private static final String MSG = "El servicio Ideafix no se encuentra disponible.";

    private static final long serialVersionUID = 6923315393719597713L;
}  // end class RestInvocationException
