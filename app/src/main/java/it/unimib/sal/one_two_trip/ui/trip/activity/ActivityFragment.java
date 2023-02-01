package it.unimib.sal.one_two_trip.ui.trip.activity;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.MOVE_TO_ACTIVITY;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_ACTIVITY_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

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
import it.unimib.sal.one_two_trip.util.Utility;

public class ActivityFragment extends Fragment implements MenuProvider {

    private long tripId;
    private long activityId;
    private Application application;
    private TripsViewModel viewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private Trip trip;

    private Activity activity;

    public ActivityFragment() {
    }

    public long getTripId() {
        return tripId;
    }

    public void setTripId(long tripId) {
        this.tripId = tripId;
    }

    public long getActivityId() {
        return activityId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
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

        if (getArguments() == null) {
            return;
        }
        this.tripId = getArguments().getLong(SELECTED_TRIP_ID);
        this.activityId = getArguments().getLong(SELECTED_ACTIVITY_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        androidx.fragment.app.FragmentActivity activity = requireActivity();
        MaterialToolbar toolbar = activity.findViewById(R.id.trip_toolbar);
        ((MenuHost) activity).addMenuProvider(this, getViewLifecycleOwner(),
                Lifecycle.State.RESUMED);

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

                        for (Trip mTrip : trips) {
                            if (mTrip.getId() == this.tripId) {
                                this.trip = mTrip;
                                break;
                            }
                        }

                        if (this.trip == null || this.trip.getActivity() == null
                                || this.trip.getActivity().getActivityList() == null) {
                            return;
                        }

                        for (Activity mActivity : this.trip.getActivity().getActivityList()) {
                            if (mActivity.getId() == this.activityId) {
                                this.activity = mActivity;
                                break;
                            }
                        }

                        if (this.activity == null) return;

                        toolbar.setTitle(this.activity.getTitle());
                    } else {
                        ErrorMessagesUtil errorMessagesUtil = new ErrorMessagesUtil(this.application);
                        Snackbar.make(view, errorMessagesUtil.getErrorMessage(((Result.Error) result)
                                .getMessage()), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.activity_appbar_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        Context context = requireContext();
        if (menuItem.getItemId() == R.id.trip_menu_rename) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            String oldTitle = this.activity.getTitle();
            EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setHint(oldTitle);

            alert.setTitle(getString(R.string.activity_title_change_title));
            alert.setMessage(getString(R.string.activity_title_change));
            alert.setView(input);
            alert.setPositiveButton(getString(R.string.activity_title_change_positive),
                    (dialog, which) -> {
                        if (input.getText() == null) return;

                        String newTitle = input.getText().toString().trim();
                        if (!newTitle.isEmpty() && !newTitle.equals(oldTitle)) {
                            this.activity.setTitle(newTitle);
                            this.viewModel.updateTrip(this.trip);
                        }
                    });
            alert.setNegativeButton(getString(R.string.activity_title_change_negative), null);
            alert.show();

            return true;
        } else if (menuItem.getItemId() == R.id.trip_menu_delete) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(getString(R.string.activity_delete_confirmation_title));
            alert.setMessage(getString(R.string.activity_delete_confirmation));
            alert.setPositiveButton(getString(R.string.activity_delete_confirmation_positive),
                    (dialog, whichButton) -> {
                        this.trip.getActivity().getActivityList().removeIf(activity ->
                                activity.getId() == activityId);
                        this.viewModel.updateTrip(this.trip);
                        Utility.onActivityDelete(this.trip, this.activity, this.application);

                        Bundle bundle = new Bundle();
                        bundle.putLong(SELECTED_TRIP_ID, this.trip.getId());
                        bundle.putBoolean(MOVE_TO_ACTIVITY, false);
                        bundle.putLong(SELECTED_ACTIVITY_ID, this.activityId);
                        Navigation.findNavController(
                                requireView()).navigate(R.id.action_activityFragment_to_tripFragment,
                                bundle);
                    });

            alert.setNegativeButton(getString(R.string.activity_delete_confirmation_negative),
                    null);
            alert.show();
            return true;
        }

        return false;
    }
}
