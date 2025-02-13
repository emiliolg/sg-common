
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.Properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.Colls;
import tekgenesis.common.collections.Seq;

import static java.lang.String.format;

import static tekgenesis.common.Predefined.*;
import static tekgenesis.common.core.Constants.*;

/**
 * Component Versions.
 */
public final class Version {

    //~ Instance Fields ..............................................................................................................................

    private final Map<String, ComponentInfo> components = new HashMap<>();

    //~ Constructors .................................................................................................................................

    private Version() {
        // Sui-generis by default
        addComponent(SUI_GENERIS, getProperty(SUIGENERIS_VERSION), getProperty(SUIGENERIS_BUILD), getProperty(SUIGENERIS_BRANCH));
    }

    //~ Methods ......................................................................................................................................

    /** Add component with specified version. */
    @SuppressWarnings("WeakerAccess")
    public void addComponent(@NotNull String component, @NotNull String version, @NotNull String build, @NotNull String branch) {
        components.put(component, ComponentInfo.create(component, version, build, branch));
    }

    /**
     * Add component from the information from a input stream representing a properties file. the
     * properties we look for values are:<br/>
     * <b>build.version</b><br/>
     * <b>build.number</b><br/>
     * <b>build.branch</b><br/>
     */
    @SuppressWarnings("WeakerAccess")
    public void addComponentFromStream(@NotNull String component, @Nullable InputStream is)
        throws IOException
    {
        final Properties p = new Properties();
        p.put(BUILD_VERSION, "");
        p.put(BUILD_NUMBER, "");
        p.put(BRANCH, "");
        if (is != null) p.load(is);
        components.put(component, ComponentInfo.create(component, p.getProperty(BUILD_VERSION), p.getProperty(BUILD_NUMBER), p.getProperty(BRANCH)));
    }

    /**
     * Add all components registered in the application components file. This method looks for a
     * file in META-INF with the list of components and register them reading the corresponding
     * other file
     *
     * @throws  IOException  on any IO Error
     */
    public void addDefaultComponents()
        throws IOException
    {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final InputStream compsIS     = classLoader.getResourceAsStream(COMPONENTS_PATH);
        final Properties  compsProps  = new Properties();
        if (compsIS != null) compsProps.load(compsIS);
        final String compsList = compsProps.getProperty(COMPONENTS_KEY, "");
        if (isNotEmpty(compsList)) {
            final String[] compsFullNames = compsList.split(",");
            for (final String compFullName : compsFullNames) {
                final InputStream compIS    = classLoader.getResourceAsStream(format(COMPONENT_PATH, compFullName));
                final Properties  compProps = new Properties();
                if (compIS != null) compProps.load(compIS);
                final String application = compProps.getProperty(BUILD_APP, compFullName);
                final String version     = compProps.getProperty(BUILD_VERSION, "");
                final String number      = compProps.getProperty(BUILD_NUMBER, "");
                final String branch      = compProps.getProperty(BRANCH, "");
                components.put(application, ComponentInfo.create(application, version, number, branch));
            }
        }
    }

    @Override public String toString() {
        return getComponents().mkString("{\n", "\n", "\n}");
    }

    /** Return component info. */
    @NotNull public ComponentInfo getComponent(@NotNull final String component) {
        return ensureNotNull(components.get(component), "Component not found!");
    }

    /** @return  List of components. */
    public Seq<ComponentInfo> getComponents() {
        return Colls.seq(components.values());
    }

    private String getProperty(String property) {
        return notNull(System.getProperty(property), "");
    }

    //~ Methods ......................................................................................................................................

    /** @return  a Version */
    public static Version getInstance() {
        return INSTANCE;
    }

    //~ Static Fields ................................................................................................................................

    private static final Version INSTANCE = new Version();

    //~ Inner Classes ................................................................................................................................

    /**
     * Version Entry.
     */
    public static final class ComponentInfo implements Serializable {
        @NotNull private final String branch;
        @NotNull private final String build;
        @NotNull private final String component;
        @NotNull private final String version;

        private ComponentInfo(@NotNull String component, @NotNull String version, @NotNull String build, @NotNull String branch) {
            this.component = component;
            this.version   = version;
            this.build     = build;
            this.branch    = branch;
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ComponentInfo)) return false;
            final ComponentInfo that = (ComponentInfo) o;
            return build.equals(that.build) && component.equals(that.component) && version.equals(that.version);
        }

        @Override public int hashCode() {
            int result = component.hashCode();
            result = 31 * result + version.hashCode();
            result = 31 * result + build.hashCode();
            return result;
        }

        @Override public String toString() {
            return getComponent() + (isEmpty(getBuild()) ? " Dev" : format(" %s %s-%s", getVersion(), getBranch(), getBuild()));
        }

        /** Branch name. */
        @NotNull public String getBranch() {
            return branch;
        }

        /** Build number. */
        @NotNull public String getBuild() {
            return build;
        }

        /** Component name. */
        @NotNull public String getComponent() {
            return component;
        }

        /** Version number. */
        @NotNull public String getVersion() {
            return version;
        }

        /** Create a new Version Entry. */
        public static ComponentInfo create(@NotNull String component, @NotNull String version, @NotNull String build, @NotNull String branch) {
            return new ComponentInfo(component, version, build, branch);
        }

        private static final long serialVersionUID = -3553520054523201767L;
    }  // end class ComponentInfo
}  // end class Version
