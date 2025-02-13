
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.ClassRule;
import org.junit.Test;

import tekgenesis.common.collections.MultiMap;
import tekgenesis.common.command.exception.CommandStackCauseException;
import tekgenesis.common.core.DateOnly;
import tekgenesis.common.env.logging.LogConfig;
import tekgenesis.common.invoker.exception.InvokerConnectionException;
import tekgenesis.common.invoker.exception.InvokerInvocationException;
import tekgenesis.common.json.JsonMapping;
import tekgenesis.common.service.cookie.Cookie;
import tekgenesis.common.service.etl.JsonMessageConverter;
import tekgenesis.common.service.exception.MessageConversionException;
import tekgenesis.common.tools.test.server.ConnectionTimeoutRule;
import tekgenesis.common.tools.test.server.ResponseExpectation;
import tekgenesis.common.tools.test.server.SgHttpServerRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import static tekgenesis.common.core.Times.SECONDS_HOUR;
import static tekgenesis.common.invoker.Person.createPerson;
import static tekgenesis.common.media.MediaType.APPLICATION_JSON;
import static tekgenesis.common.service.Method.*;
import static tekgenesis.common.service.Status.*;
import static tekgenesis.common.tools.test.server.SgHttpServerRule.httpServerRule;

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber", "ClassWithTooManyMethods" })
public class HttpInvokerTest {

    //~ Methods ......................................................................................................................................

    @Test public void testAcceptHeader() {
        server.expectPost().withAccept(APPLICATION_JSON);
        invoker.resource("/any").accept(APPLICATION_JSON).post();
    }

    @Test public void testCommandStackCauseExceptionAsRootCause() {
        server.expectGet().respond(SERVICE_UNAVAILABLE);

        final PathResource<?> resource = invoker.resource("/fail");

        try {
            resource.get(String.class);
            failBecauseExceptionWasNotThrown(InvokerInvocationException.class);
        }
        catch (final InvokerInvocationException e) {
            assertThat(e).hasRootCauseExactlyInstanceOf(CommandStackCauseException.class);
        }
    }

    @Test public void testCustomJsonConverter() {
        final HttpInvoker custom = HttpInvokers.invoker(server.getServerAddress());

        final Map<String, Object> person = new HashMap<>();
        person.put("name", "Ian Anderson");
        person.put("address", "None");
        person.put("age", new Age(80));
        person.put("birthDate", DateOnly.date(1981, 5, 12));

        server.expectGet().respondOkWith(person);

        assertDefaultPerson(custom.resource("/get/person").get(Person.class));

        // Add field to "person" to fail with UnrecognizedPropertyException for field "extra"

        person.put("extra", BigDecimal.ZERO);

        server.expectGet().respondOkWith(person);

        try {
            custom.resource("/get/person").get(Person.class);
            failBecauseExceptionWasNotThrown(MessageConversionException.class);
        }
        catch (final MessageConversionException e) {
            assertThat(e).hasMessageContaining("Unrecognized field \"extra\"");
        }

        // Customize invoker with relaxed Json message converter

        final ObjectMapper mapper = JsonMapping.json();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        custom.withConverter(new JsonMessageConverter(mapper));

        server.expectGet().respondOkWith(person);
        assertDefaultPerson(custom.resource("/get/person").get(Person.class));
    }

    @Test public void testDelete() {
        server.expectDelete();
        invoker.resource("/delete").delete();
    }

    @Test public void testDeleteWithResponse() {
        server.expectDelete().respondOkWith(createPerson());
        final Person result = invoker.resource("/delete").delete(Person.class);
        assertDefaultPerson(result);
    }

    @Test public void testErrorHandler() {
        server.expectGet().respond(SERVICE_UNAVAILABLE);

        final PathResource<?> resource = invoker.resource("/fail");
        invoker.withErrorHandler((status, headers, data) -> assertThat(status).isEqualTo(SERVICE_UNAVAILABLE));

        try {
            resource.get(Person.class);
            failBecauseExceptionWasNotThrown(InvokerInvocationException.class);
        }
        catch (final InvokerInvocationException e) {
            assertThat(e.getStatus()).isEqualTo(SERVICE_UNAVAILABLE);
        }
    }

