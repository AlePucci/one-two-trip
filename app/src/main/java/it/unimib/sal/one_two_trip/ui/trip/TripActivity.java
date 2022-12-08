package it.unimib.sal.one_two_trip.ui.trip;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.app.Application;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.model.TripResponse;
import it.unimib.sal.one_two_trip.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModel;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModelFactory;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;

public class TripActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        Application application = getApplication();
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);
        ITripsRepository tripsRepository = ServiceLocator.getInstance()
                .getTripsRepository(application);
        TripViewModel viewModel = new ViewModelProvider(this,
                new TripViewModelFactory(tripsRepository)).get(TripViewModel.class);


        long id = getIntent().getLongExtra(SELECTED_TRIP_ID, 0);
        viewModel.setId(id);

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE);
        }

        //Toolbar
        Toolbar toolbar = findViewById(R.id.trip_toolbar);

        viewModel.getTrip(Long.parseLong(lastUpdate)).observe(this, result -> {
            if(result.isSuccess()) {
                Trip fetchedTrip = ((Result.Success<TripResponse>) result).getData().getTrip();

                toolbar.setTitle(fetchedTrip.getTitle());
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }
}