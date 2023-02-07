package it.unimib.sal.one_two_trip.ui.trip;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static it.unimib.sal.one_two_trip.util.Constants.ACTIVITY_TITLE;
import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.MOVE_TO_ACTIVITY;
import static it.unimib.sal.one_two_trip.util.Constants.MOVING_ACTIVITY_TYPE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_ACTIVITY_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Utility.not;

import android.Manifest;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.adapter.TripRecyclerViewAdapter;
import it.unimib.sal.one_two_trip.data.database.model.Activity;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.database.model.Trip;
import it.unimib.sal.one_two_trip.data.repository.trips.ITripsRepository;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModel;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModelFactory;
import it.unimib.sal.one_two_trip.util.ErrorMessagesUtil;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;

/**
 * Fragment that shows the details of a trip in terms of list of activities and an interactive map.
 */
public class TripFragment extends Fragment implements MenuProvider {

    private final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    private TripsViewModel viewModel;
    private TripRecyclerViewAdapter adapter;
    private Application application;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private MapView mapView;
    private MyLocationNewOverlay mLocationOverlay;
    private ActivityResultLauncher<String[]> multiplePermissionLauncher;
    private Trip trip;
    private List<Activity> activityList;

    public TripFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));

        androidx.fragment.app.FragmentActivity activity = requireActivity();
        this.application = activity.getApplication();
        this.sharedPreferencesUtil = new SharedPreferencesUtil(this.application);
        ITripsRepository tripsRepository = ServiceLocator.getInstance()
                .getTripsRepository(this.application);
        if (tripsRepository != null) {
            this.viewModel = new ViewModelProvider(activity,
                    new TripsViewModelFactory(tripsRepository)).get(TripsViewModel.class);
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
        }
        this.activityList = new ArrayList<>();


        multiplePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                isGranted -> {
                    //TODO: rewrite
                });

        requestPerms();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("AAA", "onViewCreated");
        if (getArguments() == null) {
            return;
        }

        String tripId = getArguments().getString(SELECTED_TRIP_ID);
        boolean moveToActivity = getArguments().getBoolean(MOVE_TO_ACTIVITY);
        getArguments().remove(MOVE_TO_ACTIVITY);
        String activityId = getArguments().getString(SELECTED_ACTIVITY_ID);

        FragmentActivity activity = requireActivity();
        MaterialToolbar toolbar = activity.findViewById(R.id.trip_toolbar);

        mapView = view.findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this.requireContext()),
                mapView);
        mLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(mLocationOverlay);

        //Ask for permissions
        /**/

        ((MenuHost) activity).addMenuProvider(this, getViewLifecycleOwner(),
                Lifecycle.State.RESUMED);

        FloatingActionButton fab = view.findViewById(R.id.trip_fab);

        fab.setOnClickListener(v -> {
            androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(
                    activity, R.style.Widget_App_CustomAlertDialog);
            EditText input = new EditText(activity);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            FrameLayout container = new FrameLayout(activity);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(50, 0, 50, 0);
            input.setLayoutParams(params);
            container.addView(input);

            alert.setTitle(getString(R.string.activity_new_title));
            alert.setMessage(getString(R.string.activity_new_title_descr));
            alert.setView(container);
            alert.setPositiveButton(getString(R.string.activity_new_confirmation_positive),
                    (dialog, which) -> {
                        String title = input.getText().toString().trim();
                        if (!title.isEmpty()) {
                            Bundle bundle = new Bundle();
                            bundle.putString(SELECTED_TRIP_ID, tripId);
                            bundle.putString(ACTIVITY_TITLE, title);
                            Navigation.findNavController(v).navigate(R.id.action_tripFragment_to_activityNewFragment, bundle);
                        }
                    });
            alert.setNegativeButton(getString(R.string.activity_new_confirmation_negative), null);
            alert.show();

        });

        //RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.trip_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);

        if (moveToActivity) {
            getArguments().remove(MOVE_TO_ACTIVITY);

            Bundle bundle = new Bundle();
            bundle.putString(SELECTED_TRIP_ID, tripId);
            bundle.putString(SELECTED_ACTIVITY_ID, activityId);
            Navigation.findNavController(view).navigate(R.id.action_tripFragment_to_activityFragment,
                    bundle);
        }

        ProgressBar progressBar = view.findViewById(R.id.trip_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        this.adapter = new TripRecyclerViewAdapter(this.activityList,
                this.application,
                new TripRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onActivityClick(int position) {
                        String activityId = activityList.get(position).getId();
                        Bundle bundle = new Bundle();
                        bundle.putString(SELECTED_TRIP_ID, tripId);
                        bundle.putString(SELECTED_ACTIVITY_ID, activityId);
                        Navigation.findNavController(view).navigate(
                                R.id.action_tripFragment_to_activityFragment, bundle);
                    }

                    @Override
                    public void onDragClick(int position) {
                        //TODO: drag activity
                        view.findViewById(R.id.item_activity_dragbutton)
                                .performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        Snackbar.make(view, "Drag " + activityList.get(position).getTitle(),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });

        recyclerView.setAdapter(adapter);

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE);
        }

        this.viewModel.getTrips(Long.parseLong(lastUpdate)).observe(
                getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        List<Trip> trips = ((Result.TripSuccess) result).getData().getTripList();

                        for (Trip trip : trips) {
                            if (trip.getId().equals(tripId)) {
                                this.trip = trip;
                                break;
                            }
                        }
                        Log.d("AAA", "trip: " + this.trip.isParticipating());
                        if (!this.trip.isParticipating()) {
                            requireActivity().finish();
                        }

                        // todo check if saved

                        if (this.trip.getActivity() != null
                                && this.trip.getActivity().getActivityList() != null
                                && !this.trip.getActivity().getActivityList().isEmpty()) {
                            List<Activity> activityList = this.trip.getActivity().getActivityList();
                            activityList.sort(Comparator.comparing(Activity::getStart_date));
                            adapter.addData(activityList);
                        }

                        toolbar.setTitle(trip.getTitle());
                        progressBar.setVisibility(View.GONE);

                        Log.d("AAA", "observe");
                        if (isAdded())
                            mapSetup();
                    } else {
                        ErrorMessagesUtil errorMessagesUtil = new ErrorMessagesUtil(this.application);
                        Snackbar.make(view, errorMessagesUtil.getErrorMessage(((Result.Error) result)
                                .getMessage()), Snackbar.LENGTH_SHORT).show();

                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocationOverlay != null) {
            mLocationOverlay.enableMyLocation();
        }
        if (mapView != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            Configuration.getInstance().load(getContext(), prefs);
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationOverlay != null) {
            mLocationOverlay.disableMyLocation();
        }
        if (mapView != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            Configuration.getInstance().save(getContext(), prefs);
            mapView.onPause();
        }
    }

    /**
     * Method to setup the map
     */
    private void mapSetup() {
        Log.d("AAA", "called map setup");
        this.mapView.getOverlays().clear();

        //Markers
        List<GeoPoint> points = new ArrayList<>();
        List<Marker> markers = new ArrayList<>();

        if (this.trip.getActivity() != null && this.trip.getActivity().getActivityList() != null) {
            for (Activity a : this.trip.getActivity().getActivityList()) {
                if (a == null) continue;

                Marker marker = new Marker(this.mapView);
                GeoPoint point = new GeoPoint(a.getLatitude(), a.getLongitude());
                points.add(point);

                if (a.getType().equals(MOVING_ACTIVITY_TYPE_NAME)) {
                    marker.setTitle(a.getTitle() + " (" + getString(R.string.activity_departure) + ")");
                } else {
                    marker.setTitle(a.getTitle());
                }
                marker.setSubDescription(a.getDescription());
                marker.setPosition(point);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                markers.add(marker);

                if (a.getType().equalsIgnoreCase(MOVING_ACTIVITY_TYPE_NAME)) {
                    Marker endMarker = new Marker(this.mapView);
                    GeoPoint endPoint = new GeoPoint(a.getEndLatitude(), a.getEndLongitude());
                    points.add(endPoint);

                    endMarker.setTitle(a.getTitle() + " (" + getString(R.string.activity_arrival) + ")");
                    endMarker.setSubDescription(a.getDescription());
                    endMarker.setPosition(endPoint);
                    endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    markers.add(endMarker);
                }
            }
        }
        Polyline line = new Polyline();
        line.setPoints(points);
        this.mapView.getOverlayManager().add(line);
        this.mapView.invalidate();

        //Add the markers after the line so that the line is in background
        //TODO: order the markers so that they don't overlap
        this.mapView.getOverlays().addAll(markers);

        //Move to location
        IMapController mapController = this.mapView.getController();
        mapController.setZoom(9.5);

        GeoPoint startPoint = new GeoPoint(0.0, 0.0);

        if (this.trip.getActivity() != null && this.trip.getActivity().getActivityList() != null &&
                !this.trip.getActivity().getActivityList().isEmpty()) {
            List<Activity> activityList = new ArrayList<>(this.trip.getActivity().getActivityList());
            activityList.sort(Comparator.comparing(Activity::getStart_date));
            Activity startActivity = activityList.stream()
                    .filter(not(Activity::isCompleted)).findFirst().orElse(null);

            if (startActivity == null) {
                startActivity = activityList.get(0);
            }

            startPoint = new GeoPoint(startActivity.getLatitude(), startActivity.getLongitude());
        }
        mapController.setCenter(startPoint);
    }

    /**
     * Method to request permissions for the map
     */
    private void requestPerms() {
        boolean permissionsStatus =
                Arrays.stream(PERMISSIONS).allMatch(p ->
                        checkSelfPermission(application, p) == PackageManager.PERMISSION_GRANTED);

        if (!permissionsStatus) {
            multiplePermissionLauncher.launch(PERMISSIONS);
        }
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.trip_appbar_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.trip_menu_settings) {
            Bundle bundle = new Bundle();
            bundle.putString(SELECTED_TRIP_ID, this.trip.getId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_tripFragment_to_tripSettingsFragment, bundle);
        }

        return false;
    }
}