    @Test public void testGetWithGenericResponse() {
        server.expectGet().respondOkWith(createListOfPersons());
        final List<Person> result = invoker.resource("/get/person").get(new GenericType<List<Person>>() {});
        assertDefaultListOfPersons(result);
    }

    @Test public void testGetWithResponse() {
        server.expectGet().respondOkWith(createPerson());
        final Person result = invoker.resource("/get/person").get(Person.class);
        assertDefaultPerson(result);
    }

    @Test public void testHead() {
        server.expectHead();
        invoker.resource("/head").head();
    }

    @Test public void testInvokerConnectException()
        throws IOException
    {
        final HttpInvoker httpInvoker = HttpInvokers.invoker("http://localhost:1");
        try {
            httpInvoker.resource("/unreachable").head();
            failBecauseExceptionWasNotThrown(InvokerConnectionException.class);
        }
        catch (final InvokerConnectionException e) {
            assertThat(e.isConnectException()).isTrue();
        }
    }

    @Test public void testInvokerConnectTimeout()
        throws IOException
    {
        final HttpInvoker custom = HttpInvokers.invoker(timeout.connectionUrl());
        custom.withConnectTimeout(400);
        try {
            custom.resource("/unreachable").head();
            failBecauseExceptionWasNotThrown(InvokerConnectionException.class);
        }
        catch (final InvokerConnectionException e) {
            assertThat(e.isConnectTimeoutException()).isTrue();
        }
    }

    @Test public void testInvokerCookiesInbound() {
        final List<Cookie> expected = new ArrayList<>();

        final ResponseExpectation response = server.expectGet("/cookies").respondOk();
        expected.add(
            response.withCookie("SESSION", "uuid")
                    .withDomain("tekgenesis.com")
                    .withPath("/path")
                    .withMaxAge(SECONDS_HOUR)
                    .withSecure(true)
                    .withHttpOnly(true));
        expected.add(response.withCookie("NODE", "first").withDomain("tekgenesis.net"));

        final HttpInvokerResult<?> result = invoker.resource("/cookies").invoke(GET).execute();
        assertThat(result.getCookies()).containsAll(expected);
    }

    @Test public void testInvokerCookiesOutbound() {
        server.expectPost("/cookies").withCookie("SESSION", "uuid").withCookie("NODE", "first");

        invoker.resource("/cookies").withCookie("SESSION", "uuid").withCookie("NODE", "first").post();
    }

    @Test public void testInvokerReadTimeout()
        throws IOException
    {
        server.expectHead().respondOk().delay(600);
        final HttpInvoker custom = HttpInvokers.invoker(server.getServerAddress());
        custom.withReadTimeout(400);
        try {
            custom.resource("/delayed").head();
            failBecauseExceptionWasNotThrown(InvokerConnectionException.class);
        }
        catch (final InvokerConnectionException e) {
            assertThat(e.isConnectTimeoutException()).isTrue();
        }
    }

    @Test public void testInvokerUnknownHostException()
        throws IOException
    {
        final HttpInvoker httpInvoker = HttpInvokers.invoker("http://unknown-host:8080");
        try {
            httpInvoker.resource("/unreachable").head();
            failBecauseExceptionWasNotThrown(InvokerConnectionException.class);
        }
        catch (final InvokerConnectionException e) {
            assertThat(e.isUnknownHostException()).isTrue();
        }
    }

