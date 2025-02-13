
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.xml;

import javax.xml.stream.XMLStreamWriter;

import tekgenesis.common.collections.Stack;

import static tekgenesis.common.xml.IndentingXMLStreamWriter.State.*;

/**
 * An XmlStreamWriter that provides indentation.
 */
public class IndentingXMLStreamWriter extends DelegatingXMLStreamWriter {

    //~ Instance Fields ..............................................................................................................................

    private int depth = 0;

    private String indentStep = "  ";

    private State              state      = SEEN_NOTHING;
    private final Stack<State> stateStack = Stack.createStack();

    //~ Constructors .................................................................................................................................

    /** Create the writer. */
    public IndentingXMLStreamWriter(XMLStreamWriter writer) {
        super(writer);
    }

    //~ Methods ......................................................................................................................................

    public void writeCData(String data) {
        state = SEEN_DATA;
        super.writeCData(data);
    }

    public void writeCharacters(String text) {
        state = SEEN_DATA;
        super.writeCharacters(text);
    }

    public void writeCharacters(char[] text, int start, int len) {
        state = SEEN_DATA;
        super.writeCharacters(text, start, len);
    }

    public void writeEmptyElement(String localName) {
        onEmptyElement();
        super.writeEmptyElement(localName);
    }

    public void writeEmptyElement(String namespaceURI, String localName) {
        onEmptyElement();
        super.writeEmptyElement(namespaceURI, localName);
    }

    public void writeEmptyElement(String prefix, String localName, String namespaceURI) {
        onEmptyElement();
        super.writeEmptyElement(prefix, localName, namespaceURI);
    }

    public void writeEndElement() {
        depth--;
        if (state == SEEN_ELEMENT) {
            super.writeCharacters("\n");
            doIndent();
        }
        super.writeEndElement();
        if (depth == 0 && state == SEEN_ELEMENT) super.writeCharacters("\n");
        state = stateStack.pop();
    }

    public void writeStartDocument() {
        super.writeStartDocument();
        super.writeCharacters("\n");
    }

    public void writeStartDocument(String version) {
        super.writeStartDocument(version);
        super.writeCharacters("\n");
    }

    public void writeStartDocument(String encoding, String version) {
        super.writeStartDocument(encoding, version);
        super.writeCharacters("\n");
    }

    public void writeStartElement(String localName) {
        onStartElement();
        super.writeStartElement(localName);
    }

    public void writeStartElement(String namespaceURI, String localName) {
        onStartElement();
        super.writeStartElement(namespaceURI, localName);
    }

    public void writeStartElement(String prefix, String localName, String namespaceURI) {
        onStartElement();
        super.writeStartElement(prefix, localName, namespaceURI);
    }

    /** Set the indentation String. */
    public void setIndentStep(String s) {
        indentStep = s;
    }

    private void doIndent() {
        if (depth > 0) {
            for (int i = 0; i < depth; i++)
                super.writeCharacters(indentStep);
        }
    }

    private void onEmptyElement() {
        state = SEEN_ELEMENT;
        if (depth > 0) super.writeCharacters("\n");
        doIndent();
    }

    private void onStartElement() {
        stateStack.push(SEEN_ELEMENT);
        state = SEEN_NOTHING;
        if (depth > 0) super.writeCharacters("\n");
        doIndent();
        depth++;
    }

    //~ Enums ........................................................................................................................................

    enum State { SEEN_NOTHING, SEEN_ELEMENT, SEEN_DATA }
}  // end class IndentingXMLStreamWriter
