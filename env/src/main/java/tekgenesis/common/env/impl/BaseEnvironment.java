
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.Predefined;
import tekgenesis.common.core.Option;
import tekgenesis.common.env.Environment;
import tekgenesis.common.env.jmx.JmxHelper;
import tekgenesis.common.env.jmx.JmxPropertiesMBean;
import tekgenesis.common.util.Reflection;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.core.Option.option;
import static tekgenesis.common.env.Environment.Utils.immutable;

/**
 * A Default base implementation of an Environment.
 */
public abstract class BaseEnvironment implements Environment {

    //~ Instance Fields ..............................................................................................................................

    private final Map<String, JmxPropertiesMBean<?>> mBeanMap;
    private boolean                                  productionEnv = false;

    //~ Constructors .................................................................................................................................

    /** Empty constructor. */
    @SuppressWarnings("WeakerAccess")
    protected BaseEnvironment() {
        mBeanMap = new LinkedHashMap<>();
    }

    //~ Methods ......................................................................................................................................

    /** Mark environment as Production. */
    public void asProduction() {
        productionEnv = true;
    }

    /**
     * Delete a value from the environment. Using the default name.
     *
     * @see  Utils#name(Class)
     */
    public void delete(@NotNull String scope, @NotNull Class<?> clazz) {
        delete(scope, Utils.name(clazz));
    }

    /** Delete a value from the environment. */
    @SuppressWarnings("WeakerAccess")
    public synchronized void delete(@NotNull String scope, @NotNull String name) {
        final String   key   = makeKey(scope, name);
        final Entry<?> entry = getValue(key);
        if (entry != null) {
            if (entry.listeners.isEmpty()) remove(key);
            else entry.nullEntry(key);
        }
    }

    /** Dispose environment configuration. */
    public void dispose() {}

    /**
     * Fetch the value of an entry with an specific key.
     *
     * @param   key    The entry key
     * @param   clazz  the class type
     * @param   <T>    The Value contained in the entry
     *
     * @return  The entry value
     */
    @SuppressWarnings("WeakerAccess")
    public abstract <T> T fetchValue(@NotNull String key, @NotNull Class<T> clazz);

    @NotNull @Override public final <T> T get(@NotNull Class<T> clazz) {
        return get("", clazz);
    }

    @NotNull @Override public final <T> T get(@NotNull String scope, @NotNull Class<T> clazz) {
        return get(scope, "", clazz);
    }

    @NotNull @Override public final <T> T get(@NotNull Class<T> clazz, Listener<T> listener) {
        return get("", "", clazz, listener);
    }

    @NotNull @Override public final <T> T get(@NotNull String scope, @NotNull Class<T> clazz, Listener<T> listener) {
        return get(scope, "", clazz, listener);
    }

    @NotNull
    @SuppressWarnings("LoopStatementThatDoesntLoop")
    public final <T> T get(@NotNull String scope, @NotNull String name, @NotNull Class<T> clazz, @Nullable final Listener<T> listener) {
        if (listener != null && immutable(clazz)) throw new IllegalArgumentException(LISTENER_FOR_IMMUTABLE_VALUE_MSG);

        for (final T t : find(scope, name, clazz, listener))
            return t;

        final T value = Reflection.construct(clazz);
        put("", name, value, false);
        return find(scope, name, clazz, listener).getOrFail("Illegal State");
    }

    @Override public <T> void put(@NotNull T value) {
        put("", value);
    }

    @Override public <T> void put(@NotNull String scope, @NotNull T value) {
        put(scope, "", value);
    }

    @Override public synchronized <T> void put(@NotNull String scope, @NotNull String nm, @NotNull T value) {
        put(scope, nm, value, true);
    }
    /** Reload environment data from source. */
    @SuppressWarnings("WeakerAccess")
    public abstract void refresh();

    /**
     * Returns true if the current environment is marked as Production. When the environment is
     * marked as Production several services are disable.
     */
    public boolean isProduction() {
        return productionEnv;
    }
    @Nullable protected abstract <T> Entry<T> doGetValue(@NotNull String key);
    protected abstract void doSetValue(@NotNull String key, @NotNull Entry<?> entry);
    protected abstract void remove(@NotNull String key);

    /** Returns the entry for the given key. */
    @Nullable protected <T> Entry<T> getValue(@NotNull String key) {
        final Entry<T> entry = doGetValue(key);
        if (entry != null) registerJmx(key, entry);
        return entry;
    }

    @SuppressWarnings("WeakerAccess")
    protected final <T> void setValue(@NotNull String key, @NotNull Entry<T> entry) {
        registerJmx(key, entry);
        doSetValue(key, entry);
    }

    @NotNull private synchronized <T> Option<T> find(@NotNull String scope, @NotNull String nm, @NotNull Class<T> clazz,
                                                     @Nullable Listener<T> listener) {
        final String name = nm.isEmpty() ? Utils.name(clazz) : nm;
        final String key  = makeKey(scope, name);

        // Search if it is there
        final Entry<?> e = getValue(key);
        if (e != null) {
            final Entry<T> entry = e.as(clazz);
            entry.addListener(listener);
            final T value = entry.value;
            return value == null && !scope.isEmpty() ? find("", name, clazz, null) : option(value);
        }
        // Fetch from the original store
        final T value = fetchValue(key, clazz);
        if (value != null) {
            setValue(key, new Entry<>(name, clazz, value, listener));
            return option(value);
        }

        @Nullable final Entry<T> emptyEntry;
        // If listener != null create an empty entry to hold the listener
        if (listener == null) emptyEntry = null;
        else {
            emptyEntry = new Entry<>(name, clazz, listener);
            setValue(key, emptyEntry);
        }

        // If scope is empty give up
        if (scope.isEmpty()) return Option.empty();

        // Try with an empty scope
        return find("", name, clazz, emptyEntry == null ? null : new ScopedListener<>(emptyEntry));
    }  // end method find

