package it.unimib.sal.one_two_trip.ui.account;

import static it.unimib.sal.one_two_trip.util.Constants.IMAGE_MIME;
import static it.unimib.sal.one_two_trip.util.Constants.LAST_LOGO_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.PROFILE_PICTURE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.USER_COLOR;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.database.model.Person;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.repository.user.IUserRepository;
import it.unimib.sal.one_two_trip.data.source.storage.RemoteStorage;
import it.unimib.sal.one_two_trip.data.source.storage.RemoteStorageCallback;
import it.unimib.sal.one_two_trip.ui.welcome.UserViewModel;
import it.unimib.sal.one_two_trip.ui.welcome.UserViewModelFactory;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;
import it.unimib.sal.one_two_trip.util.Utility;

/**
 * Fragment that allows the user to edit his account data.
 */
public class EditAccountFragment extends Fragment implements RemoteStorageCallback {

    private UserViewModel userViewModel;
    private ProgressBar progressBar;
    private TextView profileImage;
    private TextInputEditText nameEditText;
    private TextInputEditText surnameEditText;
    private MaterialButton applyChangesButton;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private Application application;
    private boolean isUploading = false;
    private RemoteStorage remoteStorage;
    private String id;
    private String imagePath;
    private String oldName;
    private String oldSurname;
    private char letter;
    private int color;
    private Bitmap bitmap;

