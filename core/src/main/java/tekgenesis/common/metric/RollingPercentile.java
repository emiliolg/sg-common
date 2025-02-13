
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.metric;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.LongSupplier;

import org.jetbrains.annotations.Nullable;

import tekgenesis.common.core.DateTime;

import static tekgenesis.common.metric.RollingNumber.MILLISECONDS_BY_BUCKETS;

/**
 * Add values to a rolling window and retrieve percentile calculations such as median, 90th, 99th,
 * etc. The underlying data structure contains a circular array of buckets that "roll" over time.
 * For example, if the time window is configured to 60 seconds with 12 buckets of 5 seconds each,
 * values will be captured in each 5 second bucket and rotate each 5 seconds. This means that
 * percentile calculations are for the "rolling window" of 55-60 seconds up to 5 seconds ago. Each
 * bucket will contain a circular array of long values and if more than the configured amount (1000
 * values for example) it will wrap around and overwrite values until time passes and a new bucket
 * is allocated. This sampling approach for high volume metrics is done to conserve memory and
 * reduce sorting time when calculating percentiles.
 */
public class RollingPercentile {

    //~ Instance Fields ..............................................................................................................................

    final BucketCircularArray<Bucket> buckets;

    private final Integer bucketDataLength;

    /* This will get flipped each time a new bucket is created. */
    private volatile PercentileSnapshot currentPercentileSnapshot = new PercentileSnapshot(0);
    private final Boolean               enabled;

    private final ReentrantLock newBucketLock      = new ReentrantLock();
    private final Integer       numberOfBuckets;
    private final LongSupplier  time;
    private final Integer       timeInMilliseconds;

    //~ Constructors .................................................................................................................................

    /** Rolling percentile constructor. */
    public RollingPercentile(Integer timeInMilliseconds, Integer numberOfBuckets, Integer bucketDataLength) {
        this(DateTime::currentTimeMillis, timeInMilliseconds, numberOfBuckets, bucketDataLength, true);
    }

    RollingPercentile(LongSupplier time, Integer timeInMilliseconds, Integer numberOfBuckets, Integer bucketDataLength, Boolean enabled) {
        this.time               = time;
        this.timeInMilliseconds = timeInMilliseconds;
        this.numberOfBuckets    = numberOfBuckets;
        this.bucketDataLength   = bucketDataLength;
        this.enabled            = enabled;

        if (timeInMilliseconds % numberOfBuckets != 0) throw new IllegalArgumentException(MILLISECONDS_BY_BUCKETS);

        buckets = new BucketCircularArray<>(numberOfBuckets, Bucket[]::new);
    }

    //~ Methods ......................................................................................................................................

    /** Add value (or values) to current bucket. */
    public void addValue(int... value) {
        if (!enabled) return;  // no-op

        for (final int v : value) {
            try {
                final Bucket currentBucket = getCurrentBucket();
                if (currentBucket != null) currentBucket.data.addValue(v);
            }
            catch (final Exception ignored) {}
        }
    }

    /** This returns the mean (average) of all values in the current snapshot. */
    public int getMean() {
        if (!enabled) return -1;  // no-op

        // force logic to move buckets forward in case other requests aren't making it happen
        getCurrentBucket();
        // fetch the current snapshot
        return getCurrentPercentileSnapshot().getMean();
    }

    /** Compute a percentile from the underlying rolling buckets of values. */
    public int getPercentile(double percentile) {
        if (!enabled) return -1;  // no-op

        // force logic to move buckets forward in case other requests aren't making it happen
        getCurrentBucket();
        // fetch the current snapshot
        return getCurrentPercentileSnapshot().getPercentile(percentile);
    }

    /** Force a reset so that percentiles start being gathered from scratch. */
    void reset() {
        if (!enabled) return;  // no-op

        // clear buckets so we start over again
        buckets.clear();
    }

    private int getBucketSizeInMilliseconds() {
        return timeInMilliseconds / numberOfBuckets;
    }

