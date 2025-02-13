
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.json;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jetbrains.annotations.NotNull;

/**
 * Json Escapes utility class.
 */
public final class JsonEscapes {

    //~ Constructors .................................................................................................................................

    private JsonEscapes() {}

    //~ Methods ......................................................................................................................................

    /** Add html escaping for given mapper. */
    public static ObjectMapper escapeHtml(@NotNull final ObjectMapper mapper) {
        mapper.getFactory().setCharacterEscapes(new HTMLCharacterEscapes());
        return mapper;
    }

    //~ Inner Classes ................................................................................................................................

    private static class HTMLCharacterEscapes extends CharacterEscapes {
        private final int[] escapes;

        public HTMLCharacterEscapes() {
            // Start with set of characters known to require escaping (double-quote, backslash etc)
            escapes = CharacterEscapes.standardAsciiEscapesForJSON();
            // And force escaping of a few others:
            escapes['<'] = ESCAPE_STANDARD;
            escapes['>'] = ESCAPE_STANDARD;
            escapes['&'] = ESCAPE_STANDARD;
        }

        // This method gets called for character codes 0 - 127
        @Override public int[] getEscapeCodesForAscii() {
            return escapes;
        }

        // And this for others; we don't need anything special here, no further escaping (beyond ASCII chars) needed.
        @Override public SerializableString getEscapeSequence(int ch) {
            return null;
        }

        private static final long serialVersionUID = 8851450253968269035L;
    }
}  // end class JsonEscapes
