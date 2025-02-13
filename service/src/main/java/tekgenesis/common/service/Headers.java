
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.MultiMap;
import tekgenesis.common.collections.Seq;
import tekgenesis.common.core.DateTime;
import tekgenesis.common.core.Option;
import tekgenesis.common.logging.Logger;
import tekgenesis.common.media.InvalidMediaTypeException;
import tekgenesis.common.media.MediaType;
import tekgenesis.common.media.Mime;

import static java.lang.Long.parseLong;
import static java.util.Arrays.asList;
import static java.util.Collections.addAll;

import static tekgenesis.common.collections.Colls.*;
import static tekgenesis.common.core.Strings.split;

/**
 * Headers for service exchanges.
 */
public class Headers implements HeaderNames {

    //~ Instance Fields ..............................................................................................................................

    private final MultiMap<String, String> map;

    //~ Constructors .................................................................................................................................

    /** Default constructor. */
    public Headers() {
        map = new MultiMap.Builder<String, String>().withSortedKeys(String::compareToIgnoreCase).withUniqueValues().build();
    }

    //~ Methods ......................................................................................................................................

    /** Returns a map view with all headers. */
    public Map<String, Collection<String>> asMap() {
        return map.asMap();
    }

    /** Put header value associated with given header name. */
    public void put(String name, String value) {
        map.put(name, value);
    }

    /** Copy all headers from given instance. */
    public void putAll(@NotNull final Headers headers) {
        map.putAll(headers.map);
    }

    /** Put all header values associated with given header name. */
    public void putAll(String name, Iterable<String> values) {
        map.putAll(name, values);
    }

    /** Put all header values associated with given header name. */
    public void putAll(String name, Enumeration<String> values) {
        while (values.hasMoreElements())
            map.put(name, values.nextElement());
    }

    /** Put all header values associated with given header name. */
    public void putAll(String name, String[] values) {
        map.putAll(name, Arrays.asList(values));
    }

    /** Set single header value associated with given header name. */
    public void set(String name, String value) {
        map.removeAll(name);
        put(name, value);
    }

    /**
     * Add outbound cache headers for specified expiration in days, including {@link #CACHE_CONTROL},
     * {@link #LAST_MODIFIED}, {@link #EXPIRES}.
     */
    public Headers withCache(int days) {
        return withCache(days, TimeUnit.DAYS);
    }

    /**
     * Add outbound cache headers for specified expiration, including {@link #CACHE_CONTROL},
     * {@link #LAST_MODIFIED}, {@link #EXPIRES}.
     */
    public Headers withCache(int duration, TimeUnit unit) {
        set(CACHE_CONTROL, "public," + " max-age=" + unit.toSeconds(duration));
        final DateTime now = DateTime.current();
        setLastModified(now);
        setExpires(now.addMilliseconds(unit.toMillis(duration)));
        return this;
    }

    /** Get accepted {@link MediaType media types}, as specified by the {@link #ACCEPT} header. */
    public Seq<MediaType> getAccept() {
        return getFirstAsList(ACCEPT).map(Headers::mediaTypeFor);
    }

    /** Set the accept {@link MediaType media type}, as specified by the {@link #ACCEPT} header. */
    public void setAccept(MediaType... mimeTypes) {
        setAccept(asList(mimeTypes));
    }

    /** Set the accept {@link MediaType media type}, as specified by the {@link #ACCEPT} header. */
    public void setAccept(Iterable<MediaType> mediaTypes) {
        set(ACCEPT, mkString(mediaTypes, ","));
    }

    /** Set the accept language, as specified by the {@link #ACCEPT_LANGUAGE} header. */
    public void setAcceptLanguage(Locale locale) {
        set(ACCEPT_LANGUAGE, locale.toLanguageTag());
    }

    /** Returns a collection view of all header values associated with given header name. */
    public Seq<String> getAll(String key) {
        return map.get(key);
    }

    /** Get allowed {@link Method methods}, as specified by the {@link #ALLOW} header. */
    public Seq<Method> getAllow() {
        return getFirstAsList(ALLOW).map(Method::valueOf);
    }

    /** Set allowed {@link Method methods}, as specified by the {@link #ALLOW} header. */
    public void setAllow(Seq<Method> methods) {
        set(ALLOW, methods.mkString(","));
    }

