package com.example.worldnews;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;



public class LoginFragment<textInputLayoutEmail, textInputLayoutPassword, v> extends Fragment {


    private static final String TAG = LoginFragment.class.getSimpleName();
    private static final boolean USE_NAVIGATION_COMPONENT = true;


    private View view;


    public LoginFragment() {
        // Required empty public constructor
    }

    
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private DatePickerDialog datePickerDialog;
    public void openDatePicker(View view) {
        datePickerDialog.show();
    }



    Button buttonLogin = view.findViewById(R.id.button2);
    final Button buttonGoogleLogin = view.findViewById(R.id.buttonGoogleLogin);
    final Button buttonRegistration = view.findViewById(R.id.signin_button);

    @Override

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        buttonLogin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Navigation.findNavController(requireView()).navigate(R.id.action_welcomeFragment_to_loginFragment);
            }

        });
    };




    /*private boolean isEmailOk(String email){

        if(!EmailValidator.getInstance().isValid(email)){
            textInputLayoutEmail.setError(getString(R.string.error_email));
            return false;
        }else{
            textInputLayoutEmail.setError(null);
            return true;
        }
*/



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }
}