
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
public class MyMemo extends SingletonMemo<Integer, MyMemo> {

    //~ Instance Fields ..............................................................................................................................

    int count = -1;

    //~ Constructors .................................................................................................................................

    MyMemo() {
        super(30, TimeUnit.MINUTES);
    }

    //~ Methods ......................................................................................................................................

    public void reset() {
        count = -1;
        clear();
    }

    public Integer getCount() {
        return get();
    }

    @Override protected Integer calculate(long lastRefreshTime, Integer oldValue) {
        return ++count;
    }

    //~ Methods ......................................................................................................................................

    public static MyMemo getInstance() {
        return getInstance(MyMemo.class);
    }
}
