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
        emptyStateIllustration.setImageResource(R.drawable.no_trips);
        emptyStateIllustration.setAdjustViewBounds(true);
        emptyStateIllustration.setMaxHeight(1500);
        emptyStateIllustration.setMaxWidth(1500);
        emptyStateIllustration.setImageAlpha(220);

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
        cardViewLayout.setPadding(50,30,30,50);

        ConstraintSet set = new ConstraintSet();
        set.clone(cardViewLayout);

        // Trip name (title)
        TextView tripName = new TextView(context);
        tripName.setId(View.generateViewId());
        if(trip.getTitle() != null){
            tripName.setText(trip.getTitle());
        } else {
            tripName.setText(R.string.default_trip_title);
        }
        tripName.setTextSize(23);

        cardViewLayout.addView(tripName);
        set.connect(tripName.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
                ConstraintSet.START, 0);
        set.connect(tripName.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID,
                ConstraintSet.TOP, 0);

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
                ConstraintSet.END, 0);
        set.connect(shareTripButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID,
                ConstraintSet.TOP, 0);

        set.constrainWidth(shareTripButton.getId(), ConstraintSet.WRAP_CONTENT);
        set.constrainHeight(shareTripButton.getId(), ConstraintSet.WRAP_CONTENT);
        set.applyTo(cardViewLayout);

        shareTripButton.setOnClickListener(v -> {
           /* TO DO: share trip */
        });

        //----------------
        // Activities
        //----------------
        int activitiesCount = 0;
        Date lastDate = null;
        ConstraintLayout lastActivity = null;

        if(trip.getActivity() == null){
            throw new IllegalArgumentException("Trip has null activities");
        }

        for (Activity activity : trip.getActivity()) {
            if (activitiesCount < MAX_ACTIVITIES_IN_A_TRIP) {
                // Activity layout
                ConstraintLayout activityLayout = new ConstraintLayout(context);
                activityLayout.setId(View.generateViewId());
                activityLayout.setBackground(AppCompatResources.getDrawable(
                        context, R.drawable.trip_container_home));
                activityLayout.setPadding(20, 40, 20, 40);
                cardViewLayout.addView(activityLayout);

                set.applyTo(cardViewLayout);

                // Activity name
                TextView activityName = new TextView(context);
                activityName.setId(View.generateViewId());
                if(activity.getTitle() != null){
                    activityName.setText(activity.getTitle());
                } else {
                    activityName.setText(R.string.default_activity_name);
                }
                activityName.setTextSize(19);

                // If the activity is the first one or if the date of the activity is different
                // from the last one, add the date to the new one.
                if (lastDate == null || (activity.getStart_date() != null &&
                        !compareDate(lastDate, activity.getStart_date()))) {
                    // DateView
                    TextView dateView = new TextView(context);
                    dateView.setId(View.generateViewId());
                    dateView.setText(DateFormat
                            .getDateInstance(DateFormat.LONG)
                            .format(activity.getStart_date()));
                    dateView.setTextSize(14);

                    cardViewLayout.addView(dateView);

                    set.connect(dateView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
                            ConstraintSet.START, 0);

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
                            ConstraintSet.START, 0);
                    set.connect(activityLayout.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID,
                            ConstraintSet.END, 0);

                    set.constrainWidth(activityLayout.getId(), ConstraintSet.MATCH_CONSTRAINT);
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
                            ConstraintSet.START, 0);
                    set.connect(activityLayout.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID,
                            ConstraintSet.END, 0);
                    // connect the activityLayout top to the bottom of lastActivity layout
                    set.connect(activityLayout.getId(), ConstraintSet.TOP, lastActivity.getId(),
                            ConstraintSet.BOTTOM, 20);

                    set.constrainWidth(activityLayout.getId(), ConstraintSet.MATCH_CONSTRAINT);
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

                // Attachments
                MaterialButton attachmentsButton = null;
                if (activity.getAttachment() != null) {
                    attachmentsButton = new MaterialButton(context, null,
                            com.google.android.material.R.attr.materialIconButtonStyle);
                    attachmentsButton.setId(View.generateViewId());
                    attachmentsButton.setIcon(AppCompatResources.getDrawable(context,
                            R.drawable.ic_baseline_file_present_24));
                    attachmentsButton.setIconTint(AppCompatResources.getColorStateList(context,
                            typedValue.resourceId));
                    activityLayout.addView(attachmentsButton);

                    set.connect(attachmentsButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID,
                            ConstraintSet.END, 0);
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



                // Start time TextView
                TextView activityStartTime = new TextView(context);
                activityStartTime.setId(View.generateViewId());

                if(activity.getStart_date() != null){
                    activityStartTime.setText(DateFormat
                            .getTimeInstance(DateFormat.SHORT)
                            .format(activity.getStart_date()));
                } else {
                    throw new IllegalArgumentException("Activity has no start date");
                }

                // Start time text appearance
                activityStartTime.setTextAppearance(context, R.style.Widget_App_TripTimeAppearance);

                // Activity info layout
                ConstraintLayout activityInfo = new ConstraintLayout(context);
                activityInfo.setId(View.generateViewId());

                if (activity instanceof MovingActivity) {
                    // layout of start time and start location
                    LinearLayout startLayout = new LinearLayout(context);
                    startLayout.setId(View.generateViewId());
                    startLayout.setOrientation(LinearLayout.VERTICAL);
                    startLayout.setGravity(Gravity.CENTER);
                    activityInfo.addView(startLayout);

                    // layout of end time and end location
                    LinearLayout endLayout = new LinearLayout(context);
                    endLayout.setId(View.generateViewId());
                    endLayout.setOrientation(LinearLayout.VERTICAL);
                    endLayout.setGravity(Gravity.CENTER);
                    activityInfo.addView(endLayout);

                    activityStartTime.setTextSize(15);

                    // Start location TextView
                    TextView activityStartLocation = new TextView(context);
                    activityStartLocation.setId(View.generateViewId());
                    if (activity.getLocation() != null) {
                        activityStartLocation.setText(activity.getLocation());
                    }
                    else {
                        throw new IllegalArgumentException("Activity has no start location");
                    }
                    activityStartLocation.setTextSize(15);
                    activityStartLocation.setGravity(Gravity.CENTER);
                    activityStartLocation.setTextAppearance(context, R.style.Widget_App_TripLocationAppearance);

                    startLayout.addView(activityStartTime);
                    startLayout.addView(activityStartLocation);

                    // End time TextView
                    TextView activityEndTime = new TextView(context);
                    activityEndTime.setId(View.generateViewId());
                    if (((MovingActivity) (activity)).getEnd_date() != null) {
                        activityEndTime.setText(DateFormat
                                .getTimeInstance(DateFormat.SHORT)
                                .format(((MovingActivity) (activity)).getEnd_date()));
                    }
                    else {
                        throw new IllegalArgumentException("Moving activity has no end date");
                    }
                    activityEndTime.setTextSize(15);
                    activityEndTime.setTextAppearance(context, R.style.Widget_App_TripTimeAppearance);

                    // End location TextView
                    TextView activityEndLocation = new TextView(context);
                    activityEndLocation.setId(View.generateViewId());

                    if (((MovingActivity) (activity)).getEnd_location() != null) {
                        activityEndLocation.setText(((MovingActivity) (activity)).getEnd_location());
                    }
                    else {
                        throw new IllegalArgumentException("Moving activity has no end location");
                    }
                    activityEndLocation.setTextSize(15);
                    activityEndLocation.setGravity(Gravity.CENTER);
                    activityEndLocation.setTextAppearance(context, R.style.Widget_App_TripLocationAppearance);

                    endLayout.addView(activityEndTime);
                    endLayout.addView(activityEndLocation);

                    set.connect(endLayout.getId(), ConstraintSet.START, startLayout.getId(),
                            ConstraintSet.END, 20);
                    set.connect(endLayout.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID,
                            ConstraintSet.TOP, 0);
                    set.connect(endLayout.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
                            ConstraintSet.BOTTOM, 0);
                    set.constrainWidth(endLayout.getId(), ConstraintSet.WRAP_CONTENT);
                    set.constrainHeight(endLayout.getId(), ConstraintSet.WRAP_CONTENT);
                    set.applyTo(activityInfo);
                } else {
                    activityInfo.addView(activityStartTime);
                    activityStartTime.setTextSize(20);
                }

                activityLayout.addView(activityInfo);

                set.connect(activityInfo.getId(), ConstraintSet.START, activityName.getId(),
                        ConstraintSet.END, 0);
                if(attachmentsButton != null){
                    set.connect(activityInfo.getId(), ConstraintSet.END, attachmentsButton.getId(),
                            ConstraintSet.START, 0);
                } else {
                    set.connect(activityInfo.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID,
                            ConstraintSet.END, 0);
                }
                set.connect(activityInfo.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID,
                        ConstraintSet.TOP, 0);
                set.connect(activityInfo.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
                        ConstraintSet.BOTTOM, 0);
                set.constrainWidth(activityInfo.getId(), ConstraintSet.WRAP_CONTENT);
                set.constrainHeight(activityInfo.getId(), ConstraintSet.WRAP_CONTENT);

                set.applyTo(activityLayout);

                // Participants

                // only shows participants if the number of them is different from the number of
                // participants of the entire trip (and it's not a moving activity)
                if (activity.getParticipant() != null && trip.getParticipant() != null &&
                        activity.getParticipant().length != trip.getParticipant().length
                        && !(activity instanceof MovingActivity)) {
                    // Participants layout
                    LinearLayout participantsLayout = new LinearLayout(context);
                    participantsLayout.setId(View.generateViewId());
                    participantsLayout.setOrientation(LinearLayout.HORIZONTAL);
                    participantsLayout.setGravity(Gravity.CENTER);
                    activityInfo.addView(participantsLayout);

                    // Participants ImageView
                    ImageView participantsImage = new ImageView(context);
                    participantsImage.setId(View.generateViewId());
                    participantsImage.setImageResource(R.drawable.ic_baseline_group_24);
                    participantsImage.setImageTintList(AppCompatResources.getColorStateList(context,
                            typedValue.resourceId));

                    participantsLayout.addView(participantsImage);

                    // Participants TextView (count)
                    TextView participantsCount = new TextView(context);
                    participantsCount.setId(View.generateViewId());
                    participantsCount.setText(String.valueOf(activity.getParticipant().length));
                    participantsCount.setTextSize(20);
                    participantsCount.setTextAppearance(context, R.style.Widget_App_TripTimeAppearance);

                    participantsLayout.addView(participantsCount);

                    //can never be moving activity
                    set.connect(participantsLayout.getId(), ConstraintSet.START, activityStartTime.getId(),
                            ConstraintSet.END, 40);
                    set.connect(participantsLayout.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID,
                            ConstraintSet.TOP, 0);
                    set.connect(participantsLayout.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
                            ConstraintSet.BOTTOM, 0);
                    set.constrainWidth(participantsLayout.getId(), ConstraintSet.WRAP_CONTENT);
                    set.constrainHeight(participantsLayout.getId(), ConstraintSet.WRAP_CONTENT);
                    set.applyTo(activityInfo);
                }

                activityLayout.setOnClickListener(v -> {
                    /* TODO: open activity details */
                });

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
                /* TODO : Open trip details */
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

        cardView.addView(cardViewLayout, cardViewLayoutParams);

        cardView.setOnClickListener(v -> {
            /* TODO : Open trip details */
        });

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
