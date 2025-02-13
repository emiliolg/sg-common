
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.tools.test.server;

import java.util.Collection;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.service.Headers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Allows overriding matching headers behaviour.
 */
public interface HeadersAssertion {

    //~ Instance Fields ..............................................................................................................................

    HeadersAssertion DEFAULT_HEADERS_ASSERTION = new HeadersAssertion() {};

    //~ Methods ......................................................................................................................................

    /**
     * Override matching of expected with actual headers. Use super(expected, actual) to match
     * expected and actual headers too.
     */
    default void assertHeaders(@NotNull Headers expected, @NotNull Headers actual)
        throws AssertionError
    {
        final Map<String, Collection<String>> map = expected.asMap();
        for (final Map.Entry<String, Collection<String>> header : map.entrySet()) {
            final Collection<String> values = header.getValue();
            assertThat(actual.getAll(header.getKey())).as("'%s' headers does not match", header.getKey())
                .contains(values.toArray(new String[values.size()]));
        }
    }
}
