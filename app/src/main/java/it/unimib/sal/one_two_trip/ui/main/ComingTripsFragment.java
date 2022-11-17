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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
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

        if(comingTrips.size() == 0) {
            this.showEmptyState(layout);
        }
        else{
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
                /*
                set.constrainHeight(cardView.getId(), ConstraintSet.WRAP_CONTENT);
                set.constrainWidth(cardView.getId(), ConstraintSet.MATCH_CONSTRAINT); */

                set.applyTo(cardViewLayout);


                //we have to display the activities of the trip, max 3
                int activitiesCount = 0;
                Date lastDate = null;
                for(Activity activity : trip.getActivity()){
                    if(activitiesCount < 3) {
                        TextView activityName = new TextView(getContext());
                        activityName.setId(View.generateViewId());
                        activityName.setText(activity.getTitle());
                        activityName.setTextSize(19);

                        cardViewLayout.addView(activityName);
                        if(!activity.getStart_date().equals(lastDate)){

                        set.connect(activityName.getId(), ConstraintSet.LEFT, cardViewLayout.getId(),
                                ConstraintSet.LEFT, 50);
                        set.connect(activityName.getId(), ConstraintSet.RIGHT, cardViewLayout.getId(),
                                ConstraintSet.RIGHT, 0);
                        set.connect(activityName.getId(), ConstraintSet.TOP, cardViewLayout.getId(),
                                ConstraintSet.TOP, 250 + activitiesCount * 200);
                        set.connect(activityName.getId(), ConstraintSet.BOTTOM, cardViewLayout.getId(),
                                ConstraintSet.BOTTOM, 0);

                        set.applyTo(cardViewLayout);


                            TextView dateView = new TextView(getContext());
                            dateView.setId(View.generateViewId());
                            Log.d(TAG, "onCreateView: " + activity.getStart_date());
                            dateView.setText(DateFormat.getDateInstance(DateFormat.LONG).format(activity.getStart_date()));

                            dateView.setTextSize(14);


                            cardViewLayout.addView(dateView);
                            set.connect(dateView.getId(), ConstraintSet.LEFT, cardViewLayout.getId(),
                                    ConstraintSet.LEFT, 50);
                            set.connect(dateView.getId(), ConstraintSet.RIGHT, cardViewLayout.getId(),
                                    ConstraintSet.RIGHT, 0);
                            set.connect(dateView.getId(), ConstraintSet.TOP, cardViewLayout.getId(),
                                    ConstraintSet.TOP, 170 + activitiesCount * 200);
                            set.connect(dateView.getId(), ConstraintSet.BOTTOM, cardViewLayout.getId(),
                                    ConstraintSet.BOTTOM, 0);

                            set.applyTo(cardViewLayout);

                            lastDate = activity.getStart_date();
                        }
                        else{
                            set.connect(activityName.getId(), ConstraintSet.LEFT, cardViewLayout.getId(),
                                    ConstraintSet.LEFT, 50);
                            set.connect(activityName.getId(), ConstraintSet.RIGHT, cardViewLayout.getId(),
                                    ConstraintSet.RIGHT, 0);
                            set.connect(activityName.getId(), ConstraintSet.TOP, cardViewLayout.getId(),
                                    ConstraintSet.TOP, 250 + activitiesCount * 150);
                            set.connect(activityName.getId(), ConstraintSet.BOTTOM, cardViewLayout.getId(),
                                    ConstraintSet.BOTTOM, 0);

                            set.applyTo(cardViewLayout);

                        }

                        activitiesCount++;
                    }

                }



                cardView.addView(cardViewLayout, new CardView.LayoutParams(
                        CardView.LayoutParams.MATCH_PARENT,
                        1000));

                LinearLayout.LayoutParams cardViewParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );


                cardViewParams.setMargins(0,0,0,80);
                layout.addView(cardView, cardViewParams);






            }

        }
        return rootView;
    }


}