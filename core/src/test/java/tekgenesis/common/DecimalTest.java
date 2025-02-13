
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.math.BigDecimal;

import org.junit.Test;

import tekgenesis.common.core.Decimals;
import tekgenesis.common.exception.FieldValueException;

import static java.math.BigDecimal.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import static tekgenesis.common.core.Decimals.*;
import static tekgenesis.common.core.Decimals.multiply;

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class DecimalTest {

    //~ Instance Fields ..............................................................................................................................

    private final BigDecimal d3 = new BigDecimal("1000.00");

    //~ Methods ......................................................................................................................................

    @Test public void checkTest() {
        try {
            scaleAndCheck("f", valueOf(-1000.00), false, 10, 2);
            failBecauseExceptionWasNotThrown(FieldValueException.class);
        }
        catch (final FieldValueException e) {
            assertThat(e.getMessage()).isEqualTo("Field 'f' value '-1000.0' cannot be negative.");
        }
        try {
            scaleAndCheck("f", valueOf(1000.00), true, 5, 2);
            failBecauseExceptionWasNotThrown(FieldValueException.class);
        }
        catch (final FieldValueException e) {
            assertThat(e.getMessage()).isEqualTo("Field 'f' value '1000.0' precision 6 exceeds 5.");
        }
        final BigDecimal f = scaleAndCheck("f", valueOf(1000.11), true, 8, 1);
        assertThat(f).isEqualTo(valueOf(1000.1));
    }

    @Test public void equals() {
        assertThat(Decimals.equal(ZERO, ZERO.setScale(2, ROUND_UNNECESSARY))).isTrue();
        assertThat(Decimals.equal(valueOf(1.12), valueOf(1000))).isFalse();
    }

    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    @Test public void hasValueTest() {
        assertThat(hasValue(valueOf(1000).subtract(d3))).isFalse();
        assertThat(hasValue(valueOf(1000))).isTrue();
        assertThat(hasValue(null)).isFalse();
    }

    @Test public void multiplyTest() {
        assertThat(multiply(valueOf(1000), 0.001).doubleValue()).isEqualTo(1);
        assertThat(multiply(d3, 0.001)).isEqualTo(new BigDecimal("1.00"));
    }
}  // end class DecimalTest
