
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.tools.test.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.Colls;
import tekgenesis.common.collections.MultiMap;
import tekgenesis.common.collections.Seq;
import tekgenesis.common.core.Option;
import tekgenesis.common.logging.Logger;
import tekgenesis.common.media.MediaType;
import tekgenesis.common.service.Headers;
import tekgenesis.common.service.Method;
import tekgenesis.common.service.OutboundMessageWriter;
import tekgenesis.common.service.cookie.Cookie;
import tekgenesis.common.service.cookie.Cookies;
import tekgenesis.common.service.cookie.MutableCookie;
import tekgenesis.common.service.etl.*;
import tekgenesis.common.service.server.Request;
import tekgenesis.common.service.server.Response;

import static org.assertj.core.api.Assertions.assertThat;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.service.HeaderNames.X_LIMIT;
import static tekgenesis.common.service.HeaderNames.X_OFFSET;
import static tekgenesis.common.tools.test.server.CookiesAssertion.DEFAULT_COOKIES_ASSERTION;
import static tekgenesis.common.tools.test.server.ExpectationsConfiguration.*;
import static tekgenesis.common.tools.test.server.HeadersAssertion.DEFAULT_HEADERS_ASSERTION;

/**
 * Sui Generis simple mock http server. Server allows basically to setup an excepted request and
 * response.
 */
class SgHttpServer {

    //~ Instance Fields ..............................................................................................................................

    private final EnumSet<ExpectationsConfiguration> configuration;

    private final List<MessageConverter<?>> converters = new ArrayList<>();
    private final SgExpectationHandler      handler;
    private final NioSgHttpServer           server;

    //~ Constructors .................................................................................................................................

    SgHttpServer(int port, EnumSet<ExpectationsConfiguration> configuration) {
        this.configuration = configuration;
        handler            = new SgExpectationHandler(converters, configuration);
        server             = new NioSgHttpServer(port, handler);

        // Add default converters
        withConverter(new ByteMessageConverter());
        withConverter(new XmlMessageConverter());
        withConverter(new BasicTypeMessageConverter());
        withConverter(new StringMessageConverter());
        withConverter(new FormMessageConverter());
        withConverter(new JsonMessageConverter());
    }

    //~ Methods ......................................................................................................................................

    public void assertNoLeftExpectations() {
        assertThat(Colls.immutable(handler.expectations).filter(e -> !e.mustKeep())).as("Some expectations are left behind!").isEmpty();
    }

    void assertShutdownLeftExpectations() {
        if (configuration.contains(FAIL_REMAINING)) assertThat(Colls.immutable(handler.expectations).filter(e -> !e.mustKeep())).as(
            "Server is shutting down and some expectations are left behind!")
            .isEmpty();
    }

    RequestExpectation expectRequest(@NotNull Method method) {
        final RequestExpectation request = new RequestExpectation(method);
        handler.expect(request);
        return request;
    }

    void shutdown() {
        try {
            server.shutdown();
        }
        catch (final InterruptedException e) {
            logger.error("Exception occurred while shutting down server", e);
        }
    }

    void start() {
        server.start();
    }

    void withConverter(@NotNull MessageConverter<?> converter) {
        converters.add(0, converter);
    }

    int getPort() {
        return server.getServerPort();
    }

    //~ Static Fields ................................................................................................................................

    public static final Logger logger = Logger.getLogger(SgHttpServer.class);

    //~ Inner Classes ................................................................................................................................

    private static class ExpectationScore {
        private final RequestExpectation expectation;

        private final int scoring;

        private ExpectationScore(boolean fail, SgExpectationHandler h, Request r, RequestExpectation expectation) {
            this.expectation = expectation;
            scoring          = scoring(fail, h, r);
        }

        public ExpectationScore pickBest(ExpectationScore other) {
            return scoring == 0 ? other : other == null || other.scoring < scoring ? this : other;
        }

