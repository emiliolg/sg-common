
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
import java.net.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.ImmutableCollection;
import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.core.Constants;
import tekgenesis.common.core.Option;
import tekgenesis.common.core.Resource;

import static java.lang.Thread.currentThread;

import static tekgenesis.common.Predefined.isEmpty;
import static tekgenesis.common.Predefined.notEmpty;
import static tekgenesis.common.collections.Colls.emptyList;
import static tekgenesis.common.collections.Colls.immutable;
import static tekgenesis.common.core.Constants.*;
import static tekgenesis.common.core.Option.some;

/**
 * Resource Loading utils.
 */
public class Resources {

    //~ Constructors .................................................................................................................................

    private Resources() {}

    //~ Methods ......................................................................................................................................

    /** Build resource URL for an entry. */
    public static String buildResourceUrl(Resource.Entry entry) {
        return RESOURCE_URL + "?sha=" + entry.getSha() + FILE_NAME_URL_PARAM + entry.getName();
    }

    /** Find all Resources filtering by the specified Predicate. */
    public static ImmutableCollection<URL> findResources(String rootDirPath, Predicate<String> filter) {
        return findResources(currentClassLoader(), rootDirPath, filter);
    }

    /** Find all Resources filtering by the specified Predicate. */
    @SuppressWarnings("WeakerAccess")
    public static ImmutableCollection<URL> findResources(ClassLoader classLoader, String rootDirPath, Predicate<String> filter) {
        final Set<URL> result = new LinkedHashSet<>();
        final String   r      = rootDirPath.startsWith("/") ? rootDirPath.substring(1) : rootDirPath;
        for (final URL root : getResources(classLoader, r))
            addResources(result, root, filter);

        return immutable(result);
    }  // end method findResources

    /** Builds a suggestion item image compatible path. */
    @NotNull public static String image(@NotNull String path) {
        return image(path, "");
    }

    /** Builds a suggestion item image compatible path. */
    @NotNull public static String image(@NotNull String path, @NotNull String text) {
        return "<img src='" + path + "' class='" + ITEM_IMAGE_CLASS + "' />" + text;
    }

    /**
     * Use resource to build the right img html piece of code needed to use thumbnails in Suggest
     * Boxes suggestion list. Uses notThumbPath path if there isn't a thumb variant.
     */
    @NotNull public static String image(@NotNull Resource r, @NotNull String notThumbPath) {
        String path = notThumbPath;

        final Resource.Entry thumb = r.getThumb();
        if (thumb != null) path = buildResourceUrl(thumb);

        return image(path);
    }

    /**
     * Use resource to build the right img html piece of code needed to use thumbnails in Suggest
     * Boxes suggestion list.
     */
    @NotNull public static String imagePath(@Nullable Resource r) {
        if (r == null) return "";

        final Resource.Entry thumb = r.getThumb();
        if (thumb != null) return buildResourceUrl(thumb);
        else if (r.getMaster().isExternal()) return r.getMaster().getUrl();
        else return "";
    }

    /** Return a Reader For a given resource. */
    public static Option<Reader> readerForResource(String resourceName) {
        final URL resource = currentClassLoader().getResource(resourceName);
        if (resource == null) return Option.empty();
        return readerFromUrl(resource);
    }

    /** Returns a Reader for the given URL resource. */
    public static Option<Reader> readerFromUrl(@NotNull URL url) {
        try {
            final Reader reader = new InputStreamReader(url.openStream(), Constants.UTF8);
            return some(reader);
        }
        catch (final IOException e) {
            return Option.empty();
        }
    }

    /** Read all the lines from resources with the specified name. */
    public static ImmutableList<String> readResources(String name) {
        return readResources(currentClassLoader(), name);
    }

    /** Read all the lines from resources with the specified name. */
    public static ImmutableList<String> readResources(ClassLoader classLoader, String name) {
        return ImmutableList.build(builder -> readResourcesAsMap(classLoader, name).values().forEach(builder::addAll));
    }

    /** Read all resourced with the specified name as a Map of URL -> lines. */
    @SuppressWarnings("WeakerAccess")
    public static Map<URL, List<String>> readResourcesAsMap(ClassLoader classLoader, String name) {
        final Map<URL, List<String>> result = new LinkedHashMap<>();
        for (final URL url : getResources(classLoader, name))
            result.put(url, readFromUrl(url));
        loadExtraResources(name, result);
        return result;
    }

    /** Return the sha for a given resource. */
    public static String shaForResource(String resourceName) {
        final URL resource = currentClassLoader().getResource(resourceName);
        return resource == null ? "" : shaForUrl(resource);
    }

    /** Return the sha for a given resource. */
    @SuppressWarnings("WeakerAccess")
    public static String shaForUrl(URL url) {  //
        return readerFromUrl(url).map(Sha::digestAsString).orElse("");
    }

    /** Builds the right suggestion item with describeBy and image. */
    public static String toSuggestionItem(String text, Option<String> image, String noImagePath) {
        return image                                                                         //
               .map(imagePath -> image(isEmpty(imagePath) ? noImagePath : imagePath, text))  //
               .orElse(text);
    }

    /** Return a List with all resources with the given name. */
    @SuppressWarnings("WeakerAccess")
    public static ImmutableList<URL> getResources(String name) {
        return getResources(currentClassLoader(), name);
    }

