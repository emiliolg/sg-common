
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.xml;

import javax.xml.stream.XMLStreamException;

/**
 * A Runtime exception to wrap an {@link XMLStreamException }.
 */
@SuppressWarnings("WeakerAccess")
public class XmlException extends RuntimeException {

    //~ Constructors .................................................................................................................................

    /** Create an XMLException. */
    public XmlException(XMLStreamException cause) {
        super(cause);
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 4963044705499568731L;
}
