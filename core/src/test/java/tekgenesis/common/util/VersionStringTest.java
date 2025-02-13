
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

import tekgenesis.common.Predefined;
import tekgenesis.common.collections.ImmutableList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;

import static tekgenesis.common.collections.Colls.listOf;
import static tekgenesis.common.util.VersionString.VERSION_ZERO;
import static tekgenesis.common.util.VersionString.versionFrom;

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class VersionStringTest {

    //~ Methods ......................................................................................................................................

    @Test public void version() {
        final ImmutableList<VersionString> l = listOf(versionFrom("2"),
                versionFrom("1.1"),
                versionFrom("1.2"),
                versionFrom("1.1.1"),
                versionFrom("1.10"));

        assertThat(l.sorted(Predefined::compare).mkString(",")).isEqualTo("1.1,1.1.1,1.2,1.10,2");

        assertThat(versionFrom("1.1")).isEqualTo(versionFrom("1.01"));

        assertThat(versionFrom("1.1").hashCode()).isEqualTo(versionFrom("1.01").hashCode());

        assertThat(versionFrom("1.1.1").increment(true)).isEqualTo(versionFrom("1.1.2"));
        assertThat(versionFrom("1.1").increment(true)).isEqualTo(versionFrom("1.2"));
        assertThat(versionFrom("1").increment(true)).isEqualTo(versionFrom("1.1"));

        assertThat(versionFrom("1").increment(false)).isEqualTo(versionFrom("2.0"));
        assertThat(versionFrom("1.1.1").increment(false)).isEqualTo(versionFrom("1.2.0"));
        assertThat(versionFrom("1.1").increment(false)).isEqualTo(versionFrom("2.0"));

        assertThat(versionFrom("")).isEqualTo(VERSION_ZERO);

        try {
            versionFrom("1.1a");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage(VersionString.INVALID_VERSION_FORMAT);
        }
    }
}
