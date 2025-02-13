
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.metric;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is a circular array acting as a FIFO queue.
 */
class BucketCircularArray<T> implements Iterable<T> {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final Function<Integer, T[]> arrayCreator;

    private final int                        length;  // We don't resize, we always stay the same
    private final int                        numBuckets;
    private final AtomicReference<ListState> state;

    //~ Constructors .................................................................................................................................

    BucketCircularArray(int size, @NotNull Function<Integer, T[]> arrayCreator) {
        this.arrayCreator = arrayCreator;
        // + 1 as extra room for the add/remove;
        final AtomicReferenceArray<T> bs = new AtomicReferenceArray<>(size + 1);
        state      = new AtomicReference<>(new ListState(bs, 0, 0));
        length     = bs.length();
        numBuckets = size;
    }

    //~ Methods ......................................................................................................................................

    /**
     * Returns an iterator on a copy of the internal array so that the iterator won't fail by
     * buckets being added/removed concurrently.
     */
    public Iterator<T> iterator() {
        return Collections.unmodifiableList(Arrays.asList(getArray())).iterator();
    }

    void addLast(T o) {
        final ListState currentState = state.get();
        // Create new version of state (what we want it to become)
        state.compareAndSet(currentState, currentState.addBucket(o));
    }

    void clear() {
        while (true) {
            final ListState current  = state.get();
            final ListState newState = current.clear();
            if (state.compareAndSet(current, newState)) return;
        }
    }

    @Nullable T peekLast() {
        return state.get().tail();
    }

    int size() {
        // The size can also be worked out each time as: return (tail + data.length() - head) % data.length();
        return state.get().size;
    }

    T[] getArray() {
        return state.get().getArray();
    }

    //~ Inner Classes ................................................................................................................................

    /**
     * Immutable object that is atomically set every time the state of the BucketCircularArray
     * changes.
     */
    private class ListState {
        private final AtomicReferenceArray<T> data;
        private final int                     head;
        private final int                     size;
        private final int                     tail;

        private ListState(AtomicReferenceArray<T> data, int head, int tail) {
            this.head = head;
            this.tail = tail;
            if (head == 0 && tail == 0) size = 0;
            else size = (tail + length - head) % length;
            this.data = data;
        }

        private ListState addBucket(T b) {
            /* We could in theory have 2 threads addBucket concurrently and this compound operation would interleave. */
            data.set(tail, b);
            return incrementTail();
        }

        private ListState clear() {
            return new ListState(new AtomicReferenceArray<>(length), 0, 0);
        }

        // The convert() method takes a logical index (as if head was always 0) and calculates the index within elementData
        private int convert(int index) {
            return (index + head) % length;
        }

        private ListState incrementTail() {
            /* if incrementing results in growing larger than 'length' which is the max we should be at, then also increment head (equivalent of
             * removeFirst but done atomically) */
            if (size == numBuckets)
            // increment tail and head
            return new ListState(data, (head + 1) % length, (tail + 1) % length);
            else
            // increment only tail
            return new ListState(data, head, (tail + 1) % length);
        }

        @Nullable private T tail() {
            if (size == 0) return null;
            else
            // we want to get the last item, so size()-1
            return data.get(convert(size - 1));
        }

        private T[] getArray() {
            /*
             * This isn't technically thread-safe since it requires multiple reads on something that can change
             * but since we never clear the data directly, only increment/decrement head/tail we would never get a NULL
             * just potentially return stale data which we are okay with doing
             */
            final List<T> array = new ArrayList<>();
            for (int i = 0; i < size; i++)
                array.add(data.get(convert(i)));
            return array.toArray(arrayCreator.apply(size));
        }
    }  // end class ListState
}  // end class BucketCircularArray
