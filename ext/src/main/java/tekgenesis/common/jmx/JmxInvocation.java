
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.jmx;

import java.net.InetAddress;
import java.net.SocketTimeoutException;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.core.Option;
import tekgenesis.common.core.Times;
import tekgenesis.common.invoker.exception.InvokerConnectionException;
import tekgenesis.common.invoker.metric.InvocationKeyGenerator;
import tekgenesis.common.invoker.metric.InvocationMetrics;
import tekgenesis.common.service.Method;

import static tekgenesis.common.core.DateTime.currentTimeMillis;
import static tekgenesis.common.core.Option.*;
import static tekgenesis.common.net.Ping.ping;

/**
 * JmxInvocation.
 */
class JmxInvocation<T> {

    //~ Instance Fields ..............................................................................................................................

    @NotNull JmxFunction<T> f;

    @NotNull private final Option<Integer>        connectTimeout;
    @NotNull private final Option<String>         key;
    @NotNull private final InvocationKeyGenerator keyGenerator;
    @NotNull private final String                 objectName;

    //~ Constructors .................................................................................................................................

    @SuppressWarnings("ConstructorWithTooManyParameters")
    private JmxInvocation(int connectionTimeout, @NotNull InvocationKeyGenerator keyGenerator, @NotNull String objectName,
                          @NotNull JmxFunction<T> f) {
        this.keyGenerator = keyGenerator;
        connectTimeout    = some(connectionTimeout);
        key               = empty();
        this.objectName   = objectName;
        this.f            = f;
    }

    //~ Methods ......................................................................................................................................

    /** Execute invocation with given invoker. */
    T invokeUsing(@NotNull JmxInvokerImpl invoker) {
        return invoke(invoker, f);
    }

    private T invoke(JmxInvokerImpl invoker, @NotNull JmxFunction<T> function) {
        final Option<InvocationMetrics> metrics        = option(getMetricsInstance(invoker, objectName));
        final boolean                   metricsEnabled = invoker.isMetricsEnabled();

        final JmxEndpoint jmxEndpoint = invoker.getJmxConnection();

        final InetAddress address = jmxEndpoint.getAddress();
        if (jmxEndpoint.isNone() || address == null ||
            !ping(address, jmxEndpoint.getPort(), connectTimeout.orElse((int) (5 * Times.MILLIS_SECOND))))
        {
            final JmxException exception = new JmxException(String.format("Jmx Connection is not available (%s)", invoker.toString()));
            if (metricsEnabled) registerExceptionExecution(metrics.get(), exception);
            throw exception;
        }

        final T response;

        try(final JMXConnector connector = invoker.createConnector(invoker)) {
            final MBeanServerConnection connection = invoker.createConnection(connector);

            final long invocationTime = currentTimeMillis();

            response = function.apply(connection);

            if (metricsEnabled) registerSuccessExecution(metrics.get(), invocationTime);
        }
        catch (final Exception e) {
            if (metricsEnabled) registerExceptionExecution(metrics.get(), e);
            throw new InvokerConnectionException(e);
        }

        return response;
    }  // end method invoke

    private void registerExceptionExecution(@NotNull InvocationMetrics metrics, Exception exception) {
        if (exception instanceof SocketTimeoutException) metrics.markTimeout();
        else metrics.markFailure();
    }

    private void registerSuccessExecution(@NotNull InvocationMetrics metrics, long invocationStartTime) {
        metrics.addInvocationExecutionTime(System.currentTimeMillis() - invocationStartTime);
        metrics.markSuccess();
    }

    @Nullable private InvocationMetrics getMetricsInstance(@NotNull JmxInvokerImpl invoker, @NotNull String name) {
        if (invoker.isMetricsEnabled()) {
            final String commandGroup = invoker.getJmxConnection().toString();
            // TODO CREATE OWN KeyGenerator
            final String metricsKey = key.orElse(keyGenerator.key(commandGroup, name, Method.PUT));
            return InvocationMetrics.getOrCreateInstance(metricsKey, commandGroup);
        }
        return null;
    }

    //~ Methods ......................................................................................................................................

    static <T> JmxInvocation<T> invocation(int connectionTimeout, @NotNull InvocationKeyGenerator keyGenerator, @NotNull String objectName,
                                           @NotNull JmxFunction<T> f) {
        return new JmxInvocation<>(connectionTimeout, keyGenerator, objectName, f);
    }
}  // end class JmxInvocation
