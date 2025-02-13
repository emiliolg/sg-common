
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
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import rx.Observable;
import rx.Subscriber;
import rx.subjects.ReplaySubject;
import rx.subscriptions.CompositeSubscription;

import tekgenesis.common.command.CommandThreadPool.Factory;
import tekgenesis.common.command.CommandTimer.TimerListener;
import tekgenesis.common.command.exception.CommandInvocationException;
import tekgenesis.common.command.exception.CommandStackCauseException;
import tekgenesis.common.command.exception.CommandTimeoutException;
import tekgenesis.common.core.Option;
import tekgenesis.common.logging.Logger;
import tekgenesis.common.service.exception.MessageConversionException;

import static tekgenesis.common.Predefined.isEmpty;
import static tekgenesis.common.command.AbstractCommand.TimedOutStatus.NOT_EXECUTED;
import static tekgenesis.common.command.AbstractCommand.TimedOutStatus.TIMED_OUT;
import static tekgenesis.common.core.Option.empty;
import static tekgenesis.common.core.Option.some;

/**
 * Abstract command implementation.
 */
public abstract class AbstractCommand<T> implements Command<T> {

    //~ Instance Fields ..............................................................................................................................

    protected final CommandThreadPool pool;

    protected AtomicBoolean started = new AtomicBoolean();

    /* If this command executed and timed-out */
    protected final AtomicReference<TimedOutStatus> timeout = new AtomicReference<>(NOT_EXECUTED);

    protected final AtomicReference<Reference<TimerListener>> timer = new AtomicReference<>();

    /** Specifies fallback from throwable to command object. */
    private Option<Function<Throwable, T>> fallback;

    //~ Constructors .................................................................................................................................

    /** Allow constructing an {@link AbstractCommand} with default CommandThreadPool. */
    protected AbstractCommand() {
        this(empty());
    }

    /**
     * Allow constructing an {@link AbstractCommand} with injection of CommandThreadPool
     * functionality.
     */
    private AbstractCommand(@NotNull Option<CommandThreadPool> pool) {
        // ThreadPool initialization
        this.pool = pool.isPresent() ? pool.get() : Factory.getInstance(getThreadPoolKey());

        // Fallback function to recover from exception
        fallback = empty();
    }

    //~ Methods ......................................................................................................................................

    @Override public T execute() {
        try {
            return queue().get();
        }
        catch (final Exception e) {
            throw causify(decompose(e), new CommandStackCauseException());
        }
    }

    /**
     * Used for asynchronous execution of command with a callback by subscribing to the
     * {@link Observable}. This eagerly starts execution of the command the same as {@link #queue()}
     * and {@link #execute()}. A lazy {@link Observable} can be obtained from
     * {@link #toObservable()}.
     *
     * @return  {@code Observable<T>} that executes and calls back with the result of the command
     *          execution or a fallback if the command execution fails for any reason.
     */
    @Override public Observable<T> observe() {
        // Use a ReplaySubject to buffer the eagerly subscribed-to Observable
        final ReplaySubject<T> subject = ReplaySubject.create();
        // Eagerly kick off subscription
        toObservable().subscribe(subject);
        // Return the subject that can be subscribed to later while the execution has already started
        return subject;
    }

    @Override public Future<T> queue() {
        return toObservable().toBlocking().toFuture();
    }

    /**
     * Determines whether to throw given exception, its cause or a new
     * {@link CommandInvocationException}.
     */
    protected RuntimeException decompose(@NotNull Exception e) {
        if (decomposes(e)) return (RuntimeException) e;
        final Throwable cause = e.getCause();
        if (decomposes(cause)) return (RuntimeException) cause;
        logger.debug("Command failed while executing", e);
        return new CommandInvocationException(getClass(), e);
    }

    /**
     * Implement this method with code to be executed when {@link #execute()} or {@link #queue()}
     * are invoked.
     *
     * @return  T response type
     */
    protected abstract T run();

    protected void withFallback(@NotNull Function<Throwable, T> f) {
        fallback = some(f);
    }

    /**
     * Decorate functionality around the run() Observable with error handling, thread pool, and
     * timeout.
     */
    protected Observable<T> getDecoratedObservable() {
        Observable<T> run = Observable.create(s -> {
                if (timeout.get() == TIMED_OUT) s.onError(new RuntimeException("Timed out before executing run()"));
                else getExecutionObservable().unsafeSubscribe(s);
            });

        run = run.subscribeOn(pool.getScheduler());

        if (getTimeoutIntervalTimeInMilliseconds().isPresent()) run = run.lift(new ObservableTimeoutOperator<>(this));

        run = run.onErrorResumeNext(this::getFallbackOrThrowException);

        return run;
    }

    protected String getThreadPoolKey() {
        final String key = getClass().getCanonicalName();
        if (isEmpty(key))
            throw new IllegalStateException(
                "Command factory key cannot be empty! " +
                "If command is implemented via anonymous class, override getCommandKey method!");
        return key;
    }

    protected Option<Integer> getTimeoutIntervalTimeInMilliseconds() {
        return empty();
    }

    /** Use command stack exception as cause on given exception root cause. */
    private RuntimeException causify(@NotNull RuntimeException exception, @NotNull CommandStackCauseException stack) {
        Throwable cause = exception;
        while (cause.getCause() != null)
            cause = cause.getCause();
        cause.initCause(stack);
        return exception;
    }

