
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.tools.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.collections.Seq;
import tekgenesis.common.core.Constants;
import tekgenesis.common.core.Option;
import tekgenesis.common.util.Diff;
import tekgenesis.common.util.Files;

import static java.lang.Math.min;
import static java.lang.String.format;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import static tekgenesis.common.core.Option.empty;
import static tekgenesis.common.core.Option.of;

/**
 * Test Utility Methods.
 */
public class Tests {

    //~ Constructors .................................................................................................................................

    private Tests() {}

    //~ Methods ......................................................................................................................................

    /** Assert against a specified golden File. */
    public static boolean assertEquals(File outFile, File goldenFile) {
        try {
            return Diff.trimming().diff(new FileReader(outFile), new FileReader(goldenFile)).isEmpty();
        }
        catch (final FileNotFoundException ignored) {}
        return false;
    }

    /** Assert that the field is not null and return it. */
    @NotNull public static <T> T assertNotNull(@Nullable T e) {
        assertThat(e).isNotNull();
        return e;
    }

    /** Check against a specified golden File and fail. */
    public static void checkDiff(File outFile, File goldenFile) {
        final Option<String> diff = diff(outFile, goldenFile);
        if (diff.isPresent()) fail(diff.get());
    }

    /** Check against a specified golden File. */
    public static void checkDiff(File a, Reader b) {
        try {
            checkDiff(new FileReader(a), a.toString(), b, "");
        }
        catch (final FileNotFoundException e) {
            fail("Cannot Open File: " + a);
        }
    }

    /** Diff between 2 strings. */
    public static void checkDiff(String val1, String val2) {
        final List<Diff.Delta<String>> diffs = Diff.trimming().diff(val1, val2);
        if (!diffs.isEmpty()) fail("\n" + Diff.makeString(diffs));
    }

    /** Check against a specified golden File. */
    @SuppressWarnings("DuplicateStringLiteralInspection")
    public static void checkDiff(File outFile, File goldenFile, Diff.Equals<String> comparator) {
        final List<Diff.Delta<String>> diffs = Diff.stringDiffer(comparator).diff(outFile, goldenFile);
        if (!diffs.isEmpty()) fail("\ndiff -y -W 150 " + outFile + " " + goldenFile + "\n" + Diff.makeString(diffs));
    }

    /** Return optional diff with specified golden file. */
    public static Option<String> diff(File outFile, File goldenFile) {
        if (!goldenFile.exists())
            fail(format("Cannot Open Golden File: \n%s\nfor comparing \n%s", goldenFile.getAbsolutePath(), outFile.getAbsolutePath()));
        try {
            return diff(new FileReader(outFile), outFile.toString(), new FileReader(goldenFile), goldenFile.toString());
        }
        catch (final FileNotFoundException e) {
            fail("Cannot Open Target or Golden File: " + e.getMessage());
        }
        return empty();
    }

    /** Create a Golden Test Helper class. */
    public static GoldenTest goldenCreate(File testFile, String outputDir) {
        return new GoldenTest(testFile, outputDir);
    }

    /** Create a Golden Test Helper class. */
    public static GoldenTest goldenCreate(String testName, String outputDir, String goldenDir) {
        return new GoldenTest(testName, outputDir, goldenDir);
    }

    /** Get the files in the specified directory as parameters. */
    public static Seq<Object[]> listFiles(@NotNull String dirName, @NotNull String pattern) {
        return listFiles(new File(dirName), pattern);
    }
    /** Get the files in the specified directory as parameters. */
    public static Seq<Object[]> listFiles(@NotNull File dir, @NotNull String pattern) {
        return Files.list(dir, pattern).map(path -> new Object[] { new File(path) });
    }

    /** Return a random string with the specified length. */
    public static String randomString(int length) {
        final Random        r      = new Random();
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++)
            result.append((char) ('a' + r.nextInt('z' - 'a')));
        return result.toString();
    }

    /** Sleep during the specified number of milliseconds. */
    public static void sleep(final int millis) {
        System.out.printf("Sleeping %d ms....", millis);
        try {
            Thread.sleep(millis);
        }
        catch (final InterruptedException ignore) {}
        System.out.println("\r ");
    }

    /** Wrap single parameter as a List of objects (To be used by @Params in junit. */
    public static Seq<Object[]> wrapForParameters(@Nullable Object[] args) {
        return wrapForParameters(ImmutableList.fromArray(args));
    }

    /** Wrap single parameter as a List of objects (To be used by @Params in junit. */
    public static Seq<Object[]> wrapForParameters(ImmutableList<?> args) {
        return args.map(arg -> new Object[] { arg }).toList();
    }

    /** Returns the hostname. */
    @SuppressWarnings("WeakerAccess")
    public static String getHostName() {
        try {
            final String host = InetAddress.getLocalHost().getHostName().replaceAll("-", "");
            return host.substring(0, min(index(host, '.'), min(index(host, '_'), index(host, '-'))));
        }
        catch (final UnknownHostException e) {
            return Constants.LOCALHOST;
        }
    }

    private static void checkDiff(final Reader a, final String aName, final Reader b, final String bName) {
        final List<Diff.Delta<String>> diffs = Diff.ignoreAllSpace().diff(a, b);
        if (!diffs.isEmpty()) fail("\ndiff -y -W 150 " + aName + " " + bName + "\n" + Diff.makeString(diffs));
    }

    private static Option<String> diff(final Reader a, final String aName, final Reader b, final String bName) {
        final List<Diff.Delta<String>> diffs = Diff.ignoreAllSpace().diff(a, b);
        return diffs.isEmpty() ? empty() : of("\ndiff -y -W 150 " + aName + " " + bName + "\n" + Diff.makeString(diffs));
    }
    private static int index(String s, Character chr) {
        final int n = s.indexOf(chr);
        return n <= 0 ? s.length() : n;
    }

    //~ Inner Classes ................................................................................................................................

    public static class GoldenTest {
        private final File goldenFile;

        private final File outputFile;

        private GoldenTest(File testFile, String outputDirName) {
            final String name = testFile.getName();
            outputFile = createOutputFile(outputDirName, name);
            final int    dot      = name.lastIndexOf('.');
            final String bareName = dot == -1 ? name : name.substring(0, dot);
            goldenFile = new File(testFile.getParent(), bareName + ".golden");
        }

        private GoldenTest(String testName, String outputDirName, String goldenDirName) {
            outputFile = createOutputFile(outputDirName, testName);
            goldenFile = new File(goldenDirName, testName);
        }

        /** Check the output against the golden file. */
        public void check() {
            checkDiff(outputFile, goldenFile);
        }

        /** Returns the Golden File. */
        public File getGoldenFile() {
            return goldenFile;
        }

        /** Returns the Output File. */
        public File getOutputFile() {
            return outputFile;
        }

        private File createOutputFile(String outputDirName, String name) {
            final File outputDir = new File(outputDirName);
            outputDir.mkdirs();
            return new File(outputDir, name);
        }
    }
}  // end class Tests
