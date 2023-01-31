package it.unimib.sal.one_two_trip.ui.trip;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.SELECTED_TRIP_ID;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.TRIP_LOGO_NAME;

import android.app.AlertDialog;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.adapter.SettingsParticipantRecyclerViewAdapter;
import it.unimib.sal.one_two_trip.data.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.data.storage.RemoteStorage;
import it.unimib.sal.one_two_trip.data.storage.RemoteStorageCallback;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModel;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModelFactory;
import it.unimib.sal.one_two_trip.util.ErrorMessagesUtil;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TripSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TripSettingsFragment extends Fragment implements RemoteStorageCallback, MenuProvider {

    public static final int PICK_IMAGE = 1;
    private Application application;
    private Trip trip;
    private RemoteStorage remoteStorage;
    private String imagePath;
    private boolean tripLogoUploaded = false;
    private ShapeableImageView tripLogo;
    private long tripId;
    private boolean uploading = false;
    private TripsViewModel viewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;

    public TripSettingsFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TripSettingsFragment.
     */
    public static TripSettingsFragment newInstance() {
        return new TripSettingsFragment();
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
        this.remoteStorage = ServiceLocator.getInstance().getRemoteStorage(this.application);
        this.remoteStorage.setRemoteStorageCallback(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

        androidx.fragment.app.FragmentActivity activity = requireActivity();
        MaterialToolbar toolbar = activity.findViewById(R.id.trip_toolbar);
        ((MenuHost) activity).addMenuProvider(this, getViewLifecycleOwner(),
                Lifecycle.State.RESUMED);

        if (getArguments() != null) {
            this.tripId = getArguments().getLong(SELECTED_TRIP_ID);
        }
        this.remoteStorage.tripLogoExists(tripId);

        this.imagePath = application.getFilesDir() + "/1-" + tripId + "-" + TRIP_LOGO_NAME;
        this.tripLogo = view.findViewById(R.id.trip_logo);

        this.tripLogo.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*");

            //TODO replace deprecated method
            startActivityForResult(Intent.createChooser(pickIntent,
                            getString(R.string.trip_logo_pick_photo)),
                    PICK_IMAGE);

        });

        addParticipant.setOnClickListener(v -> Snackbar.make(view,
                "Add participant", Snackbar.LENGTH_SHORT).show());

        descriptionCardview.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
            EditText input = new EditText(requireContext());
            String oldDescription = this.trip.getDescription();
            input.setInputType(InputType.TYPE_CLASS_TEXT);

            alert.setTitle(getString(R.string.trip_description_title));
            alert.setMessage(getString(R.string.trip_description_change));
            alert.setView(input);
            alert.setPositiveButton(getString(R.string.trip_description_positive),
                    (dialog, which) -> {
                        String descriptionMessage = input.getText().toString();
                        if (!descriptionMessage.isEmpty() && !descriptionMessage.equals(oldDescription)) {
                            this.trip.setDescription(descriptionMessage);
                            this.viewModel.updateTrip(this.trip);
                        }
                    });
            alert.setNegativeButton(getString(R.string.trip_description_negative), null);
            alert.show();
        });

        Bitmap bitmap = BitmapFactory.decodeFile(this.imagePath);

        if (bitmap == null) {
            if (this.tripLogoUploaded) {
                this.remoteStorage.downloadTripLogo(this.tripId);
            } else {
                Bitmap defaultLogo = BitmapFactory.decodeResource(this.application.getResources(),
                        R.drawable.default_trip_image);
                this.tripLogo.setImageBitmap(defaultLogo);

            }
        } else {
            this.tripLogo.setImageBitmap(bitmap);
        }

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE);
        }

        this.viewModel.getTrips(Long.parseLong(lastUpdate)).observe(
                getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        List<Trip> trips = ((Result.Success) result).getData().getTripList();

                        for (Trip mTrip : trips) {
                            if (mTrip.getId() == tripId) {
                                this.trip = mTrip;
                                break;
                            }
                        }

                        if (this.trip == null) {
                            return;
                        }

                        if (this.trip.getParticipant() != null &&
                                this.trip.getParticipant().getPersonList() != null) {
                            participants.setText(String.format(getString(R.string.trip_participants),
                                    this.trip.getParticipant().getPersonList().size()));
                        } else {
                            participants.setText(String.format(getString(R.string.trip_participants),
                                    0));
                        }

                        if (this.trip.getActivity() != null
                                && this.trip.getActivity().getActivityList() != null) {
                            activities.setText(String.format(getString(R.string.trip_activities),
                                    this.trip.getActivity().getActivityList().size()));
                        } else {
                            activities.setText(String.format(getString(R.string.trip_activities),
                                    0));
                        }

                        if (this.trip.getDescription() != null
                                && !this.trip.getDescription().isEmpty()) {
                            description.setText(this.trip.getDescription());
                        } else {
                            description.setText(getString(R.string.trip_add_description));
                        }
                        LinearLayoutManager linearLayoutManager =
                                new LinearLayoutManager(this.application);
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                        SettingsParticipantRecyclerViewAdapter adapter =
                                new SettingsParticipantRecyclerViewAdapter(
                                        this.trip.getParticipant().getPersonList(),
                                        new SettingsParticipantRecyclerViewAdapter.OnItemClickListener() {
                                            @Override
                                            public void onClick(int position) {
                                                // TODO open participant profile
                                                Snackbar.make(view,
                                                        "Open participant profile",
                                                        Snackbar.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onRemoveClick(int position) {
                                                // TODO remove participant
                                                Snackbar.make(view,
                                                        "Remove participant",
                                                        Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setNestedScrollingEnabled(false);

                        toolbar.setTitle(this.trip.getTitle());
                    } else {
                        ErrorMessagesUtil errorMessagesUtil = new ErrorMessagesUtil(this.application);
                        Snackbar.make(view, errorMessagesUtil.getErrorMessage(((Result.Error) result)
                                .getMessage()), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onUploadSuccess() {
        Bitmap bitmap = BitmapFactory.decodeFile(this.imagePath);
        this.tripLogo.setImageBitmap(bitmap);
        this.uploading = false;
    }

    @Override
    public void onDownloadSuccess() {
        if (!this.uploading) {
            Bitmap bitmap = BitmapFactory.decodeFile(this.imagePath);
            tripLogo.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onUploadFailure(Exception exception) {
        Snackbar.make(requireView(), R.string.trip_logo_upload_failure,
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDownloadFailure(Exception exception) {
        Snackbar.make(requireView(), R.string.trip_logo_download_failure, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onExistsResponse(boolean exists) {
        this.tripLogoUploaded = exists;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                            this.application.getContentResolver(),
                            imageUri);
                    this.tripLogo.setImageBitmap(bitmap);
                    this.uploading = true;
                    this.remoteStorage.uploadTripLogo(bitmap, tripId);
                } catch (IOException e) {
                    throw new RuntimeException(e);
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
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            String oldTitle = this.trip.getTitle();
            EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setHint(oldTitle);

            alert.setTitle(getString(R.string.trip_title_change_title));
            alert.setMessage(getString(R.string.trip_title_change));
            alert.setView(input);
            alert.setPositiveButton(getString(R.string.trip_title_change_positive),
                    (dialog, which) -> {
                        String newTitle = input.getText().toString().trim();
                        if (!newTitle.isEmpty() && !newTitle.equals(oldTitle)) {
                            this.trip.setTitle(newTitle);
                            this.viewModel.updateTrip(this.trip);
                        }
                    });
            alert.setNegativeButton(getString(R.string.trip_title_change_negative), null);
            alert.show();

            return true;
        } else if (menuItem.getItemId() == R.id.trip_menu_delete) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(getString(R.string.trip_delete_confirmation_title));
            alert.setMessage(getString(R.string.trip_delete_confirmation));
            alert.setPositiveButton(getString(R.string.trip_delete_confirmation_positive),
                    (dialog, whichButton) -> {
                        this.viewModel.deleteTrip(this.trip);
                        requireActivity().onBackPressed();
                    });

            alert.setNegativeButton(getString(R.string.trip_delete_confirmation_negative),
                    null);
            alert.show();
            return true;
        }

        return false;
    }
}
