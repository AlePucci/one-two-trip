package it.unimib.sal.one_two_trip.util;

import java.util.Calendar;
import java.util.Date;

public class TripsListUtil {
    /**
     * Utility method to verify if two dates are in the same day
     *
     * @param date1 first date
     * @param date2 second date
     * @return true if the dates are in the same day (excluding time), false otherwise
     */
    public static int compareDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH))
            return 1;

        return 0;
    }
}
