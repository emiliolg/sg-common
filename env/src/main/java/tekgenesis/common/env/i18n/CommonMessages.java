
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.i18n;

/**
 * Common core messages.
 */
public interface CommonMessages extends I18nMessages {

    //~ Instance Fields ..............................................................................................................................

    CommonMessages COMMON_MSGS = I18nMessagesFactory.create(CommonMessages.class);

    //~ Methods ......................................................................................................................................

    /**  */
    @DefaultMessage("Field ''{0}'' not found.")
    String fieldNotFound(String fieldName);

    /**  */
    @DefaultMessage("Properties: ''{0}'' are immutable")
    String immutableProperties(String name);

    /**  */
    @DefaultMessage("Properties: ''{0}'' are not bound")
    String unboundProperties(String name);
}
