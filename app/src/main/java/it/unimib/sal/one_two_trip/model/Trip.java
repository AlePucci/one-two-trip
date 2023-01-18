package it.unimib.sal.one_two_trip.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Comparator;
import java.util.Objects;

import it.unimib.sal.one_two_trip.model.holder.ActivityListHolder;
import it.unimib.sal.one_two_trip.model.holder.PersonListHolder;

/**
 * This class represents a trip.
 */
@Entity
public class Trip {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "tripOwner")
    private String tripOwner;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @Embedded
    private ActivityListHolder activity;

    @Embedded
    private PersonListHolder participant;

    @ColumnInfo(name = "completed")
    private boolean completed;

    @Ignore
    private long start_date;

    public Trip() {
    }

    @Ignore
    public Trip(long id, String tripOwner, String title, String description,
                ActivityListHolder activity, PersonListHolder participant, boolean completed) {
        this.id = id;
        this.tripOwner = tripOwner;
        this.title = title;
        this.description = description;
        this.activity = activity;
        this.participant = participant;
        this.completed = completed;

        if (this.activity != null && this.activity.getActivityList() != null
                && !this.activity.getActivityList().isEmpty()) {
            this.activity.getActivityList().sort(Comparator.comparing(Activity::getStart_date));
            this.start_date = this.activity.getActivityList().get(0).getStart_date();
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTripOwner() {
        return tripOwner;
    }

    public void setTripOwner(String tripOwner) {
        this.tripOwner = tripOwner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ActivityListHolder getActivity() {
        return activity;
    }

    public void setActivity(ActivityListHolder activity) {
        this.activity = activity;
        if (this.activity != null && this.activity.getActivityList() != null
                && !this.activity.getActivityList().isEmpty()
                && this.activity.getActivityList().get(0) != null) {
            this.activity.getActivityList().sort(Comparator.comparing(Activity::getStart_date));
            this.start_date = this.activity.getActivityList().get(0).getStart_date();
        }
    }

    @Ignore
    public long getStart_date() {
        return start_date;
    }

    public PersonListHolder getParticipant() {
        return participant;
    }

    public void setParticipant(PersonListHolder participant) {
        this.participant = participant;
    }

    public boolean isCompleted() {
        checkCompleted();
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Check if the trip is completed, i.e. all the activities are completed.
     */
    public void checkCompleted() {
        boolean isThereAtLeastOneActivity = false;
        if (this.getActivity() == null || this.getActivity().getActivityList() == null) {
            setCompleted(false);
            return;
        }

        for (Activity a : activity.getActivityList()) {
            isThereAtLeastOneActivity = true;
            if (a == null || !a.isCompleted()) {
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
        return id == trip.id && completed == trip.completed && tripOwner.equals(trip.tripOwner) &&
                Objects.equals(title, trip.title) &&
                Objects.equals(description, trip.description) &&
                Objects.equals(activity, trip.activity) &&
                Objects.equals(participant, trip.participant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tripOwner, title, description, activity, participant, completed);
    }

    @NonNull
    @Override
    public String toString() {
        return "Trip{" + "id='" + id + '\'' + ", title='" + title + '\'' + ", completed=" +
                completed + ", activity=" + activity + '}';
    }
}
