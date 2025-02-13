
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.util.HashMap;

/**
 * A handy Timer class for debugging.
 */
public class Timer {

    //~ Instance Fields ..............................................................................................................................

    private final HashMap<String, Long> counters = new HashMap<>();
    private long                        initTime = System.currentTimeMillis();

    //~ Methods ......................................................................................................................................

    /** Prints time elapsed since counter creation with id. */
    public void elapsed(String msg, String id) {
        System.out.println(msg + ELAPSED + (System.currentTimeMillis() - counters.get(id)) + "ms");
    }

    /** Prints time elapsed since timer creation or reset with message. */
    public void lap(String msg) {
        System.out.println(msg + ELAPSED + (System.currentTimeMillis() - initTime) + "ms");
    }

    /** Resets timer. */
    public void reset() {
        initTime = System.currentTimeMillis();
        counters.clear();
    }

    /** Starts a counter with id. */
    public void start(String id) {
        counters.put(id, System.currentTimeMillis());
    }

    //~ Static Fields ................................................................................................................................

    private static final String ELAPSED = "-- Elapsed ";
}
