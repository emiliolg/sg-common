
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.core.Option;
import tekgenesis.common.env.Environment;
import tekgenesis.common.env.impl.PropertiesEnvironment;
import tekgenesis.common.util.Reflection;

import static tekgenesis.common.Predefined.*;

/**
 * The default implementation of the context.
 */

public class ContextImpl {

    //~ Instance Fields ..............................................................................................................................

    private final Map<Class<?>, Class<?>> binding;
    private final ThreadLocal<String>     currentFqn;
    private final ThreadLocal<String>     host;
    private final ThreadLocal<String>     lcid;  // Life cycle id
    private final ThreadLocal<Locale>     locale;
    private final ThreadLocal<String>     path;
    private final Map<Class<?>, Object>   singleton;
    private final Map<String, Object>     variables;

    //~ Constructors .................................................................................................................................

    ContextImpl() {
        binding   = new HashMap<>();
        variables = new HashMap<>();
        singleton = new HashMap<>();

        locale     = new ThreadLocal<>();
        lcid       = new ThreadLocal<>();
        host       = new ThreadLocal<>();
        path       = new ThreadLocal<>();
        currentFqn = new ThreadLocal<>();

        // Todo Replace this.... This is totally wired !!!
        bind(Environment.class, PropertiesEnvironment.class);
    }

    //~ Methods ......................................................................................................................................

    /** Register the implementation for the given source. */
    public <T> void bind(@NotNull Class<T> source, @NotNull Class<? extends T> implementation) {
        binding.put(source, implementation);
    }

    /** Clean up the context (nearly...). */
    public void clean() {
        binding.clear();
        // registry.clear();
        singleton.clear();
    }
    /** Returns if the given class has a binding in this context. */
    public boolean hasBinding(@NotNull Class<?> source) {
        return binding.containsKey(source);
    }

    /** Returns a new Instance for the given clazz. */
    public <T> T newInstance(@NotNull Class<T> clazz) {
        final Class<?> implementation = binding.get(clazz);
        if (implementation == null) throw new RuntimeException("Class '" + clazz.getCanonicalName() + "' not registered.");
        return cast(Reflection.construct(implementation));
    }

    /** Un-bind the implementation for the given source. */
    @SuppressWarnings("UnusedReturnValue")
    public <T> Class<? extends T> unbind(@NotNull Class<T> source) {
        final Class<? extends T> implementation = cast(binding.remove(source));
        singleton.remove(source);
        return implementation;
    }

    /** Gets the current history form fully qualified name. */
    public String getCurrentHistoryForm() {
        return currentFqn.get();
    }

    /** Sets current history form fully qualified name. */
    public void setCurrentHistoryForm(@NotNull final String fqn) {
        currentFqn.set(fqn);
    }

    /** Returns the current host. */
    public String getHost() {
        return host.get();
    }

    /** Sets the current host. */
    public void setHost(String remoteHost) {
        host.set(remoteHost);
    }

    /** Return the current thread life cycle id (if defined). */
    public Option<String> getLifeCycleId() {
        return option(lcid.get());
    }

    /** Set the current thread life cycle id (if any). */
    public void setLifeCycleId(@Nullable String id) {
        lcid.set(id);
    }

    /** Return the current thread locale. */
    public Locale getLocale() {
        final Locale l = locale.get();
        return l == null ? Locale.getDefault() : l;
    }

    /** Set the current thread locale. */
    public void setLocale(Locale l) {
        locale.set(l);
    }

    /** Return current path to be used in external navigation action. */
    public String getPath() {
        return notEmpty(path.get(), "/");
    }

    /** Set current path to be used in external navigation action. */
    public void setPath(final String p) {
        path.set(p);
    }

    /** Returns a singleton of the given class. */
    public <T> T getSingleton(@NotNull Class<T> clazz) {
        final T t = clazz.cast(singleton.get(clazz));
        return t != null ? t : initialize(clazz);
    }

    /** Sets a singleton of a given class. Returns the previous value */
    @Nullable
    @SuppressWarnings("UnusedReturnValue")  // it could be used in future uses.
    public synchronized <T> T setSingleton(@NotNull Class<T> clazz, T value) {
        if (!hasBinding(clazz)) {
            final Class<T> c = cast(value.getClass());
            bind(clazz, c);
        }
        final Object o = singleton.put(clazz, value);
        return o == null ? null : clazz.cast(o);
    }

    /** Gets the context value of the given variable key. */
    public <T> T getVariable(@NotNull String key) {
        return cast(variables.get(key));
    }

    /** Sets the given variable key with the given variable value in the context. */
    public void setVariable(@NotNull String key, @Nullable Object value) {
        variables.put(key, value);
    }

    private synchronized <T> T initialize(Class<T> clazz) {
        final Object o = singleton.get(clazz);
        if (o != null) return cast(o);

        final T t = newInstance(clazz);
        singleton.put(clazz, t);
        return t;
    }
}  // end class ContextImpl
