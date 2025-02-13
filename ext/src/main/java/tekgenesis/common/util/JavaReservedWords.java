
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jetbrains.annotations.NonNls;

import tekgenesis.common.core.Constants;

import static tekgenesis.common.core.Constants.BOOLEAN;

/**
 * The List of Java Keywords.
 */
@SuppressWarnings("DuplicateStringLiteralInspection")
public class JavaReservedWords {

    //~ Constructors .................................................................................................................................

    private JavaReservedWords() {}

    //~ Methods ......................................................................................................................................

    /** Returns true if the specified String is a Reserved Java Word. */
    public static boolean isReserved(String s) {
        return RESERVED_WORDS.contains(s);
    }

    //~ Static Fields ................................................................................................................................

    private static final Set<String> RESERVED_WORDS;

    public static final String CLASS      = Constants.CLASS;
    public static final String ENUM       = "enum";
    public static final String EXTENDS    = "extends";
    public static final String IMPLEMENTS = "implements";
    public static final String ABSTRACT   = "abstract";
    public static final String IMPORT     = "import";
    public static final String INTERFACE  = "interface";
    public static final String PACKAGE    = "package";
    public static final String FINAL      = "final";

    public static final String SUPER = "super";

    public static final String FLOAT = "float";

    public static final String RETURN = "return";

    public static final String VOID = "void";

    public static final String THIS = "this";

    public static final String THROW = "throw";

    public static final String NEW = "new";

    public static final String INSTANCE_OF = "instanceof";

    public static final String NULL = "null";

    public static final String FALSE = "false";

    @NonNls public static final String TRUE = "true";

    @NonNls public static final String PUBLIC = "public";

    static {
        RESERVED_WORDS = new LinkedHashSet<>();
        Collections.addAll(RESERVED_WORDS,
            ABSTRACT,
            "assert",
            BOOLEAN,
            "break",
            "byte",
            "case",
            "catch",
            "char",
            CLASS,
            "const",
            "continue",
            Constants.DEFAULT,
            "do",
            Constants.DOUBLE,
            "else",
            "enum",
            EXTENDS,
            FALSE,
            "final",
            "finally",
            FLOAT,
            "for",
            "goto",
            "if",
            IMPLEMENTS,
            IMPORT,
            INSTANCE_OF,
            "int",
            INTERFACE,
            "long",
            "native",
            NEW,
            NULL,
            PACKAGE,
            "private",
            "protected",
            PUBLIC,
            RETURN,
            "short",
            "static",
            "strictfp",
            SUPER,
            "switch",
            "synchronized",
            TRUE,
            THIS,
            THROW,
            "throws",
            "transient",
            "try",
            VOID,
            "volatile",
            "while");
    }
}  // end class JavaReservedWords
