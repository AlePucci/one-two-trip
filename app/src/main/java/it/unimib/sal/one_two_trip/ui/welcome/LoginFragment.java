package it.unimib.sal.one_two_trip.ui.welcome;

import static android.provider.Telephony.Carriers.PASSWORD;
import static it.unimib.sal.one_two_trip.util.Constants.EMAIL_ADDRESS;
import static it.unimib.sal.one_two_trip.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.ID_TOKEN;
import static it.unimib.sal.one_two_trip.util.Constants.INVALID_CREDENTIALS_ERROR;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec;
import com.google.android.material.progressindicator.IndeterminateDrawable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.database.model.User;
import it.unimib.sal.one_two_trip.data.repository.user.IUserRepository;
import it.unimib.sal.one_two_trip.ui.main.HomeActivity;
import it.unimib.sal.one_two_trip.util.DataEncryptionUtil;
import it.unimib.sal.one_two_trip.util.ServiceLocator;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private UserViewModel userViewModel;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private DataEncryptionUtil dataEncryptionUtil;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private MaterialButton loginButton;
    private IndeterminateDrawable<CircularProgressIndicatorSpec> progressIndicatorDrawable;


    public LoginFragment() {
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

        this.dataEncryptionUtil = new DataEncryptionUtil(application);

        // LOGIN
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

        this.activityResultLauncher = registerForActivityResult(startIntentSenderForResult,
                activityResult -> {
                    if (activityResult.getResultCode() == Activity.RESULT_OK) {
                        try {
                            SignInCredential credential =
                                    this.oneTapClient.getSignInCredentialFromIntent(activityResult.getData());
                            String idToken = credential.getGoogleIdToken();

                            if (idToken != null) {
                                this.userViewModel.getGoogleUserMutableLiveData(idToken).observe(
                                        getViewLifecycleOwner(),
                                        authenticationResult -> {
                                            if (authenticationResult.isSuccess()) {
                                                User user = ((Result.UserResponseSuccess) authenticationResult).getData();
                                                saveLoginData(user.getEmail(), null, user.getIdToken());
                                                this.userViewModel.setAuthenticationError(false);
                                                startActivity(new Intent(activity, HomeActivity.class));
                                                activity.finish();
                                            } else {
                                                this.userViewModel.setAuthenticationError(true);
                                                Snackbar.make(activity.findViewById(android.R.id.content),
                                                        getErrorMessage(((Result.Error) authenticationResult)
                                                                .getMessage()),
                                                        Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } catch (ApiException e) {
                            Snackbar.make(activity.findViewById(android.R.id.content),
                                    activity.getString(R.string.unexpected_error),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity activity = requireActivity();

        if (this.userViewModel.getLoggedUser() != null) {
            activity.startActivity(new Intent(activity, HomeActivity.class));
            activity.finish();
            return;
        }

        MaterialButton signupButton = view.findViewById(R.id.buttonRegister);
        MaterialButton forgotPasswordButton = view.findViewById(R.id.buttonForgotPassword);
        MaterialButton googleLoginButton = view.findViewById(R.id.buttonGoogleLogin);
        this.loginButton = view.findViewById(R.id.buttonLogin);
        this.emailEditText = view.findViewById(R.id.email_login);
        this.passwordEditText = view.findViewById(R.id.password_login);

        CircularProgressIndicatorSpec spec = new CircularProgressIndicatorSpec(activity, null, 0,
                com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall);
        this.progressIndicatorDrawable = IndeterminateDrawable.createCircularDrawable(activity, spec);

        this.passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                this.passwordEditText.clearFocus();
                onLoginClick();
            }
            return false;
        });

        this.loginButton.setOnClickListener(v -> onLoginClick());

        signupButton.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_loginFragment_to_signinFragment));

        forgotPasswordButton.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_loginFragment_to_forgotPasswordFragment));

        googleLoginButton.setOnClickListener(v -> {
            googleLoginButton.setEnabled(false);
            googleLoginButton.setIcon(progressIndicatorDrawable);

            this.oneTapClient.beginSignIn(this.signInRequest)
                    .addOnSuccessListener(activity, result -> {
                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent()).build();
                        this.activityResultLauncher.launch(intentSenderRequest);
                        googleLoginButton.setEnabled(true);
                        googleLoginButton.setIcon(null);
                    })
                    .addOnFailureListener(activity, e -> {
                        Snackbar.make(view,
                                activity.getString(R.string.unexpected_error),
                                Snackbar.LENGTH_SHORT).show();
                        googleLoginButton.setEnabled(true);
                        googleLoginButton.setIcon(null);
                    });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        this.userViewModel.setAuthenticationError(false);
    }

    private void onLoginClick() {
        FragmentActivity activity = requireActivity();

        this.loginButton.setEnabled(false);
        this.loginButton.setIcon(this.progressIndicatorDrawable);

        String email = "";
        if (this.emailEditText.getText() != null) {
            email = this.emailEditText.getText().toString().trim();
        }

        String password = "";
        if (this.passwordEditText.getText() != null) {
            password = this.passwordEditText.getText().toString().trim();
        }

        if (isEmailOk() && isPasswordOk()) {
            if (!this.userViewModel.isAuthenticationError()) {
                String finalEmail = email;
                String finalPassword = password;

                this.userViewModel.getUserMutableLiveData(email, password, true).observe(
                        getViewLifecycleOwner(), result -> {
                            if (result.isSuccess()) {
                                User user = ((Result.UserResponseSuccess) result).getData();
                                saveLoginData(finalEmail, finalPassword, user.getIdToken());
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
                            this.loginButton.setEnabled(true);
                            this.loginButton.setIcon(null);
                        });
            } else {
                this.userViewModel.getUser(email, password, true);
            }
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content),
                    R.string.check_login_data_message, Snackbar.LENGTH_SHORT).show();
            this.loginButton.setEnabled(true);
            this.loginButton.setIcon(null);
        }
    }

    private boolean isPasswordOk() {
        if (this.passwordEditText.getText() == null) {
            return false;
        }

        String password = this.passwordEditText.getText().toString().trim();

        return !password.isEmpty();
    }

    private boolean isEmailOk() {
        if (this.emailEditText.getText() == null) {
            return false;
        }

        String email = this.emailEditText.getText().toString().trim();

        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @NonNull
    private String getErrorMessage(@NonNull String errorType) {
        if (INVALID_CREDENTIALS_ERROR.equals(errorType)) {
            return getString(R.string.error_login_user_or_password_message);
        }
        return getString(R.string.unexpected_error);
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
