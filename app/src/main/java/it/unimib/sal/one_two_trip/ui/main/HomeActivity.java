package it.unimib.sal.one_two_trip.ui.main;

import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_NOTIFICATIONS_ON;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_TRIP_NOTIFICATIONS;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.util.AlarmReceiver;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;

/**
 * The main activity of the app that a user sees after logging in.
 * It contains a {@link BottomNavigationView} that allows the user to navigate between the
 * {@link ComingTripsFragment} that shows the coming trips of the user and the
 * {@link PastTripsFragment} that shows the past trips of the user.
 */
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private DrawerLayout drawerLayout;
    private AppBarConfiguration appBarConfiguration;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MaterialToolbar toolbar = findViewById(R.id.top_appbar);
        setSupportActionBar(toolbar);

        this.drawerLayout = findViewById(R.id.drawer_layout);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            this.navController = navHostFragment.getNavController();
        }

        NavigationView drawerNav = findViewById(R.id.drawer_navigation);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        this.appBarConfiguration = new AppBarConfiguration
                .Builder(R.id.fragment_coming_trips, R.id.fragment_past_trips,
                R.id.fragment_settings, R.id.fragment_about)
                .setOpenableLayout(this.drawerLayout).build();

        drawerNav.setCheckedItem(R.id.fragment_coming_trips);

        // For the Toolbar
        NavigationUI.setupActionBarWithNavController(this,
                this.navController, this.appBarConfiguration);

        // For the NavigationDrawer
        NavigationUI.setupWithNavController(drawerNav, this.navController);

        // For the BottomNavigationView
        NavigationUI.setupWithNavController(bottomNav, this.navController);

        addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.top_appbar_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        });

        drawerNav.getMenu().findItem(R.id.logout).setOnMenuItemClickListener(item -> {
            /* TO DO: LOGOUT */
            return false;
        });

        // For setting past trip fragment as "Home" in navigation drawer
        this.navController.addOnDestinationChangedListener((navController, navDestination, bundle)
                -> {
            if (navDestination.getId() == R.id.fragment_past_trips) {
                drawerNav.getMenu().getItem(0).setChecked(true);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(this.navController, this.appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout != null && this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}