    @Nullable
    @SuppressWarnings("OverlyNestedMethod")
    private Bucket getCurrentBucket() {
        final long currentTime = time.getAsLong();

        /* a shortcut to try and get the most common result of immediately finding the current bucket */
        /* Retrieve the latest bucket if the given time is BEFORE the end of the bucket window */
        final Bucket current = buckets.peekLast();
        if (current != null && currentTime < current.windowStart + getBucketSizeInMilliseconds()) return current;

        /* If we didn't find the current bucket above, then we have to create one */
        if (newBucketLock.tryLock()) {
            try {
                if (buckets.peekLast() == null) {
                    // The list is empty so create the first bucket
                    final Bucket b = new Bucket(currentTime, bucketDataLength);
                    buckets.addLast(b);
                    return b;
                }
                else {
                    // We go into a loop so that it will create as many buckets as needed to catch up to the current time
                    // as we want the buckets complete even if we don't have transactions during a period of time.
                    for (int i = 0; i < numberOfBuckets; i++) {
                        // We have at least 1 bucket so retrieve it
                        final Bucket lastBucket = buckets.peekLast();
                        if (lastBucket != null) {
                            if (currentTime < lastBucket.windowStart + getBucketSizeInMilliseconds()) return lastBucket;
                            else if (currentTime - (lastBucket.windowStart + getBucketSizeInMilliseconds()) > timeInMilliseconds) {
                                reset();
                                return getCurrentBucket();
                            }
                            else {
                                // We're past the window so we need to create a new bucket
                                final Bucket[] allBuckets = buckets.getArray();
                                buckets.addLast(new Bucket(lastBucket.windowStart + getBucketSizeInMilliseconds(), bucketDataLength));
                                currentPercentileSnapshot = new PercentileSnapshot(allBuckets);
                            }
                        }
                    }
                    // We have finished the for-loop and created all of the buckets, so return the lastBucket now
                    return buckets.peekLast();
                }
            }
            finally {
                newBucketLock.unlock();
            }
        }
        else {
            final Bucket bucket = buckets.peekLast();
            if (bucket != null)
            // We didn't get the lock so just return the latest bucket while another thread creates the next one
            return bucket;
            else {
                // The rare scenario where multiple threads raced to create the very first bucket
                // wait slightly and then use recursion while the other thread finishes creating a bucket
                try {
                    Thread.sleep(5);
                }
                catch (final Exception ignored) {}
                return getCurrentBucket();
            }
        }
    }

    /** This will retrieve the current snapshot. */
    private PercentileSnapshot getCurrentPercentileSnapshot() {
        return currentPercentileSnapshot;
    }

    //~ Inner Classes ................................................................................................................................

    /**
     * Counters for a given 'bucket' of time.
     */
    static class Bucket {
        final PercentileBucketData data;
        final long                 windowStart;

        Bucket(long startTime, int bucketDataLength) {
            windowStart = startTime;
            data        = new PercentileBucketData(bucketDataLength);
        }
    }

    static class PercentileBucketData {
        private final AtomicInteger      index  = new AtomicInteger();
        private final int                length;
        private final AtomicIntegerArray list;

        public PercentileBucketData(int dataLength) {
            length = dataLength;
            list   = new AtomicIntegerArray(dataLength);
        }

        public void addValue(int... latency) {
            for (final int l : latency)

                /* We just wrap around the beginning and over-write if we go past 'dataLength'
                 *as that will effectively cause us to "sample" the most recent data */
                list.set(index.getAndIncrement() % length, l);
        }

        public int length() {
            if (index.get() > list.length()) return list.length();
            else return index.get();
        }
    }

    @SuppressWarnings("MagicNumber")
    static class PercentileSnapshot {
        private final int[] data;
        private final int   length;
        private final int   mean;

        PercentileSnapshot(Bucket[] buckets) {
            int lengthFromBuckets = 0;
            // We need to calculate it dynamically as it could have been changed by properties (rare, but possible)
            // also this way we capture the actual index size rather than the max so size the int[] to only what we need
            for (final Bucket bd : buckets)
                lengthFromBuckets += bd.data.length;
            data = new int[lengthFromBuckets];
            int index = 0;
            int sum   = 0;
            for (final Bucket bd : buckets) {
                final PercentileBucketData pbd = bd.data;
                final int                  l   = pbd.length();
                for (int i = 0; i < l; i++) {
                    final int v = pbd.list.get(i);
                    data[index++] =  v;
                    sum           += v;
                }
            }
            length = index;
            if (length == 0) mean = 0;
            else mean = sum / length;

            Arrays.sort(data, 0, length);
        }

        PercentileSnapshot(int... data) {
            this.data = data;
            length    = data.length;

            int sum = 0;
            for (final int v : data)
                sum += v;
            mean = sum / length;

            Arrays.sort(this.data, 0, length);
        }

        /** Provides percentile computation. */
        public int getPercentile(double percentile) {
            if (length == 0) return 0;
            return computePercentile(percentile);
        }

        /* package for testing */ int getMean() {
            return mean;
        }

        private int computePercentile(double percent) {
            // Some just-in-case edge cases
            if (length <= 0) return 0;
            if (percent <= 0.0) return data[0];
            if (percent >= 100.0) return data[length - 1];

            // Ranking (http://en.wikipedia.org/wiki/Percentile#Alternative_methods)
            final double rank = (percent / 100.0) * length;

            // linear interpolation between closest ranks
            final int iLow  = (int) Math.floor(rank);
            final int iHigh = (int) Math.ceil(rank);
            assert 0 <= iLow && iLow <= rank && rank <= iHigh && iHigh <= length;
            assert (iHigh - iLow) <= 1;
            if (iHigh >= length)
            // Another edge case
            return data[length - 1];
            else if (iLow == iHigh) return data[iLow];
            else
            // Interpolate between the two bounding values
            return (int) (data[iLow] + (rank - iLow) * (data[iHigh] - data[iLow]));
        }
    }  // end class PercentileSnapshot
}
