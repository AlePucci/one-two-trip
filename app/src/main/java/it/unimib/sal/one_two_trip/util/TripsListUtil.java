package it.unimib.sal.one_two_trip.util;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.MovingActivity;
import it.unimib.sal.one_two_trip.model.Trip;

public class TripsListUtil {
    private static final int MAX_ACTIVITIES_IN_A_TRIP = 3;

    /** This method generates and shows the empty state of the trips list (coming or past)
     * @param context the Context of the activity that calls this method
     * @param layout the Layout where the empty state will be displayed
     * @param textID the id of the string that will be shown in the empty state
     */
    public static void showEmptyState (Context context, LinearLayout layout, int textID) {
        // Layout
        ConstraintLayout constraintLayout = new ConstraintLayout(context);
        constraintLayout.setId(View.generateViewId());

        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);

        layout.addView(constraintLayout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        // TextView
        TextView emptyStateMessage = new TextView(context);
        emptyStateMessage.setId(View.generateViewId());
        emptyStateMessage.setText(context.getString(textID));
        emptyStateMessage.setTextSize(20);
        emptyStateMessage.setGravity(Gravity.CENTER);
        constraintLayout.addView(emptyStateMessage);

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

        // ImageView
        ImageView emptyStateIllustration = new ImageView(context);
        emptyStateIllustration.setId(View.generateViewId());
        emptyStateIllustration.setImageResource(R.mipmap.no_trips);
        emptyStateIllustration.setAdjustViewBounds(true);
        emptyStateIllustration.setMaxHeight(1000);
        emptyStateIllustration.setMaxWidth(1000);
        emptyStateIllustration.setImageAlpha(200);

        constraintLayout.addView(emptyStateIllustration);

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

    /** This method generates a TripCard, which is a CardView that contains the information of a
     * trip
     * @param context the Context of the activity that calls this method
     * @param trip the Trip that will be shown in the TripCard
     * @return the TripCard
     */
    @NonNull
    public static CardView createTripCard(Context context, @NonNull Trip trip) {
        // TripCard layout
        CardView cardView = new CardView(context, null, R.style.Widget_App_CardView);
        cardView.setId(View.generateViewId());
        cardView.setRadius(20);
        cardView.setCardElevation(4);

        ConstraintLayout cardViewLayout = new ConstraintLayout(context);
        cardViewLayout.setId(View.generateViewId());

        ConstraintSet set = new ConstraintSet();
        set.clone(cardViewLayout);

        // Trip name (title)
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

        // Share trip button
        MaterialButton shareTripButton = new MaterialButton(context, null,
                com.google.android.material.R.attr.materialIconButtonStyle);
        shareTripButton.setId(View.generateViewId());
        shareTripButton.setIcon(AppCompatResources.getDrawable(context,
                R.drawable.ic_baseline_share_24));
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSecondary,
                typedValue, true);
        shareTripButton.setIconTint(AppCompatResources.getColorStateList(context,
                typedValue.resourceId));

        cardViewLayout.addView(shareTripButton);

        set.connect(shareTripButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID,
                ConstraintSet.END, 30);
        set.connect(shareTripButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID,
                ConstraintSet.TOP, 10);

        set.constrainWidth(shareTripButton.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainHeight(shareTripButton.getId(), ConstraintSet.WRAP_CONTENT);
        set.applyTo(cardViewLayout);

        //----------------
        // Activities
        //----------------
        int activitiesCount = 0;
        Date lastDate = null;
        ConstraintLayout lastActivity = null;

        for (Activity activity : trip.getActivity()) {
            if (activitiesCount < MAX_ACTIVITIES_IN_A_TRIP) {
                // Activity layout
                ConstraintLayout activityLayout = new ConstraintLayout(context);
                activityLayout.setId(View.generateViewId());
                activityLayout.setBackground(AppCompatResources.getDrawable(
                        context, R.drawable.trip_container_home));
                activityLayout.setPadding(20, 40, 20, 40);
                cardViewLayout.addView(activityLayout);
                set.constrainPercentWidth(activityLayout.getId(), 0.92f);
                set.applyTo(cardViewLayout);

                // Activity name
                TextView activityName = new TextView(context);
                activityName.setId(View.generateViewId());
                activityName.setText(activity.getTitle());
                activityName.setTextSize(19);

                // If the activity is the first one or if the date of the activity is different
                // from the last one, add the date to the new one.
                if (lastDate == null ||
                        !compareDate(lastDate, activity.getStart_date())) {
                    // DateView
                    TextView dateView = new TextView(context);
                    dateView.setId(View.generateViewId());
                    dateView.setText(DateFormat
                            .getDateInstance(DateFormat.LONG)
                            .format(activity.getStart_date()));
                    dateView.setTextSize(14);

                    cardViewLayout.addView(dateView);

                    set.connect(dateView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
                            ConstraintSet.START, 50);

                    // If it's the first activity, connect it to the trip name
                    if (lastDate == null) {
                        set.connect(dateView.getId(), ConstraintSet.TOP, tripName.getId(),
                                ConstraintSet.BOTTOM, 20);
                    }
                    // else connect it to the lastActivity layout
                    else {
                        set.connect(dateView.getId(), ConstraintSet.TOP, lastActivity.getId(),
                                ConstraintSet.BOTTOM, 20);
                    }

                    set.constrainWidth(dateView.getId(), ConstraintSet.WRAP_CONTENT);
                    set.constrainHeight(dateView.getId(), ConstraintSet.WRAP_CONTENT);
                    set.applyTo(cardViewLayout);

                    // connect the activityLayout top to the bottom of DateView
                    set.connect(activityLayout.getId(), ConstraintSet.TOP, dateView.getId(),
                            ConstraintSet.BOTTOM, 20);
                    set.connect(activityLayout.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
                            ConstraintSet.START, 50);

                    set.constrainHeight(activityLayout.getId(), ConstraintSet.WRAP_CONTENT);
                    set.applyTo(cardViewLayout);

                    activityLayout.addView(activityName);

                    set.connect(activityName.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
                            ConstraintSet.START, 25);
                    set.connect(activityName.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID,
                            ConstraintSet.TOP, 0);
                    set.connect(activityName.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
                            ConstraintSet.BOTTOM, 0);
                    set.constrainWidth(activityName.getId(), ConstraintSet.WRAP_CONTENT);
                    set.constrainHeight(activityName.getId(), ConstraintSet.WRAP_CONTENT);

                    set.applyTo(activityLayout);

                    lastDate = activity.getStart_date();
                } else {
                    // the activity is not the first one and the date is the same as the last one

                    set.connect(activityLayout.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
                            ConstraintSet.START, 50);
                    // connect the activityLayout top to the bottom of lastActivity layout
                    set.connect(activityLayout.getId(), ConstraintSet.TOP, lastActivity.getId(),
                            ConstraintSet.BOTTOM, 20);

                    set.constrainHeight(activityLayout.getId(), ConstraintSet.WRAP_CONTENT);
                    set.applyTo(cardViewLayout);

                    activityLayout.addView(activityName);

                    set.connect(activityName.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
                            ConstraintSet.START, 25);
                    set.connect(activityName.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID,
                            ConstraintSet.TOP, 0);
                    set.connect(activityName.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
                            ConstraintSet.BOTTOM, 0);
                    set.constrainWidth(activityName.getId(), ConstraintSet.WRAP_CONTENT);
                    set.constrainHeight(activityName.getId(), ConstraintSet.WRAP_CONTENT);

                    set.applyTo(activityLayout);
                }
                lastActivity = activityLayout;

                // Start time TextView
                TextView activityStartTime = new TextView(context);
                activityStartTime.setId(View.generateViewId());
                activityStartTime.setText(DateFormat
                        .getTimeInstance(DateFormat.SHORT)
                        .format(activity.getStart_date()));

                // Start time text appearance
                activityStartTime.setTextAppearance(context, R.style.Widget_App_TripTimeAppearance);

                // Activity info layout
                ConstraintLayout activityInfo = new ConstraintLayout(context);
                activityInfo.setId(View.generateViewId());
                activityInfo.addView(activityStartTime);

                if (activity instanceof MovingActivity) {
                    activityStartTime.setTextSize(15);

                    // End time TextView
                    TextView activityEndTime = new TextView(context);
                    activityEndTime.setId(View.generateViewId());
                    activityEndTime.setText(DateFormat
                            .getTimeInstance(DateFormat.SHORT)
                            .format(((MovingActivity) (activity)).getEnd_date()));
                    activityEndTime.setTextSize(15);
                    activityEndTime.setTextAppearance(context, R.style.Widget_App_TripTimeAppearance);

                    activityInfo.addView(activityEndTime);

                    set.connect(activityEndTime.getId(), ConstraintSet.START, activityStartTime.getId(),
                            ConstraintSet.END, 20);
                    set.connect(activityEndTime.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID,
                            ConstraintSet.TOP, 0);
                    set.connect(activityEndTime.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
                            ConstraintSet.BOTTOM, 0);
                    set.constrainWidth(activityEndTime.getId(), ConstraintSet.WRAP_CONTENT);
                    set.constrainHeight(activityEndTime.getId(), ConstraintSet.WRAP_CONTENT);
                    set.applyTo(activityInfo);
                } else {
                    activityStartTime.setTextSize(20);
                }

                activityLayout.addView(activityInfo);

                set.connect(activityInfo.getId(), ConstraintSet.START, activityName.getId(),
                        ConstraintSet.END, 40);
                set.connect(activityInfo.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID,
                        ConstraintSet.TOP, 0);
                set.connect(activityInfo.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
                        ConstraintSet.BOTTOM, 0);
                set.constrainWidth(activityInfo.getId(), ConstraintSet.WRAP_CONTENT);
                set.constrainHeight(activityInfo.getId(), ConstraintSet.WRAP_CONTENT);

                set.applyTo(activityLayout);

                // Attachments

                if (activity.getAttachment() != null) {
                    MaterialButton attachmentsButton = new MaterialButton(context, null,
                            com.google.android.material.R.attr.materialIconButtonStyle);
                    attachmentsButton.setId(View.generateViewId());
                    attachmentsButton.setIcon(AppCompatResources.getDrawable(context,
                            R.drawable.ic_baseline_file_present_24));
                    context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSecondary,
                            typedValue, true);
                    attachmentsButton.setIconTint(AppCompatResources.getColorStateList(context,
                            typedValue.resourceId));
                    activityLayout.addView(attachmentsButton);

                    set.connect(attachmentsButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID,
                            ConstraintSet.END, 30);
                    set.connect(attachmentsButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID,
                            ConstraintSet.TOP, 0);
                    set.connect(attachmentsButton.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
                            ConstraintSet.BOTTOM, 0);

                    set.constrainWidth(attachmentsButton.getId(), ConstraintSet.WRAP_CONTENT);
                    set.constrainHeight(attachmentsButton.getId(), ConstraintSet.WRAP_CONTENT);
                    set.applyTo(activityLayout);

                    attachmentsButton.setOnClickListener(v -> {
                        /* TODO: Open attachments list */
                    });
                }

                // Update activities counter
                activitiesCount++;
            }
        }

        if (lastActivity != null){
            // Some activities were added to the layout
            MaterialButton moreButton = new MaterialButton(context, null,
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
                Navigation.findNavController(cardView).navigate(R.id.action_fragment_coming_trips_to_trip);
            });
        }
        else{
            // no activities were added to the layout
            TextView noActivitiesText = new TextView(context);
            noActivitiesText.setId(View.generateViewId());
            noActivitiesText.setText(R.string.no_activities_added);
            noActivitiesText.setTextSize(18);

            cardViewLayout.addView(noActivitiesText);
            set.connect(noActivitiesText.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
                    ConstraintSet.START, 0);
            set.connect(noActivitiesText.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID,
                    ConstraintSet.END, 0);
            set.connect(noActivitiesText.getId(), ConstraintSet.TOP, tripName.getId(),
                    ConstraintSet.BOTTOM, 30);
            set.constrainWidth(noActivitiesText.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainHeight(noActivitiesText.getId(), ConstraintSet.WRAP_CONTENT);

            set.applyTo(cardViewLayout);

            MaterialButton startAddingButton = new MaterialButton(context, null,
                    com.google.android.material.R.attr.materialButtonOutlinedStyle);
            startAddingButton.setId(View.generateViewId());
            startAddingButton.setText(R.string.start_adding_button_text);
            startAddingButton.setTextSize(14);

            cardViewLayout.addView(startAddingButton);
            set.connect(startAddingButton.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
                    ConstraintSet.START, 0);
            set.connect(startAddingButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID,
                    ConstraintSet.END, 0);
            set.connect(startAddingButton.getId(), ConstraintSet.TOP, noActivitiesText.getId(),
                    ConstraintSet.BOTTOM, 25);

            set.constrainWidth(startAddingButton.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainHeight(startAddingButton.getId(), ConstraintSet.WRAP_CONTENT);
            set.applyTo(cardViewLayout);

            startAddingButton.setOnClickListener(v -> {
                /* TODO : Open add activity to that trip */
            });
        }

        CardView.LayoutParams cardViewLayoutParams = new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.WRAP_CONTENT);

        cardViewLayoutParams.setMargins(0,0,0,30);
        cardView.addView(cardViewLayout, cardViewLayoutParams);

        return cardView;
    }

    /** Utility method to verify if two dates are in the same day
     *
     * @param date1 first date
     * @param date2 second date
     * @return true if the dates are in the same day (excluding time), false otherwise
     */
    private static boolean compareDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
}
