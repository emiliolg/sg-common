
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.core.Enumeration;
import tekgenesis.common.core.Option;

import static tekgenesis.common.core.Option.some;

/**
 * This class represents an instantiated enumeration with parameters.
 */
public class Message implements Serializable {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final Object[]          arguments;
    @NotNull private final Enumeration<?, ?> enumeration;

    //~ Constructors .................................................................................................................................

    private Message(@NotNull final Enumeration<?, ?> enumeration, @NotNull final Object[] arguments) {
        this.enumeration = enumeration;
        this.arguments   = arguments;
    }

    //~ Methods ......................................................................................................................................

    @Override public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Message message = (Message) o;
        return Objects.equals(enumeration, message.enumeration) && Arrays.equals(arguments, message.arguments);
    }

    @Override public int hashCode() {
        return Objects.hash(enumeration, arguments);
    }

    /**
     * @return  the enumeration message with the arguments applied
     *
     * @see     Enumeration#label()
     * @see     Enumeration#label(Object...)
     */
    public String label() {
        return arguments.length != 0 ? enumeration.label(arguments) : enumeration.label();
    }

    /** @see  #label() */
    @Override public String toString() {
        return label();
    }

    /** Returns the Arguments of this message or empty. */
    @NotNull public Option<Object[]> getArguments() {
        return arguments.length != 0 ? some(arguments) : Option.empty();
    }

    /** Returns the enumeration that this Message is build from. */
    @NotNull public Enumeration<?, ?> getEnumeration() {
        return enumeration;
    }

    //~ Methods ......................................................................................................................................

    /** Creates a I18nized Message with the given enumeration as resource. */
    public static Message create(@NotNull final Enumeration<?, ?> enumeration) {
        return new Message(enumeration, EMPTY_PARAMETERS);
    }

    /** Creates a I18nized Message with the given enumeration as resource with parameters. */
    public static Message create(@NotNull final Enumeration<?, ?> enumeration, @NotNull final Object[] parameters) {
        return new Message(enumeration, parameters);
    }

    /** Creates a I18nized Message with the given enumeration as resource with parameters. */
    public static Message create(@NotNull final Enumeration<?, ?> enumeration, @NotNull final Iterable<Object> parameters) {
        final ArrayList<Object> objects = new ArrayList<>();
        parameters.forEach(objects::add);
        return new Message(enumeration, objects.toArray());
    }

    /** Creates a I18nized Message with the given enumeration as resource with parameters. */
    public static Message create(@NotNull final Enumeration<?, ?> enumeration, final Object first, final Object... rest) {
        final int      size  = rest == null ? 1 : rest.length + 1;
        final Object[] array = (Object[]) Array.newInstance(Object.class, size);
        array[0] = first;
        // array copy does not work because the types of the elements are different
        if (rest != null)  // noinspection ManualArrayCopy
            for (int i = 0; i < rest.length; i++)
                array[i + 1] = rest[i];
        return new Message(enumeration, array);
    }

    //~ Static Fields ................................................................................................................................

    private static final long     serialVersionUID = -1077273993394150206L;
    private static final Object[] EMPTY_PARAMETERS = {};
}  // end class Message
