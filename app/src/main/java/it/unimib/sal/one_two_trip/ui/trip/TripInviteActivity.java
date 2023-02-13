package it.unimib.sal.one_two_trip.ui.trip;

import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_USER_COLLECTION;
import static it.unimib.sal.one_two_trip.util.Constants.PARTICIPANT;
import static it.unimib.sal.one_two_trip.util.Constants.REFERENCE;
import static it.unimib.sal.one_two_trip.util.Constants.REMOVED;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_ID;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.repository.trips.ITripsRepository;
import it.unimib.sal.one_two_trip.data.repository.user.IUserRepository;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModel;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModelFactory;
import it.unimib.sal.one_two_trip.ui.welcome.UserViewModel;
import it.unimib.sal.one_two_trip.ui.welcome.UserViewModelFactory;
import it.unimib.sal.one_two_trip.ui.welcome.WelcomeActivity;
import it.unimib.sal.one_two_trip.util.ServiceLocator;

/**
 * Activity that handles the invitation to join a trip.
 */
public class TripInviteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_invite);

        int mode = this.getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK;


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (mode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.gray_700));

            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        } else {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));

            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }

        IUserRepository userRepository = ServiceLocator.getInstance()
                .getUserRepository(getApplication());
        UserViewModel userViewModel = null;
        if (userRepository != null) {
            userViewModel = new ViewModelProvider(this,
                    new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
        }

        if (userViewModel == null || userViewModel.getLoggedUser() == null) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        String idToken = userViewModel.getLoggedUser().getId();

        ITripsRepository tripsRepository = ServiceLocator.getInstance()
                .getTripsRepository(getApplication());
        TripsViewModel tripsViewModel = null;
        if (tripsRepository != null) {
            tripsViewModel = new ViewModelProvider(this,
                    new TripsViewModelFactory(tripsRepository)).get(TripsViewModel.class);
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
        }

        Intent incomingIntent = getIntent();
        Uri uri = incomingIntent.getData();
        String tripId = uri.getQueryParameter(SELECTED_TRIP_ID);

        if (!tripId.isEmpty()) {
            androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(
                    this, R.style.Widget_App_CustomAlertDialog);
            alert.setTitle(getString(R.string.trip_join_title));
            alert.setMessage(getString(R.string.trip_join_message));

            TripsViewModel finalTripsViewModel = tripsViewModel;
            alert.setPositiveButton(getString(R.string.trip_join_confirmation_positive),
                    (dialog, which) -> {
                        if (finalTripsViewModel != null) {
                            HashMap<String, Object> map = new HashMap<>();
                            HashMap<String, Object> newParticipant = new HashMap<>();

                            DocumentReference userRef = FirebaseFirestore.getInstance()
                                    .collection(FIREBASE_USER_COLLECTION).document(idToken);
                            newParticipant.put(REFERENCE, userRef);
                            newParticipant.put(REMOVED, false);

                            map.put(PARTICIPANT + "." + idToken, newParticipant);

                            finalTripsViewModel.updateTrip(map, tripId);

                            Intent intent = new Intent(this, WelcomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
            alert.setNegativeButton(getString(R.string.trip_join_confirmation_negative),
                    (dialog, which) -> {
                        Intent intent = new Intent(this, WelcomeActivity.class);
                        startActivity(intent);
                        finish();
                    });
            alert.show();
        }
    }
}
