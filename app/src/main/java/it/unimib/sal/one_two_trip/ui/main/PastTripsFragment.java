package it.unimib.sal.one_two_trip.ui.main;

import static it.unimib.sal.one_two_trip.util.Constants.KEY_COMPLETED;
import static it.unimib.sal.one_two_trip.util.Constants.KEY_LOCATION;
import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.MOVE_TO_ACTIVITY;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_ACTIVITY_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_ID;
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
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.adapter.TripsRecyclerViewAdapter;
import it.unimib.sal.one_two_trip.data.database.model.Activity;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.database.model.Trip;
import it.unimib.sal.one_two_trip.data.repository.trips.ITripsRepository;
import it.unimib.sal.one_two_trip.ui.trip.TripActivity;
import it.unimib.sal.one_two_trip.util.ErrorMessagesUtil;
import it.unimib.sal.one_two_trip.util.PhotoWorker;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;
import it.unimib.sal.one_two_trip.util.Utility;

/**
 * Fragment that shows the list of past trips. It is used by the {@link HomeActivity}.
 */
public class PastTripsFragment extends Fragment {

    private static final String TAG = PastTripsFragment.class.getSimpleName();

    private List<Trip> pastTrips;
    private TripsViewModel tripsViewModel;
    private TripsRecyclerViewAdapter tripsRecyclerViewAdapter;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private Application application;
    private SwipeRefreshLayout swipeRefreshLayout;

    public PastTripsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        androidx.fragment.app.FragmentActivity activity = requireActivity();
        this.application = activity.getApplication();
        this.sharedPreferencesUtil = new SharedPreferencesUtil(this.application);
        ITripsRepository tripsRepository = ServiceLocator.getInstance()
                .getTripsRepository(this.application);
        if (tripsRepository != null) {
            this.tripsViewModel = new ViewModelProvider(activity,
                    new TripsViewModelFactory(tripsRepository)).get(TripsViewModel.class);
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
        }
        this.pastTrips = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_past_trips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        androidx.fragment.app.FragmentActivity activity = requireActivity();
        RecyclerView pastTripsView = view.findViewById(R.id.past_trips_view);
        TextView noTripsText = view.findViewById(R.id.no_trips_text_pasttrips);
        ImageView noTripsImage = view.findViewById(R.id.no_trips_image_pasttrips);
        ProgressBar progressBar = view.findViewById(R.id.progress_bar_pasttrips);
        BottomNavigationView bottomNavigationView = activity
                .findViewById(R.id.bottom_navigation);
        FloatingActionButton fab = activity.findViewById(R.id.fab);
        this.swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_pasttrips);

        bottomNavigationView.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE);
        }

        this.swipeRefreshLayout.setOnRefreshListener(this::refresh);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL, false);

        this.tripsRecyclerViewAdapter = new TripsRecyclerViewAdapter(pastTrips,
                this.application,
                true,
                new TripsRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onTripShare(Trip trip) {
                        if (Utility.isConnected(activity)) {
                            AtomicBoolean oneTime = new AtomicBoolean(false);
                            boolean isCompleted = trip.isCompleted();
                            String location = Utility.getRandomTripLocation(trip, pastTrips,
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
                            Snackbar.make(view, activity
                                            .getString(R.string.no_internet_error),
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onTripClick(Trip trip) {
                        Intent intent = new Intent(activity, TripActivity.class);
                        intent.putExtra(SELECTED_TRIP_ID, trip.getId());
                        startActivity(intent);
                    }

                    @Override
                    public void onButtonClick(Trip trip) {
                        Intent intent = new Intent(activity, TripActivity.class);
                        intent.putExtra(SELECTED_TRIP_ID, trip.getId());
                        startActivity(intent);
                    }

                    @Override
                    public void onAttachmentsClick(Trip trip, Activity activity) {
                        // TODO: implement this
                        Snackbar.make(view, activity.getAttachment().toString(),
                                Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onActivityClick(Trip trip, Activity mActivity) {
                        Intent intent = new Intent(activity, TripActivity.class);
                        intent.putExtra(SELECTED_TRIP_ID, trip.getId());
                        intent.putExtra(MOVE_TO_ACTIVITY, true);
                        intent.putExtra(SELECTED_ACTIVITY_ID, mActivity.getId());
                        startActivity(intent);
                    }
                });

        pastTripsView.setLayoutManager(layoutManager);
        pastTripsView.setAdapter(tripsRecyclerViewAdapter);

        progressBar.setVisibility(View.VISIBLE);

        if (this.tripsViewModel == null) return;

        this.tripsViewModel.getTrips(Long.parseLong(lastUpdate)).observe(
                getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        List<Trip> fetchedTrips = ((Result.TripSuccess) result).getData().getTripList();

                        // IF THE ARE NO TRIPS, SHOW THE NO TRIPS IMAGE AND TEXT
                        if (fetchedTrips == null || fetchedTrips.isEmpty()) {
                            int previousSize = this.pastTrips.size() + 1;
                            this.pastTrips.clear();
                            this.tripsRecyclerViewAdapter.notifyItemRangeRemoved(0,
                                    previousSize);
                            noTripsText.setText(R.string.no_trips_added);
                            noTripsText.setVisibility(View.VISIBLE);
                            noTripsImage.setVisibility(View.VISIBLE);

                        } else {
                            List<Trip> pastTrips = new ArrayList<>(fetchedTrips);

                            // FILTERS THE TRIPS THAT ARE NOT COMPLETED (PAST TRIPS)
                            pastTrips.removeIf(trip -> trip != null && !trip.isCompleted());

                            // IF THERE ARE NO PAST TRIPS, SHOW THE NO PAST TRIPS IMAGE TEXT
                            int previousSize = this.pastTrips.size() + 1;
                            this.pastTrips.clear();
                            if (pastTrips.isEmpty()) {
                                this.tripsRecyclerViewAdapter.notifyItemRangeRemoved(0,
                                        previousSize);
                                noTripsText.setText(R.string.no_past_trips);
                                noTripsText.setVisibility(View.VISIBLE);
                                noTripsImage.setVisibility(View.VISIBLE);

                            } else {
                                noTripsText.setVisibility(View.GONE);
                                noTripsImage.setVisibility(View.GONE);

                                pastTrips.sort(Comparator.comparing(Trip::getStart_date).reversed());
                                this.pastTrips.addAll(pastTrips);
                                this.tripsRecyclerViewAdapter.notifyItemRangeChanged(0,
                                        previousSize);
                            }
                        }
                    } else {
                        ErrorMessagesUtil errorMessagesUtil = new ErrorMessagesUtil(this.application);
                        Snackbar.make(view, errorMessagesUtil.getErrorMessage(((Result.Error) result)
                                .getMessage()), Snackbar.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.swipeRefreshLayout != null) {
            this.swipeRefreshLayout.setRefreshing(false);
            this.swipeRefreshLayout.destroyDrawingCache();
            this.swipeRefreshLayout.clearAnimation();
        }
    }

    /**
     * Forces the refresh of the trips and stops the refresh animation.
     */
    private void refresh() {
        this.tripsViewModel.refreshTrips();
        this.swipeRefreshLayout.setRefreshing(false);
    }
}
