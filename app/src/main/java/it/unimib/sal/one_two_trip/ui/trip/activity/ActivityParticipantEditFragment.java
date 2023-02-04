package it.unimib.sal.one_two_trip.ui.trip.activity;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import it.unimib.sal.one_two_trip.R;
import it.unimib.sal.one_two_trip.adapter.ParticipantRecyclerViewAdapter;
import it.unimib.sal.one_two_trip.data.repository.trips.ITripsRepository;
import it.unimib.sal.one_two_trip.data.database.model.Activity;
import it.unimib.sal.one_two_trip.data.database.model.Person;
import it.unimib.sal.one_two_trip.data.database.model.Result;
import it.unimib.sal.one_two_trip.data.database.model.Trip;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModel;
import it.unimib.sal.one_two_trip.ui.main.TripsViewModelFactory;
import it.unimib.sal.one_two_trip.util.ErrorMessagesUtil;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;

/**
 * Fragment that enables the user to edit the participants of an activity.
 */
public class ActivityParticipantEditFragment extends Fragment {

    private Application application;
    private TripsViewModel viewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private Trip trip;
    private Activity activity;
    private List<Person> personList;
    private List<Person> notParticipating;
    private ParticipantRecyclerViewAdapter participantAdapter;
    private ParticipantRecyclerViewAdapter notParticipantAdapter;

    public ActivityParticipantEditFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        androidx.fragment.app.FragmentActivity activity = requireActivity();
        this.application = activity.getApplication();
        this.sharedPreferencesUtil = new SharedPreferencesUtil(this.application);
        ITripsRepository tripsRepository = ServiceLocator.getInstance()
                .getTripsRepository(this.application);
        if (tripsRepository != null) {
            this.viewModel = new ViewModelProvider(activity,
                    new TripsViewModelFactory(tripsRepository)).get(TripsViewModel.class);
        } else {
            Snackbar.make(activity.findViewById(android.R.id.content),
                    getString(R.string.unexpected_error), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity_participant_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = requireContext();

        if (getParentFragment() == null || getParentFragment().getParentFragment() == null) {
            return;
        }

        ActivityFragment parentFragment = (ActivityFragment) getParentFragment().getParentFragment();
        String tripId = parentFragment.getTripId();
        String activityId = parentFragment.getActivityId();

        RecyclerView participant_recycler = view.findViewById(R.id.activity_participant_recycler_edit);
        RecyclerView not_participant_recycler = view.findViewById(R.id.activity_not_participant_recycler_edit);
        MaterialButton confirm = view.findViewById(R.id.activity_participant_confirm);

        confirm.setOnClickListener(view1 -> {
            this.activity.getParticipant().setPersonList(this.personList);

            if (this.trip.getParticipant() != null && this.trip.getParticipant().getPersonList() != null) {
                this.activity.setEveryoneParticipate(this.personList.size()
                        == this.trip.getParticipant().getPersonList().size());
            }

            this.viewModel.updateTrip(this.trip);

            Navigation.findNavController(view).navigate(
                    R.id.action_activityParticipantEditFragment_to_activityParticipantFragment);
        });

        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE);
        }

        this.viewModel.getTrips(Long.parseLong(lastUpdate)).observe(
                getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        List<Trip> trips = ((Result.TripSuccess) result).getData().getTripList();

                        for (Trip mTrip : trips) {
                            if (mTrip.getId().equals(tripId)) {
                                this.trip = mTrip;
                                break;
                            }
                        }

                        if (this.trip == null || this.trip.getActivity() == null
                                || this.trip.getActivity().getActivityList() == null) {
                            return;
                        }

                        for (Activity mActivity : this.trip.getActivity().getActivityList()) {
                            if (mActivity.getId().equals(activityId)) {
                                this.activity = mActivity;
                                break;
                            }
                        }

                        if (this.activity == null || this.activity.getParticipant() == null
                                || this.activity.getParticipant().getPersonList() == null)
                            return;

                        this.personList = this.activity.getParticipant().getPersonList();

                        if (this.trip.getParticipant() == null || this.trip.getParticipant().getPersonList() == null) {
                            return;
                        }

                        this.notParticipating = new ArrayList<>(this.trip.getParticipant().getPersonList());
                        this.notParticipating.removeAll(this.personList);

                        //Participating
                        this.participantAdapter = new ParticipantRecyclerViewAdapter(
                                this.personList,
                                this.application,
                                position -> {
                                    Person p = this.personList.remove(position);
                                    this.notParticipating.add(p);
                                    int size = this.notParticipating.size();
                                    this.notParticipantAdapter.notifyItemRangeInserted(size - 1,
                                            size);
                                    this.participantAdapter.notifyItemRemoved(position);
                                });

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context,
                                LinearLayoutManager.HORIZONTAL, false);
                        participant_recycler.setLayoutManager(layoutManager);
                        participant_recycler.setAdapter(participantAdapter);

                        //Not Participating
                        this.notParticipantAdapter = new ParticipantRecyclerViewAdapter(
                                this.notParticipating,
                                this.application,
                                position -> {
                                    Person p = this.notParticipating.remove(position);
                                    this.personList.add(p);
                                    int size = this.personList.size();
                                    this.participantAdapter.notifyItemRangeInserted(size - 1,
                                            size);
                                    this.notParticipantAdapter.notifyItemRemoved(position);
                                });

                        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(context,
                                LinearLayoutManager.HORIZONTAL, false);
                        not_participant_recycler.setLayoutManager(layoutManager2);
                        not_participant_recycler.setAdapter(notParticipantAdapter);
                    } else {
                        ErrorMessagesUtil errorMessagesUtil = new ErrorMessagesUtil(this.application);
                        Snackbar.make(view, errorMessagesUtil.getErrorMessage(((Result.Error) result)
                                .getMessage()), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }
}
