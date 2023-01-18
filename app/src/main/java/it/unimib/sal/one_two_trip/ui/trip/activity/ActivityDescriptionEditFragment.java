package it.unimib.sal.one_two_trip.ui.trip.activity;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModel;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModelFactory;
import it.unimib.sal.one_two_trip.util.ErrorMessagesUtil;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;


public class ActivityDescriptionEditFragment extends Fragment {

    private Application application;
    private TripsViewModel viewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private Trip trip;
    private Activity activity;

    public ActivityDescriptionEditFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity_description_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getParentFragment() == null || getParentFragment().getParentFragment() == null) {
            return;
        }

        long tripId = ((ActivityFragment) getParentFragment().getParentFragment()).getTripId();
        long activityId = ((ActivityFragment) getParentFragment().getParentFragment()).getActivityId();

        MaterialButton editButton = view.findViewById(R.id.activity_descr_confirm);
        TextInputEditText description = view.findViewById(R.id.activity_descr_edittext);

        editButton.setOnClickListener(view1 -> {
            if (description.getText() != null && !description.getText().toString().equalsIgnoreCase(
                    activity.getDescription())) {
                this.activity.setDescription(description.getText().toString());
                this.viewModel.updateTrip(trip);
            }

            Navigation.findNavController(view).navigate(
                    R.id.action_activityDescriptionEditFragment_to_activityDescriptionFragment);
        });

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE);
        }

        this.viewModel.getTrips(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(),
                result -> {
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

                        description.setText(this.activity.getDescription());
                    } else {
                        ErrorMessagesUtil errorMessagesUtil = new ErrorMessagesUtil(this.application);
                        Snackbar.make(view, errorMessagesUtil.getErrorMessage(((Result.Error) result)
                                .getMessage()), Snackbar.LENGTH_SHORT).show();
                        requireActivity().finish();
                    }
                });
    }
}
