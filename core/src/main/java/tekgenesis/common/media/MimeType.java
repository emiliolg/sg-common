
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.media;

import org.jetbrains.annotations.NotNull;

/**
 * Represent a Mime Type.
 */
public interface MimeType {

    //~ Methods ......................................................................................................................................

    /** Return mime sub type. */
    @NotNull String getSubtype();

    /** Return mime type. */
    @NotNull String getType();
}
