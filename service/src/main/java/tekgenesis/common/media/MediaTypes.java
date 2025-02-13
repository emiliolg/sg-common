
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.media;

import java.nio.charset.UnsupportedCharsetException;
import java.util.LinkedHashMap;
import java.util.Map;

import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.core.Strings;

import static tekgenesis.common.Predefined.isEmpty;
import static tekgenesis.common.media.Mimes.WILDCARD;

/**
 * Utility class to deal with {@link MediaType media types}.
 */
class MediaTypes {

    //~ Constructors .................................................................................................................................

    private MediaTypes() {}

    //~ Methods ......................................................................................................................................

    /**
     * Indicate whether base {@code MediaType} includes the other given media type. For instance,
     * {@code text/*} includes {@code text/plain} and {@code text/html}, and
     * {@code application/*+xml} includes {@code application/soap+xml}, etc. This method is not
     * symmetric.
     *
     * @param   base   the base media type
     * @param   other  the reference media type with which to compare
     *
     * @return  true if this media type includes the given media type.
     */
    public static boolean includes(MimeType base, MimeType other) {
        if (other == null) return false;

        if (base.getType().equals(WILDCARD)) return true;

        if (base.getType().equals(other.getType())) {
            if (base.getSubtype().equals(other.getSubtype())) return true;

            if (base.getSubtype().equals(WILDCARD)) {
                // wildcard with suffix, e.g. application/*+xml
                final int basePlus = base.getSubtype().indexOf('+');
                if (basePlus == -1) return true;
                else {
                    // application/*+xml includes application/soap+xml
                    final int otherPlus = other.getSubtype().indexOf('+');
                    if (otherPlus != -1) {
                        final String baseSubtypeNoSuffix = base.getSubtype().substring(0, basePlus);
                        final String baseSubtypeSuffix   = base.getSubtype().substring(basePlus + 1);
                        final String otherSubtypeSuffix  = other.getSubtype().substring(otherPlus + 1);
                        if (baseSubtypeSuffix.equals(otherSubtypeSuffix) && WILDCARD.equals(baseSubtypeNoSuffix)) return true;
                    }
                }
            }
        }
        return false;
    }  // end method includes

    /**
     * Parse the given String into a single {@link MediaType}.
     *
     * @param   mime  the string to parse
     *
     * @return  the media type
     *
     * @throws  InvalidMediaTypeException  if the string cannot be parsed
     */
    static MediaType parseMediaType(String mime) {
        if (isEmpty(mime)) throw error(mime, "'mime' must not be empty");

        final ImmutableList<String> parts = Strings.split(mime, ';');

        String fullType = parts.get(0).trim();

        if (WILDCARD.equals(fullType)) fullType = Mime.ALL.getMime();

        final int subIndex = fullType.indexOf('/');

        if (subIndex == -1) throw error(mime, "does not contain '/'");

        if (subIndex == fullType.length() - 1) throw error(mime, "does not contain subtype after '/'");

        final String type    = fullType.substring(0, subIndex);
        final String subtype = fullType.substring(subIndex + 1, fullType.length());

        if (WILDCARD.equals(type) && !WILDCARD.equals(subtype)) throw error(mime, "wildcard type is legal only in '*/*' (all mime types)");

        Map<String, String> parameters = null;
        if (parts.size() > 1) {
            parameters = new LinkedHashMap<>(parts.size() - 1);
            for (int i = 1; i < parts.size(); i++) {
                final String param = parts.get(i);
                final int    eq    = param.indexOf('=');
                if (eq != -1) parameters.put(param.substring(0, eq), param.substring(eq + 1, param.length()));
            }
        }

        try {
            final Mime m = Mime.fromMimeString(type + "/" + subtype);
            if (m == null) throw error(mime, "undefined Mime for '" + type + "/" + subtype + "'");
            return parameters != null ? new MediaType(m, parameters) : new MediaType(m);
        }
        catch (final UnsupportedCharsetException ex) {
            throw error(mime, "unsupported charset '" + ex.getCharsetName() + "'");
        }
        catch (final IllegalArgumentException ex) {
            throw error(mime, ex.getMessage());
        }
    }  // end method parseMediaType

    private static InvalidMediaTypeException error(String mime, String message) {
        return new InvalidMediaTypeException(mime, message);
    }
}
