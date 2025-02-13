
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static tekgenesis.common.Predefined.equal;
import static tekgenesis.common.Predefined.option;

/**
 * Provides mutable access to a value. Typical uses are:
 *
 * <ul>
 *   <li>To enable a primitive or string to be passed to a method and allow that method to change
 *     the value.</li>
 *   <li>To modify a local value from inside a lambda expression</li>
 *   <li>To modify a value store in a collection</li>
 * </ul>
 */
public abstract class Mutable<T> implements Serializable {

    //~ Methods ......................................................................................................................................

    @Override public boolean equals(java.lang.Object obj) {
        return this == obj || obj instanceof Mutable && equal(getValue(), ((Mutable<?>) obj).getValue());
    }

    @Override public int hashCode() {
        final T value = getValue();
        return value == null ? 0 : value.hashCode();
    }

    /**  */
    public Option<T> toOption() {
        return option(getValue());
    }

    @Override public String toString() {
        return String.valueOf(getValue());
    }

    /** Gets the value of this mutable. */
    @Nullable public abstract T getValue();

    /** Sets the value of this mutable. */
    public abstract void setValue(T value);

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 3733753490451669833L;

    //~ Inner Classes ................................................................................................................................

    public static class Boolean extends Mutable<java.lang.Boolean> {
        private boolean value;

        /** Create mutable Boolean. */
        public Boolean() {
            value = false;
        }

        /** Create mutable Boolean with given value. */
        public Boolean(boolean value) {
            this.value = value;
        }

        /** Negate the value. */
        public boolean negate() {
            return value = !value;
        }
        /** Return the value as a native boolean. */
        public boolean value() {
            return value;
        }
        /** Return the value as a native int. */
        @NotNull @Override public java.lang.Boolean getValue() {
            return value;
        }
        @Override public void setValue(java.lang.Boolean value) {
            setValue(value == null ? false : value);
        }
        /** Set the value from a native int. */
        public void setValue(boolean value) {
            this.value = value;
        }

        private static final long serialVersionUID = -7380833248293834218L;
    }

    public static class Double extends Mutable<java.lang.Double> {
        private double value;

        /** Create mutable Double. */
        public Double() {
            value = 0;
        }

        /** Create mutable Double with value. */
        public Double(double value) {
            this.value = value;
        }

        /** Add the specified number to the value. */
        public double add(double b) {
            return value += b;
        }
        /** Decrement one from the value. */
        public double decrement() {
            return ++value;
        }
        /** Increment one from the value. */
        public double increment() {
            return --value;
        }

        /** Substract the specified number to the value. */
        public double sub(double b) {
            return value -= b;
        }
        /** Return the value as a native double. */
        public double value() {
            return value;
        }
        /** Decrement one from the value and return the previous value. */
        public double getAndDecrement() {
            return value--;
        }
        /** Increment one from the value and return the previous value. */
        public double getAndIncrement() {
            return value++;
        }
        @NotNull @Override public java.lang.Double getValue() {
            return value;
        }
        @Override public void setValue(java.lang.Double value) {
            setValue(value == null ? 0 : value);
        }
        /** Set the value from a native double. */
        public void setValue(double value) {
            this.value = value;
        }

        private static final long serialVersionUID = 960039067015754129L;
    }  // end class Double

    public static class Int extends Mutable<Integer> {
        private int value;

        /** Create mutable Int. */
        public Int() {
            value = 0;
        }

        /** Create mutable Int with value. */
        public Int(int value) {
            this.value = value;
        }

        /** Add the specified number to the value. */
        public int add(int b) {
            return value += b;
        }
        /** Decrement one from the value. */
        public int decrement() {
            return --value;
        }
        /** Increment one from the value. */
        public int increment() {
            return ++value;
        }
        /** Substract the specified number to the value. */
        public int sub(int b) {
            return value -= b;
        }
        /** Return the value as a native int. */
        public int value() {
            return value;
        }
        /** Decrement one from the value and return the previous value. */
        public int getAndDecrement() {
            return value--;
        }
        /** Increment one from the value and return the previous value. */
        public int getAndIncrement() {
            return value++;
        }
        @NotNull @Override public Integer getValue() {
            return value;
        }
        @Override public void setValue(Integer value) {
            setValue(value == null ? 0 : value);
        }
        /** Set the value from a native int. */
        public void setValue(int value) {
            this.value = value;
        }

        private static final long serialVersionUID = -2245228842341212071L;
    }  // end class Int

    public static class Long extends Mutable<java.lang.Long> {
        private long value;

        /** Create mutable Long. */
        public Long() {
            value = 0;
        }

        /** Create mutable Long with value. */
        public Long(long value) {
            this.value = value;
        }

        /** Add the specified number to the value. */
        public long add(long b) {
            return value += b;
        }
        /** Decrement one from the value. */
        public long decrement() {
            return --value;
        }
        /** Increment one from the value. */
        public long increment() {
            return ++value;
        }
        /** Substract the specified number to the value. */
        public long sub(long b) {
            return value -= b;
        }
        /** Return the value as a native long. */
        public long value() {
            return value;
        }
        /** Decrement one from the value and return the previous value. */
        public long getAndDecrement() {
            return value--;
        }
        /** Increment one from the value and return the previous value. */
        public long getAndIncrement() {
            return value++;
        }
        @NotNull @Override public java.lang.Long getValue() {
            return value;
        }
        @Override public void setValue(java.lang.Long value) {
            setValue(value == null ? 0 : value);
        }
        /** Set the value from a native long. */
        public void setValue(long value) {
            this.value = value;
        }

        private static final long serialVersionUID = 5351004539540279708L;
    }  // end class Long

    public static class Object<T> extends Mutable<T> {
        private T value;

        /** Create mutable Object. */
        public Object() {
            value = null;
        }
        /** Create mutable Object with value. */
        public Object(@Nullable T value) {
            this.value = value;
        }

        @Override public T getValue() {
            return value;
        }
        @Override public void setValue(T value) {
            this.value = value;
        }

        private static final long serialVersionUID = 8332817781617908928L;
    }
}  // end class Mutable
