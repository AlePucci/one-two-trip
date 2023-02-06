package it.unimib.sal.one_two_trip.model;

/**
 * Class that represents the result of an action that requires
 * the use of a Web Service or a local database.
 */
public abstract class Result {
    private Result() {}

    public static final class Success extends Result{
        private final TripsResponse response;
        public Success(TripsResponse response){
            this.response = response;
        }

        public TripsResponse getData() {
            return response;
        }
    }
    public boolean isSuccess() {
        if (this instanceof NewsResponseSuccess || this instanceof UserResponseSuccess) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Class that represents a successful action during the interaction
     * with a Web Service or a local database.
     */
    public static final class NewsResponseSuccess extends Result {
        private final NewsResponse newsResponse;
        public NewsResponseSuccess(NewsResponse newsResponse) {
            this.newsResponse = newsResponse;
        }
        public NewsResponse getData() {
            return newsResponse;
        }
    }

    /**
     * Class that represents a successful action during the interaction
     * with a Web Service or a local database.
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
