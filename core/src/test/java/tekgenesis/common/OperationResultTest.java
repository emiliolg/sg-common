
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

import tekgenesis.common.core.OperationResult;
import tekgenesis.common.exception.ApplicationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class OperationResultTest {

    //~ Methods ......................................................................................................................................

    @Test public void testBasic()
        throws Exception
    {
        final OperationResult<Object> result1 = OperationResult.none();
        assertThat(result1.isValid()).isTrue();
        assertThat(result1.getOrNull()).isNull();
        result1.addError(TestMessages.NO_PARAMS);
        assertThat(result1.isValid()).isFalse();
        assertThat(result1.isNotValid()).isTrue();
        assertThat(result1.getErrors()).isNotEmpty().hasSize(1);

        final OperationResult<String> result2 = OperationResult.some("value");
        assertThat(result2.isValid()).isTrue();
        assertThat(result2.get()).isEqualTo("value");
        assertThat(result2.getOrNull()).isEqualTo("value");
        result2.addWarning(TestMessages.NO_PARAMS);
        assertThat(result2.getWarningsAsString()).contains(TestMessages.NO_PARAMS.label());
        assertThat(result2.isValid()).isTrue();
        assertThat(result2.get()).isEqualTo("value");
        result2.addError(TestMessages.ONE_PARAM, "ok");
        assertThat(result2.isValid()).isFalse();
        assertThat(result2.getErrors()).hasSize(1);
        assertThat(result2.getErrorsAsString()).contains(TestMessages.ONE_PARAM.message("ok").label()).contains(TestMessages.ONE_PARAM.label("ok"));

        final OperationResult<String> result3 = OperationResult.some("value3");
        assertThat(result3.isValid()).isTrue();
        assertThat(result3.get()).isEqualTo("value3");
        assertThat(result3.getOrNull()).isEqualTo("value3");
        result3.addWarning(TestMessages.ONE_PARAM.message("ok3"));
        assertThat(result3.isValid()).isTrue();
        assertThat(result3.get()).isEqualTo("value3");
        result3.addError(TestMessages.NO_PARAMS.message());
        result3.addError(TestMessages.TWO_PARAMS.message("ok3", "other3"));
        assertThat(result3.isValid()).isFalse();
        assertThat(result3.getErrors()).hasSize(2);
        assertThat(result3.getErrorsAsString()).contains(TestMessages.TWO_PARAMS.message("ok3", "other3").label())
            .contains(TestMessages.TWO_PARAMS.label("ok3", "other3"));
        assertThat(result3.getErrorsAsString()).contains(TestMessages.NO_PARAMS.message().label()).contains(TestMessages.NO_PARAMS.label());
    }

    @Test public void testExceptions()
        throws Exception
    {
        try {
            OperationResult.none().get();
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        }
        catch (final NullPointerException ignore) {}
        try {
            OperationResult.none().addError(TestMessages.NO_PARAMS).get();
            failBecauseExceptionWasNotThrown(ApplicationException.class);
        }
        catch (final ApplicationException e) {
            assertThat(e.getMessage()).isEqualTo(TestMessages.NO_PARAMS.label());
        }
        try {
            OperationResult.some("value").addError(TestMessages.NO_PARAMS).get();
            failBecauseExceptionWasNotThrown(ApplicationException.class);
        }
        catch (final ApplicationException e) {
            assertThat(e.getMessage()).isEqualTo(TestMessages.NO_PARAMS.label());
        }
        try {
            OperationResult.some("value").addError(TestMessages.ONE_PARAM, "fail").get();
            failBecauseExceptionWasNotThrown(ApplicationException.class);
        }
        catch (final ApplicationException e) {
            assertThat(e.getMessage()).isEqualTo(TestMessages.ONE_PARAM.label("fail"));
        }
    }
}  // end class OperationResultTest
