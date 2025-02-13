
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.collections;

import java.util.ListIterator;

/**
 * An {@link ListIterator} that is guaranteed to leave the underlying data unmodified.
 */

public abstract class ImmutableListIterator<E> extends ImmutableIterator<E> implements ListIterator<E> {

    //~ Methods ......................................................................................................................................

    @Override public void add(final E e) {
        throw new UnsupportedOperationException();
    }
    @Override public int previousIndex() {
        return nextIndex() - 1;
    }
    @Override public void set(final E e) {
        throw new UnsupportedOperationException();
    }
}
