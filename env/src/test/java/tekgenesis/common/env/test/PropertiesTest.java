
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.test;

import org.jetbrains.annotations.Nullable;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import tekgenesis.common.core.Option;
import tekgenesis.common.env.Environment;
import tekgenesis.common.env.Mutable;
import tekgenesis.common.env.Properties;
import tekgenesis.common.env.context.Context;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * I18n Test;
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class PropertiesTest {

    //~ Instance Fields ..............................................................................................................................

    Color lastColor = null;

    //~ Methods ......................................................................................................................................

    @Test public void bindTest() {
        final Environment env = Context.getEnvironment();

        final ExampleProps props = Context.getProperties("x", ExampleProps.class);
        assertThat(props.color).isEqualTo(Color.RED);

        env.put("x", "example", new ExampleProps(Color.BLUE));

        env.get("x", ExampleProps.class, this::colorListener);

        assertThat(props.color).isEqualTo(Color.RED);
        assertThat(lastColor).isEqualTo(null);

        env.put("x", "example", new ExampleProps(Color.GREEN));

        assertThat(props.color).isEqualTo(Color.RED);
        assertThat(lastColor).isEqualTo(Color.GREEN);
    }

    @Test public void referenceProps() {
        final Environment env = Context.getEnvironment();

        env.put("x", "example", new ExampleProps(Color.BLUE));

        final RefProps props = Context.getProperties(RefProps.class);
        assertThat(props.color).isEqualTo("Color: " + Color.BLUE);
    }

    @Nullable private Color colorListener(final Option<ExampleProps> value) {
        return lastColor = value.isPresent() ? value.get().color : null;
    }

    //~ Enums ........................................................................................................................................

    enum Color { RED, GREEN, BLUE }

    //~ Inner Classes ................................................................................................................................

    @Mutable static class ExampleProps implements Properties {
        Color   color = Color.RED;
        boolean flag  = true;

        ExampleProps() {}

        ExampleProps(Color color) {
            this.color = color;
        }
    }

    static class RefProps {
        ExampleProps props = Context.getProperties("x", ExampleProps.class);
        String       color = "Color: " + props.color;
    }
}  // end class PropertiesTest
