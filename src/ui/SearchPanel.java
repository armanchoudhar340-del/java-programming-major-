package ui;

import model.Subscriber;
import model.Subscription;
import service.SubscriptionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/*
 * SearchPanel
 * Module 6: Subscription Search
 * Provides search functionality by subscriber name, subscriber
 * ID, or publication title. Demonstrates the Searching concept.
 */
public class SearchPanel extends JPanel {

    private SubscriptionService service;

    private JTextField txtSearch;
    private JComboBox<String> cboSearchType;
    private JButton btnSearch, btnClear;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JLabel lblResults;

    public SearchPanel(SubscriptionService service) {
        this.service = service;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Search Controls (North) ---
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchBar.setBorder(BorderFactory.createTitledBorder(
                "Search Subscriptions"));

        searchBar.add(new JLabel("Search By:"));
        cboSearchType = new JComboBox<>(new String[]{
                "Subscriber Name", "Subscriber ID", "Publication Title"});
        searchBar.add(cboSearchType);

        searchBar.add(new JLabel("Search:"));
        txtSearch = new JTextField(20);
        searchBar.add(txtSearch);

        btnSearch = util.UIUtils.createButton("Search", new Color(0, 123, 255), Color.WHITE);
        searchBar.add(btnSearch);

        btnClear = util.UIUtils.createSecondaryButton("Clear");
        searchBar.add(btnClear);

        lblResults = new JLabel("  Enter a search term and click Search.");
        lblResults.setFont(new Font("SansSerif", Font.ITALIC, 12));
        searchBar.add(lblResults);

        // --- Results Table (Center) ---
        String[] columns = {"Sub ID", "Subscriber", "Subscriber ID",
                "Publication", "Plan", "Start Date", "End Date",
                "Amount (Rs.)", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        resultTable = new JTable(tableModel);
        resultTable.setRowHeight(25);
        resultTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                "Search Results"));

        // Layout
        add(searchBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Actions
        btnSearch.addActionListener(e -> performSearch());
        btnClear.addActionListener(e -> clearResults());

        // Allow pressing Enter to search
        txtSearch.addActionListener(e -> performSearch());
    }

    /**
     * Perform the search based on selected criteria.
     */
    private void performSearch() {
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a search term.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        tableModel.setRowCount(0);
        int searchType = cboSearchType.getSelectedIndex();

        switch (searchType) {
            case 0: // Search by subscriber name
                searchByName(query);
                break;
            case 1: // Search by subscriber ID
                searchById(query);
                break;
            case 2: // Search by publication title
                searchByPublication(query);
                break;
        }

        lblResults.setText("  Found: " + tableModel.getRowCount() + " result(s)");
    }

    /**
     * Search by subscriber name.
     */
    private void searchByName(String name) {
        // First find matching subscribers
        List<Subscriber> matchingSubscribers =
                service.searchSubscribersByName(name);

        // Then find their subscriptions
        for (Subscriber sub : matchingSubscribers) {
            // Uses HashMap for efficient lookup
            List<Subscription> subs =
                    service.searchSubscriptionsBySubscriberId(
                            sub.getSubscriberId());
            for (Subscription s : subs) {
                addToTable(s);
            }
        }
    }

    /**
     * Search by subscriber ID.
     */
    private void searchById(String id) {
        List<Subscription> subs =
                service.searchSubscriptionsBySubscriberId(id.toUpperCase());
        for (Subscription s : subs) {
            addToTable(s);
        }

        // Also try partial match if exact match finds nothing
        if (subs.isEmpty()) {
            for (Subscription s : service.getAllSubscriptions()) {
                if (s.getSubscriber().getSubscriberId()
                        .toUpperCase().contains(id.toUpperCase())
                        || s.getSubscriptionId()
                                .toUpperCase().contains(id.toUpperCase())) {
                    addToTable(s);
                }
            }
        }
    }

    /**
     * Search by publication title.
     */
    private void searchByPublication(String title) {
        List<Subscription> results = service.searchByPublication(title);
        for (Subscription s : results) {
            addToTable(s);
        }
    }

    /**
     * Add a subscription record to the results table.
     */
    private void addToTable(Subscription s) {
        tableModel.addRow(new Object[]{
                s.getSubscriptionId(),
                s.getSubscriber().getName(),
                s.getSubscriber().getSubscriberId(),
                s.getPublication().getTitle(),
                s.getPlanType().getDisplayName(),
                s.getStartDate().toString(),
                s.getEndDate().toString(),
                String.format("%.2f", s.getAmount()),
                s.isActive() ? "Active" : "Cancelled"
        });
    }

    private void clearResults() {
        tableModel.setRowCount(0);
        txtSearch.setText("");
        lblResults.setText("  Enter a search term and click Search.");
    }

    public void refreshData() {
        // No auto-refresh needed; results depend on search query
    }
}
