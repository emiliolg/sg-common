
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
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.LongSupplier;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.core.DateTime;

import static tekgenesis.common.Predefined.cast;

/**
 * A number which can be used to track counters (increment) or set values over time. It is "rolling"
 * in the sense that a 'timeInMilliseconds' is given that you want to track (such as 10 seconds) and
 * then that is broken into buckets (defaults to 10) so that the 10 second window doesn't empty out
 * and restart every 10 seconds, but instead every 1 second you have a new bucket added and one
 * dropped so that 9 of the buckets remain and only the newest starts from scratch. This is done so
 * that the statistics are gathered over a rolling 10 second window with data being added/dropped in
 * 1 second intervals (or whatever granularity is defined by the arguments) rather than each 10
 * second window starting at 0 again. Performance-wise this class is optimized for writes, not
 * reads. This is done because it expects far higher write volume (thousands/second) than reads (a
 * few per second). For example, on each read to getSum/getCount it will iterate buckets to sum the
 * data so that on writes we don't need to maintain the overall sum and pay the synchronization cost
 * at each write to ensure the sum is up-to-date when the read can easily iterate each bucket to get
 * the sum when it needs it.
 */
public class RollingNumber<E extends Enum<E> & RollingNumberEventType> {

    //~ Instance Fields ..............................................................................................................................

    final BucketCircularArray<Bucket<E>>    buckets;
    final int                               numberOfBuckets;
    final int                               timeInMilliseconds;
    private final Function<Long, Bucket<E>> bucketCreator;
    private final CumulativeSum<E>          cumulativeSum;
    private final Class<E>                  enumClass;

    private final ReentrantLock lock = new ReentrantLock();  // Used for new bucket creation

    private final LongSupplier time;

    //~ Constructors .................................................................................................................................

    /** Rolling number constructor. */
    public RollingNumber(@NotNull Class<E> enumClass, int timeInMilliseconds, int numberOfBuckets) {
        this(enumClass, DateTime::currentTimeMillis, timeInMilliseconds, numberOfBuckets);
    }

    RollingNumber(@NotNull Class<E> enumClass, @NotNull LongSupplier time, int timeInMilliseconds, int numberOfBuckets) {
        this.enumClass          = enumClass;
        this.time               = time;
        this.timeInMilliseconds = timeInMilliseconds;
        this.numberOfBuckets    = numberOfBuckets;

        if (timeInMilliseconds % numberOfBuckets != 0) throw new IllegalArgumentException(MILLISECONDS_BY_BUCKETS);

        cumulativeSum = new CumulativeSum<>(this.enumClass);

        bucketCreator = value -> new Bucket<>(this.enumClass, value);

        buckets = new BucketCircularArray<>(numberOfBuckets, size -> cast(new Bucket<?>[size]));
    }

    //~ Methods ......................................................................................................................................

    /** Increment the counter in the current bucket by one for the given {@link E} type. */
    public void increment(@NotNull E type) {
        final Bucket<E> b = getCurrentBucket();
        if (b != null) b.getAdder(type).increment();
    }

    /**
     * Force a reset of all rolling counters (clear all buckets) so that statistics start being
     * gathered from scratch.
     */
    public void reset() {
        // If we are resetting, that means the lastBucket won't have a chance to be captured in CumulativeSum, so let's do it here
        final Bucket<E> lastBucket = buckets.peekLast();
        if (lastBucket != null) cumulativeSum.addBucket(lastBucket);

        // Clear buckets so we start over again
        buckets.clear();
    }

    /** Update a value and retain the max value. */
    public void updateRollingMax(@NotNull E type, long value) {
        final Bucket<E> b = getCurrentBucket();
        if (b != null) b.getMaxUpdater(type).accumulate(value);
    }

    /** Get the max value of values in all buckets for the given {@link E} type. */
    public long getRollingMaxValue(@NotNull E type) {
        final long[] values = getValues(type);
        if (values.length == 0) return 0;
        else {
            Arrays.sort(values);
            return values[values.length - 1];
        }
    }  // end method getRollingMaxValue

