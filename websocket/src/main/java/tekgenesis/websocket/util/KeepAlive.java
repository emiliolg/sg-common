
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.websocket.util;

import java.util.Timer;
import java.util.TimerTask;

import tekgenesis.common.logging.Logger;

/**
 * Keep alive class.
 */
public class KeepAlive {

    //~ Constructors .................................................................................................................................

    private KeepAlive() {}

    //~ Methods ......................................................................................................................................

    /** ping method. */
    public static void ping(final WebSocketAdapter socket) {
        ping(socket, () -> {});
    }
    /** ping method. */
    public static void ping(final WebSocketAdapter socket, final FailureCallback callback) {
        final TimerTask task = new TimerTask() {
                @Override public void run() {
                    try {
                        socket.sendPing();
                    }
                    catch (final Exception e) {
                        logger.error("Ping cancelled for socket " + socket + " because: " + e.getMessage());
                        cancel();
                        callback.onFailure();
                    }
                }
            };
        timer.schedule(task, DELAY, PERIOD);
    }

    //~ Static Fields ................................................................................................................................

    private static final Logger logger = Logger.getLogger(KeepAlive.class);

    public static final int DELAY  = 5000;
    public static final int PERIOD = 5000;

    private static final Timer timer = new Timer();

    //~ Inner Interfaces .............................................................................................................................

    public interface FailureCallback {
        /** on failure. */ void onFailure();
    }
}  // end class KeepAlive
