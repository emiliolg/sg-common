
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import tekgenesis.common.collections.*;
import tekgenesis.common.core.Option;
import tekgenesis.common.core.Strings;
import tekgenesis.common.core.Tuple;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.nCopies;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.reverseOrder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;

import static tekgenesis.common.Predefined.*;
import static tekgenesis.common.collections.Colls.*;
import static tekgenesis.common.collections.Seq.*;
import static tekgenesis.common.core.Option.some;
import static tekgenesis.common.core.Predicates.hasLength;
import static tekgenesis.common.core.Predicates.startsWith;
import static tekgenesis.common.core.Tuple.tuple;

/**
 * User: emilio; Date: 12/16/11 Time: 12:08 PM;
 */
@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber", "NonJREEmulationClassesInClientCode", "OverlyLongMethod" })
public class CollsTest {

    //~ Methods ......................................................................................................................................

    @SuppressWarnings("OverlyLongMethod")
    @Test public void collsTest() {
        final List<String> l = new ArrayList<>();
        l.add("aa");
        l.add("bb");
        final MultiMap<String, String> multiMap = MultiMap.createMultiMap();
        multiMap.put("aa", "11");
        multiMap.put("aa", "22");

        assertThat(Colls.drop(null, 10)).isEmpty();
        assertThat(Colls.drop(l, 10)).isEmpty();
        assertThat(Colls.drop(l, 1)).contains("bb");
        assertThat(Colls.exists(null, "aa"::equals)).isFalse();
        assertThat(Colls.exists(l, "aa"::equals)).isTrue();
        assertThat(Colls.exists(l, "xx"::equals)).isFalse();

        assertThat(first(l).get()).isEqualTo("aa");
        assertThat(first(l, s -> s.startsWith("b")).get()).isEqualTo("bb");
        assertThat(first(l, s -> s.startsWith("c")).isEmpty()).isTrue();
        assertThat(flatMap(l, multiMap::get)).contains("11", "22");
        assertThat(flatMap(null, multiMap::get)).isEmpty();

        assertThat(forAll(l, s -> s.length() == 2)).isTrue();
        assertThat(forAll(l, s -> s.startsWith("a"))).isFalse();
        final ImmutableListIterator<String> li = immutable(l.listIterator(1));
        assertThat(li.previousIndex()).isEqualTo(0);
        assertThat(li.nextIndex()).isEqualTo(1);
        assertThat(li.next()).isEqualTo("bb");
        li.previous();
        assertThat(li.previous()).isEqualTo("aa");
        assertThat(li.hasPrevious()).isFalse();
        assertThat(li.hasNext()).isTrue();

        assertThat(mkString(l)).isEqualTo("(aa, bb)");
        assertThat(mkString(l, ":")).isEqualTo("aa:bb");
        assertThat(mkString(l, "<", ":", ">")).isEqualTo("<aa:bb>");

        final ImmutableListIterator<String> pepe = singletonIteraror("pepe");
        assertThat(pepe.previousIndex()).isEqualTo(-1);
        assertThat(pepe.nextIndex()).isZero();
        assertThat(pepe.hasNext()).isTrue();
        assertThat(pepe.hasPrevious()).isFalse();
        assertThat(pepe.next()).isEqualTo("pepe");
        assertThat(pepe.hasNext()).isFalse();
        assertThat(pepe.hasPrevious()).isTrue();
        assertThat(pepe.previousIndex()).isEqualTo(0);
        assertThat(pepe.nextIndex()).isEqualTo(1);
        assertThat(pepe.previous()).isEqualTo("pepe");

        assertThat(size(null)).isZero();
        assertThat(size(l)).isEqualTo(2);
        assertThat(size(filter(l, s -> s.length() == 2))).isEqualTo(2);
        assertThat(size(filter(l, s -> s.length() == 1))).isEqualTo(0);
        final ImmutableList<String> sorted = sorted(l, Comparator.reverseOrder());
        assertThat(sorted).contains("bb", "aa");

        assertThat(sorted.drop(1).getFirst().get()).isEqualTo("aa");

        assertThat(toList(filter(l, s -> s.length() == 2))).contains("aa", "bb");

        final Set<String> set = new HashSet<>(l);
        assertThat(immutable(set)).contains("aa");
    }  // end method collsTest

