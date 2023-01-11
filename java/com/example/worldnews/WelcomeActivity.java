package com.example.worldnews;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void login(View v){
        EditText username = findViewById(R.id.textInputLayout);
        EditText password = findViewById(R.id.textInputLayout1);

    }
}