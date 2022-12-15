package it.unimib.sal.one_two_trip.ui.main;

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
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment {
    public AboutFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AboutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNavigationView = requireActivity()
                .findViewById(R.id.bottom_navigation);
        FloatingActionButton fab = requireActivity().findViewById(R.id.fab);
        LinearLayout contribute = requireActivity().findViewById(R.id.contribute_layout);
        bottomNavigationView.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);

        TextView dev1 = view.findViewById(R.id.dev_1);
        TextView dev2 = view.findViewById(R.id.dev_2);
        TextView dev3 = view.findViewById(R.id.dev_3);
        TextView dev4 = view.findViewById(R.id.dev_4);

        dev1.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
        dev2.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
        dev3.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
        dev4.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));

        contribute.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(getString(R.string.github_link)));
            startActivity(intent);
        });
    }
}
