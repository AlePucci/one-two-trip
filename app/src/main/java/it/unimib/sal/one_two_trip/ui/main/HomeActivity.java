package it.unimib.sal.one_two_trip.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.UUID;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.database.model.Person;
import it.unimib.sal.one_two_trip.data.database.model.Trip;
import it.unimib.sal.one_two_trip.ui.account.AccountActivity;
import it.unimib.sal.one_two_trip.data.repository.trips.ITripsRepository;
import it.unimib.sal.one_two_trip.data.repository.user.IUserRepository;
import it.unimib.sal.one_two_trip.ui.welcome.UserViewModel;
import it.unimib.sal.one_two_trip.ui.welcome.UserViewModelFactory;
import it.unimib.sal.one_two_trip.ui.welcome.WelcomeActivity;
import it.unimib.sal.one_two_trip.util.ServiceLocator;

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

        ITripsRepository tripsRepository = ServiceLocator.getInstance()
                .getTripsRepository(getApplication());
        TripsViewModel viewModel = null;
        if (tripsRepository != null) {
            viewModel = new ViewModelProvider(this,
                    new TripsViewModelFactory(tripsRepository)).get(TripsViewModel.class);
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
        }

        IUserRepository userRepository = ServiceLocator.getInstance()
                .getUserRepository(getApplication());
        UserViewModel userViewModel = null;
        if (userRepository != null) {
            userViewModel = new ViewModelProvider(this,
                    new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
        }

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
                if(menuItem.getItemId() == R.id.action_account){
                    Intent intent = new Intent(HomeActivity.this, AccountActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        UserViewModel finalUserViewModel = userViewModel;
        TripsViewModel finalViewModel = viewModel;
        drawerNav.getMenu().findItem(R.id.logout).setOnMenuItemClickListener(item -> {
            if (finalUserViewModel != null) {
                finalUserViewModel.logout();
                Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
                // RESET THEME
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
            return false;
        });

        // For setting past trip fragment as "Home" in navigation drawer
        this.navController.addOnDestinationChangedListener((navController, navDestination, bundle)
                -> {
            if (navDestination.getId() == R.id.fragment_past_trips) {
                drawerNav.getMenu().getItem(0).setChecked(true);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(view -> {
            if (finalViewModel != null) {
                androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(
                        this, R.style.Widget_App_CustomAlertDialog);
                EditText input = new EditText(this);
                FrameLayout container = new FrameLayout(this);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(50, 0, 50, 0);
                container.addView(input);
                input.setLayoutParams(params);
                input.setInputType(InputType.TYPE_CLASS_TEXT);

                alert.setTitle(getString(R.string.trip_new_title));
                alert.setMessage(getString(R.string.trip_new_descr));
                alert.setView(container);
                alert.setPositiveButton(getString(R.string.trip_new_positive),
                        (dialog, which) -> {
                            if (finalUserViewModel != null && finalUserViewModel.getLoggedUser() != null) {
                                Person user = finalUserViewModel.getLoggedUser();
                                String title = input.getText().toString().trim();
                                if (!title.isEmpty()) {
                                    Trip trip = new Trip();
                                    trip.setId(UUID.randomUUID().toString());
                                    trip.setTitle(title);
                                    trip.setTripOwner(user.getId());
                                    trip.setParticipating(true);

                                    ArrayList<Person> participants = new ArrayList<>();
                                    participants.add(user);
                                    trip.getParticipant().setPersonList(participants);
                                    finalViewModel.insertTrip(trip);
                                }
                            }
                        });
                alert.setNegativeButton(getString(R.string.trip_new_negative), null);
                alert.show();
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