    private boolean decomposes(Throwable cause) {
        return cause instanceof IllegalStateException || cause instanceof CommandInvocationException || cause instanceof CommandTimeoutException ||
               cause instanceof MessageConversionException;
    }

    /**
     * Used for asynchronous execution of command with a callback by subscribing to the
     * {@link Observable}. This lazily starts execution of the command only once the
     * {@link Observable} is subscribed to. An eager {@link Observable} can be obtained from
     * {@link #observe()}
     *
     * @return  {@code Observable<R>} that executes and calls back with the result of the command
     *          execution or a fallback if the command execution fails for any reason.
     */
    private Observable<T> toObservable() {
        // This is a stateful object so can only be used once
        if (!started.compareAndSet(false, true))
            throw new IllegalStateException("This instance can only be executed once. Please instantiate a new instance.");

        // Create an Observable that will lazily execute when subscribed to
        Observable<T> observable = Observable.create(observer -> {
                try {
                    getDecoratedObservable().unsafeSubscribe(observer);
                }
                catch (final RuntimeException e) {
                    observer.onError(e);
                }
            });

        // Error handling at very end (this means fallback didn't exist or failed)
        observable = observable.onErrorResumeNext(Observable::error);

        // Any final cleanup needed, such as timeout reference clear
        observable = observable.doOnTerminate(() -> {
                final Reference<TimerListener> listener = timer.get();
                if (listener != null) listener.clear();
            });

        return observable;
    }

    /** Return error fallback observable applying given function to throwable. */
    private Observable<T> getErrorFallbackObservable(@NotNull final Function<Throwable, T> f, @NotNull final Throwable t) {
        return Observable.create(s -> {
            try {
                s.onNext(f.apply(t));
                s.onCompleted();
            }
            catch (final Throwable e) {
                s.onError(e);
            }
        });
    }

    private Observable<T> getExecutionObservable() {
        return Observable.create(s -> {
            try {
                s.onNext(run());
                s.onCompleted();
            }
            catch (final Throwable e) {
                s.onError(e);
            }
        });
    }

    private Observable<T> getFallbackOrThrowException(@NotNull Throwable t) {
        if (fallback.isPresent()) return getErrorFallbackObservable(fallback.get(), t);
        return Observable.error(t);
    }

    //~ Static Fields ................................................................................................................................

    private static final Logger logger = Logger.getLogger(AbstractCommand.class);

    //~ Enums ........................................................................................................................................

    enum TimedOutStatus { NOT_EXECUTED, COMPLETED, TIMED_OUT }

    //~ Inner Classes ................................................................................................................................

    protected static class ObservableCommand<R> extends Observable<R> {
        private final AbstractCommand<R> command;

        ObservableCommand(OnSubscribe<R> func, final AbstractCommand<R> command) {
            super(func);
            this.command = command;
        }

        ObservableCommand(final Observable<R> originalObservable, final AbstractCommand<R> command) {
            super(originalObservable::unsafeSubscribe);
            this.command = command;
        }

        public AbstractCommand<R> getCommand() {
            return command;
        }
    }

    private static class ObservableTimeoutOperator<R> implements Observable.Operator<R, R> {
        private final AbstractCommand<R> command;

        private ObservableTimeoutOperator(final AbstractCommand<R> command) {
            this.command = command;
        }

        @Override public Subscriber<? super R> call(final Subscriber<? super R> child) {
            final CompositeSubscription s = new CompositeSubscription();

            // If the child un-subscribes we un-subscribe our parent as well
            child.add(s);

            /* Define the action to perform on timeout outside of the TimerListener to it can capture the context
             * of the calling thread which doesn't exist on the Timer thread. */
            final Runnable exception = () -> child.onError(new CommandTimeoutException());

            final TimerListener listener = new TimerListener() {
                    @Override public void tick() {
                        if (command.timeout.compareAndSet(NOT_EXECUTED, TIMED_OUT)) {
                            // Shut down the original request
                            s.unsubscribe();
                            exception.run();
                        }
                    }

                    @Override public int getIntervalTimeInMilliseconds() {
                        return command.getTimeoutIntervalTimeInMilliseconds().get();
                    }
                };

            final Reference<TimerListener> tl = CommandTimer.getInstance().addTimerListener(listener);

            // set externally so execute/queue can see this
            command.timer.set(tl);

            /* If this subscriber receives values it means the parent succeeded/completed */
            final Subscriber<R> parent = new Subscriber<R>() {
                    @Override public void onCompleted() {
                        if (isNotTimedOut()) {
                            // stop timer and pass notification through
                            tl.clear();
                            child.onCompleted();
                        }
                    }

                    @Override public void onError(Throwable e) {
                        if (isNotTimedOut()) {
                            tl.clear();
                            child.onError(e);
                        }
                    }

                    @Override public void onNext(R v) {
                        if (isNotTimedOut()) child.onNext(v);
                    }

                    private boolean isNotTimedOut() {
                        return command.timeout.get() == TimedOutStatus.COMPLETED ||
                               command.timeout.compareAndSet(NOT_EXECUTED, TimedOutStatus.COMPLETED);
                    }
                };

            // If s is un-subscribed we want to un-subscribe the parent
            s.add(parent);

            return parent;
        }  // end method call
    }  // end class ObservableTimeoutOperator
}  // end class AbstractCommand
