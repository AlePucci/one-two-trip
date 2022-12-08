package it.unimib.sal.one_two_trip.ui.trip;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_DRAGGING;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.adapter.TripRecyclerViewAdapter;
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.model.TripResponse;
import it.unimib.sal.one_two_trip.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;

public class TripFragment extends Fragment {

    private ActivityResultLauncher<String[]> multiplePermissionLauncher;

    private Application application;
    private TripViewModel viewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;


    private final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    private MapView mapView;

    private int bottomSheetLastDelta = 0;

    private List<Activity> activityList;

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
                new TripViewModelFactory(tripsRepository)).get(TripViewModel.class);

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
            if(!isGranted.containsValue(false)) {
                loadMap();
            }
        });

        mapSetup();

        bottomSheetSetup();

        //RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.trip_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        TripRecyclerViewAdapter adapter = new TripRecyclerViewAdapter(activityList);
        recyclerView.setAdapter(adapter);

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE);
        }

        viewModel.getTrip(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> {
            if(result.isSuccess()) {
                Trip fetchedTrip = ((Result.Success<TripResponse>) result).getData().getTrip();

                int initialSize = activityList.size();
                activityList.clear();
                activityList.addAll(fetchedTrip.getActivity().activityList);
                adapter.notifyItemRangeInserted(initialSize, activityList.size());
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

    }

    private void loadMap() {
        boolean permissionsStatus = true;

        for(String p: PERMISSIONS) {
            permissionsStatus &= ActivityCompat.checkSelfPermission(requireContext(), p) == PackageManager.PERMISSION_GRANTED;
        }

        if(permissionsStatus) {
            //Get the Map
            Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()));
        } else {
            multiplePermissionLauncher.launch(PERMISSIONS);
        }
    }

    private void bottomSheetSetup() {
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(requireView().findViewById(R.id.trip_sheet));
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                ExtendedFloatingActionButton fab = requireView().findViewById(R.id.trip_extended_fab);
                final int delta = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());

                if(newState == STATE_COLLAPSED) {
                    final Animation animation = new TranslateAnimation(0,0, delta, 0);
                    animation.setDuration(500);
                    animation.setFillAfter(true);
                    fab.startAnimation(animation);

                    bottomSheetLastDelta = 0;
                } else if(newState == STATE_DRAGGING) {
                    final Animation animation = new TranslateAnimation(0,0,bottomSheetLastDelta, delta);
                    animation.setDuration(500);
                    animation.setFillAfter(true);
                    fab.startAnimation(animation);

                    bottomSheetLastDelta = delta;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

    }
}