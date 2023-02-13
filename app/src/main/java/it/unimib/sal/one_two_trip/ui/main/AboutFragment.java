package it.unimib.sal.one_two_trip.ui.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import it.unimib.sal.one_two_trip.R;

/**
 * Fragment that shows simple information about the application, such as the version number,
 * the developers and the GitHub repository to contribute to the project.
 * It is used by the {@link HomeActivity}.
 */
public class AboutFragment extends Fragment {

    private static final String TAG = AboutFragment.class.getSimpleName();

    public AboutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        androidx.fragment.app.FragmentActivity activity = requireActivity();
        BottomNavigationView bottomNavigationView = activity
                .findViewById(R.id.bottom_navigation);
        FloatingActionButton fab = activity.findViewById(R.id.fab);
        LinearLayout contribute = activity.findViewById(R.id.contribute_layout);
        bottomNavigationView.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);

        TextView dev1 = view.findViewById(R.id.dev_1);
        TextView dev2 = view.findViewById(R.id.dev_2);
        TextView dev3 = view.findViewById(R.id.dev_3);
        TextView dev4 = view.findViewById(R.id.dev_4);

        Context context = getContext();

        dev1.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
        dev2.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
        dev3.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
        dev4.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));

        contribute.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(getString(R.string.github_link)));
            startActivity(intent);
        });
    }
}
