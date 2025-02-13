
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service;

/**
 * All standard headers.
 */
public interface HeaderNames {

    //~ Instance Fields ..............................................................................................................................

    @SuppressWarnings("DuplicateStringLiteralInspection")
    String ACCEPT                    = "Accept";
    String ACCEPT_CHARSET            = "Accept-Charset";
    String ACCEPT_ENCODING           = "Accept-Encoding";
    String ACCEPT_LANGUAGE           = "Accept-Language";
    String ACCEPT_RANGES             = "Accept-Ranges";
    String AGE                       = "Age";
    String ALLOW                     = "Allow";
    String AUTHORIZATION             = "Authorization";
    String CACHE_CONTROL             = "Cache-Control";
    @SuppressWarnings("DuplicateStringLiteralInspection")
    String CONNECTION                = "Connection";
    String CONTENT_DISPOSITION       = "Content-Disposition";
    String CONTENT_ENCODING          = "Content-Encoding";
    String CONTENT_LANGUAGE          = "Content-Language";
    String CONTENT_LENGTH            = "Content-Length";
    String CONTENT_LOCATION          = "Content-Location";
    String CONTENT_MD5               = "Content-MD5";
    String CONTENT_RANGE             = "Content-Range";
    String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
    String CONTENT_TYPE              = "Content-Type";
    String COOKIE                    = "Cookie";
    String DATE                      = "Date";
    String ETAG                      = "Etag";
    String EXPECT                    = "Expect";
    @SuppressWarnings("DuplicateStringLiteralInspection")
    String EXPIRES             = "Expires";
    String FROM                = "From";
    String HOST                = "Host";
    String IF_MATCH            = "If-Match";
    String IF_MODIFIED_SINCE   = "If-Modified-Since";
    String IF_NONE_MATCH       = "If-None-Match";
    String IF_RANGE            = "If-Range";
    String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
    String LAST_MODIFIED       = "Last-Modified";
    String LOCATION            = "Location";
    String MAX_FORWARDS        = "Max-Forwards";
    String OLD_TEK_APP_TOKEN   = "TEK_APP_TOKEN";
    @SuppressWarnings("DuplicateStringLiteralInspection")
    String ORIGIN              = "Origin";
    String PRAGMA              = "Pragma";
    String PROXY_AUTHENTICATE  = "Proxy-Authenticate";
    String PROXY_AUTHORIZATION = "Proxy-Authorization";
    @SuppressWarnings("DuplicateStringLiteralInspection")
    String RANGE       = "Range";
    String REFERER     = "Referer";
    String RETRY_AFTER = "Retry-After";
    String SERVER      = "Server";
    String SET_COOKIE  = "Set-Cookie";
    String SET_COOKIE2 = "Set-Cookie2";
    String TE          = "Te";

    String TEK_APP_TOKEN           = "X-Tek-App-Token";
    String TEK_APP_TOKEN_SURROGATE = "X-Tek-App-Token-Surrogate";
    String TRAILER                 = "Trailer";
    String TRANSFER_ENCODING       = "Transfer-Encoding";
    String UPGRADE                 = "Upgrade";
    String USER_AGENT              = "User-Agent";
    String VARY                    = "Vary";
    String VIA                     = "Via";
    @SuppressWarnings("DuplicateStringLiteralInspection")
    String WARNING                 = "Warning";
    String WWW_AUTHENTICATE        = "WWW-Authenticate";
    String X_APPLICATION_EXCEPTION = "X-Application-Exception";

    String X_FIELDS          = "X-Fields";
    String X_FORWARD_FOR     = "X-Forwarded-For";
    String X_FORWARDED_HOST  = "X-Forwarded-Host";
    String X_FORWARDED_PROTO = "X-Forwarded-Proto";
    String X_LIMIT           = "X-limit";
    String X_MDC_UUID        = "X-mdc-uuid";
    String X_NEWER           = "X-newer";
    String X_OFFSET          = "X-OFFSET";
    String X_OPER_DEF        = "X-OPER-DEF";
    String X_PAGE_SIZE       = "X-page-size";
    String X_REAL_IP         = "X-Real-IP";
}  // end interface HeaderNames