    @Test public void testMethodWithGenericResponse() {
        server.expectGet().respond(ACCEPTED, createListOfPersons());
        final HttpInvokerResult<List<Person>> result = invoker.resource("/get").invoke(GET, new GenericType<List<Person>>() {}).execute();
        assertThat(result.get()).isNotNull();
        assertDefaultListOfPersons(result.get());
        assertThat(result.getHeaders().getContentType()).isEqualTo(APPLICATION_JSON);
        assertThat(result.getStatus()).isEqualTo(ACCEPTED);
    }

    @Test public void testMethodWithNoPayloadAndNoResponse() {
        server.expectHead().respond(NO_CONTENT);
        final HttpInvokerResult<?> result = invoker.resource("/head").invoke(HEAD).execute();
        assertThat(result.get()).isNull();
        assertThat(result.getHeaders().getContentType()).isNull();
        assertThat(result.getStatus()).isEqualTo(NO_CONTENT);
    }

    @Test public void testMethodWithPayload() {
        final Person payload = createPerson();
        server.expectPost(payload).respond(NO_CONTENT);
        final HttpInvokerResult<?> result = invoker.resource("/post").invoke(POST, payload).execute();
        assertThat(result.get()).isNull();
        assertThat(result.getHeaders().getContentType()).isNull();
        assertThat(result.getStatus()).isEqualTo(NO_CONTENT);
    }

    @Test public void testMethodWithPayloadAndGenericResponse() {
        final Person payload = createPerson();
        server.expectPost(payload).respond(CREATED).withContent(createListOfPersons());
        final HttpInvokerResult<List<Person>> result = invoker.resource("/post").invoke(POST, new GenericType<List<Person>>() {}, payload).execute();
        assertThat(result.get()).isNotNull();
        assertDefaultListOfPersons(result.get());
        assertThat(result.getHeaders().getContentType()).isEqualTo(APPLICATION_JSON);
        assertThat(result.getStatus()).isEqualTo(CREATED);
    }

    @Test public void testMethodWithPayloadAndResponse() {
        final Person person = createPerson();
        server.expectPost(person).respond(CREATED, person);
        final HttpInvokerResult<Person> result = invoker.resource("/post").invoke(POST, Person.class, person).execute();
        assertThat(result.get()).isNotNull();
        assertDefaultPerson(result.get());
        assertThat(result.getHeaders().getContentType()).isEqualTo(APPLICATION_JSON);
        assertThat(result.getStatus()).isEqualTo(CREATED);
    }

    @Test public void testMethodWithResponse() {
        server.expectGet().respond(ACCEPTED).withContent(createPerson());

        final HttpInvokerResult<Person> result = invoker.resource("/get").invoke(GET, Person.class).execute();
        assertThat(result.get()).isNotNull();
        assertDefaultPerson(result.get());
        assertThat(result.getHeaders().getContentType()).isEqualTo(APPLICATION_JSON);
        assertThat(result.getStatus()).isEqualTo(ACCEPTED);
    }

    @Test public void testParameters() {
        final MultiMap<String, String> map = MultiMap.createMultiMap();
        final String                   one = "one!:value";
        map.put("a", one);
        final String two = "two&value";
        map.put("b", two);
        server.expectHead().withParameters(map);
        invoker.resource("/params").param("a", one).param("b", two).head();
    }

    @Test public void testPostWithGenericResponse() {
        server.expectPost().respondOkWith(createListOfPersons());
        final List<Person> result = invoker.resource("/post").post(new GenericType<List<Person>>() {});
        assertDefaultListOfPersons(result);
    }

    @Test public void testPostWithNoPayloadAndNoResponse() {
        server.expectPost();
        invoker.resource("/post/1").post();
    }

    @Test public void testPostWithPayload() {
        final Person payload = createPerson();
        server.expectPost(payload);
        invoker.resource("/post").post(payload);
    }

    @Test public void testPostWithPayloadAndGenericResponse() {
        server.expectPost(createPerson()).respondOkWith(createListOfPersons());
        final List<Person> result = invoker.resource("/post").post(new GenericType<List<Person>>() {}, createPerson());
        assertDefaultListOfPersons(result);
    }

