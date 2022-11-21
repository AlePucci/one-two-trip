package it.unimib.sal.one_two_trip.model;

import java.util.Date;

public class Utils {
    public static final Person admin = new Person("0","Admin", "Test", "admin@test.com", "password", "1234567890", "none");
    public static final Trip trip_1 = new Trip("1", "admin@test.com", "Parigi", "");
    public static final Trip trip_2 = new Trip("2", "admin@test.com", "Londra", "");
    public static final Trip trip_3 = new Trip("3", "admin@test.com", "Amsterdam", "");

    public static final Person[] participants = {admin};
    public static final Activity activity_1 = new MovingActivity("1", "Volo #EZY3932", "Volo in aereo bla bla bla", "MXP", new Date(123,1,14, 9, 30, 0), participants, false, "1", new Object[]{}, null, "CDG", new Date(123,1,14,11,30,0));
    public static final Activity activity_2 = new Activity("2", "Museo del Louvre", "Gita", "PARIS", new Date(123,1,14, 15, 0, 0), participants, false, "1",  new Object[]{}, null);
    public static final Activity activity_3 = new Activity("3", "Champs Elisées", "Gita", "PARIS", new Date(123,1,15, 10, 0, 0), participants, false, "1", null, null);
    public static final Activity activity_4 = new MovingActivity("4", "Volo #FR8753", "A", "LIN", new Date(122,3,16, 11, 45, 0), participants, false, "1",  new Object[]{}, null, "STN", new Date(122,3,16, 13, 45, 0));
    public static final Activity activity_5 = new Activity("5", "Buckingham Palace", "B", "LONDON", new Date(122,3,16, 15, 0, 0), participants, false, "1", null, null);
    public static final Activity activity_6 = new Activity("6", "Harry Potter Studios", "C", "LONDON", new Date(122,3,16, 17, 0, 0), participants, false, "1", null, null);
    public static final Activity activity_7 = new MovingActivity("7", "Volo #AZ120", "B", "MXP", new Date(123,2,1, 8, 35, 0), participants, false, "1",  new Object[]{}, null, "AMS", new Date(123,2,1, 10, 55, 0));
    public static final Activity activity_8 = new Activity("8", "Van Gogh Museum", "C", "AMSTERDAM", new Date(123,2,2, 9, 0, 0), participants, false, "1", null, null);

    public static final Trip[] trips = {trip_1, trip_2, trip_3};
}