    @NotNull private <T> T get(@NotNull String scope, @NotNull String name, @NotNull Class<T> clazz) {
        return get(scope, name, clazz, null);
    }

    private synchronized <T> void put(@NotNull String scope, @NotNull String nm, @NotNull T value, boolean notify) {
        final Class<T> clazz = cast(value.getClass());
        final String   name  = nm.isEmpty() ? Utils.name(clazz) : nm;
        final String   key   = makeKey(scope, name);

        final Entry<?> e = getValue(key);

        final Entry<T> entry;
        if (e == null) {
            entry = new Entry<>(name, clazz, value, null);
            setValue(key, entry);
        }
        else {
            entry = e.as(clazz);
            removedScopedListener(scope, name, e);
            entry.setValue(value, notify);
            setValue(key, entry);
        }
    }

    private <T> void registerJmx(@NotNull String key, @NotNull Entry<T> entry) {
        if (!mBeanMap.containsKey(key)) {
            final String                scope = extractScope(key);
            final JmxPropertiesMBean<T> bean  = new JmxPropertiesMBean<>(scope, entry.getClazz(), entry.getValue(), this);
            mBeanMap.put(key, bean);
            JmxHelper.registerMBean(scope, "tekgenesis.configuration", entry.getName(), bean);
        }
    }

    private void removedScopedListener(String scope, String name, Entry<?> entry) {
        if (entry.value == null && !scope.isEmpty()) {
            final Entry<?> e = getValue(makeKey("", name));
            if (e != null) e.removeScopedListener(entry);
        }
    }

    //~ Methods ......................................................................................................................................

    private static String extractScope(@NotNull String key) {
        assert !Predefined.isEmpty(key);
        final int i = key.lastIndexOf('.');
        if (i == -1) return "";
        else return key.substring(0, i);
    }

    /** Constructs a key. */
    private static String makeKey(@NotNull String scope, @NotNull String name) {
        return scope.isEmpty() ? name : scope + "." + name;
    }

    //~ Static Fields ................................................................................................................................

    public static final String LISTENER_FOR_IMMUTABLE_VALUE_MSG = "Listener for immutable value";

    //~ Inner Classes ................................................................................................................................

    /**
     * An entry into the Environment.
     */
    public class Entry<T> {
        @NotNull private final Class<T> clazz;
        private final List<Listener<T>> listeners;
        @NotNull private final String   name;
        @Nullable private T             value;

        /** Creates an Entry. */
        Entry(String name, @NotNull Class<T> clazz, @Nullable Listener<T> listener) {
            this(name, clazz, null, listener);
        }

        /** Creates an Entry. */
        public Entry(@NotNull String name, @NotNull Class<T> clazz, @Nullable T value, @Nullable Listener<T> listener) {
            this.name  = name;
            this.clazz = clazz;
            this.value = value;
            listeners  = new ArrayList<>(1);
            addListener(listener);
        }

        /** Removes the scoped listener. */
        public void removeScopedListener(Entry<?> scopedEntry) {
            listeners.removeIf(l -> l instanceof ScopedListener && ((ScopedListener<?>) l).entry == scopedEntry);
        }

        /** Entry Class. */
        @NotNull public Class<T> getClazz() {
            return clazz;
        }

        /** Entry name. */
        @NotNull public String getName() {
            return name;
        }

        /** Entry value. */
        @Nullable public T getValue() {
            return value;
        }

        /** Sets the entry value. */
        public void setValue(@NotNull T value) {
            setValue(value, true);
        }

        void addListener(@Nullable Listener<T> listener) {
            if (listener != null) listeners.add(listener);
        }

        <E> Entry<E> as(Class<E> c) {
            if (!c.isAssignableFrom(clazz)) throw new IllegalArgumentException(c.getName() + " must be assignable from " + clazz.getName());
            return cast(this);
        }

        void fireListeners(@NotNull Option<T> v) {
            for (final Listener<T> listener : listeners)
                listener.onChange(v);
        }

        void nullEntry(String key) {
            if (listeners.isEmpty()) return;
            if (value == null) return;

            value = null;
            final boolean noScope = key.equals(name);
            fireListeners(noScope ? Option.empty() : find("", name, clazz, new ScopedListener<>(this)));
        }  // end method nullEntry

        void setValue(@NotNull T v, boolean notify) {
            value = v;
            if (notify) fireListeners(option(v));
        }
    }  // end class Entry

    protected static class ScopedListener<T> implements Listener<T> {
        private final Entry<T> entry;

        ScopedListener(Entry<T> e) {
            entry = e;
        }

        @Override public void onChange(@NotNull Option<T> value) {
            entry.fireListeners(value);
        }
    }
}
