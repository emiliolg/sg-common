
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
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import tekgenesis.common.collections.Colls;
import tekgenesis.common.collections.MultiMap;
import tekgenesis.common.collections.Seq;
import tekgenesis.common.core.Constants;
import tekgenesis.common.core.DateOnly;
import tekgenesis.common.core.DateTime;
import tekgenesis.common.invoker.GenericType;
import tekgenesis.common.media.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.Predefined.equal;
import static tekgenesis.common.collections.Colls.listOf;
import static tekgenesis.common.media.MediaType.*;

/**
 * Test for default implementations of {@link MessageConverter}.
 */
@SuppressWarnings({ "InstanceVariableMayNotBeInitialized", "JavaDoc", "MagicNumber" })
public class MessageConverterTest {

    //~ Methods ......................................................................................................................................

    @Test public void testBasicObjectsConverter()
        throws IOException
    {
        testBasicObjectConverter(Boolean.class, Boolean.TRUE, 4);
        testBasicObjectConverter(Boolean.class, Boolean.FALSE, 5);
        testBasicObjectConverter(Integer.class, 5, 1);
        testBasicObjectConverter(Double.class, 1.1, 3);
        testBasicObjectConverter(BigDecimal.class, new BigDecimal(1.1), 53);
        testBasicObjectConverter(DateOnly.class, DateOnly.current(), 10);
        testBasicObjectConverter(DateTime.class, DateTime.current(), 24);
    }

    @Test public void testByteConverter()
        throws IOException
    {
        final ByteMessageConverter converter = new ByteMessageConverter();

        final byte[] write = hello_world.getBytes();

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        testWrite(converter, TEXT_PLAIN, byte[].class, byte[].class, stream, write);

        assertThat(stream.size()).isEqualTo(33);

        final InputStream input = new ByteArrayInputStream(stream.toByteArray());
        final byte[]      read  = (byte[]) testRead(converter, APPLICATION_JSON, byte[].class, byte[].class, input);

        assertThat(read).isEqualTo(write);
    }

    @Test public void testFormConverter()
        throws IOException
    {
        final FormMessageConverter converter = new FormMessageConverter();

        final MultiMap<String, String> write = createMultiMapExample();

        final ByteArrayOutputStream           stream     = new ByteArrayOutputStream();
        final Class<MultiMap<String, String>> writeClass = cast(write.getClass());
        testWrite(converter, APPLICATION_FORM_URLENCODED, writeClass, writeClass, stream, write);

        assertThat(stream.size()).isEqualTo(317);

        final String decode = URLDecoder.decode(new String(stream.toByteArray()), Constants.UTF8);
        //J-
        assertThat(decode).isEqualTo(
                hello_world+"="+hello_world+"&"+
                        hello_world+"="+dlrow_olleh+"&"+
                        dlrow_olleh+"="+hello_world);
        //J+
    }

    @Test public void testJsonConverter()
        throws IOException
    {
        final JsonMessageConverter converter = new JsonMessageConverter();

        final JsonExample write = createJsonExample();

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        testWrite(converter, APPLICATION_JSON, JsonExample.class, JsonExample.class, stream, write);

        assertThat(stream.size()).isEqualTo(43);

        final InputStream input = new ByteArrayInputStream(stream.toByteArray());
        final JsonExample read  = (JsonExample) testRead(converter, APPLICATION_JSON, JsonExample.class, JsonExample.class, input);

        assertThat(read).isEqualTo(write);
    }

    @SuppressWarnings("unchecked")
    @Test public void testJsonGenericListConverter()
        throws IOException
    {
        final JsonMessageConverter converter = new JsonMessageConverter();

        final List<JsonExample> write = createJsonListExample();

        final ByteArrayOutputStream          stream      = new ByteArrayOutputStream();
        final GenericType<List<JsonExample>> genericType = new GenericType<List<JsonExample>>() {};
        testWrite(converter, APPLICATION_JSON, genericType.getRaw(), genericType.getType(), stream, write);

        assertThat(stream.size()).isEqualTo(133);

        final InputStream       input = new ByteArrayInputStream(stream.toByteArray());
        final List<JsonExample> read  = (List<JsonExample>) testRead(converter, APPLICATION_JSON, genericType.getRaw(), genericType.getType(), input);

        assertThat(read).containsExactlyElementsOf(write);
    }

    @SuppressWarnings("unchecked")
    @Test public void testJsonGenericSeqConverter()
        throws IOException
    {
        final JsonMessageConverter converter = new JsonMessageConverter();

        final Seq<JsonExample> write = Colls.seq(createJsonListExample());

        final ByteArrayOutputStream         stream      = new ByteArrayOutputStream();
        final GenericType<Seq<JsonExample>> genericType = new GenericType<Seq<JsonExample>>() {};
        testWrite(converter, APPLICATION_JSON, genericType.getRaw(), genericType.getType(), stream, write);

        assertThat(stream.size()).isEqualTo(133);

        final InputStream      input = new ByteArrayInputStream(stream.toByteArray());
        final Seq<JsonExample> read  = (Seq<JsonExample>) testRead(converter, APPLICATION_JSON, genericType.getRaw(), genericType.getType(), input);

        assertThat(read).containsExactlyElementsOf(write);
    }