    @Test public void foldElements() {
        final ImmutableList<Integer> s = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        assertThat(s.foldLeft(0, (a, b) -> a + b)).isEqualTo(55);
        assertThat(s.foldLeft("", (a, b) -> a + b)).isEqualTo("12345678910");

        assertThat(s.collect(Collectors.averagingInt(a -> a))).isEqualTo(5.5);

        final Collection<Object> values = s.take(2).collect(TreeMap::new, (a, b) -> a.put(b, String.valueOf(b)), TreeMap::values);

        assertThat(values).containsExactly("1", "2");
    }

    @Test public void fromTree() {
        final Node tree = new Node("A", new Node("A.A"), new Node("A.B", new Node("A.B.A"), new Node("A.B.B")), new Node("A.C"));

        assertThat(deepSeq(tree).toStrings()).containsExactly("A.A", "A.B", "A.B.A", "A.B.B", "A.C");
    }
    @Test public void groupBy() {
        final ImmutableList<Tuple<Integer, String>> s = listOf(Tuple.tuple(1, "A"), Tuple.tuple(2, "B"), Tuple.tuple(3, "A"));

        final MultiMap<String, Tuple<Integer, String>> mm = s.groupBy(Tuple::_2);

        assertThat(mm.get("A").map(Tuple::_1)).containsExactly(1, 3);
        assertThat(mm.get("B").map(Tuple::_1)).containsExactly(2);
    }

    @Test public void lazyFindFirst() {
        final ImmutableList<Object> javaOrder = ImmutableList.build(b ->
                    Stream.of("a1", "b1", "c1", "d1", "e1", "f1").filter(s -> {
                              b.add("filter: " + s);
                              return s.startsWith("d");
                          }).findFirst().ifPresent(b::add));

        final ImmutableList<Object> suigenOrder = ImmutableList.build(b ->
                    Colls.listOf("a1", "b1", "c1", "d1", "e1", "f1").filter(s -> {
                             b.add("filter: " + s);
                             return s.startsWith("d");
                         }).getFirst().ifPresent(b::add));

        assertThat(javaOrder).containsExactlyElementsOf(suigenOrder);
    }

    @Test public void mapFactories() {
        final Map<String, Integer> m = Maps.hashMap(tuple("One", 1), tuple("Two", 2), tuple("Three", 3));

        assertThat(m.keySet()).contains("Three", "One", "Two");
        assertThat(m.values()).contains(3, 1, 2);

        final Map<String, Integer> m2 = Maps.linkedHashMap(tuple("One", 1), tuple("Two", 2), tuple("Three", 3));

        assertThat(m2.keySet()).containsExactly("One", "Two", "Three");
        assertThat(m2.values()).containsExactly(1, 2, 3);

        final Map<String, Integer> m3 = Maps.treeMap(tuple("One", 1), tuple("Two", 2), tuple("Three", 3));

        assertThat(m3.keySet()).containsExactly("One", "Three", "Two");
        assertThat(m3.values()).containsExactly(1, 3, 2);
    }

    @Test public void maps() {
        final ImmutableList<Integer> l = listOf(1, 2, 3);
        final Map<Integer, Integer>  m = Maps.identity(l);
        assertThat(m.keySet()).containsExactly(1, 2, 3);
        assertThat(m.values()).containsExactly(1, 2, 3);
    }

    @Test public void orderFilterMapSortedForEach() {
        final ImmutableList<String> javaOrder = ImmutableList.build(b ->
                    Stream.of("a1", "a2", "b1", "c2", "c1")
                          .filter(s ->
                            s.startsWith("c"))
                          .map(String::toUpperCase)
                          .sorted()
                          .forEach(b::add));

        final ImmutableList<String> suigenOrder = ImmutableList.build(b ->
                    Colls.listOf("a1", "a2", "b1", "c2", "c1")
                         .filter(s ->
                            s.startsWith("c"))
                         .map(String::toUpperCase)
                         .sorted(String::compareTo)
                         .forEach(b::add));

        assertThat(javaOrder).containsExactlyElementsOf(suigenOrder);
    }

