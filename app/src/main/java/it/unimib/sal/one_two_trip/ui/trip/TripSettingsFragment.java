package it.unimib.sal.one_two_trip.ui.trip;

import static it.unimib.sal.one_two_trip.util.Constants.DESCRIPTION;
import static it.unimib.sal.one_two_trip.util.Constants.ID;
import static it.unimib.sal.one_two_trip.util.Constants.IMAGE_MIME;
import static it.unimib.sal.one_two_trip.util.Constants.JOIN_BASE_URL;
import static it.unimib.sal.one_two_trip.util.Constants.LAST_LOGO_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.PARTICIPANT;
import static it.unimib.sal.one_two_trip.util.Constants.REMOVED;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.TEXT_TYPE;
import static it.unimib.sal.one_two_trip.util.Constants.TITLE;
import static it.unimib.sal.one_two_trip.util.Constants.TRIP_LOGO_NAME;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.adapter.SettingsParticipantRecyclerViewAdapter;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.database.model.Trip;
import it.unimib.sal.one_two_trip.data.repository.trips.ITripsRepository;
import it.unimib.sal.one_two_trip.data.repository.user.IUserRepository;
import it.unimib.sal.one_two_trip.data.source.storage.RemoteStorage;
import it.unimib.sal.one_two_trip.data.source.storage.RemoteStorageCallback;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModel;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModelFactory;
import it.unimib.sal.one_two_trip.ui.welcome.UserViewModel;
import it.unimib.sal.one_two_trip.ui.welcome.UserViewModelFactory;
import it.unimib.sal.one_two_trip.util.ErrorMessagesUtil;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;
import it.unimib.sal.one_two_trip.util.Utility;
import jp.wasabeef.blurry.Blurry;

/**
 * Fragment that shows the settings of a trip, such as the participants, the activities and the
 * description. Here the user can also change the trip logo.
 */
public class TripSettingsFragment extends Fragment implements RemoteStorageCallback, MenuProvider {

    private Application application;
    private Trip trip;
    private RemoteStorage remoteStorage;
    private String imagePath;
    private ShapeableImageView tripLogo;
    private String tripId;
    private boolean isUploading = false;
    private TripsViewModel viewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private ProgressBar progressBar;
    private MaterialToolbar toolbar;

    public TripSettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        androidx.fragment.app.FragmentActivity activity = requireActivity();
        this.application = activity.getApplication();
        this.sharedPreferencesUtil = new SharedPreferencesUtil(this.application);
        ITripsRepository tripsRepository = ServiceLocator.getInstance()
                .getTripsRepository(this.application);
        if (tripsRepository != null) {
            this.viewModel = new ViewModelProvider(activity,
                    new TripsViewModelFactory(tripsRepository)).get(TripsViewModel.class);
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
        }

