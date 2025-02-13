
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

import tekgenesis.common.core.Resource;

/**
 * Html builder.
 */
public interface HtmlBuilder {

    //~ Methods ......................................................................................................................................

    /** Return an html instance builder for given path. */
    @NotNull HtmlInstanceBuilder.Xhtml html(@NotNull String path);

    /** Return an html instance builder for given resource. */
    @NotNull HtmlInstanceBuilder.Xhtml html(@NotNull Resource resource);

    /** Return an html instance builder for a given source code. */
    @NotNull HtmlInstanceBuilder.Xhtml htmlSource(@NotNull String html);

    /** Return an html instance builder for given path. */
    @NotNull HtmlInstanceBuilder.Jade jade(@NotNull String path);

    /** Return an html instance builder for given resource. */
    @NotNull HtmlInstanceBuilder.Jade jade(@NotNull Resource resource);

    /** Return an html instance builder for a given source code. */
    @NotNull HtmlInstanceBuilder.Jade jadeSource(@NotNull String html);

    /** Return an html instance builder for given path. */
    @NotNull HtmlInstanceBuilder.Mustache mustache(@NotNull String path);

    /** Return an html instance builder for given resource. */
    @NotNull HtmlInstanceBuilder.Mustache mustache(@NotNull Resource resource);

    /** Return an html instance builder for a given source code. */
    @NotNull HtmlInstanceBuilder.Mustache mustacheSource(@NotNull String html);

    /** Return an html instance builder for a given source code. */
    @NotNull HtmlInstanceBuilder.Static staticSource(@NotNull String html);
}