    @Test public void testStringConverter()
        throws IOException
    {
        final StringMessageConverter converter = new StringMessageConverter();

        final String write = hello_world;

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        testWrite(converter, TEXT_PLAIN, String.class, String.class, stream, write);

        assertThat(stream.size()).isEqualTo(33);

        final InputStream input = new ByteArrayInputStream(stream.toByteArray());
        final String      read  = testRead(converter, TEXT_PLAIN, String.class, String.class, input);

        assertThat(read).isEqualTo(write);
    }

    @Test public void testXmlConverter()
        throws IOException
    {
        final XmlMessageConverter converter = new XmlMessageConverter();

        final Document write = createXmlExample();

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        testWrite(converter, APPLICATION_XML, Document.class, Document.class, stream, write);

        assertThat(stream.size()).isEqualTo(70);

        final InputStream input = new ByteArrayInputStream(stream.toByteArray());
        final Document    read  = testRead(converter, APPLICATION_XML, Document.class, Document.class, input);

        final NodeList writeNodes = write.getChildNodes();
        final NodeList readNodes  = read.getChildNodes();

        assertThat(writeNodes.getLength()).isEqualTo(readNodes.getLength());

        final NamedNodeMap writeAttributes = write.getDocumentElement().getAttributes();
        final NamedNodeMap readAttributes  = read.getDocumentElement().getAttributes();

        assertThat(writeAttributes.getLength()).isEqualTo(readAttributes.getLength());
        assertThat(readAttributes.getNamedItem("a").getNodeValue()).isEqualTo(hello_world);
        assertThat(readAttributes.getNamedItem("b").getNodeValue()).isEqualTo(dlrow_olleh);
    }

    private JsonExample createJsonExample() {
        final JsonExample result = new JsonExample();
        result.id = hello_world;
        return result;
    }

    private List<JsonExample> createJsonListExample() {
        return listOf(createJsonExample(), createJsonExample(), createJsonExample());
    }

    private MultiMap<String, String> createMultiMapExample() {
        final MultiMap<String, String> result = MultiMap.createMultiMap();
        result.put(hello_world, hello_world);
        result.put(hello_world, dlrow_olleh);
        result.put(dlrow_olleh, hello_world);
        return result;
    }

    private Document createXmlExample() {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder db       = factory.newDocumentBuilder();
            final Document        document = db.newDocument();
            final Element         root     = document.createElement("root");
            root.setAttribute("a", hello_world);
            root.setAttribute("b", dlrow_olleh);
            document.appendChild(root);
            return document;
        }
        catch (final ParserConfigurationException ignored) {
            throw new RuntimeException(ignored);
        }
    }

    private <T> void testBasicObjectConverter(final Class<T> type, @Nullable final T write, final int writeSize)
        throws IOException
    {
        final BasicTypeMessageConverter converter = new BasicTypeMessageConverter();
        final ByteArrayOutputStream     stream    = new ByteArrayOutputStream();
        testWrite(converter, TEXT_PLAIN, type, type, stream, write);

        assertThat(stream.size()).isEqualTo(writeSize);

        final InputStream input = new ByteArrayInputStream(stream.toByteArray());
        final Object      read  = testRead(converter, TEXT_PLAIN, type, type, input);

        assertThat(read).isEqualTo(write);
    }

    private <T> T testRead(@NotNull MessageConverter<T> converter, MediaType mediaType, Class<? extends T> responseType, Type genericType,
                           InputStream input)
        throws IOException
    {
        final boolean canRead = converter.canRead(responseType, genericType, mediaType);
        assertThat(canRead).isTrue();
        final T read = converter.read(responseType, genericType, mediaType, input);
        assertThat(read).isNotNull();
        return read;
    }

    private <T> void testWrite(MessageConverter<T> converter, MediaType mediaType, Class<? extends T> requestType, Type genericType,
                               OutputStream output, @Nullable T payload)
        throws IOException
    {
        final boolean canWrite = converter.canWrite(requestType, genericType, mediaType);
        assertThat(canWrite).isTrue();
        if (payload != null) converter.write(payload, mediaType, output);
    }

    //~ Static Fields ................................................................................................................................

    private static final String hello_world = "¡Helló wørld!\n¡Helló wørld!";
    private static final String dlrow_olleh = "¿dlrøw ólleH?";

    //~ Inner Classes ................................................................................................................................

    private static class JsonExample {
        public String id;

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final JsonExample that = (JsonExample) o;
            return equal(id, that.id);
        }

        @Override public int hashCode() {
            return id.hashCode();
        }
    }
}  // end class MessageConverterTest
