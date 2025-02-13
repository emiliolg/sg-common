
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.service.html;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.Seq;
import tekgenesis.common.core.Builder;
import tekgenesis.type.Struct;

/**
 * Html instance builder.
 */
public interface HtmlInstanceBuilder<T extends HtmlInstanceBuilder<T>> extends Builder<Html> {

    //~ Methods ......................................................................................................................................

    /** Append Html argument to builder. */
    @NotNull T html(@NotNull String name, @NotNull Html value);

    /** Append Enum messages to builder. */
    @NotNull T msg(@NotNull String name, @NotNull String enumeration);

    /** Append param value to builder. */
    @NotNull T param(@NotNull String name, @Nullable Object value);

    //~ Inner Interfaces .............................................................................................................................

    interface Jade extends HtmlInstanceBuilder<Jade> {}

    interface Mustache extends HtmlInstanceBuilder<Mustache> {}

    interface Static extends HtmlInstanceBuilder<Static> {}

    interface Xhtml extends HtmlInstanceBuilder<Xhtml> {
        @Override Html.WithMetadata build();

        /** Append String argument to builder. */
        @NotNull Xhtml str(@NotNull String name, @NotNull String value);

        /** Append multiple String argument to builder. */
        @NotNull Xhtml str(@NotNull String name, @NotNull Seq<String> values);

        /** Append Struct argument to builder. */
        @NotNull Xhtml struct(@NotNull String name, @NotNull Struct value);

        /** Append multiple Struct argument to builder. */
        @NotNull Xhtml struct(@NotNull String name, @NotNull Seq<? extends Struct> values);
    }
}
