package it.unimib.sal.one_two_trip.util.holder;

import androidx.room.Ignore;

import java.util.List;

import it.unimib.sal.one_two_trip.model.Person;

/**
 * This class is used to store the list of persons in the database.
 */
public class PersonListHolder {
    public List<Person> personList;

    public PersonListHolder() {
    }

    @Ignore
    public PersonListHolder(List<Person> personList) {
        this.personList = personList;
    }

    public List<Person> getPersonList() {
        return personList;
    }

    public void setPersonList(List<Person> personList) {
        this.personList = personList;
    }
}
