
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.command;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.logging.Logger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Timer used by {@link AbstractCommand} to timeout async executions.
 */
class CommandTimer {

    //~ Instance Fields ..............................................................................................................................

    private final AtomicReference<ScheduledExecutor> executor = new AtomicReference<>();

    //~ Constructors .................................................................................................................................

    private CommandTimer() {}

    //~ Methods ......................................................................................................................................

    protected void startThreadIfNeeded() {
        // Create and start thread if one doesn't exist
        while (executor.get() == null || !executor.get().isInitialized()) {
            if (executor.compareAndSet(null, new ScheduledExecutor()))
            // Initialize the executor that we 'won' setting
            executor.get().initialize();
        }
    }

    /**
     * Add a {@link TimerListener} that will be executed until it is garbage collected or removed by
     * clearing the returned {@link Reference}.
     */
    Reference<TimerListener> addTimerListener(final TimerListener l) {
        startThreadIfNeeded();

        final int interval = l.getIntervalTimeInMilliseconds();

        return new TimerReference(l, executor.get().getThreadPool().scheduleAtFixedRate(() -> tick(l), interval, interval, MILLISECONDS));
    }

    private void tick(final TimerListener listener) {
        try {
            listener.tick();
        }
        catch (final Exception e) {
            logger.error("Failed while ticking TimerListener", e);
        }
    }

    //~ Methods ......................................................................................................................................

    /** Clears all listeners. */
    static void reset() {
        final ScheduledExecutor ex = INSTANCE.executor.getAndSet(null);
        if (ex != null && ex.getThreadPool() != null) ex.getThreadPool().shutdownNow();
    }

    /** Retrieve the global instance. */
    static CommandTimer getInstance() {
        return INSTANCE;
    }

    //~ Static Fields ................................................................................................................................

    private static final CommandTimer INSTANCE = new CommandTimer();

    private static final Logger logger = Logger.getLogger(CommandTimer.class);

    //~ Inner Interfaces .............................................................................................................................

    interface TimerListener {
        /** The 'tick' is called each time the interval occurs. */
        void tick();

        /** How often this TimerListener should 'tick' defined in milliseconds. */
        int getIntervalTimeInMilliseconds();
    }

    //~ Inner Classes ................................................................................................................................

    static class ScheduledExecutor {
        volatile ScheduledThreadPoolExecutor executor    = null;
        private volatile boolean             initialized;

        /**
         * We want this only done once when created in compareAndSet so use an initialize method.
         */
        public void initialize() {
            executor    = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
                        final AtomicInteger counter = new AtomicInteger();

                        @Override public Thread newThread(@NotNull Runnable r) {
                            final Thread thread = new Thread(r, "CommandTimer-" + counter.incrementAndGet());
                            thread.setDaemon(true);
                            return thread;
                        }
                    });
            initialized = true;
        }

        public boolean isInitialized() {
            return initialized;
        }

        public ScheduledThreadPoolExecutor getThreadPool() {
            return executor;
        }
    }

    private class TimerReference extends SoftReference<TimerListener> {
        private final ScheduledFuture<?> f;

        TimerReference(TimerListener referent, ScheduledFuture<?> f) {
            super(referent);
            this.f = f;
        }

        @Override public void clear() {
            super.clear();
            // Stop this ScheduledFuture from any further executions
            f.cancel(false);
        }
    }
}  // end class CommandTimer
