package it.unimib.sal.one_two_trip.ui.trip;

import static android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
import static it.unimib.sal.one_two_trip.util.Constants.MOVE_TO_ACTIVITY;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_ACTIVITY_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_NAME;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.appbar.MaterialToolbar;

import org.osmdroid.config.Configuration;

import it.unimib.sal.one_two_trip.BuildConfig;
import it.unimib.sal.one_two_trip.R;

/**
 * Activity that contains Fragments to allow user to see, edit and delete the details of a trip.
 * It contains a interactive map that shows the activities of the trip.
 */
public class TripActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);


        String tripId = getIntent().getStringExtra(SELECTED_TRIP_ID);
        boolean moveToActivity = getIntent().getBooleanExtra(MOVE_TO_ACTIVITY, false);
        String activityId = "";
        if (moveToActivity) {
            activityId = getIntent().getStringExtra(SELECTED_ACTIVITY_ID);
        }
        String tripName = getIntent().getStringExtra(SELECTED_TRIP_NAME);

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        //Toolbar
        MaterialToolbar toolbar = findViewById(R.id.trip_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        ActionBar actionBar = getSupportActionBar();

        int mode = this.getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK;


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (mode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.gray_700));

            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        } else {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));

            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }

        if (actionBar != null) {
            actionBar.setTitle(tripName);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().
                findFragmentById(R.id.trip_nav_host);

        Bundle bundle = new Bundle();
        bundle.putString(SELECTED_TRIP_ID, tripId);
        bundle.putBoolean(MOVE_TO_ACTIVITY, moveToActivity);
        bundle.putString(SELECTED_ACTIVITY_ID, activityId);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            navController.setGraph(R.navigation.trip_nav_graph, bundle);
        }
    }
}
