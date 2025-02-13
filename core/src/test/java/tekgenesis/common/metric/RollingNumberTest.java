
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.metric;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({ "JavaDoc", "MagicNumber" })
public class RollingNumberTest {

    //~ Methods ......................................................................................................................................

    @Test public void testCounterRetrievalRefreshesBuckets() {
        final MockedTime               time    = new MockedTime();
        final RollingNumber<TestEvent> counter = new RollingNumber<>(TestEvent.class, time, 200, 10);

        // Increment
        counter.increment(TestEvent.SUCCESS);
        counter.increment(TestEvent.SUCCESS);
        counter.increment(TestEvent.SUCCESS);
        counter.increment(TestEvent.SUCCESS);
        counter.increment(TestEvent.FAILURE);
        counter.increment(TestEvent.FAILURE);

        // Sleep to get to a new bucket
        time.increment(counter.getBucketSizeInMilliseconds() * 3);

        // We should have 1 bucket since nothing has triggered the update of buckets in the elapsed time
        assertThat(counter.buckets.size()).isEqualTo(1);

        // The total counts
        assertThat(counter.getRollingSum(TestEvent.SUCCESS)).isEqualTo(4);
        assertThat(counter.getRollingSum(TestEvent.FAILURE)).isEqualTo(2);

        // We should have 4 buckets as the counter 'gets' should have triggered the buckets being created to fill in time
        assertThat(counter.buckets.size()).isEqualTo(4);

        // Wait until window passes
        time.increment(counter.timeInMilliseconds);

        // The total counts should all be 0 (and the buckets cleared by the get, not only increment)
        assertThat(counter.getRollingSum(TestEvent.SUCCESS)).isEqualTo(0);
        assertThat(counter.getRollingSum(TestEvent.FAILURE)).isEqualTo(0);

        // Increment
        counter.increment(TestEvent.SUCCESS);

        // The total counts should now include only the last bucket after a reset since the window passed
        assertThat(counter.getRollingSum(TestEvent.SUCCESS)).isEqualTo(1);
        assertThat(counter.getRollingSum(TestEvent.FAILURE)).isEqualTo(0);
    }

    @Test public void testCreatesBuckets() {
        final MockedTime               time    = new MockedTime();
        final RollingNumber<TestEvent> counter = new RollingNumber<>(TestEvent.class, time, 200, 10);

        // Confirm the initial settings
        assertThat(counter.timeInMilliseconds).isEqualTo(200);
        assertThat(counter.numberOfBuckets).isEqualTo(10);
        assertThat(counter.getBucketSizeInMilliseconds()).isEqualTo(20);

        // We start out with 0 buckets in the queue
        assertThat(counter.buckets.size()).isEqualTo(0);

        // Add a success in each interval which should result in all 10 buckets being created with 1 success in each
        for (int i = 0; i < counter.numberOfBuckets; i++) {
            counter.increment(TestEvent.SUCCESS);
            time.increment(counter.getBucketSizeInMilliseconds());
        }

        // Confirm we have all 10 buckets
        assertThat(counter.buckets.size()).isEqualTo(10);

        // Add 1 more and we should still only have 10 buckets since that's the max
        counter.increment(TestEvent.SUCCESS);
        assertThat(counter.buckets.size()).isEqualTo(10);
    }

    @Test public void testCumulativeCounterAfterRolling() {
        final MockedTime               time    = new MockedTime();
        final TestEvent                type    = TestEvent.SUCCESS;
        final RollingNumber<TestEvent> counter = new RollingNumber<>(TestEvent.class, time, 20, 2);

        assertThat(counter.getCumulativeSum(type)).isEqualTo(0);

        // Iterate over 20 buckets on a queue sized for 2
        for (int i = 0; i < 20; i++) {
            // First bucket
            counter.increment(type);
            try {
                time.increment(counter.getBucketSizeInMilliseconds());
            }
            catch (final Exception ignored) {}

            assertThat(counter.getValues(type).length).isEqualTo(2);

            counter.getValueOfLatestBucket(type);
        }

        // Cumulative count should be 20 (for the number of loops above) regardless of buckets rolling
        assertThat(counter.getCumulativeSum(type)).isEqualTo(20);
    }

