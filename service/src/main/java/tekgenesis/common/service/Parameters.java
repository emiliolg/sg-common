
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.service;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.MultiMap;
import tekgenesis.common.core.*;

import static java.net.URLEncoder.encode;

import static tekgenesis.common.collections.Colls.map;
import static tekgenesis.common.core.Constants.UTF8;

/**
 * Utility class to deal with parameters.
 */
public class Parameters {

    //~ Constructors .................................................................................................................................

    private Parameters() {}

    //~ Methods ......................................................................................................................................

    /** Map of parameters to query string. */
    public static String mapToQueryString(MultiMap<String, String> parameters) {
        return mapToQueryString(parameters.asMap());
    }

    /** Map of parameters to query string. */
    public static String mapToQueryString(Map<String, Collection<String>> stringCollectionMap) {
        return map(stringCollectionMap.entrySet(), Parameters::encodedQueryFromParameters).mkString("&");
    }

    /** String of query string to map of parameters. */
    @Nullable public static MultiMap<String, String> queryStringToMap(@Nullable String parameters) {
        return parameters == null ? null : parametersFromQuery(parameters);
    }

    @NotNull private static String encodedQueryFromParameters(final Map.Entry<String, Collection<String>> entry) {
        return map(entry.getValue(), value -> utf8Encode(entry.getKey()) + "=" + utf8Encode(value)).mkString("&");
    }

    @NotNull private static MultiMap<String, String> parametersFromQuery(final String parameters) {
        final MultiMap<String, String> result = MultiMap.createLinkedMultiMap();

        for (final String param : Strings.split(parameters, '&')) {
            final int i = param.indexOf("=");
            if (i != -1) result.put(param.substring(0, i), param.substring(i + 1));
        }

        return result;
    }

    /** Encode the url in UTF 8. */
    private static String utf8Encode(String str) {
        try {
            return encode(str, UTF8);
        }
        catch (final UnsupportedEncodingException e) {
            // UTF8 should be supported
            throw new RuntimeException(e);
        }
    }
}  // end class Parameters