    @Test public void orderFilterSortedMapForEach() {
        final ImmutableList<String> javaOrder = ImmutableList.build(b ->
                    Stream.of("d2", "a2", "b1", "b3", "c").filter(s -> {
                              b.add("filter: " + s);
                              return s.startsWith("a");
                          }).sorted((s1, s2) -> {
                              b.add(format("sort: %s; %s\n", s1, s2));
                              return s1.compareTo(s2);
                          }).map(s -> {
                              b.add("map: " + s);
                              return s.toUpperCase();
                          }).forEach(s -> b.add("forEach: " + s)));

        final ImmutableList<String> suigenOrder = ImmutableList.build(b ->
                    Stream.of("d2", "a2", "b1", "b3", "c").filter(s -> {
                              b.add("filter: " + s);
                              return s.startsWith("a");
                          }).sorted((s1, s2) -> {
                              b.add(format("sort: %s; %s\n", s1, s2));
                              return s1.compareTo(s2);
                          }).map(s -> {
                              b.add("map: " + s);
                              return s.toUpperCase();
                          }).forEach(s -> b.add("forEach: " + s)));

        assertThat(javaOrder).containsExactlyElementsOf(suigenOrder);
    }

    @Test public void orderMapFilterForEach() {
        final ImmutableList<String> javaOrder = ImmutableList.build(b ->
                    Stream.of("d2", "a2", "b1", "b3", "c").map(s -> {
                              b.add("map: " + s);
                              return s.toUpperCase();
                          }).filter(s -> {
                              b.add("filter: " + s);
                              return s.startsWith("A");
                          }).forEach(s -> b.add("forEach: " + s)));

        final ImmutableList<String> suigenOrder = ImmutableList.build(b ->
                    Colls.listOf("d2", "a2", "b1", "b3", "c").map(s -> {
                             b.add("map: " + s);
                             return s.toUpperCase();
                         }).filter(s -> {
                             b.add("filter: " + s);
                             return s.startsWith("A");
                         }).forEach(s -> b.add("forEach: " + s)));

        assertThat(javaOrder).containsExactlyElementsOf(suigenOrder);
    }

    @Test public void orderSorterFilterMapForEach() {
        final ImmutableList<String> javaOrder = ImmutableList.build(b ->
                    Colls.listOf("d2", "a2", "b1", "b3", "c").sorted((s1, s2) -> {
                             b.add(format("sort: %s; %s\n", s1, s2));
                             return s1.compareTo(s2);
                         }).filter(s -> {
                             b.add("filter: " + s);
                             return s.startsWith("a");
                         }).map(s -> {
                             b.add("map: " + s);
                             return s.toUpperCase();
                         }).forEach(s -> b.add("forEach: " + s)));

        final ImmutableList<String> suigenOrder = ImmutableList.build(b ->
                    Colls.listOf("d2", "a2", "b1", "b3", "c").sorted((s1, s2) -> {
                             b.add(format("sort: %s; %s\n", s1, s2));
                             return s1.compareTo(s2);
                         }).filter(s -> {
                             b.add("filter: " + s);
                             return s.startsWith("a");
                         }).map(s -> {
                             b.add("map: " + s);
                             return s.toUpperCase();
                         }).forEach(s -> b.add("forEach: " + s)));

        assertThat(javaOrder).containsExactlyElementsOf(suigenOrder);
    }

