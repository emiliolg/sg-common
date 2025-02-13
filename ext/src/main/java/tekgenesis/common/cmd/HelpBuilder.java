
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tekgenesis.common.collections.ImmutableList;

import static tekgenesis.common.cmd.CmdConstants.*;
import static tekgenesis.common.collections.Colls.immutable;
import static tekgenesis.common.core.Strings.spaces;

/**
 * Utility class.
 */
class HelpBuilder {

    //~ Instance Fields ..............................................................................................................................

    private final Command<?>   command;
    private final List<String> result;

    //~ Constructors .................................................................................................................................

    HelpBuilder(final Command<?> command) {
        this.command = command;
        result       = new ArrayList<>();
    }

    //~ Methods ......................................................................................................................................

    public ImmutableList<String> result() {
        return immutable(result);
    }

    <T extends Command<T>> HelpBuilder addCommands(final Map<String, Command<T>> commands) {
        if (!commands.isEmpty()) {
            result.add("");
            result.add(COMMANDS);

            final Rows rows = new Rows();
            for (final Command<T> c : commands.values()) {
                int i = rows.size();
                rows.add("  " + c.name());
                for (final String s : c.description())
                    rows.get(i++).setDescription(s);
            }
            rows.produceLines(result);
        }
        return this;
    }

    HelpBuilder buildHelpHeader() {
        String       firstLine = USAGE + command.name();
        final String indent    = spaces(firstLine.length() + 1);

        // Add first line of description to first line of help
        final List<String> d = command.description();
        if (!d.isEmpty()) firstLine += " " + d.get(0);
        result.add(firstLine);

        // Add the next description lines
        for (int i = 1; i < d.size(); i++)
            result.add(indent + d.get(i));

        // Blank line
        result.add("");
        return this;
    }

    HelpBuilder buildHelpOptions() {
        result.add(OPTIONS);

        // Create a list of pairs (option-key, option-description)
        final Rows rows = new Rows();
        for (final Opt option : command.getOptions()) {
            if (!option.isHidden() && !option.isMain()) rows.addAll(helpFor(option));
        }

        rows.produceLines(result);
        return this;
    }

    //~ Methods ......................................................................................................................................

    private static Rows helpFor(Opt option) {
        final Rows result = new Rows();
        for (final String nm : option.getNames()) {
            if (nm.length() == 1) result.add("  -" + nm.charAt(0) + ", ");
        }
        int i = 0;
        for (final String nm : option.getNames()) {
            if (nm.length() > 1) result.get(i++).appendKey("--" + nm);
        }
        int j = 0;
        for (final String d : option.getDescription())
            result.get(j++).setDescription(d);

        final Object o = option.getDefault();
        if (o != null && result.size() > 0) result.get(0).appendKey(" (Default: " + o + ")");
        return result;
    }

    //~ Inner Classes ................................................................................................................................

    static class Row {
        private String description;
        private String key;

        Row(final String key) {
            this.key    = key;
            description = "";
        }

        void appendKey(final String s) {
            key += s;
        }
        String asString(final int max) {
            return key + spaces(max + 1 - key.length()) + description;
        }
        void setDescription(final String str) {
            description = str;
        }
    }

    static class Rows {
        List<Row> rows = new ArrayList<>();

        void add(final String key) {
            rows.add(new Row(key));
        }

        void addAll(final Rows b) {
            rows.addAll(b.rows);
        }

        void produceLines(List<String> lines) {
            // Get max option-key length
            int max = 0;
            for (final Row r : rows)
                max = Math.max(max, r.key.length());

            // Produce the lines
            for (final Row s : rows)
                lines.add(s.asString(max));
        }
        int size() {
            return rows.size();
        }

        private Row get(final int j) {
            if (j >= rows.size()) rows.add(new Row(SPACES_6));
            return rows.get(j);
        }
    }
}  // end class HelpBuilder
