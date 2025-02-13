
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.command;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import org.jetbrains.annotations.NonNls;
import org.junit.Test;

import rx.Observable;

import tekgenesis.common.command.exception.CommandTimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * Command tests.
 */
@SuppressWarnings({ "JavaDoc", "MagicNumber", "DuplicateStringLiteralInspection" })
public class CommandTest {

    //~ Methods ......................................................................................................................................

    @Test public void testExecute() {
        final Command<String> command = createTestCommand();

        final String result = command.execute();
        assertThat(result).isEqualTo(HELLO);
    }

    @Test public void testExecuteWithDelay() {
        final Command<String> command = createTestCommand().withExecutionDelay(200);

        final String result = command.execute();
        assertThat(result).isEqualTo(HELLO);
    }

    @Test public void testExecuteWithDelayAndTimeout() {
        final Command<String> command = createTestCommand().withExecutionDelay(400).withExecutionTimeout(200);

        try {
            command.execute();
            failBecauseExceptionWasNotThrown(CommandTimeoutException.class);
        }
        catch (final CommandTimeoutException ignored) {}
    }
    @Test public void testObserve()
        throws ExecutionException, InterruptedException
    {
        final Command<String> command = createTestCommand();

        final Observable<String> result = command.observe();
        result.subscribe(s -> assertThat(s).isEqualTo(HELLO));
    }

    @Test public void testObserveWithDelay()
        throws ExecutionException, InterruptedException
    {
        final Command<String> command = createTestCommand().withExecutionDelay(200);

        final AtomicReference<String> value = new AtomicReference<>();

        final Observable<String> result = command.observe();
        result.subscribe(value::set);

        assertThat(value.get()).isNull();

        Thread.sleep(300);

        assertThat(value.get()).isEqualTo(HELLO);
    }

    @Test public void testObserveWithDelayAndTimeout()
        throws InterruptedException
    {
        final Command<String> command = createTestCommand().withExecutionDelay(400).withExecutionTimeout(200);

        final AtomicReference<String>    value = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();

        final Observable<String> result = command.observe();
        result.subscribe(value::set, error::set);

        assertThat(value.get()).isNull();
        assertThat(error.get()).isNull();

        Thread.sleep(300);

        assertThat(value.get()).isNull();
        final Throwable t = error.get();
        assertThat(t).isNotNull();
        assertThat(t.getClass()).isEqualTo(CommandTimeoutException.class);
    }

    @Test public void testQueue()
        throws ExecutionException, InterruptedException
    {
        final Command<String> command = createTestCommand();

        final Future<String> f = command.queue();
        assertThat(f.get()).isEqualTo(HELLO);
    }

    @Test public void testQueueCancel()
        throws ExecutionException, InterruptedException
    {
        final Command<String> command = createTestCommand().withExecutionDelay(400);

        final Future<String> f = command.queue();

        assertThat(f.isDone()).isFalse();
        assertThat(f.isCancelled()).isFalse();

        Thread.sleep(100);

        f.cancel(false);

        assertThat(f.isCancelled()).isTrue();
        assertThat(f.isDone()).isTrue();

        try {
            assertThat(f.get()).isEqualTo(HELLO);
            failBecauseExceptionWasNotThrown(CancellationException.class);
        }
        catch (final CancellationException e) {
            assertThat(e).hasMessageContaining("Subscription unsubscribed");
        }
    }

    @Test public void testQueueWithDelay()
        throws ExecutionException, InterruptedException
    {
        final Command<String> command = createTestCommand().withExecutionDelay(200);

        final Future<String> f = command.queue();
        assertThat(f.get()).isEqualTo(HELLO);
    }

    @Test public void testQueueWithDelayAndTimeout()
        throws InterruptedException
    {
        final Command<String> command = createTestCommand().withExecutionDelay(400).withExecutionTimeout(200);

        try {
            command.queue().get();
            failBecauseExceptionWasNotThrown(ExecutionException.class);
        }
        catch (final ExecutionException e) {
            assertThat(e.getCause().getClass()).isEqualTo(CommandTimeoutException.class);
        }
    }

    private TestCommand createTestCommand() {
        return new TestCommand(HELLO);
    }

    //~ Static Fields ................................................................................................................................

    @NonNls private static final String HELLO = "Hello World!";
}  // end class CommandTest
