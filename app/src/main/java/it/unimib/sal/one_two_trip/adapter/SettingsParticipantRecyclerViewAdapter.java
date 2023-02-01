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

public class SettingsParticipantRecyclerViewAdapter
        extends RecyclerView.Adapter<SettingsParticipantRecyclerViewAdapter.SettingsParticipantHolder> {

    private final List<Person> personList;
    private final OnItemClickListener onItemClickListener;
    private final Application application;

    public SettingsParticipantRecyclerViewAdapter(List<Person> personList, Application application,
                                                  OnItemClickListener onItemClickListener) {
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
        holder.bind(this.personList.get(position));
    }

    @Override
    public int getItemCount() {
        if (this.personList == null) {
            return 0;
        }

        return this.personList.size();
    }

    public interface OnItemClickListener {
        void onClick(int position);

        void onRemoveClick(int position);
    }

    public class SettingsParticipantHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView participantName;
        private final TextView participantImage;
        private final TextView participantEmail;
        private final MaterialButton removeParticipant;

        public SettingsParticipantHolder(@NonNull View itemView) {
            super(itemView);
            participantName = itemView.findViewById(R.id.participant_settings_name);
            participantImage = itemView.findViewById(R.id.participant_settings_image);
            participantEmail = itemView.findViewById(R.id.participant_settings_email);
            removeParticipant = itemView.findViewById(R.id.participant_settings_remove);
        }

        public void bind(Person person) {
            String fullName = person.getName() + " " + person.getSurname();
            participantName.setText(fullName);
            participantImage.setText(fullName.substring(0, 1));
            participantEmail.setText(person.getEmail_address());

            SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);
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
            participantImage.setBackgroundTintList(ColorStateList.valueOf(color));

            removeParticipant.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.participant_settings_remove) {
                onItemClickListener.onRemoveClick(getAdapterPosition());
            } else {
                onItemClickListener.onClick(getAdapterPosition());
            }
        }
    }
}
