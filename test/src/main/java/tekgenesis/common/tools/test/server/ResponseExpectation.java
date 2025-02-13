
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.tools.test.server;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.core.Option;
import tekgenesis.common.service.Status;
import tekgenesis.common.service.cookie.Cookie;
import tekgenesis.common.service.cookie.Cookies;
import tekgenesis.common.service.cookie.MutableCookie;
import tekgenesis.common.service.server.Request;

import static tekgenesis.common.collections.Colls.mkString;
import static tekgenesis.common.core.Option.some;
import static tekgenesis.common.media.MediaType.TEXT_PLAIN;
import static tekgenesis.common.service.Status.OK;

/**
 * Request expectation.
 */
public class ResponseExpectation extends BaseExpectation<ResponseExpectation> {

    //~ Instance Fields ..............................................................................................................................

    private Option<Function<Request, byte[]>> bytes;

    private Option<Function<Request, Object>> content;
    private final List<Cookie>                cookies;
    private Option<Integer>                   delay;

    private final Status status;

    //~ Constructors .................................................................................................................................

    ResponseExpectation(Status status) {
        this.status = status;
        content     = Option.empty();
        bytes       = Option.empty();
        delay       = Option.empty();
        cookies     = new ArrayList<>();
    }

    //~ Methods ......................................................................................................................................

    /** Set response expected process delay in milliseconds. */
    public ResponseExpectation delay(int responseDelay) {
        delay = some(responseDelay);
        return this;
    }

    @Override public String toString() {
        return "(" + hashCode() + ")ResponseExpectation{" +
               "status=" + status + ", content=" + content + ", delay=" + delay + ", cookies=" + mkString(cookies, "[", ",", "]") + '}';
    }

    /** Set response expected content retriever function. */
    public ResponseExpectation withContent(@NotNull final Function<Request, Object> retriever) {
        content = some(retriever);
        return this;
    }

    /** Set response expected content. */
    public ResponseExpectation withContent(@NotNull final Object responseContent) {
        return withContent(constant(responseContent));
    }

    /** Set response cookies. Return cookie for further configuration. */
    public MutableCookie withCookie(@NotNull final String name, @NotNull final String value) {
        final MutableCookie cookie = Cookies.create(name, value);
        cookies.add(cookie);
        return cookie;
    }

    /** Set response raw expected content. May override headers with no effects. */
    public ResponseExpectation withRawContent(@NotNull byte[] raw) {
        withRawContent(constant(raw));
        return this;
    }

    /** Set raw response expected content retriever function. */
    public ResponseExpectation withRawContent(@NotNull final Function<Request, byte[]> retriever) {
        bytes = some(retriever);
        return this;
    }

    Option<Function<Request, Object>> getContentFn() {
        return content;
    }

    Iterable<Cookie> getCookies() {
        return cookies;
    }

    Option<Integer> getDelay() {
        return delay;
    }

    Option<Function<Request, byte[]>> getRawContentFn() {
        return bytes;
    }

    Status getStatus() {
        return status;
    }

    private <T> Function<Request, T> constant(final T constant) {
        return value -> constant;
    }

    //~ Static Fields ................................................................................................................................

    public static final ResponseExpectation DEFAULT_RESPONSE = new ResponseExpectation(OK).withContentType(TEXT_PLAIN);
}  // end class ResponseExpectation
