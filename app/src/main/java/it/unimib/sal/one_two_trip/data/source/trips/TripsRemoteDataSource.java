package it.unimib.sal.one_two_trip.data.source.trips;

import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_TRIPS_COLLECTION;
import static it.unimib.sal.one_two_trip.util.Constants.FIREBASE_USER_COLLECTION;
import static it.unimib.sal.one_two_trip.util.Constants.STATUS_OK;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.security.crypto.EncryptedSharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import it.unimib.sal.one_two_trip.data.database.model.Activity;
import it.unimib.sal.one_two_trip.data.database.model.Person;
import it.unimib.sal.one_two_trip.data.database.model.Trip;
import it.unimib.sal.one_two_trip.data.database.model.response.TripsApiResponse;
import it.unimib.sal.one_two_trip.util.DataEncryptionUtil;

/**
 * Class to get Trips from a remote source using Firebase Realtime Database.
 */
public class TripsRemoteDataSource extends BaseTripsRemoteDataSource {

    private final CollectionReference tripsCollectionReference;
    private final String idToken;

    public TripsRemoteDataSource(String idToken) {
        super();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.tripsCollectionReference = db.collection(FIREBASE_TRIPS_COLLECTION);

        this.idToken = idToken;


        this.addTripListener();
    }

    /**
     * Add a listener to all the trips of the user in Firebase Realtime Database.
     */
    private void addTripListener() {
        final CompletableFuture<Boolean>[] myFuture = new CompletableFuture[]{new CompletableFuture<>()};

        com.google.firebase.firestore.EventListener<QuerySnapshot> tripListener = (value, error) -> {
            if (error == null && value != null) {
                List<Trip> tripList = new ArrayList<>();

                if (value.isEmpty()) {
                    tripCallback.onSuccessFromRemote(new TripsApiResponse(STATUS_OK,
                            tripList.size(), tripList), System.currentTimeMillis());
                    return;
                }

                for (QueryDocumentSnapshot ds : value) {
                    Trip trip = new Trip();
                    trip.setId(ds.get("id").toString());
                    trip.setTripOwner(ds.get("tripOwner").toString());
                    trip.setCompleted((boolean) ds.get("completed"));
                    trip.setTitle(ds.get("title").toString());
                    Log.d("AAAAAAAAAAAAAAAAAAAAAAA", trip.getTitle());
                    trip.setDescription(ds.get("description").toString());
                    Log.d("TripsRemoteDataSource", "removed " + (boolean) ds.get("participant.2.removed") + "");
                    trip.setParticipating(!(boolean) ds.get("participant.2.removed"));
                    CollectionReference a = FirebaseFirestore.getInstance().collection(FIREBASE_TRIPS_COLLECTION).document(trip.getId()).collection("activity");
                    a.addSnapshotListener((value1, error1) -> {
                        if (error1 == null) {
                            getTrips();
                        }
                    });
                    a.get().addOnCompleteListener(
                            task -> {
                                if (task.isSuccessful()) {
                                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                                    if (myFuture[0] != null) {
                                        myFuture[0].cancel(true);
                                        myFuture[0] = null;
                                    }
                                    myFuture[0] = queryDatabaseAsync(trip, ds);

                                    myFuture[0].thenAccept(isSuccess -> {
                                        for (DocumentSnapshot doc : docs) {
                                            Activity activity = new Activity();
                                            activity.setId(doc.get("id").toString());
                                            activity.setCompleted((boolean) doc.get("completed"));
                                            activity.setTitle(doc.get("title").toString());
                                            activity.setDescription(doc.get("description").toString());
                                            activity.setEveryoneParticipate((boolean) doc.get("everyoneParticipate"));
                                            activity.setLatitude(Double.parseDouble(doc.get("latitude").toString()));
                                            activity.setEndLatitude(Double.parseDouble(doc.get("endLatitude").toString()));
                                            activity.setLongitude(Double.parseDouble(doc.get("longitude").toString()));
                                            activity.setEndLongitude(Double.parseDouble(doc.get("endLongitude").toString()));
                                            activity.setStart_date((long) doc.get("start_date"));
                                            activity.setEnd_date((long) doc.get("end_date"));
                                            activity.setLocation(doc.get("location").toString());
                                            activity.setEnd_location(doc.get("endLocation").toString());
                                            activity.setTrip_id(doc.get("trip_id").toString());
                                            activity.setType(doc.get("type").toString());


                                            // PARTECIPANTI ATTIVITA'
                                            List<Person> personList = new ArrayList<>();
                                            for (DocumentReference person : (List<DocumentReference>) doc.get("participant")) {
                                                for (Person p : trip.getParticipant().getPersonList()) {
                                                    Log.d("TripsRemoteDataSource", "id: " + person.getId());
                                                    Log.d("TripsRemoteDataSource", p.getId());
                                                    Log.d("TripsRemoteDataSource", person.getId());
                                                    if (p.getId().equals(person.getId())) {
                                                        personList.add(p);
                                                    }
                                                }
                                            }

                                            activity.getParticipant().setPersonList(personList);
                                            Log.d("TripsRemoteDataSource", "activity: " + activity.getParticipant().getPersonList());

                                            trip.getActivity().getActivityList().add(activity);
                                            Log.d("TripsRemoteDataSource", "activity: " + activity);
                                        }
                                        tripList.add(trip);
                                        tripCallback.onSuccessFromRemote(new TripsApiResponse(STATUS_OK,
                                                tripList.size(), tripList), System.currentTimeMillis());

                                    });
                                } else {
                                    tripCallback.onFailureFromRemote(task.getException());
                                }
                            }
                    );
                }
            }
        };
        this.tripsCollectionReference.whereNotEqualTo("participant.2", null).addSnapshotListener(tripListener);
    }

