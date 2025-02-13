
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

/**
 * Character utilities.
 */
public class Characters {

    //~ Constructors .................................................................................................................................

    private Characters() {}

    //~ Methods ......................................................................................................................................

    /** Convert the character to an ascii one removing diacritics. */
    public static char toAscii(char c) {
        return isAscii(c) ? c : isLatin(c) ? (char) latinWithoutDiacritics[c - LATIN_LOW] : '?';
    }

    /** Returns true if the character is in the ascii range. */
    public static boolean isAscii(char c) {
        return c <= ASCII_HIGH;
    }

    /** Returns true if the character is in the Unicode Latin Block (U+00C0 to U+017F) range. */
    public static boolean isLatin(char c) {
        return c >= LATIN_LOW && c <= LATIN_HIGH;
    }

    //~ Static Fields ................................................................................................................................

    /** Mirror of the unicode table from 00c0 to 017f without diacritics. */
    private static final byte[] latinWithoutDiacritics = {
        (byte) 'A',                                                      // À
        (byte) 'A',                                                      // Á
        (byte) 'A',                                                      // Â
        (byte) 'A',                                                      // Ã
        (byte) 'A',                                                      // Ä
        (byte) 'A',                                                      // Å
        (byte) 'A',                                                      // Æ
        (byte) 'C',                                                      // Ç
        (byte) 'E',                                                      // È
        (byte) 'E',                                                      // É
        (byte) 'E',                                                      // Ê
        (byte) 'E',                                                      // Ë
        (byte) 'I',                                                      // Ì
        (byte) 'I',                                                      // Í
        (byte) 'I',                                                      // Î
        (byte) 'I',                                                      // Ï
        (byte) 'D',                                                      // Ð
        (byte) 'N',                                                      // Ñ
        (byte) 'O',                                                      // Ò
        (byte) 'O',                                                      // Ó
        (byte) 'O',                                                      // Ô
        (byte) 'O',                                                      // Õ
        (byte) 'O',                                                      // Ö
        (byte) 'x',                                                      // ×
        (byte) 'O',                                                      // Ø
        (byte) 'U',                                                      // Ù
        (byte) 'U',                                                      // Ú
        (byte) 'U',                                                      // Û
        (byte) 'U',                                                      // Ü
        (byte) 'Y',                                                      // Ý
        (byte) 'T',                                                      // Þ
        (byte) 'S',                                                      // ß
        (byte) 'a',                                                      // à
        (byte) 'a',                                                      // á
        (byte) 'a',                                                      // â
        (byte) 'a',                                                      // ã
        (byte) 'a',                                                      // ä
        (byte) 'a',                                                      // å
        (byte) 'a',                                                      // æ
        (byte) 'c',                                                      // ç
        (byte) 'e',                                                      // è
        (byte) 'e',                                                      // é
        (byte) 'e',                                                      // ê
        (byte) 'e',                                                      // ë
        (byte) 'i',                                                      // ì
        (byte) 'i',                                                      // í
        (byte) 'i',                                                      // î
        (byte) 'i',                                                      // ï
        (byte) 'd',                                                      // ð
        (byte) 'n',                                                      // ñ
        (byte) 'o',                                                      // ò
        (byte) 'o',                                                      // ó
        (byte) 'o',                                                      // ô
        (byte) 'o',                                                      // õ
        (byte) 'o',                                                      // ö
        (byte) '/',                                                      // ÷
        (byte) 'o',                                                      // ø
        (byte) 'u',                                                      // ù
        (byte) 'u',                                                      // ú
        (byte) 'u',                                                      // û
        (byte) 'u',                                                      // ü
        (byte) 'y',                                                      // ý
        (byte) 't',                                                      // þ
        (byte) 'y',                                                      // ÿ
        (byte) 'A',                                                      // Ā
        (byte) 'a',                                                      // ā
        (byte) 'A',                                                      // Ă
        (byte) 'a',                                                      // ă
        (byte) 'A',                                                      // Ą
        (byte) 'a',                                                      // ą
        (byte) 'C',                                                      // Ć
        (byte) 'c',                                                      // ć
        (byte) 'C',                                                      // Ĉ
        (byte) 'c',                                                      // ĉ
        (byte) 'C',                                                      // Ċ
        (byte) 'c',                                                      // ċ
        (byte) 'C',                                                      // Č
        (byte) 'c',                                                      // č
        (byte) 'D',                                                      // Ď
        (byte) 'd',                                                      // ď
        (byte) 'D',                                                      // Đ
        (byte) 'd',                                                      // đ
        (byte) 'E',                                                      // Ē
        (byte) 'e',                                                      // ē
        (byte) 'E',                                                      // Ĕ
        (byte) 'e',                                                      // ĕ
        (byte) 'E',                                                      // Ė
        (byte) 'e',                                                      // ė
        (byte) 'E',                                                      // Ę
        (byte) 'e',                                                      // ę
        (byte) 'E',                                                      // Ě
        (byte) 'e',                                                      // ě
        (byte) 'G',                                                      // Ĝ
        (byte) 'g',                                                      // ĝ
        (byte) 'G',                                                      // Ğ
        (byte) 'g',                                                      // ğ
        (byte) 'G',                                                      // Ġ
        (byte) 'g',                                                      // ġ
        (byte) 'G',                                                      // Ģ
        (byte) 'g',                                                      // ģ
        (byte) 'H',                                                      // Ĥ
        (byte) 'h',                                                      // ĥ
        (byte) 'H',                                                      // Ħ
        (byte) 'h',                                                      // ħ
        (byte) 'I',                                                      // Ĩ
        (byte) 'i',                                                      // ĩ
        (byte) 'I',                                                      // Ī
        (byte) 'i',                                                      // ī
        (byte) 'I',                                                      // Ĭ
        (byte) 'i',                                                      // ĭ
        (byte) 'I',                                                      // Į
        (byte) 'i',                                                      // į
        (byte) 'I',                                                      // İ
        (byte) 'i',                                                      // ı
        (byte) 'I',                                                      // Ĳ
        (byte) 'i',                                                      // ĳ
        (byte) 'J',                                                      // Ĵ
        (byte) 'j',                                                      // ĵ
        (byte) 'K',                                                      // Ķ
        (byte) 'k',                                                      // ķ
        (byte) 'k',                                                      // ĸ
        (byte) 'L',                                                      // Ĺ
        (byte) 'l',                                                      // ĺ
        (byte) 'L',                                                      // Ļ
        (byte) 'l',                                                      // ļ
        (byte) 'L',                                                      // Ľ
        (byte) 'l',                                                      // ľ
        (byte) 'L',                                                      // Ŀ
        (byte) 'l',                                                      // ŀ
        (byte) 'l',                                                      // Ł
        (byte) 'l',                                                      // ł
        (byte) 'N',                                                      // Ń
        (byte) 'n',                                                      // ń
        (byte) 'N',                                                      // Ņ
        (byte) 'n',                                                      // ņ
        (byte) 'N',                                                      // Ň
        (byte) 'n',                                                      // ň
        (byte) 'n',                                                      // ŉ
        (byte) 'N',                                                      // Ŋ
        (byte) 'n',                                                      // ŋ
        (byte) 'O',                                                      // Ō
        (byte) 'o',                                                      // ō
        (byte) 'O',                                                      // Ŏ
        (byte) 'o',                                                      // ŏ
        (byte) 'O',                                                      // Ő
        (byte) 'o',                                                      // ő
        (byte) 'O',                                                      // Œ
        (byte) 'o',                                                      // œ
        (byte) 'R',                                                      // Ŕ
        (byte) 'r',                                                      // ŕ
        (byte) 'R',                                                      // Ŗ
        (byte) 'r',                                                      // ŗ
        (byte) 'R',                                                      // Ř
        (byte) 'r',                                                      // ř
        (byte) 'S',                                                      // Ś
        (byte) 's',                                                      // ś
        (byte) 'S',                                                      // Ŝ
        (byte) 's',                                                      // ŝ
        (byte) 'S',                                                      // Ş
        (byte) 's',                                                      // ş
        (byte) 'S',                                                      // Š
        (byte) 's',                                                      // š
        (byte) 'T',                                                      // Ţ
        (byte) 't',                                                      // ţ
        (byte) 'T',                                                      // Ť
        (byte) 't',                                                      // ť
        (byte) 'T',                                                      // Ŧ
        (byte) 't',                                                      // ŧ
        (byte) 'U',                                                      // Ũ
        (byte) 'u',                                                      // ũ
        (byte) 'U',                                                      // Ū
        (byte) 'u',                                                      // ū
        (byte) 'U',                                                      // Ŭ
        (byte) 'u',                                                      // ŭ
        (byte) 'U',                                                      // Ů
        (byte) 'u',                                                      // ů
        (byte) 'U',                                                      // Ű
        (byte) 'u',                                                      // ű
        (byte) 'U',                                                      // Ų
        (byte) 'u',                                                      // ų
        (byte) 'W',                                                      // Ŵ
        (byte) 'w',                                                      // ŵ
        (byte) 'Y',                                                      // Ŷ
        (byte) 'y',                                                      // ŷ
        (byte) 'Y',                                                      // Ÿ
        (byte) 'Z',                                                      // Ź
        (byte) 'z',                                                      // ź
        (byte) 'Z',                                                      // Ż
        (byte) 'z',                                                      // ż
        (byte) 'Z',                                                      // Ž
        (byte) 'z',                                                      // ž
        (byte) 'f',                                                      // ſ
    };

    private static final char ASCII_HIGH = 0x7f;
    private static final char LATIN_LOW  = 0xC0;
    private static final char LATIN_HIGH = 0x17F;
}  // end class Characters
