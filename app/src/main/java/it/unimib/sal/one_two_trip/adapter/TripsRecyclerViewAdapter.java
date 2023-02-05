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

import java.util.ArrayList;
import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.database.model.Activity;
import it.unimib.sal.one_two_trip.data.database.model.Trip;

/**
 * Custom adapter that extends RecyclerView.Adapter to show an ArrayList of Trips
 * with a RecyclerView (in the HomeFragment)
 */
public class TripsRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEADER = 0;
    private static final int TRIP = 1;

    private final List<Trip> tripList;
    private final OnItemClickListener onItemClickListener;
    private final Application application;
    private final boolean tripsCompleted;

    public TripsRecyclerViewAdapter(List<Trip> tripList, Application application,
                                    boolean tripsCompleted,
                                    OnItemClickListener onItemClickListener) {
        super();
        this.tripList = tripList;
        this.onItemClickListener = onItemClickListener;
        this.application = application;
        this.tripsCompleted = tripsCompleted;
    }

    /**
     * Check if the item at the given position is the header.
     *
     * @param position the position of the item
     * @return true if the item is the header, false otherwise
     */
    private boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return HEADER;
        }
        return TRIP;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header_home, parent, false));
        } else {
            return new TripViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.trip_item_home, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(this.tripList.size(), this.tripsCompleted);
        } else {
            Trip trip = this.tripList.get(position - 1);
            if (trip == null) return;

            ((TripViewHolder) holder).bind(trip);
        }
    }

    @Override
    public int getItemCount() {
        if (this.tripList != null) {
            return 1 + this.tripList.size();
        }
        return 1;
    }

    /**
     * Interface to associate a click listener with
     * a RecyclerView item.
     */
    public interface OnItemClickListener {

        void onTripShare(Trip trip);

        void onTripClick(Trip trip);

        void onButtonClick(Trip trip);

        void onAttachmentsClick(Trip trip, Activity activity);

        void onActivityClick(Trip trip, Activity activity);
    }

    /**
     * Custom ViewHolder to bind data to the RecyclerView items (trips).
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
                List<Activity> activityList = new ArrayList<>(trip.getActivity().getActivityList());

                if (!trip.isCompleted()) {
                    activityList.removeIf(activity -> activity == null || activity.isCompleted());
                }

                ActivitiesRecyclerViewAdapter activitiesRecyclerViewAdapter =
                        new ActivitiesRecyclerViewAdapter(
                                activityList,
                                new ActivitiesRecyclerViewAdapter.OnItemClickListener() {
                                    @Override
                                    public void onAttachmentsClick(Activity activity) {
                                        onItemClickListener.onAttachmentsClick(trip, activity);

                                    }

                                    @Override
                                    public void onActivityClick(Activity activity) {
                                        onItemClickListener.onActivityClick(trip, activity);
                                    }
                                });

                this.activityView.setLayoutManager(this.layoutManager);
                this.activityView.setAdapter(activitiesRecyclerViewAdapter);
            }

            this.tripTitle.setText(trip.getTitle());

            if (trip.getActivity() == null || trip.getActivity().getActivityList() == null
                    || trip.getActivity().getActivityList().isEmpty()) {
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
                onItemClickListener.onTripShare(tripList.get(getAdapterPosition() - 1));
            } else if (v.getId() == R.id.more_button) {
                onItemClickListener.onButtonClick(tripList.get(getAdapterPosition() - 1));
            } else if (v.getId() == R.id.trip_card_view) {
                onItemClickListener.onTripClick(tripList.get(getAdapterPosition() - 1));
            }
        }
    }

    /**
     * Custom ViewHolder to bind data to the RecyclerView items (header).
     */
    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView headerTitle;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.headerTitle = itemView.findViewById(R.id.header_title);
        }

        public void bind(int tripCount, boolean tripsCompleted) {
            if (tripCount == 0) {
                this.headerTitle.setVisibility(View.GONE);
            } else if (tripCount == 1) {
                this.headerTitle.setText(tripsCompleted ? R.string.past_trips_title
                        : R.string.coming_trips_title_single);
                this.headerTitle.setVisibility(View.VISIBLE);
            } else {
                if (tripsCompleted) {
                    this.headerTitle.setText(R.string.past_trips_title);
                } else {
                    this.headerTitle.setText(String
                            .format(application.getString(R.string.coming_trips_title_multiple),
                                    tripCount));
                }
                this.headerTitle.setVisibility(View.VISIBLE);
            }
        }
    }
}
