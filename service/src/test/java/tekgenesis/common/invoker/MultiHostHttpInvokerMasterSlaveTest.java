
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.junit.ClassRule;
import org.junit.Test;

import tekgenesis.common.env.logging.LogConfig;
import tekgenesis.common.invoker.exception.InvokerInvocationException;
import tekgenesis.common.invoker.exception.NoInvokerAvailable;
import tekgenesis.common.tools.test.server.ConnectionTimeoutRule;
import tekgenesis.common.tools.test.server.RequestExpectation;
import tekgenesis.common.tools.test.server.SgHttpServerRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import static tekgenesis.common.invoker.HttpInvokers.invoker;
import static tekgenesis.common.media.MediaType.TEXT_PLAIN;
import static tekgenesis.common.service.Method.HEAD;
import static tekgenesis.common.service.Status.OK;
import static tekgenesis.common.service.Status.SERVICE_UNAVAILABLE;
import static tekgenesis.common.tools.test.server.SgHttpServerRule.httpServerRule;

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber", "ClassWithTooManyMethods" })
public class MultiHostHttpInvokerMasterSlaveTest {

    //~ Instance Fields ..............................................................................................................................

    private final String timeoutServer = timeout.connectionUrl();
    private final String unknownServer = "http://unknown-host:8080";

    //~ Methods ......................................................................................................................................

    @Test public void testLastSlaveWorking() {
        expectHead();
        final HttpInvoker multiHost = invoker(Strategy.MASTER_SLAVE, unknownServer, timeoutServer, workingServer).withConnectTimeout(400);
        invokerHeadAssertOk(multiHost);
    }

    @Test public void testMasterTimeoutSlaveWorking() {
        expectHead();
        final HttpInvoker multiHost = invoker(Strategy.MASTER_SLAVE, timeoutServer, workingServer, unknownServer).withConnectTimeout(400);
        invokerHeadAssertOk(multiHost);
    }

    @Test public void testMasterUnknownSlaveWorking() {
        expectHead();
        final HttpInvoker multiHost = invoker(Strategy.MASTER_SLAVE, unknownServer, workingServer, timeoutServer);
        invokerHeadAssertOk(multiHost);
    }

    @Test public void testMasterWorking() {
        expectHead();
        final HttpInvoker multiHost = invoker(Strategy.MASTER_SLAVE, workingServer, timeoutServer, unknownServer);
        invokerHeadAssertOk(multiHost);
    }

    @Test public void testMinimumAmountOfInvokers() {
        expectHead();
        final HttpInvoker invoker = invoker(workingServer);
        invokerHeadAssertOk(invoker);
    }

    @Test public void testNoneWorking() {
        expectHead().respond(SERVICE_UNAVAILABLE);
        final HttpInvoker multiHost = invoker(Strategy.MASTER_SLAVE, unknownServer, workingServer, timeoutServer).withConnectTimeout(400);
        try {
            invokerHeadAssertOk(multiHost);
            failBecauseExceptionWasNotThrown(NoInvokerAvailable.class);
        }
        catch (final NoInvokerAvailable e) {
            assertThat(e.getMessage()).contains("No invoker available");
            assertThat(e.getCause()).isInstanceOf(SocketTimeoutException.class);
            assertThat(e.getCause().getCause()).isInstanceOf(InvokerInvocationException.class);
            assertThat(e.getCause().getCause().getCause()).isInstanceOf(UnknownHostException.class);
        }
    }

    private RequestExpectation expectHead() {
        return server.expectHead();
    }

    private void invokerHeadAssertOk(HttpInvoker invoker) {
        final HttpInvokerResult<?> result = invoker.resource("/head").invoke(HEAD).execute();
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getHeaders().getContentType()).isEqualTo(TEXT_PLAIN);
    }

    //~ Static Fields ................................................................................................................................

    @ClassRule public static final SgHttpServerRule      server  = httpServerRule().onStart(sgHttpServerRule -> LogConfig.start()).build();
    @ClassRule public static final ConnectionTimeoutRule timeout = new ConnectionTimeoutRule();

    private static final String workingServer = server.getServerAddress();
}  // end class MultiHostHttpInvokerMasterSlaveTest
