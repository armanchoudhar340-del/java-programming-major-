package model;

/*
 * Subscriber
 * Represents a subscriber who can subscribe to publications.
 * Each subscriber has a unique ID, name, contact details,
 * and delivery address.
 */
public class Subscriber {

    private String subscriberId;  // Unique ID, e.g., "SUB001"
    private String name;
    private String email;
    private String phone;
    private String address;

    // Parameterized constructor to initialize subscriber
    public Subscriber(String subscriberId, String name, String email,
                      String phone, String address) {
        this.subscriberId = subscriberId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    // --- Getters ---

    public String getSubscriberId() {
        return subscriberId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    // --- Setters ---

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return subscriberId + " - " + name;
    }
}
