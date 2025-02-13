
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.exception.ApplicationException;

/**
 * Enum exception.
 */
public interface EnumException<E extends ApplicationException> {

    //~ Methods ......................................................................................................................................

    /** Returns default exception. */
    @NotNull E exception();

    /** Returns exception, formatted with arguments. */
    @NotNull E exception(Object... args);
}
