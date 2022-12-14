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
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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

    private MaterialCardView notifications_cardview;
    private TextView notifications_disabled;
    private SwitchMaterial notifications_switch;
    private ToggleButton twelve_hours_trip;
    private ToggleButton one_day_trip;
    private ToggleButton two_days_trip;
    private ToggleButton half_hour_activity;
    private ToggleButton one_hour_activity;
    private ToggleButton two_hours_activity;

    public SettingsFragment() {
    }

    @NonNull
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        this.notifications_switch = view.findViewById(R.id.notifications_switch);
        this.notifications_cardview = view.findViewById(R.id.notifications_cardview);
        this.notifications_disabled = view.findViewById(R.id.notifications_disabled);
        this.twelve_hours_trip = view.findViewById(R.id.trip_twelve_hours);
        this.one_day_trip = view.findViewById(R.id.trip_one_day);
        this.two_days_trip = view.findViewById(R.id.trip_two_days);
        this.half_hour_activity = view.findViewById(R.id.activity_half_hour);
        this.one_hour_activity = view.findViewById(R.id.activity_one_hour);
        this.two_hours_activity = view.findViewById(R.id.activity_two_hours);
        MaterialButton save_button = view.findViewById(R.id.save_settings_button);

        this.restoreSettings();

        this.notifications_switch
                .setOnCheckedChangeListener((v, isChecked) -> toggleNotificationsList(isChecked));

        save_button.setOnClickListener(this::saveSettings);
    }

    private void toggleNotificationsList(boolean isVisible) {
        this.notifications_cardview.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        this.notifications_disabled.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }

    private void saveSettings(View v) {
        SharedPreferencesUtil sharedPreferencesUtil
                = new SharedPreferencesUtil(requireActivity().getApplication());
        boolean notifications = this.notifications_switch.isChecked();

        if (notifications) {
            Set<String> trip_notifications = new HashSet<>();
            Set<String> activity_notifications = new HashSet<>();

            // TRIP SETTINGS
            if (this.twelve_hours_trip.isChecked()) {
                trip_notifications.add(TWELVE_HOURS);
            }
            if (this.one_day_trip.isChecked()) {
                trip_notifications.add(ONE_DAY);
            }
            if (this.two_days_trip.isChecked()) {
                trip_notifications.add(TWO_DAYS);
            }

            // ACTIVITY SETTINGS
            if (this.half_hour_activity.isChecked()) {
                activity_notifications.add(HALF_HOUR);
            }
            if (this.one_hour_activity.isChecked()) {
                activity_notifications.add(ONE_HOUR);
            }
            if (this.two_hours_activity.isChecked()) {
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

        Snackbar.make(v, getString(R.string.settings_saved), Snackbar.LENGTH_SHORT).show();
    }

    private void restoreSettings() {
        SharedPreferencesUtil sharedPreferencesUtil
                = new SharedPreferencesUtil(requireActivity().getApplication());

        boolean notifications = Boolean.parseBoolean(
                sharedPreferencesUtil.readStringData(
                        SHARED_PREFERENCES_FILE_NAME, SHARED_PREFERENCES_NOTIFICATIONS_ON));

        this.notifications_switch.setChecked(notifications);

        if (notifications) {
            Set<String> trip_notifications = sharedPreferencesUtil.readStringSetData(
                    SHARED_PREFERENCES_FILE_NAME, SHARED_PREFERENCES_TRIP_NOTIFICATIONS);
            Set<String> activity_notifications = sharedPreferencesUtil.readStringSetData(
                    SHARED_PREFERENCES_FILE_NAME, SHARED_PREFERENCES_ACTIVITY_NOTIFICATIONS);

            if (trip_notifications != null) {
                if (trip_notifications.contains(TWELVE_HOURS)) {
                    this.twelve_hours_trip.setChecked(true);
                }
                if (trip_notifications.contains(ONE_DAY)) {
                    this.one_day_trip.setChecked(true);
                }
                if (trip_notifications.contains(TWO_DAYS)) {
                    this.two_days_trip.setChecked(true);
                }
            }

            if (activity_notifications != null) {
                if (activity_notifications.contains(HALF_HOUR)) {
                    this.half_hour_activity.setChecked(true);
                }
                if (activity_notifications.contains(ONE_HOUR)) {
                    this.one_hour_activity.setChecked(true);
                }
                if (activity_notifications.contains(TWO_HOURS)) {
                    this.two_hours_activity.setChecked(true);
                }
            }
        } else {
            this.toggleNotificationsList(false);
        }
    }
}
