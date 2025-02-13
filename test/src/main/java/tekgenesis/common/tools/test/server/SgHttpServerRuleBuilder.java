
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
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.core.Builder;
import tekgenesis.common.core.Option;

import static tekgenesis.common.core.Option.some;
import static tekgenesis.common.tools.test.server.ExpectationsConfiguration.*;

/**
 * Builder used to construct {@link SgHttpServerRule rules}. See
 * {@link SgHttpServerRule#httpServerRule()}.
 */
public class SgHttpServerRuleBuilder implements Builder<SgHttpServerRule> {

    //~ Instance Fields ..............................................................................................................................

    private final EnumSet<ExpectationsConfiguration> configuration;

    private Option<Consumer<SgHttpServerRule>> onShutdown = Option.empty();
    private Option<Consumer<SgHttpServerRule>> onStart    = Option.empty();

    private int port = DEFAULT_PORT;

    //~ Constructors .................................................................................................................................

    /** Private constructor, use {@link SgHttpServerRule#httpServerRule()} instead. */
    SgHttpServerRuleBuilder() {
        // Setup default configuration
        configuration = EnumSet.of(ORDERED, UNIQUE, FAIL_REMAINING, SILENT);
    }

    //~ Methods ......................................................................................................................................

    @Override public SgHttpServerRule build() {
        return new SgHttpServerRule(port, configuration) {
            @Override protected void before()
                throws Throwable
            {
                super.before();
                for (final Consumer<SgHttpServerRule> block : onStart)
                    block.accept(this);
            }
            @Override protected void after() {
                super.after();
                for (final Consumer<SgHttpServerRule> block : onShutdown)
                    block.accept(this);
            }
        };
    }

    /**
     * Configure to ignore left unmatched expectations. See
     * {@link ExpectationsConfiguration#IGNORE_REMAINING}
     */
    public SgHttpServerRuleBuilder ignoreRemaining() {
        return configuration(IGNORE_REMAINING);
    }

    /** Block to be called on server onShutdown. */
    public SgHttpServerRuleBuilder onShutdown(@NotNull final Consumer<SgHttpServerRule> shutdown) {
        onShutdown = some(shutdown);
        return this;
    }

    /** Block to be called on server start. */
    public SgHttpServerRuleBuilder onStart(@NotNull final Consumer<SgHttpServerRule> start) {
        onStart = some(start);
        return this;
    }

    /** Configure to repeat expectations. See {@link ExpectationsConfiguration#REPEATABLE} */
    public SgHttpServerRuleBuilder repeatable() {
        return configuration(REPEATABLE);
    }

    /**
     * Configure to consume expectations on any order. See
     * {@link ExpectationsConfiguration#UNORDERED}
     */
    public SgHttpServerRuleBuilder unordered() {
        return configuration(UNORDERED);
    }

    /** Configure verbose expectations. See {@link ExpectationsConfiguration#VERBOSE} */
    public SgHttpServerRuleBuilder verbose() {
        return configuration(VERBOSE);
    }

    /** Update server default port {@link #DEFAULT_PORT}. */
    public SgHttpServerRuleBuilder withPort(int p) {
        port = p;
        return this;
    }

    /** Configure expectations behaviour. */
    private SgHttpServerRuleBuilder configuration(ExpectationsConfiguration c) {
        c.into(configuration);
        return this;
    }

    //~ Static Fields ................................................................................................................................

    private static final int DEFAULT_PORT = 8224;
}  // end class SgHttpServerRuleBuilder
