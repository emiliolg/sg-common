
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.io.Serializable;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.Predefined;
import tekgenesis.common.collections.ImmutableList;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.collections.Colls.immutable;
import static tekgenesis.common.collections.Colls.listOf;

/**
 * This interface defines type-safe tuples using generics.
 *
 * <p>One of the most powerful uses of tuples is for creating keys on-the-fly. Tuples can be used in
 * maps to store and retrieve objects by a multi-valued key.</p>
 *
 * <p>For Example:</p>
 *
 * <blockquote>
 * <pre>
 Map&lt;Tuple&lt;String,Integer&gt;,Person&gt; map = ....;

 map.put(Tuple.create("DNI", "52456123"), p);
 * </pre>
 * </blockquote>
 *
 * <p>Another powerful reason for using tuples is to return multiple values from a single method.
 * </p>
 *
 * <p>For Example:</p>
 *
 * <blockquote>
 * <pre>
 return Tuple.create(division, remainder);
 * </pre>
 * </blockquote>
 */
public interface Tuple<T, U> extends Serializable {

    //~ Instance Fields ..............................................................................................................................

    long serialVersionUID = 201612080900L;

    //~ Methods ......................................................................................................................................

    /** Returns the first element in the <code>Tuple</code>. */
    @NotNull T _1();

    /** Returns the second element in the <code>Tuple</code>. */
    @NotNull U _2();

    /** Append Object and create a Tuple of a greater order. */
    default Tuple<T, U> append(Object o) {
        final List<Object> objects = cast(asList());
        return cast(tupleFromList(objects, o));
    }

    /** The arity of the tuple. */
    int arity();

    /** Returns the Tuple as a list of its elements . */
    @NotNull ImmutableList<?> asList();

    /** Returns the first element in the <code>Tuple</code>. */
    @NotNull default T first() {
        return _1();
    }

    /** Returns the second element in the <code>Tuple</code>. */
    @NotNull default U second() {
        return _2();
    }

    /** Return the last element in the tuple. */
    @NotNull Object getLast();

    //~ Methods ......................................................................................................................................

    /**
     * Invoke over a Tuple object and return it as a List (A singleton in the case the key is only
     * one field).
     */
    static ImmutableList<?> asList(@NotNull Object o) {
        if (o instanceof List) return immutable((List<?>) o);
        if (o instanceof Tuple) return ((Tuple<?, ?>) o).asList();
        return listOf(o);
    }

    /** Create a Tuple Appending to an Object. */
    static <CK> CK createAppending(Object base, Object last) {
        if (base instanceof Tuple) {
            final Tuple<?, ?> t = (Tuple<?, ?>) base;
            return cast(t.append(last));
        }
        return cast(tuple(base, last));
    }

    /**
     * Creates a 2 elements <code>Tuple.</code>
     *
     * @param   first   The first tuple element
     * @param   second  The second tuple element
     *
     * @return  A 2 elements Tuple
     */
    @NotNull static <T, U> Tuple<T, U> tuple(@NotNull T first, @NotNull U second) {
        return new Tuple2<>(first, second);
    }

    /**
     * Creates an <code>IntIntTuple.</code>
     *
     * @param   first   The first tuple element
     * @param   second  The second tuple element
     *
     * @return  An IntIntTuple
     */
    @NotNull static IntIntTuple tuple(int first, int second) {
        return new IntIntTuple(first, second);
    }

    /**
     * Creates an <code>IntIntTuple.</code>
     *
     * @param   first   The first tuple element
     * @param   second  The second tuple element
     *
     * @return  An IntIntTuple
     */
    @NotNull static IntIntTuple tuple(@NotNull Integer first, @NotNull Integer second) {
        return new IntIntTuple(first, second);
    }

    /**
     * Creates a 3 elements <code>Tuple</code> {@link Tuple3}.
     *
     * @param   first   The first tuple element
     * @param   second  The second tuple element
     * @param   third   The third tuple element
     *
     * @return  A 3 elements <code>Tuple</code> {@link Tuple3}
     */
    @NotNull static <T, U, V> Tuple3<T, U, V> tuple(@NotNull T first, @NotNull U second, @NotNull V third) {
        return new Tuple3<>(first, second, third);
    }

    /**
     * Creates a 4 elements <code>Tuple</code> {@link Tuple4}.
     *
     * @param   first   The first tuple element
     * @param   second  The second tuple element
     * @param   third   The third tuple element
     * @param   fourth  The fourth tuple element
     *
     * @return  A 4 elements <code>Tuple</code> {@link Tuple4}
     */
    @NotNull static <T, U, V, W> Tuple4<T, U, V, W> tuple(@NotNull T first, @NotNull U second, @NotNull V third, @NotNull W fourth) {
        return new Tuple4<>(first, second, third, fourth);
    }

