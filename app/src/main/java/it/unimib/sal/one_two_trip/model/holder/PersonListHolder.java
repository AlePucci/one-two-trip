package it.unimib.sal.one_two_trip.model.holder;

import androidx.annotation.NonNull;
import androidx.room.Ignore;

import java.util.List;
import java.util.Objects;

import it.unimib.sal.one_two_trip.model.Person;

/**
 * Class used to store the list of persons in Room database.
 */
public class PersonListHolder {

    private List<Person> personList;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonListHolder that = (PersonListHolder) o;
        return Objects.equals(personList, that.personList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personList);
    }

    @NonNull
    @Override
    public String toString() {
        return "PersonListHolder{" +
                "personList=" + personList +
                '}';
    }
}
