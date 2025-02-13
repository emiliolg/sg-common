
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker.metric;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({ "NestedTryStatement", "MagicNumber", "JavaDoc" })
public class InvocationMetricsPollerTest {

    //~ Methods ......................................................................................................................................

    public void sleep(int delay) {
        try {
            Thread.sleep(delay);
        }
        catch (final InterruptedException ignored) {}
    }

    @Test public void testStartStopStart() {
        final AtomicInteger metricsCount = new AtomicInteger();

        final InvocationMetricsPoller poller = new InvocationMetricsPoller(json -> {
                    System.out.println("Received: " + json);
                    metricsCount.incrementAndGet();
                },
                100);

        try {
            final InvocationMetrics instance = InvocationMetrics.getOrCreateInstance("MetricsPollerTest", "Tests");
            instance.markSuccess();

            poller.start();

            sleep(500);

            poller.pause();

            // Ensure count delay is covered
            sleep(200);

            final int count1 = metricsCount.get();

            // Some metrics must have been collected
            assertThat(count1).isGreaterThan(0);

            sleep(500);

            final int count2 = metricsCount.get();

            // They should be the same since we were paused
            assertThat(count1).isEqualTo(count2);

            poller.start();

            sleep(500);

            final int count3 = metricsCount.get();

            // We should have more metrics again
            assertThat(count3).isGreaterThan(count2);
        }
        finally {
            poller.shutdown();
        }
    }  // end method testStartStopStart
}  // end class InvocationMetricsPollerTest
