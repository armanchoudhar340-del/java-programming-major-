package exception;

/*
 * InvalidDeliveryException
 * Custom checked exception thrown when delivery data is invalid
 * (e.g., delivery date before subscription start, missing address).
 */
public class InvalidDeliveryException extends Exception {

    // Constructor with error message
    public InvalidDeliveryException(String message) {
        super(message);
    }

    // Constructor with error message and root cause
    public InvalidDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
