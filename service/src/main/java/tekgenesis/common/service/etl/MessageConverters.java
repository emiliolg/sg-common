
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service.etl;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

/**
 * Utility class to deal with {@link MessageConverter}. Any converter listed here may be extended or
 * replaced.
 */
public class MessageConverters {

    //~ Constructors .................................................................................................................................

    private MessageConverters() {}

    //~ Methods ......................................................................................................................................

    /** Add default application exception message converter. */
    public static void withApplicationExceptionConverter(@NotNull Consumer<MessageConverter<?>> consumer) {
        consumer.accept(EXCEPTION_CONVERTER);
    }

    /** Add default basic types message converter. */
    public static void withBasicTypeConverter(@NotNull Consumer<MessageConverter<?>> consumer) {
        consumer.accept(BASIC_TYPE_CONVERTER);
    }

    /** Add default byte message converter. */
    public static void withByteConverter(@NotNull Consumer<MessageConverter<?>> consumer) {
        consumer.accept(BYTE_CONVERTER);
    }

    /** Add default form message converter. */
    public static void withFormConverter(@NotNull Consumer<MessageConverter<?>> consumer) {
        consumer.accept(FORM_CONVERTER);
    }

    /** Add default json message converter. */
    public static void withJsonConverter(@NotNull Consumer<MessageConverter<?>> consumer) {
        consumer.accept(JSON_CONVERTER);
    }

    /** Add default string message converter. */
    public static void withStringConverter(@NotNull Consumer<MessageConverter<?>> consumer) {
        consumer.accept(STRING_CONVERTER);
    }

    /** Add default xml message converter. */
    public static void withXmlConverter(@NotNull Consumer<MessageConverter<?>> consumer) {
        consumer.accept(XML_CONVERTER);
    }

    //~ Static Fields ................................................................................................................................

    private static final ByteMessageConverter                 BYTE_CONVERTER       = new ByteMessageConverter();
    private static final FormMessageConverter                 FORM_CONVERTER       = new FormMessageConverter();
    private static final BasicTypeMessageConverter            BASIC_TYPE_CONVERTER = new BasicTypeMessageConverter();
    private static final JsonMessageConverter                 JSON_CONVERTER       = new JsonMessageConverter();
    private static final ApplicationExceptionMessageConverter EXCEPTION_CONVERTER  = new ApplicationExceptionMessageConverter();
    private static final StringMessageConverter               STRING_CONVERTER     = new StringMessageConverter();
    private static final XmlMessageConverter                  XML_CONVERTER        = new XmlMessageConverter();
}  // end class MessageConverters
