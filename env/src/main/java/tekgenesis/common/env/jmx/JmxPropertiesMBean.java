
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.jmx;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;

import javax.management.*;

import ch.qos.logback.classic.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.env.Environment;
import tekgenesis.common.env.impl.BaseEnvironment;
import tekgenesis.common.logging.Logger;
import tekgenesis.common.util.Conversions;
import tekgenesis.common.util.Reflection;

import static tekgenesis.common.Predefined.ensureNotNull;
import static tekgenesis.common.core.Constants.CANCEL;

/**
 * MBean for management of Environment Properties.
 */
public class JmxPropertiesMBean<T> implements DynamicMBean {

    //~ Instance Fields ..............................................................................................................................

    private final Class<T> clazz;

    private T defaultValue = null;

    private final BaseEnvironment environment;
    private final String          scope;
    private T                     valueEnv;
    private T                     valueTemp = null;

    //~ Constructors .................................................................................................................................

    /** Constructs a JmxPropertiesMBean. */
    public JmxPropertiesMBean(@NotNull String scope, @NotNull Class<T> clazz, @Nullable T value, @NotNull BaseEnvironment environment) {
        this.scope       = scope;
        this.clazz       = clazz;
        valueEnv         = value;
        this.environment = environment;
    }

    //~ Methods ......................................................................................................................................

    /** Applies the changes into the environment. */
    public boolean apply() {
        if (valueTemp != null) {
            environment.put(scope, valueTemp);
            valueTemp = null;
            valueEnv  = environment.get(scope, clazz);
            return true;
        }
        return false;
    }

    /** Cancels all changes. */
    public void cancel() {
        valueTemp = null;
    }

    @Override public Object invoke(String actionName, Object[] params, String[] signature)
        throws MBeanException, ReflectionException
    {
        return Reflection.invoke(this, actionName, params);
    }

    @Override public Object getAttribute(String attribute)
        throws AttributeNotFoundException, MBeanException, ReflectionException
    {
        final T val;
        if (valueTemp != null) val = valueTemp;
        else if (valueEnv != null) val = valueEnv;
        else val = getDefaultValue();
        return ensureNotNull(Reflection.getPrivateField(val, attribute)).toString();
    }

    @Override public void setAttribute(Attribute attribute)
        throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
    {
        setFieldValue(attribute, getTempValue());
    }

    @Override public final AttributeList getAttributes(String[] attributes) {
        final AttributeList result = new AttributeList(attributes.length);
        for (final String attrName : attributes) {
            try {
                final Object attrValue = getAttribute(attrName);
                result.add(new Attribute(attrName, attrValue));
            }
            catch (final Exception e) {
                // OK: attribute is not included in returned list, per spec
                logger.warning(e);
            }
        }
        return result;
    }

    @Override public final AttributeList setAttributes(AttributeList attributes) {
        final AttributeList result = new AttributeList(attributes.size());
        for (final Object attrObj : attributes) {
            // We can't use AttributeList.asList because it has side-effects
            final Attribute attr = (Attribute) attrObj;
            try {
                setAttribute(attr);
                result.add(new Attribute(attr.getName(), attr.getValue()));
            }
            catch (final Exception e) {
                // OK: attribute is not included in returned list, per spec
                logger.warning(e);
            }
        }
        return result;
    }

    public synchronized MBeanInfo getMBeanInfo() {
        final boolean              mutable  = !Environment.Utils.immutable(clazz);
        final Set<Field>           fields   = Reflection.getPublicFields(clazz);
        final MBeanAttributeInfo[] attrs    = new MBeanAttributeInfo[fields.size()];
        final Iterator<Field>      iterator = fields.iterator();
        for (int i = 0; i < attrs.length; i++) {
            final Field  field = iterator.next();
            final String name  = field.getName();
            Class<?>     type  = field.getType();
            if (!type.isPrimitive()) type = String.class;
            attrs[i] = new MBeanAttributeInfo(name, type.getName(), "Property " + name, true,  // isReadable
                    mutable,  // isWritable
                    false);   // isIs
        }
        final MBeanOperationInfo[] operations = new MBeanOperationInfo[2];
        // noinspection DuplicateStringLiteralInspection
        operations[0] = new MBeanOperationInfo("apply",
                "Applies the changes into the environment",
                null,
                Boolean.class.getName(),
                MBeanOperationInfo.ACTION);
        operations[1] = new MBeanOperationInfo(CANCEL, "Cancels all changes", null, null, MBeanOperationInfo.ACTION);

        return new MBeanInfo(getClass().getName(),
            "Property Manager MBean",
            attrs,       // attributes
            null,        // constructors
            operations,  // operations
            null);       // notifications
    }

    private T getDefaultValue() {
        if (defaultValue == null) defaultValue = environment.get("", clazz);
        return defaultValue;
    }

    private void setFieldValue(Attribute attribute, Object entryValue)
        throws ReflectionException
    {
        try {
            final Field    field = entryValue.getClass().getField(attribute.getName());
            Object         value = attribute.getValue();
            final Class<?> type  = field.getType();

            if (type.equals(Level.class)) {
                final Level currentValue = Reflection.getFieldValue(entryValue, field);
                value = Level.toLevel((String) value, currentValue);
            }
            else value = Conversions.fromString((String) value, type);
            Reflection.setFieldValue(entryValue, field, value);
        }
        catch (final NoSuchFieldException e) {
            throw new ReflectionException(e);
        }
    }  // end method setFieldValue

    private T getTempValue() {
        if (valueTemp == null) {
            valueTemp = Reflection.construct(clazz);
            Reflection.copyDeclaredFields(valueEnv != null ? valueEnv : getDefaultValue(), valueTemp);
        }
        return valueTemp;
    }

    //~ Static Fields ................................................................................................................................

    private static final Logger logger = Logger.getLogger(JmxPropertiesMBean.class);
}
