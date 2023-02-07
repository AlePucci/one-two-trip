package it.unimib.sal.one_two_trip.ui.welcome;

import static it.unimib.sal.one_two_trip.util.Constants.EMAIL_ADDRESS;
import static it.unimib.sal.one_two_trip.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.ID_TOKEN;
import static it.unimib.sal.one_two_trip.util.Constants.PASSWORD;
import static it.unimib.sal.one_two_trip.util.Constants.USER_COLLISION_ERROR;
import static it.unimib.sal.one_two_trip.util.Constants.WEAK_PASSWORD_ERROR;

import android.app.Application;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.database.model.User;
import it.unimib.sal.one_two_trip.data.repository.user.IUserRepository;
import it.unimib.sal.one_two_trip.util.Constants;
import it.unimib.sal.one_two_trip.util.DataEncryptionUtil;
import it.unimib.sal.one_two_trip.util.ServiceLocator;


public class SignupFragment extends Fragment {

    private DataEncryptionUtil dataEncryptionUtil;
    private UserViewModel userViewModel;
    private EditText name_edit_text;
    private EditText surname_edit_text;
    private EditText email_edit_text;
    private EditText password_email_edit_text;
    private EditText confirm_password_email_edit_text;

    public SignupFragment() {
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
            this.userViewModel.setAuthenticationError(false);
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
        }

        this.dataEncryptionUtil = new DataEncryptionUtil(application);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.name_edit_text = view.findViewById(R.id.name_textedit);
        this.surname_edit_text = view.findViewById(R.id.surname_textedit);
        this.email_edit_text = view.findViewById(R.id.email_edittext);
        this.password_email_edit_text = view.findViewById(R.id.password_edittext);
        this.confirm_password_email_edit_text = view.findViewById(R.id.confirm_password_edittext);
        MaterialButton buttonRegistration = view.findViewById(R.id.signup_button);

        buttonRegistration.setOnClickListener(v -> {
            buttonRegistration.setEnabled(false);
            String email = email_edit_text.getText().toString().trim();
            String password = password_email_edit_text.getText().toString().trim();
            String confirm_password = confirm_password_email_edit_text.getText().toString().trim();
            if (isNameOk() && isEmailOk() && isPasswordOk()) {
                if (password.equals(confirm_password)) {
                    if (!this.userViewModel.isAuthenticationError()) {
                        this.userViewModel.getUserMutableLiveData(email, password, false).observe(
                                getViewLifecycleOwner(), result -> {
                                    if (result.isSuccess()) {
                                        User user = ((Result.UserResponseSuccess) result).getData();
                                        saveLoginData(email, password, user.getIdToken());
                                        this.userViewModel.setAuthenticationError(false);
                                        buttonRegistration.setEnabled(true);
                                        Navigation.findNavController(view).navigate(
                                                R.id.action_signinFragment_to_loginFragment);
                                    } else {
                                        buttonRegistration.setEnabled(true);
                                        this.userViewModel.setAuthenticationError(true);
                                        Snackbar.make(view,
                                                getErrorMessage(((Result.Error) result).getMessage()),
                                                Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        this.userViewModel.getUser(email, password, false);
                        buttonRegistration.setEnabled(true);
                    }
                } else {
                    buttonRegistration.setEnabled(true);
                    this.userViewModel.setAuthenticationError(true);
                    confirm_password_email_edit_text.setError(getString(R.string.error_password_dont_match));
                    Snackbar.make(view,
                            R.string.check_login_data_message, Snackbar.LENGTH_SHORT).show();
                }
            } else {
                buttonRegistration.setEnabled(true);
                this.userViewModel.setAuthenticationError(true);
                Snackbar.make(view,
                        R.string.check_login_data_message, Snackbar.LENGTH_SHORT).show();
            }
        });
    }


    private String getErrorMessage(@NonNull String message) {
        switch (message) {
            case WEAK_PASSWORD_ERROR:
                return requireActivity().getString(R.string.error_password_too_weak);
            case USER_COLLISION_ERROR:
                return requireActivity().getString(R.string.error_user_collision_message);
            default:
                return requireActivity().getString(R.string.unexpected_error);
        }
    }

    private boolean isNameOk() {
        String name = this.name_edit_text.getText().toString().trim();
        String surname = this.surname_edit_text.getText().toString().trim();

        if (!name.isEmpty() && !surname.isEmpty()) {
            this.name_edit_text.setError(null);
            this.surname_edit_text.setError(null);
            this.userViewModel.setAuthenticationError(false);
            return true;
        } else {
            this.name_edit_text.setError(getString(R.string.error_empty_name));
            this.surname_edit_text.setError(getString(R.string.error_empty_surname));
            return false;
        }
    }

    private boolean isEmailOk() {
        String email = email_edit_text.getText().toString().trim();

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.email_edit_text.setError(null);
            this.userViewModel.setAuthenticationError(false);
            return true;
        } else {
            this.email_edit_text.setError(getString(R.string.error_email_not_valid));
            return false;
        }
    }

    /**
     * Checks if the password is not empty.
     *
     * @return True if the password has at least 6 characters, false otherwise
     */
    private boolean isPasswordOk() {
        String password = this.password_email_edit_text.getText().toString().trim();
        if (password.isEmpty() || password.length() < Constants.MINIMUM_PASSWORD_LENGTH) {
            this.password_email_edit_text.setError(getString(R.string.error_password_too_weak));
            return false;
        } else {
            this.password_email_edit_text.setError(null);
            return true;
        }
    }

    private void saveLoginData(String email, String password, String idToken) {
        try {
            this.dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS, email);
            this.dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, password);
            this.dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN, idToken);
            this.dataEncryptionUtil.writeSecreteDataOnFile(ENCRYPTED_DATA_FILE_NAME,
                    email.concat(":").concat(password));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}
