
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.tools.test.server;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Future;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.collections.MultiMap;
import tekgenesis.common.collections.Seq;
import tekgenesis.common.logging.Logger;
import tekgenesis.common.media.MediaType;
import tekgenesis.common.media.Mime;
import tekgenesis.common.service.Headers;
import tekgenesis.common.service.Method;
import tekgenesis.common.service.Parameters;
import tekgenesis.common.service.Status;
import tekgenesis.common.service.cookie.Cookie;
import tekgenesis.common.service.cookie.Cookies;
import tekgenesis.common.service.cookie.MutableCookie;
import tekgenesis.common.service.server.Request;
import tekgenesis.common.service.server.Response;
import tekgenesis.common.tools.test.server.SgHttpServer.SgExpectationHandler;
import tekgenesis.common.util.Files;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.EXPECTATION_FAILED;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import static tekgenesis.common.Predefined.equal;
import static tekgenesis.common.Predefined.isEmpty;
import static tekgenesis.common.collections.Colls.emptyList;
import static tekgenesis.common.collections.Colls.immutable;
import static tekgenesis.common.service.HeaderNames.CONNECTION;
import static tekgenesis.common.service.cookie.Cookies.hash;

/**
 * Sg Nio Http Server.
 */
class NioSgHttpServer {

    //~ Instance Fields ..............................................................................................................................

    private ChannelFuture              binding;
    private NioEventLoopGroup          bossGroup;
    private final SgExpectationHandler expectations;

    private final int                          port;
    private final AtomicReference<ServerState> state;
    private NioEventLoopGroup                  workerGroup;

    //~ Constructors .................................................................................................................................

    NioSgHttpServer(int port, SgExpectationHandler expectations) {
        this.port         = port;
        this.expectations = expectations;
        state             = new AtomicReference<>(ServerState.CREATED);
        binding           = null;
        bossGroup         = null;
        workerGroup       = null;
    }

    //~ Methods ......................................................................................................................................

    void shutdown()
        throws InterruptedException
    {
        if (!state.compareAndSet(ServerState.STARTED, ServerState.SHUTDOWN))
            throw new IllegalStateException("The server is already shutdown or wasn't started: " + state.get());
        else {
            try {
                binding.channel().close().await();
            }
            finally {
                final Future<?> bf = bossGroup.shutdownGracefully();
                final Future<?> wf = workerGroup.shutdownGracefully();
                bf.await();
                wf.await();
            }
        }
    }  // end method shutdown

    void start() {
        if (!state.compareAndSet(ServerState.CREATED, ServerState.STARTING)) throw new IllegalStateException("Server already started");

        // Configure the server.
        final int processors = Runtime.getRuntime().availableProcessors();
        bossGroup   = new NioEventLoopGroup(processors, createThreadFactory("Boss"));
        workerGroup = new NioEventLoopGroup(processors * 2, createThreadFactory("Worker"));
        try {
            final ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, MAX_CONNECTIONS);
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new SgServerInitializer(expectations));

            binding = b.bind(port).sync();

            if (!binding.isSuccess()) throw new RuntimeException(binding.cause());

