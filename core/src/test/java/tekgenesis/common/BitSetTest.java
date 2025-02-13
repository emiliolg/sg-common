
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import tekgenesis.common.collections.ext.BitSet;

import static java.util.Arrays.asList;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;

import static tekgenesis.common.core.Tuple.tuple;

/**
 * BitSet tests.
 */
@SuppressWarnings({ "MagicNumber", "DuplicateStringLiteralInspection" })
public class BitSetTest {

    //~ Methods ......................................................................................................................................

    /** BitSet test. */
    @SuppressWarnings("OverlyLongMethod")
    @Test public void bitSet() {
        final BitSet ba = new BitSet(34);
        assertThat(ba.size()).isEqualTo(64);
        ba.set(7);
        ba.set(15);
        ba.set(31);
        ba.set(32);
        ba.set(35);

        assertThat(ba.get(7)).isTrue();
        assertThat(ba.get(14)).isFalse();
        assertThat(ba.get(32)).isTrue();
        assertThat(ba.get(35)).isTrue();

        ba.set(15, false);

        assertThat(ba.get(15)).isFalse();

        assertThat(ba.toString()).isEqualTo("7,31-32,35");
        assertThat(ba.isEmpty()).isFalse();
        assertThat(ba.toRange()).isNull();
        ba.set(7, false);
        ba.set(33);
        ba.set(34);
        assertThat(ba.toRange()).isEqualTo(tuple(31, 35));

        ba.clear();
        assertThat(ba.toString()).isEmpty();
        assertThat(ba.isEmpty()).isTrue();

        ba.setAll(true);
        assertThat(ba.toString()).isEqualTo("0-63");
        assertThat(ba.isEmpty()).isFalse();

        final BitSet ba2 = BitSet.valueOf("1-5,7,9");
        assertThat(ba2.toString()).isEqualTo("1-5,7,9");

        assertThat(ba2.get(6)).isFalse();
        assertThat(ba2.get(7)).isTrue();
        assertThat(ba2.get(9)).isTrue();

        ba2.set(6);
        ba2.set(9, false);

        assertThat(ba2.toRange()).isEqualTo(tuple(1, 7));

        assertThat(ba2.toString()).isEqualTo("1-7");

        assertThat(ba.intersects(ba2)).isTrue();

        try {
            ba.get(-1);
            failBecauseExceptionWasNotThrown(IndexOutOfBoundsException.class);
        }
        catch (final IndexOutOfBoundsException e) {
            assertThat(e).hasMessage("bitIndex < 0: -1");
        }

        try {
            ba.set(-1, true);
            failBecauseExceptionWasNotThrown(IndexOutOfBoundsException.class);
        }
        catch (final IndexOutOfBoundsException e) {
            assertThat(e).hasMessage("bitIndex < 0: -1");
        }

        try {
            new BitSet(-1);
            failBecauseExceptionWasNotThrown(NegativeArraySizeException.class);
        }
        catch (final NegativeArraySizeException e) {
            // noinspection SpellCheckingInspection
            assertThat(e).hasMessage("nbits < 0: -1");
        }

        assertThat(ba2.remove(1)).isTrue();
        assertThat(ba2.remove(1000)).isFalse();
    }  // end method bitSet

    /** Cluster test. */
    @Test public void cluster() {
        final List<List<String>> input = list(list("1", "2", "1"),
                list("1", "2", ""),
                list("1", "2", "3"),
                list("1", "2", "10"),
                list("1", "3", "1"),
                list("1", "3", "2"),
                list("1", "3", "3"),
                list("1", "3", "9"));
        assertThat(BitSet.cluster(input, 2))  //
        .isEqualTo(list(list("1", "2", ""), list("1", "2", "1,3,10"), list("1", "3", "1-3,9")));
    }

    /** Mapped test. */
    @Test public void mapped() {
        final BitSet ba = new BitSet(34);
        assertThat(ba.size()).isEqualTo(64);
        ba.set(7);
        ba.set(15);
        ba.set(31);
        ba.set(32);
        ba.set(35);

        final Set<String> ma = ba.mapToSet(Integer::valueOf, Object::toString);

        assertThat(ma.isEmpty()).isFalse();

        assertThat(ma.contains("7")).isTrue();

        assertThat(ma.contains("8")).isFalse();

        ma.add("8");
        ma.add("33");
        assertThat(ma.contains("8")).isTrue();

        ba.add(34);
        assertThat(ba.toString()).isEqualTo("7-8,15,31-35");

        ma.remove("8");

        assertThat(ma.contains("8")).isFalse();

        assertThat(ma.size()).isEqualTo(64);

        ma.clear();

        assertThat(ma.isEmpty()).isTrue();
    }

    //~ Methods ......................................................................................................................................

    /** List of elements. */
    @NotNull @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> List<T> list(T... elements) {
        return new ArrayList<>(asList(elements));
    }
}  // end class BitSetTest
