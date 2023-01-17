package it.unimib.sal.one_two_trip.ui.trip;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_ACTIVITY_POS;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_POS;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.adapter.TripRecyclerViewAdapter;
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModel;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModelFactory;
import it.unimib.sal.one_two_trip.util.ErrorMessagesUtil;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;

public class TripFragment extends Fragment implements MenuProvider {

    private ActivityResultLauncher<String[]> multiplePermissionLauncher;

    private Application application;
    TripsViewModel viewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;
    TripRecyclerViewAdapter adapter;
    private Trip trip;


    private final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private MapView mapView;

    private List<Activity> activityList;
    private String title;


    public TripFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = requireActivity().getApplication();

        sharedPreferencesUtil = new SharedPreferencesUtil(application);

        ITripsRepository tripsRepository = ServiceLocator.getInstance()
                .getTripsRepository(application);
        viewModel = new ViewModelProvider(requireActivity(),
                new TripsViewModelFactory(tripsRepository)).get(TripsViewModel.class);

        activityList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Ask for permissions
        ActivityResultContracts.RequestMultiplePermissions multiplePermissionsContract = new ActivityResultContracts.RequestMultiplePermissions();

        multiplePermissionLauncher = registerForActivityResult(multiplePermissionsContract, isGranted -> {
            if (!isGranted.containsValue(false)) {
                loadMap();
            }
        });

        //Toolbar
        Toolbar toolbar = requireActivity().findViewById(R.id.trip_toolbar);

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        mapSetup();

        //FAB
        FloatingActionButton fab = view.findViewById(R.id.trip_fab);
        //TODO: add activity
        fab.setOnClickListener(v -> Snackbar.make(v, "Add Activity", Snackbar.LENGTH_SHORT).show());

        //RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.trip_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(linearLayoutManager);
         adapter = new TripRecyclerViewAdapter(activityList, new TripRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onActivityClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putInt(SELECTED_ACTIVITY_POS, position);
                bundle.putInt(SELECTED_TRIP_POS, getArguments().getInt(SELECTED_TRIP_POS));
                Navigation.findNavController(requireView()).navigate(R.id.action_tripFragment_to_activityFragment, bundle);
            }

            @Override
            public void onDragClick(int position) {
                //TODO: drag activity
                requireView().findViewById(R.id.item_activity_dragbutton).performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                Snackbar.make(requireView(), "Drag " + activityList.get(position).getTitle(), Snackbar.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE);
        }

        viewModel.getTrips(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> {
            ProgressBar progressBar = requireView().findViewById(R.id.trip_progressbar);

            if (result.isSuccess()) {
                List<Trip> trips = ((Result.Success) result).getData().getTripList();
                int tripPos = getArguments().getInt(SELECTED_TRIP_POS);

                trip = trips.get(tripPos);

                title = trip.getTitle();
                toolbar.setTitle(title);

                activityList.addAll(trip.getActivity().activityList);
                adapter.addData(trip.getActivity().activityList);

                progressBar.setVisibility(View.GONE);
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
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    private void mapSetup() {
        //Load the map
        loadMap();

        //Set the controls
        mapView = requireView().findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(false);

        //TODO: draw the travel route
        //TODO: zoom in the trip next activity

        //Move to location
        IMapController mapController = mapView.getController();
        mapController.setZoom(9.5);
        GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
        mapController.setCenter(startPoint);

        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getContext()), mapView);
        mLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(mLocationOverlay);

    }

    private void loadMap() {
        boolean permissionsStatus = true;

        for (String p : PERMISSIONS) {
            permissionsStatus &= ActivityCompat.checkSelfPermission(requireContext(), p) == PackageManager.PERMISSION_GRANTED;
        }

        if (permissionsStatus) {
            //Get the Map
            Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()));
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
        if (menuItem.getItemId() == R.id.trip_menu_rename) {
            //TODO: rename trip
            //...

            viewModel.updateTrip(trip);
            return true;
        } else if (menuItem.getItemId() == R.id.trip_menu_delete) {
            AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
            alert.setTitle(getString(R.string.trip_delete_confirmation_title));
            alert.setMessage(getString(R.string.trip_delete_confirmation));
            alert.setPositiveButton(getString(R.string.trip_delete_confirmation_positive),
                    (dialog, whichButton) -> {
                        viewModel.deleteTrip(trip);
                        getActivity().finish();
                    });

            alert.setNegativeButton(getString(R.string.trip_delete_confirmation_negative), null);
            alert.show();
            return true;
        }

        return false;
    }
}