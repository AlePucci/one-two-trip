package it.unimib.sal.one_two_trip.adapter;

import static it.unimib.sal.one_two_trip.util.Constants.MAX_ACTIVITIES_PER_TRIP_HOME;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.util.TripsListUtil;

/**
 * Custom adapter that extends RecyclerView.Adapter to show an ArrayList of News
 * with a RecyclerView.
 */
public class ActivitiesRecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_WITH_DATE = 0;
    private static final int VIEW_WITHOUT_DATE = 1;
    private static final int MOVING_VIEW_WITH_DATE = 2;
    private static final int MOVING_VIEW_WITHOUT_DATE = 3;

    private final List<Activity> activityList;
    private final Application application;
    private final OnItemClickListener onItemClickListener;
    public ActivitiesRecyclerViewAdapter(List<Activity> activityList, Application application,
                                         OnItemClickListener onItemClickListener) {
        this.activityList = activityList;
        this.application = application;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        //IF IT'S A MOVING ACTIVITY
        if (activityList.get(position) != null && activityList.get(position).getType().
                equalsIgnoreCase("moving")) {
            if (activityList.get(position) != null && position != 0) {
                Date lastDate = activityList.get((position - 1)).getStart_date();
                Date thisDate = activityList.get(position).getStart_date();
                return TripsListUtil.compareDate(lastDate, thisDate) + 2;
            }
            return MOVING_VIEW_WITH_DATE;
        }
        //ELSE
        if (activityList.get(position) != null && position != 0) {
            Date lastDate = activityList.get((position - 1)).getStart_date();
            Date thisDate = activityList.get(position).getStart_date();
            return TripsListUtil.compareDate(lastDate, thisDate);
        }

        return VIEW_WITH_DATE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_WITH_DATE: {
                return new ActivityViewHolderDate(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.activity_item_home_with_date, parent, false));
            }
            case VIEW_WITHOUT_DATE: {
                return new ActivityViewHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.activity_item_home, parent, false));
            }
            case MOVING_VIEW_WITH_DATE: {
                return new MovingActivityViewHolderDate(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.moving_activity_item_home_with_date, parent, false));
            }
            case MOVING_VIEW_WITHOUT_DATE: {
                return new MovingActivityViewHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.moving_activity_item_home, parent, false));
            }
            default: {
                return new ActivityViewHolderDate(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.activity_item_home_with_date, parent, false));
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Activity activity = activityList.get(position);
        if (holder instanceof ActivityViewHolderDate) {
            ((ActivityViewHolderDate) holder).bind(activity);
            return;
        }
        if (holder instanceof ActivityViewHolder) {
            ((ActivityViewHolder) holder).bind(activity);
            return;
        }
        if (holder instanceof MovingActivityViewHolderDate) {
            ((MovingActivityViewHolderDate) holder).bind(activity);
            return;
        }
        if (holder instanceof MovingActivityViewHolder) {
            ((MovingActivityViewHolder) holder).bind(activity);
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
     * Interface to associate a click listener with
     * a RecyclerView item.
     */
    public interface OnItemClickListener {
        void onAttachmentsClick(Activity activity);
    }

    /**
     * Custom ViewHolder to bind data to the RecyclerView items.
     */
    public class ActivityViewHolderDate extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView activityDate;
        private final TextView activityName;
        private final TextView activityStartTime;
        private final TextView participants;
        private final MaterialButton attachments;

        public ActivityViewHolderDate(@NonNull View itemView) {
            super(itemView);
            activityDate = itemView.findViewById(R.id.activity_date);
            activityName = itemView.findViewById(R.id.activity_name);
            activityStartTime = itemView.findViewById(R.id.activity_start_time);
            participants = itemView.findViewById(R.id.participants);
            attachments = itemView.findViewById(R.id.attachments);

            attachments.setOnClickListener(this);
        }

        public void bind(Activity activity) {
            activityDate.setText(DateFormat
                    .getDateInstance(DateFormat.LONG)
                    .format(activity.getStart_date()));
            activityName.setText(activity.getTitle());
            activityStartTime.setText(DateFormat
                    .getTimeInstance(DateFormat.SHORT)
                    .format(activity.getStart_date()));

            if (activity.getParticipant().personList == null || activity.getParticipant().personList.isEmpty() ||
                    activity.doesEveryoneParticipate()) {
                participants.setVisibility(View.GONE);
            }
            else{
                participants.setText(String.valueOf(activity.getParticipant().personList.size()));
                participants.setVisibility(View.VISIBLE);
            }

            if(activity.getAttachment() == null || activity.getAttachment().isEmpty()){
                attachments.setVisibility(View.GONE);
            }
            else{
                attachments.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public void onClick(View v) {
            /* TO DO: ATTACHMENTS CLICK */
        }
    }

    /**
     * Custom ViewHolder to bind data to the RecyclerView items.
     */
    public class ActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView activityName;
        private final TextView activityStartTime;
        private final TextView participants;
        private final MaterialButton attachments;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            activityName = itemView.findViewById(R.id.activity_name);
            activityStartTime = itemView.findViewById(R.id.activity_start_time);
            participants = itemView.findViewById(R.id.participants);
            attachments = itemView.findViewById(R.id.attachments);

            attachments.setOnClickListener(this);
        }

        public void bind(Activity activity) {
            activityName.setText(activity.getTitle());
            activityStartTime.setText(DateFormat
                    .getTimeInstance(DateFormat.SHORT)
                    .format(activity.getStart_date()));

            if (activity.getParticipant().personList == null || activity.getParticipant().personList.isEmpty() ||
                    activity.doesEveryoneParticipate()) {
                participants.setVisibility(View.GONE);
            }
            else{
                participants.setText(String.valueOf(activity.getParticipant().personList.size()));
                participants.setVisibility(View.VISIBLE);
            }

            if(activity.getAttachment() == null || activity.getAttachment().isEmpty()){
                attachments.setVisibility(View.INVISIBLE);
            }
            else{
                attachments.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            /* TO DO: ATTACHMENTS CLICK */
        }
    }

    /**
     * Custom ViewHolder to bind data to the RecyclerView items.
     */
    public class MovingActivityViewHolderDate extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView activityDate;
        private final TextView activityName;
        private final TextView activityStartTime;
        private final TextView activityStartLocation;
        private final TextView activityEndTime;
        private final TextView activityEndLocation;
        private final TextView participants;
        private final MaterialButton attachments;

        public MovingActivityViewHolderDate(@NonNull View itemView) {
            super(itemView);
            activityDate = itemView.findViewById(R.id.activity_date);
            activityName = itemView.findViewById(R.id.activity_name);
            activityStartTime = itemView.findViewById(R.id.activity_start_time);
            activityStartLocation = itemView.findViewById(R.id.activity_start_location);
            activityEndTime = itemView.findViewById(R.id.activity_end_time);
            activityEndLocation = itemView.findViewById(R.id.activity_end_location);
            participants = itemView.findViewById(R.id.participants);
            attachments = itemView.findViewById(R.id.attachments);

            attachments.setOnClickListener(this);
        }

        public void bind(Activity activity) {
            activityDate.setText(DateFormat
                    .getDateInstance(DateFormat.LONG)
                    .format(activity.getStart_date()));
            activityName.setText(activity.getTitle());
            activityStartTime.setText(DateFormat
                    .getTimeInstance(DateFormat.SHORT)
                    .format(activity.getStart_date()));
            activityStartLocation.setText(activity.getLocation());
            activityEndTime.setText(DateFormat
                    .getTimeInstance(DateFormat.SHORT)
                    .format(activity.getEnd_date()));
            activityEndLocation.setText(activity.getEnd_location());

            // for space reasons
            participants.setVisibility(View.GONE);

            if(activity.getAttachment() == null || activity.getAttachment().isEmpty()){
                attachments.setVisibility(View.INVISIBLE);
            }
            else{
                attachments.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            /* TO DO: ATTACHMENTS CLICK */
        }
    }

    /**
     * Custom ViewHolder to bind data to the RecyclerView items.
     */
    public class MovingActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView activityName;
        private final TextView activityStartTime;
        private final TextView participants;
        private final MaterialButton attachments;

        public MovingActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            activityName = itemView.findViewById(R.id.activity_name);
            activityStartTime = itemView.findViewById(R.id.activity_start_time);
            participants = itemView.findViewById(R.id.participants);
            attachments = itemView.findViewById(R.id.attachments);

            attachments.setOnClickListener(this);
        }

        public void bind(Activity activity) {
            activityName.setText(activity.getTitle());
            activityStartTime.setText(DateFormat
                    .getTimeInstance(DateFormat.SHORT)
                    .format(activity.getStart_date()));

            // for space reasons
            participants.setVisibility(View.GONE);

            if(activity.getAttachment() == null || activity.getAttachment().isEmpty()){
                attachments.setVisibility(View.INVISIBLE);
            }
            else{
                attachments.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            /* TO DO: ATTACHMENTS CLICK */
        }
    }
}
