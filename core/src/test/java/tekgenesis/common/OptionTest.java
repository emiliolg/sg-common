
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
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.collections.Immutables;
import tekgenesis.common.collections.Seq;
import tekgenesis.common.core.Option;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;

import static tekgenesis.common.Predefined.option;
import static tekgenesis.common.collections.Colls.immutable;
import static tekgenesis.common.core.Option.some;

/**
 * Option test.
 */
@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class OptionTest {

    //~ Methods ......................................................................................................................................

    @Test public void optionExceptions() {
        final Option<String> n = option(null);

        try {
            n.get();
            failBecauseExceptionWasNotThrown(NoSuchElementException.class);
        }
        catch (final NoSuchElementException e) {
            assertThat(e).hasMessage(null);
        }

        try {
            n.orElseThrow(NullPointerException::new);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        }
        catch (final NullPointerException e) {
            assertThat(e).hasMessage(null);
        }

        try {
            n.iterator().next();
            failBecauseExceptionWasNotThrown(NoSuchElementException.class);
        }
        catch (final NoSuchElementException e) {
            assertThat(e).hasMessage(Immutables.EMPTY_ITERATOR_MSG);
        }

        try {
            n.iterator().remove();
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final UnsupportedOperationException e) {
            assertThat(e).hasMessage(null);
        }

        try {
            Option.empty().get();
            failBecauseExceptionWasNotThrown(NoSuchElementException.class);
        }
        catch (final NoSuchElementException e) {
            assertThat(e).hasMessage(null);
        }

        try {
            Option.empty().getOrFail("Msg");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        }
        catch (final IllegalStateException e) {
            assertThat(e).hasMessage("Msg");
        }
    }  // end method optionExceptions

    @Test public void optionSupplierException() {
        final Option<String> n = option(null);

        try {
            n.orElseThrow(() -> new UnsupportedOperationException("Some runtime exception."));
            failBecauseExceptionWasNotThrown(UnsupportedOperationException.class);
        }
        catch (final Exception e) {
            assertThat(e).hasMessage("Some runtime exception.");
        }
    }

    @Test public void optionTest() {
        final Option<String> n = option(null);
        assertThat(n.orElse("other")).isEqualTo("other");
        assertThat(n.orElseGet(() -> "other")).isEqualTo("other");
        assertThat(n.or(() -> some("other")).get()).isEqualTo("other");
        assertThat(n.isPresent()).isFalse();
        assertThat(n.isEmpty()).isTrue();
        assertThat(n.toString()).isEqualTo(Option.OPTION_EMPTY);
        assertThat(n.getOrNull()).isEqualTo(null);

        assertThat(n.castTo(String.class)).isEqualTo(n);

        assertThat(n.map(String::length).isEmpty()).isTrue();
        assertThat(n.flatMap(s -> some(10)).isEmpty()).isTrue();
        assertThat(n.filter(s -> s.length() < 10).isEmpty()).isTrue();

        final Seq<String> list = immutable(n);
        assertThat(list).isEmpty();

        final Option<String> s = option("some");

        assertThat(s.get()).isEqualTo("some");
        assertThat(s.orElseThrow(NullPointerException::new)).isEqualTo("some");
        assertThat(s.getOrNull()).isEqualTo("some");
        assertThat(s.getOrFail("msg")).isEqualTo("some");

        assertThat(s.orElse("other")).isEqualTo("some");
        assertThat(s.orElseGet(() -> "other")).isEqualTo("some");
        assertThat(s.or(() -> some("other")).get()).isEqualTo("some");
        assertThat(s.isPresent()).isTrue();
        assertThat(s.isEmpty()).isFalse();
        assertThat(s.toString()).isEqualTo("some(some)");
        assertThat(s.castTo(Date.class)).isEqualTo(n);
        assertThat(s.castTo(String.class)).isEqualTo(s);
        assertThat(s.map(String::length).get()).isEqualTo(4);
        assertThat(s.flatMap(str -> some(str.length())).get()).isEqualTo(4);
        assertThat(s.flatMap(str -> Option.empty()).isEmpty()).isTrue();

        assertThat(s.filter(str -> str.length() < 10).get()).isEqualTo("some");

        final Seq<String> list2 = immutable(s);
        assertThat(list2).hasSize(1);

        assertThat(some("xx").get()).isEqualTo("xx");

        // noinspection RedundantStringConstructorCall
        assertThat(option("xx")).isEqualTo(some(new String("xx")));
        assertThat(option("xx")).isNotEqualTo(some("yy"));
        assertThat(option("xx")).isNotEqualTo("xx");

        assertThat(option("yy").hashCode()).isEqualTo(some("yy").hashCode());

        final Option<X> ox  = option(new X(1, 10, 20, 30));
        final Option<X> oxn = Option.empty();

        assertThat(ox.toList().flatMap(X::getM)).containsExactly(10, 20, 30);
        assertThat(oxn.toList().flatMap(X::getM)).isEmpty();
    }  // end method optionTest

    //~ Inner Classes ................................................................................................................................

    static class X {
        private final List<Integer> m;

        public X(int n, int... m) {
            this.m = new ArrayList<>();
            for (final int i : m)
                this.m.add(i);
        }

        public ImmutableList<Integer> getM() {
            return immutable(m);
        }
    }

    //
    // for (x <- optionX; m <- x.getM) yield m
    // optionX.flatMap( x -> x.getM().map(m))
}  // end class OptionTest