    /** Get the sum of all buckets in the rolling counter for the given {@link E} type. */
    public long getRollingSum(@NotNull E type) {
        final Bucket<E> last = getCurrentBucket();
        if (last == null) return 0;

        long sum = 0;
        for (final Bucket<E> b : buckets)
            sum += b.getAdder(type).sum();
        return sum;
    }

    /** Add to the counter in the current bucket for the given {@link E} type. */
    void add(@NotNull E type, long value) {
        final Bucket<E> b = getCurrentBucket();
        if (b != null) b.getAdder(type).add(value);
    }

    int getBucketSizeInMilliseconds() {
        return timeInMilliseconds / numberOfBuckets;
    }

    /**
     * Get the cumulative sum of all buckets ever since the JVM started without rolling for the
     * given {@link E} type.
     */
    long getCumulativeSum(@NotNull E type) {
        return getValueOfLatestBucket(type) + cumulativeSum.get(type);
    }

    @Nullable Bucket<E> getCurrentBucket() {
        final long currentTime = time.getAsLong();

        /* Retrieve the latest bucket if the given time is BEFORE the end of the bucket window. */
        final Bucket<E> current = buckets.peekLast();
        if (current != null && currentTime < current.windowStart + getBucketSizeInMilliseconds()) return current;

        /* If we didn't find the current bucket above, then we have to create one */
        if (!lock.tryLock()) return createBucket();

        try {
            if (buckets.peekLast() == null) {
                // The list is empty so create the first bucket
                final Bucket<E> b = bucketCreator.apply(currentTime);
                buckets.addLast(b);
                return b;
            }

            for (int i = 0; i < numberOfBuckets; i++) {
                // We have at least 1 bucket so retrieve it
                final Bucket<E> lastBucket = buckets.peekLast();
                if (lastBucket == null) return null;
                if (currentTime < lastBucket.windowStart + getBucketSizeInMilliseconds()) return lastBucket;

                if (currentTime - (lastBucket.windowStart + getBucketSizeInMilliseconds()) > timeInMilliseconds) {
                    reset();
                    return getCurrentBucket();
                }

                final Bucket<E> b = bucketCreator.apply(lastBucket.windowStart + getBucketSizeInMilliseconds());
                buckets.addLast(b);
                cumulativeSum.addBucket(lastBucket);
            }
            // We have finished the for-loop and created all of the buckets, so return the lastBucket now
            return buckets.peekLast();
        }
        finally {
            lock.unlock();
        }
    }  // end method getCurrentBucket

    /**
     * Get the value of the latest (current) bucket in the rolling counter for the given {@link E}
     * type.
     */
    long getValueOfLatestBucket(@NotNull E type) {
        final Bucket<E> lastBucket = getCurrentBucket();
        if (lastBucket == null) return 0;
        // we have bucket data so we'll return the lastBucket
        return lastBucket.get(type);
    }

    /**
     * Get an array of values for all buckets in the rolling counter for the given {@link E} type.
     */
    long[] getValues(@NotNull E type) {
        final Bucket<E> lastBucket = getCurrentBucket();
        if (lastBucket == null) return ZERO_LONGS;

        // get buckets as an array (which is a copy of the current state at this point in time)
        final Bucket<E>[] bs = buckets.getArray();

        // we have bucket data so we'll return an array of values for all buckets
        final long[] values = new long[bs.length];
        int          i      = 0;
        for (final Bucket<E> b : bs) {
            if (type.isCounter()) values[i++] = b.getAdder(type).sum();
            else if (type.isMaximum()) values[i++] = b.getMaxUpdater(type).get();
        }
        return values;
    }

    @Nullable private Bucket<E> createBucket() {
        final Bucket<E> bucket = buckets.peekLast();
        // We didn't get the lock so just return the latest bucket while another thread creates the next one
        if (bucket != null) return bucket;

        try {
            Thread.sleep(5);
        }
        catch (final Exception ignored) {}
        return getCurrentBucket();
    }

    //~ Static Fields ................................................................................................................................

