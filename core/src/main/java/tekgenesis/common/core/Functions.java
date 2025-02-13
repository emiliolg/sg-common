
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.util.Map;
import java.util.function.Function;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.core.Tuple.tuple;

/**
 * Some Utility Functions.
 */
public interface Functions {

    //~ Instance Fields ..............................................................................................................................

    /** format a Map.Entry as an String. */
    Function<Map.Entry<?, ?>, String> MAP_ENTRY_TO_STRING = value -> value.getKey() + ":" + value.getValue();

    /** Parse an string as an integer. */
    Function<String, Integer> PARSE_INT = Integer::parseInt;

    //~ Methods ......................................................................................................................................

    /** Returns a Function that converts an array to a tuple . */
    static <U, V> Function<Object[], Tuple<U, V>> arrayToTuple() {
        return a -> {
                   final U first  = cast(a[0]);
                   final V second = cast(a[1]);
                   return tuple(first, second);
               };
    }

    /** Returns a Function that converts an array to a tuple . */
    static <U, V, W> Function<Object[], Tuple3<U, V, W>> arrayToTuple3() {
        return a -> tuple(cast(a[0]), cast(a[1]), cast(a[2]));
    }

    /** Returns a function that fails always. throwing Unsupported operation exception . */
    static Function<?, ?> fail() {
        return value -> { throw new UnsupportedOperationException(); };
    }

    /** Returns a Function that converts to String a value . */
    static <T> Function<T, String> mkString() {
        return String::valueOf;
    }

    /** Returns a Function that always returns null. */
    static <U, V> Function<U, V> nullFunction() {
        return u -> null;
    }

    /** Returns a Function that returns the first element of an array . */
    static <T> Function<Object[], T> scalar() {
        return value -> cast(value[0]);
    }

    /**
     * Returns a Function that splits a string in a tuple of 2 elements using the specified char.
     */
    static Function<String, Tuple<String, String>> stringToTuple(final String sep) {
        return value -> Strings.splitToTuple(value, sep);
    }
}  // end class Functions
