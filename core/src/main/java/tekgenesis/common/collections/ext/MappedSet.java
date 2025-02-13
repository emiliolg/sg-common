
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.collections.ext;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static tekgenesis.common.Predefined.cast;

/**
 * A Mapper for Sets It allows the creation of a 'virtual' set changing the original set type.
 */
class MappedSet<U, V> extends AbstractSet<V> {

    //~ Instance Fields ..............................................................................................................................

    private final Function<V, U> in;
    private final Set<U>         original;
    private final Function<U, V> out;

    //~ Constructors .................................................................................................................................

    public MappedSet(Set<U> original, Function<V, U> in, Function<U, V> out) {
        this.original = original;
        this.in       = in;
        this.out      = out;
    }

    //~ Methods ......................................................................................................................................

    @Override public boolean add(V v) {
        return original.add(mapIn(v));
    }

    @Override public void clear() {
        original.clear();
    }

    @Override public boolean contains(Object o) {
        final V v = cast(o);
        return original.contains(mapIn(v));
    }

    @NotNull @Override public Iterator<V> iterator() {
        return new Iterator<V>() {
            final Iterator<U> it = original.iterator();

            @Override public boolean hasNext() {
                return it.hasNext();
            }
            @Nullable @Override public V next() {
                return mapOut(it.next());
            }
            @Override public void remove() {
                it.remove();
            }
        };
    }

    @Override public boolean remove(Object o) {
        final V v = cast(o);
        return original.remove(mapIn(v));
    }

    @Override public int size() {
        return original.size();
    }

    @Override public boolean isEmpty() {
        return original.isEmpty();
    }

    @Nullable private U mapIn(V v) {
        return v == null ? null : in.apply(v);
    }
    @Nullable private V mapOut(U u) {
        return u == null ? null : out.apply(u);
    }
}  // end class MappedSet
