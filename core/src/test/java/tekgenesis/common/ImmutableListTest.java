
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.UnaryOperator;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.collections.ImmutableListIterator;
import tekgenesis.common.collections.Immutables;
import tekgenesis.common.util.Reflection;

import static java.util.Arrays.asList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;

import static tekgenesis.common.collections.Colls.*;

/**
 * User: emilio; Date: 12/16/11 Time: 12:08 PM;
 */
@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber", "NonJREEmulationClassesInClientCode", "OverlyLongMethod" })
public class ImmutableListTest {

    //~ Methods ......................................................................................................................................

    @SuppressWarnings("OverlyLongMethod")
    @Test public void testEmptyList() {
        assertThat(emptyList()).hasSize(0);
        assertThat(emptyList()).isEmpty();
        assertThat(emptyList().toArray()).hasSize(0);
        assertThat(emptyList().iterator()).isEmpty();
        assertThat(emptyList()).doesNotContain(2);
        assertThat(emptyList()).doesNotContain(1, 2);
        assertThat(emptyList().indexOf(2)).isEqualTo(-1);
        assertThat(emptyList().lastIndexOf(2)).isEqualTo(-1);
        assertThat(emptyList().subList(0, 0)).isSameAs(emptyList());
        assertThat(emptyList().contains(10)).isFalse();
        assertThat(emptyList().containsAll(asList(10, 20))).isFalse();

        final ImmutableList<Object> actual = emptyList().subList(0, 0);
        assertThat(actual).isSameAs(emptyList());

        final ImmutableListIterator<Object> listIterator = emptyList().listIterator();
        assertThat(listIterator).isEmpty();
        assertThat(listIterator.previousIndex()).isEqualTo(-1);
        assertThat(listIterator.nextIndex()).isEqualTo(0);
        assertThat(listIterator.hasNext()).isFalse();
        assertThat(listIterator.hasPrevious()).isFalse();
        try {
            listIterator.next();
            failBecauseExceptionWasNotThrown(NoSuchElementException.class);
        }
        catch (final NoSuchElementException ignore) {}
        try {
            listIterator.previous();
            failBecauseExceptionWasNotThrown(NoSuchElementException.class);
        }
        catch (final NoSuchElementException ignore) {}

        assertThat(emptyList().toArray()).hasSize(0);

        final String[]              a     = { "xx" };
        final ImmutableList<String> empty = emptyList();
        assertThat(empty.toArray(a)[0]).isNull();

        try {
            emptyList().get(0);
            failBecauseExceptionWasNotThrown(IndexOutOfBoundsException.class);
        }
        catch (final IndexOutOfBoundsException e) {
            assertThat(e).hasMessage("Empty List");
        }
        try {
            emptyList().subList(0, 1);
            failBecauseExceptionWasNotThrown(IndexOutOfBoundsException.class);
        }
        catch (final IndexOutOfBoundsException e) {
            assertThat(e).hasMessage("subList(0, 1) applied to list of size 0");
        }
        try {
            emptyList().subList(2, 3);
            failBecauseExceptionWasNotThrown(IndexOutOfBoundsException.class);
        }
        catch (final IndexOutOfBoundsException e) {
            assertThat(e).hasMessage("subList(2, 3) applied to list of size 0");
        }

        assertThat(emptyList().drop(2)).isEqualTo(emptyList());
        assertThat(emptyList().take(2)).isEqualTo(emptyList());
        assertThat(emptyList().slice(1, 10)).isEqualTo(emptyList());

        // Just for coverage
        Reflection.construct(Immutables.class);
        try {
            final Method sublistOf = emptyList().getClass().getDeclaredMethod("sublistOf", Integer.TYPE, Integer.TYPE);
            sublistOf.setAccessible(true);
            assertThat(emptyList()).isSameAs(sublistOf.invoke(emptyList(), 0, 0));
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            fail(e.getMessage(), e);
        }
    }  // end method testEmptyList

