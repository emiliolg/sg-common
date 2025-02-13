
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.i18n;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.Predefined;
import tekgenesis.common.core.Suppliers;
import tekgenesis.common.env.context.Context;
import tekgenesis.common.logging.Logger;

import static tekgenesis.common.Predefined.ensureNotNull;
import static tekgenesis.common.Predefined.notNull;

/**
 * A wrapper over Resource Bundle with some additional functionality.
 */
@SuppressWarnings("NonJREEmulationClassesInClientCode")
public class I18nBundle {

    //~ Instance Fields ..............................................................................................................................

    private final String                                bundleName;
    @NotNull private Supplier<? extends ResourceBundle> bundleRef;
    private ClassLoader                                 classLoader;
    private Locale                                      currentBundleLocale;

    //~ Constructors .................................................................................................................................

    /** Create a new Bundle. */
    private I18nBundle(String bundleName) {
        this.bundleName     = bundleName;
        bundleRef           = Suppliers.empty();
        currentBundleLocale = null;
        classLoader         = Thread.currentThread().getContextClassLoader();
    }

    //~ Methods ......................................................................................................................................

    /** Returns true if the bundle contains a value for the specified key. */
    public boolean containsKey(@NotNull String key, @NotNull Locale locale) {
        return getBundle(locale, false).containsKey(key);
    }

    /** Returns true if the bundle exists for the specified locale. */
    public boolean existsFor(Locale locale) {
        getBundle(locale, false);
        return bundleRef != EMPTY_BUNDLE;
    }

    /** sets ClassLoader(used in plugin). */
    public I18nBundle setClassLoader(ClassLoader loader) {
        classLoader = loader;
        return this;
    }
    /** Returns the bundle name. */
    public String getName() {
        return bundleName;
    }

    /**
     * Gets an String for the given key from this resource bundle or one of its parents.
     *
     * @param   key  the key for the desired object
     *
     * @return  the object for the given or "" if not existent (A message will be logged in the last
     *          case)
     */
    @NotNull public String getString(@NotNull String key, @NotNull Locale locale) {
        return notNull(find(String.class, key, null, locale, true));
    }

    /**
     * Returns an String for the given key from this resource bundle or the specified default if not
     * present.
     *
     * @param  key  the key for the desired object
     */
    @NotNull public String getString(@NotNull String key, @NotNull String defaultValue) {
        return getString(key, defaultValue, Context.getContext().getLocale());
    }

    /**
     * Returns an String for the given key from this resource bundle or the specified default if not
     * present.
     *
     * @param  key  the key for the desired object
     */
    @NotNull public String getString(@NotNull String key, @NotNull String defaultValue, @NotNull Locale locale) {
        return notNull(find(String.class, key, defaultValue, locale, false), defaultValue);
    }

    @Nullable private <T> T find(Class<T> clazz, @NotNull String key, @Nullable T defaultValue, @NotNull Locale locale, boolean log) {
        final ResourceBundle bundle = getBundle(locale, log);

        /* Avoid failing when I have a default value */
        if (defaultValue != null && !bundle.containsKey(key)) return defaultValue;

        try {
            final Object object = bundle.getObject(key);
            if (clazz.isInstance(object)) return Predefined.cast(object);
            log(log, "Invalid type for Resource", key);
        }
        catch (final MissingResourceException r) {
            log(log, "Can't find Resource", key);
        }
        return defaultValue;
    }

    private void log(boolean log, String message, String key) {
        if (log) logger.warning(message + " : " + bundleName + (key.isEmpty() ? "" : "." + key));
    }

    /** Some basic extra caching over the bundle. */
    private ResourceBundle getBundle(Locale locale, boolean log) {
        ResourceBundle bundle = bundleRef.get();
        if (bundle != null && locale.equals(currentBundleLocale)) return bundle;
        try {
            bundle = ResourceBundle.getBundle(bundleName, locale, classLoader);
            final Reference<ResourceBundle> reference = new SoftReference<>(bundle);
            bundleRef = reference::get;
        }
        catch (final MissingResourceException e) {
            log(log, "Can't find bundle", "");
            bundleRef = EMPTY_BUNDLE;
            bundle    = bundleRef.get();
        }
        currentBundleLocale = locale;

        return ensureNotNull(bundle);
    }

    //~ Methods ......................................................................................................................................

    /**
     * Returns a I18nBundle.
     *
     * @param   clazz  the class
     *
     * @return  I18nBundle
     */
    public static I18nBundle getBundle(Class<?> clazz) {
        return getBundle(clazz.getName());
    }

    /**
     * Returns a I18nBundle.
     *
     * @param   bundleName  the bundleName
     *
     * @return  I18nBundle
     */
    public static I18nBundle getBundle(String bundleName) {
        final I18nBundle i18nBundle;
        synchronized (LOCK) {
            i18nBundle = bundleMap.computeIfAbsent(bundleName, k -> new I18nBundle(bundleName));
        }
        return i18nBundle;
    }

    //~ Static Fields ................................................................................................................................

    private static final Map<String, I18nBundle> bundleMap = new HashMap<>();
    private static final Object                  LOCK      = new Object();

    public static final String GET_METHOD = "getString";

    private static final Logger logger = Logger.getLogger(I18nBundle.class);

    private static final Object[][]                         NOTHING      = new Object[0][];
    private static final Supplier<? extends ResourceBundle> EMPTY_BUNDLE = Suppliers.fromObject(new ListResourceBundle() {
                @Override protected Object[][] getContents() {
                    return NOTHING;
                }
            });
}  // end class I18nBundle
