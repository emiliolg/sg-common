
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.io;

import java.io.IOException;
import java.io.Writer;

import org.jetbrains.annotations.NotNull;

/**
 * This class implements a Writer which does not write anything.
 */
@SuppressWarnings({ "UnusedDeclaration", "WeakerAccess" })
public class EmptyWriter extends Writer {

    //~ Constructors .................................................................................................................................

    /** Creates a new EmptyWriter instance. */
    public EmptyWriter() {
        super();
    }

    //~ Methods ......................................................................................................................................

    @Override public void close()
        throws IOException
    {
        // nothing to do
    }

    @Override public void flush()
        throws IOException
    {
        // nothing to do
    }

    @Override public void write(@NotNull char[] buf, int off, int len)
        throws IOException
    {
        // nothing to do
    }
}
