package it.unimib.sal.one_two_trip.ui.welcome;

import static it.unimib.sal.one_two_trip.util.Constants.INVALID_CREDENTIALS_ERROR;

import android.app.Application;
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

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.repository.user.IUserRepository;
import it.unimib.sal.one_two_trip.util.ServiceLocator;

/**
 * Fragment that allows the user to reset the password of the account.
 * It is used by the {@link WelcomeActivity}.
 */
public class ForgotPasswordFragment extends Fragment {

    private UserViewModel userViewModel;
    private MaterialButton resetButton;
    private TextInputEditText emailEditText;
    private IndeterminateDrawable<CircularProgressIndicatorSpec> progressIndicatorDrawable;

    public ForgotPasswordFragment() {
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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FragmentActivity activity = requireActivity();

        MaterialButton backButton = view.findViewById(R.id.forgot_pwd_back_button);
        backButton.setOnClickListener(v -> activity.onBackPressed());

        this.resetButton = view.findViewById(R.id.reset_password_button);
        this.emailEditText = view.findViewById(R.id.email_forgot_pass);

        CircularProgressIndicatorSpec spec = new CircularProgressIndicatorSpec(activity, null, 0,
                com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall);
        this.progressIndicatorDrawable = IndeterminateDrawable.createCircularDrawable(activity, spec);

        this.emailEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                this.emailEditText.clearFocus();
                onResetPassword();
            }
            return false;
        });

        this.resetButton.setOnClickListener(v -> onResetPassword());
    }

    /**
     * Method to reset the password of the account.
     */
    private void onResetPassword() {
        FragmentActivity activity = requireActivity();
        this.resetButton.setEnabled(false);
        this.resetButton.setIcon(this.progressIndicatorDrawable);

        String email = "";

        if (this.emailEditText.getText() != null) {
            email = this.emailEditText.getText().toString().trim();
        }

        if (isEmailOk()) {
            this.userViewModel.resetPassword(email).observe(getViewLifecycleOwner(), result -> {
                if (result != null) {
                    if (result.isSuccess()) {
                        Snackbar.make(activity.findViewById(android.R.id.content),
                                getString(R.string.reset_password_mail_sent), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(activity.findViewById(android.R.id.content),
                                getErrorMessage(((Result.Error) result).getMessage()), Snackbar.LENGTH_SHORT).show();
                    }
                }
                this.resetButton.setEnabled(true);
                this.resetButton.setIcon(null);
            });
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content),
                    R.string.check_login_data_message, Snackbar.LENGTH_SHORT).show();
            this.resetButton.setEnabled(true);
            this.resetButton.setIcon(null);
        }
    }

    /**
     * Checks if the email is valid.
     *
     * @return true if the email is valid, false otherwise.
     */
    private boolean isEmailOk() {
        if (this.emailEditText.getText() == null) {
            return false;
        }

        String email = this.emailEditText.getText().toString().trim();

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.emailEditText.setError(null);
            return true;
        } else {
            this.emailEditText.setError(getString(R.string.error_email_not_valid));
            return false;
        }
    }

    /**
     * Returns the error message to show to the user.
     *
     * @param message the error message returned by the server.
     * @return the error message localized to show to the user.
     */
    private String getErrorMessage(@NonNull String message) {
        if (INVALID_CREDENTIALS_ERROR.equals(message)) {
            return requireActivity().getString(R.string.error_user_doesnt_exists);
        }
        return requireActivity().getString(R.string.unexpected_error);
    }
}
