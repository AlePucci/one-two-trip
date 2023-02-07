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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.database.model.Person;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;
import it.unimib.sal.one_two_trip.util.Utility;

/**
 * Custom adapter that extends RecyclerView.Adapter to show an ArrayList of Participants
 * with a RecyclerView (in the TripFragment/ActivityFragment)
 */
public class ParticipantRecyclerViewAdapter
        extends RecyclerView.Adapter<ParticipantRecyclerViewAdapter.ParticipantHolder> {

    private final List<Person> personList;
    private final OnItemClickListener onItemClickListener;
    private final Application application;

    public ParticipantRecyclerViewAdapter(List<Person> personList, Application application,
                                          OnItemClickListener onItemClickListener) {
        super();
        this.personList = personList;
        this.onItemClickListener = onItemClickListener;
        this.application = application;
    }

    @NonNull
    @Override
    public ParticipantHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new ParticipantHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.participant_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantHolder holder, int position) {
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
    }

    /**
     * Custom ViewHolder to bind data to the RecyclerView items (participants).
     */
    public class ParticipantHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView participantName;
        private final TextView participantImage;
        private final ConstraintLayout participantLayout;
        private final SharedPreferencesUtil sharedPreferencesUtil;

        public ParticipantHolder(@NonNull View itemView) {
            super(itemView);
            this.participantName = itemView.findViewById(R.id.participant_name);
            this.participantImage = itemView.findViewById(R.id.participant_image);
            this.participantLayout = itemView.findViewById(R.id.participant_constraint);
            this.sharedPreferencesUtil = new SharedPreferencesUtil(application);
        }

        public void bind(@NonNull Person person) {
            String fullName = person.getName() + " " + person.getSurname();
            this.participantName.setText(fullName);
            this.participantImage.setText(fullName.substring(0, 1));

            int color;

            String id = person.getId();
            if (this.sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    USER_COLOR + "_" + id) != null) {
                color = Integer.parseInt(this.sharedPreferencesUtil.readStringData(
                        SHARED_PREFERENCES_FILE_NAME, USER_COLOR + "_" + id));
            } else {
                color = Utility.getRandomColor();
                this.sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME,
                        USER_COLOR + "_" + id, String.valueOf(color));
            }

            this.participantImage.setBackgroundTintList(ColorStateList.valueOf(color));
            this.participantLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onClick(getAdapterPosition());
        }
    }
}
