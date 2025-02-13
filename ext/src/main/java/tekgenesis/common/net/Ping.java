
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.core.Times;
import tekgenesis.common.logging.Logger;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Ping utility.
 */
public final class Ping {

    //~ Constructors .................................................................................................................................

    private Ping() {}

    //~ Methods ......................................................................................................................................

    /**
     * Ping to a specific host port.
     *
     * @param   address  The InetAddress
     * @param   port     the port
     *
     * @return  true or false if the ping is ok
     */
    public static boolean ping(@NotNull final InetAddress address, final int port) {
        return ping(address, port, (int) (10 * Times.MILLIS_SECOND));
    }

    /**
     * Pings a HTTP URL sending a HEAD request and returns <code>true</code> if the response code is
     * in the 200-399 range.
     *
     * @param   urlString  The HTTP URL to be pinged.
     * @param   timeout    The timeout in millis for both the connection timeout and the response
     *                     read timeout. Note that the total timeout is effectively two times the
     *                     given timeout.
     *
     * @return  <code>true</code> if the given HTTP URL has returned response code 200-399 on a HEAD
     *          request within the given timeout, otherwise <code>false</code>.
     */
    @SuppressWarnings({ "MagicNumber", "DuplicateStringLiteralInspection" })
    public static boolean ping(@NotNull final String urlString, final int timeout) {
        final String urlStr = urlString.replaceFirst("https", "http");  // avoid invalid SSL certificates.

        try {
            final URL url = new URL(urlStr);
            logger.debug("Ping to '%s", url);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("HEAD");
            final int responseCode = connection.getResponseCode();
            logger.debug("Ping response '%d", responseCode);
            return HTTP_OK <= responseCode && responseCode <= 500;
        }
        catch (final IOException exception) {
            logger.warning("Ping fail ", exception);
            return false;
        }
    }

    /**
     * Ping to a specific host port.
     *
     * @param   address  The InetAddress
     * @param   port     the port
     *
     * @return  true or false if the ping is ok
     */
    public static boolean ping(@NotNull final InetAddress address, final int port, int timeout) {
        boolean available = true;
        try(final Socket socket = new Socket()) {
            final SocketAddress addr = new InetSocketAddress(address, port);
            socket.connect(addr, timeout);
        }
        catch (final IOException e) {
            available = false;
        }
        return available;
    }

    //~ Static Fields ................................................................................................................................

    private static final Logger logger = Logger.getLogger(Ping.class);
}  // end class Ping
