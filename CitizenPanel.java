package GOVTECHFORM;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class CitizenPanel extends JPanel {

    private JTable citizenTable;
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

    public CitizenPanel() {
        initializeDatabase();
        setLayout(new BorderLayout());
        initializeUI();
        loadCitizens();
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

        JLabel titleLabel = new JLabel("👨‍👩‍👧‍👦 Citizen Management");
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
        searchField.setToolTipText("Search citizens by name, national ID, address, or contact");
        
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

        addBtn = createStyledButton("➕ Add Citizen", new Color(46, 204, 113));
        editBtn = createStyledButton("✏️ Edit Citizen", new Color(52, 152, 219));
        deleteBtn = createStyledButton("🗑️ Delete Citizen", new Color(231, 76, 60));
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
                if (columnIndex == 0) return Long.class; // CitizenID
                return String.class;
            }
        };
        
        // Add columns based on your database structure
        String[] columns = {"ID", "Full Name", "National ID", "Address", "Contact", "Registered Date"};
        for (String column : columns) {
            tableModel.addColumn(column);
        }

        citizenTable = new JTable(tableModel);
        citizenTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        citizenTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        citizenTable.getTableHeader().setBackground(new Color(240, 240, 240));
        citizenTable.getTableHeader().setForeground(textPrimaryColor);
        citizenTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        citizenTable.setRowHeight(30);
        citizenTable.setIntercellSpacing(new Dimension(0, 1));
        citizenTable.setShowGrid(true);
        citizenTable.setGridColor(new Color(240, 240, 240));
        citizenTable.setBackground(cardBgColor);
        citizenTable.setForeground(textPrimaryColor);
        citizenTable.setSelectionBackground(new Color(220, 240, 255));
        citizenTable.setSelectionForeground(textPrimaryColor);

        // Enable sorting
        citizenTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(citizenTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(cardBgColor);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        // Status Panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(cardBgColor);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        statusLabel = new JLabel("Ready - Select a citizen to manage");
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
        addBtn.addActionListener(e -> addCitizen());
        editBtn.addActionListener(e -> editCitizen());
        deleteBtn.addActionListener(e -> deleteCitizen());
        refreshBtn.addActionListener(e -> refreshData());
        
        searchBtn.addActionListener(e -> searchCitizens());
        searchField.addActionListener(e -> searchCitizens());
        
        // Clear search button
        Component[] searchComps = ((JPanel) getComponent(1)).getComponents();
        for (Component comp : searchComps) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals("Clear")) {
                ((JButton) comp).addActionListener(e -> {
                    searchField.setText("");
                    loadCitizens();
                });
            }
        }

        // Double-click to edit
        citizenTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editCitizen();
                }
            }
        });

        // Selection listener to update status
        citizenTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = citizenTable.getSelectedRow();
                if (row != -1) {
                    String citizenName = (String) tableModel.getValueAt(
                        citizenTable.convertRowIndexToModel(row), 1);
                    updateStatus("Selected: " + citizenName);
                }
            }
        });
    }

    public void setBackButtonListener(java.awt.event.ActionListener listener) {
        backBtn.addActionListener(listener);
    }

    private void loadCitizens() {
        try {
            String sql = "SELECT * FROM citizen ORDER BY CitizenID";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            tableModel.setRowCount(0);
            int rowCount = 0;
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            while (rs.next()) {
                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                String createdDate = (createdAt != null) ? dateFormat.format(createdAt) : "N/A";
                
                tableModel.addRow(new Object[]{
                    rs.getLong("CitizenID"),
                    rs.getString("FullName"),
                    formatNationalId(rs.getString("NationalID")),
                    rs.getString("Address"),
                    formatContact(rs.getString("Contact")),
                    createdDate
                });
                rowCount++;
            }
            
            updateStatus("Loaded " + rowCount + " citizens");
            updateStatistics();
            
        } catch (SQLException e) {
            showError("Error loading citizens: " + e.getMessage());
        }
    }

    private String formatNationalId(String nationalId) {
        if (nationalId == null || nationalId.length() < 4) return nationalId;
        // Format for better readability: XXXX-XXXX-XXXX-XXXX
        return nationalId.replaceAll("(.{4})(?=.)", "$1-");
    }

    private String formatContact(String contact) {
        if (contact == null) return "N/A";
        if (contact.startsWith("+250")) {
            // Format Rwandan phone numbers: +250 XXX XXX XXX
            return contact.replaceAll("(\\+250)(\\d{3})(\\d{3})(\\d{3})", "$1 $2 $3 $4");
        }
        return contact;
    }

    private void updateStatistics() {
        // Update statistics in status panel
        SwingUtilities.invokeLater(() -> {
            Component[] comps = ((JPanel) getComponent(3)).getComponents();
            for (Component comp : comps) {
                if (comp instanceof JLabel && ((JLabel) comp).getText().contains("Citizens:")) {
                    ((JLabel) comp).setText("Citizens: " + tableModel.getRowCount());
                    break;
                }
            }
        });
    }

    private void updateStatistics(JLabel statsLabel) {
        statsLabel.setText("Citizens: " + tableModel.getRowCount());
    }

    private void searchCitizens() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadCitizens();
            return;
        }

        try {
            String sql = "SELECT * FROM citizen WHERE FullName LIKE ? OR NationalID LIKE ? OR Address LIKE ? OR Contact LIKE ? ORDER BY CitizenID";
            PreparedStatement stmt = conn.prepareStatement(sql);
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            stmt.setString(3, likeTerm);
            stmt.setString(4, likeTerm);
            
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);
            int rowCount = 0;
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            while (rs.next()) {
                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                String createdDate = (createdAt != null) ? dateFormat.format(createdAt) : "N/A";
                
                tableModel.addRow(new Object[]{
                    rs.getLong("CitizenID"),
                    rs.getString("FullName"),
                    formatNationalId(rs.getString("NationalID")),
                    rs.getString("Address"),
                    formatContact(rs.getString("Contact")),
                    createdDate
                });
                rowCount++;
            }
            
            updateStatus("Found " + rowCount + " citizens matching '" + searchTerm + "'");
            
        } catch (SQLException e) {
            showError("Error searching citizens: " + e.getMessage());
        }
    }

    private void addCitizen() {
        // Create form panel with better styling
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBackground(cardBgColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField fullNameField = new JTextField();
        JTextField nationalIdField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField contactField = new JTextField();

        // Add labels and fields
        formPanel.add(createFormLabel("Full Name *:"));
        formPanel.add(fullNameField);
        formPanel.add(createFormLabel("National ID *:"));
        formPanel.add(nationalIdField);
        formPanel.add(createFormLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(createFormLabel("Contact:"));
        formPanel.add(contactField);

        int option = JOptionPane.showConfirmDialog(this, formPanel, 
            "➕ Add New Citizen", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            try {
                // Validate required fields
                if (fullNameField.getText().trim().isEmpty()) {
                    showError("Full Name is required!");
                    return;
                }
                if (nationalIdField.getText().trim().isEmpty()) {
                    showError("National ID is required!");
                    return;
                }

                // Check if National ID already exists
                if (isNationalIdExists(nationalIdField.getText().trim())) {
                    showError("National ID '" + nationalIdField.getText().trim() + "' already exists in the system!");
                    return;
                }

                String sql = "INSERT INTO citizen (FullName, NationalID, Address, Contact) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, fullNameField.getText().trim());
                ps.setString(2, nationalIdField.getText().trim());
                
                // Handle address - set to NULL if empty
                if (addressField.getText().trim().isEmpty()) {
                    ps.setNull(3, Types.VARCHAR);
                } else {
                    ps.setString(3, addressField.getText().trim());
                }
                
                // Handle contact - set to NULL if empty
                if (contactField.getText().trim().isEmpty()) {
                    ps.setNull(4, Types.VARCHAR);
                } else {
                    ps.setString(4, contactField.getText().trim());
                }
                
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    showSuccess("Citizen '" + fullNameField.getText().trim() + "' added successfully!");
                    loadCitizens();
                }
            } catch (SQLException e) {
                showError("Error adding citizen: " + e.getMessage());
            }
        }
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(textPrimaryColor);
        return label;
    }

    private void editCitizen() {
        int row = citizenTable.getSelectedRow();
        if (row == -1) {
            showWarning("Please select a citizen to edit.");
            return;
        }

        int modelRow = citizenTable.convertRowIndexToModel(row);
        Long citizenId = (Long) tableModel.getValueAt(modelRow, 0);
        String currentNationalId = ((String) tableModel.getValueAt(modelRow, 2)).replace("-", "");

        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBackground(cardBgColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField fullNameField = new JTextField((String) tableModel.getValueAt(modelRow, 1));
        JTextField nationalIdField = new JTextField(currentNationalId);
        JTextField addressField = new JTextField(tableModel.getValueAt(modelRow, 3) != null ? 
            tableModel.getValueAt(modelRow, 3).toString() : "");
        JTextField contactField = new JTextField(tableModel.getValueAt(modelRow, 4) != null ? 
            tableModel.getValueAt(modelRow, 4).toString().replace(" ", "") : "");

        formPanel.add(createFormLabel("Full Name *:"));
        formPanel.add(fullNameField);
        formPanel.add(createFormLabel("National ID *:"));
        formPanel.add(nationalIdField);
        formPanel.add(createFormLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(createFormLabel("Contact:"));
        formPanel.add(contactField);

        int option = JOptionPane.showConfirmDialog(this, formPanel, 
            "✏️ Edit Citizen: " + fullNameField.getText(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            try {
                // Validate required fields
                if (fullNameField.getText().trim().isEmpty()) {
                    showError("Full Name is required!");
                    return;
                }
                if (nationalIdField.getText().trim().isEmpty()) {
                    showError("National ID is required!");
                    return;
                }

                // Check if National ID already exists (excluding current citizen)
                String newNationalId = nationalIdField.getText().trim();
                if (!newNationalId.equals(currentNationalId) && isNationalIdExists(newNationalId)) {
                    showError("National ID '" + newNationalId + "' already exists in the system!");
                    return;
                }

                String sql = "UPDATE citizen SET FullName=?, NationalID=?, Address=?, Contact=? WHERE CitizenID=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, fullNameField.getText().trim());
                ps.setString(2, newNationalId);
                
                // Handle address - set to NULL if empty
                if (addressField.getText().trim().isEmpty()) {
                    ps.setNull(3, Types.VARCHAR);
                } else {
                    ps.setString(3, addressField.getText().trim());
                }
                
                // Handle contact - set to NULL if empty
                if (contactField.getText().trim().isEmpty()) {
                    ps.setNull(4, Types.VARCHAR);
                } else {
                    ps.setString(4, contactField.getText().trim());
                }
                
                ps.setLong(5, citizenId);
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    showSuccess("Citizen '" + fullNameField.getText().trim() + "' updated successfully!");
                    loadCitizens();
                }
            } catch (SQLException e) {
                showError("Error updating citizen: " + e.getMessage());
            }
        }
    }

    private void deleteCitizen() {
        int row = citizenTable.getSelectedRow();
        if (row == -1) {
            showWarning("Please select a citizen to delete.");
            return;
        }

        int modelRow = citizenTable.convertRowIndexToModel(row);
        Long citizenId = (Long) tableModel.getValueAt(modelRow, 0);
        String citizenName = (String) tableModel.getValueAt(modelRow, 1);
        String nationalId = (String) tableModel.getValueAt(modelRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
            "<html><b>Are you sure you want to delete citizen?</b><br><br>" +
            "Name: <b>" + citizenName + "</b><br>" +
            "National ID: " + nationalId + "<br>" +
            "ID: " + citizenId + "<br><br>" +
            "This action cannot be undone!</html>",
            "🗑️ Confirm Delete Citizen",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Check if citizen has related records
                if (hasRelatedRecords(citizenId)) {
                    showWarning("Cannot delete citizen '" + citizenName + "'. " +
                               "There are related records (cases, service requests) in the system. " +
                               "Please delete the related records first.");
                    return;
                }

                String sql = "DELETE FROM citizen WHERE CitizenID=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setLong(1, citizenId);
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    showSuccess("Citizen '" + citizenName + "' deleted successfully!");
                    loadCitizens();
                }
            } catch (SQLException e) {
                showError("Error deleting citizen: " + e.getMessage());
            }
        }
    }

    private boolean isNationalIdExists(String nationalId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM citizen WHERE NationalID = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, nationalId);
        ResultSet rs = stmt.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    private boolean hasRelatedRecords(Long citizenId) throws SQLException {
        // Check if citizen has cases
        try {
            String sql = "SELECT COUNT(*) FROM casetable WHERE CitizenID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, citizenId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            // Table might not exist, ignore
        }

        // Check if citizen has service requests
        try {
            String sql = "SELECT COUNT(*) FROM servicerequest WHERE CitizenID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, citizenId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            // Table might not exist, ignore
        }

        // Check if citizen has user account
        try {
            String sql = "SELECT COUNT(*) FROM useraccount WHERE CitizenID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, citizenId);
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
        loadCitizens();
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
        if (citizenTable != null) {
            citizenTable.setBackground(cardBgColor);
            citizenTable.setForeground(textPrimaryColor);
            citizenTable.getTableHeader().setBackground(new Color(240, 240, 240));
            citizenTable.getTableHeader().setForeground(textPrimaryColor);
        }
        
        repaint();
    }
}