package it.unimib.sal.one_two_trip.util;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.MovingActivity;
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

        TextView emptyStateMessage = new TextView(context);
        emptyStateMessage.setId(View.generateViewId());
        emptyStateMessage.setText(context.getString(textID));
        emptyStateMessage.setTextSize(20);
        emptyStateMessage.setGravity(Gravity.CENTER);
        constraintLayout.addView(emptyStateMessage, new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT));

        set.connect(emptyStateMessage.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
                ConstraintSet.START, 0);
        set.connect(emptyStateMessage.getId(), ConstraintSet.END,  ConstraintSet.PARENT_ID,
                ConstraintSet.END, 0);
        set.connect(emptyStateMessage.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID,
                ConstraintSet.TOP, 0);
        set.connect(emptyStateMessage.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM, 0);
        set.setVerticalBias(emptyStateMessage.getId(), 0.6f);

        set.constrainWidth(emptyStateMessage.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainHeight(emptyStateMessage.getId(), ConstraintSet.WRAP_CONTENT);
        set.applyTo(constraintLayout);

        ImageView emptyStateIllustration = new ImageView(context);
        emptyStateIllustration.setId(View.generateViewId());
        emptyStateIllustration.setImageResource(R.drawable.no_trips);
        emptyStateIllustration.setAdjustViewBounds(true);
        emptyStateIllustration.setMaxHeight(1000);
        emptyStateIllustration.setMaxWidth(1000);
        emptyStateIllustration.setImageAlpha(200);

        constraintLayout.addView(emptyStateIllustration, new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT));

        set.connect(emptyStateIllustration.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
                ConstraintSet.START, 0);
        set.connect(emptyStateIllustration.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID,
                ConstraintSet.END, 0);
        set.connect(emptyStateIllustration.getId(), ConstraintSet.BOTTOM,  emptyStateMessage.getId(),
                ConstraintSet.TOP, 0);

        set.constrainWidth(emptyStateIllustration.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainHeight(emptyStateIllustration.getId(), ConstraintSet.WRAP_CONTENT);
        set.applyTo(constraintLayout);
    }

   public static CardView createTripCard(Context context, Trip trip) {
      CardView cardView = new CardView(context, null, R.style.Widget_App_CardView);
      cardView.setId(View.generateViewId());
      cardView.setRadius(20);
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
      set.connect(tripName.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
              ConstraintSet.START, 50);
      set.connect(tripName.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID,
              ConstraintSet.TOP, 30);

      set.constrainWidth(tripName.getId(), ConstraintSet.WRAP_CONTENT);
      set.constrainHeight(tripName.getId(), ConstraintSet.WRAP_CONTENT);
      set.applyTo(cardViewLayout);

      MaterialButton shareTripButton = new MaterialButton(context, null,
              com.google.android.material.R.attr.materialIconButtonStyle);
      shareTripButton.setId(View.generateViewId());
      shareTripButton.setIcon(AppCompatResources.getDrawable(context,
              R.drawable.ic_baseline_share_32));
      TypedValue typedValue = new TypedValue();
      context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSecondary,
              typedValue, true);
     shareTripButton.setIconTint(AppCompatResources.getColorStateList(context, typedValue.resourceId));

      cardViewLayout.addView(shareTripButton);

      set.connect(shareTripButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID,
              ConstraintSet.END, 30);
      set.connect(shareTripButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID,
              ConstraintSet.TOP, 10);

      set.constrainWidth(shareTripButton.getId(), ConstraintSet.WRAP_CONTENT);
      set.constrainHeight(shareTripButton.getId(), ConstraintSet.WRAP_CONTENT);
      set.applyTo(cardViewLayout);

      int activitiesCount = 0;
      Date lastDate = null;
      TextView lastDateView;
      LinearLayout lastActivity = null;

      for(Activity activity : trip.getActivity()){
         if(activitiesCount < 3) {
            LinearLayout activityLayout = new LinearLayout(context);
            activityLayout.setId(View.generateViewId());
            activityLayout.setOrientation(LinearLayout.HORIZONTAL);
            activityLayout.setBackground(AppCompatResources.getDrawable(
                    context, R.drawable.trip_container_home));
            activityLayout.setPadding(20, 30, 20, 30);
            cardViewLayout.addView(activityLayout);
            set.constrainPercentWidth(activityLayout.getId(), 0.92f);
            set.applyTo(cardViewLayout);


            TextView activityName = new TextView(context);

            activityName.setId(View.generateViewId());
            activityName.setText(activity.getTitle());
            activityName.setTextSize(19);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");  //utility

            if(lastDate == null ||
                    !sdf.format(activity.getStart_date()).equals(sdf.format(lastDate))){
               TextView dateView = new TextView(context);
               dateView.setId(View.generateViewId());
               dateView.setText(DateFormat
                       .getDateInstance(DateFormat.LONG)
                       .format(activity.getStart_date()));
               dateView.setTextSize(14);

               cardViewLayout.addView(dateView);
               set.connect(dateView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
                       ConstraintSet.START, 50);

               if(lastDate == null){
                  set.connect(dateView.getId(), ConstraintSet.TOP, tripName.getId(),
                          ConstraintSet.BOTTOM, 20);
               }
               else {
                  set.connect(dateView.getId(), ConstraintSet.TOP, lastActivity.getId(),
                          ConstraintSet.BOTTOM, 20);
               }

               set.constrainWidth(dateView.getId(), ConstraintSet.WRAP_CONTENT);
               set.constrainHeight(dateView.getId(), ConstraintSet.WRAP_CONTENT);
               set.applyTo(cardViewLayout);

               lastDateView = dateView;

               set.connect(activityLayout.getId(), ConstraintSet.TOP, lastDateView.getId(),
                            ConstraintSet.BOTTOM, 20);

               set.connect(activityLayout.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
                       ConstraintSet.START, 50);

               set.constrainHeight(activityLayout.getId(), ConstraintSet.WRAP_CONTENT);
               set.applyTo(cardViewLayout);


               LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                       LinearLayout.LayoutParams.WRAP_CONTENT,
                       LinearLayout.LayoutParams.WRAP_CONTENT
               );
               params.setMargins(25, 0, 0, 0);
               activityLayout.addView(activityName, params);

               lastDate = activity.getStart_date();
               lastActivity = activityLayout;
            }
            else{
               set.connect(activityLayout.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
                       ConstraintSet.START, 50);
               set.connect(activityLayout.getId(), ConstraintSet.TOP, lastActivity.getId(),
                       ConstraintSet.BOTTOM, 20);

               set.constrainHeight(activityLayout.getId(), ConstraintSet.WRAP_CONTENT);
               set.applyTo(cardViewLayout);

               LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                       LinearLayout.LayoutParams.WRAP_CONTENT,
                       LinearLayout.LayoutParams.WRAP_CONTENT
               );
               params.setMargins(25, 0, 0, 0);
               activityLayout.addView(activityName, params);
               lastActivity = activityLayout;
            }

            TextView activityStartTime = new TextView(context);

            if(activity instanceof MovingActivity){

            }
            else{

            }



            activitiesCount++;
         }
      }

      MaterialButton moreButton = new MaterialButton(context,null,
              com.google.android.material.R.attr.materialButtonOutlinedStyle);
      moreButton.setId(View.generateViewId());
      moreButton.setText(R.string.more_button_text);
      moreButton.setTextSize(14);

      cardViewLayout.addView(moreButton);
      set.connect(moreButton.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
              ConstraintSet.START, 0);
      set.connect(moreButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID,
              ConstraintSet.END, 0);
      set.connect(moreButton.getId(), ConstraintSet.TOP, lastActivity.getId(),
              ConstraintSet.BOTTOM, 25);

      set.constrainWidth(moreButton.getId(), ConstraintSet.WRAP_CONTENT);
      set.constrainHeight(moreButton.getId(), ConstraintSet.WRAP_CONTENT);
      set.applyTo(cardViewLayout);

      moreButton.setOnClickListener(v -> {
         /* TO DO : open trip details activity */
         Log.d("TripCard", "More button clicked:" + trip.getTitle());
      });

      CardView.LayoutParams cardViewLayoutParams = new CardView.LayoutParams(
              CardView.LayoutParams.MATCH_PARENT,
              CardView.LayoutParams.WRAP_CONTENT);

      cardViewLayoutParams.setMargins(0,0,0,30);

      cardView.addView(cardViewLayout, cardViewLayoutParams);

      return cardView;
   }
}
