package it.unimib.sal.one_two_trip.model;

/**
 * Class that represents the result of an action that requires
 * the use of a Web Service or a local database.
 */
public abstract class Result {

    private Result() {
    }

    public boolean isSuccess() {
        return (this instanceof Success);
    }

    /**
     * Class that represents a successful action during the interaction
     * with a Web Service or a local database.
     */
    public static final class Success extends Result {
        private final TripsResponse tripsResponse;

        public Success(TripsResponse tripsResponse) {
            this.tripsResponse = tripsResponse;
        }

        public TripsResponse getData() {
            return tripsResponse;
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