    public EditAccountFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentActivity activity = requireActivity();
        this.application = activity.getApplication();

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(application);
        if (userRepository != null) {
            this.userViewModel = new ViewModelProvider(
                    activity,
                    new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
        }

        this.sharedPreferencesUtil = new SharedPreferencesUtil(application);

        this.remoteStorage = new RemoteStorage(this.application);
        this.remoteStorage.setRemoteStorageCallback(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity activity = requireActivity();

        ImageButton goBackButton = view.findViewById(R.id.goBackButton);
        this.applyChangesButton = view.findViewById(R.id.applyChangesButton);
        this.nameEditText = view.findViewById(R.id.editName);
        this.surnameEditText = view.findViewById(R.id.editSurname);
        this.profileImage = view.findViewById(R.id.profileImage);
        this.progressBar = view.findViewById(R.id.progressBar);

        if (this.userViewModel.getLoggedUser() == null) {
            activity.finish();
            return;
        }
        this.id = this.userViewModel.getLoggedUser().getId();

        this.imagePath = this.application.getFilesDir() + "/" + id + "-" + PROFILE_PICTURE_NAME;

        this.surnameEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                this.surnameEditText.clearFocus();
                onSubmit();
            }
            return false;
        });

        ActivityResultLauncher<Intent> photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri imageUri = data.getData();
                            try {
                                this.bitmap = MediaStore.Images.Media.getBitmap(
                                        this.application.getContentResolver(),
                                        imageUri);

                                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(
                                        requireActivity().getResources(), bitmap);
                                roundedBitmapDrawable.setCircular(true);
                                this.profileImage.setBackground(roundedBitmapDrawable);
                                this.profileImage.setText(null);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });

        this.profileImage.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_MIME);

            photoPickerLauncher.launch(Intent.createChooser(pickIntent,
                    getString(R.string.profile_pick_photo)));
        });


        Bitmap bitmap = BitmapFactory.decodeFile(this.imagePath);
        if (bitmap != null) {
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(
                    activity.getResources(), bitmap);
            roundedBitmapDrawable.setCircular(true);
            this.profileImage.setText(null);
            this.profileImage.setBackground(roundedBitmapDrawable);
        } else {
            this.profileImage.setBackgroundResource(R.drawable.person_icon);
        }

        this.progressBar.setVisibility(View.VISIBLE);
        this.profileImage.setVisibility(View.INVISIBLE);

        this.userViewModel.getUser(this.id).observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        Person p = ((Result.PersonResponseSuccess) result).getData();
                        String name = p.getName();
                        String surname = p.getSurname();
                        this.oldName = name;
                        this.oldSurname = surname;
                        String fullName = name + " " + surname;
                        this.nameEditText.setText(name);
                        this.surnameEditText.setText(surname);

                        this.letter = fullName.charAt(0);

                        int color;
                        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                                USER_COLOR + "_" + id) != null) {
                            color = Integer.parseInt(sharedPreferencesUtil.readStringData(
                                    SHARED_PREFERENCES_FILE_NAME,
                                    USER_COLOR + "_" + id));
                        } else {
                            color = Utility.getRandomColor();
                            sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME,
                                    USER_COLOR + "_" + id, String.valueOf(color));
                        }
                        this.color = color;

                        if (!this.isUploading) {
                            this.remoteStorage.profilePictureExists(id);
                        }
                    } else {
                        activity.finish();
                    }
                });

        goBackButton.setOnClickListener(v -> activity.onBackPressed());

        this.applyChangesButton.setOnClickListener(v -> onSubmit());
    }

    private void onSubmit() {
        String email = userViewModel.getLoggedUser().getEmail_address();
        String finalOldName = this.oldName;
        String finalOldSurname = this.oldSurname;

        this.applyChangesButton.setEnabled(false);
        if (this.nameEditText.getText() == null
                || this.nameEditText.getText().toString().trim().isEmpty()) {
            this.nameEditText.setError(getString(R.string.error_empty_name));
            return;
        }
        if (this.surnameEditText.getText() == null
                || this.surnameEditText.getText().toString().trim().isEmpty()) {
            this.surnameEditText.setError(getString(R.string.error_empty_surname));
            return;
        }

        String newName = this.nameEditText.getText().toString().trim();
        String newSurname = this.surnameEditText.getText().toString().trim();

        if (newName.equals(finalOldName) &&
                newSurname.equals(finalOldSurname)) {
            if (this.bitmap != null) {
                this.isUploading = true;
                this.remoteStorage.uploadProfilePicture(this.bitmap, this.id);
            } else {
                if (isAdded()) {
                    requireActivity().onBackPressed();
                }
            }
        } else {
            Person p2 = new Person();
            p2.setId(id);
            p2.setName(newName);
            p2.setSurname(newSurname);
            p2.setEmail_address(email);
            p2.setProfile_picture("");

            this.userViewModel.updateUserData(p2).observe(getViewLifecycleOwner(),
                    result -> {
                        if (result.isSuccess()) {
                            if (this.bitmap == null) {
                                if (isAdded()) {
                                    requireActivity().onBackPressed();
                                }
                            } else {
                                this.isUploading = true;
                                this.remoteStorage.uploadProfilePicture(this.bitmap, this.id);
                            }
                        } else {
                            Snackbar.make(requireView(), requireActivity().getString(R.string.unexpected_error),
                                    Snackbar.LENGTH_SHORT).show();
                            this.applyChangesButton.setEnabled(true);
                        }
                    });
        }
    }

    @Override
    public void onUploadSuccess(long lastUpdate) {
        this.sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_LOGO_UPDATE + "_" + this.id, String.valueOf(lastUpdate));
        this.isUploading = false;
        this.profileImage.setVisibility(View.VISIBLE);
        this.progressBar.setVisibility(View.GONE);
        if (isAdded()) {
            requireActivity().onBackPressed();
        }
    }

    @Override
    public void onDownloadSuccess() {
        this.progressBar.setVisibility(View.GONE);
        this.profileImage.setVisibility(View.VISIBLE);
        if (!this.isUploading) {
            Bitmap bitmap = BitmapFactory.decodeFile(this.imagePath);
            Drawable d;
            if (bitmap == null) {
                d = ContextCompat.getDrawable(requireContext(), R.drawable.person_icon);
                this.profileImage.setBackground(d);
                this.profileImage.setBackgroundTintList(ColorStateList.valueOf(this.color));
                this.profileImage.setText(String.valueOf(this.letter));
            } else {
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(
                        requireActivity().getResources(), bitmap);
                roundedBitmapDrawable.setCircular(true);
                this.profileImage.setBackground(roundedBitmapDrawable);
                this.profileImage.setText(null);
            }
        }
    }

    @Override
    public void onUploadFailure(Exception exception) {
        this.progressBar.setVisibility(View.GONE);
        this.profileImage.setVisibility(View.VISIBLE);
        Snackbar.make(requireView(), R.string.trip_logo_upload_failure,
                Snackbar.LENGTH_LONG).show();
        this.isUploading = false;
        this.applyChangesButton.setEnabled(true);
    }

    @Override
    public void onDownloadFailure(Exception exception) {
        this.progressBar.setVisibility(View.GONE);
        this.profileImage.setVisibility(View.VISIBLE);
        Drawable d = ContextCompat.getDrawable(requireContext(), R.drawable.person_icon);
        this.profileImage.setBackgroundTintList(ColorStateList.valueOf(this.color));
        this.profileImage.setText(String.valueOf(this.letter));
        this.profileImage.setBackground(d);
        Snackbar.make(requireView(), R.string.trip_logo_download_failure,
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onExistsResponse(long lastUpdate) {
        if (isAdded()) {
            long savedLastUpdate = 0;

            if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_LOGO_UPDATE + "_" + this.id) != null) {
                savedLastUpdate = Long.parseLong(
                        sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                                LAST_LOGO_UPDATE + "_" + this.id));
            }

            if (lastUpdate > 0 && savedLastUpdate != lastUpdate) {
                // NEED TO DOWNLOAD
                this.remoteStorage.downloadProfilePicture(this.id);
                sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME,
                        LAST_LOGO_UPDATE + "_" + this.id, String.valueOf(lastUpdate));
            } else {
                // NO NEED TO DOWNLOAD
                Bitmap bitmap = BitmapFactory.decodeFile(this.imagePath);
                Drawable d;
                this.profileImage.setVisibility(View.VISIBLE);
                this.progressBar.setVisibility(View.GONE);
                if (bitmap == null) {
                    d = ContextCompat.getDrawable(requireContext(), R.drawable.person_icon);
                    this.profileImage.setBackgroundTintList(ColorStateList.valueOf(this.color));
                    this.profileImage.setText(String.valueOf(this.letter));
                    this.profileImage.setBackground(d);
                } else {
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(
                            requireActivity().getResources(), bitmap);
                    roundedBitmapDrawable.setCircular(true);
                    this.profileImage.setBackground(roundedBitmapDrawable);
                    this.profileImage.setText(null);
                }
            }
        }
    }
}
