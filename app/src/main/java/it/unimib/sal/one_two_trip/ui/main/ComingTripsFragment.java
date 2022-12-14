package it.unimib.sal.one_two_trip.ui.main;

import static it.unimib.sal.one_two_trip.util.Constants.KEY_COMPLETED;
import static it.unimib.sal.one_two_trip.util.Constants.KEY_LOCATION;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.adapter.TripsRecyclerViewAdapter;
import it.unimib.sal.one_two_trip.data.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.util.ErrorMessagesUtil;
import it.unimib.sal.one_two_trip.util.PhotoWorker;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;
import it.unimib.sal.one_two_trip.util.Utility;

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
    private SwipeRefreshLayout swipeRefreshLayout;

    public ComingTripsFragment() {
    }

    @NonNull
    public static ComingTripsFragment newInstance() {
        return new ComingTripsFragment();
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
        this.comingTrips = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_coming_trips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView comingTripsView = view.findViewById(R.id.coming_trips_view);
        TextView noTripsText = view.findViewById(R.id.no_trips_text);
        ImageView noTripsImage = view.findViewById(R.id.no_trips_image);
        ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        BottomNavigationView bottomNavigationView = requireActivity()
                .findViewById(R.id.bottom_navigation);
        FloatingActionButton fab = requireActivity().findViewById(R.id.fab);
        this.swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        bottomNavigationView.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE);
        }

        final String finalLastUpdate = lastUpdate;
        this.swipeRefreshLayout.setOnRefreshListener(() -> {
            this.refresh(finalLastUpdate);
            this.swipeRefreshLayout.setRefreshing(false);
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);

        this.tripsRecyclerViewAdapter = new TripsRecyclerViewAdapter(comingTrips,
                this.application,
                false,
                new TripsRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onTripShare(Trip trip) {
                        if (Utility.isConnected(requireActivity())) {
                            AtomicBoolean oneTime = new AtomicBoolean(false);
                            boolean isCompleted = trip.isCompleted();
                            String location = Utility.getRandomTripLocation(trip, comingTrips,
                                    application, view);

                            if (location == null || location.isEmpty()) {
                                return;
                            }

                            // WORKER INITIALIZATION
                            Data inputData = new Data.Builder()
                                    .putString(KEY_LOCATION, location)
                                    .putBoolean(KEY_COMPLETED, isCompleted)
                                    .build();

                            Constraints constraints = new Constraints.Builder()
                                    .setRequiresStorageNotLow(true)
                                    .build();

                            OneTimeWorkRequest photoRequest =
                                    new OneTimeWorkRequest.Builder(PhotoWorker.class)
                                            .setInputData(inputData)
                                            .setConstraints(constraints)
                                            .build();
                            UUID requestId = photoRequest.getId();
                            WorkManager.getInstance(application).enqueue(photoRequest);

                            WorkManager.getInstance(application).getWorkInfoByIdLiveData(requestId)
                                    .observe(getViewLifecycleOwner(), workInfo -> {
                                        if (!oneTime.get()
                                                && workInfo.getState() == WorkInfo.State.FAILED) {
                                            Snackbar.make(view,
                                                            R.string.share_trip_error,
                                                            Snackbar.LENGTH_SHORT)
                                                    .show();
                                            oneTime.set(true);
                                        }
                                    });
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

        comingTripsView.setLayoutManager(layoutManager);
        comingTripsView.setAdapter(tripsRecyclerViewAdapter);

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
                            } else {
                                noTripsText.setVisibility(View.GONE);
                                noTripsImage.setVisibility(View.GONE);

                                this.comingTrips.clear();
                                this.comingTrips.addAll(comingTrips);
                                this.tripsRecyclerViewAdapter.notifyItemRangeChanged(0,
                                        this.comingTrips.size() + 1);
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

    private void refresh(String lastUpdate) {
        this.tripsViewModel.fetchTrips(Long.parseLong(lastUpdate));
        this.swipeRefreshLayout.setRefreshing(false);
    }
}
