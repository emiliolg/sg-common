
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.util.Date;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.Integer.parseInt;

import static tekgenesis.common.core.DateOnly.current;
import static tekgenesis.common.core.Strings.extractDigits;

/**
 * More flexible date parser.
 */
public class PrettyDateParser {

    //~ Constructors .................................................................................................................................

    private PrettyDateParser() {}

    //~ Methods ......................................................................................................................................

    /** Pretty parse. */
    @Nullable public static Date prettyParse(@NotNull final String dateText, @NotNull final String pattern) {
        final String onlyDigits = extractDigits(dateText, dateText.length());
        final int    textLength = onlyDigits.length();

        if (textLength != 6 && textLength != 8) return null;  // if it's not larger than 6 chars, we can't do anything.

        int year  = 0;
        int month = 0;
        int day   = 0;

        final char firstLetter = pattern.toLowerCase().charAt(0);

        if (firstLetter == 'd') {
            day   = parseInt(onlyDigits.substring(0, 2));
            month = parseInt(onlyDigits.substring(2, 4));
            year  = textLength == 8 ? parseInt(onlyDigits.substring(4, 8)) : year(parseInt(onlyDigits.substring(4, 6)));
        }
        else if (firstLetter == 'm') {
            month = parseInt(onlyDigits.substring(0, 2));
            day   = parseInt(onlyDigits.substring(2, 4));
            year  = textLength == 8 ? parseInt(onlyDigits.substring(4, 8)) : year(parseInt(onlyDigits.substring(4, 6)));
        }
        else if (firstLetter == 'y') {
            year  = textLength == 8 ? parseInt(onlyDigits.substring(0, 4)) : year(parseInt(onlyDigits.substring(0, 2)));
            month = parseInt(textLength == 8 ? onlyDigits.substring(4, 6) : onlyDigits.substring(2, 4));
            day   = parseInt(textLength == 8 ? onlyDigits.substring(6, 8) : onlyDigits.substring(4, 6));
        }

        return year != 0 && month != 0 && day != 0 ? DateOnly.date(year, month, day).toDate() : null;
    }

    private static int year(int y) {
        final int actualYear   = current().getYear();
        final int probableYear = Y2K + y;

        if (probableYear - actualYear <= YEARS_AHEAD_30) return probableYear;
        else return YEAR_1900 + y;
    }

    //~ Static Fields ................................................................................................................................

    private static final int Y2K            = 2000;
    private static final int YEAR_1900      = 1900;
    private static final int YEARS_AHEAD_30 = 30;
}  // end class PrettyDateParser
