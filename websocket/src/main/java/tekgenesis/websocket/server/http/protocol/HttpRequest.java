
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.websocket.server.http.protocol;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

/**
 * Http request.
 */
public class HttpRequest {

    //~ Instance Fields ..............................................................................................................................

    private String body = null;

    private final Map<String, Collection<String>> headers = new HashMap<>();
    private String                                id      = null;
    private String                                method  = null;

    private String routingId = null;
    private String url       = null;

    //~ Constructors .................................................................................................................................

    /** Constructor. */
    public HttpRequest() {
        // required for json
    }

    /** Constructor. */
    public HttpRequest(String routingId, String id, String method, String url) {
        this.routingId = routingId;
        this.id        = id;
        this.method    = method;
        this.url       = url;
    }

    //~ Methods ......................................................................................................................................

    /** Return body or null if not available. */
    @Nullable public String getBody() {
        return body;
    }

    /** Set request body. */
    public void setBody(String body) {
        this.body = body;
    }

    /** Return headers. */
    public Map<String, Collection<String>> getHeaders() {
        return headers;
    }

    /** Return id. */
    public String getId() {
        return id;
    }

    /** Return method. */
    public String getMethod() {
        return method;
    }

    /** Return routing id. */
    public String getRoutingId() {
        return routingId;
    }

    /** Return url. */
    public String getUrl() {
        return url;
    }
}  // end class HttpRequest
