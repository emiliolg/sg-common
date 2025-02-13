
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.util.EnumSet;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import tekgenesis.common.collections.OrderedEnumSet;
import tekgenesis.common.core.Enumeration;
import tekgenesis.common.core.enumeration.Enumerations;

import static java.util.Arrays.asList;

import static org.assertj.core.api.Assertions.assertThat;

import static tekgenesis.common.EnumerationsTest.Color.*;
import static tekgenesis.common.core.enumeration.Enumerations.*;

/**
 * User: emilio; Date: 12/16/11; Time: 12:08 PM;
 */

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class EnumerationsTest {

    //~ Methods ......................................................................................................................................

    @Test public void enumerationTest() {
        final Color red = Enumerations.valueOf(Color.class, "RED");
        assertThat(red).isEqualTo(RED);

        final String str = asString(asList(red, BLUE));

        assertThat(str).isEqualTo("RED,BLUE");

        final EnumSet<Color> set = enumSet(Color.class, str);

        assertThat(set).contains(BLUE);
        assertThat(set).contains(RED);
        assertThat(set).doesNotContain(GREEN);
        assertThat(Enumerations.asLong(set)).isEqualTo(0b101L);
        assertThat(Enumerations.longToSet(Color.class, 0b101L)).isEqualTo(set);
    }

    @Test public void orderedEnumTest() {
        final OrderedEnumSet<Color> set = new OrderedEnumSet<>(GREEN, RED);
        assertThat(set).contains(RED);
        assertThat(set).contains(GREEN);
        assertThat(set).doesNotContain(BLUE);

        assertThat(set.size()).isEqualTo(2);
        assertThat(set).containsExactly(GREEN, RED);
    }

    //~ Enums ........................................................................................................................................

    /**
     * A Test Enum.
     */
    enum Color implements Enumeration<Color, String> {
        RED, GREEN, BLUE;

        @Override public int index() {
            return ordinal();
        }

        @Override public String key() {
            return name();
        }

        @NotNull @Override public String label() {
            return name().toLowerCase();
        }
    }
}  // end class EnumerationsTest
