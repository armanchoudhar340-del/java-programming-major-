package model;

import java.time.LocalDate;

/*
 * Delivery
 * Represents a single delivery event for a subscription.
 * Tracks the delivery date, current status, and address.
 * Delivery records are stored in a LinkedList (see service).
 */
public class Delivery {

    private String deliveryId;         // Unique ID, e.g., "DEL001"
    private Subscription subscription; // Which subscription this delivery belongs to
    private LocalDate deliveryDate;    // Scheduled or actual delivery date
    private String status;             // "Scheduled", "Delivered", "Failed", "Pending"
    private String deliveryAddress;    // Delivery address (may differ from subscriber address)

    // Parameterized constructor
    public Delivery(String deliveryId, Subscription subscription,
                    LocalDate deliveryDate, String status, String deliveryAddress) {
        this.deliveryId = deliveryId;
        this.subscription = subscription;
        this.deliveryDate = deliveryDate;
        this.status = status;
        this.deliveryAddress = deliveryAddress;
    }

    // --- Getters ---

    public String getDeliveryId() {
        return deliveryId;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public String getStatus() {
        return status;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    // --- Setters ---

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    @Override
    public String toString() {
        return deliveryId + " | " + subscription.getSubscriptionId()
                + " | " + deliveryDate + " | " + status;
    }
}
