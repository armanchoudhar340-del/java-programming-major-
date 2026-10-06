package exception;

/*
 * ValidationException
 * Custom unchecked exception thrown when user input fails
 * validation (e.g., invalid email format, empty name, bad phone).
 * Extends RuntimeException so it doesn't need explicit throws.
 */
public class ValidationException extends RuntimeException {

    private final String fieldName;  // Which field failed validation

    // Constructor with field name and message
    public ValidationException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    /**
     * Returns the name of the field that failed validation.
     */
    public String getFieldName() {
        return fieldName;
    }
}
