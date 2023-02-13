package it.unimib.sal.one_two_trip.ui.welcome;

import static it.unimib.sal.one_two_trip.util.Constants.EMAIL_ADDRESS;
import static it.unimib.sal.one_two_trip.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.ID_TOKEN;
import static it.unimib.sal.one_two_trip.util.Constants.MINIMUM_PASSWORD_LENGTH;
import static it.unimib.sal.one_two_trip.util.Constants.PASSWORD;
import static it.unimib.sal.one_two_trip.util.Constants.USER_COLLISION_ERROR;
import static it.unimib.sal.one_two_trip.util.Constants.WEAK_PASSWORD_ERROR;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec;
import com.google.android.material.progressindicator.IndeterminateDrawable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.database.model.Person;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.repository.user.IUserRepository;
import it.unimib.sal.one_two_trip.ui.main.HomeActivity;
import it.unimib.sal.one_two_trip.util.DataEncryptionUtil;
import it.unimib.sal.one_two_trip.util.ServiceLocator;

/**
 * Fragment that allows the user to register to the application.
 * It is used by the {@link WelcomeActivity}.
 */
public class SignupFragment extends Fragment {

    private DataEncryptionUtil dataEncryptionUtil;
    private UserViewModel userViewModel;
    private TextInputEditText name_edit_text;
    private TextInputEditText surname_edit_text;
    private TextInputEditText email_edit_text;
    private TextInputEditText password_edit_text;
    private TextInputEditText confirm_password_edit_text;
    private MaterialButton buttonRegistration;
    private IndeterminateDrawable<CircularProgressIndicatorSpec> progressIndicatorDrawable;

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
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity activity = requireActivity();

        MaterialButton backButton = view.findViewById(R.id.signup_back_button);
        backButton.setOnClickListener(v -> activity.onBackPressed());

        this.name_edit_text = view.findViewById(R.id.name_textedit);
        this.surname_edit_text = view.findViewById(R.id.surname_textedit);
        this.email_edit_text = view.findViewById(R.id.email_edittext);
        this.password_edit_text = view.findViewById(R.id.password_edittext);
        this.confirm_password_edit_text = view.findViewById(R.id.confirm_password_edittext);
        this.buttonRegistration = view.findViewById(R.id.signup_button);

        CircularProgressIndicatorSpec spec = new CircularProgressIndicatorSpec(activity, null, 0,
                com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall);
        this.progressIndicatorDrawable = IndeterminateDrawable.createCircularDrawable(activity, spec);

