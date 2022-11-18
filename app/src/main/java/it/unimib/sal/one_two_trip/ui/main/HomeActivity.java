package it.unimib.sal.one_two_trip.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Date;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.Person;
import it.unimib.sal.one_two_trip.model.Trip;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();

    public static final Person admin = new Person("0","Admin", "Test", "admin@test.com", "password", "1234567890", "none");
    private static final Trip trip_1 = new Trip("1", "admin@test.com", "Parigi", "");
    private static final Trip trip_2 = new Trip("2", "admin@test.com", "Londra", "");
    private static final Trip trip_3 = new Trip("3", "admin@test.com", "Amsterdam", "");

    private static final Person[] participants = {admin};
    private static final Activity activity_1 = new Activity("1", "Volo #EZY3932", "Volo in aereo bla bla bla", "PARIS", new Date(123,1,14, 9, 30, 0), participants, false, "1", null, null);
    private static final Activity activity_2 = new Activity("2", "Museo del Louvre", "Gita", "PARIS", new Date(123,1,14, 15, 0, 0), participants, false, "1", null, null);
    private static final Activity activity_3 = new Activity("3", "Champs Elisées", "Gita", "PARIS", new Date(123,1,15, 10, 0, 0), participants, false, "1", null, null);
    private static final Activity activity_4 = new Activity("4", "Volo #FR8753", "A", "LONDON", new Date(122,3,16, 11, 45, 0), participants, false, "1", null, null);
    private static final Activity activity_5 = new Activity("5", "Buckingham Palace", "B", "LONDON", new Date(122,3,16, 15, 0, 0), participants, false, "1", null, null);
    private static final Activity activity_6 = new Activity("6", "Harry Potter Studios", "C", "LONDON", new Date(122,3,16, 17, 0, 0), participants, false, "1", null, null);
    private static final Activity activity_7 = new Activity("7", "Volo #AZ120", "B", "AMSTERDAM", new Date(123,2,1, 8, 35, 0), participants, false, "1", null, null);
    private static final Activity activity_8 = new Activity("8", "Van Gogh Museum", "C", "AMSTERDAM", new Date(123,2,2, 9, 0, 0), participants, false, "1", null, null);

    public static final Trip[] trips = {trip_1, trip_2, trip_3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        trip_1.setActivity(new Activity[]{activity_1, activity_2, activity_3});
        trip_2.setActivity(new Activity[]{activity_4, activity_5, activity_6});
        trip_3.setActivity(new Activity[]{activity_7, activity_8});

        MaterialToolbar toolbar = findViewById(R.id.top_appbar);
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().
                findFragmentById(R.id.nav_host_fragment);

        NavController navController = null;
        if(navHostFragment != null)  navController = navHostFragment.getNavController();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.fragment_coming_trips, R.id.fragment_past_trips).build();

        // For the Toolbar
        if(navController != null) NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // For the BottomNavigationView
        if(navController != null) NavigationUI.setupWithNavController(bottomNav, navController);

    }
}
