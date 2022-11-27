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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.Trip;

/**
 * Custom adapter that extends RecyclerView.Adapter to show an ArrayList of News
 * with a RecyclerView.
 */
public class TripsRecyclerViewAdapter extends
        RecyclerView.Adapter<TripsRecyclerViewAdapter.TripViewHolder> {

    /**
     * Interface to associate a click listener with
     * a RecyclerView item.
     */
    public interface OnItemClickListener {
        void onTripShare(Trip trip);
    }

    private final List<Trip> tripList;
    private final Application application;
    private final OnItemClickListener onItemClickListener;

    public TripsRecyclerViewAdapter(List<Trip> tripList, Application application,
                                   OnItemClickListener onItemClickListener) {
        this.tripList = tripList;
        this.application = application;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.trip_item_home, parent, false);

        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        holder.bind(tripList.get(position));
    }

    @Override
    public int getItemCount() {
        if (tripList != null) {
            return tripList.size();
        }
        return 0;
    }

    /**
     * Custom ViewHolder to bind data to the RecyclerView items.
     */
    public class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView tripTitle;
        private final RecyclerView tripView;
        private final MaterialButton tripShare;
        private final MaterialButton moreButton;
        private final TextView noActivitiesAddedTextView;

        private ActivitiesRecyclerViewAdapter activitiesRecyclerViewAdapter;
        private final RecyclerView.LayoutManager layoutManager;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tripTitle = itemView.findViewById(R.id.trip_title);
            tripView = itemView.findViewById(R.id.trip_view);
            tripShare = itemView.findViewById(R.id.share_trip_button);
            moreButton = itemView.findViewById(R.id.more_button);
            noActivitiesAddedTextView = itemView.findViewById(R.id.no_activities_added_textview);

            layoutManager = new LinearLayoutManager(application.getApplicationContext(),
                    LinearLayoutManager.VERTICAL, false);

            tripShare.setOnClickListener(this);
            moreButton.setOnClickListener(this);
        }

        public void bind(Trip trip) {
            tripTitle.setText(trip.getTitle());

            activitiesRecyclerViewAdapter = new ActivitiesRecyclerViewAdapter(trip.getActivity().activityList,
                    application,
                    activity -> Log.d("click", "click"));
            tripView.setLayoutManager(layoutManager);
            tripView.setAdapter(activitiesRecyclerViewAdapter);

            if(trip.getActivity().activityList == null || trip.getActivity().activityList.size() == 0){
                moreButton.setText(R.string.start_adding_button_text);
                noActivitiesAddedTextView.setText(R.string.no_activities_added);
                noActivitiesAddedTextView.setVisibility(View.VISIBLE);
            }
            else{
                moreButton.setText(R.string.more_button_text);
                noActivitiesAddedTextView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.share_trip_button) {
                /* TO DO: share trip */
            } else {
                /* TO DO: OPEN UP ACTIVITY LIST */
            }
        }
    }
}
