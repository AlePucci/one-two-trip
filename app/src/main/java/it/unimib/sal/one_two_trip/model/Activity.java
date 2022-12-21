package it.unimib.sal.one_two_trip.model;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import it.unimib.sal.one_two_trip.util.holder.PersonListHolder;

/**
 * This class represents an activity.
 * {@link #type Type property} tells us if it's a moving activity (like a flight or train ride)
 * or a standard activity (like a visit to a museum).
 */
@Entity
public class Activity {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String title;
    private String description;
    private String location;
    private String end_location;
    private long start_date;
    private long end_date;
    @Embedded
    private PersonListHolder participant;
    private long trip_id;
    private List<Object> attachment;
    private List<String> link;
    private boolean completed;
    private String type;
    private boolean everyoneParticipate;

    public Activity() {
    }

    @Ignore
    public Activity(long id, String title, String description, String location, String end_location,
                    long start_date, long end_date, PersonListHolder participant, long trip_id,
                    List<Object> attachment, List<String> link, boolean completed, String type,
                    boolean everyoneParticipate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.end_location = end_location;
        this.start_date = start_date;
        this.end_date = end_date;
        this.participant = participant;
        this.trip_id = trip_id;
        this.attachment = attachment;
        this.link = link;
        this.completed = completed;
        this.type = type;
        this.everyoneParticipate = everyoneParticipate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEnd_location() {
        return end_location;
    }

    public void setEnd_location(String end_location) {
        this.end_location = end_location;
    }

    public long getStart_date() {
        return start_date;
    }

    public void setStart_date(long start_date) {
        this.start_date = start_date;
    }

    public long getEnd_date() {
        return end_date;
    }

    public void setEnd_date(long end_date) {
        this.end_date = end_date;
    }

    public PersonListHolder getParticipant() {
        return participant;
    }

    public void setParticipant(PersonListHolder participant) {
        this.participant = participant;
    }

    public long getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(long trip_id) {
        this.trip_id = trip_id;
    }

    public List<Object> getAttachment() {
        return attachment;
    }

    public void setAttachment(List<Object> attachment) {
        this.attachment = attachment;
    }

    public List<String> getLink() {
        return link;
    }

    public void setLink(List<String> link) {
        this.link = link;
    }

    public boolean isCompleted() {
        checkCompleted();
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEveryoneParticipate() {
        return everyoneParticipate;
    }

    public void setEveryoneParticipate(boolean everyoneParticipate) {
        this.everyoneParticipate = everyoneParticipate;
    }

    /**
     * This method checks if the activity is completed.
     * An activity is automatically marked as completed if the current date
     * is after the end date.
     */
    public void checkCompleted() {
        boolean completed;

        if(end_date != 0) {
            completed = end_date < (new Date().getTime());
        }
        else{
            completed = start_date < (new Date().getTime());
        }

        setCompleted(completed);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return id == activity.id && start_date == activity.start_date &&
                end_date == activity.end_date && trip_id == activity.trip_id &&
                completed == activity.completed &&
                everyoneParticipate == activity.everyoneParticipate &&
                Objects.equals(title, activity.title) &&
                Objects.equals(description, activity.description) &&
                Objects.equals(location, activity.location) &&
                Objects.equals(end_location, activity.end_location) &&
                Objects.equals(participant, activity.participant) &&
                Objects.equals(attachment, activity.attachment) &&
                Objects.equals(link, activity.link) && Objects.equals(type, activity.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, location, end_location, start_date, end_date,
                participant, trip_id, attachment, link, completed, type, everyoneParticipate);
    }

    @NonNull
    @Override
    public String toString() {
        return "Activity{" + "id='" + id + '\'' + ", title='" + title + '\'' +
                ", location='" + location + '\'' + ", start_date=" + start_date +
                ", everyoneParticipate=" + everyoneParticipate + '}';
    }
}
