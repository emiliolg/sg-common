
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import org.junit.ClassRule;
import org.junit.Test;

import tekgenesis.common.env.logging.LogConfig;
import tekgenesis.common.invoker.exception.InvokerConnectionException;
import tekgenesis.common.invoker.metric.InvocationEvent;
import tekgenesis.common.invoker.metric.InvocationMetrics;
import tekgenesis.common.tools.test.server.ConnectionTimeoutRule;
import tekgenesis.common.tools.test.server.SgHttpServerRule;

import static org.assertj.core.api.Assertions.*;

import static tekgenesis.common.service.Method.GET;
import static tekgenesis.common.service.Status.OK;
import static tekgenesis.common.tools.test.server.SgHttpServerRule.httpServerRule;

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class InvokerMetricsRecorderTest {

    //~ Methods ......................................................................................................................................

    @Test public void testFailureResponses() {
        final String path = "/failure";

        final String      host        = "http://unknown-host:8080";
        final HttpInvoker httpInvoker = HttpInvokers.invoker(host).withMetrics((s, p, m) -> p);
        try {
            httpInvoker.resource(path).head();
            failBecauseExceptionWasNotThrown(InvokerConnectionException.class);
        }
        catch (final InvokerConnectionException e) {
            assertThat(e.isUnknownHostException()).isTrue();
        }

        final InvocationMetrics metrics = InvocationMetrics.getInstance(path);

        assertThat(metrics.getRollingCount(InvocationEvent.SUCCESS)).isEqualTo(0);
        assertThat(metrics.getRollingCount(InvocationEvent.EXCEPTION_THROWN)).isEqualTo(0);
        assertThat(metrics.getRollingCount(InvocationEvent.FAILURE)).isEqualTo(1);
        assertThat(metrics.getRollingCount(InvocationEvent.FAILURE)).isEqualTo(1);
        assertThat(metrics.getRollingCount(InvocationEvent.BAD_REQUEST)).isEqualTo(0);
        assertThat(metrics.getRollingCount(InvocationEvent.TIMEOUT)).isEqualTo(0);

        final InvocationMetrics.HealthCounts healthCounts = metrics.getHealthCounts();
        assertThat(healthCounts.getTotalRequests()).isEqualTo(1);
        assertThat(healthCounts.getErrorCount()).isEqualTo(1);
        assertThat(healthCounts.getErrorPercentage()).isEqualTo(100);
    }

    @Test public void testSuccessResponses() {
        final int repetitions = 5;

        final HttpInvoker invoker = HttpInvokers.invoker(server.getServerAddress()).withMetrics((s, p, m) -> p);
        server.expectGet().repeated(repetitions);

        final String path = "/success";
        for (int i = 0; i < repetitions; i++) {
            final HttpInvokerResult<?> result = invoker.resource(path).invoke(GET).execute();
            assertThat(result.getStatus()).isEqualTo(OK);
        }

        final InvocationMetrics metrics = InvocationMetrics.getInstance(path);

        assertThat(metrics.getRollingCount(InvocationEvent.SUCCESS)).isEqualTo(repetitions);
        assertThat(metrics.getRollingCount(InvocationEvent.EXCEPTION_THROWN)).isEqualTo(0);
        assertThat(metrics.getRollingCount(InvocationEvent.FAILURE)).isEqualTo(0);
        assertThat(metrics.getRollingCount(InvocationEvent.BAD_REQUEST)).isEqualTo(0);
        assertThat(metrics.getRollingCount(InvocationEvent.TIMEOUT)).isEqualTo(0);

        final InvocationMetrics.HealthCounts healthCounts = metrics.getHealthCounts();
        assertThat(healthCounts.getTotalRequests()).isEqualTo(repetitions);
        assertThat(healthCounts.getErrorCount()).isEqualTo(0);
        assertThat(healthCounts.getErrorPercentage()).isEqualTo(0);
    }

    @Test public void testTimeoutResponses() {
        final HttpInvoker httpInvoker = HttpInvokers.invoker(timeout.connectionUrl()).withMetrics((s, p, m) -> p);
        httpInvoker.withConnectTimeout(400);
        final String path = "/timeout";
        try {
            httpInvoker.resource(path).head();
            failBecauseExceptionWasNotThrown(InvokerConnectionException.class);
        }
        catch (final InvokerConnectionException e) {
            assertThat(e.isConnectTimeoutException()).isTrue();
        }

        final InvocationMetrics metrics = InvocationMetrics.getInstance(path);

        assertThat(metrics.getRollingCount(InvocationEvent.SUCCESS)).isEqualTo(0);
        assertThat(metrics.getRollingCount(InvocationEvent.EXCEPTION_THROWN)).isEqualTo(0);
        assertThat(metrics.getRollingCount(InvocationEvent.FAILURE)).isEqualTo(0);
        assertThat(metrics.getRollingCount(InvocationEvent.BAD_REQUEST)).isEqualTo(0);
        assertThat(metrics.getRollingCount(InvocationEvent.TIMEOUT)).isEqualTo(1);

        final InvocationMetrics.HealthCounts healthCounts = metrics.getHealthCounts();
        assertThat(healthCounts.getTotalRequests()).isEqualTo(1);
        assertThat(healthCounts.getErrorCount()).isEqualTo(1);
        assertThat(healthCounts.getErrorPercentage()).isEqualTo(100);
    }

    //~ Static Fields ................................................................................................................................

    @ClassRule public static final SgHttpServerRule      server  = httpServerRule().onStart(sgHttpServerRule -> LogConfig.start()).build();
    @ClassRule public static final ConnectionTimeoutRule timeout = new ConnectionTimeoutRule();
}  // end class InvokerMetricsRecorderTest
