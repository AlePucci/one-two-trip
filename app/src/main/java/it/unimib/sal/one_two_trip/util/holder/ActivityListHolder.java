package it.unimib.sal.one_two_trip.util.holder;

import java.util.List;

import it.unimib.sal.one_two_trip.model.Activity;

public class ActivityListHolder {
    public final List<Activity> activityList;

    public ActivityListHolder(List<Activity> activityList) {
        this.activityList = activityList;
    }
}
