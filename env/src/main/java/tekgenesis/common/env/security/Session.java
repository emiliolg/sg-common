
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.security;

import java.io.Serializable;

/**
 * Manage the application session.
 */
public interface Session {

    //~ Methods ......................................................................................................................................

    /**
     * Authenticate the current session with the given authentication token.
     *
     * @param  authenticationToken  Authentication token to authenticate the session.
     */
    void authenticate(Object authenticationToken);

    /**
     * Authenticate the current session with the given user and password.
     *
     * @param  user      User to Authenticate.
     * @param  password  Password of the given user to authenticate.
     */
    void authenticate(String user, String password);

    /**
     * Authenticate the current session with the given user and password.
     *
     * @param  user        User to Authenticate.
     * @param  password    Password of the given user to authenticate.
     * @param  rememberMe  remember user session
     */
    void authenticate(String user, String password, boolean rememberMe);

    /** Logout the current session. */
    void logout();

    /** Return audit user. */
    String getAuditUser();

    /** Set audit user. */
    void setAuditUser();

    /** Returns current client ip. */
    String getClientIp();

    /**
     * Returns if the principal of the session proved their identity during their current session by
     * providing valid credentials matching those known to the system.
     *
     * @return  <code>true</code> if the principal was authenticated
     */
    boolean isAuthenticated();

    /**
     * Returns {@code true} if this {@code Subject} has an identity (it is not anonymous) and the
     * identity is remembered from a successful authentication during a previous session.
     *
     * @return  <code>true</code> if the principal is remembered
     */
    boolean isRemembered();

    /**
     * Returns the session id.
     *
     * @return  Session identification
     */
    Serializable getId();

    /**
     * Returns the principal associated to the session.
     *
     * @return  Principal associated to the session
     */
    Principal getPrincipal();

    /** Returns the session timeout. */
    int getTimeout();

    /** Set session timeout. */
    void setTimeout(int seconds);
}  // end interface Session
