
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

import org.junit.Test;

import tekgenesis.common.collections.Stack;

import static org.assertj.core.api.Assertions.assertThat;

import static tekgenesis.common.collections.Colls.listOf;

/**
 * User: emilio; Date: 12/16/11; Time: 12:47 PM;
 */

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class StackTest {

    //~ Methods ......................................................................................................................................

    @Test public void stackTest() {
        final Stack<Integer> stack1 = Stack.createStack(l);
        final Stack<Integer> stack2 = Stack.createStack(l);
        final Stack<Integer> stack3 = Stack.createStack();
        stack3.push(1).push(2).push(3);

        assertThat(stack1.iterator().next()).isEqualTo(3);

        assertThat(stack1.equals(l)).isFalse();

        assertThat(stack1.equals(stack2)).isTrue();
        assertThat(stack3.equals(stack1)).isTrue();
        assertThat(stack1.pop()).isEqualTo(3);

        assertThat(stack1.equals(stack2)).isFalse();

        assertThat(stack2.hashCode()).isEqualTo(stack3.hashCode());

        assertThat(stack1.isEmpty()).isFalse();
        assertThat(stack1.peek()).isEqualTo(2);
        assertThat(stack1.pop()).isEqualTo(2);
        assertThat(stack1.size()).isEqualTo(1);
        stack1.push(4);
        assertThat(stack1.peek()).isEqualTo(4);
        stack1.clear();
        assertThat(stack1.isEmpty()).isTrue();
    }

    //~ Static Fields ................................................................................................................................

    private static final List<Integer> l = new ArrayList<>(listOf(1, 2, 3));
}
