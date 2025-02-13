
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.security;

import java.util.Locale;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This interface represents a principal.
 */
public interface Principal {

    //~ Methods ......................................................................................................................................

    /**
     * Validate if the principal has all given permissions.
     *
     * @param   permissions  Permissions to validate
     *
     * @return  <code>true</code> if the principal has all given permissions
     */
    boolean hasAllPermissions(String... permissions);

    /**
     * Validate if the principal has all of the given roles.
     *
     * @param   roles  Roles to validate
     *
     * @return  <code>true</code> if the principal has all of the given roles
     */
    boolean hasAllRoles(String... roles);

    /**
     * Validate if the principal has any given permissions.
     *
     * @param   permissions  Permissions to validate
     *
     * @return  <code>true</code> if the principal has any given permissions
     */
    boolean hasAnyPermissions(String... permissions);

    /**
     * Validate if the principal has any of the given roles.
     *
     * @param   roles  Roles to validate
     *
     * @return  <code>true</code> if the principal has any of the given roles
     */
    boolean hasAnyRole(String... roles);

    /**
     * Validate if the principal has the given permission.
     *
     * @param   permission  Permission to validate
     *
     * @return  <code>true</code> if the principal has the given permission
     */
    boolean hasPermission(String permission);

    /**
     * Validate if the principal has the given role.
     *
     * @param   role  Role to validate
     *
     * @return  <code>true</code> if the principal has the given role
     */
    boolean hasRole(String role);

    /** Returns optional facebook id (or empty). */
    @NotNull String getFacebookId();

    /** Returns optional facebook token (or empty). */
    @NotNull String getFacebookToken();

    /** Returns the principal nick name. */
    @NotNull String getId();

    /** Returns the principal locale. */
    Locale getLocale();

    /** Sets the principal locale. */
    void setLocale(@Nullable Locale locale);

    /** Returns true if principal is system dummy user. */
    boolean isSystem();

    /** Returns the principal display name. */
    @NotNull String getName();

    /** Returns the OrgUnit. */
    String getOrgUnit();

    /** Returns the principal nick name. */
    @NotNull String getSessionProperty(String property);

    /** Return surrogate user. */
    String getSurrogate();
}  // end interface Principal
