
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import org.junit.Test;

import tekgenesis.common.util.CharArraySequence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;

/**
 * User: emilio; Date: 12/16/11; Time: 12:47 PM;
 */

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class CharArraySequenceTest {

    //~ Methods ......................................................................................................................................

    @SuppressWarnings({ "EqualsBetweenInconvertibleTypes", "LiteralAsArgToStringEquals" })
    @Test public void charArraySequence() {
        final char[]            chars = { 'a', 'b', 'c', 'd' };
        final CharArraySequence c     = new CharArraySequence(chars);

        assertThat(c.length()).isEqualTo(4);
        assertThat(c.charAt(2)).isEqualTo('c');
        assertThat(c.equals("abcd")).isTrue();
        assertThat(c.equals("abcc")).isFalse();
        assertThat(c.equals("ab")).isFalse();
        assertThat(c.equals(10)).isFalse();

        assertThat(c.hashCode()).isEqualTo("abcd".hashCode());
        assertThat(c.toString()).isEqualTo("abcd");

        try {
            c.charAt(10);
            failBecauseExceptionWasNotThrown(IndexOutOfBoundsException.class);
        }
        catch (final IndexOutOfBoundsException e) {
            assertThat(e).hasMessage("index: 10");
        }

        try {
            c.charAt(-1);
            failBecauseExceptionWasNotThrown(IndexOutOfBoundsException.class);
        }
        catch (final IndexOutOfBoundsException e) {
            assertThat(e).hasMessage("index: -1");
        }

        try {
            c.subSequence(10, 12);
            failBecauseExceptionWasNotThrown(IndexOutOfBoundsException.class);
        }
        catch (final IndexOutOfBoundsException e) {
            assertThat(e).hasMessage(null);
        }

        try {
            c.subSequence(-1, 10);
            failBecauseExceptionWasNotThrown(IndexOutOfBoundsException.class);
        }
        catch (final IndexOutOfBoundsException e) {
            assertThat(e).hasMessage(null);
        }

        try {
            c.subSequence(20, 2);
            failBecauseExceptionWasNotThrown(IndexOutOfBoundsException.class);
        }
        catch (final IndexOutOfBoundsException e) {
            assertThat(e).hasMessage(null);
        }

        final CharSequence c1 = c.subSequence(2, 4);
        assertThat(c1).isEqualTo("cd");

        final CharArraySequence c3 = new CharArraySequence(c);
        assertThat(c3.subSequence(2, 4)).isEqualTo(c1);
    }  // end method charArraySequence
}  // end class CharArraySequenceTest
