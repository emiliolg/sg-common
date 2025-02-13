
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.io.Reader;
import java.io.StringReader;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import tekgenesis.common.util.Preprocessor.Escape;

import static java.lang.Integer.parseInt;

import static org.assertj.core.api.Assertions.assertThat;

import static tekgenesis.common.collections.Colls.listOf;
import static tekgenesis.common.core.Strings.nChars;

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class PreprocessorTest {

    //~ Instance Fields ..............................................................................................................................

    final String input1 = "\n" +
                          "  #define A my_a\n" +
                          "  if ( A > 10 ) A = 0\n";

    final String input2 = "\n" +
                          "#if A | B\n" +
                          "    a = MULT(10,20);\n" +
                          "#elsif $B\n" +
                          "    b = \"ZERO\"; s = REPEAT(*,10);\n" +
                          "#else\n" +
                          "    c = MULT($ONE,5); c = MULT($ONE,(5,6));If(D, d = 10;)\n" +
                          "#end\n";

    final String output1 = "\n" +
                           "\n" +
                           "  if ( my_a > 10 ) my_a = 0\n";

    final String outputA = "\n" +
                           "\n" +
                           "    a = 20 * 10;\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "\n";

    final String outputB = "\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "    b = \"ZERO\"; s = **********;\n" +
                           "\n" +
                           "\n" +
                           "\n";

    final String outputB2 = "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "    b = \"0\"; s = **********;\n" +
                            "\n" +
                            "\n" +
                            "\n";

    final String outputC = "\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "    c = 5 * 1; c = (5,6) * 1;\n" +
                           "\n";

    final String outputD = "\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "\n" +
                           "    c = 5 * 1; c = (5,6) * 1;d = 10;\n" +
                           "\n";

    final String sqlInput = "select A, B, C, Back(A)\n" +
                            "  from PEPE\n" +
                            "  where PEPE.A > \"X\"\n";

    final String sqlOutput = "select \"A\", \"B\", \"C\", pre(\"A\", \"B\", \"C\")\n" +
                             "  from \"PEPE\"\n" +
                             "  where \"PEPE\".\"A\" > \"X\"\n";

    final String wrongInput1 = "\n" +
                               "  #if A | B\n" +
                               "    a = MULT(10,20);\n" +
                               "  #if $B\n" +
                               "    b = ZERO;\n" +
                               "  #else\n" +
                               "    c = MULT($ONE,5);\n" +
                               "  #end\n";

    final String wrongInput2 = "\n" +
                               "  #elsif A | B\n" +
                               "    a = MULT(10,20);\n";

    final String wrongInput3 = "\n" +
                               "  #else\n" +
                               "    a = MULT(10,20);\n";

    final String wrongInput4 = "\n" +
                               "  #if A | B\n" +
                               "    a = MULT(10,20);\n" +
                               "  #elsif $B\n" +
                               "    b = ZERO;\n" +
                               "  #else\n" +
                               "    c = MULT($ONE,5);\n" +
                               "  #end\n" +
                               "  #end\n";

    //~ Methods ......................................................................................................................................

    @Test public void conditions() {
        assertThat(process(createReader(), createPreprocessor().define("A"))).isEqualTo(outputA);
        assertThat(process(createReader(), createPreprocessor().define("B"))).isEqualTo(outputA);
        assertThat(process(createReader(), createPreprocessor().define("$B"))).isEqualTo(outputB);
        assertThat(process(createReader(), createPreprocessor())).isEqualTo(outputC);
        assertThat(process(createReader(), createPreprocessor().define("D"))).isEqualTo(outputD);
        assertThat(process(createReader(), createPreprocessor().define("$B").expandStrings())).isEqualTo(outputB2);
    }

    @Test public void define() {
        final String result = process(new StringReader(input1), new Preprocessor());
        assertThat(result).isEqualTo(output1);
    }

    @Test public void errors() {
        final Preprocessor pp = createPreprocessor().define("A");

        testError(pp, wrongInput1, "Reached end of processing with an unclosed 'if' directive declared at line '2'.");

        testError(pp, wrongInput2, "Illegal 'elsif' directive at line '10' without previous 'if'.");
        testError(pp, wrongInput3, "Illegal 'else' directive at line '12' without previous 'if'.");
        testError(pp, wrongInput4, "Illegal 'end' directive at line '21' without previous 'if'.");
    }
    @Test public void expressions() {
        final Preprocessor pp = new Preprocessor().define("A").define("B");
        assertThat(pp.evaluate("A & B")).isTrue();
        assertThat(pp.evaluate("A & C")).isFalse();
        assertThat(pp.evaluate("A | C")).isTrue();
        assertThat(pp.evaluate("A & !C")).isTrue();
        assertThat(pp.evaluate("A & ! C")).isTrue();
        assertThat(pp.evaluate("!A | C")).isFalse();
        assertThat(pp.evaluate("!A | !C")).isTrue();
    }

    @Test public void quoteIds() {
        final Preprocessor preprocessor = new Preprocessor().escapeIds(Escape.QUOTE_UPPER_CASE).define("Back", "pre($1, $-2, $-1)");
        final String       result       = process(new StringReader(sqlInput), preprocessor);
        assertThat(result).isEqualTo(sqlOutput);
    }

    @Test public void recursion() {
        final Preprocessor pp = new Preprocessor().define("A", "B").define("B", "A");
        assertThat(process("A", pp)).isEqualTo("A");
    }

    private Preprocessor createPreprocessor() {
        return new Preprocessor().define("ZERO", "0")
               .define("$ONE", "1")
               .define("MULT", "$2 * $1")
               .define("REPEAT", args ->
                    nChars(args.get(0).charAt(0), parseInt(args.get(1))));
    }

    private StringReader createReader() {
        return new StringReader(input2);
    }

    //~ Methods ......................................................................................................................................

    private static String process(Reader reader, Preprocessor p) {
        return p.process(reader).mkString("", "\n", "\n");
    }
    private static String process(String str, Preprocessor p) {
        return p.process(listOf(str)).mkString("", "", "");
    }
    private static void testError(Preprocessor pp, String input, String message) {
        try {
            process(new StringReader(input), pp);
            Assertions.failBecauseExceptionWasNotThrown(Preprocessor.Exception.class);
        }
        catch (final Preprocessor.Exception e) {
            assertThat(e.getMessage()).isEqualTo(message);
        }
    }
}  // end class PreprocessorTest
