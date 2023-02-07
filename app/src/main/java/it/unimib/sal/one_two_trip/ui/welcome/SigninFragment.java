package it.unimib.sal.one_two_trip.ui.welcome;

import static it.unimib.sal.one_two_trip.util.Constants.EMAIL_ADDRESS;
import static it.unimib.sal.one_two_trip.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.ID_TOKEN;
import static it.unimib.sal.one_two_trip.util.Constants.PASSWORD;
import static it.unimib.sal.one_two_trip.util.Constants.USER_COLLISION_ERROR;
import static it.unimib.sal.one_two_trip.util.Constants.WEAK_PASSWORD_ERROR;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.User;
import it.unimib.sal.one_two_trip.util.Constants;


public class SigninFragment extends Fragment {

    private DataEncryptionUtil dataEncryptionUtil;
    private UserViewModel userViewModel;

    public SigninFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        this.userViewModel.setAuthenticationError(false);
        this.dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText email_edit_text = view.findViewById(R.id.email_edittext);
        EditText password_email_edit_text = view.findViewById(R.id.password_edittext);


        Button buttonRegistration = view.findViewById(R.id.signup_button);
        buttonRegistration.setOnClickListener(v -> {
            String email = email_edit_text.getText().toString().trim();
            String password = password_email_edit_text.getText().toString().trim();
            if (isEmailOk(email) & isPasswordOk(password)) {
                if (!this.userViewModel.isAuthenticationError()) {
                    this.userViewModel.getUserMutableLiveData(email, password, false).observe(
                            getViewLifecycleOwner(), result -> {
                                if (result.isSuccess()) {
                                    User user = ((Result.UserResponseSuccess) result).getData();
                                    saveLoginData(email, password, user.getIdToken());
                                    this.userViewModel.setAuthenticationError(false);
                                    Navigation.findNavController(view).navigate(
                                            R.id.action_signinFragment_to_loginFragment);
                                } else {
                                    this.userViewModel.setAuthenticationError(true);
                                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                            getErrorMessage(((Result.Error) result).getMessage()),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    this.userViewModel.getUser(email, password, false);
                }
            } else {
                this.userViewModel.setAuthenticationError(true);
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        R.string.check_login_data_message, Snackbar.LENGTH_SHORT).show();
            }
        });
    }


    private String getErrorMessage(@NonNull String message) {
        switch (message) {
            case WEAK_PASSWORD_ERROR:
                return requireActivity().getString(R.string.error_password);
            case USER_COLLISION_ERROR:
                return requireActivity().getString(R.string.error_user_collision_message);
            default:
                return requireActivity().getString(R.string.unexpected_error);
        }
    }

    private boolean isEmailOk(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Checks if the password is not empty.
     *
     * @param password The password to be checked
     * @return True if the password has at least 6 characters, false otherwise
     */
    private boolean isPasswordOk(String password) {
        // Check if the password length is correct
        EditText pass = requireView().findViewById(R.id.password_edittext);
        if (password == null || password.isEmpty() || password.length() < Constants.MINIMUM_PASSWORD_LENGHT) {
            pass.setError("Error password");
            return false;
        } else {
            pass.setError(null);
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