
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.net.URL;
import java.util.TreeSet;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.core.RichComparable;

import static java.lang.Integer.parseInt;
import static java.lang.Math.min;

import static tekgenesis.common.Predefined.isEmpty;
import static tekgenesis.common.core.Predicates.endsWith;
import static tekgenesis.common.util.Resources.findResources;

/**
 * A Version String (Format like m.n or m.n.p).
 */
public class VersionString implements RichComparable<VersionString> {

    //~ Instance Fields ..............................................................................................................................

    private final String version;

    //~ Constructors .................................................................................................................................

    private VersionString(@NotNull String version) {
        this.version = version;
    }

    //~ Methods ......................................................................................................................................

    @Override public int compareTo(@NotNull VersionString that) {
        final String[] thisParts = version.split("\\.");
        final String[] thatParts = that.version.split("\\.");
        final int      length    = min(thisParts.length, thatParts.length);
        for (int i = 0; i < length; i++) {
            final int cmp = parseInt(thisParts[i]) - parseInt(thatParts[i]);
            if (cmp != 0) return cmp;
        }
        return thisParts.length - thatParts.length;
    }

    @Override public boolean equals(Object that) {
        return this == that || that != null && getClass() == that.getClass() && compareTo((VersionString) that) == 0;
    }

    @Override public final int hashCode() {
        int hashCode = 0;
        for (final String part : version.split("\\."))
            hashCode = 31 * hashCode + parseInt(part);
        return hashCode;
    }

    /** Increment the version number. */
    public VersionString increment(final boolean minor) {
        final int last = version.lastIndexOf('.');
        if (last == -1) return new VersionString(minor ? version + ".1" : (parseInt(version) + 1) + ".0");
        if (minor) {
            final int n = parseInt(version.substring(last + 1)) + 1;
            return new VersionString(version.substring(0, last + 1) + n);
        }
        final int prev = version.lastIndexOf('.', last - 1);
        final int n    = parseInt(version.substring(prev + 1, last)) + 1;
        return new VersionString(version.substring(0, prev + 1) + n + ".0");
    }

    @Override public String toString() {
        return version;
    }

    /** Check if the difference between the versions is a minor one. */
    public boolean isMinor(final VersionString that) {
        return removeMinor().equals(that.removeMinor());
    }
    private String removeMinor() {
        final int last = version.lastIndexOf('.');
        return last == -1 ? version : version.substring(0, last);
    }

    //~ Methods ......................................................................................................................................

    /** Find all available Versions. */
    public static TreeSet<VersionString> findVersions(String dir, String subDir, String schemaFileName) {
        final TreeSet<VersionString> vs = new TreeSet<>();
        for (final URL url : findResources(dir, endsWith(subDir + schemaFileName))) {
            final VersionString v = versionString(url, dir + "v");
            if (v != null) vs.add(v);
        }
        return vs;
    }

    /** Creates a Version from string. */
    public static VersionString valueOf(String version) {
        return versionFrom(version);
    }

    /** Creates a Version from string. */
    public static VersionString versionFrom(String version) {
        if (isEmpty(version)) return VERSION_ZERO;
        if (!version.matches("[0-9]+(\\.[0-9]+)*")) throw new IllegalArgumentException(INVALID_VERSION_FORMAT);
        return new VersionString(version);
    }

    @Nullable private static VersionString versionString(URL url, String prefix) {
        final String path = url.getPath();
        final int    pos  = path.lastIndexOf(prefix);
        if (pos == -1) return null;
        final int first = pos + prefix.length();
        final int last  = path.indexOf('/', first);
        return last == -1 ? null : versionFrom(path.substring(first, last));
    }

    //~ Static Fields ................................................................................................................................

    @NonNls public static final String INVALID_VERSION_FORMAT = "Invalid version format";

    public static final VersionString VERSION_ZERO = valueOf("0");
    public static final VersionString VERSION_ONE  = valueOf("1.0");
}  // end class VersionString