            state.set(ServerState.STARTED);
        }
        catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    int getServerPort() {
        return port;
    }

    private DefaultThreadFactory createThreadFactory(String type) {
        return new DefaultThreadFactory(String.format("%s-Server-%d", type, port));
    }

    //~ Static Fields ................................................................................................................................

    private static final int MAX_CONTENT_LENGTH = 8192;

    private static final Logger logger = Logger.getLogger(NioSgHttpServer.class);

    private static final int MAX_CONNECTIONS = 1024;

    //~ Enums ........................................................................................................................................

    private enum ServerState { CREATED, STARTING, STARTED, SHUTDOWN }

    //~ Inner Classes ................................................................................................................................

    private static class NioRequestCookieAdapter implements MutableCookie {
        private final io.netty.handler.codec.http.cookie.Cookie cookie;

        private NioRequestCookieAdapter(io.netty.handler.codec.http.cookie.Cookie cookie) {
            this.cookie = cookie;
        }

        @Override public boolean equals(Object o) {
            return this == o || o instanceof Cookie && Cookies.equal(this, (Cookie) o);
        }

        @Override public int hashCode() {
            return hash(this);
        }

        @Override public String toString() {
            return Cookies.toString(this);
        }

        @Override public MutableCookie withDomain(@Nullable String domain) {
            cookie.setDomain(domain);
            return this;
        }

        @Override public MutableCookie withHttpOnly(boolean flag) {
            cookie.setHttpOnly(flag);
            return this;
        }

        @Override public MutableCookie withMaxAge(long seconds) {
            cookie.setMaxAge(seconds);
            return this;
        }

        @Override public MutableCookie withPath(@Nullable String path) {
            cookie.setPath(path);
            return this;
        }

        @Override public MutableCookie withSecure(boolean secure) {
            cookie.setSecure(secure);
            return this;
        }

        @Override public MutableCookie withValue(@NotNull String value) {
            cookie.setValue(value);
            return this;
        }

        @Nullable @Override public String getDomain() {
            return cookie.domain();
        }

        @Override public boolean isSecure() {
            return cookie.isSecure();
        }

        @Override public long getMaxAge() {
            final long maxAge = cookie.maxAge();
            return maxAge == Long.MIN_VALUE ? -1 : maxAge;
        }

        @NotNull @Override public String getName() {
            return cookie.name();
        }

        @Nullable @Override public String getPath() {
            return cookie.path();
        }

        @NotNull @Override public String getValue() {
            return cookie.value();
        }

        @Override public boolean isHttpOnly() {
            return cookie.isHttpOnly();
        }
    }  // end class NioRequestCookieAdapter

    private static class NioSgRequest implements Request {
        private final byte[]      bytes;
        private final ByteBuf     content;
        private final Seq<Cookie> cookies;
        private final Headers     headers;

        private final Method                   method;
        private final MultiMap<String, String> parameters;
        private final String                   path;

        private final String scheme;
        private final String uri;

        NioSgRequest(FullHttpRequest request) {
            method = Method.valueOf(request.method().name());
            uri    = request.uri();
            scheme = request.protocolVersion().protocolName();
            final QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
            path       = decoder.path();
            parameters = createParameters(decoder);
            content    = request.content();
            bytes      = cacheContent();
            headers    = createHeaders(request.headers());
            cookies    = createCookies(request.headers());
        }

        /** Close message content stream. */
        @Override public void close() {
            content.discardReadBytes();
        }

        @Override public String toString() {
            // noinspection DuplicateStringLiteralInspection
            return "(" + hashCode() + ")NioSgRequest{" +
                   "method=" + method + ", path='" + path + '\'' + ", parameters=" + parameters + '}';
        }

        @Override public Object getAttribute(@NotNull String name) {
            throw new UnsupportedOperationException();
        }

        /** Return message content as stream. */
        @Override public InputStream getContent()
            throws IOException
        {
            return new ByteArrayInputStream(bytes);
        }

        @Override public int getContentLength() {
            return content.capacity();
        }

        @Override public Seq<Cookie> getCookies() {
            return cookies;
        }

        /** Return message headers. */
        @NotNull @Override public Headers getHeaders() {
            return headers;
        }

        /** Return request method. */
        @Override public Method getMethod() {
            return method;
        }

        /** Return request parameters. */
        @Override public MultiMap<String, String> getParameters() {
            return parameters;
        }

        /** Return request path. */
        @Override public String getPath() {
            return path;
        }

        @Override public String getQueryString() {
            return Parameters.mapToQueryString(parameters);
        }

        @Override public String getScheme() {
            return scheme;
        }

        @Override public String getUri() {
            return path;
        }

        @Override public String getUrl() {
            return uri;
        }

        /**
         * NioSgRequest content must be cached in order to allow testing multiple times request
         * content with expectations!
         */
        private byte[] cacheContent() {
            return content.capacity() != 0 ? Files.toByteArray(new ByteBufInputStream(content)) : EMPTY_CONTENT;
        }

        private ImmutableList<Cookie> createCookies(@NotNull HttpHeaders h) {
            //J-
            final String header = h.get(HttpHeaderNames.COOKIE);
            return isEmpty(header) ? emptyList()  //
                                   : immutable(ServerCookieDecoder.LAX.decode(header))
                                     .map((cookie) -> (Cookie) new NioRequestCookieAdapter(cookie))
                                     .toList();
            //J+
        }

        private Headers createHeaders(@NotNull HttpHeaders h) {
            final Headers result = new Headers();
            for (final String header : h.names())
                result.putAll(header, h.getAll((CharSequence) header));
            return result;
        }

        private MultiMap<String, String> createParameters(@NotNull QueryStringDecoder decoder) {
            final MultiMap<String, String> result = MultiMap.createMultiMap();
            for (final Map.Entry<String, List<String>> p : decoder.parameters().entrySet())
                result.putAll(p.getKey(), p.getValue());
            return result;
        }

        private static final byte[] EMPTY_CONTENT = {};
    }  // end class NioSgRequest

    private static class NioSgResponse implements Response {
        private final Headers headers;

        private final ByteArrayOutputStream output;
        private Status                      status;

        private NioSgResponse() {
            output  = new ByteArrayOutputStream();
            headers = new Headers();
            status  = Status.OK;
        }

        @NotNull @Override public Response withContentType(@NotNull MediaType mime) {
            headers.setContentType(mime);
            return this;
        }

        @NotNull @Override public Response withContentType(@NotNull Mime mime, @NotNull Charset charset) {
            headers.setContentType(MediaType.forMimeWithEncoding(mime, charset.name()));
            return this;
        }

        @Override public MutableCookie withCookie(@NotNull String name, @NotNull String value) {
            throw new UnsupportedOperationException("NioSgResponse::withCookie not implemented.");
        }

        @NotNull @Override public Response withHeader(String name, String value) {
            headers.put(name, value);
            return this;
        }

        /** Return message content as stream. */
        @Override public OutputStream getContent()
            throws IOException
        {
            return output;
        }

        /** Return message headers. */
        @NotNull @Override public Headers getHeaders() {
            return headers;
        }

        @NotNull @Override public Status getStatus() {
            return status;
        }

        /** Set response status. */
        @Override public void setStatus(@NotNull Status status) {
            this.status = status;
        }

        private FullHttpResponse toHttpResponse() {
            final HttpResponseStatus s      = HttpResponseStatus.valueOf(status.code());
            final FullHttpResponse   result = new DefaultFullHttpResponse(HTTP_1_1, s, Unpooled.wrappedBuffer(output.toByteArray()));
            writeHeaders(result.headers());
            return result;
        }

        private void writeHeaders(HttpHeaders h) {
            for (final Map.Entry<String, Collection<String>> entry : headers.asMap().entrySet())
                h.add((CharSequence) entry.getKey(), entry.getValue());
        }
    }  // end class NioSgResponse

    private static class SgServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
        private final SgExpectationHandler expectations;

        private SgServerHandler(SgExpectationHandler expectations) {
            this.expectations = expectations;
        }

        @Override public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        @Override public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            /* Avoid logging when response expectation (typically delayed) attempts to write output and connection
             *was already reset by client */
            if (!(cause instanceof IOException) || !equal(cause.getMessage(), "Connection reset by peer")) logger.error(cause);
            ctx.close();
        }

        @Override protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req)
            throws Exception
        {
            FullHttpResponse resp;

            try {
                final NioSgResponse response = new NioSgResponse();
                expectations.handle(new NioSgRequest(req), response);
                resp = response.toHttpResponse();
                resp.headers().set(CONTENT_LENGTH, resp.content().readableBytes());

                final boolean keepAlive = HttpUtil.isKeepAlive(req);

                if (keepAlive) {
                    resp.headers().set((CharSequence) CONNECTION, KEEP_ALIVE);
                    ctx.write(resp);
                }
                else ctx.write(resp).addListener(ChannelFutureListener.CLOSE);
            }
            catch (final Throwable cause) {
                resp = writeErrorResponse(cause);
                ctx.write(resp).addListener(ChannelFutureListener.CLOSE);
            }
        }

        private FullHttpResponse writeErrorResponse(Throwable cause) {
            logger.error(cause);
            final HttpResponseStatus status  = cause instanceof AssertionError ? EXPECTATION_FAILED : INTERNAL_SERVER_ERROR;
            String                   message = cause.getLocalizedMessage();
            if (message == null) message = cause.getClass().getCanonicalName();
            final ByteBuf content = Unpooled.wrappedBuffer(message.getBytes());
            return new DefaultFullHttpResponse(HTTP_1_1, status, content);
        }
    }

    private static class SgServerInitializer extends ChannelInitializer<SocketChannel> {
        private final SgExpectationHandler expectations;

        private SgServerInitializer(SgExpectationHandler expectations) {
            this.expectations = expectations;
        }

        @Override public void initChannel(SocketChannel ch) {
            final ChannelPipeline p = ch.pipeline();
            p.addLast(new HttpServerCodec());
            p.addLast(new HttpObjectAggregator(MAX_CONTENT_LENGTH));
            p.addLast(new SgServerHandler(expectations));
        }
    }
}  // end class NioSgHttpServer
