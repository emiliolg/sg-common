
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service.cookie;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Mutable version of {@link Cookie}.
 */
public interface MutableCookie extends Cookie {

    //~ Methods ......................................................................................................................................

    /** Specifies the domain within which this cookie should be presented. */
    MutableCookie withDomain(@Nullable String domain);

    /**
     * Indicates whether the cookie may be accessed by only http, excluding scripts (subject to
     * browser support).
     */
    MutableCookie withHttpOnly(boolean flag);

    /**
     * Specifies the maximum age in seconds for this cookie. A negative value means that the cookie
     * is not stored persistently and will be deleted when the Web browser exits. A zero value
     * causes the cookie to be deleted.
     */
    MutableCookie withMaxAge(long seconds);

    /** Specifies a path for the cookie to which the client should return the cookie. */
    MutableCookie withPath(@Nullable String uri);

    /**
     * Indicates to the browser whether the cookie should only be sent using a secure protocol, such
     * as HTTPS or SSL.
     */
    MutableCookie withSecure(boolean flag);

    /** Assigns a new value to this cookie. */
    MutableCookie withValue(@NotNull String newValue);
}
