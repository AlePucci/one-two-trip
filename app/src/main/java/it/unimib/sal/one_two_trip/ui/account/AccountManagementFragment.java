package it.unimib.sal.one_two_trip.ui.account;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_LOGO_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.PROFILE_PICTURE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.USER_COLOR;

import android.app.Application;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.database.model.Person;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.repository.user.IUserRepository;
import it.unimib.sal.one_two_trip.data.source.storage.RemoteStorage;
import it.unimib.sal.one_two_trip.data.source.storage.RemoteStorageCallback;
import it.unimib.sal.one_two_trip.ui.welcome.UserViewModel;
import it.unimib.sal.one_two_trip.ui.welcome.UserViewModelFactory;
import it.unimib.sal.one_two_trip.ui.welcome.WelcomeActivity;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;
import it.unimib.sal.one_two_trip.util.Utility;

/**
 * Fragment that shows the user's account information and allows him to go to the
 * fragment for the editing of his data and to log out.
 */
public class AccountManagementFragment extends Fragment implements RemoteStorageCallback {

    private UserViewModel userViewModel;
    private ProgressBar progressBar;
    private TextView nameSurnameTextView;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private TextView emailTextView;
    private TextView profileImage;
    private RemoteStorage remoteStorage;
    private String imagePath;
    private Application application;
    private String id;
    private char letter;
    private int color;

    public AccountManagementFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentActivity activity = requireActivity();
        this.application = activity.getApplication();

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(this.application);
        if (userRepository != null) {
            this.userViewModel = new ViewModelProvider(
                    activity,
                    new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
        }

        this.sharedPreferencesUtil = new SharedPreferencesUtil(application);

        this.remoteStorage = new RemoteStorage(application);
        this.remoteStorage.setRemoteStorageCallback(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity activity = requireActivity();

        ImageButton goBackButton = view.findViewById(R.id.goBackButton);
        MaterialButton editAccountButton = view.findViewById(R.id.editAccountButton);
        MaterialButton logoutButton = view.findViewById(R.id.logoutButton);
        this.nameSurnameTextView = view.findViewById(R.id.nameSurnameTextView);
        this.emailTextView = view.findViewById(R.id.emailTextView);
        this.profileImage = view.findViewById(R.id.profileImage);
        this.progressBar = view.findViewById(R.id.progressBar);

        if (this.userViewModel.getLoggedUser() == null) {
            activity.finish();
            return;
        }
        this.id = this.userViewModel.getLoggedUser().getId();

        this.imagePath = this.application.getFilesDir() + "/" + id + "-" + PROFILE_PICTURE_NAME;

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

        this.userViewModel.getUser(id).observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                Person p = ((Result.PersonResponseSuccess) result).getData();
                String fullName = p.getName() + " " + p.getSurname();
                this.nameSurnameTextView.setText(fullName);
                this.emailTextView.setText(p.getEmail_address());

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
                this.remoteStorage.profilePictureExists(id);
            } else {
                activity.finish();
            }
        });

        goBackButton.setOnClickListener(v -> activity.onBackPressed());

        editAccountButton.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_accountManagementFragment_to_editAccountFragment));

        logoutButton.setOnClickListener(v -> {
                    this.userViewModel.logout();
                    Intent intent = new Intent(activity, WelcomeActivity.class);
                    startActivity(intent);
                    activity.finish();
                }
        );
    }

    @Override
    public void onUploadSuccess(long lastUpdate) {
        // CANNOT UPLOAD FROM HERE
    }

    @Override
    public void onDownloadSuccess() {
        this.progressBar.setVisibility(View.GONE);
        this.profileImage.setVisibility(View.VISIBLE);

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

    @Override
    public void onUploadFailure(Exception exception) {
        // CANNOT UPLOAD FROM HERE
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
