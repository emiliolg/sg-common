
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.jmx;

import org.jetbrains.annotations.NotNull;

/**
 * Jmx Exception.
 */
@SuppressWarnings("WeakerAccess")
public class JmxException extends RuntimeException {

    //~ Constructors .................................................................................................................................

    /** Constructor with msg. */
    public JmxException(@NotNull final String msg) {
        super(msg);
    }
    /**
     * Default Constructor.
     *
     * @param  e  Exception
     */
    public JmxException(@NotNull Throwable e) {
        super(e);
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = -5782849223391320985L;
}
