package it.unimib.sal.one_two_trip.util.holder;

import java.util.List;

import it.unimib.sal.one_two_trip.model.Activity;

/**
 * This class is used to store the list of activities in the database.
 */
public class ActivityListHolder {
    public final List<Activity> activityList;

    public ActivityListHolder(List<Activity> activityList) {
        this.activityList = activityList;
    }
}
