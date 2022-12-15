package it.unimib.sal.one_two_trip.ui.main;

import static it.unimib.sal.one_two_trip.util.Constants.HALF_HOUR;
import static it.unimib.sal.one_two_trip.util.Constants.ONE_DAY;
import static it.unimib.sal.one_two_trip.util.Constants.ONE_HOUR;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_ACTIVITY_NOTIFICATIONS;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_NOTIFICATIONS_ON;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_TRIP_NOTIFICATIONS;
import static it.unimib.sal.one_two_trip.util.Constants.TWELVE_HOURS;
import static it.unimib.sal.one_two_trip.util.Constants.TWO_DAYS;
import static it.unimib.sal.one_two_trip.util.Constants.TWO_HOURS;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.HashSet;
import java.util.Set;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    private ConstraintLayout notifications_layout;
    private SwitchMaterial notifications_switch;
    private ToggleButton twelve_hours_trip;
    private ToggleButton one_day_trip;
    private ToggleButton two_days_trip;
    private ToggleButton half_hour_activity;
    private ToggleButton one_hour_activity;
    private ToggleButton two_hours_activity;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNavigationView = requireActivity()
                .findViewById(R.id.bottom_navigation);
        FloatingActionButton fab = requireActivity().findViewById(R.id.fab);
        bottomNavigationView.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);

        notifications_switch = view.findViewById(R.id.notifications_switch);
        notifications_layout = view.findViewById(R.id.notifications_layout);
        twelve_hours_trip = view.findViewById(R.id.trip_twelve_hours);
        one_day_trip = view.findViewById(R.id.trip_one_day);
        two_days_trip = view.findViewById(R.id.trip_two_days);
        half_hour_activity = view.findViewById(R.id.activity_half_hour);
        one_hour_activity = view.findViewById(R.id.activity_one_hour);
        two_hours_activity = view.findViewById(R.id.activity_two_hours);
        MaterialButton save_button = view.findViewById(R.id.save_settings_button);

        restoreSettings();

        notifications_switch
                .setOnCheckedChangeListener((v, isChecked) -> toggleNotificationsList(isChecked));

        save_button.setOnClickListener(v -> saveSettings());
    }

    private void toggleNotificationsList(boolean isVisible) {
        notifications_layout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void saveSettings() {
        SharedPreferencesUtil sharedPreferencesUtil
                = new SharedPreferencesUtil(requireActivity().getApplication());
        boolean notifications = notifications_switch.isChecked();

        if (notifications) {
            Set<String> trip_notifications = new HashSet<>();
            Set<String> activity_notifications = new HashSet<>();

            // TRIP SETTINGS
            if (twelve_hours_trip.isChecked()) {
                trip_notifications.add(TWELVE_HOURS);
            }
            if (one_day_trip.isChecked()) {
                trip_notifications.add(ONE_DAY);
            }
            if (two_days_trip.isChecked()) {
                trip_notifications.add(TWO_DAYS);
            }

            // ACTIVITY SETTINGS
            if (half_hour_activity.isChecked()) {
                activity_notifications.add(HALF_HOUR);
            }
            if (one_hour_activity.isChecked()) {
                activity_notifications.add(ONE_HOUR);
            }
            if (two_hours_activity.isChecked()) {
                activity_notifications.add(TWO_HOURS);
            }

            sharedPreferencesUtil.writeStringSetData(
                    SHARED_PREFERENCES_FILE_NAME, SHARED_PREFERENCES_TRIP_NOTIFICATIONS,
                    trip_notifications);

            sharedPreferencesUtil.writeStringSetData(SHARED_PREFERENCES_FILE_NAME,
                    SHARED_PREFERENCES_ACTIVITY_NOTIFICATIONS, activity_notifications);
        }

        sharedPreferencesUtil.writeStringData(
                SHARED_PREFERENCES_FILE_NAME, SHARED_PREFERENCES_NOTIFICATIONS_ON,
                String.valueOf(notifications));
    }

    private void restoreSettings() {
        SharedPreferencesUtil sharedPreferencesUtil
                = new SharedPreferencesUtil(requireActivity().getApplication());

        boolean notifications = Boolean.parseBoolean(
                sharedPreferencesUtil.readStringData(
                        SHARED_PREFERENCES_FILE_NAME, SHARED_PREFERENCES_NOTIFICATIONS_ON));

        notifications_switch.setChecked(notifications);

        if (notifications) {
            Set<String> trip_notifications = sharedPreferencesUtil.readStringSetData(
                    SHARED_PREFERENCES_FILE_NAME, SHARED_PREFERENCES_TRIP_NOTIFICATIONS);
            Set<String> activity_notifications = sharedPreferencesUtil.readStringSetData(
                    SHARED_PREFERENCES_FILE_NAME, SHARED_PREFERENCES_ACTIVITY_NOTIFICATIONS);

            if (trip_notifications != null) {
                if (trip_notifications.contains(TWELVE_HOURS)) {
                    twelve_hours_trip.setChecked(true);
                }
                if (trip_notifications.contains(ONE_DAY)) {
                    one_day_trip.setChecked(true);
                }
                if (trip_notifications.contains(TWO_DAYS)) {
                    two_days_trip.setChecked(true);
                }
            }

            if (activity_notifications != null) {
                if (activity_notifications.contains(HALF_HOUR)) {
                    half_hour_activity.setChecked(true);
                }
                if (activity_notifications.contains(ONE_HOUR)) {
                    one_hour_activity.setChecked(true);
                }
                if (activity_notifications.contains(TWO_HOURS)) {
                    two_hours_activity.setChecked(true);
                }
            }
        }
        else toggleNotificationsList(false);
    }
}