    @Test public void sequence() {
        final Seq<Integer> s = listOf(1);
        assertThat(s.mkString("{", ",", "}")).isEqualTo("{1}");
        assertThat(s.getFirst().get()).isEqualTo(1);
        assertThat(s).isNotEmpty();

        final ImmutableIterator<Integer> it = s.iterator();
        assertThat(it.next()).isEqualTo(1);
        try {
            it.next();
            failBecauseExceptionWasNotThrown(NoSuchElementException.class);
        }
        catch (final NoSuchElementException e) {
            assertThat(e).hasMessage(null);
        }

        assertThat(s.toList()).isEqualTo(listOf(1));

        assertThat(s.hashCode()).isEqualTo(32);

        assertThat(s.equals(listOf(1))).isTrue();
        assertThat(s.equals(listOf(2))).isFalse();

        assertThat(s.filter(Integer.class)).isNotEmpty();

        final Seq<Integer> newSeq = s.append(listOf(2, 3));
        assertThat(newSeq).containsExactly(1, 2, 3);
        assertThat(newSeq).hasSize(3);
        assertThat(newSeq.getFirst(v -> v != null && v >= 2)).isEqualTo(some(2));

        assertThat(seq(new ArrayList<Integer>())).isEmpty();

        assertThat(repeat("A").take(5)).containsExactly("A", "A", "A", "A", "A");
        assertThat(repeat("A").take(5).toSet()).containsExactly("A");

        assertThat(newSeq.revert()).containsExactly(3, 2, 1);
        assertThat(newSeq.hashCode()).isEqualTo(30817);
        assertThat(newSeq.filter(n -> n < 3).size()).isEqualTo(2);
        assertThat(newSeq.toString()).isEqualTo("(1, 2, 3)");

        // noinspection unchecked
        final Seq<ImmutableList<Integer>> grouped = fromTo(1, 10).grouped(3);
        final Seq<ImmutableList<Integer>> result  = listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9), listOf(10));
        assertThat(grouped.equals(result)).isTrue();
    }  // end method sequence

    @Test public void sequenceSet() {
        final Seq<Integer> s = listOf(1, 2, 3).append(listOf(4, 5, 6)).append(Colls.emptyList()).append(listOf(7, 8, 9));
        assertThat(s).hasSize(9);
        assertThat(s).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    @Test public void takeElements() {
        final ImmutableList<Integer> s = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        assertThat(take(s, 3)).containsExactly(1, 2, 3);

        assertThat(take(s, 11)).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test public void testAppend() {
        final Seq<Integer> seq = listOf(1).append(2).append(3, 4, 5).append(repeat(10).take(5));
        assertThat(seq.size()).isEqualTo(10);
        assertThat(seq).contains(1, 2, 3, 4, 5, 10, 10, 10, 10, 10);
        assertThat(seq.contains(10)).isTrue();
        assertThat(seq.drop(4).take(2).equals(listOf(5, 10))).isTrue();
    }

    @Test public void testContains() {
        assertThat(Colls.contains(STRINGS, "aa")).isTrue();
        assertThat(Colls.contains(null, "aa")).isFalse();
        assertThat(Colls.contains(STRINGS, "dd")).isFalse();
        assertThat(STRINGS.contains("aa")).isTrue();
        assertThat(STRINGS.contains("bb")).isTrue();
        assertThat(STRINGS.contains("dd")).isFalse();
        assertThat(STRINGS.containsAll(asList("aa", "bb"))).isTrue();
        assertThat(STRINGS.containsAll(asList("aa", "zz"))).isFalse();
    }

    @Test public void testEmptyIterator() {
        assertThat(emptyIterator()).hasSize(0);
        try {
            emptyIterator().next();
            failBecauseExceptionWasNotThrown(NoSuchElementException.class);
        }
        catch (final NoSuchElementException e) {
            assertThat(e).hasMessage(Immutables.EMPTY_ITERATOR_MSG);
        }
    }

    @Test public void testExists() {
        assertThat(STRINGS.exists(startsWith("b"))).isTrue();
        assertThat(STRINGS.exists(startsWith("x"))).isFalse();
    }

    @Test public void testFilter() {
        assertThat(STRINGS.filter(s -> s.startsWith("b"))).containsExactly("bb", "b1", "b2");
        assertThat(STRINGS.filter(s -> !s.startsWith("b"))).containsExactly("aa", "cc");

        final ImmutableList<Object> l2 = listOf("aa", 10, "b1", 20, "cc2");

        final Seq<String> strings = l2.filter(String.class);

        assertThat(strings).containsExactly("aa", "b1", "cc2");
        assertThat(strings).isNotEmpty();
        assertThat(l2.filter(String.class, s -> s.length() == 2)).containsExactly("aa", "b1");
        final List<String> a2 = into(filter(l2, String.class), new ArrayList<>());
        assertThat(strings.equals(a2));

        assertThat(listOf("aa", null, "b1", null, "cc2").filter(Objects::isNull).size()).isEqualTo(2);
    }

    @Test public void testFlatMap() {
        final ImmutableList<Integer> l = listOf(1, 2, 3, 4, 5, 6);

        final Seq<Integer> map = l.flatMap(l::take).take(8);

        assertThat(map).containsExactly(1, 1, 2, 1, 2, 3, 1, 2);
        final ImmutableList<String> m2 = listOf(1, 0, 3, 1).flatMap(CollsTest::charList).toList();
        assertThat(m2).containsExactly("*", "*", "*", "*", "*");

        final Seq<Integer> map1 = l.flatMap(n -> n % 2 != 0 ? Option.empty() : Option.of(n));
        assertThat(map1).containsExactly(2, 4, 6);
    }

    @Test public void testForAll() {
        assertThat(STRINGS.forAll(hasLength(2))).isTrue();
        assertThat(STRINGS.forAll(startsWith("b"))).isFalse();
    }
    @Test public void testImmutable() {
        final Set<String> set = new HashSet<>();
        set.add("aa");
        set.add("bb");
        final ImmutableSet<String> iset = immutable(set);
        try {
            iset.add("cc");
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException ignore) {}

        try {
            iset.addAll(set);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException ignore) {}

        try {
            iset.clear();
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException ignore) {}

        try {
            iset.remove("aa");
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException ignore) {}

        try {
            iset.removeAll(set);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException ignore) {}

        try {
            iset.removeIf(s -> s.length() == 2);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException ignore) {}

        try {
            iset.retainAll(set);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException ignore) {}
        try {
            for (final ImmutableIterator<String> iterator = iset.iterator(); iterator.hasNext();) {
                iterator.remove();
                iterator.next();
            }
            iset.retainAll(set);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException ignore) {}

        final ImmutableList<String> strings = iset.toList();
        try {
            strings.listIterator().add("xx");
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException ignore) {}
        try {
            strings.listIterator().set("xx");
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException ignore) {}
    }  // end method testImmutable
    @Test public void testImmutableCollection() {
        final Map<String, String>         map = Maps.linkedHashMap(tuple("aa", "a1a1"), tuple("bb", "b1b1"));
        final ImmutableCollection<String> c   = immutable(map.values());
        assertThat(c.getFirst().get()).isEqualTo("a1a1");
        assertThat(c.contains("b1b1")).isTrue();
        assertThat(c.containsAll(asList("a1a1", "b1b1"))).isTrue();
        assertThat(c.size()).isEqualTo(2);
        assertThat(c.getSize().get()).isEqualTo(2);
    }

    @Test public void testImmutableSet() {
        final Set<String>          set  = new HashSet<>(asList("aa", "bb", "cc"));
        final ImmutableSet<String> iset = immutable(set);
        assertThat(iset.contains("bb")).isTrue();
        assertThat(iset.containsAll(asList("bb", "aa"))).isTrue();
        assertThat(iset.isEmpty()).isFalse();
        assertThat(iset.toSet()).isSameAs(iset);
        assertThat(iset.equals(set)).isTrue();
    }

    @Test public void testIsEmpty() {
        checkEmpty(null, null, null);
        checkEmpty("", emptyList(), emptyIterable());

        final ImmutableList<String> lst      = listOf("xx");
        final Iterable<String>      iterable = listOf("xx");

        assertThat(isEmpty("x")).isFalse();
        assertThat(isEmpty(lst)).isFalse();
        assertThat(isEmpty(iterable)).isFalse();

        assertThat(isNotEmpty("x")).isTrue();
        assertThat(isNotEmpty(lst)).isTrue();
        assertThat(isNotEmpty(iterable)).isTrue();
    }

    @Test public void testIsInstanceOf() {
        final ImmutableList<Integer> l1 = listOf(1, 2, 3);
        final ImmutableList<Integer> l2 = emptyList();

        assertThat(isInstanceOf(l1, List.class, Integer.class)).isTrue();
        assertThat(isInstanceOf(l1, List.class, String.class)).isFalse();

        assertThat(isInstanceOf(l2, List.class, Integer.class)).isTrue();
        assertThat(isInstanceOf(l2, List.class, String.class)).isTrue();

        assertThat(isInstanceOf("", List.class, String.class)).isFalse();
    }

    @Test public void testMap() {
        final ImmutableList<String> l = listOf("aa", "bb", "b1", "b2", "cc");

        final Seq<String> map = l.map(value -> value.substring(1));
        assertThat(map.isEmpty()).isFalse();

        assertThat(map).containsExactly("a", "b", "1", "2", "c");

        final Iterator<String> iterator = map.iterator();
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isEqualTo("a");
        assertThat(iterator.next()).isEqualTo("b");
    }

    @Test public void testSlice() {
        assertThat(STRINGS.slice(2, 3)).containsExactly("b1");
        assertThat(STRINGS.slice(0, 2)).containsExactly("aa", "bb");
        assertThat(STRINGS.drop(2)).containsExactly("b1", "b2", "cc");
        assertThat(STRINGS.slice(2, 0)).isEqualTo(emptyList());
        assertThat(STRINGS.slice(20, 30)).isEqualTo(emptyList());
        assertThat(STRINGS.take(2)).containsExactly("aa", "bb");

        final ArrayList<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(2);
        integers.add(3);
        integers.add(4);
        final ImmutableCollection<Integer> immutable = immutable((Collection<Integer>) integers);
        assertThat(immutable.take(2)).containsExactly(1, 2);
        assertThat(immutable.take(4)).containsExactly(1, 2, 3, 4);
        assertThat(immutable.take(10)).containsExactly(1, 2, 3, 4);
    }

    @Test public void testTake() {
        assertThat(STRINGS.take(2)).containsExactly("aa", "bb");
        assertThat(STRINGS.take(20)).containsExactly("aa", "bb", "b1", "b2", "cc");
    }
    @Test public void testToArray() {
        final Set<String> set = new HashSet<>();
        set.add("aa");
        set.add("bb");
        final ImmutableSet<String> iset = immutable(set);
        final Object[]             a    = iset.toArray();
        assertThat(a[0]).isEqualTo("aa");
        assertThat(a[1]).isEqualTo("bb");
        assertThat(a).isInstanceOf(Object[].class);

        final String[] a2 = iset.toArray(new String[4]);
        assertThat(a2[0]).isEqualTo("aa");
        assertThat(a2[1]).isEqualTo("bb");
        assertThat(a2[2]).isNull();
        assertThat(a2).isInstanceOf(String[].class);

        final String[] a3 = iset.toArray(new String[1]);
        assertThat(a3[0]).isEqualTo("aa");
        assertThat(a3[1]).isEqualTo("bb");
        assertThat(a3).isInstanceOf(String[].class);
        assertThat(a3.length).isEqualTo(2);

        final String[] a4 = iset.toArray(String[]::new);
        assertThat(a4[0]).isEqualTo("aa");
        assertThat(a4[1]).isEqualTo("bb");
        assertThat(a4).isInstanceOf(String[].class);
        assertThat(a4.length).isEqualTo(2);
    }

    @Test public void testTraversable() {
        final Traversable<Character> st = Strings.traverse("Hello World");

        assertThat(st.min(naturalOrder()).get()).isEqualTo(' ');
        assertThat(st.max(naturalOrder()).get()).isEqualTo('r');

        final Traversable<Character> emptySt = Strings.traverse("");

        assertThat(emptySt.min(naturalOrder())).isEmpty();
        assertThat(emptySt.max(naturalOrder())).isEmpty();

        assertThat(st.filter(c -> c != ' ').sorted(naturalOrder()).foldLeft("", (s, c) -> s + c)).isEqualTo("HWdellloor");
        assertThat(st.sorted(reverseOrder()).mkString("")).isEqualTo("roollledWH ");

        final StringBuilder r = new StringBuilder();
        st.forEachWhile(c -> {
            if (c == ' ') return false;
            r.append(c);
            return true;
        });
        assertThat(r.toString()).isEqualTo("Hello");

        final StringBuilder r2 = new StringBuilder();
        st.map(String::valueOf).forEach(s -> r2.insert(0, s));
        assertThat(r2.toString()).isEqualTo("dlroW olleH");

        final String s = st.flatMap(c -> Character.isUpperCase(c) ? listOf(c, c) : emptyList()).mkString(".");
        assertThat(s).isEqualTo("H.H.W.W");
    }

    @Test public void topologicalSort() {
        final ImmutableList<Integer> s = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        final Set<Integer> r = s.topologicalSort(value -> {
                if (value < 9) return listOf(value + 2);
                return emptyIterable();
            });

        assertThat(r).containsExactly(9, 7, 5, 3, 1, 10, 8, 6, 4, 2);

        final Set<Integer> r2 = s.topologicalSort(value -> {
                if (value < 9) return listOf(value + 2, value + 1);
                return emptyIterable();
            });
        assertThat(r2).containsExactly(9, 10, 8, 7, 6, 5, 4, 3, 2, 1);
    }

    @Test public void zipElements() {
        final ImmutableList<String> l      = listOf("aa", "bb", "cc", "dd");
        final ImmutableList<String> sorted = l.sorted(Comparator.reverseOrder());

        final Seq<Tuple<String, String>> zip = l.zip(sorted);
        // noinspection unchecked
        assertThat(zip.map(t -> t._1() + t._2())).containsExactly("aadd", "bbcc", "ccbb", "ddaa");

        assertThat(l.zipWith((a, b) -> a + "." + b, sorted)).containsExactly("aa.dd", "bb.cc", "cc.bb", "dd.aa");

        final Seq<Integer> ns = fromTo(1, 4);

        final List<String> list1 = ns.zipWith(Collections::nCopies, l).reduce((a, b) -> append(a, b).toList());
        final Seq<String>  list2 = ns.zip(l).flatMap(t -> nCopies(t._1(), t._2())).toList();
        final List<String> list3 = Seq.flatten(ns.zipWith(Collections::nCopies, l)).toList();

        assertThat(equal(list1, list2)).isTrue();
        assertThat(equal(list2, list3)).isTrue();

        final Seq<Integer> l2 = from(1).zipWith(Integer::sum, repeat(1)).take(3).toList();
        assertThat(l2).containsExactly(2, 3, 4);

        final Seq<Integer> factorials = from(1).map(n -> fromTo(1, n).reduce((a, b) -> a * b));
        assertThat(factorials.takeWhile(n -> n < 100).toList()).containsExactly(1, 2, 6, 24);

        // final Seq<Integer> ps = primes().take(5).toList();
        // System.out.println("ps = " + ps);
    }

    // private static Seq<Integer> primes() {
    // final Seq<Integer> primesPromise = Seq.lazySeq(CollsTest::primes);
    // return from(2).filter(n -> primesPromise.takeWhile(v -> v > n).forAll(v -> n%v != 0));
    // }

    private void checkEmpty(@Nullable String str, @Nullable ImmutableList<Object> list, @Nullable Seq<Object> iterable) {
        assertThat(isEmpty(str)).isTrue();
        assertThat(isEmpty(list)).isTrue();
        assertThat(isEmpty(iterable)).isTrue();

        assertThat(isNotEmpty(str)).isFalse();
        assertThat(isNotEmpty(list)).isFalse();
        assertThat(isNotEmpty(iterable)).isFalse();
    }

    //~ Methods ......................................................................................................................................

    @Nullable private static List<String> charList(int n) {
        if (n == 0) return null;
        final List<String> r = new ArrayList<>();
        for (int i = 0; i < n; i++)
            r.add("*");
        return r;
    }

    //~ Static Fields ................................................................................................................................

    static final ImmutableList<String> STRINGS = listOf("aa", "bb", "b1", "b2", "cc");

    //~ Inner Classes ................................................................................................................................

    private static class Node implements Iterable<Node> {
        List<Node> children;
        String     value;

        private Node(String value, Node... children) {
            this.value    = value;
            this.children = asList(children);
        }

        @Override public Iterator<Node> iterator() {
            return children.iterator();
        }

        @Override public String toString() {
            return value;
        }
    }
}  // end class CollsTest
