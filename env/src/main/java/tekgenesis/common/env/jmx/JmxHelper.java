
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.jmx;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.Predefined;
import tekgenesis.common.core.Constants;
import tekgenesis.common.logging.Logger;

/**
 * JMX Helper.
 */
public class JmxHelper {

    //~ Constructors .................................................................................................................................

    private JmxHelper() {}

    //~ Methods ......................................................................................................................................

    /**
     * Create a ObjectName.
     *
     * @param   resourceName  the resource name
     * @param   domain        the domain
     * @param   type          the type
     *
     * @return  a jmx object name
     */
    @SuppressWarnings("WeakerAccess")
    public static ObjectName createObjectName(@NotNull String resourceName, @NotNull String domain, @NotNull String type)
        throws MalformedObjectNameException
    {
        final String name = Predefined.isEmpty(resourceName) ? Constants.DEFAULT : resourceName;
        return new ObjectName(domain + ":Name=" + name + ",type=" + type);
    }

    /**
     * Registers a JMX Mbean.
     *
     * @param  resourceName  the resource name
     * @param  domain        the domain
     * @param  type          the type
     * @param  object        the MBean object
     */
    public static void registerMBean(@NotNull String resourceName, @NotNull String domain, @NotNull String type, @NotNull Object object) {
        final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        registerMBean(mbs, resourceName, domain, type, object);
    }

    /** Registers a JMX Mbean. */
    public static void registerMBean(final MBeanServer mbs, @NotNull String resourceName, @NotNull String domain, @NotNull String type,
                                     @NotNull Object object) {
        if (mbs != null) {
            try {
                final ObjectName objectName = createObjectName(resourceName, domain, type);
                if (!isRegistered(objectName)) mbs.registerMBean(object, objectName);
            }
            catch (final Exception e) {
                logger.error(e);
            }
        }
        else logger.error("Unable to obtain the PlatformMBeanserver");
    }

    /** Un-Registers a JMX Mbean. */
    public static void unregisterMBean(@NotNull String resourceName, @NotNull String domain, @NotNull String type) {
        try {
            final ObjectName  objectName = createObjectName(resourceName, domain, type);
            final MBeanServer mbs        = ManagementFactory.getPlatformMBeanServer();
            if (isRegistered(objectName)) mbs.unregisterMBean(objectName);
        }
        catch (final Exception e) {
            logger.error(e);
        }
    }

    /**
     * @param   objectName  the object name
     *
     * @return  true or false if the object name is already registered
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean isRegistered(@NotNull ObjectName objectName) {
        final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        return mbs.isRegistered(objectName);
    }

    //~ Static Fields ................................................................................................................................

    public static final String CLUSTER_DOMAIN = "tekgenesis.cluster";

    private static final Logger logger = Logger.getLogger(JmxHelper.class);
}  // end class JmxHelper
