package it.unimib.sal.one_two_trip.data.source.trips;

import static it.unimib.sal.one_two_trip.util.Constants.ACTIVITY;
import static it.unimib.sal.one_two_trip.util.Constants.COMPLETED;
import static it.unimib.sal.one_two_trip.util.Constants.DELETED;
import static it.unimib.sal.one_two_trip.util.Constants.DESCRIPTION;
import static it.unimib.sal.one_two_trip.util.Constants.ENDDATE;
import static it.unimib.sal.one_two_trip.util.Constants.ENDLATITUDE;
import static it.unimib.sal.one_two_trip.util.Constants.ENDLOCATION;
import static it.unimib.sal.one_two_trip.util.Constants.ENDLONGITUDE;
import static it.unimib.sal.one_two_trip.util.Constants.EVERYONEPARTICIPATE;
import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_QUERY_PARTICIPANT;
import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_QUERY_REMOVED;
import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_TRIPS_COLLECTION;
import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_USER_COLLECTION;
import static it.unimib.sal.one_two_trip.util.Constants.ID;
import static it.unimib.sal.one_two_trip.util.Constants.LATITUDE;
import static it.unimib.sal.one_two_trip.util.Constants.LOCATION;
import static it.unimib.sal.one_two_trip.util.Constants.LONGITUDE;
import static it.unimib.sal.one_two_trip.util.Constants.PARTICIPANT;
import static it.unimib.sal.one_two_trip.util.Constants.REFERENCE;
import static it.unimib.sal.one_two_trip.util.Constants.REMOVED;
import static it.unimib.sal.one_two_trip.util.Constants.STARTDATE;
import static it.unimib.sal.one_two_trip.util.Constants.STATUS_OK;
import static it.unimib.sal.one_two_trip.util.Constants.TITLE;
import static it.unimib.sal.one_two_trip.util.Constants.TRIPID;
import static it.unimib.sal.one_two_trip.util.Constants.TRIPOWNER;
import static it.unimib.sal.one_two_trip.util.Constants.TYPE;
import static it.unimib.sal.one_two_trip.util.Constants.UNEXPECTED_ERROR;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import it.unimib.sal.one_two_trip.data.database.model.Activity;
import it.unimib.sal.one_two_trip.data.database.model.Person;
import it.unimib.sal.one_two_trip.data.database.model.Trip;
import it.unimib.sal.one_two_trip.data.database.model.response.TripsApiResponse;

/**
 * Class to get Trips from a remote source using Firebase Cloud Firestore
 */
public class TripsRemoteDataSource extends BaseTripsRemoteDataSource {

    private final CollectionReference tripsCollectionReference;
    private final CollectionReference usersCollectionReference;

    public TripsRemoteDataSource() {
        super();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.tripsCollectionReference = db.collection(FIREBASE_TRIPS_COLLECTION);
        this.usersCollectionReference = db.collection(FIREBASE_USER_COLLECTION);

        this.addTripListener();
    }

    /**
     * Add a listener to all the trips the users has joined in, using Firebase Cloud Firestore.
     */
    private void addTripListener() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String idToken = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final String tripsQuery = FIREBASE_QUERY_PARTICIPANT + idToken;

