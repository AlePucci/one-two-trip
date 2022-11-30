package it.unimib.sal.one_two_trip.ui.main;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import it.unimib.sal.one_two_trip.R;

/**
 * The main activity of the app that a user sees after logging in.
 * It contains a {@link BottomNavigationView} that allows the user to navigate between the
 * {@link ComingTripsFragment} that shows the coming trips of the user and the
 * {@link PastTripsFragment} that shows the past trips of the user.
 */
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MaterialToolbar toolbar = findViewById(R.id.top_appbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().
                findFragmentById(R.id.nav_host_fragment);

        navController = navHostFragment.getNavController();

        NavigationView topNav = findViewById(R.id.top_navigation);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        appBarConfiguration = new AppBarConfiguration.
                Builder(R.id.fragment_coming_trips, R.id.fragment_past_trips).setOpenableLayout(drawerLayout).build();

        // For the Toolbar
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // For the Toolbar Menu
        NavigationUI.setupWithNavController(topNav, navController);

        // For the BottomNavigationView
        NavigationUI.setupWithNavController(bottomNav, navController);

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
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration);
    }




}
