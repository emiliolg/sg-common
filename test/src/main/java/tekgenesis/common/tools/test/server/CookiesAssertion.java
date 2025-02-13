
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.tools.test.server;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.service.cookie.Cookie;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Allows overriding matching cookies behaviour.
 */
public interface CookiesAssertion {

    //~ Instance Fields ..............................................................................................................................

    CookiesAssertion DEFAULT_COOKIES_ASSERTION = new CookiesAssertion() {};

    //~ Methods ......................................................................................................................................

    /** Default matching of expected with actual cookies. */
    default void assertCookies(@NotNull List<Cookie> expected, @NotNull List<Cookie> actual)
        throws AssertionError
    {
        assertThat(actual).as("Cookies does not match").contains(expected.toArray(new Cookie[expected.size()]));
    }
}
