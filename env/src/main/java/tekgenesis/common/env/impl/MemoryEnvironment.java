
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static tekgenesis.common.Predefined.cast;

/**
 * Environment based on Memory map.
 */
public class MemoryEnvironment extends BaseEnvironment {

    //~ Instance Fields ..............................................................................................................................

    private final Map<String, Entry<?>> values;

    //~ Constructors .................................................................................................................................

    /** Empty constructor. */
    public MemoryEnvironment() {
        this(null);
    }

    /** Copy constructor. */
    public MemoryEnvironment(MemoryEnvironment env) {
        values = env == null ? new LinkedHashMap<>() : env.values;
    }

    //~ Methods ......................................................................................................................................

    /** Dispose environment configuration. */
    public void dispose() {
        // Clean the memory map
        synchronized (values) {
            values.clear();
        }
    }

    @Override public <T> T fetchValue(@NotNull String key, @NotNull Class<T> clazz) {
        return null;
    }

    /** Reload all entries. */
    public void refresh() {
        for (final Map.Entry<String, BaseEnvironment.Entry<?>> e : values.entrySet()) {
            final Entry<Object> entry = cast(e.getValue());
            final String        key   = e.getKey();
            final Object        value = fetchValue(key, entry.getClazz());
            if (value != null) entry.setValue(value);
            else entry.nullEntry(key);
        }
    }

    @Nullable @Override protected <T> Entry<T> doGetValue(@NotNull String key) {
        return cast(values.get(key));
    }

    @Override protected void doSetValue(@NotNull String key, @NotNull BaseEnvironment.Entry<?> entry) {
        values.put(key, entry);
    }

    @Override protected void remove(@NotNull String key) {
        values.remove(key);
    }
}  // end class MemoryEnvironment
