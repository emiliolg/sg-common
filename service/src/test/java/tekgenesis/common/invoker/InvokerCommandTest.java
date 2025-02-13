
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import java.io.IOException;

import org.junit.ClassRule;
import org.junit.Test;

import rx.Observable;

import tekgenesis.common.env.logging.LogConfig;
import tekgenesis.common.invoker.exception.InvokerConnectionException;
import tekgenesis.common.tools.test.server.ConnectionTimeoutRule;
import tekgenesis.common.tools.test.server.SgHttpServerRule;

import static java.lang.Integer.parseInt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import static tekgenesis.common.service.Method.GET;
import static tekgenesis.common.service.Method.HEAD;
import static tekgenesis.common.service.Status.BAD_REQUEST;
import static tekgenesis.common.tools.test.server.SgHttpServerRule.httpServerRule;

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber", "ClassWithTooManyMethods" })
public class InvokerCommandTest {

    //~ Methods ......................................................................................................................................

    @Test public void testInvokerCommandChaining() {
        server.expectGet().repeated(5).respondOkWith(request -> "/" + (2 * parseInt(request.getPath().substring(1)))).delay(100);

        final long start = System.currentTimeMillis();

        Observable<HttpInvokerResult<String>> chaining = invoker.resource("/1").invoke(GET, String.class).observe();

        for (int i = 1; i < 5; i++)
            chaining = chaining.concatMap(result -> invoker.resource(result.get()).invoke(GET, String.class).observe());

        final String result = chaining.toBlocking().first().get();

        assertThat(result).isEqualTo("/32");
        assertThat(System.currentTimeMillis() - start).as("Observe should have been executed chained").isGreaterThanOrEqualTo(500);
    }

    @Test public void testInvokerCommandMerge() {
        server.expectGet().respondOkWith("Tortoise").delay(500);
        server.expectGet().respondOkWith("Hare").delay(200);

        final InvokerCommand<String> alpha = invoker.resource("/race").invoke(GET, String.class);
        final InvokerCommand<String> beta  = invoker.resource("/race").invoke(GET, String.class);

        final long start = System.currentTimeMillis();

        final Observable<HttpInvokerResult<String>> a = alpha.observe();
        final Observable<HttpInvokerResult<String>> b = beta.observe();

        final Observable<HttpInvokerResult<String>> race = a.mergeWith(b);

        final String first = race.toBlocking().first().get();

        assertThat(first).isEqualTo("Hare");
        assertThat(System.currentTimeMillis() - start).as("Observe should have been executed on parallel").isLessThan(300);
    }

    @Test public void testInvokerCommandObserveErrors() {
        server.expectGet().respondOkWith("Tortoise").delay(500);
        server.expectGet().respond(BAD_REQUEST).delay(200);

        final InvokerCommand<String> alpha = invoker.resource("/race").invoke(GET, String.class);
        final InvokerCommand<String> beta  = invoker.resource("/race").invoke(GET, String.class);

        final long start = System.currentTimeMillis();

        final Observable<HttpInvokerResult<String>> empty = Observable.empty();
        final Observable<HttpInvokerResult<String>> a     = alpha.observe().onExceptionResumeNext(empty);
        final Observable<HttpInvokerResult<String>> b     = beta.observe().onExceptionResumeNext(empty);

        final Observable<HttpInvokerResult<String>> race = a.mergeWith(b);

        final String first = race.toBlocking().first().get();

        assertThat(first).isEqualTo("Tortoise");
        assertThat(System.currentTimeMillis() - start).as("Observe should have been executed on parallel").isGreaterThanOrEqualTo(500);
    }

    @Test public void testInvokerConnectException()
        throws IOException
    {
        final HttpInvoker httpInvoker = HttpInvokers.invoker("http://localhost:1");
        try {
            final InvokerCommand<?> command = httpInvoker.resource("/unreachable").invoke(HEAD);
            command.execute();
            failBecauseExceptionWasNotThrown(InvokerConnectionException.class);
        }
        catch (final InvokerConnectionException e) {
            assertThat(e.isConnectException()).isTrue();
        }
    }

    @Test public void testInvokerConnectTimeout()
        throws IOException
    {
        final HttpInvoker custom = HttpInvokers.invoker(timeout.connectionUrl());
        try {
            final InvokerCommand<?> command = custom.resource("/unreachable").invoke(HEAD);
            command.withConnectTimeout(200).execute();
            failBecauseExceptionWasNotThrown(InvokerConnectionException.class);
        }
        catch (final InvokerConnectionException e) {
            assertThat(e.isConnectTimeoutException()).isTrue();
        }
    }

    @Test public void testInvokerReadTimeout()
        throws IOException
    {
        server.expectHead().respondOk().delay(500);
        final HttpInvoker custom = HttpInvokers.invoker(server.getServerAddress());
        try {
            final InvokerCommand<?> command = custom.resource("/delayed").invoke(HEAD);
            command.withReadTimeout(200).execute();
            failBecauseExceptionWasNotThrown(InvokerConnectionException.class);
        }
        catch (final InvokerConnectionException e) {
            assertThat(e.isConnectTimeoutException()).isTrue();
        }
    }

    @Test public void testInvokerUnknownHostException()
        throws IOException
    {
        final HttpInvoker httpInvoker = HttpInvokers.invoker("http://unknown-host:8080");
        try {
            final InvokerCommand<?> command = httpInvoker.resource("/unreachable").invoke(HEAD);
            command.execute();
            failBecauseExceptionWasNotThrown(InvokerConnectionException.class);
        }
        catch (final InvokerConnectionException e) {
            assertThat(e.isUnknownHostException()).isTrue();
        }
    }

    //~ Static Fields ................................................................................................................................

    @ClassRule public static final SgHttpServerRule      server  = httpServerRule().onStart(s -> LogConfig.start()).build();
    @ClassRule public static final ConnectionTimeoutRule timeout = new ConnectionTimeoutRule();

    private static final HttpInvoker invoker = HttpInvokers.invoker(server.getServerAddress());
}  // end class InvokerCommandTest
