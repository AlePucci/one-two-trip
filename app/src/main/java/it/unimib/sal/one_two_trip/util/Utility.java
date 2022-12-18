package it.unimib.sal.one_two_trip.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Utility class to provide useful methods among the application.
 */
public class Utility{
    /**
     * Utility method to verify if two dates are in the same day (ignoring time).
     *
     * @param date1 first date
     * @param date2 second date
     * @return true if the dates are in the same day (ignoring time), false otherwise
     */
    public static boolean compareDate(long date1, long date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(new Date(date1));
        cal2.setTime(new Date(date2));

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
}
