
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.core.Constants;
import tekgenesis.common.logging.Logger;
import tekgenesis.common.util.Conversions;

import static java.lang.reflect.Modifier.isStatic;

import static tekgenesis.common.env.i18n.CommonMessages.COMMON_MSGS;
import static tekgenesis.common.util.Reflection.construct;
import static tekgenesis.common.util.Reflection.setFieldValue;

/**
 * A Properties based Environment.
 */
public class PropertiesEnvironment extends MemoryEnvironment {

    //~ Instance Fields ..............................................................................................................................

    @Nullable private final File            file;
    private final SortedMap<String, String> properties;

    //~ Constructors .................................................................................................................................

    /** Empty constructor. */
    public PropertiesEnvironment() {
        this(getPropertiesFile());
    }

    /** Empty constructor. */
    public PropertiesEnvironment(MemoryEnvironment mem) {
        this(null, mem);
    }

    /** Constructor from Properties file. */
    public PropertiesEnvironment(@Nullable File propFile) {
        this(propFile, null);
    }

    private PropertiesEnvironment(@Nullable File propFile, @Nullable MemoryEnvironment env) {
        super(env);
        file       = propFile;
        properties = new TreeMap<>();
        loadProperties(propFile);
        loadProperties(System.getProperties());
    }

    //~ Methods ......................................................................................................................................

    public <T> T fetchValue(@NotNull String key, @NotNull Class<T> clazz) {
        if (Conversions.isConvertible(clazz)) return Conversions.fromString(properties.get(key), clazz);
        final Map<String, String> m = subMap(properties, key);
        return m.isEmpty() ? null : buildObject(clazz, m, key + ".");
    }

    /** Reload the properties file. */
    public void refresh() {
        loadProperties(file);
        loadProperties(System.getProperties());
        super.refresh();
    }

    private <T> T buildObject(Class<T> clazz, Map<String, String> map, String prefix) {
        final T            result       = construct(clazz);
        @Nullable Class<?> currentClazz = clazz;
        while (currentClazz != null) {
            for (final Field field : currentClazz.getDeclaredFields()) {
                if (!isStatic(field.getModifiers())) {
                    final String fieldName = field.getName();
                    final String value     = map.get(prefix + fieldName);
                    if (value == null) logger.debug(COMMON_MSGS.fieldNotFound(fieldName));
                    else setFieldValue(result, field, Conversions.fromString(value, field.getType()));
                }
            }

            currentClazz = currentClazz.getSuperclass();
            if (!tekgenesis.common.env.Properties.class.isAssignableFrom(currentClazz)) currentClazz = null;
        }

        return result;
    }  // end method buildObject

    private void loadProperties(File propFile) {
        try {
            if (propFile != null && propFile.exists()) {
                final Properties p = new Properties();
                p.load(new FileReader(propFile));
                loadProperties(p);
            }
        }
        catch (final IOException e) {
            logger.error(e);
        }
    }

    @SuppressWarnings("TypeMayBeWeakened")
    private void loadProperties(Properties ps) {
        for (final Map.Entry<Object, Object> e : ps.entrySet()) {
            final Object key   = e.getKey();
            final Object value = e.getValue();
            if (key instanceof String) {
                if (value instanceof String) properties.put((String) key, (String) value);
                else
                    logger.warning(
                        String.format("Properties value %s for key %s is not a String (%s)", value.toString(), key, value.getClass().getName()));
            }
            else logger.warning(String.format("Properties key %s is not a String (%s)", key.toString(), key.getClass().getName()));
        }
    }

    /**
     * A sub map of all the entries starting with path, because "." is the separator "/' is used as
     * the ceiling because is the next character in the ascii table.
     */
    private Map<String, String> subMap(SortedMap<String, String> map, String path) {
        return map.subMap(path, path + "/");
    }

    //~ Methods ......................................................................................................................................

    private static File getPropertiesFile() {
        return new File(System.getProperty(Constants.SUIGEN_PROPS, DEFAULT_CONFIG_FILE));
    }

    //~ Static Fields ................................................................................................................................

    private static final String DEFAULT_CONFIG_FILE = "SuiGeneris.properties";

    private static final Logger logger = Logger.getLogger(PropertiesEnvironment.class);
}  // end class PropertiesEnvironment
