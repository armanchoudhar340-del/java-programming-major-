package util;

import java.util.regex.Pattern;
import exception.ValidationException;

/*
 * Validator
 * Utility class providing static methods to validate user input.
 * Uses regex patterns to enforce correct formats for email,
 * phone numbers, IDs, and other fields.
 */
public class Validator {

    // Regex patterns for validation
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[6-9]\\d{9}$");  // Indian 10-digit mobile number

    private static final Pattern SUBSCRIBER_ID_PATTERN =
            Pattern.compile("^SUB\\d{3,}$");  // Format: SUB followed by 3+ digits

    private static final Pattern PUBLICATION_ID_PATTERN =
            Pattern.compile("^PUB\\d{3,}$");  // Format: PUB followed by 3+ digits

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[A-Za-z][A-Za-z .'-]{1,49}$");  // Letters, spaces, dots

    // Validate subscriber name
    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Name", "Name cannot be empty.");
        }
        if (!NAME_PATTERN.matcher(name.trim()).matches()) {
            throw new ValidationException("Name",
                    "Name must contain only letters, spaces, dots, or hyphens (2-50 chars).");
        }
    }

    // Validate email using regex
    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email", "Email cannot be empty.");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Email",
                    "Invalid email format. Example: user@example.com");
        }
    }

    // Validate phone number using regex
    public static void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException("Phone", "Phone number cannot be empty.");
        }
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new ValidationException("Phone",
                    "Phone must be a 10-digit Indian mobile number starting with 6-9.");
        }
    }

    // Validate address
    public static void validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new ValidationException("Address", "Address cannot be empty.");
        }
        if (address.trim().length() < 10) {
            throw new ValidationException("Address",
                    "Address must be at least 10 characters long.");
        }
    }

    // Validate subscriber ID format
    public static void validateSubscriberId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Subscriber ID",
                    "Subscriber ID cannot be empty.");
        }
        if (!SUBSCRIBER_ID_PATTERN.matcher(id.trim()).matches()) {
            throw new ValidationException("Subscriber ID",
                    "Subscriber ID must be in format SUBnnn (e.g., SUB001).");
        }
    }

    // Validate publication ID format
    public static void validatePublicationId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("Publication ID",
                    "Publication ID cannot be empty.");
        }
        if (!PUBLICATION_ID_PATTERN.matcher(id.trim()).matches()) {
            throw new ValidationException("Publication ID",
                    "Publication ID must be in format PUBnnn (e.g., PUB001).");
        }
    }

    // Validate payment amount
    public static void validatePaymentAmount(double amount) {
        if (amount <= 0) {
            throw new ValidationException("Amount",
                    "Payment amount must be greater than zero.");
        }
        if (amount > 100000) {
            throw new ValidationException("Amount",
                    "Payment amount seems too high. Maximum allowed: Rs.1,00,000.");
        }
    }

    // Validate a date string (YYYY-MM-DD format)
    public static void validateDateString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new ValidationException("Date", "Date cannot be empty.");
        }
        if (!dateStr.trim().matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new ValidationException("Date",
                    "Date must be in YYYY-MM-DD format.");
        }
    }
}
