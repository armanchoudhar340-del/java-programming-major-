package ui;

import model.Subscription;
import service.SubscriptionService;
import exception.InvalidSubscriptionException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/*
 * RenewalPanel
 * Module 4: Renewal Tracking
 * Displays subscriptions sorted by renewal date using the
 * TreeMap data structure. Allows one-click renewal.
 */
public class RenewalPanel extends JPanel {

    private SubscriptionService service;

    private JTable renewalTable;
    private DefaultTableModel tableModel;
    private JButton btnRenew, btnRefresh;
    private JComboBox<String> cboSortBy;

    public RenewalPanel(SubscriptionService service) {
        this.service = service;
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top Controls ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.add(new JLabel("Sort By:"));
        cboSortBy = new JComboBox<>(new String[]{
                "Renewal Date (Nearest First)",
                "Plan Type (Monthly → Annual)"});
        topPanel.add(cboSortBy);

        btnRefresh = util.UIUtils.createSecondaryButton("Refresh");
        btnRefresh.addActionListener(e -> refreshData());
        topPanel.add(btnRefresh);

        btnRenew = util.UIUtils.createButton("Renew Selected", new Color(40, 167, 69), Color.WHITE);
        btnRenew.addActionListener(e -> renewSubscription());
        topPanel.add(btnRenew);

        // --- Table ---
        String[] columns = {"Sub ID", "Subscriber", "Publication",
                "Plan", "Start Date", "End Date", "Days Left", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        renewalTable = new JTable(tableModel);
        renewalTable.setRowHeight(28);
        renewalTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));

        // Color-code rows by urgency
        renewalTable.setDefaultRenderer(Object.class,
                new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String daysStr = (String) table.getValueAt(row, 6);
                    try {
                        long days = Long.parseLong(daysStr);
                        if (days <= 7) {
                            c.setBackground(new Color(255, 200, 200)); // Red
                        } else if (days <= 15) {
                            c.setBackground(new Color(255, 230, 180)); // Orange
                        } else if (days <= 30) {
                            c.setBackground(new Color(255, 255, 200)); // Yellow
                        } else {
                            c.setBackground(new Color(200, 255, 200)); // Green
                        }
                    } catch (NumberFormatException e) {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(renewalTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                "Subscriptions Sorted by Renewal Date (TreeMap)"));

        // --- Info Label ---
        JLabel infoLabel = new JLabel(
                "  \u2B24 Red = ≤7 days  |  \u2B24 Orange = ≤15 days  |  "
                + "\u2B24 Yellow = ≤30 days  |  \u2B24 Green = Safe");
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));

        // Layout
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(infoLabel, BorderLayout.SOUTH);

        // Sort change listener
        cboSortBy.addActionListener(e -> refreshData());
    }

    /**
     * Renew a selected subscription.
     */
    private void renewSubscription() {
        int row = renewalTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a subscription to renew.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String subId = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Renew subscription " + subId + "?", "Confirm Renewal",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.renewSubscription(subId);
                refreshData();
                JOptionPane.showMessageDialog(this,
                        "Subscription " + subId + " renewed successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (InvalidSubscriptionException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Refresh table with sorted subscription data.
     */
    public void refreshData() {
        tableModel.setRowCount(0);

        List<Subscription> sorted;
        int sortIndex = cboSortBy != null ? cboSortBy.getSelectedIndex() : 0;

        if (sortIndex == 1) {
            // Sort by plan type using Comparator
            sorted = service.getSortedByPlanType();
        } else {
            // Sort by renewal date using Comparable
            sorted = service.getSortedByRenewalDate();
        }

        for (Subscription s : sorted) {
            if (s.isActive()) {
                long daysLeft = s.getDaysUntilExpiry();
                tableModel.addRow(new Object[]{
                        s.getSubscriptionId(),
                        s.getSubscriber().getName(),
                        s.getPublication().getTitle(),
                        s.getPlanType().getDisplayName(),
                        s.getStartDate().toString(),
                        s.getEndDate().toString(),
                        String.valueOf(daysLeft),
                        daysLeft <= 0 ? "EXPIRED" : "Active"
                });
            }
        }
    }
}
