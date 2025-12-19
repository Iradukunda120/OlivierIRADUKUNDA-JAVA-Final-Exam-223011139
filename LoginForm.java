package GOVTECHFORM;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.prefs.Preferences;

public class LoginForm extends JFrame implements ActionListener {
    // UI Components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JCheckBox rememberMeCheckBox;
    private JCheckBox showPasswordCheckBox;
    private JCheckBox notRobotCheckBox;
    
    // Buttons
    private JButton loginButton;
    private JButton signupButton;
    private JButton clearButton;
    private JButton cancelButton;
    private JButton adminCRUDButton;
    private JButton forgotPasswordButton;
    
    // Preferences for Remember Me
    private Preferences prefs;
    
    // Database connection
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;
    
    // Theme Colors
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color WARNING_COLOR = new Color(241, 196, 15);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color INFO_COLOR = new Color(155, 89, 182);
    private final Color DARK_COLOR = new Color(52, 73, 94);
    private final Color LIGHT_COLOR = new Color(236, 240, 241);
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    
    public LoginForm() {
        initializeComponents();
        setupUI();
        loadRememberedCredentials();
        setupEventListeners();
        
        setVisible(true);
    }
    
    private void initializeComponents() {
        // Text Fields
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        
        // Combo Box
        roleComboBox = new JComboBox<>(new String[]{"Select Role", "Admin", "Officer", "Citizen"});
        
        // Check Boxes
        rememberMeCheckBox = new JCheckBox("Remember Me");
        showPasswordCheckBox = new JCheckBox("Show Password");
        notRobotCheckBox = new JCheckBox("I'm not a robot");
        
        // Buttons
        loginButton = new JButton("LOGIN");
        signupButton = new JButton("SIGN UP");
        clearButton = new JButton("CLEAR");
        cancelButton = new JButton("CANCEL");
        adminCRUDButton = new JButton("ADMIN CRUD");
        forgotPasswordButton = new JButton("Forgot Password?");
        
        // Preferences
        prefs = Preferences.userRoot().node(this.getClass().getName());
    }
    
