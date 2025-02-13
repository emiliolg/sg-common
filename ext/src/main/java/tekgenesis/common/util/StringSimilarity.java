
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import static tekgenesis.common.Predefined.notNull;

/**
 * String Similarity using Levenshtein Distance.
 */
@SuppressWarnings("WeakerAccess")
public class StringSimilarity {

    //~ Constructors .................................................................................................................................

    private StringSimilarity() {}

    //~ Methods ......................................................................................................................................

    /**
     * Returns the similarity between the 2 Strings in the range 0..100 100 the strings are equal 0.
     */
    public static int similarity(String a, String b) {
        final String s1 = notNull(a).toLowerCase();
        final String s2 = notNull(b).toLowerCase();
        final double l1 = s1.length();
        final double l2 = s2.length();
        if (l1 == 0 && l2 == 0) return 100;

        final double f = l1 > l2 ? (l1 - computeEditDistance(s1, s2)) / l1 : (l2 - computeEditDistance(s2, s1)) / l2;
        return (int) Math.round(f * 100);
    }

    private static int computeEditDistance(String s1, String s2) {
        final int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) costs[j] = j;
                else if (j > 0) {
                    int newValue = costs[j - 1];
                    if (s1.charAt(i - 1) != s2.charAt(j - 1)) newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                    costs[j - 1] = lastValue;
                    lastValue    = newValue;
                }
            }
            if (i > 0) costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }  // end method computeEditDistance
}
