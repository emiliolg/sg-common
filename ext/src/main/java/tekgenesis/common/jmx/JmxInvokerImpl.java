
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.jmx;

import java.io.IOException;
import java.net.InetAddress;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.core.Option;
import tekgenesis.common.core.Times;
import tekgenesis.common.invoker.error.InvokerErrorHandler;
import tekgenesis.common.invoker.metric.InvocationKeyGenerator;
import tekgenesis.common.util.Files;

import static tekgenesis.common.Predefined.ensureNotNull;

/**
 * Jmx Invoker based on commands.
 */
public class JmxInvokerImpl implements JmxInvoker, JmxInvokerCommandFactory {

    //~ Instance Fields ..............................................................................................................................

    private JmxEndpoint connection;
    private int         connectTimeout;

    @NotNull private final JmxInvokerCommandFactory factory;
    @NotNull private InvocationKeyGenerator         keyGenerator = NO_KEY_GENERATOR;

    private final Option<String> threadPoolKey;

    //~ Constructors .................................................................................................................................

    /** Construct the implementation. */
    private JmxInvokerImpl() {
        connection = null;
        // noinspection MagicNumber
        connectTimeout = (int) (5 * Times.MILLIS_SECOND);

        // Specify self as default command factory
        factory = this;
        // Use default InvokerCommand pool
        threadPoolKey = Option.empty();
    }

    //~ Methods ......................................................................................................................................

    @Override public <T> JmxInvokerCommandImpl<T> command(JmxInvocation<T> invocation) {
        return new JmxInvokerCommandImpl<T>(invocation) {
            @Override protected String getThreadPoolKey() {
                return threadPoolKey.orElse(super.getThreadPoolKey());
            }
        };
    }

    public MBeanResourceImpl mbean(String objectName) {
        return new MBeanResourceImpl(this, connectTimeout, factory, keyGenerator, objectName);
    }

    @Override public String toString() {
        return connection.toString();
    }

    /** specify the service name. */
    public JmxInvoker using(@NotNull JmxEndpoint c) {
        connection = c;
        return this;
    }

    @Override public JmxInvoker withConnectTimeout(int timeout) {
        connectTimeout = timeout;
        return this;
    }

    public JmxInvoker withErrorHandler(@NotNull InvokerErrorHandler handler) {
        return this;
    }

    @Override public JmxInvoker withMetrics() {
        return withMetrics(new InvocationKeyGenerator.CamelCaseKeyGenerator());
    }

    public JmxInvoker withMetrics(@NotNull InvocationKeyGenerator generator) {
        keyGenerator = generator;
        return this;
    }

    @Override public JmxInvoker withReadTimeout(int timeout) {
        throw new UnsupportedOperationException();
    }

    MBeanServerConnection createConnection(JMXConnector connector)
        throws IOException
    {
        return connector.getMBeanServerConnection();
    }

    JMXConnector createConnector(@NotNull JmxInvokerImpl jmxInvoker)
        throws IOException
    {
        final JmxEndpoint   conn    = jmxInvoker.connection;
        final InetAddress   a       = ensureNotNull(conn.getAddress());
        final JMXServiceURL address = new JMXServiceURL(
                String.format("service:jmx:rmi:///jndi/rmi://[%s]:%d/%s", a.getHostAddress(), conn.getPort(), conn.getServiceName()));

        return JMXConnectorFactory.connect(address);
    }

    /** Disconnect. */
    @SuppressWarnings("WeakerAccess")
    void disconnect(JMXConnector connector) {
        Files.close(connector);
    }

    boolean isMetricsEnabled() {
        return keyGenerator != NO_KEY_GENERATOR;
    }

    JmxEndpoint getJmxConnection() {
        return connection;
    }

    //~ Methods ......................................................................................................................................

    /** Invoke the method. */
    public static JmxInvoker invoker(JmxEndpoint endpoint) {
        return new JmxInvokerImpl().using(endpoint);
    }

    //~ Static Fields ................................................................................................................................

    private static final InvocationKeyGenerator NO_KEY_GENERATOR = (server, path, method) -> "";
}  // end class JmxInvokerImpl
