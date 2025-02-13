
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.media;

import java.util.Map;
import java.util.TreeMap;

import org.jetbrains.annotations.NotNull;

import static tekgenesis.common.media.Mimes.WILDCARD;

/**
 * Represent a Mime Type.
 */
@SuppressWarnings("DuplicateStringLiteralInspection")
public enum Mime implements MimeType {

    //~ Enum constants ...............................................................................................................................

    APPLICATION_ANDREW_INSET("application", "andrew-inset"), APPLICATION_JSON("application", "json"), APPLICATION_ZIP("application", "zip"),
    APPLICATION_X_GZIP("application", "x-gzip"), APPLICATION_TGZ("application", "tgz"), APPLICATION_MSWORD("application", "msword"),
    APPLICATION_POSTSCRIPT("application", "postscript"), APPLICATION_PDF("application", "pdf"), APPLICATION_JNLP("application", "jnlp"),
    APPLICATION_MAC_BINHEX40("application", "mac-binhex40"), APPLICATION_MAC_COMPACTPRO("application", "mac-compactpro"),
    APPLICATION_MATHML_XML("application", "mathml+xml"), APPLICATION_OCTET_STREAM("application", "octet-stream"),
    APPLICATION_ODA("application", "oda"), APPLICATION_RDF_XML("application", "rdf+xml"), APPLICATION_JAVA_ARCHIVE("application", "java-archive"),
    APPLICATION_RDF_SMIL("application", "smil"), APPLICATION_SRGS("application", "srgs"), APPLICATION_SRGS_XML("application", "srgs+xml"),
    APPLICATION_VND_MIF("application", "vnd.mif"), APPLICATION_VND_MSEXCEL("application", "vnd.ms-excel"),
    APPLICATION_VND_MSPOWERPOINT("application", "vnd.ms-powerpoint"), APPLICATION_VND_RNREALMEDIA("application", "vnd.rn-realmedia"),
    APPLICATION_X_BCPIO("application", "x-bcpio"), APPLICATION_X_CDLINK("application", "x-cdlink"),
    APPLICATION_X_CHESS_PGN("application", "x-chess-pgn"), APPLICATION_X_CPIO("application", "x-cpio"), APPLICATION_X_CSH("application", "x-csh"),
    APPLICATION_X_DIRECTOR("application", "x-director"), APPLICATION_X_DVI("application", "x-dvi"),
    APPLICATION_X_FUTURESPLASH("application", "x-futuresplash"), APPLICATION_X_GTAR("application", "x-gtar"),
    APPLICATION_X_HDF("application", "x-hdf"), APPLICATION_JAVASCRIPT("application", "javascript"), APPLICATION_X_KOAN("application", "x-koan"),
    APPLICATION_X_LATEX("application", "x-latex"), APPLICATION_X_NETCDF("application", "x-netcdf"), APPLICATION_X_OGG("application", "x-ogg"),
    APPLICATION_X_SH("application", "x-sh"), APPLICATION_X_SHAR("application", "x-shar"),
    APPLICATION_X_SHOCKWAVE_FLASH("application", "x-shockwave-flash"), APPLICATION_X_STUFFIT("application", "x-stuffit"),
    APPLICATION_X_SV4CPIO("application", "x-sv4cpio"), APPLICATION_X_SV4CRC("application", "x-sv4crc"), APPLICATION_X_TAR("application", "x-tar"),
    APPLICATION_X_RAR_COMPRESSED("application", "x-rar-compressed"), APPLICATION_X_TCL("application", "x-tcl"),
    APPLICATION_X_TEX("application", "x-tex"), APPLICATION_X_TEXINFO("application", "x-texinfo"), APPLICATION_X_TROFF("application", "x-troff"),
    APPLICATION_X_TROFF_MAN("application", "x-troff-man"), APPLICATION_X_TROFF_ME("application", "x-troff-me"),
    APPLICATION_X_TROFF_MS("application", "x-troff-ms"), APPLICATION_X_USTAR("application", "x-ustar"),
    APPLICATION_X_WAIS_SOURCE("application", "x-wais-source"), APPLICATION_FORM_URLENCODED("application", "x-www-form-urlencoded"),
    APPLICATION_VND_MOZZILLA_XUL_XML("application", "vnd.mozilla.xul+xml"), APPLICATION_XHTML_XML("application", "xhtml+xml"),
    APPLICATION_XSLT_XML("application", "xslt+xml"), APPLICATION_XML("application", "xml"), APPLICATION_XML_DTD("application", "xml-dtd"),
    IMAGE_BMP("image", "bmp"), IMAGE_CGM("image", "cgm"), IMAGE_GIF("image", "gif"), IMAGE_IEF("image", "ief"), IMAGE_JPEG("image", "jpeg"),
    IMAGE_TIFF("image", "tiff"), IMAGE_PNG("image", "png"), IMAGE_SVG_XML("image", "svg+xml"), IMAGE_VND_DJVU("image", "vnd.djvu"),
    IMAGE_WAP_WBMP("image", "vnd.wap.wbmp"), IMAGE_X_CMU_RASTER("image", "x-cmu-raster"), IMAGE_X_ICON("image", "x-icon"),
    IMAGE_X_PORTABLE_ANYMAP("image", "x-portable-anymap"), IMAGE_X_PORTABLE_BITMAP("image", "x-portable-bitmap"),
    IMAGE_X_PORTABLE_GRAYMAP("image", "x-portable-graymap"), IMAGE_X_PORTABLE_PIXMAP("image", "x-portable-pixmap"), IMAGE_X_RGB("image", "x-rgb"),
    AUDIO_BASIC("audio", "basic"), AUDIO_MIDI("audio", "midi"), AUDIO_MPEG("audio", "mpeg"), AUDIO_X_AIFF("audio", "x-aiff"),
    AUDIO_X_MPEGURL("audio", "x-mpegurl"), AUDIO_X_PN_REALAUDIO("audio", "x-pn-realaudio"), AUDIO_X_WAV("audio", "x-wav"),
    CHEMICAL_X_PDB("chemical", "x-pdb"), CHEMICAL_X_XYZ("chemical", "x-xyz"), MODEL_IGES("model", "iges"), MODEL_MESH("model", "mesh"),
    MODEL_VRLM("model", "vrml"), TEXT_PLAIN("text", "plain"), TEXT_RICHTEXT("text", "richtext"), TEXT_CSV("text", "csv"), TEXT_RTF("text", "rtf"),
    TEXT_HTML("text", "html"), TEXT_CALENDAR("text", "calendar"), TEXT_CSS("text", "css"), TEXT_EVENT_STREAM("text", "event-stream"),
    TEXT_SGML("text", "sgml"), TEXT_XML("text", "xml"), TEXT_TAB_SEPARATED_VALUES("text", "tab-separated-values"),
    TEXT_VND_WAP_XML("text", "vnd.wap.wml"), TEXT_VND_WAP_WMLSCRIPT("text", "vnd.wap.wmlscript"), TEXT_X_SETEXT("text", "x-setext"),
    TEXT_X_COMPONENT("text", "x-component"), TEXT_X_GWT_RPC("text/x-gwt-rpc"), VIDEO_QUICKTIME("video", "quicktime"), VIDEO_MPEG("video", "mpeg"),
    VIDEO_VND_MPEGURL("video", "vnd.mpegurl"), VIDEO_X_MSVIDEO("video", "x-msvideo"), VIDEO_X_MS_WMV("video", "x-ms-wmv"),
    VIDEO_X_SGI_MOVIE("video", "x-sgi-movie"), X_CONFERENCE_X_COOLTALK("x-conference", "x-cooltalk"),
    APPLICATION_X_OLE_STORAGE("application", "x-ole-storage"), IMAGE("image"), VIDEO("video"), DOCUMENT("document"), ALL(WILDCARD);

    //~ Instance Fields ..............................................................................................................................

    private final String subtype;

    private final String type;

    //~ Constructors .................................................................................................................................

    Mime(String type) {
        this(type, WILDCARD);
    }

    Mime(String type, String subtype) {
        this.type    = type;
        this.subtype = subtype;
    }

    //~ Methods ......................................................................................................................................

    /** Returns mime type String. */
    public String getMime() {
        return type + "/" + subtype;
    }

    @NotNull @Override public String getSubtype() {
        return subtype;
    }

    @NotNull @Override public String getType() {
        return type;
    }

    //~ Methods ......................................................................................................................................

    /** Return the {@link Mime} for a mime string, or null (eg.: "text/plain" -> TEXT_PLAIN) */
    public static Mime fromMimeString(String mime) {
        return stringToMime.get(mime.toLowerCase());
    }

    //~ Static Fields ................................................................................................................................

    private static final Mime[]            VALUES       = values();
    private static final Map<String, Mime> stringToMime;

    static {
        stringToMime = new TreeMap<>();
        for (final Mime mime : VALUES)
            stringToMime.put(mime.getMime().toLowerCase(), mime);
    }
}
