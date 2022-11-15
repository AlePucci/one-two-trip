package it.unimib.sal.one_two_trip.ui.main;

import android.os.Bundle;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.Trip;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComingTripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComingTripsFragment extends Fragment {

    private static final String TAG = ComingTripsFragment.class.getSimpleName();
    private final Set<Trip> comingTrips = new HashSet<>();

    public ComingTripsFragment() {}

    public static ComingTripsFragment newInstance() { return new ComingTripsFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_coming_trips, container,
                false);
        LinearLayout layout = rootView.findViewById(R.id.fragment_coming_trips_layout);

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
            }
            else{
                comingTripsTitle.setText(String.format(
                        getString(R.string.coming_trips_title_multiple),
                        comingTripsCount));
            }

            comingTripsTitle.setTextSize(25);

            LinearLayout.LayoutParams comingTripsTitleParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            comingTripsTitleParams.setMargins(50, 50, 0, 50);
            layout.addView(comingTripsTitle, comingTripsTitleParams);

            for(Trip trip : comingTrips){
                CardView cardView = new CardView(getContext(), null, R.style.CardView);

                cardView.setRadius(15);
                cardView.setCardElevation(4);

                ConstraintLayout cardViewLayout = new ConstraintLayout(getContext());
                cardViewLayout.setId(View.generateViewId());
                ConstraintSet set = new ConstraintSet();
                set.clone(cardViewLayout);
                cardViewLayout.setLayoutParams(new ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT));

                TextView tripName = new TextView(getContext());
                tripName.setId(View.generateViewId());
                tripName.setText(trip.getTitle());
                tripName.setTextSize(23);

                cardViewLayout.addView(tripName);
                set.connect(tripName.getId(), ConstraintSet.LEFT, cardViewLayout.getId(),
                        ConstraintSet.LEFT, 50);
                set.connect(tripName.getId(), ConstraintSet.RIGHT, cardViewLayout.getId(),
                        ConstraintSet.RIGHT, 0);
                set.connect(tripName.getId(), ConstraintSet.TOP, cardViewLayout.getId(),
                        ConstraintSet.TOP, 30);
                set.connect(tripName.getId(), ConstraintSet.BOTTOM, cardViewLayout.getId(),
                        ConstraintSet.BOTTOM, 0);
                set.constrainHeight(cardView.getId(), ConstraintSet.WRAP_CONTENT);
                set.constrainWidth(cardView.getId(), ConstraintSet.MATCH_CONSTRAINT);

                set.applyTo(cardViewLayout);

                cardView.addView(cardViewLayout, new CardView.LayoutParams(
                        CardView.LayoutParams.MATCH_PARENT,
                        1000));
                layout.addView(cardView, new CardView.LayoutParams(
                        CardView.LayoutParams.MATCH_PARENT,
                        CardView.LayoutParams.WRAP_CONTENT));




            }

        }

        return rootView;
    }


}