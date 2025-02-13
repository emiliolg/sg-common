
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.metric;

/**
 * Rolling Number Event Type. Indicates if event to be stored is of counter type (views count) or
 * maximum type (maximum concurrency).
 */
public interface RollingNumberEventType {

    //~ Methods ......................................................................................................................................

    /** Return true if number event is maximum (eg. maximum concurrency). */
    boolean isMaximum();

    /** Return true if number event is counter (eg. views count). */
    boolean isCounter();
}