    /** Return a List with all resources with the given name. */
    @SuppressWarnings("WeakerAccess")
    public static ImmutableList<URL> getResources(ClassLoader classLoader, String name) {
        try {
            final ImmutableList.Builder<URL> result = ImmutableList.builder();
            final Enumeration<URL>           urls   = classLoader.getResources(name);
            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                result.add(url);
            }
            return result.build();
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void addJarResources(Set<URL> result, URL rootUrl, Predicate<String> filter)
        throws IOException
    {
        final URLConnection con = rootUrl.openConnection();

        if (con instanceof JarURLConnection) {
            // Should usually be the case for traditional JAR files.
            final JarURLConnection jarCon = (JarURLConnection) con;
            jarCon.setUseCaches(false);
            final JarEntry jarEntry = jarCon.getJarEntry();
            addJarResources(result, rootUrl, jarCon.getJarFile(), jarEntry == null ? "" : jarEntry.getName(), filter, false);
        }
        else {
            // No JarURLConnection resort to URL file parsing.
            final String urlFile        = rootUrl.getFile();
            final int    separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
            if (separatorIndex == -1) addJarResources(result, rootUrl, new JarFile(urlFile), "", filter, true);
            else
                addJarResources(result,
                    rootUrl,
                    getJarFile(urlFile.substring(0, separatorIndex)),
                    urlFile.substring(separatorIndex + JAR_URL_SEPARATOR.length()),
                    filter,
                    true);
        }
    }

    private static void addJarResources(Set<URL> result, URL rootUrl, JarFile jarFile, String rootPath, Predicate<String> filter, boolean closeJar)
        throws IOException
    {
        final String root = rootPath.isEmpty() || rootPath.endsWith("/") ? rootPath : rootPath + "/";
        try {
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final String entryPath = entries.nextElement().getName();
                if (entryPath.startsWith(root)) {
                    final String entryName = entryPath.substring(root.length());
                    if (filter.test(entryName)) result.add(new URL(rootUrl, entryName));
                }
            }
        }
        finally {
            if (closeJar) jarFile.close();
        }
    }

    private static void addMatchingFiles(Set<URL> result, URL root, Predicate<String> pattern)
        throws IOException
    {
        final File rootDir = new File(root.getFile());
        if (rootDir.exists() && rootDir.isDirectory() && rootDir.canRead()) addMatchingFiles(result, root, rootDir, pattern);
    }

    private static void addMatchingFiles(Set<URL> result, URL root, File rootDir, Predicate<String> pattern)
        throws MalformedURLException
    {
        final File[] dirContents = rootDir.listFiles();
        if (dirContents == null) return;

        final int prefixLen = root.getFile().length() + 1;
        for (final File content : dirContents) {
            if (content.isDirectory()) addMatchingFiles(result, root, content, pattern);
            else {
                final String relativePath = content.getPath().substring(prefixLen);
                if (pattern.test(relativePath)) result.add(content.toURI().toURL());
            }
        }
    }  // end method addMatchingFiles

    private static void addResources(Set<URL> result, URL rootUrl, Predicate<String> filter) {
        try {
            if (isJarURL(rootUrl)) addJarResources(result, rootUrl, filter);
            else addMatchingFiles(result, rootUrl, filter);
        }
        catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static ClassLoader currentClassLoader() {
        return currentThread().getContextClassLoader();
    }

    // Todo move this to Application properties and limit a little...
    // I guess it should be used only for seeds
    private static void loadExtraResources(String name, Map<URL, List<String>> result) {
        final String property = System.getProperty("suigen.extra.resources");
        if (property == null) return;

        for (final String s : property.split(":")) {
            final File resource = new File(s, name);
            if (resource.exists()) {
                try {
                    final URL          url   = resource.toURI().toURL();
                    final List<String> lines = readFromUrl(url);
                    result.put(url, lines);
                }
                catch (final MalformedURLException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
    }

    private static List<String> readFromUrl(URL url) {
        return readerFromUrl(url)  //
               .map(Files::readLines)  //
               .orElse(emptyList());
    }

    /** Resolve the given jar file URL into a JarFile object. */
    private static JarFile getJarFile(String jarFileUrl)
        throws IOException
    {
        String name = jarFileUrl;
        if (jarFileUrl.startsWith(FILE_URL_PREFIX)) {
            try {
                name = new URI(jarFileUrl.replace(" ", "%20")).getSchemeSpecificPart();
            }
            catch (final URISyntaxException ex) {
                // Fallback for URLs that are not valid URIs (should hardly ever happen).
                name = jarFileUrl.substring(FILE_URL_PREFIX.length());
            }
        }
        return new JarFile(name);
    }

    private static boolean isJarURL(URL resource) {
        final String proto = resource.getProtocol();
        return "jar".equals(proto) || "zip".equals(proto) || "wsjar".equals(proto);
    }

    //~ Static Fields ................................................................................................................................

    /** URL prefix for loading from the file system: "file:" */
    private static final String FILE_URL_PREFIX = "file:";
    /** Separator between JAR URL and file path within the JAR. */
    private static final String JAR_URL_SEPARATOR = "!/";

    // Using plain property and not ApplicationProps due to dependency.
    private static final String RESOURCE_URL = notEmpty(System.getProperty("application.resourceUrl"), RESOURCE_SERVLET_PATH);
}