    @Test public void testCumulativeCounterAfterRollingAndReset() {
        final MockedTime               time    = new MockedTime();
        final TestEvent                type    = TestEvent.SUCCESS;
        final RollingNumber<TestEvent> counter = new RollingNumber<>(TestEvent.class, time, 20, 2);

        assertThat(counter.getCumulativeSum(type)).isEqualTo(0);

        // Iterate over 20 buckets on a queue sized for 2
        for (int i = 0; i < 20; i++) {
            // First bucket
            counter.increment(type);
            try {
                time.increment(counter.getBucketSizeInMilliseconds());
            }
            catch (final Exception ignored) {}

            assertThat(counter.getValues(type).length).isEqualTo(2);

            counter.getValueOfLatestBucket(type);

            if (i == 5 || i == 15)
            // Simulate a reset occurring every once in a while
            counter.reset();
        }

        // Cumulative count should be 20 (for the number of loops above) regardless of buckets rolling and reset
        assertThat(counter.getCumulativeSum(type)).isEqualTo(20);
    }

    @Test public void testCumulativeCounterAfterRollingAndReset2() {
        final MockedTime               time    = new MockedTime();
        final TestEvent                type    = TestEvent.SUCCESS;
        final RollingNumber<TestEvent> counter = new RollingNumber<>(TestEvent.class, time, 20, 2);

        assertThat(counter.getCumulativeSum(type)).isEqualTo(0);

        counter.increment(type);
        counter.increment(type);
        counter.increment(type);

        // iterate over 20 buckets on a queue sized for 2
        for (int i = 0; i < 20; i++) {
            try {
                time.increment(counter.getBucketSizeInMilliseconds());
            }
            catch (final Exception ignored) {}

            if (i == 5 || i == 15)
            // Simulate a reset occurring every once in a while
            counter.reset();
        }

        // No increments during the loop, just some before and after
        counter.increment(type);
        counter.increment(type);

        // Cumulative count should be 5 regardless of buckets rolling
        assertThat(counter.getCumulativeSum(type)).isEqualTo(5);
    }

    @Test public void testCumulativeCounterAfterRollingAndReset3() {
        final MockedTime               time    = new MockedTime();
        final TestEvent                type    = TestEvent.SUCCESS;
        final RollingNumber<TestEvent> counter = new RollingNumber<>(TestEvent.class, time, 20, 2);

        assertThat(counter.getCumulativeSum(type)).isEqualTo(0);

        counter.increment(type);
        counter.increment(type);
        counter.increment(type);

        // Iterate over 20 buckets on a queue sized for 2
        for (int i = 0; i < 20; i++) {
            try {
                time.increment(counter.getBucketSizeInMilliseconds());
            }
            catch (final Exception ignored) {}
        }

        counter.increment(type);
        counter.increment(type);

        // Cumulative count should be 5 regardless of buckets rolling
        assertThat(counter.getCumulativeSum(type)).isEqualTo(5);
    }

    @Test public void testEmptyBucketsFillIn() {
        final MockedTime               time    = new MockedTime();
        final RollingNumber<TestEvent> counter = new RollingNumber<>(TestEvent.class, time, 200, 10);

        // Add 1
        counter.increment(TestEvent.SUCCESS);

        // We should have 1 bucket
        assertThat(counter.buckets.size()).isEqualTo(1);

        // Wait past 3 bucket time periods (the 1st bucket then 2 empty ones)
        time.increment(counter.getBucketSizeInMilliseconds() * 3);

        // Add another
        counter.increment(TestEvent.SUCCESS);

        // We should have 4 (1 + 2 empty + 1 new one) buckets
        assertThat(counter.buckets.size()).isEqualTo(4);
    }

    @Test public void testEmptyLatestValue() {
        final MockedTime               time    = new MockedTime();
        final TestEvent                type    = TestEvent.THREAD_MAX_ACTIVE;
        final RollingNumber<TestEvent> counter = new RollingNumber<>(TestEvent.class, time, 200, 10);
        assertThat(counter.getValueOfLatestBucket(type)).isEqualTo(0);
    }

    @Test public void testEmptyMax() {
        final MockedTime               time    = new MockedTime();
        final TestEvent                type    = TestEvent.THREAD_MAX_ACTIVE;
        final RollingNumber<TestEvent> counter = new RollingNumber<>(TestEvent.class, time, 200, 10);
        assertThat(counter.getRollingMaxValue(type)).isEqualTo(0);
    }

    @Test public void testEmptySum() {
        final MockedTime               time    = new MockedTime();
        final TestEvent                type    = TestEvent.COLLAPSED;
        final RollingNumber<TestEvent> counter = new RollingNumber<>(TestEvent.class, time, 200, 10);
        assertThat(counter.getRollingSum(type)).isEqualTo(0);
    }

    @Test public void testExceptionThrow() {
        testCounterType(TestEvent.EXCEPTION_THROWN);
    }

