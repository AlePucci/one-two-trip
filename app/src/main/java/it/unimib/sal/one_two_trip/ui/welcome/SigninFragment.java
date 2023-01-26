package it.unimib.sal.one_two_trip.ui.welcome;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

import it.unimib.sal.one_two_trip.R;


public class SigninFragment extends Fragment {

    boolean maschio;

    public SigninFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
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
    }



    public static SigninFragment newInstance() {
        return new SigninFragment();
    }
    DatePickerDialog datePickerDialog;
    private Button dateButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


}