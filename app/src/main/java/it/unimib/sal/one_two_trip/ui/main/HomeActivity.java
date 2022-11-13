package it.unimib.sal.one_two_trip.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import it.unimib.sal.one_two_trip.R;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.top_appbar);
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

/*
        Intent intent = getIntent();
        Log.d(TAG, "Times: " + intent.getIntExtra(EXTRA_BUTTON_PRESSED_COUNTER_KEY, 0));
        Log.d(TAG, "News: " + intent.getParcelableExtra(EXTRA_NEWS_KEY)); */
    }
}