package it.unimib.sal.one_two_trip.ui.account;

import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.USER_COLOR;

import android.app.Application;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.database.model.Person;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.repository.user.IUserRepository;
import it.unimib.sal.one_two_trip.ui.welcome.UserViewModel;
import it.unimib.sal.one_two_trip.ui.welcome.UserViewModelFactory;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;
import it.unimib.sal.one_two_trip.util.Utility;

public class EditAccountFragment extends Fragment {

    private UserViewModel userViewModel;
    private TextView profileImage;
    private TextInputEditText nameEditText;
    private TextInputEditText surnameEditText;
    private SharedPreferencesUtil sharedPreferencesUtil;

    public EditAccountFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentActivity activity = requireActivity();
        Application application = activity.getApplication();

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
        Button applyChangesButton = view.findViewById(R.id.applyChangesButton);
        this.nameEditText = view.findViewById(R.id.editName);
        this.surnameEditText = view.findViewById(R.id.editSurname);
        this.profileImage = view.findViewById(R.id.profileImage);

        String id = userViewModel.getLoggedUser().getId();
        String email = userViewModel.getLoggedUser().getEmail_address();

        this.userViewModel.getUser(id).observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        Person p = ((Result.PersonResponseSuccess) result).getData();
                        String fullName = p.getName() + " " + p.getSurname();
                        this.nameEditText.setText(p.getName());
                        this.surnameEditText.setText(p.getSurname());

                        if (p.getProfile_picture() == null || p.getProfile_picture().isEmpty()) {
                            this.profileImage.setText(fullName.substring(0, 1));
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
                            this.profileImage.setBackgroundTintList(ColorStateList.valueOf(color));
                        }
                    }
                });

        String oldName = "";
        if (this.nameEditText.getText() != null) {
            oldName = this.nameEditText.getText().toString();
        }

        String oldSurname = "";
        if (this.surnameEditText.getText() != null) {
            oldSurname = this.surnameEditText.getText().toString();
        }

        goBackButton.setOnClickListener(v -> activity.onBackPressed());

        String finalOldName = oldName;
        String finalOldSurname = oldSurname;
        applyChangesButton.setOnClickListener(v -> {
            if (this.nameEditText.getText() == null || this.nameEditText.getText().toString().trim().isEmpty()) {
                this.nameEditText.setError(getString(R.string.error_empty_name));
                return;
            }
            if (this.surnameEditText.getText() == null || this.surnameEditText.getText().toString().trim().isEmpty()) {
                this.surnameEditText.setError(getString(R.string.error_empty_surname));
                return;
            }

            String newName = this.nameEditText.getText().toString().trim();
            String newSurname = this.surnameEditText.getText().toString().trim();

            if (newName.equals(finalOldName) &&
                    newSurname.equals(finalOldSurname)) {
                activity.onBackPressed();
                return;
            }

            Person p2 = new Person();
            p2.setId(id);
            p2.setName(newName);
            p2.setSurname(newSurname);
            p2.setEmail_address(email);
            p2.setProfile_picture("");

            this.userViewModel.updateUserData(p2).observe(getViewLifecycleOwner(),
                    result -> {
                        if (result.isSuccess()) {
                            activity.onBackPressed();
                        } else {
                            Snackbar.make(view, activity.getString(R.string.unexpected_error),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
