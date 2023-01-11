package it.unimib.sal.one_two_trip.ui.trip.activity;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.MOVING_ACTIVITY_TYPE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.STATIC_ACTIVITY_TYPE_NAME;

import android.app.Application;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.utils.widget.MockView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
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


public class ActivityDateEditFragment extends Fragment {
    private Application application;
    private TripViewModel viewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;

    private Trip trip;
    private Activity activity;

    public ActivityDateEditFragment() {
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activity_date_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DateFormat df = SimpleDateFormat.getInstance();

        MaterialButton dateb1 = requireView().findViewById(R.id.activity_when1_edit);
        MaterialButton dateb2 = requireView().findViewById(R.id.activity_when2_edit);
        MaterialButton editButton = requireView().findViewById(R.id.activity_when_confirm);
        ImageView arrow = requireView().findViewById(R.id.activity_when_arrow_edit);

        dateb1.setOnClickListener(view12 -> {
            final Calendar c = Calendar.getInstance();

            new DatePickerDialog(getContext(), (datePicker, i, i1, i2) -> new TimePickerDialog(getContext(), (timePicker, j, j1) -> {
                Date date = new Date(i, i1, i2, j, j1);
                dateb1.setText(df.format(date));
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show(), c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)).show();
        });

        dateb2.setOnClickListener(view12 -> {
            final Calendar c = Calendar.getInstance();

            new DatePickerDialog(getContext(), (datePicker, i, i1, i2) -> new TimePickerDialog(getContext(), (timePicker, j, j1) -> {
                Date date = new Date(i, i1, i2, j, j1);
                dateb2.setText(df.format(date));
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show(), c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)).show();
        });

        editButton.setOnClickListener(view1 -> {
            Date date1;
            Date date2;
            try {
                date1 = df.parse(dateb1.getText().toString());
            } catch (Exception e) {
                date1 = activity.getStart_date();
            }

            try {
                date2 = df.parse(dateb2.getText().toString());
            } catch (Exception e) {
                date2 = activity.getType().equals(MOVING_ACTIVITY_TYPE_NAME) ? activity.getEnd_date() : null;
            }

            boolean valid = false;

            if (date1 == null) {
                dateb1.setError(getResources().getString(R.string.unexpected_error));
                return;
            }

            if (!date1.equals(activity.getStart_date())) {
                activity.setStart_date(date1);
                valid = true;
            }

            if (activity.getType().equals(MOVING_ACTIVITY_TYPE_NAME)) {
                if (date2 == null) {
                    dateb2.setError(getResources().getString(R.string.unexpected_error));
                    return;
                }

                if (date1.after(date2)) {
                    dateb2.setError(getResources().getString(R.string.activity_field_error));
                    return;
                }

                if (!date2.equals(activity.getEnd_date())) {
                    activity.setEnd_date(date2);
                    valid = true;
                }
            }

            if (valid) {
                trip.getActivity().activityList.sort(Comparator.comparing(Activity::getStart_date));
                viewModel.updateTrip(trip);
            }

            Navigation.findNavController(view1).navigate(R.id.action_activityDateEditFragment_to_activityDateFragment);
        });

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE);
        }

        viewModel.getTrip(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                trip = ((Result.Success<TripResponse>) result).getData().getTrip();
                activity = trip.getActivity().activityList.get(viewModel.getActivityPosition());


                dateb1.setHint(df.format(activity.getStart_date()));

                if (activity.getType().equals(Constants.MOVING_ACTIVITY_TYPE_NAME)) {
                    dateb2.setHint(df.format(activity.getEnd_date()));

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