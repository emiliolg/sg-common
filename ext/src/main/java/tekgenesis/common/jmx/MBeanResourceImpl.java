
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.jmx;

import java.util.HashSet;

import javax.management.AttributeList;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.invoker.exception.InvokerApplicationException;
import tekgenesis.common.invoker.exception.InvokerConnectionException;
import tekgenesis.common.invoker.exception.InvokerInvocationException;
import tekgenesis.common.invoker.metric.InvocationKeyGenerator;

import static tekgenesis.common.Predefined.cast;

/**
 * Impl.
 */
public class MBeanResourceImpl implements MBeanResource {

    //~ Instance Fields ..............................................................................................................................

    private final int connectTimeout;

    @NotNull private final JmxInvokerCommandFactory factory;
    @NotNull private final JmxInvokerImpl           invoker;
    @NotNull private final InvocationKeyGenerator   keyGenerator;
    @NotNull private final String                   objectName;

    //~ Constructors .................................................................................................................................

    MBeanResourceImpl(@NotNull JmxInvokerImpl jmxInvoker, int connectTimeout, @NotNull JmxInvokerCommandFactory factory,
                      @NotNull InvocationKeyGenerator keyGenerator, @NotNull String objectName) {
        invoker             = jmxInvoker;
        this.connectTimeout = connectTimeout;
        this.factory        = factory;
        this.keyGenerator   = keyGenerator;
        this.objectName     = objectName;
    }

    //~ Methods ......................................................................................................................................

    @Override public <R> R invoke(@NotNull String methodName, @Nullable String[] signature, @Nullable Object[] params)
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException
    {
        return cast(invoke(f -> f.invoke(createObjectName(), methodName, params, signature)).get());
    }

    @Override public HashSet<ObjectName> queryMBean(@NotNull String expr)
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException
    {
        return cast(invoke(f -> f.queryNames(new ObjectName(expr), null)).get());
    }

    @Override public <R> R getAttribute(@NotNull final String attr)
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException
    {
        return cast(invoke(f -> f.getAttribute(createObjectName(), attr)).get());
    }

    @Override public <R> R getAttributesValue(String[] attributes) {
        return cast(invoke(f -> f.getAttributes(createObjectName(), attributes)).get());
    }

    @Override public void setAttributesValue(@NotNull AttributeList attributes)
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException
    {
        invoke(f -> f.setAttributes(createObjectName(), attributes)).get();
    }

    @Override public <R> R getInfo()
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException
    {
        return cast(invoke(f -> f.getMBeanInfo(createObjectName())).get());
    }

    private ObjectName createObjectName() {
        try {
            return ObjectName.getInstance(objectName);
        }
        catch (final MalformedObjectNameException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private <R> JmxInvokerCommand<R> invoke(@NotNull JmxFunction<R> f) {
        final JmxInvocation<R> invocation = JmxInvocation.invocation(connectTimeout, keyGenerator, objectName, f);
        return factory.command(invocation).withInvoker(invoker);
    }
}  // end class MBeanResourceImpl
