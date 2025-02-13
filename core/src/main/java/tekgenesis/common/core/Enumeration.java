
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.util.Map;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.annotation.GwtIncompatible;
import tekgenesis.common.util.Message;
import tekgenesis.common.util.Reflection;

/**
 * This interface is implemented by Enum generated classes and enums.
 */
public interface Enumeration<T extends Enum<T>, K> extends RichComparable<T> {

    //~ Instance Fields ..............................................................................................................................

    @NonNls String MAP_METHOD = "map";

    //~ Methods ......................................................................................................................................

    /** Return the enum image path. */
    @Nullable default String imagePath() {
        return null;
    }

    /** Check that the enum is one of the specified values. */
    default boolean in(T e1, T e2) {
        return this == e1 || this == e2;
    }
    /** Check that the enum is one of the specified values. */
    default boolean in(T e1, T e2, T e3) {
        return this == e1 || this == e2 || this == e3;
    }
    /** Check that the enum is one of the specified values. */
    default boolean in(T e1, T e2, T e3, T e4) {
        return this == e1 || this == e2 || this == e3 || this == e4;
    }

    /**
     * Get a numeric value for this enum If the Key is an integer then the key will be returned If
     * not the ordinal.
     */
    int index();

    /** Get the id for this enum. */
    K key();

    /** Returns default label. */
    @NotNull default String label() {
        return name();
    }

    /** Returns label, formatted with arguments using {@link String#format(String, Object...)}. */
    @GwtIncompatible @NotNull
    @SuppressWarnings("NonJREEmulationClassesInClientCode")
    default String label(Object... args) {
        return String.format(label(), args);
    }

    /** Returns a message for this enumeration. */
    @GwtIncompatible default Message message() {
        return Message.create(this);
    }

    /** Returns a message for this enumeration with the instantiated arguments. */
    @GwtIncompatible default Message message(Object... args) {
        return Message.create(this, args);
    }

    /** Return the enum key. */
    String name();

    //~ Methods ......................................................................................................................................

    /** Return the set of values for any Enumeration. */
    @GwtIncompatible static <T extends Enum<T> & Enumeration<T, K>, K> Map<K, T> mapFor(Class<T> enumClass) {
        return Reflection.invokeStatic(enumClass, MAP_METHOD);
    }
}  // end interface Enumeration
