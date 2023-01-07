package it.unimib.sal.one_two_trip.adapter;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.Trip;

/**
 * Custom adapter that extends RecyclerView.Adapter to show an ArrayList of Trips
 * with a RecyclerView.
 */
public class TripsRecyclerViewAdapter
        extends RecyclerView.Adapter<TripsRecyclerViewAdapter.TripViewHolder> {

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
        return new TripViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_item_home, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        if (this.tripList.get(position) == null) return;
        holder.bind(this.tripList.get(position));
    }

    @Override
    public int getItemCount() {
        if (this.tripList != null) {
            return this.tripList.size();
        }
        return 0;
    }

    /**
     * Interface to associate a click listener with
     * a RecyclerView item.
     */
    public interface OnItemClickListener {
        void onTripShare(Trip trip);

        void onTripClick(Trip trip);

        void onButtonClick(Trip trip);
    }

    /**
     * Custom ViewHolder to bind data to the RecyclerView items.
     */
    public class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView tripTitle;
        private final RecyclerView activityView;
        private final MaterialButton moreButton;
        private final TextView noActivitiesAddedText;
        private final RecyclerView.LayoutManager layoutManager;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tripTitle = itemView.findViewById(R.id.trip_title);
            this.activityView = itemView.findViewById(R.id.trip_view);
            MaterialButton tripShare = itemView.findViewById(R.id.share_trip_button);
            this.moreButton = itemView.findViewById(R.id.more_button);
            this.noActivitiesAddedText = itemView.findViewById(R.id.no_activities_added_text);

            this.layoutManager = new LinearLayoutManager(application.getApplicationContext(),
                    LinearLayoutManager.VERTICAL, false);

            itemView.setOnClickListener(this);
            tripShare.setOnClickListener(this);
            this.moreButton.setOnClickListener(this);
        }

        public void bind(@NonNull Trip trip) {
            if (trip.getActivity() != null && trip.getActivity().getActivityList() != null) {
                ActivitiesRecyclerViewAdapter activitiesRecyclerViewAdapter =
                        new ActivitiesRecyclerViewAdapter(trip.getActivity().activityList,
                                new ActivitiesRecyclerViewAdapter.OnItemClickListener() {
                                    @Override
                                    public void onAttachmentsClick(Activity activity) {
                                        Snackbar.make(itemView, activity.getAttachment().toString(),
                                                Snackbar.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onActivityClick(Activity activity) {
                                        Snackbar.make(itemView, activity.getTitle(),
                                                Snackbar.LENGTH_SHORT).show();
                                    }
                                });

                this.activityView.setLayoutManager(layoutManager);
                this.activityView.setAdapter(activitiesRecyclerViewAdapter);
            }

            this.tripTitle.setText(trip.getTitle());

            if (trip.getActivity() == null || trip.getActivity().activityList == null
                    || trip.getActivity().activityList.isEmpty()) {
                this.moreButton.setText(R.string.start_adding_button_text);
                this.noActivitiesAddedText.setText(R.string.no_activities_added);
                this.noActivitiesAddedText.setVisibility(View.VISIBLE);
            } else {
                this.moreButton.setText(R.string.more_button_text);
                this.noActivitiesAddedText.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(@NonNull View v) {
            if (v.getId() == R.id.share_trip_button) {
                onItemClickListener.onTripShare(tripList.get(getAdapterPosition()));
            } else if (v.getId() == R.id.more_button) {
                onItemClickListener.onButtonClick(tripList.get(getAdapterPosition()));
            } else {
                onItemClickListener.onTripClick(tripList.get(getAdapterPosition()));
            }
        }
    }
}
