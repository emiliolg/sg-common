
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
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import tekgenesis.common.core.Enumeration;
import tekgenesis.common.exception.ApplicationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;

import static tekgenesis.common.Predefined.*;

/**
 * User: emilio; Date: 12/16/11; Time: 12:08 PM;
 */

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class PredefinedTest {

    //~ Methods ......................................................................................................................................

    @Test public void compareTest() {
        assertThat(compare(null, null)).isZero();
        assertThat(compare("xx", "xx")).isZero();
        assertThat(compare("yy", "yy")).isZero();
        assertThat(compare(null, "xx")).isNegative();
        assertThat(compare("xx", null)).isPositive();
        assertThat(compare("xx", "zz")).isNegative();
    }

    @SuppressWarnings("RedundantStringConstructorCall")
    @Test public void equalsTest() {
        assertThat(equal(null, "xx")).isFalse();
        assertThat(equal("xx", null)).isFalse();
        assertThat(equal("xx", "yy")).isFalse();
        assertThat(equal(null, null)).isTrue();
        assertThat(equal("xx", "xx")).isTrue();
        final String xx = new String("xx");
        assertThat(equal("xx", xx)).isTrue();
        assertThat(equal(new ArrayList<>(), new ArrayList<>())).isTrue();
    }

    @Test public void hashAll() {
        assertThat(hashCodeAll("xx")).isEqualTo("xx".hashCode());
        assertThat(hashCodeAll(null)).isZero();
        assertThat(hashCodeAll("xx", "yy")).isEqualTo(122912);
        assertThat(hashCodeAll("yy", "xx")).isEqualTo(123872);
        assertThat(hashCodeAll("xx", null, "yy")).isEqualTo(3694112);
        assertThat(hashCodeAll("xx", "yy", null)).isEqualTo(3810272);
        assertThat(hashCodeAll(null, "xx")).isEqualTo(3840);

        assertThat(hashCodeAll(new ArrayList<>())).isEqualTo(1);
        final List<String> list = new ArrayList<>();
        list.add("hello");
        list.add("world");
        assertThat(hashCodeAll(list)).isEqualTo(-1107615551);
    }
    @Test public void mapNullableTest() {
        final String s = "aaa";
        assertThat(mapNullable(s, String::length)).isEqualTo(3);
        final String n = null;
        assertThat(mapNullable(n, String::length)).isNull();
    }

    @Test public void minMax() {
        assertThat(min("bb", "aa")).isEqualTo("aa");
        assertThat(max("aa", "bb")).isEqualTo("bb");
    }

    @Test public void notNullTest() {
        assertThat(notNull("a", "b")).isEqualTo("a");
        assertThat(notNull(null, "b")).isEqualTo("b");
        assertThat(notNull("a", () -> "b")).isEqualTo("a");
        assertThat(notNull(null, () -> "b")).isEqualTo("b");
        assertThat(notNull((Integer) null)).isZero();
        try {
            ensureNotNull(null, IllegalArgumentException::new);
            failBecauseExceptionWasNotThrown(IllegalAccessException.class);
        }
        catch (final IllegalArgumentException ignore) {}

        try {
            ensureNotNull(null, "Throw NPE");
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        }
        catch (final NullPointerException e) {
            assertThat(e).hasMessage("Throw NPE");
        }

        @NotNull final String a = ensureNotNull("a", Message.TEST_1);
        assertThat(a).isEqualTo("a");
        try {
            ensureNotNull(null, Message.TEST_1);
            failBecauseExceptionWasNotThrown(ApplicationException.class);
        }
        catch (final ApplicationException e) {
            assertThat(e.getEnumeration()).isEqualTo(Message.TEST_1);
        }

        @NotNull final String b = ensureNotNull("a", Message.TEST_2, "test");
        assertThat(b).isEqualTo("a");
        try {
            ensureNotNull(null, Message.TEST_2, "test");
            failBecauseExceptionWasNotThrown(ApplicationException.class);
        }
        catch (final ApplicationException e) {
            assertThat(e.getEnumeration()).isEqualTo(Message.TEST_2);
            assertThat(e.getArguments().isEmpty()).isFalse();
            assertThat(e.getArguments().get()).hasSize(1);
        }
    }  // end method notNullTest

    @Test public void toStringBuilder() {
        final String str1 = createToStringBuilder("Test").add("field1", 10).add("field2", "xxx").build();

        assertThat(str1).isEqualTo("Test(field1=10, field2=xxx)");

        final String str2 = createToStringBuilder("Point").add(10).add(100.0).build();

        assertThat(str2).isEqualTo("Point(10, 100.0)");

        final String str3 = createToStringBuilder("Vec").add(new Object[] { 10, 20, 30, 40 }).build();

        assertThat(str3).isEqualTo("Vec([10, 20, 30, 40])");

        final String str4 = createToStringBuilder("Vec2").add(new Object[] { 10, "xxx", null, new Object[] { 10, null, 20 } }).build();

        assertThat(str4).isEqualTo("Vec2([10, xxx, null, [10, null, 20]])");

        final List<Integer> l    = Arrays.asList(10, 20, 30);
        final String        str5 = createToStringBuilder("List").add("list", l).build();

        assertThat(str5).isEqualTo("List(list=[10, 20, 30])");
    }

    //~ Enums ........................................................................................................................................

    enum Message implements Enumeration<Message, String> {
        TEST_1, TEST_2;

        @Override public int index() {
            return ordinal();
        }

        /** Returns the enum primary key. */
        @NotNull public final String key() {
            return name();
        }
    }
}  // end class PredefinedTest
