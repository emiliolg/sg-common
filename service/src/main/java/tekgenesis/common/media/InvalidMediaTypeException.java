
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.media;

/**
 * Exception thrown during {@link MediaType media type} construction.
 */
public class InvalidMediaTypeException extends RuntimeException {

    //~ Instance Fields ..............................................................................................................................

    private final String mime;

    //~ Constructors .................................................................................................................................

    InvalidMediaTypeException(String mime, String message) {
        super("Invalid mime type \"" + mime + "\": " + message);
        this.mime = mime;
    }

    //~ Methods ......................................................................................................................................

    /** Get exception mime. */
    public String getMime() {
        return mime;
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = -7194948191604050148L;
}
