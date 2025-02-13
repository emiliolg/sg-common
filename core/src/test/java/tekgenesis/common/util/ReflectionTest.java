
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.awt.*;
import java.io.File;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;

import static tekgenesis.common.Predefined.ensureNotNull;
import static tekgenesis.common.util.Reflection.*;

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber", "NonJREEmulationClassesInClientCode" })
public class ReflectionTest {

    //~ Methods ......................................................................................................................................

    @Test public void construction() {
        final File file = construct(File.class, "dir", "file");
        assertThat(file).hasParent("dir").hasName("file");

        final String str = construct(String.class);
        assertThat(str).isEmpty();

        try {
            construct(File.class, "dir", 20, 20);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Cannot create class java.io.File");
        }

        try {
            construct(File.class, "dir", 20.0);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Cannot create class java.io.File");
        }

        try {
            construct(X.class, "xx");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Cannot create class tekgenesis.common.util.ReflectionTest$X");
        }
    }

    @Test public void constructPrimitives() {
        assertThat(construct(Integer.class, 1)).isEqualTo(1);
        assertThat(construct(Long.class, (long) 1)).isEqualTo(1);
        assertThat(construct(Float.class, (float) 1)).isEqualTo(1);
        assertThat(construct(Short.class, new Short("1"))).isEqualTo((short) 1);
        assertThat(construct(Double.class, (double) 1)).isEqualTo(1);
        assertThat(construct(Character.class, 'a')).isEqualTo('a');
        assertThat(construct(String.class, "hello")).isEqualTo("hello");
        assertThat(construct(Boolean.class, true)).isTrue();

        final Long longVal = construct("java.lang.Long", (long) 1);
        assertThat(longVal).isEqualTo(1);

        assertThat(Reflection.<Integer>invokeStatic(Integer.class, "valueOf", "1")).isEqualTo(1);
        assertThat(Reflection.<Integer>invokeStatic(Integer.class, "valueOf", 1)).isEqualTo(1);

        try {
            construct(Short.class, 1);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Cannot create class java.lang.Short");
        }

        try {
            construct("java.lang.Short", 1);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Cannot create class java.lang.Short");
        }
    }

    @Test public void copyFields() {
        final Point p1 = new Point(1, 1);
        final Point p2 = new Point(0, 0);

        copyDeclaredFields(p1, p2);

        final Integer x = getFieldValue(p2, findFieldOrFail(Point.class, "x"));
        assertThat(x).isEqualTo(1);
        final Integer y = getFieldValue(p2, findFieldOrFail(Point.class, "y"));
        assertThat(y).isEqualTo(1);
    }

    @Test public void fields() {
        final Y y = new Y("privateVal");
        assertThat(ensureNotNull(getPrivateField(y, "privateField")).toString()).isEqualTo("privateVal");
        final Instance instance = Instance.create(Y.class, "val");
        instance.setPrivateField("privateField", "b");

        final Object b = instance.getPrivateField("privateField");
        assertThat(b).isNotNull();
        assertThat(b.toString()).isEqualTo("b");

        assertThat(Reflection.getFields(Y.class).size()).isEqualTo(1);

        final Instance z = Instance.create(Z.class);
        z.setPrivateField("privateField", "b");
        final Object b1 = z.getPrivateField("privateField");
        assertThat(b1).isNotNull();
        assertThat(b1.toString()).isEqualTo("b");

        // public;
        final Integer x = getPublicField(new Point(), "x");
        assertThat(x).isZero();
        final Instance point = Instance.create(Point.class);
        point.setPublicField("x", 1);
        assertThat((Integer) point.getPublicField("x")).isEqualTo(1);

        assertThat(findField(Point.class, "x").isPresent()).isTrue();

        assertThat(findMethod(Point.class, "move", Integer.TYPE, Integer.TYPE).isPresent()).isTrue();
        // errors;
        try {
            instance.setPublicField("doesntExist", "a");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Cannot find field tekgenesis.common.util.ReflectionTest$Y.doesntExist");
        }

        try {
            z.setPrivateField("doesntExist", "a");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Cannot find field tekgenesis.common.util.ReflectionTest$Z.doesntExist");
        }

        try {
            instance.getPublicField("doesntExist");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Cannot find field tekgenesis.common.util.ReflectionTest$Y.doesntExist");
        }
    }  // end method fields

    @Test public void instance() {
        try {
            Instance.create("does not exist");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Cannot create class does not exist");
        }
    }

    @Test public void invocation() {
        final File   file = new File("dir", "file");
        final Object obj  = invoke(file, "getPath");
        assertThat(obj).isNotNull();

        // noinspection ConstantConditions
        assertThat(obj.toString()).isEqualTo("dir/file");

        invoke(file, "compareTo", file);

        try {
            invoke(file, "compareTo", "pepe", "pepe");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Cannot invoke method java.io.File.compareTo");
        }

        try {
            invoke(file, "does not exist");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        }
        catch (final IllegalArgumentException e) {
            assertThat(e).hasMessage("Cannot invoke method java.io.File.does not exist");
        }
    }

    //~ Inner Classes ................................................................................................................................

    abstract static class X {
        public String s;

        X(String s) {
            this.s = s;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    static class Y {
        private final String privateField;

        Y(String privateField) {
            this.privateField = privateField;
        }
    }

    static class Z extends Y {
        Z() {
            super("val");
        }
    }
}  // end class ReflectionTest
