
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.command;

import tekgenesis.common.core.Option;

import static tekgenesis.common.core.Option.some;

class TestCommand extends AbstractCommand<String> {

    //~ Instance Fields ..............................................................................................................................

    private Option<Integer> executionDelay   = Option.empty();
    private Option<Integer> executionTimeout = Option.empty();

    private final String message;

    //~ Constructors .................................................................................................................................

    TestCommand(String message) {
        this.message = message;
    }

    //~ Methods ......................................................................................................................................

    @Override protected String run() {
        delayRun();
        return message;
    }

    @Override protected String getThreadPoolKey() {
        return TEST_COMMAND_KEY;
    }

    @Override protected Option<Integer> getTimeoutIntervalTimeInMilliseconds() {
        return executionTimeout;
    }

    TestCommand withExecutionDelay(int delayInMilliseconds) {
        executionDelay = some(delayInMilliseconds);
        return this;
    }

    TestCommand withExecutionTimeout(int timeoutInMilliseconds) {
        executionTimeout = some(timeoutInMilliseconds);
        return this;
    }

    private void delayRun() {
        if (executionDelay.isPresent()) {
            try {
                Thread.sleep(executionDelay.get());
            }
            catch (final InterruptedException ignored) {}
        }
    }

    //~ Static Fields ................................................................................................................................

    public static final String TEST_COMMAND_KEY = "TestCommand";
}  // end class TestCommand
