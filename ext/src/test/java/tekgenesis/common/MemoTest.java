
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.LongSupplier;

import org.assertj.core.api.Condition;
import org.jetbrains.annotations.Nullable;
import org.junit.Rule;
import org.junit.Test;

import tekgenesis.common.core.DateTime;
import tekgenesis.common.core.Times;
import tekgenesis.common.logging.Logger;
import tekgenesis.common.tools.test.TimeProviderRule;
import tekgenesis.common.util.Memo;
import tekgenesis.common.util.MemoEntry;
import tekgenesis.common.util.MemoMap;
import tekgenesis.common.util.SingletonMemo;

import static java.util.concurrent.TimeUnit.MINUTES;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SingletonMemo Test.
 */
@SuppressWarnings("JavaDoc")
public class MemoTest {

    //~ Instance Fields ..............................................................................................................................

    @Rule public TimeProviderRule timer = new TimeProviderRule(DateTime.current());

    //~ Methods ......................................................................................................................................

    @Test public void anoymousDemo() {
        final MemoEntry<Integer> myMemo = new MemoEntry<Integer>();
        myMemo.clear();

        final BiFunction<Long, Integer, Integer> calculation = (l, v) -> v == null ? 0 : ++v;
        final long                               minutes     = MINUTES.toMillis(1);

        assertThat(myMemo.get(2 * minutes, calculation)).isEqualTo(0);
        assertThat(myMemo.get(2 * minutes, calculation)).isEqualTo(0);
        assertThat(myMemo.get(2 * minutes, calculation)).isEqualTo(0);

        timer.increment(minutes);
        assertThat(myMemo.get(2 * minutes, calculation)).isEqualTo(0);
        timer.increment(10 * minutes);
        assertThat(myMemo.get(minutes, calculation)).isEqualTo(1);
        assertThat(myMemo.get(-1, calculation)).isEqualTo(2);
    }

    @Test public void changeDuration() {
        final MyMemo myMemo = MyMemo.getInstance();
        myMemo.reset();

        assertThat(myMemo.getCount()).isEqualTo(0);
        assertThat(myMemo.getCount()).isEqualTo(0);
        assertThat(myMemo.getCount()).isEqualTo(0);

        assertThat(MyMemo.getInstance().withDuration(5, MINUTES).getCount()).isEqualTo(0);
        timer.increment(Times.MILLIS_MINUTE);
        assertThat(MyMemo.getInstance().withDuration(5, MINUTES).getCount()).isEqualTo(0);
        timer.increment(7 * Times.MILLIS_MINUTE);
        assertThat(myMemo.getCount()).isEqualTo(1);
        assertThat(MyMemo.getInstance().withDuration(-1, MINUTES).getCount()).isEqualTo(2);
        assertThat(myMemo.getCount()).isEqualTo(3);
        assertThat(myMemo.getCount()).isEqualTo(4);
    }

    @Test public void clear() {
        final MyMemo myMemo = MyMemo.getInstance().withDuration(5, MINUTES);
        myMemo.reset();

        assertThat(myMemo.getCount()).isEqualTo(0);
        assertThat(myMemo.getCount()).isEqualTo(0);
        assertThat(myMemo.getCount()).isEqualTo(0);

        timer.increment(5 * Times.MILLIS_MINUTE);
        assertThat(myMemo.getCount()).isEqualTo(1);
        timer.increment(5 * Times.MILLIS_MINUTE);
        assertThat(myMemo.getCount()).isEqualTo(2);

        myMemo.force();
        assertThat(myMemo.getCount()).isEqualTo(3);
    }

    @Test public void defaultMemo() {
        final MyMemo myMemo = MyMemo.getInstance();
        myMemo.reset();

        assertThat(myMemo.getCount()).isEqualTo(0);
        assertThat(myMemo.getCount()).isEqualTo(0);
        assertThat(myMemo.getCount()).isEqualTo(0);

        timer.increment(45 * Times.MILLIS_MINUTE);
        assertThat(myMemo.getCount()).isEqualTo(1);
        timer.increment(35 * Times.MILLIS_MINUTE);
        assertThat(myMemo.getCount()).isEqualTo(2);
    }

    @Test public void keys() {
        final MyMemoMap myMemo1 = MyMemoMap.getInstance().withDuration(5, MINUTES);
        myMemo1.reset();

        assertThat(myMemo1.getCount(1)).isEqualTo(0);
        assertThat(myMemo1.getCount(2)).isEqualTo(2);
        assertThat(myMemo1.getCount(3)).isEqualTo(6);

        timer.increment(MINUTES.toMillis(1));
        assertThat(myMemo1.getCount(1)).isEqualTo(0);
        timer.increment(MINUTES.toMillis(7));
        assertThat(myMemo1.getCount(1)).isEqualTo(3);
        assertThat(myMemo1.getCount(2)).isEqualTo(8);
    }

