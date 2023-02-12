package it.unimib.sal.one_two_trip.ui.trip.activity;

import static it.unimib.sal.one_two_trip.util.Constants.ACTIVITY_TITLE;
import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.MOVING_ACTIVITY_TYPE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.STATIC_ACTIVITY_TYPE_NAME;

import android.app.Application;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.UUID;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.adapter.ParticipantRecyclerViewAdapter;
import it.unimib.sal.one_two_trip.data.database.model.Activity;
import it.unimib.sal.one_two_trip.data.database.model.Person;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.database.model.Trip;
import it.unimib.sal.one_two_trip.data.database.model.holder.PersonListHolder;
import it.unimib.sal.one_two_trip.data.repository.trips.ITripsRepository;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModel;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModelFactory;
import it.unimib.sal.one_two_trip.util.Constants;
import it.unimib.sal.one_two_trip.util.ErrorMessagesUtil;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;
import it.unimib.sal.one_two_trip.util.Utility;
import it.unimib.sal.one_two_trip.util.geocoding.GeocodingUtility;
import it.unimib.sal.one_two_trip.util.geocoding.GeocodingUtilityCallback;

/**
 * Fragment that enables the user to create a new activity.
 */
public class ActivityNewFragment extends Fragment {

    private Application application;
    private TripsViewModel viewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private Trip trip;
    private Activity activity;
    private List<Person> personList;
    private List<Person> notParticipating;
    private ParticipantRecyclerViewAdapter participantAdapter;
    private ParticipantRecyclerViewAdapter notParticipantAdapter;

