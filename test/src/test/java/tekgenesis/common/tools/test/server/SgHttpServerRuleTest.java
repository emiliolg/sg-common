
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.tools.test.server;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import tekgenesis.common.invoker.HttpInvoker;
import tekgenesis.common.invoker.HttpInvokers;
import tekgenesis.common.invoker.exception.InvokerInvocationException;
import tekgenesis.common.service.Call;
import tekgenesis.common.service.Method;
import tekgenesis.common.service.Status;
import tekgenesis.common.util.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import static tekgenesis.common.Predefined.equal;
import static tekgenesis.common.Predefined.hashCodeAll;
import static tekgenesis.common.json.JsonMapping.shared;
import static tekgenesis.common.media.MediaType.TEXT_PLAIN;

/**
 * Test for testing SgHttpServerRule.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection" })
public class SgHttpServerRuleTest {

    //~ Methods ......................................................................................................................................

    @Test public void test1Unordered() {
        final String result = invoker.resource("/some").get(String.class);
        assertThat(result).isEqualTo("a");
    }

    @Test public void test2Repetition() {
        final String result = invoker.resource("/some").get(String.class);
        assertThat(result).isEqualTo("a");

        invoker.resource("/some").post();
    }

    @Test public void test3Repeatable() {
        try {
            invoker.resource("/some").get(String.class);
            failBecauseExceptionWasNotThrown(InvokerInvocationException.class);
        }
        catch (final InvokerInvocationException e) {
            assertThat(e.getStatus()).isEqualTo(Status.EXPECTATION_FAILED);
        }
    }

    @Test public void test4Content() {
        // Specifying text plain content type
        server.expectPost().withContent("Pepe").withContentType(TEXT_PLAIN);
        invoker.resource("/post").contentType(TEXT_PLAIN).post("Pepe");
    }

    @Test public void test5RequestCachedContent() {
        // With matching application json content type
        server.expectPost().withContent(new TestType("Pepa"));
        server.expectPost().withContent(new TestType("Pepo"));
        server.expectPost().withContent(new TestType("Pepe"));

        invoker.resource("/post").post(new TestType("Pepo"));
    }

    @Test public void test6PostWithResponseBasedOnRequest() {
        // With matching application json content type
        server.expectPost("/shout").respondOkWith(value -> {
            try {
                final TestType type = shared().readValue(value.getContent(), TestType.class);
                return type.yield();
            }
            catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        });

        final TestType type   = new TestType("Pepe");
        final TestType result = invoker.resource("/shout").post(TestType.class, type);
        assertThat(result).isEqualTo(type.yield());
    }

    @Test public void test7CallWithResponseBasedOnRequest() {
        // Specifying text plain content type
        final Call   call    = new Call(Method.POST, "/call");
        final String content = "Content";
        server.expectCall(call, content).withContentType(TEXT_PLAIN).respondOk().withContentType(TEXT_PLAIN).withContent(value -> {
            try {
                return new String(Files.toByteArray(value.getContent())) + "!";
            }
            catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        });

        final String result = invoker.resource(call).contentType(TEXT_PLAIN).invoke(String.class, content).get();
        assertThat(result).isEqualTo(content + "!");
    }

    //~ Methods ......................................................................................................................................

    @BeforeClass public static void before() {
        server.expectPost("/some");
        server.expectGet("/other").respondOkWith("b");
        server.expectGet("/some").repeated(2).respondOkWith("a");
    }

    //~ Static Fields ................................................................................................................................

    @ClassRule public static final SgHttpServerRule server = SgHttpServerRule.httpServerRule().verbose().unordered().ignoreRemaining().build();

    public static HttpInvoker invoker = HttpInvokers.invoker(server.getServerAddress());

    //~ Inner Classes ................................................................................................................................

    public static class TestType {
        private String field = null;

        public TestType() {}

        public TestType(String field) {
            this.field = field;
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final TestType testType = (TestType) o;
            return equal(field, testType.field);
        }

        @Override public int hashCode() {
            return hashCodeAll(field);
        }

        public TestType yield() {
            return new TestType("¡" + field + "!");
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}  // end class SgHttpServerRuleTest
