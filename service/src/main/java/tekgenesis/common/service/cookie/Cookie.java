
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
 * Cookie for service messages.
 */
public interface Cookie {

    //~ Methods ......................................................................................................................................

    /** Returns the domain name of this cookie, or null if has no domain. */
    @Nullable String getDomain();

    /**
     * Returns true if the browser is sending cookies only over a secure protocol, or false if the
     * browser can send cookies using any protocol.
     */
    boolean isSecure();

    /**
     * Returns the maximum age in seconds of this cookie. By default, -1 is returned. A negative
     * value means that the cookie is not stored persistently and will be deleted when the Web
     * browser exits. A zero value causes the cookie to be deleted.
     */
    long getMaxAge();

    /** Returns the name of this cookie. */
    @NotNull String getName();

    /**
     * Returns the path on the server to which the browser returns this cookie. The cookie is
     * visible to all subpaths on the server.
     */
    @Nullable String getPath();

    /** Returns the value of this cookie. */
    @NotNull String getValue();

    /**
     * Returns true if cookie is http only and may not be accessed by scripts (subject to browser
     * support).
     */
    boolean isHttpOnly();
}  // end interface Cookie
