
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

import tekgenesis.common.core.*;

import static org.assertj.core.api.Assertions.assertThat;

import static tekgenesis.common.core.Tuple.tuple;

/**
 * User: emilio; Date: 12/16/11; Time: 12:47 PM;
 */

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class TupleTest {

    //~ Instance Fields ..............................................................................................................................

    private final IntIntTuple                                                                                      iit = tuple(1, 2);
    private final Tuple10<String, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t10 = tuple("Hello",
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            9);
    private final Tuple<String, Integer>                                                                           t2  = tuple("Hello", 1);
    private final Tuple3<String, Integer, Integer>                                                                 t3  = tuple("Hello", 1, 2);
    private final Tuple4<String, Integer, Integer, Integer>                                                        t4  = tuple("Hello", 1, 2, 3);
    private final Tuple5<String, Integer, Integer, Integer, Integer>                                               t5  = tuple("Hello", 1, 2, 3, 4);
    private final Tuple6<String, Integer, Integer, Integer, Integer, Integer>                                      t6  = tuple("Hello",
            1,
            2,
            3,
            4,
            5);
    private final Tuple7<String, Integer, Integer, Integer, Integer, Integer, Integer>                             t7  = tuple("Hello",
            1,
            2,
            3,
            4,
            5,
            6);
    private final Tuple8<String, Integer, Integer, Integer, Integer, Integer, Integer, Integer>                    t8  = tuple("Hello",
            1,
            2,
            3,
            4,
            5,
            6,
            7);
    private final Tuple9<String, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>           t9  = tuple("Hello",
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8);

    //~ Methods ......................................................................................................................................

    @Test public void append() {
        assertThat(iit.append(3)).isEqualTo(tuple(1, 2, 3));
        assertThat(t2.append(2)).isEqualTo(t3);
        assertThat(t3.append(3)).isEqualTo(t4);
        assertThat(t4.append(4)).isEqualTo(t5);
        assertThat(t5.append(5)).isEqualTo(t6);
        assertThat(t6.append(6)).isEqualTo(t7);
        assertThat(t7.append(7)).isEqualTo(t8);
        assertThat(t8.append(8)).isEqualTo(t9);
        assertThat(t9.append(9)).isEqualTo(t10);
    }

    @Test public void arity() {
        assertThat(iit.arity()).isEqualTo(2);
        assertThat(t2.arity()).isEqualTo(2);
        assertThat(t3.arity()).isEqualTo(3);
        assertThat(t4.arity()).isEqualTo(4);
        assertThat(t5.arity()).isEqualTo(5);
        assertThat(t6.arity()).isEqualTo(6);
        assertThat(t7.arity()).isEqualTo(7);
        assertThat(t8.arity()).isEqualTo(8);
        assertThat(t9.arity()).isEqualTo(9);
        assertThat(t10.arity()).isEqualTo(10);
    }

    @Test public void elements() {
        final Tuple4<String, Integer, Boolean, Double> t = tuple("a", 10, true, 12.5);
        assertThat(t.first()).isEqualTo("a");
        assertThat(t.second()).isEqualTo(10);
        assertThat(t.third()).isTrue();
        assertThat(t.fourth()).isEqualTo(12.5);
    }

    @Test public void equals() {
        assertThat(tuple("a", 10)).isEqualTo(tuple("a", 5 + 5));
        assertThat(tuple("a", 11)).isNotEqualTo(tuple("a", 5 + 5));
        assertThat(tuple("b", 11)).isNotEqualTo(tuple("a", 5 + 5));
        assertThat(tuple("b", 11)).isNotEqualTo("b11");

        assertThat(tuple("a", 10, true)).isEqualTo(tuple("a", 10, true));
        assertThat(tuple("a", 10)).isNotEqualTo(tuple("a", 10, true));

        assertThat(tuple("a", 10, true, 12.5)).isEqualTo(tuple("a", 10, true, 12.5));
        assertThat(tuple("a", 10, true, 12.5)).isNotEqualTo(tuple("a", 10, true, 10.5));
        assertThat(tuple("a", 10, false, 12.5)).isNotEqualTo(tuple("a", 10, true, 12.5));
        assertThat(tuple("a1", 10, true, 12.5)).isNotEqualTo(tuple("a", 10, true, 12.5));
        assertThat(tuple("a1", 10, true, 12.5)).isNotEqualTo(tuple("a", 10, true));

        assertThat(tuple("a", 10, true)).isEqualTo(tuple("a", 10, true));
        assertThat(tuple("a", 10, true)).isNotEqualTo(tuple("a", 10, false));
        assertThat(tuple("a", 10, true)).isNotEqualTo(tuple("a", 11, true));
        assertThat(tuple("a", 10, true)).isNotEqualTo(tuple("a", 10));

        final IntIntTuple t = tuple(10, 5);
        assertThat(t).isEqualTo(tuple(10, 5));
        assertThat(t).isNotEqualTo(tuple("a", 10));
        assertThat(t).isNotEqualTo(tuple(5, 10));

        assertThat(new P(1, 2)).isEqualTo(tuple(1, 2));
    }

    @Test public void hashcode() {
        assertThat(tuple("a", 10).hashCode()).isEqualTo(tuple("a", 5 + 5).hashCode());
        assertThat(tuple("a", 10).hashCode()).isEqualTo(3017);
        assertThat(tuple("a", 10, true).hashCode()).isEqualTo(94758);
        assertThat(tuple("a", 10, true, 12.5).hashCode()).isEqualTo(1079366298);
    }
    @Test public void toStringTest() {
        assertThat(tuple("a", 10).toString()).isEqualTo("Tuple(a, 10)");
        assertThat(tuple("a", 10, true).toString()).isEqualTo("Tuple(a, 10, true)");
        assertThat(tuple("a", 10, true, 12.5).toString()).isEqualTo("Tuple(a, 10, true, 12.5)");
    }

    @Test public void tuple10() {
        final Tuple10<String, Integer, Boolean, Double, Integer, Integer, Integer, Integer, Integer, Boolean> t = Tuple.tuple("a",
                10,
                true,
                12.5,
                1981,
                1,
                2,
                3,
                4,
                false);
        assertThat(t.first()).isEqualTo("a");
        assertThat(t.second()).isEqualTo(10);
        assertThat(t.third()).isTrue();
        assertThat(t.fourth()).isEqualTo(12.5);
        assertThat(t.fifth()).isEqualTo(1981);
        assertThat(t.sixth()).isEqualTo(1);
        assertThat(t.seventh()).isEqualTo(2);
        assertThat(t.eighth()).isEqualTo(3);
        assertThat(t.ninth()).isEqualTo(4);

        assertThat(t.hashCode()).isEqualTo(-802206128);

        assertThat(t.asList().toString()).isEqualTo("(a, 10, true, 12.5, 1981, 1, 2, 3, 4, false)");
    }

    @Test public void tuple5() {
        final Tuple5<String, Integer, Boolean, Double, Integer> t = tuple("a", 10, true, 12.5, 1981);
        assertThat(t.first()).isEqualTo("a");
        assertThat(t.second()).isEqualTo(10);
        assertThat(t.third()).isTrue();
        assertThat(t.fourth()).isEqualTo(12.5);
        assertThat(t.fifth()).isEqualTo(1981);

        assertThat(t.hashCode()).isEqualTo(-899381149);

        assertThat(t.asList().toString()).isEqualTo("(a, 10, true, 12.5, 1981)");
    }

    @Test public void tuple6() {
        final Tuple6<String, Integer, Boolean, Double, Integer, Integer> t = tuple("a", 10, true, 12.5, 1981, 1);
        assertThat(t.first()).isEqualTo("a");
        assertThat(t.second()).isEqualTo(10);
        assertThat(t.third()).isTrue();
        assertThat(t.fourth()).isEqualTo(12.5);
        assertThat(t.fifth()).isEqualTo(1981);
        assertThat(t.sixth()).isEqualTo(1);

        assertThat(t.hashCode()).isEqualTo(-2111011842);
        assertThat(t.sixth().equals(1));

        assertThat(t.asList().toString()).isEqualTo("(a, 10, true, 12.5, 1981, 1)");
    }

    @Test public void tuple7() {
        final Tuple7<String, Integer, Boolean, Double, Integer, Integer, Integer> t = tuple("a", 10, true, 12.5, 1981, 1, 2);
        assertThat(t.first()).isEqualTo("a");
        assertThat(t.second()).isEqualTo(10);
        assertThat(t.third()).isTrue();
        assertThat(t.fourth()).isEqualTo(12.5);
        assertThat(t.fifth()).isEqualTo(1981);
        assertThat(t.sixth()).isEqualTo(1);
        assertThat(t.seventh()).isEqualTo(2);

        assertThat(t.hashCode()).isEqualTo(-1016857660);

        assertThat(t.asList().toString()).isEqualTo("(a, 10, true, 12.5, 1981, 1, 2)");
    }

    @Test public void tuple9() {
        final Tuple9<String, Integer, Boolean, Double, Integer, Integer, Integer, Integer, Integer> t = tuple("a", 10, true, 12.5, 1981, 1, 2, 3, 4);
        assertThat(t.first()).isEqualTo("a");
        assertThat(t.second()).isEqualTo(10);
        assertThat(t.third()).isTrue();
        assertThat(t.fourth()).isEqualTo(12.5);
        assertThat(t.fifth()).isEqualTo(1981);
        assertThat(t.sixth()).isEqualTo(1);
        assertThat(t.seventh()).isEqualTo(2);
        assertThat(t.eighth()).isEqualTo(3);
        assertThat(t.ninth()).isEqualTo(4);

        assertThat(t.hashCode()).isEqualTo(2052332325);

        assertThat(t.asList().toString()).isEqualTo("(a, 10, true, 12.5, 1981, 1, 2, 3, 4)");
    }

    //~ Inner Classes ................................................................................................................................

    static class P extends IntIntTuple {
        protected P(int first, int second) {
            super(first, second);
        }

        int getX() {
            return _1();
        }
        int getY() {
            return _2();
        }

        private static final long serialVersionUID = 1437107587330149746L;
    }
}  // end class TupleTest