    @Test public void testPostWithPayloadAndResponse() {
        final Person person = createPerson();
        server.expectPost(person).respondOkWith(person);
        final Person result = invoker.resource("/post").post(Person.class, person);
        assertDefaultPerson(result);
    }

    @Test public void testPostWithResponse() {
        server.expectPost().respondOkWith(createPerson());
        final Person result = invoker.resource("/post").post(Person.class);
        assertDefaultPerson(result);
    }

    @Test public void testPutWithGenericResponse() {
        server.expectPut().respondOkWith(createListOfPersons());
        final List<Person> result = invoker.resource("/put/1").put(new GenericType<List<Person>>() {});
        assertDefaultListOfPersons(result);
    }

    @Test public void testPutWithNoPayloadAndNoResponse() {
        server.expectPut();
        invoker.resource("/put/1").put();
    }

    @Test public void testPutWithPayload() {
        final Person payload = createPerson();
        server.expectPut(payload);
        invoker.resource("/put/1").put(payload);
    }

    @Test public void testPutWithPayloadAndGenericResponse() {
        final Person payload = createPerson();
        server.expectPut(payload).respondOkWith(createListOfPersons());
        final List<Person> result = invoker.resource("/put/1").put(new GenericType<List<Person>>() {}, payload);
        assertDefaultListOfPersons(result);
    }

    @Test public void testPutWithPayloadAndResponse() {
        final Person person = createPerson();
        server.expectPut(person).respondOkWith(person);
        final Person result = invoker.resource("/put/1").put(Person.class, person);
        assertDefaultPerson(result);
    }

    @Test public void testPutWithResponse() {
        server.expectPut().respondOkWith(createPerson());
        final Person result = invoker.resource("/put/1").put(Person.class);
        assertDefaultPerson(result);
    }

    @Test public void testSgAppToken() {
        final String token = "xyz";
        server.expectGet().withSgAppToken(token);
        invoker.withSgAppToken(token).resource("/get/catalog").get(String.class);
    }

    @Test public void testUnhandledCodeException() {
        server.expectGet().respond(SERVICE_UNAVAILABLE);
        try {
            invoker.resource("/fail").get(Person.class);
            failBecauseExceptionWasNotThrown(InvokerInvocationException.class);
        }
        catch (final InvokerInvocationException e) {
            assertThat(e.getMessage().contains(SERVICE_UNAVAILABLE.name()));
        }
    }

    @Test public void testUriSyntaxException()
        throws IOException
    {
        final HttpInvoker httpInvoker = HttpInvokers.invoker("invalid syntax");
        try {
            httpInvoker.resource("/unreachable").head();
            failBecauseExceptionWasNotThrown(InvokerConnectionException.class);
        }
        catch (final InvokerConnectionException e) {
            assertThat(e.isUriSyntaxException()).isTrue();
        }
    }

    private void assertDefaultListOfPersons(List<Person> result) {
        assertThat(result).isNotNull();
        assertThat(result).hasSize(10);
        assertDefaultPerson(result.get(0));
        assertDefaultPerson(result.get(9));
    }

    private void assertDefaultPerson(Person result) {
        assertThat(result.name).isEqualTo("Ian Anderson");
        assertThat(result.address).isEqualTo("None");
        assertThat(result.age.value).isEqualTo(80);
        assertThat(result.birthDate).isEqualTo(DateOnly.date(1981, 5, 12));
    }

    private List<Person> createListOfPersons() {
        final List<Person> result = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            result.add(createPerson());
        return result;
    }

    //~ Static Fields ................................................................................................................................

    @ClassRule public static final SgHttpServerRule server = httpServerRule().onStart(s -> LogConfig.start()).build();

    private static final HttpInvoker                     invoker = HttpInvokers.invoker(server.getServerAddress());
    @ClassRule public static final ConnectionTimeoutRule timeout = new ConnectionTimeoutRule();
}  // end class HttpInvokerTest
