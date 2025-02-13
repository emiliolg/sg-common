
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.websocket.util;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

import tekgenesis.common.logging.Logger;

/**
 * WebSocket Adapter.
 */
public class WebSocketAdapter implements WebSocketListener {

    //~ Instance Fields ..............................................................................................................................

    protected Session    session = null;
    private final Object lock    = new Object();
    private final Logger logger  = Logger.getLogger(WebSocketAdapter.class);

    //~ Methods ......................................................................................................................................

    public void onWebSocketBinary(byte[] payload, int offset, int len) {}

    public void onWebSocketClose(int statusCode, String reason) {
        logger.debug("onWebSocketClose(" + statusCode + ", " + reason + ")");
    }

    public void onWebSocketConnect(Session s) {
        synchronized (lock) {
            session = s;
            lock.notify();
        }
    }

    public void onWebSocketError(Throwable cause) {
        logger.debug("onWebSocketError: " + cause);
    }

    public void onWebSocketText(String message) {
        logger.debug("Unhandled message: " + message);
    }

    /** Send message. */
    public synchronized void send(String message)
        throws IOException
    {
        if (session == null) throw new IOException(NOT_CONNECTED);

        logger.debug("Sending message(size=" + message.length() + "): " + message + " to " + session.getRemoteAddress());
        final RemoteEndpoint remote = session.getRemote();
        remote.sendString(message);
    }

    /** Send binary. */
    public synchronized void sendBinary(ByteBuffer message)
        throws IOException
    {
        if (session == null) throw new IOException(NOT_CONNECTED);

        logger.debug("Sending binary message to " + session.getRemoteAddress());
        session.getRemote().sendBytes(message);
    }

    /** Send ping. */
    public synchronized void sendPing()
        throws IOException
    {
        if (session == null) throw new IOException(NOT_CONNECTED);
        final RemoteEndpoint remote = session.getRemote();
        remote.sendPing(EMPTY_BUFFER);
    }

    @Override public String toString() {
        return "WebSocket(" + (session != null ? session.getRemoteAddress() : "") + ")";
    }

    /** wait for connection. */
    public void waitConnection()
        throws InterruptedException
    {
        synchronized (lock) {
            if (session == null) lock.wait(TIMEOUT);
        }
    }

    //~ Static Fields ................................................................................................................................

    public static final String NOT_CONNECTED = "Not connected";
    public static final int    TIMEOUT       = 5000;

    private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(new byte[0]);
}  // end class WebSocketAdapter
