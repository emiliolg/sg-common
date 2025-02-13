
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.xml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * A XMLWriter delegate.
 */
@SuppressWarnings("ClassWithTooManyMethods")
public abstract class DelegatingXMLStreamWriter implements XMLStreamWriter {

    //~ Instance Fields ..............................................................................................................................

    private int started;

    private final XMLStreamWriter writer;

    //~ Constructors .................................................................................................................................

    protected DelegatingXMLStreamWriter(XMLStreamWriter writer) {
        this.writer = writer;
        started     = 0;
    }

    //~ Methods ......................................................................................................................................

    public void close() {
        try {
            writer.close();
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void flush() {
        try {
            writer.flush();
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeAttribute(String localName, String value) {
        try {
            writer.writeAttribute(localName, value);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeAttribute(String namespaceURI, String localName, String value) {
        try {
            writer.writeAttribute(namespaceURI, localName, value);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) {
        try {
            writer.writeAttribute(prefix, namespaceURI, localName, value);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeCData(String data) {
        try {
            writer.writeCData(data);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeCharacters(String text) {
        try {
            writer.writeCharacters(text);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeCharacters(char[] text, int start, int len) {
        try {
            writer.writeCharacters(text, start, len);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeComment(String data) {
        try {
            writer.writeComment(data);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeDefaultNamespace(String namespaceURI) {
        try {
            writer.writeDefaultNamespace(namespaceURI);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeDTD(String dtd) {
        try {
            writer.writeDTD(dtd);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeEmptyElement(String localName) {
        try {
            writer.writeEmptyElement(localName);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeEmptyElement(String namespaceURI, String localName) {
        try {
            writer.writeEmptyElement(namespaceURI, localName);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeEmptyElement(String prefix, String localName, String namespaceURI) {
        try {
            writer.writeEmptyElement(prefix, localName, namespaceURI);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeEndDocument() {
        try {
            for (int i = started; i > 0; i--)
                writeEndElement();
            writer.writeEndDocument();
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeEndElement() {
        try {
            started--;
            writer.writeEndElement();
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeEntityRef(String name) {
        try {
            writer.writeEntityRef(name);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeNamespace(String prefix, String namespaceURI) {
        try {
            writer.writeNamespace(prefix, namespaceURI);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeProcessingInstruction(String target) {
        try {
            writer.writeProcessingInstruction(target);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeProcessingInstruction(String target, String data) {
        try {
            writer.writeProcessingInstruction(target, data);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeStartDocument() {
        try {
            writer.writeStartDocument();
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeStartDocument(String version) {
        try {
            writer.writeStartDocument(version);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeStartDocument(String encoding, String version) {
        try {
            writer.writeStartDocument(encoding, version);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeStartElement(String localName) {
        try {
            started++;
            writer.writeStartElement(localName);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeStartElement(String namespaceURI, String localName) {
        try {
            started++;
            writer.writeStartElement(namespaceURI, localName);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void writeStartElement(String prefix, String localName, String namespaceURI) {
        try {
            started++;
            writer.writeStartElement(prefix, localName, namespaceURI);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void setDefaultNamespace(String uri) {
        try {
            writer.setDefaultNamespace(uri);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public NamespaceContext getNamespaceContext() {
        return writer.getNamespaceContext();
    }

    public void setNamespaceContext(NamespaceContext context) {
        try {
            writer.setNamespaceContext(context);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public String getPrefix(String uri) {
        try {
            return writer.getPrefix(uri);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public void setPrefix(String prefix, String uri) {
        try {
            writer.setPrefix(prefix, uri);
        }
        catch (final XMLStreamException e) {
            throw new XmlException(e);
        }
    }

    public Object getProperty(String name)
        throws IllegalArgumentException
    {
        return writer.getProperty(name);
    }
}  // end class DelegatingXMLStreamWriter
