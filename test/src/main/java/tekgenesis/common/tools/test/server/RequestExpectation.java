
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

import tekgenesis.common.collections.MultiMap;
import tekgenesis.common.core.Enumeration;
import tekgenesis.common.core.Option;
import tekgenesis.common.media.MediaType;
import tekgenesis.common.service.ApplicationExceptionResult;
import tekgenesis.common.service.HeaderNames;
import tekgenesis.common.service.Method;
import tekgenesis.common.service.Status;
import tekgenesis.common.service.cookie.Cookies;
import tekgenesis.common.service.cookie.MutableCookie;
import tekgenesis.common.service.server.Request;

import static tekgenesis.common.core.Option.some;
import static tekgenesis.common.service.Status.BAD_REQUEST;
import static tekgenesis.common.tools.test.server.ResponseExpectation.DEFAULT_RESPONSE;

/**
 * Request expectation.
 */
@SuppressWarnings("ParameterHidesMemberVariable")
public class RequestExpectation extends BaseExpectation<RequestExpectation> {

    //~ Instance Fields ..............................................................................................................................

    private Option<Object> content;

    private final List<MutableCookie>        cookies;
    private boolean                          keep;
    private final Method                     method;
    private Option<MultiMap<String, String>> parameters;
    private Option<String>                   path;
    private Option<ResponseExpectation>      response;
    private int                              times;

    //~ Constructors .................................................................................................................................

    RequestExpectation(Method method) {
        this.method = method;
        path        = Option.empty();
        response    = Option.empty();
        content     = Option.empty();
        parameters  = Option.empty();
        times       = 1;
        cookies     = new ArrayList<>();
    }

    //~ Methods ......................................................................................................................................

    /** Mark request to be kept after consumed. */
    public RequestExpectation keep() {
        keep = true;
        return this;
    }

    /** Return true if shoukd be kept ater consumed. */
    public boolean mustKeep() {
        return keep;
    }

    /** Set request expected path. */
    public RequestExpectation on(@NotNull final String path) {
        this.path = some(path);
        return this;
    }

    /** Set amount of requests to be expected. */
    public RequestExpectation repeated(int times) {
        this.times = times;
        return this;
    }

    /** Respond empty response with given status. */
    public ResponseExpectation respond(@NotNull Status status) {
        final ResponseExpectation r = new ResponseExpectation(status);
        response = some(r);
        return r;
    }

    /** Respond response with given status and content. */
    public ResponseExpectation respond(@NotNull Status status, @NotNull Object content) {
        return respond(status).withContent(content);
    }

    /** Respond empty ok response. */
    public ResponseExpectation respondOk() {
        return respond(Status.OK);
    }

    /** Respond ok response with content. */
    public ResponseExpectation respondOkWith(@NotNull Object content) {
        return respondOk().withContent(content);
    }

    /** Respond ok response with content specified by retriever. */
    public ResponseExpectation respondOkWith(@NotNull Function<Request, Object> content) {
        return respondOk().withContent(content);
    }

    /** Respond response with given status and raw byte[] content. */
    public ResponseExpectation respondRaw(@NotNull Status status, @NotNull byte[] content) {
        return respond(status).withRawContent(content);
    }

    /** Respond application exception response with specified enum error. */
    public ResponseExpectation respondWithApplicationException(@NotNull Enumeration<?, String> error, Object... args) {
        return respond(BAD_REQUEST).withHeader(HeaderNames.X_APPLICATION_EXCEPTION, Boolean.TRUE.toString())
               .withContent(new ApplicationExceptionResult((Enum<?>) error, error.label(args)));
    }

    @Override public String toString() {
        // noinspection DuplicateStringLiteralInspection
        return "(" + hashCode() + ")RequestExpectation{" +
               "content=" + content + ", method=" + method + ", parameters=" + parameters + ", path=" + path + ", response=" + response + ", times=" +
               times + '}';
    }

    /** Set request expected accept headers. */
    public RequestExpectation withAccept(@NotNull MediaType... accepts) {
        headers.setAccept(accepts);
        return this;
    }

    /** Set request expected content (for put or post methods). */
    public RequestExpectation withContent(@NotNull Object content) {
        assert method == Method.POST || method == Method.PUT;
        this.content = some(content);
        return this;
    }

    /** Add Cookie to RequestExpectation. */
    public RequestExpectation withCookie(@NotNull final String name, @NotNull final String value) {
        cookies.add(Cookies.create(name, value));
        return this;
    }

    /**
     * Set request expected parameter. Calling this multiple times for one expected request will
     * accumulate parameters
     */
    public RequestExpectation withParameter(@NotNull String key, @NotNull String value) {
        if (parameters.isPresent()) parameters.get().put(key, value);
        else {
            parameters = some(MultiMap.<String, String>createMultiMap());
            parameters.get().put(key, value);
        }
        return this;
    }  // end method withParameter

    /** Set request expected parameters. */
    public RequestExpectation withParameters(@NotNull MultiMap<String, String> parameters) {
        this.parameters = some(parameters);
        return this;
    }

    /** Set request expected sg app token header. */
    public RequestExpectation withSgAppToken(String token) {
        headers.set(HeaderNames.TEK_APP_TOKEN, token);
        return this;
    }

    /** Mark expectation as consumed. Return true if there are repetitions left. */
    boolean consume() {
        return --times > 0;
    }

    Option<Object> getContent() {
        return content;
    }

    List<MutableCookie> getCookies() {
        return cookies;
    }

    Method getMethod() {
        return method;
    }

    Option<MultiMap<String, String>> getParameters() {
        return parameters;
    }

    Option<String> getPath() {
        return path;
    }

    ResponseExpectation getResponse() {
        return response.orElse(DEFAULT_RESPONSE);
    }

    int getTimes() {
        return times;
    }
}  // end class RequestExpectation