        this.confirm_password_edit_text.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                this.confirm_password_edit_text.clearFocus();
                onRegistrationClick();
            }
            return false;
        });

        this.buttonRegistration.setOnClickListener(v -> onRegistrationClick());
    }

    /**
     * Method to sign up the user.
     */
    private void onRegistrationClick() {
        FragmentActivity activity = requireActivity();

        this.buttonRegistration.setEnabled(false);
        this.buttonRegistration.setIcon(this.progressIndicatorDrawable);

        String name = "";
        String surname = "";
        String email = "";
        String password = "";
        String confirm_password = "";

        if (this.name_edit_text.getText() != null) {
            name = this.name_edit_text.getText().toString().trim();
        }
        if (this.surname_edit_text.getText() != null) {
            surname = this.surname_edit_text.getText().toString().trim();
        }
        if (this.email_edit_text.getText() != null) {
            email = this.email_edit_text.getText().toString().trim();
        }
        if (this.password_edit_text.getText() != null) {
            password = this.password_edit_text.getText().toString().trim();
        }
        if (this.confirm_password_edit_text.getText() != null) {
            confirm_password = this.confirm_password_edit_text.getText().toString().trim();
        }

        if (isNameOk() && isEmailOk() && isPasswordOk()) {
            if (password.equals(confirm_password)) {
                if (!this.userViewModel.isAuthenticationError()) {
                    String finalEmail = email;
                    String finalPassword = password;
                    this.userViewModel.getUserMutableLiveData(email, password, name, surname).observe(
                            getViewLifecycleOwner(), result -> {
                                if (result.isSuccess()) {
                                    Person person = ((Result.PersonResponseSuccess) result).getData();
                                    saveLoginData(finalEmail, finalPassword, person.getId());
                                    this.userViewModel.setAuthenticationError(false);
                                    startActivity(new Intent(activity, HomeActivity.class));
                                    activity.finish();
                                    return;
                                } else {
                                    this.userViewModel.setAuthenticationError(true);
                                    Snackbar.make(activity.findViewById(android.R.id.content),
                                            getErrorMessage(((Result.Error) result).getMessage()),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                                this.buttonRegistration.setEnabled(true);
                                this.buttonRegistration.setIcon(null);
                            });
                } else {
                    this.userViewModel.getUser(email, password, name, surname);
                }
            } else {
                Snackbar.make(activity.findViewById(android.R.id.content),
                        R.string.check_login_data_message, Snackbar.LENGTH_SHORT).show();
                this.password_edit_text.setError(getString(R.string.error_password_dont_match), null);
                this.confirm_password_edit_text.setError(getString(R.string.error_password_dont_match), null);
                this.buttonRegistration.setEnabled(true);
                this.buttonRegistration.setIcon(null);
            }
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content),
                    R.string.check_login_data_message, Snackbar.LENGTH_SHORT).show();
            this.buttonRegistration.setEnabled(true);
            this.buttonRegistration.setIcon(null);
        }
    }

    /**
     * Returns the error message to show to the user.
     *
     * @param message the error message returned by the server.
     * @return the error message localized to show to the user.
     */
    private String getErrorMessage(@NonNull String message) {
        switch (message) {
            case WEAK_PASSWORD_ERROR:
                return String.format(requireActivity().getString(R.string.error_password_too_weak),
                        MINIMUM_PASSWORD_LENGTH);
            case USER_COLLISION_ERROR:
                return requireActivity().getString(R.string.error_user_collision_message);
            default:
                return requireActivity().getString(R.string.unexpected_error);
        }
    }

    /**
     * Method to check if the names are ok.
     *
     * @return true if the names are ok, false otherwise.
     */
    private boolean isNameOk() {
        if (this.name_edit_text.getText() == null || this.surname_edit_text.getText() == null) {
            return false;
        }

        String name = this.name_edit_text.getText().toString().trim();
        String surname = this.surname_edit_text.getText().toString().trim();

        if (!name.isEmpty() && !surname.isEmpty()) {
            this.name_edit_text.setError(null);
            this.surname_edit_text.setError(null);
            return true;
        } else {
            if (name.isEmpty()) {
                this.name_edit_text.setError(getString(R.string.error_empty_name));
            }
            if (surname.isEmpty()) {
                this.surname_edit_text.setError(getString(R.string.error_empty_surname));
            }
            return false;
        }
    }

    /**
     * Method to check if the email is ok.
     *
     * @return true if the email is ok, false otherwise.
     */
    private boolean isEmailOk() {
        if (this.email_edit_text.getText() == null) {
            return false;
        }

        String email = email_edit_text.getText().toString().trim();

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.email_edit_text.setError(null);
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
        if (this.password_edit_text.getText() == null) {
            return false;
        }

        String password = this.password_edit_text.getText().toString().trim();

        if (password.isEmpty() || password.length() < MINIMUM_PASSWORD_LENGTH) {
            this.password_edit_text.setError(String.format(getString(R.string.error_password_too_weak),
                    MINIMUM_PASSWORD_LENGTH), null);
            return false;
        } else {
            this.password_edit_text.setError(null);
            return true;
        }
    }

    /**
     * Method to save the login data in the shared preferences.
     *
     * @param email    the email of the user.
     * @param password the password of the user.
     * @param idToken  the id token of the user.
     */
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