    @Test public void testIncrementInMultipleBuckets() {
        final MockedTime               time    = new MockedTime();
        final RollingNumber<TestEvent> counter = new RollingNumber<>(TestEvent.class, time, 200, 10);

        // Increment
        counter.increment(TestEvent.SUCCESS);
        counter.increment(TestEvent.SUCCESS);
        counter.increment(TestEvent.SUCCESS);
        counter.increment(TestEvent.SUCCESS);
        counter.increment(TestEvent.FAILURE);
        counter.increment(TestEvent.FAILURE);
        counter.increment(TestEvent.TIMEOUT);
        counter.increment(TestEvent.TIMEOUT);
        counter.increment(TestEvent.SHORT_CIRCUITED);

        // Sleep to get to a new bucket
        time.increment(counter.getBucketSizeInMilliseconds() * 3);

        // Increment
        counter.increment(TestEvent.SUCCESS);
        counter.increment(TestEvent.SUCCESS);
        counter.increment(TestEvent.FAILURE);
        counter.increment(TestEvent.FAILURE);
        counter.increment(TestEvent.FAILURE);
        counter.increment(TestEvent.TIMEOUT);
        counter.increment(TestEvent.SHORT_CIRCUITED);

        // We should have 4 buckets
        assertThat(counter.buckets.size()).isEqualTo(4);

        // The counts of the last bucket
        final RollingNumber.Bucket<TestEvent> pls = counter.buckets.peekLast();
        assertThat(pls).isNotNull();
        // noinspection ConstantConditions
        assertThat(pls.getAdder(TestEvent.SUCCESS).sum()).isEqualTo(2);
        final RollingNumber.Bucket<TestEvent> plf = counter.buckets.peekLast();
        assertThat(plf).isNotNull();
        // noinspection ConstantConditions
        assertThat(plf.getAdder(TestEvent.FAILURE).sum()).isEqualTo(3);
        final RollingNumber.Bucket<TestEvent> plst = counter.buckets.peekLast();
        assertThat(plst).isNotNull();
        // noinspection ConstantConditions
        assertThat(plst.getAdder(TestEvent.TIMEOUT).sum()).isEqualTo(1);
        final RollingNumber.Bucket<TestEvent> plsc = counter.buckets.peekLast();
        assertThat(plsc).isNotNull();
        // noinspection ConstantConditions
        assertThat(plsc.getAdder(TestEvent.SHORT_CIRCUITED).sum()).isEqualTo(1);

        // The total counts
        assertThat(counter.getRollingSum(TestEvent.SUCCESS)).isEqualTo(6);
        assertThat(counter.getRollingSum(TestEvent.FAILURE)).isEqualTo(5);
        assertThat(counter.getRollingSum(TestEvent.TIMEOUT)).isEqualTo(3);
        assertThat(counter.getRollingSum(TestEvent.SHORT_CIRCUITED)).isEqualTo(2);

        // Wait until window passes
        time.increment(counter.timeInMilliseconds);

        // Increment
        counter.increment(TestEvent.SUCCESS);

        // The total counts should now include only the last bucket after a reset since the window passed
        assertThat(counter.getRollingSum(TestEvent.SUCCESS)).isEqualTo(1);
        assertThat(counter.getRollingSum(TestEvent.FAILURE)).isEqualTo(0);
        assertThat(counter.getRollingSum(TestEvent.TIMEOUT)).isEqualTo(0);
    }  // end method testIncrementInMultipleBuckets

    @Test public void testIncrementInSingleBucket() {
        final MockedTime               time    = new MockedTime();
        final RollingNumber<TestEvent> counter = new RollingNumber<>(TestEvent.class, time, 200, 10);

        // Increment
        counter.increment(TestEvent.SUCCESS);
        counter.increment(TestEvent.SUCCESS);
        counter.increment(TestEvent.SUCCESS);
        counter.increment(TestEvent.SUCCESS);
        counter.increment(TestEvent.FAILURE);
        counter.increment(TestEvent.FAILURE);
        counter.increment(TestEvent.TIMEOUT);

        // We should have 1 bucket
        assertThat(counter.buckets.size()).isEqualTo(1);

        // The count should be 4
        final RollingNumber.Bucket<TestEvent> pls = counter.buckets.peekLast();
        assertThat(pls).isNotNull();
        // noinspection ConstantConditions
        assertThat(pls.getAdder(TestEvent.SUCCESS).sum()).isEqualTo(4);
        final RollingNumber.Bucket<TestEvent> plf = counter.buckets.peekLast();
        assertThat(plf).isNotNull();
        // noinspection ConstantConditions
        assertThat(plf.getAdder(TestEvent.FAILURE).sum()).isEqualTo(2);
        final RollingNumber.Bucket<TestEvent> plt = counter.buckets.peekLast();
        assertThat(plt).isNotNull();
        // noinspection ConstantConditions
        assertThat(plt.getAdder(TestEvent.TIMEOUT).sum()).isEqualTo(1);
    }

