
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.tools.test.server;

import java.util.EnumSet;

import org.jetbrains.annotations.NotNull;
import org.junit.rules.ExternalResource;

import tekgenesis.common.core.Constants;
import tekgenesis.common.logging.Logger;
import tekgenesis.common.service.Call;
import tekgenesis.common.service.Method;

/**
 * Rule for Sui Generis mock http server.
 */
public class SgHttpServerRule extends ExternalResource {

    //~ Instance Fields ..............................................................................................................................

    private final EnumSet<ExpectationsConfiguration> configuration;
    private final int                                port;
    private SgHttpServer                             server;

    //~ Constructors .................................................................................................................................

    /**
     * Default private constructor creates {@link SgHttpServer server}. To construct a
     * SgHttpServerRule see {@link #httpServerRule()}
     */
    SgHttpServerRule(int port, EnumSet<ExpectationsConfiguration> configuration) {
        this.port          = port;
        this.configuration = configuration;
        server             = new SgHttpServer(port, configuration);
    }

    //~ Methods ......................................................................................................................................

    /** Manually assert no left expectations. Configuration is ignored. */
    public void assertNoLeftExpectations() {
        server.assertNoLeftExpectations();
    }

    /** Expected call request. */
    public RequestExpectation expectCall(@NotNull Call call) {
        return expectRequest(call.getMethod()).on(call.getUrl());
    }

    /** Expected call request with given content. */
    public RequestExpectation expectCall(@NotNull Call call, @NotNull Object content) {
        return expectCall(call).withContent(content);
    }

    /** Expected delete request on any path. */
    public RequestExpectation expectDelete() {
        return expectRequest(Method.DELETE);
    }

    /** Expected delete request on given path. */
    public RequestExpectation expectDelete(@NotNull String path) {
        return expectDelete().on(path);
    }

    /** Expected get request on any path. */
    public RequestExpectation expectGet() {
        return expectRequest(Method.GET);
    }

    /** Expected get request on given path. */
    public RequestExpectation expectGet(@NotNull String path) {
        return expectGet().on(path);
    }

    /** Expected head request on any path. */
    public RequestExpectation expectHead() {
        return expectRequest(Method.HEAD);
    }

    /** Expected head request on given path. */
    public RequestExpectation expectHead(@NotNull String path) {
        return expectHead().on(path);
    }

    /** Expected post request on any path. */
    public RequestExpectation expectPost() {
        return expectRequest(Method.POST);
    }

    /** Expected post request with given content on any path. */
    public RequestExpectation expectPost(@NotNull Object content) {
        return expectPost().withContent(content);
    }

    /** Expected post request on given path. */
    public RequestExpectation expectPost(@NotNull String path) {
        return expectPost().on(path);
    }

    /** Expected post request with given content on given path. */
    public RequestExpectation expectPost(@NotNull String path, @NotNull Object content) {
        return expectPost(content).on(path);
    }

    /** Expected put request on any path. */
    public RequestExpectation expectPut() {
        return expectRequest(Method.PUT);
    }

    /** Expected put request with given content on any path. */
    public RequestExpectation expectPut(@NotNull Object content) {
        return expectPut().withContent(content);
    }

    /** Expected put request on given path. */
    public RequestExpectation expectPut(@NotNull String path) {
        return expectPut().on(path);
    }

    /** Expected put request with given content on given path. */
    public RequestExpectation expectPut(@NotNull String path, @NotNull Object content) {
        return expectPut(content).on(path);
    }

    /** Return {@link SgHttpServer server} address. */
    public String getServerAddress() {
        return Constants.HTTP_LOCALHOST + getServerPort();
    }

    /**
     * Return server control. Use with caution: attempting to start/shutdown server on invalid
     * states will throw exception.
     */
    public ServerControl getServerControl() {
        return new ServerControl() {
            @Override public void start() {
                server = new SgHttpServer(port, configuration);
                server.start();
            }
            @Override public void shutdown() {
                server.shutdown();
            }
        };
    }

    /** Return {@link SgHttpServer server} port. */
    public int getServerPort() {
        return server.getPort();
    }

    /** Shutdown {@link SgHttpServer server}. */
    @Override protected void after() {
        server.shutdown();
        server.assertShutdownLeftExpectations();
    }

    /** Start {@link SgHttpServer server}. */
    @Override protected void before()
        throws Throwable
    {
        server.start();
    }

    private RequestExpectation expectRequest(Method method) {
        return server.expectRequest(method);
    }

    //~ Methods ......................................................................................................................................

    /** Create and return a rule builder. */
    public static SgHttpServerRuleBuilder httpServerRule() {
        return new SgHttpServerRuleBuilder();
    }

    //~ Static Fields ................................................................................................................................

    public static final Logger logger = Logger.getLogger(SgHttpServerRule.class);
}  // end class SgHttpServerRule
