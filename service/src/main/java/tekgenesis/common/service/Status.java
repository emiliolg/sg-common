
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service;

import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * Service status. Resembles http status.
 */
@SuppressWarnings("MagicNumber")
public enum Status {

    //~ Enum constants ...............................................................................................................................

    CONTINUE(100), SWITCHING_PROTOCOLS(101),

    OK(200), CREATED(201), ACCEPTED(202), NON_AUTHORITATIVE_INFORMATION(203), NO_CONTENT(204), RESET_CONTENT(205), PARTIAL_CONTENT(206),

    MULTIPLE_CHOICES(300), MOVED_PERMANENTLY(301), FOUND(302), SEE_OTHER(303), NOT_MODIFIED(304), USE_PROXY(305), TEMPORARY_REDIRECT(307),

    BAD_REQUEST(400), UNAUTHORIZED(401), PAYMENT_REQUIRED(402), FORBIDDEN(403), NOT_FOUND(404), METHOD_NOT_ALLOWED(405), NOT_ACCEPTABLE(406),
    PROXY_AUTHENTICATION_REQUIRED(407), REQUEST_TIMEOUT(408), CONFLICT(409), GONE(410), LENGTH_REQUIRED(411), PRECONDITION_FAILED(412),
    REQUEST_ENTITY_TOO_LARGE(413), REQUEST_URI_TOO_LONG(414), UNSUPPORTED_MEDIA_TYPE(415), REQUESTED_RANGE_NOT_SATISFIABLE(416),
    EXPECTATION_FAILED(417), LOCKED(423), TOO_MANY_REQUEST(429),

    INTERNAL_SERVER_ERROR(500), NOT_IMPLEMENTED(501), BAD_GATEWAY(502), SERVICE_UNAVAILABLE(503), GATEWAY_TIMEOUT(504),
    HTTP_VERSION_NOT_SUPPORTED(505);

    //~ Instance Fields ..............................................................................................................................

    private final int code;

    //~ Constructors .................................................................................................................................

    @SuppressWarnings("WeakerAccess")
    Status(int code) {
        this.code = code;
    }

    //~ Methods ......................................................................................................................................

    /** Return status code. */
    public int code() {
        return code;
    }

    /** Return true if status belongs to informational series. */
    public boolean isInformational() {
        return informational.contains(this);
    }

    /** Return true if status belongs to successful series. */
    public boolean isSuccessful() {
        return successful.contains(this);
    }

    /** Return true if status belongs to redirection series. */
    public boolean isRedirection() {
        return redirection.contains(this);
    }

    /** Return true if status belongs to client error series. */
    public boolean isClientError() {
        return clientError.contains(this);
    }

    /** Return true if status belongs to client or server error series. */
    public boolean isError() {
        return isClientError() || isServerError();
    }

    /** Return true if status belongs to server error series. */
    public boolean isServerError() {
        return serverError.contains(this);
    }

    //~ Methods ......................................................................................................................................

    /** Return the {@link Status} for a given code, or null. */
    public static Status fromCode(int code) {
        return codeToStatus.get(code);
    }

    //~ Static Fields ................................................................................................................................

    private static final Status[]             VALUES       = values();
    private static final Map<Integer, Status> codeToStatus;

    private static final EnumSet<Status> informational = EnumSet.range(CONTINUE, SWITCHING_PROTOCOLS);
    private static final EnumSet<Status> successful    = EnumSet.range(OK, PARTIAL_CONTENT);
    private static final EnumSet<Status> redirection   = EnumSet.range(MULTIPLE_CHOICES, TEMPORARY_REDIRECT);
    private static final EnumSet<Status> clientError   = EnumSet.range(BAD_REQUEST, TOO_MANY_REQUEST);
    private static final EnumSet<Status> serverError   = EnumSet.range(INTERNAL_SERVER_ERROR, HTTP_VERSION_NOT_SUPPORTED);

    static {
        codeToStatus = new TreeMap<>();
        for (final Status status : VALUES)
            codeToStatus.put(status.code(), status);
    }
}
