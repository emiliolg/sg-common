
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.cmd;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.Character.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import static tekgenesis.common.cmd.CmdConstants.HELP_DESCRIPTION;
import static tekgenesis.common.cmd.CmdConstants.HELP_REQUESTED_FIELD;
import static tekgenesis.common.core.Constants.EMPTY_STRING_ARRAY;
import static tekgenesis.common.util.Conversions.fromString;
import static tekgenesis.common.util.Primitives.wrapperFor;
import static tekgenesis.common.util.Reflection.findFieldOrFail;

/**
 */
@SuppressWarnings("WeakerAccess")
public class Opt implements Comparable<Opt> {

    //~ Instance Fields ..............................................................................................................................

    private final int arity;

    @Nullable private final Object      defaultValue;
    @NotNull private final List<String> description;
    @NotNull private final Field        field;
    private final boolean               hidden;
    private final boolean               list;
    private final boolean               main;

    @NotNull private final List<String> names;
    @NotNull private final Object       object;
    @NotNull private final String       pattern;

    private final boolean required;
    /** Field used to define sorting order for options. */
    @NotNull private final String   sortName;
    @NotNull private final Class<?> type;

    //~ Constructors .................................................................................................................................

    private Opt(@NotNull Field field, Option option, @NotNull Object object) {
        this.field  = field;
        this.object = object;
        main        = option.main();

        final String[] nms = option.name();
        // Names
        if (main) {
            names    = emptyList();
            sortName = "";
        }
        else {
            if (nms.length == 0) {
                final String nm = idFromJavaId(field.getName());
                sortName = nm;
                names    = asList(nm, nm.substring(0, 1));
            }
            else {
                sortName = nms[0];
                names    = new ArrayList<>(1);
                for (final String name : nms) {
                    if (!name.isEmpty()) names.add(name);
                }
            }
        }
        // Description
        description = asList(option.description());

        // Flags
        hidden   = option.hidden();
        required = option.required();

        // To be or not to be a list
        list = List.class.isAssignableFrom(field.getType());
        if (list) {
            type  = wrapperFor(option.elementType());
            arity = option.arity();
        }
        else {
            type  = wrapperFor(field.getType());
            arity = isBoolean() ? option.arity() : 1;
        }
        // default
        defaultValue = makeDefault(asList(option.defaultValue()));
        // pattern
        pattern = option.pattern();
    }  // end ctor Opt

    //~ Methods ......................................................................................................................................

    @Override public int compareTo(@NotNull Opt o) {
        return sortName.compareTo(o.sortName);
    }

    @Override public boolean equals(Object o) {
        return o instanceof Opt && sortName.equals(((Opt) o).sortName);
    }

    @Override public int hashCode() {
        return sortName.hashCode();
    }

    @Override public String toString() {
        return sortName;
    }

    /** Returns option Arity. */
    public int getArity() {
        return arity;
    }

    /** Returns true if the option is required. */
    public boolean isRequired() {
        return required;
    }

    /** Returns the Option default value. */
    @Nullable public Object getDefault() {
        return defaultValue;
    }

    /** Returns the Option description. */
    @NotNull public List<String> getDescription() {
        return description;
    }

    /** Returns true if the Option is a boolean. */
    public boolean isBoolean() {
        return type == Boolean.class;
    }

    /** Returns true if the Option is hidden. */
    public boolean isHidden() {
        return hidden;
    }

    /** Returns true if this is the main option. */
    public boolean isMain() {
        return main;
    }

    /** Return the Option names. */
    @NotNull public List<String> getNames() {
        return names;
    }

    /** Return the Option pattern. */
    @NotNull public String getPattern() {
        return pattern;
    }

    /** Returns true if the option is a list. */
    public boolean isList() {
        return list;
    }

    /** Returns the type of the option. */
    @NotNull public Class<?> getType() {
        return type;
    }

    /** Set Option Values. */
    public void setValue(List<String> args) {
        if (arity == 0 && isBoolean()) setField(Boolean.TRUE);
        else if (isList()) setField(convertToList(args, type));
        else setField(fromString(args.get(0), type));
    }

    void initializeDefault() {
        final Object def = getDefault();
        if (def != null) setField(def);
    }

    private Object makeDefault(List<String> strings) {
        final boolean hasDefault = !strings.isEmpty();
        return list ? (hasDefault ? convertToList(strings, type) : emptyList()) : (hasDefault ? fromString(strings.get(0), type) : null);
    }

    private void setField(@Nullable Object value) {
        try {
            field.setAccessible(true);
            field.set(object, value);
        }
        catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    //~ Methods ......................................................................................................................................

    /** Create The Opt. */
    @Nullable public static Opt create(Field field, Object object) {
        final Option option = field.getAnnotation(Option.class);
        return option == null ? null : new Opt(field, option, object);
    }

    /** Create an Opt for Help. */
    static Opt createHelp(final Command<?> cmd, final String[] names) {
        return new Opt(findFieldOrFail(Command.class, HELP_REQUESTED_FIELD), new HelpOption(names), cmd);
    }

    static String idFromJavaId(String name) {
        final StringBuilder result = new StringBuilder();

        for (int i = 0; i < name.length(); i++) {
            final char chr = name.charAt(i);

            if (i > 0 && isUpperCase(chr) && isLowerCase(name.charAt(i - 1))) result.append('-');

            if (chr == '_' || chr == '$') result.append('-');
            else result.append(toLowerCase(chr));
        }

        return result.toString();
    }

    private static <T> List<T> convertToList(List<String> strings, Class<T> type) {
        final List<T> result = new ArrayList<>(strings.size());
        for (final String string : strings)
            result.add(fromString(string, type));
        return result;
    }

    //~ Inner Classes ................................................................................................................................

    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class HelpOption implements Option {
        private final String[] names;

        HelpOption(final String[] names) {
            this.names = names;
        }

        @Override public Class<? extends Annotation> annotationType() {
            return Option.class;
        }
        @Override public int arity() {
            return 0;
        }
        @Override public String[] defaultValue() {
            return EMPTY_STRING_ARRAY;
        }
        @Override public String[] description() {
            return HELP_DESCRIPTION;
        }
        @Override public Class<?> elementType() {
            return Boolean.TYPE;
        }
        @Override public boolean hidden() {
            return false;
        }
        @Override public boolean main() {
            return false;
        }
        @Override public String[] name() {
            return names;
        }
        @Override public String pattern() {
            return ".*";
        }
        @Override public boolean required() {
            return false;
        }
    }
}  // end class Opt
