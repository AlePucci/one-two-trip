package it.unimib.sal.one_two_trip.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Person {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String surname;
    private String email_address;
    private String password;
    private String phone_number;
    private String profile_picture;

    public Person() {
    }

    @Ignore
    public Person(long id, String name, String surname, String email_address, String password, String phone_number, String profile_picture) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email_address = email_address;
        this.password = password;
        this.phone_number = phone_number;
        this.profile_picture = profile_picture;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
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
        return "Person{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", surname='" + surname + '\'' + ", email_address='" + email_address + '\'' + '}';
    }

}
