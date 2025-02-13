
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.metric;

import org.junit.Test;

import tekgenesis.common.metric.RollingPercentile.PercentileSnapshot;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SuppressWarnings({ "MagicNumber", "JavaDoc", "DuplicateStringLiteralInspection" })
public class RollingPercentileTest {

    //~ Methods ......................................................................................................................................

    @Test public void testPercentileAlgorithm_Extremes() {
        //J-
        final PercentileSnapshot p =
            new PercentileSnapshot(2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                    2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 800, 768, 657, 700, 867);
        //J+

        System.out.println("0.01: " + p.getPercentile(0.01));
        System.out.println("10th: " + p.getPercentile(10));
        System.out.println("Median: " + p.getPercentile(50));
        System.out.println("75th: " + p.getPercentile(75));
        System.out.println("90th: " + p.getPercentile(90));
        System.out.println("99th: " + p.getPercentile(99));
        System.out.println("99.5th: " + p.getPercentile(99.5));
        System.out.println("99.99: " + p.getPercentile(99.99));

        assertThat(p.getPercentile(50)).isEqualTo(2);
        assertThat(p.getPercentile(10)).isEqualTo(2);
        assertThat(p.getPercentile(75)).isEqualTo(2);
        if (p.getPercentile(95) < 600) fail("We expect the 90th to be over 600 to show the extremes but got: " + p.getPercentile(90));
        if (p.getPercentile(99) < 600) fail("We expect the 99th to be over 600 to show the extremes but got: " + p.getPercentile(99));
    }  // end method testPercentileAlgorithm_Extremes

    @Test public void testPercentileAlgorithm_HighPercentile() {
        final PercentileSnapshot p = getPercentileForValues(1, 2, 3);
        assertThat(p.getPercentile(50)).isEqualTo(2);
        assertThat(p.getPercentile(75)).isEqualTo(3);
    }

    @Test public void testPercentileAlgorithm_LowPercentile() {
        final PercentileSnapshot p = getPercentileForValues(1, 2);
        assertThat(p.getPercentile(25)).isEqualTo(1);
        assertThat(p.getPercentile(75)).isEqualTo(2);
    }

    @Test public void testPercentileAlgorithm_Median1() {
        final PercentileSnapshot list = new PercentileSnapshot(100, 100, 100, 100, 200, 200, 200, 300, 300, 300, 300);
        assertThat(list.getPercentile(50)).isEqualTo(200);
    }

    @Test public void testPercentileAlgorithm_Median2() {
        final PercentileSnapshot list = new PercentileSnapshot(100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 500);
        assertThat(list.getPercentile(50)).isEqualTo(100);
    }

    @Test public void testPercentileAlgorithm_Median3() {
        final PercentileSnapshot list = new PercentileSnapshot(50, 75, 100, 125, 160, 170, 180, 200, 210, 300, 500);
        assertThat(list.getPercentile(50)).isEqualTo(175);
    }

    @Test public void testPercentileAlgorithm_Median4() {
        // Unsorted so it is expected to sort it for us
        final PercentileSnapshot list = new PercentileSnapshot(300, 75, 125, 500, 100, 160, 180, 200, 210, 50, 170);
        assertThat(list.getPercentile(50)).isEqualTo(175);
    }

    @Test public void testPercentileAlgorithm_NISTExample() {
        final PercentileSnapshot p = getPercentileForValues(951772,
                951567,
                951937,
                951959,
                951442,
                950610,
                951591,
                951195,
                951772,
                950925,
                951990,
                951682);
        assertThat(p.getPercentile(90)).isEqualTo(951983);
        assertThat(p.getPercentile(100)).isEqualTo(951990);
    }

    @Test public void testPercentileAlgorithm_Percentiles() {
        final PercentileSnapshot p     = getPercentileForValues(10, 30, 20, 40);
        final double             delta = 1.0e-5;
        assertThat((double) p.getPercentile(30)).isBetween(22 - delta, 22 + delta);
        assertThat((double) p.getPercentile(25)).isBetween(20 - delta, 20 + delta);
        assertThat((double) p.getPercentile(75)).isBetween(40 - delta, 40 + delta);
        assertThat((double) p.getPercentile(50)).isBetween(30 - delta, 30 + delta);

        // Invalid percentiles
        assertThat(p.getPercentile(-1)).isEqualTo(10);
        assertThat(p.getPercentile(101)).isEqualTo(40);
    }

