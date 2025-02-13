
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Age.
 */
public class Age {

    //~ Instance Fields ..............................................................................................................................

    @JsonProperty public int value;

    //~ Constructors .................................................................................................................................

    public Age() {}

    Age(int v) {
        value = v;
    }

    //~ Methods ......................................................................................................................................

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Age age = (Age) o;
        return value == age.value;
    }

    @Override public int hashCode() {
        return value;
    }
}
