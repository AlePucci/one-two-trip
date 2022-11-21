package it.unimib.sal.one_two_trip.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Trip {
    private String id;
    private String tripOwner; // email address of the owner
    private String title;
    private String description;
    private Activity[] activity;
    private Person[] participant;
    private boolean completed = false;

    public Trip(String id, String tripOwner, String title, String description) {
        this.id = id;
        this.tripOwner = tripOwner;
        this.title = title;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTripOwner() {
        return tripOwner;
    }

    public String getDescription() {
        return description;
    }

    public Activity[] getActivity() {
        return activity;
    }

    public Person[] getParticipant() {
        return participant;
    }

    public boolean isCompleted() {
        checkCompleted();
        return completed;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTripOwner(String tripOwner) {
        this.tripOwner = tripOwner;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setActivity(Activity[] activity) {
        this.activity = activity;
    }

    public void setParticipant(Person[] participant) {
        this.participant = participant;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void checkCompleted(){
        boolean isThereAtLeastOneActivity = false;
        if (this.getActivity() == null) {
            setCompleted(false);
            return;
        }

        for(Activity a : activity){
            isThereAtLeastOneActivity = true;
            if(a == null || !a.isCompleted()){
                setCompleted(false);
                return;
            }
        }

        setCompleted(isThereAtLeastOneActivity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return id.equals(trip.id) && tripOwner.equals(trip.tripOwner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tripOwner);
    }

    @NonNull
    @Override
    public String toString() {
        return "Trip{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                '}';
    }
}
