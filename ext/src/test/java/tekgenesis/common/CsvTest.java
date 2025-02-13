
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import tekgenesis.common.collections.Colls;
import tekgenesis.common.io.CsvInput;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;

import static org.assertj.core.api.Assertions.assertThat;

import static tekgenesis.common.collections.Colls.listOf;

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class CsvTest {

    //~ Methods ......................................................................................................................................

    @Test public void csvInput() {
        final CsvInput<List<String>> csv = createCsvInput(data);
        assertThat(csv.readLine()).isEqualTo(listOf("a", "10", "Hello", "true"));

        assertThat(csv.readLine()).isEqualTo(listOf("b", "10", "Good Bye", "false"));

        final CsvInput<List<String>> csv2 = createCsvInput(data);
        assertThat(Colls.mkString(csv2)).isEqualTo("([a, 10, Hello, true], [b, 10, Good Bye, false])");
        csv.close();
    }

    @Test public void inputAsMap() {
        final CsvInput<Map<String, String>> csv = createCsvInput(data).asMap("letter", "number", "greeting", "boolean");

        final Map<String, String> map = csv.readLine();

        assertThat(map.get("letter")).isEqualTo("a");
        assertThat(map.get("greeting")).isEqualTo("Hello");

        final Map<String, String> map2 = csv.readLine();
        assertThat(map2.get("letter")).isEqualTo("b");
        assertThat(map2.get("greeting")).isEqualTo("Good Bye");

        final String m = Colls.mkString(createCsvInput(data).asMap("letter", "number", "greeting", "boolean"));
        assertThat(m).isEqualTo("({letter=a, number=10, greeting=Hello, boolean=true}, {letter=b, number=10, greeting=Good Bye, boolean=false})");
    }
    @Test public void inputAsObject() {
        final CsvInput<Obj> csv = createCsvInput(data).withMapper(l -> new Obj(l.get(0), parseInt(l.get(1)), l.get(2), parseBoolean(l.get(3))));

        final Obj map = csv.readLine();

        assertThat(map.letter).isEqualTo("a");
        assertThat(map.number).isEqualTo(10);
        assertThat(map.b).isTrue();
    }

    @Test public void tabbedInput() {
        final CsvInput<List<String>> csv = createCsvInput(spacedData).withSeparator(" ");

        assertThat(csv.readLine()).isEqualTo(listOf("a", "10", "Hello", "true"));

        assertThat(csv.readLine()).isEqualTo(listOf("b", "10", "Good Bye", "false"));

        final CsvInput<List<String>> csv2 = createCsvInput(data);
        assertThat(Colls.mkString(csv2)).isEqualTo("([a, 10, Hello, true], [b, 10, Good Bye, false])");
        csv.close();
    }

    //~ Methods ......................................................................................................................................

    private static CsvInput<List<String>> createCsvInput(String s) {
        return CsvInput.createCsvInput(new StringReader(s));
    }

    //~ Static Fields ................................................................................................................................

    private static final String data =  //
                                       "a,10,\"Hello\",true\n" +
                                       "b,10,\"Good Bye\",false";

    private static final String spacedData =  //
                                             "a 10 \"Hello\" true\n" +
                                             "b 10 \"Good Bye\" false";

    //~ Inner Classes ................................................................................................................................

    public static class Obj {
        boolean b;
        String  greeting;
        String  letter;
        int     number;

        public Obj(String letter, int number, String greeting, boolean b) {
            this.letter   = letter;
            this.number   = number;
            this.greeting = greeting;
            this.b        = b;
        }
    }
}  // end class CsvTest