        @SuppressWarnings("MagicNumber")
        private int scoring(boolean fail, SgExpectationHandler h, Request r) {
            int result = 8;
            try {
                h.checkRequestMethod(r.getMethod(), expectation.getMethod());
                if (h.checkRequestPath(r.getPath(), expectation.getPath())) result += 4;
                if (h.checkRequestParameters(r.getParameters(), expectation.getParameters())) result += 2;
                if (h.checkRequestCookies(r.getCookies(), expectation.getCookies(), expectation.getCookiesAssertion())) result += 2;
                if (h.checkRequestHeaders(r.getHeaders(), expectation.getHeaders(), expectation.getHeadersAssertion())) result += 2;
                if (h.checkRequestContent(r, expectation)) result += 2;
            }
            catch (final AssertionError error) {
                if (fail) throw error;
                else result = 0;
            }
            return result;
        }

        private static ExpectationScore assertion(SgExpectationHandler h, Request r, @NotNull RequestExpectation expectation) {
            return new ExpectationScore(true, h, r, expectation);
        }

        private static ExpectationScore calculate(SgExpectationHandler h, Request r, @NotNull RequestExpectation expectation) {
            return new ExpectationScore(false, h, r, expectation);
        }
    }  // end class ExpectationScore

    static class SgExpectationHandler {
        private final EnumSet<ExpectationsConfiguration>        configuration;
        private final List<MessageConverter<?>>                 converters;
        private final ConcurrentLinkedDeque<RequestExpectation> expectations = new ConcurrentLinkedDeque<>();

        private SgExpectationHandler(List<MessageConverter<?>> converters, EnumSet<ExpectationsConfiguration> configuration) {
            this.converters    = converters;
            this.configuration = configuration;
        }

        void handle(@NotNull Request request, @NotNull Response response) {
            assertThat(expectations).as("Received request %s but no expectations were defined!", request).isNotEmpty();

            final RequestExpectation expectation = match(request);
            if (expectation != null) {
                logExpectation("REQUEST MATCHED: ", expectation);
                queueRepetitions(expectation);
                createResponse(request, response, expectation.getResponse());
            }
            assertThat(expectation).as("Received request %s but no expectation matched!", request).isNotNull();
        }

        private <T> boolean checkRequestContent(final Request request, final RequestExpectation expectation) {
            if (expectation.getContent().isEmpty()) return false;

            try {
                final long length = request.getHeaders().getContentLength();
                assertThat(length).as("Expected content on request").isGreaterThan(0);

                final InputStream         stream         = request.getContent();
                final Object              expected       = expectation.getContent().get();
                final Class<T>            responseType   = cast(expected.getClass());
                final Headers             requestHeaders = expectation.getHeaders();
                final MediaType           contentType    = requestHeaders.getContentType();
                final MessageConverter<T> converter      = getSuitableConverter(converters, contentType, responseType, responseType);

                if (converter == null) {
                    final String message = "Could not extract response from request: no suitable converter found " +
                                           "for response type " + responseType + " and content type '" + contentType + "'";
                    throw new RuntimeException(message);
                }

                final T actual = converter.read(responseType, responseType, contentType, stream);
                assertThat(actual).isEqualTo(expected);
            }
            catch (final IOException e) {
                return false;
            }
            return true;
        }

        private boolean checkRequestCookies(Seq<Cookie> actual, List<MutableCookie> expected, CookiesAssertion assertion) {
            assertion.assertCookies(actual.toList(), cast(expected));
            return assertion != DEFAULT_COOKIES_ASSERTION || !expected.isEmpty();
        }

        private boolean checkRequestHeaders(Headers actual, Headers expected, HeadersAssertion assertion) {
            assertion.assertHeaders(expected, actual);
            return assertion != DEFAULT_HEADERS_ASSERTION || !expected.asMap().isEmpty();
        }

        private void checkRequestMethod(Method actual, Method expected) {
            assertThat(actual.name()).as("Request method does not match").isEqualTo(expected.name());
        }

        private boolean checkRequestParameters(MultiMap<String, String> actual, Option<MultiMap<String, String>> expected) {
            if (expected.isPresent()) {
                for (final Map.Entry<String, Collection<String>> entry : expected.get().asMap().entrySet())
                    assertThat(actual.get(entry.getKey())).as("Request parameter '%s'", entry.getKey()).containsAll(entry.getValue());
                return true;
            }
            return false;
        }

