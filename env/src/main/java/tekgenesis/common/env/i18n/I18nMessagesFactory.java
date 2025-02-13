
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.i18n;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Locale;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.core.Option;
import tekgenesis.common.env.context.Context;

import static java.lang.reflect.Proxy.newProxyInstance;

import static tekgenesis.common.core.Option.of;

/**
 * Factory class to create instances of the given class on non-GWT code.
 */
public class I18nMessagesFactory {

    //~ Constructors .................................................................................................................................

    private I18nMessagesFactory() {}

    //~ Methods ......................................................................................................................................

    /** Creates an instance of the given class on non-GWT code. */
    public static <T extends I18nMessages> T create(final Class<T> clazz) {
        try {
            final Class<?>[] interfaces = { clazz };
            return clazz.cast(newProxyInstance(clazz.getClassLoader(), interfaces, new Handler<>(clazz, Option.empty())));
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Creates an instance of the given class on non-GWT code with a given Locale. */
    public static <T extends I18nMessages> T create(@NotNull final Class<T> clazz, @NotNull Locale locale) {
        try {
            final Class<?>[] interfaces = { clazz };
            final Handler<T> handler    = new Handler<>(clazz, of(locale));
            handler.bundle.setClassLoader(clazz.getClassLoader());
            return clazz.cast(newProxyInstance(clazz.getClassLoader(), interfaces, handler));
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    //~ Inner Classes ................................................................................................................................

    private static class Handler<T> implements InvocationHandler {
        private final I18nBundle     bundle;
        private final Option<Locale> locale;

        public Handler(Class<T> clazz, Option<Locale> locale) {
            bundle      = I18nBundle.getBundle(clazz);
            this.locale = locale;
        }

        @Override public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable
        {
            final String defaultValue = method.getAnnotation(I18nMessages.DefaultMessage.class).value();
            final String msg          = bundle.getString(method.getName(), defaultValue, locale.orElse(Context.getContext().getLocale()));
            return args == null ? msg : MessageFormat.format(msg, args);
        }
    }
}  // end class I18nMessagesFactory
