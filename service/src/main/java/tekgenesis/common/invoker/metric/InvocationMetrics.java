
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker.metric;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.env.context.Context;
import tekgenesis.common.invoker.HttpInvoker;
import tekgenesis.common.logging.Logger;
import tekgenesis.common.metric.RollingNumber;
import tekgenesis.common.metric.RollingPercentile;

/**
 * Used by {@link HttpInvoker invokers} to record metrics.
 */
public class InvocationMetrics {

    //~ Instance Fields ..............................................................................................................................

    private final AtomicInteger concurrentExecutionCount = new AtomicInteger();

    private final RollingNumber<InvocationEvent> counter;
    private final String                         group;

    private volatile HealthCounts healthCountsSnapshot = null;
    private final String          key;

    private final AtomicLong               lastHealthCountsSnapshot = new AtomicLong(System.currentTimeMillis());
    private final RollingPercentile        percentileExecution;
    private final RollingPercentile        percentileTotal;
    private final InvokerMetricsProperties props;

    //~ Constructors .................................................................................................................................

    InvocationMetrics(@NotNull String key, @NotNull String group, @NotNull InvokerMetricsProperties props) {
        counter             = new RollingNumber<>(InvocationEvent.class, props.statisticalWindowTime, props.statisticalWindowBuckets);
        this.key            = key;
        this.group          = group;
        this.props          = props;
        percentileExecution = new RollingPercentile(props.percentileWindowTime, props.percentileWindowBuckets, props.percentileBucketSize);
        percentileTotal     = new RollingPercentile(props.percentileWindowTime, props.percentileWindowBuckets, props.percentileBucketSize);
    }

    //~ Methods ......................................................................................................................................

    /** Execution time of {@link tekgenesis.common.invoker.HttpInvokerImpl#execute}. */
    public void addInvocationExecutionTime(long duration) {
        percentileExecution.addValue((int) duration);
    }

    /** Called when an invocation completes with an error code. */
    public void markError() {
        counter.increment(InvocationEvent.BAD_REQUEST);
    }

    /** Called when an invocation fails to complete. */
    public void markFailure() {
        counter.increment(InvocationEvent.FAILURE);
    }

    /** Called when an invocation successfully completes. */
    public void markSuccess() {
        counter.increment(InvocationEvent.SUCCESS);
    }

    /** Called when an invocation times out (fails to complete). */
    public void markTimeout() {
        counter.increment(InvocationEvent.TIMEOUT);
    }

    /** Group key these metrics represent. */
    public String getCommandGroup() {
        return group;
    }

    /** Key these metrics represent. */
    public String getCommandKey() {
        return key;
    }

    /** Current number of concurrent invocations. */
    public int getCurrentConcurrentExecutionCount() {
        return concurrentExecutionCount.get();
    }

    /** The mean (average) execution time (in milliseconds) for the invocation run. */
    public int getExecutionTimeMean() {
        return percentileExecution.getMean();
    }

    /** Retrieve the execution time (in milliseconds) for the invocation at a given percentile. */
    public int getExecutionTimePercentile(double percentile) {
        return percentileExecution.getPercentile(percentile);
    }

