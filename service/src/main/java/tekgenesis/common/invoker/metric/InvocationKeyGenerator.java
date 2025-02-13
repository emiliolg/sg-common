
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker.metric;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.service.Method;

import static tekgenesis.common.core.Strings.toCamelCase;

/**
 * Key generator for invocations.
 */
public interface InvocationKeyGenerator {

    //~ Methods ......................................................................................................................................

    /** Generate key for given parameters. */
    @NotNull String key(@NotNull String server, @NotNull String path, @NotNull Method method);

    //~ Inner Classes ................................................................................................................................

    /**
     * CamelCase key generator based on path only (used as default).
     */
    class CamelCaseKeyGenerator implements InvocationKeyGenerator {
        @NotNull @Override public String key(@NotNull String server, @NotNull String path, @NotNull Method method) {
            return toCamelCase(path.replaceAll("/", "_"));
        }
    }
}
