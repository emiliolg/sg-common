
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import org.jetbrains.annotations.Nullable;

import static tekgenesis.common.Predefined.cast;

/**
 * Used to return a result from a iterative process Valid values are:
 *
 * <ul>
 *   <li>next() meaning continue the iteration</li>
 *   <li>done() meaning end the iteration and return Option.none()</li>
 *   <li>done(value) meaning end the iteration and return Option.some(value)</li>
 * </ul>
 */
public class StepResult<T> {

    //~ Instance Fields ..............................................................................................................................

    private final boolean done;
    private final T       value;

    //~ Constructors .................................................................................................................................

    private StepResult(boolean done, @Nullable T value) {
        this.done  = done;
        this.value = value;
    }

    //~ Methods ......................................................................................................................................

    /** Returns true if the iteration is done. */
    public boolean isDone() {
        return done;
    }

    /** Returns the final value if the iteration is done. */
    public Option<T> getValue() {
        if (!done) throw new IllegalStateException("Iteration is not done");
        return Option.option(value);
    }

    //~ Methods ......................................................................................................................................

    /** Return a next() value. */
    public static <T> StepResult<T> done() {
        return cast(NONE);
    }
    /** Returns a done value. */
    public static <T> StepResult<T> done(T value) {
        return new StepResult<>(true, value);
    }

    /** Return a next() value. */
    public static <T> StepResult<T> next() {
        return cast(NEXT);
    }

    //~ Static Fields ................................................................................................................................

    private static final StepResult<Object> NEXT = new StepResult<>(false, null);
    public static final StepResult<Object>  NONE = new StepResult<>(true, null);
}  // end class StepResult