        this.remoteStorage = new RemoteStorage(this.application);
        this.remoteStorage.setRemoteStorageCallback(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView participants = view.findViewById(R.id.trip_participants);
        TextView activities = view.findViewById(R.id.trip_activities);
        TextView description = view.findViewById(R.id.trip_description);
        RecyclerView recyclerView = view.findViewById(R.id.trip_participants_recycler_view);
        MaterialButton addParticipant = view.findViewById(R.id.trip_add_participant_button);
        CardView descriptionCardview = view.findViewById(R.id.trip_description_cardview);
        this.progressBar = view.findViewById(R.id.trip_settings_progress_bar);

        androidx.fragment.app.FragmentActivity activity = requireActivity();
        this.toolbar = activity.findViewById(R.id.trip_toolbar);
        ((MenuHost) activity).addMenuProvider(this, getViewLifecycleOwner(),
                Lifecycle.State.RESUMED);

        if (getArguments() != null) {
            this.tripId = getArguments().getString(SELECTED_TRIP_ID);
        }

        this.imagePath = this.application.getFilesDir() + "/" + tripId + "-" + TRIP_LOGO_NAME;
        this.tripLogo = view.findViewById(R.id.trip_logo);

        ActivityResultLauncher<Intent> photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri imageUri = data.getData();
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                        this.application.getContentResolver(),
                                        imageUri);
                                this.tripLogo.setImageBitmap(bitmap);
                                this.isUploading = true;
                                this.remoteStorage.uploadTripLogo(bitmap, tripId);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });

        this.tripLogo.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_MIME);

            photoPickerLauncher.launch(Intent.createChooser(pickIntent,
                    getString(R.string.trip_logo_pick_photo)));

        });

        addParticipant.setOnClickListener(v -> {
            String joinURL = JOIN_BASE_URL + tripId;
            String joinMessage = getString(R.string.trip_join_share_message) + " " + joinURL;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(TEXT_TYPE);
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_url));
            intent.putExtra(Intent.EXTRA_TEXT, joinMessage);
            startActivity(Intent.createChooser(intent, getString(R.string.share_url)));
        });

        descriptionCardview.setOnClickListener(v -> {
            androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(
                    activity, R.style.Widget_App_CustomAlertDialog);
            EditText input = new EditText(requireContext());
            FrameLayout container = new FrameLayout(requireContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(50, 0, 50, 0);
            input.setLayoutParams(params);

            String oldDescription = this.trip.getDescription();
            input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            input.setSingleLine(false);
            input.setMaxLines(3);
            container.addView(input);

            alert.setTitle(getString(R.string.trip_description_title));
            alert.setMessage(getString(R.string.trip_description_change));
            alert.setView(container);
            alert.setPositiveButton(getString(R.string.trip_description_positive),
                    (dialog, which) -> {
                        if (input.getText() == null) return;

                        String descriptionMessage = input.getText().toString().trim();
                        if (!descriptionMessage.equals(oldDescription)) {
                            input.setText(descriptionMessage);
                            HashMap<String, Object> map = new HashMap<>();
                            this.trip.setDescription(descriptionMessage);
                            map.put(DESCRIPTION, descriptionMessage);
                            this.viewModel.updateTrip(map, tripId);
                        }
                    });
            alert.setNegativeButton(getString(R.string.trip_description_negative), null);
            alert.show();
        });

        Bitmap bitmap = BitmapFactory.decodeFile(this.imagePath);
        if (bitmap != null) {
            this.tripLogo.setImageBitmap(bitmap);
        } else {
            this.tripLogo.setImageBitmap(BitmapFactory.decodeResource(activity.getResources(),
                    R.drawable.default_trip_image));
        }
        this.remoteStorage.tripLogoExists(tripId);

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE);
        }

        UserViewModel userViewModel = null;
        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(application);
        if (userRepository != null) {
            userViewModel = new ViewModelProvider(
                    activity,
                    new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
        }

        if (userViewModel == null || userViewModel.getLoggedUser() == null) return;

        UserViewModel finalUserViewModel = userViewModel;
        this.viewModel.getTrips(Long.parseLong(lastUpdate)).observe(
                getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        List<Trip> trips = ((Result.TripSuccess) result).getData().getTripList();

                        this.trip = null;

                        for (Trip mTrip : trips) {
                            if (mTrip.getId().equals(tripId)) {
                                this.trip = mTrip;
                                break;
                            }
                        }

                        if (this.trip == null || !this.trip.isParticipating()
                                || this.trip.isDeleted()) {
                            requireActivity().finish();
                            return;
                        }

                        if (this.trip.getParticipant() != null &&
                                this.trip.getParticipant().getPersonList() != null) {
                            int size = this.trip.getParticipant().getPersonList().size();
                            if (size != 1) {
                                participants.setText(String.format(getString(R.string.trip_participants_plural),
                                        size));
                            } else {
                                participants.setText(String.format(getString(R.string.trip_participants_singular)));
                            }
                        } else {
                            participants.setText(String.format(getString(R.string.trip_participants_plural),
                                    0));
                        }

                        if (this.trip.getActivity() != null
                                && this.trip.getActivity().getActivityList() != null) {
                            int size = this.trip.getActivity().getActivityList().size();
                            if (size != 1) {
                                activities.setText(String.format(getString(R.string.trip_activities_plural),
                                        size));
                            } else {
                                activities.setText(String.format(getString(R.string.trip_activities_singular)));
                            }
                        } else {
                            activities.setText(String.format(getString(R.string.trip_activities_plural),
                                    0));
                        }

                        if (this.trip.getDescription() != null
                                && !this.trip.getDescription().isEmpty()) {
                            description.setText(this.trip.getDescription());
                        } else {
                            description.setText(getString(R.string.trip_add_description));
                        }

                        RecyclerView.LayoutManager linearLayoutManager =
                                new LinearLayoutManager(this.application,
                                        LinearLayoutManager.VERTICAL, false);

                        SettingsParticipantRecyclerViewAdapter adapter =
                                new SettingsParticipantRecyclerViewAdapter(
                                        this.trip.getParticipant().getPersonList(),
                                        this.application,
                                        new SettingsParticipantRecyclerViewAdapter.OnItemClickListener() {
                                            @Override
                                            public void onClick(String userId) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString(ID, userId);
                                                Navigation.findNavController(view).navigate(R.id.action_tripSettingsFragment_to_externalAccountFragment,
                                                        bundle);
                                            }

                                            @Override
                                            public void onRemoveClick(String userId) {
                                                androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(
                                                        activity, R.style.Widget_App_CustomAlertDialog);

                                                String positive;
                                                String negative;
                                                if (userId.equals(finalUserViewModel.getLoggedUser().getId())) {
                                                    alert.setTitle(getString(R.string.trip_leave));
                                                    alert.setMessage(getString(R.string.trip_leave_message));
                                                    positive = getString(R.string.trip_leave_positive);
                                                    negative = getString(R.string.trip_leave_negative);
                                                } else {
                                                    alert.setTitle(getString(R.string.trip_delete_participant));
                                                    alert.setMessage(getString(R.string.trip_delete_participant_message));
                                                    positive = getString(R.string.trip_delete_participant_positive);
                                                    negative = getString(R.string.trip_delete_participant_negative);
                                                }

                                                alert.setPositiveButton(positive,
                                                        (dialog, which) -> {
                                                            if (viewModel != null) {
                                                                HashMap<String, Object> map = new HashMap<>();

                                                                map.put(PARTICIPANT + "." + userId
                                                                        + "." + REMOVED, true);

                                                                viewModel.updateTrip(map, tripId);
                                                            }
                                                        });
                                                alert.setNegativeButton(
                                                        negative,
                                                        null);
                                                alert.show();
                                            }
                                        });

                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setNestedScrollingEnabled(false);

                        this.toolbar.setTitle(this.trip.getTitle());
                    } else {
                        ErrorMessagesUtil errorMessagesUtil = new ErrorMessagesUtil(this.application);
                        Snackbar.make(view, errorMessagesUtil.getErrorMessage(((Result.Error) result)
                                .getMessage()), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onUploadSuccess(long lastUpdate) {
        this.sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_LOGO_UPDATE + "_" + this.tripId, String.valueOf(lastUpdate));
        Bitmap bitmap = BitmapFactory.decodeFile(this.imagePath);
        if (bitmap == null) {
            tripLogo.setImageBitmap(BitmapFactory.decodeResource(requireActivity().getResources(),
                    R.drawable.default_trip_image));
        } else {
            tripLogo.setImageBitmap(bitmap);
        }
        this.isUploading = false;
    }

    @Override
    public void onDownloadSuccess() {
        this.progressBar.setVisibility(View.GONE);
        if (!this.isUploading) {
            Bitmap bitmap = BitmapFactory.decodeFile(this.imagePath);
            if (bitmap == null) {
                this.tripLogo.setImageBitmap(
                        BitmapFactory.decodeResource(requireActivity().getResources(),
                                R.drawable.default_trip_image));
            } else {
                this.tripLogo.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void onUploadFailure(Exception exception) {
        Snackbar.make(requireView(), R.string.trip_logo_upload_failure,
                Snackbar.LENGTH_LONG).show();
        this.isUploading = false;
    }

    @Override
    public void onDownloadFailure(Exception exception) {
        this.progressBar.setVisibility(View.GONE);
        Snackbar.make(requireView(), R.string.trip_logo_download_failure,
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onExistsResponse(long lastUpdate) {
        if (isAdded()) {
            long savedLastUpdate = 0;

            if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_LOGO_UPDATE + "_" + this.tripId) != null) {
                savedLastUpdate = Long.parseLong(
                        sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                                LAST_LOGO_UPDATE + "_" + this.tripId));
            }

            if (lastUpdate > 0 && savedLastUpdate != lastUpdate) {
                // NEED TO DOWNLOAD
                this.progressBar.setVisibility(View.VISIBLE);
                Blurry.with(this.application)
                        .radius(25)
                        .sampling(2)
                        .async()
                        .capture(this.tripLogo)
                        .into(this.tripLogo);
                this.remoteStorage.downloadTripLogo(this.tripId);
                sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME,
                        LAST_LOGO_UPDATE + "_" + this.tripId, String.valueOf(lastUpdate));
            } else {
                // NO NEED TO DOWNLOAD
                Bitmap bitmap = BitmapFactory.decodeFile(this.imagePath);
                if (bitmap == null) {
                    this.tripLogo.setImageBitmap(
                            BitmapFactory.decodeResource(requireActivity().getResources(),
                                    R.drawable.default_trip_image));
                } else {
                    this.tripLogo.setImageBitmap(bitmap);
                }
            }
        }
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.trip_settings_appbar_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        Context context = requireContext();
        if (menuItem.getItemId() == R.id.trip_menu_rename) {
            androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(
                    context, R.style.Widget_App_CustomAlertDialog);
            EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            FrameLayout container = new FrameLayout(context);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(50, 0, 50, 0);
            input.setLayoutParams(params);
            container.addView(input);
            String oldTitle = this.trip.getTitle();
            input.setHint(oldTitle);

            alert.setTitle(getString(R.string.trip_title_change_title));
            alert.setMessage(getString(R.string.trip_title_change));
            alert.setView(container);
            alert.setPositiveButton(getString(R.string.trip_title_change_positive),
                    (dialog, which) -> {
                        String newTitle = input.getText().toString().trim();
                        if (!newTitle.isEmpty() && !newTitle.equals(oldTitle)) {
                            HashMap<String, Object> map = new HashMap<>();
                            this.trip.setTitle(newTitle);
                            this.toolbar.setTitle(newTitle);
                            map.put(TITLE, newTitle);
                            this.viewModel.updateTrip(map, tripId);
                        }
                    });
            alert.setNegativeButton(getString(R.string.trip_title_change_negative), null);
            alert.show();

            return true;
        } else if (menuItem.getItemId() == R.id.trip_menu_delete) {
            androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(
                    context, R.style.Widget_App_CustomAlertDialog);
            alert.setTitle(getString(R.string.trip_delete_confirmation_title));
            alert.setMessage(getString(R.string.trip_delete_confirmation));
            alert.setPositiveButton(getString(R.string.trip_delete_confirmation_positive),
                    (dialog, whichButton) -> {
                        this.viewModel.deleteTrip(this.trip);
                        this.remoteStorage.deleteTripLogo(this.tripId);
                        Utility.deleteNotifications(this.trip, this.application);

                        if (this.trip != null && this.trip.getActivity() != null
                                && this.trip.getActivity().getActivityList() != null) {
                            for (it.unimib.sal.one_two_trip.data.database.model.Activity activity
                                    : this.trip.getActivity().getActivityList()) {
                                Utility.deleteNotifications(activity, this.application, this.tripId);
                            }
                        }
                        requireActivity().finish();
                    });

            alert.setNegativeButton(getString(R.string.trip_delete_confirmation_negative),
                    null);
            alert.show();
            return true;
        }

        return false;
    }
}
