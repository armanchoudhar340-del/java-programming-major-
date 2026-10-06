package service;

import model.*;
import exception.*;
import util.Validator;

import java.time.LocalDate;
import java.util.*;

/*
 * Handles the main subscription operations.
 * Demonstrates use of:
 *   : storing subscriber & subscription records
 *   : maintaining delivery history
 *   : mapping Subscriber ID -> Subscriptions
 *   : subscriptions sorted by renewal date
 *   : Create, Read, Update, Delete operations
 *   : search by name, ID, publication
 *   : sort by renewal date and plan type
 *   : validating data before operations
 */
public class SubscriptionService {

    // Store all subscriber records
    private ArrayList<Subscriber> subscribers = new ArrayList<>();

    // Store all publication records
    private ArrayList<Publication> publications = new ArrayList<>();

    // Store all subscription records
    private ArrayList<Subscription> allSubscriptions = new ArrayList<>();

    // Maintain delivery history (chronological order)
    private LinkedList<Delivery> deliveryHistory = new LinkedList<>();

    // Subscriber ID -> List of their subscriptions
    private HashMap<String, List<Subscription>> subscriberSubscriptionMap = new HashMap<>();

    // Subscriptions sorted by renewal/end date (auto-sorted keys)
    private TreeMap<LocalDate, List<Subscription>> renewalDateMap = new TreeMap<>();

    // Auto-increment counters for generating unique IDs
    private int subscriberCounter = 1;
    private int subscriptionCounter = 1;
    private int deliveryCounter = 1;

    // ===================== SUBSCRIBER CRUD =====================

    /**
     * Add a new subscriber.
     * Validates input using the Validator utility.
     */
    public void addSubscriber(Subscriber subscriber) throws InvalidSubscriptionException {
        // Validate before adding
        try {
            Validator.validateName(subscriber.getName());
            Validator.validateEmail(subscriber.getEmail());
            Validator.validatePhone(subscriber.getPhone());
            Validator.validateAddress(subscriber.getAddress());
        } catch (Exception e) {
            throw new InvalidSubscriptionException("Invalid subscriber data: " + e.getMessage());
        }

        // Check for duplicate ID
        for (Subscriber s : subscribers) {
            if (s.getSubscriberId().equals(subscriber.getSubscriberId())) {
                throw new InvalidSubscriptionException(
                        "Subscriber ID " + subscriber.getSubscriberId() + " already exists.");
            }
        }

        subscribers.add(subscriber);
        subscriberSubscriptionMap.put(subscriber.getSubscriberId(), new ArrayList<>());
    }