    @Test public void testImmutableList() {
        final ImmutableList<String> a = listOf("a", "b");

        assertThat(a.getSize().get()).isEqualTo(2);
        try {
            a.add(1, "c");
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException ignore) {}
        try {
            a.set(1, "c");
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException ignore) {}
        try {
            a.remove(1);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException ignore) {}
        final ImmutableList<String> xy = listOf("x", "y");
        try {
            a.addAll(1, xy);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException ignore) {}
        try {
            a.replaceAll(UnaryOperator.identity());
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException ignore) {}
        try {
            a.sort(Comparator.naturalOrder());
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException ignore) {}

        final ImmutableListIterator<String> it = a.listIterator(1);
        assertThat(it.previousIndex()).isZero();
        assertThat(it.nextIndex()).isEqualTo(1);
        assertThat(it.next()).isEqualTo("b");
        assertThat(it.hasNext()).isFalse();
        assertThat(it.hasPrevious()).isTrue();

        final ImmutableList<String> is = ImmutableList.<String>builder(a.size()).addAll(a).addAll(xy).add("b").build();
        assertThat(is.lastIndexOf("b")).isEqualTo(4);
        assertThat(is.lastIndexOf("w")).isEqualTo(-1);
        assertThat(is.toArray()).containsExactly("a", "b", "x", "y", "b");

        final ImmutableListIterator<String> it2 = is.listIterator(1);
        assertThat(it2.nextIndex()).isEqualTo(1);
        assertThat(it2.previousIndex()).isZero();
        assertThat(it2.next()).isEqualTo("b");
        assertThat(it2.hasNext()).isTrue();
        assertThat(it2.hasPrevious()).isTrue();
        assertThat(it2.previous()).isEqualTo("b");
        assertThat(it2.previous()).isEqualTo("a");
        try {
            it2.previous();
            Assertions.failBecauseExceptionWasNotThrown(NoSuchElementException.class);
        }
        catch (final NoSuchElementException ignore) {}
        while (it2.hasNext())
            it2.next();
        try {
            it2.next();
            Assertions.failBecauseExceptionWasNotThrown(NoSuchElementException.class);
        }
        catch (final NoSuchElementException ignore) {}

        assertThat(is.spliterator().estimateSize()).isEqualTo(5);

        final List<String> wa = new ArrayList<>();
        wa.add("aa");
        wa.add("bb");
        wa.add("aa");
        final ImmutableList<String> w = immutable(wa);
        assertThat(w.get(0)).isEqualTo("aa");
        assertThat(w.contains("bb")).isTrue();
        assertThat(w.containsAll(listOf("aa", "bb"))).isTrue();
        final StringBuilder sb = new StringBuilder();
        w.forEach(sb::append);
        assertThat(sb.toString()).isEqualTo("aabbaa");

        assertThat(w.indexOf("aa")).isZero();
        assertThat(w.lastIndexOf("aa")).isEqualTo(2);
        assertThat(w.isEmpty()).isFalse();
        assertThat(w.subList(2, 3)).containsExactly("aa");
        assertThat(w.subList(0, 2)).containsExactly("aa", "bb");
        assertThat(w.listIterator(1).next()).isEqualTo("bb");

        // Just for coverage
        Reflection.construct(is.getClass());
        Reflection.construct(w.getClass());
    }  // end method testImmutableList

    @Test public void testSingleton() {
        final ImmutableList<String> s = listOf("Hello");
        assertThat(s).hasSize(1);
        assertThat(s).isNotEmpty();
        assertThat(s.toArray()).hasSize(1);
        assertThat(s.iterator()).isNotEmpty();
        assertThat(s).contains("Hello");
        assertThat(s).doesNotContain("A", "B");
        assertThat(s.indexOf("A")).isEqualTo(-1);
        assertThat(s.lastIndexOf("A")).isEqualTo(-1);
        assertThat(s.subList(0, 0)).isSameAs(emptyList());
        assertThat(s.get(0)).isEqualTo("Hello");
        try {
            s.get(1);
            failBecauseExceptionWasNotThrown(IndexOutOfBoundsException.class);
        }
        catch (final IndexOutOfBoundsException ignore) {}
        assertThat(s.spliterator().estimateSize()).isEqualTo(1);
        s.sort(Comparator.naturalOrder());
        assertThat(s.drop(1)).isSameAs(emptyList());
        //
        // Just for coverage
        Reflection.construct(s.getClass());
        try {
            final Method sublistOf = s.getClass().getDeclaredMethod("sublistOf", Integer.TYPE, Integer.TYPE);
            sublistOf.setAccessible(true);
            assertThat(s).isSameAs(sublistOf.invoke(s, 0, 0));
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            fail(e.getMessage(), e);
        }
    }
}  // end class ImmutableListTest
