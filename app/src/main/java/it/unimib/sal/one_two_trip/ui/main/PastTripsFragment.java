package it.unimib.sal.one_two_trip.ui.main;

import static it.unimib.sal.one_two_trip.util.Constants.NO_PAST_TRIPS;
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
import it.unimib.sal.one_two_trip.model.Utils;
import it.unimib.sal.one_two_trip.util.TripsListUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PastTripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PastTripsFragment extends Fragment {

    private static final String TAG = PastTripsFragment.class.getSimpleName();
    private final Set<Trip> pastTrips = new HashSet<>();

    public PastTripsFragment() { }

    public static PastTripsFragment newInstance() { return new PastTripsFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_past_trips, container, false);
        LinearLayout layout = rootView.findViewById(R.id.fragment_past_trips_layout);

        for(Trip trip : Utils.trips){
            if(trip.isCompleted()){
                pastTrips.add(trip);
            }
        }

        if(Utils.trips.length == 0){
            // There are no trips at all
            TripsListUtil.showEmptyState(getContext(), layout, NO_TRIPS_ADDED);
        }
        else if(pastTrips.size() == 0){
            // There are no past trips but there are coming trips
            TripsListUtil.showEmptyState(getContext(), layout, NO_PAST_TRIPS);
        }
        else{
            TextView pastTripsTitle = new TextView(getContext());
            pastTripsTitle.setId(View.generateViewId());
            pastTripsTitle.setTextSize(25);

            pastTripsTitle.setText(R.string.past_trips_title);

            LinearLayout.LayoutParams pastTripsTitleParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            pastTripsTitleParams.setMargins(50, 50, 0, 50);
            layout.addView(pastTripsTitle, pastTripsTitleParams);
            for(Trip trip : pastTrips){
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
