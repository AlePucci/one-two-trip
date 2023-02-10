package it.unimib.sal.one_two_trip.ui.trip.activity;

import static it.unimib.sal.one_two_trip.util.Constants.ENDLATITUDE;
import static it.unimib.sal.one_two_trip.util.Constants.ENDLOCATION;
import static it.unimib.sal.one_two_trip.util.Constants.ENDLONGITUDE;
import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.LATITUDE;
import static it.unimib.sal.one_two_trip.util.Constants.LOCATION;
import static it.unimib.sal.one_two_trip.util.Constants.LONGITUDE;
import static it.unimib.sal.one_two_trip.util.Constants.MOVING_ACTIVITY_TYPE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.database.model.Activity;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.database.model.Trip;
import it.unimib.sal.one_two_trip.data.repository.trips.ITripsRepository;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModel;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModelFactory;
import it.unimib.sal.one_two_trip.util.ErrorMessagesUtil;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;
import it.unimib.sal.one_two_trip.util.geocoding.GeocodingUtility;
import it.unimib.sal.one_two_trip.util.geocoding.GeocodingUtilityCallback;

/**
 * Fragment that enables the user to edit the location(s) of an activity.
 */
public class ActivityLocationEditFragment extends Fragment {

    private Application application;
    private TripsViewModel viewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private Trip trip;
    private Activity activity;

    public ActivityLocationEditFragment() {
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
            this.viewModel = new ViewModelProvider(activity,
                    new TripsViewModelFactory(tripsRepository)).get(TripsViewModel.class);
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity_location_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getParentFragment() == null || getParentFragment().getParentFragment() == null) {
            return;
        }

        ActivityFragment parentFragment = (ActivityFragment) getParentFragment().getParentFragment();
        String tripId = parentFragment.getTripId();
        String activityId = parentFragment.getActivityId();

        TextInputLayout loc1 = view.findViewById(R.id.activity_where1_edit);
        EditText loc1text = view.findViewById(R.id.activity_where_edittext1);
        TextInputLayout loc2 = view.findViewById(R.id.activity_where2_edit);
        EditText loc2text = view.findViewById(R.id.activity_where_edittext2);
        MaterialButton confirmButton = view.findViewById(R.id.activity_where_confirm);
        ImageView arrow = view.findViewById(R.id.activity_where_arrow_edit);
        HashMap<String, Object> map = new HashMap<>();

        //Confirm Edit
        confirmButton.setOnClickListener(view1 -> {
            String location1 = null;
            String location2 = null;

            GeocodingUtility endUtility = new GeocodingUtility(this.application);
            endUtility.setGeocodingUtilityCallback(new GeocodingUtilityCallback() {
                @Override
                public void onGeocodingSuccess(String lat, String lon) {
                    activity.setEndLatitude(Double.parseDouble(lat));
                    activity.setEndLongitude(Double.parseDouble(lon));
                    map.put(ENDLATITUDE, Double.parseDouble(lat));
                    map.put(ENDLONGITUDE, Double.parseDouble(lon));

                    viewModel.updateActivity(map, tripId, activityId);
                }

                @Override
                public void onGeocodingFailure(Exception exception) {
                    activity.setEndLatitude(0);
                    activity.setEndLongitude(0);

                    map.put(ENDLATITUDE, 0);
                    map.put(ENDLONGITUDE, 0);
                    viewModel.updateActivity(map, tripId, activityId);
                }
            });


            GeocodingUtility utility = new GeocodingUtility(this.application);
            utility.setGeocodingUtilityCallback(new GeocodingUtilityCallback() {
                @Override
                public void onGeocodingSuccess(String lat, String lon) {
                    activity.setLatitude(Double.parseDouble(lat));
                    activity.setLongitude(Double.parseDouble(lon));
                    map.put(LATITUDE, Double.parseDouble(lat));
                    map.put(LONGITUDE, Double.parseDouble(lon));

                    if (activity.getType().equalsIgnoreCase(MOVING_ACTIVITY_TYPE_NAME)) {
                        endUtility.search(activity.getEnd_location(), 1);
                    } else {
                        viewModel.updateActivity(map, tripId, activityId);
                    }
                }

                @Override
                public void onGeocodingFailure(Exception exception) {
                    activity.setLatitude(0);
                    activity.setLongitude(0);
                    map.put(LATITUDE, 0);
                    map.put(LONGITUDE, 0);
                    viewModel.updateActivity(map, tripId, activityId);
                }
            });

            if (loc1.getEditText() != null) {
                location1 = loc1.getEditText().getText().toString().trim();
            }

            if (loc2.getEditText() != null) {
                location2 = loc2.getEditText().getText().toString().trim();
            }

            boolean valid = false;

            if (location1 == null) {
                loc1.setError(getString(R.string.activity_field_error));
                return;
            }

            if (location1.isEmpty()) {
                location1 = this.activity.getLocation();
            }


            if (!this.activity.getLocation().equalsIgnoreCase(location1)) {
                this.activity.setLocation(location1);

                map.put(LOCATION, location1);
                valid = true;
            }

            if (this.activity.getType().equalsIgnoreCase(MOVING_ACTIVITY_TYPE_NAME)) {
                if (location2 == null) {
                    loc2.setError(getString(R.string.activity_field_error));
                    return;
                }

                if (location2.isEmpty()) {
                    location2 = this.activity.getEnd_location();
                }

                if (!this.activity.getEnd_location().equalsIgnoreCase(location2)) {
                    this.activity.setEnd_location(location2);
                    map.put(ENDLOCATION, location2);
                    valid = true;
                }
            }

            if (valid) {
                utility.search(this.activity.getLocation(), 1);
            }
            Navigation.findNavController(view1).navigate(
                    R.id.action_activityLocationEditFragment_to_activityLocationFragment);
        });

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

                        this.trip = null;

                        for (Trip mTrip : trips) {
                            if (mTrip.getId().equals(tripId)) {
                                this.trip = mTrip;
                                break;
                            }
                        }

                        if (this.trip == null || !this.trip.isParticipating() || this.trip.isDeleted()) {
                            requireActivity().finish();
                            return;
                        }

                        if (this.trip.getActivity() == null
                                || this.trip.getActivity().getActivityList() == null) {
                            return;
                        }

                        this.activity = null;

                        for (Activity mActivity : this.trip.getActivity().getActivityList()) {
                            if (mActivity.getId().equals(activityId)) {
                                this.activity = mActivity;
                                break;
                            }
                        }

                        if (this.activity == null) {
                            requireActivity().finish();
                            return;
                        }

                        String location1 = this.activity.getLocation();
                        loc1.setHint(location1);
                        loc1text.setText(location1);

                        if (this.activity.getType().equalsIgnoreCase(MOVING_ACTIVITY_TYPE_NAME)) {
                            String location2 = this.activity.getEnd_location();
                            loc2.setHint(location2);
                            loc2text.setText(location2);
                            loc1text.setImeOptions(EditorInfo.IME_ACTION_NEXT);

                            loc2.setVisibility(View.VISIBLE);
                            arrow.setVisibility(View.VISIBLE);
                        } else {
                            loc1text.setImeOptions(EditorInfo.IME_ACTION_DONE);
                            loc2.setVisibility(View.GONE);
                            arrow.setVisibility(View.GONE);
                        }
                    } else {
                        ErrorMessagesUtil errorMessagesUtil = new ErrorMessagesUtil(this.application);
                        Snackbar.make(view, errorMessagesUtil.getErrorMessage(((Result.Error) result)
                                .getMessage()), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }
}
