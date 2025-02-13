
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service;

/**
 * Method.
 */
public enum Method {

    //~ Enum constants ...............................................................................................................................

    GET, POST, PUT, DELETE, HEAD, OPTIONS, PATCH;

    //~ Methods ......................................................................................................................................

    /** Returns true if method allows message body associated with request or response. */
    public boolean allowsMessageBody() {
        return this == POST || this == PUT || this == PATCH;
    }
}
