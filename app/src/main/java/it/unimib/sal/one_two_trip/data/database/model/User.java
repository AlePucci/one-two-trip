package it.unimib.sal.one_two_trip.data.database.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import org.jetbrains.annotations.Contract;

public class User implements Parcelable {

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    private String name;
    private String email;
    private String idToken;

    public User(String name, String email, String idToken) {
        this.name = name;
        this.email = email;
        this.idToken = idToken;
    }

    protected User(@NonNull Parcel in) {
        this.name = in.readString();
        this.email = in.readString();
        this.idToken = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{ " + "name='" + name + '\'' + ", email='" + email + '\'' + ", idToken='" + idToken + '\'' + '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.idToken);
    }


    public void readFromParcel(@NonNull Parcel source) {
        this.name = source.readString();
        this.email = source.readString();
        this.idToken = source.readString();
    }
}
