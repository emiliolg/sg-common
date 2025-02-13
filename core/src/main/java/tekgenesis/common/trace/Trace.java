
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.trace;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Trace interface used for internal/external services like NewRelic.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Trace {
    /**
     * Metric name.
     */
    String metricName() default NULL;

    /**
     * Dispatcher?
     */
    boolean dispatcher() default false;

    /**
     * Tracer factory name.
     */
    String tracerFactoryName() default NULL;

    //~ Static Fields ................................................................................................................................

    @SuppressWarnings("UnnecessaryInterfaceModifier")
    public static final String NULL = "";
}
