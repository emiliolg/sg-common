
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.json;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

import static tekgenesis.common.json.JsonMapping.shared;

@SuppressWarnings("JavaDoc")
public class JSonMappingTest {

    //~ Methods ......................................................................................................................................

    @Test public void testDeSerialization()
        throws IOException
    {
        final A a1 = new A(10);
        a1.name = "xx";

        final String json = "{\"id\":1,\"name\":\"My bean\"}";

        final A a = shared().readValue(json, A.class);

        assertThat(a.id).isEqualTo(1);
        assertThat(a.name).isEqualTo("My bean");

        final AA aa = new AA(a1, a);

        final String saa = shared().writeValueAsString(aa);

        final AA a2 = shared().readValue(saa, AA.class);
        assertThat(a2.a1.name).isEqualTo("xx");
    }

    //~ Inner Classes ................................................................................................................................

    @JsonPropertyOrder({ "name", "id" })
    static class A {
        public String     name = "";
        private final int id;

        @JsonCreator A(@JsonProperty("id") final int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    @JsonPropertyOrder({ "a1", "a2" })
    static class AA {
        private final A a1;
        private final A a2;

        @JsonCreator AA(@JsonProperty("a1") final A a1,
                        @JsonProperty("a2") final A a2) {
            this.a1 = a1;
            this.a2 = a2;
        }

        public A getA1() {
            return a1;
        }

        public A getA2() {
            return a2;
        }
    }
}  // end class JSonMappingTest
