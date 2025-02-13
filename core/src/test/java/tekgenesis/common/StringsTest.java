
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import tekgenesis.common.collections.Colls;
import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.core.QName;
import tekgenesis.common.core.StrBuilder;
import tekgenesis.common.core.Strings;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;

import static tekgenesis.common.collections.Colls.listOf;
import static tekgenesis.common.core.QName.*;
import static tekgenesis.common.core.Strings.*;
import static tekgenesis.common.core.Tuple.tuple;

/**
 * User: emilio; Date: 12/16/11; Time: 12:47 PM;
 */
@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class StringsTest {

    //~ Methods ......................................................................................................................................

    @Test public void camelCase() {
        assertThat(toCamelCase("DATE_TIME")).isEqualTo("DateTime");
        assertThat(toCamelCase("check_box")).isEqualTo("checkBox");
        assertThat(fromCamelCase("DateTime")).isEqualTo("DATE_TIME");
        assertThat(fromCamelCase("fromURL")).isEqualTo("FROM_URL");
        assertThat(fromCamelCase("fromURL")).isEqualTo("FROM_URL");
        assertThat(fromCamelCase("des1adm")).isEqualTo("DES1ADM");
        assertThat(fromCamelCase("H_Adm")).isEqualTo("H_ADM");
    }

    @Test public void capitalize() {
        assertThat(capitalizeFirst("hello")).isEqualTo("Hello");
        assertThat(capitalizeFirst("")).isEqualTo("");
        assertThat(deCapitalizeFirst("Hello")).isEqualTo("hello");
        assertThat(deCapitalizeFirst("")).isEqualTo("");
    }
    @Test public void countTest() {
        assertThat(count("abba", 'a')).isEqualTo(2);
    }

    @Test public void decodeTest() {
        assertThat(decode("\\t\\n\\uABCD")).isEqualTo("\t\n\uABCD");
        assertThat(decode("\\r\\n\\f\\\\")).isEqualTo("\r\n\f\\");
        assertThat(decode("\"Hello\\\"")).isEqualTo("Hello\\");
        final String Q3 = "\"\"\"";
        assertThat(decode(Q3 + "Hello \"Dolly\" " + Q3)).isEqualTo("Hello \"Dolly\" ");

        try {
            decode("\\uZBCD");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Malformed unicode encoding: ZBCD");
        }
    }

    @Test public void encode() {
        assertThat(encodeChar('\n')).isEqualTo("\\n");
        assertThat(encodeChar('\t')).isEqualTo("\\t");
        assertThat(encodeChar('\r')).isEqualTo("\\r");
        assertThat(encodeChar('\f')).isEqualTo("\\f");
        assertThat(encodeChar('ñ')).isEqualTo("ñ");
    }

    @Test public void extractDigitsTest() {
        assertThat(extractDigits(" 1 2 3 ", 10)).isEqualTo("123");
        assertThat(extractDigits("12345678AB", 5)).isEqualTo("12345");
        assertThat(extractDigits("PEPE", 5)).isEqualTo("");
    }

    @Test public void findSubstringTest() {
        assertThat(findSubstring("1 2 3 ", "123")).isEqualTo(null);
        assertThat(findSubstring(" 1 2 123", "123")).isEqualTo(tuple(5, 8));
        assertThat(findSubstring("12 3 1", "12 3")).isEqualTo(tuple(0, 4));
        assertThat(findSubstring(" 1 22 3 ", "123")).isEqualTo(null);
        assertThat(findSubstring("1 2 3 ", "1 2 3")).isEqualTo(tuple(0, 5));

        assertThat(findSubstring("30.314.471", "30314471")).isEqualTo(tuple(0, 10));
        assertThat(findSubstring("30.314.471", "30.314.471")).isEqualTo(tuple(0, 10));
        assertThat(findSubstring("DNI 30.314.471", "30314471")).isEqualTo(tuple(4, 14));
        assertThat(findSubstring("DNI 30.314.471 Cliente", "30314471")).isEqualTo(tuple(4, 14));

        assertThat(findSubstring("ESJ-214", "ESJ214")).isEqualTo(tuple(0, 7));
        assertThat(findSubstring("ESJ-214", "ESJ-214")).isEqualTo(tuple(0, 7));
        assertThat(findSubstring("Patente ESJ-214", "ESJ214")).isEqualTo(tuple(8, 15));
        assertThat(findSubstring("Patente ESJ-214 Auto", "ESJ214")).isEqualTo(tuple(8, 15));

        assertThat(findSubstring("123 ", "1.2.3")).isEqualTo(tuple(0, 4));

        assertThat(findSubstring("lucas luppani 30.314.471", "lu.ca")).isEqualTo(tuple(0, 4));
        assertThat(findSubstring("López Gabeiras 30.314.471", "lopez gabeiras")).isEqualTo(tuple(0, 14));
    }

    @Test public void getterNameAndSetterName() {
        assertThat(getterName("method1", "boolean")).isEqualTo("isMethod1");
        assertThat(getterName("method1", "String")).isEqualTo("getMethod1");
        assertThat(getterName("", "boolean")).isEqualTo("is");
        assertThat(getterName("", "String")).isEqualTo("get");

        assertThat(setterName("")).isEqualTo("set");
        assertThat(setterName("method1")).isEqualTo("setMethod1");
    }

    @Test public void linesTest() {
        assertThat(lines("Hello World")).isEqualTo(singletonList("Hello World"));
        assertThat(lines("Hello\nWorld")).isEqualTo(asList("Hello", "World"));
        assertThat(lines(null)).hasSize(0);
    }

    @Test public void maxLengthTest() {
        assertThat(maxLength(asList("hello", "a", "long one"))).isEqualTo(8);
        assertThat(maxLength(null)).isEqualTo(0);
    }
    @Test public void nCharsTest() {
        assertThat(nChars('*', 5)).isEqualTo("*****");
    }

    @Test public void pluralizeTest() {
        assertThat(pluralize("Child")).isEqualTo("Children");
        assertThat(pluralize("child")).isEqualTo("children");
        assertThat(pluralize("This")).isEqualTo("These");
        assertThat(pluralize("this")).isEqualTo("these");
        assertThat(pluralize("kiss")).isEqualTo("kisses");
        assertThat(pluralize("phase")).isEqualTo("phases");
        assertThat(pluralize("lap")).isEqualTo("laps");
        assertThat(pluralize("cat")).isEqualTo("cats");
        assertThat(pluralize("boy")).isEqualTo("boys");
        assertThat(pluralize("chair")).isEqualTo("chairs");
    }

    @Test public void qualifyTest() {
        assertThat(extractName("A.B.C")).isEqualTo("C");
        assertThat(extractName("C")).isEqualTo("C");
        assertThat(extractName("")).isEqualTo("");

        assertThat(QName.extractQualification("A.B.C")).isEqualTo("A.B");
        assertThat(QName.extractQualification("C")).isEqualTo("");
        assertThat(extractQualification("C", "A.B")).isEqualTo("A.B");

        assertThat(qualify("X.Y", "A.B.C")).isEqualTo("A.B.C");
        assertThat(qualify("X.Y", "C")).isEqualTo("X.Y.C");
        final QName q1 = QName.createQName("a", "b");
        final QName q2 = QName.createQName("a.b");
        assertThat(q1.getName()).isEqualTo(q2.getName());
        assertThat(q1.getQualification()).isEqualTo(q2.getQualification());
        assertThat(QName.isQualified("a.x")).isTrue();
        assertThat(QName.isQualified("x")).isFalse();
    }

    @Test public void quoted() {
        final String s = "a\"quoted\"c";
        assertThat(Strings.quoted(s, '\\')).isEqualTo("\"a\\\"quoted\\\"c\"");
        assertThat(Strings.quoted(s, '"')).isEqualTo("\"a\"\"quoted\"\"c\"");
    }

    @Test public void singularizeTest() {
        assertThat(singularize("Children")).isEqualTo("Child");
        assertThat(singularize("children")).isEqualTo("child");
        assertThat(singularize("These")).isEqualTo("This");
        assertThat(singularize("these")).isEqualTo("this");
        assertThat(singularize("kisses")).isEqualTo("kiss");
        assertThat(singularize("laps")).isEqualTo("lap");
        assertThat(singularize("cats")).isEqualTo("cat");
        assertThat(singularize("boys")).isEqualTo("boy");
        assertThat(singularize("chairs")).isEqualTo("chair");
    }

    @Test public void spacesTest() {
        assertThat(spaces(10)).isEqualTo("          ");
        assertThat(spaces(0)).isEqualTo("");
        try {
            spaces(-10);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("n = -10");
        }
    }

    @Test public void splitJoin() {
        assertThat(split("A,B,C", ',')).isEqualTo(listOf("A", "B", "C"));

        final String                str1  = "A1,B\\,2,C3";
        final ImmutableList<String> list1 = listOf("A1", "B,2", "C3");

        assertThat(splitNotEscaped(str1, ',')).isEqualTo(list1);
        assertThat(join(list1, ',')).isEqualTo(str1);
        final String                example2  = "Emilio\\:Lopez Gabeiras:2341";
        final ImmutableList<String> example2l = listOf("Emilio:Lopez Gabeiras", "2341");
        assertThat(splitNotEscaped(example2, ':')).isEqualTo(example2l);
        assertThat(join(example2l, ':')).isEqualTo(example2);

        final String                str2  = ",B\\,2,";
        final ImmutableList<String> list2 = listOf("", "B,2", "");
        assertThat(splitNotEscaped(str2, ',')).isEqualTo(list2);
        assertThat(join(list2, ',')).isEqualTo(str2);

        assertThat(join(listOf("A,B", null, ""), ',')).isEqualTo("A\\,B,null,");

        assertThat(splitToArray(str1, "", ",", "", 5)).containsExactly("A1", "B,2", "C3", "", "");
        assertThat(splitToArray(str1, "", ",", "", 2)).containsExactly("A1", "B,2");
        assertThat(splitToArray(str1, "(", ",", ")", 5)).containsExactly("A1", "B,2", "C3", "", "");
        assertThat(splitToArray("(A1,B\\,2,C3)", "(", ",", ")", 5)).containsExactly("A1", "B,2", "C3", "", "");

        assertThat(splitToArray("A1:B:C", 3)).containsExactly("A1", "B", "C");
        assertThat(splitToArray("A1:B\\:1:C", 3)).containsExactly("A1", "B:1", "C");

        assertThat(escapeCharOn("B:1:2", ':')).isEqualTo("B\\:1\\:2");

        assertThat(splitToArray("A1:" + escapeCharOn("B:1:2", ':'), 2)).containsExactly("A1", "B:1:2");
    }

    @Test public void strBuilder() {
        final StrBuilder s = new StrBuilder();
        s.append("Hello");
        s.append(" ");
        s.append("World");
        assertThat(s.toString()).isEqualTo("Hello World");
        assertThat(s.quote("Cup").toString()).isEqualTo("Hello World\"Cup\"");

        final List<String> list = asList("a", "b", "c");

        final StrBuilder s2 = new StrBuilder();
        s2.append("[").append(list).append("]");
        assertThat(s2.toString()).isEqualTo("[a,b,c]");

        final StrBuilder s3 = new StrBuilder();
        s3.append("[");
        for (final int a : asList(1, 2, 3))
            s3.appendElement(a, ":");
        s3.append("]");
        s3.startCollection();
        s3.append("(");
        for (final int a : asList(1, 2, 3))
            s3.appendElement(a, ";");
        s3.append(")");

        assertThat(s3.build()).isEqualTo("[1:2:3](1;2;3)");

        final StrBuilder s4 = new StrBuilder();
        s4.append(list, " -> ");
        assertThat(s4.toString()).isEqualTo("a -> b -> c");

        final StrBuilder s5 = new StrBuilder();
        for (final String str : asList("A1", "A:1", "A::2"))
            s5.appendEscapedElement(str, ':');
        assertThat(s5.toString()).isEqualTo("A1:A\\:1:A\\:\\:2");
    }

    @Test public void stripAccentsTest() {
        assertThat(stripAccents("")).isEqualTo("");
        assertThat(stripAccents("Ñandú")).isEqualTo("Nandu");
        assertThat(stripAccents("Güemes")).isEqualTo("Guemes");
        assertThat(stripAccents("TekGénesis™")).isEqualTo("TekGenesis");

        // Verify that returns the same String when it doesn't have accents;
        final String s = "Ascii";
        assertThat(stripAccents(s)).isSameAs(s);
    }

    @Test public void testReplaceLast() {
        assertThat(replaceLast("some.g.SomeClass", ".g.", ".")).isEqualTo("some.SomeClass");
    }

    @Test public void truncateTest() {
        assertThat(truncate("Hello", "-", 20)).isEqualTo("Hello");
        assertThat(truncate("Hello, Good Bye and Etc", "", 20)).isEqualTo("Hello, Good BC2C433C");
        assertThat(truncate("Hello, Good Bye and Etc", "@", 20)).isEqualTo("Hello, Good@BC2C433C");
        assertThat(truncate("GOOD", "MORNING", "_", 20)).isEqualTo("GOOD_MORNING");
        assertThat(truncate("GOOD_MORNING", "VIETNAM", "_", 19)).isEqualTo("GOOD_334c16_VIETNAM");
        assertThat(truncate("GOOD_MORNING", "PEOPLE_OF_VIETNAM", "::", 20)).isEqualTo("GOOD::aa3801::PEOPLE");
        final ArrayList<String> list = new ArrayList<>();
        list.add("GOOD_MORNING");
        list.add("PEOPLE_OF_VIETNAM");
        assertThat(truncate(list, 4)).containsExactly("GOOD", "PEOP");
        assertThat(truncate((List<String>) Colls.immutable(list), 4)).containsExactly("GOOD", "PEOP");
        final List<String> nullList = null;
        assertThat(truncate(nullList, 2)).isNull();
    }

    @Test public void uncommentTextTest() {
        assertThat(unCommentText("/** Hello      */")).isEqualTo("Hello");
        assertThat(unCommentText("/** \n*Hello\n*\n      */")).isEqualTo("Hello");
        assertThat(unCommentText("/** \n*Hello\n*World\n      */")).isEqualTo("Hello\nWorld");
    }

    @Test public void unquoteTest() {
        assertThat(unquote("\"Hello World\"")).isEqualTo("Hello World");
        assertThat(unquote("'This is a valid expression'")).isEqualTo("This is a valid expression");
    }

    @Test public void uppercase() {
        assertThat(isUpperCase("A_A23")).isTrue();
        assertThat(isUpperCase("A_a23")).isFalse();

        assertThat(isLowerCase("a_a23")).isTrue();
        assertThat(isUpperCase("A_a23")).isFalse();
    }

    @Test public void verifyHexadecimalColor() {
        assertThat(verifyHexColor("#ffffff")).isTrue();
        assertThat(verifyHexColor("#zabbcc")).isFalse();
        assertThat(verifyHexColor("#zabbcc")).isFalse();
        assertThat(verifyHexColor("#0000000")).isFalse();
        assertThat(verifyHexColor("#0000000")).isFalse();
        assertThat(verifyHexColor("#ac8b01")).isTrue();
        assertThat(verifyHexColor("#ac8b01")).isTrue();
        assertThat(verifyHexColor("#AC8B01")).isTrue();
        assertThat(verifyHexColor("#AC8B01")).isTrue();
        assertThat(verifyHexColor("#cc00FF")).isTrue();
        assertThat(verifyHexColor("#cc00FF")).isTrue();
        assertThat(verifyHexColor("cc00FF")).isFalse();
        assertThat(verifyHexColor("cc00FF")).isFalse();
        assertThat(verifyHexColor("#fff")).isTrue();
        assertThat(verifyHexColor("#fff")).isTrue();
        assertThat(verifyHexColor("#AAA")).isTrue();
        assertThat(verifyHexColor("#AAA")).isTrue();
        assertThat(verifyHexColor("#f403")).isFalse();
        assertThat(verifyHexColor("#f403")).isFalse();
        assertThat(verifyHexColor("#Jose Gonzalez")).isFalse();
        assertThat(verifyHexColor("#Jose Gonzalez")).isFalse();
        assertThat(verifyHexColor("#f0")).isFalse();
        assertThat(verifyHexColor("#f0")).isFalse();
    }

    @Test public void word() {
        assertThat(toWords("TEK_GENESIS")).isEqualTo("Tek Genesis");
        assertThat(toWords("DATE_TIME")).isEqualTo("Date Time");
        assertThat(toWords("FROM_URL")).isEqualTo("From Url");
        assertThat(toWords("CREATE")).isEqualTo("Create");
    }

    @Test public void isBlankTest() {
        assertThat(isBlank(null)).isTrue();
        assertThat(isBlank(" aaa ")).isFalse();
        assertThat(isBlank(" \t")).isTrue();
        assertThat(isBlank(" \u00A0 ")).isTrue();
    }
}  // end class StringsTest
