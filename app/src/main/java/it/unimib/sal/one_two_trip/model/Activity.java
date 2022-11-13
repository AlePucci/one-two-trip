package it.unimib.sal.one_two_trip.model;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Objects;

public class Activity {
    private String id;
    private String title;
    private String description;
    private String location;
    private Date start_date;
    private Person[] participant;
    private String trip_id;
    private Object[] attachment;
    private String[] link;
    private boolean completed;

    public Activity(String id, String title, String description, String location, Date start_date, Person[] participant, boolean completed, String trip_id, Object[] attachment, String[] link) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.start_date = start_date;
        this.participant = participant;
        this.completed = completed;
        this.trip_id = trip_id;
        this.attachment = attachment;
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Date getStart_date() {
        return start_date;
    }

    public Person[] getParticipant() {
        return participant;
    }

    public boolean isCompleted() {
        checkCompleted();
        return completed;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public Object[] getAttachment() {
        return attachment;
    }

    public String[] getLink() {
        return link;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public void setParticipant(Person[] participant) {
        this.participant = participant;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public void setAttachment(Object[] attachment) {
        this.attachment = attachment;
    }

    public void setLink(String[] link) {
        this.link = link;
    }

    public void checkCompleted(){
       if(start_date.before(new Date())){
           setCompleted(true);
       }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return id.equals(activity.id) && trip_id.equals(activity.trip_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, trip_id);
    }

    @NonNull
    @Override
    public String toString() {
        return "Activity{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", start_date=" + start_date +
                '}';
    }
}