    @Test public void testMaxValue() {
        final MockedTime time = new MockedTime();
        final TestEvent  type = TestEvent.THREAD_MAX_ACTIVE;

        final RollingNumber<TestEvent> counter = new RollingNumber<>(TestEvent.class, time, 200, 10);

        counter.updateRollingMax(type, 10);

        // Sleep to get to a new bucket
        time.increment(counter.getBucketSizeInMilliseconds());

        counter.updateRollingMax(type, 30);

        // Sleep to get to a new bucket
        time.increment(counter.getBucketSizeInMilliseconds());

        counter.updateRollingMax(type, 40);

        // Sleep to get to a new bucket
        time.increment(counter.getBucketSizeInMilliseconds());

        counter.updateRollingMax(type, 15);

        assertThat(counter.getRollingMaxValue(type)).isEqualTo(40);
    }

    @Test public void testResetBuckets() {
        final MockedTime               time    = new MockedTime();
        final RollingNumber<TestEvent> counter = new RollingNumber<>(TestEvent.class, time, 200, 10);

        // We start out with 0 buckets in the queue
        assertThat(counter.buckets.size()).isEqualTo(0);

        // Add 1
        counter.increment(TestEvent.SUCCESS);

        // Confirm we have 1 bucket
        assertThat(counter.buckets.size()).isEqualTo(1);

        // Confirm we still have 1 bucket
        assertThat(counter.buckets.size()).isEqualTo(1);

        // Add 1
        counter.increment(TestEvent.SUCCESS);

        // We should now have a single bucket with no values in it instead of 2 or more buckets
        assertThat(counter.buckets.size()).isEqualTo(1);
    }

    @Test public void testRolling() {
        final MockedTime               time    = new MockedTime();
        final TestEvent                type    = TestEvent.THREAD_MAX_ACTIVE;
        final RollingNumber<TestEvent> counter = new RollingNumber<>(TestEvent.class, time, 20, 2);

        // Iterate over 20 buckets on a queue sized for 2
        for (int i = 0; i < 20; i++) {
            // First bucket
            counter.getCurrentBucket();
            try {
                time.increment(counter.getBucketSizeInMilliseconds());
            }
            catch (final Exception ignored) {}

            assertThat(counter.getValues(type).length).isEqualTo(2);

            counter.getValueOfLatestBucket(type);
        }
    }

    @Test public void testShortCircuited() {
        testCounterType(TestEvent.SHORT_CIRCUITED);
    }

    @Test public void testTimeout() {
        testCounterType(TestEvent.TIMEOUT);
    }

    @Test public void testUpdateMax1() {
        final MockedTime               time    = new MockedTime();
        final RollingNumber<TestEvent> counter = new RollingNumber<>(TestEvent.class, time, 200, 10);

        // Increment
        counter.updateRollingMax(TestEvent.THREAD_MAX_ACTIVE, 10);

        // We should have 1 bucket
        assertThat(counter.buckets.size()).isEqualTo(1);

        // The count should be 10
        final RollingNumber.Bucket<TestEvent> pltma = counter.buckets.peekLast();
        assertThat(pltma).isNotNull();
        // noinspection ConstantConditions
        assertThat(pltma.getMaxUpdater(TestEvent.THREAD_MAX_ACTIVE).get()).isEqualTo(10);
        assertThat(counter.getRollingMaxValue(TestEvent.THREAD_MAX_ACTIVE)).isEqualTo(10);

        // Sleep to get to a new bucket
        time.increment(counter.getBucketSizeInMilliseconds() * 3);

        // Increment again in latest bucket
        counter.updateRollingMax(TestEvent.THREAD_MAX_ACTIVE, 20);

        // We should have 4 buckets
        assertThat(counter.buckets.size()).isEqualTo(4);

        // The max
        final RollingNumber.Bucket<TestEvent> plmax = counter.buckets.peekLast();
        assertThat(plmax).isNotNull();
        // noinspection ConstantConditions
        assertThat(plmax.getMaxUpdater(TestEvent.THREAD_MAX_ACTIVE).get()).isEqualTo(20);

        // Counts per bucket
        final long[] values = counter.getValues(TestEvent.THREAD_MAX_ACTIVE);
        assertThat(values[0]).isEqualTo(10);  // oldest bucket
        assertThat(values[1]).isEqualTo(0);
        assertThat(values[2]).isEqualTo(0);
        assertThat(values[3]).isEqualTo(20);  // latest bucket
    }

