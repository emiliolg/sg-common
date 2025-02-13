
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.cmd;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.jetbrains.annotations.NotNull;

import static tekgenesis.common.util.Files.readLines;

/**
 * The option Command Parser.
 */
class CommandParser {

    //~ Instance Fields ..............................................................................................................................

    private final List<String> arguments;
    private final Command<?>   command;
    private String             optionName;

    //~ Constructors .................................................................................................................................

    CommandParser(List<String> arguments, Command<?> command) {
        this.arguments = expandArgs(arguments);
        this.command   = command;
        optionName     = "";
    }

    //~ Methods ......................................................................................................................................

    public void parse() {
        buildArguments(parseOptions());
    }

    public int parseOptions() {
        initializeDefaults();

        int i = 0;
        while (i < arguments.size()) {
            final String arg = arguments.get(i);

            if ("--".equals(arg)) return i + 1;
            if (!arg.startsWith("-")) return i;

            final Opt          opt     = findOption(arg);
            final List<String> optArgs = extractArgs(opt, i + 1);
            i += optArgs.size();
            opt.setValue(optArgs);
            i++;
        }
        return arguments.size();
    }

    private void buildArguments(int firstArg) {
        final Opt main = command.mainOption();
        if (main != null) main.setValue(extractArgs(main, firstArg));
        else {
            final List<String> mainArgs = arguments.subList(firstArg, arguments.size());
            if (!mainArgs.isEmpty()) throw new ExtraArgumentsException(mainArgs);
        }
    }  // end method buildArguments

    /**
     * Expand the command line parameters to take @ parameters into account. When @ is encountered,
     * the content of the file that follows is inserted in the command line.
     */
    private List<String> expandArgs(List<String> args) {
        final List<String> result = new ArrayList<>();

        for (final String arg : args) {
            if (arg.startsWith("@")) {
                try {
                    result.addAll(readLines(new FileReader(arg.substring(1))));
                }
                catch (final FileNotFoundException e) {
                    throw new JCommandException(e.getMessage());
                }
            }
            else result.add(arg);
        }
        return result;
    }

    private List<String> extractArgs(Opt opt, int from) {
        final int n = opt.getArity() == 0 && opt.isList() ? Integer.MAX_VALUE : opt.getArity();
        int       j = from;
        for (; j < arguments.size() && j - from < n; j++)
            if (arguments.get(j).startsWith("-")) break;
        if (j == from && n == 1 && !opt.isList()) throw new MissingArgumentException(opt, optionName);
        return arguments.subList(from, j);
    }

    @NotNull private Opt findOption(String arg) {
        optionName = arg.substring(arg.startsWith("--") ? 2 : 1);
        final Opt opt = command.find(optionName);
        if (opt == null) throw new InvalidOptionException(optionName);
        return opt;
    }

    private void initializeDefaults() {
        final SortedSet<Opt> options = command.getOptions();
        options.forEach(Opt::initializeDefault);
    }
}
