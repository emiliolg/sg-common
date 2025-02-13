
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
 * or not. What makes it difficult to implement the {@link Iterator#hasNext()} method. Using this
 * class, one just have to implement only the {@link #advance} method,</p>
 */
public abstract class AbstractIterator<T> extends ImmutableIterator<T> {

    //~ Instance Fields ..............................................................................................................................

    protected T next;

    private boolean first;

    private boolean hasNext;

    //~ Constructors .................................................................................................................................

    protected AbstractIterator() {
        first = true;
        next  = null;
    }

    //~ Methods ......................................................................................................................................

    @Override public final boolean hasNext() {
        if (first) {
            hasNext = start();
            first   = false;
        }
        return hasNext;
    }

    @Override public final T next() {
        if (!hasNext()) throw new NoSuchElementException();
        final T result = next;
        hasNext = advance();
        return result;
    }

    /**
     * Advance the input and updates the {@link #next} field. It will return <code>false</code> when
     * the input is consumed.
     *
     * @return  <code>true</code> if there are more input. <code>false</code> otherwise
     */
    protected abstract boolean advance();

    /**
     * Called to initialize the iterator, it can be overridden to return a particular 'first'
     * element.
     */
    protected boolean start() {
        return advance();
    }
}  // end class AbstractIterator
