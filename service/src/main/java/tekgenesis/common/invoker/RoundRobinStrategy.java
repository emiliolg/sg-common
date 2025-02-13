
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.collections.Seq;
import tekgenesis.common.core.DateTime;
import tekgenesis.common.core.Option;
import tekgenesis.common.logging.Logger;

import static java.lang.String.format;

import static tekgenesis.common.Predefined.checkArgument;
import static tekgenesis.common.Predefined.option;
import static tekgenesis.common.collections.Colls.immutable;
import static tekgenesis.common.core.Option.some;

/**
 * RoundRobin strategy for multi host invoker support with fail over support and penalisation.
 */
class RoundRobinStrategy implements MultiHostStrategy {

    //~ Instance Fields ..............................................................................................................................

    protected int                     current       = -1;
    final ImmutableList<InvokerEntry> invokers;
    List<InvokerEntry>                triedInvokers = null;

    //~ Constructors .................................................................................................................................

    RoundRobinStrategy(List<HttpInvoker> invokers) {
        checkArgument(invokers.size() >= 2, "RoundRobin strategy must be implemented with a minimum of two invokers.");
        this.invokers = immutable(invokers).map(InvokerEntry::valueOf).toList();
    }

    //~ Methods ......................................................................................................................................

    @Override public Option<HttpInvoker> next(@NotNull Logger logger, @NotNull final HttpInvoker last, @NotNull Invocation<?> failed) {
        final Seq<InvokerEntry> invokerAllEntries = invokers.filter(e -> !e.invoker.equals(last) && !triedInvokers.contains(e)).toList();
        if (invokerAllEntries.isEmpty()) return logAndReturn(logger, failed, last, null);

        final Seq<InvokerEntry> invokerGoodEntries = invokerAllEntries.filter(e -> e != null && DateTime.current().minutesFrom(e.lastFailed) > 5);

        final Option<InvokerEntry> entry = invokerGoodEntries.getFirst().isPresent() ? invokerGoodEntries.getFirst() : invokerAllEntries.getFirst();
        return logAndReturn(logger, failed, last, entry.get().invoker);
    }

    @Override public Option<HttpInvoker> pick() {
        current       = (current + 1) % invokers.size();
        triedInvokers = new ArrayList<>();
        return some(getCurrent());
    }

    private Option<HttpInvoker> logAndReturn(Logger logger, Invocation<?> failed, @NotNull HttpInvoker last, @Nullable HttpInvoker next) {
        final InvokerEntry lastEntry = InvokerEntry.valueOf(last);
        final int          lastIndex = invokers.indexOf(lastEntry);
        triedInvokers.add(lastEntry);
        final String failure = format(MasterSlaveStrategy.EXECUTION_FAILED_ON_SERVER, failed.getPath(), last, triedInvokers.size(), invokers.size());
        invokers.get(lastIndex).failed(DateTime.current());
        final String retry = next != null ? format(WILL_RETRY_ON_SERVER, next, triedInvokers.size() + 1, invokers.size())
                                          : NO_FURTHER_SERVER_TO_RETRIEVE;
        logger.warning(failure + ". " + retry);
        return option(next);
    }

    private HttpInvoker getCurrent() {
        return invokers.get(current).invoker;
    }

    //~ Inner Classes ................................................................................................................................

    static class InvokerEntry {
        protected final HttpInvoker invoker;
        DateTime                    lastFailed = DateTime.EPOCH;

        InvokerEntry(HttpInvoker invoker) {
            this.invoker = invoker;
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final InvokerEntry that = (InvokerEntry) o;

            return invoker == that.invoker;
        }

        public void failed(DateTime dateTime) {
            lastFailed = dateTime;
        }

        @Override public int hashCode() {
            return invoker.hashCode();
        }

        public static InvokerEntry valueOf(HttpInvoker invoker) {
            return new InvokerEntry(invoker);
        }
    }
}  // end class RoundRobinStrategy
