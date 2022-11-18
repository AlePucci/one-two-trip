package it.unimib.sal.one_two_trip.ui.main;

import static it.unimib.sal.one_two_trip.util.Constants.NO_COMING_TRIPS;
import static it.unimib.sal.one_two_trip.util.Constants.NO_TRIPS_ADDED;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.util.TripsListUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComingTripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComingTripsFragment extends Fragment {

    private static final String TAG = ComingTripsFragment.class.getSimpleName();
    private final Set<Trip> comingTrips = new HashSet<>();

    public ComingTripsFragment() { }

    public static ComingTripsFragment newInstance() { return new ComingTripsFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_coming_trips, container, false);
        LinearLayout layout = rootView.findViewById(R.id.fragment_coming_trips_layout);

        for(Trip trip : HomeActivity.trips ){
            if(!trip.isCompleted()){
                comingTrips.add(trip);
            }
        }

        int comingTripsCount = comingTrips.size();

        if(HomeActivity.trips.length == 0){
            TripsListUtil.showEmptyState(getContext(), layout, NO_TRIPS_ADDED);
        }
        else if(comingTripsCount == 0){
            TripsListUtil.showEmptyState(getContext(), layout, NO_COMING_TRIPS);
        }
        else{
            TextView comingTripsTitle = new TextView(getContext());
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

            for(Trip trip : comingTrips){
                CardView tripCard = TripsListUtil.createTripCard(getContext(), trip);

                LinearLayout.LayoutParams tripCardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                tripCardParams.setMargins(0,0,0,80);
                layout.addView(tripCard, tripCardParams);
            }
        }
        return rootView;
    }
}
