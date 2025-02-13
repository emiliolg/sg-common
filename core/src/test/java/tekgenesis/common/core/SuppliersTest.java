
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.junit.Test;

import static java.lang.System.currentTimeMillis;

import static org.assertj.core.api.Assertions.*;

/**
 * User: emilio; Date: 12/16/11; Time: 12:47 PM;
 */

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class SuppliersTest {

    //~ Instance Fields ..............................................................................................................................

    private int i;

    //~ Methods ......................................................................................................................................

    @Test public void equals() {
        final Supplier<String> r = Suppliers.fromObject("Hello");
        assertThat(r.get()).isEqualTo("Hello");

        final ToStringBuilder b = new ToStringBuilder("Test");
        b.add(10).add(20);

        final Supplier<String> r3 = Suppliers.fromBuilder(b);
        assertThat(r3.get()).isEqualTo("Test(10, 20)");
    }

    @Test public void memoize() {
        final MemoizingSupplier<String> r = Suppliers.memoize(() -> {
                    sleep(10);
                    return "Hello";
                },
                1, TimeUnit.MINUTES);

        final long ts = currentTimeMillis();
        assertThat(r.get()).isEqualTo("Hello");
        assertThat(currentTimeMillis() - ts).isGreaterThanOrEqualTo(10);
        final long ts2 = currentTimeMillis();
        assertThat(r.get()).isEqualTo("Hello");
        assertThat(currentTimeMillis() - ts2).isLessThan(2);
        r.reset();
        assertThat(r.get()).isEqualTo("Hello");
        assertThat(currentTimeMillis() - ts).isGreaterThanOrEqualTo(10);
    }
    @Test public void memoizeExpiration() {
        i = 0;
        final MemoizingSupplier<String> r = Suppliers.memoize(() -> "Hello" + ++i, 20, TimeUnit.MILLISECONDS);

        assertThat(r.get()).isEqualTo("Hello1");
        assertThat(r.get()).isEqualTo("Hello1");
        sleep(30);
        assertThat(r.get()).isEqualTo("Hello2");
    }

    //~ Methods ......................................................................................................................................

    static void sleep(int ms) {
        System.out.println("Sleeping = " + ms);
        try {
            Thread.sleep(ms);
        }
        catch (final InterruptedException ignore) {}
    }
}  // end class SuppliersTest
