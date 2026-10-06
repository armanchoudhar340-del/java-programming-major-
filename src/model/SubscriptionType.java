package model;

/*
 * SubscriptionType
 * This enum defines the three subscription plan types offered
 * by the publishing house. Each constant stores its display name,
 * duration in months, and a discount factor applied to pricing.
 */
public enum SubscriptionType {

    MONTHLY("Monthly", 1, 1.0),        // Full price, 1 month
    QUARTERLY("Quarterly", 3, 0.95),   // 5% discount, 3 months
    ANNUAL("Annual", 12, 0.85);        // 15% discount, 12 months

    private final String displayName;
    private final int durationMonths;
    private final double discountFactor;

    // Enum constructor initializes each constant
    SubscriptionType(String displayName, int durationMonths, double discountFactor) {
        this.displayName = displayName;
        this.durationMonths = durationMonths;
        this.discountFactor = discountFactor;
    }

    // --- Getter Methods ---

    public String getDisplayName() {
        return displayName;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public double getDiscountFactor() {
        return discountFactor;
    }

    /**
     * Calculates total subscription cost.
     * Formula: monthlyPrice × durationMonths × discountFactor
     *
     * @param monthlyPrice the monthly base price of the publication
     * @return total cost for this plan type
     */
    public double calculateCost(double monthlyPrice) {
        return monthlyPrice * durationMonths * discountFactor;
    }

    @Override
    public String toString() {
        return displayName + " (" + durationMonths + " month"
                + (durationMonths > 1 ? "s" : "") + ")";
    }
}
