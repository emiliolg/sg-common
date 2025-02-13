
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.command;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import org.jetbrains.annotations.NonNls;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import tekgenesis.common.command.CommandThreadPool.Factory;
import tekgenesis.common.command.exception.CommandInvocationException;
import tekgenesis.common.command.exception.CommandStackCauseException;
import tekgenesis.common.env.context.Context;
import tekgenesis.common.invoker.HttpInvoker;
import tekgenesis.common.invoker.HttpInvokers;
import tekgenesis.common.invoker.InvokerCommand;
import tekgenesis.common.tools.test.server.ConnectionTimeoutRule;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.service.Method.HEAD;

/**
 * Command tests.
 */
@SuppressWarnings({ "JavaDoc", "MagicNumber", "DuplicateStringLiteralInspection" })
public class CommandThreadPoolTest {

    //~ Methods ......................................................................................................................................

    @Test public void testCustomPropertiesSpecification() {
        final CommandThreadPool pool = Factory.getInstance(THREAD_POOL_COMMAND_KEY);
        assertThat(pool.getExecutor().getCorePoolSize()).isEqualTo(1);
        assertThat(pool.getExecutor().getMaximumPoolSize()).isEqualTo(1);
        assertThat(pool.getExecutor().getPoolSize()).isEqualTo(0);
        assertThat(pool.getExecutor().getQueue().size()).isEqualTo(0);
    }

    @Test public void testRejection()
        throws ExecutionException, InterruptedException
    {
        testCustomPropertiesSpecification();
        final CommandThreadPool pool = Factory.getInstance(THREAD_POOL_COMMAND_KEY);

        // Fill the pool
        final Future<String> a = createTestCommand(THREAD_POOL_COMMAND_KEY).withExecutionDelay(200).queue();
        final Future<String> b = createTestCommand(THREAD_POOL_COMMAND_KEY).withExecutionDelay(200).queue();

        try {
            // Attempt work submission
            createTestCommand(THREAD_POOL_COMMAND_KEY).execute();
            failBecauseExceptionWasNotThrown(CommandInvocationException.class);
        }
        catch (final CommandInvocationException e) {
            assertThat(e.getCause()).isInstanceOf(ExecutionException.class);
            assertThat(e.getCause().getCause()).isInstanceOf(RejectedExecutionException.class);
        }

        // Assert pool thread and queue full
        assertThat(pool.getExecutor().getPoolSize()).isEqualTo(1);
        assertThat(pool.getExecutor().getQueue().size()).isEqualTo(1);

        // Wait termination :)
        a.get();
        b.get();
    }

    @Test public void testRejectionDueToInfiniteTimeout()
        throws InterruptedException, ExecutionException
    {
        final HttpInvoker        custom   = HttpInvokers.invoker(timeout.connectionUrl()).withCommandPool("TestCommandPool").withMetrics();
        final CommandThreadPool  pool     = Factory.getInstance("TestCommandPool");
        final ThreadPoolExecutor executor = pool.getExecutor();
        final int                max      = executor.getMaximumPoolSize() + executor.getQueue().remainingCapacity();

        for (int i = 0; i < max; i++)
            // Queue an infinite task
            custom.resource("/unreachable/" + i).invoke(HEAD).queue();
        try {
            // Prepare
            custom.resource("/unreachable").invoke(HEAD).execute();
            failBecauseExceptionWasNotThrown(CommandInvocationException.class);
        }
        catch (final CommandInvocationException e) {
            // CommandInvocationException -> ExecutionException -> RejectedExecutionException -> CommandStackCauseException
            assertThat(e).hasCauseExactlyInstanceOf(ExecutionException.class);
            assertThat(e.getCause()).hasCauseExactlyInstanceOf(RejectedExecutionException.class);
            assertThat(e).hasRootCauseExactlyInstanceOf(CommandStackCauseException.class);
        }
        finally {
            // Clear test garbage
            executor.shutdown();
            Factory.threadPools.remove("TestCommandPool");
        }
    }

    @Test public void testRejectionWithFallback()
        throws ExecutionException, InterruptedException
    {
        testCustomPropertiesSpecification();
        final CommandThreadPool pool = Factory.getInstance(THREAD_POOL_COMMAND_KEY);

        // Fill the pool
        final Future<String> a = createTestCommand(THREAD_POOL_COMMAND_KEY).withExecutionDelay(200).queue();
        final Future<String> b = createTestCommand(THREAD_POOL_COMMAND_KEY).withExecutionDelay(200).queue();

        final TestCommand c = createTestCommand(THREAD_POOL_COMMAND_KEY);
        c.withFallback(t -> {
            assertThat(t).isInstanceOf(RejectedExecutionException.class);
            return "fallback";
        });

        // Attempt work submission
        final String result = c.execute();
        assertThat(result).isEqualTo("fallback");

        // Assert pool thread and queue full
        assertThat(pool.getExecutor().getPoolSize()).isEqualTo(1);
        assertThat(pool.getExecutor().getQueue().size()).isEqualTo(1);

        // Wait termination :)
        a.get();
        b.get();
    }

