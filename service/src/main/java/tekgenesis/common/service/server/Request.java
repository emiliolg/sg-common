
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service.server;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.collections.MultiMap;
import tekgenesis.common.service.InboundMessage;
import tekgenesis.common.service.Method;

import static tekgenesis.common.core.Strings.split;
import static tekgenesis.common.service.HeaderNames.HOST;

/**
 * Represents a server request.
 */
public interface Request extends InboundMessage {

    //~ Methods ......................................................................................................................................

    /** Return request attribute. */
    Object getAttribute(@NotNull String name);

    /** Returns the length, in bytes, of the request body, or -1 if the length is not known. */
    int getContentLength();

    /** Return request domain. */
    default String getDomain() {
        return split(getHost(), ':').getFirst().orElse("");
    }

    /** Return request host (domain, optionally port). */
    default String getHost() {
        return getHeaders().getOrEmpty(HOST);
    }

    /** Return request method. */
    Method getMethod();

    /** Return request parameters. */
    MultiMap<String, String> getParameters();

    /** Return request path. */
    String getPath();

    /** Return request port. */
    default String getPort() {
        return split(getHost(), ':').drop(1).mkString("");
    }

    /** Return request query string. */
    String getQueryString();

    /** Return request scheme. */
    String getScheme();

    /** Return request uri. */
    String getUri();

    /** Return request url. */
    String getUrl();
}  // end interface Request
