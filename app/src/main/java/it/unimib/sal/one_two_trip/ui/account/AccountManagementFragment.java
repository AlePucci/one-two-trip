package it.unimib.sal.one_two_trip.ui.account;

import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.USER_COLOR;

import android.app.Application;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import it.unimib.sal.one_two_trip.ui.welcome.UserViewModel;
import it.unimib.sal.one_two_trip.ui.welcome.UserViewModelFactory;
import it.unimib.sal.one_two_trip.ui.welcome.WelcomeActivity;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;
import it.unimib.sal.one_two_trip.util.Utility;

public class AccountManagementFragment extends Fragment {

    private UserViewModel userViewModel;
    private ProgressBar progressBar;
    private TextView nameSurnameTextView;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private TextView emailTextView;
    private TextView profileImage;

    public AccountManagementFragment() {
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

        String id = this.userViewModel.getLoggedUser().getId();

        this.progressBar.setVisibility(View.VISIBLE);
        this.profileImage.setVisibility(View.INVISIBLE);

        this.userViewModel.getUser(id).observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                Person p = ((Result.PersonResponseSuccess) result).getData();
                String fullName = p.getName() + " " + p.getSurname();
                this.nameSurnameTextView.setText(fullName);
                this.emailTextView.setText(p.getEmail_address());
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

                this.profileImage.setVisibility(View.VISIBLE);
                this.progressBar.setVisibility(View.GONE);
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
}
