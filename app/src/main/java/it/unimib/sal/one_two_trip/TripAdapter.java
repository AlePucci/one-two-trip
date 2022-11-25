package it.unimib.sal.one_two_trip;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.util.TripsListUtil;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripHolder> {

    private enum type {HEADER, BODY};

    private int dateSize;
    private List<Activity> activities;

    public TripAdapter(List<Activity> activities) {
        this.activities = activities;

        //Get the unique dates
        if(this.activities != null) {
            List<Date> dateList = new ArrayList<Date>();

            for (Activity a : this.activities) {
                if (!dateList.contains(TripsListUtil.getDateDay(a.getStart_date()))) {
                    dateList.add(TripsListUtil.getDateDay(a.getStart_date()));
                }
            }

            dateSize = dateList.size();
        }
    }

    public class TripHolder extends RecyclerView.ViewHolder {
        private TextView item_header;
        private TextView item_title;

        public TripHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            if(viewType == type.HEADER.ordinal()) {
                item_header = itemView.findViewById(R.id.item_activity_date);
            } else {
                item_title = itemView.findViewById(R.id.item_activity_title);
            }
        }

        public void bind(Activity activity) {
            item_title.setText(activity.getTitle());
        }

        public void bind(Date date) {
            DateFormat df = SimpleDateFormat.getDateInstance();
            item_header.setText(df.format(date));
        }
    }

    @NonNull
    @Override
    public TripHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if(viewType == type.HEADER.ordinal()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_header_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_item, parent, false);
        }

        return new TripHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull TripHolder holder, int position) {
        int index = positionToIndex(position);
        Activity activity = activities.get(index);

        if(getItemViewType(position) == type.HEADER.ordinal()) {
            holder.bind(TripsListUtil.getDateDay(activity.getStart_date()));
        } else {
            holder.bind(activity);
        }
    }

    @Override
    public int getItemCount() {
        //Activities + Unique Dates
        if(activities == null) {
            return 0;
        }

        return activities.size() + dateSize;
    }

    @Override
    public int getItemViewType(int position) {
        int currentPosition = -1;
        List<Date> dateList = new ArrayList<Date>();

        for(Activity a: activities) {
            if(!dateList.contains(TripsListUtil.getDateDay(a.getStart_date()))) {
                dateList.add(TripsListUtil.getDateDay(a.getStart_date()));
                currentPosition += 1;
                if(currentPosition == position) {
                    return type.HEADER.ordinal();
                }
            }

            currentPosition += 1;
            if(currentPosition == position) {
                return type.BODY.ordinal();
            }
        }

        return type.BODY.ordinal();
    }

    private int positionToIndex(int position) {
        int index = -1;
        int currentPosition = -1;
        List<Date> dateList = new ArrayList<Date>();

        for(Activity a: activities) {
            if(!dateList.contains(TripsListUtil.getDateDay(a.getStart_date()))) {
                dateList.add(TripsListUtil.getDateDay(a.getStart_date()));
                currentPosition += 1;
            }
            currentPosition += 1;
            index += 1;

            if(currentPosition >= position) {
                return index;
            }
        }

        return index;
    }
}
