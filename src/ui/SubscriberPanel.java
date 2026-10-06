package ui;

import model.Subscriber;
import service.SubscriptionService;
import exception.InvalidSubscriptionException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/*
 * SubscriberPanel
 * Module 1: Subscriber Registration
 * Provides a form to register, view, update, and delete
 * subscribers. Displays all subscribers in a JTable.
 */
public class SubscriberPanel extends JPanel {

    private SubscriptionService service;

    // UI Components
    private JTable subscriberTable;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtName, txtEmail, txtPhone, txtAddress;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    public SubscriberPanel(SubscriptionService service) {
        this.service = service;
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Table (Center) ---
        String[] columns = {"Subscriber ID", "Name", "Email", "Phone", "Address"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only table
            }
        };
        subscriberTable = new JTable(tableModel);
        subscriberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        subscriberTable.setRowHeight(25);
        subscriberTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));

        // Populate form when a row is selected
        subscriberTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = subscriberTable.getSelectedRow();
                if (row >= 0) {
                    txtId.setText((String) tableModel.getValueAt(row, 0));
                    txtName.setText((String) tableModel.getValueAt(row, 1));
                    txtEmail.setText((String) tableModel.getValueAt(row, 2));
                    txtPhone.setText((String) tableModel.getValueAt(row, 3));
                    txtAddress.setText((String) tableModel.getValueAt(row, 4));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(subscriberTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("All Subscribers"));

        // --- Form (South) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Subscriber Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: ID
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Subscriber ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtId = new JTextField(15);
        txtId.setEditable(false); // Auto-generated
        txtId.setBackground(new Color(230, 230, 230));
        formPanel.add(txtId, gbc);
        gbc.weightx = 0;

        // Row 0: Name
        gbc.gridx = 2;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        txtName = new JTextField(15);
        formPanel.add(txtName, gbc);
        gbc.weightx = 0;

        // Row 1: Email
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtEmail = new JTextField(15);
        formPanel.add(txtEmail, gbc);
        gbc.weightx = 0;

        // Row 1: Phone
        gbc.gridx = 2;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        txtPhone = new JTextField(15);
        formPanel.add(txtPhone, gbc);
        gbc.weightx = 0;

        // Row 2: Address
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        txtAddress = new JTextField(40);
        formPanel.add(txtAddress, gbc);
        gbc.gridwidth = 1; gbc.weightx = 0;

        // Row 3: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnAdd = util.UIUtils.createButton("Add Subscriber", new Color(40, 167, 69), Color.WHITE);
        btnUpdate = util.UIUtils.createButton("Update", new Color(0, 123, 255), Color.WHITE);
        btnDelete = util.UIUtils.createButton("Delete", new Color(220, 53, 69), Color.WHITE);
        btnClear = util.UIUtils.createSecondaryButton("Clear Form");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);

        // --- Add to main layout ---
        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);

        // --- Button Actions ---
        btnAdd.addActionListener(e -> addSubscriber());
        btnUpdate.addActionListener(e -> updateSubscriber());
        btnDelete.addActionListener(e -> deleteSubscriber());
        btnClear.addActionListener(e -> clearForm());

        // Pre-fill the ID for adding
        txtId.setText(service.generateSubscriberId());
    }

    private void addSubscriber() {
        try {
            // Validation in service layer
            Subscriber sub = new Subscriber(
                    txtId.getText().trim(),
                    txtName.getText().trim(),
                    txtEmail.getText().trim(),
                    txtPhone.getText().trim(),
                    txtAddress.getText().trim()
            );
            service.addSubscriber(sub);
            JOptionPane.showMessageDialog(this,
                    "Subscriber added successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            refreshData();
        } catch (InvalidSubscriptionException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSubscriber() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a subscriber to update.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Subscriber updated = new Subscriber(
                    txtId.getText().trim(),
                    txtName.getText().trim(),
                    txtEmail.getText().trim(),
                    txtPhone.getText().trim(),
                    txtAddress.getText().trim()
            );
            service.updateSubscriber(updated);
            JOptionPane.showMessageDialog(this,
                    "Subscriber updated successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (InvalidSubscriptionException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSubscriber() {
        int row = subscriberTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a subscriber to delete.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete subscriber " + id + "?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.deleteSubscriber(id);
                clearForm();
                refreshData();
            } catch (InvalidSubscriptionException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtId.setText(service.generateSubscriberId());
        txtName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        subscriberTable.clearSelection();
    }

    /**
     * Refresh table data from the service.
     */
    public void refreshData() {
        tableModel.setRowCount(0);
        for (Subscriber s : service.getAllSubscribers()) {
            tableModel.addRow(new Object[]{
                    s.getSubscriberId(),
                    s.getName(),
                    s.getEmail(),
                    s.getPhone(),
                    s.getAddress()
            });
        }
    }
}
