package it.unimib.sal.one_two_trip.adapter;

import android.app.Application;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.util.TripsListUtil;

/**
 * Custom adapter that extends RecyclerView.Adapter to show an ArrayList of News
 * with a RecyclerView.
 */
public class ActivitiesRecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_WITH_DATE = 0;
    private static final int VIEW_WITHOUT_DATE = 1;

    /**
     * Interface to associate a click listener with
     * a RecyclerView item.
     */
    public interface OnItemClickListener {
        void onAttachmentsClick(Activity activity);
    }

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
        Log.d("c", "nop");
        if (activityList.get(position) != null && position != 0) {
            Date lastDate = activityList.get((position - 1)).getStart_date();
            Date thisDate = activityList.get(position).getStart_date();
            Log.d("c", TripsListUtil.compareDate(lastDate, thisDate) + "");
            return TripsListUtil.compareDate(lastDate, thisDate);
        }

        return VIEW_WITH_DATE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("b", viewType + "");
        if (viewType == VIEW_WITH_DATE) {
            return new ActivityViewHolderDate(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.activity_item_home_with_date, parent, false));
        } else {
            return new ActivityViewHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.activity_item_home, parent, false));
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
    }

    @Override
    public int getItemCount() {
        if (activityList != null) {
            return activityList.size();
        }
        return 0;
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

        }

        @Override
        public void onClick(View v) {
            /* TO DO: ATTACHMENTS CLICK */
        }
    }
}
