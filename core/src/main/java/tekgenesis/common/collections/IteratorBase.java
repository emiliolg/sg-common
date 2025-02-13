
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class provides a skeletal implementation of the {@link Iterator} interface.
 *
 * <p>Many data sources needs to actually read the data before discovering if there is a next item
 * or not. With you just implement hasNext and leave the next value in the protected value. Only
 * work for iterators that return not nullable values.</p>
 */
public abstract class IteratorBase<T> extends ImmutableIterator<T> {

    //~ Instance Fields ..............................................................................................................................

    protected boolean nextExists = false;

    protected T nextValue = null;

    //~ Methods ......................................................................................................................................

    @Override public T next() {
        if (!nextExists && !hasNext()) throw new NoSuchElementException();
        nextExists = false;
        return nextValue;
    }
}
