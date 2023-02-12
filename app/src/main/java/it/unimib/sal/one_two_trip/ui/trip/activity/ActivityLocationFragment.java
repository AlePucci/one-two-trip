package it.unimib.sal.one_two_trip.ui.trip.activity;

import static it.unimib.sal.one_two_trip.util.Constants.GOOGLE_NAVIGATION;
import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.MOVING_ACTIVITY_TYPE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_ACTIVITY_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.ZOOM_TO_ACTIVITY;
import static it.unimib.sal.one_two_trip.util.Constants.ZOOM_TO_END_LOCATION;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

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

/**
 * Fragment that enables the user to see the location(s) of an activity.
 */
public class ActivityLocationFragment extends Fragment {

    private Application application;
    private TripsViewModel viewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private Activity activity;

    public ActivityLocationFragment() {
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
        return inflater.inflate(R.layout.fragment_activity_location, container, false);
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

        MaterialButton navButton1 = view.findViewById(R.id.activity_where_navigation1);
        navButton1.setOnClickListener(view12 -> {
            Uri query = Uri.parse(GOOGLE_NAVIGATION + activity.getLatitude() + ","
                    + activity.getLongitude());
            Intent intent = new Intent(Intent.ACTION_VIEW, query);
            startActivity(intent);
        });

        MaterialButton navButton2 = view.findViewById(R.id.activity_where_navigation2);
        navButton2.setOnClickListener(view12 -> {
            Uri query = Uri.parse(GOOGLE_NAVIGATION + activity.getEndLatitude() + ","
                    + activity.getEndLongitude());
            Intent intent = new Intent(Intent.ACTION_VIEW, query);
            startActivity(intent);
        });

        MaterialButton locButton1 = view.findViewById(R.id.activity_where_locate1);
        locButton1.setOnClickListener(view12 -> {
            Bundle bundle = new Bundle();
            bundle.putString(SELECTED_TRIP_ID, tripId);
            bundle.putString(SELECTED_ACTIVITY_ID, activityId);
            bundle.putBoolean(ZOOM_TO_ACTIVITY, true);

            ((ActivityFragment) getParentFragment().getParentFragment()).navigate(bundle);
        });

        MaterialButton locButton2 = view.findViewById(R.id.activity_where_locate2);
        locButton2.setOnClickListener(view12 -> {
            Bundle bundle = new Bundle();
            bundle.putString(SELECTED_TRIP_ID, tripId);
            bundle.putString(SELECTED_ACTIVITY_ID, activityId);
            bundle.putBoolean(ZOOM_TO_ACTIVITY, true);
            bundle.putBoolean(ZOOM_TO_END_LOCATION, true);

            ((ActivityFragment) getParentFragment().getParentFragment()).navigate(bundle);
        });

        MaterialButton editButton = view.findViewById(R.id.activity_where_edit);
        editButton.setOnClickListener(view1 ->
                Navigation.findNavController(view1).navigate
                        (R.id.action_activityLocationFragment_to_activityLocationEditFragment));

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
                        Trip trip = null;
                        for (Trip mTrip : trips) {
                            if (mTrip.getId().equals(tripId)) {
                                trip = mTrip;
                                break;
                            }
                        }

                        if (trip == null || !trip.isParticipating() || trip.isDeleted()) {
                            requireActivity().finish();
                            return;
                        }

                        if (trip.getActivity() == null
                                || trip.getActivity().getActivityList() == null) {
                            return;
                        }

                        this.activity = null;

                        for (Activity mActivity : trip.getActivity().getActivityList()) {
                            if (mActivity.getId().equals(activityId)) {
                                this.activity = mActivity;
                                break;
                            }
                        }

                        if (this.activity == null) {
                            requireActivity().finish();
                            return;
                        }

                        TextView loc1 = view.findViewById(R.id.activity_where1);
                        loc1.setText(this.activity.getLocation());

                        TextView loc2 = view.findViewById(R.id.activity_where2);
                        ImageView arrow = view.findViewById(R.id.activity_where_arrow);

                        if (this.activity.getType().equalsIgnoreCase(MOVING_ACTIVITY_TYPE_NAME)) {
                            loc2.setText(this.activity.getEnd_location());
                            loc2.setVisibility(View.VISIBLE);
                            locButton2.setVisibility(View.VISIBLE);
                            navButton2.setVisibility(View.VISIBLE);
                            arrow.setVisibility(View.VISIBLE);
                        } else {
                            loc2.setVisibility(View.GONE);
                            locButton2.setVisibility(View.GONE);
                            navButton2.setVisibility(View.GONE);
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