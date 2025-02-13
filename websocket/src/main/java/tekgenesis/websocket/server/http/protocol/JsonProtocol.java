
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.websocket.server.http.protocol;

import java.io.IOException;
import java.io.StringWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

import tekgenesis.common.json.JsonMapping;

/**
 */
public class JsonProtocol {

    //~ Instance Fields ..............................................................................................................................

    private final ObjectMapper mapper = JsonMapping.shared();

    //~ Methods ......................................................................................................................................

    /**  */
    public <T> T read(String text, Class<T> type)
        throws IOException
    {
        return mapper.readValue(text, type);
    }

    /**  */
    public String write(Object object)
        throws IOException
    {
        final StringWriter writer = new StringWriter();
        mapper.writeValue(writer, object);
        return writer.toString();
    }
}
