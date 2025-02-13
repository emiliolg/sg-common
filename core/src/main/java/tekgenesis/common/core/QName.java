
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

/**
 * Methods to Manage Qualified Names.
 */
@SuppressWarnings("FieldMayBeFinal")  // Gwt
public class QName implements RichComparable<QName>, Serializable {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private String name;

    @NotNull private String qualification;

    //~ Constructors .................................................................................................................................

    private QName() {
        this("", "");
    }

    /** Create a qualified name. */
    private QName(@NotNull String fullQualifiedName) {
        final int i = fullQualifiedName.indexOf(':');

        String fqn = fullQualifiedName;

        if (i > 0) fqn = fullQualifiedName.substring(0, i);

        final int dot = fqn.lastIndexOf('.');
        if (dot == -1) {
            name          = fullQualifiedName;
            qualification = "";
        }
        else {
            name          = fqn.substring(dot + 1);
            qualification = fqn.substring(0, dot);
        }

        if (i > 0) name = name + ":" + fullQualifiedName.substring(i + 1);
    }

    /** Create a qualified name. */
    private QName(@NotNull String qualification, @NotNull String name) {
        this.qualification = qualification;
        this.name          = name;
    }

    //~ Methods ......................................................................................................................................

    /** Append to the current qname and create a new one. */
    public QName append(String nm) {
        return new QName(getFullName(), nm);
    }

    @Override
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    public int compareTo(@NotNull QName that) {
        final int d = qualification.compareTo(that.qualification);
        return d != 0 ? d : name.compareTo(that.name);
    }

    @Override public boolean equals(Object obj) {
        if (!(obj instanceof QName)) return false;
        final QName that = (QName) obj;
        return qualification.equals(that.qualification) && name.equals(that.name);
    }

    @Override public int hashCode() {
        return qualification.hashCode() * 31 + name.hashCode();
    }

    @Override public String toString() {
        return getFullName();
    }

    /** Returns the 'full' (qualified) name. */
    public String getFullName() {
        return qualification.isEmpty() ? name : qualification + "." + name;
    }

    /** Get the name. */
    @NotNull public String getName() {
        return name;
    }

    /** Get the qualification part of the name. */
    @NotNull public String getQualification() {
        return qualification;
    }

    /** Return true if the Key is Empty. */
    public boolean isEmpty() {
        return this == EMPTY || qualification.isEmpty() && name.isEmpty();
    }

    //~ Methods ......................................................................................................................................

    /** create a key from a fully qualified name. */
    @NotNull public static QName createQName(String fqn) {
        return fqn.isEmpty() ? EMPTY : new QName(fqn);
    }

    /** create a key from a class. */
    public static QName createQName(@NotNull final Class<?> c) {
        return new QName(c.getName());
    }

    /** create a key. */
    public static QName createQName(@NotNull String qualification, @NotNull String name) {
        return qualification.isEmpty() && name.isEmpty() ? EMPTY : new QName(qualification, name);
    }

    /** Returns the name part of a full qualified name. */
    public static String extractName(@NotNull String fullQualifiedName) {
        return new QName(fullQualifiedName).getName();
    }

    /** Returns the qualification (For example package name) part of a full qualified name. */
    public static String extractQualification(@NotNull String fqn) {
        return new QName(fqn).getQualification();
    }
    /**
     * Returns the qualification (For example package name) part of a full qualified name. it
     * provides a defaultValue in the case the name is not qualified
     */
    public static String extractQualification(@NotNull String fqn, @NotNull String defaultValue) {
        return isQualified(fqn) ? extractQualification(fqn) : defaultValue;
    }

    /** Optionally qualify a name (If it is not yet qualified). */
    public static String qualify(String defaultQualification, String name) {
        return isQualified(name) ? name : new QName(defaultQualification, name).toString();
    }

    /** Remove qualification if it is equal to any of the specified ones. */
    public static String removeQualification(String fqn, String... defaults) {
        final QName q = new QName(fqn);
        for (final String s : defaults) {
            if (q.getQualification().equals(s)) return q.getName();
        }
        return fqn;
    }

    /** Return false if given name is qualified. */
    public static boolean isNotQualified(String name) {
        return !isQualified(name);
    }

    /** Return true if given name is qualified. */
    public static boolean isQualified(String name) {
        return name.indexOf('.') != -1;
    }

    //~ Static Fields ................................................................................................................................

    public static final QName EMPTY = new QName();

    private static final long serialVersionUID = 7582447615608615516L;
}  // end class QName
