package ui;

import model.*;
import service.SubscriptionService;
import exception.InvalidDeliveryException;
import util.InvoiceGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/*
 * DeliveryPanel
 * Module 3: Delivery Scheduling
 * Allows scheduling deliveries, updating delivery status, and
 * viewing the delivery history stored in the LinkedList.
 */
public class DeliveryPanel extends JPanel {

    private SubscriptionService service;

    private JTable deliveryTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> cboSubscription, cboStatus;
    private JTextField txtDeliveryDate, txtAddress;
    private JButton btnSchedule, btnUpdateStatus, btnViewLabel;

    public DeliveryPanel(SubscriptionService service) {
        this.service = service;
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Delivery Table ---
        String[] columns = {"Delivery ID", "Subscription", "Subscriber",
                "Publication", "Delivery Date", "Status", "Address"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        deliveryTable = new JTable(tableModel);
        deliveryTable.setRowHeight(25);
        deliveryTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(deliveryTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                "Delivery History (LinkedList)"));

        // --- Form ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                "Schedule / Update Delivery"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Subscription dropdown
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Subscription:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 3;
        cboSubscription = new JComboBox<>();
        formPanel.add(cboSubscription, gbc);
        gbc.gridwidth = 1; gbc.weightx = 0;

        // Delivery Date
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Delivery Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtDeliveryDate = new JTextField(LocalDate.now().toString());
        formPanel.add(txtDeliveryDate, gbc);
        gbc.weightx = 0;

        // Status dropdown
        gbc.gridx = 2;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        cboStatus = new JComboBox<>(new String[]{
                "Scheduled", "Pending", "Delivered", "Failed"});
        formPanel.add(cboStatus, gbc);
        gbc.weightx = 0;

        // Address
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Delivery Address:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        txtAddress = new JTextField();
        formPanel.add(txtAddress, gbc);
        gbc.gridwidth = 1; gbc.weightx = 0;

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnSchedule = util.UIUtils.createButton("Schedule Delivery", new Color(40, 167, 69), Color.WHITE);
        btnUpdateStatus = util.UIUtils.createButton("Update Status", new Color(0, 123, 255), Color.WHITE);
        btnViewLabel = util.UIUtils.createSecondaryButton("View Delivery Label");

        btnPanel.add(btnSchedule);
        btnPanel.add(btnUpdateStatus);
        btnPanel.add(btnViewLabel);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        formPanel.add(btnPanel, gbc);

        // Layout
        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);

        // Auto-fill address when subscription is selected
        cboSubscription.addActionListener(e -> {
            int idx = cboSubscription.getSelectedIndex();
            if (idx >= 0 && idx < service.getAllSubscriptions().size()) {
                Subscription sub = service.getAllSubscriptions().get(idx);
                txtAddress.setText(sub.getSubscriber().getAddress());
            }
        });

        // Actions
        btnSchedule.addActionListener(e -> scheduleDelivery());
        btnUpdateStatus.addActionListener(e -> updateStatus());
        btnViewLabel.addActionListener(e -> viewLabel());
    }

    /**
     * Schedule a new delivery.
     */
    private void scheduleDelivery() {
        try {
            int subIdx = cboSubscription.getSelectedIndex();
            if (subIdx < 0) {
                JOptionPane.showMessageDialog(this,
                        "Select a subscription.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Subscription subscription = service.getAllSubscriptions().get(subIdx);

            LocalDate deliveryDate;
            try {
                deliveryDate = LocalDate.parse(txtDeliveryDate.getText().trim());
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date. Use YYYY-MM-DD format.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String address = txtAddress.getText().trim();
            if (address.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Delivery address cannot be empty.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String status = (String) cboStatus.getSelectedItem();
            String deliveryId = service.generateDeliveryId();

            Delivery delivery = new Delivery(deliveryId, subscription,
                    deliveryDate, status, address);

            service.scheduleDelivery(delivery);

            JOptionPane.showMessageDialog(this,
                    "Delivery " + deliveryId + " scheduled successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            refreshData();

        } catch (InvalidDeliveryException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Update status of selected delivery.
     */
    private void updateStatus() {
        int row = deliveryTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a delivery to update.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String delId = (String) tableModel.getValueAt(row, 0);
        String newStatus = (String) cboStatus.getSelectedItem();
        try {
            service.updateDeliveryStatus(delId, newStatus);
            refreshData();
            JOptionPane.showMessageDialog(this,
                    "Status updated to: " + newStatus,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (InvalidDeliveryException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * View delivery label for selected delivery.
     */
    private void viewLabel() {
        int row = deliveryTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a delivery to view label.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String delId = (String) tableModel.getValueAt(row, 0);
        // Find the delivery in the linked list
        for (Delivery d : service.getDeliveryHistory()) {
            if (d.getDeliveryId().equals(delId)) {
                String label = InvoiceGenerator.generateDeliveryLabel(d);
                JTextArea area = new JTextArea(label);
                area.setFont(new Font("Monospaced", Font.PLAIN, 12));
                area.setEditable(false);
                JScrollPane sp = new JScrollPane(area);
                sp.setPreferredSize(new Dimension(550, 300));
                JOptionPane.showMessageDialog(this, sp,
                        "Delivery Label", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
    }

    public void refreshData() {
        // Refresh subscription dropdown
        cboSubscription.removeAllItems();
        for (Subscription s : service.getAllSubscriptions()) {
            if (s.isActive()) {
                cboSubscription.addItem(s.toString());
            }
        }

        // Refresh table from LinkedList
        tableModel.setRowCount(0);
        // Iterating over delivery history
        for (Delivery d : service.getDeliveryHistory()) {
            tableModel.addRow(new Object[]{
                    d.getDeliveryId(),
                    d.getSubscription().getSubscriptionId(),
                    d.getSubscription().getSubscriber().getName(),
                    d.getSubscription().getPublication().getTitle(),
                    d.getDeliveryDate().toString(),
                    d.getStatus(),
                    d.getDeliveryAddress()
            });
        }
    }
}
