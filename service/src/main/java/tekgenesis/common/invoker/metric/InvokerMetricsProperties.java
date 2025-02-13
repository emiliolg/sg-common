
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker.metric;

import javax.inject.Named;

import tekgenesis.common.env.Properties;
import tekgenesis.common.invoker.HttpInvoker;

/**
 * Properties for metrics on {@link HttpInvoker invocations}.
 */
@Named("metrics")
@SuppressWarnings({ "MagicNumber", "DuplicateStringLiteralInspection" })
public class InvokerMetricsProperties implements Properties {

    //~ Instance Fields ..............................................................................................................................

    /** Whether circuit breaker should be enabled. */
    public Boolean circuitBreakerEnabled = false;

    /**
     * Percentage (%) of 'marks' that must be failed to trip the circuit (defaults to 50 = if 50%+
     * of requests in 10 seconds are failures or latent when we will trip the circuit).
     */
    public Integer circuitBreakerErrorThresholdPercentage = 50;

    /**
     * Property to allow ignoring errors and therefore never trip 'open' (ie. allow all traffic
     * through)
     */
    public Boolean circuitBreakerForceClosed = false;

    /** Property to allow forcing the circuit open (stopping all requests). */
    public Boolean circuitBreakerForceOpen = false;

    /**
     * Number of requests that must be made within a statisticalWindow before open/close decisions
     * are made using stats (default: 20 requests in 10 seconds must occur before statistics matter)
     */
    public Integer circuitBreakerRequestVolumeThreshold = 20;

    /**
     * Milliseconds after tripping circuit before allowing retry (default 5000 = 5 seconds that we
     * will sleep before trying again after tripping the circuit).
     */
    public Integer circuitBreakerSleepWindowInMilliseconds = 5000;

    /** Number of permits for execution semaphore. */
    public Integer executionIsolationSemaphoreMaxConcurrentRequests = 10;

    /** Whether a command should be executed in a separate thread or not. */
    public String executionIsolationStrategy = "";

    /**
     * Whether an underlying Future/Thread (when runInSeparateThread == true) should be interrupted
     * after a timeout.
     */
    public Boolean executionIsolationThreadInterruptOnTimeout = true;

    /** What thread-pool this command should run in (if running on a separate thread). */
    public String executionIsolationThreadPoolKeyOverride = null;

    /** Whether timeout should be triggered. */
    public Boolean executionTimeoutEnabled = true;

    /** Timeout value in milliseconds for a command (default 1000 = 1 second). */
    public Integer executionTimeoutInMilliseconds = 1000;

    /** Whether fallback should be attempted. */
    public Boolean fallbackEnabled = true;

    /** Number of permits for fallback semaphore. */
    public Integer fallbackIsolationSemaphoreMaxConcurrentRequests = 10;

    /** Time between health snapshots. */
    public Integer healthSnapshotInterval = 1000;

    /** Maximum concurrent connections to stream servlet. */
    public Integer maxStreamConcurrentConnections = 5;

    /**
     * Milliseconds back that will be tracked (60000 = 60 seconds, and default of 6 buckets so each
     * bucket is 10 second).
     */
    public Integer metricsRollingStatisticalWindowInMilliseconds = 60000;

    /**
     * How many values will be stored in each percentile bucket. Default to 100 values max per
     * bucket.
     */
    public Integer percentileBucketSize = 100;

    /** Whether monitoring should be enabled. */
    public Boolean percentileEnabled = true;

    /**
     * Number of buckets percentile window will be divided into (default 6 buckets = 10 seconds each
     * in 60 second window).
     */
    public Integer percentileWindowBuckets = 6;

    /** Milliseconds back that will be tracked in percentile window (default is 1 minute). */
    public Integer percentileWindowTime = 60000;

    /** Whether request caching is enabled. */
    public Boolean requestCacheEnabled = true;

    /** Whether command request logging is enabled. */
    public Boolean requestLogEnabled = true;

    /**
     * Number of buckets in the statisticalWindowTime (20 buckets in a 60 second window so each
     * bucket is 3 second).
     */
    public Integer statisticalWindowBuckets = 20;

    /**
     * Milliseconds back that will be tracked (default is 10000, and default of 10 buckets so each
     * bucket is 1 second).
     */
    public Integer statisticalWindowTime = 60000;
}  // end class InvokerMetricsProperties