    /** Creates a 5 elements <code>Tuple</code> {@link Tuple5}. */
    @NotNull static <T, U, V, W, X> Tuple5<T, U, V, W, X> tuple(@NotNull T first, @NotNull U second, @NotNull V third, @NotNull W fourth,
                                                                @NotNull X fifth) {
        return new Tuple5<>(first, second, third, fourth, fifth);
    }

    /** Creates a 6 elements <code>Tuple</code> {@link Tuple6}. */
    @NotNull static <T, U, V, W, X, Y> Tuple6<T, U, V, W, X, Y> tuple(@NotNull T first, @NotNull U second, @NotNull V third, @NotNull W fourth,
                                                                      @NotNull X fifth, @NotNull Y sixth) {
        return new Tuple6<>(first, second, third, fourth, fifth, sixth);
    }

    /** Creates a 7 elements <code>Tuple</code> {@link Tuple7}. */
    @NotNull static <T, U, V, W, X, Y, Z> Tuple7<T, U, V, W, X, Y, Z> tuple(@NotNull T first, @NotNull U second, @NotNull V third, @NotNull W fourth,
                                                                            @NotNull X fifth, @NotNull Y sixth, @NotNull Z seventh) {
        return new Tuple7<>(first, second, third, fourth, fifth, sixth, seventh);
    }

    /** Creates a 8 elements <code>Tuple</code> {@link Tuple8}. */
    @NotNull static <S, T, U, V, W, X, Y, Z> Tuple8<S, T, U, V, W, X, Y, Z> tuple(@NotNull S first, @NotNull T second, @NotNull U third,
                                                                                  @NotNull V fourth, @NotNull W fifth, @NotNull X sixth,
                                                                                  @NotNull Y seventh, @NotNull Z eighth) {
        return new Tuple8<>(first, second, third, fourth, fifth, sixth, seventh, eighth);
    }

    /** Creates a 9 elements <code>Tuple</code> {@link Tuple9}. */
    @NotNull
    @SuppressWarnings("MethodWithTooManyParameters")
    static <R, S, T, U, V, W, X, Y, Z> Tuple9<R, S, T, U, V, W, X, Y, Z> tuple(@NotNull R first, @NotNull S second, @NotNull T third,
                                                                               @NotNull U fourth, @NotNull V fifth, @NotNull W sixth,
                                                                               @NotNull X seventh, @NotNull Y eighth, @NotNull Z ninth) {
        return new Tuple9<>(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth);
    }

    /** Creates a 10 elements <code>Tuple</code> {@link Tuple10}. */
    @NotNull
    @SuppressWarnings("MethodWithTooManyParameters")
    static <Q, R, S, T, U, V, W, X, Y, Z> Tuple10<Q, R, S, T, U, V, W, X, Y, Z> tuple(@NotNull Q first, @NotNull R second, @NotNull S third,
                                                                                      @NotNull T fourth, @NotNull U fifth, @NotNull V sixth,
                                                                                      @NotNull W seventh, @NotNull X eighth, @NotNull Y ninth,
                                                                                      @NotNull Z tenth) {
        return new Tuple10<>(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth);
    }

    /** Creates a 2 element <code>Tuple</code> {@link Tuple2}. */
    @NotNull static <T, U> Tuple<T, U> tuple2(@NotNull T first, @NotNull U second) {
        return tuple(first, second);
    }

    /** Create a tuple from a list of elements. */
    static <E> E tupleFromList(List<?> e) {
        final int n = e.size();
        if (n < 1) throw new IllegalArgumentException("Invalid size: " + n);
        return cast(tupleFromList(e.subList(0, n - 1), e.get(n - 1)));
    }

    /** Create a tuple from a list of elements. */
    @SuppressWarnings("MethodWithMultipleReturnPoints")
    static Object tupleFromList(List<?> e, Object last) {
        final int n = e.size() + 1;
        switch (n) {
        case 1:
            return last;
        case 2:
            return tuple(e.get(0), last);
        case 3:
            return tuple(e.get(0), e.get(1), last);
        case 4:
            return tuple(e.get(0), e.get(1), e.get(2), last);
        case 5:
            return tuple(e.get(0), e.get(1), e.get(2), e.get(3), last);
        case 6:
            return tuple(e.get(0), e.get(1), e.get(2), e.get(3), e.get(4), last);
        case 7:
            return tuple(e.get(0), e.get(1), e.get(2), e.get(3), e.get(4), e.get(5), last);
        case 8:
            return tuple(e.get(0), e.get(1), e.get(2), e.get(3), e.get(4), e.get(5), e.get(6), last);
        case 9:
            return tuple(e.get(0), e.get(1), e.get(2), e.get(3), e.get(4), e.get(5), e.get(6), e.get(7), last);
        case 10:
            return tuple(e.get(0), e.get(1), e.get(2), e.get(3), e.get(4), e.get(5), e.get(6), e.get(7), e.get(8), last);
        default:
            throw Predefined.notImplemented("Tuple of " + n + " elements");
        }
    }
}  // end interface Tuple
