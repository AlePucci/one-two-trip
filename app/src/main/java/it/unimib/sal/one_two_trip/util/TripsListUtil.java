package it.unimib.sal.one_two_trip.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.text.DateFormat;
import java.util.Date;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.Trip;

public class TripsListUtil {
     public static void showEmptyState (Context context, LinearLayout layout, int textID) {
        ConstraintLayout constraintLayout = new ConstraintLayout(context);
        constraintLayout.setId(View.generateViewId());

        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);

        layout.addView(constraintLayout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        TextView noComingTripsMessage = new TextView(context);
        noComingTripsMessage.setId(View.generateViewId());
        noComingTripsMessage.setText(context.getString(textID));
        noComingTripsMessage.setTextSize(20);
        noComingTripsMessage.setGravity(Gravity.CENTER);
        constraintLayout.addView(noComingTripsMessage, new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT));

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

        ImageView noComingTripsIllustration = new ImageView(context);
        noComingTripsIllustration.setId(View.generateViewId());
        noComingTripsIllustration.setImageResource(R.drawable.no_trips);
        noComingTripsIllustration.setAdjustViewBounds(true);
        noComingTripsIllustration.setMaxHeight(1000);
        noComingTripsIllustration.setMaxWidth(1000);
        noComingTripsIllustration.setImageAlpha(200);

        constraintLayout.addView(noComingTripsIllustration, new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT));

        set.connect(noComingTripsIllustration.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
                ConstraintSet.START, 0);
        set.connect(noComingTripsIllustration.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID,
                ConstraintSet.END, 0);
        set.connect(noComingTripsIllustration.getId(), ConstraintSet.BOTTOM,  noComingTripsMessage.getId(),
                ConstraintSet.TOP, 0);

        set.constrainWidth(noComingTripsIllustration.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainHeight(noComingTripsIllustration.getId(), ConstraintSet.WRAP_CONTENT);
        set.applyTo(constraintLayout);
    }

   public static CardView createTripCard(Context context, Trip trip) {
      CardView cardView = new CardView(context, null, R.style.CardView);

      cardView.setRadius(15);
      cardView.setCardElevation(4);

      ConstraintLayout cardViewLayout = new ConstraintLayout(context);
      cardViewLayout.setId(View.generateViewId());
      ConstraintSet set = new ConstraintSet();
      set.clone(cardViewLayout);
      cardViewLayout.setLayoutParams(new ConstraintLayout.LayoutParams(
              ConstraintLayout.LayoutParams.MATCH_PARENT,
              ConstraintLayout.LayoutParams.MATCH_PARENT));

      TextView tripName = new TextView(context);
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
            TextView activityName = new TextView(context);
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

               TextView dateView = new TextView(context);
               dateView.setId(View.generateViewId());
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

      return cardView;
   }
}
