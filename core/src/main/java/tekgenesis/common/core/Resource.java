
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.io.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.annotation.GwtIncompatible;
import tekgenesis.common.collections.ImmutableCollection;
import tekgenesis.common.serializer.StreamWriter;

/**
 * Interface to a Resource Type Resources are Images or documents.
 */
public interface Resource extends Comparable<Resource>, Serializable {

    //~ Methods ......................................................................................................................................

    /** Add the large Entry. */
    Factory addLarge();

    /** Add the thumb Entry. */
    Factory addThumb();

    /** Add a variant. */
    Factory addVariant(String variant);
    @Override int compareTo(@NotNull Resource resource);

    /** Create a copy of this resource. */
    Resource copy();
    @Override boolean equals(Object obj);
    @Override int hashCode();

    /** Serialize the Resource in the specified Stream. */
    void serialize(StreamWriter w);
    @Override String toString();

    /**
     * Returns all contentMap for this resource. The MASTER resource is the only resource which has
     * contentMap
     */
    ImmutableCollection<Entry> getEntries();

    /** Returns a specified Entry. If variant is null or "" it will return null */
    @Nullable Entry getEntry(@Nullable String variant);

    /** Returns the Large Entry. */
    @Nullable Entry getLarge();

    /** Returns the Master Entry. */
    @NotNull Entry getMaster();

    /** Returns the Thumb Entry. */
    @Nullable Entry getThumb();

    /** Returns the resource uuid. */
    String getUuid();

    //~ Inner Interfaces .............................................................................................................................

    /**
     * A Resource Content.
     */
    interface Content {
        /** Copy the content to the specified Writer. Returns the size in characters. */
        @GwtIncompatible
        @SuppressWarnings("UnusedReturnValue")
        int copyTo(Writer writer);

        /** Copy the content to the specified Writer. Returns the size in bytes. */
        int copyTo(OutputStream outputStream);

        /** Returns mime type. */
        String getMimeType();
    }

    /**
     * A Resource Entry.
     */
    interface Entry extends Content, Serializable {
        /** Returns true if the Resource is external (We do not store the data). */
        boolean isExternal();

        /** Return Metadata. */
        @NotNull Metadata getMetadata();

        /** Return the Name of the Entry. */
        @NotNull String getName();

        /** Return the Sha of the Entry. */
        @NotNull String getSha();

        /** The Url of the entry. */
        @NotNull String getUrl();

        /** Return the name of the variant. */
        @NotNull String getVariant();
    }

    interface Factory {
        /** Create a resource based on the content of the file. */
        @GwtIncompatible Resource upload(@NotNull File file);
        /** Create a resource based on an URL. */
        Resource upload(@NotNull String name, @NotNull String url);

        /** Create a resource with the specified mime-type based on the specified InputStream. */
        @GwtIncompatible Resource upload(@NotNull String name, @NotNull String mimeType, @NotNull InputStream is);

        /** Create a resource with the specified mime-type based on the specified Reader. */
        @GwtIncompatible Resource upload(@NotNull String name, @NotNull String mimeType, @NotNull Reader reader);

        /** Create a resource based on sha. */
        Resource uploadFromSha(@NotNull String name, @NotNull String sha, @NotNull String mimeType);
    }
}  // end interface Resource
