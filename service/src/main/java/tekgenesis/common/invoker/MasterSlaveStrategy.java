
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.core.Option;
import tekgenesis.common.logging.Logger;

import static java.lang.String.format;

import static tekgenesis.common.Predefined.option;
import static tekgenesis.common.core.Option.some;

/**
 * Simple master / slave strategy for multi host invoker support.
 */
class MasterSlaveStrategy implements MultiHostStrategy {

    //~ Instance Fields ..............................................................................................................................

    private final List<HttpInvoker> invokers;

    //~ Constructors .................................................................................................................................

    MasterSlaveStrategy(List<HttpInvoker> invokers) {
        if (invokers.size() < 2) throw new IllegalArgumentException("Master / Slave strategy must be implemented with a minimum of two invokers.");
        this.invokers = invokers;
    }

    //~ Methods ......................................................................................................................................

    @Override public Option<HttpInvoker> next(@NotNull Logger logger, @NotNull HttpInvoker last, @NotNull Invocation<?> failed) {
        final int         index = invokers.indexOf(last) + 1;
        final HttpInvoker next  = index < invokers.size() ? invokers.get(index) : null;
        log(logger, failed, last, next);
        return option(next);
    }

    @Override public Option<HttpInvoker> pick() {
        return some(getMaster());
    }

    private void log(Logger logger, Invocation<?> failed, @NotNull HttpInvoker last, @Nullable HttpInvoker next) {
        final String failure = format(EXECUTION_FAILED_ON_SERVER, failed.getPath(), last, invokers.indexOf(last) + 1, invokers.size());
        final String retry   = next != null ? format(WILL_RETRY_ON_SERVER, next, invokers.indexOf(next) + 1, invokers.size())
                                            : NO_FURTHER_SERVER_TO_RETRIEVE;
        logger.warning(failure + ". " + retry);
    }

    private HttpInvoker getMaster() {
        return invokers.get(0);
    }
}
