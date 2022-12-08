package it.unimib.sal.one_two_trip.ui.main;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.app.Application;
import android.content.Intent;
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

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.adapter.TripsRecyclerViewAdapter;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.model.TripsResponse;
import it.unimib.sal.one_two_trip.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.ui.trip.TripActivity;
import it.unimib.sal.one_two_trip.util.ErrorMessagesUtil;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;

/**
 * A simple {@link Fragment} subclass that shows the coming trips of the user.
 * Use the {@link ComingTripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComingTripsFragment extends Fragment {

    private static final String TAG = ComingTripsFragment.class.getSimpleName();

    private List<Trip> comingTrips;
    private TripsViewModel tripsViewModel;
    private TripsRecyclerViewAdapter tripsRecyclerViewAdapter;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private Application application;

    public ComingTripsFragment() {
    }

    public static ComingTripsFragment newInstance() {
        return new ComingTripsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = requireActivity().getApplication();

        sharedPreferencesUtil = new SharedPreferencesUtil(this.application);
        ITripsRepository tripsRepository = ServiceLocator.getInstance()
                .getTripsRepository(this.application);
        tripsViewModel = new ViewModelProvider(requireActivity(),
                new TripsViewModelFactory(tripsRepository)).get(TripsViewModel.class);
        comingTrips = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_coming_trips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView comingTripsView = view.findViewById(R.id.coming_trips_view);
        TextView comingTripsTitle = view.findViewById(R.id.coming_trips_title);
        TextView noTripsText = view.findViewById(R.id.no_trips_text);
        ImageView noTripsImage = view.findViewById(R.id.no_trips_image);
        ProgressBar progressBar = view.findViewById(R.id.progress_bar);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);

        tripsRecyclerViewAdapter = new TripsRecyclerViewAdapter(comingTrips,
                this.application,
                new TripsRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onTripShare(Trip trip) {
                        Snackbar.make(view, "Share " + trip.getTitle(),
                                Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onTripClick(Trip trip) {
                        Intent intent = new Intent(requireContext(), TripActivity.class);
                        intent.putExtra("tripId", trip.getId());
                        requireContext().startActivity(intent);
                    }

                    @Override
                    public void onButtonClick(Trip trip) {
                        Intent intent = new Intent(requireContext(), TripActivity.class);
                        intent.putExtra("tripId", trip.getId());
                        requireContext().startActivity(intent);
                    }
                });

        comingTripsView.setNestedScrollingEnabled(false);
        comingTripsView.setLayoutManager(layoutManager);
        comingTripsView.setAdapter(tripsRecyclerViewAdapter);

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE);
        }

        progressBar.setVisibility(View.VISIBLE);

        tripsViewModel.getTrips(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        List<Trip> fetchedTrips = ((Result.Success<TripsResponse>) result).getData().getTripList();

                        // IF THE ARE NO TRIPS, SHOW THE NO TRIPS IMAGE AND TEXT
                        if (fetchedTrips == null || fetchedTrips.isEmpty()) {
                            noTripsText.setText(R.string.no_trips_added);
                            noTripsText.setVisibility(View.VISIBLE);
                            noTripsImage.setVisibility(View.VISIBLE);
                            comingTripsTitle.setVisibility(View.GONE);
                        } else {
                            List<Trip> comingTrips = new ArrayList<>(fetchedTrips);

                            // FILTERS THE TRIPS THAT ARE NOT COMPLETED (COMING TRIPS)
                            for (Iterator<Trip> i = comingTrips.iterator(); i.hasNext(); ) {
                                Trip trip = i.next();
                                if (trip != null && trip.isCompleted()) i.remove();
                            }

                            // IF THERE ARE NO COMING TRIPS, SHOW THE NO COMING TRIPS IMAGE TEXT
                            if (comingTrips.isEmpty()) {
                                noTripsText.setText(R.string.no_past_trips);
                                noTripsText.setVisibility(View.VISIBLE);
                                noTripsImage.setVisibility(View.VISIBLE);
                                comingTripsTitle.setVisibility(View.GONE);
                            } else {
                                int comingTripsCount = comingTrips.size();
                                if (comingTripsCount == 1) {
                                    comingTripsTitle.setText(R.string.coming_trips_title_single);
                                } else {
                                    comingTripsTitle.setText(String
                                            .format(getString(R.string.coming_trips_title_multiple),
                                                    comingTripsCount));
                                }

                                comingTripsTitle.setVisibility(View.VISIBLE);
                                noTripsText.setVisibility(View.GONE);
                                noTripsImage.setVisibility(View.GONE);

                                int initialSize = this.comingTrips.size();
                                this.comingTrips.clear();
                                this.comingTrips.addAll(comingTrips);
                                tripsRecyclerViewAdapter.notifyItemRangeInserted(initialSize,
                                        this.comingTrips.size());
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
