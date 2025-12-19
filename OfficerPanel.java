package GOVTECHFORM;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class OfficerPanel extends JPanel {

    private JTable officerTable;
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

    public OfficerPanel() {
        initializeDatabase();
        setLayout(new BorderLayout());
        initializeUI();
        loadOfficers();
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

        JLabel titleLabel = new JLabel("👮 Officer Management");
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
        searchField.setToolTipText("Search officers by name, identifier, department, or location");
        
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

        addBtn = createStyledButton("➕ Add Officer", new Color(46, 204, 113));
        editBtn = createStyledButton("✏️ Edit Officer", new Color(52, 152, 219));
        deleteBtn = createStyledButton("🗑️ Delete Officer", new Color(231, 76, 60));
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
                if (columnIndex == 0) return Long.class; // OfficerID
                return String.class;
            }
        };
        
        // Add columns based on your database structure
        String[] columns = {"ID", "Officer Name", "Identifier", "Department", "Contact", "Status", "Location", "Assigned Since"};
        for (String column : columns) {
            tableModel.addColumn(column);
        }

        officerTable = new JTable(tableModel);
        officerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        officerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        officerTable.getTableHeader().setBackground(new Color(240, 240, 240));
        officerTable.getTableHeader().setForeground(textPrimaryColor);
        officerTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        officerTable.setRowHeight(30);
        officerTable.setIntercellSpacing(new Dimension(0, 1));
        officerTable.setShowGrid(true);
        officerTable.setGridColor(new Color(240, 240, 240));
        officerTable.setBackground(cardBgColor);
        officerTable.setForeground(textPrimaryColor);
        officerTable.setSelectionBackground(new Color(220, 240, 255));
        officerTable.setSelectionForeground(textPrimaryColor);

        // Enable sorting
        officerTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(officerTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(cardBgColor);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        // Status Panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(cardBgColor);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        statusLabel = new JLabel("Ready - Select an officer to manage");
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
        addBtn.addActionListener(e -> addOfficer());
        editBtn.addActionListener(e -> editOfficer());
        deleteBtn.addActionListener(e -> deleteOfficer());
        refreshBtn.addActionListener(e -> refreshData());
        
        searchBtn.addActionListener(e -> searchOfficers());
        searchField.addActionListener(e -> searchOfficers());
        
        // Clear search button
        Component[] searchComps = ((JPanel) getComponent(1)).getComponents();
        for (Component comp : searchComps) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals("Clear")) {
                ((JButton) comp).addActionListener(e -> {
                    searchField.setText("");
                    loadOfficers();
                });
            }
        }

        // Double-click to edit
        officerTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editOfficer();
                }
            }
        });

        // Selection listener to update status
        officerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = officerTable.getSelectedRow();
                if (row != -1) {
                    String officerName = (String) tableModel.getValueAt(
                        officerTable.convertRowIndexToModel(row), 1);
                    updateStatus("Selected: " + officerName);
                }
            }
        });
    }

    public void setBackButtonListener(java.awt.event.ActionListener listener) {
        backBtn.addActionListener(listener);
    }

    private void loadOfficers() {
        try {
            String sql = "SELECT o.OfficerID, o.Name, o.Identifier, d.Name as DepartmentName, " +
                        "o.Contact, o.Status, o.Location, o.AssignedSince " +
                        "FROM officer o " +
                        "LEFT JOIN department d ON o.DepartmentID = d.DepartmentID " +
                        "ORDER BY o.OfficerID";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            tableModel.setRowCount(0);
            int rowCount = 0;
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getLong("OfficerID"),
                    rs.getString("FullName"),
                    rs.getString("Identifier"),
                    rs.getString("DepartmentName"),
                    formatContact(rs.getString("Contact")),
                    getStatusWithIcon(rs.getString("Status")),
                    rs.getString("Location"),
                    rs.getDate("AssignedSince")
                });
                rowCount++;
            }
            
            updateStatus("Loaded " + rowCount + " officers");
            updateStatistics();
            
        } catch (SQLException e) {
            showError("Error loading officers: " + e.getMessage());
        }
    }

    private String formatContact(String contact) {
        if (contact == null) return "N/A";
        if (contact.startsWith("+250")) {
            // Format Rwandan phone numbers: +250 XXX XXX XXX
            return contact.replaceAll("(\\+250)(\\d{3})(\\d{3})(\\d{3})", "$1 $2 $3 $4");
        }
        return contact;
    }

    private String getStatusWithIcon(String status) {
        if (status == null) return "❓ Unknown";
        switch (status.toLowerCase()) {
            case "active": return "✅ Active";
            case "inactive": return "⏸️ Inactive";
            case "on leave": return "🏖️ On Leave";
            default: return "❓ " + status;
        }
    }

    private void updateStatistics() {
        // Update statistics in status panel
        SwingUtilities.invokeLater(() -> {
            Component[] comps = ((JPanel) getComponent(3)).getComponents();
            for (Component comp : comps) {
                if (comp instanceof JLabel && ((JLabel) comp).getText().contains("Officers:")) {
                    int activeCount = getActiveOfficersCount();
                    ((JLabel) comp).setText("Officers: " + tableModel.getRowCount() + " (Active: " + activeCount + ")");
                    break;
                }
            }
        });
    }

    private int getActiveOfficersCount() {
        int count = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String status = (String) tableModel.getValueAt(i, 5);
            if (status.contains("✅ Active")) {
                count++;
            }
        }
        return count;
    }

    private void updateStatistics(JLabel statsLabel) {
        int activeCount = getActiveOfficersCount();
        statsLabel.setText("Officers: " + tableModel.getRowCount() + " (Active: " + activeCount + ")");
    }

    private void searchOfficers() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadOfficers();
            return;
        }

        try {
            String sql = "SELECT o.OfficerID, o.Name, o.Identifier, d.Name as DepartmentName, " +
                        "o.Contact, o.Status, o.Location, o.AssignedSince " +
                        "FROM officer o " +
                        "LEFT JOIN department d ON o.DepartmentID = d.DepartmentID " +
                        "WHERE o.Name LIKE ? OR o.Identifier LIKE ? OR o.Location LIKE ? OR d.Name LIKE ? " +
                        "ORDER BY o.OfficerID";
            PreparedStatement stmt = conn.prepareStatement(sql);
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            stmt.setString(3, likeTerm);
            stmt.setString(4, likeTerm);
            
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);
            int rowCount = 0;
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getLong("OfficerID"),
                    rs.getString("FullName"),
                    rs.getString("Identifier"),
                    rs.getString("DepartmentName"),
                    formatContact(rs.getString("Contact")),
                    getStatusWithIcon(rs.getString("Status")),
                    rs.getString("Location"),
                    rs.getDate("AssignedSince")
                });
                rowCount++;
            }
            
            updateStatus("Found " + rowCount + " officers matching '" + searchTerm + "'");
            
        } catch (SQLException e) {
            showError("Error searching officers: " + e.getMessage());
        }
    }

    private void addOfficer() {
        try {
            // Get departments for dropdown
            Map<Long, String> departments = getDepartmentsMap();
            if (departments.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No departments found. Please add departments first.", 
                    "No Departments", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Create form panel with better styling
            JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
            formPanel.setBackground(cardBgColor);
            formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JTextField nameField = new JTextField();
            JTextField identifierField = new JTextField();
            JComboBox<String> departmentCombo = new JComboBox<>();
            JTextField contactField = new JTextField();
            JTextField locationField = new JTextField();
            JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Inactive", "On Leave"});

            // Populate department combo
            for (Map.Entry<Long, String> entry : departments.entrySet()) {
                departmentCombo.addItem(entry.getValue() + " (ID: " + entry.getKey() + ")");
            }

            // Add labels and fields
            formPanel.add(createFormLabel("Officer Name *:"));
            formPanel.add(nameField);
            formPanel.add(createFormLabel("Identifier *:"));
            formPanel.add(identifierField);
            formPanel.add(createFormLabel("Department *:"));
            formPanel.add(departmentCombo);
            formPanel.add(createFormLabel("Contact:"));
            formPanel.add(contactField);
            formPanel.add(createFormLabel("Location:"));
            formPanel.add(locationField);
            formPanel.add(createFormLabel("Status:"));
            formPanel.add(statusCombo);

            int option = JOptionPane.showConfirmDialog(this, formPanel, 
                "➕ Add New Officer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                // Validate required fields
                if (nameField.getText().trim().isEmpty() || identifierField.getText().trim().isEmpty()) {
                    showError("Officer Name and Identifier are required fields!");
                    return;
                }

                // Check if identifier already exists
                if (isIdentifierExists(identifierField.getText().trim())) {
                    showError("Identifier '" + identifierField.getText().trim() + "' already exists in the system!");
                    return;
                }

                // Extract department ID from combo selection
                String selectedDept = (String) departmentCombo.getSelectedItem();
                Long departmentId = extractDepartmentId(selectedDept);

                String sql = "INSERT INTO officer (Name, Identifier, DepartmentID, Contact, Location, Status, AssignedSince) " +
                            "VALUES (?, ?, ?, ?, ?, ?, CURDATE())";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, nameField.getText().trim());
                pstmt.setString(2, identifierField.getText().trim());
                pstmt.setLong(3, departmentId);
                pstmt.setString(4, contactField.getText().trim());
                pstmt.setString(5, locationField.getText().trim());
                pstmt.setString(6, (String) statusCombo.getSelectedItem());

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    showSuccess("Officer '" + nameField.getText().trim() + "' added successfully!");
                    loadOfficers();
                }
            }
        } catch (SQLException e) {
            showError("Error adding officer: " + e.getMessage());
        }
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(textPrimaryColor);
        return label;
    }

    private void editOfficer() {
        int row = officerTable.getSelectedRow();
        if (row == -1) {
            showWarning("Please select an officer to edit.");
            return;
        }

        try {
            int modelRow = officerTable.convertRowIndexToModel(row);
            Long officerId = (Long) tableModel.getValueAt(modelRow, 0);
            String currentIdentifier = ((String) tableModel.getValueAt(modelRow, 2));

            // Get current officer data
            String sql = "SELECT * FROM officer WHERE OfficerID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, officerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Get departments for dropdown
                Map<Long, String> departments = getDepartmentsMap();
                if (departments.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "No departments found.", 
                        "No Departments", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Create form panel
                JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
                formPanel.setBackground(cardBgColor);
                formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JTextField nameField = new JTextField(rs.getString("Name"));
                JTextField identifierField = new JTextField(rs.getString("Identifier"));
                JComboBox<String> departmentCombo = new JComboBox<>();
                JTextField contactField = new JTextField(rs.getString("Contact"));
                JTextField locationField = new JTextField(rs.getString("Location"));
                JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Inactive", "On Leave"});

                // Populate department combo and set current selection
                Long currentDeptId = rs.getLong("DepartmentID");
                String currentSelection = "";
                for (Map.Entry<Long, String> entry : departments.entrySet()) {
                    String item = entry.getValue() + " (ID: " + entry.getKey() + ")";
                    departmentCombo.addItem(item);
                    if (entry.getKey().equals(currentDeptId)) {
                        currentSelection = item;
                    }
                }
                departmentCombo.setSelectedItem(currentSelection);
                statusCombo.setSelectedItem(rs.getString("Status"));

                // Add labels and fields
                formPanel.add(createFormLabel("Officer Name *:"));
                formPanel.add(nameField);
                formPanel.add(createFormLabel("Identifier *:"));
                formPanel.add(identifierField);
                formPanel.add(createFormLabel("Department *:"));
                formPanel.add(departmentCombo);
                formPanel.add(createFormLabel("Contact:"));
                formPanel.add(contactField);
                formPanel.add(createFormLabel("Location:"));
                formPanel.add(locationField);
                formPanel.add(createFormLabel("Status:"));
                formPanel.add(statusCombo);

                int option = JOptionPane.showConfirmDialog(this, formPanel, 
                    "✏️ Edit Officer: " + nameField.getText(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    // Validate required fields
                    if (nameField.getText().trim().isEmpty() || identifierField.getText().trim().isEmpty()) {
                        showError("Officer Name and Identifier are required fields!");
                        return;
                    }

                    // Check if identifier already exists (excluding current officer)
                    String newIdentifier = identifierField.getText().trim();
                    if (!newIdentifier.equals(currentIdentifier) && isIdentifierExists(newIdentifier)) {
                        showError("Identifier '" + newIdentifier + "' already exists in the system!");
                        return;
                    }

                    // Extract department ID from combo selection
                    String selectedDept = (String) departmentCombo.getSelectedItem();
                    Long departmentId = extractDepartmentId(selectedDept);

                    String updateSql = "UPDATE officer SET Name=?, Identifier=?, DepartmentID=?, Contact=?, " +
                                     "Location=?, Status=? WHERE OfficerID=?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                    updateStmt.setString(1, nameField.getText().trim());
                    updateStmt.setString(2, newIdentifier);
                    updateStmt.setLong(3, departmentId);
                    updateStmt.setString(4, contactField.getText().trim());
                    updateStmt.setString(5, locationField.getText().trim());
                    updateStmt.setString(6, (String) statusCombo.getSelectedItem());
                    updateStmt.setLong(7, officerId);

                    int affectedRows = updateStmt.executeUpdate();
                    if (affectedRows > 0) {
                        showSuccess("Officer '" + nameField.getText().trim() + "' updated successfully!");
                        loadOfficers();
                    }
                }
            }
        } catch (SQLException e) {
            showError("Error editing officer: " + e.getMessage());
        }
    }

    private void deleteOfficer() {
        int row = officerTable.getSelectedRow();
        if (row == -1) {
            showWarning("Please select an officer to delete.");
            return;
        }

        int modelRow = officerTable.convertRowIndexToModel(row);
        Long officerId = (Long) tableModel.getValueAt(modelRow, 0);
        String officerName = (String) tableModel.getValueAt(modelRow, 1);
        String identifier = (String) tableModel.getValueAt(modelRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
            "<html><b>Are you sure you want to delete officer?</b><br><br>" +
            "Name: <b>" + officerName + "</b><br>" +
            "Identifier: " + identifier + "<br>" +
            "ID: " + officerId + "<br><br>" +
            "This action cannot be undone!</html>",
            "🗑️ Confirm Delete Officer",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Check if officer has assigned cases
                if (hasAssignedCases(officerId)) {
                    showWarning("Cannot delete officer '" + officerName + "'. " +
                               "There are cases assigned to this officer. " +
                               "Please reassign or close the cases first.");
                    return;
                }

                // Check if officer has user account
                if (hasUserAccount(officerId)) {
                    showWarning("Cannot delete officer '" + officerName + "'. " +
                               "There is a user account associated with this officer. " +
                               "Please delete the user account first.");
                    return;
                }

                String sql = "DELETE FROM officer WHERE OfficerID = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setLong(1, officerId);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    showSuccess("Officer '" + officerName + "' deleted successfully!");
                    loadOfficers();
                }
            } catch (SQLException e) {
                showError("Error deleting officer: " + e.getMessage());
            }
        }
    }

    private boolean isIdentifierExists(String identifier) throws SQLException {
        String sql = "SELECT COUNT(*) FROM officer WHERE Identifier = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, identifier);
        ResultSet rs = stmt.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    private boolean hasAssignedCases(Long officerId) throws SQLException {
        try {
            String sql = "SELECT COUNT(*) FROM officercase WHERE OfficerID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, officerId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            // Table might not exist, ignore
            return false;
        }
    }

    private boolean hasUserAccount(Long officerId) throws SQLException {
        try {
            String sql = "SELECT COUNT(*) FROM useraccount WHERE OfficerID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, officerId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            // Table might not exist, ignore
            return false;
        }
    }

    private Map<Long, String> getDepartmentsMap() throws SQLException {
        Map<Long, String> departments = new HashMap<>();
        String sql = "SELECT DepartmentID, Name FROM department ORDER BY Name";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            departments.put(rs.getLong("DepartmentID"), rs.getString("Name"));
        }
        return departments;
    }

    private Long extractDepartmentId(String departmentString) {
        try {
            // Extract ID from format: "Department Name (ID: 123)"
            String idPart = departmentString.substring(departmentString.lastIndexOf("ID: ") + 4);
            idPart = idPart.replace(")", "").trim();
            return Long.parseLong(idPart);
        } catch (Exception e) {
            return 0L;
        }
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
        loadOfficers();
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
        if (officerTable != null) {
            officerTable.setBackground(cardBgColor);
            officerTable.setForeground(textPrimaryColor);
            officerTable.getTableHeader().setBackground(new Color(240, 240, 240));
            officerTable.getTableHeader().setForeground(textPrimaryColor);
        }
        
        repaint();
    }
}