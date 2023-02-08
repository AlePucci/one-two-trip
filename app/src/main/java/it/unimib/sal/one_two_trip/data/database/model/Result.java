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
        return this instanceof UserResponseSuccess || this instanceof TripSuccess || this instanceof PasswordResetSuccess;
    }

    /**
     * Class that represents a successful action during the interaction
     * with a Web Service or a local database. (TRIP)
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
     * Class that represents a successful action during the interaction
     * with a Web Service or a local database. (TRIP)
     */
    public static final class PasswordResetSuccess extends Result {
        private final boolean success;

        public PasswordResetSuccess(boolean success) {
            this.success = success;
        }

        public boolean getData() {
            return success;
        }
    }

    /**
     * Class that represents a successful action during the interaction
     * with a Web Service or a local database. (USER)
     */
    public static final class UserResponseSuccess extends Result {
        private final User user;

        public UserResponseSuccess(User user) {
            this.user = user;
        }

        public User getData() {
            return user;
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
