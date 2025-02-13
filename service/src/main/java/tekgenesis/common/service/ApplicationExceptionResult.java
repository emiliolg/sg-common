
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.Predefined;

import static tekgenesis.common.Predefined.equal;
import static tekgenesis.common.core.QName.*;

/**
 * Application exception result to be sent if an exception(xyz) is returned from handlers.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationExceptionResult {

    //~ Instance Fields ..............................................................................................................................

    @NotNull private String code = "";
    @NotNull private String msg  = "";

    //~ Constructors .................................................................................................................................

    /** Jackson default constructor. */
    public ApplicationExceptionResult() {}

    /** Construct application exception result with given enum error and message. */
    public ApplicationExceptionResult(@NotNull Enum<?> error, @NotNull String msg) {
        code     = qualify(error.getDeclaringClass().getCanonicalName(), error.name());
        this.msg = msg;
    }

    //~ Methods ......................................................................................................................................

    public boolean equals(Object that) {
        return this == that || that instanceof ApplicationExceptionResult && eq((ApplicationExceptionResult) that);
    }

    public int hashCode() {
        return Predefined.hashCodeAll(code, msg);
    }

    /** Get exception enum fqn code. */
    @NotNull public String getCode() {
        return code;
    }

    /** Get exception enum class. */
    @NotNull public String getEnumClass() {
        return extractQualification(code);
    }

    /** Get exception enum name. */
    @NotNull public String getEnumName() {
        return extractName(code);
    }

    /** Get exception message. */
    @NotNull public String getMsg() {
        return msg;
    }

    private boolean eq(@NotNull ApplicationExceptionResult that) {
        return equal(code, that.code) && equal(msg, that.msg);
    }
}  // end class ApplicationExceptionResult