        private boolean checkRequestPath(String path, Option<String> expected) {
            if (expected.isPresent()) {
                assertThat(path).as("Request path does not match").isEqualTo(expected.get());
                return true;
            }
            return false;
        }

        private void createResponse(Request request, final Response response, final ResponseExpectation expectation) {
            responseDelay(expectation);
            response.setStatus(expectation.getStatus());

            writeHeaders(expectation.getHeaders(), expectation.getCookies(), response);

            if (expectation.getRawContentFn().isPresent()) {
                final byte[] content = expectation.getRawContentFn().get().apply(request);
                writeRawContent(response, content);
            }
            else if (expectation.getContentFn().isPresent()) writeContent(response, expectation.getContentFn().get().apply(request));

            final Headers reqHeaders = request.getHeaders();

            if (reqHeaders.getFirst(X_OFFSET).isPresent()) response.getHeaders().put(X_OFFSET, reqHeaders.getFirst(X_OFFSET).get());
            if (reqHeaders.getFirst(X_LIMIT).isPresent()) response.getHeaders().put(X_LIMIT, reqHeaders.getFirst(X_LIMIT).get());

            logExpectation("RETURNING RESPONSE: ", expectation);
        }  // end method createResponse

        private void expect(@NotNull RequestExpectation request) {
            expectations.add(request);
        }

        private void logExpectation(String action, @NotNull BaseExpectation<?> expectation) {
            if (configuration.contains(VERBOSE)) expectation.log(action);
        }

        @Nullable private RequestExpectation match(@NotNull Request request) {
            return configuration.contains(ORDERED) ? matchNextExpectation(request) : matchBestExpectation(request);
        }

        @Nullable private RequestExpectation matchBestExpectation(Request request) {
            ExpectationScore best = null;

            for (final RequestExpectation expectation : expectations) {
                final ExpectationScore score = ExpectationScore.calculate(this, request, expectation);
                best = score.pickBest(best);
            }

            if (best != null) {
                expectations.remove(best.expectation);
                return best.expectation;
            }

            for (final RequestExpectation failed : expectations)
                logExpectation("FAILED MATCH: ", failed);
            return null;
        }

        @NotNull private RequestExpectation matchNextExpectation(Request request) {
            final RequestExpectation expectation = expectations.poll();
            ExpectationScore.assertion(this, request, expectation);
            return expectation;
        }

        /** Queue expectation left repetitions. */
        private void queueRepetitions(@NotNull RequestExpectation expectation) {
            if (configuration.contains(REPEATABLE)) expectations.addLast(expectation);
            else if (expectation.consume() || expectation.mustKeep()) expectations.addFirst(expectation);
        }

        private void responseDelay(ResponseExpectation expectation) {
            for (final Integer delay : expectation.getDelay()) {
                try {
                    if (delay > 0) Thread.sleep(delay);
                }
                catch (final InterruptedException e) {
                    throw new AssertionError("Interrupted!", e);
                }
            }
        }

        private void writeContent(Response response, Object content) {
            final OutboundMessageWriter writer = new OutboundMessageWriter();

            try {
                writer.write(response, converters, content);
            }
            catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        private void writeHeaders(Headers headers, Iterable<Cookie> cookies, Response response) {
            Cookies.encodeServerCookies(response.getHeaders(), cookies);
            for (final Map.Entry<String, Collection<String>> header : headers.asMap().entrySet())
                response.getHeaders().putAll(header.getKey(), header.getValue());
        }

        private void writeRawContent(Response response, byte[] content) {
            try {
                response.getContent().write(content);
            }
            catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        private <T> MessageConverter<T> getSuitableConverter(List<MessageConverter<?>> cs, MediaType contentType, Class<?> responseType,
                                                             Class<?> genericType) {
            for (final MessageConverter<?> converter : cs) {
                if (converter.canRead(responseType, genericType, contentType)) return cast(converter);
            }
            return null;
        }
    }  // end class SgExpectationHandler
}  // end class SgHttpServer
