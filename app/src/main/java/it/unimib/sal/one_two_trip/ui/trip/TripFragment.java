package it.unimib.sal.one_two_trip.ui.trip;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_DRAGGING;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_SETTLING;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import it.unimib.sal.one_two_trip.BuildConfig;
import it.unimib.sal.one_two_trip.R;

public class TripFragment extends Fragment {

    private ActivityResultLauncher<String[]> multiplePermissionLauncher;

    private final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private MapView mapView;

    private int fabDeltaDp = 0;

    public TripFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        //Map setup
        loadMap();
        mapView = view.findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(false);
        //TODO: draw the travel route
        //TODO: zoom in the trip next activity
        IMapController mapController = mapView.getController();
        mapController.setZoom(9.5);
        GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
        mapController.setCenter(startPoint);

        //Bottom Sheet
        BottomSheetBehavior.from(view.findViewById(R.id.trip_sheet)).addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                ExtendedFloatingActionButton fab = view.findViewById(R.id.trip_extended_fab);

                if(newState == STATE_COLLAPSED) {
                    final Animation animation = new TranslateAnimation(0,0, fabDeltaDp, 0);
                    animation.setDuration(500);
                    animation.setFillAfter(true);
                    fab.startAnimation(animation);

                    fabDeltaDp = 0;
                } else if(newState == STATE_SETTLING) {
                    final int toDelta = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
                    final Animation animation = new TranslateAnimation(0,0,fabDeltaDp, toDelta);
                    animation.setDuration(500);
                    animation.setFillAfter(true);
                    fab.startAnimation(animation);

                    fabDeltaDp = toDelta;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
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

    private void loadMap() {
        boolean permissionsStatus = true;

        for(String p: PERMISSIONS) {
            permissionsStatus &= ActivityCompat.checkSelfPermission(getContext(), p) == PackageManager.PERMISSION_GRANTED;
        }

        if(permissionsStatus) {
            //Get the Map
            Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
            Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        } else {
            multiplePermissionLauncher.launch(PERMISSIONS);
        }
    }
}