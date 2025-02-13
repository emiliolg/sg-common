
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.cmd;

import java.lang.reflect.Field;
import java.util.*;

import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.ImmutableList;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.cmd.CmdConstants.DEFAULT_HELP;
import static tekgenesis.common.cmd.Opt.createHelp;

/**
 * The base class for a Command In CommandExecutor Applications are sets of 1 or more commands.
 */
@SuppressWarnings("WeakerAccess")
public abstract class Command<T extends Command<T>> {

    //~ Instance Fields ..............................................................................................................................

    private T                  commonOptions;
    private final List<String> description;
    private final Opt          helpOption;

    @SuppressWarnings("FieldMayBeFinal")
    private boolean                helpRequested;
    private Opt                    mainOption;
    private final String           name;
    private final SortedSet<Opt>   options;
    private final Map<String, Opt> optionsByName;

    //~ Constructors .................................................................................................................................

    protected Command() {
        options       = new TreeSet<>();
        optionsByName = new HashMap<>();
        mainOption    = null;
        commonOptions = null;
        helpRequested = false;

        final CommandInfo info = findAnnotation();

        if (info == null) {
            name        = Opt.idFromJavaId(getClass().getName());
            description = emptyList();
            helpOption  = createHelp(this, DEFAULT_HELP);
        }
        else {
            name        = info.name();
            description = asList(info.description());
            final String[] hlp = info.help();
            helpOption = hlp.length == 0 ? createHelp(this, DEFAULT_HELP) : hlp[0].isEmpty() ? null : createHelp(this, hlp);
        }
        if (helpOption != null) options.add(helpOption);

        collectOptions(options, this);

        for (final Opt opt : options) {
            for (final String str : opt.getNames())
                optionsByName.put(str, opt);
            if (opt.isMain()) mainOption = opt;
        }
    }

    //~ Methods ......................................................................................................................................

    /** Build Command Help. */
    public final ImmutableList<String> buildHelp() {
        return new HelpBuilder(this).buildHelpHeader().buildHelpOptions().result();
    }

    /** Returns the description of the command. */
    public List<String> description() {
        return description;
    }

    /** Execute the command, Options and Arguments will be taken from fields. */
    public abstract void execute();

    /** Returns the name of the command. */
    public String name() {
        return name;
    }

    /** Create a Command Executor for the command with the specified arguments. */
    public CommandExecutor<T> withArgs(List<String> args) {
        final T cast = cast(this);
        return new CommandExecutor<>(cast, args);
    }

    /** Specify the Command arguments a typical use will be. */
    public CommandExecutor<T> withArgs(String... args) {
        return withArgs(args, 0);
    }

    /**
     * Specify the Command arguments a typical use will be.
     *
     * <pre>
           public static void main(String[] args) {
              new Example().withArgs(args, 0).run();
           }
     </pre>
     */
    public CommandExecutor<T> withArgs(String[] args, int from) {
        return withArgs(asList(args).subList(from, args.length));
    }

    /** Return common Options for multiple commands. */
    public T getCommonOptions() {
        return commonOptions;
    }

    /** Return the set of options. */
    public SortedSet<Opt> getOptions() {
        return options;
    }

    Opt find(String nm) {
        return nm.isEmpty() ? null : optionsByName.get(nm);
    }

    boolean helpRequested() {
        return helpRequested;
    }

    void initParent(T parent) {
        commonOptions = parent;
        // Is a sub command => remove helpOption
        if (helpOption != null) {
            options.remove(helpOption);
            optionsByName.remove(helpOption.getNames().get(0));
        }
    }

    @Nullable Opt mainOption() {
        return mainOption;
    }

    private CommandInfo findAnnotation() {
        for (Class<?> c = getClass(); c != null; c = c.getSuperclass()) {
            final CommandInfo annotation = c.getAnnotation(CommandInfo.class);
            if (annotation != null) return annotation;
        }
        return null;
    }

    //~ Methods ......................................................................................................................................

    private static void collectOptions(Set<Opt> options, Command<?> object) {
        if (object == null) return;
        for (Class<?> c = object.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            for (final Field f : c.getDeclaredFields()) {
                if (Command.class.isAssignableFrom(f.getType())) collectOptions(options, getField(object, f, Command.class));
                else {
                    final Opt opt = Opt.create(f, object);
                    if (opt != null) options.add(opt);
                }
            }
        }
    }  // end method collectOptions

    private static <T> T getField(Object object, Field f, Class<T> clazz) {
        try {
            f.setAccessible(true);
            return clazz.cast(f.get(object));
        }
        catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
