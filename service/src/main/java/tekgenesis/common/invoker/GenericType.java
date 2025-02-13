
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.jetbrains.annotations.Nullable;

import static tekgenesis.common.Predefined.cast;

/**
 * Represents a generic 'T' reference.
 */
public abstract class GenericType<T> {

    //~ Instance Fields ..............................................................................................................................

    private final Class<?> raw;

    private final Type type;

    //~ Constructors .................................................................................................................................

    /**
     * Constructs a new generic type, deriving the generic type and class from type parameter. Note
     * that this constructor is protected, users should create a (usually anonymous).
     */
    protected GenericType() {
        this(null);
    }

    protected GenericType(@Nullable Class<?> clazz) {
        final Type superclass = getClass().getGenericSuperclass();
        if (!(superclass instanceof ParameterizedType)) throw new RuntimeException("Missing type parameter.");
        final ParameterizedType parameterized = (ParameterizedType) superclass;

        type = parameterized.getActualTypeArguments()[0];
        raw  = clazz == null ? getClass(type) : clazz;
    }

    //~ Methods ......................................................................................................................................

    /** Gets underlying raw class instance derived from the type. */
    public Class<T> getRaw() {
        return cast(raw);
    }

    /** Gets underlying {@link Type} instance derived from the type. */
    public Type getType() {
        return type;
    }

    //~ Methods ......................................................................................................................................

    private static Class<?> getClass(Type type) {
        if (type instanceof Class) return (Class<?>) type;
        if (type instanceof ParameterizedType) {
            final ParameterizedType parameterized = (ParameterizedType) type;
            if (parameterized.getRawType() instanceof Class) return (Class<?>) parameterized.getRawType();
        }
        throw new IllegalArgumentException("Type parameter not a class or " +
            "parameterized type whose raw type is a class");
    }
}  // end class GenericType
