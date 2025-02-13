
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.collections.MultiMap;
import tekgenesis.common.service.Headers;

/**
 * Base HTTP resource.
 */
public interface HttpResource<T> extends WithHeaders<T> {

    //~ Methods ......................................................................................................................................

    /** Add query parameter with value. */
    T param(@NotNull String parameter, @NotNull String value);

    /** Add query parameter with list of value. */
    T param(@NotNull String parameter, @NotNull Iterable<String> values);

    /** Add query parameters. */
    T params(@NotNull Map<String, Iterable<String>> params);

    /** Add query parameters. */
    T params(@NotNull MultiMap<String, String> params);

    /** Return resource headers. */
    @NotNull Headers getHeaders();
}
