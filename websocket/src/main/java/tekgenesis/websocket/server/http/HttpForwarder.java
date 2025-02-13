
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.websocket.server.http;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import tekgenesis.common.core.Times;
import tekgenesis.common.logging.Logger;
import tekgenesis.websocket.util.KeepAlive;

/**
 * HttpForwarder.
 */
public class HttpForwarder {

    //~ Instance Fields ..............................................................................................................................

    private final Logger logger        = Logger.getLogger(HttpForwarder.class);
    private final Object reconnectLock = new Object();
    private boolean      running       = true;

    private final String sourceURL;
    private final String targetURL;
    private int          timeout = TIMEOUT;

    //~ Constructors .................................................................................................................................

    /** Constructor. */
    public HttpForwarder(String sourceURL, String targetURL, int timeout) {
        this.sourceURL = sourceURL;
        this.targetURL = targetURL;
        this.timeout   = timeout;
    }

    //~ Methods ......................................................................................................................................

    /** Start. */
    public void start()
        throws Exception
    {
        final URI socketURL = new URI(sourceURL);
        new Thread(() -> runForwarder(socketURL)).start();
    }  // end method start

    /** stop. */
    public void stop() {
        synchronized (reconnectLock) {
            running = false;
            reconnectLock.notify();
        }
    }

    /** Returns timeout in ms. */
    public int getTimeout() {
        return timeout;
    }

    /** Set timeout in ms. */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    private void runForwarder(final URI socketURL) {
        while (running) {
            WebSocketClient client = null;
            try {
                client = new WebSocketClient();
                client.setMaxIdleTimeout(timeout * 1000);

                client.start();
                logger.info("HttpForwarder Connecting to " + targetURL);
                final HttpForwarderSocket socket = new HttpForwarderSocket(targetURL, timeout * 1000);
                client.connect(socket, socketURL, new ClientUpgradeRequest());

                KeepAlive.ping(socket, () -> {
                        synchronized (reconnectLock) {
                            reconnectLock.notify();
                        }
                    });

                synchronized (reconnectLock) {
                    reconnectLock.wait();
                    socket.close();
                }
            }
            catch (final Exception e) {
                logger.error(e);
            }
            finally {
                try {
                    if (client != null) client.stop();
                }
                catch (final Exception e) {
                    // ignore
                }
            }
        }
    }

    //~ Methods ......................................................................................................................................

    /** Main method. */
    public static void main(String[] args)
        throws Exception
    {
        final List<String> argsList = Arrays.asList(args);

        main(argsList);
    }

    /** Main method. */
    public static void main(List<String> argsList)
        throws Exception
    {
        if (argsList.size() != 2) {
            System.err.println("Usage: forward <sourceSocket> <httpTargetURL>");
            System.err.println("e.g.: forward ws://exchange-server:9090/backend http://localhost:8080");
            System.exit(1);
        }

        final String sourceURL = argsList.get(0);  // web socket
        final String targetURL = argsList.get(1);

        final HttpForwarder backend = new HttpForwarder(sourceURL, targetURL, (int) Times.MILLIS_MINUTE);
        backend.start();

        System.out.println("Receiving traffic from " + sourceURL + ". Sending to " + targetURL);

        // wait forever ...
        synchronized (HttpForwarder.class) {
            HttpForwarder.class.wait();
        }
    }

    //~ Static Fields ................................................................................................................................

    private static final int TIMEOUT = 300;

    public static final int SIXTY_THOUSAND = 60000;
}  // end class HttpForwarder
