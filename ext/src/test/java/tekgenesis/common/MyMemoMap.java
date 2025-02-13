
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

import tekgenesis.common.util.MemoMap;

/**
 * MyMemo test class.
 */
public class MyMemoMap extends MemoMap<Integer, Integer, MyMemoMap> {

    //~ Instance Fields ..............................................................................................................................

    int count = -1;

    //~ Constructors .................................................................................................................................

    MyMemoMap() {
        super(30, TimeUnit.MINUTES);
    }

    //~ Methods ......................................................................................................................................

    public void reset() {
        count = -1;
        force();
    }

    public int getCount(int key) {
        return get(key);
    }

    @Override protected Integer calculate(Integer key, long lastRefreshTime, Integer oldValue) {
        return ++count * key;
    }

    //~ Methods ......................................................................................................................................

    public static MyMemoMap getInstance() {
        return getInstance(MyMemoMap.class);
    }
}
