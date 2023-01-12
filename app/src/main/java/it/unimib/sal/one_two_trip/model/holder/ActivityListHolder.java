package it.unimib.sal.one_two_trip.model.holder;

import androidx.annotation.NonNull;
import androidx.room.Ignore;

import java.util.List;
import java.util.Objects;

import it.unimib.sal.one_two_trip.model.Activity;

/**
 * This class is used to store the list of activities in the database.
 */
public class ActivityListHolder {
    
    private List<Activity> activityList;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityListHolder that = (ActivityListHolder) o;
        return Objects.equals(activityList, that.activityList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityList);
    }

    @NonNull
    @Override
    public String toString() {
        return "ActivityListHolder{" +
                "activityList=" + activityList +
                '}';
    }
}
