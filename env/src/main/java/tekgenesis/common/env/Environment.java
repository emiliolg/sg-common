
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env;

import javax.inject.Named;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.core.Option;
import tekgenesis.common.core.Strings;

/**
 * PropertyProvider interface.
 */
public interface Environment {

    //~ Methods ......................................................................................................................................

    /** Mark environment as Production. */
    void asProduction();

    /**
     * Delete a value from the environment. Using the default name.
     *
     * @see  Utils#name(Class)
     */
    void delete(@NotNull String scope, @NotNull Class<?> clazz);

    /** Delete a value from the environment. */
    void delete(@NotNull String scope, @NotNull String name);

    /** Dispose environment configuration. */
    void dispose();

    /**
     * Returns an instance of the specified class with the values updated to the Environment ones.
     *
     * @see  Environment#get(String, String, Class, Listener)
     */
    @NotNull <T> T get(@NotNull Class<T> clazz);

    /**
     * Returns an instance of the specified class with the values updated to the Environment ones.
     *
     * @see  Environment#get(String, String, Class, Listener)
     */
    @NotNull <T> T get(@NotNull String scope, @NotNull Class<T> clazz);

    /**
     * Returns an instance of the specified class with the values updated to the Environment ones.
     *
     * @see  Environment#get(String, String, Class, Listener)
     */
    @NotNull <T> T get(@NotNull Class<T> clazz, @Nullable Listener<T> listener);
    /**
     * Returns an instance of the specified class with the values updated to the Environment ones.
     *
     * @see  Environment#get(String, String, Class, Listener)
     */
    @NotNull <T> T get(@NotNull String scope, @NotNull Class<T> clazz, @Nullable Listener<T> listener);

    /**
     * Returns an instance of the specified class with the values updated to the Environment ones.
     * If the class is mutable the values will be updated under a change of the Environment ones. A
     * listener can be set to be called when the instance is modify.
     *
     * @param  scope     The scope to prefix the name of the variable (If not empty) when looking
     *                   into the environment.
     * @param  name      The name of the variable. If empty then {@link Utils#name(Class)} will be
     *                   used as the name).
     * @param  clazz     The Class of the object to be returned.
     * @param  listener  The listener to be invoked when the Environment values are modified.
     */
    @NotNull <T> T get(@NotNull String scope, @NotNull String name, @NotNull Class<T> clazz, @Nullable Listener<T> listener);

    /**
     * Add a value to the environment. Using the default name based on the class and empty scope
     *
     * @see  Utils#name(Class)
     */
    <T> void put(@NotNull T value);

    /**
     * Add a value to the environment. Using the default name based on the class.
     *
     * @see  Utils#name(Class)
     */
    <T> void put(@NotNull String scope, @NotNull T value);

    /** Add a value to the environment. */
    <T> void put(@NotNull String scope, @NotNull String name, @NotNull T value);

    /**
     * Returns true if the current environment is marked as Production. When the environment is
     * marked as Production several services are disable.
     */
    boolean isProduction();

    //~ Inner Interfaces .............................................................................................................................

    /**
     * Listen to changes in the property bundle.
     */
    interface Listener<T> {
        /** Value changes. */
        @SuppressWarnings("AbstractMethodWithMissingImplementations")  // Bug in Intellij
        void onChange(@NotNull Option<T> value);
    }

    //~ Inner Classes ................................................................................................................................

    /**
     * Utility methods.
     */
    class Utils {
        private Utils() {}

        /** Returns true if the PropertySet is mutable (changes can be processed on runtime). */
        public static boolean immutable(Class<?> clazz) {
            return clazz.getAnnotation(Mutable.class) == null;
        }

        /** Returns the external name of the Bundle. */
        public static String name(Class<?> clazz) {
            final Named annotation = getNamed(clazz);
            if (annotation != null) {
                final String name = annotation.value();
                if (!name.isEmpty()) return name;
            }

            String nm = clazz.getSimpleName();
            // Remove default extension names
            for (final String ext : extensions) {
                if (nm.endsWith(ext)) {
                    nm = nm.substring(0, nm.length() - ext.length());
                    break;
                }
            }
            return Strings.deCapitalizeFirst(nm);
        }

        private static Named getNamed(Class<?> clazz) {
            Class<?> currentClazz = clazz;
            while (currentClazz != null) {
                final Named annotation = currentClazz.getAnnotation(Named.class);
                if (annotation != null) return annotation;

                currentClazz = currentClazz.getSuperclass();
            }

            return null;
        }

        @SuppressWarnings("DuplicateStringLiteralInspection")
        private static final String[] extensions = { "Props", "Properties", "Config", "Cfg" };
    }  // end class Utils
}  // end interface Environment
