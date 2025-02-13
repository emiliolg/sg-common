
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

/**
 * Named class that is used from I18nMessagesFactory on GWT code to then create instances.
 */
public interface Instantiator {

    //~ Methods ......................................................................................................................................

    /** Creates a class given the className. */
    <T> T create(String className);
}
