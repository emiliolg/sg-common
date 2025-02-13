
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.context;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.env.Environment;

import static tekgenesis.common.core.Constants.DEFAULT_SCOPE;

/**
 * Context.
 */
public abstract class Context {

    //~ Methods ......................................................................................................................................

    /** Bind the given class with the given implementation class. */
    public static <T> void bind(@NotNull Class<T> source, @NotNull Class<? extends T> implementation) {
        getContext().bind(source, implementation);
    }

    /** Get Context. */
    public static ContextImpl getContext() {
        return instance;
    }

    /** Get the @link Environment associated with this context. */
    @SuppressWarnings("WeakerAccess")
    public static Environment getEnvironment() {
        return getSingleton(Environment.class);
    }

    /** Create a Configuration Object and Bind it to the current {@link Environment}. */
    public static <T> T getProperties(Class<T> clazz) {
        return getProperties(DEFAULT_SCOPE, clazz);
    }

    /** Create a Configuration Object and Bind it to the current {@link Environment}. */
    public static <T> T getProperties(String scope, Class<T> clazz) {
        return getEnvironment().get(scope, clazz);
    }

    /** Returns a singleton of the given class. */
    public static <T> T getSingleton(@NotNull Class<T> clazz) {
        return getContext().getSingleton(clazz);
    }

    //~ Static Fields ................................................................................................................................

    private static final ContextImpl instance = new ContextImpl();
}  // end class Context
