
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
import java.util.Random;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.collections.Seq;
import tekgenesis.common.core.DateTime;
import tekgenesis.common.core.Option;

import static tekgenesis.common.core.Option.some;

/**
 * Random strategy for multi host invoker support with fail over support and penalisation.
 */
class RandomStrategy extends RoundRobinStrategy {

    //~ Instance Fields ..............................................................................................................................

    private final Random random;

    //~ Constructors .................................................................................................................................

    RandomStrategy(List<HttpInvoker> invokers) {
        super(invokers);
        random = new Random();
    }

    //~ Methods ......................................................................................................................................

    @Override public Option<HttpInvoker> pick() {
        final Seq<InvokerEntry> invokerGoodEntries = invokers.filter(e -> e != null && DateTime.current().minutesFrom(e.lastFailed) > 5).toList();

        @NotNull final HttpInvoker next;
        if (!invokerGoodEntries.isEmpty()) {
            final InvokerEntry invokerEntry = invokerGoodEntries.toList().get(random.nextInt(invokerGoodEntries.size()));
            current = invokers.indexOf(invokerEntry);
            next    = invokerEntry.invoker;
        }
        else {
            final int index = random.nextInt(invokers.size());
            current = index;
            next    = invokers.get(index).invoker;
        }

        triedInvokers = new ArrayList<>();
        return some(next);
    }

    /* Sets the seed of strategy random number for test purposes. */
    void setRandomSeed(long seed) {
        random.setSeed(seed);
    }
}  // end class RandomStrategy