    private void setupUI() {
        setTitle("GovTech Solutions Portal Secure Login Portal");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Main container with background
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(BACKGROUND_COLOR);
        
        // Header Panel with gradient
        JPanel headerPanel = createHeaderPanel();
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        
        // Center Panel with login form
        JPanel centerPanel = createCenterPanel();
        mainContainer.add(centerPanel, BorderLayout.CENTER);
        
        // Footer Panel
        JPanel footerPanel = createFooterPanel();
        mainContainer.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainContainer);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_COLOR,
                    getWidth(), getHeight(), SECONDARY_COLOR
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(getWidth(), 120));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Title
        JLabel titleLabel = new JLabel("GOVTECH SOLUTIONS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.green);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Secure Government Service Portal");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(240, 240, 240));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(titleLabel, BorderLayout.CENTER);
        textPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        headerPanel.add(textPanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.blue);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome Back!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(DARK_COLOR);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(welcomeLabel, gbc);
        
        // Instruction Label
        JLabel instructionLabel = new JLabel("Please enter your credentials to access the system");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructionLabel.setForeground(new Color(100, 100, 100));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridy = 1;
        centerPanel.add(instructionLabel, gbc);
        
        // Form Fields
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(createFormLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        styleTextField(usernameField);
        centerPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        centerPanel.add(createFormLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        styleTextField(passwordField);
        centerPanel.add(passwordField, gbc);
        
        // Role Selection
        gbc.gridx = 0;
        gbc.gridy = 4;
        centerPanel.add(createFormLabel("Role:"), gbc);
        
        gbc.gridx = 1;
        styleComboBox(roleComboBox);
        centerPanel.add(roleComboBox, gbc);
        
        // Checkboxes Panel
        JPanel checkboxesPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        checkboxesPanel.setOpaque(false);
        
        styleCheckBox(showPasswordCheckBox);
        styleCheckBox(rememberMeCheckBox);
        styleCheckBox(notRobotCheckBox);
        
        checkboxesPanel.add(showPasswordCheckBox);
        checkboxesPanel.add(rememberMeCheckBox);
        checkboxesPanel.add(notRobotCheckBox);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        centerPanel.add(checkboxesPanel, gbc);
        
        // Buttons Panel - Row 1
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JPanel buttonsPanel1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonsPanel1.setOpaque(false);
        
        styleButton(loginButton, SUCCESS_COLOR);
        styleButton(clearButton, WARNING_COLOR);
        styleButton(cancelButton, DANGER_COLOR);
        
        buttonsPanel1.add(loginButton);
        buttonsPanel1.add(clearButton);
        buttonsPanel1.add(cancelButton);
        
        centerPanel.add(buttonsPanel1, gbc);
        
        // Buttons Panel - Row 2
        gbc.gridy = 7;
        JPanel buttonsPanel2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonsPanel2.setOpaque(false);
        
        styleButton(signupButton, INFO_COLOR);
        styleButton(adminCRUDButton, DARK_COLOR);
        
        buttonsPanel2.add(signupButton);
        buttonsPanel2.add(adminCRUDButton);
        
        centerPanel.add(buttonsPanel2, gbc);
        
        // Forgot Password
        gbc.gridy = 8;
        JPanel forgotPasswordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        forgotPasswordPanel.setOpaque(false);
        
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setForeground(DANGER_COLOR);
        forgotPasswordButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        forgotPasswordPanel.add(forgotPasswordButton);
        centerPanel.add(forgotPasswordPanel, gbc);
        
        // Quick Access Info
        gbc.gridy = 9;
        JPanel quickAccessPanel = new JPanel();
        quickAccessPanel.setOpaque(false);
        quickAccessPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel quickAccessLabel = new JLabel("<html><center>"
                + "<b>Quick Access:</b><br>"
                + "• Admin: Full system access<br>"
                + "• Officer: Service management<br>"
                + "• Citizen: Public services access"
                + "</center></html>");
        quickAccessLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        quickAccessLabel.setForeground(new Color(100, 100, 100));
        
        quickAccessPanel.add(quickAccessLabel);
        centerPanel.add(quickAccessPanel, gbc);
        
        return centerPanel;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(DARK_COLOR);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Left side - Copyright
        JLabel copyrightLabel = new JLabel("© 2024 GovTech Solutions. All rights reserved.");
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        copyrightLabel.setForeground(Color.GREEN);
        
        // Right side - Version
        JLabel versionLabel = new JLabel("Version 2.1.0 | Build 20241218");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(200, 200, 200));
        
        footerPanel.add(copyrightLabel, BorderLayout.WEST);
        footerPanel.add(versionLabel, BorderLayout.EAST);
        
        return footerPanel;
    }
    
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(DARK_COLOR);
        return label;
    }
    
    private void styleTextField(JComponent textField) {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(250, 40));
        
        if (textField instanceof JTextField) {
            ((JTextField) textField).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
        } else if (textField instanceof JPasswordField) {
            ((JPasswordField) textField).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            ((JPasswordField) textField).setEchoChar('•');
        }
    }
    
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(250, 40));
        comboBox.setBackground(Color.GREEN);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }
    
    private void styleCheckBox(JCheckBox checkBox) {
        checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        checkBox.setForeground(new Color(80, 80, 80));
        checkBox.setBackground(Color.GREEN);
        checkBox.setFocusPainted(false);
    }
    
    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.GREEN);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(backgroundColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
    }
    
    private void loadRememberedCredentials() {
        String savedUsername = prefs.get("username", "");
        if (!savedUsername.isEmpty()) {
            usernameField.setText(savedUsername);
            rememberMeCheckBox.setSelected(true);
            passwordField.requestFocus();
        }
    }
    
    private void setupEventListeners() {
        // Button listeners
        loginButton.addActionListener(this);
        signupButton.addActionListener(this);
        clearButton.addActionListener(this);
        cancelButton.addActionListener(this);
        adminCRUDButton.addActionListener(this);
        forgotPasswordButton.addActionListener(this);
        
        // Checkbox listeners
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });
        
        // Enter key listener for login
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == loginButton) {
            performLogin();
        } else if (source == signupButton) {
            showSignupDialog();
        } else if (source == clearButton) {
            clearForm();
        } else if (source == cancelButton) {
            confirmExit();
        } else if (source == adminCRUDButton) {
            showAdminCRUD();
        } else if (source == forgotPasswordButton) {
            showForgotPasswordDialog();
        }
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String selectedRole = (String) roleComboBox.getSelectedItem();
        
        // Validation
        if (!validateForm(username, password, selectedRole)) {
            return;
        }
        
        // Authenticate user
        authenticateUser(username, password, selectedRole);
    }
    
    private boolean validateForm(String username, String password, String role) {
        if (username.isEmpty()) {
            showError("Username is required", "Please enter your username");
            usernameField.requestFocus();
            return false;
        }
        
        if (password.isEmpty()) {
            showError("Password is required", "Please enter your password");
            passwordField.requestFocus();
            return false;
        }
        
        if ("Select Role".equals(role)) {
            showError("Role selection required", "Please select your role");
            roleComboBox.requestFocus();
            return false;
        }
        
        if (!notRobotCheckBox.isSelected()) {
            showError("Verification required", "Please confirm you are not a robot");
            notRobotCheckBox.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void authenticateUser(String username, String password, String role) {
        try {
            conn = DB.getConnection();
            String sql = "SELECT u.*, c.FullName, c.CitizenID, o.Name as OfficerName " +
                        "FROM useraccount u " +
                        "LEFT JOIN citizen c ON u.CitizenID = c.CitizenID " +
                        "LEFT JOIN officer o ON u.OfficerID = o.OfficerID " +
                        "WHERE u.Username = ? AND u.PasswordHash = ? AND u.Role = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role.toUpperCase());
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                handleSuccessfulLogin(rs, username, role);
            } else {
                showError("Login Failed", 
                    "Invalid username, password, or role. Please try again.");
            }
        } catch (SQLException ex) {
            showError("Database Error", 
                "Unable to connect to database. Please check your connection.\nError: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            closeDatabaseResources();
        }
    }
    
    private void handleSuccessfulLogin(ResultSet rs, String username, String role) throws SQLException {
        int userId = rs.getInt("UserID");
        String fullName = rs.getString("FullName");
        Integer citizenId = rs.getInt("CitizenID");
        String officerName = rs.getString("OfficerName");
        
        String displayName = fullName != null ? fullName : 
                           officerName != null ? officerName : username;
        
        // Save username if Remember Me is checked
        if (rememberMeCheckBox.isSelected()) {
            prefs.put("username", username);
        } else {
            prefs.remove("username");
        }
        
        // Show success message
        showSuccess("Login Successful", 
            "Welcome " + displayName + "!\nYou have been logged in as " + role);
        
        dispose();
        
        // Redirect to appropriate dashboard
        redirectToDashboard(role.toUpperCase(), userId, displayName, citizenId, username);
    }
    
    private void redirectToDashboard(String role, int userId, String displayName, Integer citizenId, String username) {
        switch (role) {
            case "ADMIN":
                openAdminDashboard(userId, displayName);
                break;
            case "OFFICER":
                openOfficerDashboard(userId, displayName);
                break;
            case "CITIZEN":
                openCitizenDashboard(userId, displayName, citizenId, username);
                break;
            default:
                showError("Role Error", "Unknown role: " + role);
        }
    }
    
    private void openAdminDashboard(int userId, String displayName) {
        SwingUtilities.invokeLater(() -> {
            try {
                AdminDashboard adminDashboard = new AdminDashboard(userId, displayName);
                adminDashboard.setVisible(true);
            } catch (Exception e) {
                showError("Dashboard Error", 
                    "Unable to open Admin Dashboard: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    private void openOfficerDashboard(int userId, String displayName) {
        SwingUtilities.invokeLater(() -> {
            try {
                OfficerDashboard officerDashboard = new OfficerDashboard(userId, displayName);
                officerDashboard.setVisible(true);
            } catch (Exception e) {
                showError("Dashboard Error", 
                    "Unable to open Officer Dashboard: " + e.getMessage());
                e.printStackTrace();
                showFallbackDashboard("Officer", displayName);
            }
        });
    }
    
    private void openCitizenDashboard(int userId, String displayName, Integer citizenId, String username) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (citizenId != null && citizenId > 0) {
                    CitizenDashboard citizenDashboard = new CitizenDashboard(citizenId, username);
                    citizenDashboard.setVisible(true);
                } else {
                    showError("Profile Error", 
                        "Citizen profile not found. Please contact administrator.");
                    showFallbackDashboard("Citizen", displayName);
                }
            } catch (Exception e) {
                showError("Dashboard Error", 
                    "Unable to open Citizen Dashboard: " + e.getMessage());
                e.printStackTrace();
                showFallbackDashboard("Citizen", displayName);
            }
        });
    }
    
    private void showFallbackDashboard(String role, String displayName) {
        JFrame fallbackFrame = new JFrame(role + " Dashboard - GovTech Solutions");
        fallbackFrame.setSize(800, 600);
        fallbackFrame.setLocationRelativeTo(null);
        fallbackFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("Welcome, " + displayName + " (" + role + ")", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(PRIMARY_COLOR);
        
        JTextArea content = new JTextArea();
        content.setEditable(false);
        content.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        content.setText(
            "This is a fallback interface while the full dashboard is being loaded.\n\n" +
            "User Information:\n" +
            "• Name: " + displayName + "\n" +
            "• Role: " + role + "\n\n" +
            "Please wait while we load your dashboard..."
        );
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(content), BorderLayout.CENTER);
        
        fallbackFrame.add(panel);
        fallbackFrame.setVisible(true);
    }
    
    private void showSignupDialog() {
        JDialog signupDialog = new JDialog(this, "New User Registration", true);
        signupDialog.setSize(500, 500);
        signupDialog.setLocationRelativeTo(this);
        signupDialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField fullNameField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Citizen", "Officer"});
        JTextArea addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        
        formPanel.add(new JLabel("Full Name*:"));
        formPanel.add(fullNameField);
        formPanel.add(new JLabel("Username*:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password*:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Confirm Password*:"));
        formPanel.add(confirmPasswordField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Role*:"));
        formPanel.add(roleCombo);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(new JScrollPane(addressArea));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");
        
        styleButton(submitButton, SUCCESS_COLOR);
        styleButton(cancelButton, DANGER_COLOR);
        
        submitButton.addActionListener(e -> {
            if (validateRegistration(fullNameField, usernameField, passwordField, 
                                   confirmPasswordField, emailField, phoneField, roleCombo)) {
                JOptionPane.showMessageDialog(signupDialog,
                    "Registration submitted for approval!\n\n" +
                    "You will receive a confirmation email once your account is activated.",
                    "Registration Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                signupDialog.dispose();
            }
        });
        
        cancelButton.addActionListener(e -> signupDialog.dispose());
        
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        
        signupDialog.add(formPanel, BorderLayout.CENTER);
        signupDialog.add(buttonPanel, BorderLayout.SOUTH);
        signupDialog.setVisible(true);
    }
    
    private boolean validateRegistration(JTextField fullName, JTextField username, 
                                         JPasswordField password, JPasswordField confirmPassword,
                                         JTextField email, JTextField phone, JComboBox<String> role) {
        if (fullName.getText().trim().isEmpty()) {
            showError("Validation Error", "Full name is required");
            return false;
        }
        
        if (username.getText().trim().isEmpty()) {
            showError("Validation Error", "Username is required");
            return false;
        }
        
        String pass = new String(password.getPassword());
        String confirmPass = new String(confirmPassword.getPassword());
        
        if (pass.isEmpty()) {
            showError("Validation Error", "Password is required");
            return false;
        }
        
        if (!pass.equals(confirmPass)) {
            showError("Validation Error", "Passwords do not match");
            return false;
        }
        
        if (pass.length() < 6) {
            showError("Validation Error", "Password must be at least 6 characters");
            return false;
        }
        
        return true;
    }
    
    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        roleComboBox.setSelectedIndex(0);
        rememberMeCheckBox.setSelected(false);
        showPasswordCheckBox.setSelected(false);
        notRobotCheckBox.setSelected(false);
        
        usernameField.requestFocus();
        
        showInfo("Form Cleared", "All fields have been cleared");
    }
    
    private void confirmExit() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit the application?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    private void showAdminCRUD() {
        String adminUsername = JOptionPane.showInputDialog(this,
            "Enter Admin Username:",
            "Admin Authentication",
            JOptionPane.QUESTION_MESSAGE);
        
        if (adminUsername == null || adminUsername.trim().isEmpty()) return;
        
        String adminPassword = JOptionPane.showInputDialog(this,
            "Enter Admin Password:",
            "Admin Authentication",
            JOptionPane.PLAIN_MESSAGE);
        
        if (adminPassword == null || adminPassword.trim().isEmpty()) return;
        
        // Verify admin credentials
        try {
            conn = DB.getConnection();
            String sql = "SELECT * FROM useraccount WHERE Username = ? AND PasswordHash = ? AND Role = 'ADMIN'";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, adminUsername);
            pstmt.setString(2, adminPassword);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                openAdminCRUDInterface();
            } else {
                showError("Access Denied", 
                    "Invalid admin credentials or insufficient privileges");
            }
        } catch (SQLException ex) {
            showError("Database Error", "Error verifying admin credentials: " + ex.getMessage());
        } finally {
            closeDatabaseResources();
        }
    }
    
    private void openAdminCRUDInterface() {
        SwingUtilities.invokeLater(() -> {
            AdminCRUD adminCRUD = new AdminCRUD();
            adminCRUD.setVisible(true);
        });
    }
    
    private void showForgotPasswordDialog() {
        JDialog forgotDialog = new JDialog(this, "Password Recovery", true);
        forgotDialog.setSize(400, 250);
        forgotDialog.setLocationRelativeTo(this);
        forgotDialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel instructionLabel = new JLabel("<html><center>"
            + "Enter your registered email address or username<br>"
            + "to receive password reset instructions."
            + "</center></html>");
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JTextField recoveryField = new JTextField();
        styleTextField(recoveryField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton sendButton = new JButton("Send Reset Link");
        JButton cancelButton = new JButton("Cancel");
        
        styleButton(sendButton, SUCCESS_COLOR);
        styleButton(cancelButton, DANGER_COLOR);
        
        sendButton.addActionListener(e -> {
            if (!recoveryField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(forgotDialog,
                    "Password reset instructions have been sent to:\n" + recoveryField.getText() +
                    "\n\nPlease check your email and follow the instructions.",
                    "Recovery Email Sent",
                    JOptionPane.INFORMATION_MESSAGE);
                forgotDialog.dispose();
            } else {
                showError("Input Required", "Please enter your email or username");
            }
        });
        
        cancelButton.addActionListener(e -> forgotDialog.dispose());
        
        buttonPanel.add(sendButton);
        buttonPanel.add(cancelButton);
        
        panel.add(instructionLabel, BorderLayout.NORTH);
        panel.add(recoveryField, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        forgotDialog.add(panel);
        forgotDialog.setVisible(true);
    }
    
    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this,
            message,
            title,
            JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccess(String title, String message) {
        JOptionPane.showMessageDialog(this,
            message,
            title,
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showInfo(String title, String message) {
        JOptionPane.showMessageDialog(this,
            message,
            title,
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void closeDatabaseResources() {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Run the application
        SwingUtilities.invokeLater(() -> {
            new LoginForm();
        });
    }
}

// Separate class for Admin CRUD interface
class AdminCRUD extends JFrame {
    private DefaultTableModel tableModel;
    private JTable userTable;
    private JLabel statusLabel;
    
    public AdminCRUD() {
        setTitle("Admin CRUD - User Management");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        setupUI();
        loadUserData();
    }
    
    private void setupUI() {
        // Main layout
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Admin CRUD Operations - User Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] buttonNames = {"Add User", "Edit User", "Delete User", "Refresh", 
                               "Search", "Assign Operations", "View Logs"};
        Color[] buttonColors = {
            new Color(46, 204, 113), new Color(241, 196, 15), new Color(231, 76, 60),
            new Color(52, 152, 219), new Color(155, 89, 182), new Color(142, 68, 173),
            new Color(39, 174, 96)
        };
        
        for (int i = 0; i < buttonNames.length; i++) {
            JButton button = new JButton(buttonNames[i]);
            styleCRUDButton(button, buttonColors[i]);
            toolbarPanel.add(button);
            
            // Add action listeners (you can implement these)
            final String action = buttonNames[i];
            button.addActionListener(e -> handleCRUDAction(action));
        }
        
        add(toolbarPanel, BorderLayout.CENTER);
        
        // Table
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Username", "Role", "Email", "Created At"}, 0
        );
        
        userTable = new JTable(tableModel);
        userTable.setRowHeight(30);
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Status bar
        statusLabel = new JLabel("Total users: 0");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    private void styleCRUDButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
    }
    
    private void loadUserData() {
        // Implement database loading
        tableModel.setRowCount(0);
        statusLabel.setText("Total users: 0");
        
        // Add sample data (replace with actual database query)
        Object[][] sampleData = {
            {1, "admin_john", "ADMIN", "john@example.com", "2024-01-15"},
            {2, "officer_sarah", "OFFICER", "sarah@example.com", "2024-02-20"},
            {3, "citizen_mike", "CITIZEN", "mike@example.com", "2024-03-10"}
        };
        
        for (Object[] row : sampleData) {
            tableModel.addRow(row);
        }
        
        statusLabel.setText("Total users: " + sampleData.length);
    }
    
    private void handleCRUDAction(String action) {
        switch (action) {
            case "Add User":
                JOptionPane.showMessageDialog(this, "Add User functionality");
                break;
            case "Edit User":
                JOptionPane.showMessageDialog(this, "Edit User functionality");
                break;
            case "Delete User":
                JOptionPane.showMessageDialog(this, "Delete User functionality");
                break;
            case "Refresh":
                loadUserData();
                break;
            case "Search":
                JOptionPane.showMessageDialog(this, "Search functionality");
                break;
            case "Assign Operations":
                JOptionPane.showMessageDialog(this, "Assign Operations functionality");
                break;
            case "View Logs":
                JOptionPane.showMessageDialog(this, "View Logs functionality");
                break;
        }
    }
}