
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import com.fasterxml.jackson.annotation.JsonProperty;

import tekgenesis.common.core.DateOnly;

import static tekgenesis.common.Predefined.equal;

/**
 * Person.
 */
public class Person {

    //~ Instance Fields ..............................................................................................................................

    @JsonProperty public String   address   = null;
    @JsonProperty public Age      age       = null;
    @JsonProperty public DateOnly birthDate = null;
    @JsonProperty public String   name      = null;

    //~ Constructors .................................................................................................................................

    /** Constructor for jackson.* */
    public Person() {}

    Person(String n, String add, Age a, DateOnly b) {
        name      = n;
        address   = add;
        age       = a;
        birthDate = b;
    }

    //~ Methods ......................................................................................................................................

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Person person = (Person) o;

        return equal(address, person.address) && equal(age, person.age) && equal(birthDate, person.birthDate) && equal(name, person.name);
    }

    @Override public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    //~ Methods ......................................................................................................................................

    /** Create a Person. */
    public static Person createPerson() {
        // noinspection DuplicateStringLiteralInspection,MagicNumber
        return new Person("Ian Anderson", "None", new Age(80), DateOnly.date(1981, 5, 12));
    }
}  // end class Person
