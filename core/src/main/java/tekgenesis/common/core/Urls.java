
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

import tekgenesis.common.Characters;

import static tekgenesis.common.Predefined.isEmpty;

/**
 * Url utility methods.
 */
public class Urls {

    //~ Constructors .................................................................................................................................

    private Urls() {}

    //~ Methods ......................................................................................................................................

    /** Url normalization method for pretty urls. */
    @NotNull public static String slugUrl(@NotNull String seoStr) {
        return slugUrl(seoStr, '-', true);
    }

    /**
     * Url normalization method for pretty urls or normalized user inputs. Result will need no
     * encoding.
     */
    @NotNull public static String slugUrl(@NotNull String seoStr, char whiteSpaceChar, boolean toLowerCase) {
        if (isEmpty(seoStr)) return seoStr;

        final StringBuilder result = new StringBuilder(seoStr.length());

        for (int i = 0; i < seoStr.length(); i++)
            slugChar(whiteSpaceChar, toLowerCase, result, seoStr.charAt(i));

        // remove ending 'white spaces'
        if (lastEq(result, whiteSpaceChar)) return result.substring(0, result.length() - 1);

        return result.toString();
    }  // end method slugUrl

    private static boolean lastEq(@NotNull StringBuilder result, char ch) {
        return result.length() > 0 && result.charAt(result.length() - 1) == ch;
    }

    private static boolean lastNeq(@NotNull StringBuilder result, char ch) {
        return result.length() > 0 && result.charAt(result.length() - 1) != ch;
    }

    @SuppressWarnings("IfStatementWithTooManyBranches")
    private static void slugChar(final char whiteSpaceChar, final boolean toLowerCase, final StringBuilder result, final char ch) {
        // not necessary to encode chars
        if ((ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9')) result.append(ch);

        // uppercase letters to lower case
        else if (ch >= 'A' && ch <= 'Z') result.append(toLowerCase ? Character.toLowerCase(ch) : ch);

        // not necessary to encode chars
        else if (ch == '-' || ch == '_' || ch == '.') {
            if (lastNeq(result, ch)) result.append(ch);
        }

        // whitespace for slush
        else if (ch == ' ' || ch == '\n' || ch == '\t') {
            if (lastNeq(result, whiteSpaceChar)) result.append(whiteSpaceChar);
        }

        // remove accent and other diacritics
        else if (Characters.isLatin(ch)) {
            final char inAscii = Characters.toAscii(ch);
            result.append(toLowerCase ? Character.toLowerCase(inAscii) : inAscii);
        }

        // else: ignore everything else
    }  // end method slugChar
}  // end class Urls
