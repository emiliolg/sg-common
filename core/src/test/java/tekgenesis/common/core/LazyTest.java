
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("JavaDoc")
public class LazyTest {

    //~ Instance Fields ..............................................................................................................................

    private final Lazy<String> r  = new Lazy<>(() -> "r");
    private final Lazy<String> s1 = new Lazy<>(() -> "s");
    private final Lazy<String> s2 = new Lazy<>(() -> "s");

    //~ Methods ......................................................................................................................................

    @Test public void testEquals()
        throws Exception
    {
        assertThat(s1).isEqualTo(s2);
        assertThat(s1).isEqualTo("s");
        assertThat(s1).isNotEqualTo(r);
        assertThat(s1).isNotEqualTo(null);
        assertThat(s1).isNotEqualTo("r");
    }

    @Test public void testGet()
        throws Exception
    {
        assertThat(s1.get()).isEqualTo("s");
    }

    @Test public void testHashCode()
        throws Exception
    {
        assertThat(s1.hashCode()).isEqualTo("s".hashCode());
    }

    @Test public void testIsDefined()
        throws Exception
    {
        assertThat(s1.isDefined()).isFalse();
        s1.get();
        assertThat(s1.isDefined()).isTrue();
    }

    @Test public void testToString()
        throws Exception
    {
        assertThat(s1.toString()).isEqualTo("s");
    }
}  // end class LazyTest
