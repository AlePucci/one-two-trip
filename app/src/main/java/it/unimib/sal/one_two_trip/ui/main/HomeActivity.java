package it.unimib.sal.one_two_trip.ui.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.Utils;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Utils.trip_1.setActivity(new Activity[]{Utils.activity_1, Utils.activity_2, Utils.activity_3});
        Utils.trip_2.setActivity(new Activity[]{Utils.activity_4, Utils.activity_5, Utils.activity_6});
        Utils.trip_3.setActivity(new Activity[]{});

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
        if(navController != null) NavigationUI.setupActionBarWithNavController(this,
                navController, appBarConfiguration);

        // For the BottomNavigationView
        if(navController != null) NavigationUI.setupWithNavController(bottomNav, navController);

    }
}
