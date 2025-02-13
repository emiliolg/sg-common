
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

/**
 * Observer Service.
 */
public interface ObserverService<OS extends ObserverService<OS, OO>, OO extends ObservableObject<OO, OS>> {

    //~ Methods ......................................................................................................................................

    /** Observer service on close handler. */
    default void onClose(OO o) {}

    /** Observer service on init handler. */
    default void onInit(OO o) {}
}
