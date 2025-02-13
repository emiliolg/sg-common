
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

import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.util.Diff;

import static org.assertj.core.api.Assertions.assertThat;

import static tekgenesis.common.collections.Colls.listOf;

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class DiffTest {

    //~ Methods ......................................................................................................................................

    @Test public void caseInsensitiveDiff() {
        final ImmutableList<Diff.Delta<String>> deltas = Diff.caseInsensitive().diff(first, second);
        final String                            out2   = "1,3c1\n" +
                                                         "<   The Way that can be told of is not the eternal Way;\n" +
                                                         "<     The name that can be named is not the eternal name.\n" +
                                                         "<         The Nameless is the origin of Heaven and Earth;\n" +
                                                         "---\n" +
                                                         ">     The Nameless is the origin of Heaven and Earth;\n" +
                                                         "4a3\n" +
                                                         "> \n" +
                                                         "11a11,13\n" +
                                                         ">     They both may be called deep and profound.\n" +
                                                         ">     Deeper and more profound\n" +
                                                         ">     The door of all subtleties!\n";
        assertThat(Diff.makeString(deltas)).isEqualTo(out2);

        assertThat(deltas.get(0).toString()).startsWith("Diff.Delta(0, c, 0, (  The");
    }
    @Test public void caseInsensitiveDiffStringsByLine() {
        final List<Diff.Delta<String>> deltas = Diff.caseInsensitive().diff("Hello\nWorld", "HELLO\nWORLD");
        assertThat(deltas).isEmpty();
    }

    @Test public void caseSensitiveDiff() {
        final ImmutableList<Diff.Delta<String>> deltas = Diff.caseSensitive().diff(first, second);
        final String                            out1   = "1,4c1,3\n" +
                                                         "<   The Way that can be told of is not the eternal Way;\n" +
                                                         "<     The name that can be named is not the eternal name.\n" +
                                                         "<         The Nameless is the origin of Heaven and Earth;\n" +
                                                         "<     The Named is the mother of all things.\n" +
                                                         "---\n" +
                                                         ">     The Nameless is the origin of Heaven and Earth;\n" +
                                                         ">     The named is the mother of all things.\n" +
                                                         "> \n" +
                                                         "11a11,13\n" +
                                                         ">     They both may be called deep and profound.\n" +
                                                         ">     Deeper and more profound\n" +
                                                         ">     The door of all subtleties!\n";
        assertThat(Diff.makeString(deltas)).isEqualTo(out1);
    }

    @Test public void diffStringsByLine() {
        final List<Diff.Delta<String>> deltas = Diff.caseSensitive().diff("Hello\nWorld", "");
        assertThat(deltas.size()).isEqualTo(1);
        assertThat(deltas.get(0).toString()).isEqualTo("Diff.Delta(0, d, 0, (Hello, World), ())");
    }

    //~ Static Fields ................................................................................................................................

    private static final ImmutableList<String> first = listOf("  The Way that can be told of is not the eternal Way;",
            "    The name that can be named is not the eternal name.",
            "        The Nameless is the origin of Heaven and Earth;",
            "    The Named is the mother of all things.",
            "    Therefore let there always be non-being,",
            "      so we may see their subtlety,",
            "    And let there always be being,",
            "      so we may see their outcome.",
            "    The two are the same,",
            "    But after they are produced,",
            "      they have different names.");

    private static final ImmutableList<String> second = listOf("    The Nameless is the origin of Heaven and Earth;",
            "    The named is the mother of all things.",
            "",
            "    Therefore let there always be non-being,",
            "      so we may see their subtlety,",
            "    And let there always be being,",
            "      so we may see their outcome.",
            "    The two are the same,",
            "    But after they are produced,",
            "      they have different names.",
            "    They both may be called deep and profound.",
            "    Deeper and more profound",
            "    The door of all subtleties!");
}  // end class DiffTest