    @Test public void keysClear() {
        final MyMemoMap myMemo1 = MyMemoMap.getInstance().withDuration(5, MINUTES);
        myMemo1.reset();

        assertThat(myMemo1.getCount(1)).isEqualTo(0);
        assertThat(myMemo1.getCount(2)).isEqualTo(2);
        assertThat(myMemo1.getCount(3)).isEqualTo(6);

        timer.increment(7 * Times.MILLIS_MINUTE);
        assertThat(myMemo1.getCount(1)).isEqualTo(3);
        assertThat(myMemo1.getCount(2)).isEqualTo(8);

        myMemo1.force(1);

        assertThat(myMemo1.getCount(1)).isEqualTo(5);
        assertThat(myMemo1.getCount(2)).isEqualTo(8);

        Memo.forceRecalculation(MyMemoMap.class.getCanonicalName());

        assertThat(myMemo1.getCount(1)).isEqualTo(6);
        assertThat(myMemo1.getCount(2)).isEqualTo(14);

        MemoMap.forceRecalculation(MyMemoMap.class.getCanonicalName(), 2);
        assertThat(myMemo1.getCount(1)).isEqualTo(6);
        assertThat(myMemo1.getCount(2)).isEqualTo(16);
    }

    @Test public void testConcurrency()
        throws InterruptedException
    {
        MyMemo.getInstance().reset();
        MyMemo2.getInstance().reset();
        final LongSupplier oldProvider = DateTime.setTimeSupplier(System::currentTimeMillis);
        try {
            final ExecutorService      executorService = Executors.newFixedThreadPool(5);
            final ArrayList<Future<?>> futures         = new ArrayList<>();
            final String               ok              = "OK";
            for (int i = 0; i < 5; i++)
                futures.add(executorService.submit(new MemoRunnable(), ok));
            executorService.shutdown();
            assertThat(executorService.awaitTermination(1, MINUTES)).isTrue();
            assertThat(futures).are(new Condition<Future<?>>() {
                    @Override public boolean matches(Future<?> value) {
                        try {
                            return value.get() == ok;
                        }
                        catch (final InterruptedException | ExecutionException e) {
                            return false;
                        }
                    }
                });
        }
        finally {
            DateTime.setTimeSupplier(oldProvider);
        }
    }

    @Test public void testConcurrencyWithExpiration()
        throws InterruptedException
    {
        MyMemo.getInstance().reset();
        MyMemo2.getInstance().reset();

        final ExecutorService      executorService = Executors.newFixedThreadPool(5);
        final ArrayList<Future<?>> futures         = new ArrayList<>();
        final String               ok              = "OK";
        for (int i = 0; i < 5; i++)
            futures.add(executorService.submit(new MemoExpirableRunnable(), ok));
        executorService.shutdown();
        assertThat(executorService.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
        assertThat(futures).are(new Condition<Future<?>>() {
                @Override public boolean matches(Future<?> value) {
                    try {
                        return value.get() == ok;
                    }
                    catch (final InterruptedException | ExecutionException e) {
                        return false;
                    }
                }
            });

        assertThat(MyMemo.getInstance().getCount()).isGreaterThan(2000);
        assertThat(MyMemo2.getInstance().getCount()).isGreaterThan(2000);
    }

    @Test public void testSlowCalculation() {
        final SlowMemo memo = SlowMemo.getInstance();
        memo.reset();

        int count = memo.getCount();
        assertThat(count).isEqualTo(0);
        for (int i = 0; i < 100; i++) {
            count = memo.getCount();
            memo.force();
        }
        assertThat(count).isEqualTo(0);
        memo.cancel();
    }

    //~ Static Fields ................................................................................................................................

    private static final Logger logger = Logger.getLogger(MemoTest.class);

    //~ Inner Classes ................................................................................................................................

    private class MemoExpirableRunnable implements Runnable {
        @Override public void run() {
            for (int i = 1; i < 100000; i++) {
                try {
                    assertThat(MyMemo.getInstance().getCount()).isGreaterThanOrEqualTo(0);
                    assertThat(MyMemo2.getInstance().getCount()).isGreaterThanOrEqualTo(0);
                    if (i % 50 == 0) timer.increment(35 * Times.MILLIS_MINUTE);
                }
                catch (final Throwable e) {
                    logger.error(e);
                    throw e;
                }
            }
        }
    }

    private class MemoRunnable implements Runnable {
        @Override public void run() {
            for (int i = 100; i < 100000; i++) {
                try {
                    assertThat(MyMemo.getInstance().getCount()).isEqualTo(0);
                    assertThat(MyMemo2.getInstance().getCount()).isEqualTo(0);
                }
                catch (final Throwable e) {
                    logger.error(e);
                    throw e;
                }
            }
        }
    }
}  // end class MemoTest
