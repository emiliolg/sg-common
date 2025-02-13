
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.media;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.core.Tuple;

import static tekgenesis.common.Predefined.equal;
import static tekgenesis.common.collections.Maps.hashMap;
import static tekgenesis.common.core.Constants.UTF8;
import static tekgenesis.common.core.Strings.unquote;
import static tekgenesis.common.core.Tuple.tuple;
import static tekgenesis.common.media.MediaTypes.parseMediaType;

/**
 * Represent a media type. Media types extends Mime types, and adds support for parameters, and
 * quality parameters as defined in the HTTP specification.
 */
public class MediaType implements MimeType {

    //~ Instance Fields ..............................................................................................................................

    private final Mime                mime;
    private final Map<String, String> parameters;

    //~ Constructors .................................................................................................................................

    /** Create media type wrapping given mime type. */
    public MediaType(@NotNull Mime mime) {
        this(mime, Collections.emptyMap());
    }

    /** Create media type wrapping given mime type and with specified parameters. */
    public MediaType(@NotNull Mime mime, @NotNull Map<String, String> parameters) {
        this.mime       = mime;
        this.parameters = parameters;
    }

    /** Create media type wrapping given mime type and with specified parameter. */
    public MediaType(@NotNull Mime mime, @NotNull Tuple<String, String> parameter) {
        this.mime  = mime;
        parameters = hashMap(parameter);
    }

    //~ Methods ......................................................................................................................................

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final MediaType other = (MediaType) o;

        if (mime != other.mime) return false;

        for (final Map.Entry<String, String> entry : parameters.entrySet()) {
            final String v = other.parameters.get(entry.getKey());
            if (!equal(entry.getValue(), v)) return false;
        }

        return true;
    }

    @Override public int hashCode() {
        int result = mime.hashCode();
        for (final Map.Entry<String, String> entry : parameters.entrySet())
            result = 31 * result + entry.getKey().hashCode() + entry.getValue().hashCode();
        return result;
    }

    /** Indicate whether base {@code MediaType} includes the other given media type. */
    public boolean includes(MediaType mediaType) {
        return MediaTypes.includes(this, mediaType);
    }

    @Override public String toString() {
        final StringBuilder builder = new StringBuilder();
        appendTo(builder);
        return builder.toString();
    }

    /**
     * Return the character set, as indicated by a {@code charset} parameter, if any.
     *
     * @return  the character set; or {@code null} if not available
     */
    @Nullable public Charset getCharset() {
        final String charset = getParameter(CHARSET_PARAMETER);
        return charset != null ? Charset.forName(unquote(charset)) : null;
    }

    /** Return media type mime. */
    @NotNull public Mime getMime() {
        return mime;
    }

    /** Return a generic parameter value, given a parameter name. */
    public String getParameter(String name) {
        return parameters.get(name);
    }

    /** Return media type parameters. */
    @NotNull public Map<String, String> getParameters() {
        return parameters;
    }

    @NotNull @Override public String getSubtype() {
        return mime.getSubtype();
    }

    @NotNull @Override public String getType() {
        return mime.getType();
    }

    protected void appendTo(StringBuilder builder) {
        builder.append(mime.getMime());
        appendTo(parameters, builder);
    }

    private void appendTo(@NotNull Map<String, String> map, StringBuilder builder) {
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            builder.append(';');
            builder.append(entry.getKey());
            builder.append('=');
            builder.append(entry.getValue());
        }
    }

    //~ Methods ......................................................................................................................................

    /** Construct a media type with no arguments for given Mime. */
    public static MediaType forMime(@NotNull Mime mime) {
        return new MediaType(mime);
    }

    /** Construct a media type with encoding argument. */
    public static MediaType forMimeWithEncoding(@NotNull Mime mime, @NotNull String encoding) {
        return new MediaType(mime, tuple(CHARSET_PARAMETER, encoding));
    }

    /** Parse the given String into a single {@link MediaType}. */
    public static MediaType fromString(@NotNull String mediaType) {
        return parseMediaType(mediaType);
    }

    //~ Static Fields ................................................................................................................................

    public static final MediaType ALL;
    public static final MediaType TEXT_XML;
    public static final MediaType TEXT_PLAIN;
    public static final MediaType TEXT_HTML;
    public static final MediaType TEXT_EVENT_STREAM;
    public static final MediaType APPLICATION_XML;
    public static final MediaType APPLICATION_JSON;
    public static final MediaType APPLICATION_JAVASCRIPT;
    public static final MediaType APPLICATION_OCTET_STREAM;
    public static final MediaType APPLICATION_FORM_URLENCODED;

    static {
        ALL                         = forMime(Mime.ALL);
        TEXT_XML                    = forMime(Mime.TEXT_XML);
        TEXT_PLAIN                  = forMime(Mime.TEXT_PLAIN);
        TEXT_HTML                   = forMime(Mime.TEXT_HTML);
        TEXT_EVENT_STREAM           = forMimeWithEncoding(Mime.TEXT_EVENT_STREAM, UTF8);
        APPLICATION_XML             = forMimeWithEncoding(Mime.APPLICATION_XML, UTF8);
        APPLICATION_JSON            = forMimeWithEncoding(Mime.APPLICATION_JSON, UTF8);
        APPLICATION_JAVASCRIPT      = forMimeWithEncoding(Mime.APPLICATION_JAVASCRIPT, UTF8);
        APPLICATION_OCTET_STREAM    = forMime(Mime.APPLICATION_OCTET_STREAM);
        APPLICATION_FORM_URLENCODED = forMime(Mime.APPLICATION_FORM_URLENCODED);
    }

    @SuppressWarnings("DuplicateStringLiteralInspection")
    public static final String CHARSET_PARAMETER = "charset";
}  // end class MediaType
