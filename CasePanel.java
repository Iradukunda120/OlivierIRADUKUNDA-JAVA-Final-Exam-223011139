package GOVTECHFORM;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class CasePanel extends JPanel {

    private JTable caseTable;
    private DefaultTableModel tableModel;
    private JButton addBtn, editBtn, deleteBtn, refreshBtn, backBtn;
    private JPanel headerPanel;
    private JTextField searchField;
    private JButton searchBtn;
    private JLabel statusLabel;
    private Connection conn;
    private Color primaryColor = new Color(41, 128, 185);
    private Color backgroundColor = Color.WHITE;
    private Color cardBgColor = Color.WHITE;
    private Color textPrimaryColor = new Color(44, 62, 80);

    public CasePanel() {
        initializeDatabase();
        setLayout(new BorderLayout());
        initializeUI();
        loadCases();
        setupEventListeners();
    }

    private void initializeDatabase() {
        try {
            conn = DB.getConnection();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Database connection failed: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeUI() {
        setBackground(backgroundColor);

        // Header Panel with title and back button
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("📋 Case Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        backBtn = new JButton("← Back to Dashboard");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backBtn.setBackground(new Color(255, 255, 255, 40));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 80), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        headerPanel.add(backBtn, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Control Panel with search and buttons
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(cardBgColor);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(cardBgColor);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchLabel.setForeground(textPrimaryColor);

        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setToolTipText("Search cases by type, description, or priority");
        
        searchBtn = createStyledButton("🔍 Search", new Color(52, 152, 219));
        
        JButton clearSearchBtn = createStyledButton("Clear", new Color(149, 165, 166));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearSearchBtn);

        controlPanel.add(searchPanel, BorderLayout.WEST);

        // Action Buttons Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionPanel.setBackground(cardBgColor);

        addBtn = createStyledButton("➕ Add Case", new Color(46, 204, 113));
        editBtn = createStyledButton("✏️ Edit Case", new Color(52, 152, 219));
        deleteBtn = createStyledButton("🗑️ Delete Case", new Color(231, 76, 60));
        refreshBtn = createStyledButton("🔄 Refresh", new Color(149, 165, 166));

        actionPanel.add(addBtn);
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(refreshBtn);

        controlPanel.add(actionPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        tablePanel.setBackground(backgroundColor);

        // Create table model
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Long.class; // CaseID
                return String.class;
            }
        };
        
        // Add columns based on your database structure
        String[] columns = {"ID", "Case Type", "Description", "Priority", "Created Date"};
        for (String column : columns) {
            tableModel.addColumn(column);
        }

        caseTable = new JTable(tableModel);
        caseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        caseTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        caseTable.getTableHeader().setBackground(new Color(240, 240, 240));
        caseTable.getTableHeader().setForeground(textPrimaryColor);
        caseTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        caseTable.setRowHeight(30);
        caseTable.setIntercellSpacing(new Dimension(0, 1));
        caseTable.setShowGrid(true);
        caseTable.setGridColor(new Color(240, 240, 240));
        caseTable.setBackground(cardBgColor);
        caseTable.setForeground(textPrimaryColor);
        caseTable.setSelectionBackground(new Color(220, 240, 255));
        caseTable.setSelectionForeground(textPrimaryColor);

        // Enable sorting
        caseTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(caseTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(cardBgColor);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        // Status Panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(cardBgColor);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        statusLabel = new JLabel("Ready - Select a case to manage");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(100, 100, 100));

        // Statistics label
        JLabel statsLabel = new JLabel();
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statsLabel.setForeground(new Color(100, 100, 100));
        
        // Update stats after loading data
        SwingUtilities.invokeLater(() -> updateStatistics(statsLabel));

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(statsLabel, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }

    private void setupEventListeners() {
        addBtn.addActionListener(e -> addCase());
        editBtn.addActionListener(e -> editCase());
        deleteBtn.addActionListener(e -> deleteCase());
        refreshBtn.addActionListener(e -> refreshData());
        
        searchBtn.addActionListener(e -> searchCases());
        searchField.addActionListener(e -> searchCases());
        
        // Clear search button
        Component[] searchComps = ((JPanel) getComponent(1)).getComponents();
        for (Component comp : searchComps) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals("Clear")) {
                ((JButton) comp).addActionListener(e -> {
                    searchField.setText("");
                    loadCases();
                });
            }
        }

        // Double-click to edit
        caseTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editCase();
                }
            }
        });

        // Selection listener to update status
        caseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = caseTable.getSelectedRow();
                if (row != -1) {
                    String caseType = (String) tableModel.getValueAt(
                        caseTable.convertRowIndexToModel(row), 1);
                    updateStatus("Selected: " + caseType);
                }
            }
        });
    }

    public void setBackButtonListener(java.awt.event.ActionListener listener) {
        backBtn.addActionListener(listener);
    }

    private void loadCases() {
        try {
            // Fixed table name from "Case" to "casetable" to match your database
            String sql = "SELECT * FROM casetable ORDER BY CaseID";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            tableModel.setRowCount(0);
            int rowCount = 0;
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            while (rs.next()) {
                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                String createdDate = (createdAt != null) ? dateFormat.format(createdAt) : "N/A";
                
                tableModel.addRow(new Object[]{
                    rs.getLong("CaseID"),
                    rs.getString("CaseType"),
                    truncateDescription(rs.getString("Description")),
                    getPriorityWithIcon(rs.getString("Priority")),
                    createdDate
                });
                rowCount++;
            }
            
            updateStatus("Loaded " + rowCount + " cases");
            updateStatistics();
            
        } catch (SQLException e) {
            showError("Error loading cases: " + e.getMessage());
        }
    }

    private String truncateDescription(String description) {
        if (description == null || description.length() <= 50) {
            return description;
        }
        return description.substring(0, 50) + "...";
    }

    private String getPriorityWithIcon(String priority) {
        if (priority == null) return "❓ Unknown";
        switch (priority.toLowerCase()) {
            case "high": return "🔴 High";
            case "medium": return "🟡 Medium";
            case "low": return "🟢 Low";
            default: return "❓ " + priority;
        }
    }

    private void updateStatistics() {
        // Update statistics in status panel
        SwingUtilities.invokeLater(() -> {
            Component[] comps = ((JPanel) getComponent(3)).getComponents();
            for (Component comp : comps) {
                if (comp instanceof JLabel && ((JLabel) comp).getText().contains("Cases:")) {
                    int highPriorityCount = getHighPriorityCount();
                    ((JLabel) comp).setText("Cases: " + tableModel.getRowCount() + " (High: " + highPriorityCount + ")");
                    break;
                }
            }
        });
    }

    private int getHighPriorityCount() {
        int count = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String priority = (String) tableModel.getValueAt(i, 3);
            if (priority.contains("🔴 High")) {
                count++;
            }
        }
        return count;
    }

    private void updateStatistics(JLabel statsLabel) {
        int highPriorityCount = getHighPriorityCount();
        statsLabel.setText("Cases: " + tableModel.getRowCount() + " (High: " + highPriorityCount + ")");
    }

    private void searchCases() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadCases();
            return;
        }

        try {
            String sql = "SELECT * FROM casetable WHERE CaseType LIKE ? OR Description LIKE ? OR Priority LIKE ? ORDER BY CaseID";
            PreparedStatement stmt = conn.prepareStatement(sql);
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            stmt.setString(3, likeTerm);
            
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);
            int rowCount = 0;
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            while (rs.next()) {
                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                String createdDate = (createdAt != null) ? dateFormat.format(createdAt) : "N/A";
                
                tableModel.addRow(new Object[]{
                    rs.getLong("CaseID"),
                    rs.getString("CaseType"),
                    truncateDescription(rs.getString("Description")),
                    getPriorityWithIcon(rs.getString("Priority")),
                    createdDate
                });
                rowCount++;
            }
            
            updateStatus("Found " + rowCount + " cases matching '" + searchTerm + "'");
            
        } catch (SQLException e) {
            showError("Error searching cases: " + e.getMessage());
        }
    }

    private void addCase() {
        // Create form panel with better styling
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBackground(cardBgColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField caseTypeField = new JTextField();
        JTextArea descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        JComboBox<String> priorityCombo = new JComboBox<>(new String[]{"High", "Medium", "Low"});

        // Add labels and fields
        formPanel.add(createFormLabel("Case Type *:"));
        formPanel.add(caseTypeField);
        formPanel.add(createFormLabel("Description:"));
        formPanel.add(descriptionScroll);
        formPanel.add(createFormLabel("Priority:"));
        formPanel.add(priorityCombo);

        int option = JOptionPane.showConfirmDialog(this, formPanel, 
            "➕ Add New Case", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            try {
                // Validate required fields
                if (caseTypeField.getText().trim().isEmpty()) {
                    showError("Case Type is required!");
                    return;
                }

                String sql = "INSERT INTO casetable (CaseType, Description, Priority) VALUES (?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, caseTypeField.getText().trim());
                
                // Handle description - set to NULL if empty
                if (descriptionArea.getText().trim().isEmpty()) {
                    ps.setNull(2, Types.VARCHAR);
                } else {
                    ps.setString(2, descriptionArea.getText().trim());
                }
                
                ps.setString(3, (String) priorityCombo.getSelectedItem());

                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    showSuccess("Case '" + caseTypeField.getText().trim() + "' added successfully!");
                    loadCases();
                }
            } catch (SQLException e) {
                showError("Error adding case: " + e.getMessage());
            }
        }
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(textPrimaryColor);
        return label;
    }

    private void editCase() {
        int row = caseTable.getSelectedRow();
        if (row == -1) {
            showWarning("Please select a case to edit.");
            return;
        }

        int modelRow = caseTable.convertRowIndexToModel(row);
        Long caseId = (Long) tableModel.getValueAt(modelRow, 0);
        String currentCaseType = (String) tableModel.getValueAt(modelRow, 1);

        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBackground(cardBgColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField caseTypeField = new JTextField(currentCaseType);
        JTextArea descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        // Get full description from database
        String fullDescription = getFullDescription(caseId);
        descriptionArea.setText(fullDescription != null ? fullDescription : "");
        
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        JComboBox<String> priorityCombo = new JComboBox<>(new String[]{"High", "Medium", "Low"});

        // Set current priority
        String currentPriority = getCurrentPriority((String) tableModel.getValueAt(modelRow, 3));
        priorityCombo.setSelectedItem(currentPriority);

        formPanel.add(createFormLabel("Case Type *:"));
        formPanel.add(caseTypeField);
        formPanel.add(createFormLabel("Description:"));
        formPanel.add(descriptionScroll);
        formPanel.add(createFormLabel("Priority:"));
        formPanel.add(priorityCombo);

        int option = JOptionPane.showConfirmDialog(this, formPanel, 
            "✏️ Edit Case: " + currentCaseType, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            try {
                // Validate required fields
                if (caseTypeField.getText().trim().isEmpty()) {
                    showError("Case Type is required!");
                    return;
                }

                String sql = "UPDATE casetable SET CaseType=?, Description=?, Priority=? WHERE CaseID=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, caseTypeField.getText().trim());
                
                // Handle description - set to NULL if empty
                if (descriptionArea.getText().trim().isEmpty()) {
                    ps.setNull(2, Types.VARCHAR);
                } else {
                    ps.setString(2, descriptionArea.getText().trim());
                }
                
                ps.setString(3, (String) priorityCombo.getSelectedItem());
                ps.setLong(4, caseId);

                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    showSuccess("Case '" + caseTypeField.getText().trim() + "' updated successfully!");
                    loadCases();
                }
            } catch (SQLException e) {
                showError("Error updating case: " + e.getMessage());
            }
        }
    }

    private String getFullDescription(Long caseId) {
        try {
            String sql = "SELECT Description FROM casetable WHERE CaseID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, caseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Description");
            }
        } catch (SQLException e) {
            // Ignore error, return null
        }
        return null;
    }

    private String getCurrentPriority(String priorityWithIcon) {
        if (priorityWithIcon == null) return "Medium";
        if (priorityWithIcon.contains("High")) return "High";
        if (priorityWithIcon.contains("Medium")) return "Medium";
        if (priorityWithIcon.contains("Low")) return "Low";
        return "Medium";
    }

    private void deleteCase() {
        int row = caseTable.getSelectedRow();
        if (row == -1) {
            showWarning("Please select a case to delete.");
            return;
        }

        int modelRow = caseTable.convertRowIndexToModel(row);
        Long caseId = (Long) tableModel.getValueAt(modelRow, 0);
        String caseType = (String) tableModel.getValueAt(modelRow, 1);
        String priority = (String) tableModel.getValueAt(modelRow, 3);

        int confirm = JOptionPane.showConfirmDialog(this,
            "<html><b>Are you sure you want to delete this case?</b><br><br>" +
            "Case Type: <b>" + caseType + "</b><br>" +
            "Priority: " + priority + "<br>" +
            "ID: " + caseId + "<br><br>" +
            "This action cannot be undone!</html>",
            "🗑️ Confirm Delete Case",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Check if case has related records
                if (hasRelatedRecords(caseId)) {
                    showWarning("Cannot delete case '" + caseType + "'. " +
                               "There are related records (documents, officer assignments) in the system. " +
                               "Please delete the related records first.");
                    return;
                }

                String sql = "DELETE FROM casetable WHERE CaseID = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setLong(1, caseId);

                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    showSuccess("Case '" + caseType + "' deleted successfully!");
                    loadCases();
                }
            } catch (SQLException e) {
                showError("Error deleting case: " + e.getMessage());
            }
        }
    }

    private boolean hasRelatedRecords(Long caseId) throws SQLException {
        // Check if case has documents
        try {
            String sql = "SELECT COUNT(*) FROM document WHERE CaseID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, caseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            // Table might not exist, ignore
        }

        // Check if case has officer assignments
        try {
            String sql = "SELECT COUNT(*) FROM officercase WHERE CaseID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, caseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            // Table might not exist, ignore
        }

        return false;
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            "<html><b>Error:</b><br>" + message + "</html>", 
            "❌ Error", 
            JOptionPane.ERROR_MESSAGE);
        updateStatus("Error: " + message);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, 
            "<html><b>Warning:</b><br>" + message + "</html>", 
            "⚠️ Warning", 
            JOptionPane.WARNING_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, 
            "<html><b>Success:</b><br>" + message + "</html>", 
            "✅ Success", 
            JOptionPane.INFORMATION_MESSAGE);
        updateStatus(message);
    }

    public void refreshData() {
        loadCases();
    }

    // Clean up resources
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update theme colors from AdminDashboard
    public void updateTheme(boolean darkMode) {
        if (darkMode) {
            backgroundColor = new Color(45, 45, 45);
            cardBgColor = new Color(60, 60, 60);
            textPrimaryColor = Color.WHITE;
        } else {
            backgroundColor = Color.WHITE;
            cardBgColor = Color.WHITE;
            textPrimaryColor = new Color(44, 62, 80);
        }
        
        // Update component colors
        headerPanel.setBackground(primaryColor);
        setBackground(backgroundColor);
        
        // Update table colors
        if (caseTable != null) {
            caseTable.setBackground(cardBgColor);
            caseTable.setForeground(textPrimaryColor);
            caseTable.getTableHeader().setBackground(new Color(240, 240, 240));
            caseTable.getTableHeader().setForeground(textPrimaryColor);
        }
        
        repaint();
    }
}