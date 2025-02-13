
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.collections;

import java.util.*;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.Predefined;

/**
 * A simple Stack backed by an Array.
 */
public class Stack<T> extends BaseSeq<T> {

    //~ Instance Fields ..............................................................................................................................

    private final List<T> list;

    //~ Constructors .................................................................................................................................

    /**
     * Constructs an stack containing the elements of the specified Iterable, in the order they are
     * returned.
     */
    Stack(@NotNull Iterable<T> init) {
        this(init instanceof Collection ? ((Collection<T>) init).size() : 8);
        for (final T e : init)
            push(e);
    }
    /**
     * Constructs an empty stack an initial capacity sufficient to hold the specified number of
     * elements.
     */
    Stack(int initialCapacity) {
        list = new ArrayList<>(initialCapacity);
    }

    //~ Methods ......................................................................................................................................

    /**
     * Removes all of the elements from the stack. The stack will be empty after this call returns.
     */
    public void clear() {
        list.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        return o instanceof Stack && Predefined.equalElements(this, (Stack<T>) o);
    }

    @Override public int hashCode() {
        return list.hashCode();
    }

    @NotNull @Override public ImmutableIterator<T> iterator() {
        final ListIterator<T> l = list.listIterator(list.size());
        return new ImmutableIterator<T>() {
            @Override public boolean hasNext() {
                return l.hasPrevious();
            }
            @Override public T next() {
                return l.previous();
            }
        };
    }

    /** Looks at the object at the top of this stack without removing it from the stack. */
    @NotNull public T peek() {
        return list.get(list.size() - 1);
    }

    /**
     * Removes the object at the top of this stack and returns that object as the value of this
     * function.
     *
     * @throws  NoSuchElementException  if this stack is empty
     */
    @NotNull public T pop() {
        return list.remove(list.size() - 1);
    }

    /** Adds the specified element into the top of the stack. */
    @SuppressWarnings("UnusedReturnValue")  // chaining pattern, it could be used eventually.
    public Stack<T> push(@NotNull T t) {
        list.add(t);
        return this;
    }

    /** Returns the number of elements in this stack. */
    public int size() {
        return list.size();
    }

    /** Returns <tt>true</tt> if this stack contains no elements. */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    //~ Methods ......................................................................................................................................

    /** Constructs an empty stack with an initial capacity sufficient to hold 8 elements. */
    public static <T> Stack<T> createStack() {
        return new Stack<>(8);
    }

    /** Constructs an stack with the specified initial capacity. */
    public static <T> Stack<T> createStack(int capacity) {
        return new Stack<>(capacity);
    }

    /**
     * Constructs an stack containing the elements of the specified Iterable, in the order they are
     * returned.
     */
    public static <T> Stack<T> createStack(@NotNull Iterable<T> init) {
        return new Stack<>(init);
    }
}  // end class Stack
