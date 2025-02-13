
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import org.junit.Test;

import tekgenesis.common.exception.ApplicationException;
import tekgenesis.common.util.Message;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ThrowableInstanceNeverThrown")
public class MessageTest {

    //~ Methods ......................................................................................................................................

    @Test public void testApplicationException()
        throws Exception
    {
        final Message              noParams  = Message.create(TestMessages.NO_PARAMS);
        final ApplicationException exception = new ApplicationException(noParams);
        assertThat(exception.getEnumeration()).isEqualTo(TestMessages.NO_PARAMS);
        assertThat(exception.getArguments()).isEmpty();
        assertThat(exception.getMessage()).isEqualTo(TestMessages.NO_PARAMS.label());

        final Message              oneParam   = Message.create(TestMessages.ONE_PARAM, "value");
        final ApplicationException exception1 = new ApplicationException(oneParam);
        assertThat(exception1.getEnumeration()).isEqualTo(TestMessages.ONE_PARAM);
        assertThat(exception1.getArguments().get()).isEqualTo(new Object[] { "value" });
        assertThat(exception1.getMessage()).isEqualTo(TestMessages.ONE_PARAM.label("value"));

        final Message              twoParam   = Message.create(TestMessages.TWO_PARAMS, "value", "other");
        final ApplicationException exception2 = new ApplicationException(twoParam);
        assertThat(exception2.getEnumeration()).isEqualTo(TestMessages.TWO_PARAMS);
        assertThat(exception2.getArguments().get()).isEqualTo(new Object[] { "value", "other" });
        assertThat(exception2.getMessage()).isEqualTo(TestMessages.TWO_PARAMS.label("value", "other"));
    }
    @Test public void testBasic()
        throws Exception
    {
        final Message noParams = Message.create(TestMessages.NO_PARAMS);
        assertThat(noParams.label()).isEqualTo("No Parameters");

        final Message oneParam = Message.create(TestMessages.ONE_PARAM, "value");
        assertThat(oneParam.label()).isEqualTo("One Parameter value").isEqualTo(TestMessages.ONE_PARAM.label("value"));
        assertThat(oneParam.getArguments().get()).hasSize(1).contains("value");

        final Message twoParams = Message.create(TestMessages.TWO_PARAMS, "value", "other");
        assertThat(twoParams.label()).isEqualTo("Two Parameters value other").isEqualTo(TestMessages.TWO_PARAMS.label("value", "other"));
        assertThat(twoParams.getArguments().get()).hasSize(2).contains("value", "other");

        final Message twoParamsInt = Message.create(TestMessages.TWO_PARAMS_INT, "value", 10);
        assertThat(twoParamsInt.label()).isEqualTo("Two Parameters One INT value 10");
        assertThat(twoParamsInt.getArguments().get()).hasSize(2).contains("value", 10);

        final Message twoParamsBool = Message.create(TestMessages.TWO_PARAMS_BOOL, "value", true);
        assertThat(twoParamsBool.label()).isEqualTo("Two Parameters One BOOL value true");
        assertThat(twoParamsBool.getArguments().get()).hasSize(2).contains("value", true);

        final Message threeParams = Message.create(TestMessages.THREE_PARAMS, "value", "other", "more");
        assertThat(threeParams.label()).isEqualTo("Three Parameters value other more");
        assertThat(threeParams.getArguments().get()).hasSize(3).contains("value", "other", "more");

        final Object[] params       = { null };
        final Message  oneParamNull = Message.create(TestMessages.ONE_PARAM, params);
        assertThat(oneParamNull.label()).isEqualTo("One Parameter null").isEqualTo(TestMessages.ONE_PARAM.label("null"));
    }

    @Test public void testEnum()
        throws Exception
    {
        final Message noParams = TestMessages.NO_PARAMS.message();
        assertThat(noParams.label()).isEqualTo("No Parameters");

        final Message oneParam = TestMessages.ONE_PARAM.message("value");
        assertThat(oneParam.label()).isEqualTo("One Parameter value").isEqualTo(TestMessages.ONE_PARAM.label("value"));

        final Message twoParams = TestMessages.TWO_PARAMS.message("value", "other");
        assertThat(twoParams.label()).isEqualTo("Two Parameters value other").isEqualTo(TestMessages.TWO_PARAMS.label("value", "other"));

        final Message twoParamsInt = TestMessages.TWO_PARAMS_INT.message("value", 10);
        assertThat(twoParamsInt.label()).isEqualTo("Two Parameters One INT value 10");

        final Message twoParamsBool = TestMessages.TWO_PARAMS_BOOL.message("value", true);
        assertThat(twoParamsBool.label()).isEqualTo("Two Parameters One BOOL value true");
        assertThat(twoParamsBool.label()).isEqualTo("Two Parameters One BOOL value true");

        final Message threeParams = TestMessages.THREE_PARAMS.message("value", "other", "more");
        assertThat(threeParams.label()).isEqualTo("Three Parameters value other more");

        final Object[] params       = { null };
        final Message  oneParamNull = TestMessages.ONE_PARAM.message(params);
        assertThat(oneParamNull.label()).isEqualTo("One Parameter null").isEqualTo(TestMessages.ONE_PARAM.label("null"));
    }
}  // end class MessageTest
