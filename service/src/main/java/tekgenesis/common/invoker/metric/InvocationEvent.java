
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker.metric;

import tekgenesis.common.metric.RollingNumberEventType;

/**
 * Various states/events that can be captured in the {@link tekgenesis.common.metric.RollingNumber}.
 */
public enum InvocationEvent implements RollingNumberEventType {

    //~ Enum constants ...............................................................................................................................

    SUCCESS, FAILURE, TIMEOUT, SHORT_CIRCUITED, THREAD_POOL_REJECTED, SEMAPHORE_REJECTED, BAD_REQUEST, FALLBACK_SUCCESS, FALLBACK_FAILURE,
    FALLBACK_REJECTION, EXCEPTION_THROWN, EMIT, FALLBACK_EMIT, THREAD_EXECUTION, COLLAPSED, RESPONSE_FROM_CACHE, COLLAPSER_REQUEST_BATCHED,
    COLLAPSER_BATCH, THREAD_MAX_ACTIVE(false), COMMAND_MAX_ACTIVE(false);

    //~ Instance Fields ..............................................................................................................................

    private final boolean counter;

    //~ Constructors .................................................................................................................................

    InvocationEvent() {
        this(true);
    }

    InvocationEvent(boolean counter) {
        this.counter = counter;
    }

    //~ Methods ......................................................................................................................................

    /** True if event is maximum updated. */
    @Override public boolean isMaximum() {
        return !counter;
    }

    /** True if event is counter. */
    @Override public boolean isCounter() {
        return counter;
    }
}
