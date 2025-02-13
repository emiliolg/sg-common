
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import tekgenesis.common.collections.Seq;
import tekgenesis.common.service.Headers;
import tekgenesis.common.service.Status;
import tekgenesis.common.service.cookie.Cookie;

/**
 * Invoker typed result containing body (if any), headers, and status.
 */
public interface HttpInvokerResult<T> {

    //~ Methods ......................................................................................................................................

    /** Return the body of the result. */
    T get();

    /** Return message cookies. */
    Seq<Cookie> getCookies();

    /** Return message headers. */
    Headers getHeaders();

    /** Get invoker that served resource. */
    HttpInvoker getInvoker();

    /** Get the status code. */
    Status getStatus();
}
