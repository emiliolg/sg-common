
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * A Class that represents alternative values.
 */
public abstract class Either<A, B> {

    //~ Methods ......................................................................................................................................

    /** If the value is left invoke the specified consumer with the value, otherwise do nothing. */
    public abstract Either<A, B> ifLeft(Consumer<? super A> consumer);

    /** If the value is right invoke the specified consumer with the value, otherwise do nothing. */
    public abstract Either<A, B> ifRight(Consumer<? super B> consumer);

    /** Returns the Left element. */
    public abstract A getLeft();

    /** Returns the Right element. */
    public abstract B getRight();

    /** Returns true if the right value is defined. */
    public abstract boolean isRight();

    //~ Methods ......................................................................................................................................

    /** Create a left value. */
    public static <A, B> Either<A, B> left(A a) {
        return new Left<>(a);
    }
    /** Create a right value. */
    public static <A, B> Either<A, B> right(B b) {
        return new Right<>(b);
    }

    //~ Inner Classes ................................................................................................................................

    private static class Left<A, B> extends Either<A, B> {
        private final A leftValue;

        Left(A a) {
            leftValue = a;
        }

        @Override public Either<A, B> ifLeft(Consumer<? super A> consumer) {
            consumer.accept(leftValue);
            return this;
        }

        @Override public Either<A, B> ifRight(Consumer<? super B> consumer) {
            return this;
        }

        @Override public String toString() {
            return "left(" + leftValue + ")";
        }

        @Override public A getLeft() {
            return leftValue;
        }
        @Override public B getRight() {
            throw new NoSuchElementException("Either.right() called on Left");
        }

        @Override public boolean isRight() {
            return false;
        }
    }

    private static class Right<A, B> extends Either<A, B> {
        private final B rightValue;

        Right(B b) {
            rightValue = b;
        }

        @Override public Either<A, B> ifLeft(Consumer<? super A> consumer) {
            return this;
        }

        @Override public Either<A, B> ifRight(Consumer<? super B> consumer) {
            consumer.accept(rightValue);
            return this;
        }
        @Override public String toString() {
            return "right(" + rightValue + ")";
        }
        @Override public A getLeft() {
            throw new NoSuchElementException("Either.left() called on Right");
        }
        @Override public B getRight() {
            return rightValue;
        }

        @Override public boolean isRight() {
            return true;
        }
    }
}  // end class Either
