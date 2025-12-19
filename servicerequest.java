package GOVTECHFORM;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class servicerequest extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton addBtn, editBtn, deleteBtn, refreshBtn;

    public servicerequest() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Service Request Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Table setup
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        tableModel.addColumn("ID");
        tableModel.addColumn("CitizenID");
        tableModel.addColumn("DepartmentID");
        tableModel.addColumn("RequestType");
        tableModel.addColumn("Description");
        tableModel.addColumn("Status");
        tableModel.addColumn("CreatedAt");

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        addBtn = new JButton("Add");
        editBtn = new JButton("Edit");
        deleteBtn = new JButton("Delete");
        refreshBtn = new JButton("Refresh");
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        loadServiceRequests();

        addBtn.addActionListener(e -> addServiceRequest());
        editBtn.addActionListener(e -> editServiceRequest());
        deleteBtn.addActionListener(e -> deleteServiceRequest());
        refreshBtn.addActionListener(e -> loadServiceRequests());
    }

    private void loadServiceRequests() {
        try (Connection conn = DB.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ServiceRequest ORDER BY ServiceRequestID");
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getLong("ServiceRequestID"),
                        rs.getLong("CitizenID"),
                        rs.getLong("DepartmentID"),
                        rs.getString("RequestType"),
                        rs.getString("Description"),
                        rs.getString("Status"),
                        rs.getTimestamp("CreatedAt")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading service requests: " + e.getMessage());
        }
    }

    private void addServiceRequest() {
        JTextField citizenIdField = new JTextField();
        JTextField departmentIdField = new JTextField();
        JTextField typeField = new JTextField();
        JTextArea descField = new JTextArea(3, 20);

        Object[] fields = {
                "Citizen ID:", citizenIdField,
                "Department ID:", departmentIdField,
                "Request Type:", typeField,
                "Description:", new JScrollPane(descField)
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Add Service Request", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = DB.getConnection()) {
                String sql = "INSERT INTO ServiceRequest (CitizenID, DepartmentID, RequestType, Description) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setLong(1, Long.parseLong(citizenIdField.getText()));
                ps.setLong(2, Long.parseLong(departmentIdField.getText()));
                ps.setString(3, typeField.getText());
                ps.setString(4, descField.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Service Request added successfully.");
                loadServiceRequests();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding service request: " + ex.getMessage());
            }
        }
    }

    private void editServiceRequest() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a service request to edit.");
            return;
        }
        Long id = (Long) tableModel.getValueAt(row, 0);

        JTextField citizenIdField = new JTextField(tableModel.getValueAt(row, 1).toString());
        JTextField departmentIdField = new JTextField(tableModel.getValueAt(row, 2).toString());
        JTextField typeField = new JTextField(tableModel.getValueAt(row, 3).toString());
        JTextArea descField = new JTextArea(tableModel.getValueAt(row, 4).toString(), 3, 20);

        Object[] fields = {
                "Citizen ID:", citizenIdField,
                "Department ID:", departmentIdField,
                "Request Type:", typeField,
                "Description:", new JScrollPane(descField)
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Edit Service Request", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = DB.getConnection()) {
                String sql = "UPDATE ServiceRequest SET CitizenID=?, DepartmentID=?, RequestType=?, Description=? WHERE ServiceRequestID=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setLong(1, Long.parseLong(citizenIdField.getText()));
                ps.setLong(2, Long.parseLong(departmentIdField.getText()));
                ps.setString(3, typeField.getText());
                ps.setString(4, descField.getText());
                ps.setLong(5, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Service Request updated successfully.");
                loadServiceRequests();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error updating service request: " + ex.getMessage());
            }
        }
    }

    private void deleteServiceRequest() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a service request to delete.");
            return;
        }
        Long id = (Long) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this request?");
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DB.getConnection()) {
                String sql = "DELETE FROM ServiceRequest WHERE ServiceRequestID=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setLong(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Service Request deleted successfully.");
                loadServiceRequests();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting service request: " + ex.getMessage());
            }
        }
    }
}
