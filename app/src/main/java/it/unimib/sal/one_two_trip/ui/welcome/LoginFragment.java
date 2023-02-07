package it.unimib.sal.one_two_trip.ui.welcome;

import static android.provider.Telephony.Carriers.PASSWORD;
import static it.unimib.sal.one_two_trip.util.Constants.EMAIL_ADDRESS;
import static it.unimib.sal.one_two_trip.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.ID_TOKEN;
import static it.unimib.sal.one_two_trip.util.Constants.INVALID_CREDENTIALS_ERROR;
import static it.unimib.sal.one_two_trip.util.Constants.INVALID_USER_ERROR;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;



import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.User;
import it.unimib.sal.one_two_trip.repository.user.IUserRepository;
import it.unimib.sal.one_two_trip.util.Constants;
import it.unimib.sal.one_two_trip.util.ServiceLocator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private static final String TAG = LoginFragment.class.getSimpleName();
    private UserViewModel userViewModel;
    private TextInputEditText textInputEditEmail;
    private TextInputEditText textInputEditPassword;
    private LinearProgressIndicator progressIndicator;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private FirebaseAuth mFirebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton mLoginButton;

    private ActivityResultContracts.StartIntentSenderForResult startIntentSenderForResult;

    private static final boolean USE_NAVIGATION_COMPONENT = true;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    private View view;
    private DataEncryptionUtil dataEncryptionUtil;


    public LoginFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }
    private void startActivityBasedOnCondition(Class<?> destinationActivity, int destination) {
        if (USE_NAVIGATION_COMPONENT) {
            Navigation.findNavController(requireView()).navigate(destination);
        } else {
            Intent intent = new Intent(requireContext(), destinationActivity);
            startActivity(intent);
        }
        requireActivity().finish();
    }

    private void retrieveUserInformationAndStartActivity(User user, int destination) {
        progressIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());

        oneTapClient = Identity.getSignInClient(requireActivity());
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();

        startIntentSenderForResult = new ActivityResultContracts.StartIntentSenderForResult();

        activityResultLauncher = registerForActivityResult(startIntentSenderForResult, activityResult -> {
            if (activityResult.getResultCode() == Activity.RESULT_OK) {
                Log.d(TAG, "result.getResultCode() == Activity.RESULT_OK");
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(activityResult.getData());
                    String idToken = credential.getGoogleIdToken();
                    if (idToken !=  null) {
                        // Got an ID token from Google. Use it to authenticate with Firebase.
                        userViewModel.getGoogleUserMutableLiveData(idToken).observe(getViewLifecycleOwner(), authenticationResult -> {
                            if (authenticationResult.isSuccess()) {
                                User user = ((Result.UserResponseSuccess) authenticationResult).getData();
                                saveLoginData(user.getEmail(), null, user.getIdToken());
                                userViewModel.setAuthenticationError(false);
                                retrieveUserInformationAndStartActivity(user, R.id.nav_graph);
                            } else {
                                userViewModel.setAuthenticationError(true);
                                progressIndicator.setVisibility(View.GONE);
                                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                        getErrorMessage(((Result.Error) authenticationResult).getMessage()),
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

    private String getErrorMessage(String errorType) {
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
                dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                        ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS, email);
                dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                        ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, password);
                dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                        ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN, idToken);

                if (password != null) {
                    dataEncryptionUtil.writeSecreteDataOnFile(ENCRYPTED_DATA_FILE_NAME,
                            email.concat(":").concat(password));
                }

            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button buttonLogin = view.findViewById(R.id.button2);
        buttonLogin.setOnClickListener(v -> {
            textInputEditEmail = view.findViewById(R.id.email_login);
            textInputEditPassword = view.findViewById(R.id.password_login);
            String email = textInputEditEmail.getText().toString().trim();
            String password = textInputEditPassword.getText().toString().trim();

            // Start login if email and password are ok
            if (isEmailOk(email) & isPasswordOk(password)) {
                if (!userViewModel.isAuthenticationError()) {
                    userViewModel.getUserMutableLiveData(email, password, true).observe(
                            getViewLifecycleOwner(), result -> {
                                if (result.isSuccess()) {
                                    User user = ((Result.UserResponseSuccess) result).getData();
                                    saveLoginData(email, password, user.getIdToken());
                                    userViewModel.setAuthenticationError(false);
                                    retrieveUserInformationAndStartActivity(user, R.id.action_welcomeFragment_to_homeActivity);
                                } else {
                                    userViewModel.setAuthenticationError(true);
                                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                            getErrorMessage(((Result.Error) result).getMessage()),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    userViewModel.getUser(email, password, true);
                }
            } else {
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        R.string.check_login_data_message, Snackbar.LENGTH_SHORT).show();
            }
        });

        Button buttonNotReg = view.findViewById(R.id.button3);
        buttonNotReg.setOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigate(R.id.action_welcomeFragment_to_loginFragment);
        });


        Button buttonForgotPassword = view.findViewById(R.id.button);
        buttonForgotPassword.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.action_welcomeFragment_to_forgotPasswordFragment));

        Button buttonGoogleLogin = view.findViewById(R.id.buttonGoogleLogin);
        buttonGoogleLogin.setOnClickListener(v -> oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        Log.d(TAG, "onSuccess from oneTapClient.beginSignIn(BeginSignInRequest)");
                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent()).build();
                        activityResultLauncher.launch(intentSenderRequest);
                    }
                })
                .addOnFailureListener(requireActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d(TAG, e.getLocalizedMessage());

                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                requireActivity().getString(R.string.error_no_google_account_found_message),
                                Snackbar.LENGTH_SHORT).show();
                    }
                }));
    };
    private boolean isPasswordOk(String password) {
        // Check if the password length is correct
        EditText pass = requireView().findViewById(R.id.password_login);
        if (password == null || password.length() < Constants.MINIMUM_PASSWORD_LENGHT) {
            pass.setError("Error password");
            return false;
        } else {
            pass.setError(null);
            return true;
        }
    }
    private boolean isEmailOk(String email) {
        EditText emailToText = requireActivity().findViewById(R.id.email_login);
        if (emailToText != null && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getActivity().getApplicationContext(), "Email Verified !", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Enter valid Email address !", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        userViewModel.setAuthenticationError(false);
    }




}