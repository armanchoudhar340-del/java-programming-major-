package util;

import model.Delivery;
import model.Subscription;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/*
 * InvoiceGenerator
 * Utility class that uses StringBuilder to generate formatted
 * text output such as subscription invoices, delivery labels,
 * and delivery report summaries.
 */
public class InvoiceGenerator {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Generates a formatted invoice for a subscription.
     * Uses StringBuilder to efficiently build a multi-line string.
     *
     * @param sub the subscription to generate an invoice for
     * @return formatted invoice string
     */
    public static String generateInvoice(Subscription sub) {
        // Using StringBuilder to build invoice text
        StringBuilder sb = new StringBuilder();

        sb.append("╔══════════════════════════════════════════════════╗\n");
        sb.append("║          SUBSCRIPTION INVOICE                   ║\n");
        sb.append("╠══════════════════════════════════════════════════╣\n");
        sb.append("║  Invoice Date : ").append(LocalDate.now().format(DATE_FMT));
        padRight(sb, 50);
        sb.append("║\n");
        sb.append("╠══════════════════════════════════════════════════╣\n");

        sb.append("║  Subscription ID : ").append(sub.getSubscriptionId());
        padRight(sb, 50);
        sb.append("║\n");

        sb.append("║  Subscriber      : ").append(sub.getSubscriber().getName());
        padRight(sb, 50);
        sb.append("║\n");

        sb.append("║  Subscriber ID   : ").append(sub.getSubscriber().getSubscriberId());
        padRight(sb, 50);
        sb.append("║\n");

        sb.append("║  Email           : ").append(sub.getSubscriber().getEmail());
        padRight(sb, 50);
        sb.append("║\n");

        sb.append("╠══════════════════════════════════════════════════╣\n");

        sb.append("║  Publication     : ").append(sub.getPublication().getTitle());
        padRight(sb, 50);
        sb.append("║\n");

        sb.append("║  Type            : ").append(sub.getPublication().getType());
        padRight(sb, 50);
        sb.append("║\n");

        sb.append("║  Plan            : ").append(sub.getPlanType().getDisplayName());
        padRight(sb, 50);
        sb.append("║\n");

        sb.append("║  Duration        : ").append(sub.getPlanType().getDurationMonths()).append(" month(s)");
        padRight(sb, 50);
        sb.append("║\n");

        sb.append("╠══════════════════════════════════════════════════╣\n");

        sb.append("║  Start Date      : ").append(sub.getStartDate().format(DATE_FMT));
        padRight(sb, 50);
        sb.append("║\n");

        sb.append("║  End Date        : ").append(sub.getEndDate().format(DATE_FMT));
        padRight(sb, 50);
        sb.append("║\n");

        sb.append("║  Monthly Price   : Rs. ").append(String.format("%.2f", sub.getPublication().getMonthlyPrice()));
        padRight(sb, 50);
        sb.append("║\n");

        sb.append("║  Discount        : ").append(String.format("%.0f%%",
                (1 - sub.getPlanType().getDiscountFactor()) * 100));
        padRight(sb, 50);
        sb.append("║\n");

        sb.append("║  TOTAL AMOUNT    : Rs. ").append(String.format("%.2f", sub.getAmount()));
        padRight(sb, 50);
        sb.append("║\n");

        sb.append("║  Status          : ").append(sub.isActive() ? "ACTIVE" : "CANCELLED");
        padRight(sb, 50);
        sb.append("║\n");

        sb.append("╠══════════════════════════════════════════════════╣\n");
        sb.append("║  Thank you for your subscription!               ║\n");
        sb.append("╚══════════════════════════════════════════════════╝\n");

        return sb.toString();
    }

    /**
     * Generates a delivery label for a delivery record.
     *
     * @param delivery the delivery to generate a label for
     * @return formatted delivery label string
     */
    public static String generateDeliveryLabel(Delivery delivery) {
        // Building delivery label
        StringBuilder sb = new StringBuilder();

        sb.append("┌──────────────────────────────────────────────────┐\n");
        sb.append("│          DELIVERY LABEL                         │\n");
        sb.append("├──────────────────────────────────────────────────┤\n");

        sb.append("│  Delivery ID    : ").append(delivery.getDeliveryId());
        padRight(sb, 50);
        sb.append("│\n");

        sb.append("│  Publication    : ").append(
                delivery.getSubscription().getPublication().getTitle());
        padRight(sb, 50);
        sb.append("│\n");

        sb.append("│  Subscriber     : ").append(
                delivery.getSubscription().getSubscriber().getName());
        padRight(sb, 50);
        sb.append("│\n");

        sb.append("│  Delivery Date  : ").append(
                delivery.getDeliveryDate().format(DATE_FMT));
        padRight(sb, 50);
        sb.append("│\n");

        sb.append("│  Status         : ").append(delivery.getStatus());
        padRight(sb, 50);
        sb.append("│\n");

        sb.append("├──────────────────────────────────────────────────┤\n");
        sb.append("│  DELIVER TO:                                    │\n");
        sb.append("│  ").append(delivery.getDeliveryAddress());
        padRight(sb, 50);
        sb.append("│\n");

        sb.append("└──────────────────────────────────────────────────┘\n");

        return sb.toString();
    }

    /**
     * Helper method to pad the current line to a fixed width.
     * Ensures the box-drawing characters align properly.
     */
    private static void padRight(StringBuilder sb, int totalWidth) {
        // Find the last newline to measure current line length
        int lastNewline = sb.lastIndexOf("\n");
        int currentLineLength = sb.length() - lastNewline - 1;
        while (currentLineLength < totalWidth) {
            sb.append(' ');
            currentLineLength++;
        }
    }
}
