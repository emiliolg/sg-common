
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.service.html;

import java.io.IOException;
import java.io.Writer;

/**
 * Represents an html template.
 */
public interface Html {

    //~ Methods ......................................................................................................................................

    /** Return the html key. */
    String key();

    /** Writes the processed html to the provided Writer. */
    void render(Writer writer)
        throws IOException;

    /** Return the html unique hash. */
    String getHash();

    //~ Inner Interfaces .............................................................................................................................

    interface WithMetadata extends Html {
        /** Add metadata property to html. */
        void metadata(String property, String content);
    }
}
