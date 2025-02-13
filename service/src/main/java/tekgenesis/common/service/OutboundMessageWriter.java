
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.logging.Logger;
import tekgenesis.common.media.MediaType;
import tekgenesis.common.service.etl.MessageConverter;

import static tekgenesis.common.Predefined.cast;
import static tekgenesis.common.logging.Logger.Level.DEBUG;

/**
 * Interface for writing outbound messages payload.
 */
public class OutboundMessageWriter {

    //~ Instance Fields ..............................................................................................................................

    @Nullable private MediaType defaultContentType = null;

    //~ Methods ......................................................................................................................................

    /** Perform writing on outbound message. Modify headers, write content, and so on. */
    public void write(@NotNull OutboundMessage message, @NotNull Iterable<MessageConverter<?>> converters, @NotNull Object payload)
        throws IOException
    {
        final Class<?>  payloadType = payload.getClass();
        final MediaType contentType = getContentType(message);
        for (final MessageConverter<?> messageConverter : converters) {
            if (messageConverter.canWrite(payloadType, payloadType, contentType)) {
                log(contentType, messageConverter, payload);
                final MessageConverter<Object> typedConverter = cast(messageConverter);
                typedConverter.write(payload, contentType, message.getHeaders());
                // Getting output stream from outbound message will automatically write headers
                typedConverter.write(payload, contentType, message.getContent());
                return;
            }
        }
        String msg = "Could not write message: no suitable MessageConverter found for payload type [" + payloadType.getName() + "]";
        if (contentType != null) msg += " and content type [" + contentType + "]";
        throw new IllegalStateException(msg);
    }

    /** Set writer default content type. */
    public void setDefaultContentType(@Nullable MediaType contentType) {
        defaultContentType = contentType;
    }

    @SuppressWarnings("DuplicateStringLiteralInspection")
    private void log(@Nullable MediaType contentType, MessageConverter<?> messageConverter, Object payload) {
        if (logger.isLoggable(DEBUG)) {
            if (contentType != null) logger.debug("Writing [" + payload + "] as \"" + contentType + "\" using [" + messageConverter + "]");
            else logger.debug("Writing [" + payload + "] using [" + messageConverter + "]");
        }
    }

    @Nullable private MediaType getContentType(@NotNull final OutboundMessage message) {
        final MediaType contentType = message.getHeaders().getContentType();
        if (contentType == null && logger.isLoggable(DEBUG)) logger.debug("No Content-Type header found, writing message as " + defaultContentType);
        return contentType != null ? contentType : defaultContentType;
    }

    //~ Static Fields ................................................................................................................................

    private static final Logger logger = Logger.getLogger(OutboundMessageWriter.class);
}  // end class OutboundMessageWriter
