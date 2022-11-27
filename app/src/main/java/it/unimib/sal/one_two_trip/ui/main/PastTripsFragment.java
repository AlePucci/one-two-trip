package it.unimib.sal.one_two_trip.ui.main;

import static it.unimib.sal.one_two_trip.util.Constants.NO_PAST_TRIPS;
import static it.unimib.sal.one_two_trip.util.Constants.NO_TRIPS_ADDED;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.adapter.TripsRecyclerViewAdapter;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.repository.TripsMockRepository;
import it.unimib.sal.one_two_trip.util.ResponseCallback;
import it.unimib.sal.one_two_trip.util.TripsListUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PastTripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PastTripsFragment extends Fragment implements ResponseCallback {

    private static final String TAG = PastTripsFragment.class.getSimpleName();

    private List<Trip> pastTrips;
    private ITripsRepository iTripsRepository;
    private TripsRecyclerViewAdapter tripsRecyclerViewAdapter;

    public PastTripsFragment() {
    }

    public static PastTripsFragment newInstance() {
        return new PastTripsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pastTrips = new ArrayList<>();
        iTripsRepository = new TripsMockRepository(requireActivity().getApplication(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_past_trips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerViewPastTrips = view.findViewById(R.id.past_trips_view);
        TextView emptyStateMessage = view.findViewById(R.id.no_trips_textview);
        ImageView emptyStateIllustration = view.findViewById(R.id.no_trips_imageview);
        TextView pastTripsTitle = view.findViewById(R.id.past_trips_title);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        tripsRecyclerViewAdapter = new TripsRecyclerViewAdapter(pastTrips,
                requireActivity().getApplication(),
                trip -> {
                    /* TO DO SHARE */
                });

        recyclerViewPastTrips.setLayoutManager(layoutManager);
        recyclerViewPastTrips.setAdapter(tripsRecyclerViewAdapter);

        iTripsRepository.fetchTrips(0);

        pastTripsTitle.setText(R.string.past_trips_title);
        emptyStateMessage.setText(R.string.no_coming_trips);

        tripsRecyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                check();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                check();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                check();
            }

            void check() {
                emptyStateIllustration.setVisibility(tripsRecyclerViewAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                emptyStateMessage.setVisibility(tripsRecyclerViewAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                recyclerViewPastTrips.setVisibility(tripsRecyclerViewAdapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
                pastTripsTitle.setVisibility(tripsRecyclerViewAdapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
            }
        });
/*
        if (tripList == null || tripList.size() == 0) {
            // There are no trips at all
            TripsListUtil.showEmptyState(context, layout, NO_TRIPS_ADDED);
            return;
        }

        for (Trip trip : tripList) {
            if (!trip.isCompleted()) tripList.remove(trip);
        }

        int tripListCount = tripList.size();

        if (tripList == null || tripListCount == 0) {
            // There are no past trips but there are coming trips
            TripsListUtil.showEmptyState(context, layout, NO_PAST_TRIPS);
        } else {
            TextView pastTripsTitle = new TextView(context);
            pastTripsTitle.setId(View.generateViewId());
            pastTripsTitle.setTextSize(25);

            pastTripsTitle.setText(R.string.past_trips_title);

            LinearLayout.LayoutParams pastTripsTitleParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            pastTripsTitleParams.setMargins(50, 50, 0, 50);
            layout.addView(pastTripsTitle, pastTripsTitleParams);
            for (Trip trip : tripList) {
                CardView tripCard = TripsListUtil.createTripCard(context, trip);

                LinearLayout.LayoutParams tripCardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                tripCardParams.setMargins(0, 0, 0, 80);

                layout.addView(tripCard, tripCardParams);
            }
        } */
    }

    @Override
    public void onSuccess(List<Trip> tripList, long lastUpdate) {
        if (tripList != null || tripList.size() > 0) {
            this.pastTrips.clear();

            List<Trip> temp = new ArrayList<>(tripList);

            Iterator<Trip> tripIt = temp.iterator();
            while(tripIt.hasNext()){
                Trip trip = tripIt.next();
                if (trip != null && !trip.isCompleted()) tripIt.remove();
            }

            this.pastTrips.addAll(temp);

            requireActivity().runOnUiThread(() -> tripsRecyclerViewAdapter.notifyDataSetChanged());
        }
    }

    @Override
    public void onFailure(String errorMessage) {

    }
}
