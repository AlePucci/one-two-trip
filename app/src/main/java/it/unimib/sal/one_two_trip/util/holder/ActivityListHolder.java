package it.unimib.sal.one_two_trip.util.holder;

import androidx.room.Ignore;

import java.util.List;

import it.unimib.sal.one_two_trip.model.Activity;

/**
 * This class is used to store the list of activities in the database.
 */
public class ActivityListHolder {
    public List<Activity> activityList;

    public ActivityListHolder() {
    }

    @Ignore
    public ActivityListHolder(List<Activity> activityList) {
        this.activityList = activityList;
    }

    public List<Activity> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<Activity> activityList) {
        this.activityList = activityList;
    }
}
