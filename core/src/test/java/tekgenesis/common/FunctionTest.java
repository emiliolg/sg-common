
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.util.function.Function;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import static tekgenesis.common.core.Functions.*;

/**
 * User: emilio; Date: 12/16/11; Time: 12:47 PM;
 */

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class FunctionTest {

    //~ Methods ......................................................................................................................................

    @Test public void function() {
        final Function<Integer, String> f = String::valueOf;

        assertThat(f.apply(10)).isEqualTo("10");

        final Function<Object, Object> f1 = Function.identity();
        assertThat(f1.apply(10)).isEqualTo(10);

        final Function<Object[], Object> f2 = scalar();

        assertThat(f2.apply(new Object[] { "d", "a" })).isEqualTo("d");
    }
}
