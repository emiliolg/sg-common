
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.command;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.NotNull;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.schedulers.ScheduledAction;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

import tekgenesis.common.env.context.Context;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import static tekgenesis.common.core.Times.MILLIS_SECOND;

/**
 * ThreadPool used to executed {@link AbstractCommand#run()} on separate threads.
 */
interface CommandThreadPool {

    //~ Methods ......................................................................................................................................

    /** Implementation of {@link ThreadPoolExecutor}. */
    ThreadPoolExecutor getExecutor();

    /** Implementation of {@link Scheduler}. */
    Scheduler getScheduler();

    //~ Inner Classes ................................................................................................................................

    class DefaultCommandThreadPool implements CommandThreadPool {
        private final Scheduler scheduler;

        private final ThreadPoolExecutor threadPool;

        DefaultCommandThreadPool(final String commandKey) {
            final CommandProps            props   = Context.getEnvironment().get(commandKey, CommandProps.class);
            final BlockingQueue<Runnable> queue   = props.poolThreadQueueSize > 0 ? new LinkedBlockingQueue<>(props.poolThreadQueueSize)
                                                                                  : new SynchronousQueue<>();
            final int                     threads = props.poolTotalThreads != 0
                                                    ? props.poolTotalThreads
                                                    : Runtime.getRuntime().availableProcessors() * props.poolThreadsPerCore;
            // We always want maxSize the same as coreSize, we are not using a dynamically resizing pool
            // KeepAliveTime doesn't really matter since we're not resizing
            threadPool = new ThreadPoolExecutor(threads, threads, MILLIS_SECOND, MILLISECONDS, queue, createThreadFactory(commandKey));
            scheduler  = new ThreadPoolScheduler(this);
        }

        @Override public ThreadPoolExecutor getExecutor() {
            return threadPool;
        }

        @Override public Scheduler getScheduler() {
            return scheduler;
        }

        private ThreadFactory createThreadFactory(final String threadPoolKey) {
            return new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(0);

                @Override public Thread newThread(@NotNull Runnable r) {
                    final Thread t = new Thread(r, "command-" + threadPoolKey + "-" + threadNumber.incrementAndGet());
                    t.setDaemon(true);
                    return t;
                }
            };
        }
    }

    class Factory {
        private Factory() {}

        /** Initiate the shutdown of all {@link CommandThreadPool} instances. */
        static synchronized void shutdown() {
            for (final CommandThreadPool pool : threadPools.values())
                pool.getExecutor().shutdown();
            threadPools.clear();
        }

        /**
         * Initiate the shutdown of all {@link CommandThreadPool} instances and wait up to the given
         * time on each pool to complete.
         *
         * @return  {@code true} if this executor terminated and {@code false} if the timeout
         *          elapsed before termination
         */
        static synchronized boolean shutdown(long timeout, TimeUnit unit) {
            for (final CommandThreadPool pool : threadPools.values())
                pool.getExecutor().shutdown();

            boolean result = true;
            for (final CommandThreadPool pool : threadPools.values()) {
                try {
                    result &= pool.getExecutor().awaitTermination(timeout, unit);
                }
                catch (final InterruptedException e) {
                    throw new RuntimeException(
                        "Interrupted while waiting for thread-pools to terminate. Pools may not be correctly shutdown or cleared.",
                        e);
                }
            }
            threadPools.clear();
            return result;
        }

        /** Get the {@link CommandThreadPool} instance for a given key. */
        static CommandThreadPool getInstance(String commandKey) {
            final CommandThreadPool cached = threadPools.get(commandKey);
            if (cached != null) return cached;

            synchronized (CommandThreadPool.class) {
                if (!threadPools.containsKey(commandKey)) threadPools.put(commandKey, new DefaultCommandThreadPool(commandKey));
            }

            return threadPools.get(commandKey);
        }

        static final ConcurrentHashMap<String, CommandThreadPool> threadPools = new ConcurrentHashMap<>();
    }  // end class Factory

    class ThreadPoolScheduler extends Scheduler {
        private final CommandThreadPool threadPool;

        ThreadPoolScheduler(CommandThreadPool threadPool) {
            this.threadPool = threadPool;
        }

        @Override public Worker createWorker() {
            return new ThreadPoolWorker(threadPool);
        }
    }

    /**
     * Purely for scheduling work on a thread-pool.
     */
    class ThreadPoolWorker extends Scheduler.Worker {
        private final CompositeSubscription subscription = new CompositeSubscription();

        private final CommandThreadPool threadPool;

        ThreadPoolWorker(CommandThreadPool threadPool) {
            this.threadPool = threadPool;
        }

        @Override public Subscription schedule(final Action0 action) {
            if (subscription.isUnsubscribed()) return Subscriptions.unsubscribed();

            // This is internal RxJava API but it is too useful.
            final ScheduledAction scheduled = new ScheduledAction(action);

            subscription.add(scheduled);
            scheduled.addParent(subscription);

            final Future<?> f = threadPool.getExecutor().submit(scheduled);
            scheduled.add(f);

            return scheduled;
        }

        @Override public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            throw new UnsupportedOperationException("Delayed scheduling not supported");
        }

        @Override public void unsubscribe() {
            subscription.unsubscribe();
        }

        @Override public boolean isUnsubscribed() {
            return subscription.isUnsubscribed();
        }
    }
}  // end interface CommandThreadPool
