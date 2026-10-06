package ui;

import model.*;
import service.SubscriptionService;
import exception.InvalidSubscriptionException;
import util.InvoiceGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/*
 * PlanSelectionPanel
 * Module 2: Plan Selection
 * Allows selecting a subscriber, publication, and plan type
 * to create a new subscription. Shows invoice on creation.
 */
public class PlanSelectionPanel extends JPanel {

    private SubscriptionService service;

    // UI Components
    private JTable subscriptionTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> cboSubscriber, cboPublication, cboPlanType;
    private JTextField txtStartDate, txtCost;
    private JButton btnCreate, btnCancel, btnDelete, btnInvoice;

    public PlanSelectionPanel(SubscriptionService service) {
        this.service = service;
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Subscription Table (Center) ---
        String[] columns = {"Sub ID", "Subscriber", "Publication",
                "Plan Type", "Start Date", "End Date", "Amount (Rs.)", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        subscriptionTable = new JTable(tableModel);
        subscriptionTable.setRowHeight(25);
        subscriptionTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(subscriptionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                "All Subscriptions"));

        // --- Form (South) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                "Create New Subscription"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Subscriber dropdown
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Subscriber:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cboSubscriber = new JComboBox<>();
        formPanel.add(cboSubscriber, gbc);
        gbc.weightx = 0;

        // Publication dropdown
        gbc.gridx = 2;
        formPanel.add(new JLabel("Publication:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        cboPublication = new JComboBox<>();
        formPanel.add(cboPublication, gbc);
        gbc.weightx = 0;

        // Plan Type dropdown
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Plan Type:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cboPlanType = new JComboBox<>();
        for (SubscriptionType type : SubscriptionType.values()) {
            cboPlanType.addItem(type.toString());
        }
        formPanel.add(cboPlanType, gbc);
        gbc.weightx = 0;

        // Start Date
        gbc.gridx = 2;
        formPanel.add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        txtStartDate = new JTextField(LocalDate.now().toString());
        formPanel.add(txtStartDate, gbc);
        gbc.weightx = 0;

        // Estimated Cost (read-only)
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Estimated Cost:"), gbc);
        gbc.gridx = 1;
        txtCost = new JTextField("--");
        txtCost.setEditable(false);
        txtCost.setBackground(new Color(230, 230, 230));
        formPanel.add(txtCost, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnCreate = util.UIUtils.createButton("Create Subscription", new Color(40, 167, 69), Color.WHITE);
        btnCancel = util.UIUtils.createButton("Cancel Subscription", new Color(220, 53, 69), Color.WHITE);
        btnDelete = util.UIUtils.createSecondaryButton("Delete Subscription");
        btnInvoice = util.UIUtils.createButton("View Invoice", new Color(0, 123, 255), Color.WHITE);

        btnPanel.add(btnCreate);
        btnPanel.add(btnCancel);
        btnPanel.add(btnDelete);
        btnPanel.add(btnInvoice);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        formPanel.add(btnPanel, gbc);

        // --- Layout ---
        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);

        // --- Actions ---
        btnCreate.addActionListener(e -> createSubscription());
        btnCancel.addActionListener(e -> cancelSubscription());
        btnDelete.addActionListener(e -> deleteSubscription());
        btnInvoice.addActionListener(e -> viewInvoice());

        // Update cost estimate when plan or publication changes
        cboPlanType.addActionListener(e -> updateCostEstimate());
        cboPublication.addActionListener(e -> updateCostEstimate());
    }

    /**
     * Calculate and display the estimated subscription cost.
     */
    private void updateCostEstimate() {
        int pubIndex = cboPublication.getSelectedIndex();
        int planIndex = cboPlanType.getSelectedIndex();
        if (pubIndex >= 0 && planIndex >= 0) {
            Publication pub = service.getAllPublications().get(pubIndex);
            SubscriptionType planType = SubscriptionType.values()[planIndex];
            double cost = planType.calculateCost(pub.getMonthlyPrice());
            txtCost.setText(String.format("Rs. %.2f", cost));
        }
    }

    /**
     * Create a new subscription.
     */
    private void createSubscription() {
        try {
            int subIdx = cboSubscriber.getSelectedIndex();
            int pubIdx = cboPublication.getSelectedIndex();
            if (subIdx < 0 || pubIdx < 0) {
                JOptionPane.showMessageDialog(this,
                        "Please select a subscriber and publication.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Subscriber subscriber = service.getAllSubscribers().get(subIdx);
            Publication publication = service.getAllPublications().get(pubIdx);
            SubscriptionType planType =
                    SubscriptionType.values()[cboPlanType.getSelectedIndex()];

            // Validate date
            LocalDate startDate;
            try {
                startDate = LocalDate.parse(txtStartDate.getText().trim());
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date format. Use YYYY-MM-DD.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String subId = service.generateSubscriptionId();
            Subscription subscription = new Subscription(
                    subId, subscriber, publication, planType, startDate);

            service.createSubscription(subscription);

            // Generate and display invoice
            String invoice = InvoiceGenerator.generateInvoice(subscription);
            JTextArea invoiceArea = new JTextArea(invoice);
            invoiceArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            invoiceArea.setEditable(false);
            JScrollPane sp = new JScrollPane(invoiceArea);
            sp.setPreferredSize(new Dimension(550, 400));

            JOptionPane.showMessageDialog(this, sp,
                    "Subscription Created - Invoice",
                    JOptionPane.INFORMATION_MESSAGE);

            refreshData();

        } catch (InvalidSubscriptionException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cancel selected subscription.
     */
    private void cancelSubscription() {
        int row = subscriptionTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a subscription to cancel.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String subId = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel subscription " + subId + "?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.cancelSubscription(subId);
                refreshData();
            } catch (InvalidSubscriptionException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Permanently remove a selected subscription.
     */
    private void deleteSubscription() {
        int row = subscriptionTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a subscription to delete.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String subId = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete subscription " + subId + " permanently?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.deleteSubscription(subId);
                refreshData();
            } catch (InvalidSubscriptionException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * View invoice for selected subscription.
     */
    private void viewInvoice() {
        int row = subscriptionTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a subscription to view invoice.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String subId = (String) tableModel.getValueAt(row, 0);
        Subscription sub = service.getSubscription(subId);
        if (sub != null) {
            // Generate invoice text
            String invoice = InvoiceGenerator.generateInvoice(sub);
            JTextArea area = new JTextArea(invoice);
            area.setFont(new Font("Monospaced", Font.PLAIN, 12));
            area.setEditable(false);
            JScrollPane sp = new JScrollPane(area);
            sp.setPreferredSize(new Dimension(550, 400));
            JOptionPane.showMessageDialog(this, sp,
                    "Invoice - " + subId, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Refresh table and dropdown data.
     */
    public void refreshData() {
        // Refresh subscriber dropdown
        cboSubscriber.removeAllItems();
        for (Subscriber s : service.getAllSubscribers()) {
            cboSubscriber.addItem(s.toString());
        }

        // Refresh publication dropdown
        cboPublication.removeAllItems();
        for (Publication p : service.getAllPublications()) {
            cboPublication.addItem(p.toString());
        }

        // Refresh subscription table
        tableModel.setRowCount(0);
        for (Subscription s : service.getAllSubscriptions()) {
            tableModel.addRow(new Object[]{
                    s.getSubscriptionId(),
                    s.getSubscriber().getName(),
                    s.getPublication().getTitle(),
                    s.getPlanType().getDisplayName(),
                    s.getStartDate().toString(),
                    s.getEndDate().toString(),
                    String.format("%.2f", s.getAmount()),
                    s.isActive() ? "Active" : "Cancelled"
            });
        }

        updateCostEstimate();
    }
}