    /**
     * Get a subscriber by ID.
     */
    public Subscriber getSubscriber(String subscriberId) {
        for (Subscriber s : subscribers) {
            if (s.getSubscriberId().equals(subscriberId)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Get all subscribers.
     */
    public ArrayList<Subscriber> getAllSubscribers() {
        return subscribers;
    }

    /**
     * Update subscriber details.
     */
    public void updateSubscriber(Subscriber updated) throws InvalidSubscriptionException {
        try {
            Validator.validateName(updated.getName());
            Validator.validateEmail(updated.getEmail());
            Validator.validatePhone(updated.getPhone());
            Validator.validateAddress(updated.getAddress());
        } catch (Exception e) {
            throw new InvalidSubscriptionException("Invalid data: " + e.getMessage());
        }

        for (int i = 0; i < subscribers.size(); i++) {
            if (subscribers.get(i).getSubscriberId().equals(updated.getSubscriberId())) {
                subscribers.set(i, updated);
                return;
            }
        }
        throw new InvalidSubscriptionException("Subscriber not found: " + updated.getSubscriberId());
    }

    /**
     * Remove a subscriber by ID.
     */
    public void deleteSubscriber(String subscriberId) throws InvalidSubscriptionException {
        boolean removed = subscribers.removeIf(
                s -> s.getSubscriberId().equals(subscriberId));
        if (!removed) {
            throw new InvalidSubscriptionException("Subscriber not found: " + subscriberId);
        }
        // Also remove their subscription mappings
        subscriberSubscriptionMap.remove(subscriberId);
    }

    /**
     * Generate the next subscriber ID (e.g., SUB001, SUB002, ...).
     */
    public String generateSubscriberId() {
        String id;
        do {
            id = String.format("SUB%03d", subscriberCounter++);
        } while (getSubscriber(id) != null);
        return id;
    }

    // ===================== PUBLICATION MANAGEMENT =====================

    /**
     * Add a publication to the catalog.
     */
    public void addPublication(Publication pub) {
        publications.add(pub);
    }

    /**
     * Get all publications.
     */
    public ArrayList<Publication> getAllPublications() {
        return publications;
    }

    /**
     * Find a publication by its ID.
     */
    public Publication getPublication(String pubId) {
        for (Publication p : publications) {
            if (p.getPublicationId().equals(pubId)) {
                return p;
            }
        }
        return null;
    }

    // ===================== SUBSCRIPTION CRUD =====================

    /**
     * Create a new subscription.
     * Updates HashMap and TreeMap accordingly.
     */
    public void createSubscription(Subscription subscription)
            throws InvalidSubscriptionException {
        if (subscription.getSubscriber() == null) {
            throw new InvalidSubscriptionException("Subscriber cannot be null.");
        }
        if (subscription.getPublication() == null) {
            throw new InvalidSubscriptionException("Publication cannot be null.");
        }
        if (subscription.getStartDate() == null) {
            throw new InvalidSubscriptionException("Start date cannot be null.");
        }

        // Add to main list
        allSubscriptions.add(subscription);

        // Map subscriber ID to subscription
        String subId = subscription.getSubscriber().getSubscriberId();
        subscriberSubscriptionMap
                .computeIfAbsent(subId, k -> new ArrayList<>())
                .add(subscription);

        // Map renewal date to subscription
        renewalDateMap
                .computeIfAbsent(subscription.getEndDate(), k -> new ArrayList<>())
                .add(subscription);
    }

    /**
     * Get a subscription by its ID.
     */
    public Subscription getSubscription(String subscriptionId) {
        for (Subscription s : allSubscriptions) {
            if (s.getSubscriptionId().equals(subscriptionId)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Get all subscriptions.
     */
    public ArrayList<Subscription> getAllSubscriptions() {
        return allSubscriptions;
    }

    /**
     * Renew a subscription by extending its end date.
     * Creates a new period starting from the current end date.
     */
    public void renewSubscription(String subscriptionId)
            throws InvalidSubscriptionException {
        Subscription sub = getSubscription(subscriptionId);
        if (sub == null) {
            throw new InvalidSubscriptionException(
                    "Subscription not found: " + subscriptionId);
        }

        // Remove from old renewal date in TreeMap
        LocalDate oldEnd = sub.getEndDate();
        List<Subscription> oldList = renewalDateMap.get(oldEnd);
        if (oldList != null) {
            oldList.remove(sub);
            if (oldList.isEmpty()) {
                renewalDateMap.remove(oldEnd);
            }
        }

        // Extend: new start = old end, new end = old end + plan duration
        sub.setStartDate(oldEnd);
        LocalDate newEnd = oldEnd.plusMonths(sub.getPlanType().getDurationMonths());
        sub.setEndDate(newEnd);
        sub.setActive(true);

        // Add to new renewal date in TreeMap
        renewalDateMap
                .computeIfAbsent(newEnd, k -> new ArrayList<>())
                .add(sub);
    }

    /**
     * Cancel (deactivate) a subscription.
     */
    public void cancelSubscription(String subscriptionId)
            throws InvalidSubscriptionException {
        Subscription sub = getSubscription(subscriptionId);
        if (sub == null) {
            throw new InvalidSubscriptionException(
                    "Subscription not found: " + subscriptionId);
        }
        sub.setActive(false);
    }

    /**
     * Permanently delete a subscription and remove it from both lookup maps.
     */
    public void deleteSubscription(String subscriptionId)
            throws InvalidSubscriptionException {
        Subscription sub = getSubscription(subscriptionId);
        if (sub == null) {
            throw new InvalidSubscriptionException(
                    "Subscription not found: " + subscriptionId);
        }

        allSubscriptions.remove(sub);

        String subscriberId = sub.getSubscriber().getSubscriberId();
        List<Subscription> bySubscriber = subscriberSubscriptionMap.get(subscriberId);
        if (bySubscriber != null) {
            bySubscriber.remove(sub);
        }

        LocalDate endDate = sub.getEndDate();
        List<Subscription> byDate = renewalDateMap.get(endDate);
        if (byDate != null) {
            byDate.remove(sub);
            if (byDate.isEmpty()) {
                renewalDateMap.remove(endDate);
            }
        }
    }

    /**
     * Generate the next subscription ID.
     */
    public String generateSubscriptionId() {
        return String.format("SUBS%03d", subscriptionCounter++);
    }

    // ===================== DELIVERY MANAGEMENT =====================

    /**
     * Schedule a new delivery.
     * Adds to the delivery history linked list.
     */
    public void scheduleDelivery(Delivery delivery) throws InvalidDeliveryException {
        if (delivery.getSubscription() == null) {
            throw new InvalidDeliveryException("Subscription cannot be null.");
        }
        if (delivery.getDeliveryDate() == null) {
            throw new InvalidDeliveryException("Delivery date cannot be null.");
        }
        if (delivery.getDeliveryAddress() == null
                || delivery.getDeliveryAddress().trim().isEmpty()) {
            throw new InvalidDeliveryException("Delivery address cannot be empty.");
        }

        // Add delivery to history
        deliveryHistory.addLast(delivery);
    }

    /**
     * Update the status of a delivery.
     */
    public void updateDeliveryStatus(String deliveryId, String newStatus)
            throws InvalidDeliveryException {
        for (Delivery d : deliveryHistory) {
            if (d.getDeliveryId().equals(deliveryId)) {
                d.setStatus(newStatus);
                return;
            }
        }
        throw new InvalidDeliveryException("Delivery not found: " + deliveryId);
    }

    /**
     * Get entire delivery history.
     */
    public LinkedList<Delivery> getDeliveryHistory() {
        return deliveryHistory;
    }

    /**
     * Get deliveries for a specific subscription.
     */
    public List<Delivery> getDeliveriesForSubscription(String subscriptionId) {
        List<Delivery> result = new ArrayList<>();
        for (Delivery d : deliveryHistory) {
            if (d.getSubscription().getSubscriptionId().equals(subscriptionId)) {
                result.add(d);
            }
        }
        return result;
    }

    /**
     * Generate the next delivery ID.
     */
    public String generateDeliveryId() {
        return String.format("DEL%03d", deliveryCounter++);
    }

    // ===================== SEARCHING =====================

    /**
     * Search subscribers by name (case-insensitive partial match).
     */
    public List<Subscriber> searchSubscribersByName(String name) {
        List<Subscriber> results = new ArrayList<>();
        String searchTerm = name.toLowerCase();
        for (Subscriber s : subscribers) {
            if (s.getName().toLowerCase().contains(searchTerm)) {
                results.add(s);
            }
        }
        return results;
    }

    /**
     * Search subscribers by ID (exact match).
     */
    public Subscriber searchSubscriberById(String subscriberId) {
        for (Subscriber s : subscribers) {
            if (s.getSubscriberId().equalsIgnoreCase(subscriberId)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Search subscriptions by publication title.
     */
    public List<Subscription> searchByPublication(String publicationTitle) {
        List<Subscription> results = new ArrayList<>();
        String searchTerm = publicationTitle.toLowerCase();
        for (Subscription s : allSubscriptions) {
            if (s.getPublication().getTitle().toLowerCase().contains(searchTerm)) {
                results.add(s);
            }
        }
        return results;
    }

    /**
     * Search subscriptions by subscriber ID.
     * Uses the HashMap for efficient lookup.
     */
    public List<Subscription> searchSubscriptionsBySubscriberId(String subscriberId) {
        // Direct lookup from map
        return subscriberSubscriptionMap.getOrDefault(subscriberId, new ArrayList<>());
    }

    // ===================== SORTING =====================

    /**
     * Sort subscriptions by renewal/end date.
     * Uses the Comparable interface implemented by Subscription.
     */
    public List<Subscription> getSortedByRenewalDate() {
        List<Subscription> sorted = new ArrayList<>(allSubscriptions);
        Collections.sort(sorted);  // Uses Subscription.compareTo (by endDate)
        return sorted;
    }

    /**
     * Sort subscriptions by plan type using a Comparator.
     * Order: MONTHLY -> QUARTERLY -> ANNUAL
     */
    public List<Subscription> getSortedByPlanType() {
        List<Subscription> sorted = new ArrayList<>(allSubscriptions);
        // Custom comparator for plan type
        sorted.sort(new Comparator<Subscription>() {
            @Override
            public int compare(Subscription s1, Subscription s2) {
                return Integer.compare(
                        s1.getPlanType().getDurationMonths(),
                        s2.getPlanType().getDurationMonths());
            }
        });
        return sorted;
    }

    /**
     * Get subscriptions sorted by renewal date from TreeMap.
     * TreeMap naturally keeps keys sorted.
     */
    public TreeMap<LocalDate, List<Subscription>> getRenewalDateMap() {
        return renewalDateMap;
    }

    // ===================== EXPIRY ALERTS =====================

    /**
     * Get subscriptions expiring within the given number of days.
     * Used by the Expiry Alerts module.
     */
    public List<Subscription> getExpiringSubscriptions(int days) {
        List<Subscription> expiring = new ArrayList<>();
        for (Subscription sub : allSubscriptions) {
            if (sub.isActive() && sub.isExpiringSoon(days)) {
                expiring.add(sub);
            }
        }
        // Sort by days until expiry (most urgent first)
        expiring.sort(Comparator.comparingLong(Subscription::getDaysUntilExpiry));
        return expiring;
    }

    // ===================== SAMPLE DATA =====================

    /**
     * Loads sample/dummy data so the GUI shows data on first run.
     * Creates 5 subscribers, 5 publications, 7 subscriptions,
     * and several delivery records with varying statuses.
     */
    public void loadSampleData() {
        try {
            // --- Sample Subscribers ---
            Subscriber s1 = new Subscriber("SUB001", "Rahul Sharma",
                    "rahul@email.com", "9876543210", "45 MG Road, Connaught Place, Delhi");
            Subscriber s2 = new Subscriber("SUB002", "Priya Patel",
                    "priya@email.com", "9876543211", "12 Marine Drive, South Mumbai, Maharashtra");
            Subscriber s3 = new Subscriber("SUB003", "Amit Kumar",
                    "amit@email.com", "9876543212", "78 Brigade Road, Indiranagar, Bangalore");
            Subscriber s4 = new Subscriber("SUB004", "Sneha Gupta",
                    "sneha@email.com", "9876543213", "23 Anna Salai, T Nagar, Chennai");
            Subscriber s5 = new Subscriber("SUB005", "Vikram Singh",
                    "vikram@email.com", "9876543214", "56 FC Road, Deccan Area, Pune");

            addSubscriber(s1);
            addSubscriber(s2);
            addSubscriber(s3);
            addSubscriber(s4);
            addSubscriber(s5);

            subscriberCounter = 6; // Next ID will be SUB006

            // --- Sample Publications ---
            Publication p1 = new Publication("PUB001", "India Today",
                    "Magazine", "Monthly", 200.0);
            Publication p2 = new Publication("PUB002", "The Times of India",
                    "Newspaper", "Daily", 150.0);
            Publication p3 = new Publication("PUB003", "Outlook",
                    "Magazine", "Weekly", 120.0);
            Publication p4 = new Publication("PUB004", "The Hindu",
                    "Newspaper", "Daily", 130.0);
            Publication p5 = new Publication("PUB005", "Frontline",
                    "Magazine", "Fortnightly", 180.0);

            addPublication(p1);
            addPublication(p2);
            addPublication(p3);
            addPublication(p4);
            addPublication(p5);

            // --- Sample Subscriptions (with varying expiry dates) ---
            LocalDate now = LocalDate.now();

            // Expiring in 5 days (critical)
            Subscription sub1 = new Subscription("SUBS001", s1, p1,
                    SubscriptionType.MONTHLY,
                    now.minusMonths(1).plusDays(5), now.plusDays(5));

            // Expiring in 12 days (warning)
            Subscription sub2 = new Subscription("SUBS002", s2, p2,
                    SubscriptionType.QUARTERLY,
                    now.minusMonths(3).plusDays(12), now.plusDays(12));

            // Expiring in 25 days (caution)
            Subscription sub3 = new Subscription("SUBS003", s3, p3,
                    SubscriptionType.ANNUAL,
                    now.minusMonths(12).plusDays(25), now.plusDays(25));

            // Expiring in 60 days (safe)
            Subscription sub4 = new Subscription("SUBS004", s4, p4,
                    SubscriptionType.MONTHLY,
                    now.minusDays(10), now.plusDays(60));

            // Expiring in 3 days (critical)
            Subscription sub5 = new Subscription("SUBS005", s5, p5,
                    SubscriptionType.ANNUAL,
                    now.minusMonths(12).plusDays(3), now.plusDays(3));

            // Expiring in 45 days (safe)
            Subscription sub6 = new Subscription("SUBS006", s1, p2,
                    SubscriptionType.QUARTERLY,
                    now.minusMonths(2), now.plusDays(45));

            // Expiring in 8 days (critical)
            Subscription sub7 = new Subscription("SUBS007", s3, p1,
                    SubscriptionType.MONTHLY,
                    now.minusDays(22), now.plusDays(8));

            createSubscription(sub1);
            createSubscription(sub2);
            createSubscription(sub3);
            createSubscription(sub4);
            createSubscription(sub5);
            createSubscription(sub6);
            createSubscription(sub7);

            subscriptionCounter = 8; // Next ID will be SUBS008

            // --- Sample Deliveries ---
            Delivery d1 = new Delivery("DEL001", sub1,
                    now.minusDays(5), "Delivered", s1.getAddress());
            Delivery d2 = new Delivery("DEL002", sub1,
                    now.minusDays(2), "Delivered", s1.getAddress());
            Delivery d3 = new Delivery("DEL003", sub2,
                    now.minusDays(3), "Delivered", s2.getAddress());
            Delivery d4 = new Delivery("DEL004", sub2,
                    now, "Pending", s2.getAddress());
            Delivery d5 = new Delivery("DEL005", sub3,
                    now.minusDays(7), "Delivered", s3.getAddress());
            Delivery d6 = new Delivery("DEL006", sub4,
                    now.minusDays(1), "Delivered", s4.getAddress());
            Delivery d7 = new Delivery("DEL007", sub5,
                    now, "Scheduled", s5.getAddress());
            Delivery d8 = new Delivery("DEL008", sub3,
                    now.minusDays(14), "Failed", s3.getAddress());
            Delivery d9 = new Delivery("DEL009", sub6,
                    now.minusDays(4), "Delivered", s1.getAddress());
            Delivery d10 = new Delivery("DEL010", sub7,
                    now, "Scheduled", s3.getAddress());

            scheduleDelivery(d1);
            scheduleDelivery(d2);
            scheduleDelivery(d3);
            scheduleDelivery(d4);
            scheduleDelivery(d5);
            scheduleDelivery(d6);
            scheduleDelivery(d7);
            scheduleDelivery(d8);
            scheduleDelivery(d9);
            scheduleDelivery(d10);

            deliveryCounter = 11; // Next ID will be DEL011

        } catch (InvalidSubscriptionException | InvalidDeliveryException e) {
            System.err.println("Error loading sample data: " + e.getMessage());
        }
    }
}
