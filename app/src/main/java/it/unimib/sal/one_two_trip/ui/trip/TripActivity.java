package it.unimib.sal.one_two_trip.ui.trip;

import static it.unimib.sal.one_two_trip.util.Constants.MOVE_TO_ACTIVITY;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_ACTIVITY_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_NAME;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.appbar.MaterialToolbar;

import org.osmdroid.config.Configuration;

import it.unimib.sal.one_two_trip.BuildConfig;
import it.unimib.sal.one_two_trip.R;

public class TripActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        long tripId = getIntent().getLongExtra(SELECTED_TRIP_ID, 0);
        boolean moveToActivity = getIntent().getBooleanExtra(MOVE_TO_ACTIVITY, false);
        long activityId = 0;
        if (moveToActivity) {
            activityId = getIntent().getLongExtra(SELECTED_ACTIVITY_ID, 0);
        }
        String tripName = getIntent().getStringExtra(SELECTED_TRIP_NAME);

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        //Toolbar
        MaterialToolbar toolbar = findViewById(R.id.trip_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        ActionBar actionBar = getSupportActionBar();

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.yellow_500));

        if (actionBar != null) {
            actionBar.setTitle(tripName);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().
                findFragmentById(R.id.trip_nav_host);

        Bundle bundle = new Bundle();
        bundle.putLong(SELECTED_TRIP_ID, tripId);
        bundle.putBoolean(MOVE_TO_ACTIVITY, moveToActivity);
        bundle.putLong(SELECTED_ACTIVITY_ID, activityId);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            navController.setGraph(R.navigation.trip_nav_graph, bundle);
        }
    }
}
