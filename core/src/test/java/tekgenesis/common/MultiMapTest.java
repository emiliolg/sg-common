
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.util.Comparator;

import org.junit.Test;

import tekgenesis.common.collections.MultiMap;

import static java.util.Comparator.reverseOrder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: emilio; Date: 12/16/11; Time: 12:47 PM;
 */

public class MultiMapTest {

    //~ Methods ......................................................................................................................................

    @Test public void multiMap() {
        final MultiMap<String, String> lmm = MultiMap.createLinkedMultiMap();
        lmm.put("1", "1");
        lmm.put("1", "one");
        lmm.put("2", "2");

        assertThat(lmm.keys()).containsExactly("1", "2");

        assertThat(lmm.asMap().toString()).isEqualTo("{1=[1, one], 2=[2]}");

        final MultiMap<String, String> smm = MultiMap.createSortedMultiMap();
        smm.put("1", "bbbb");
        smm.put("1", "aaaa");
        smm.put("2", "aaaa");
        smm.putIfEmpty("2", "ccc");
        smm.putIfEmpty("3", "ccc");

        assertThat(smm.keys()).containsExactly("1", "2", "3");

        assertThat(smm.asMap().toString()).isEqualTo("{1=[aaaa, bbbb], 2=[aaaa], 3=[ccc]}");

        final MultiMap<String, String> umm = MultiMap.createUniqueMultiMap();
        umm.put("1", "bbbb");
        umm.put("2", "aaaa");
        umm.put("2", "aaaa");

        assertThat(umm.keys()).containsExactly("1", "2");

        assertThat(umm.asMap().toString()).isEqualTo("{1=[bbbb], 2=[aaaa]}");

        final MultiMap<String, String> ssmm = new MultiMap.Builder<String, String>().withSortedKeys(reverseOrder())
                                              .withSortedValues(reverseOrder())
                                              .build();
        ssmm.putAll(smm);
        ssmm.putAll("1", lmm.get("1"));

        assertThat(ssmm.toString()).isEqualTo("{3=[ccc], 2=[aaaa], 1=[one, bbbb, aaaa, 1]}");
        assertThat(ssmm.isEmpty()).isFalse();
        assertThat(ssmm.allValues()).containsExactly("ccc", "aaaa", "one", "bbbb", "aaaa", "1");

        final MultiMap<String, String> ssmm2 = new MultiMap.Builder<String, String>().withSortedKeys().withSortedValues().build();
        ssmm2.putAll(ssmm);
        assertThat(ssmm2.toString()).isEqualTo("{1=[1, aaaa, bbbb, one], 2=[aaaa], 3=[ccc]}");

        ssmm.removeAll("1");
        assertThat(ssmm.toString()).isEqualTo("{3=[ccc], 2=[aaaa]}");
        assertThat(ssmm.values().toString()).isEqualTo("[[ccc], [aaaa]]");
    }  // end method multiMap

    @Test public void simpleMultiMap() {
        final MultiMap<String, String> mm = MultiMap.createMultiMap();
        mm.put("1", "1");
        mm.put("1", "uno");
        mm.put("1", "one");
        mm.put("2", "2");
        mm.put("2", "two");

        assertThat(mm.get("1")).containsExactly("1", "uno", "one");
        assertThat(mm.get("2")).containsExactly("2", "two");
        assertThat(mm.allValues().toList()).containsExactly("1", "uno", "one", "2", "two");

        assertThat(mm.containsKey("1")).isTrue();
        assertThat(mm.containsKey("9")).isFalse();

        mm.remove("1", "uno");
        mm.remove("3", "uno");
        assertThat(mm.get("1")).containsExactly("1", "one");

        assertThat(mm.keys()).containsExactly("1", "2");
        assertThat(mm.getFirst("1").get()).isEqualTo("1");
    }

    @Test public void sortedMultiMap() {
        {
            final MultiMap<String, S> si = MultiMap.createSortedMultiMap();
            si.put("a", new S(1));
            si.put("a", new S(2));
            si.put("a", new S(10));

            assertThat(si.toString()).isEqualTo("{a=[1, 10, 2]}");
        }

        {
            final MultiMap<String, S> si = MultiMap.createSortedMultiMap(Comparator.comparingInt(s -> s.n));
            si.put("a", new S(1));
            si.put("a", new S(2));
            si.put("a", new S(10));

            assertThat(si.toString()).isEqualTo("{a=[1, 2, 10]}");
        }
    }

    //~ Inner Classes ................................................................................................................................

    private static class S {
        final int n;

        private S(int n) {
            this.n = n;
        }

        @Override public String toString() {
            return String.valueOf(n);
        }
    }
}  // end class MultiMapTest
