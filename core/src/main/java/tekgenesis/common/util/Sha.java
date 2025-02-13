
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static tekgenesis.common.core.Constants.*;

/**
 * Manage the creation of SHA Digests.
 */
public class Sha {

    //~ Instance Fields ..............................................................................................................................

    private final MessageDigest messageDigest;

    //~ Constructors .................................................................................................................................

    /** Create a Default (256) SHA Digest. */
    public Sha() {
        messageDigest = createShaDigest();
    }

    //~ Methods ......................................................................................................................................

    /** Process the InputStream over the Digest and return the processed bytes. */
    public byte[] filter(InputStream inputStream) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        process(inputStream, out);
        return out.toByteArray();
    }

    /** Process the Reader over the Digest and return the processed chars. */
    public char[] filter(Reader reader) {
        final CharArrayWriter out = new CharArrayWriter();
        process(reader, out);
        return out.toCharArray();
    }

    /** Process the InputStream over the Digest. */
    public void process(InputStream inputStream) {
        process(inputStream, null);
    }

    /** Process the Reader over the Digest. */
    public void process(Reader reader) {
        process(reader, null);
    }

    /** Process the specified bytes over the digest. */
    public void process(byte[] bytes) {
        messageDigest.update(bytes);
    }

    /** Return the processed Message Digest. */
    @SuppressWarnings("WeakerAccess")
    public byte[] getDigest() {
        return messageDigest.digest();
    }

    /** Return the processed Message Digest as a Long Value. */

    public long getDigestAsLong() {
        final byte[] digested = getDigest();
        long         hash     = 0;
        for (int i = Math.min(digested.length, 8) - 1; i >= 0; i--)
            hash = (hash << 8) | (digested[i] & MAX_BYTE);
        return hash;
    }

    /** Return the processed Message Digest as an Hexa String. */
    public String getDigestAsString() {
        final StringBuilder s = new StringBuilder();
        for (final byte b : getDigest()) {
            final int n = b & MAX_BYTE;
            if (n < HEXADECIMAL_RADIX) s.append('0');
            s.append(Integer.toHexString(n));
        }
        return s.toString();
    }

    private void process(@NotNull Reader input, @Nullable Writer out) {
        try {
            final Charset utf8  = Charset.forName(UTF8);
            final char[]  chars = new char[4 * KILO];
            int           read;
            while ((read = read(input, chars)) != -1) {
                messageDigest.update(utf8.encode(CharBuffer.wrap(chars)));
                if (out != null) out.write(chars, 0, read);
            }
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void process(@NotNull InputStream input, @Nullable OutputStream out) {
        try {
            final byte[] bytes = new byte[4 * KILO];
            int          read;
            while ((read = read(input, bytes)) != -1) {
                messageDigest.update(bytes);
                if (out != null) out.write(bytes, 0, read);
            }
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    //~ Methods ......................................................................................................................................

    /** Get the Digest for the specified reader. */
    public static String digestAsString(Reader reader) {
        final Sha sha = new Sha();
        sha.process(reader);
        return sha.getDigestAsString();
    }

    private static MessageDigest createShaDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        }
        catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static int read(InputStream is, byte[] bytes) {
        try {
            return is.read(bytes);
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static int read(Reader reader, char[] chars) {
        try {
            return reader.read(chars);
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}  // end class Sha
