
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.media;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static tekgenesis.common.Predefined.notNull;
import static tekgenesis.common.core.Constants.CLASS;
import static tekgenesis.common.core.Constants.XHTML_EXT;
import static tekgenesis.common.media.Mime.*;

/**
 * Utility class to deal with {@link Mime mime types} and extensions.
 */
@SuppressWarnings({ "DuplicateStringLiteralInspection", "NonJREEmulationClassesInClientCode" })
public class Mimes {

    //~ Constructors .................................................................................................................................

    private Mimes() {}

    //~ Methods ......................................................................................................................................

    /** Returns true if given mime type represents a psd. */
    public static boolean isPSD(String name) {
        return name.contains(".psd");
    }

    /** Returns true if the mime-type is application/octet-stream. */
    public static boolean isUnresolved(@Nullable String mimeType) {
        return mimeType == null || APPLICATION_OCTET_STREAM.getMime().equals(mimeType);
    }

    /** Returns true if given mime type represents an image. */
    public static boolean isImage(String mimeType) {
        return mimeType.startsWith(Mime.IMAGE.getType() + "/");
    }

    /**
     * Returns the corresponding MIME type to the given URI. If no MIME type was found it returns
     * 'application/octet-stream' type.
     */
    public static String getMimeType(String uri) {
        final Mime mimeType = notNull(lookupMimeType(uri.substring(uri.lastIndexOf(".") + 1)), APPLICATION_OCTET_STREAM);
        return mimeType.getMime();
    }

    /** Returns true if the mime-type is a text one. */
    public static boolean isText(String mimeType) {
        return mimeType.startsWith(TEXT + "/");
    }

    private static void extensionMapping(@NotNull String extension, @NotNull Mime mime) {
        if (mapping.put(extension, mime) != null) throw new IllegalArgumentException("Duplicate extension mapping " + extension);
    }

    /** Simply returns MIME type or <code>null</code> if no type is found. */
    private static Mime lookupMimeType(String ext) {
        return mapping.get(ext.toLowerCase());
    }

    //~ Static Fields ................................................................................................................................

    private static final Map<String, Mime> mapping = new HashMap<>(1 << 8);

