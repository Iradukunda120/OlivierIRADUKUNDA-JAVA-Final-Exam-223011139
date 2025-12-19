package GOVTECHFORM;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CaseDocuments extends JFrame {
    private int officerId;
    private String username;
    private String officerName;
    
    // Theme variables
    private boolean darkMode = false;
    private final Color PRIMARY_COLOR_LIGHT = new Color(41, 128, 185);
    private final Color PRIMARY_COLOR_DARK = new Color(33, 47, 60);
    private final Color SIDEBAR_COLOR_LIGHT = new Color(44, 62, 80);
    private final Color SIDEBAR_COLOR_DARK = new Color(33, 47, 60);
    private final Color BACKGROUND_LIGHT = new Color(236, 240, 241);
    private final Color BACKGROUND_DARK = new Color(45, 45, 45);
    private final Color CARD_BG_LIGHT = Color.WHITE;
    private final Color CARD_BG_DARK = new Color(60, 60, 60);
    private final Color TEXT_PRIMARY_LIGHT = new Color(44, 62, 80);
    private final Color TEXT_PRIMARY_DARK = Color.WHITE;
    
    private Color primaryColor;
    private Color sidebarColor;
    private Color backgroundColor;
    private Color cardBgColor;
    private Color textPrimaryColor;
    
    // Main panels
    private JPanel mainPanel;
    private JTable documentsTable;
    private DefaultTableModel tableModel;
    
    // Document types
    private String[] documentTypes = {
        "Case File",
        "Citizen Identification",
        "Proof Document",
        "Certificate",
        "Report",
        "Approval Letter",
        "Payment Receipt",
        "Application Form",
        "Contract",
        "Photograph",
        "Other"
    };
    
    // File paths
    private String uploadsDirectory = "uploads/documents/";
    
    public CaseDocuments(int officerId, String username, String officerName) {
        this.officerId = officerId;
        this.username = username;
        this.officerName = officerName;
        
        initializeTheme();
        setupUI();
        loadDocuments();
        
        setVisible(true);
    }
    
    private void initializeTheme() {
        updateThemeColors();
    }
    
    private void updateThemeColors() {
        if (darkMode) {
            primaryColor = PRIMARY_COLOR_DARK;
            sidebarColor = SIDEBAR_COLOR_DARK;
            backgroundColor = BACKGROUND_DARK;
            cardBgColor = CARD_BG_DARK;
            textPrimaryColor = TEXT_PRIMARY_DARK;
        } else {
            primaryColor = PRIMARY_COLOR_LIGHT;
            sidebarColor = SIDEBAR_COLOR_LIGHT;
            backgroundColor = BACKGROUND_LIGHT;
            cardBgColor = CARD_BG_LIGHT;
            textPrimaryColor = TEXT_PRIMARY_LIGHT;
        }
    }
    
    private void setupUI() {
        setTitle("GovTech Solutions - Case Document Management");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create main panel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(backgroundColor);
        
        // Title
        JLabel titleLabel = new JLabel("📁 Case Document Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(textPrimaryColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Create documents table
        createDocumentsTable();
        JScrollPane scrollPane = new JScrollPane(documentsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(darkMode ? Color.DARK_GRAY : Color.LIGHT_GRAY));
        
        // Action buttons panel
        JPanel actionPanel = createActionPanel();
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Create uploads directory if it doesn't exist
        createUploadsDirectory();
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(primaryColor);
        header.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        header.setPreferredSize(new Dimension(getWidth(), 70));
        
        // Left side - Logo and title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(primaryColor);
        
        JLabel logoLabel = new JLabel("GovTech");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Document Management");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(200, 200, 200));
        
        leftPanel.add(logoLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        leftPanel.add(titleLabel);
        
        // Right side - Officer info and close button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(primaryColor);
        
        JLabel officerLabel = new JLabel("Officer: " + officerName);
        officerLabel.setForeground(Color.WHITE);
        officerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JButton closeBtn = new JButton("Close");
        styleCloseButton(closeBtn);
        closeBtn.addActionListener(e -> dispose());
        
        rightPanel.add(officerLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        rightPanel.add(closeBtn);
        
        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private void styleCloseButton(JButton button) {
        button.setBackground(new Color(231, 76, 60));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(192, 57, 43));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(231, 76, 60));
            }
        });
    }
    
    private void createDocumentsTable() {
        String[] columns = {"Document ID", "Case ID", "Document Name", "Type", "Upload Date", "Uploaded By", "Status", "File Size"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        documentsTable = new JTable(tableModel);
        documentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        documentsTable.setRowHeight(35);
        documentsTable.setBackground(cardBgColor);
        documentsTable.setForeground(textPrimaryColor);
        documentsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        documentsTable.getTableHeader().setBackground(primaryColor);
        documentsTable.getTableHeader().setForeground(Color.WHITE);
        
        // Add double-click listener to view document
        documentsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewDocumentDetails();
                }
            }
        });
    }
    
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JButton uploadBtn = createActionButton("📤 Upload Document", new Color(46, 204, 113));
        JButton viewBtn = createActionButton("👁️ View Details", new Color(52, 152, 219));
        JButton downloadBtn = createActionButton("⬇️ Download", new Color(155, 89, 182));
        JButton deleteBtn = createActionButton("🗑️ Delete", new Color(231, 76, 60));
        JButton refreshBtn = createActionButton("🔄 Refresh", new Color(241, 196, 15));
        JButton searchBtn = createActionButton("🔍 Search", new Color(52, 73, 94));
        
        uploadBtn.addActionListener(e -> uploadDocument());
        viewBtn.addActionListener(e -> viewDocumentDetails());
        downloadBtn.addActionListener(e -> downloadDocument());
        deleteBtn.addActionListener(e -> deleteDocument());
        refreshBtn.addActionListener(e -> loadDocuments());
        searchBtn.addActionListener(e -> searchDocuments());
        
        panel.add(uploadBtn);
        panel.add(viewBtn);
        panel.add(downloadBtn);
        panel.add(deleteBtn);
        panel.add(refreshBtn);
        panel.add(searchBtn);
        
        return panel;
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void createUploadsDirectory() {
        try {
            Path uploadPath = Paths.get(uploadsDirectory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created uploads directory: " + uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error creating uploads directory: " + e.getMessage());
        }
    }
    
    private void loadDocuments() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            
            // Create documents table if it doesn't exist
            createDocumentsTableIfNotExists(conn);
            
            String sql = "SELECT d.*, o.Name as OfficerName " +
                        "FROM documents d " +
                        "LEFT JOIN officer o ON d.UploadedBy = o.OfficerID " +
                        "WHERE d.UploadedBy = ? OR d.CaseID IN (SELECT CaseID FROM officercase WHERE OfficerID = ?) " +
                        "ORDER BY d.UploadDate DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, officerId);
            pstmt.setInt(2, officerId);
            rs = pstmt.executeQuery();
            
            tableModel.setRowCount(0);
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            while (rs.next()) {
                String fileSize = formatFileSize(rs.getLong("FileSize"));
                String status = rs.getString("Status");
                if (status == null) status = "Active";
                
                tableModel.addRow(new Object[]{
                    rs.getInt("DocumentID"),
                    rs.getInt("CaseID"),
                    rs.getString("DocumentName"),
                    rs.getString("DocumentType"),
                    sdf.format(rs.getTimestamp("UploadDate")),
                    rs.getString("OfficerName"),
                    status,
                    fileSize
                });
            }
            
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No documents found. Start by uploading a document.", 
                    "No Documents", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading documents: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private void createDocumentsTableIfNotExists(Connection conn) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS documents (" +
                               "DocumentID INT AUTO_INCREMENT PRIMARY KEY, " +
                               "CaseID INT NOT NULL, " +
                               "DocumentName VARCHAR(200) NOT NULL, " +
                               "DocumentType VARCHAR(50), " +
                               "Description TEXT, " +
                               "FileName VARCHAR(255), " +
                               "FilePath VARCHAR(500), " +
                               "FileSize BIGINT, " +
                               "UploadDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                               "UploadedBy INT, " +
                               "Status VARCHAR(20) DEFAULT 'Active', " +
                               "Version INT DEFAULT 1, " +
                               "FOREIGN KEY (CaseID) REFERENCES casetable(CaseID), " +
                               "FOREIGN KEY (UploadedBy) REFERENCES officer(OfficerID))";
        
        Statement stmt = conn.createStatement();
        stmt.execute(createTableSQL);
        
        // Also create document_versions table for version control
        String createVersionsTableSQL = "CREATE TABLE IF NOT EXISTS document_versions (" +
                                       "VersionID INT AUTO_INCREMENT PRIMARY KEY, " +
                                       "DocumentID INT, " +
                                       "VersionNumber INT, " +
                                       "FileName VARCHAR(255), " +
                                       "FilePath VARCHAR(500), " +
                                       "FileSize BIGINT, " +
                                       "UploadDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                       "UploadedBy INT, " +
                                       "ChangeDescription TEXT, " +
                                       "FOREIGN KEY (DocumentID) REFERENCES documents(DocumentID))";
        stmt.execute(createVersionsTableSQL);
    }
    
    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024.0));
        return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
    }
    
    private void uploadDocument() {
        // First, let the officer select a case
        Integer caseId = selectCaseForDocument();
        if (caseId == null) {
            return; // User cancelled
        }
        
        JDialog uploadDialog = new JDialog(this, "Upload Document", true);
        uploadDialog.setSize(600, 500);
        uploadDialog.setLocationRelativeTo(this);
        uploadDialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(cardBgColor);
        
        // Header
        JLabel headerLabel = new JLabel("Upload Document for Case #" + caseId);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(textPrimaryColor);
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(cardBgColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Document Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Document Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(25);
        formPanel.add(nameField, gbc);
        
        // Document Type
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Document Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> typeCombo = new JComboBox<>(documentTypes);
        formPanel.add(typeCombo, gbc);
        
        // Description
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        JTextArea descArea = new JTextArea(3, 25);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        formPanel.add(descScroll, gbc);
        
        // File selection
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Select File:"), gbc);
        gbc.gridx = 1;
        JPanel filePanel = new JPanel(new BorderLayout(5, 0));
        filePanel.setBackground(cardBgColor);
        JTextField filePathField = new JTextField();
        filePathField.setEditable(false);
        JButton browseBtn = new JButton("Browse...");
        browseBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Document to Upload");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            
            int result = fileChooser.showOpenDialog(uploadDialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
                
                // Auto-fill document name if empty
                if (nameField.getText().trim().isEmpty()) {
                    String fileName = selectedFile.getName();
                    // Remove extension for name
                    int dotIndex = fileName.lastIndexOf('.');
                    if (dotIndex > 0) {
                        fileName = fileName.substring(0, dotIndex);
                    }
                    nameField.setText(fileName);
                }
            }
        });
        
        filePanel.add(filePathField, BorderLayout.CENTER);
        filePanel.add(browseBtn, BorderLayout.EAST);
        formPanel.add(filePanel, gbc);
        
        // Status
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Archived", "Pending Review", "Approved"});
        formPanel.add(statusCombo, gbc);
        
        // Current file info
        JLabel fileInfoLabel = new JLabel("No file selected");
        fileInfoLabel.setForeground(new Color(100, 100, 100));
        fileInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        browseBtn.addActionListener(e -> {
            String filePath = filePathField.getText();
            if (!filePath.isEmpty()) {
                File file = new File(filePath);
                if (file.exists()) {
                    fileInfoLabel.setText("File: " + file.getName() + " | Size: " + formatFileSize(file.length()));
                }
            }
        });
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        formPanel.add(fileInfoLabel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(cardBgColor);
        
        JButton uploadBtn = createActionButton("Upload", new Color(46, 204, 113));
        JButton cancelBtn = createActionButton("Cancel", new Color(231, 76, 60));
        
        uploadBtn.addActionListener(e -> {
            String documentName = nameField.getText().trim();
            String documentType = (String) typeCombo.getSelectedItem();
            String description = descArea.getText().trim();
            String filePath = filePathField.getText();
            String status = (String) statusCombo.getSelectedItem();
            
            if (documentName.isEmpty()) {
                JOptionPane.showMessageDialog(uploadDialog, "Please enter a document name.", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (filePath.isEmpty()) {
                JOptionPane.showMessageDialog(uploadDialog, "Please select a file to upload.", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            File file = new File(filePath);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(uploadDialog, "Selected file does not exist.", 
                    "File Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Upload the file
            if (uploadDocumentToServer(caseId, documentName, documentType, description, 
                                       file, status, uploadDialog)) {
                JOptionPane.showMessageDialog(uploadDialog, "Document uploaded successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                uploadDialog.dispose();
                loadDocuments();
            }
        });
        
        cancelBtn.addActionListener(e -> uploadDialog.dispose());
        
        buttonPanel.add(uploadBtn);
        buttonPanel.add(cancelBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        uploadDialog.add(mainPanel);
        uploadDialog.setVisible(true);
    }
    
    private Integer selectCaseForDocument() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT ct.CaseID, ct.CaseType, ct.Description " +
                        "FROM casetable ct " +
                        "JOIN officercase oc ON ct.CaseID = oc.CaseID " +
                        "WHERE oc.OfficerID = ? " +
                        "ORDER BY ct.CaseID";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, officerId);
            rs = pstmt.executeQuery();
            
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Case ID", "Case Type", "Description"}, 0
            );
            
            while (rs.next()) {
                String description = rs.getString("Description");
                if (description != null && description.length() > 50) {
                    description = description.substring(0, 47) + "...";
                }
                
                model.addRow(new Object[]{
                    rs.getInt("CaseID"),
                    rs.getString("CaseType"),
                    description
                });
            }
            
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No cases assigned to you.", 
                    "No Cases", JOptionPane.INFORMATION_MESSAGE);
                return null;
            }
            
            JTable casesTable = new JTable(model);
            casesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(casesTable);
            
            int result = JOptionPane.showConfirmDialog(this, scrollPane, 
                "Select Case for Document Upload", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                int selectedRow = casesTable.getSelectedRow();
                if (selectedRow != -1) {
                    return (Integer) model.getValueAt(selectedRow, 0);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a case.", 
                        "Selection Required", JOptionPane.WARNING_MESSAGE);
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading cases: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
        
        return null;
    }
    
    private boolean uploadDocumentToServer(int caseId, String documentName, String documentType, 
                                          String description, File file, String status, JDialog dialog) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DB.getConnection();
            
            // Generate unique filename
            String originalFileName = file.getName();
            String fileExtension = "";
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = originalFileName.substring(dotIndex);
            }
            
            String uniqueFileName = "doc_" + System.currentTimeMillis() + "_" + officerId + fileExtension;
            String targetFilePath = uploadsDirectory + uniqueFileName;
            
            // Copy file to uploads directory
            Path sourcePath = file.toPath();
            Path targetPath = Paths.get(targetFilePath);
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Insert document record into database
            String sql = "INSERT INTO documents (CaseID, DocumentName, DocumentType, Description, " +
                        "FileName, FilePath, FileSize, UploadedBy, Status) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, caseId);
            pstmt.setString(2, documentName);
            pstmt.setString(3, documentType);
            pstmt.setString(4, description);
            pstmt.setString(5, originalFileName);
            pstmt.setString(6, targetFilePath);
            pstmt.setLong(7, file.length());
            pstmt.setInt(8, officerId);
            pstmt.setString(9, status);
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                // Log the action
                String logSql = "INSERT INTO system_logs (User, Action, Details) VALUES (?, ?, ?)";
                pstmt = conn.prepareStatement(logSql);
                pstmt.setString(1, username);
                pstmt.setString(2, "Document Uploaded");
                pstmt.setString(3, "Case #" + caseId + ": " + documentName + " (" + originalFileName + ")");
                pstmt.executeUpdate();
                return true;
            }
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(dialog, "Error copying file: " + e.getMessage(), 
                "File Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "Error saving document record: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DB.closeResources(null, pstmt, conn);
        }
        
        return false;
    }
    
    private void viewDocumentDetails() {
        int selectedRow = documentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a document to view details.", 
                "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int documentId = (int) tableModel.getValueAt(selectedRow, 0);
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT d.*, o.Name as OfficerName, ct.CaseType " +
                        "FROM documents d " +
                        "LEFT JOIN officer o ON d.UploadedBy = o.OfficerID " +
                        "LEFT JOIN casetable ct ON d.CaseID = ct.CaseID " +
                        "WHERE d.DocumentID = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, documentId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                StringBuilder details = new StringBuilder();
                details.append("DOCUMENT DETAILS\n\n");
                details.append("Document ID: ").append(rs.getInt("DocumentID")).append("\n");
                details.append("Document Name: ").append(rs.getString("DocumentName")).append("\n");
                details.append("Case ID: ").append(rs.getInt("CaseID")).append("\n");
                details.append("Case Type: ").append(rs.getString("CaseType")).append("\n");
                details.append("Document Type: ").append(rs.getString("DocumentType")).append("\n");
                details.append("Description: ").append(rs.getString("Description")).append("\n");
                details.append("Original File: ").append(rs.getString("FileName")).append("\n");
                details.append("File Size: ").append(formatFileSize(rs.getLong("FileSize"))).append("\n");
                details.append("Upload Date: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(rs.getTimestamp("UploadDate"))).append("\n");
                details.append("Uploaded By: ").append(rs.getString("OfficerName")).append("\n");
                details.append("Status: ").append(rs.getString("Status")).append("\n");
                details.append("Version: ").append(rs.getInt("Version")).append("\n");
                
                // Get version history
                String versionSql = "SELECT VersionNumber, UploadDate, UploadedBy, ChangeDescription " +
                                   "FROM document_versions WHERE DocumentID = ? ORDER BY VersionNumber DESC";
                PreparedStatement versionStmt = conn.prepareStatement(versionSql);
                versionStmt.setInt(1, documentId);
                ResultSet versionRs = versionStmt.executeQuery();
                
                if (versionRs.next()) {
                    details.append("\nVERSION HISTORY:\n");
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
                    do {
                        details.append("Version ").append(versionRs.getInt("VersionNumber"))
                               .append(" - ").append(sdf.format(versionRs.getTimestamp("UploadDate")))
                               .append("\n");
                        String changeDesc = versionRs.getString("ChangeDescription");
                        if (changeDesc != null && !changeDesc.isEmpty()) {
                            details.append("   Changes: ").append(changeDesc).append("\n");
                        }
                    } while (versionRs.next());
                }
                versionRs.close();
                versionStmt.close();
                
                JTextArea detailsArea = new JTextArea(details.toString());
                detailsArea.setEditable(false);
                detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JScrollPane scrollPane = new JScrollPane(detailsArea);
                scrollPane.setPreferredSize(new Dimension(600, 400));
                
                JOptionPane.showMessageDialog(this, scrollPane, 
                    "Document Details - ID: " + documentId, JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading document details: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private void downloadDocument() {
        int selectedRow = documentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a document to download.", 
                "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int documentId = (int) tableModel.getValueAt(selectedRow, 0);
        String documentName = (String) tableModel.getValueAt(selectedRow, 2);
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT FilePath, FileName FROM documents WHERE DocumentID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, documentId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String filePath = rs.getString("FilePath");
                String originalFileName = rs.getString("FileName");
                
                File sourceFile = new File(filePath);
                if (!sourceFile.exists()) {
                    JOptionPane.showMessageDialog(this, 
                        "Document file not found on server. It may have been deleted.", 
                        "File Not Found", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Let user choose where to save
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Document As");
                fileChooser.setSelectedFile(new File(originalFileName));
                
                int result = fileChooser.showSaveDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File targetFile = fileChooser.getSelectedFile();
                    
                    // Copy file
                    Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    
                    // Log the download
                    String logSql = "INSERT INTO system_logs (User, Action, Details) VALUES (?, ?, ?)";
                    pstmt = conn.prepareStatement(logSql);
                    pstmt.setString(1, username);
                    pstmt.setString(2, "Document Downloaded");
                    pstmt.setString(3, "Document ID: " + documentId + " - " + documentName);
                    pstmt.executeUpdate();
                    
                    JOptionPane.showMessageDialog(this, 
                        "Document downloaded successfully to:\n" + targetFile.getAbsolutePath(), 
                        "Download Complete", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error downloading file: " + e.getMessage(), 
                "Download Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error accessing document: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private void deleteDocument() {
        int selectedRow = documentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a document to delete.", 
                "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int documentId = (int) tableModel.getValueAt(selectedRow, 0);
        String documentName = (String) tableModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete document:\n\"" + documentName + "\"?\n\n" +
            "Note: This will only archive the document (mark as deleted) in the system.\n" +
            "The actual file will be kept for record-keeping purposes.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            
            try {
                conn = DB.getConnection();
                
                // Instead of deleting, mark as archived
                String sql = "UPDATE documents SET Status = 'Archived' WHERE DocumentID = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, documentId);
                
                int rows = pstmt.executeUpdate();
                
                if (rows > 0) {
                    // Log the action
                    String logSql = "INSERT INTO system_logs (User, Action, Details) VALUES (?, ?, ?)";
                    pstmt = conn.prepareStatement(logSql);
                    pstmt.setString(1, username);
                    pstmt.setString(2, "Document Archived");
                    pstmt.setString(3, "Document ID: " + documentId + " - " + documentName);
                    pstmt.executeUpdate();
                    
                    JOptionPane.showMessageDialog(this, "Document archived successfully.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadDocuments();
                }
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting document: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                DB.closeResources(null, pstmt, conn);
            }
        }
    }
    
    private void searchDocuments() {
        JDialog searchDialog = new JDialog(this, "Search Documents", true);
        searchDialog.setSize(500, 400);
        searchDialog.setLocationRelativeTo(this);
        searchDialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(cardBgColor);
        
        // Search criteria
        JPanel criteriaPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        criteriaPanel.setBackground(cardBgColor);
        criteriaPanel.setBorder(BorderFactory.createTitledBorder("Search Criteria"));
        
        JTextField caseIdField = new JTextField();
        JTextField docNameField = new JTextField();
        JComboBox<String> docTypeCombo = new JComboBox<>(documentTypes);
        docTypeCombo.insertItemAt("All Types", 0);
        docTypeCombo.setSelectedIndex(0);
        
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"All Status", "Active", "Archived", "Pending Review", "Approved"});
        JTextField dateFromField = new JTextField();
        dateFromField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        JTextField dateToField = new JTextField();
        dateToField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        
        criteriaPanel.add(new JLabel("Case ID:"));
        criteriaPanel.add(caseIdField);
        criteriaPanel.add(new JLabel("Document Name:"));
        criteriaPanel.add(docNameField);
        criteriaPanel.add(new JLabel("Document Type:"));
        criteriaPanel.add(docTypeCombo);
        criteriaPanel.add(new JLabel("Status:"));
        criteriaPanel.add(statusCombo);
        criteriaPanel.add(new JLabel("From Date (yyyy-mm-dd):"));
        criteriaPanel.add(dateFromField);
        criteriaPanel.add(new JLabel("To Date (yyyy-mm-dd):"));
        criteriaPanel.add(dateToField);
        
        mainPanel.add(criteriaPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(cardBgColor);
        
        JButton searchBtn = createActionButton("Search", new Color(46, 204, 113));
        JButton clearBtn = createActionButton("Clear", new Color(241, 196, 15));
        JButton cancelBtn = createActionButton("Cancel", new Color(231, 76, 60));
        
        searchBtn.addActionListener(e -> {
            performSearch(caseIdField.getText(), docNameField.getText(), 
                         (String) docTypeCombo.getSelectedItem(), 
                         (String) statusCombo.getSelectedItem(),
                         dateFromField.getText(), dateToField.getText());
            searchDialog.dispose();
        });
        
        clearBtn.addActionListener(e -> {
            caseIdField.setText("");
            docNameField.setText("");
            docTypeCombo.setSelectedIndex(0);
            statusCombo.setSelectedIndex(0);
            dateFromField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            dateToField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        });
        
        cancelBtn.addActionListener(e -> searchDialog.dispose());
        
        buttonPanel.add(searchBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(cancelBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        searchDialog.add(mainPanel);
        searchDialog.setVisible(true);
    }
    
    private void performSearch(String caseIdStr, String docName, String docType, 
                               String status, String dateFrom, String dateTo) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            
            StringBuilder sql = new StringBuilder(
                "SELECT d.*, o.Name as OfficerName " +
                "FROM documents d " +
                "LEFT JOIN officer o ON d.UploadedBy = o.OfficerID " +
                "WHERE (d.UploadedBy = ? OR d.CaseID IN (SELECT CaseID FROM officercase WHERE OfficerID = ?)) ");
            
            // Add search conditions
            if (caseIdStr != null && !caseIdStr.trim().isEmpty()) {
                try {
                    int caseId = Integer.parseInt(caseIdStr.trim());
                    sql.append("AND d.CaseID = ? ");
                } catch (NumberFormatException e) {
                    // Ignore invalid case ID
                }
            }
            
            if (docName != null && !docName.trim().isEmpty()) {
                sql.append("AND d.DocumentName LIKE ? ");
            }
            
            if (docType != null && !docType.equals("All Types")) {
                sql.append("AND d.DocumentType = ? ");
            }
            
            if (status != null && !status.equals("All Status")) {
                sql.append("AND d.Status = ? ");
            }
            
            if (dateFrom != null && !dateFrom.trim().isEmpty()) {
                sql.append("AND DATE(d.UploadDate) >= ? ");
            }
            
            if (dateTo != null && !dateTo.trim().isEmpty()) {
                sql.append("AND DATE(d.UploadDate) <= ? ");
            }
            
            sql.append("ORDER BY d.UploadDate DESC");
            
            pstmt = conn.prepareStatement(sql.toString());
            
            int paramIndex = 1;
            pstmt.setInt(paramIndex++, officerId);
            pstmt.setInt(paramIndex++, officerId);
            
            if (caseIdStr != null && !caseIdStr.trim().isEmpty()) {
                try {
                    int caseId = Integer.parseInt(caseIdStr.trim());
                    pstmt.setInt(paramIndex++, caseId);
                } catch (NumberFormatException e) {
                    // Already handled
                }
            }
            
            if (docName != null && !docName.trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + docName.trim() + "%");
            }
            
            if (docType != null && !docType.equals("All Types")) {
                pstmt.setString(paramIndex++, docType);
            }
            
            if (status != null && !status.equals("All Status")) {
                pstmt.setString(paramIndex++, status);
            }
            
            if (dateFrom != null && !dateFrom.trim().isEmpty()) {
                pstmt.setString(paramIndex++, dateFrom.trim());
            }
            
            if (dateTo != null && !dateTo.trim().isEmpty()) {
                pstmt.setString(paramIndex++, dateTo.trim());
            }
            
            rs = pstmt.executeQuery();
            
            tableModel.setRowCount(0);
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            int count = 0;
            while (rs.next()) {
                count++;
                String fileSize = formatFileSize(rs.getLong("FileSize"));
                String docStatus = rs.getString("Status");
                if (docStatus == null) docStatus = "Active";
                
                tableModel.addRow(new Object[]{
                    rs.getInt("DocumentID"),
                    rs.getInt("CaseID"),
                    rs.getString("DocumentName"),
                    rs.getString("DocumentType"),
                    sdf.format(rs.getTimestamp("UploadDate")),
                    rs.getString("OfficerName"),
                    docStatus,
                    fileSize
                });
            }
            
            JOptionPane.showMessageDialog(this, "Search complete. Found " + count + " document(s).", 
                "Search Results", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching documents: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    // Update the OfficerDashboard to include this new feature
    public static void addToOfficerDashboard(JPanel documentsPanel) {
        // This method would be called from OfficerDashboard to integrate this feature
        // For now, we'll just show how to open the CaseDocuments window
        JButton openDocumentsBtn = new JButton("📁 Open Document Management");
        openDocumentsBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        openDocumentsBtn.setBackground(new Color(52, 152, 219));
        openDocumentsBtn.setForeground(Color.WHITE);
        openDocumentsBtn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        openDocumentsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        openDocumentsBtn.addActionListener(e -> {
            // In the actual implementation, you would pass the officer details
            new CaseDocuments(1, "officer_jean", "Officer Jean");
        });
        
        documentsPanel.add(openDocumentsBtn, BorderLayout.CENTER);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // For testing
            new CaseDocuments(1, "test_officer", "Test Officer");
        });
    }
}