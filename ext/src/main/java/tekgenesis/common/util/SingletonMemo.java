
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.util.concurrent.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.core.Tuple;

import static tekgenesis.common.Predefined.cast;

/**
 * A Class that memoize the call to {@link #calculate} for a given time.
 */

public abstract class SingletonMemo<T, M extends SingletonMemo<T, M>> extends Memo<T, M> {

    //~ Instance Fields ..............................................................................................................................

    private final MemoEntry<T>         entry;
    @Nullable private ExecutorService  executor;

    //~ Constructors .................................................................................................................................

    protected SingletonMemo(long duration, TimeUnit unit) {
        this(duration, unit, false);
    }

    protected SingletonMemo(long duration, TimeUnit unit, boolean async) {
        super(duration, unit);
        executor = async ? createExecutor(getClass().getName()) : null;
        entry    = new MemoEntry<T>();
    }

    //~ Methods ......................................................................................................................................

    @Override public void clear() {
        entry.clear();
    }

    /** force calculation. */
    public void force() {
        entry.force();
    }

    /** Get the memoized value. */
    public T get() {
        return entry.get(getDuration(), this::calculate, executor);
    }

    /** Define the Executor Service for the Memo (null will force synchronous execution). */
    public M withExecutor(@Nullable ExecutorService executorService) {
        executor = executorService;
        return cast(this);
    }

    protected abstract T calculate(long lastRefreshTime, @Nullable T oldValue);

    //~ Methods ......................................................................................................................................

    @NotNull private static ThreadPoolExecutor createExecutor(String name) {
        final ThreadPoolExecutor e = new ThreadPoolExecutor(0,
                1,
                1,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(1),
                new MemoCalculatorThreadFactory(name));
        e.allowCoreThreadTimeOut(true);
        return e;
    }

    //~ Inner Classes ................................................................................................................................

    private static class MemoCalculatorThreadFactory implements ThreadFactory {
        private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
        private final String        name;

        MemoCalculatorThreadFactory(String name) {
            this.name = name;
        }

        public Thread newThread(@NotNull Runnable r) {
            final Thread t = defaultFactory.newThread(r);
            t.setName(threadName + "-" + name);
            return t;
        }

        private static final String threadName = "MemoCalculator";
    }
}  // end class SingletonMemo
