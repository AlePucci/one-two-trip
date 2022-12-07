package it.unimib.sal.one_two_trip.util.holder;

import java.util.List;

import it.unimib.sal.one_two_trip.model.Person;

/**
 * This class is used to store the list of persons in the database.
 */
public class PersonListHolder {
    public final List<Person> personList;

    public PersonListHolder(List<Person> personList) {
        this.personList = personList;
    }
}
