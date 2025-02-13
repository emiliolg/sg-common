
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
import javax.management.ObjectName;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.invoker.exception.InvokerApplicationException;
import tekgenesis.common.invoker.exception.InvokerConnectionException;
import tekgenesis.common.invoker.exception.InvokerInvocationException;

/**
 * CallMBean interface.
 */
public interface MBeanResource {

    //~ Methods ......................................................................................................................................

    /** Invokes a mbean method. */
    <R> R invoke(@NotNull final String methodName, @Nullable final String[] signature, @Nullable final Object[] params)
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException;

    /** Query for MBeans based on expression name query. */
    HashSet<ObjectName> queryMBean(@NotNull String expr)
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException;

    /** @return  MBean attribute value */
    <R> R getAttribute(@NotNull final String attr)
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException;

    /** @return  Mbean attributes values */
    <R> R getAttributesValue(String[] attributes)
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException;

    /** Set an attribute value in MBean. */
    void setAttributesValue(@NotNull final AttributeList attributes)
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException;

    /** @return  MBeanInfo */
    <R> R getInfo()
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException;
}
