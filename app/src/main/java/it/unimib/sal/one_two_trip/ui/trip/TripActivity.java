package it.unimib.sal.one_two_trip.ui.trip;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_POS;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.app.Application;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModel;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModelFactory;
import it.unimib.sal.one_two_trip.util.ErrorMessagesUtil;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;

public class TripActivity extends AppCompatActivity {
    private Application application;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private TripsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        Application application = getApplication();
        sharedPreferencesUtil = new SharedPreferencesUtil(application);
        ITripsRepository tripsRepository = ServiceLocator.getInstance()
                .getTripsRepository(application);
        viewModel = new ViewModelProvider(this,
                new TripsViewModelFactory(tripsRepository)).get(TripsViewModel.class);


        //Toolbar
        Toolbar toolbar = findViewById(R.id.trip_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());


        long id = getIntent().getLongExtra(SELECTED_TRIP_ID, 0);

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE);
        }

        viewModel.getTrips(Long.parseLong(lastUpdate)).observe(this, result -> {
            if(result.isSuccess()) {
                List<Trip> trips = ((Result.Success) result).getData().getTripList();

                int pos = 0;
                for(Trip t: trips) {
                    if(t.getId() == id) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(SELECTED_TRIP_POS, pos);

                        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().
                                findFragmentById(R.id.trip_nav_host);

                        navHostFragment.getNavController().setGraph(R.navigation.trip_nav_graph, bundle);
                        break;
                    }
                    pos += 1;
                }
            } else {
                ErrorMessagesUtil errorMessagesUtil = new ErrorMessagesUtil(this.application);
                Snackbar.make(toolbar, errorMessagesUtil.getErrorMessage(((Result.Error) result)
                        .getMessage()), Snackbar.LENGTH_SHORT).show();

                finish();
            }
        });
    }
}