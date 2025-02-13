
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.websocket.server.http;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.xml.bind.DatatypeConverter;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;
import com.ning.http.client.Response;

import org.jetbrains.annotations.Nullable;

import tekgenesis.common.core.Constants;
import tekgenesis.common.logging.Logger;
import tekgenesis.websocket.server.http.protocol.HttpRequest;
import tekgenesis.websocket.server.http.protocol.HttpResponse;
import tekgenesis.websocket.server.http.protocol.JsonProtocol;
import tekgenesis.websocket.util.WebSocketAdapter;

/**
 * HttpForwarderSocket.
 */
public class HttpForwarderSocket extends WebSocketAdapter {

    //~ Instance Fields ..............................................................................................................................

    private final AsyncHttpClient asyncHttpClient;
    private final Logger          logger   = Logger.getLogger(HttpForwarderSocket.class);
    private final JsonProtocol    protocol = new JsonProtocol();

    private final String targetURL;

    //~ Constructors .................................................................................................................................

    /** Constructor. */
    public HttpForwarderSocket(String targetURL, int timeout) {
        this.targetURL = targetURL;
        final AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
        builder.setRequestTimeout(timeout);
        asyncHttpClient = new AsyncHttpClient(builder.build());
    }

    //~ Methods ......................................................................................................................................

    /** Close socket. */
    public void close() {
        asyncHttpClient.closeAsynchronously();
    }

    @Override public void onWebSocketText(String message) {
        logger.debug(RECEIVED_MESSAGE_ON_SERVER + message + " replying LATER to " + session.getRemoteAddress());

        try {
            final int space = message.indexOf(' ');
            // String routingId = message.substring(0, space); // todo: remove routingID (it is part of the message)
            final String json = message.substring(space + 1);

            final HttpRequest request = protocol.read(json, HttpRequest.class);

            switch (request.getMethod()) {
            case "GET":
                asyncGet(request);
                break;
            case "POST":
                asyncPost(request);
                break;
            case "PUT":
                asyncPut(request);
                break;
            // noinspection DuplicateStringLiteralInspection
            case "DELETE":
                asyncDelete(request);
                break;
            default:
                logger.warning("Unsupported method: " + request.getMethod());
            }
        }
        catch (final IOException e) {
            e.printStackTrace(System.err);
        }
    }

    protected void asyncDelete(final HttpRequest request) {
        logger.debug(DISPATCHING_HTTP_REQUEST + request.getId() + " " + request.getUrl());

        final AsyncHttpClient.BoundRequestBuilder delete = asyncHttpClient.prepareDelete(targetURL + request.getUrl());
        final String                              body   = request.getBody();

        if (body != null) delete.setBody(body.getBytes(UTF8));

        executeRequest(request, delete);
    }

    protected void asyncGet(final HttpRequest request) {
        logger.debug(DISPATCHING_HTTP_REQUEST + request.getId() + " " + request.getUrl());
        final AsyncHttpClient.BoundRequestBuilder get = asyncHttpClient.prepareGet(targetURL + request.getUrl());

        executeRequest(request, get);
    }  // end method asyncGet

    protected void asyncPost(final HttpRequest request) {
        logger.debug(DISPATCHING_HTTP_REQUEST + request.getId() + " " + request.getUrl());

        final AsyncHttpClient.BoundRequestBuilder post = asyncHttpClient.preparePost(targetURL + request.getUrl());
        final String                              body = request.getBody();

        if (body != null) post.setBody(body.getBytes(UTF8));

        executeRequest(request, post);
    }

    protected void asyncPut(final HttpRequest request) {
        logger.debug(DISPATCHING_HTTP_REQUEST + request.getId() + " " + request.getUrl());

        final AsyncHttpClient.BoundRequestBuilder put  = asyncHttpClient.preparePut(targetURL + request.getUrl());
        final String                              body = request.getBody();

        if (body != null) put.setBody(body.getBytes(UTF8));

        executeRequest(request, put);
    }

    private void executeRequest(HttpRequest request, AsyncHttpClient.BoundRequestBuilder req) {
        req.setHeaders(request.getHeaders());
        req.execute(new ResponseAsyncCompletionHandler(request));
    }

    private boolean isText(String contentType) {
        return contentType != null && (contentType.contains("text") || contentType.contains("json"));
    }

    //~ Static Fields ................................................................................................................................

    private static final String DISPATCHING_HTTP_REQUEST = "Dispatching HTTP request ";

    public static final Charset UTF8 = Charset.forName(Constants.UTF8);

    public static final String RECEIVED_MESSAGE_ON_SERVER = "Received message on server: ";
    @SuppressWarnings("MagicNumber")
    public static final int    MAX_SIZE = 1024 * 32;

    //~ Inner Classes ................................................................................................................................

    private class ResponseAsyncCompletionHandler extends AsyncCompletionHandler<Response> {
        private final HttpRequest request;

        public ResponseAsyncCompletionHandler(HttpRequest request) {
            this.request = request;
        }

        @Override public Response onCompleted(Response response)
            throws Exception
        {
            logger.debug("HTTP Response received for " + request.getId() + ". Response status: " + response.getStatusText());

            final FluentCaseInsensitiveStringsMap headers = response.getHeaders();

            final String routingId = request.getRoutingId();

            final String  body;
            final boolean text = isText(response.getContentType());

            if (text) body = response.getResponseBody();
            else body = DatatypeConverter.printBase64Binary(response.getResponseBodyAsBytes());

            final int status = response.getStatusCode();

            final HttpResponse httpResponse = HttpResponse.create(routingId, request.getId(), headers, status, text, body);
            logger.debug("Sending response for " + request.getId() + " to " + session.getRemoteAddress());

            @Nullable String json = protocol.write(httpResponse);

            /*
             * Message format for small messages:
             * ---------------------------------
             *
             *      routingId <space> json
             *
             * Message format por splitted messages:
             * ------------------------------------
             *
             *      routingId+requestId <space> json-part1
             *      ...
             *      routingId.requestId <space> json-part3
             *
             */

            boolean partitioned = false;

            while (json != null)
                synchronized (HttpForwarderSocket.this) {
                    String    packetId      = routingId;
                    final int routingIdSize = routingId.length() + request.getId().length() + 2;
                    final int msgSize       = routingIdSize + json.length();

                    final String packet;

                    final int maxSize = MAX_SIZE;

                    if (msgSize >= maxSize || partitioned) {
                        final boolean isLast = msgSize < maxSize;

                        packetId += (isLast ? '.' : '+') + request.getId();

                        if (isLast) {
                            packet = json;
                            json   = null;
                        }
                        else {
                            final int index = maxSize - routingIdSize;
                            packet = json.substring(0, index);
                            json   = json.substring(index);
                        }
                        partitioned = true;
                    }
                    else {
                        packet = json;
                        json   = null;
                    }

                    send(packetId + " " + packet);
                }

            return response;
        }  // end method onCompleted

        @Override public void onThrowable(Throwable t) {
            t.printStackTrace(System.err);
        }
    }  // end class ResponseAsyncCompletionHandler
}  // end class HttpForwarderSocket
