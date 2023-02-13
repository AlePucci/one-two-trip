package it.unimib.sal.one_two_trip.ui.welcome;

import static android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import it.unimib.sal.one_two_trip.R;

/**
 * This activity manages the signin/signup/password forgot process.
 */
public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.yellow_800));
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        // Used to hide the keyboard when the user clicks outside an EditText
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}

