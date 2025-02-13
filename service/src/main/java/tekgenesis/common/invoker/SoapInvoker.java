
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import java.io.StringWriter;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import tekgenesis.common.logging.Logger;

import static tekgenesis.common.logging.Logger.Level.DEBUG;

/**
 * Soap invoker.
 */
public class SoapInvoker {

    //~ Constructors .................................................................................................................................

    private SoapInvoker() {}

    //~ Methods ......................................................................................................................................

    /** Execute a soap message. */
    public static SOAPMessage execute(final String url, final SOAPMessage request) {
        logMessage(request);
        SOAPMessage    response   = null;
        SOAPConnection connection = null;
        try {
            final SOAPConnectionFactory connectionFactory = SOAPConnectionFactory.newInstance();
            connection = connectionFactory.createConnection();
            response   = connection.call(request, url);
            logMessage(response);
        }
        catch (final Exception e) {
            logger.warning("Error occurred while sending SOAP Request to Server", e);
        }
        finally {
            if (connection != null) try {
                connection.close();}
            catch (final SOAPException e) {
                logger.debug(e);}
        }
        return response;
    }

    private static void logMessage(final SOAPMessage message) {
        if (!logger.isLoggable(DEBUG)) return;
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            final Transformer  transformer = transformerFactory.newTransformer();
            final Source       source      = message.getSOAPPart().getContent();
            final StringWriter sWriter     = new StringWriter();
            final StreamResult result      = new StreamResult(sWriter);
            transformer.transform(source, result);
            sWriter.flush();
            sWriter.close();

            logger.debug(sWriter.toString());
        }
        catch (final Exception e) {
            logger.debug(e);
        }
    }

    //~ Static Fields ................................................................................................................................

    private static final Logger logger = Logger.getLogger(SoapInvoker.class);
}  // end class SoapInvoker
