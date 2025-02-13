
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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.core.Constants;
import tekgenesis.common.core.Functions;
import tekgenesis.common.core.StrBuilder;

import static java.util.Arrays.asList;

import static tekgenesis.common.collections.Colls.emptyList;
import static tekgenesis.common.collections.Colls.map;
import static tekgenesis.common.core.Constants.KILO;

/**
 * Common utility classes to deal with Files and IO in general.
 */
@SuppressWarnings({ "WeakerAccess", "ClassWithTooManyMethods" })  // Util class
public class Files {

    //~ Constructors .................................................................................................................................

    private Files() {}

    //~ Methods ......................................................................................................................................

    /** Given an IOSupplier, handles IOException and throws an Unchecked one. */
    public static <T> T asUnchecked(IOSupplier<T> s) {
        try {
            return s.get();
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Given an IORunnable, handles IOException and throws an Unchecked one. */
    public static void asUnchecked(IORunnable r) {
        try {
            r.run();
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Change the extension for a {@link File}. */
    public static File changeExtension(File file, String newExt) {
        return new File(file.getParentFile(), removeExtension(file) + (newExt.startsWith(".") ? newExt : "." + newExt));
    }

    /** Unconditionally close a <code>Closeable</code>, any exceptions will be ignored. */
    public static void close(@Nullable Closeable closeable) {
        try {
            if (closeable != null) closeable.close();
        }
        catch (final IOException ioe) {
            // ignore
        }
    }

    /** Copy a whole stream to a new one. */
    @SuppressWarnings("UnusedReturnValue")
    public static int copy(InputStream in, OutputStream out) {
        return copy(in, out, true);
    }

    /** Copy a whole {@link Reader} to a {@link Writer}. */
    public static int copy(Reader in, Writer out) {
        int size = 0;
        try {
            int          len;
            final char[] buffer = new char[4 * KILO];
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
                size += len;
            }
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }

        close(in);
        close(out);

        return size;
    }

    /** Copy a whole stream to a new one. */
    public static int copy(@NotNull InputStream in, OutputStream out, boolean closeOutput) {
        return copy(in, out, true, closeOutput);
    }

    /** Copy source to target if it is newer. iF the force argument is true, always copy it */
    public static void copy(File mm, File target, boolean force)
        throws IOException
    {
        ensureDirExists(target.getParentFile());
        if (force || target.lastModified() < mm.lastModified()) copy(new FileInputStream(mm), new FileOutputStream(target));
    }
    /** Copy a whole stream to a new one. */
    public static int copy(@NotNull InputStream in, OutputStream out, boolean closeInput, boolean closeOutput) {
        int size = 0;
        try {
            int          len;
            final byte[] buffer = new byte[KILO];
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
                size += len;
            }
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }

        if (closeInput) close(in);
        if (closeOutput) close(out);

        return size;
    }

    /** Copy a whole directory hierarchy to a new destination. */
    public static void copyDirectory(@NotNull File sourceDir, @NotNull File targetDir)
        throws IOException
    {
        final File[] files = sourceDir.listFiles();
        if (files == null) return;

        for (final File source : files) {
            if (source.isFile()) copyToDir(source, targetDir);
            else if (source.isDirectory()) {
                final File target = new File(targetDir, source.getName());
                if (!target.exists() && !target.mkdirs()) throw new IOException("Cannot create directory '" + target.getAbsolutePath());

                copyDirectory(source, target);
            }
        }
    }  // end method copyDirectory

    /** Copy the Set of files from the source directory to the specified output directory. */
    public static void copyFiles(File sourceDir, Iterable<String> sourceFiles, File outputDir, boolean force)
        throws IOException
    {
        for (final String file : sourceFiles)
            copy(new File(sourceDir, file), new File(outputDir, file), force);
    }
    /** Copy a file to a target directory. */
    @SuppressWarnings("WeakerAccess")
    public static void copyToDir(@NotNull File source, @NotNull File targetDir)
        throws IOException
    {
        copy(source, new File(targetDir, source.getName()), true);
    }
    /** Check that the specified directory exists, if not try to create it. */
    public static boolean deleteDirIfEmpty(File targetDir) {
        return targetDir.exists() && targetDir.isDirectory() && targetDir.delete();
    }

    /** Check that the specified directory exists, if not try to create it. */
    public static void ensureDirExists(File targetDir)
        throws IOException
    {
        if (!targetDir.exists() && !targetDir.mkdirs()) throw new IOException("Cannot create directory: " + targetDir.getAbsolutePath());
    }

    /** Compares 2 files line by line returns true if both files are equal, false otherwise. */
    public static boolean equalsContent(Reader first, Reader second) {
        final BufferedReader a = new BufferedReader(first);
        final BufferedReader b = new BufferedReader(second);
        try {
            String line1;
            while ((line1 = a.readLine()) != null) {
                final String line2 = b.readLine();
                if (line2 == null || !line1.equals(line2)) return false;
            }
            return b.readLine() == null;
        }
        catch (final IOException e) {
            return false;
        }
        finally {
            close(a);
            close(b);
        }
    }

    /** The extension for a File name. */
    public static String extension(String fileName) {
        return extension(new File(fileName));
    }

    /** The extension for a {@link File}. */
    public static String extension(File file) {
        final String name  = file.getName();
        final int    index = name.lastIndexOf('.');
        return index == -1 ? "" : name.substring(index + 1);
    }

    /** Return the File for a specific URL. */
    public static File fromUrl(URL url) {
        try {
            return new File(url.toURI());
        }
        catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /** Recursively select all files that verify the specified condition. */
    public static ImmutableList<String> list(@NotNull File root, @NotNull FileFilter filter) {
        final ImmutableList.Builder<String> result = ImmutableList.builder();
        list(result, root, filter);
        return result.build();
    }

    /** Recursively select all files that verify the specified pattern. */
    public static ImmutableList<String> list(@NotNull File root, @NotNull final String pattern) {
        final Pattern p = Pattern.compile(pattern);
        return list(root, f -> p.matcher(f.getPath()).matches());
    }

    /** Recursively select all files that verify the specified condition. */
    public static List<String> list(Iterable<File> roots, FileFilter filter) {
        final ImmutableList.Builder<String> result = ImmutableList.builder();
        for (final File root : roots)
            list(result, root, filter);
        return result.build();
    }
    /**
     * Normalize a File name.<br>
     * 1. All "." segments are removed.<br>
     * 2. If a ".." segment is preceded by a non-".." segment then both of these segments are
     * removed. This step is repeated until it is no longer applicable.
     */
    public static String normalize(String fileName) {
        try {
            return new URI(fileName).normalize().getPath();
        }
        catch (final URISyntaxException e) {
            return fileName;
        }
    }

    /**
     * Normalize a File name.<br>
     * 1. All "." segments are removed.<br>
     * 2. If a ".." segment is preceded by a non-".." segment then both of these segments are
     * removed. This step is repeated until it is no longer applicable.
     */
    public static File normalize(File file) {
        return new File(normalize(file.getPath()));
    }

    /** Converts all line separators in the specified string to the Unix separator. */
    @Nullable public static String normalizeLineSeparators(@Nullable String str) {
        return str == null ? null : str.replaceAll("\r\n|\r", "\n");
    }

    /** Wrap in a PrintWriter on demand. */
    public static PrintWriter printWriter(final Writer writer) {
        return writer instanceof PrintWriter ? (PrintWriter) writer : new PrintWriter(writer);
    }
    /** Get the content of a <code>Reader</code> as a String. */
    public static String readInput(@Nullable Reader input) {
        if (input == null) return "";

        final StrBuilder builder = new StrBuilder();
        builder.startCollection("\n");

        try(final BufferedReader reader = new BufferedReader(input)) {
            String line;
            while ((line = reader.readLine()) != null)
                builder.appendElement(line);
        }
        catch (final IOException ignored) {}

        return builder.toString();
    }

    /**
     * Get the content of a <code>Reader</code> as a list of Strings, one entry per line. Returns an
     * empty List if an IOException is raised during reading
     */
    public static ImmutableList<String> readLines(@Nullable Reader input) {
        if (input == null) return emptyList();

        try(final BufferedReader reader = new BufferedReader(input)) {
            final ImmutableList.Builder<String> lines = ImmutableList.builder();
            String                              line;
            while ((line = reader.readLine()) != null)
                lines.add(line);
            return lines.build();
        }
        catch (final IOException e) {
            return emptyList();
        }
    }
    /**
     * Get the content of a <code>File</code> as a list of Strings, one entry per line. Returns an
     * empty List if an IOException is raised during reading
     */
    public static ImmutableList<String> readLines(File file) {
        try {
            return readLines(new FileReader(file));
        }
        catch (final FileNotFoundException e) {
            return emptyList();
        }
    }

    /** Removes a directory with all its files. */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void remove(@NotNull File dir) {
        if (dir.exists()) {
            final File[] listedFiles = dir.listFiles();
            if (listedFiles != null) {
                for (final File file : listedFiles) {
                    if (file.isDirectory()) remove(file);
                    else file.delete();
                }
            }
            dir.delete();
        }
    }

    /** Remove the extension for a {@link File}. */
    public static String removeExtension(final File file) {
        final String name  = file.getName();
        final int    index = name.lastIndexOf('.');
        return index == -1 ? name : name.substring(0, index);
    }

    /** Copy a whole stream to a new one. */
    public static byte[] toByteArray(InputStream in) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream(4 * KILO);
        copy(in, out, true, true);
        return out.toByteArray();
    }

    /**
     * Make a path independent from system OS. For example, windows path separator is '\', but it's
     * changed to '/'
     */
    @Nullable public static String toSystemIndependentName(@Nullable String path) {
        return path == null ? null : path.replace('\\', '/');
    }

    /** Return an url array or the specified files. */
    public static URL[] toURL(@NotNull final List<File> file) {
        final URL[] result = new URL[file.size()];
        for (int i = 0; i < result.length; i++)
            result[i] = toUrl(file.get(i));
        return result;
    }

    /** Return the url or the specified file. */
    @SuppressWarnings("WeakerAccess")
    public static URL toUrl(@NotNull final File file) {
        try {
            return file.toURI().toURL();
        }
        catch (final MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** Write the list of Strings to the writer. One by line. */
    public static void writeLines(Writer output, Iterable<String> lines) {
        final PrintWriter writer = new PrintWriter(output);
        for (final String line : lines)
            writer.println(line);
        close(writer);
    }

    /**
     * Write the list of Strings to the file, using UTF8 encoding. One by line. Ensure the directory
     * for the file exists
     */
    public static void writeLines(File file, Iterable<String> lines) {
        try {
            ensureDirExists(file.getParentFile());
            writeLines(new OutputStreamWriter(new FileOutputStream(file), Constants.UTF8), lines);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Write the list of Strings to the file, using UTF8 encoding. One by line. Ensure the directory
     * for the file exists
     */
    public static void writeLines(File file, String... lines) {
        writeLines(file, asList(lines));
    }

    /**
     * Write the Map to a file, using UTF8 encoding. One entry by line, using the 'key : value'
     * format. Ensure the directory for the file exists
     */
    public static void writeLines(File file, Map<String, ?> entries) {
        writeLines(file, map(entries.entrySet(), Functions.MAP_ENTRY_TO_STRING));
    }

    /**
     * Removes authority from url String and returns its corresponding URI Authority must be removed
     * in Windows ecosystem so that URI instantiation works correctly.
     */
    @NotNull public static URI getURIWithoutFileAuthority(@NotNull final String url)
        throws URISyntaxException
    {
        if (!url.contains("file:///") && url.contains("file://")) return new URI(url.replace("file://", "file:///"));
        return new URI(url);
    }

    private static void list(ImmutableList.Builder<String> result, File file, FileFilter filter) {
        if (file.isFile() && filter.accept(file)) result.add(file.getAbsolutePath());
        else if (file.isDirectory()) {
            final String[] list = file.list();
            if (list != null) {
                for (final String f : list)
                    list(result, new File(file, f), filter);
            }
        }
    }  // end method list

    //~ Inner Interfaces .............................................................................................................................

    public interface IORunnable {
        /** Gets a result. */
        void run()
            throws IOException;
    }

    public interface IOSupplier<T> {
        /** Gets a result. */
        T get()
            throws IOException;
    }
}  // end class Files
