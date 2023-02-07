package it.unimib.sal.one_two_trip.ui.welcome;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import it.unimib.sal.one_two_trip.R;

public class ForgotPasswordFragment extends Fragment {
    private TextInputEditText editText;

    public ForgotPasswordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button submitButton = view.findViewById(R.id.submit_button);
        editText = view.findViewById(R.id.email_forgot_pass);

        submitButton.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (editText.getText() == null || editText.getText().toString().isEmpty()) {
                editText.setError("Email is required");
                return;
            }

            auth.sendPasswordResetEmail((editText.getText().toString())).addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Password reset email sent");
                        } else {
                            Log.d(TAG, "Failed to send password reset email", task.getException());
                        }
                    }
            );

        });


    }
}