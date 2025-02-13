
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service;

import org.jetbrains.annotations.NotNull;

/**
 * Service message exchange, common for requests and responses.
 */
public interface Message {

    //~ Methods ......................................................................................................................................

    /** Return message headers. */
    @NotNull Headers getHeaders();
}
