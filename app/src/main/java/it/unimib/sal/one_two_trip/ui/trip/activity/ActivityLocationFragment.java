package it.unimib.sal.one_two_trip.ui.trip.activity;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_ACTIVITY_POS;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_POS;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModel;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModelFactory;
import it.unimib.sal.one_two_trip.util.Constants;
import it.unimib.sal.one_two_trip.util.ErrorMessagesUtil;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;


public class ActivityLocationFragment extends Fragment {
    private Application application;
    private TripsViewModel viewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;

    public ActivityLocationFragment() {
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
                new TripsViewModelFactory(tripsRepository)).get(TripsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton editButton = requireView().findViewById(R.id.activity_where_edit);
        Bundle bundle = new Bundle();
        bundle.putInt(SELECTED_TRIP_POS, getArguments().getInt(SELECTED_TRIP_POS));
        bundle.putInt(SELECTED_ACTIVITY_POS, getArguments().getInt(SELECTED_ACTIVITY_POS));
        editButton.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_activityLocationFragment_to_activityLocationEditFragment, bundle));

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE);
        }

        viewModel.getTrips(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> {
            if(result.isSuccess()) {
                List<Trip> trips = ((Result.Success) result).getData().getTripList();
                int tripPos = getArguments().getInt(SELECTED_TRIP_POS);
                Trip trip = trips.get(tripPos);
                int activityPos = getArguments().getInt(SELECTED_ACTIVITY_POS);
                Activity activity = trip.getActivity().activityList.get(activityPos);

                TextView loc1 = requireView().findViewById(R.id.activity_where1);
                loc1.setText(activity.getLocation());

                TextView loc2 = requireView().findViewById(R.id.activity_where2);
                MaterialButton locate2 = requireView().findViewById(R.id.activity_where_locate2);
                MaterialButton navigate2 = requireView().findViewById(R.id.activity_where_navigation2);
                ImageView arrow = requireView().findViewById(R.id.activity_where_arrow);
                if(activity.getType().equals(Constants.MOVING_ACTIVITY_TYPE_NAME)) {
                    loc2.setText(activity.getEnd_location());

                    loc2.setVisibility(View.VISIBLE);
                    locate2.setVisibility(View.VISIBLE);
                    navigate2.setVisibility(View.VISIBLE);
                    arrow.setVisibility(View.VISIBLE);
                } else {
                    loc2.setVisibility(View.GONE);
                    locate2.setVisibility(View.GONE);
                    navigate2.setVisibility(View.GONE);
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