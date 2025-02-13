
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker.metric;

import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.invoker.metric.InvocationMetrics.HealthCounts;
import tekgenesis.common.logging.Logger;

/**
 * Polls Invoker metrics and output JSON strings for each metric to a MetricsPollerListener. Polling
 * can be stopped/started. Use shutdown() to permanently shutdown the poller.
 */
public class InvocationMetricsPoller {

    //~ Instance Fields ..............................................................................................................................

    private final int                         delay;
    private final ScheduledExecutorService    executor;
    private final MetricsAsJsonPollerListener listener;
    private final AtomicBoolean               running       = new AtomicBoolean(false);
    private volatile ScheduledFuture<?>       scheduledTask = null;

    /**
     * Used to protect against leaking ExecutorServices and threads if this class is abandoned for
     * GC without shutting down.
     */
    @SuppressWarnings("unused")
    private final Object finalizerGuardian = new Object() {
            @SuppressWarnings("FinalizeDoesntCallSuperFinalize")
            protected void finalize()
                throws Throwable
            {
                if (!executor.isShutdown()) {
                    logger.warning("Poller was not shutdown. Caught in Finalize Guardian and shutting down.");
                    try {
                        shutdown();
                    }
                    catch (final Exception e) {
                        logger.error("Failed to shutdown.", e);
                    }
                }
            }
        };

    //~ Constructors .................................................................................................................................

    /**
     * Allocate resources to begin polling. Use <code>start</code> to begin polling. Use <code>
     * shutdown</code> to cleanup resources and stop polling. Use <code>pause</code> to temporarily
     * stop polling that can be restarted again with <code>start</code>.
     */
    public InvocationMetricsPoller(MetricsAsJsonPollerListener listener, int delay) {
        this.listener = listener;
        this.delay    = delay;
        executor      = new ScheduledThreadPoolExecutor(1, new MetricsPollerThreadFactory());
    }

    //~ Methods ......................................................................................................................................

    /**
     * Pause (stop) polling. Polling can be started again with start as long as shutdown is not
     * called.
     */
    public synchronized void pause() {
        if (running.compareAndSet(true, false)) {
            logger.info("Stopping the InvokerMetricsPoller");
            scheduledTask.cancel(true);
        }
        else logger.debug("Attempted to pause a stopped poller");
    }

    /** Stops polling and shuts down the ExecutorService. This instance can no longer be used. */
    public synchronized void shutdown() {
        pause();
        executor.shutdown();
    }

    /** Start polling. */
    public synchronized void start() {
        if (running.compareAndSet(false, true)) {
            logger.info("Starting InvokerMetricsPoller...");
            try {
                scheduledTask = executor.scheduleWithFixedDelay(new MetricsPoller(listener), 0, delay, TimeUnit.MILLISECONDS);
            }
            catch (final Throwable ex) {
                logger.error("Exception while creating the MetricsPoller task", ex);
                running.set(false);
            }
        }
    }

    /** Test if poller is running. */
    public boolean isRunning() {
        return running.get();
    }

    //~ Static Fields ................................................................................................................................

    static final Logger logger = Logger.getLogger(InvocationMetricsPoller.class);

    //~ Inner Interfaces .............................................................................................................................

    public interface MetricsAsJsonPollerListener {
        /** Handle json metric. */
        void handleJsonMetric(String json);
    }

    //~ Inner Classes ................................................................................................................................

    private class MetricsPoller implements Runnable {
        private final JsonFactory factory = new JsonFactory();

        private final MetricsAsJsonPollerListener output;

        public MetricsPoller(MetricsAsJsonPollerListener output) {
            this.output = output;
        }

        @Override public void run() {
            try {
                for (final InvocationMetrics metrics : InvocationMetrics.getInstances()) {
                    final String jsonString = getCommandJson(metrics);
                    output.handleJsonMetric(jsonString);
                }
            }
            catch (final Exception e) {
                logger.warning("Failed to output metrics as JSON", e);
                pause();
            }
        }

