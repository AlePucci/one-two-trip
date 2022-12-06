package it.unimib.sal.one_two_trip;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.MovingActivity;
import it.unimib.sal.one_two_trip.util.TripsListUtil;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripHolder> {
    private final List<Activity> activities;

    public TripAdapter(List<Activity> activities) {
        this.activities = activities;
    }

    public class TripHolder extends RecyclerView.ViewHolder {
        private final TextView item_title;
        private final TextView item_header;
        private final TextView item_descr;
        private final TextView item_pos1;
        private final TextView item_pos2;
        private final TextView item_time1;
        private final TextView item_time2;
        private final ImageView item_separator;

        public TripHolder(@NonNull View itemView) {
            super(itemView);

            item_header = itemView.findViewById(R.id.item_activity_date);
            item_title = itemView.findViewById(R.id.item_activity_title);
            item_descr = itemView.findViewById(R.id.item_activity_descr);
            item_pos1 = itemView.findViewById(R.id.item_activity_pos1);
            item_pos2 = itemView.findViewById(R.id.item_activity_pos2);
            item_time1 = itemView.findViewById(R.id.item_activity_time1);
            item_time2 = itemView.findViewById(R.id.item_activity_time2);
            item_separator = itemView.findViewById(R.id.item_activity_separator);

        }

        public void bind(Activity activity) {
            //Check if it is the first activity of the day
            int index = activities.indexOf(activity);
            if(index == 0 || !isSameDay(activities.get(index - 1).getStart_date(), activity.getStart_date())){
                item_header.setVisibility(View.VISIBLE);

                String date = DateFormat.getDateInstance().format(activity.getStart_date());
                item_header.setText(date);
            } else {
                item_header.setVisibility(View.GONE);
            }

            //Title
            item_title.setText(activity.getTitle());

            //Description
            if(activity.getDescription().equals("")) {
                item_descr.setVisibility(View.GONE);
            } else {
                item_descr.setVisibility(View.VISIBLE);
                item_descr.setText(activity.getDescription());
            }

            //Time and Place
            item_pos1.setText(activity.getLocation());

            DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
            String date = df.format(activity.getStart_date());
            item_time1.setText(date);

            if(activity instanceof MovingActivity) {
                MovingActivity a = (MovingActivity) activity;
                item_pos2.setVisibility(View.VISIBLE);
                item_pos2.setText(a.getEnd_location());

                String end_date = df.format(a.getEnd_date());
                item_time2.setVisibility(View.VISIBLE);
                item_time2.setText(end_date);

                item_separator.setVisibility(View.VISIBLE);
            } else {
                item_pos2.setVisibility(View.GONE);
                item_time2.setVisibility(View.GONE);
                item_separator.setVisibility(View.GONE);
            }
        }
    }

    @NonNull
    @Override
    public TripHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_item, parent, false);

        return new TripHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripHolder holder, int position) {
        Activity activity = activities.get(position);
        holder.bind(activity);
    }

    @Override
    public int getItemCount() {
        if(activities == null) {
            return 0;
        }

        return activities.size();
    }

    private boolean isSameDay(Date date1, Date date2) {
        DateFormat df = DateFormat.getDateInstance();
        String day1 = df.format(date1);
        String day2 = df.format(date2);

        return day1.equals(day2);
    }
}
