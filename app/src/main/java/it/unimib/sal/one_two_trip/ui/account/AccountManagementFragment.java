package it.unimib.sal.one_two_trip.ui.account;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.database.model.Person;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.repository.user.IUserRepository;
import it.unimib.sal.one_two_trip.ui.main.HomeActivity;
import it.unimib.sal.one_two_trip.ui.welcome.UserViewModel;
import it.unimib.sal.one_two_trip.ui.welcome.UserViewModelFactory;
import it.unimib.sal.one_two_trip.ui.welcome.WelcomeActivity;
import it.unimib.sal.one_two_trip.util.ServiceLocator;

public class AccountManagementFragment extends Fragment {

    /** manca l'immagine **/
    private UserViewModel userViewModel;

    private ImageButton goBackButton;

    private Button editAccountButton;

    private Button deleteAccountButton;

    private Button logoutButton;

    private TextView nameSurnameTextView;

    private TextView emailTextView;

    public AccountManagementFragment() {
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
        return inflater.inflate(R.layout.fragment_account_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity activity = requireActivity();

        this.goBackButton = view.findViewById(R.id.goBackButton);
        this.nameSurnameTextView = view.findViewById(R.id.nameSurnameTextView);
        this.emailTextView = view.findViewById(R.id.emailTextView);
        this.editAccountButton = view.findViewById(R.id.editAccountButton);
        this.deleteAccountButton = view.findViewById(R.id.deleteAccountButton);
        this.logoutButton = view.findViewById(R.id.logoutButton);

        if(userViewModel.getLoggedUser() == null){
            activity.finish();
            return;
        }

        String id = userViewModel.getLoggedUser().getId();

        this.userViewModel.getUser(id).observe(getViewLifecycleOwner(), result -> {
            if(result.isSuccess()){
                Person p = ((Result.PersonResponseSuccess) result).getData();
                nameSurnameTextView.setText(p.getName() + " " + p.getSurname());
                emailTextView.setText(p.getEmail_address());
            }
        });

        this.goBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity, HomeActivity.class);
            startActivity(intent);
            activity.finish();
            return;
        });


        this.editAccountButton.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_accountManagementFragment_to_editAccountFragment));


        this.deleteAccountButton.setOnClickListener(v -> {
            userViewModel.deleteUser().observe(getViewLifecycleOwner(), result -> {
                if(result.isSuccess()){
                    Intent intent = new Intent(activity, WelcomeActivity.class);
                    startActivity(intent);
                    activity.finish();
                    return;
                }
                else{
                    Snackbar.make(view, activity.getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
                }
            });
        });


        this.logoutButton.setOnClickListener(v -> {
            userViewModel.logout().observe(getViewLifecycleOwner(), result -> {
                if(result.isSuccess()){
                    Intent intent = new Intent(activity, WelcomeActivity.class);
                    startActivity(intent);
                    activity.finish();
                    return;
                }
                else{
                    Snackbar.make(view, activity.getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
                }
            });
        });
    }
}