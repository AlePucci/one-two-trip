package it.unimib.sal.one_two_trip.ui.trip.activity;

import static it.unimib.sal.one_two_trip.util.Constants.COMPLETED;
import static it.unimib.sal.one_two_trip.util.Constants.ENDDATE;
import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.MOVING_ACTIVITY_TYPE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.STARTDATE;

import android.app.Application;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
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

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
 * Fragment that enables the user to edit the date(s) of an activity.
 */
public class ActivityDateEditFragment extends Fragment {

    private Application application;
    private TripsViewModel viewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private Trip trip;
    private Activity activity;

    public ActivityDateEditFragment() {
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
        return inflater.inflate(R.layout.fragment_activity_date_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getParentFragment() == null || getParentFragment().getParentFragment() == null) {
            return;
        }

        ActivityFragment parentFragment = (ActivityFragment) getParentFragment().getParentFragment();
        Context context = requireContext();

        String tripId = parentFragment.getTripId();
        String activityId = parentFragment.getActivityId();

        DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

        MaterialButton dateb1 = view.findViewById(R.id.activity_when1_edit);
        MaterialButton dateb2 = view.findViewById(R.id.activity_when2_edit);
        MaterialButton editButton = view.findViewById(R.id.activity_when_confirm);
        ImageView arrow = view.findViewById(R.id.activity_when_arrow_edit);

        dateb1.setOnClickListener(view12 -> {
            final Calendar c = Calendar.getInstance();

            if (this.activity.getStart_date() != 0) {
                c.setTime(new Date(this.activity.getStart_date()));
            }

            new DatePickerDialog(context, (datePicker, i, i1, i2) ->
                    new TimePickerDialog(context, (timePicker, j, j1) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(i, i1, i2, j, j1);
                        dateb1.setText(df.format(new Date(calendar.getTimeInMillis())));
                    }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show(),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)).show();
        });

        dateb2.setOnClickListener(view12 -> {
            final Calendar c = Calendar.getInstance();

            if (this.activity.getEnd_date() != 0) {
                c.setTime(new Date(this.activity.getEnd_date()));
            }

            new DatePickerDialog(context, (datePicker, i, i1, i2) ->
                    new TimePickerDialog(context, (timePicker, j, j1) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(i, i1, i2, j, j1);
                        dateb2.setText(df.format(new Date(calendar.getTimeInMillis())));
                    }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show(),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)).show();
        });

        editButton.setOnClickListener(view1 -> {
            HashMap<String, Object> map = new HashMap<>();

            long date1;
            long date2;

            if (dateb1.getText().toString().trim().isEmpty()) {
                date1 = this.activity.getStart_date();
            } else {
                Date temp = df.parse(dateb1.getText().toString().trim(),
                        new ParsePosition(0));
                if (temp != null) {
                    date1 = temp.getTime();
                } else {
                    date1 = 0;
                }
            }

            if (this.activity.getType().equalsIgnoreCase(MOVING_ACTIVITY_TYPE_NAME)) {
                if (dateb2.getText().toString().trim().isEmpty()) {
                    date2 = this.activity.getEnd_date();
                } else {
                    Date temp2 = df.parse(dateb2.getText().toString().trim(),
                            new ParsePosition(0));
                    if (temp2 != null) {
                        date2 = temp2.getTime();
                    } else {
                        date2 = 0;
                    }
                }
            } else {
                date2 = 0;
            }

            if (date1 == 0) {
                dateb1.setError(getString(R.string.unexpected_error));
                return;
            }

            if (this.activity.getType().equalsIgnoreCase(MOVING_ACTIVITY_TYPE_NAME)) {
                if (date2 == 0) {
                    dateb2.setError(getString(R.string.unexpected_error));
                    return;
                }

                if (date1 > date2) {
                    dateb2.setError(getString(R.string.activity_field_error));
                    return;
                }

                if (date1 != this.activity.getStart_date()) {
                    this.activity.setStart_date(date1);
                    map.put(STARTDATE, date1);
                }

                if (date2 != this.activity.getEnd_date()) {
                    this.activity.setEnd_date(date2);
                    map.put(ENDDATE, date2);
                    map.put(COMPLETED, this.activity.isCompleted());
                }
            } else if (date1 != activity.getStart_date()) {
                this.activity.setStart_date(date1);
                map.put(STARTDATE, date1);
                map.put(COMPLETED, this.activity.isCompleted());
            }

            if (!map.isEmpty()) {
                this.viewModel.updateActivity(map, tripId, activityId);
                Utility.onActivityCreate(this.trip, this.activity, this.application);
            }

            Navigation.findNavController(view1).navigate(
                    R.id.action_activityDateEditFragment_to_activityDateFragment);
        });


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
                            if (mTrip.getId().equals(tripId)) {
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
                            if (mActivity.getId().equals(activityId)) {
                                this.activity = mActivity;
                                break;
                            }
                        }

                        if (this.activity == null) {
                            requireActivity().finish();
                            return;
                        }

                        dateb1.setHint(df.format(this.activity.getStart_date()));

                        if (this.activity.getType().equalsIgnoreCase(MOVING_ACTIVITY_TYPE_NAME)) {
                            dateb2.setHint(df.format(this.activity.getEnd_date()));
                            dateb2.setVisibility(View.VISIBLE);
                            arrow.setVisibility(View.VISIBLE);
                        } else {
                            dateb2.setVisibility(View.GONE);
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
