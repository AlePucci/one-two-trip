package it.unimib.sal.one_two_trip.model;

import java.util.Date;

public class MovingActivity extends Activity{
    private String end_location;
    private Date end_date;

    public MovingActivity(String id, String title, String description, String location, Date start_date, Person[] participant, boolean completed, String trip_id, Object[] attachment, String[] link, String end_location, Date end_date) {
        super(id, title, description, location, start_date, participant, completed, trip_id, attachment, link);
        this.end_location = end_location;
        this.end_date = end_date;
    }

    public String getEnd_location() {
        return end_location;
    }

    public void setEnd_location(String end_location) {
        this.end_location = end_location;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }
}
