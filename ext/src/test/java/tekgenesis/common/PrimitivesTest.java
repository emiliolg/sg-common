
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.lang.Integer;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import static tekgenesis.common.util.Primitives.*;

/**
 * User: emilio; Date: 12/16/11; Time: 12:47 PM;
 */

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class PrimitivesTest {

    //~ Methods ......................................................................................................................................

    @Test public void primitives() {
        assertThat(isPrimitive("int")).isTrue();
        assertThat(isPrimitive("String")).isFalse();
        assertThat(isPrimitive(Integer.TYPE)).isTrue();

        assertThat(isWrapper(Integer.class)).isTrue();

        assertThat(wrapperFor("int")).isEqualTo(Integer.class);
        assertThat(wrapIfNeeded("int")).isEqualTo("java.lang.Integer");
        assertThat(wrapIfNeeded("a.b.X")).isEqualTo("a.b.X");

        assertThat(primitiveFor("java.lang.Double")).isEqualTo("double");
        assertThat(primitiveFor("Double")).isEqualTo("double");
        assertThat(primitiveFor("x")).isEqualTo("");

        assertThat(primitiveFor(Integer.class)).isEqualTo(Integer.TYPE);
        assertThat(primitiveFor(String.class)).isEqualTo(null);
    }
}
