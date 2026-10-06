package exception;

/*
 * InvalidSubscriptionException
 * Custom checked exception thrown when subscription data is
 * invalid (e.g., missing subscriber, invalid plan, past dates).
 */
public class InvalidSubscriptionException extends Exception {

    // Constructor with error message
    public InvalidSubscriptionException(String message) {
        super(message);
    }

    // Constructor with error message and root cause
    public InvalidSubscriptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
