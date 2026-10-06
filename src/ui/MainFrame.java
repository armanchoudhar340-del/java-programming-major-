package ui;

import service.SubscriptionService;

import javax.swing.*;
import java.awt.*;

/*
 * Main application window.
 */
public class MainFrame extends JFrame {

    private SubscriptionService service;
    private JTabbedPane tabbedPane;

    // References to all panels (for refreshing on tab change)
    private SubscriberPanel subscriberPanel;
    private PlanSelectionPanel planSelectionPanel;
    private DeliveryPanel deliveryPanel;
    private RenewalPanel renewalPanel;
    private ExpiryAlertPanel expiryAlertPanel;
    private SearchPanel searchPanel;
    private DeliveryReportPanel deliveryReportPanel;

    // Initialize the main frame
    public MainFrame(SubscriptionService service) {
        this.service = service;
        initComponents();
    }

    private void initComponents() {
        // --- Frame Setup ---
        setTitle("Magazine & Newspaper Subscription Management System");
        setSize(1150, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Center on screen

        // Set a nice look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // --- Header Panel ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(33, 37, 41));
        headerPanel.setPreferredSize(new Dimension(0, 50));
        JLabel titleLabel = new JLabel(
                "   \uD83D\uDCF0  Magazine & Newspaper Subscription Management System");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerPanel.add(titleLabel);

        // --- Create All Panels ---
        subscriberPanel = new SubscriberPanel(service);
        planSelectionPanel = new PlanSelectionPanel(service);
        deliveryPanel = new DeliveryPanel(service);
        renewalPanel = new RenewalPanel(service);
        expiryAlertPanel = new ExpiryAlertPanel(service);
        searchPanel = new SearchPanel(service);
        deliveryReportPanel = new DeliveryReportPanel(service);

        // --- Tabbed Pane ---
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabbedPane.addTab("\uD83D\uDC64 Subscribers", subscriberPanel);
        tabbedPane.addTab("\uD83D\uDCCB Plan Selection", planSelectionPanel);
        tabbedPane.addTab("\uD83D\uDE9A Delivery", deliveryPanel);
        tabbedPane.addTab("\uD83D\uDD04 Renewals", renewalPanel);
        tabbedPane.addTab("\u26A0\uFE0F Expiry Alerts", expiryAlertPanel);
        tabbedPane.addTab("\uD83D\uDD0D Search", searchPanel);
        tabbedPane.addTab("\uD83D\uDCCA Delivery Reports", deliveryReportPanel);

        // Refresh data when switching tabs
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            switch (index) {
                case 0: subscriberPanel.refreshData(); break;
                case 1: planSelectionPanel.refreshData(); break;
                case 2: deliveryPanel.refreshData(); break;
                case 3: renewalPanel.refreshData(); break;
                case 4: expiryAlertPanel.refreshData(); break;
                case 5: searchPanel.refreshData(); break;
                case 6: deliveryReportPanel.refreshData(); break;
            }
        });

        // --- Layout ---
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
}
