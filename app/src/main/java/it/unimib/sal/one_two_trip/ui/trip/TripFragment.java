package it.unimib.sal.one_two_trip.ui.trip;

import static it.unimib.sal.one_two_trip.util.Constants.ACTIVITY_TITLE;
import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.MOVE_TO_ACTIVITY;
import static it.unimib.sal.one_two_trip.util.Constants.MOVING_ACTIVITY_TYPE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_ACTIVITY_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
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
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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
import it.unimib.sal.one_two_trip.data.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModel;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModelFactory;
import it.unimib.sal.one_two_trip.util.ErrorMessagesUtil;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;
import it.unimib.sal.one_two_trip.util.Utility;

public class TripFragment extends Fragment implements MenuProvider {

    private final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
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

        ActivityResultContracts.RequestMultiplePermissions multiplePermissionsContract =
                new ActivityResultContracts.RequestMultiplePermissions();

        multiplePermissionLauncher = registerForActivityResult(multiplePermissionsContract,
                isGranted -> {
                    if (!isGranted.containsValue(false)) {
                        //loadMap();
                    }
                });

        loadMap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

        long tripId = getArguments().getLong(SELECTED_TRIP_ID);
        boolean moveToActivity = getArguments().getBoolean(MOVE_TO_ACTIVITY);
        getArguments().remove(MOVE_TO_ACTIVITY);
        long activityId = getArguments().getLong(SELECTED_ACTIVITY_ID);

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
        //TODO: add activity
        fab.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
            EditText input = new EditText(requireContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setHint(R.string.activity_new_title_hint);

            alert.setTitle(getString(R.string.activity_new_title));
            alert.setMessage(getString(R.string.activity_new_title_descr));
            alert.setView(input);
            alert.setPositiveButton(getString(R.string.activity_new_confirmation_positive),
                    (dialog, which) -> {
                        String title = input.getText().toString().trim();
                        if (!title.isEmpty()) {
                            Bundle bundle = new Bundle();
                            bundle.putLong(SELECTED_TRIP_ID, tripId);
                            bundle.putString(ACTIVITY_TITLE, title);
                            Navigation.findNavController(v).navigate(R.id.action_tripFragment_to_activityNewFragment, bundle);
                        }
                    });
            alert.setNegativeButton(getString(R.string.activity_new_confirmation_negative), null);
            alert.show();

        });

        //RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.trip_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        if (moveToActivity) {
            getArguments().remove(MOVE_TO_ACTIVITY);

            Bundle bundle = new Bundle();
            bundle.putLong(SELECTED_TRIP_ID, tripId);
            bundle.putLong(SELECTED_ACTIVITY_ID, activityId);
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
                        long activityId = activityList.get(position).getId();
                        Bundle bundle = new Bundle();
                        bundle.putLong(SELECTED_TRIP_ID, tripId);
                        bundle.putLong(SELECTED_ACTIVITY_ID, activityId);
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
                        List<Trip> trips = ((Result.Success) result).getData().getTripList();

                        for (Trip trip : trips) {
                            if (trip.getId() == tripId) {
                                this.trip = trip;
                                break;
                            }
                        }

                        if (this.trip == null) {
                            return;
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

                        if (isVisible())
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
            mapView.onResume();
        }

        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        //Configuration.getInstance().load(requireContext(), prefs);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationOverlay != null) {
            mLocationOverlay.disableMyLocation();
        }
        if (mapView != null) {
            mapView.onPause();
        }

        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        //Configuration.getInstance().save(requireContext(), prefs);
    }

    private void mapSetup() {
        Log.d("AAA", "called map setup");
        mapView.getOverlays().clear();

        //Markers
        List<GeoPoint> points = new ArrayList<>();
        List<Marker> markers = new ArrayList<>();
        for (Activity a : trip.getActivity().getActivityList()) {
            Marker marker = new Marker(mapView);
            GeoPoint point = new GeoPoint(a.getLatitude(), a.getLongitude());
            points.add(point);

            marker.setTitle(a.getTitle());
            marker.setSubDescription(a.getDescription());
            marker.setPosition(point);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            markers.add(marker);

            if (a.getType().equals(MOVING_ACTIVITY_TYPE_NAME)) {
                Marker endMarker = new Marker(mapView);
                GeoPoint endPoint = new GeoPoint(a.getEndLatitude(), a.getEndLongitude());
                points.add(endPoint);

                endMarker.setTitle(a.getTitle());
                endMarker.setSubDescription(a.getDescription());
                endMarker.setPosition(endPoint);
                endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                markers.add(endMarker);
            }
        }
        Polyline line = new Polyline();
        line.setPoints(points);
        mapView.getOverlayManager().add(line);
        mapView.invalidate();

        //Add the markers after the line so that the line is in background
        //TODO: order the markers so that they don't overlap
        mapView.getOverlays().addAll(markers);

        //Move to location
        IMapController mapController = mapView.getController();
        mapController.setZoom(9.5);

        GeoPoint startPoint = new GeoPoint(0.0, 0.0);

        if (!trip.getActivity().getActivityList().isEmpty()) {
            Activity startActivity = trip.getActivity().getActivityList().stream().filter(Activity::isCompleted).findFirst().orElse(null);
            if (startActivity == null) {
                startActivity = trip.getActivity().getActivityList().get(0);
            }

            startPoint = new GeoPoint(startActivity.getLatitude(), startActivity.getLongitude());
        }
        mapController.setCenter(startPoint);
    }

    private void loadMap() {
        boolean permissionsStatus =
                Arrays.stream(PERMISSIONS).allMatch(p ->
                        ActivityCompat.checkSelfPermission(application, p) == PackageManager.PERMISSION_GRANTED);

        if (permissionsStatus) {
            Configuration.getInstance().load(application,
                    PreferenceManager.getDefaultSharedPreferences(application));
        } else {
            multiplePermissionLauncher.launch(PERMISSIONS);
        }
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.trip_appbar_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        Context context = requireContext();
        if (menuItem.getItemId() == R.id.trip_menu_rename) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            String oldTitle = this.trip.getTitle();
            EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setHint(oldTitle);

            alert.setTitle(getString(R.string.trip_title_change_title));
            alert.setMessage(getString(R.string.trip_title_change));
            alert.setView(input);
            alert.setPositiveButton(getString(R.string.trip_title_change_positive),
                    (dialog, which) -> {
                        String newTitle = input.getText().toString().trim();
                        if (!newTitle.isEmpty() && !newTitle.equals(oldTitle)) {
                            this.trip.setTitle(newTitle);
                            this.viewModel.updateTrip(this.trip);
                        }
                    });
            alert.setNegativeButton(getString(R.string.trip_title_change_negative), null);
            alert.show();

            return true;
        } else if (menuItem.getItemId() == R.id.trip_menu_delete) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(getString(R.string.trip_delete_confirmation_title));
            alert.setMessage(getString(R.string.trip_delete_confirmation));
            alert.setPositiveButton(getString(R.string.trip_delete_confirmation_positive),
                    (dialog, whichButton) -> {
                        this.viewModel.deleteTrip(this.trip);
                        Utility.deleteNotifications(this.trip, this.application);
                        for (Activity a : this.trip.getActivity().getActivityList()) {
                            Utility.deleteNotifications(a, this.application, this.trip.getId());
                        }
                        requireActivity().onBackPressed();
                    });

            alert.setNegativeButton(getString(R.string.trip_delete_confirmation_negative),
                    null);
            alert.show();
            return true;
        } else if (menuItem.getItemId() == R.id.trip_menu_settings) {
            Bundle bundle = new Bundle();
            bundle.putLong(SELECTED_TRIP_ID, this.trip.getId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_tripFragment_to_tripSettingsFragment, bundle);
        }

        return false;
    }
}