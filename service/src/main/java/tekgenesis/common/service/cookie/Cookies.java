
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

import tekgenesis.common.collections.Colls;
import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.service.Headers;

import static tekgenesis.common.core.Strings.containsWhiteSpace;
import static tekgenesis.common.core.Strings.quoted;
import static tekgenesis.common.service.HeaderNames.COOKIE;
import static tekgenesis.common.service.HeaderNames.SET_COOKIE;

/**
 * {@link Cookie cookies} utility class.
 */
public class Cookies {

    //~ Constructors .................................................................................................................................

    private Cookies() {}

    //~ Methods ......................................................................................................................................

    /** Create MutableCookie given name and value. */
    @NotNull public static MutableCookie create(String name, String value) {
        return new MutableCookieImpl(name, value);
    }

    /** Decode multiple {@link Headers#SET_COOKIE} headers into cookies. */
    public static Iterable<Cookie> decodeClientCookies(@NotNull final Headers headers) {
        return headers.getAll(SET_COOKIE).map(ClientCookies::decode);
    }

    /**
     * Encode given cookies into a single {@link Headers#COOKIE} header with multiple name/value
     * pairs.
     */
    public static void encodeClientCookies(@NotNull final Headers headers, @NotNull final Iterable<Cookie> cookies) {
        final ImmutableList<Cookie> list = Colls.toList(cookies);
        if (!list.isEmpty()) headers.put(COOKIE, list.map(ClientCookies::encode).mkString("; "));
    }

    /** Encode given cookies into multiple {@link Headers#SET_COOKIE} headers. */
    public static void encodeServerCookies(@NotNull final Headers headers, @NotNull final Iterable<Cookie> cookies) {
        final ImmutableList<Cookie> list = Colls.toList(cookies);
        if (!list.isEmpty()) headers.putAll(SET_COOKIE, list.map(ServerCookies::encode));
    }

    /** Cookie equality implementation. */
    public static boolean equal(Cookie a, Cookie b) {
        return a == b ||
               b != null && a.getMaxAge() == b.getMaxAge() && a.isHttpOnly() == b.isHttpOnly() && a.isSecure() == b.isSecure() &&
               a.getName().equals(b.getName()) &&
               (a.getDomain() != null
                ? a.getDomain().equals(b.getDomain())
                : b.getDomain() == null &&
                  (a.getPath() != null ? a.getPath().equals(b.getPath()) : b.getPath() == null && a.getValue().equals(b.getValue())));
    }

    /** Cookie hash implementation. */
    @SuppressWarnings("MagicNumber")
    public static int hash(Cookie cookie) {
        int result = cookie.getName().hashCode();
        result = 31 * result + (cookie.getDomain() != null ? cookie.getDomain().hashCode() : 0);
        result = 31 * result + (int) (cookie.getMaxAge() ^ (cookie.getMaxAge() >>> 32));
        result = 31 * result + (cookie.isHttpOnly() ? 1 : 0);
        result = 31 * result + (cookie.getPath() != null ? cookie.getPath().hashCode() : 0);
        result = 31 * result + (cookie.isSecure() ? 1 : 0);
        result = 31 * result + cookie.getValue().hashCode();
        return result;
    }

    /** Cookie toString implementation. */
    public static String toString(@NotNull Cookie cookie) {
        return "{" +
               "name='" + cookie.getName() + '\'' + ", value='" + cookie.getValue() + '\'' + ", domain='" + cookie.getDomain() + '\'' + ", path='" +
               cookie.getPath() + '\'' + ", maxAge=" + cookie.getMaxAge() + ", http=" + cookie.isHttpOnly() + ", secure=" + cookie.isSecure() + '}';
    }

    /** Append string value quoted only if given value contains whitespace. */
    static void appendQuotedIfWhitespace(@NotNull final StringBuilder b, @NotNull final String value) {
        b.append(containsWhiteSpace(value) ? quoted(value) : value);
    }

    //~ Inner Classes ................................................................................................................................

    /**
     * Private implementation of {@link MutableCookie}.
     */
    private static class MutableCookieImpl implements MutableCookie {
        private String       domain = null;
        private boolean      http   = false;
        private long         maxAge = -1;
        private final String name;
        private String       path   = null;
        private boolean      secure = false;
        private String       value;

        private MutableCookieImpl(String name, String value) {
            this.name  = name;
            this.value = value;
        }

        @Override public boolean equals(Object o) {
            return this == o || o instanceof Cookie && equal(this, (Cookie) o);
        }

        @Override public int hashCode() {
            return hash(this);
        }

        @Override public String toString() {
            return Cookies.toString(this);
        }

        @Override public MutableCookie withDomain(@Nullable String d) {
            domain = d;
            return this;
        }

        @Override public MutableCookie withHttpOnly(boolean flag) {
            http = flag;
            return this;
        }

        @Override public MutableCookie withMaxAge(long seconds) {
            maxAge = seconds;
            return this;
        }

        @Override public MutableCookie withPath(@Nullable String p) {
            path = p;
            return this;
        }

        @Override public MutableCookie withSecure(boolean flag) {
            secure = flag;
            return this;
        }

        @Override public MutableCookie withValue(@NotNull String v) {
            value = v;
            return this;
        }

        @Nullable @Override public String getDomain() {
            return domain;
        }

        @Override public boolean isSecure() {
            return secure;
        }

        @Override public long getMaxAge() {
            return maxAge;
        }

        @NotNull @Override public String getName() {
            return name;
        }

        @Nullable @Override public String getPath() {
            return path;
        }

        @NotNull @Override public String getValue() {
            return value;
        }

        @Override public boolean isHttpOnly() {
            return http;
        }
    }  // end class MutableCookieImpl
}  // end class Cookies