    @Test public void testRejectionWithFallbackException()
        throws InterruptedException, ExecutionException
    {
        final HttpInvoker        custom   = HttpInvokers.invoker(timeout.connectionUrl()).withCommandPool("TestCommandPool").withMetrics();
        final CommandThreadPool  pool     = Factory.getInstance("TestCommandPool");
        final ThreadPoolExecutor executor = pool.getExecutor();
        final int                max      = executor.getMaximumPoolSize() + executor.getQueue().remainingCapacity();

        for (int i = 0; i < max; i++)
            // Queue an infinite task
            custom.resource("/unreachable/" + i).invoke(HEAD).queue();
        try {
            // Prepare
            final InvokerCommand<?> command = cast(custom.resource("/unreachable").invoke(HEAD));
            command.onErrorFallback(this::rejectedFallback);
            command.execute();
            failBecauseExceptionWasNotThrown(CommandInvocationException.class);
        }
        catch (final CommandInvocationException e) {
            // CommandInvocationException -> ExecutionException -> ServiceDownException -> CommandStackCauseException
            assertThat(e).hasCauseExactlyInstanceOf(ExecutionException.class);
            assertThat(e.getCause()).hasCauseExactlyInstanceOf(ServiceDownException.class);
            assertThat(e).hasRootCauseExactlyInstanceOf(CommandStackCauseException.class);
        }
        finally {
            // Clear test garbage
            executor.shutdown();
            Factory.threadPools.remove("TestCommandPool");
        }
    }

    @Test public void testShutdown() {
        final CommandThreadPool pool = Factory.getInstance(THREAD_POOL_COMMAND_KEY);

        assertThat(Factory.threadPools.size()).isNotEqualTo(0);
        assertThat(pool.getExecutor().isShutdown()).isFalse();

        Factory.shutdown();

        // Ensure all pools were removed from cache
        assertThat(Factory.threadPools.size()).isEqualTo(0);
        assertThat(pool.getExecutor().isShutdown()).isTrue();
    }

    @Test public void testShutdownWithTimeout()
        throws ExecutionException, InterruptedException
    {
        final CommandThreadPool pool = Factory.getInstance(SHUTDOWN_COMMAND_KEY);

        final Future<String> fast = queueShutdownCommand(200);
        final Future<String> slow = queueShutdownCommand(700);

        assertThat(fast.isCancelled()).isFalse();
        assertThat(fast.isDone()).isFalse();
        assertThat(slow.isCancelled()).isFalse();
        assertThat(slow.isDone()).isFalse();

        assertThat(Factory.threadPools.size()).isNotEqualTo(0);
        assertThat(pool.getExecutor().isShutdown()).isFalse();

        final long before = System.currentTimeMillis();

        final boolean result = Factory.shutdown(500, MILLISECONDS);
        assertThat(result).isFalse();  // Slow command await must have returned timeout

        assertThat(System.currentTimeMillis() - before).isGreaterThanOrEqualTo(500);

        // Ensure all pools were removed from cache
        assertThat(Factory.threadPools.size()).isEqualTo(0);
        assertThat(pool.getExecutor().isShutdown()).isTrue();

        assertThat(fast.isCancelled()).isFalse();
        assertThat(fast.isDone()).isTrue();
        assertThat(slow.isCancelled()).isFalse();
        assertThat(slow.isDone()).isFalse();

        assertThat(fast.get()).isEqualTo(HELLO);
        assertThat(slow.get()).isEqualTo(HELLO);
    }

    private TestCommand createTestCommand(final String commandKey) {
        return new TestCommand(HELLO) {
            @Override protected String getThreadPoolKey() {
                return commandKey;
            }
        };
    }

    private Future<String> queueShutdownCommand(int delay) {
        return createTestCommand(SHUTDOWN_COMMAND_KEY).withExecutionDelay(delay).queue();
    }

    private <T> T rejectedFallback(Throwable throwable) {
        if (throwable instanceof RejectedExecutionException) throw new ServiceDownException();
        if (throwable instanceof RuntimeException) throw (RuntimeException) throwable;
        throw new RuntimeException(throwable);
    }

    //~ Static Fields ................................................................................................................................

    @ClassRule public static ExternalResource properties = new ExternalResource() {
            @Override protected void before()
                throws Throwable
            {
                super.before();
                final CommandProps props = new CommandProps();
                props.poolTotalThreads    = 1;
                props.poolThreadQueueSize = 1;
                Context.getEnvironment().put(THREAD_POOL_COMMAND_KEY, props);
            }

            @Override protected void after() {
                Context.getEnvironment().delete(THREAD_POOL_COMMAND_KEY, CommandProps.class);
                super.after();
            }
        };

    private static final String SHUTDOWN_COMMAND_KEY    = "ShutdownCommand";
    private static final String THREAD_POOL_COMMAND_KEY = "ThreadPoolTest";

    @NonNls private static final String HELLO = "Hello World!";

    @ClassRule public static final ConnectionTimeoutRule timeout = new ConnectionTimeoutRule();

    //~ Inner Classes ................................................................................................................................

    private static class ServiceDownException extends RuntimeException {
        private ServiceDownException() {
            super("Service Down");
        }

        private static final long serialVersionUID = -6672741773933009808L;
    }
}  // end class CommandThreadPoolTest
