package GOVTECHFORM;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DepartmentPanel extends JPanel {

    private JTable departmentTable;
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

    public DepartmentPanel() {
        initializeDatabase();
        setLayout(new BorderLayout());
        initializeUI();
        loadDepartments();
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

        JLabel titleLabel = new JLabel("🏢 Department Management");
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
        searchField.setToolTipText("Search departments by name, manager, or address");
        
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

        addBtn = createStyledButton("➕ Add Department", new Color(46, 204, 113));
        editBtn = createStyledButton("✏️ Edit Department", new Color(52, 152, 219));
        deleteBtn = createStyledButton("🗑️ Delete Department", new Color(231, 76, 60));
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
                if (columnIndex == 0) return Long.class; // DepartmentID
                if (columnIndex == 3) return Integer.class; // Capacity
                return String.class;
            }
        };
        
        // Add columns based on your database structure
        String[] columns = {"ID", "Department Name", "Address", "Capacity", "Manager", "Contact"};
        for (String column : columns) {
            tableModel.addColumn(column);
        }

        departmentTable = new JTable(tableModel);
        departmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        departmentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        departmentTable.getTableHeader().setBackground(new Color(240, 240, 240));
        departmentTable.getTableHeader().setForeground(textPrimaryColor);
        departmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        departmentTable.setRowHeight(30);
        departmentTable.setIntercellSpacing(new Dimension(0, 1));
        departmentTable.setShowGrid(true);
        departmentTable.setGridColor(new Color(240, 240, 240));
        departmentTable.setBackground(cardBgColor);
        departmentTable.setForeground(textPrimaryColor);
        departmentTable.setSelectionBackground(new Color(220, 240, 255));
        departmentTable.setSelectionForeground(textPrimaryColor);

        // Enable sorting
        departmentTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(departmentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(cardBgColor);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        // Status Panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(cardBgColor);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        statusLabel = new JLabel("Ready - Select a department to manage");
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
        addBtn.addActionListener(e -> addDepartment());
        editBtn.addActionListener(e -> editDepartment());
        deleteBtn.addActionListener(e -> deleteDepartment());
        refreshBtn.addActionListener(e -> refreshData());
        
        searchBtn.addActionListener(e -> searchDepartments());
        searchField.addActionListener(e -> searchDepartments());
        
        // Clear search button
        Component[] searchComps = ((JPanel) getComponent(1)).getComponents();
        for (Component comp : searchComps) {
            if (comp instanceof JButton && ((JButton) comp).getText().equals("Clear")) {
                ((JButton) comp).addActionListener(e -> {
                    searchField.setText("");
                    loadDepartments();
                });
            }
        }

        // Double-click to edit
        departmentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editDepartment();
                }
            }
        });

        // Selection listener to update status
        departmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = departmentTable.getSelectedRow();
                if (row != -1) {
                    String deptName = (String) tableModel.getValueAt(
                        departmentTable.convertRowIndexToModel(row), 1);
                    updateStatus("Selected: " + deptName);
                }
            }
        });
    }

    public void setBackButtonListener(java.awt.event.ActionListener listener) {
        backBtn.addActionListener(listener);
    }

    private void loadDepartments() {
        try {
            String sql = "SELECT * FROM department ORDER BY DepartmentID";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            tableModel.setRowCount(0);
            int rowCount = 0;
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getLong("DepartmentID"),
                    rs.getString("Name"),
                    rs.getString("Address"),
                    rs.getInt("Capacity"),
                    rs.getString("Manager"),
                    rs.getString("Contact")
                });
                rowCount++;
            }
            
            updateStatus("Loaded " + rowCount + " departments");
            updateStatistics();
            
        } catch (SQLException e) {
            showError("Error loading departments: " + e.getMessage());
        }
    }

    private void updateStatistics() {
        // Update statistics in status panel
        SwingUtilities.invokeLater(() -> {
            Component[] comps = ((JPanel) getComponent(3)).getComponents();
            for (Component comp : comps) {
                if (comp instanceof JLabel && ((JLabel) comp).getText().contains("Departments:")) {
                    ((JLabel) comp).setText("Departments: " + tableModel.getRowCount());
                    break;
                }
            }
        });
    }

    private void updateStatistics(JLabel statsLabel) {
        statsLabel.setText("Departments: " + tableModel.getRowCount());
    }

    private void searchDepartments() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadDepartments();
            return;
        }

        try {
            String sql = "SELECT * FROM department WHERE Name LIKE ? OR Manager LIKE ? OR Address LIKE ? ORDER BY DepartmentID";
            PreparedStatement stmt = conn.prepareStatement(sql);
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            stmt.setString(3, likeTerm);
            
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);
            int rowCount = 0;
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getLong("DepartmentID"),
                    rs.getString("Name"),
                    rs.getString("Address"),
                    rs.getInt("Capacity"),
                    rs.getString("Manager"),
                    rs.getString("Contact")
                });
                rowCount++;
            }
            
            updateStatus("Found " + rowCount + " departments matching '" + searchTerm + "'");
            
        } catch (SQLException e) {
            showError("Error searching departments: " + e.getMessage());
        }
    }

    private void addDepartment() {
        // Create form panel with better styling
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBackground(cardBgColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField capacityField = new JTextField();
        JTextField managerField = new JTextField();
        JTextField contactField = new JTextField();

        // Add labels and fields
        formPanel.add(createFormLabel("Department Name *:"));
        formPanel.add(nameField);
        formPanel.add(createFormLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(createFormLabel("Capacity:"));
        formPanel.add(capacityField);
        formPanel.add(createFormLabel("Manager:"));
        formPanel.add(managerField);
        formPanel.add(createFormLabel("Contact:"));
        formPanel.add(contactField);

        int option = JOptionPane.showConfirmDialog(this, formPanel, 
            "➕ Add New Department", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            try {
                // Validate required fields
                if (nameField.getText().trim().isEmpty()) {
                    showError("Department Name is required!");
                    return;
                }

                // Validate capacity is a number if provided
                if (!capacityField.getText().trim().isEmpty()) {
                    try {
                        Integer.parseInt(capacityField.getText().trim());
                    } catch (NumberFormatException e) {
                        showError("Capacity must be a valid number!");
                        return;
                    }
                }

                String sql = "INSERT INTO department (Name, Address, Capacity, Manager, Contact) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, nameField.getText().trim());
                ps.setString(2, addressField.getText().trim());
                
                // Handle capacity - set to NULL if empty
                if (capacityField.getText().trim().isEmpty()) {
                    ps.setNull(3, Types.INTEGER);
                } else {
                    ps.setInt(3, Integer.parseInt(capacityField.getText().trim()));
                }
                
                // Handle manager - set to NULL if empty
                if (managerField.getText().trim().isEmpty()) {
                    ps.setNull(4, Types.VARCHAR);
                } else {
                    ps.setString(4, managerField.getText().trim());
                }
                
                // Handle contact - set to NULL if empty
                if (contactField.getText().trim().isEmpty()) {
                    ps.setNull(5, Types.VARCHAR);
                } else {
                    ps.setString(5, contactField.getText().trim());
                }
                
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    showSuccess("Department '" + nameField.getText().trim() + "' added successfully!");
                    loadDepartments();
                }
            } catch (SQLException e) {
                showError("Error adding department: " + e.getMessage());
            }
        }
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(textPrimaryColor);
        return label;
    }

    private void editDepartment() {
        int row = departmentTable.getSelectedRow();
        if (row == -1) {
            showWarning("Please select a department to edit.");
            return;
        }

        int modelRow = departmentTable.convertRowIndexToModel(row);
        Long id = (Long) tableModel.getValueAt(modelRow, 0);
        String currentName = (String) tableModel.getValueAt(modelRow, 1);

        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBackground(cardBgColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField((String) tableModel.getValueAt(modelRow, 1));
        JTextField addressField = new JTextField(tableModel.getValueAt(modelRow, 2) != null ? tableModel.getValueAt(modelRow, 2).toString() : "");
        JTextField capacityField = new JTextField(tableModel.getValueAt(modelRow, 3) != null ? tableModel.getValueAt(modelRow, 3).toString() : "");
        JTextField managerField = new JTextField(tableModel.getValueAt(modelRow, 4) != null ? tableModel.getValueAt(modelRow, 4).toString() : "");
        JTextField contactField = new JTextField(tableModel.getValueAt(modelRow, 5) != null ? tableModel.getValueAt(modelRow, 5).toString() : "");

        formPanel.add(createFormLabel("Department Name *:"));
        formPanel.add(nameField);
        formPanel.add(createFormLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(createFormLabel("Capacity:"));
        formPanel.add(capacityField);
        formPanel.add(createFormLabel("Manager:"));
        formPanel.add(managerField);
        formPanel.add(createFormLabel("Contact:"));
        formPanel.add(contactField);

        int option = JOptionPane.showConfirmDialog(this, formPanel, 
            "✏️ Edit Department: " + currentName, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            try {
                // Validate required fields
                if (nameField.getText().trim().isEmpty()) {
                    showError("Department Name is required!");
                    return;
                }

                // Validate capacity is a number if provided
                if (!capacityField.getText().trim().isEmpty()) {
                    try {
                        Integer.parseInt(capacityField.getText().trim());
                    } catch (NumberFormatException e) {
                        showError("Capacity must be a valid number!");
                        return;
                    }
                }

                String sql = "UPDATE department SET Name=?, Address=?, Capacity=?, Manager=?, Contact=? WHERE DepartmentID=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, nameField.getText().trim());
                ps.setString(2, addressField.getText().trim());
                
                // Handle capacity - set to NULL if empty
                if (capacityField.getText().trim().isEmpty()) {
                    ps.setNull(3, Types.INTEGER);
                } else {
                    ps.setInt(3, Integer.parseInt(capacityField.getText().trim()));
                }
                
                // Handle manager - set to NULL if empty
                if (managerField.getText().trim().isEmpty()) {
                    ps.setNull(4, Types.VARCHAR);
                } else {
                    ps.setString(4, managerField.getText().trim());
                }
                
                // Handle contact - set to NULL if empty
                if (contactField.getText().trim().isEmpty()) {
                    ps.setNull(5, Types.VARCHAR);
                } else {
                    ps.setString(5, contactField.getText().trim());
                }
                
                ps.setLong(6, id);
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    showSuccess("Department '" + nameField.getText().trim() + "' updated successfully!");
                    loadDepartments();
                }
            } catch (SQLException e) {
                showError("Error updating department: " + e.getMessage());
            }
        }
    }

    private void deleteDepartment() {
        int row = departmentTable.getSelectedRow();
        if (row == -1) {
            showWarning("Please select a department to delete.");
            return;
        }

        int modelRow = departmentTable.convertRowIndexToModel(row);
        Long id = (Long) tableModel.getValueAt(modelRow, 0);
        String departmentName = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "<html><b>Are you sure you want to delete department?</b><br><br>" +
            "Department: <b>" + departmentName + "</b><br>" +
            "ID: " + id + "<br><br>" +
            "This action cannot be undone!</html>",
            "🗑️ Confirm Delete Department",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Check if department has assigned officers
                if (hasAssignedOfficers(id)) {
                    showWarning("Cannot delete department '" + departmentName + "'. " +
                               "There are officers assigned to this department. " +
                               "Please reassign or remove the officers first.");
                    return;
                }

                String sql = "DELETE FROM department WHERE DepartmentID=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setLong(1, id);
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    showSuccess("Department '" + departmentName + "' deleted successfully!");
                    loadDepartments();
                }
            } catch (SQLException e) {
                showError("Error deleting department: " + e.getMessage());
            }
        }
    }

    private boolean hasAssignedOfficers(Long departmentId) throws SQLException {
        try {
            String sql = "SELECT COUNT(*) FROM officer WHERE DepartmentID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, departmentId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            // Table might not exist, ignore
            return false;
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
        loadDepartments();
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
        if (departmentTable != null) {
            departmentTable.setBackground(cardBgColor);
            departmentTable.setForeground(textPrimaryColor);
            departmentTable.getTableHeader().setBackground(new Color(240, 240, 240));
            departmentTable.getTableHeader().setForeground(textPrimaryColor);
        }
        
        repaint();
    }
}