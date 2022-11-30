package it.unimib.sal.one_two_trip.util;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.Person;
import it.unimib.sal.one_two_trip.util.holder.ActivityListHolder;
import it.unimib.sal.one_two_trip.util.holder.PersonListHolder;

/**
 * This class contains the converters to convert the types used to the types supported by Room
 * database.
 */
public class Converters {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static List<Activity> storedStringToActivityList(String data) {
        Gson gson = new Gson();
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Activity>>() {
        }.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String activityListToStoredString(List<Activity> myObjects) {
        Gson gson = new Gson();
        return gson.toJson(myObjects);
    }

    @TypeConverter
    public static ActivityListHolder storedStringToActivityListHolder(String data) {
        Gson gson = new Gson();
        return gson.fromJson(data, ActivityListHolder.class);
    }

    @TypeConverter
    public static String activityListHolderToStoredString(ActivityListHolder myObjects) {
        Gson gson = new Gson();
        return gson.toJson(myObjects);
    }

    @TypeConverter
    public static PersonListHolder storedStringToPersonListHolder(String data) {
        Gson gson = new Gson();
        return gson.fromJson(data, PersonListHolder.class);
    }

    @TypeConverter
    public static String personListHolderToStoredString(PersonListHolder myObjects) {
        Gson gson = new Gson();
        return gson.toJson(myObjects);
    }

    @TypeConverter
    public static List<Person> storedStringToPersonList(String data) {
        Gson gson = new Gson();
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Activity>>() {
        }.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String personListToStoredString(List<Person> myObjects) {
        Gson gson = new Gson();
        return gson.toJson(myObjects);
    }

    @TypeConverter
    public static List<Object> storedStringToObjectList(String data) {
        Gson gson = new Gson();
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Object>>() {
        }.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String objectListToStoredString(List<Object> myObjects) {
        Gson gson = new Gson();
        return gson.toJson(myObjects);
    }

    @TypeConverter
    public static List<String> storedStringToStringList(String data) {
        Gson gson = new Gson();
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String stringListToStoredString(List<String> myObjects) {
        Gson gson = new Gson();
        return gson.toJson(myObjects);
    }
}
