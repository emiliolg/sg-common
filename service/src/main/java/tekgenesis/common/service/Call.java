
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service;

import org.jetbrains.annotations.NotNull;

import static tekgenesis.common.core.Strings.toCamelCase;

/**
 * Defines a request call and can be used to create links or fill redirect data.
 */
public class Call {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final String key;
    @NotNull private final Method method;
    @NotNull private final String url;

    //~ Constructors .................................................................................................................................

    /** Call (legacy) constructor. */
    public Call(@NotNull final Method method, @NotNull final String url) {
        this(method, url, toCamelCase(url.replaceAll("/", "_")));
    }

    /** Call constructor with invocation key. */
    public Call(@NotNull final Method method, @NotNull final String url, @NotNull final String key) {
        this.method = method;
        this.url    = url;
        this.key    = key;
    }

    //~ Methods ......................................................................................................................................

    /** Get call key. */
    @NotNull public String getKey() {
        return key;
    }

    /** Get call method. */
    @NotNull public Method getMethod() {
        return method;
    }

    /** Get call url. */
    @NotNull public String getUrl() {
        return url;
    }
}
