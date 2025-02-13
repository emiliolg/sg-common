
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.core.Enumeration;

public enum TestMessages implements Enumeration<TestMessages, String> {

    //~ Enum constants ...............................................................................................................................

    NO_PARAMS("No Parameters"), ONE_PARAM("One Parameter %s"), TWO_PARAMS("Two Parameters %s %s"), THREE_PARAMS("Three Parameters %s %s %s"),
    TWO_PARAMS_INT("Two Parameters One INT %s %d"), TWO_PARAMS_BOOL("Two Parameters One BOOL %s %b");

    //~ Instance Fields ..............................................................................................................................

    @NotNull private final String label;

    //~ Constructors .................................................................................................................................

    TestMessages(@NotNull String label) {
        this.label = label;
    }

    //~ Methods ......................................................................................................................................

    @Override public int index() {
        return ordinal();
    }

    @NotNull public final String key() {
        return name();
    }

    @NotNull @Override public String label() {
        return label;
    }
}
