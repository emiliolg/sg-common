
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class ObservableTest {

    //~ Instance Fields ..............................................................................................................................

    private int testData = 0;

    //~ Methods ......................................................................................................................................

    @Test public void interceptor() {
        try(MyObservableObject i = new MyObservableObject(100)) {
            assertThat(i.testValue(101)).isTrue();
            assertThat(i.testValue(50)).isFalse();
            assertThat(i.data).isEqualTo(101);
        }
        assertThat(testData).isEqualTo(101);
    }

    //~ Inner Interfaces .............................................................................................................................

    /**
     * This is defined in the module where I change the object.
     */
    interface MyObserver extends ObserverService<MyObserver, MyObservableObject> {
        boolean testValue(MyObservableObject oo, int value);
    }

    //~ Inner Classes ................................................................................................................................

    /**
     * This is defined in the module where I change the object.
     */
    class MyObservableObject extends ObservableObject<MyObservableObject, MyObserver> {
        private int data;

        MyObservableObject(int i) {
            super(MyObserver.class);
            data = i;
            init();
        }

        boolean testValue(int value) {
            for (final MyObserver myObserver : observers()) {
                if (!myObserver.testValue(this, value)) return false;
            }
            return true;
        }
        void setData(int data) {
            this.data = data;
            testData  = data;
        }
    }

    /**
     * This is the implementation of the observer that should reside in the other module See
     * META-INF/services/tekgenesis.common.util.ObservableTest$MyObserver.
     */
    public static class OneObserver implements MyObserver {
        @Override public void onClose(MyObservableObject o) {
            o.setData(o.data);
        }

        @Override public void onInit(MyObservableObject o) {
            o.data++;
        }

        @Override public boolean testValue(MyObservableObject oo, int value) {
            return oo.data == value;
        }
    }
}  // end class ObservableTest
