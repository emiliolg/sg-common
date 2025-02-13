
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.util.List;

import org.junit.Test;

import tekgenesis.common.cmd.Command;
import tekgenesis.common.cmd.CommandExecutor;
import tekgenesis.common.cmd.CommandInfo;
import tekgenesis.common.cmd.Option;

import static org.assertj.core.api.Assertions.*;

import static tekgenesis.common.Predefined.unreachable;

/**
 * User: emilio; Date: 12/16/11; Time: 12:47 PM;
 */

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class CommandTest {

    //~ Methods ......................................................................................................................................

    @Test public void help() {
        final Example command = new Example();
        assertThat(command.buildHelp()).containsExactly(  //
            "Usage: example [Options] arguments",
            "               An Example Command",
            "               used for test.",
            "",
            "Options:",
            "  -h, --help    Print Help Information and exit",
            "  -v, --verbose Level of verbosity",
            "                0: quiet",
            "                1: info",
            "                2: debug",
            "  -w, --warn    Display Warning messages",
            "      --warning ");
    }

    @Test public void multipleCommand() {
        final Info info = new Info() {
                @Override public void execute() {
                    assertThat(verbose).isEqualTo(3);
                    assertThat(getCommonOptions().verbose).isEqualTo(2);
                }
            };
        new Common().withArgs("--verbose", "2", "info", "-v", "3").withCommand(info).withCommand(new Ls()).run();
    }

    @Test public void multipleHelp() {
        final Common                  main = new Common();
        final Info                    info = new Info();
        final CommandExecutor<Common> ce   = main.withArgs("--help").withCommand(info).withCommand(new Ls());
        assertThat(ce.buildHelp(main)).containsExactly("Usage: Common",
            "",
            "Options:",
            "  -h, --help    Print Help Information and exit",
            "  -v, --verbose Level of verbosity",
            "",
            "Commands:",
            "  info Info Command",
            "  list List Command",
            "       List all Files");
        assertThat(ce.buildHelp(info)).containsExactly("Usage: info Info Command", "", "Options:", "  -v, --verbose Level of verbosity");
    }

    @Test public void noOptions() {
        final Example command = new Example() {
                @Override public void execute() {
                    assertThat(verbose).isEqualTo(1);
                    assertThat(args).containsExactly("Hello", "World");
                }
            };
        command.withArgs("Hello", "World").run();
    }

    @Test public void withOptions() {
        final Example command = new Example() {
                @Override public void execute() {
                    assertThat(verbose).isEqualTo(10);
                    assertThat(args).containsExactly("a", "b");
                }
            };
        command.withArgs("--verbose", "10", "a", "b").run();
    }

    //~ Inner Classes ................................................................................................................................

    @CommandInfo(name = "Common")
    static class Common extends Command<Common> {
        @Option(description = "Level of verbosity")
        int verbose = 1;

        @Override public void execute() {}
    }

    @CommandInfo(
                 name        = "example",  //
                 description = {
                     "[Options] arguments",
                     "An Example Command",
                     "used for test."
                 }
                )
    static class Example extends Command<Example> {
        @Option(main = true)
        List<String> args = null;

        @Option(description = { "Level of verbosity", "0: quiet", "1: info", "2: debug" })
        int verbose = 1;

        @Option(description = "Display Warning messages", name = { "w", "warn", "warning" })
        Boolean warn = false;

        @Override public void execute() {
            throw unreachable();
        }
    }

    @CommandInfo(name        = "info", description = "Info Command")
    static class Info extends Command<Common> {
        @Option(main = true)
        List<String> args = null;

        @Option(description = "Level of verbosity")
        int verbose = 1;

        @Override public void execute() {}
    }

    @CommandInfo(name        = "list", description = { "List Command", "List all Files" })
    static class Ls extends Command<Common> {
        @Override public void execute() {}
    }
}  // end class CommandTest