    static {
        extensionMapping("xul", APPLICATION_VND_MOZZILLA_XUL_XML);
        extensionMapping("json", APPLICATION_JSON);
        extensionMapping("ice", X_CONFERENCE_X_COOLTALK);
        extensionMapping("movie", VIDEO_X_SGI_MOVIE);
        extensionMapping("avi", VIDEO_X_MSVIDEO);
        extensionMapping("wmv", VIDEO_X_MS_WMV);
        extensionMapping("m4u", VIDEO_VND_MPEGURL);
        extensionMapping("mxu", VIDEO_VND_MPEGURL);
        extensionMapping("htc", TEXT_X_COMPONENT);
        extensionMapping("etx", TEXT_X_SETEXT);
        extensionMapping("wmls", TEXT_VND_WAP_WMLSCRIPT);
        extensionMapping("wml", TEXT_VND_WAP_XML);
        extensionMapping("tsv", TEXT_TAB_SEPARATED_VALUES);
        extensionMapping("sgm", TEXT_SGML);
        extensionMapping("sgml", TEXT_SGML);
        extensionMapping("css", TEXT_CSS);
        extensionMapping("less", TEXT_CSS);
        extensionMapping("ifb", TEXT_CALENDAR);
        extensionMapping("ics", TEXT_CALENDAR);
        extensionMapping("wrl", MODEL_VRLM);
        extensionMapping("vrlm", MODEL_VRLM);
        extensionMapping("silo", MODEL_MESH);
        extensionMapping("mesh", MODEL_MESH);
        extensionMapping("msh", MODEL_MESH);
        extensionMapping("iges", MODEL_IGES);
        extensionMapping("igs", MODEL_IGES);
        extensionMapping("rgb", IMAGE_X_RGB);
        extensionMapping("ppm", IMAGE_X_PORTABLE_PIXMAP);
        extensionMapping("pgm", IMAGE_X_PORTABLE_GRAYMAP);
        extensionMapping("pbm", IMAGE_X_PORTABLE_BITMAP);
        extensionMapping("pnm", IMAGE_X_PORTABLE_ANYMAP);
        extensionMapping("ico", IMAGE_X_ICON);
        extensionMapping("ras", IMAGE_X_CMU_RASTER);
        extensionMapping("wbmp", IMAGE_WAP_WBMP);
        extensionMapping("djv", IMAGE_VND_DJVU);
        extensionMapping("djvu", IMAGE_VND_DJVU);
        extensionMapping("svg", IMAGE_SVG_XML);
        extensionMapping("ief", IMAGE_IEF);
        extensionMapping("cgm", IMAGE_CGM);
        extensionMapping("bmp", IMAGE_BMP);
        extensionMapping("xyz", CHEMICAL_X_XYZ);
        extensionMapping("pdb", CHEMICAL_X_PDB);
        extensionMapping("ra", AUDIO_X_PN_REALAUDIO);
        extensionMapping("ram", AUDIO_X_PN_REALAUDIO);
        extensionMapping("m3u", AUDIO_X_MPEGURL);
        extensionMapping("aifc", AUDIO_X_AIFF);
        extensionMapping("aif", AUDIO_X_AIFF);
        extensionMapping("aiff", AUDIO_X_AIFF);
        extensionMapping("mp3", AUDIO_MPEG);
        extensionMapping("mp2", AUDIO_MPEG);
        extensionMapping("mp1", AUDIO_MPEG);
        extensionMapping("mpga", AUDIO_MPEG);
        extensionMapping("kar", AUDIO_MIDI);
        extensionMapping("mid", AUDIO_MIDI);
        extensionMapping("midi", AUDIO_MIDI);
        extensionMapping("dtd", APPLICATION_XML_DTD);
        extensionMapping("xsl", APPLICATION_XML);
        extensionMapping("xml", APPLICATION_XML);
        extensionMapping("xslt", APPLICATION_XSLT_XML);
        extensionMapping("xht", APPLICATION_XHTML_XML);
        extensionMapping(XHTML_EXT, APPLICATION_XHTML_XML);
        extensionMapping("src", APPLICATION_X_WAIS_SOURCE);
        extensionMapping("ustar", APPLICATION_X_USTAR);
        extensionMapping("ms", APPLICATION_X_TROFF_MS);
        extensionMapping("me", APPLICATION_X_TROFF_ME);
        extensionMapping("man", APPLICATION_X_TROFF_MAN);
        extensionMapping("roff", APPLICATION_X_TROFF);
        extensionMapping("tr", APPLICATION_X_TROFF);
        extensionMapping("t", APPLICATION_X_TROFF);
        extensionMapping("texi", APPLICATION_X_TEXINFO);
        extensionMapping("texinfo", APPLICATION_X_TEXINFO);
        extensionMapping("tex", APPLICATION_X_TEX);
        extensionMapping("tcl", APPLICATION_X_TCL);
        extensionMapping("sv4crc", APPLICATION_X_SV4CRC);
        extensionMapping("sv4cpio", APPLICATION_X_SV4CPIO);
        extensionMapping("sit", APPLICATION_X_STUFFIT);
        extensionMapping("swf", APPLICATION_X_SHOCKWAVE_FLASH);
        extensionMapping("shar", APPLICATION_X_SHAR);
        extensionMapping("sh", APPLICATION_X_SH);
        extensionMapping("cdf", APPLICATION_X_NETCDF);
        extensionMapping("nc", APPLICATION_X_NETCDF);
        extensionMapping("latex", APPLICATION_X_LATEX);
        extensionMapping("skm", APPLICATION_X_KOAN);
        extensionMapping("skt", APPLICATION_X_KOAN);
        extensionMapping("skd", APPLICATION_X_KOAN);
        extensionMapping("skp", APPLICATION_X_KOAN);
        extensionMapping("js", APPLICATION_JAVASCRIPT);
        extensionMapping("hdf", APPLICATION_X_HDF);
        extensionMapping("gtar", APPLICATION_X_GTAR);
        extensionMapping("spl", APPLICATION_X_FUTURESPLASH);
        extensionMapping("dvi", APPLICATION_X_DVI);
        extensionMapping("dxr", APPLICATION_X_DIRECTOR);
        extensionMapping("dir", APPLICATION_X_DIRECTOR);
        extensionMapping("dcr", APPLICATION_X_DIRECTOR);
        extensionMapping("csh", APPLICATION_X_CSH);
        extensionMapping("cpio", APPLICATION_X_CPIO);
        extensionMapping("pgn", APPLICATION_X_CHESS_PGN);
        extensionMapping("vcd", APPLICATION_X_CDLINK);
        extensionMapping("bcpio", APPLICATION_X_BCPIO);
        extensionMapping("rm", APPLICATION_VND_RNREALMEDIA);
        extensionMapping("ppt", APPLICATION_VND_MSPOWERPOINT);
        extensionMapping("mif", APPLICATION_VND_MIF);
        extensionMapping("grxml", APPLICATION_SRGS_XML);
        extensionMapping("gram", APPLICATION_SRGS);
        extensionMapping("smil", APPLICATION_RDF_SMIL);
        extensionMapping("smi", APPLICATION_RDF_SMIL);
        extensionMapping("rdf", APPLICATION_RDF_XML);
        extensionMapping("ogg", APPLICATION_X_OGG);
        extensionMapping("oda", APPLICATION_ODA);
        extensionMapping("dmg", APPLICATION_OCTET_STREAM);
        extensionMapping("lzh", APPLICATION_OCTET_STREAM);
        extensionMapping("so", APPLICATION_OCTET_STREAM);
        extensionMapping("lha", APPLICATION_OCTET_STREAM);
        extensionMapping("dms", APPLICATION_OCTET_STREAM);
        extensionMapping("bin", APPLICATION_OCTET_STREAM);
        extensionMapping("mathml", APPLICATION_MATHML_XML);
        extensionMapping("cpt", APPLICATION_MAC_COMPACTPRO);
        extensionMapping("hqx", APPLICATION_MAC_BINHEX40);
        extensionMapping("jnlp", APPLICATION_JNLP);
        extensionMapping("ez", APPLICATION_ANDREW_INSET);
        extensionMapping("txt", TEXT_PLAIN);
        extensionMapping("ini", TEXT_PLAIN);
        extensionMapping("c", TEXT_PLAIN);
        extensionMapping("h", TEXT_PLAIN);
        extensionMapping("cpp", TEXT_PLAIN);
        extensionMapping("cxx", TEXT_PLAIN);
        extensionMapping("cc", TEXT_PLAIN);
        extensionMapping("chh", TEXT_PLAIN);
        extensionMapping("java", TEXT_PLAIN);
        extensionMapping("bat", TEXT_PLAIN);
        extensionMapping("cmd", TEXT_PLAIN);
        extensionMapping("asc", TEXT_PLAIN);
        extensionMapping("csv", TEXT_CSV);
        extensionMapping("rtf", TEXT_RTF);
        extensionMapping("rtx", TEXT_RICHTEXT);
        extensionMapping("html", TEXT_HTML);
        extensionMapping("htm", TEXT_HTML);
        extensionMapping("zip", APPLICATION_ZIP);
        extensionMapping("rar", APPLICATION_X_RAR_COMPRESSED);
        extensionMapping("gzip", APPLICATION_X_GZIP);
        extensionMapping("gz", APPLICATION_X_GZIP);
        extensionMapping("tgz", APPLICATION_TGZ);
        extensionMapping("tar", APPLICATION_X_TAR);
        extensionMapping("gif", IMAGE_GIF);
        extensionMapping("jpeg", IMAGE_JPEG);
        extensionMapping("jpg", IMAGE_JPEG);
        extensionMapping("jpe", IMAGE_JPEG);
        extensionMapping("tiff", IMAGE_TIFF);
        extensionMapping("tif", IMAGE_TIFF);
        extensionMapping("png", IMAGE_PNG);
        extensionMapping("au", AUDIO_BASIC);
        extensionMapping("snd", AUDIO_BASIC);
        extensionMapping("wav", AUDIO_X_WAV);
        extensionMapping("mov", VIDEO_QUICKTIME);
        extensionMapping("qt", VIDEO_QUICKTIME);
        extensionMapping("mpeg", VIDEO_MPEG);
        extensionMapping("mpg", VIDEO_MPEG);
        extensionMapping("mpe", VIDEO_MPEG);
        extensionMapping("abs", VIDEO_MPEG);
        extensionMapping("doc", APPLICATION_MSWORD);
        extensionMapping("xls", APPLICATION_VND_MSEXCEL);
        extensionMapping("eps", APPLICATION_POSTSCRIPT);
        extensionMapping("ai", APPLICATION_POSTSCRIPT);
        extensionMapping("ps", APPLICATION_POSTSCRIPT);
        extensionMapping("pdf", APPLICATION_PDF);
        extensionMapping("exe", APPLICATION_OCTET_STREAM);
        extensionMapping("dll", APPLICATION_OCTET_STREAM);
        extensionMapping(CLASS, APPLICATION_OCTET_STREAM);
        extensionMapping("jar", APPLICATION_JAVA_ARCHIVE);
        extensionMapping("msi", APPLICATION_X_OLE_STORAGE);
    }

    public static final String  TEXT        = Mime.TEXT_PLAIN.getType();
    public static final String  APPLICATION = Mime.APPLICATION_OCTET_STREAM.getType();
    @NonNls static final String WILDCARD    = "*";
}  // end class Mimes
