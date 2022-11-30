package it.unimib.sal.one_two_trip.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.adapter.TripsRecyclerViewAdapter;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.repository.TripsMockRepository;
import it.unimib.sal.one_two_trip.util.ResponseCallback;

/**
 * A simple {@link Fragment} subclass that shows the coming trips of the user.
 * Use the {@link ComingTripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComingTripsFragment extends Fragment implements ResponseCallback {

    private static final String TAG = ComingTripsFragment.class.getSimpleName();

    private List<Trip> comingTrips;
    private ITripsRepository iTripsRepository;
    private TripsRecyclerViewAdapter tripsRecyclerViewAdapter;
    private ProgressBar progressBar;

    public ComingTripsFragment() {
    }

    public static ComingTripsFragment newInstance() {
        return new ComingTripsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        comingTrips = new ArrayList<>();
        iTripsRepository = new TripsMockRepository(requireActivity().getApplication(),
                this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_coming_trips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView comingTripsView = view.findViewById(R.id.coming_trips_view);
        TextView comingTripsTitle = view.findViewById(R.id.coming_trips_title);
        TextView noTripsText = view.findViewById(R.id.no_trips_text);
        ImageView noTripsImage = view.findViewById(R.id.no_trips_image);

        progressBar = view.findViewById(R.id.progress_bar);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL, false);

        tripsRecyclerViewAdapter = new TripsRecyclerViewAdapter(comingTrips,
                requireActivity().getApplication(),
                new TripsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onTripShare(Trip trip) {
                Snackbar.make(view, "Share " + trip.getTitle(), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onTripClick(Trip trip) {
                Snackbar.make(view, trip.getTitle(), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onButtonClick(Trip trip) {
                Snackbar.make(view, trip.getTitle(), Snackbar.LENGTH_SHORT).show();
            }
        });

        comingTripsView.setLayoutManager(layoutManager);
        comingTripsView.setAdapter(tripsRecyclerViewAdapter);

        progressBar.setVisibility(View.VISIBLE);
        iTripsRepository.fetchTrips(0);

        noTripsText.setText(R.string.no_coming_trips);

        tripsRecyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkSize();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkSize();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkSize();
            }

            private void checkSize() {
                int comingTripsCount = tripsRecyclerViewAdapter.getItemCount();

                if (comingTripsCount == 1) {
                    comingTripsTitle.setText(R.string.coming_trips_title_single);
                } else {
                    comingTripsTitle.setText(String.format(getString(R.string.coming_trips_title_multiple),
                            comingTripsCount));
                }

                comingTripsView.setVisibility(comingTripsCount == 0 ? View.GONE : View.VISIBLE);
                comingTripsTitle.setVisibility(comingTripsCount == 0 ? View.GONE : View.VISIBLE);
                noTripsImage.setVisibility(comingTripsCount == 0 ? View.VISIBLE : View.GONE);
                noTripsText.setVisibility(comingTripsCount == 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onSuccess(List<Trip> tripList, long lastUpdate) {
        if (tripList != null && !tripList.isEmpty()) {
            this.comingTrips.clear();

            List<Trip> temp = new ArrayList<>(tripList);

            for (Iterator<Trip> i = temp.iterator(); i.hasNext(); ) {
                Trip trip = i.next();
                if (trip != null && trip.isCompleted()) i.remove();
            }
            this.comingTrips.addAll(temp);

            requireActivity().runOnUiThread(() -> {
                tripsRecyclerViewAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            });
        }
    }

    @Override
    public void onFailure(String errorMessage) {
        Snackbar.make(requireActivity().findViewById(android.R.id.content), errorMessage,
                Snackbar.LENGTH_LONG).show();
    }
}