    @Test public void testRolling() {
        final MockedTime        time = new MockedTime();
        final RollingPercentile p    = new RollingPercentile(time, timeInMilliseconds, numberOfBuckets, bucketDataLength, enabled);
        p.addValue(1000);
        p.addValue(1000);
        p.addValue(1000);
        p.addValue(2000);

        assertEquals(1, p.buckets.size());

        // No bucket turnover yet so percentile not yet generated
        assertEquals(0, p.getPercentile(50));

        time.increment(6000);

        // Still only 1 bucket until we touch it again
        assertEquals(1, p.buckets.size());

        // A bucket has been created so we have a new percentile
        assertEquals(1000, p.getPercentile(50));

        // Now 2 buckets since getting a percentile causes bucket retrieval
        assertEquals(2, p.buckets.size());

        p.addValue(1000);
        p.addValue(500);

        // Should still be 2 buckets
        assertEquals(2, p.buckets.size());

        p.addValue(200);
        p.addValue(200);
        p.addValue(1600);
        p.addValue(200);
        p.addValue(1600);
        p.addValue(1600);

        // We haven't progressed to a new bucket so the percentile should be the same and ignore the most recent bucket
        assertEquals(1000, p.getPercentile(50));

        // increment to another bucket so we include all of the above in the PercentileSnapshot
        time.increment(6000);

        // The rolling version should have the same data as creating a snapshot like this
        final PercentileSnapshot ps = new PercentileSnapshot(1000, 1000, 1000, 2000, 1000, 500, 200, 200, 1600, 200, 1600, 1600);

        assertEquals(ps.getPercentile(0.15), p.getPercentile(0.15));
        assertEquals(ps.getPercentile(0.50), p.getPercentile(0.50));
        assertEquals(ps.getPercentile(0.90), p.getPercentile(0.90));
        assertEquals(ps.getPercentile(0.995), p.getPercentile(0.995));

        System.out.println("100th: " + ps.getPercentile(100) + "  " + p.getPercentile(100));
        System.out.println("99.5th: " + ps.getPercentile(99.5) + "  " + p.getPercentile(99.5));
        System.out.println("99th: " + ps.getPercentile(99) + "  " + p.getPercentile(99));
        System.out.println("90th: " + ps.getPercentile(90) + "  " + p.getPercentile(90));
        System.out.println("50th: " + ps.getPercentile(50) + "  " + p.getPercentile(50));
        System.out.println("10th: " + ps.getPercentile(10) + "  " + p.getPercentile(10));

        // Mean = 1000+1000+1000+2000+1000+500+200+200+1600+200+1600+1600/12
        assertEquals(991, ps.getMean());
    }  // end method testRolling

    @Test public void testValueIsZeroAfterRollingWindowPassesAndNoTraffic() {
        final MockedTime        time = new MockedTime();
        final RollingPercentile p    = new RollingPercentile(time, timeInMilliseconds, numberOfBuckets, bucketDataLength, enabled);
        p.addValue(1000);
        p.addValue(1000);
        p.addValue(1000);
        p.addValue(2000);
        p.addValue(4000);

        assertEquals(1, p.buckets.size());

        // No bucket turnover yet so percentile not yet generated
        assertEquals(0, p.getPercentile(50));

        time.increment(6000);

        // Still only 1 bucket until we touch it again
        assertEquals(1, p.buckets.size());

        // A bucket has been created so we have a new percentile
        assertEquals(1500, p.getPercentile(50));

        // Let 1 minute pass
        time.increment(60000);

        // No data in a minute should mean all buckets are empty (or reset) so we should not have any percentiles
        assertEquals(0, p.getPercentile(50));
    }

    private PercentileSnapshot getPercentileForValues(int... values) {
        return new PercentileSnapshot(values);
    }

    //~ Static Fields ................................................................................................................................

    private static final Integer timeInMilliseconds = 60000;
    private static final Integer numberOfBuckets    = 12;  // 12 buckets at 5000ms each
    private static final Integer bucketDataLength   = 1000;
    private static final Boolean enabled            = true;
}                                                          // end class RollingPercentileTest
