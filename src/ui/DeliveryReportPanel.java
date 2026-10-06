package ui;

import model.Delivery;
import model.Subscription;
import service.SubscriptionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;

/*
 * DeliveryReportPanel
 * Module 7: Delivery Reports
 * Generates summary reports of deliveries filtered by date
 * range, status, or subscription. Uses StringBuilder for
 * report text generation.
 */
public class DeliveryReportPanel extends JPanel {

    private SubscriptionService service;

    private JTextField txtFromDate, txtToDate;
    private JComboBox<String> cboFilterStatus;
    private JButton btnGenerate, btnClear;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JTextArea txtReport;
    private JLabel lblSummary;

    public DeliveryReportPanel(SubscriptionService service) {
        this.service = service;
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Filter Controls (North) ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder(
                "Report Filters"));

        filterPanel.add(new JLabel("From Date:"));
        txtFromDate = new JTextField(
                LocalDate.now().minusMonths(1).toString(), 10);
        filterPanel.add(txtFromDate);

        filterPanel.add(new JLabel("To Date:"));
        txtToDate = new JTextField(LocalDate.now().toString(), 10);
        filterPanel.add(txtToDate);

        filterPanel.add(new JLabel("Status:"));
        cboFilterStatus = new JComboBox<>(new String[]{
                "All", "Delivered", "Scheduled", "Pending", "Failed"});
        filterPanel.add(cboFilterStatus);

        btnGenerate = util.UIUtils.createButton("Generate Report", new Color(0, 123, 255), Color.WHITE);
        filterPanel.add(btnGenerate);

        btnClear = util.UIUtils.createSecondaryButton("Clear");
        filterPanel.add(btnClear);

        lblSummary = new JLabel("  ");
        lblSummary.setFont(new Font("SansSerif", Font.BOLD, 12));
        filterPanel.add(lblSummary);

        // --- Split Pane: Table + Text Report ---
        // Table
        String[] columns = {"Delivery ID", "Subscription", "Subscriber",
                "Publication", "Date", "Status", "Address"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        reportTable = new JTable(tableModel);
        reportTable.setRowHeight(25);
        reportTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));

        // Color code by status
        reportTable.setDefaultRenderer(Object.class,
                new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String status = (String) table.getValueAt(row, 5);
                    switch (status) {
                        case "Delivered":
                            c.setBackground(new Color(200, 255, 200));
                            break;
                        case "Failed":
                            c.setBackground(new Color(255, 200, 200));
                            break;
                        case "Pending":
                            c.setBackground(new Color(255, 255, 200));
                            break;
                        case "Scheduled":
                            c.setBackground(new Color(200, 220, 255));
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });

        JScrollPane tableScroll = new JScrollPane(reportTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder(
                "Delivery Records"));

        // Text report area
        txtReport = new JTextArea();
        txtReport.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReport.setEditable(false);
        JScrollPane reportScroll = new JScrollPane(txtReport);
        reportScroll.setBorder(BorderFactory.createTitledBorder(
                "Summary Report (StringBuilder)"));
        reportScroll.setPreferredSize(new Dimension(0, 180));

        // Split pane
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, tableScroll, reportScroll);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.65);

        // Layout
        add(filterPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // Actions
        btnGenerate.addActionListener(e -> generateReport());
        btnClear.addActionListener(e -> clearReport());
    }

    /**
     * Generate the delivery report.
     */
    private void generateReport() {
        tableModel.setRowCount(0);

        // Parse filter dates
        LocalDate fromDate, toDate;
        try {
            fromDate = LocalDate.parse(txtFromDate.getText().trim());
            toDate = LocalDate.parse(txtToDate.getText().trim());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format. Use YYYY-MM-DD.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String statusFilter = (String) cboFilterStatus.getSelectedItem();

        // Iterate over delivery history
        LinkedList<Delivery> allDeliveries = service.getDeliveryHistory();

        int totalDeliveries = 0;
        int delivered = 0, scheduled = 0, pending = 0, failed = 0;

        for (Delivery d : allDeliveries) {
            // Apply date filter
            if (d.getDeliveryDate().isBefore(fromDate)
                    || d.getDeliveryDate().isAfter(toDate)) {
                continue;
            }

            // Apply status filter
            if (!"All".equals(statusFilter)
                    && !d.getStatus().equals(statusFilter)) {
                continue;
            }

            // Add to table
            tableModel.addRow(new Object[]{
                    d.getDeliveryId(),
                    d.getSubscription().getSubscriptionId(),
                    d.getSubscription().getSubscriber().getName(),
                    d.getSubscription().getPublication().getTitle(),
                    d.getDeliveryDate().toString(),
                    d.getStatus(),
                    d.getDeliveryAddress()
            });

            totalDeliveries++;
            switch (d.getStatus()) {
                case "Delivered": delivered++; break;
                case "Scheduled": scheduled++; break;
                case "Pending":   pending++;   break;
                case "Failed":    failed++;    break;
            }
        }

        // Build summary report text
        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════════════\n");
        report.append("          DELIVERY SUMMARY REPORT\n");
        report.append("═══════════════════════════════════════════════════\n");
        report.append("  Report Generated : ").append(LocalDate.now()).append("\n");
        report.append("  Period           : ").append(fromDate)
              .append(" to ").append(toDate).append("\n");
        report.append("  Status Filter    : ").append(statusFilter).append("\n");
        report.append("───────────────────────────────────────────────────\n");
        report.append("  Total Deliveries : ").append(totalDeliveries).append("\n");
        report.append("  Delivered        : ").append(delivered)
              .append(" (").append(totalDeliveries > 0
                      ? String.format("%.1f%%",
                              (double) delivered / totalDeliveries * 100) : "0%")
              .append(")\n");
        report.append("  Scheduled        : ").append(scheduled).append("\n");
        report.append("  Pending          : ").append(pending).append("\n");
        report.append("  Failed           : ").append(failed).append("\n");
        report.append("───────────────────────────────────────────────────\n");

        if (failed > 0) {
            report.append("  ⚠ WARNING: ").append(failed)
                  .append(" delivery(ies) failed in this period.\n");
        }
        if (pending > 0) {
            report.append("  ℹ NOTE: ").append(pending)
                  .append(" delivery(ies) still pending.\n");
        }

        report.append("═══════════════════════════════════════════════════\n");

        txtReport.setText(report.toString());

        lblSummary.setText("  Total: " + totalDeliveries
                + " | Delivered: " + delivered
                + " | Failed: " + failed);
    }

    private void clearReport() {
        tableModel.setRowCount(0);
        txtReport.setText("");
        lblSummary.setText("  ");
    }

    public void refreshData() {
        // Auto-generate report on refresh
        generateReport();
    }
}
