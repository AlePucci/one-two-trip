package it.unimib.sal.one_two_trip.util;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import it.unimib.sal.one_two_trip.data.database.model.Activity;
import it.unimib.sal.one_two_trip.data.database.model.Person;

/**
 * Utility class containing the converters used to convert the types used in the application
 * to the types supported by the Room database.
 */
public class Converters {

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
    public static List<Person> storedStringToPersonList(String data) {
        Gson gson = new Gson();
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Person>>() {
        }.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String personListToStoredString(List<Person> myObjects) {
        Gson gson = new Gson();
        return gson.toJson(myObjects);
    }
}
