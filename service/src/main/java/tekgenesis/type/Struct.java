
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.type;

import java.io.OutputStream;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.json.JsonMapping;

/**
 * Struct type.
 */
public interface Struct {

    //~ Methods ......................................................................................................................................

    /** Attempt to serialize instance to a json string. */
    default String toJson() {
        return JsonMapping.toJson(this);
    }

    /** Attempt to serialize instance to a json string into given output stream. */
    default void toJson(@NotNull OutputStream stream) {
        JsonMapping.toJson(stream, this);
    }
}
