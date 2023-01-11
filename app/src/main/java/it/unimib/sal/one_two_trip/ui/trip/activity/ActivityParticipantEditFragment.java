package it.unimib.sal.one_two_trip.ui.trip.activity;

import static it.unimib.sal.one_two_trip.util.Constants.LAST_UPDATE;
import static it.unimib.sal.one_two_trip.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
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
import it.unimib.sal.one_two_trip.model.Activity;
import it.unimib.sal.one_two_trip.model.Person;
import it.unimib.sal.one_two_trip.model.Result;
import it.unimib.sal.one_two_trip.model.Trip;
import it.unimib.sal.one_two_trip.model.TripResponse;
import it.unimib.sal.one_two_trip.repository.ITripsRepository;
import it.unimib.sal.one_two_trip.ui.trip.TripViewModel;
import it.unimib.sal.one_two_trip.ui.trip.TripViewModelFactory;
import it.unimib.sal.one_two_trip.util.ErrorMessagesUtil;
import it.unimib.sal.one_two_trip.util.ServiceLocator;
import it.unimib.sal.one_two_trip.util.SharedPreferencesUtil;


public class ActivityParticipantEditFragment extends Fragment {
    private Application application;
    private TripViewModel viewModel;
    private SharedPreferencesUtil sharedPreferencesUtil;

    private Trip trip;
    private Activity activity;
    private List<Person> personList;
    private List<Person> notParticipating;

    private ParticipantRecyclerViewAdapter participantAdapter;
    private ParticipantRecyclerViewAdapter notParticipantAdapter;

    public ActivityParticipantEditFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = requireActivity().getApplication();

        sharedPreferencesUtil = new SharedPreferencesUtil(application);

        ITripsRepository tripsRepository = ServiceLocator.getInstance()
                .getTripsRepository(application);
        viewModel = new ViewModelProvider(requireActivity(),
                new TripViewModelFactory(tripsRepository)).get(TripViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activity_participant_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView participant_recycler = requireView().findViewById(R.id.activity_participant_recycler_edit);
        RecyclerView not_participant_recycler = requireView().findViewById(R.id.activity_not_participant_recycler_edit);
        MaterialButton confirm = requireView().findViewById(R.id.activity_participant_confirm);

        confirm.setOnClickListener(view1 -> {
            //TODO: implement the participant editing
            Navigation.findNavController(view).navigate(R.id.action_activityParticipantEditFragment_to_activityParticipantFragment);
        });


        String lastUpdate = "0";
        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
                    LAST_UPDATE);
        }


        viewModel.getTrip(Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(), result -> {
            if(result.isSuccess()) {
                trip = ((Result.Success<TripResponse>) result).getData().getTrip();
                activity = trip.getActivity().activityList.get(viewModel.getActivityPosition());

                personList = activity.getParticipant().personList;
                /* personList contains Activities???
                TODO: resolve this */
                List<Person> notParticipating = trip.getParticipant().personList;
                Log.d("BBB", "Trip " + trip.getParticipant().personList);
                Log.d("BBB", "List " + notParticipating);
                notParticipating.removeAll(personList);
                Log.d("BBB", "After " + notParticipating);

                //Participating
                participantAdapter = new ParticipantRecyclerViewAdapter(personList,
                        position -> {
                            //TODO: goto user page
                            Snackbar.make(requireView(), "User " + personList.get(position).getFullName(),
                                    Snackbar.LENGTH_SHORT).show();
                        });
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                participant_recycler.setLayoutManager(layoutManager);
                participant_recycler.setAdapter(participantAdapter);

                //Not Participating
                notParticipantAdapter = new ParticipantRecyclerViewAdapter(notParticipating,
                        position -> {
                            //TODO: goto user page
                            Snackbar.make(requireView(), "User " + notParticipating.get(position).getFullName(),
                                    Snackbar.LENGTH_SHORT).show();
                        });
                LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
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