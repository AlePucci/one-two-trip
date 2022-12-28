package it.unimib.sal.one_two_trip.ui.main;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.adapter.TripsRecyclerViewAdapter;
import it.unimib.sal.one_two_trip.data.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.util.ErrorMessagesUtil;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;
import it.unimib.sal.one_two_trip.util.Utility;

/**
 * A simple {@link Fragment} subclass that shows the past trips of the user.
 * Use the {@link PastTripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PastTripsFragment extends Fragment {

    private static final String TAG = PastTripsFragment.class.getSimpleName();

    private List<Trip> pastTrips;
    private TripsViewModel tripsViewModel;
    private TripsRecyclerViewAdapter tripsRecyclerViewAdapter;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private Application application;

    public PastTripsFragment() {
    }

    public static PastTripsFragment newInstance() {
        return new PastTripsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.application = requireActivity().getApplication();
        this.sharedPreferencesUtil = new SharedPreferencesUtil(this.application);
        ITripsRepository tripsRepository = ServiceLocator.getInstance()
                .getTripsRepository(this.application);
        if (tripsRepository != null) {
            this.tripsViewModel = new ViewModelProvider(requireActivity(),
                    new TripsViewModelFactory(tripsRepository)).get(TripsViewModel.class);
        } else {
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
        }
        this.pastTrips = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_past_trips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView pastTripsView = view.findViewById(R.id.past_trips_view);
        TextView pastTripsTitle = view.findViewById(R.id.past_trips_title);
        TextView noTripsText = view.findViewById(R.id.no_trips_text);
        ImageView noTripsImage = view.findViewById(R.id.no_trips_image);
        ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        BottomNavigationView bottomNavigationView = requireActivity()
                .findViewById(R.id.bottom_navigation);
        FloatingActionButton fab = requireActivity().findViewById(R.id.fab);

        bottomNavigationView.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);

        this.tripsRecyclerViewAdapter = new TripsRecyclerViewAdapter(pastTrips,
                this.application,
                new TripsRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onTripShare(Trip trip) {
                        if (Utility.isConnected(requireActivity())) {
                            Utility.onTripShare(trip, pastTrips, application);
                        } else {
                            Snackbar.make(view, requireContext()
                                            .getString(R.string.no_internet_error),
                                    Snackbar.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onTripClick(Trip trip) {
                        Snackbar.make(view, trip.getTitle(), Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onButtonClick(Trip trip) {
                        Snackbar.make(view, trip.getTitle(), Snackbar.LENGTH_SHORT).show();
                    }
                });

        pastTripsView.setNestedScrollingEnabled(false);
        pastTripsView.setLayoutManager(layoutManager);
        pastTripsView.setAdapter(tripsRecyclerViewAdapter);

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE);
        }

        progressBar.setVisibility(View.VISIBLE);

        this.tripsViewModel.getTrips(Long.parseLong(lastUpdate)).observeForever(
                result -> {
                    if (result.isSuccess()) {
                        List<Trip> fetchedTrips = ((Result.Success) result).getData().getTripList();

                        // IF THE ARE NO TRIPS, SHOW THE NO TRIPS IMAGE AND TEXT
                        if (fetchedTrips == null || fetchedTrips.isEmpty()) {
                            noTripsText.setText(R.string.no_trips_added);
                            noTripsText.setVisibility(View.VISIBLE);
                            noTripsImage.setVisibility(View.VISIBLE);
                            pastTripsTitle.setVisibility(View.GONE);
                        } else {
                            List<Trip> pastTrips = new ArrayList<>(fetchedTrips);

                            // FILTERS THE TRIPS THAT ARE NOT COMPLETED (PAST TRIPS)
                            for (Iterator<Trip> i = pastTrips.iterator(); i.hasNext(); ) {
                                Trip trip = i.next();
                                if (trip != null && !trip.isCompleted()) i.remove();
                            }

                            // IF THERE ARE NO PAST TRIPS, SHOW THE NO PAST TRIPS IMAGE TEXT
                            if (pastTrips.isEmpty()) {
                                noTripsText.setText(R.string.no_past_trips);
                                noTripsText.setVisibility(View.VISIBLE);
                                noTripsImage.setVisibility(View.VISIBLE);
                                pastTripsTitle.setVisibility(View.GONE);
                            } else {
                                pastTripsTitle.setVisibility(View.VISIBLE);
                                noTripsText.setVisibility(View.GONE);
                                noTripsImage.setVisibility(View.GONE);

                                this.pastTrips.clear();
                                this.pastTrips.addAll(pastTrips);
                                tripsRecyclerViewAdapter.notifyItemRangeChanged(0,
                                        this.pastTrips.size());
                            }
                        }

                        progressBar.setVisibility(View.GONE);
                    } else {
                        ErrorMessagesUtil errorMessagesUtil = new ErrorMessagesUtil(this.application);
                        Snackbar.make(view, errorMessagesUtil.getErrorMessage(((Result.Error) result)
                                .getMessage()), Snackbar.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}