    /**
     * Get all trips from Firebase Realtime Database.
     */
    @Override
    public void getTrips() {
        final CompletableFuture<Boolean>[] myFuture = new CompletableFuture[]{new CompletableFuture<>()};

        this.tripsCollectionReference.whereNotEqualTo("participant.2", null).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                tripCallback.onFailureFromRemote(task.getException());
            } else {
                List<Trip> tripList = new ArrayList<>();
                if (task.getResult().isEmpty()) {
                    tripCallback.onSuccessFromRemote(new TripsApiResponse(STATUS_OK,
                            tripList.size(), tripList), System.currentTimeMillis());
                    return;
                }
                for (QueryDocumentSnapshot ds : task.getResult()) {
                    Trip trip = new Trip();
                    trip.setId(Objects.requireNonNull(ds.get("id")).toString());
                    trip.setTripOwner(Objects.requireNonNull(ds.get("tripOwner")).toString());
                    trip.setCompleted((boolean) ds.get("completed"));
                    trip.setTitle(Objects.requireNonNull(ds.get("title")).toString());
                    trip.setDescription(Objects.requireNonNull(ds.get("description")).toString());
                    Log.d("TripsRemoteDataSource", "removed " + (boolean) ds.get("participant.2.removed") + "");
                    trip.setParticipating(!(boolean) ds.get("participant.2.removed"));
                    CollectionReference a = FirebaseFirestore.getInstance().collection(FIREBASE_TRIPS_COLLECTION).document(trip.getId()).collection("activity");
                    a.get().addOnCompleteListener(
                            task1 -> {
                                if (task1.isSuccessful()) {
                                    List<DocumentSnapshot> docs = task1.getResult().getDocuments();


                                    if (myFuture[0] != null) {
                                        myFuture[0].cancel(true);
                                        myFuture[0] = null;
                                    }
                                    myFuture[0] = queryDatabaseAsync(trip, ds);

                                    if (myFuture[0] != null) {
                                        myFuture[0].thenAccept(isSuccess -> {

                                            for (DocumentSnapshot doc : docs) {
                                                Activity activity = new Activity();
                                                activity.setId(doc.get("id").toString());
                                                activity.setCompleted((boolean) doc.get("completed"));
                                                activity.setTitle(doc.get("title").toString());
                                                activity.setDescription(doc.get("description").toString());
                                                activity.setEveryoneParticipate((boolean) doc.get("everyoneParticipate"));
                                                activity.setLatitude(Double.parseDouble(doc.get("latitude").toString()));
                                                activity.setEndLatitude(Double.parseDouble(doc.get("endLatitude").toString()));
                                                activity.setLongitude(Double.parseDouble(doc.get("longitude").toString()));
                                                activity.setEndLongitude(Double.parseDouble(doc.get("endLongitude").toString()));
                                                activity.setStart_date((long) doc.get("start_date"));
                                                activity.setEnd_date((long) doc.get("end_date"));
                                                activity.setLocation(doc.get("location").toString());
                                                activity.setEnd_location(doc.get("endLocation").toString());
                                                activity.setTrip_id(doc.get("trip_id").toString());
                                                activity.setType(doc.get("type").toString());

                                                // PARTECIPANTI ATTIVITA'
                                                List<Person> personList = new ArrayList<>();
                                                for (DocumentReference person : (List<DocumentReference>) doc.get("participant")) {
                                                    for (Person p : trip.getParticipant().getPersonList()) {
                                                        Log.d("TripsRemoteDataSource", "id: " + person.getId());
                                                        Log.d("TripsRemoteDataSource", p.getId());
                                                        Log.d("TripsRemoteDataSource", person.getId());
                                                        if (p.getId().equals(person.getId())) {
                                                            personList.add(p);
                                                        }
                                                    }
                                                }

                                                activity.getParticipant().setPersonList(personList);
                                                Log.d("TripsRemoteDataSource", "activity: " + activity.getParticipant().getPersonList());

                                                trip.getActivity().getActivityList().add(activity);
                                                Log.d("TripsRemoteDataSource", "activity: " + activity);
                                            }

                                            tripList.add(trip);
                                            tripCallback.onSuccessFromRemote(new TripsApiResponse(STATUS_OK,
                                                    tripList.size(), tripList), System.currentTimeMillis());
                                        });
                                    }
                                } else {
                                    tripCallback.onFailureFromRemote(task1.getException());
                                }
                            });
                }
            }
        });
    }

    private CompletableFuture<Boolean> queryDatabaseAsync(Trip trip, DocumentSnapshot ds) {

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        // if at least one fails then flag will switch
        AtomicBoolean isSuccess = new AtomicBoolean(true);

        if (ds.get("participant") == null) return null;

        HashMap<String, HashMap<String, Object>> map = new HashMap<>(((HashMap<String, HashMap<String, Object>>) ds.get("participant")));
        int n = map.size();
        AtomicInteger stack = new AtomicInteger(n);

        for (String key : map.keySet()) {
            DocumentReference ref = (DocumentReference) map.get(key).get("reference");
            ref.get().addOnCompleteListener(task1 -> {
                if (!task1.isSuccessful()) {
                    tripCallback.onFailureFromRemote(task1.getException());
                } else {
                    DocumentSnapshot ds1 = task1.getResult();
                    Person p = ds1.toObject(Person.class);

                    if (p != null) {
                        trip.getParticipant().getPersonList().add(p);
                    }
                    Log.d("Trip", trip.getParticipant().getPersonList().toString());
                }

                // finish if last
                if (stack.decrementAndGet() < 1) {
                    future.complete(isSuccess.get());
                }
            });
        }


        return future;
    }

    /**
     * Update a trip in Firebase Realtime Database.
     */
    @Override
    public void updateTrip(@NonNull HashMap<String, Object> trip, @NonNull String tripId) {
        this.tripsCollectionReference.document(tripId).update(trip);
    }

    @Override
    public void updateActivity(HashMap<String, Object> activity, String tripId, String
            activityId) {
        this.tripsCollectionReference.document(tripId).collection("activity").document(activityId).update(activity);
    }

    @Override
    public void insertActivity(Activity activity, Trip trip) {
        HashMap<String, Object> activityMap = new HashMap<>();
        activityMap.put("id", activity.getId());
        activityMap.put("title", activity.getTitle());
        activityMap.put("description", activity.getDescription());
        activityMap.put("start_date", activity.getStart_date());
        activityMap.put("end_date", activity.getEnd_date());
        activityMap.put("location", activity.getLocation());
        activityMap.put("endLocation", activity.getEnd_location());
        activityMap.put("latitude", activity.getLatitude());
        activityMap.put("endLatitude", activity.getEndLatitude());
        activityMap.put("longitude", activity.getLongitude());
        activityMap.put("endLongitude", activity.getEndLongitude());
        activityMap.put("type", activity.getType());
        activityMap.put("trip_id", activity.getTrip_id());
        activityMap.put("everyoneParticipate", activity.isEveryoneParticipate());
        activityMap.put("completed", activity.isCompleted());

        ArrayList<DocumentReference> drs = new ArrayList<>();
        for (Person p : activity.getParticipant().getPersonList()) {
            drs.add(FirebaseFirestore.getInstance().collection(FIREBASE_USER_COLLECTION).document(p.getId()));
        }
        activityMap.put("participant", drs);

        this.tripsCollectionReference.document(trip.getId()).collection("activity").document(activity.getId()).set(activityMap);
    }

    /**
     * Insert a trip in Firebase Realtime Database.
     */
    @Override
    public void insertTrip(@NonNull Trip trip) {
        HashMap<String, Object> tripMap = new HashMap<>();
        tripMap.put("id", trip.getId());
        tripMap.put("title", trip.getTitle());
        tripMap.put("description", trip.getDescription());
        tripMap.put("start_date", trip.getStart_date());
        tripMap.put("tripOwner", trip.getTripOwner());
        tripMap.put("completed", trip.isCompleted());

        HashMap<String, HashMap<String, Object>> participantMap = new HashMap<>();
        for (Person p : trip.getParticipant().getPersonList()) {
            HashMap<String, Object> participant = new HashMap<>();
            participant.put("reference", FirebaseFirestore.getInstance().collection(FIREBASE_USER_COLLECTION).document(p.getId()));
            participant.put("removed", false);
            participantMap.put(p.getId(), participant);
        }
        tripMap.put("participant", participantMap);

        this.tripsCollectionReference.document(trip.getId()).set(tripMap);
    }

    /**
     * Delete a trip in Firebase Realtime Database.
     */
    @Override
    public void deleteTrip(@NonNull Trip trip) {
        this.tripsCollectionReference.document(trip.getId()).delete();

        for (Activity activity : trip.getActivity().getActivityList()) {
            this.tripsCollectionReference.document(trip.getId()).collection("activity").document(activity.getId()).delete();
        }
    }

    @Override
    public void deleteActivity(Activity activity, Trip trip) {
        this.tripsCollectionReference.document(trip.getId()).collection("activity").document(activity.getId()).delete();
    }
}
