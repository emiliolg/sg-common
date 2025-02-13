
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.tools.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import static org.assertj.core.api.Assertions.fail;

/**
 * A Rule to mute specified loggers.
 */
public class ConcurrentRule implements TestRule {

    //~ Instance Fields ..............................................................................................................................

    @Nullable private CyclicBarrier    allDone         = null;
    @Nullable private CyclicBarrier    allReady        = null;
    private final List<AssertionError> assertionErrors = Collections.synchronizedList(new ArrayList<>());

    private final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());
    private final List<Future<?>> futures    = new ArrayList<>();

    private final List<Consumer<Integer>> runnableList = new ArrayList<>();
    private final AtomicBoolean           startTimeout = new AtomicBoolean();
    private int                           startWait    = 10;
    @Nullable private ExecutorService     threadPool   = null;
    private int                           waitTime     = DEFAULT_WAIT_TIME;

    //~ Methods ......................................................................................................................................

    /** Add a runnable. */
    public int add(Consumer<Integer> r) {
        runnableList.add(r);
        return runnableList.size() - 1;
    }

    @Override public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override public void evaluate()
                throws Throwable
            {
                try {
                    base.evaluate();
                    runAndWait();
                }
                finally {
                    if (threadPool != null) threadPool.shutdownNow();
                }
            }
        };
    }

    /** Cancel the execution of the nth runnable. */
    public void cancel(int n) {
        futures.get(n).cancel(true);
    }

    /** Create a Barrier see: {@link CyclicBarrier } */
    public Barrier createBarrier() {
        return new Barrier();
    }

    /** Creates an exchanger see: {@link java.util.concurrent.Exchanger }. */
    public <T> Exchanger<T> createExchanger() {
        return new Exchanger<>();
    }

    /** Wait for the specified number of milliseconds. */
    public synchronized void doWait(int ms) {
        try {
            wait(ms);
        }
        catch (final InterruptedException ignore) {}
    }

    /** Set the staring wait time per thread. */
    public void setStartingWaitByThread(int milliseconds) {
        startWait = milliseconds;
    }
    /** Set the total wait time after execution. */
    public void setWaitTime(int milliseconds) {
        waitTime = milliseconds;
    }

    private void markEnd() {
        try {
            if (allDone != null) allDone.await();
        }
        catch (InterruptedException | BrokenBarrierException ignore) {}
    }

    private void runAndWait()
        throws InterruptedException
    {
        final int n = runnableList.size();
        if (n == 0) return;
        threadPool = Executors.newFixedThreadPool(n);

        allReady = new CyclicBarrier(n);
        allDone  = new CyclicBarrier(n + 1);

        futures.clear();
        for (final Consumer<Integer> c : runnableList)
            futures.add(threadPool.submit(runConsumer(c)));

        final boolean timeout = waitForExecution();
        if (startTimeout.get()) fail("Timeout initializing threads! Perform long lasting initializations before starting them");
        if (!assertionErrors.isEmpty()) throw assertionErrors.get(0);
        if (!exceptions.isEmpty()) fail("Test failed with exception(s)", exceptions.get(0));
        if (timeout) fail("Timeout! waiting for finalization");
    }

    private Runnable runConsumer(Consumer<Integer> r) {
        return () -> {
                   if (allDone == null) return;
                   try {
                       final int pos = waitForStartUp();
                       if (pos >= 0) {
                           r.accept(pos);
                           markEnd();
                           return;
                       }
                   }
                   catch (final AssertionError e) {
                       assertionErrors.add(e);
                   }
                   catch (final Throwable e) {
                       exceptions.add(e);
                   }
                   allDone.reset();
               };
    }  // end method runConsumer

    private boolean waitForExecution()
        throws InterruptedException
    {
        boolean timeout = false;
        try {
            if (allDone != null) allDone.await(waitTime, MILLISECONDS);
        }
        catch (BrokenBarrierException | TimeoutException ignore) {
            timeout = true;
        }
        return timeout;
    }

    private int waitForStartUp() {
        try {
            if (allReady != null) return allReady.getParties() - allReady.await(startWait * runnableList.size(), MILLISECONDS);
        }
        catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
            startTimeout.set(true);
        }
        return -1;
    }

    //~ Static Fields ................................................................................................................................

    private static final int DEFAULT_WAIT_TIME = 5_000;

    //~ Inner Classes ................................................................................................................................

    /**
     * A wrapper of class {@link CyclicBarrier}.
     */
    public class Barrier {
        private CyclicBarrier b = null;

        /**
         * Waits until all the parties have invoked {@code await} on this barrier. Returns the order
         * of invocation of the await method or -1 if the barrier was broken
         */
        public int await() {
            return doWait(0L);
        }
        /**
         * Waits until all the parties have invoked {@code await} on this barrier. Returns the order
         * of invocation of the await method or -1 if the barrier was broken
         */
        public int await(long timeoutMillis) {
            return doWait(timeoutMillis);
        }

        private int doWait(long l) {
            if (b == null) b = new CyclicBarrier(runnableList.size());
            try {
                final int await = l == 0 ? b.await() : b.await(l, TimeUnit.MILLISECONDS);
                return b.getParties() - await;
            }
            catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
                return -1;
            }
        }
    }

    /**
     * A wrapper of class {@link java.util.concurrent.Exchanger}.
     */
    public static class Exchanger<T> {
        private final java.util.concurrent.Exchanger<T> e = new java.util.concurrent.Exchanger<>();

        /**
         * Waits for another thread to arrive at this exchange point, and then transfers the given
         * object to it, receiving its object in return.
         */
        public T exchange(T n) {
            try {
                return e.exchange(n);
            }
            catch (final InterruptedException e1) {
                throw new AssertionError("Interrupted Exception");
            }
        }
    }
}  // end class ConcurrentRule
