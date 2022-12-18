package it.unimib.sal.one_two_trip.ui.trip.activity;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.MOVING_ACTIVITY_TYPE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.model.TripResponse;
import it.unimib.sal.one_two_trip.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.ui.trip.TripViewModel;
import it.unimib.sal.one_two_trip.ui.trip.TripViewModelFactory;
import it.unimib.sal.one_two_trip.util.Constants;
import it.unimib.sal.one_two_trip.util.ErrorMessagesUtil;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;


public class ActivityLocationEditFragment extends Fragment {
    private Application application;
    private TripViewModel viewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;

    private Trip trip;
    private Activity activity;

    public ActivityLocationEditFragment() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity_location_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputLayout loc1 = requireView().findViewById(R.id.activity_where1_edit);
        TextInputLayout loc2 = requireView().findViewById(R.id.activity_where2_edit);
        MaterialButton confirmButton = requireView().findViewById(R.id.activity_where_confirm);

        //Confirm Edit
        confirmButton.setOnClickListener(view1 -> {
            String location1 = null;
            String location2 = null;

            if(loc1.getEditText() != null) {
                location1 = loc1.getEditText().getText().toString();
            }

            if(loc2.getEditText() != null) {
                location2 = loc2.getEditText().getText().toString();
            }

            Log.d("AAA", location1 + " " + location2);

            boolean valid = false;

            if(location1 == null) {
                loc1.setError(getResources().getString(R.string.activity_field_error));
                return;
            }

            if(location1.equals("")) {
                location1 = activity.getLocation();
            }

            if(!activity.getLocation().equals(location1)) {
                activity.setLocation(location1);
                valid = true;
            }

            if(activity.getType().equals(MOVING_ACTIVITY_TYPE_NAME)) {
                if(location2 == null) {
                    loc2.setError(getResources().getString(R.string.activity_field_error));
                    return;
                }

                if(location2.equals("")) {
                    location2 = activity.getEnd_location();
                }

                if(!activity.getEnd_location().equals(location2)) {
                    activity.setEnd_location(location2);
                    valid = true;
                }
            }

            if(valid) {
                viewModel.updateTrip(trip);
            }

            Navigation.findNavController(view1).navigate(R.id.action_activityLocationEditFragment_to_activityLocationFragment);
        });

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE);
        }

        viewModel.getTrip(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> {
            if(result.isSuccess()) {
                trip = ((Result.Success<TripResponse>) result).getData().getTrip();
                activity = trip.getActivity().activityList.get(viewModel.getActivityPosition());

                loc1.setHint(activity.getLocation());

                if(activity.getType().equals(Constants.MOVING_ACTIVITY_TYPE_NAME)) {
                    loc2.setHint(activity.getEnd_location());

                    loc2.setVisibility(View.VISIBLE);
                } else {
                    loc2.setVisibility(View.GONE);
                }
            } else {
                ErrorMessagesUtil errorMessagesUtil = new ErrorMessagesUtil(this.application);
                Snackbar.make(view, errorMessagesUtil.getErrorMessage(((Result.Error) result)
                        .getMessage()), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private boolean hasLocationChanged(TextInputLayout location) {
        return location.getEditText() != null &&
                !location.getEditText().getText().toString().equals("") &&
                !location.getEditText().getText().toString().equals(activity.getLocation());
    }
}