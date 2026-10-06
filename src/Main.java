import service.SubscriptionService;
import ui.MainFrame;

import javax.swing.*;

/*
 * Starts the application.
 */
public class Main {

    public static void main(String[] args) {
        // Launch GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel for native appearance
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            // Create the central service and load sample data
            SubscriptionService service = new SubscriptionService();
            service.loadSampleData();

            // Create and show the main application window
            MainFrame frame = new MainFrame(service);
            frame.setVisible(true);

            System.out.println("===========================================");
            System.out.println("  Subscription Management System Started");
            System.out.println("  Sample data loaded successfully.");
            System.out.println("===========================================");
        });
    }
}
