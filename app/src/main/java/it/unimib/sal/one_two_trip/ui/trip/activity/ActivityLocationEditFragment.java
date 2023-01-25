package it.unimib.sal.one_two_trip.ui.trip.activity;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.MOVING_ACTIVITY_TYPE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModel;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModelFactory;
import it.unimib.sal.one_two_trip.util.ErrorMessagesUtil;
import it.unimib.sal.one_two_trip.util.GeocodingUtility;
import it.unimib.sal.one_two_trip.util.GeocodingUtilityCallback;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;

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

        this.application = requireActivity().getApplication();
        this.sharedPreferencesUtil = new SharedPreferencesUtil(this.application);
        ITripsRepository tripsRepository = ServiceLocator.getInstance()
                .getTripsRepository(this.application);
        if (tripsRepository != null) {
            this.viewModel = new ViewModelProvider(requireActivity(),
                    new TripsViewModelFactory(tripsRepository)).get(TripsViewModel.class);
        } else {
            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity_location_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getParentFragment() == null || getParentFragment().getParentFragment() == null) {
            return;
        }

        long tripId = ((ActivityFragment) getParentFragment().getParentFragment()).getTripId();
        long activityId = ((ActivityFragment) getParentFragment().getParentFragment()).getActivityId();

        TextInputLayout loc1 = view.findViewById(R.id.activity_where1_edit);
        TextInputLayout loc2 = view.findViewById(R.id.activity_where2_edit);
        MaterialButton confirmButton = view.findViewById(R.id.activity_where_confirm);
        ImageView arrow = view.findViewById(R.id.activity_where_arrow_edit);

        //Confirm Edit
        confirmButton.setOnClickListener(view1 -> {
            String location1 = null;
            String location2 = null;

            GeocodingUtility endUtility = new GeocodingUtility(application);
            endUtility.setGeocodingUtilityCallback(new GeocodingUtilityCallback() {
                @Override
                public void onGeocodingSuccess(String lat, String lon) {
                    activity.setEndLatitude(Double.parseDouble(lat));
                    activity.setEndLongitude(Double.parseDouble(lon));

                    viewModel.updateTrip(trip);
                }

                @Override
                public void onGeocodingFailure(Exception exception) {
                    Snackbar.make(requireView(), exception.getMessage() != null ? exception.getMessage() : "Could not locate activity", Snackbar.LENGTH_SHORT).show();
                    activity.setLatitude(0);
                    activity.setLongitude(0);
                    viewModel.updateTrip(trip);
                }
            });


            GeocodingUtility utility = new GeocodingUtility(application);
            utility.setGeocodingUtilityCallback(new GeocodingUtilityCallback() {
                @Override
                public void onGeocodingSuccess(String lat, String lon) {
                    activity.setLatitude(Double.parseDouble(lat));
                    activity.setLongitude(Double.parseDouble(lon));

                    if(activity.getType().equals(MOVING_ACTIVITY_TYPE_NAME)) {
                        endUtility.search(activity.getEnd_location(), 1);
                    } else {
                        viewModel.updateTrip(trip);
                    }
                }

                @Override
                public void onGeocodingFailure(Exception exception) {
                    Snackbar.make(requireView(), exception.getMessage() != null ? exception.getMessage() : "Could not locate activity", Snackbar.LENGTH_SHORT).show();
                    activity.setLatitude(0);
                    activity.setLongitude(0);
                    viewModel.updateTrip(trip);
                }
            });

            if (loc1.getEditText() != null) {
                location1 = loc1.getEditText().getText().toString();
            }

            if (loc2.getEditText() != null) {
                location2 = loc2.getEditText().getText().toString();
            }

            boolean valid = false;

            if (location1 == null) {
                loc1.setError(getResources().getString(R.string.activity_field_error));
                return;
            }

            if (location1.isEmpty()) {
                location1 = this.activity.getLocation();
            }

            if (!this.activity.getLocation().equalsIgnoreCase(location1)) {
                this.activity.setLocation(location1);
                valid = true;
            }

            if (this.activity.getType().equalsIgnoreCase(MOVING_ACTIVITY_TYPE_NAME)) {
                if (location2 == null) {
                    loc2.setError(getResources().getString(R.string.activity_field_error));
                    return;
                }

                if (location2.isEmpty()) {
                    location2 = this.activity.getEnd_location();
                }

                if (!this.activity.getEnd_location().equalsIgnoreCase(location2)) {
                    this.activity.setEnd_location(location2);
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

        this.viewModel.getTrips(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                List<Trip> trips = ((Result.Success) result).getData().getTripList();

                for (Trip mTrip : trips) {
                    if (mTrip.getId() == tripId) {
                        trip = mTrip;
                        break;
                    }
                }

                if (trip == null || trip.getActivity() == null
                        || trip.getActivity().getActivityList() == null) {
                    return;
                }

                for (Activity mActivity : trip.getActivity().getActivityList()) {
                    if (mActivity.getId() == activityId) {
                        this.activity = mActivity;
                        break;
                    }
                }

                if (this.activity == null) return;

                loc1.setHint(this.activity.getLocation());

                if (this.activity.getType().equalsIgnoreCase(MOVING_ACTIVITY_TYPE_NAME)) {
                    loc2.setHint(this.activity.getEnd_location());

                    loc2.setVisibility(View.VISIBLE);
                    arrow.setVisibility(View.VISIBLE);
                } else {
                    loc2.setVisibility(View.GONE);
                    arrow.setVisibility(View.GONE);
                }
            } else {
                ErrorMessagesUtil errorMessagesUtil = new ErrorMessagesUtil(this.application);
                Snackbar.make(view, errorMessagesUtil.getErrorMessage(((Result.Error) result)
                        .getMessage()), Snackbar.LENGTH_SHORT).show();
                requireActivity().finish();
            }
        });
    }
}
