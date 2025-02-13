
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.web;

import org.jetbrains.annotations.NotNull;

/**
 * User-Agent analyzer.
 */
public final class UserAgentUtil {

    //~ Constructors .................................................................................................................................

    private UserAgentUtil() {}

    //~ Methods ......................................................................................................................................

    /**
     * @param   userAgent  User-Agent str
     *
     * @return  true or false
     */
    public static boolean isMobile(@NotNull String userAgent) {
        return userAgent.toLowerCase()
               .matches(
                "(?i).*((android|bb\\d+|meego).+mobile|avantgo|bada\\/|blackberry|" +
                "blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od|ad)|iris|kindle|lge |maemo|midp|mmp|netfront|" +
                "opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\\/|plucker|pocket|psp|series(4|6)0|symbian|treo|" +
                "up\\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino).*");
    }
}
