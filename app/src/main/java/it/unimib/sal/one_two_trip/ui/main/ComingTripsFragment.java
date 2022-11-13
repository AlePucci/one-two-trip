package it.unimib.sal.one_two_trip.ui.main;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Trip;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComingTripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComingTripsFragment extends Fragment {

    private static final String TAG = ComingTripsFragment.class.getSimpleName();

    private Set<Trip> comingTrips = new HashSet<>();

    public ComingTripsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ComingTripsFragment newInstance() {
        return new ComingTripsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_coming_trips, container, false);

        ConstraintLayout layout = rootView.findViewById(R.id.fragment_coming_trips_layout);

        for(Trip trip : HomeActivity.trips ){
            if(!trip.isCompleted()){
                comingTrips.add(trip);
            }
        }

        if(comingTrips.size() != 0){
            int comingTripsCount = comingTrips.size();
            TextView comingTripsTitle = new TextView(getContext());
            if(comingTripsCount == 1){
                comingTripsTitle.setText(R.string.coming_trips_title_single);
                Log.d(TAG, "onCreate: " + comingTripsTitle.getText());
            }
            else{
                comingTripsTitle.setText(String.format(getString(R.string.coming_trips_title_multiple), comingTripsCount));
                Log.d(TAG, "onCreate: " + comingTripsTitle.getText());
            }

            comingTripsTitle.setTextSize(22);
            layout.addView(comingTripsTitle, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        return rootView;
    }


}