    /** Returns the value of the {@link #CACHE_CONTROL} header max-age value. */
    public long getCacheControlMaxAge() {
        final Option<String> cache = getFirst(CACHE_CONTROL);
        if (cache.isEmpty()) return 0;

        try {
            return split(cache.get(), ',').map(String::trim).getFirst(s -> s != null && s.startsWith(MAX_AGE)).map(t ->
                    parseLong(t.substring(MAX_AGE.length()))).orElse(0L);
        }
        catch (final NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Return the length of the body in bytes, as specified by the {@link #CONTENT_LENGTH} header.
     *
     * @return  the content length or -1 when the content-length is unknown.
     */
    public long getContentLength() {
        return getFirst(CONTENT_LENGTH).map(Long::valueOf).orElse(-1L);
    }

    /** Set the length of the body in bytes, as specified by the {@link #CONTENT_LENGTH} header. */
    public void setContentLength(long contentLength) {
        set(CONTENT_LENGTH, Long.toString(contentLength));
    }

    /**
     * Return the {@link MediaType media type} of the body, as specified by the
     * {@link #CONTENT_TYPE} header.
     *
     * @return  the content type or null if the content-type is unknown.
     */
    @Nullable public MediaType getContentType() {
        return getFirst(CONTENT_TYPE).map(Headers::mediaTypeFor).getOrNull();
    }

    /**
     * Set the {@link Mime media type} of the body, as specified by the {@link #CONTENT_TYPE}
     * header.
     */
    public void setContentType(MediaType mediaType) {
        set(CONTENT_TYPE, mediaType.toString());
    }

    /** Set {@link DateTime time} of expiration, as specified by the {@link #EXPIRES} header. */
    public void setExpires(@NotNull final DateTime time) {
        set(EXPIRES, DATE_HEADER_FORMAT.format(time.toDate()));
    }

    /** Returns the first optional header value associated with given header name. */
    public Option<String> getFirst(String key) {
        return map.get(key).getFirst();
    }

    /** Returns a collection view of all header values. */
    public Seq<String> getKeys() {
        return map.keys();
    }

    /**
     * Set {@link DateTime time} of last modification, as specified by the {@link #LAST_MODIFIED}
     * header.
     */
    public void setLastModified(@NotNull final DateTime time) {
        set(LAST_MODIFIED, DATE_HEADER_FORMAT.format(time.toDate()));
    }

    /**
     * Return the (new) location of a resource, as specified by the {@link #LOCATION} header.
     *
     * @return  the location or null if the location is unknown.
     */
    @Nullable public URI getLocation() {
        return getFirst(LOCATION).map(URI::create).getOrNull();
    }

    /** Set the (new) location of a resource, as specified by the {@link #LOCATION} header. */
    public void setLocation(URI location) {
        set(LOCATION, location.toASCIIString());
    }

    /** Returns the first header value associated with given header name or empty. */
    public String getOrEmpty(String key) {
        return getFirst(key).orElse("");
    }

    /**
     * Returns the value of the {@link #ORIGIN} header.
     *
     * @return  the value of the header or null if origin is undefined
     */
    @Nullable public String getOrigin() {
        return getFirst(ORIGIN).getOrNull();
    }

    /** Set the (new) value of the {@link #ORIGIN} header. */
    public void setOrigin(String origin) {
        set(ORIGIN, origin);
    }

    private Seq<String> getFirstAsList(String name) {
        final Option<String> value = getFirst(name);
        if (value.isEmpty()) return emptyIterable();
        else {
            final List<String> result = new ArrayList<>();
            addAll(result, value.get().split(",\\s*"));
            return seq(result);
        }
    }  // end method getFirstAsList

    //~ Methods ......................................................................................................................................

    @Nullable private static MediaType mediaTypeFor(final String headerValue) {
        try {
            return MediaType.fromString(headerValue);
        }
        catch (final InvalidMediaTypeException e) {
            logger.error(e);
            return null;
        }
    }

    //~ Static Fields ................................................................................................................................

    private static final DateFormat DATE_HEADER_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
    private static final Logger     logger             = Logger.getLogger(Headers.class);
    private static final String     MAX_AGE            = "max-age=";
}
