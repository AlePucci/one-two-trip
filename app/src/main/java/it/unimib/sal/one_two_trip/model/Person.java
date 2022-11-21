package it.unimib.sal.one_two_trip.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Person {
    private String id;
    private String name;
    private String surname;
    private String email_address;
    private String password;
    private String phone_number;
    private String profile_picture;

    private Trip[] owned_trips;
    private Trip[] joined_trips;

    public Person(String id, String name, String surname, String email_address, String password, String phone_number, String profile_picture) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email_address = email_address;
        this.password = password;
        this.phone_number = phone_number;
        this.profile_picture = profile_picture;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail_address() {
        return email_address;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public Trip[] getOwned_trips() {
        return owned_trips;
    }

    public Trip[] getJoined_trips() {
        return joined_trips;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public void setOwned_trips(Trip[] owned_trips) {
        this.owned_trips = owned_trips;
    }

    public void setJoined_trips(Trip[] joined_trips) {
        this.joined_trips = joined_trips;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return email_address.equals(person.email_address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email_address);
    }

    @NonNull
    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email_address='" + email_address + '\'' +
                '}';
    }
}
