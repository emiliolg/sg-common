
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.lang.reflect.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.IteratorBase;
import tekgenesis.common.core.Constants;
import tekgenesis.common.core.Option;
import tekgenesis.common.exception.ApplicationException;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isStatic;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.Predefined.option;
import static tekgenesis.common.collections.Colls.emptyIterator;
import static tekgenesis.common.collections.ImmutableList.fromArray;
import static tekgenesis.common.core.Constants.CANNOT_CREATE_CLASS;

/**
 * Common utility methods for simplifying reflection uses.
 */
@SuppressWarnings({ "NonJREEmulationClassesInClientCode", "ClassWithTooManyMethods", "OverlyComplexClass" })
// Not included in GWT
public final class Reflection {

    //~ Constructors .................................................................................................................................

    private Reflection() {}

    //~ Methods ......................................................................................................................................

    /**
     * Creates an Object of the specified class using a constructor that matches with the specified
     * arguments.
     */
    public static <T> T construct(@NotNull Class<T> clazz, @NotNull Object... args) {
        try {
            if (args.length == 0) {
                final Constructor<T> c = clazz.getDeclaredConstructor();
                c.setAccessible(true);
                return c.newInstance();
            }
            for (final Constructor<?> c : clazz.getDeclaredConstructors()) {
                c.setAccessible(true);
                if (areAssignableFrom(c.getParameterTypes(), args)) return cast(c.newInstance(args));
            }
        }
        catch (final Exception e) {
            throw new IllegalArgumentException(CANNOT_CREATE_CLASS + clazz.getName(), e);
        }
        throw new IllegalArgumentException(CANNOT_CREATE_CLASS + clazz.getName());
    }
    /**
     * Creates an Object of the specified class using a constructor that matches with the specified
     * arguments.
     */
    public static <T> T construct(@NotNull String className, @NotNull Object... args) {
        try {
            final Class<T> clazz = findClass(className);
            if (args.length == 0) return clazz.newInstance();
            for (final Constructor<?> c : clazz.getDeclaredConstructors()) {
                c.setAccessible(true);
                if (areAssignableFrom(c.getParameterTypes(), args)) return cast(c.newInstance(args));
            }
        }
        catch (final Exception e) {
            throw new IllegalArgumentException(CANNOT_CREATE_CLASS + className, e);
        }
        throw new IllegalArgumentException(CANNOT_CREATE_CLASS + className);
    }

