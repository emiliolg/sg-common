
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service;

import java.io.IOException;
import java.lang.reflect.Type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.logging.Logger;
import tekgenesis.common.media.MediaType;
import tekgenesis.common.service.etl.MessageConverter;
import tekgenesis.common.service.etl.StringMessageConverter;
import tekgenesis.common.service.exception.MessageConversionException;

import static tekgenesis.common.Predefined.cast;

/**
 * Interface for reading specified type object from inbound message.
 */
public class InboundMessageReader<T> {

    //~ Instance Fields ..............................................................................................................................

    @Nullable private MediaType defaultContentType;

    @NotNull private final Type     genericType;
    @NotNull private final Class<T> type;

    //~ Constructors .................................................................................................................................

    /** Create a reader with specified type. */
    public InboundMessageReader(@NotNull final Class<T> type) {
        this(type, type);
    }

    /** Create a reader with specified type and generic type. */
    public InboundMessageReader(@NotNull final Class<T> type, @NotNull final Type genericType) {
        this.type          = type;
        this.genericType   = genericType;
        defaultContentType = null;
    }

    //~ Methods ......................................................................................................................................

    /** Extract specified object from message. */
    public T read(@NotNull InboundMessage message, @NotNull Iterable<MessageConverter<?>> converters) {
        final MediaType contentType = getContentType(message);

        final MessageConverter<T> converter = getSuitableConverter(converters, contentType);

        if (converter == null) {
            final String msg  = "Could not extract inbound message: no suitable converter found " +
                                "for type '" + type + "' and content type '" + contentType + "'";
            final String body = bodyAsString(message);
            logger.error(msg + " with body: " + body);
            throw new MessageConversionException(msg);
        }

        try {
            return converter.read(type, genericType, contentType, message.getContent());
        }
        catch (final IOException e) {
            throw new MessageConversionException(e);
        }
    }

    /** Set reader default content type. */
    public void setDefaultContentType(@Nullable MediaType contentType) {
        defaultContentType = contentType;
    }

    private String bodyAsString(@NotNull final InboundMessage response) {
        try {
            return new StringMessageConverter().read(String.class, String.class, getContentType(response), response.getContent());
        }
        catch (final IOException e) {
            logger.error(e);
        }
        return "[cannot read message]";
    }

    private void log(@Nullable MediaType contentType, MessageConverter<?> messageConverter) {
        logger.debug("Reading '" + type.getName() + "' as '" + contentType + "' using '" + messageConverter + "'");
    }

    @Nullable private MediaType getContentType(@NotNull final InboundMessage message) {
        final MediaType contentType = message.getHeaders().getContentType();
        if (contentType == null) logger.debug("No Content-Type header found, reading message as " + defaultContentType);
        return contentType != null ? contentType : defaultContentType;
    }

    @Nullable private MessageConverter<T> getSuitableConverter(Iterable<MessageConverter<?>> converters, @Nullable MediaType contentType) {
        for (final MessageConverter<?> converter : converters) {
            if (converter.canRead(type, genericType, contentType)) {
                log(contentType, converter);
                return cast(converter);
            }
        }
        return null;
    }

    //~ Static Fields ................................................................................................................................

    private static final Logger logger = Logger.getLogger(InboundMessageReader.class);
}  // end class InboundMessageReader
