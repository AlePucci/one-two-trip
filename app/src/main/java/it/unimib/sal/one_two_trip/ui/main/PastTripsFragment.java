package it.unimib.sal.one_two_trip.ui.main;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Trip;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PastTripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PastTripsFragment extends Fragment {

    private static final String TAG = PastTripsFragment.class.getSimpleName();
    private final Set<Trip> pastTrips = new HashSet<>();

    public PastTripsFragment() { }

    public static PastTripsFragment newInstance() {
        return new PastTripsFragment();
    }

    private void showEmptyState (LinearLayout layout) {
        //show no coming trips illustration and message
        ConstraintLayout constraintLayout = new ConstraintLayout(getContext());
        constraintLayout.setId(View.generateViewId());

        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);
        layout.addView(constraintLayout, new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));


        TextView noComingTripsMessage = new TextView(getContext());
        noComingTripsMessage.setId(View.generateViewId());
        noComingTripsMessage.setText(R.string.no_trips_added);
        noComingTripsMessage.setTextSize(20);
        noComingTripsMessage.setGravity(Gravity.CENTER);
        constraintLayout.addView(noComingTripsMessage, new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        set.connect(noComingTripsMessage.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
                ConstraintSet.START, 0);
        set.connect(noComingTripsMessage.getId(), ConstraintSet.END,  ConstraintSet.PARENT_ID,
                ConstraintSet.END, 0);
        set.connect(noComingTripsMessage.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID,
                ConstraintSet.TOP, 0);
        set.connect(noComingTripsMessage.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM, 0);
        set.setVerticalBias(noComingTripsMessage.getId(), 0.6f);

        set.constrainWidth(noComingTripsMessage.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainHeight(noComingTripsMessage.getId(), ConstraintSet.WRAP_CONTENT);
        set.applyTo(constraintLayout);


        ImageView noComingTripsIllustration = new ImageView(getContext());
        noComingTripsIllustration.setId(View.generateViewId());
        noComingTripsIllustration.setImageResource(R.drawable.no_trips);
        noComingTripsIllustration.setAdjustViewBounds(true);
        noComingTripsIllustration.setScaleType(ImageView.ScaleType.FIT_CENTER);

        constraintLayout.addView(noComingTripsIllustration,  new ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        set.connect(noComingTripsIllustration.getId(), ConstraintSet.START, constraintLayout.getId(),
                ConstraintSet.START, 0);
        set.connect(noComingTripsIllustration.getId(), ConstraintSet.END, constraintLayout.getId(),
                ConstraintSet.END, 0);
        set.connect(noComingTripsIllustration.getId(), ConstraintSet.BOTTOM,  noComingTripsMessage.getId(),
                ConstraintSet.TOP, 0);

        set.constrainWidth(noComingTripsIllustration.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainHeight(noComingTripsIllustration.getId(), ConstraintSet.WRAP_CONTENT);
        set.applyTo(constraintLayout);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_past_trips, container, false);

        LinearLayout layout = rootView.findViewById(R.id.fragment_past_trips_layout);
        for(Trip trip : HomeActivity.trips ){
            if(trip.isCompleted()){
                pastTrips.add(trip);
            }
        }

        if(pastTrips.size() == 0) {
            this.showEmptyState(layout);
        }
        return rootView;
    }
}