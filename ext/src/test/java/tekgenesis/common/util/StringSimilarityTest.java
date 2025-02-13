
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

import static tekgenesis.common.util.StringSimilarity.similarity;

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class StringSimilarityTest {

    //~ Methods ......................................................................................................................................

    @Test public void stringSimilarity() {
        assertThat(similarity("", "")).isEqualTo(100);
        assertThat(similarity("123", "1234")).isEqualTo(75);
        assertThat(similarity("1234", "12345")).isEqualTo(80);
        assertThat(similarity("The quick fox jumped", "The Quick Brown fox jumped")).isEqualTo(77);
    }
}
