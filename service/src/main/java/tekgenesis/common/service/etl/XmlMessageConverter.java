
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service.etl;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import tekgenesis.common.logging.Logger;
import tekgenesis.common.media.MediaType;

/**
 * Message converter for reading and writing {@link Document documents}.
 */
public class XmlMessageConverter extends AbstractMessageConverter<Document> {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final Charset charset;

    //~ Constructors .................................................................................................................................

    /** Default constructor that uses {@link #DEFAULT_CHARSET} as default charset. */
    public XmlMessageConverter() {
        this(DEFAULT_CHARSET);
    }

    /** Constructor accepting a default charset to use if content type does not specifies one. */
    public XmlMessageConverter(@NotNull Charset charset) {
        super(MediaType.APPLICATION_XML, MediaType.TEXT_XML);
        this.charset = charset;
    }

    //~ Methods ......................................................................................................................................

    @Override public Document read(Class<? extends Document> type, Type genericType, MediaType contentType, InputStream stream)
        throws IOException
    {
        final Charset                c       = getContentTypeCharsetOrDefault(contentType, charset);
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder db     = factory.newDocumentBuilder();
            final InputSource     source = new InputSource();
            source.setCharacterStream(new InputStreamReader(stream, c));
            return db.parse(source);
        }
        catch (ParserConfigurationException | SAXException e) {
            logger.error(e);
            return null;
        }
    }

    @Override public void write(Document content, MediaType contentType, OutputStream stream)
        throws IOException
    {
        final Charset            c       = getContentTypeCharsetOrDefault(contentType, charset);
        final TransformerFactory factory = TransformerFactory.newInstance();
        try {
            final Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            final OutputStreamWriter writer = new OutputStreamWriter(stream, c);
            transformer.transform(new DOMSource(content), new StreamResult(writer));
            writer.flush();
        }
        catch (final TransformerException e) {
            logger.error(e);
        }
    }

    @Override protected boolean supports(Class<?> clazz) {
        return Document.class.isAssignableFrom(clazz);
    }

    //~ Static Fields ................................................................................................................................

    private static final Logger logger = Logger.getLogger(XmlMessageConverter.class);
}  // end class XmlMessageConverter
