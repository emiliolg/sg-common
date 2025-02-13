
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.jmx;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.command.AbstractCommand;
import tekgenesis.common.command.FallbackCommand;
import tekgenesis.common.invoker.HttpInvoker;
import tekgenesis.common.invoker.exception.InvokerApplicationException;
import tekgenesis.common.invoker.exception.InvokerConnectionException;
import tekgenesis.common.invoker.exception.InvokerInvocationException;

/**
 * Jmx InvokerCommand Impl.
 */
public class JmxInvokerCommandImpl<T> extends AbstractCommand<T> implements JmxInvokerCommand<T> {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final JmxInvocation<T> invocation;

    @Nullable private JmxInvokerImpl invoker;

    //~ Constructors .................................................................................................................................

    JmxInvokerCommandImpl(@NotNull final JmxInvocation<T> invocation) {
        this.invocation = invocation;
        invoker         = null;
    }

    //~ Methods ......................................................................................................................................

    @Override public T get()
        throws InvokerApplicationException, InvokerInvocationException, InvokerConnectionException
    {
        return execute();
    }

    @Override public FallbackCommand<T> onErrorFallback(@NotNull Function<Throwable, T> fallback) {
        // getFallback().onError(f);
        return this;
    }

    @Override protected T run() {
        return invocation.invokeUsing(getInvoker());
    }

    @Override protected String getThreadPoolKey() {
        return "JmxInvokerCommand";
    }

    /** Specify {@link HttpInvoker invoker} to perform invocation command with. */
    JmxInvokerCommand<T> withInvoker(@NotNull JmxInvokerImpl i) {
        invoker = i;
        return this;
    }

    @NotNull
    @SuppressWarnings("DuplicateStringLiteralInspection")
    JmxInvokerImpl getInvoker() {
        if (invoker == null) throw new IllegalStateException("Invoker hasn't been specified!");
        return invoker;
    }
}  // end class JmxInvokerCommandImpl
