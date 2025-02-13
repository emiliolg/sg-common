
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.io.Serializable;

/**
 * Metadata info for resources.
 */
public class Metadata implements Serializable {

    //~ Instance Fields ..............................................................................................................................

    private double height = -1;
    private int    size   = -1;
    private double width  = -1;

    //~ Constructors .................................................................................................................................

    protected Metadata() {}

    //~ Methods ......................................................................................................................................

    /** Sets Width and Height. */
    public Metadata withDimension(double w, double h) {
        width  = w;
        height = h;

        return this;
    }

    /** Sets image size in bytes. */
    public Metadata withSize(int s) {
        size = s;

        return this;
    }

    /** Return height. */
    public double getHeight() {
        return height;
    }

    /** Return size in bytes. */
    public int getSize() {
        return size;
    }

    /** Return width. */
    public double getWidth() {
        return width;
    }

    //~ Methods ......................................................................................................................................

    /** Create metadata. */
    public static Metadata create() {
        return new Metadata();
    }

    /** Empty MetaData instance. */
    public static Metadata empty() {
        return EMPTY_INSTANCE;
    }

    //~ Static Fields ................................................................................................................................

    private static final long     serialVersionUID = 8792907616095271870L;
    private static final Metadata EMPTY_INSTANCE   = new Metadata();
}  // end class Metadata
