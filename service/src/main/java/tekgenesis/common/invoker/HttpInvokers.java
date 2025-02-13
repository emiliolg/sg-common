
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import org.jetbrains.annotations.NotNull;

/**
 * Factory class for {@link HttpInvokerImpl invokers}.
 */
public class HttpInvokers {

    //~ Constructors .................................................................................................................................

    private HttpInvokers() {}

    //~ Methods ......................................................................................................................................

    /**
     * Create multi host http invoker for specified servers (or default http invoker is a single
     * server is specified).
     */
    public static HttpInvoker invoker(@NotNull String server) {
        return new HttpInvokerImpl().withServer(server);
    }

    /**
     * Create multi host http invoker for specified servers (or default http invoker is a single
     * server is specified).
     */
    public static HttpInvoker invoker(@NotNull Strategy strategy, @NotNull String... servers) {
        if (servers.length == 1) return invoker(servers[0]);
        if (servers.length > 1) return new MultiHostHttpInvoker(strategy, servers);
        throw new IllegalArgumentException("No servers provided.");
    }
}
