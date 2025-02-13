
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.command;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

/**
 * Represents ways to fallback exception and errors on commands.
 */
public interface FallbackCommand<T> {

    //~ Methods ......................................................................................................................................

    /**
     * If a {@link Command command} method fails in any way then the specified function will be
     * invoked to provide an opportunity to return a fallback response. This should be a static or
     * cached result that can immediately be returned upon failure.
     */
    FallbackCommand<T> onErrorFallback(@NotNull Function<Throwable, T> fallback);
}
