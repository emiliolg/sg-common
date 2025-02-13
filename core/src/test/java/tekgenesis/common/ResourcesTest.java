
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.function.Predicate;

import org.junit.Test;

import tekgenesis.common.collections.ImmutableCollection;
import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.core.Option;

import static org.assertj.core.api.Assertions.assertThat;

import static tekgenesis.common.core.Constants.PROPERTIES_EXT;
import static tekgenesis.common.core.Predicates.*;
import static tekgenesis.common.util.Files.readLines;
import static tekgenesis.common.util.Resources.*;

/**
 * User: emilio; Date: 12/16/11; Time: 12:08 PM;
 */

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber", "NonJREEmulationClassesInClientCode" })
public class ResourcesTest {

    //~ Methods ......................................................................................................................................

    @Test public void basicTests() {
        assertThat(readerForResource("notFound")).isEqualTo(Option.empty());
        assertThat(readResources("test/Test.txt").toString()).isEqualTo("(Line 1, Line 2)");

        assertThat(shaForResource("test/Test.txt")).isEqualTo("cc28f73f9dcff9c16cde11ed1e4b77e1c7a75e2aa9febb59d9e36dbb26ff3ae2");

        assertThat(getResources("test/Test.txt").get(0).toString()).endsWith("test/test/Test.txt");
    }

    @Test public void findResourceFromFileSystem() {
        assertThat(findResources("test", endsWith(".txt")).size()).isEqualTo(2);
        assertThat(findResources("test", endsWith(PROPERTIES_EXT)).size()).isEqualTo(1);
        assertThat(findResources("test", startsWith("Test.")).size()).isEqualTo(2);
        final Predicate<String> objectPredicate = o -> true;

        final Reader reader = readerFromUrl(findResources("test2", objectPredicate).getFirst().get()).get();
        assertThat(readLines(reader)).containsExactly("Line 1", "Line 2");
    }

    @Test public void findResourceFromJar()
        throws IOException
    {
        final ImmutableCollection<URL> r1 = findResources("META-INF/", "MANIFEST.MF"::equals);
        final ImmutableList<URL>       r2 = getResources("META-INF/MANIFEST.MF");
        assertThat(r2.containsAll(r1)).isTrue();

        final Option<URL> first = r1.getFirst();
        assertThat(first.isPresent()).isTrue();
        final URL url = first.get();

        // Just check that we can read the file from the jar. There was a bug that the url was malformed.
        final InputStream inputStream = url.openStream();
        inputStream.close();
    }
}
