package it.unimib.sal.one_two_trip.ui.welcome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import it.unimib.sal.one_two_trip.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private static final String TAG = LoginFragment.class.getSimpleName();

    private static final boolean USE_NAVIGATION_COMPONENT = true;


    private View view;


    public LoginFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Button buttonLogin = view.findViewById(R.id.button3);
        final Button buttonGoogleLogin = view.findViewById(R.id.buttonGoogleLogin);
        final Button buttonRegistration = view.findViewById(R.id.signin_button);
        buttonLogin.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.action_welcomeFragment_to_loginFragment));

        Button buttonForgotPassword = view.findViewById(R.id.button);
        buttonForgotPassword.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigate(R.id.action_welcomeFragment_to_forgotPasswordFragment));
    }




    ;
}