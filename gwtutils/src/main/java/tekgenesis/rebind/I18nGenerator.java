
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.rebind;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import tekgenesis.common.core.Instantiator;
import tekgenesis.common.env.i18n.I18nMessages;

/**
 * GWT Generator implementation that discovers all classes that will be instantiated with
 * I18nMessages during compile time and generates a factory class that will return instances of each
 * class.
 */
@SuppressWarnings("WeakerAccess")
public class I18nGenerator extends Generator {

    //~ Methods ......................................................................................................................................

    public String generate(TreeLogger logger, GeneratorContext context, String requestedClass)
        throws UnableToCompleteException
    {
        final TypeOracle oracle = context.getTypeOracle();

        final JClassType instantiableType = oracle.findType(I18nMessages.class.getName());

        final List<JClassType> clazzes = new ArrayList<>();

        for (final JClassType classType : oracle.getTypes())
            if (!classType.equals(instantiableType) && classType.isAssignableTo(instantiableType)) clazzes.add(classType);

        final String genPackageName = "tekgenesis.common.i18n";
        final String genClassName   = "I18nMessagesImpl";

        final ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(genPackageName, genClassName);
        composer.addImplementedInterface(Instantiator.class.getCanonicalName());
        composer.addImport("com.google.gwt.core.client.GWT");

        final PrintWriter printWriter = context.tryCreate(logger, genPackageName, genClassName);

        if (printWriter != null) {
            final SourceWriter sourceWriter = composer.createSourceWriter(context, printWriter);
            sourceWriter.println("I18nMessagesImpl() { }");

            printFactoryMethod(clazzes, sourceWriter);

            sourceWriter.commit(logger);
        }

        return composer.getCreatedClassName();
    }

    private void printFactoryMethod(List<JClassType> clazzes, SourceWriter sourceWriter) {
        sourceWriter.println();

        sourceWriter.println("public <T> T create(String className) {");

        for (final JClassType classType : clazzes) {
            sourceWriter.println();
            sourceWriter.indent();
            sourceWriter.println("if (className.endsWith(\"." + classType.getName() + "\")) {");
            sourceWriter.indent();
            sourceWriter.println("return GWT.create(" + classType.getQualifiedSourceName() + ".class);");
            sourceWriter.outdent();
            sourceWriter.println("}");
            sourceWriter.outdent();
            sourceWriter.println();
        }

        sourceWriter.indent();
        sourceWriter.println("return (T) null;");
        sourceWriter.outdent();
        sourceWriter.println();
        sourceWriter.println("}");
        sourceWriter.outdent();
        sourceWriter.println();
    }
}  // end class I18nGenerator
