
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.cmd;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.TreeMap;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.collections.ImmutableList;

import static tekgenesis.common.Predefined.cast;

/**
 * The CommandExecutor main class Create one, configure using fluent interface and run it.
 */
public class CommandExecutor<T extends Command<T>> {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final List<String>             arguments;
    @NotNull private final Map<String, Command<T>>  commands;
    @NotNull private final T                        mainCommand;

    //~ Constructors .................................................................................................................................

    CommandExecutor(@NotNull T command, @NotNull List<String> args) {
        mainCommand = command;
        arguments   = args;
        commands    = new TreeMap<>();
    }

    //~ Methods ......................................................................................................................................

    /** Build Command Help. */
    public ImmutableList<String> buildHelp(Command<?> command) {
        final HelpBuilder hb = new HelpBuilder(command).buildHelpHeader().buildHelpOptions();
        if (command == mainCommand) hb.addCommands(commands);
        return hb.result();
    }

    /** Discover commands using services interface. */
    public CommandExecutor<T> discoverCommands() {
        for (final Command<?> c : ServiceLoader.load(Command.class)) {
            final Command<T> command = cast(c);
            withCommand(command);
        }
        return this;
    }

    /** Run the command. */
    public void run() {
        try {
            final CommandParser parser = new CommandParser(arguments, mainCommand);
            if (commands.isEmpty()) singleCommandRun(parser);
            else multipleCommandRun(parser);
        }
        catch (final JCommandException e) {
            System.out.println(e.getMessage());
            printHelp();
        }
    }  // end method run

    /** Define command to be used. */
    @SuppressWarnings({ "UnusedReturnValue", "WeakerAccess" })
    public CommandExecutor<T> withCommand(Command<T> command) {
        commands.put(command.name(), command);
        command.initParent(mainCommand);
        return this;
    }

    /** Return the name of the main command. */
    @NotNull public String getName() {
        return mainCommand.name();
    }

    private Command<T> findCommand(final int firstArg) {
        final String     cmd     = arguments.get(firstArg);
        final Command<T> command = commands.get(cmd);
        if (command != null) return command;
        throw new InvalidCommandException(cmd);
    }

    private void multipleCommandRun(final CommandParser parser) {
        final int firstArg = parser.parseOptions();
        if (firstArg == arguments.size()) printHelp();
        // try {
        // startCommandLine();
        // }
        // catch (IOException e) {
        // throw new RuntimeException(e);
        // }
        else {
            final Command<T> command = findCommand(firstArg);
            command.initParent(mainCommand);
            if (mainCommand.helpRequested()) printHelp(command);
            else {
                final CommandParser cp = new CommandParser(arguments.subList(firstArg + 1, arguments.size()), command);
                cp.parse();
                command.execute();
            }
        }
    }  // end method multipleCommandRun

    private void printHelp() {
        printHelp(mainCommand);
    }

    /** Print command help. */
    private void printHelp(Command<?> command) {
        for (final String line : buildHelp(command))
            System.out.println(line);
    }

    private void singleCommandRun(final CommandParser parser) {
        if (mainCommand.helpRequested()) printHelp();
        else {
            parser.parse();
            mainCommand.execute();
        }
    }

    // private void startCommandLine() throws IOException {
    // String line;
    // ConsoleReader reader = new ConsoleReader();
    // reader.setDefaultPrompt("\033[31m-->\033[0m");
    // //reader.addCompletor(new Completor());
    //
    // while ((line = reader.readLine()) != null) {
    // reader.printString("\u001B[33m======>\u001B[0m\"" + line + "\"\n");
    // reader.flushConsole();
    //
    // // If we input the special word then we will mask
    // // the next line.
    // if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
    // break;
    // }
    // }
    // }
}  // end class CommandExecutor
