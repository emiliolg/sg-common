
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.security;

import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.util.ThreadContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.env.context.Context;

import static tekgenesis.common.Predefined.isEmpty;
import static tekgenesis.common.Predefined.isNotEmpty;
import static tekgenesis.common.env.context.Context.getContext;

/**
 * Class to manage the Security Authentication and Authorization.
 */
public final class SecurityUtils {

    //~ Constructors .................................................................................................................................

    private SecurityUtils() {}

    //~ Methods ......................................................................................................................................

    /** Clear system surrogate. */
    public static void clearSystemSurrogate() {
        systemSurrogate.remove();
    }

    /** Rebind current session. Useful for testing */
    public static void rebindSession() {
        currentSession.set(getContext().newInstance(Session.class));
    }

    /** Unbind subject. */
    public static void unbindContext() {
        ThreadContext.unbindSubject();
    }

    /** Get current User Id. */
    public static String getAuditUserId() {
        if (isNotEmpty(systemSurrogate.get())) return systemSurrogate.get();
        String surrogate = null;
        try {
            surrogate = getSession().getPrincipal().getSurrogate();
        }
        catch (final Exception e) {
            // ignore
        }
        return getSession().getAuditUser() + (isEmpty(surrogate) ? "" : ":" + surrogate);
    }

    /** Returns the client ip if the session is authenticated. */
    @Nullable public static String getClientIp() {
        return getSession().getClientIp();
    }

    /** Returns the current context session. */
    @NotNull public static Session getSession() {
        return currentSession.get();
    }

    /** Set surrogate it shiro session. */
    public static void setSurrogate(final String surrogate) {
        if (Context.getContext().hasBinding(Session.class)) {
            try {
                final String currentSurrogate = getSession().getPrincipal().getSurrogate();
                final String newSurrogate     = isEmpty(currentSurrogate) ? surrogate : currentSurrogate + ":" + surrogate;
                // noinspection DuplicateStringLiteralInspection
                org.apache.shiro.SecurityUtils.getSubject().getSession().setAttribute("surrogate", newSurrogate);
            }
            catch (final UnavailableSecurityManagerException | UnknownSessionException ignore) {
                // ignore
            }
        }
    }

    /**
     * Set system surrogate. This method should be used when setting a surrogate user with no
     * session. i.e. Tasks
     */
    public static void setSystemSurrogate(final String surrogate) {
        systemSurrogate.set(surrogate);
    }

    /** Get current User Id. */
    public static String getUserId() {
        return getSession().getPrincipal().getId();
    }

    //~ Static Fields ................................................................................................................................

    private static final ThreadLocal<Session> currentSession = ThreadLocal.withInitial(() -> getContext().newInstance(Session.class));

    private static final ThreadLocal<String> systemSurrogate = ThreadLocal.withInitial(() -> "");
}  // end class SecurityUtils