        com.google.firebase.firestore.EventListener<QuerySnapshot> tripListener = (value, error) -> {
            if (error == null && value != null) {
                List<Trip> tripList = new ArrayList<>();

                if (value.isEmpty()) {
                    tripCallback.onSuccessFromRemote(new TripsApiResponse(STATUS_OK,
                            tripList.size(), tripList), System.currentTimeMillis());
                } else {
                    for (QueryDocumentSnapshot tripSnapshot : value) {
                        String tripId = Objects.requireNonNull(tripSnapshot.get(ID, String.class));
                        CollectionReference activityCollection = this.tripsCollectionReference
                                .document(tripId).collection(ACTIVITY);

                        getTrips();
                        activityCollection.addSnapshotListener((value1, error1) -> getTrips());
                    }
                }
            }
        };
        this.tripsCollectionReference.whereNotEqualTo(tripsQuery, null).addSnapshotListener(tripListener);
    }

    /**
     * Get all trips from Firebase Cloud Firestore
     */
    @Override
    public void getTrips() {
        AtomicInteger status = new AtomicInteger(-1);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        final String idToken = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final String tripsQuery = FIREBASE_QUERY_PARTICIPANT + idToken;
        final String tripParticipantQuery = FIREBASE_QUERY_PARTICIPANT + idToken + FIREBASE_QUERY_REMOVED;

        OnCompleteListener<QuerySnapshot> tripsDownload = (task -> {
            if (!task.isSuccessful()) {
                tripCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
            } else {
                int size = task.getResult().size();
                ArrayList<Trip> tripList = new ArrayList<>();

                if (size == 0) {
                    tripCallback.onSuccessFromRemote(new TripsApiResponse(STATUS_OK,
                            tripList.size(), tripList), System.currentTimeMillis());
                    return;
                }

                for (QueryDocumentSnapshot tripSnapshot : task.getResult()) {
                    Trip trip = new Trip();

                    trip.setId(Objects.requireNonNull(tripSnapshot.get(ID, String.class)));
                    trip.setTripOwner(tripSnapshot.get(TRIPOWNER, String.class));
                    trip.setCompleted(Boolean.TRUE.equals(tripSnapshot.get(COMPLETED, Boolean.class)));
                    trip.setTitle(tripSnapshot.get(TITLE, String.class));
                    trip.setDescription(tripSnapshot.get(DESCRIPTION, String.class));
                    trip.setParticipating(!Boolean.TRUE.equals(tripSnapshot.get(tripParticipantQuery, Boolean.class)));
                    trip.setDeleted(Boolean.TRUE.equals(tripSnapshot.get(DELETED, Boolean.class)));

                    CollectionReference activityCollection = this.tripsCollectionReference
                            .document(trip.getId()).collection(ACTIVITY);

                    activityCollection.get().addOnCompleteListener(
                            task1 -> {
                                if (task1.isSuccessful()) {
                                    // GOT ALL OF THE ACTIVITIES
                                    Map<String, HashMap<String, Object>> map = new HashMap<>(((HashMap<String, HashMap<String, Object>>) Objects.requireNonNull(tripSnapshot.get(PARTICIPANT))));
                                    List<Task<DocumentSnapshot>> tripParticipantSnapshot = new ArrayList<>();

                                    for (String key : map.keySet()) {
                                        if (map.get(key) != null) {
                                            DocumentReference ref = (DocumentReference) Objects.requireNonNull(map.get(key)).get(REFERENCE);
                                            Task<DocumentSnapshot> newTask = Objects.requireNonNull(ref).get();
                                            tripParticipantSnapshot.add(newTask);
                                        }
                                    }

                                    List<DocumentSnapshot> activitySnapshotList = task1.getResult().getDocuments();

                                    Tasks.whenAllSuccess(tripParticipantSnapshot).addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            // GOT ALL OF THE TRIPS PARTICIPANTS
                                            List<Object> list = task2.getResult();
                                            for (Object obj : list) {
                                                DocumentSnapshot doc = (DocumentSnapshot) obj;
                                                Person person = doc.toObject(Person.class);
                                                trip.getParticipant().getPersonList().add(person);
                                            }

                                            for (DocumentSnapshot activitySnapshot : activitySnapshotList) {
                                                Activity activity = new Activity();
                                                activity.setId(Objects.requireNonNull(activitySnapshot.get(ID, String.class)));
                                                activity.setCompleted(Boolean.TRUE.equals(activitySnapshot.get(COMPLETED, Boolean.class)));
                                                activity.setTitle(activitySnapshot.get(TITLE, String.class));
                                                activity.setDescription(activitySnapshot.get(DESCRIPTION, String.class));
                                                activity.setEveryoneParticipate(Boolean.TRUE.equals(activitySnapshot.get(EVERYONEPARTICIPATE, Boolean.class)));
                                                activity.setLatitude(Objects.requireNonNull(activitySnapshot.get(LATITUDE, Double.class)));
                                                activity.setLongitude(Objects.requireNonNull(activitySnapshot.get(LONGITUDE, Double.class)));
                                                activity.setEndLatitude(Objects.requireNonNull(activitySnapshot.get(ENDLATITUDE, Double.class)));
                                                activity.setEndLongitude(Objects.requireNonNull(activitySnapshot.get(ENDLONGITUDE, Double.class)));
                                                activity.setStart_date(Objects.requireNonNull(activitySnapshot.get(STARTDATE, Long.class)));
                                                activity.setEnd_date(Objects.requireNonNull(activitySnapshot.get(ENDDATE, Long.class)));
                                                activity.setLocation(activitySnapshot.get(LOCATION, String.class));
                                                activity.setEnd_location(activitySnapshot.get(ENDLOCATION, String.class));
                                                activity.setTrip_id(activitySnapshot.get(TRIPID, String.class));
                                                activity.setType(activitySnapshot.get(TYPE, String.class));

                                                // ACTIVITY PARTICIPANTS
                                                List<Person> personList = new ArrayList<>();

                                                List<Object> participantList = (List<Object>) activitySnapshot.get(PARTICIPANT);

                                                if (participantList != null) {
                                                    for (Object object : participantList) {
                                                        DocumentReference person = (DocumentReference) object;
                                                        for (Person p : trip.getParticipant().getPersonList()) {
                                                            if (p != null && p.getId().equals(person.getId())) {
                                                                personList.add(p);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    activity.getParticipant().setPersonList(personList);
                                                }

                                                trip.getActivity().getActivityList().add(activity);
                                            }

                                            trip.getActivity().getActivityList().sort(Comparator.comparing(Activity::getStart_date));
                                            trip.getParticipant().getPersonList().sort(Comparator.comparing(Person::getName));
                                            trip.checkStartDate();
                                            tripList.add(trip);

                                            if (tripList.size() >= status.get()) {
                                                status.set(tripList.size());
                                                tripCallback.onSuccessFromRemote(new TripsApiResponse(STATUS_OK,
                                                        tripList.size(), tripList), System.currentTimeMillis());
                                            }
                                        } else {
                                            tripCallback.onFailureFromRemote(task2.getException());
                                        }
                                    });
                                } else {
                                    tripCallback.onFailureFromRemote(task1.getException());
                                }
                            });
                }
            }
        });
        this.tripsCollectionReference.whereNotEqualTo(tripsQuery, null).get().addOnCompleteListener(tripsDownload);
    }

    /**
     * Update a trip in Firebase Cloud Firestore.
     *
     * @param trip   map of the trip to update. The key is the field to update and the value is the new value.
     * @param tripId id of the trip to update
     */
    @Override
    public void updateTrip(@NonNull HashMap<String, Object> trip, @NonNull String tripId) {
        this.tripsCollectionReference.document(tripId).update(trip);
    }

    /**
     * Update an activity in Firebase Cloud Firestore.
     *
     * @param activity   map of the activity to update. The key is the field to update and the value
     *                   is the new value.
     * @param tripId     id of the trip in which the activity is contained
     * @param activityId id of the activity to update
     */
    @Override
    public void updateActivity(HashMap<String, Object> activity, String tripId, String
            activityId) {
        this.tripsCollectionReference.document(tripId).collection(ACTIVITY).document(activityId).update(activity);
    }

    /**
     * Insert an activity in a trip in Firebase Cloud Firestore.
     *
     * @param activity the activity to insert
     * @param trip     the trip in which the activity will be inserted
     */
    @Override
    public void insertActivity(@NonNull Activity activity, Trip trip) {
        HashMap<String, Object> activityMap = new HashMap<>();
        String activityId = activity.getId();
        activityMap.put(ID, activityId);
        activityMap.put(TITLE, activity.getTitle());
        activityMap.put(DESCRIPTION, activity.getDescription());
        activityMap.put(STARTDATE, activity.getStart_date());
        activityMap.put(ENDDATE, activity.getEnd_date());
        activityMap.put(LOCATION, activity.getLocation());
        activityMap.put(ENDLOCATION, activity.getEnd_location());
        activityMap.put(LATITUDE, activity.getLatitude());
        activityMap.put(ENDLATITUDE, activity.getEndLatitude());
        activityMap.put(LONGITUDE, activity.getLongitude());
        activityMap.put(ENDLONGITUDE, activity.getEndLongitude());
        activityMap.put(TYPE, activity.getType());
        activityMap.put(TRIPID, activity.getTrip_id());
        activityMap.put(EVERYONEPARTICIPATE, activity.isEveryoneParticipate());
        activityMap.put(COMPLETED, activity.isCompleted());

        List<DocumentReference> drs = new ArrayList<>();
        for (Person p : activity.getParticipant().getPersonList()) {
            drs.add(this.usersCollectionReference.document(p.getId()));
        }
        activityMap.put(PARTICIPANT, drs);

        this.tripsCollectionReference.document(trip.getId()).collection(ACTIVITY).document(activityId).set(activityMap);
    }

    /**
     * Insert a trip in Firebase Cloud Firestore.
     *
     * @param trip the trip to insert
     */
    @Override
    public void insertTrip(@NonNull Trip trip) {
        HashMap<String, Object> tripMap = new HashMap<>();
        String tripId = trip.getId();
        tripMap.put(ID, tripId);
        tripMap.put(TITLE, trip.getTitle());
        tripMap.put(DESCRIPTION, trip.getDescription());
        tripMap.put(STARTDATE, trip.getStart_date());
        tripMap.put(TRIPOWNER, trip.getTripOwner());
        tripMap.put(COMPLETED, trip.isCompleted());
        tripMap.put(DELETED, false);

        Map<String, Map<String, Object>> participantMap = new HashMap<>();
        for (Person p : trip.getParticipant().getPersonList()) {
            if (p != null) {
                String pId = p.getId();
                Map<String, Object> participant = new HashMap<>();
                participant.put(REFERENCE, this.usersCollectionReference.document(pId));
                participant.put(REMOVED, false);
                participantMap.put(pId, participant);
            }
        }
        tripMap.put(PARTICIPANT, participantMap);

        this.tripsCollectionReference.document(tripId).set(tripMap);
    }

    /**
     * Delete a trip in Firebase Cloud Firestore.
     *
     * @param trip trip to delete
     */
    @Override
    public void deleteTrip(@NonNull Trip trip) {
        this.tripsCollectionReference.document(trip.getId()).update(DELETED, true);
    }

    /**
     * Delete an activity of a trip in Firebase Cloud Firestore.
     *
     * @param activity activity to delete
     * @param trip     trip of the activity
     */
    @Override
    public void deleteActivity(@NonNull Activity activity, @NonNull Trip trip) {
        this.tripsCollectionReference.document(trip.getId()).collection(ACTIVITY).document(activity.getId()).delete();
    }
}
