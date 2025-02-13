
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.io.FileFilter;

/**
 * Handy file filters.
 */
public class FileFilters {

    //~ Constructors .................................................................................................................................

    private FileFilters() {}

    //~ Methods ......................................................................................................................................

    /** A filter that accepts files with the specified Extension. */
    public static FileFilter withExtension(String extension) {
        final String ext = extension.charAt(0) == '.' ? extension : '.' + extension;
        return pathName -> pathName.getName().endsWith(ext);
    }

    //~ Static Fields ................................................................................................................................

    /** Accept ALL files. */
    public static final FileFilter ALL = pathName -> true;
}
