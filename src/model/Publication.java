package model;

/*
 * Publication
 * Represents a magazine or newspaper title offered by the
 * publishing house. Stores publication details and pricing.
 */
public class Publication {

    private String publicationId;  // Unique ID, e.g., "PUB001"
    private String title;          // Name of the publication
    private String type;           // "Magazine" or "Newspaper"
    private String frequency;      // "Daily", "Weekly", "Monthly", "Fortnightly"
    private double monthlyPrice;   // Base price per month

    // Parameterized constructor
    public Publication(String publicationId, String title, String type,
                       String frequency, double monthlyPrice) {
        this.publicationId = publicationId;
        this.title = title;
        this.type = type;
        this.frequency = frequency;
        this.monthlyPrice = monthlyPrice;
    }

    // --- Getters ---

    public String getPublicationId() {
        return publicationId;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getFrequency() {
        return frequency;
    }

    public double getMonthlyPrice() {
        return monthlyPrice;
    }

    // --- Setters ---

    public void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setMonthlyPrice(double monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    @Override
    public String toString() {
        return publicationId + " - " + title + " (" + type + ")";
    }
}
