package it.unimib.sal.one_two_trip.ui.welcome;

import static it.unimib.sal.one_two_trip.util.Constants.EMAIL_ADDRESS;
import static it.unimib.sal.one_two_trip.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.sal.one_two_trip.util.Constants.ID_TOKEN;
import static it.unimib.sal.one_two_trip.util.Constants.PASSWORD;
import static it.unimib.sal.one_two_trip.util.Constants.USER_COLLISION_ERROR;
import static it.unimib.sal.one_two_trip.util.Constants.WEAK_PASSWORD_ERROR;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.common.SignInButton;
import com.google.android.material.snackbar.Snackbar;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.User;
import it.unimib.sal.one_two_trip.util.Constants;


public class SigninFragment extends Fragment {

    private DataEncryptionUtil dataEncryptionUtil;
    private UserViewModel userViewModel;
    boolean maschio;

    public SigninFragment() {
        // Required empty public constructor
    }




    public static SigninFragment newInstance() {
        return new SigninFragment();
    }
    DatePickerDialog datePickerDialog;
    private Button dateButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.setAuthenticationError(false);
        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());
        initDatePicker();
        datePickerDialog = new DatePickerDialog(requireContext());
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }


    public void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, day) -> {
            month = month +1;
            String date = makeDateString(day, month, year);
            dateButton.setText(date);
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        //datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if(month ==1)
            return "JAN";
        if(month ==2)
            return "FEB";
        if(month ==3)
            return "MAR";
        if(month ==4)
            return "APR";
        if(month ==5)
            return "MAY";
        if(month ==6)
            return "JUN";
        if(month ==7)
            return "JUL";
        if(month ==8)
            return "AUG";
        if(month ==9)
            return "SEP";
        if(month ==10)
            return "OCT";
        if(month ==11)
            return "NOV";
        if(month ==12)
            return "DEC";
        //default
        return "JAN";
    }

    public void openDatePicker(){
        datePickerDialog.show();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }

    public void openDatePicker(View view) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(year, month, dayOfMonth);
                month = month +1;
                String date = makeDateString(dayOfMonth, month, year);
                dateButton.setText(date);
            };
        },  cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }


    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){
            case R.id.firstButton:
                if(checked) {
                    this.maschio = true;
                }
                break;
            case R.id.secondButton:
                if(checked){
                    this.maschio = false;
                }
                break;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dateButton = view.findViewById(R.id.datePicker);
        dateButton.setText(getTodaysDate());
        Button setDate = view.findViewById(R.id.datePicker);
        Button maleButton = view.findViewById(R.id.firstButton);
        Button femaleButton = view.findViewById(R.id.secondButton);
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(v);
            }
        });
        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });
        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });


        EditText email_edit_text = view.findViewById(R.id.email_edit_text);
        EditText password_email_edit_text = view.findViewById(R.id.password_email_edit_text);


        Button buttonRegistration = view.findViewById(R.id.signin_button);
        buttonRegistration.setOnClickListener(v -> {
            String email = email_edit_text.getText().toString().trim();
            String password = password_email_edit_text.getText().toString().trim();
            if (isEmailOk(email) & isPasswordOk(password)) {
                if (!userViewModel.isAuthenticationError()) {
                    userViewModel.getUserMutableLiveData(email, password, false).observe(
                            getViewLifecycleOwner(), result -> {
                                if (result.isSuccess()) {
                                    User user = ((Result.UserResponseSuccess) result).getData();
                                    saveLoginData(email, password, user.getIdToken());
                                    userViewModel.setAuthenticationError(false);
                                    Navigation.findNavController(view).navigate(
                                            R.id.action_welcomeFragment_to_homeActivity);
                                } else {
                                    userViewModel.setAuthenticationError(true);
                                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                            getErrorMessage(((Result.Error) result).getMessage()),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    userViewModel.getUser(email, password, false);
                }
            } else {
                userViewModel.setAuthenticationError(true);
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        R.string.check_login_data_message, Snackbar.LENGTH_SHORT).show();
            }
        });
    }


    private String getErrorMessage(String message) {
        switch(message) {
            case WEAK_PASSWORD_ERROR:
                return requireActivity().getString(R.string.error_password);
            case USER_COLLISION_ERROR:
                return requireActivity().getString(R.string.error_user_collision_message);
            default:
                return requireActivity().getString(R.string.unexpected_error);
        }
    }

    private boolean isEmailOk(String email) {
        EditText emailToText = requireActivity().findViewById(R.id.email_edit_text);
        if (emailToText != null && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getActivity().getApplicationContext(), "Email Verified !", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Enter valid Email address !", Toast.LENGTH_SHORT).show();
            return false;
        }


    }

    /**
     * Checks if the password is not empty.
     * @param password The password to be checked
     * @return True if the password has at least 6 characters, false otherwise
     */
    private boolean isPasswordOk(String password) {
        // Check if the password length is correct
        EditText pass = requireView().findViewById(R.id.password_email_edit_text);
        if (password == null || password.length() < Constants.MINIMUM_PASSWORD_LENGHT) {
            pass.setError("Error password");
            return false;
        } else {
            pass.setError(null);
            return true;
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
            dataEncryptionUtil.writeSecreteDataOnFile(ENCRYPTED_DATA_FILE_NAME,
                    email.concat(":").concat(password));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



}