    @Test public void testUpdateMax2() {
        final MockedTime               time    = new MockedTime();
        final RollingNumber<TestEvent> counter = new RollingNumber<>(TestEvent.class, time, 200, 10);

        // Increment
        counter.updateRollingMax(TestEvent.THREAD_MAX_ACTIVE, 10);
        counter.updateRollingMax(TestEvent.THREAD_MAX_ACTIVE, 30);
        counter.updateRollingMax(TestEvent.THREAD_MAX_ACTIVE, 20);

        // We should have 1 bucket
        assertThat(counter.buckets.size()).isEqualTo(1);

        // The count should be 30
        final RollingNumber.Bucket<TestEvent> plmaxc = counter.buckets.peekLast();
        assertThat(plmaxc).isNotNull();
        // noinspection ConstantConditions
        assertThat(plmaxc.getMaxUpdater(TestEvent.THREAD_MAX_ACTIVE).get()).isEqualTo(30);
        assertThat(counter.getRollingMaxValue(TestEvent.THREAD_MAX_ACTIVE)).isEqualTo(30);

        // Sleep to get to a new bucket
        time.increment(counter.getBucketSizeInMilliseconds() * 3);

        counter.updateRollingMax(TestEvent.THREAD_MAX_ACTIVE, 30);
        counter.updateRollingMax(TestEvent.THREAD_MAX_ACTIVE, 30);
        counter.updateRollingMax(TestEvent.THREAD_MAX_ACTIVE, 50);

        // We should have 4 buckets
        assertThat(counter.buckets.size()).isEqualTo(4);

        // The count
        final RollingNumber.Bucket<TestEvent> plmc = counter.buckets.peekLast();
        assertThat(plmc).isNotNull();
        // noinspection ConstantConditions
        assertThat(plmc.getMaxUpdater(TestEvent.THREAD_MAX_ACTIVE).get()).isEqualTo(50);
        assertThat(counter.getValueOfLatestBucket(TestEvent.THREAD_MAX_ACTIVE)).isEqualTo(50);

        // Values per bucket
        final long[] values = counter.getValues(TestEvent.THREAD_MAX_ACTIVE);
        assertThat(values[0]).isEqualTo(30);  // oldest bucket
        assertThat(values[1]).isEqualTo(0);
        assertThat(values[2]).isEqualTo(0);
        assertThat(values[3]).isEqualTo(50);  // latest bucket
    }                                         // end method testUpdateMax2

    private void testCounterType(@NotNull TestEvent type) {
        final MockedTime               time    = new MockedTime();
        final RollingNumber<TestEvent> counter = new RollingNumber<>(TestEvent.class, time, 200, 10);

        // Increment
        counter.increment(type);

        // We should have 1 bucket
        assertThat(counter.buckets.size()).isEqualTo(1);

        // The count should be 1
        final RollingNumber.Bucket<TestEvent> pls = counter.buckets.peekLast();
        assertThat(pls).isNotNull();
        // noinspection ConstantConditions
        assertThat(pls.getAdder(type).sum()).isEqualTo(1);
        assertThat(counter.getRollingSum(type)).isEqualTo(1);

        // Sleep to get to a new bucket
        time.increment(counter.getBucketSizeInMilliseconds() * 3);

        // Increment again in latest bucket
        counter.increment(type);

        // We should have 4 buckets
        assertThat(counter.buckets.size()).isEqualTo(4);

        // The counts of the last bucket
        final RollingNumber.Bucket<TestEvent> pl = counter.buckets.peekLast();
        assertThat(pl).isNotNull();
        // noinspection ConstantConditions
        assertThat(pl.getAdder(type).sum()).isEqualTo(1);

        // The total counts
        assertThat(counter.getRollingSum(type)).isEqualTo(2);
    }

    //~ Enums ........................................................................................................................................

    private enum TestEvent implements RollingNumberEventType {
        SUCCESS, FAILURE, THREAD_MAX_ACTIVE(false), COLLAPSED, EXCEPTION_THROWN, TIMEOUT, SHORT_CIRCUITED;

        private final boolean counter;

        TestEvent() {
            this(true);
        }

        TestEvent(boolean counter) {
            this.counter = counter;
        }

        @Override public boolean isMaximum() {
            return !counter;
        }

        @Override public boolean isCounter() {
            return counter;
        }
    }
}  // end class RollingNumberTest
