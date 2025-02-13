
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import tekgenesis.common.collections.Colls;
import tekgenesis.common.collections.ImmutableList;

import static tekgenesis.common.core.Constants.JAVA_CLASS_EXT;
import static tekgenesis.common.core.Constants.UTF8;

/**
 * Some utilities to deal with Resources inside a Jar.
 */
@SuppressWarnings("WeakerAccess")
public class JarResources {

    //~ Constructors .................................................................................................................................

    private JarResources() {}

    //~ Methods ......................................................................................................................................

    /**
     * Find the URL for a directory inside the classpath using a class as a reference.
     *
     * @param   clazz  Any java class that lives in the same place as the resources you want.
     * @param   path   Should end with "/", but not start with one.
     *
     * @return  Just the name of each member item, not the full paths.
     */
    public static URL urlForDirectory(Class<?> clazz, String path) {
        final ClassLoader loader = clazz.getClassLoader();
        if (loader == null) return null;
        final URL url = loader.getResource(path);
        return url == null ? loader.getResource(clazz.getName().replace(".", "/") + JAVA_CLASS_EXT) : url;
    }

    /**
     * List directory contents for a resource folder. Not recursive. This is basically a brute-force
     * implementation. Works for regular files and also JARs.
     *
     * @param   clazz  Any java class that lives in the same place as the resources you want.
     * @param   path   Should end with "/", but not start with one.
     *
     * @return  Just the name of each member item, not the full paths.
     */
    static ImmutableList<String> listResources(Class<?> clazz, String path, FileFilter filter) {
        final URL dirURL = urlForDirectory(clazz, path);
        if (dirURL == null) return Colls.emptyList();

        final String protocol = dirURL.getProtocol();

        if ("file".equals(protocol)) return Files.list(Files.fromUrl(dirURL), filter);

        if (!"jar".equals(protocol)) throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);

        return ImmutableList.build(result -> {
            final Enumeration<JarEntry> entries = openJar(dirURL).entries();
            while (entries.hasMoreElements()) {
                final String name = entries.nextElement().getName();
                // filter according to the path
                if (name.startsWith(path) && filter.accept(new File(name))) result.add(name);
            }
        });
    }

    /** Open the jar for a given URL. */
    private static JarFile openJar(URL dirURL) {
        // strip out the JAR name
        final String path    = dirURL.getPath();
        final String jarPath = path.substring(5, path.indexOf("!"));
        try {
            return new JarFile(URLDecoder.decode(jarPath, UTF8));
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}  // end class JarResources
