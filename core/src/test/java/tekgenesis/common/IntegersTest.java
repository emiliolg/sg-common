
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import org.junit.Test;

import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.exception.FieldValueException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import static tekgenesis.common.core.Integers.checkSignedLength;
import static tekgenesis.common.core.Integers.getLength;

/**
 * User: emilio; Date: 12/16/11; Time: 12:47 PM;
 */

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class IntegersTest {

    //~ Methods ......................................................................................................................................

    @Test public void checkTest() {
        try {
            checkSignedLength("f", -10, false, 10);
            failBecauseExceptionWasNotThrown(FieldValueException.class);
        }
        catch (final FieldValueException e) {
            assertThat(e.getMessage()).isEqualTo("Field 'f' value '-10' cannot be negative.");
        }
        try {
            checkSignedLength("f", 10000, true, 4);
            failBecauseExceptionWasNotThrown(FieldValueException.class);
        }
        catch (final FieldValueException e) {
            assertThat(e.getMessage()).isEqualTo("Field 'f' value '10000' precision 5 exceeds 4.");
        }
        final ImmutableList<String> errors = FieldValueException.captureErrors(() -> {
                assertThat(checkSignedLength("f", -10L, false, 10)).isEqualTo(-10L);
                assertThat(checkSignedLength("f", 10000L, true, 4)).isEqualTo(10000L);
            });
        assertThat(errors).containsExactly("Field 'f' value '-10' cannot be negative.", "Field 'f' value '10000' precision 5 exceeds 4.");
    }
    @Test public void lengthTest() {
        assertThat(getLength(0)).isEqualTo(1);
        assertThat(getLength(123)).isEqualTo(3);
        assertThat(getLength(-123)).isEqualTo(3);
        assertThat(getLength(123456789012L)).isEqualTo(12);
        assertThat(getLength(-123456789012L)).isEqualTo(12);
    }
}
