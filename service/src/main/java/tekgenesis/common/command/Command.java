
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.command;

import java.util.concurrent.Future;

import rx.Observable;

/**
 * Used to wrap code that will execute potentially risky functionality (typically meaning a service
 * call over the network). This command is essentially a blocking command but provides an Observable
 * facade if used with observe()
 */
public interface Command<T> {

    //~ Methods ......................................................................................................................................

    /**
     * Used for synchronous execution of command.
     *
     * @return  T result of {@link Command} execution
     */
    T execute();

    /**
     * Used for asynchronous execution of command with a callback by subscribing to the
     * {@link Observable}. This eagerly starts execution of the command the same as {@link #queue()}
     * and {@link #execute()}.
     *
     * @return  {@code Observable<T>} that executes and calls back with the result of the command
     *          execution or a fallback if the command execution fails for any reason.
     */
    Observable<T> observe();

    /**
     * Used for asynchronous execution of command. This will queue up the command on the thread pool
     * and return an {@link Future} to get the result once it completes.
     *
     * @return  {@code Future<T>} Result of {@link AbstractCommand} execution
     */
    Future<T> queue();
}
