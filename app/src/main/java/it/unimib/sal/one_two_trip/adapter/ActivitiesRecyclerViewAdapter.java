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
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.util.Utility;

/**
 * Custom adapter that extends RecyclerView.Adapter to show an ArrayList of Activities
 * with a RecyclerView.
 */
public class ActivitiesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ACTIVITY = 0;
    private static final int MOVING_ACTIVITY = 1;

    private final List<Activity> activityList;
    private final OnItemClickListener onItemClickListener;

    public ActivitiesRecyclerViewAdapter(List<Activity> activityList,
                                         OnItemClickListener onItemClickListener) {
        this.activityList = activityList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (activityList.get(position) != null && activityList.get(position).getType() != null
                && activityList.get(position).getType()
                .equalsIgnoreCase(MOVING_ACTIVITY_TYPE_NAME)) {
            return MOVING_ACTIVITY;
        }
        return ACTIVITY;
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
        Activity activity = activityList.get(position);
        if (holder instanceof MovingActivityViewHolder) {
            ((MovingActivityViewHolder) holder).bind(activity, position);
        } else {
            ((ActivityViewHolder) holder).bind(activity, position);
        }
    }

    @Override
    public int getItemCount() {
        if (activityList != null) {
            return Math.min(activityList.size(), MAX_ACTIVITIES_PER_TRIP_HOME);
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
        if (activityList.get(position) != null && position != 0) {
            long lastDate = activityList.get((position - 1)).getStart_date();
            long thisDate = activityList.get(position).getStart_date();
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
     * Custom ViewHolder to bind data to the RecyclerView items (activities).
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
            activityDate = itemView.findViewById(R.id.activity_date);
            activityName = itemView.findViewById(R.id.activity_name);
            activityStartTime = itemView.findViewById(R.id.activity_start_time);
            participants = itemView.findViewById(R.id.participants);
            attachments = itemView.findViewById(R.id.attachments);

            itemView.setOnClickListener(this);
            attachments.setOnClickListener(this);
        }

        public void bind(Activity activity, int position) {
            activityName.setText(activity.getTitle());
            activityStartTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT)
                    .format(activity.getStart_date()));

            if (isActivityFirstOfTheDay(position)) {
                activityDate.setText(DateFormat.getDateInstance(DateFormat.LONG)
                        .format(activity.getStart_date()));
                activityDate.setVisibility(View.VISIBLE);
            } else {
                activityDate.setVisibility(View.GONE);
            }

            if (activity.getParticipant() == null || activity.getParticipant().personList == null
                    || activity.getParticipant().personList.isEmpty()
                    || activity.doesEveryoneParticipate()) {
                participants.setVisibility(View.GONE);
            } else {
                participants.setText(String.valueOf(activity.getParticipant().personList.size()));
                participants.setVisibility(View.VISIBLE);
            }

            if (activity.getAttachment() == null || activity.getAttachment().isEmpty()) {
                attachments.setVisibility(View.GONE);
            } else {
                attachments.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.attachments) {
                onItemClickListener.onAttachmentsClick(activityList.get(getAdapterPosition()));
            } else {
                //click on the activity itself
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
            activityDate = itemView.findViewById(R.id.activity_date);
            activityName = itemView.findViewById(R.id.activity_name);
            activityStartTime = itemView.findViewById(R.id.activity_start_time);
            activityStartLocation = itemView.findViewById(R.id.activity_start_location);
            activityEndTime = itemView.findViewById(R.id.activity_end_time);
            activityEndLocation = itemView.findViewById(R.id.activity_end_location);
            participants = itemView.findViewById(R.id.participants);
            attachments = itemView.findViewById(R.id.attachments);

            itemView.setOnClickListener(this);
            attachments.setOnClickListener(this);
        }

        public void bind(Activity activity, int position) {
            participants.setVisibility(View.GONE);

            activityName.setText(activity.getTitle());
            activityStartTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT)
                    .format(activity.getStart_date()));
            activityStartLocation.setText(activity.getLocation());
            activityEndTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT)
                    .format(activity.getEnd_date()));
            activityEndLocation.setText(activity.getEnd_location());

            if (isActivityFirstOfTheDay(position)) {
                activityDate.setText(DateFormat.getDateInstance(DateFormat.LONG)
                        .format(activity.getStart_date()));
                activityDate.setVisibility(View.VISIBLE);
            } else {
                activityDate.setVisibility(View.GONE);
            }

            if (activity.getAttachment() == null || activity.getAttachment().isEmpty()) {
                attachments.setVisibility(View.GONE);
            } else {
                attachments.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.attachments) {
                onItemClickListener.onAttachmentsClick(activityList.get(getAdapterPosition()));
            } else {
                //click on the activity itself
                onItemClickListener.onActivityClick(activityList.get(getAdapterPosition()));
            }
        }
    }
}
