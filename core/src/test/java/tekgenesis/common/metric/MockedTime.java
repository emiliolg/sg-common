
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.metric;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

/**
 * Mocked time allowing time increment.
 */
class MockedTime implements LongSupplier {

    //~ Instance Fields ..............................................................................................................................

    private final AtomicLong time = new AtomicLong(0);

    //~ Methods ......................................................................................................................................

    @Override public long getAsLong() {
        return time.get();
    }

    void increment(int millis) {
        time.addAndGet(millis);
    }
}
