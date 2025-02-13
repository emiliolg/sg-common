
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service.etl;

import java.lang.reflect.Type;
import java.nio.charset.Charset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.collections.Seq;
import tekgenesis.common.media.MediaType;
import tekgenesis.common.media.Mime;
import tekgenesis.common.service.Headers;

import static tekgenesis.common.Predefined.notNull;
import static tekgenesis.common.collections.Colls.listOf;
import static tekgenesis.common.core.Constants.UTF8;

/**
 * Abstract message converter. Implements basic methods.
 */
public abstract class AbstractMessageConverter<T> implements MessageConverter<T> {

    //~ Instance Fields ..............................................................................................................................

    private final Seq<MediaType> supportedMimes;

    //~ Constructors .................................................................................................................................

    /** Construct a converter with no supported media types. */
    protected AbstractMessageConverter() {
        this(MediaType.ALL);
    }

    /** Construct a converter with one supported media type. */
    protected AbstractMessageConverter(MediaType mediaType) {
        this(listOf(mediaType));
    }

    /** Construct a converter with multiple supported media type. */
    protected AbstractMessageConverter(MediaType... mediaTypes) {
        this(ImmutableList.fromArray(mediaTypes));
    }

    private AbstractMessageConverter(@NotNull Seq<MediaType> supportedMimes) {
        this.supportedMimes = supportedMimes;
    }

    //~ Methods ......................................................................................................................................

    /** Checks if the given class and given mime are supported. */
    @Override public boolean canRead(Class<?> type, Type genericType, MediaType contentType) {
        return supports(type) && canRead(contentType);
    }

    /** Checks if the given class and given mime are supported. */
    @Override public boolean canWrite(Class<?> type, Type genericType, MediaType contentType) {
        return supports(type) && canWrite(contentType);
    }

    @Override public void write(T payload, MediaType contentType, Headers headers) {
        if (contentType == null && !supportedMimes.isEmpty()) headers.setContentType(supportedMimes.getFirst().get());

        if (contentType != null && headers.getContentType() == null) headers.setContentType(contentType);
        if (headers.getAccept().isEmpty()) headers.setAccept(supportedMimes);
    }

    @Override public Seq<MediaType> getSupportedMediaTypes() {
        return supportedMimes;
    }

    /** Returns true if the supported media types are compatible with the given mime. */
    protected boolean canRead(MediaType mediaType) {
        if (mediaType == null) return true;

        for (final MediaType supported : getSupportedMediaTypes()) {
            if (supported.includes(mediaType)) return true;
        }

        return false;
    }

    /** Returns true if the supported media types are compatible with the given mime. */
    protected boolean canWrite(@Nullable MediaType mediaType) {
        if (mediaType == null || Mime.ALL == mediaType.getMime()) return true;

        for (final MediaType supported : getSupportedMediaTypes()) {
            if (supported.includes(mediaType)) return true;
        }

        return false;
    }

    protected abstract boolean supports(Class<?> clazz);

    @NotNull protected Charset getContentTypeCharsetOrDefault(@Nullable MediaType mediaType, @NotNull Charset defaultCharset) {
        if (mediaType != null) return notNull(mediaType.getCharset(), defaultCharset);
        return defaultCharset;
    }

    //~ Static Fields ................................................................................................................................

    protected static final Charset DEFAULT_CHARSET = Charset.forName(UTF8);
}  // end class AbstractMessageConverter