    /** Retrieve a snapshot of total requests, error count and error percentage. */
    public HealthCounts getHealthCounts() {
        // We put an interval between snapshots so high-volume commands don't
        // spend too much unnecessary time calculating metrics in very small time periods
        final long lastTime    = lastHealthCountsSnapshot.get();
        final long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= props.healthSnapshotInterval || healthCountsSnapshot == null) {
            if (lastHealthCountsSnapshot.compareAndSet(lastTime, currentTime)) {
                // Our thread won setting the snapshot time so we will proceed with generating a new snapshot
                // losing threads will continue using the old snapshot
                final long success            = counter.getRollingSum(InvocationEvent.SUCCESS);
                final long failure            = counter.getRollingSum(InvocationEvent.FAILURE);
                final long timeout            = counter.getRollingSum(InvocationEvent.TIMEOUT);
                final long threadPoolRejected = counter.getRollingSum(InvocationEvent.THREAD_POOL_REJECTED);
                final long semaphoreRejected  = counter.getRollingSum(InvocationEvent.SEMAPHORE_REJECTED);
                final long shortCircuited     = counter.getRollingSum(InvocationEvent.SHORT_CIRCUITED);
                final long totalCount         = failure + timeout + threadPoolRejected + shortCircuited + semaphoreRejected + success;
                final long errorCount         = failure + timeout + threadPoolRejected + shortCircuited + semaphoreRejected;
                int        errorPercentage    = 0;

                if (totalCount > 0) errorPercentage = (int) ((double) errorCount / totalCount * 100);

                healthCountsSnapshot = new HealthCounts(totalCount, errorCount, errorPercentage);
            }
        }
        return healthCountsSnapshot;
    }

    /** {@link InvokerMetricsProperties} of the invocation these metrics represent. */
    public InvokerMetricsProperties getProperties() {
        return props;
    }

    /** Get the rolling count (sum) for the given {@link InvocationEvent}. */
    public long getRollingCount(@NotNull InvocationEvent event) {
        return counter.getRollingSum(event);
    }

    /** Get the rolling count (max) for the given concurrent invocations. */
    public long getRollingMaxConcurrentExecutions() {
        return counter.getRollingMaxValue(InvocationEvent.COMMAND_MAX_ACTIVE);
    }

    /** The mean (average) execution time (in milliseconds) for invocations. */
    public int getTotalTimeMean() {
        return percentileTotal.getMean();
    }

    /** Retrieve the total end-to-end invocation time (in milliseconds) for a given percentile. */
    public int getTotalTimePercentile(double percentile) {
        return percentileTotal.getPercentile(percentile);
    }

    /** Complete execution time. */
    void addUserThreadExecutionTime(long duration) {
        percentileTotal.addValue((int) duration);
    }

    /** Decrement concurrent requests counter. */
    void decrementConcurrentExecutionCount() {
        concurrentExecutionCount.decrementAndGet();
    }

    /** Increment concurrent requests counter. */
    void incrementConcurrentExecutionCount() {
        final int numConcurrent = concurrentExecutionCount.incrementAndGet();
        counter.updateRollingMax(InvocationEvent.COMMAND_MAX_ACTIVE, (long) numConcurrent);
    }

    void markEmit() {
        counter.increment(InvocationEvent.EMIT);
    }

    /** When an invocation throws an exception. */
    void markExceptionThrown() {
        counter.increment(InvocationEvent.EXCEPTION_THROWN);
    }

    void markFallbackEmit() {
        counter.increment(InvocationEvent.FALLBACK_EMIT);
    }

    void markFallbackFailure() {
        counter.increment(InvocationEvent.FALLBACK_FAILURE);
    }

    void markFallbackRejection() {
        counter.increment(InvocationEvent.FALLBACK_REJECTION);
    }

    void markFallbackSuccess() {
        counter.increment(InvocationEvent.FALLBACK_SUCCESS);
    }

    void markResponseFromCache() {
        counter.increment(InvocationEvent.RESPONSE_FROM_CACHE);
    }

    void markSemaphoreRejection() {
        counter.increment(InvocationEvent.SEMAPHORE_REJECTED);
    }

    void markShortCircuited() {
        counter.increment(InvocationEvent.SHORT_CIRCUITED);
    }

    void markThreadPoolRejection() {
        counter.increment(InvocationEvent.THREAD_POOL_REJECTED);
    }

    void resetCounter() {
        counter.reset();
        lastHealthCountsSnapshot.set(System.currentTimeMillis());
        healthCountsSnapshot = null;
    }

    //~ Methods ......................................................................................................................................

    /** Get the {@link InvocationMetrics} instance for a given key or null if one does not exist. */
    public static InvocationMetrics getInstance(@NotNull String key) {
        return metrics.get(key);
    }

    /** All registered instances of {@link InvocationMetrics}. */
    public static Collection<InvocationMetrics> getInstances() {
        return Collections.unmodifiableCollection(metrics.values());
    }

    /**
     * Get or create the {@link InvocationMetrics} instance for a given invocation key. This is
     * thread-safe and ensures only 1 {@link InvocationMetrics} per key.
     */
    public static InvocationMetrics getOrCreateInstance(@NotNull String key, @NotNull String commandGroup) {
        // Attempt to retrieve from cache first
        final InvocationMetrics cached = metrics.get(key);
        if (cached != null) return cached;

        // It doesn't exist so we need to create it
        final InvocationMetrics created = new InvocationMetrics(key, commandGroup, properties);
        // Attempt to store it (race other threads)
        final InvocationMetrics existing = metrics.putIfAbsent(key, created);

        return existing == null ? created : existing;
    }

    /** Clears all state from metrics. Metrics will be started from scratch. */
    static void reset() {
        metrics.clear();
    }

    //~ Static Fields ................................................................................................................................

    private static final Logger logger = Logger.getLogger(InvocationMetrics.class);

    private static final ConcurrentHashMap<String, InvocationMetrics> metrics = new ConcurrentHashMap<>();

    private static final InvokerMetricsProperties properties = Context.getEnvironment().get(InvokerMetricsProperties.class);

    //~ Inner Classes ................................................................................................................................

    /**
     * Number of requests during rolling window. Number that failed (failure + success + timeout +
     * threadPoolRejected + shortCircuited + semaphoreRejected). Error percentage;
     */
    public static class HealthCounts {
        private final long errorCount;
        private final int  errorPercentage;
        private final long totalCount;

        HealthCounts(long total, long error, int errorPercentage) {
            totalCount           = total;
            errorCount           = error;
            this.errorPercentage = errorPercentage;
        }

        /** Return error count. */
        public long getErrorCount() {
            return errorCount;
        }

        /** Return error percentage. */
        public int getErrorPercentage() {
            return errorPercentage;
        }

        /** Return total requests. */
        public long getTotalRequests() {
            return totalCount;
        }
    }
}  // end class InvocationMetrics
