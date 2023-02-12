package it.unimib.sal.one_two_trip.ui.trip.activity;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.MOVE_TO_ACTIVITY;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_ACTIVITY_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.TITLE;

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
import android.widget.FrameLayout;

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
import it.unimib.sal.one_two_trip.util.Utility;

/**
 * Fragment that enables the user to see and edit the details of an activity.
 * It implements the {@link MenuProvider} interface to provide the menu.
 */
public class ActivityFragment extends Fragment implements MenuProvider {

    private String tripId;
    private String activityId;
    private Application application;
    private TripsViewModel viewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private Trip trip;
    private Activity activity;
    private MaterialToolbar toolbar;

    public ActivityFragment() {
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
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
        this.tripId = getArguments().getString(SELECTED_TRIP_ID);
        this.activityId = getArguments().getString(SELECTED_ACTIVITY_ID);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        androidx.fragment.app.FragmentActivity activity = requireActivity();
        this.toolbar = activity.findViewById(R.id.trip_toolbar);
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
                        List<Trip> trips = ((Result.TripSuccess) result).getData().getTripList();

                        this.trip = null;

                        for (Trip mTrip : trips) {
                            if (mTrip.getId().equals(this.tripId)) {
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
                            if (mActivity.getId().equals(this.activityId)) {
                                this.activity = mActivity;
                                break;
                            }
                        }

                        if (this.activity == null) {
                            requireActivity().finish();
                            return;
                        }

                        this.toolbar.setTitle(this.activity.getTitle());
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
            androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(
                    context, R.style.Widget_App_CustomAlertDialog);
            EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            FrameLayout container = new FrameLayout(context);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(50, 0, 50, 0);
            input.setLayoutParams(params);
            container.addView(input);
            String oldTitle = this.activity.getTitle();
            input.setHint(oldTitle);

            alert.setTitle(getString(R.string.activity_title_change_title));
            alert.setMessage(getString(R.string.activity_title_change));
            alert.setView(container);
            alert.setPositiveButton(getString(R.string.activity_title_change_positive),
                    (dialog, which) -> {
                        if (input.getText() == null) return;

                        String newTitle = input.getText().toString().trim();
                        if (!newTitle.isEmpty() && !newTitle.equals(oldTitle)) {
                            this.activity.setTitle(newTitle);
                            this.toolbar.setTitle(this.activity.getTitle());
                            HashMap<String, Object> map = new HashMap<>();
                            map.put(TITLE, newTitle);
                            this.viewModel.updateActivity(map, tripId, activityId);
                        }
                    });
            alert.setNegativeButton(getString(R.string.activity_title_change_negative), null);
            alert.show();

            return true;
        } else if (menuItem.getItemId() == R.id.trip_menu_delete) {
            androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(
                    context, R.style.Widget_App_CustomAlertDialog);
            alert.setTitle(getString(R.string.activity_delete_confirmation_title));
            alert.setMessage(getString(R.string.activity_delete_confirmation));
            alert.setPositiveButton(getString(R.string.activity_delete_confirmation_positive),
                    (dialog, whichButton) -> {
                        this.trip.getActivity().getActivityList().removeIf(activity ->
                                activity.getId().equals(activityId));
                        this.viewModel.deleteActivity(activity, trip);
                        Utility.onActivityDelete(this.trip, this.activity, this.application);

                        Bundle bundle = new Bundle();
                        bundle.putString(SELECTED_TRIP_ID, this.tripId);
                        bundle.putBoolean(MOVE_TO_ACTIVITY, false);
                        bundle.putString(SELECTED_ACTIVITY_ID, this.activityId);
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

    public void navigate(Bundle bundle) {
        Navigation.findNavController(requireView()).navigate(R.id.action_activityFragment_to_tripFragment, bundle);
    }
}
