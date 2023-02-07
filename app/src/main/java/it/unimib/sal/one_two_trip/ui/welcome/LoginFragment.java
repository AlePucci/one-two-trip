package it.unimib.sal.one_two_trip.ui.welcome;

import static android.provider.Telephony.Carriers.PASSWORD;
import static it.unimib.sal.one_two_trip.util.Constants.EMAIL_ADDRESS;
import static it.unimib.sal.one_two_trip.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.ID_TOKEN;
import static it.unimib.sal.one_two_trip.util.Constants.INVALID_CREDENTIALS_ERROR;
import static it.unimib.sal.one_two_trip.util.Constants.INVALID_USER_ERROR;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.User;
import it.unimib.sal.one_two_trip.repository.user.IUserRepository;
import it.unimib.sal.one_two_trip.ui.main.HomeActivity;
import it.unimib.sal.one_two_trip.util.Constants;
import it.unimib.sal.one_two_trip.util.ServiceLocator;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private UserViewModel userViewModel;
    private TextInputEditText textInputEditEmail;
    private TextInputEditText textInputEditPassword;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private DataEncryptionUtil dataEncryptionUtil;


    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentActivity activity = requireActivity();
        Application application = activity.getApplication();
        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(application);
        this.userViewModel = new ViewModelProvider(
                activity,
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        this.dataEncryptionUtil = new DataEncryptionUtil(application);

        this.oneTapClient = Identity.getSignInClient(activity);
        this.signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.WEB_SERVER_ID))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .setAutoSelectEnabled(true)
                .build();

        ActivityResultContracts.StartIntentSenderForResult startIntentSenderForResult =
                new ActivityResultContracts.StartIntentSenderForResult();

        this.activityResultLauncher = registerForActivityResult(startIntentSenderForResult, activityResult -> {
            if (activityResult.getResultCode() == Activity.RESULT_OK) {
                try {
                    SignInCredential credential = this.oneTapClient.getSignInCredentialFromIntent(activityResult.getData());
                    String idToken = credential.getGoogleIdToken();
                    if (idToken != null) {
                        this.userViewModel.getGoogleUserMutableLiveData(idToken).observe(
                                getViewLifecycleOwner(),
                                authenticationResult -> {
                                    if (authenticationResult.isSuccess()) {
                                        User user = ((Result.UserResponseSuccess) authenticationResult).getData();
                                        saveLoginData(user.getEmail(), null, user.getIdToken());
                                        userViewModel.setAuthenticationError(false);
                                        startActivity(new Intent(activity, HomeActivity.class));
                                    } else {
                                        userViewModel.setAuthenticationError(true);
                                        Snackbar.make(activity.findViewById(android.R.id.content),
                                                getErrorMessage(((Result.Error) authenticationResult)
                                                        .getMessage()),
                                                Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } catch (ApiException e) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            requireActivity().getString(R.string.unexpected_error),
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button buttonLogin = view.findViewById(R.id.button2);
        Button buttonNotReg = view.findViewById(R.id.button3);
        Button buttonForgotPassword = view.findViewById(R.id.button);
        Button buttonGoogleLogin = view.findViewById(R.id.buttonGoogleLogin);

        buttonLogin.setOnClickListener(v -> {
            this.textInputEditEmail = view.findViewById(R.id.email_login);
            this.textInputEditPassword = view.findViewById(R.id.password_login);
            String email = "";

            if (textInputEditEmail.getText() != null) {
                email = textInputEditEmail.getText().toString().trim();
            }

            String password = "";
            if (textInputEditPassword.getText() != null) {
                password = textInputEditPassword.getText().toString().trim();
            }

            if (isEmailOk(email) & isPasswordOk(password)) {
                if (!this.userViewModel.isAuthenticationError()) {
                    String finalEmail = email;
                    String finalPassword = password;
                    this.userViewModel.getUserMutableLiveData(
                            email, password, true).observe(
                            getViewLifecycleOwner(), result -> {
                                if (result.isSuccess()) {
                                    User user = ((Result.UserResponseSuccess) result).getData();
                                    saveLoginData(finalEmail, finalPassword, user.getIdToken());
                                    this.userViewModel.setAuthenticationError(false);
                                    startActivity(new Intent(requireActivity(), HomeActivity.class));
                                } else {
                                    this.userViewModel.setAuthenticationError(true);
                                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                            getErrorMessage(((Result.Error) result).getMessage()),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    this.userViewModel.getUser(email, password, true);
                }
            } else {
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        R.string.check_login_data_message, Snackbar.LENGTH_SHORT).show();
            }
        });


        buttonNotReg.setOnClickListener(v -> Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_signinFragment));

        buttonForgotPassword.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_loginFragment_to_forgotPasswordFragment));

        buttonGoogleLogin.setOnClickListener(v -> this.oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity(), result -> {
                    IntentSenderRequest intentSenderRequest =
                            new IntentSenderRequest.Builder(result.getPendingIntent()).build();
                    activityResultLauncher.launch(intentSenderRequest);
                })
                .addOnFailureListener(requireActivity(), e ->
                        Log.d("GoogleOneTap", "Error: " + e.getMessage())));
    }

    @Override
    public void onResume() {
        super.onResume();
        this.userViewModel.setAuthenticationError(false);
    }

    private boolean isPasswordOk(String password) {
        EditText pass = requireView().findViewById(R.id.password_login);

        if (password == null || password.isEmpty() || password.length() < Constants.MINIMUM_PASSWORD_LENGHT) {
            pass.setError("Error password");
            return false;
        } else {
            pass.setError(null);
            return true;
        }
    }

    private boolean isEmailOk(String email) {
        EditText emailToText = requireView().findViewById(R.id.email_login);

        return emailToText != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private String getErrorMessage(@NonNull String errorType) {
        switch (errorType) {
            case INVALID_CREDENTIALS_ERROR:
                return requireActivity().getString(R.string.error_login_password_message);
            case INVALID_USER_ERROR:
                return requireActivity().getString(R.string.error_login_user_message);
            default:
                return requireActivity().getString(R.string.unexpected_error);
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

            if (password != null) {
                this.dataEncryptionUtil.writeSecreteDataOnFile(ENCRYPTED_DATA_FILE_NAME,
                        email.concat(":").concat(password));
            }

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}