    public ActivityNewFragment() {
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
        return inflater.inflate(R.layout.fragment_activity_new, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() == null) {
            return;
        }

        String tripId = getArguments().getString(SELECTED_TRIP_ID);
        String title = getArguments().getString(ACTIVITY_TITLE);

        DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        GeocodingUtility endUtility = new GeocodingUtility(this.application);
        Context context = requireContext();

        endUtility.setGeocodingUtilityCallback(new GeocodingUtilityCallback() {
            @Override
            public void onGeocodingSuccess(String lat, String lon) {
                activity.setEndLatitude(Double.parseDouble(lat));
                activity.setEndLongitude(Double.parseDouble(lon));
                onNewActivityCreated();
            }

            @Override
            public void onGeocodingFailure(Exception exception) {
                activity.setEndLatitude(0);
                activity.setEndLongitude(0);
                onNewActivityCreated();
            }
        });

        GeocodingUtility utility = new GeocodingUtility(this.application);
        utility.setGeocodingUtilityCallback(new GeocodingUtilityCallback() {
            @Override
            public void onGeocodingSuccess(String lat, String lon) {
                activity.setLatitude(Double.parseDouble(lat));
                activity.setLongitude(Double.parseDouble(lon));

                if (activity.getType().equalsIgnoreCase(MOVING_ACTIVITY_TYPE_NAME)) {
                    endUtility.search(activity.getEnd_location(), 1);
                } else {
                    onNewActivityCreated();
                }
            }

            @Override
            public void onGeocodingFailure(Exception exception) {
                activity.setLatitude(0);
                activity.setLongitude(0);
                onNewActivityCreated();
            }
        });

        TextView activity_title = view.findViewById(R.id.activity_new_title);
        activity_title.setText(title);

        TextInputLayout where1 = view.findViewById(R.id.activity_new_where1_edit);
        EditText where1Edit = view.findViewById(R.id.activity_new_where_edittext1);
        MaterialButton when1 = view.findViewById(R.id.activity_new_when1_edit);
        TextInputLayout descr = view.findViewById(R.id.activity_new_descr_textlayout);
        MaterialSwitch moving = view.findViewById(R.id.activity_new_moving);
        TextInputLayout where2 = view.findViewById(R.id.activity_new_where2_edit);
        MaterialButton when2 = view.findViewById(R.id.activity_new_when2_edit);
        ImageView whereArrow = view.findViewById(R.id.activity_new_where_arrow_edit);
        ImageView whenArrow = view.findViewById(R.id.activity_new_when_arrow_edit);
        RecyclerView participatingRV = view.findViewById(R.id.activity_new_participant_recycler_edit);
        RecyclerView notParticipatingRV = view.findViewById(R.id.activity_new_not_participant_recycler_edit);

        moving.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                //MOVING
                where1Edit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                where2.setVisibility(View.VISIBLE);
                when2.setVisibility(View.VISIBLE);
                whereArrow.setVisibility(View.VISIBLE);
                whenArrow.setVisibility(View.VISIBLE);
            } else {
                //STATIC
                where1Edit.setImeOptions(EditorInfo.IME_ACTION_DONE);
                where2.setVisibility(View.GONE);
                when2.setVisibility(View.GONE);
                whereArrow.setVisibility(View.GONE);
                whenArrow.setVisibility(View.GONE);
            }
        });

        when1.setOnClickListener(view12 -> {
            final Calendar c = Calendar.getInstance();

            if (when1.getText() != null && !when1.getText().toString().isEmpty()) {
                Date date1 = df.parse(when1.getText().toString(), new ParsePosition(0));

                if (date1 != null) {
                    c.setTime(date1);
                }
            }

            new DatePickerDialog(context, (datePicker, i, i1, i2) ->
                    new TimePickerDialog(context, (timePicker, j, j1) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(i, i1, i2, j, j1);
                        when1.setText(df.format(new Date(calendar.getTimeInMillis())));
                    }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show(),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)).show();
        });

        when2.setOnClickListener(view12 -> {
            final Calendar c = Calendar.getInstance();

            if (when2.getText() != null && !when2.getText().toString().isEmpty()) {
                Date date2 = df.parse(when2.getText().toString(), new ParsePosition(0));

                if (date2 != null) {
                    c.setTime(date2);
                }
            }

            new DatePickerDialog(context, (datePicker, i, i1, i2) ->
                    new TimePickerDialog(context, (timePicker, j, j1) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(i, i1, i2, j, j1);
                        when2.setText(df.format(new Date(calendar.getTimeInMillis())));
                    }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show(),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)).show();
        });

        MaterialButton materialButton = view.findViewById(R.id.activity_new_confirm);
        materialButton.setOnClickListener(view1 -> {
            if (where1.getEditText() != null && where1.getEditText().getText().toString().trim().isEmpty()) {
                where1.setError(getString(R.string.activity_field_error));
                return;
            } else {
                where1.setErrorEnabled(false);
            }

            if (when1.getText().toString().trim().isEmpty()) {
                when1.setError(getString(R.string.unexpected_error));
                return;
            } else {
                where1.setErrorEnabled(false);
            }

            if (descr.getEditText() == null) {
                return;
            }

            String location = where1.getEditText().getText().toString().trim();

            Date parsed = df.parse(when1.getText().toString(), new ParsePosition(0));
            if (parsed == null) {
                when1.setError(getString(R.string.unexpected_error));
                return;
            } else {
                where1.setErrorEnabled(false);
            }
            long date = parsed.getTime();

            String description = descr.getEditText().getText().toString().trim();

            this.activity = new Activity();
            this.activity.setId(UUID.randomUUID().toString());
            this.activity.setTitle(title);
            this.activity.setLocation(location);
            this.activity.setStart_date(date);
            this.activity.setDescription(description);
            this.activity.setParticipant(new PersonListHolder(this.personList));
            this.activity.setTrip_id(this.trip.getId());

            if (this.personList.size() == this.trip.getParticipant().getPersonList().size()) {
                this.activity.setEveryoneParticipate(true);
            }

            if (moving.isChecked()) {
                if (where2.getEditText() != null && where2.getEditText().getText().toString().trim().isEmpty()) {
                    where2.setError(getString(R.string.activity_field_error));
                    return;
                } else {
                    where2.setErrorEnabled(false);
                }

                if (when2.getText().toString().trim().isEmpty()) {
                    when2.setError(getString(R.string.unexpected_error));
                    return;
                } else {
                    where2.setErrorEnabled(false);
                }

                this.activity.setType(Constants.MOVING_ACTIVITY_TYPE_NAME);

                String location2 = where2.getEditText().getText().toString().trim();

                parsed = df.parse(when2.getText().toString(), new ParsePosition(0));

                if (parsed == null) {
                    when2.setError(getString(R.string.unexpected_error));
                    return;
                } else {
                    where2.setErrorEnabled(false);
                }

                long date2 = parsed.getTime();

                if (date > date2) {
                    when2.setError(getString(R.string.activity_field_error));
                    return;
                }

                this.activity.setEnd_location(location2);
                this.activity.setEnd_date(date2);
            } else {
                this.activity.setType(STATIC_ACTIVITY_TYPE_NAME);
            }

            this.activity.checkCompleted();

            if (this.trip != null) {
                utility.search(location, 1);

                this.trip.getActivity().getActivityList().add(this.activity);
            }
        });

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE);
        }

        this.viewModel.getTrips(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        List<Trip> trips = ((Result.TripSuccess) result).getData().getTripList();

                        this.trip = null;

                        for (Trip trip : trips) {
                            if (trip.getId().equals(tripId)) {
                                this.trip = trip;
                                break;
                            }
                        }

                        if (this.trip == null || !this.trip.isParticipating() || this.trip.isDeleted()) {
                            requireActivity().finish();
                            return;
                        }

                        if (this.trip.getParticipant() == null
                                || this.trip.getParticipant().getPersonList() == null) {
                            return;
                        }

                        this.personList = new ArrayList<>(trip.getParticipant().getPersonList());
                        this.notParticipating = new ArrayList<>();

                        this.participantAdapter = new ParticipantRecyclerViewAdapter(
                                this.personList,
                                this.application,
                                position -> {
                                    if (position >= 0 && position < this.personList.size()) {
                                        Person p = this.personList.remove(position);
                                        this.notParticipating.add(p);
                                        int size = this.notParticipating.size();
                                        this.notParticipantAdapter.notifyItemRangeInserted(size - 1,
                                                size);
                                        this.participantAdapter.notifyItemRemoved(position);
                                    }
                                });

                        this.notParticipantAdapter = new ParticipantRecyclerViewAdapter(
                                this.notParticipating,
                                this.application,
                                position -> {
                                    if (position >= 0 && position < this.notParticipating.size()) {
                                        Person p = this.notParticipating.remove(position);
                                        this.personList.add(p);
                                        int size = this.personList.size();
                                        this.participantAdapter.notifyItemRangeInserted(size - 1,
                                                size);
                                        this.notParticipantAdapter.notifyItemRemoved(position);
                                    }
                                });

                        participatingRV.setLayoutManager(new LinearLayoutManager(context,
                                LinearLayoutManager.HORIZONTAL, false));
                        notParticipatingRV.setLayoutManager(new LinearLayoutManager(context,
                                LinearLayoutManager.HORIZONTAL, false));

                        participatingRV.setAdapter(participantAdapter);
                        notParticipatingRV.setAdapter(notParticipantAdapter);

                    } else {
                        ErrorMessagesUtil errorMessagesUtil = new ErrorMessagesUtil(this.application);
                        Snackbar.make(view, errorMessagesUtil.getErrorMessage(((Result.Error) result)
                                .getMessage()), Snackbar.LENGTH_SHORT).show();
                    }
                });

    }

    /**
     * This method is called when the activity is created in order to update the trip
     * and schedule the notification for both the activity and the trip.
     */
    private void onNewActivityCreated() {
        this.viewModel.insertActivity(this.activity, this.trip);
        Utility.onActivityCreate(this.trip, this.activity, this.application);
        this.requireActivity().runOnUiThread(() -> requireActivity().onBackPressed());
    }
}
