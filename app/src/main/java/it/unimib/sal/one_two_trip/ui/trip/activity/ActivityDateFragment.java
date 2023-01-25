package it.unimib.sal.one_two_trip.ui.trip.activity;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.MOVING_ACTIVITY_TYPE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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


public class ActivityDateFragment extends Fragment {

    private Application application;
    private TripsViewModel viewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;

    private Activity activity;

    public ActivityDateFragment() {
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
        return inflater.inflate(R.layout.fragment_activity_date, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getParentFragment() == null || getParentFragment().getParentFragment() == null) {
            return;
        }

        long tripId = ((ActivityFragment) getParentFragment().getParentFragment()).getTripId();
        long activityId = ((ActivityFragment) getParentFragment().getParentFragment()).getActivityId();

        MaterialButton calendarButton1 = view.findViewById(R.id.activity_when_save1);
        calendarButton1.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.Events.TITLE, activity.getTitle())
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, activity.getLocation())
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, activity.getStart_date());

            if(activity.getType().equals(MOVING_ACTIVITY_TYPE_NAME)) {
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, activity.getEnd_date());
            }

            startActivity(intent);
        });

        MaterialButton editButton = view.findViewById(R.id.activity_when_edit);

        editButton.setOnClickListener(btn ->
                Navigation.findNavController(btn).navigate(
                        R.id.action_activityDateFragment_to_activityDateEditFragment));

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

                        TextView date1 = view.findViewById(R.id.activity_when1);
                        DateFormat df = SimpleDateFormat.getInstance();
                        date1.setText(df.format(activity.getStart_date()));

                        TextView date2 = view.findViewById(R.id.activity_when2);
                        ImageView arrow = view.findViewById(R.id.activity_when_arrow);
                        if (activity.getType().equalsIgnoreCase(MOVING_ACTIVITY_TYPE_NAME)) {
                            date2.setText(df.format(activity.getEnd_date()));

                            date2.setVisibility(View.VISIBLE);
                            arrow.setVisibility(View.VISIBLE);
                        } else {
                            date2.setVisibility(View.GONE);
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
