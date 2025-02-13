
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import org.jetbrains.annotations.NotNull;

/**
 * Interface that specifies a request/response modification or adaptation.
 */
public interface MessageModifier {

    //~ Methods ......................................................................................................................................

    /** Receives the original request to modify or adapt. */
    void modify(@NotNull final HttpConnectionRequest request);

    /** Receives the original response to modify or adapt. */
    void modify(@NotNull final HttpConnectionResponse response);
}
