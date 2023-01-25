package it.unimib.sal.one_two_trip.adapter;

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

public class SettingsParticipantRecyclerViewAdapter
        extends RecyclerView.Adapter<SettingsParticipantRecyclerViewAdapter.SettingsParticipantHolder> {

    private final List<Person> personList;
    private final OnItemClickListener onItemClickListener;

    public SettingsParticipantRecyclerViewAdapter(List<Person> personList, OnItemClickListener onItemClickListener) {
        this.personList = personList;
        this.onItemClickListener = onItemClickListener;
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
            participantName.setText(person.getName() + " " + person.getSurname());
            participantImage.setText(person.getName().substring(0, 1));
            participantEmail.setText(person.getEmail_address());
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
