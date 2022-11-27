package it.unimib.sal.one_two_trip.util;


import java.util.List;

import it.unimib.sal.one_two_trip.model.Person;

public class PersonListHolder  {
    public List<Person> personList;

    public PersonListHolder(List<Person> personList){
        this.personList = personList;
    }
}
