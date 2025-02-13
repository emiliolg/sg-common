
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.websocket.server.http.protocol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Http response.
 */
public class HttpResponse {

    //~ Instance Fields ..............................................................................................................................

    private String                    body    = null;
    private Map<String, List<String>> headers = new HashMap<>();
    private String                    id      = null;

    private String  routingId = null;
    private int     status;
    private boolean text;

    //~ Constructors .................................................................................................................................

    /** Empty constructor. */
    public HttpResponse() {
        // required for json
    }

    /** Constructor. */
    public HttpResponse(String routingId, String id, Map<String, List<String>> headers, int status, boolean text, String body) {
        this.routingId = routingId;
        this.id        = id;
        this.headers   = headers;
        this.status    = status;
        this.text      = text;
        this.body      = body;
    }

    //~ Methods ......................................................................................................................................

    @Override public String toString() {
        return "HttpResponse(" + getRoutingId() + ", " + getId() + ", " + getHeaders() + ")";
    }

    /** Return body. */
    public String getBody() {
        return body;
    }

    /** Return headers. */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /** Return id. */
    public String getId() {
        return id;
    }

    /** Return routing id. */
    public String getRoutingId() {
        return routingId;
    }

    /** Return status. */
    public int getStatus() {
        return status;
    }

    /** Return true if it is text. */
    public boolean isText() {
        return text;
    }

    //~ Methods ......................................................................................................................................

    /** Create http response. */
    public static HttpResponse create(String routingId, String id, Map<String, List<String>> headers, int status, boolean text, String body) {
        return new HttpResponse(routingId, id, headers, status, text, body);
    }
}  // end class HttpResponse
