
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import org.junit.Test;

import tekgenesis.common.util.Diff;
import tekgenesis.common.util.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import static tekgenesis.common.collections.Colls.emptyIterable;
import static tekgenesis.common.collections.Colls.listOf;
import static tekgenesis.common.util.Diff.caseInsensitive;
import static tekgenesis.common.util.Diff.caseSensitive;
import static tekgenesis.common.util.Diff.ignoreAllSpace;
import static tekgenesis.common.util.Files.close;
import static tekgenesis.common.util.Files.readInput;
import static tekgenesis.common.util.Files.readLines;

/**
 * User: emilio; Date: 12/16/11; Time: 12:47 PM;
 */

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class FilesTest {

    //~ Methods ......................................................................................................................................

    @Test public void copyDirectory()
        throws IOException
    {
        final File baseOutputDir = new File("target/common/core/test-run");
        final File outputDir     = new File(baseOutputDir, "copyDir");
        Files.ensureDirExists(outputDir);

        final Iterable<String> empty = emptyIterable();
        Files.writeLines(new File(outputDir, "file1"), empty);
        Files.writeLines(new File(outputDir, "file2"), empty);
        Files.writeLines(new File(outputDir, "file3"), empty);

        final File subDir = new File(outputDir, "subDir");
        Files.ensureDirExists(subDir);
        Files.writeLines(new File(subDir, "files1"), empty);
        Files.writeLines(new File(subDir, "files2"), empty);

        final File copiedDir = new File(baseOutputDir, "copiedDir");
        Files.copyDirectory(outputDir, copiedDir);

        final File[] files = outputDir.listFiles();
        assertThat(copiedDir.listFiles()).hasSize(files == null ? 0 : files.length);
        final File[] files1 = subDir.listFiles();
        assertThat(new File(copiedDir, "subDir").listFiles()).hasSize(files1 == null ? 0 : files1.length);
    }

    @Test public void diffTest() {
        final StringReader r1 = new StringReader("Hello\nWorld\n");
        final StringReader r2 = new StringReader("Bye\nWorld\n");
        assertThat(Diff.caseSensitive().diff(r1, r2).toStrings()).containsExactly("Diff.Delta(0, c, 0, (Hello), (Bye))");

        final StringReader r3 = new StringReader("Hello\nWorld\n");
        final StringReader r4 = new StringReader("HELLO\nWORLD\n");
        assertThat(caseInsensitive().diff(r3, r4)).isEmpty();

        final StringReader r5 = new StringReader("Hello\nWorld\n");
        final StringReader r6 = new StringReader("Hello\n\n\nWorld\n");
        assertThat(caseSensitive().diff(r5, r6).toStrings()).containsExactly("Diff.Delta(1, a, 1, (), (, ))");

        final StringReader r7 = new StringReader("H e l l o\nW o r l d\n");
        final StringReader r8 = new StringReader("Hello\n\n\nWorld\n");
        assertThat(ignoreAllSpace().diff(r7, r8).toStrings()).isEmpty();
    }

    @Test public void extension() {
        assertThat(Files.extension("/a/x/y.c")).isEqualTo("c");
        assertThat(Files.extension("/a/x/y.c/x")).isEqualTo("");
        final File f = new File("xxx");
        assertThat(Files.extension(f)).isEqualTo("");
    }

    @Test public void listFiles()
        throws IOException
    {
        final File listDir1 = new File("target/common/core/test-run/listDir1");
        Files.ensureDirExists(listDir1);

        final File listDir2 = new File("target/common/core/test-run/listDir2");
        Files.ensureDirExists(listDir2);

        final Iterable<String> empty = emptyIterable();
        Files.writeLines(new File(listDir1, "file1"), empty);
        Files.writeLines(new File(listDir1, "file2"), empty);
        Files.writeLines(new File(listDir1, "file3"), empty);
        final File subDir = new File(listDir1, "subDir");
        Files.ensureDirExists(subDir);
        Files.writeLines(new File(subDir, "files1"), empty);
        Files.writeLines(new File(subDir, "files2"), empty);

        Files.writeLines(new File(listDir2, "file4"), empty);
        Files.writeLines(new File(listDir2, "file5"), empty);

        assertThat(Files.list(listOf(listDir1, listDir2), pathname -> true)).hasSize(7);
        assertThat(Files.list(listDir1, ".*file[13]")).hasSize(2);
    }

    @Test public void normalize() {
        assertThat(Files.normalize("/a/./x/../y.c")).isEqualTo("/a/y.c");
        assertThat(Files.normalize(new File("/a/./x/../y.c")).getPath()).isEqualTo("/a/y.c");
        assertThat(Files.normalize("a/./x/../y.c")).isEqualTo("a/y.c");
    }

    @Test public void readInputTest() {
        final StringReader r = new StringReader("Hello\nWorld\n");
        assertThat(readInput(r)).isEqualTo("Hello\nWorld");
        close(r);
    }

    @Test public void readLinesTest() {
        final StringReader r = new StringReader("Hello\nWorld\n");
        assertThat(readLines(r)).isEqualTo(listOf("Hello", "World"));
        close(r);
    }

    @Test public void testIORunnable() {
        final Files.IORunnable s = () -> { throw new IOException(); };
        try {
            s.run();
            failBecauseExceptionWasNotThrown(IOException.class);
        }
        catch (final IOException ignored) {}
    }

    @Test public void testIOSupplier() {
        final Files.IOSupplier<Object> s = () -> { throw new IOException(); };
        try {
            s.get();
            failBecauseExceptionWasNotThrown(IOException.class);
        }
        catch (final IOException ignored) {}
    }

    @Test public void toUrls() {
        final File file1 = new File("target/common/core/test-run/file1");
        final File file2 = new File("target/common/core/test-run/file2");

        final URL[] urls = Files.toURL(listOf(file1, file2));

        assertThat(urls).hasSize(2);

        for (final URL url : urls)
            assertThat(url.toString()).startsWith("file:/");
    }
}  // end class FilesTest
