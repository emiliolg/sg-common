
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.jmx;

import java.io.Serializable;
import java.net.InetAddress;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static tekgenesis.common.net.Ping.ping;

/**
 * Jmx Connection info.
 */
public class JmxEndpoint implements Serializable {

    //~ Instance Fields ..............................................................................................................................

    private final InetAddress address;
    private final int         port;
    private final String      serviceName;

    //~ Constructors .................................................................................................................................

    private JmxEndpoint(@Nullable InetAddress address, int port, @NotNull String serviceName) {
        this.address     = address;
        this.port        = port;
        this.serviceName = serviceName;
    }

    //~ Methods ......................................................................................................................................

    @Override public String toString() {
        return String.format("Jmx->%s:%d/%s", address, port, serviceName);
    }

    /** @return  The Address */
    @Nullable public InetAddress getAddress() {
        return address;
    }

    /** @return  if the connection is available */
    public boolean isAvailable() {
        return address != null && ping(address, getPort());
    }

    /** returns true if the endpoint is absent. */
    public boolean isNone() {
        return false;
    }

    /** @return  the port number */
    public int getPort() {
        return port;
    }

    /** @return  the serviceName */
    public String getServiceName() {
        return serviceName;
    }

    //~ Methods ......................................................................................................................................

    /**
     * Create a new JmxConnection.
     *
     * @param   address  address
     * @param   port     port
     *
     * @return  JmxConnection
     */
    public static JmxEndpoint create(@NotNull InetAddress address, int port, @NotNull String serviceName) {
        return new JmxEndpoint(address, port, serviceName);
    }

    //~ Static Fields ................................................................................................................................

    public static final JmxEndpoint NULL = new NullJmxEndpoint();

    private static final long serialVersionUID = 7599227662062913689L;

    //~ Inner Classes ................................................................................................................................

    private static class NullJmxEndpoint extends JmxEndpoint {
        private NullJmxEndpoint() {
            super(null, 0, "");
        }

        public boolean isAvailable() {
            return false;
        }
        public boolean isNone() {
            return true;
        }

        private static final long serialVersionUID = 8559225682062913689L;
    }
}  // end class JmxEndpoint
