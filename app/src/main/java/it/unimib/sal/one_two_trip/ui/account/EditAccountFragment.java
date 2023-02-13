package it.unimib.sal.one_two_trip.ui.account;

import android.app.Application;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec;
import com.google.android.material.progressindicator.IndeterminateDrawable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.database.model.Person;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.repository.user.IUserRepository;
import it.unimib.sal.one_two_trip.ui.main.HomeActivity;
import it.unimib.sal.one_two_trip.ui.welcome.UserViewModel;
import it.unimib.sal.one_two_trip.ui.welcome.UserViewModelFactory;
import it.unimib.sal.one_two_trip.util.ServiceLocator;

public class EditAccountFragment extends Fragment {
    private UserViewModel userViewModel;

    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;

    private ImageButton goBackButton;

    private Button editProfileImageButton;

    private TextInputEditText nameEditText;

    private TextInputEditText surnameEditText;

    private TextInputEditText emailEditText;

    private MaterialButton applyChangesButton;

    private IndeterminateDrawable<CircularProgressIndicatorSpec> progressIndicatorDrawable;


    public EditAccountFragment() {
        // Required empty public constructor
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity activity = requireActivity();

        this.goBackButton = view.findViewById(R.id.goBackButton);
        this.editProfileImageButton = view.findViewById(R.id.editProfileImageButton);
        this.applyChangesButton = view.findViewById(R.id.applyChangesButton);
        this.nameEditText = view.findViewById(R.id.editName);
        this.surnameEditText = view.findViewById(R.id.editSurname);
        this.emailEditText = view.findViewById(R.id.editEmail);

        Person p = userViewModel.getLoggedUser();

        this.nameEditText.setText(p.getName());
        this.surnameEditText.setText(p.getSurname());
        this.emailEditText.setText(p.getEmail_address());

        String oldEmail = emailEditText.getText().toString();

        this.goBackButton.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_editAccountFragment_to_accountManagementFragment));

        this.applyChangesButton.setOnClickListener(v -> {

            this.applyChangesButton.setEnabled(false);
            this.applyChangesButton.setIcon(this.progressIndicatorDrawable);

            if(TextUtils.isEmpty(nameEditText.getText().toString())){
                nameEditText.setError("this field cannot be empty");
                return;
            }
            if(TextUtils.isEmpty(surnameEditText.getText().toString())){
                surnameEditText.setError("this field cannot be empty");
                return;
            }
            if(!isEmailOk()){
                emailEditText.setError("email not valid");
                return;
            }

            Person p2 = new Person();
            p2.setId(p.getId());
            p2.setName(nameEditText.getText().toString().trim());
            p2.setSurname(surnameEditText.getText().toString().trim());
            p2.setEmail_address(emailEditText.getText().toString().trim());

            /** manca foto profilo **/

            String newEmail = emailEditText.getText().toString().trim();
            if(!newEmail.equals(oldEmail)){
                userViewModel.changeEmail(newEmail).observe(getViewLifecycleOwner(), result -> {
                    if(result.isSuccess()){
                        Snackbar.make(view, "user email updated",  Snackbar.LENGTH_SHORT).show();
                    }
                    else{
                        Snackbar.make(view, activity.getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
            userViewModel.updateUserData(p2).observe(getViewLifecycleOwner(), result -> {
                if(result.isSuccess()){
                    Navigation.findNavController(view).navigate(R.id.action_editAccountFragment_to_accountManagementFragment);
                    return;
                    /** snackbar in next activity? **/
                }
                else{
                    Snackbar.make(view, activity.getString(R.string.unexpected_error),
                            Snackbar.LENGTH_SHORT).show();
                }
                this.applyChangesButton.setEnabled(true);
                this.applyChangesButton.setIcon(null);
            });
        });
    }

    private boolean isEmailOk() {
        if (this.emailEditText.getText() == null) {
            return false;
        }

        String email = this.emailEditText.getText().toString().trim();

        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}