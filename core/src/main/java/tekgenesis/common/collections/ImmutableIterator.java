
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

/**
 * An {@link Iterator} that is guaranteed to leave the underlying data unmodified.
 */
public abstract class ImmutableIterator<E> implements Iterator<E> {

    //~ Constructors .................................................................................................................................

    /** Constructor for use by subclasses. */
    protected ImmutableIterator() {}

    //~ Methods ......................................................................................................................................

    @Override public final void remove() {
        throw new UnsupportedOperationException();
    }
}
