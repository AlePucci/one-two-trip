package it.unimib.sal.one_two_trip.ui.trip.activity;

import static it.unimib.sal.one_two_trip.util.Constants.ACTIVITY_TITLE;
import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.MOVING_ACTIVITY_TYPE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.app.Application;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.Person;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.model.holder.PersonListHolder;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModel;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModelFactory;
import it.unimib.sal.one_two_trip.util.Constants;
import it.unimib.sal.one_two_trip.util.ErrorMessagesUtil;
import it.unimib.sal.one_two_trip.util.GeocodingUtility;
import it.unimib.sal.one_two_trip.util.GeocodingUtilityCallback;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;


public class ActivityNewFragment extends Fragment {

    private Application application;
    private TripsViewModel viewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;

    private Trip trip;
    private Activity activity;

    public ActivityNewFragment() {
        // Required empty public constructor
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
        return inflater.inflate(R.layout.fragment_activity_new, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

        GeocodingUtility endUtility = new GeocodingUtility(application);
        endUtility.setGeocodingUtilityCallback(new GeocodingUtilityCallback() {
            @Override
            public void onGeocodingSuccess(String lat, String lon) {
                activity.setEndLatitude(Double.parseDouble(lat));
                activity.setEndLongitude(Double.parseDouble(lon));

                viewModel.updateTrip(trip);
            }

            @Override
            public void onGeocodingFailure(Exception exception) {
                //Snackbar.make(requireView(), exception.getMessage() != null ? exception.getMessage() : "Could not locate activity", Snackbar.LENGTH_SHORT).show();
                activity.setLatitude(0);
                activity.setLongitude(0);
                viewModel.updateTrip(trip);
            }
        });


        GeocodingUtility utility = new GeocodingUtility(application);
        utility.setGeocodingUtilityCallback(new GeocodingUtilityCallback() {
            @Override
            public void onGeocodingSuccess(String lat, String lon) {
                activity.setLatitude(Double.parseDouble(lat));
                activity.setLongitude(Double.parseDouble(lon));

                if(activity.getType().equals(MOVING_ACTIVITY_TYPE_NAME)) {
                    endUtility.search(activity.getEnd_location(), 1);
                } else {
                    viewModel.updateTrip(trip);
                }
            }

            @Override
            public void onGeocodingFailure(Exception exception) {
                //Snackbar.make(requireView(), exception.getMessage() != null ? exception.getMessage() : "Could not locate activity", Snackbar.LENGTH_SHORT).show();
                activity.setLatitude(0);
                activity.setLongitude(0);
                viewModel.updateTrip(trip);
            }
        });

        TextView activity_title = view.findViewById(R.id.activity_new_title);
        if(getArguments() != null) {
            String title = getArguments().getString(ACTIVITY_TITLE);
            activity_title.setText(title);
        }

        TextInputLayout where1 = view.findViewById(R.id.activity_new_where1_edit);
        MaterialButton when1 = view.findViewById(R.id.activity_new_when1_edit);
        TextInputLayout descr = view.findViewById(R.id.activity_new_descr_textlayout);
        MaterialSwitch moving = view.findViewById(R.id.activity_new_moving);
        TextInputLayout where2 = view.findViewById(R.id.activity_new_where2_edit);
        MaterialButton when2 = view.findViewById(R.id.activity_new_when2_edit);
        ImageView whereArrow = view.findViewById(R.id.activity_new_where_arrow_edit);
        ImageView whenArrow = view.findViewById(R.id.activity_new_when_arrow_edit);

        moving.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b) {
                where2.setVisibility(View.VISIBLE);
                when2.setVisibility(View.VISIBLE);
                whereArrow.setVisibility(View.VISIBLE);
                whenArrow.setVisibility(View.VISIBLE);
            } else {
                where2.setVisibility(View.GONE);
                when2.setVisibility(View.GONE);
                whereArrow.setVisibility(View.GONE);
                whenArrow.setVisibility(View.GONE);
            }
        });

        when1.setOnClickListener(view12 -> {
            final Calendar c = Calendar.getInstance();

            new DatePickerDialog(requireContext(), (datePicker, i, i1, i2) ->
                    new TimePickerDialog(requireContext(), (timePicker, j, j1) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(i, i1, i2, j, j1);
                        when1.setText(df.format(new Date(calendar.getTimeInMillis())));
                    }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show(),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)).show();
        });

        when2.setOnClickListener(view12 -> {
            final Calendar c = Calendar.getInstance();

            new DatePickerDialog(requireContext(), (datePicker, i, i1, i2) ->
                    new TimePickerDialog(requireContext(), (timePicker, j, j1) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(i, i1, i2, j, j1);
                        when2.setText(df.format(new Date(calendar.getTimeInMillis())));
                    }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show(),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)).show();
        });

        MaterialButton materialButton = view.findViewById(R.id.activity_new_confirm);
        materialButton.setOnClickListener(view1 -> {

            if(where1.getEditText() != null && where1.getEditText().getText().toString().isEmpty()) {
                where1.setError(getString(R.string.activity_field_error));
                return;
            } else {
                where1.setErrorEnabled(false);
            }

            if(when1.getText().toString().isEmpty()) {
                when1.setError(getString(R.string.unexpected_error));
                return;
            } else {
                where1.setErrorEnabled(false);
            }

            if(descr.getEditText() == null) {
                return;
            }

            if(getArguments() == null) {
                return;
            }

            String title = getArguments().getString(ACTIVITY_TITLE);
            String location = where1.getEditText().getText().toString();

            Date parsed = df.parse(when1.getText().toString(), new ParsePosition(0));
            if(parsed == null) {
                when1.setError(getString(R.string.unexpected_error));
                return;
            } else {
                where1.setErrorEnabled(false);
            }
            long date = parsed.getTime();

            String description = descr.getEditText().getText().toString();

            activity = new Activity();
            activity.setId(System.currentTimeMillis());
            activity.setTitle(title);
            activity.setLocation(location);
            activity.setStart_date(date);
            activity.setDescription(description);
            ArrayList<Person> personList = new ArrayList<>();
            personList.add(new Person(80, "test", "aa", "sage", "seg", "afge", "asef"));
            activity.setParticipant(new PersonListHolder(personList));

            if(moving.isChecked()) {
                if(where2.getEditText() != null && where2.getEditText().getText().toString().isEmpty()) {
                    where2.setError(getString(R.string.activity_field_error));
                    return;
                } else {
                    where2.setErrorEnabled(false);
                }

                if(when2.getText().toString().isEmpty()) {
                    when2.setError(getString(R.string.unexpected_error));
                    return;
                } else {
                    where2.setErrorEnabled(false);
                }

                activity.setType(Constants.MOVING_ACTIVITY_TYPE_NAME);

                String location2 = where2.getEditText().getText().toString();

                parsed = df.parse(when2.getText().toString(), new ParsePosition(0));
                if(parsed == null) {
                    when2.setError(getString(R.string.unexpected_error));
                    return;
                } else {
                    where2.setErrorEnabled(false);
                }
                date = parsed.getTime();

                activity.setEnd_location(location2);
                activity.setEnd_date(date);
            } else {
                activity.setType(Constants.STATIC_ACTIVITY_TYPE_NAME);
            }

            if(trip != null) {
                utility.search(location, 1);

                trip.getActivity().getActivityList().add(activity);

                requireActivity().onBackPressed();
            }
        });


        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE);
        }

        this.viewModel.getTrips(Long.parseLong(lastUpdate)).observeForever(result -> {
            if (result.isSuccess()) {
                if(getArguments() == null) {
                    return;
                }

                long tripId = getArguments().getLong(SELECTED_TRIP_ID);
                List<Trip> trips = ((Result.Success) result).getData().getTripList();

                for (Trip trip : trips) {
                    if (trip.getId() == tripId) {
                        this.trip = trip;
                        break;
                    }
                }
            } else {
                ErrorMessagesUtil errorMessagesUtil = new ErrorMessagesUtil(this.application);
                Snackbar.make(view, errorMessagesUtil.getErrorMessage(((Result.Error) result)
                        .getMessage()), Snackbar.LENGTH_SHORT).show();

                requireActivity().onBackPressed();
            }

        });
    }
}