
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import tekgenesis.common.core.Constants;

/**
 * A simple class to print stats of memory consumption and elapsed time.
 */
class RunStat {

    //~ Instance Fields ..............................................................................................................................

    private long       lastTime;
    private final long startMem;

    private final long startTime;

    //~ Constructors .................................................................................................................................

    /** Creates a instance of the RunStat. */
    public RunStat() {
        startTime = System.currentTimeMillis();
        lastTime  = startTime;
        startMem  = currentMemory();
    }

    //~ Methods ......................................................................................................................................

    /** Print the stat. */
    public void printStat() {
        printStat("");
    }

    /** Print the stat prefixed by the specified msg. */
    @SuppressWarnings("WeakerAccess")
    public void printStat(String msg) {
        final long ts  = System.currentTimeMillis();
        final long mem = currentMemory();
        System.out.printf("%-20s %5d %5d %5dMB %5dMB\n", msg, ts - lastTime, ts - startTime, mem - startMem, mem);
        lastTime = ts;
    }

    private long currentMemory() {
        // System.gc();
        final Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / Constants.MEGA;
    }
}  // end class RunStat
