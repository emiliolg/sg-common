
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.tools.test.server;

import java.util.EnumSet;

/**
 * Expectations configuration, to be used during rule construction with
 * {@link SgHttpServerRule#httpServerRule()}.
 */
public enum ExpectationsConfiguration {

    //~ Enum constants ...............................................................................................................................

    /**
     * Expectations will be consumed in order, requests will attempt match with next expectation
     * only This is default configuration, suppressed by {@link #UNORDERED}.
     */
    ORDERED,

    /**
     * Expectations will be consumed in any order, requests will attempt match with any expectation
     * If defined, suppress {@link #ORDERED} default configuration.
     */
    UNORDERED(ORDERED),

    /**
     * Expectations are discarded after positive match, further requests will match remaining
     * expectations This is default configuration, suppressed by {@link #REPEATABLE}.
     */
    UNIQUE,

    /**
     * Expectations are not discarded after positive match, further requests may match same
     * expectations If defined, suppress {@link #UNIQUE} default configuration.
     */
    REPEATABLE(UNIQUE),

    /**
     * Expectations left unmatched after rule completion will fail This is default configuration,
     * suppressed by {@link #IGNORE_REMAINING}.
     */
    FAIL_REMAINING,

    /**
     * Expectations left unmatched after rule completion will be ignored If defined, suppress
     * {@link #FAIL_REMAINING} default configuration.
     */
    IGNORE_REMAINING(FAIL_REMAINING),

    /**
     * Do not log expectations during server lifecycle This is default configuration, suppressed by
     * {@link #VERBOSE}.
     */
    SILENT,

    /**
     * Log expectations during server lifecycle If defined, suppress {@link #SILENT} default
     * configuration.
     */
    VERBOSE(SILENT);

    //~ Instance Fields ..............................................................................................................................

    private final ExpectationsConfiguration suppresses;

    //~ Constructors .................................................................................................................................

    ExpectationsConfiguration() {
        this(null);
    }

    ExpectationsConfiguration(ExpectationsConfiguration suppresses) {
        this.suppresses = suppresses;
    }

    //~ Methods ......................................................................................................................................

    void into(EnumSet<ExpectationsConfiguration> set) {
        if (suppresses != null) set.remove(suppresses);
        set.add(this);
    }
}
