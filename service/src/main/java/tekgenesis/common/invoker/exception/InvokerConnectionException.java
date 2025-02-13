
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker.exception;

import java.net.*;

import tekgenesis.common.command.exception.CommandInvocationException;
import tekgenesis.common.invoker.CallResource;
import tekgenesis.common.invoker.HttpInvoker;
import tekgenesis.common.invoker.InvokerCommand;
import tekgenesis.common.invoker.PathResource;

/**
 * Http Invocation exception raised when using {@link HttpInvoker}, {@link CallResource}, and
 * {@link PathResource}.
 */
public class InvokerConnectionException extends CommandInvocationException {

    //~ Constructors .................................................................................................................................

    /** Construct HttpInvocationException with throwable. */
    public InvokerConnectionException(Throwable cause) {
        super(InvokerCommand.class, cause);
    }

    //~ Methods ......................................................................................................................................

    /** Return true if invocation exception has connect failure cause. */
    public boolean isConnectException() {
        return ConnectException.class.isAssignableFrom(getCause().getClass());
    }

    /** Return true if invocation exception has connect timeout cause. */
    public boolean isConnectTimeoutException() {
        return SocketTimeoutException.class.isAssignableFrom(getCause().getClass());
    }

    /** Return true if invocation exception has unknown host cause. */
    public boolean isUnknownHostException() {
        return UnknownHostException.class.isAssignableFrom(getCause().getClass());
    }

    /** Return true if invocation exception has uri syntax cause. */
    public boolean isUriSyntaxException() {
        return URISyntaxException.class.isAssignableFrom(getCause().getClass()) ||
               MalformedURLException.class.isAssignableFrom(getCause().getClass());
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = -8345438874205122012L;
}