    /** Copy all fields from an object to another of the same type. */
    public static <T> void copyDeclaredFields(T from, T to) {
        final Class<?> clazz = from.getClass();
        if (clazz != to.getClass()) throw new IllegalArgumentException("Object must be of the same class");
        try {
            for (final Field field : clazz.getDeclaredFields()) {
                if (!isFinal(field.getModifiers())) {
                    field.setAccessible(true);
                    field.set(to, field.get(from));
                }
            }
        }
        catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /** Try to create the class object for a given name. */
    public static <T> Class<T> findClass(String className) {
        return findClass(className, () -> new IllegalArgumentException(CANNOT_CREATE_CLASS + className));
    }

    /** Try to create the class object for a given name. Throws the given exception if fails. */
    public static <T> Class<T> findClass(String className, Supplier<RuntimeException> fail) {
        try {
            return cast(Class.forName(className, true, Thread.currentThread().getContextClassLoader()));
        }
        catch (final ClassNotFoundException e) {
            throw fail.get();
        }
    }
    /**
     * Try to create the class object for a given name and ensure that the Class extends the
     * specified Class. returns Option#none() if the Class is not found or do not extends the
     * specified clas
     */
    public static <T> Option<Class<? extends T>> findClass(String className, Class<T> base) {
        try {
            return option(Class.forName(className, true, Thread.currentThread().getContextClassLoader()).asSubclass(base));
        }
        catch (final ClassNotFoundException | ClassCastException e) {
            return Option.empty();
        }
    }
    /** Return a {@link Field } given its name, and ensuring is accessible. */
    @SuppressWarnings("WeakerAccess")
    public static Option<Field> findField(@NotNull Class<?> clazz, String name) {
        for (Class<?> type = clazz; type != null && type != Object.class; type = type.getSuperclass()) {
            for (final Field field : type.getDeclaredFields()) {
                if (field.getName().equals(name)) {
                    field.setAccessible(true);
                    return Option.option(field);
                }
            }
        }
        return Option.empty();
    }

    /**
     * Return a {@link Field} given its name, and ensuring is accessible, and fail if it is not
     * possible.
     */
    public static Field findFieldOrFail(Class<?> clazz, String fieldName) {
        return findField(clazz, fieldName).getOrFail("No such field: " + fieldName);
    }

    /** Try to find the inner class with a given name. */
    @SuppressWarnings("WeakerAccess")
    public static Option<Class<?>> findInnerClass(@NotNull Class<?> clazz, @NotNull String name) {
        for (final Class<?> inner : clazz.getClasses()) {
            if (inner.getSimpleName().equals(name)) return Option.some(inner);
        }
        return Option.empty();
    }

    /**
     * Find a method with the specified name and parameters, returns {@link Option#empty()} if not
     * found.
     */
    @NotNull public static Option<Method> findMethod(Class<?> targetType, String name, Class<?>... args) {
        try {
            final Method method = targetType.getMethod(name, args);
            method.setAccessible(true);
            return Option.some(method);
        }
        catch (final NoSuchMethodException e) {
            return Option.empty();
        }
    }

    /** Invokes any public method from given instance that matches name and specified arguments. */
    @Nullable public static <T> T invoke(@NotNull Object instance, @NotNull String name, @Nullable Object... args) {
        return invoke(instance, Reflection::methods, name, args);
    }

    /**
     * Invokes any method (public, protected, package protected, and private) from given instance
     * that matches name and specified arguments.
     */
    @Nullable public static <T> T invokeDeclared(@NotNull Object instance, @NotNull String name, @Nullable Object... args) {
        return invoke(instance, Reflection::declaredMethods, name, args);
    }

    /** Invoke the specified static method. */
    @Nullable public static <T> T invokeStatic(Method method, Object... args) {
        try {
            return cast(method.invoke(null, args));
        }
        catch (final InvocationTargetException e) {
            throw cannotInvoke(method.getClass(), method.getName(), e.getTargetException());
        }
        catch (final Exception e) {
            throw cannotInvoke(method.getClass(), method.getName(), e);
        }
    }

    /** Invokes an static method from the class that matches the name. */
    public static <T> T invokeStatic(@NotNull Class<?> clazz, @NotNull String methodName) {
        try {
            final Method m = clazz.getMethod(methodName);
            m.setAccessible(true);
            return cast(m.invoke(null));
        }
        catch (final InvocationTargetException e) {
            throw cannotInvoke(clazz, methodName, e.getTargetException());
        }
        catch (final Exception e) {
            throw cannotInvoke(clazz, methodName, e);
        }
    }

    /**
     * Invokes an static method from the class that matches the name and the specifies arguments.
     */
    @Nullable public static <T> T invokeStatic(@NotNull String className, @NotNull String name, @NotNull Object... args) {
        return invokeStatic(findClass(className), name, args);
    }

    /**
     * Invokes an static method from the class that matches the name and the specifies arguments.
     */
    @Nullable public static <T> T invokeStatic(@NotNull Class<?> clazz, @NotNull String name, @NotNull Object... args) {
        try {
            Class<?> recursiveClass = clazz;
            while (Object.class != recursiveClass) {
                for (final Method method : recursiveClass.getMethods()) {
                    if (isStatic(method.getModifiers()) && method.getName().equals(name) && areAssignableFrom(method.getParameterTypes(), args)) {
                        method.setAccessible(true);
                        final Object[] args1 = convertParameters(method.getParameterTypes(), args);
                        return invokeStatic(method, args1);
                    }
                }

                recursiveClass = recursiveClass.getSuperclass();
            }
        }
        catch (final Exception e) {
            throw cannotInvoke(clazz, name, e);
        }
        throw cannotInvoke(clazz, name, null);
    }

    /** Returns all fields including the superclass ones. */
    public static Set<Field> getFields(Class<?> cl) {
        final Set<Field> fields = new HashSet<>();
        Class<?>         c      = cl;
        while (c != null) {
            Collections.addAll(fields, c.getDeclaredFields());
            c = c.getSuperclass();
        }
        return fields;
    }

    /** Return the value of a Field. */
    @Nullable public static <T, V> V getFieldValue(@Nullable T object, Field field) {
        try {
            field.setAccessible(true);
            return cast(field.get(object));
        }
        catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /** Sets the value of a Field. */
    public static <T, V> void setFieldValue(@Nullable T instance, @NotNull Field field, @Nullable V value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        }
        catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /** Set the value of a Final Field. */
    public static void setFinalField(@Nullable Object obj, Field field, Object value) {
        try {
            final int   mod      = field.getModifiers();
            final Field modField = getDeclaredField(Field.class, "modifiers");
            assert modField != null;
            modField.setAccessible(true);
            modField.set(field, mod & ~Modifier.FINAL);
            field.setAccessible(true);
            field.set(obj, value);
        }
        catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /** Return the value of a Field and ensure it is not null . */
    @NotNull public static <T, V> V getNotNullFieldValue(@Nullable T object, Field field) {
        final V v = getFieldValue(object, field);
        if (v == null) throw new IllegalStateException("Null value for field " + field.getName());
        return v;
    }

    /** Retrieves a field value for the instance. */
    public static <T> T getPrivateField(@NotNull Object instance, @NotNull String fieldName) {
        return getField(instance, fieldName, false);
    }

    /** Sets a private field value for the instance. */
    public static void setPrivateField(@NotNull Object instance, @NotNull String fieldName, @Nullable Object value) {
        final Class<?> clazz = instance.getClass();

        try {
            setFieldValue(instance, findDeclaredField(instance.getClass(), fieldName), value);
        }
        catch (final Exception e) {
            throw cannotFindField(fieldName, clazz, e);
        }
    }

    /** Invokes an static method from the class that matches the name. */
    public static <T> T getPrivateStaticField(@NotNull Class<?> clazz, @NotNull String fieldName) {
        try {
            final Field f = findDeclaredField(clazz, fieldName);
            f.setAccessible(true);
            return cast(f.get(null));
        }
        catch (final Exception e) {
            throw cannotFindField(fieldName, clazz, e);
        }
    }

    /** Retrieves a field value for the instance. */
    @SuppressWarnings("WeakerAccess")
    public static <T> T getPublicField(@NotNull Object instance, @NotNull String fieldName) {
        return getField(instance, fieldName, true);
    }

    /** Returns public fields. */
    public static Set<Field> getPublicFields(Class<?> cl) {
        final Set<Field> fields = new HashSet<>();
        Collections.addAll(fields, cl.getFields());
        return fields;
    }

    /** Invokes an static method from the class that matches the name. */
    public static <T> T getPublicStaticField(@NotNull Class<?> clazz, @NotNull String fieldName) {
        try {
            final Field f = clazz.getField(fieldName);
            f.setAccessible(true);
            return cast(f.get(null));
        }
        catch (final Exception e) {
            throw cannotFindField(fieldName, clazz, e);
        }
    }

    private static boolean areAssignableFrom(Class<?>[] types, @Nullable Object[] objects) {
        if (objects == null || types.length != objects.length) return false;
        for (int i = 0; i < types.length; i++) {
            if (!isAssignableFrom(types[i], objects[i])) return false;
        }
        return true;
    }

    private static IllegalArgumentException cannotFindField(String fieldName, Class<?> aClass, @Nullable Exception e) {
        final String msg = Constants.CANNOT_FIND_FIELD + aClass.getName() + "." + fieldName;
        return e == null ? new IllegalArgumentException(msg) : new IllegalArgumentException(msg, e);
    }

    private static RuntimeException cannotInvoke(Class<?> clazz, String name, @Nullable Throwable t) {
        if (t instanceof ApplicationException) return ((RuntimeException) t);
        final String message = Constants.CANNOT_INVOKE_METHOD + clazz.getName() + "." + name;
        return t == null ? new IllegalArgumentException(message) : new IllegalArgumentException(message, t);
    }

    private static Object[] convertParameters(Class<?>[] params, Object[] values) {
        final Object[] args = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            if (params[i].isPrimitive()) args[i] = convertToPrimitive(params[i], values[i]);
            else args[i] = values[i];
        }

        return args;
    }

    @SuppressWarnings({ "UnnecessaryUnboxing", "MethodWithMultipleReturnPoints" })
    private static Object convertToPrimitive(Class<?> primitive, Object value) {
        if (primitive == Float.TYPE) return ((Number) value).floatValue();
        if (primitive == Integer.TYPE) return ((Number) value).intValue();
        if (primitive == Short.TYPE) return ((Number) value).shortValue();
        if (primitive == Long.TYPE) return ((Number) value).longValue();
        if (primitive == Double.TYPE) return ((Number) value).doubleValue();
        if (primitive == Character.TYPE) {
            if (value instanceof String) return ((String) value).charAt(0);
            else if (value instanceof Character) return ((Character) value).charValue();
        }
        else if (primitive == Boolean.TYPE) return ((Boolean) value).booleanValue();

        throw new IllegalArgumentException(value.getClass().getName() + " can not be converted to " + primitive.getName());
    }

    @NotNull private static Iterable<Method> declaredMethods(@NotNull final Object instance) {
        return () ->
               new IteratorBase<Method>() {
                private Class<?>         clazz   = instance.getClass();
                private Iterator<Method> methods = emptyIterator();

                @Override public boolean hasNext() {
                    if (nextExists) return true;
                    // noinspection LoopConditionNotUpdatedInsideLoop
                    while (!methods.hasNext()) {
                        if (clazz == null) {
                            nextExists = false;
                            return false;
                        }
                        methods = more();
                    }
                    nextValue  = methods.next();
                    nextExists = true;
                    return true;
                }

                @NotNull private Iterator<Method> more() {
                    final Iterator<Method> result = fromArray(clazz.getDeclaredMethods()).iterator();
                    clazz = clazz.getSuperclass();
                    return result;
                }
            };
    }

    /** Searches for the declared declared field (includes private). */
    private static Field findDeclaredField(final Class<?> instanceClazz, final String fieldName) {
        for (Class<?> clazz = instanceClazz; clazz != null; clazz = clazz.getSuperclass()) {
            final Field field = getDeclaredField(clazz, fieldName);
            if (field != null) return field;
        }
        throw cannotFindField(fieldName, instanceClazz, null);
    }

    @Nullable private static <T> T invoke(@NotNull Object instance, @NotNull Function<Object, Iterable<Method>> methods, @NotNull String name,
                                          @Nullable Object[] args) {
        final Class<?> clazz = instance.getClass();

        try {
            for (final Method method : methods.apply(instance)) {
                if (name.equals(method.getName())) {
                    method.setAccessible(true);

                    if (args == null) return cast(method.invoke(instance));

                    if (areAssignableFrom(method.getParameterTypes(), args)) {
                        final Object[] args1 = convertParameters(method.getParameterTypes(), args);
                        return cast(method.invoke(instance, args1));
                    }
                }
            }
        }
        catch (final InvocationTargetException e) {
            throw cannotInvoke(clazz, name, e.getTargetException());
        }
        catch (final Exception e) {
            throw cannotInvoke(clazz, name, e);
        }
        throw cannotInvoke(clazz, name, null);
    }

    @NotNull private static Iterable<Method> methods(@NotNull Object instance) {
        return fromArray(instance.getClass().getMethods());
    }

    @Nullable private static Field getDeclaredField(final Class<?> clazz, final String fieldName) {
        try {
            final Field field = clazz.getDeclaredField(fieldName);
            if (field != null) {
                field.setAccessible(true);
                return field;
            }
        }
        catch (final Exception ignore) {}
        return null;
    }

    /** Retrieves a field value for the instance. */
    private static <T> T getField(@NotNull Object instance, @NotNull String fieldName, boolean isPublic) {
        final Class<?> clazz = instance.getClass();

        try {
            final Field field = isPublic ? clazz.getField(fieldName) : findDeclaredField(instance.getClass(), fieldName);
            return cast(field.get(instance));
        }
        catch (final Exception e) {
            throw cannotFindField(fieldName, clazz, e);
        }
    }

    /** Sets a public field value for the instance. */
    private static void setField(@NotNull Object instance, @NotNull String fieldName, @Nullable Object value) {
        final Class<?> clazz = instance.getClass();
        try {
            clazz.getField(fieldName).set(instance, value);
        }
        catch (final Exception e) {
            throw cannotFindField(fieldName, clazz, e);
        }
    }

    @SuppressWarnings("IfStatementWithTooManyBranches")
    private static boolean isAssignableFrom(Class<?> type, Object value) {
        if (value == null) return true;
        boolean isAssignableFrom = false;

        if (type.isPrimitive()) {
            if (value instanceof Short) isAssignableFrom = type == Short.TYPE;
            else if (value instanceof Integer) isAssignableFrom = type == Integer.TYPE;
            else if (value instanceof Long) isAssignableFrom = type == Long.TYPE;
            else if (value instanceof Float) isAssignableFrom = type == Float.TYPE;
            else if (value instanceof Double) isAssignableFrom = type == Double.TYPE;
            else if (value instanceof String || value instanceof Character) isAssignableFrom = type == Character.TYPE;
            else if (value instanceof Boolean) isAssignableFrom = type == Boolean.TYPE;
        }
        else isAssignableFrom = type.isAssignableFrom(value.getClass());

        return isAssignableFrom;
    }  // end method isAssignableFrom

    //~ Inner Classes ................................................................................................................................

    /**
     * Wraps a reflection instance.
     */
    @SuppressWarnings("WeakerAccess")
    public static class Instance {
        private final Object instance;

        protected Instance(@NotNull Object instance) {
            this.instance = instance;
        }

        /** Try to find the inner class with a given name. */
        @SuppressWarnings("WeakerAccess")
        public Option<Class<?>> findInnerClass(@NotNull String className) {
            return Reflection.findInnerClass(instance.getClass(), className);
        }

        /** Invokes the method for the wrapped instance. */
        @Nullable public <T> T invoke(@NotNull String name, Object... args) {
            return Reflection.invoke(instance, name, args);
        }

        /** Returns the instance created in this Reflection.Instance. */
        public Object getInstance() {
            return instance;
        }

        /** Retrieves a private field value for the wrapped instance. */
        public <T> T getPrivateField(@NotNull String fieldName) {
            return Reflection.getPrivateField(instance, fieldName);
        }

        /** Sets a private field value for the wrapped instance. */
        public void setPrivateField(@NotNull String fieldName, @Nullable Object value) {
            Reflection.setPrivateField(instance, fieldName, value);
        }

        /** Retrieves a Public field value for the wrapped instance. */
        public <T> T getPublicField(@NotNull String fieldName) {
            return Reflection.getPublicField(instance, fieldName);
        }

        /** Sets a public field value for the wrapped instance. */
        public void setPublicField(@NotNull String fieldName, @Nullable Object value) {
            setField(instance, fieldName, value);
        }

        /** Construct an object for the given class. */
        public static Instance create(@NotNull Class<?> clazz, @NotNull Object... args) {
            return new Instance(construct(clazz, args));
        }

        /** Construct an object for the specified class. */
        public static Instance create(@NotNull String clazzName, @NotNull Object... args) {
            return create(findClass(clazzName), args);
        }

        /** Construct a Reflection.Instance over the given object. */
        public static Instance wrap(@NotNull final Object instance) {
            return new Instance(instance);
        }
    }  // end class Instance
}  // end class Reflection
