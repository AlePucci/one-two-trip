package it.unimib.sal.one_two_trip.adapter;

import static it.unimib.sal.one_two_trip.util.Constants.MOVING_ACTIVITY_TYPE_NAME;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.DateFormat;
import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.database.model.Activity;
import it.unimib.sal.one_two_trip.util.Utility;

/**
 * Custom adapter that extends RecyclerView.Adapter to show an ArrayList of Activities
 * with a RecyclerView (in the TripFragment)
 */
public class TripRecyclerViewAdapter extends RecyclerView.Adapter<TripRecyclerViewAdapter.TripHolder> {

    private final List<Activity> activities;
    private final OnItemClickListener onClickListener;
    private final Application application;

    public TripRecyclerViewAdapter(List<Activity> activities, Application application,
                                   OnItemClickListener onClickListener) {
        super();
        this.activities = activities;
        this.onClickListener = onClickListener;
        this.application = application;
    }

    @NonNull
    @Override
    public TripHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new TripHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_item,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TripHolder holder, int position) {
        Activity activity = this.activities.get(position);
        if (activity == null) return;

        holder.bind(activity);
    }

    @Override
    public int getItemCount() {
        if (this.activities == null) {
            return 0;
        }

        return this.activities.size();
    }

    /**
     * Method to add a list of activities to the adapter, clearing the previous ones and
     * notifying the RecyclerView that the data has changed.
     *
     * @param activities the list of activities to add.
     */
    public void addData(List<Activity> activities) {
        this.activities.clear();
        this.activities.addAll(activities);
        notifyDataSetChanged();
    }

    /**
     * Interface to associate a click listener with
     * a RecyclerView item.
     */
    public interface OnItemClickListener {

        void onActivityClick(int position);

        void onDragClick(int position);

        void onParticipantClick(String id);
    }

    /**
     * Custom ViewHolder to bind data to the RecyclerView items (activities).
     */
    public class TripHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView item_title;
        private final TextView item_header;
        private final TextView item_descr;
        private final TextView item_pos1;
        private final TextView item_pos2;
        private final TextView item_time1;
        private final TextView item_time2;
        private final ImageView item_separator;
        private final RecyclerView participants;
        private final MaterialButton drag_button;
        private final MaterialCardView cardView;
        private final RecyclerView.LayoutManager layoutManager;

        public TripHolder(@NonNull View itemView) {
            super(itemView);

            this.item_header = itemView.findViewById(R.id.item_activity_date);
            this.item_title = itemView.findViewById(R.id.item_activity_title);
            this.item_descr = itemView.findViewById(R.id.item_activity_descr);
            this.item_pos1 = itemView.findViewById(R.id.item_activity_pos1);
            this.item_pos2 = itemView.findViewById(R.id.item_activity_pos2);
            this.item_time1 = itemView.findViewById(R.id.item_activity_time1);
            this.item_time2 = itemView.findViewById(R.id.item_activity_time2);
            this.item_separator = itemView.findViewById(R.id.item_activity_separator);
            this.participants = itemView.findViewById(R.id.participants_recycler);
            this.drag_button = itemView.findViewById(R.id.item_activity_dragbutton);
            this.cardView = itemView.findViewById(R.id.item_activity_cardview);

            this.layoutManager = new LinearLayoutManager(itemView.getContext(),
                    LinearLayoutManager.HORIZONTAL, false);
        }

        public void bind(Activity activity) {
            //Check if it is the first activity of the day
            int index = activities.indexOf(activity);
            long startDate = activity.getStart_date();

            if (index == 0 || activities.get(index - 1) == null
                    || !Utility.compareDate(activities.get(index - 1).getStart_date(), startDate)) {
                item_header.setText(DateFormat.getDateInstance(DateFormat.LONG)
                        .format(startDate));
                item_header.setVisibility(View.VISIBLE);
            } else {
                item_header.setVisibility(View.GONE);
            }

            //Participants
            ParticipantRecyclerViewAdapter adapter = new ParticipantRecyclerViewAdapter(
                    activity.getParticipant().getPersonList(),
                    application,
                    position -> onClickListener.onParticipantClick(activity.getParticipant().getPersonList().get(position).getId()));

            this.participants.setLayoutManager(this.layoutManager);
            this.participants.setAdapter(adapter);

            //Title
            this.item_title.setText(activity.getTitle());

            //Description
            String descr = activity.getDescription();
            if (descr.isEmpty()) {
                this.item_descr.setVisibility(View.GONE);
            } else {
                this.item_descr.setText(descr);
                this.item_descr.setVisibility(View.VISIBLE);
            }

            //Time and Place
            this.item_pos1.setText(activity.getLocation());

            DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
            String date = df.format(startDate);
            this.item_time1.setText(date);

            if (activity.getType().equalsIgnoreCase(MOVING_ACTIVITY_TYPE_NAME)) {
                this.item_pos2.setText(activity.getEnd_location());
                this.item_pos2.setVisibility(View.VISIBLE);

                long endDate = activity.getEnd_date();
                String end_date = df.format(endDate);
                if ((endDate - startDate) < 86400000) {
                    this.item_time2.setText(end_date);
                } else {
                    String longActivity = end_date + "*";
                    this.item_time2.setText(longActivity);
                }
                this.item_time2.setVisibility(View.VISIBLE);

                this.item_separator.setVisibility(View.VISIBLE);
            } else {
                this.item_pos2.setVisibility(View.GONE);
                this.item_time2.setVisibility(View.GONE);
                this.item_separator.setVisibility(View.GONE);
            }

            //Drag Button
            this.drag_button.setOnClickListener(this);

            //CardView
            this.cardView.setOnClickListener(this);

            //ItemView
            this.itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(@NonNull View v) {
            if (v.getId() == R.id.item_activity_dragbutton) {
                onClickListener.onDragClick(getAdapterPosition());
            } else if (v.getId() == R.id.item_activity_cardview) {
                onClickListener.onActivityClick(getAdapterPosition());
            }
        }
    }
}
