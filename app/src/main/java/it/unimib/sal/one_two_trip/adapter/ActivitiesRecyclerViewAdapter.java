package it.unimib.sal.one_two_trip.adapter;

import static it.unimib.sal.one_two_trip.util.Constants.MAX_ACTIVITIES_PER_TRIP_HOME;
import static it.unimib.sal.one_two_trip.util.Constants.MOVING_ACTIVITY_TYPE_NAME;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.database.model.Activity;
import it.unimib.sal.one_two_trip.util.Utility;

/**
 * Custom adapter that extends RecyclerView.Adapter to show an ArrayList of Activities
 * with a RecyclerView (in the HomeFragment)
 */
public class ActivitiesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int STATIC_ACTIVITY = 0;
    private static final int MOVING_ACTIVITY = 1;

    private final List<Activity> activityList;
    private final OnItemClickListener onItemClickListener;

    public ActivitiesRecyclerViewAdapter(List<Activity> activityList,
                                         OnItemClickListener onItemClickListener) {
        super();
        this.activityList = activityList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (this.activityList.get(position) != null
                && this.activityList.get(position).getType() != null
                && this.activityList.get(position).getType()
                .equalsIgnoreCase(MOVING_ACTIVITY_TYPE_NAME)) {
            return MOVING_ACTIVITY;
        }
        return STATIC_ACTIVITY;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MOVING_ACTIVITY) {
            return new MovingActivityViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.moving_activity_item_home, parent, false));
        } else {
            return new ActivityViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_item_home, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Activity activity = this.activityList.get(position);

        if (activity == null) return;

        if (holder instanceof MovingActivityViewHolder) {
            ((MovingActivityViewHolder) holder).bind(activity, position);
        } else {
            ((ActivityViewHolder) holder).bind(activity, position);
        }
    }

    @Override
    public int getItemCount() {
        if (this.activityList != null) {
            return Math.min(this.activityList.size(), MAX_ACTIVITIES_PER_TRIP_HOME);
        }
        return 0;
    }

    /**
     * Utility method to check if the activity is the first one of the list or the first one
     * of the day.
     *
     * @param position the position of the activity in the list.
     * @return true if the activity is the first one of the list or the first one of the day,
     * false otherwise.
     */
    protected boolean isActivityFirstOfTheDay(int position) {
        if (position != 0 && this.activityList.get(position) != null
                && this.activityList.get(position - 1) != null) {
            long lastDate = this.activityList.get((position - 1)).getStart_date();
            long thisDate = this.activityList.get(position).getStart_date();
            return !Utility.compareDate(lastDate, thisDate);
        }
        return true;
    }

    /**
     * Interface to associate a click listener with
     * a RecyclerView item.
     */
    public interface OnItemClickListener {

        void onAttachmentsClick(Activity activity);

        void onActivityClick(Activity activity);
    }

    /**
     * Custom ViewHolder to bind data to the RecyclerView items (static activities).
     */
    public class ActivityViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final TextView activityDate;
        private final TextView activityName;
        private final TextView activityStartTime;
        private final TextView participants;
        private final MaterialButton attachments;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            this.activityDate = itemView.findViewById(R.id.activity_date);
            this.activityName = itemView.findViewById(R.id.activity_name);
            this.activityStartTime = itemView.findViewById(R.id.activity_start_time);
            this.participants = itemView.findViewById(R.id.participants);
            this.attachments = itemView.findViewById(R.id.attachments);

            itemView.setOnClickListener(this);
            this.attachments.setOnClickListener(this);
        }

        public void bind(@NonNull Activity activity, int position) {
            this.activityName.setText(activity.getTitle());

            long start_date = activity.getStart_date();
            this.activityStartTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT)
                    .format(start_date));

            if (isActivityFirstOfTheDay(position)) {
                this.activityDate.setText(DateFormat.getDateInstance(DateFormat.LONG)
                        .format(start_date));
                this.activityDate.setVisibility(View.VISIBLE);
            } else {
                this.activityDate.setVisibility(View.GONE);
            }

            if (activity.isEveryoneParticipate() ||
                    activity.getParticipant() == null
                    || activity.getParticipant().getPersonList() == null
                    || activity.getParticipant().getPersonList().isEmpty()) {
                this.participants.setVisibility(View.GONE);
            } else {
                this.participants.setText(
                        String.valueOf(activity.getParticipant().getPersonList().size())
                );
                this.participants.setVisibility(View.VISIBLE);
            }

            if (activity.getAttachment() == null || activity.getAttachment().isEmpty()) {
                this.attachments.setVisibility(View.GONE);
            } else {
                this.attachments.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(@NonNull View v) {
            if (v.getId() == R.id.attachments) {
                onItemClickListener.onAttachmentsClick(activityList.get(getAdapterPosition()));
            } else {
                onItemClickListener.onActivityClick(activityList.get(getAdapterPosition()));
            }
        }
    }

    /**
     * Custom ViewHolder to bind data to the RecyclerView items (moving activities).
     */
    public class MovingActivityViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private final TextView activityDate;
        private final TextView activityName;
        private final TextView activityStartTime;
        private final TextView activityStartLocation;
        private final TextView activityEndTime;
        private final TextView activityEndLocation;
        private final TextView participants;
        private final MaterialButton attachments;

        public MovingActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            this.activityDate = itemView.findViewById(R.id.activity_date);
            this.activityName = itemView.findViewById(R.id.activity_name);
            this.activityStartTime = itemView.findViewById(R.id.activity_start_time);
            this.activityStartLocation = itemView.findViewById(R.id.activity_start_location);
            this.activityEndTime = itemView.findViewById(R.id.activity_end_time);
            this.activityEndLocation = itemView.findViewById(R.id.activity_end_location);
            this.participants = itemView.findViewById(R.id.participants);
            this.attachments = itemView.findViewById(R.id.attachments);

            itemView.setOnClickListener(this);
            this.attachments.setOnClickListener(this);
        }

        public void bind(@NonNull Activity activity, int position) {
            this.participants.setVisibility(View.GONE);

            this.activityName.setText(activity.getTitle());

            long start_date = activity.getStart_date();
            long end_date = activity.getEnd_date();
            this.activityStartTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT)
                    .format(start_date));
            this.activityStartLocation.setText(activity.getLocation());

            if ((end_date - start_date) < 86400000) {
                this.activityEndTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT)
                        .format(end_date));
            } else {
                String longActivity = DateFormat.getTimeInstance(DateFormat.SHORT)
                        .format(end_date) + "*";
                this.activityEndTime.setText(longActivity);
            }
            this.activityEndLocation.setText(activity.getEnd_location());

            if (isActivityFirstOfTheDay(position)) {
                this.activityDate.setText(DateFormat.getDateInstance(DateFormat.LONG)
                        .format(start_date));
                this.activityDate.setVisibility(View.VISIBLE);
            } else {
                this.activityDate.setVisibility(View.GONE);
            }

            if (activity.getAttachment() == null || activity.getAttachment().isEmpty()) {
                this.attachments.setVisibility(View.GONE);
            } else {
                this.attachments.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(@NonNull View v) {
            if (v.getId() == R.id.attachments) {
                onItemClickListener.onAttachmentsClick(activityList.get(getAdapterPosition()));
            } else {
                onItemClickListener.onActivityClick(activityList.get(getAdapterPosition()));
            }
        }
    }
}
