
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.util.concurrent.TimeUnit;

import tekgenesis.common.util.SingletonMemo;

/**
 * MyMemo test class.
 */
public class SlowMemo extends SingletonMemo<Integer, SlowMemo> {

    //~ Instance Fields ..............................................................................................................................

    int            count  = -1;
    private Thread thread = null;

    //~ Constructors .................................................................................................................................

    SlowMemo() {
        super(30, TimeUnit.MINUTES, true);
    }

    //~ Methods ......................................................................................................................................

    public void cancel() {
        thread.interrupt();
        count = -1;
    }

    public void reset() {
        count = -1;
        clear();
    }

    public Integer getCount() {
        return get();
    }

    @Override protected Integer calculate(long lastRefreshTime, Integer oldValue) {
        if (count >= 0)
            try {
                thread = Thread.currentThread();
                Thread.sleep(5000);
                return count;
            }
            catch (final InterruptedException e) {
                return count;
            }
        return ++count;
    }

    //~ Methods ......................................................................................................................................

    public static SlowMemo getInstance() {
        return getInstance(SlowMemo.class);
    }
}  // end class SlowMemo