    static final String MILLISECONDS_BY_BUCKETS = "The time in milliseconds must divide equally into the number of buckets. " +
                                                  "For example 1000/10 is ok, 1000/11 is not.";

    @NonNls private static final String UNKNOWN_TYPE_OF_EVENT         = "Unknown type of event: ";
    @NonNls private static final String TYPE_IS_NOT_A_COUNTER         = "Type is not a Counter: ";
    @NonNls private static final String TYPE_IS_NOT_A_MAXIMUM_UPDATER = "Type is not a Maximum Updater: ";
    private static final long[]         ZERO_LONGS                    = new long[0];

    //~ Inner Classes ................................................................................................................................

    /**
     * Counters for a given 'bucket' of time.
     */
    static class Bucket<E extends Enum<E> & RollingNumberEventType> {
        final long                            windowStart;
        private final Map<E, LongAdder>       adderForCounterType;
        private final Map<E, LongAccumulator> updaterForCounterType;

        Bucket(@NotNull Class<E> enumClass, long startTime) {
            windowStart = startTime;

            adderForCounterType   = new EnumMap<>(enumClass);
            updaterForCounterType = new EnumMap<>(enumClass);

            for (final E event : enumClass.getEnumConstants()) {
                if (event.isCounter()) adderForCounterType.put(event, new LongAdder());
                if (event.isMaximum()) updaterForCounterType.put(event, new LongAccumulator(Long::max, 0));
            }
        }

        long get(@NotNull E type) {
            if (type.isCounter()) return adderForCounterType.get(type).sum();
            if (type.isMaximum()) return updaterForCounterType.get(type).get();
            throw new IllegalStateException(UNKNOWN_TYPE_OF_EVENT + type.name());
        }

        LongAdder getAdder(@NotNull E type) {
            if (!type.isCounter()) throw new IllegalStateException(TYPE_IS_NOT_A_COUNTER + type.name());
            return adderForCounterType.get(type);
        }

        LongAccumulator getMaxUpdater(E type) {
            if (!type.isMaximum()) throw new IllegalStateException(TYPE_IS_NOT_A_MAXIMUM_UPDATER + type.name());
            return updaterForCounterType.get(type);
        }
    }

    /**
     * Cumulative counters (from start of JVM) from each Type.
     */
    private static class CumulativeSum<E extends Enum<E> & RollingNumberEventType> {
        final Map<E, LongAdder>       adderForCounterType;
        final Map<E, LongAccumulator> updaterForCounterType;
        private final E[]             constants;

        private CumulativeSum(@NotNull Class<E> enumClass) {
            constants             = enumClass.getEnumConstants();
            adderForCounterType   = new EnumMap<>(enumClass);
            updaterForCounterType = new EnumMap<>(enumClass);

            for (final E event : constants) {
                if (event.isCounter()) adderForCounterType.put(event, new LongAdder());
                if (event.isMaximum()) updaterForCounterType.put(event, new LongAccumulator(Long::max, 0));
            }
        }

        private void addBucket(@NotNull Bucket<E> lastBucket) {
            for (final E event : constants) {
                if (event.isCounter()) getAdder(event).add(lastBucket.getAdder(event).sum());
                if (event.isMaximum()) getMaxUpdater(event).accumulate(lastBucket.getMaxUpdater(event).get());
            }
        }

        private long get(E type) {
            if (type.isCounter()) return adderForCounterType.get(type).sum();
            if (type.isMaximum()) return updaterForCounterType.get(type).get();
            throw new IllegalStateException(UNKNOWN_TYPE_OF_EVENT + type.name());
        }

        private LongAdder getAdder(E type) {
            if (!type.isCounter()) throw new IllegalStateException(TYPE_IS_NOT_A_COUNTER + type.name());
            return adderForCounterType.get(type);
        }

        private LongAccumulator getMaxUpdater(E type) {
            if (!type.isMaximum()) throw new IllegalStateException(TYPE_IS_NOT_A_MAXIMUM_UPDATER + type.name());
            return updaterForCounterType.get(type);
        }
    }
}
