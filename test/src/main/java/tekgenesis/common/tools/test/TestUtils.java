
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.tools.test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import tekgenesis.common.core.Constants;

import static java.lang.Math.min;

/**
 */
@SuppressWarnings("WeakerAccess")
public class TestUtils {

    //~ Constructors .................................................................................................................................

    private TestUtils() {}

    //~ Methods ......................................................................................................................................

    /** Returns the hostname. */
    public static String getHostName() {
        try {
            final String host = InetAddress.getLocalHost().getHostName();
            return host.substring(0, min(index(host, '.'), min(index(host, '_'), index(host, '-'))));
        }
        catch (final UnknownHostException e) {
            return Constants.LOCALHOST;
        }
    }

    private static int index(String s, Character chr) {
        final int n = s.indexOf(chr);
        return n <= 0 ? s.length() : n;
    }
}  // end class TestUtils
