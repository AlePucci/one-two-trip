package it.unimib.sal.one_two_trip.adapter;

import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.USER_COLOR;

import android.app.Application;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Person;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;
import it.unimib.sal.one_two_trip.util.Utility;

/**
 * Custom adapter that extends RecyclerView.Adapter to show an ArrayList of Participants
 * with a RecyclerView (in the TripSettingsFragment)
 */
public class SettingsParticipantRecyclerViewAdapter
        extends RecyclerView.Adapter<SettingsParticipantRecyclerViewAdapter.SettingsParticipantHolder> {

    private final List<Person> personList;
    private final OnItemClickListener onItemClickListener;
    private final Application application;

    public SettingsParticipantRecyclerViewAdapter(List<Person> personList, Application application,
                                                  OnItemClickListener onItemClickListener) {
        super();
        this.personList = personList;
        this.onItemClickListener = onItemClickListener;
        this.application = application;
    }

    @NonNull
    @Override
    public SettingsParticipantHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new SettingsParticipantHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.settings_participant_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsParticipantHolder holder, int position) {
        Person person = this.personList.get(position);
        if (person == null) return;

        holder.bind(person);
    }

    @Override
    public int getItemCount() {
        if (this.personList == null) {
            return 0;
        }

        return this.personList.size();
    }

    /**
     * Interface to associate a click listener with
     * a RecyclerView item.
     */
    public interface OnItemClickListener {
        void onClick(int position);

        void onRemoveClick(int position);
    }

    /**
     * Custom ViewHolder to bind data to the RecyclerView items (participants).
     */
    public class SettingsParticipantHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView participantName;
        private final TextView participantImage;
        private final TextView participantEmail;
        private final MaterialButton removeParticipant;
        private final SharedPreferencesUtil sharedPreferencesUtil;

        public SettingsParticipantHolder(@NonNull View itemView) {
            super(itemView);
            this.participantName = itemView.findViewById(R.id.participant_settings_name);
            this.participantImage = itemView.findViewById(R.id.participant_settings_image);
            this.participantEmail = itemView.findViewById(R.id.participant_settings_email);
            this.removeParticipant = itemView.findViewById(R.id.participant_settings_remove);
            this.sharedPreferencesUtil = new SharedPreferencesUtil(application);
        }

        public void bind(@NonNull Person person) {
            String fullName = person.getName() + " " + person.getSurname();
            this.participantName.setText(fullName);
            this.participantImage.setText(fullName.substring(0, 1));
            this.participantEmail.setText(person.getEmail_address());

            int color;
            if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    USER_COLOR + "_" + person.getId()) != null) {
                color = Integer.parseInt(sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                        USER_COLOR + "_" + person.getId()));
            } else {
                color = Utility.getRandomColor();
                sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME,
                        USER_COLOR + "_" + person.getId(), String.valueOf(color));
            }

            this.participantImage.setBackgroundTintList(ColorStateList.valueOf(color));

            this.removeParticipant.setOnClickListener(this);
            this.itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(@NonNull View v) {
            if (v.getId() == R.id.participant_settings_remove) {
                onItemClickListener.onRemoveClick(getAdapterPosition());
            } else {
                onItemClickListener.onClick(getAdapterPosition());
            }
        }
    }
}