        @SuppressWarnings({ "OverlyLongMethod", "DuplicateStringLiteralInspection", "MagicNumber" })
        private String getCommandJson(@NotNull InvocationMetrics metrics)
            throws IOException
        {
            final StringWriter  jsonString = new StringWriter();
            final JsonGenerator json       = factory.createGenerator(jsonString);

            json.writeStartObject();
            json.writeStringField("type", "HystrixCommand");
            json.writeStringField("name", metrics.getCommandKey());
            json.writeStringField("group", metrics.getCommandGroup());
            json.writeNumberField("currentTime", System.currentTimeMillis());
            json.writeBooleanField("isCircuitBreakerOpen", false);

            final HealthCounts healthCounts = metrics.getHealthCounts();
            json.writeNumberField("errorPercentage", healthCounts.getErrorPercentage());
            json.writeNumberField("errorCount", healthCounts.getErrorCount());
            json.writeNumberField("requestCount", healthCounts.getTotalRequests());

            // Rolling counters
            json.writeNumberField("rollingCountBadRequests", metrics.getRollingCount(InvocationEvent.BAD_REQUEST));
            json.writeNumberField("rollingCountCollapsedRequests", metrics.getRollingCount(InvocationEvent.COLLAPSED));
            json.writeNumberField("rollingCountEmit", metrics.getRollingCount(InvocationEvent.EMIT));
            json.writeNumberField("rollingCountExceptionsThrown", metrics.getRollingCount(InvocationEvent.EXCEPTION_THROWN));
            json.writeNumberField("rollingCountFailure", metrics.getRollingCount(InvocationEvent.FAILURE));
            json.writeNumberField("rollingCountEmit", metrics.getRollingCount(InvocationEvent.FALLBACK_EMIT));
            json.writeNumberField("rollingCountFallbackFailure", metrics.getRollingCount(InvocationEvent.FALLBACK_FAILURE));
            json.writeNumberField("rollingCountFallbackRejection", metrics.getRollingCount(InvocationEvent.FALLBACK_REJECTION));
            json.writeNumberField("rollingCountFallbackSuccess", metrics.getRollingCount(InvocationEvent.FALLBACK_SUCCESS));
            json.writeNumberField("rollingCountResponsesFromCache", metrics.getRollingCount(InvocationEvent.RESPONSE_FROM_CACHE));
            json.writeNumberField("rollingCountSemaphoreRejected", metrics.getRollingCount(InvocationEvent.SEMAPHORE_REJECTED));
            json.writeNumberField("rollingCountShortCircuited", metrics.getRollingCount(InvocationEvent.SHORT_CIRCUITED));
            json.writeNumberField("rollingCountSuccess", metrics.getRollingCount(InvocationEvent.SUCCESS));
            json.writeNumberField("rollingCountThreadPoolRejected", metrics.getRollingCount(InvocationEvent.THREAD_POOL_REJECTED));
            json.writeNumberField("rollingCountTimeout", metrics.getRollingCount(InvocationEvent.TIMEOUT));

            json.writeNumberField("currentConcurrentExecutionCount", metrics.getCurrentConcurrentExecutionCount());
            json.writeNumberField("rollingMaxConcurrentExecutionCount", metrics.getRollingMaxConcurrentExecutions());

            // Latency execution percentiles
            json.writeNumberField("latencyExecute_mean", metrics.getExecutionTimeMean());
            json.writeObjectFieldStart("latencyExecute");
            json.writeNumberField("0", metrics.getExecutionTimePercentile(0));
            json.writeNumberField("25", metrics.getExecutionTimePercentile(25));
            json.writeNumberField("50", metrics.getExecutionTimePercentile(50));
            json.writeNumberField("75", metrics.getExecutionTimePercentile(75));
            json.writeNumberField("90", metrics.getExecutionTimePercentile(90));
            json.writeNumberField("95", metrics.getExecutionTimePercentile(95));
            json.writeNumberField("99", metrics.getExecutionTimePercentile(99));
            json.writeNumberField("99.5", metrics.getExecutionTimePercentile(99.5));
            json.writeNumberField("100", metrics.getExecutionTimePercentile(100));
            json.writeEndObject();

            // Latency total percentiles
            json.writeNumberField("latencyTotal_mean", metrics.getTotalTimeMean());
            json.writeObjectFieldStart("latencyTotal");
            json.writeNumberField("0", metrics.getTotalTimePercentile(0));
            json.writeNumberField("25", metrics.getTotalTimePercentile(25));
            json.writeNumberField("50", metrics.getTotalTimePercentile(50));
            json.writeNumberField("75", metrics.getTotalTimePercentile(75));
            json.writeNumberField("90", metrics.getTotalTimePercentile(90));
            json.writeNumberField("95", metrics.getTotalTimePercentile(95));
            json.writeNumberField("99", metrics.getTotalTimePercentile(99));
            json.writeNumberField("99.5", metrics.getTotalTimePercentile(99.5));
            json.writeNumberField("100", metrics.getTotalTimePercentile(100));
            json.writeEndObject();

            // property values for reporting what is actually seen by the command rather than what was set somewhere
            final InvokerMetricsProperties props = metrics.getProperties();

            json.writeNumberField("propertyValue_circuitBreakerRequestVolumeThreshold", props.circuitBreakerRequestVolumeThreshold);
            json.writeNumberField("propertyValue_circuitBreakerSleepWindowInMilliseconds", props.circuitBreakerSleepWindowInMilliseconds);
            json.writeNumberField("propertyValue_circuitBreakerErrorThresholdPercentage", props.circuitBreakerErrorThresholdPercentage);
            json.writeBooleanField("propertyValue_circuitBreakerForceOpen", props.circuitBreakerForceOpen);
            json.writeBooleanField("propertyValue_circuitBreakerForceClosed", props.circuitBreakerForceClosed);
            json.writeBooleanField("propertyValue_circuitBreakerEnabled", props.circuitBreakerEnabled);

            json.writeStringField("propertyValue_executionIsolationStrategy", props.executionIsolationStrategy);
            json.writeNumberField("propertyValue_executionIsolationThreadTimeoutInMilliseconds", props.executionTimeoutInMilliseconds);
            json.writeNumberField("propertyValue_executionTimeoutInMilliseconds", props.executionTimeoutInMilliseconds);
            json.writeBooleanField("propertyValue_executionIsolationThreadInterruptOnTimeout", props.executionIsolationThreadInterruptOnTimeout);
            json.writeStringField("propertyValue_executionIsolationThreadPoolKeyOverride", props.executionIsolationThreadPoolKeyOverride);
            json.writeNumberField("propertyValue_executionIsolationSemaphoreMaxConcurrentRequests",
                props.executionIsolationSemaphoreMaxConcurrentRequests);
            json.writeNumberField("propertyValue_fallbackIsolationSemaphoreMaxConcurrentRequests",
                props.fallbackIsolationSemaphoreMaxConcurrentRequests);

            json.writeNumberField("propertyValue_metricsRollingStatisticalWindowInMilliseconds", props.metricsRollingStatisticalWindowInMilliseconds);

            json.writeBooleanField("propertyValue_requestCacheEnabled", props.requestCacheEnabled);
            json.writeBooleanField("propertyValue_requestLogEnabled", props.requestLogEnabled);

            json.writeNumberField("reportingHosts", 1);  // This will get summed across all instances in a cluster

            json.writeEndObject();
            json.close();

            return jsonString.getBuffer().toString();
        }  // end method getCommandJson
    }  // end class MetricsPoller

    private class MetricsPollerThreadFactory implements ThreadFactory {
        private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

        public Thread newThread(@NotNull Runnable r) {
            final Thread t = defaultFactory.newThread(r);
            t.setName(threadName);
            return t;
        }

        private static final String threadName = "InvokerMetricPoller";
    }
}  // end class InvocationMetricsPoller
