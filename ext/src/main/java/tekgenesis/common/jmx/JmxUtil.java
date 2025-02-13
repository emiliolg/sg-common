
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.jmx;

import java.io.IOException;
import java.io.Writer;

import javax.management.openmbean.CompositeData;

import org.jetbrains.annotations.NotNull;

import static java.lang.management.ManagementFactory.THREAD_MXBEAN_NAME;

/**
 * Wrapper class for Jmx Stuffs.
 */
public final class JmxUtil {

    //~ Constructors .................................................................................................................................

    private JmxUtil() {}

    //~ Methods ......................................................................................................................................

    /**
     * Generate a Thread Dump.
     *
     * @param  conn    JmxConnection
     * @param  writer  the output file
     */
    public static void generateThreadDump(@NotNull JmxEndpoint conn, @NotNull final Writer writer) {
        //J-
        try {
            final CompositeData[] compositeDatas = JmxInvokerImpl.invoker(conn)
                                                    .mbean(THREAD_MXBEAN_NAME)
                                                    .invoke("dumpAllThreads",
                                                            new String[]{boolean.class.getName(), boolean.class.getName()},
                                                            new Object[]{true, true});

            ThreadDumper.dump(compositeDatas, writer);
        } catch (final IOException e) {
            throw new JmxException(e);
        }
        //J+
    }
}  // end class JmxUtil
