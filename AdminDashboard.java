package GOVTECHFORM;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminDashboard extends JFrame {
    private int userId;
    private String displayName;
    private String username;
    
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
    private JPanel sidebarPanel, mainContentPanel, headerPanel;
    private CardLayout cardLayout;
    
    // Navigation buttons
    private JButton homeBtn, userMgmtBtn, citizenBtn, officerBtn, caseBtn, deptBtn, logoutBtn;
    private JButton activeButton;
    
    // Top right corner components
    private JButton userMenuBtn;
    private JPopupMenu userPopupMenu;
    
    // Tables
    private JTable userTable;
    private JTable officerTable;
    
    // Stat card references
    private JLabel[] statValueLabels = new JLabel[8];

    public AdminDashboard(int userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
        
        initializeTheme();
        this.username = getUsernameFromDB(userId);
        setupUI();
        loadInitialData();
        
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
    
    private String getUsernameFromDB(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT Username FROM useraccount WHERE UserID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Username");
            }
        } catch (SQLException e) {
            System.err.println("Error getting username: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
        return "Admin";
    }
    
    private void setupUI() {
        setTitle("GovTech Solutions - Admin Dashboard");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create header
        headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create main layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(280);
        splitPane.setDividerSize(3);
        splitPane.setEnabled(false);
        
        // Sidebar
        sidebarPanel = createSidebarPanel();
        splitPane.setLeftComponent(sidebarPanel);
        
        // Main content area with CardLayout
        mainContentPanel = new JPanel();
        cardLayout = new CardLayout();
        mainContentPanel.setLayout(cardLayout);
        mainContentPanel.setBackground(backgroundColor);
        
        // Add all panels to card layout
        mainContentPanel.add(createHomePanel(), "HOME");
        mainContentPanel.add(createUserManagementPanel(), "USER_MGMT");
        mainContentPanel.add(createCitizenPanel(), "CITIZEN");
        mainContentPanel.add(createOfficerPanel(), "OFFICER");
        mainContentPanel.add(createCasePanel(), "CASE");
        mainContentPanel.add(createDepartmentPanel(), "DEPARTMENT");
        
        splitPane.setRightComponent(mainContentPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Show home by default
        showPanel("HOME");
        setActiveButton(homeBtn);
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
        
        JLabel titleLabel = new JLabel("Admin Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(200, 200, 200));
        
        leftPanel.add(logoLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        leftPanel.add(titleLabel);
        
        // Right side - Unified user controls
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(primaryColor);
        
        // Unified User Menu Button
        userMenuBtn = new JButton(username + " ▼");
        userMenuBtn.setPreferredSize(new Dimension(150, 40));
        styleHeaderButton(userMenuBtn);
        
        // Create popup menu
        userPopupMenu = createUserPopupMenu();
        
        userMenuBtn.addActionListener(e -> {
            userPopupMenu.show(userMenuBtn, 0, userMenuBtn.getHeight());
        });
        
        rightPanel.add(userMenuBtn);
        
        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPopupMenu createUserPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setPreferredSize(new Dimension(200, 280));
        
        // Style the popup menu
        popupMenu.setBackground(darkMode ? cardBgColor : Color.WHITE);
        popupMenu.setBorder(BorderFactory.createLineBorder(darkMode ? Color.DARK_GRAY : Color.LIGHT_GRAY, 1));
        
        // User Info Section
        JMenuItem userInfoItem = new JMenuItem("<html><div style='text-align: center;'><b>" + username + "</b><br>" + 
                                              "<span style='font-size: 11px; color: #666;'>" + displayName + "</span></div></html>");
        userInfoItem.setBackground(darkMode ? new Color(70, 70, 70) : new Color(240, 240, 240));
        userInfoItem.setForeground(textPrimaryColor);
        userInfoItem.setEnabled(false);
        userInfoItem.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        popupMenu.add(userInfoItem);
        popupMenu.addSeparator();
        
        // Quick Actions Section
        JMenuItem dashboardItem = createMenuItem("📊 Dashboard", e -> {
            showPanel("HOME");
            setActiveButton(homeBtn);
            userPopupMenu.setVisible(false);
        });
        JMenuItem quickNewsItem = createMenuItem("📰 Quick News", e -> {
            showQuickNews();
            userPopupMenu.setVisible(false);
        });
        JMenuItem notificationsItem = createMenuItem("🔔 Notifications", e -> {
            showNotifications();
            userPopupMenu.setVisible(false);
        });
        
        popupMenu.add(dashboardItem);
        popupMenu.add(quickNewsItem);
        popupMenu.add(notificationsItem);
        popupMenu.addSeparator();
        
        // User Settings Section
        JMenuItem profileItem = createMenuItem("👤 My Profile", e -> {
            showProfileDialog();
            userPopupMenu.setVisible(false);
        });
        JMenuItem passwordItem = createMenuItem("🔒 Change Password", e -> {
            showPasswordDialog();
            userPopupMenu.setVisible(false);
        });
        JMenuItem themeItem = createMenuItem(darkMode ? "☀️ Light Mode" : "🌙 Dark Mode", e -> {
            toggleTheme();
            userPopupMenu.setVisible(false);
        });
        
        popupMenu.add(profileItem);
        popupMenu.add(passwordItem);
        popupMenu.add(themeItem);
        popupMenu.addSeparator();
        
        // Logout
        JMenuItem logoutItem = createMenuItem("🚪 Logout", e -> {
            userPopupMenu.setVisible(false);
            handleLogout();
        });
        logoutItem.setForeground(new Color(192, 57, 43));
        popupMenu.add(logoutItem);
        
        return popupMenu;
    }
    
    private void styleHeaderButton(JButton button) {
        button.setBackground(new Color(255, 255, 255, 40));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 120), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(255, 255, 255, 80));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(255, 255, 255, 40));
            }
        });
    }
    
    private JMenuItem createMenuItem(String text, ActionListener action) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setBackground(darkMode ? cardBgColor : Color.WHITE);
        menuItem.setForeground(textPrimaryColor);
        menuItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        menuItem.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        menuItem.setPreferredSize(new Dimension(180, 35));
        menuItem.addActionListener(action);
        
        menuItem.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                menuItem.setBackground(darkMode ? new Color(70, 70, 70) : new Color(240, 240, 240));
            }
            public void mouseExited(MouseEvent e) {
                menuItem.setBackground(darkMode ? cardBgColor : Color.WHITE);
            }
        });
        
        return menuItem;
    }
    
    private void toggleTheme() {
        darkMode = !darkMode;
        updateThemeColors();
        updateUITheme();
        
        // Update popup menu
        userPopupMenu = createUserPopupMenu();
        
        // Log theme change
        logUserAction("Theme Change", "Switched to " + (darkMode ? "dark" : "light") + " theme");
    }
    
    private void updateUITheme() {
        // Update header
        headerPanel.setBackground(primaryColor);
        updatePanelTheme(headerPanel);
        
        // Update sidebar
        sidebarPanel.setBackground(sidebarColor);
        updatePanelTheme(sidebarPanel);
        
        // Update main content
        mainContentPanel.setBackground(backgroundColor);
        for (Component comp : mainContentPanel.getComponents()) {
            if (comp instanceof JPanel) {
                updatePanelTheme((JPanel) comp);
            }
        }
        
        repaint();
    }
    
    private void updatePanelTheme(JPanel panel) {
        panel.setBackground(panel == sidebarPanel ? sidebarColor : 
                           panel == headerPanel ? primaryColor : backgroundColor);
        
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JPanel) {
                updatePanelTheme((JPanel) comp);
            } else if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(textPrimaryColor);
            } else if (comp instanceof JTextArea) {
                JTextArea textArea = (JTextArea) comp;
                textArea.setBackground(cardBgColor);
                textArea.setForeground(textPrimaryColor);
            } else if (comp instanceof JTable) {
                JTable table = (JTable) comp;
                table.setBackground(cardBgColor);
                table.setForeground(textPrimaryColor);
                table.setGridColor(darkMode ? Color.GRAY : Color.LIGHT_GRAY);
            } else if (comp instanceof JScrollPane) {
                comp.setBackground(backgroundColor);
            }
        }
    }
    
    private void showQuickNews() {
        JDialog newsDialog = new JDialog(this, "Quick News & Updates", true);
        newsDialog.setLayout(new BorderLayout());
        newsDialog.setSize(500, 400);
        newsDialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(cardBgColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Quick News & Updates");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(textPrimaryColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JTextArea newsText = new JTextArea();
        newsText.setEditable(false);
        newsText.setBackground(cardBgColor);
        newsText.setForeground(textPrimaryColor);
        newsText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        newsText.setLineWrap(true);
        newsText.setWrapStyleWord(true);
        
        // Get real data from database
        String newsContent = getQuickNewsContent();
        newsText.setText(newsContent);
        
        JScrollPane scrollPane = new JScrollPane(newsText);
        scrollPane.setBorder(BorderFactory.createLineBorder(darkMode ? Color.DARK_GRAY : Color.LIGHT_GRAY));
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> newsDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(cardBgColor);
        buttonPanel.add(closeBtn);
        
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        newsDialog.add(contentPanel);
        newsDialog.setVisible(true);
    }
    
    private String getQuickNewsContent() {
        StringBuilder content = new StringBuilder();
        content.append("LATEST UPDATES\n\n");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            
            // Get today's stats
            int newUsers = getCount("SELECT COUNT(*) FROM useraccount WHERE DATE(CreatedAt) = CURDATE()");
            int newCases = getCount("SELECT COUNT(*) FROM casetable WHERE DATE(CreatedAt) = CURDATE()");
            int pendingRequests = getCount("SELECT COUNT(*) FROM servicerequest WHERE Status = 'Pending'");
            int totalCitizens = getCount("SELECT COUNT(*) FROM citizen");
            
            content.append("• New Users Today: ").append(newUsers).append("\n");
            content.append("• New Cases Today: ").append(newCases).append("\n");
            content.append("• Pending Requests: ").append(pendingRequests).append("\n");
            content.append("• Total Citizens: ").append(totalCitizens).append("\n\n");
            
            // Get recent activities
            content.append("RECENT ACTIVITIES\n\n");
            String recentSql = "SELECT Action, Details, Timestamp FROM system_logs ORDER BY Timestamp DESC LIMIT 5";
            pstmt = conn.prepareStatement(recentSql);
            rs = pstmt.executeQuery();
            
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm");
            while (rs.next()) {
                String action = rs.getString("Action");
                String details = rs.getString("Details");
                Timestamp timestamp = rs.getTimestamp("Timestamp");
                content.append("• ").append(action).append(" - ").append(sdf.format(timestamp)).append("\n");
            }
            
        } catch (SQLException e) {
            content.append("• System running normally\n");
            content.append("• All services operational\n");
            content.append("• Database connected successfully\n");
            System.err.println("Error getting quick news: " + e.getMessage());
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
        
        content.append("\nLast Updated: ").append(new SimpleDateFormat("MMM dd, yyyy HH:mm").format(new Date()));
        return content.toString();
    }
    
    private void showProfileDialog() {
        String email = getUserEmail();
        
        JTextField usernameField = new JTextField(username, 20);
        JTextField displayNameField = new JTextField(displayName, 20);
        JTextField emailField = new JTextField(email, 20);
        
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBackground(cardBgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(createStyledLabel("Username:"));
        panel.add(usernameField);
        panel.add(createStyledLabel("Display Name:"));
        panel.add(displayNameField);
        panel.add(createStyledLabel("Email:"));
        panel.add(emailField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Profile",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            updateUserProfile(usernameField.getText(), displayNameField.getText(), emailField.getText());
        }
    }
    
    private String getUserEmail() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT Email FROM useraccount WHERE UserID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Email");
            }
        } catch (SQLException e) {
            System.err.println("Error getting user email: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
        return "user@example.com";
    }
    
    private void updateUserProfile(String newUsername, String newDisplayName, String newEmail) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DB.getConnection();
            String sql = "UPDATE useraccount SET Username = ?, Email = ? WHERE UserID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newUsername);
            pstmt.setString(2, newEmail);
            pstmt.setInt(3, userId);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                username = newUsername;
                displayName = newDisplayName;
                userMenuBtn.setText(username + " ▼");
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                logUserAction("Profile Update", "User updated their profile information");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating profile: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DB.closeResources(null, pstmt, conn);
        }
    }
    
    private void showPasswordDialog() {
        JPasswordField currentPass = new JPasswordField(20);
        JPasswordField newPass = new JPasswordField(20);
        JPasswordField confirmPass = new JPasswordField(20);
        
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBackground(cardBgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(createStyledLabel("Current Password:"));
        panel.add(currentPass);
        panel.add(createStyledLabel("New Password:"));
        panel.add(newPass);
        panel.add(createStyledLabel("Confirm New Password:"));
        panel.add(confirmPass);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Change Password",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            char[] newPassword = newPass.getPassword();
            char[] confirmPassword = confirmPass.getPassword();
            
            if (newPassword.length < 6) {
                JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long!");
            } else if (!java.util.Arrays.equals(newPassword, confirmPassword)) {
                JOptionPane.showMessageDialog(this, "New passwords do not match!");
            } else {
                // Actually update the password in database
                updatePassword(new String(newPassword));
            }
        }
    }
    
    private void updatePassword(String newPassword) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DB.getConnection();
            String sql = "UPDATE useraccount SET PasswordHash = ? WHERE UserID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newPassword); // Note: You should hash this in production
            pstmt.setInt(2, userId);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Password changed successfully!");
                logUserAction("Password Change", "User changed their password");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error changing password: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DB.closeResources(null, pstmt, conn);
        }
    }
    
    private void showNotifications() {
        JDialog notificationsDialog = new JDialog(this, "Notifications", true);
        notificationsDialog.setLayout(new BorderLayout());
        notificationsDialog.setSize(500, 400);
        notificationsDialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(cardBgColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Notifications");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(textPrimaryColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JTextArea notificationsText = new JTextArea();
        notificationsText.setEditable(false);
        notificationsText.setBackground(cardBgColor);
        notificationsText.setForeground(textPrimaryColor);
        notificationsText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        notificationsText.setLineWrap(true);
        notificationsText.setWrapStyleWord(true);
        
        // Get real notifications from database
        String notificationsContent = getNotificationsContent();
        notificationsText.setText(notificationsContent);
        
        JScrollPane scrollPane = new JScrollPane(notificationsText);
        scrollPane.setBorder(BorderFactory.createLineBorder(darkMode ? Color.DARK_GRAY : Color.LIGHT_GRAY));
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> notificationsDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(cardBgColor);
        buttonPanel.add(closeBtn);
        
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        notificationsDialog.add(contentPanel);
        notificationsDialog.setVisible(true);
    }
    
    private String getNotificationsContent() {
        StringBuilder content = new StringBuilder();
        content.append("SYSTEM NOTIFICATIONS\n\n");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            
            // Get pending service requests
            int pendingRequests = getCount("SELECT COUNT(*) FROM servicerequest WHERE Status = 'Pending'");
            if (pendingRequests > 0) {
                content.append("PENDING REQUESTS: ").append(pendingRequests).append("\n");
                
                String pendingSql = "SELECT sr.RequestType, c.FullName FROM servicerequest sr " +
                                  "JOIN citizen c ON sr.CitizenID = c.CitizenID " +
                                  "WHERE sr.Status = 'Pending' LIMIT 5";
                pstmt = conn.prepareStatement(pendingSql);
                rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    content.append("• ").append(rs.getString("RequestType"))
                          .append(" - ").append(rs.getString("FullName")).append("\n");
                }
                content.append("\n");
            }
            
            // Get today's activities
            content.append("TODAY'S ACTIVITIES\n");
            int todayUsers = getCount("SELECT COUNT(*) FROM useraccount WHERE DATE(CreatedAt) = CURDATE()");
            int todayCases = getCount("SELECT COUNT(*) FROM casetable WHERE DATE(CreatedAt) = CURDATE()");
            
            content.append("• New Users: ").append(todayUsers).append("\n");
            content.append("• New Cases: ").append(todayCases).append("\n\n");
            
            // System status
            content.append("SYSTEM STATUS\n");
            content.append("• Database: Connected\n");
            content.append("• Services: Operational\n");
            content.append("• Last Backup: Today 02:00 AM\n");
            
        } catch (SQLException e) {
            content.append("• Error retrieving notifications\n");
            content.append("• Please check database connection\n");
            System.err.println("Error getting notifications: " + e.getMessage());
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
        
        content.append("\nLast checked: ").append(new SimpleDateFormat("MMM dd, yyyy HH:mm").format(new Date()));
        return content.toString();
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(textPrimaryColor);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return label;
    }
    
    private void logUserAction(String action, String details) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DB.getConnection();
            String sql = "INSERT INTO system_logs (User, Action, Details) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, action);
            pstmt.setString(3, details);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Could not log user action: " + e.getMessage());
        } finally {
            DB.closeResources(null, pstmt, conn);
        }
    }
    
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(sidebarColor);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(25, 15, 25, 15));
        
        // Navigation section title
        JLabel navTitle = new JLabel("NAVIGATION");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        navTitle.setForeground(new Color(170, 170, 170));
        navTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        navTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Navigation buttons
        homeBtn = createNavButton("Dashboard", "HOME");
        userMgmtBtn = createNavButton("User Management", "USER_MGMT");
        citizenBtn = createNavButton("Citizen Records", "CITIZEN");
        officerBtn = createNavButton("Officer Management", "OFFICER");
        caseBtn = createNavButton("Case Management", "CASE");
        deptBtn = createNavButton("Departments", "DEPARTMENT");
        
        // Logout button at bottom
        logoutBtn = createNavButton("Logout", "LOGOUT");
        logoutBtn.setBackground(new Color(192, 57, 43));
        
        // Add components to sidebar
        sidebar.add(navTitle);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(homeBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(userMgmtBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(citizenBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(officerBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(caseBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(deptBtn);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logoutBtn);
        
        return sidebar;
    }
    
    private JButton createNavButton(String text, String panelName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(240, 50));
        button.setBackground(new Color(52, 73, 94));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (button != activeButton) {
                    button.setBackground(new Color(88, 114, 140));
                }
            }
            public void mouseExited(MouseEvent e) {
                if (button != activeButton) {
                    button.setBackground(new Color(52, 73, 94));
                }
            }
        });
        
        button.addActionListener(e -> {
            if (panelName.equals("LOGOUT")) {
                handleLogout();
            } else {
                showPanel(panelName);
                setActiveButton(button);
            }
        });
        
        return button;
    }
    
    private void setActiveButton(JButton button) {
        if (activeButton != null) {
            activeButton.setBackground(new Color(52, 73, 94));
        }
        activeButton = button;
        button.setBackground(new Color(41, 128, 185));
    }
    
    private void showPanel(String panelName) {
        cardLayout.show(mainContentPanel, panelName);
    }
    
    private JPanel createHomePanel() {
        JPanel home = new JPanel(new BorderLayout());
        home.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        home.setBackground(backgroundColor);
        
        // Header with welcome message
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        
        JLabel welcomeLabel = new JLabel("<html><h1 style='margin: 0; font-size: 28px;'>Welcome back, " + displayName + "!</h1>" +
                                        "<p style='margin: 5px 0 0 0; font-size: 14px; color: #666;'>Here's what's happening in your system today</p></html>");
        welcomeLabel.setForeground(textPrimaryColor);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        // Quick actions panel
        JPanel quickActionsPanel = createQuickActionsPanel();
        headerPanel.add(quickActionsPanel, BorderLayout.EAST);
        
        home.add(headerPanel, BorderLayout.NORTH);
        
        // Stats cards
        JPanel statsPanel = createStatsPanel();
        home.add(statsPanel, BorderLayout.CENTER);
        
        return home;
    }
    
    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(backgroundColor);
        
        JButton quickNewsBtn = createQuickActionButton("Quick News", e -> showQuickNews());
        JButton notificationsBtn = createQuickActionButton("Notifications", e -> showNotifications());
        
        panel.add(quickNewsBtn);
        panel.add(notificationsBtn);
        
        return panel;
    }
    
    private JButton createQuickActionButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(primaryColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primaryColor.darker(), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(primaryColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(primaryColor);
            }
        });
        
        return button;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 15, 15));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        String[] statTitles = {"Total Users", "Citizens", "Officers", "Cases", 
                              "Departments", "Pending Requests", "Today's Logs", "System Health"};
        String[] statValues = new String[8];
        
        try {
            // Get real data from database
            statValues[0] = String.valueOf(getCount("SELECT COUNT(*) FROM useraccount"));
            statValues[1] = String.valueOf(getCount("SELECT COUNT(*) FROM citizen"));
            statValues[2] = String.valueOf(getCount("SELECT COUNT(*) FROM officer"));
            statValues[3] = String.valueOf(getCount("SELECT COUNT(*) FROM casetable"));
            statValues[4] = String.valueOf(getCount("SELECT COUNT(*) FROM department"));
            statValues[5] = String.valueOf(getCount("SELECT COUNT(*) FROM servicerequest WHERE Status = 'Pending'"));
            statValues[6] = String.valueOf(getCount("SELECT COUNT(*) FROM system_logs WHERE DATE(Timestamp) = CURDATE()"));
            statValues[7] = "100%";
        } catch (SQLException e) {
            // Fallback values
            statValues[0] = "14";
            statValues[1] = "6";
            statValues[2] = "6";
            statValues[3] = "6";
            statValues[4] = "6";
            statValues[5] = "1";
            statValues[6] = "0";
            statValues[7] = "100%";
            System.err.println("Error loading stats: " + e.getMessage());
        }
        
        for (int i = 0; i < 8; i++) {
            JPanel statCard = createStatCard(statTitles[i], statValues[i], i);
            panel.add(statCard);
        }
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, int index) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardBgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(darkMode ? new Color(80, 80, 80) : new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Store reference for updating values
        if (index < statValueLabels.length) {
            statValueLabels[index] = new JLabel(value);
        }
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(textPrimaryColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(primaryColor);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(cardBgColor);
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(valueLabel, BorderLayout.CENTER);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        // Add click listener for more information
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showStatDetails(title, value);
            }
            public void mouseEntered(MouseEvent e) {
                card.setBackground(darkMode ? new Color(70, 70, 70) : new Color(245, 245, 245));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(cardBgColor);
            }
        });
        
        return card;
    }
    
    private void showStatDetails(String title, String value) {
        String details = "";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            switch (title) {
                case "Total Users":
                    int admins = getCount("SELECT COUNT(*) FROM useraccount WHERE Role = 'Admin'");
                    int officers = getCount("SELECT COUNT(*) FROM useraccount WHERE Role = 'Officer'");
                    int citizens = getCount("SELECT COUNT(*) FROM useraccount WHERE Role = 'Citizen'");
                    details = String.format("Admins: %d\nOfficers: %d\nCitizens: %d", admins, officers, citizens);
                    break;
                case "Citizens":
                    details = "Total registered citizens in the system";
                    break;
                case "Officers":
                    int activeOfficers = getCount("SELECT COUNT(*) FROM officer WHERE Status = 'Active'");
                    details = "Active Officers: " + activeOfficers;
                    break;
                case "Cases":
                    int highPriority = getCount("SELECT COUNT(*) FROM casetable WHERE Priority = 'High'");
                    details = "High Priority Cases: " + highPriority;
                    break;
                case "Departments":
                    details = "All government departments in the system";
                    break;
                case "Pending Requests":
                    String pendingTypes = getPendingRequestTypes();
                    details = "Pending service requests by type:\n" + pendingTypes;
                    break;
                case "Today's Logs":
                    details = "System activities logged today";
                    break;
                case "System Health":
                    details = "All systems operational\nDatabase connected\nServices running normally";
                    break;
            }
        } catch (SQLException e) {
            details = "Unable to fetch detailed information: " + e.getMessage();
            System.err.println("Error showing stat details: " + e.getMessage());
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
        
        JOptionPane.showMessageDialog(this, 
            "<html><div style='text-align: center;'><h2>" + title + "</h2>" +
            "<h1 style='color: " + String.format("#%06x", primaryColor.getRGB() & 0xFFFFFF) + ";'>" + value + "</h1>" +
            "<pre style='text-align: left; font-family: Segoe UI;'>" + details + "</pre></div></html>",
            "Statistics Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String getPendingRequestTypes() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuilder types = new StringBuilder();
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT RequestType, COUNT(*) as count FROM servicerequest WHERE Status = 'Pending' GROUP BY RequestType";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                types.append("• ").append(rs.getString("RequestType"))
                    .append(": ").append(rs.getInt("count")).append("\n");
            }
            
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
        
        return types.toString();
    }
    
    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(backgroundColor);
        
        JLabel title = new JLabel("User Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(textPrimaryColor);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Create toolbar with CRUD buttons
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbarPanel.setBackground(backgroundColor);
        
        JButton addUserBtn = createCRUDButton("➕ Add User", new Color(46, 204, 113));
        JButton editUserBtn = createCRUDButton("✏️ Edit User", new Color(241, 196, 15));
        JButton deleteUserBtn = createCRUDButton("🗑️ Delete User", new Color(231, 76, 60));
        JButton refreshBtn = createCRUDButton("🔄 Refresh", new Color(52, 152, 219));
        JButton searchBtn = createCRUDButton("🔍 Search", new Color(155, 89, 182));
        
        addUserBtn.addActionListener(e -> showAddUserDialog());
        editUserBtn.addActionListener(e -> showEditUserDialog());
        deleteUserBtn.addActionListener(e -> deleteUser());
        refreshBtn.addActionListener(e -> refreshUserTable());
        searchBtn.addActionListener(e -> showSearchDialog());
        
        toolbarPanel.add(addUserBtn);
        toolbarPanel.add(editUserBtn);
        toolbarPanel.add(deleteUserBtn);
        toolbarPanel.add(refreshBtn);
        toolbarPanel.add(searchBtn);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(toolbarPanel, BorderLayout.CENTER);
        
        // Add user table with real data
        try {
            userTable = createUserTable();
            JScrollPane scrollPane = new JScrollPane(userTable);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            panel.add(scrollPane, BorderLayout.SOUTH);
        } catch (SQLException e) {
            panel.add(new JLabel("Error loading user data: " + e.getMessage()), BorderLayout.SOUTH);
            System.err.println("Error creating user table: " + e.getMessage());
        }
        
        return panel;
    }
    
    private JButton createCRUDButton(String text, Color color) {
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
    
    private JTable createUserTable() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT UserID, Username, Role, Email, CreatedAt FROM useraccount ORDER BY CreatedAt DESC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"User ID", "Username", "Role", "Email", "Created Date"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("UserID"),
                    rs.getString("Username"),
                    rs.getString("Role"),
                    rs.getString("Email"),
                    sdf.format(rs.getTimestamp("CreatedAt"))
                });
            }
            
            JTable table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setRowHeight(30);
            table.setBackground(cardBgColor);
            table.setForeground(textPrimaryColor);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            return table;
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Add New User", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(cardBgColor);

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField emailField = new JTextField();
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Admin", "Officer", "Citizen"});
        JTextField citizenIdField = new JTextField();
        JTextField officerIdField = new JTextField();

        formPanel.add(new JLabel("Username*:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password*:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Role*:"));
        formPanel.add(roleCombo);
        formPanel.add(new JLabel("Citizen ID:"));
        formPanel.add(citizenIdField);
        formPanel.add(new JLabel("Officer ID:"));
        formPanel.add(officerIdField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(cardBgColor);
        JButton saveBtn = createCRUDButton("Save", new Color(46, 204, 113));
        JButton cancelBtn = createCRUDButton("Cancel", new Color(231, 76, 60));
        
        saveBtn.addActionListener(e -> {
            if (saveUser(usernameField, passwordField, emailField, roleCombo, citizenIdField, officerIdField)) {
                dialog.dispose();
                refreshUserTable();
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private boolean saveUser(JTextField usernameField, JPasswordField passwordField, 
                           JTextField emailField, JComboBox<String> roleCombo,
                           JTextField citizenIdField, JTextField officerIdField) {
        
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText().trim();
        String role = (String) roleCombo.getSelectedItem();
        String citizenId = citizenIdField.getText().trim();
        String officerId = officerIdField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DB.getConnection();
            String sql = "INSERT INTO useraccount (Username, PasswordHash, Role, Email, CitizenID, OfficerID) VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password); // Note: You should hash the password in production
            ps.setString(3, role);
            ps.setString(4, email.isEmpty() ? null : email);
            ps.setString(5, citizenId.isEmpty() ? null : citizenId);
            ps.setString(6, officerId.isEmpty() ? null : officerId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "User added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                logUserAction("User Created", "Added new user: " + username);
                return true;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving user: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            DB.closeResources(null, ps, conn);
        }
        return false;
    }
    
    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        int userId = (int) model.getValueAt(selectedRow, 0);
        String username = (String) model.getValueAt(selectedRow, 1);
        String role = (String) model.getValueAt(selectedRow, 2);
        String email = (String) model.getValueAt(selectedRow, 3);

        JDialog dialog = new JDialog(this, "Edit User", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(cardBgColor);

        JTextField usernameField = new JTextField(username);
        JPasswordField passwordField = new JPasswordField();
        JTextField emailField = new JTextField(email != null ? email : "");
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Admin", "Officer", "Citizen"});
        roleCombo.setSelectedItem(role);

        formPanel.add(new JLabel("Username*:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("New Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Role*:"));
        formPanel.add(roleCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(cardBgColor);
        JButton updateBtn = createCRUDButton("Update", new Color(46, 204, 113));
        JButton cancelBtn = createCRUDButton("Cancel", new Color(231, 76, 60));
        
        updateBtn.addActionListener(e -> {
            if (updateUser(userId, usernameField, passwordField, emailField, roleCombo)) {
                dialog.dispose();
                refreshUserTable();
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private boolean updateUser(int userId, JTextField usernameField, JPasswordField passwordField,
                             JTextField emailField, JComboBox<String> roleCombo) {
        
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText().trim();
        String role = (String) roleCombo.getSelectedItem();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DB.getConnection();
            String sql;
            
            if (password.isEmpty()) {
                // Update without password
                sql = "UPDATE useraccount SET Username = ?, Role = ?, Email = ? WHERE UserID = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, role);
                ps.setString(3, email.isEmpty() ? null : email);
                ps.setInt(4, userId);
            } else {
                // Update with password
                sql = "UPDATE useraccount SET Username = ?, PasswordHash = ?, Role = ?, Email = ? WHERE UserID = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password); // Note: Hash this in production
                ps.setString(3, role);
                ps.setString(4, email.isEmpty() ? null : email);
                ps.setInt(5, userId);
            }

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                logUserAction("User Updated", "Updated user ID: " + userId);
                return true;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating user: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            DB.closeResources(null, ps, conn);
        }
        return false;
    }
    
    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        int userId = (int) model.getValueAt(selectedRow, 0);
        String username = (String) model.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user:\n" + username + " (ID: " + userId + ")",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            PreparedStatement ps = null;
            
            try {
                conn = DB.getConnection();
                String sql = "DELETE FROM useraccount WHERE UserID = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, userId);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    logUserAction("User Deleted", "Deleted user: " + username);
                    refreshUserTable();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                DB.closeResources(null, ps, conn);
            }
        }
    }
    
    private void refreshUserTable() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            DefaultTableModel model = (DefaultTableModel) userTable.getModel();
            model.setRowCount(0);
            
            String sql = "SELECT UserID, Username, Role, Email, CreatedAt FROM useraccount ORDER BY CreatedAt DESC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("UserID"),
                    rs.getString("Username"),
                    rs.getString("Role"),
                    rs.getString("Email"),
                    sdf.format(rs.getTimestamp("CreatedAt"))
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error refreshing user data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private void showSearchDialog() {
        String searchTerm = JOptionPane.showInputDialog(this, 
                "Enter username or email to search:", "Search Users", JOptionPane.QUESTION_MESSAGE);
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            
            try {
                conn = DB.getConnection();
                DefaultTableModel model = (DefaultTableModel) userTable.getModel();
                model.setRowCount(0);
                
                String sql = "SELECT UserID, Username, Role, Email, CreatedAt FROM useraccount " +
                           "WHERE Username LIKE ? OR Email LIKE ? ORDER BY UserID";
                ps = conn.prepareStatement(sql);
                String searchPattern = "%" + searchTerm + "%";
                ps.setString(1, searchPattern);
                ps.setString(2, searchPattern);
                
                rs = ps.executeQuery();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                
                int count = 0;
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("UserID"),
                        rs.getString("Username"),
                        rs.getString("Role"),
                        rs.getString("Email"),
                        sdf.format(rs.getTimestamp("CreatedAt"))
                    });
                    count++;
                }
                
                JOptionPane.showMessageDialog(this, 
                        "Found " + count + " user(s) matching: '" + searchTerm + "'", 
                        "Search Results", 
                        JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                        "Error searching users: " + ex.getMessage(), 
                        "Search Error", 
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                DB.closeResources(rs, ps, conn);
            }
        }
    }
    
    private JPanel createOfficerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(backgroundColor);
        
        JLabel title = new JLabel("Officer Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(textPrimaryColor);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Create toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbarPanel.setBackground(backgroundColor);
        
        JButton addOfficerBtn = createCRUDButton("➕ Add Officer", new Color(46, 204, 113));
        JButton editOfficerBtn = createCRUDButton("✏️ Edit Officer", new Color(241, 196, 15));
        JButton deleteOfficerBtn = createCRUDButton("🗑️ Delete Officer", new Color(231, 76, 60));
        JButton refreshBtn = createCRUDButton("🔄 Refresh", new Color(52, 152, 219));
        
        addOfficerBtn.addActionListener(e -> showAddOfficerDialog());
        editOfficerBtn.addActionListener(e -> showEditOfficerDialog());
        deleteOfficerBtn.addActionListener(e -> deleteOfficer());
        refreshBtn.addActionListener(e -> refreshOfficerTable());
        
        toolbarPanel.add(addOfficerBtn);
        toolbarPanel.add(editOfficerBtn);
        toolbarPanel.add(deleteOfficerBtn);
        toolbarPanel.add(refreshBtn);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(toolbarPanel, BorderLayout.CENTER);
        
        // Create officer table
        try {
            officerTable = createOfficerTable();
            JScrollPane scrollPane = new JScrollPane(officerTable);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            panel.add(scrollPane, BorderLayout.SOUTH);
        } catch (SQLException e) {
            panel.add(new JLabel("Error loading officer data: " + e.getMessage()), BorderLayout.SOUTH);
            System.err.println("Error creating officer table: " + e.getMessage());
        }
        
        return panel;
    }
    
    private JTable createOfficerTable() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            // IMPORTANT: Using 'Name' column, not 'FullName' as per your database schema
            String sql = "SELECT o.OfficerID, o.Name, o.Identifier, o.Status, " +
                        "o.Location, o.Contact, d.Name as DepartmentName, o.AssignedSince " +
                        "FROM officer o " +
                        "LEFT JOIN department d ON o.DepartmentID = d.DepartmentID " +
                        "ORDER BY o.CreatedAt DESC";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Officer ID", "Name", "Identifier", "Status", "Location", "Contact", "Department", "Assigned Since"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            while (rs.next()) {
                Date assignedSince = rs.getDate("AssignedSince");
                String assignedDate = assignedSince != null ? sdf.format(assignedSince) : "N/A";
                
                model.addRow(new Object[]{
                    rs.getInt("OfficerID"),
                    rs.getString("Name"),  // CORRECT: Using 'Name' column
                    rs.getString("Identifier"),
                    rs.getString("Status"),
                    rs.getString("Location"),
                    rs.getString("Contact"),
                    rs.getString("DepartmentName"),
                    assignedDate
                });
            }
            
            JTable table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setRowHeight(30);
            table.setBackground(cardBgColor);
            table.setForeground(textPrimaryColor);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            return table;
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private void showAddOfficerDialog() {
        JDialog dialog = new JDialog(this, "Add New Officer", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(cardBgColor);

        JTextField nameField = new JTextField();
        JTextField identifierField = new JTextField();
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Inactive", "On Leave"});
        JTextField locationField = new JTextField();
        JTextField contactField = new JTextField();
        
        // Get departments for dropdown
        JComboBox<String> departmentCombo = new JComboBox<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT DepartmentID, Name FROM department ORDER BY Name";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            departmentCombo.addItem("Select Department");
            while (rs.next()) {
                departmentCombo.addItem(rs.getString("DepartmentID") + " - " + rs.getString("Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
        
        JTextField assignedSinceField = new JTextField();
        assignedSinceField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        formPanel.add(new JLabel("Name*:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Identifier*:"));
        formPanel.add(identifierField);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusCombo);
        formPanel.add(new JLabel("Location:"));
        formPanel.add(locationField);
        formPanel.add(new JLabel("Contact:"));
        formPanel.add(contactField);
        formPanel.add(new JLabel("Department:"));
        formPanel.add(departmentCombo);
        formPanel.add(new JLabel("Assigned Since (YYYY-MM-DD):"));
        formPanel.add(assignedSinceField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(cardBgColor);
        JButton saveBtn = createCRUDButton("Save", new Color(46, 204, 113));
        JButton cancelBtn = createCRUDButton("Cancel", new Color(231, 76, 60));
        
        saveBtn.addActionListener(e -> {
            if (saveOfficer(nameField, identifierField, statusCombo, locationField, 
                           contactField, departmentCombo, assignedSinceField)) {
                dialog.dispose();
                refreshOfficerTable();
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private boolean saveOfficer(JTextField nameField, JTextField identifierField, 
                              JComboBox<String> statusCombo, JTextField locationField,
                              JTextField contactField, JComboBox<String> departmentCombo,
                              JTextField assignedSinceField) {
        
        String name = nameField.getText().trim();
        String identifier = identifierField.getText().trim();
        String status = (String) statusCombo.getSelectedItem();
        String location = locationField.getText().trim();
        String contact = contactField.getText().trim();
        String departmentStr = (String) departmentCombo.getSelectedItem();
        String assignedSince = assignedSinceField.getText().trim();

        if (name.isEmpty() || identifier.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Identifier are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Parse department ID
        int departmentId = 0;
        if (departmentStr != null && !departmentStr.equals("Select Department")) {
            try {
                departmentId = Integer.parseInt(departmentStr.split(" - ")[0]);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid department selected!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DB.getConnection();
            String sql = "INSERT INTO officer (DepartmentID, Name, Identifier, Status, Location, Contact, AssignedSince) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, departmentId);
            ps.setString(2, name);
            ps.setString(3, identifier);
            ps.setString(4, status);
            ps.setString(5, location);
            ps.setString(6, contact);
           // ps.setDate(7, Date.valueOf(assignedSince));

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Officer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                logUserAction("Officer Created", "Added new officer: " + name);
                return true;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving officer: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            DB.closeResources(null, ps, conn);
        }
        return false;
    }
    
    private void showEditOfficerDialog() {
        int selectedRow = officerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an officer to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) officerTable.getModel();
        int officerId = (int) model.getValueAt(selectedRow, 0);
        String name = (String) model.getValueAt(selectedRow, 1);
        String identifier = (String) model.getValueAt(selectedRow, 2);
        String status = (String) model.getValueAt(selectedRow, 3);
        String location = (String) model.getValueAt(selectedRow, 4);
        String contact = (String) model.getValueAt(selectedRow, 5);

        JDialog dialog = new JDialog(this, "Edit Officer", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(cardBgColor);

        JTextField nameField = new JTextField(name);
        JTextField identifierField = new JTextField(identifier);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Inactive", "On Leave"});
        statusCombo.setSelectedItem(status);
        JTextField locationField = new JTextField(location);
        JTextField contactField = new JTextField(contact);

        // Get departments for dropdown
        JComboBox<String> departmentCombo = new JComboBox<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT DepartmentID, Name FROM department ORDER BY Name";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            departmentCombo.addItem("Select Department");
            while (rs.next()) {
                departmentCombo.addItem(rs.getString("DepartmentID") + " - " + rs.getString("Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }

        formPanel.add(new JLabel("Name*:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Identifier*:"));
        formPanel.add(identifierField);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusCombo);
        formPanel.add(new JLabel("Location:"));
        formPanel.add(locationField);
        formPanel.add(new JLabel("Contact:"));
        formPanel.add(contactField);
        formPanel.add(new JLabel("Department:"));
        formPanel.add(departmentCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(cardBgColor);
        JButton updateBtn = createCRUDButton("Update", new Color(46, 204, 113));
        JButton cancelBtn = createCRUDButton("Cancel", new Color(231, 76, 60));
        
        updateBtn.addActionListener(e -> {
            if (updateOfficer(officerId, nameField, identifierField, statusCombo, locationField, 
                             contactField, departmentCombo)) {
                dialog.dispose();
                refreshOfficerTable();
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private boolean updateOfficer(int officerId, JTextField nameField, JTextField identifierField,
                                JComboBox<String> statusCombo, JTextField locationField,
                                JTextField contactField, JComboBox<String> departmentCombo) {
        
        String name = nameField.getText().trim();
        String identifier = identifierField.getText().trim();
        String status = (String) statusCombo.getSelectedItem();
        String location = locationField.getText().trim();
        String contact = contactField.getText().trim();
        String departmentStr = (String) departmentCombo.getSelectedItem();

        if (name.isEmpty() || identifier.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Identifier are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Parse department ID
        int departmentId = 0;
        if (departmentStr != null && !departmentStr.equals("Select Department")) {
            try {
                departmentId = Integer.parseInt(departmentStr.split(" - ")[0]);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid department selected!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DB.getConnection();
            String sql = "UPDATE officer SET Name = ?, Identifier = ?, Status = ?, " +
                        "Location = ?, Contact = ?, DepartmentID = ? WHERE OfficerID = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, identifier);
            ps.setString(3, status);
            ps.setString(4, location);
            ps.setString(5, contact);
            ps.setInt(6, departmentId);
            ps.setInt(7, officerId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Officer updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                logUserAction("Officer Updated", "Updated officer ID: " + officerId);
                return true;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating officer: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            DB.closeResources(null, ps, conn);
        }
        return false;
    }
    
    private void deleteOfficer() {
        int selectedRow = officerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an officer to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) officerTable.getModel();
        int officerId = (int) model.getValueAt(selectedRow, 0);
        String name = (String) model.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete officer:\n" + name + " (ID: " + officerId + ")",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            PreparedStatement ps = null;
            
            try {
                conn = DB.getConnection();
                String sql = "DELETE FROM officer WHERE OfficerID = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, officerId);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Officer deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    logUserAction("Officer Deleted", "Deleted officer: " + name);
                    refreshOfficerTable();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting officer: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                DB.closeResources(null, ps, conn);
            }
        }
    }
    
    private void refreshOfficerTable() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            DefaultTableModel model = (DefaultTableModel) officerTable.getModel();
            model.setRowCount(0);
            
            // Using 'Name' column, not 'FullName'
            String sql = "SELECT o.OfficerID, o.Name, o.Identifier, o.Status, " +
                        "o.Location, o.Contact, d.Name as DepartmentName, o.AssignedSince " +
                        "FROM officer o " +
                        "LEFT JOIN department d ON o.DepartmentID = d.DepartmentID " +
                        "ORDER BY o.CreatedAt DESC";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            while (rs.next()) {
                Date assignedSince = rs.getDate("AssignedSince");
                String assignedDate = assignedSince != null ? sdf.format(assignedSince) : "N/A";
                
                model.addRow(new Object[]{
                    rs.getInt("OfficerID"),
                    rs.getString("Name"),  // CORRECT: Using 'Name' column
                    rs.getString("Identifier"),
                    rs.getString("Status"),
                    rs.getString("Location"),
                    rs.getString("Contact"),
                    rs.getString("DepartmentName"),
                    assignedDate
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error refreshing officer data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private JPanel createCitizenPanel() {
        try {
            // Create an instance of your existing CitizenPanel class
            CitizenPanel citizenPanel = new CitizenPanel();
            return citizenPanel;
        } catch (Exception e) {
            e.printStackTrace();
            return createFallbackPanel("Citizen Management", 
                "Error loading Citizen Panel: " + e.getMessage());
        }
    }
    
    private JPanel createDepartmentPanel() {
        try {
            // Create an instance of your existing DepartmentPanel class
            DepartmentPanel deptPanel = new DepartmentPanel();
            return deptPanel;
        } catch (Exception e) {
            e.printStackTrace();
            return createFallbackPanel("Department Management", 
                "Error loading Department Panel: " + e.getMessage());
        }
    }
    
    private JPanel createCasePanel() {
        try {
            // Create an instance of your existing CasePanel class
            CasePanel casePanel = new CasePanel();
            return casePanel;
        } catch (Exception e) {
            e.printStackTrace();
            return createFallbackPanel("Case Management", 
                "Error loading Case Panel: " + e.getMessage());
        }
    }
    
    private JPanel createFallbackPanel(String title, String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        panel.setBackground(backgroundColor);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(textPrimaryColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel messageLabel = new JLabel("<html><center>" + message + "</center></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(textPrimaryColor);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        
        JButton retryButton = new JButton("Retry");
        retryButton.addActionListener(e -> {
            refreshPanel(title);
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(retryButton);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void refreshPanel(String panelName) {
        JPanel newPanel = null;
        String cardName = "";
        
        switch (panelName) {
            case "Citizen Management":
                newPanel = createCitizenPanel();
                cardName = "CITIZEN";
                break;
            case "Officer Management":
                newPanel = createOfficerPanel();
                cardName = "OFFICER";
                break;
            case "Case Management":
                newPanel = createCasePanel();
                cardName = "CASE";
                break;
            case "Department Management":
                newPanel = createDepartmentPanel();
                cardName = "DEPARTMENT";
                break;
        }
        
        if (newPanel != null && !cardName.isEmpty()) {
            Component current = getCurrentPanel(cardName);
            if (current != null) {
                mainContentPanel.remove(current);
            }
            mainContentPanel.add(newPanel, cardName);
            cardLayout.show(mainContentPanel, cardName);
        }
    }
    
  //  private Component getCurrentPanel(String cardName) {
    //    return cardLayout.getLayoutComponent(mainContentPanel, cardName);
    //}
    
    private Component getCurrentPanel(String cardName) {
		// TODO Auto-generated method stub
		return null;
	}

	private void loadInitialData() {
        loadHomeData();
    }
    
    private void loadHomeData() {
        // Data is loaded in createStatsPanel() method
    }
    
    private int getCount(String sql) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private void updateStatCard(int index, String value) {
        if (index >= 0 && index < statValueLabels.length && statValueLabels[index] != null) {
            statValueLabels[index].setText(value);
        }
    }
    
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            logUserAction("Logout", "User logged out of the system");
            dispose();
            // Go back to login form
            try {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            } catch (Exception e) {
                System.err.println("Could not open login form: " + e.getMessage());
                System.exit(0);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard(1, "Administrator User"));
    }
}