
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
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

import tekgenesis.common.collections.Colls;
import tekgenesis.common.collections.ImmutableCollection;
import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.collections.ImmutableListIterator;
import tekgenesis.common.collections.Seq;
import tekgenesis.common.collections.Stack;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;

import static tekgenesis.common.collections.Colls.*;

/**
 * User: emilio; Date: 12/16/11; Time: 12:47 PM;
 */

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class ImmutableTest {

    //~ Methods ......................................................................................................................................

    @Test public void collection() {
        final ImmutableCollection<Integer> immutableCollection = immutable((Collection<Integer>) LIST);

        try {
            immutableCollection.iterator().remove();
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException e) {
            assertThat(e).hasMessage(null);
        }

        assertThat(immutable(immutableCollection)).isSameAs(immutableCollection);

        assertThat(immutableCollection).contains(1);
        assertThat(immutableCollection).contains(1, 2);
        assertThat(immutableCollection.size()).isEqualTo(3);
        assertThat(immutableCollection.toString()).isEqualTo("(1, 2, 3)");
        assertThat(immutableCollection.toArray()).containsExactly(1, 2, 3);
        assertThat(immutableCollection).isNotEmpty();
    }

    @Test public void iterable() {
        final Seq<Integer> immutableIterable = immutable((Iterable<Integer>) LIST);

        try {
            immutableIterable.iterator().remove();
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException e) {
            assertThat(e).hasMessage(null);
        }

        assertThat(immutable(immutableIterable)).isSameAs(immutableIterable);
    }

    @Test public void iterator() {
        try {
            immutable(LIST.iterator()).remove();
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException e) {
            assertThat(e).hasMessage(null);
        }

        final ImmutableListIterator<Integer> immutableListIterator = immutable(LIST.listIterator());

        assertThat(immutableListIterator.hasPrevious()).isFalse();
        assertThat(immutableListIterator.next()).isEqualTo(1);
        assertThat(immutableListIterator.nextIndex()).isEqualTo(1);
        assertThat(immutableListIterator.previousIndex()).isZero();
        assertThat(immutableListIterator.previous()).isEqualTo(1);

        try {
            immutableListIterator.remove();
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException e) {
            assertThat(e).hasMessage(null);
        }

        try {
            immutableListIterator.set(10);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException e) {
            assertThat(e).hasMessage(null);
        }

        try {
            immutableListIterator.add(10);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException e) {
            assertThat(e).hasMessage(null);
        }

        final ImmutableListIterator<Integer> w = immutable(immutableListIterator);
        assertThat((Object) w).isSameAs(immutableListIterator);

        assertThat(mkString(LIST, "(", ":", ")")).isEqualTo("(1:2:3)");
        assertThat(mkString(LIST)).isEqualTo("(1, 2, 3)");

        final ImmutableListIterator<Object> emptyListIterable = emptyList().listIterator();
        assertThat(emptyListIterable.hasPrevious()).isFalse();
        assertThat(emptyListIterable.nextIndex()).isZero();
        assertThat(emptyListIterable.previousIndex()).isEqualTo(-1);
        try {
            emptyListIterable.next();
            failBecauseExceptionWasNotThrown(NoSuchElementException.class);
        }
        catch (final NoSuchElementException e) {
            assertThat(e).hasMessage(null);
        }
        try {
            emptyListIterable.previous();
            failBecauseExceptionWasNotThrown(NoSuchElementException.class);
        }
        catch (final NoSuchElementException e) {
            assertThat(e).hasMessage(null);
        }
    }  // end method iterator

    @Test public void listExceptions() {
        final ImmutableList<Integer> immutableList = immutable(LIST);
        try {
            immutableList.add(10);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException e) {
            assertThat(e).hasMessage(null);
        }

        try {
            immutableList.add(2, 10);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException e) {
            assertThat(e).hasMessage(null);
        }

        try {
            immutableList.addAll(LIST);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException e) {
            assertThat(e).hasMessage(null);
        }

        try {
            immutableList.addAll(2, LIST);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException e) {
            assertThat(e).hasMessage(null);
        }

        try {
            immutableList.remove(2);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException e) {
            assertThat(e).hasMessage(null);
        }

        try {
            immutableList.set(2, 2);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException e) {
            assertThat(e).hasMessage(null);
        }

        try {
            immutableList.listIterator(2).remove();
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException e) {
            assertThat(e).hasMessage(null);
        }

        try {
            immutableList.clear();
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException e) {
            assertThat(e).hasMessage(null);
        }

        try {
            immutableList.remove(Integer.valueOf(2));
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException e) {
            assertThat(e).hasMessage(null);
        }

        try {
            immutableList.removeAll(LIST);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException e) {
            assertThat(e).hasMessage(null);
        }

        try {
            immutableList.retainAll(LIST);
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException e) {
            assertThat(e).hasMessage(null);
        }
    }  // end method listExceptions
    @SuppressWarnings({ "ObjectEqualsNull", "EqualsWithItself" })
    @Test public void listMethods() {
        final ImmutableList<Integer> immutableList = immutable(LIST);

        assertThat(immutableList.equals(LIST)).isTrue();
        assertThat(immutableList.hashCode()).isEqualTo(LIST.hashCode());

        assertThat(immutableList.equals(listOf(1, 2))).isFalse();
        assertThat(immutableList.equals(listOf(1, 2, 3, 4))).isFalse();
        assertThat(immutableList.equals(listOf(2, 3, 4))).isFalse();
        assertThat(immutableList.equals(null)).isFalse();
        assertThat(immutableList.equals(immutableList)).isTrue();
        assertThat(immutable(listOf(1, null, 2)).hashCode()).isEqualTo(30754);

        assertThat(immutableList.contains(1)).isTrue();
        assertThat(immutableList.containsAll(listOf(1, 2))).isTrue();
        assertThat(immutableList.size()).isEqualTo(3);
        assertThat(immutableList.toString()).isEqualTo("(1, 2, 3)");
        assertThat(immutableList.subList(0, 2).toString()).isEqualTo("(1, 2)");
        assertThat(immutableList.lastIndexOf(2)).isEqualTo(1);
        assertThat(immutableList.toArray()).containsExactly(1, 2, 3);
        assertThat(immutableList.toArray(new Integer[1])).containsExactly(1, 2, 3);

        assertThat(immutable((List<String>) null)).isEmpty();
        assertThat(Colls.size(null)).isZero();
        assertThat(Colls.mkString(LIST)).isEqualTo("(1, 2, 3)");
    }

    @Test public void stackTest() {
        final Stack<Integer> stack1 = Stack.createStack(LIST);
        final Stack<Integer> s      = Stack.createStack(10);
        final Stack<Integer> stack2 = s.push(1).push(2).push(3);

        assertThat(stack1.iterator().next()).isEqualTo(3);

        assertThat(stack1.equals(LIST)).isFalse();

        assertThat(stack1.equals(stack2)).isTrue();
        assertThat(stack1.pop()).isEqualTo(3);
        assertThat(stack1.equals(stack2)).isFalse();
    }

    //~ Static Fields ................................................................................................................................

    private static final List<Integer> LIST = new ArrayList<>(listOf(1, 2, 3));
}  // end class ImmutableTest
