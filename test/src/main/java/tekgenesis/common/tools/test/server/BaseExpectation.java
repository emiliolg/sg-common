
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.tools.test.server;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.logging.Logger;
import tekgenesis.common.media.MediaType;
import tekgenesis.common.service.Headers;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.tools.test.server.CookiesAssertion.DEFAULT_COOKIES_ASSERTION;
import static tekgenesis.common.tools.test.server.HeadersAssertion.DEFAULT_HEADERS_ASSERTION;

/**
 * Base expectation common superclass.
 */
class BaseExpectation<This extends BaseExpectation<This>> {

    //~ Instance Fields ..............................................................................................................................

    protected final Headers  headers;
    private CookiesAssertion cookiesAssertion;
    private HeadersAssertion headersAssertion;

    //~ Constructors .................................................................................................................................

    BaseExpectation() {
        headers          = new Headers();
        headersAssertion = DEFAULT_HEADERS_ASSERTION;
        cookiesAssertion = DEFAULT_COOKIES_ASSERTION;
    }

    //~ Methods ......................................................................................................................................

    /** Set request expected content type header. */
    public This withContentType(@NotNull MediaType contentType) {
        headers.setContentType(contentType);
        return This();
    }

    /** Set cookies assertions. Overrides default matching behaviour. */
    public This withCookiesAssertion(@NotNull final CookiesAssertion assertion) {
        cookiesAssertion = assertion;
        return This();
    }

    /** Set request expected header. */
    public This withHeader(@NotNull String name, @NotNull String value) {
        headers.put(name, value);
        return This();
    }

    /** Set headers assertions. Overrides default matching behaviour. */
    public This withHeadersAssertion(@NotNull final HeadersAssertion assertion) {
        headersAssertion = assertion;
        return This();
    }

    void log(@NotNull String action) {
        logger.info(String.format("%s > %s%n", action, toString()));
    }

    CookiesAssertion getCookiesAssertion() {
        return cookiesAssertion;
    }

    Headers getHeaders() {
        return headers;
    }

    HeadersAssertion getHeadersAssertion() {
        return headersAssertion;
    }

    private This This() {
        return cast(this);
    }

    //~ Static Fields ................................................................................................................................

    private static final Logger logger = Logger.getLogger(BaseExpectation.class);
}  // end class BaseExpectation
