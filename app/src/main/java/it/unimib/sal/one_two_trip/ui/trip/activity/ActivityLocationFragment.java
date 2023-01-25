package it.unimib.sal.one_two_trip.ui.trip.activity;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.MOVING_ACTIVITY_TYPE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

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
import it.unimib.sal.one_two_trip.data.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModel;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModelFactory;
import it.unimib.sal.one_two_trip.util.ErrorMessagesUtil;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;


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
        return inflater.inflate(R.layout.fragment_activity_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getParentFragment() == null || getParentFragment().getParentFragment() == null) {
            return;
        }

        long tripId = ((ActivityFragment) getParentFragment().getParentFragment()).getTripId();
        long activityId = ((ActivityFragment) getParentFragment().getParentFragment()).getActivityId();

        MaterialButton navButton1 = view.findViewById(R.id.activity_where_navigation1);
        navButton1.setOnClickListener(view12 -> {
            Uri query = Uri.parse("google.navigation:q=" + activity.getLatitude() + "," + activity.getLongitude());
            Intent intent = new Intent(Intent.ACTION_VIEW, query);
            startActivity(intent);
        });

        MaterialButton navButton2 = view.findViewById(R.id.activity_where_navigation2);
        navButton2.setOnClickListener(view12 -> {
            Uri query = Uri.parse("google.navigation:q=" + activity.getEndLatitude() + "," + activity.getEndLongitude());
            Intent intent = new Intent(Intent.ACTION_VIEW, query);
            startActivity(intent);
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

        this.viewModel.getTrips(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(),
                result -> {
            if (result.isSuccess()) {
                List<Trip> trips = ((Result.Success) result).getData().getTripList();
                Trip trip = null;
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
                        activity = mActivity;
                        break;
                    }
                }

                if (activity == null) return;

                TextView loc1 = view.findViewById(R.id.activity_where1);
                loc1.setText(activity.getLocation());

                TextView loc2 = view.findViewById(R.id.activity_where2);
                MaterialButton locate2 = view.findViewById(R.id.activity_where_locate2);
                ImageView arrow = view.findViewById(R.id.activity_where_arrow);

                if (activity.getType().equalsIgnoreCase(MOVING_ACTIVITY_TYPE_NAME)) {
                    loc2.setText(activity.getEnd_location());
                    loc2.setVisibility(View.VISIBLE);
                    locate2.setVisibility(View.VISIBLE);
                    navButton2.setVisibility(View.VISIBLE);
                    arrow.setVisibility(View.VISIBLE);
                } else {
                    loc2.setVisibility(View.GONE);
                    locate2.setVisibility(View.GONE);
                    navButton2.setVisibility(View.GONE);
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