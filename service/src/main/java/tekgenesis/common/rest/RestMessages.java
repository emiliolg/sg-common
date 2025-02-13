
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.rest;

import tekgenesis.common.env.i18n.I18nMessages;
import tekgenesis.common.env.i18n.I18nMessagesFactory;

/**
 * Rest Messages.
 */
public interface RestMessages extends I18nMessages {

    //~ Instance Fields ..............................................................................................................................

    RestMessages REST_MSGS = I18nMessagesFactory.create(RestMessages.class);

    //~ Methods ......................................................................................................................................

    /**  */
    @DefaultMessage("Bad Gateway")
    String badGateway();

    /**  */
    @DefaultMessage("Bad request")
    String badRequest();

    /**  */
    @DefaultMessage("Document Not Found")
    String documentNotFound();

    /**  */
    @DefaultMessage("''{0}'' (http code : ''{1}'', msg: ''{2}'')")
    String httpInvocationError(String msg, int errorCode, String errMsg);

    /**  */
    @DefaultMessage("The record is Locked")
    String httpLock();

    /**  */
    @DefaultMessage("Internal Server Error")
    String internalServerError();

    /**  */
    @DefaultMessage("Service unavailable")
    String serviceUnavailable();

    /**  */
    @DefaultMessage("Timeout error")
    String timeout();
}  // end interface RestMessages
