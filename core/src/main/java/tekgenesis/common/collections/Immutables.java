
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.collections;

import java.util.NoSuchElementException;

import org.jetbrains.annotations.NonNls;

/**
 * Immutable Singletons.
 */
public class Immutables {

    //~ Constructors .................................................................................................................................

    private Immutables() {}

    //~ Static Fields ................................................................................................................................

    @NonNls public static final String EMPTY_ITERATOR_MSG = "Empty Iterator";

    static final ImmutableIterator<Object> EMPTY_ITERATOR = new ImmutableIterator<Object>() {
            @Override public boolean hasNext() {
                return false;
            }
            @Override public Object next() {
                throw new NoSuchElementException(EMPTY_ITERATOR_MSG);
            }
        };

    static final Seq<Object> EMPTY_ITERABLE = Seq.createSeq(() -> EMPTY_ITERATOR);

    static final ImmutableListIterator<Object> EMPTY_LIST_ITERATOR = new ImmutableListIterator<Object>() {
            @Override public boolean hasNext() {
                return false;
            }
            @Override public boolean hasPrevious() {
                return false;
            }
            @Override public Object next() {
                throw new NoSuchElementException();
            }
            @Override public int nextIndex() {
                return 0;
            }
            @Override public Object previous() {
                throw new NoSuchElementException();
            }
        };

    static final ImmutableList<Object> EMPTY_LIST = new ImmutableList.EmptyList<>();
}  // end class Immutables
