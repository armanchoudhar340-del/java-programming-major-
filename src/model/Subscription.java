package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/*
 * Subscription
 * Implements Comparable for natural
 *   ordering by renewal/end date.
 * Links a Subscriber to a Publication with a specific plan type,
 * tracking dates, cost, and active status.
 */
public class Subscription implements Comparable<Subscription> {

    private String subscriptionId;        // Unique ID, e.g., "SUBS001"
    private Subscriber subscriber;        // The subscriber
    private Publication publication;      // The publication subscribed to
    private SubscriptionType planType;    // Plan type
    private LocalDate startDate;          // When subscription started
    private LocalDate endDate;            // When subscription expires (= renewal date)
    private double amount;                // Total cost for this plan
    private boolean active;               // Whether the subscription is currently active

    // Parameterized constructor
    public Subscription(String subscriptionId, Subscriber subscriber,
                        Publication publication, SubscriptionType planType,
                        LocalDate startDate) {
        this.subscriptionId = subscriptionId;
        this.subscriber = subscriber;
        this.publication = publication;
        this.planType = planType;
        this.startDate = startDate;

        // Calculate end date based on plan duration
        this.endDate = startDate.plusMonths(planType.getDurationMonths());

        // Calculate cost using enum's calculateCost method
        this.amount = planType.calculateCost(publication.getMonthlyPrice());

        this.active = true;
    }

    // Overloaded constructor: also accepts pre-computed endDate (for sample data)
    public Subscription(String subscriptionId, Subscriber subscriber,
                        Publication publication, SubscriptionType planType,
                        LocalDate startDate, LocalDate endDate) {
        this.subscriptionId = subscriptionId;
        this.subscriber = subscriber;
        this.publication = publication;
        this.planType = planType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.amount = planType.calculateCost(publication.getMonthlyPrice());
        this.active = true;
    }

    // --- Business Logic Methods ---

    /**
     * Checks if the subscription is expiring within the given number of days.
     *
     * @param days number of days threshold
     * @return true if expiring within the given days
     */
    public boolean isExpiringSoon(int days) {
        if (!active) return false;
        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), endDate);
        return daysLeft >= 0 && daysLeft <= days;
    }

    /**
     * Returns the number of days until the subscription expires.
     * Negative value means already expired.
     */
    public long getDaysUntilExpiry() {
        return ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }

    /**
     * Checks if the subscription has already expired.
     */
    public boolean isExpired() {
        return endDate.isBefore(LocalDate.now());
    }

    // Natural ordering by end date
    @Override
    public int compareTo(Subscription other) {
        return this.endDate.compareTo(other.endDate);
    }

    // --- Getters ---

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public Publication getPublication() {
        return publication;
    }

    public SubscriptionType getPlanType() {
        return planType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isActive() {
        return active;
    }

    // --- Setters ---

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public void setPlanType(SubscriptionType planType) {
        this.planType = planType;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return subscriptionId + " | " + subscriber.getName()
                + " -> " + publication.getTitle()
                + " [" + planType.getDisplayName() + "]";
    }
}
