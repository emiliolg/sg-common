
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

import tekgenesis.common.core.DateOnly;

import static org.assertj.core.api.Assertions.assertThat;

import static tekgenesis.common.core.PrettyDateParser.prettyParse;

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class PrettyDateParserTest {

    //~ Methods ......................................................................................................................................

    @Test public void simplePrettyDateParserDayMonthYear() {
        assertThat(prettyParse("23/04/2014", "dd/MM/yyyy")).isEqualTo(DateOnly.date(2014, 4, 23).toDate());
        assertThat(prettyParse("23-04-2014", "dd/MM/yyyy")).isEqualTo(DateOnly.date(2014, 4, 23).toDate());
        assertThat(prettyParse("23042014", "dd/MM/yyyy")).isEqualTo(DateOnly.date(2014, 4, 23).toDate());

        assertThat(prettyParse("23/04/14", "dd/MM/yyyy")).isEqualTo(DateOnly.date(2014, 4, 23).toDate());
        assertThat(prettyParse("23-04-14", "dd/MM/yyyy")).isEqualTo(DateOnly.date(2014, 4, 23).toDate());
        assertThat(prettyParse("230414", "dd/MM/yyyy")).isEqualTo(DateOnly.date(2014, 4, 23).toDate());

        assertThat(prettyParse("08/12/1954", "dd/MM/yyyy")).isEqualTo(DateOnly.date(1954, 12, 8).toDate());
        assertThat(prettyParse("08-12-1954", "dd/MM/yyyy")).isEqualTo(DateOnly.date(1954, 12, 8).toDate());
        assertThat(prettyParse("08121954", "dd/MM/yyyy")).isEqualTo(DateOnly.date(1954, 12, 8).toDate());

        assertThat(prettyParse("08/12/54", "dd/MM/yyyy")).isEqualTo(DateOnly.date(1954, 12, 8).toDate());
        assertThat(prettyParse("08-12-54", "dd/MM/yyyy")).isEqualTo(DateOnly.date(1954, 12, 8).toDate());
        assertThat(prettyParse("081254", "dd/MM/yyyy")).isEqualTo(DateOnly.date(1954, 12, 8).toDate());

        assertThat(prettyParse("22/01/10", "dd/MM/yyyy")).isEqualTo(DateOnly.date(2010, 1, 22).toDate());
        assertThat(prettyParse("22-01-10", "dd/MM/yyyy")).isEqualTo(DateOnly.date(2010, 1, 22).toDate());
        assertThat(prettyParse("220110", "dd/MM/yyyy")).isEqualTo(DateOnly.date(2010, 1, 22).toDate());

        assertThat(prettyParse("22/01/2010", "dd/MM/yyyy")).isEqualTo(DateOnly.date(2010, 1, 22).toDate());
        assertThat(prettyParse("22-01-2010", "dd/MM/yyyy")).isEqualTo(DateOnly.date(2010, 1, 22).toDate());
        assertThat(prettyParse("22012010", "dd/MM/yyyy")).isEqualTo(DateOnly.date(2010, 1, 22).toDate());

        assertThat(prettyParse("15/06/20", "dd/MM/yyyy")).isEqualTo(DateOnly.date(2020, 6, 15).toDate());
        assertThat(prettyParse("15-06-20", "dd/MM/yyyy")).isEqualTo(DateOnly.date(2020, 6, 15).toDate());
        assertThat(prettyParse("150620", "dd/MM/yyyy")).isEqualTo(DateOnly.date(2020, 6, 15).toDate());

        assertThat(prettyParse("15/06/2020", "dd/MM/yyyy")).isEqualTo(DateOnly.date(2020, 6, 15).toDate());
        assertThat(prettyParse("15-06-2020", "dd/MM/yyyy")).isEqualTo(DateOnly.date(2020, 6, 15).toDate());
        assertThat(prettyParse("15062020", "dd/MM/yyyy")).isEqualTo(DateOnly.date(2020, 6, 15).toDate());
    }

    @Test public void simplePrettyDateParserMonthDayYear() {
        assertThat(prettyParse("04/23/2014", "M/d/yy")).isEqualTo(DateOnly.date(2014, 4, 23).toDate());
        assertThat(prettyParse("04-23-2014", "M/d/yy")).isEqualTo(DateOnly.date(2014, 4, 23).toDate());
        assertThat(prettyParse("04232014", "M/d/yy")).isEqualTo(DateOnly.date(2014, 4, 23).toDate());

        assertThat(prettyParse("04/23/14", "M/d/yy")).isEqualTo(DateOnly.date(2014, 4, 23).toDate());
        assertThat(prettyParse("04-23-14", "M/d/yy")).isEqualTo(DateOnly.date(2014, 4, 23).toDate());
        assertThat(prettyParse("042314", "M/d/yy")).isEqualTo(DateOnly.date(2014, 4, 23).toDate());

        assertThat(prettyParse("12/08/1954", "M/d/yy")).isEqualTo(DateOnly.date(1954, 12, 8).toDate());
        assertThat(prettyParse("12-08-1954", "M/d/yy")).isEqualTo(DateOnly.date(1954, 12, 8).toDate());
        assertThat(prettyParse("12081954", "M/d/yy")).isEqualTo(DateOnly.date(1954, 12, 8).toDate());

        assertThat(prettyParse("12/08/54", "M/d/yy")).isEqualTo(DateOnly.date(1954, 12, 8).toDate());
        assertThat(prettyParse("12-08-54", "M/d/yy")).isEqualTo(DateOnly.date(1954, 12, 8).toDate());
        assertThat(prettyParse("120854", "M/d/yy")).isEqualTo(DateOnly.date(1954, 12, 8).toDate());

        assertThat(prettyParse("01/22/10", "M/d/yy")).isEqualTo(DateOnly.date(2010, 1, 22).toDate());
        assertThat(prettyParse("01-22-10", "M/d/yy")).isEqualTo(DateOnly.date(2010, 1, 22).toDate());
        assertThat(prettyParse("012210", "M/d/yy")).isEqualTo(DateOnly.date(2010, 1, 22).toDate());

        assertThat(prettyParse("01/22/2010", "M/d/yy")).isEqualTo(DateOnly.date(2010, 1, 22).toDate());
        assertThat(prettyParse("01-22-2010", "M/d/yy")).isEqualTo(DateOnly.date(2010, 1, 22).toDate());
        assertThat(prettyParse("01222010", "M/d/yy")).isEqualTo(DateOnly.date(2010, 1, 22).toDate());

        assertThat(prettyParse("06/15/20", "M/d/yy")).isEqualTo(DateOnly.date(2020, 6, 15).toDate());
        assertThat(prettyParse("06-15-20", "M/d/yy")).isEqualTo(DateOnly.date(2020, 6, 15).toDate());
        assertThat(prettyParse("061520", "M/d/yy")).isEqualTo(DateOnly.date(2020, 6, 15).toDate());

        assertThat(prettyParse("06/15/2020", "M/d/yy")).isEqualTo(DateOnly.date(2020, 6, 15).toDate());
        assertThat(prettyParse("06-15-2020", "M/d/yy")).isEqualTo(DateOnly.date(2020, 6, 15).toDate());
        assertThat(prettyParse("06152020", "M/d/yy")).isEqualTo(DateOnly.date(2020, 6, 15).toDate());
    }

    @Test public void simplePrettyDateParserNullCases() {
        assertThat(prettyParse("abcde", "dd/MM/yyyy")).isEqualTo(null);
        assertThat(prettyParse("abcde", "M/d/yy")).isEqualTo(null);
        assertThat(prettyParse("abcde", "yyyy/MM/dd")).isEqualTo(null);

        assertThat(prettyParse("abcdefghij", "dd/MM/yyyy")).isEqualTo(null);
        assertThat(prettyParse("abcdefghij", "M/d/yy")).isEqualTo(null);
        assertThat(prettyParse("abcdefghij", "yyyy/MM/dd")).isEqualTo(null);
    }

    @Test public void simplePrettyDateParserYearMonthDay() {
        assertThat(prettyParse("2014/04/23", "yyyy/MM/dd")).isEqualTo(DateOnly.date(2014, 4, 23).toDate());
        assertThat(prettyParse("2014-04-23", "yyyy/MM/dd")).isEqualTo(DateOnly.date(2014, 4, 23).toDate());
        assertThat(prettyParse("2014-04-23", "yyyy/MM/dd")).isEqualTo(DateOnly.date(2014, 4, 23).toDate());

        assertThat(prettyParse("14/04/23", "yyyy/MM/dd")).isEqualTo(DateOnly.date(2014, 4, 23).toDate());
        assertThat(prettyParse("14-04-23", "yyyy/MM/dd")).isEqualTo(DateOnly.date(2014, 4, 23).toDate());
        assertThat(prettyParse("140423", "yyyy/MM/dd")).isEqualTo(DateOnly.date(2014, 4, 23).toDate());

        assertThat(prettyParse("1954/12/08", "yyyy/MM/dd")).isEqualTo(DateOnly.date(1954, 12, 8).toDate());
        assertThat(prettyParse("1954-12-08", "yyyy/MM/dd")).isEqualTo(DateOnly.date(1954, 12, 8).toDate());
        assertThat(prettyParse("19541208", "yyyy/MM/dd")).isEqualTo(DateOnly.date(1954, 12, 8).toDate());

        assertThat(prettyParse("54/12/08", "yyyy/MM/dd")).isEqualTo(DateOnly.date(1954, 12, 8).toDate());
        assertThat(prettyParse("54-12-08", "yyyy/MM/dd")).isEqualTo(DateOnly.date(1954, 12, 8).toDate());
        assertThat(prettyParse("541208", "yyyy/MM/dd")).isEqualTo(DateOnly.date(1954, 12, 8).toDate());

        assertThat(prettyParse("10/01/22", "yyyy/MM/dd")).isEqualTo(DateOnly.date(2010, 1, 22).toDate());
        assertThat(prettyParse("10-01-22", "yyyy/MM/dd")).isEqualTo(DateOnly.date(2010, 1, 22).toDate());
        assertThat(prettyParse("100122", "yyyy/MM/dd")).isEqualTo(DateOnly.date(2010, 1, 22).toDate());

        assertThat(prettyParse("2010/01/22", "yyyy/MM/dd")).isEqualTo(DateOnly.date(2010, 1, 22).toDate());
        assertThat(prettyParse("2010-01-22", "yyyy/MM/dd")).isEqualTo(DateOnly.date(2010, 1, 22).toDate());
        assertThat(prettyParse("20100122", "yyyy/MM/dd")).isEqualTo(DateOnly.date(2010, 1, 22).toDate());

        assertThat(prettyParse("20/06/15", "yyyy/MM/dd")).isEqualTo(DateOnly.date(2020, 6, 15).toDate());
        assertThat(prettyParse("20-06-15", "yyyy/MM/dd")).isEqualTo(DateOnly.date(2020, 6, 15).toDate());
        assertThat(prettyParse("200615", "yyyy/MM/dd")).isEqualTo(DateOnly.date(2020, 6, 15).toDate());

        assertThat(prettyParse("2020/06/15", "yyyy/MM/dd")).isEqualTo(DateOnly.date(2020, 6, 15).toDate());
        assertThat(prettyParse("2020-06-15", "yyyy/MM/dd")).isEqualTo(DateOnly.date(2020, 6, 15).toDate());
        assertThat(prettyParse("20200615", "yyyy/MM/dd")).isEqualTo(DateOnly.date(2020, 6, 15).toDate());
    }
}  // end class PrettyDateParserTest
