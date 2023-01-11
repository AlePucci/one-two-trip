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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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


public class ActivityDateFragment extends Fragment {
    private Application application;
    private TripsViewModel viewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;

    public ActivityDateFragment() {
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
        return inflater.inflate(R.layout.fragment_activity_date, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton editButton = requireView().findViewById(R.id.activity_when_edit);
        Bundle bundle = new Bundle();
        bundle.putInt(SELECTED_TRIP_POS, getArguments().getInt(SELECTED_TRIP_POS));
        bundle.putInt(SELECTED_ACTIVITY_POS, getArguments().getInt(SELECTED_ACTIVITY_POS));
        editButton.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_activityDateFragment_to_activityDateEditFragment, bundle));

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

                TextView date1 = requireView().findViewById(R.id.activity_when1);
                DateFormat df = SimpleDateFormat.getInstance();
                date1.setText(df.format(activity.getStart_date()));

                TextView date2 = requireView().findViewById(R.id.activity_when2);
                MaterialButton save2 = requireView().findViewById(R.id.activity_when_save2);
                ImageView arrow = requireView().findViewById(R.id.activity_when_arrow);
                if(activity.getType().equals(Constants.MOVING_ACTIVITY_TYPE_NAME)) {
                    date2.setText(df.format(activity.getEnd_date()));

                    date2.setVisibility(View.VISIBLE);
                    save2.setVisibility(View.VISIBLE);
                    arrow.setVisibility(View.VISIBLE);
                } else {
                    date2.setVisibility(View.GONE);
                    save2.setVisibility(View.GONE);
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