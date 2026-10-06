package ui;

import model.Subscription;
import service.SubscriptionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/*
 * ExpiryAlertPanel
 * Module 5: Expiry Alerts
 * Highlights subscriptions expiring within 7, 15, or 30 days.
 * Uses color-coded rows: red (≤7), orange (≤15), yellow (≤30).
 */
public class ExpiryAlertPanel extends JPanel {

    private SubscriptionService service;

    private JTable alertTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> cboTimeframe;
    private JButton btnCheck;
    private JLabel lblCount;

    public ExpiryAlertPanel(SubscriptionService service) {
        this.service = service;
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top Controls ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.add(new JLabel("Show subscriptions expiring within:"));
        cboTimeframe = new JComboBox<>(new String[]{
                "7 days (Critical)", "15 days (Warning)", "30 days (All Near-Expiry)"});
        topPanel.add(cboTimeframe);

        btnCheck = util.UIUtils.createButton("Check Expiry", new Color(220, 53, 69), Color.WHITE);
        btnCheck.addActionListener(e -> refreshData());
        topPanel.add(btnCheck);

        lblCount = new JLabel("  Found: 0 subscriptions");
        lblCount.setFont(new Font("SansSerif", Font.BOLD, 13));
        topPanel.add(lblCount);

        // --- Alert Table ---
        String[] columns = {"Sub ID", "Subscriber", "Email", "Publication",
                "Plan", "End Date", "Days Left", "Urgency"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        alertTable = new JTable(tableModel);
        alertTable.setRowHeight(30);
        alertTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));

        // Custom cell renderer for color-coding
        alertTable.setDefaultRenderer(Object.class,
                new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String urgency = (String) table.getValueAt(row, 7);
                    switch (urgency) {
                        case "CRITICAL":
                            c.setBackground(new Color(255, 180, 180));
                            c.setForeground(new Color(139, 0, 0));
                            break;
                        case "WARNING":
                            c.setBackground(new Color(255, 220, 170));
                            c.setForeground(new Color(150, 80, 0));
                            break;
                        case "CAUTION":
                            c.setBackground(new Color(255, 255, 190));
                            c.setForeground(new Color(120, 120, 0));
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                    }
                }
                setFont(new Font("SansSerif", Font.BOLD, 12));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(alertTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                "\u26A0 Expiry Alerts"));

        // --- Legend ---
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        legendPanel.setBorder(BorderFactory.createTitledBorder("Legend"));

        JLabel lblCritical = new JLabel(" \u25A0 CRITICAL (≤ 7 days) ");
        lblCritical.setOpaque(true);
        lblCritical.setBackground(new Color(255, 180, 180));
        lblCritical.setForeground(new Color(139, 0, 0));
        lblCritical.setFont(new Font("SansSerif", Font.BOLD, 11));

        JLabel lblWarning = new JLabel(" \u25A0 WARNING (≤ 15 days) ");
        lblWarning.setOpaque(true);
        lblWarning.setBackground(new Color(255, 220, 170));
        lblWarning.setForeground(new Color(150, 80, 0));
        lblWarning.setFont(new Font("SansSerif", Font.BOLD, 11));

        JLabel lblCaution = new JLabel(" \u25A0 CAUTION (≤ 30 days) ");
        lblCaution.setOpaque(true);
        lblCaution.setBackground(new Color(255, 255, 190));
        lblCaution.setForeground(new Color(120, 120, 0));
        lblCaution.setFont(new Font("SansSerif", Font.BOLD, 11));

        legendPanel.add(lblCritical);
        legendPanel.add(lblWarning);
        legendPanel.add(lblCaution);

        // Layout
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(legendPanel, BorderLayout.SOUTH);
    }

    /**
     * Refresh the alert table based on selected timeframe.
     */
    public void refreshData() {
        tableModel.setRowCount(0);

        int days;
        int selectedIndex = cboTimeframe != null ? cboTimeframe.getSelectedIndex() : 2;
        switch (selectedIndex) {
            case 0: days = 7; break;
            case 1: days = 15; break;
            default: days = 30; break;
        }

        // Get expiring subscriptions from service
        List<Subscription> expiring = service.getExpiringSubscriptions(days);

        for (Subscription s : expiring) {
            long daysLeft = s.getDaysUntilExpiry();
            String urgency;
            if (daysLeft <= 7) {
                urgency = "CRITICAL";
            } else if (daysLeft <= 15) {
                urgency = "WARNING";
            } else {
                urgency = "CAUTION";
            }

            tableModel.addRow(new Object[]{
                    s.getSubscriptionId(),
                    s.getSubscriber().getName(),
                    s.getSubscriber().getEmail(),
                    s.getPublication().getTitle(),
                    s.getPlanType().getDisplayName(),
                    s.getEndDate().toString(),
                    String.valueOf(daysLeft),
                    urgency
            });
        }

        if (lblCount != null) {
            lblCount.setText("  Found: " + expiring.size() + " subscription(s)");
            if (expiring.size() > 0) {
                lblCount.setForeground(new Color(220, 53, 69));
            } else {
                lblCount.setForeground(new Color(40, 167, 69));
            }
        }
    }
}
