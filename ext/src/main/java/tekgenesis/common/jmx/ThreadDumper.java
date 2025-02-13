
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
import java.lang.management.LockInfo;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;

import javax.management.openmbean.CompositeData;

import org.jetbrains.annotations.NotNull;

/**
 * Parse a ThreadInfo[] and write it in a OutputStream.
 */
final class ThreadDumper {

    //~ Constructors .................................................................................................................................

    private ThreadDumper() {}

    //~ Methods ......................................................................................................................................

    /**
     * Save a Thread Dump.
     *
     * @param  threadInfos  The TreadInfo
     * @param  out          The output file
     */
    public static void dump(@NotNull final CompositeData[] threadInfos, @NotNull Writer out)
        throws IOException
    {
        for (final CompositeData compositeData : threadInfos) {
            final ThreadInfo threadInfo = ThreadInfo.from(compositeData);
            writeThreadInfo(threadInfo, out);
            writeLockInfo(threadInfo.getLockedSynchronizers(), out);
        }
    }

    private static void printThread(@NotNull ThreadInfo ti, @NotNull Writer out)
        throws IOException
    {
        final StringBuilder sb = new StringBuilder("\"" + ti.getThreadName() + "\"" + " Id=" + ti.getThreadId() + " in " + ti.getThreadState());

        if (ti.getLockName() != null) sb.append(" on lock=").append(ti.getLockName());
        if (ti.isSuspended()) sb.append(" (suspended)");
        if (ti.isInNative()) sb.append(" (running in native)");

        out.write(sb.toString());

        if (ti.getLockOwnerName() != null) out.write("\t owned by " + ti.getLockOwnerName() + " Id=" + ti.getLockOwnerId());
        out.write("\n");
    }

    private static void writeLockInfo(LockInfo[] locks, @NotNull Writer out)
        throws IOException
    {
        out.write("\tLocked Synchronizers: " + locks.length);
        for (final LockInfo li : locks) {
            out.write("\t - " + li.toString());
            out.write("\n");
        }
        out.write("\n");
    }

    private static void writeThreadInfo(@NotNull final ThreadInfo ti, @NotNull Writer out)
        throws IOException
    {
        printThread(ti, out);

        // print stack trace with locks
        final StackTraceElement[] stacktrace = ti.getStackTrace();
        final MonitorInfo[]       monitors   = ti.getLockedMonitors();

        for (int i = 0; i < stacktrace.length; i++) {
            final StackTraceElement ste = stacktrace[i];
            out.write("\t at ");
            out.write(ste.toString());
            out.write("\n");

            for (final MonitorInfo mi : monitors) {
                if (mi.getLockedStackDepth() == i) {
                    out.write("\t - locked ");
                    out.write(mi.toString());
                    out.write("\n");
                }
            }
        }
        out.write("\n");
    }
}  // end class ThreadDumper
