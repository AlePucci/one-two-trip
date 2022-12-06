package it.unimib.sal.one_two_trip.ui.trip;

import static it.unimib.sal.one_two_trip.util.TemporaryTrips.trips;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.TripViewModel;
import it.unimib.sal.one_two_trip.model.Trip;

public class TripActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        TripViewModel viewModel = new ViewModelProvider(this).get(TripViewModel.class);
        String id = getIntent().getStringExtra("tripId");
        for(Trip t: trips) {
            if(t.getId().equals(id)) {
                viewModel.setTrip(t);
                break;
            }
        }



        //Toolbar
        Toolbar toolbar = findViewById(R.id.trip_toolbar);
        toolbar.setTitle(viewModel.getTrip().getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
    }
}