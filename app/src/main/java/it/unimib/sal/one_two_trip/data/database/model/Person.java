package it.unimib.sal.one_two_trip.data.database.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

/**
 * This class represents a person/user of the application.
 */
@Entity
public class Person {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "surname")
    private String surname;

    @ColumnInfo(name = "email_address")
    private String email_address;

    @ColumnInfo(name = "profile_picture")
    private String profile_picture;

    public Person() {
        id = "";
    }

    @Ignore
    public Person(@NonNull String id, String name, String surname, String email_address, String profile_picture) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email_address = email_address;
        this.profile_picture = profile_picture;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
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
        return id.equals(person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        return "Person{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", surname='" +
                surname + '\'' + ", email_address='" + email_address + '\'' + '}';
    }
}
