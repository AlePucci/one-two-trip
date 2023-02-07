package it.unimib.sal.one_two_trip.data.database.model;

import it.unimib.sal.one_two_trip.data.database.model.response.TripsResponse;

/**
 * Class that represents the result of an action that requires
 * the use of a Web Service or a local database.
 */
public abstract class Result {

    private Result() {
    }

    public boolean isSuccess() {
        return (this instanceof TripSuccess);
    }

    /**
     * Class that represents a successful action during the interaction
     * with a Web Service or a local database. (TRIPS)
     */
    public static final class TripSuccess extends Result {
        private final TripsResponse response;

        public TripSuccess(TripsResponse response) {
            this.response = response;
        }

        public TripsResponse getData() {
            return response;
        }
    }

    /**
     * Class that represents an error occurred during the interaction
     * with a Web Service or a local database.
     */
    public static final class Error extends Result {
        private final String message;

        public Error(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
