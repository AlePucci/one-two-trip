package it.unimib.sal.one_two_trip.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.adapter.TripsRecyclerViewAdapter;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.repository.TripsMockRepository;
import it.unimib.sal.one_two_trip.util.ResponseCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComingTripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComingTripsFragment extends Fragment implements ResponseCallback {

    private static final String TAG = ComingTripsFragment.class.getSimpleName();

    private List<Trip> comingTrips;
    private ITripsRepository iTripsRepository;
    private TripsRecyclerViewAdapter tripsRecyclerViewAdapter;


    public ComingTripsFragment() {
    }

    public static ComingTripsFragment newInstance() {
        return new ComingTripsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        comingTrips = new ArrayList<>();
        iTripsRepository = new TripsMockRepository(requireActivity().getApplication(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_coming_trips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerViewComingTrips = view.findViewById(R.id.coming_trips_view);
        TextView emptyStateMessage = view.findViewById(R.id.no_trips_textview);
        ImageView emptyStateIllustration = view.findViewById(R.id.no_trips_imageview);
        TextView comingTripsTitle = view.findViewById(R.id.coming_trips_title);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        tripsRecyclerViewAdapter = new TripsRecyclerViewAdapter(comingTrips,
                requireActivity().getApplication(),
                trip -> {
                    /* TO DO SHARE */
                });

        recyclerViewComingTrips.setLayoutManager(layoutManager);
        recyclerViewComingTrips.setAdapter(tripsRecyclerViewAdapter);

        iTripsRepository.fetchTrips(0);

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

            private void check() {
                int comingTripsCount = tripsRecyclerViewAdapter.getItemCount();

                if (comingTripsCount == 1) {
                    comingTripsTitle.setText(R.string.coming_trips_title_single);
                } else {
                    comingTripsTitle.setText(String.format(
                            getString(R.string.coming_trips_title_multiple),
                            comingTripsCount));
                }
                emptyStateMessage.setText(R.string.no_coming_trips);
                emptyStateIllustration.setVisibility(tripsRecyclerViewAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                emptyStateMessage.setVisibility(tripsRecyclerViewAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                recyclerViewComingTrips.setVisibility(tripsRecyclerViewAdapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
                comingTripsTitle.setVisibility(tripsRecyclerViewAdapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
            }
        });

    /*
        if(noTrips == true){
            // There are no trips at all
            TripsListUtil.showEmptyState(context, layout, NO_TRIPS_ADDED);
            return;
        }

        int comingTripsCount = comingTrips.size();

        if(comingTrips == null || comingTripsCount == 0){
            // There are no coming trips but there are past trips
            TripsListUtil.showEmptyState(context, layout, NO_COMING_TRIPS);
        }
        else{
            TextView comingTripsTitle = new TextView(context);
            comingTripsTitle.setId(View.generateViewId());
            comingTripsTitle.setTextSize(25);

            if(comingTripsCount == 1){
                comingTripsTitle.setText(R.string.coming_trips_title_single);
            }
            else{
                comingTripsTitle.setText(String.format(
                        getString(R.string.coming_trips_title_multiple),
                        comingTripsCount));
            }

            LinearLayout.LayoutParams comingTripsTitleParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            comingTripsTitleParams.setMargins(50, 50, 0, 50);
            layout.addView(comingTripsTitle, comingTripsTitleParams);

            Iterator<Trip> it = this.comingTrips.iterator();

            while(it.hasNext()){
                CardView tripCard = TripsListUtil.createTripCard(context, it.next());

                LinearLayout.LayoutParams tripCardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                tripCardParams.setMargins(0,0,0,80);

                layout.addView(tripCard, tripCardParams);
            }
        } */
    }

    @Override
    public void onSuccess(List<Trip> tripList, long lastUpdate) {
        if (tripList != null || tripList.size() > 0) {
            this.comingTrips.clear();

            List<Trip> temp = new ArrayList<>(tripList);

            Iterator<Trip> tripIt = temp.iterator();
            while(tripIt.hasNext()){
                Trip trip = tripIt.next();
                if (trip != null && trip.isCompleted()) tripIt.remove();
            }
            this.comingTrips.addAll(temp);

            requireActivity().runOnUiThread(() -> tripsRecyclerViewAdapter.notifyDataSetChanged());
        }

    }

    @Override
    public void onFailure(String errorMessage) {
        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                errorMessage, Snackbar.LENGTH_LONG).show();
    }
}
