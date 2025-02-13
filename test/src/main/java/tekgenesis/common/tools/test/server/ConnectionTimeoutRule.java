
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.tools.test.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.rules.ExternalResource;

import tekgenesis.common.core.Constants;

/**
 * Rule to simulate ConnectionTimeout.
 */
public class ConnectionTimeoutRule extends ExternalResource {

    //~ Instance Fields ..............................................................................................................................

    private ServerSocket  serverSocket = null;
    private final boolean useIpTables;

    //~ Constructors .................................................................................................................................

    /** Default constructor. */
    public ConnectionTimeoutRule() {
        useIpTables = "Linux".equals(System.getProperty("os.name")) && new File(SBIN_IPTABLES).exists();
    }

    //~ Methods ......................................................................................................................................

    /** Get connection url. */
    public String connectionUrl() {
        return Constants.HTTP_LOCALHOST + serverSocket.getLocalPort();
    }

    @Override protected void after() {
        if (useIpTables) {
            try {
                new ProcessBuilder(SBIN_IPTABLES,
                    "-D",
                    Constants.INPUT,
                    "-p",
                    "tcp",
                    DPORT,
                    String.valueOf(serverSocket.getLocalPort()),
                    "-j",
                    "DROP").start().waitFor();
            }
            catch (final IOException | InterruptedException e) {
                // ignore
            }
        }
        if (!serverSocket.isClosed()) {
            try {
                serverSocket.close();
            }
            catch (final IOException e) {
                // ignore
            }
        }
    }

    @Override protected void before()
        throws Throwable
    {
        serverSocket = new ServerSocket(0, 1);
        new Socket().connect(serverSocket.getLocalSocketAddress());
        if (useIpTables) {
            try {
                new ProcessBuilder(SBIN_IPTABLES,
                    "-A",
                    Constants.INPUT,
                    "-p",
                    "tcp",
                    DPORT,
                    String.valueOf(serverSocket.getLocalPort()),
                    "-j",
                    "DROP").start().waitFor();
            }
            catch (final IOException | InterruptedException e) {
                // ignore
            }
        }
    }

    //~ Static Fields ................................................................................................................................

    private static final String SBIN_IPTABLES = "/sbin/iptables";
    private static final String DPORT         = "--dport";
}  // end class ConnectionTimeoutRule
