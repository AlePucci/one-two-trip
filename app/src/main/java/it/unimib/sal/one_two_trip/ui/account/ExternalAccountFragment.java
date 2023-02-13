package it.unimib.sal.one_two_trip.ui.account;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.data.database.model.Person;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.repository.user.IUserRepository;
import it.unimib.sal.one_two_trip.ui.welcome.UserViewModel;
import it.unimib.sal.one_two_trip.ui.welcome.UserViewModelFactory;
import it.unimib.sal.one_two_trip.util.ServiceLocator;

public class ExternalAccountFragment extends Fragment {

    /**
     * manca l'immagine
     **/
    private UserViewModel userViewModel;

    private ImageButton goBackButton;

    private TextView nameSurnameTextView;

    private TextView emailTextView;

    public ExternalAccountFragment() {
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String id = "";

        FragmentActivity activity = requireActivity();

        this.goBackButton = view.findViewById(R.id.goBackButton);
        this.nameSurnameTextView = view.findViewById(R.id.nameSurnameTextView);
        this.emailTextView = view.findViewById(R.id.emailTextView);

        this.goBackButton.setOnClickListener(v -> activity.finish());

        this.userViewModel.getUser(id).observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                Person p = ((Result.PersonResponseSuccess) result).getData();
            } else {
                activity.finish();
            }
        });
    }
}