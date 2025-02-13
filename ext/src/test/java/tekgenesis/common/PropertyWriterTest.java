
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import tekgenesis.common.io.PropertyWriter;

import static org.assertj.core.api.Assertions.assertThat;

import static tekgenesis.common.collections.Maps.linkedHashMap;
import static tekgenesis.common.core.Tuple.tuple;

/**
 * User: emilio; Date: 12/16/11; Time: 12:47 PM;
 */

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber", "SpellCheckingInspection" })
public class PropertyWriterTest {

    //~ Instance Fields ..............................................................................................................................

    final File outputDir = new File("target/common/ext/test-output");

    //~ Methods ......................................................................................................................................

    @Test public void writeProperties()
        throws IOException
    {
        final Map<String, String> map =  //
                                        linkedHashMap(
                tuple("NECESITO",
                    "Necesito alguien que me emparche un poco\n" +
                    "y que limpie mi cabeza.\n" +
                    "Que cocine guisos de madre, postres de abuela;\n" +
                    "y torres de caramelo.\n" +
                    "que me quiera cuando estoy, cuando me voy,\n" +
                    "cuando me fui,\n" +
                    "Y que sepa servir el te, besarme depués\n" +
                    "y echar a reír."),
                tuple("RUSSIAN_ANTHEM",
                    "Россия – священная наша держава,\n" +
                    "Россия – любимая наша страна.\n" +
                    "Могучая воля, великая слава –\n" +
                    "Твоё достоянье на все времена!"),
                tuple("SPECIAL_CHARS", "Un enter \r , un tab \t un f \f"));

        final File outFile = new File(outputDir, "Test.properties");

        final PropertyWriter writer = new PropertyWriter(outFile);
        writer.writeComment();
        writer.writeComment("Songs");
        writer.writeComment();
        for (final Map.Entry<String, String> e : map.entrySet())
            writer.writeProperty(e.getKey(), e.getValue());
        writer.close();

        final Properties props = new Properties();
        props.load(new FileInputStream(outFile));

        for (final Map.Entry<String, String> e : map.entrySet())
            assertThat(props.getProperty(e.getKey())).isEqualTo(e.getValue());
    }
}  // end class PropertyWriterTest
