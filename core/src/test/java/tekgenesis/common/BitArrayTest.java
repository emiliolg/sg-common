
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
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import tekgenesis.common.core.BitArray;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;

/**
 * BitArray tests.
 */
@RunWith(JUnit4.class)
@SuppressWarnings({ "MagicNumber", "DuplicateStringLiteralInspection" })
public class BitArrayTest {

    //~ Methods ......................................................................................................................................

    /** BitArray test. */
    @Test public void bitArray() {
        final BitArray ba = new BitArray(34);
        assertThat(ba.size()).isEqualTo(64);

        ba.set(7, true);
        ba.set(15, true);
        ba.set(31, true);
        ba.set(32, true);
        ba.set(35, true);

        assertThat(ba.get(7)).isTrue();
        assertThat(ba.get(14)).isFalse();
        assertThat(ba.get(32)).isTrue();
        assertThat(ba.get(35)).isTrue();

        ba.set(15, false);

        assertThat(ba.get(15)).isFalse();
        assertThat(ba.toString()).isEqualTo("7,31-32,35");
        assertThat(ba.isEmpty()).isFalse();

        ba.clear();
        assertThat(ba.toString()).isEmpty();
        assertThat(ba.isEmpty()).isTrue();

        ba.setAll(true);
        assertThat(ba.toString()).isEqualTo("0-63");
        assertThat(ba.isEmpty()).isFalse();

        try {
            ba.get(-1);
            failBecauseExceptionWasNotThrown(ArrayIndexOutOfBoundsException.class);
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            assertThat(e).hasMessage("Array index out of range: -1");
        }

        try {
            ba.get(1000);
            failBecauseExceptionWasNotThrown(ArrayIndexOutOfBoundsException.class);
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            assertThat(e).hasMessage("Array index out of range: 1000");
        }

        try {
            ba.set(-1, true);
            failBecauseExceptionWasNotThrown(ArrayIndexOutOfBoundsException.class);
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            assertThat(e).hasMessage("Array index out of range: -1");
        }

        try {
            ba.set(1000, true);
            failBecauseExceptionWasNotThrown(ArrayIndexOutOfBoundsException.class);
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            assertThat(e).hasMessage("Array index out of range: 1000");
        }

        try {
            new BitArray(-1);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Illegal Size: -1");
        }
    }  // end method bitArray
}  // end class BitArrayTest
