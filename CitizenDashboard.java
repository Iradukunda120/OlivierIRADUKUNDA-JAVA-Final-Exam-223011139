package GOVTECHFORM;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CitizenDashboard extends JFrame {
    private int citizenId;
    private String username;
    private String fullName;
    
    // Theme variables
    private boolean darkMode = false;
    private final Color PRIMARY_COLOR_LIGHT = new Color(41, 128, 185);
    private final Color PRIMARY_COLOR_DARK = new Color(33, 47, 60);
    private final Color SIDEBAR_COLOR_LIGHT = new Color(44, 62, 80);
    private final Color SIDEBAR_COLOR_DARK = new Color(33, 47, 60);
    private final Color BACKGROUND_LIGHT = new Color(236, 240, 241);
    private final Color BACKGROUND_DARK = new Color(45, 45, 45);
    private final Color CARD_BG_LIGHT = Color.green;
    private final Color CARD_BG_DARK = new Color(60, 60, 60);
    private final Color TEXT_PRIMARY_LIGHT = new Color(44, 62, 80);
    private final Color TEXT_PRIMARY_DARK = Color.green;
    
    private Color primaryColor;
    private Color sidebarColor;
    private Color backgroundColor;
    private Color cardBgColor;
    private Color textPrimaryColor;
    
    // Main panels
    private JPanel sidebarPanel, mainContentPanel, headerPanel;
    private CardLayout cardLayout;
    
    // Navigation buttons
    private JButton homeBtn, profileBtn, servicesBtn, requestsBtn, documentsBtn, notificationsBtn, logoutBtn;
    private JButton activeButton;
    
    // Top right corner components
    private JButton userMenuBtn;
    private JPopupMenu userPopupMenu;

    public CitizenDashboard(int citizenId, String username) {
        this.citizenId = citizenId;
        this.username = username;
        this.fullName = getCitizenName(citizenId);
        
        initializeTheme();
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
    
    private String getCitizenName(int citizenId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT FullName FROM citizen WHERE CitizenID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, citizenId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("FullName");
            }
        } catch (SQLException e) {
            System.err.println("Error getting citizen name: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
        return "Citizen";
    }
    
    private void setupUI() {
        setTitle("GovTech Solutions - Citizen Portal");
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
        mainContentPanel.add(createProfilePanel(), "PROFILE");
        mainContentPanel.add(createServicesPanel(), "SERVICES");
        mainContentPanel.add(createMyRequestsPanel(), "REQUESTS");
        mainContentPanel.add(createMyDocumentsPanel(), "DOCUMENTS");
        mainContentPanel.add(createNotificationsPanel(), "NOTIFICATIONS");
        
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
        logoLabel.setForeground(Color.green);
        
        JLabel titleLabel = new JLabel("Citizen Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(200, 200, 200));
        
        leftPanel.add(logoLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        leftPanel.add(titleLabel);
        
        // Right side - User controls
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(primaryColor);
        
        // User Menu Button
        userMenuBtn = new JButton(fullName + " ▼");
        userMenuBtn.setPreferredSize(new Dimension(180, 40));
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
        popupMenu.setPreferredSize(new Dimension(200, 250));
        
        popupMenu.setBackground(darkMode ? cardBgColor : Color.green);
        popupMenu.setBorder(BorderFactory.createLineBorder(darkMode ? Color.DARK_GRAY : Color.LIGHT_GRAY, 1));
        
        // User Info Section
        JMenuItem userInfoItem = new JMenuItem("<html><div style='text-align: center;'><b>" + username + "</b><br>" + 
                                              "<span style='font-size: 11px; color: #666;'>" + fullName + "</span></div></html>");
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
        JMenuItem profileItem = createMenuItem("👤 My Profile", e -> {
            showPanel("PROFILE");
            setActiveButton(profileBtn);
            userPopupMenu.setVisible(false);
        });
        
        popupMenu.add(dashboardItem);
        popupMenu.add(profileItem);
        popupMenu.addSeparator();
        
        // Theme toggle
        JMenuItem themeItem = createMenuItem(darkMode ? "☀️ Light Mode" : "🌙 Dark Mode", e -> {
            toggleTheme();
            userPopupMenu.setVisible(false);
        });
        
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
        
        userPopupMenu = createUserPopupMenu();
        logCitizenAction("Theme Change", "Switched to " + (darkMode ? "dark" : "light") + " theme");
    }
    
    private void updateUITheme() {
        headerPanel.setBackground(primaryColor);
        updatePanelTheme(headerPanel);
        
        sidebarPanel.setBackground(sidebarColor);
        updatePanelTheme(sidebarPanel);
        
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
            } else if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setForeground(textPrimaryColor);
                button.setBackground(cardBgColor);
            }
        }
    }
    
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(sidebarColor);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(25, 15, 25, 15));
        
        // Navigation section title
        JLabel navTitle = new JLabel("CITIZEN SERVICES");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        navTitle.setForeground(new Color(170, 170, 170));
        navTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        navTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Navigation buttons
        homeBtn = createNavButton("Dashboard", "HOME");
        profileBtn = createNavButton("My Profile", "PROFILE");
        servicesBtn = createNavButton("Available Services", "SERVICES");
        requestsBtn = createNavButton("My Requests", "REQUESTS");
        documentsBtn = createNavButton("My Documents", "DOCUMENTS");
        notificationsBtn = createNavButton("Notifications", "NOTIFICATIONS");
        
        // Logout button at bottom
        logoutBtn = createNavButton("Logout", "LOGOUT");
        logoutBtn.setBackground(new Color(192, 57, 43));
        
        // Add components to sidebar
        sidebar.add(navTitle);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(homeBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(profileBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(servicesBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(requestsBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(documentsBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(notificationsBtn);
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
        
        JLabel welcomeLabel = new JLabel("<html><h1 style='margin: 0; font-size: 28px;'>Welcome, " + fullName + "!</h1>" +
                                        "<p style='margin: 5px 0 0 0; font-size: 14px; color: #666;'>Access government services and manage your requests</p></html>");
        welcomeLabel.setForeground(textPrimaryColor);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        // Stats panel
        JPanel statsPanel = createStatsPanel();
        headerPanel.add(statsPanel, BorderLayout.EAST);
        
        home.add(headerPanel, BorderLayout.NORTH);
        
        // Quick links panel
        JPanel quickLinksPanel = createQuickLinksPanel();
        home.add(quickLinksPanel, BorderLayout.CENTER);
        
        return home;
    }
    
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setBackground(backgroundColor);
        
        int pendingRequests = getPendingRequestsCount();
        int approvedRequests = getApprovedRequestsCount();
        int totalDocuments = getDocumentsCount();
        
        statsPanel.add(createStatCard("📋 Pending", String.valueOf(pendingRequests), primaryColor));
        statsPanel.add(createStatCard("✅ Approved", String.valueOf(approvedRequests), new Color(46, 204, 113)));
        statsPanel.add(createStatCard("📄 Documents", String.valueOf(totalDocuments), new Color(155, 89, 182)));
        
        return statsPanel;
    }
    
    private int getPendingRequestsCount() {
        return getCitizenCount("SELECT COUNT(*) FROM servicerequest WHERE CitizenID = ? AND Status = 'Pending'", citizenId);
    }
    
    private int getApprovedRequestsCount() {
        return getCitizenCount("SELECT COUNT(*) FROM servicerequest WHERE CitizenID = ? AND Status = 'Approved'", citizenId);
    }
    
    private int getDocumentsCount() {
        return getCitizenCount("SELECT COUNT(*) FROM document WHERE CitizenID = ?", citizenId);
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(valueLabel);
        
        return card;
    }
    
    private JPanel createQuickLinksPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 20, 20));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Create quick link cards
        panel.add(createQuickLinkCard("My Profile", "View and update your personal information", "👤", e -> {
            showPanel("PROFILE");
            setActiveButton(profileBtn);
        }));
        
        panel.add(createQuickLinkCard("Available Services", "Browse available government services", "🏛️", e -> {
            showPanel("SERVICES");
            setActiveButton(servicesBtn);
        }));
        
        panel.add(createQuickLinkCard("Submit Request", "Submit a new service request", "📝", e -> {
            showNewRequestDialog();
        }));
        
        panel.add(createQuickLinkCard("My Requests", "Track your submitted requests", "📋", e -> {
            showPanel("REQUESTS");
            setActiveButton(requestsBtn);
        }));
        
        panel.add(createQuickLinkCard("My Documents", "Access your documents and certificates", "📄", e -> {
            showPanel("DOCUMENTS");
            setActiveButton(documentsBtn);
        }));
        
        panel.add(createQuickLinkCard("Notifications", "View system notifications", "🔔", e -> {
            showPanel("NOTIFICATIONS");
            setActiveButton(notificationsBtn);
        }));
        
        return panel;
    }
    
    private JPanel createQuickLinkCard(String title, String description, String icon, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardBgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(darkMode ? new Color(80, 80, 80) : new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        iconLabel.setForeground(primaryColor);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(textPrimaryColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        
        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(darkMode ? new Color(180, 180, 180) : new Color(100, 100, 100));
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setBackground(cardBgColor);
        contentPanel.add(iconLabel, BorderLayout.NORTH);
        contentPanel.add(titleLabel, BorderLayout.CENTER);
        contentPanel.add(descLabel, BorderLayout.SOUTH);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        // Add click listener
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(new ActionEvent(card, ActionEvent.ACTION_PERFORMED, ""));
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
    
    private void showNewRequestDialog() {
        JDialog dialog = new JDialog(this, "Submit New Service Request", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(cardBgColor);

        JComboBox<String> requestTypeCombo = new JComboBox<>(new String[]{
            "Health Insurance Registration",
            "School Admission Certificate",
            "Driver License Renewal",
            "Farming Subsidy Request",
            "Tax Payment Assistance",
            "Trade License Application",
            "Birth Certificate",
            "Marriage Certificate",
            "Death Certificate",
            "Business Registration"
        });
        
        JTextArea descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        
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
                departmentCombo.addItem(rs.getString("Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }

        formPanel.add(new JLabel("Request Type*:"));
        formPanel.add(requestTypeCombo);
        formPanel.add(new JLabel("Department*:"));
        formPanel.add(departmentCombo);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descriptionScroll);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(cardBgColor);
        JButton submitBtn = createActionButton("Submit Request", new Color(46, 204, 113));
        JButton cancelBtn = createActionButton("Cancel", new Color(231, 76, 60));
        
        submitBtn.addActionListener(e -> {
            if (submitServiceRequest((String) requestTypeCombo.getSelectedItem(), 
                                   (String) departmentCombo.getSelectedItem(), 
                                   descriptionArea.getText())) {
                dialog.dispose();
                showPanel("REQUESTS");
                setActiveButton(requestsBtn);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private boolean submitServiceRequest(String requestType, String departmentName, String description) {
        if (requestType == null || requestType.isEmpty() || 
            departmentName == null || departmentName.equals("Select Department")) {
            JOptionPane.showMessageDialog(this, "Request type and department are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DB.getConnection();
            
            // Get department ID
            String deptSql = "SELECT DepartmentID FROM department WHERE Name = ?";
            ps = conn.prepareStatement(deptSql);
            ps.setString(1, departmentName);
            ResultSet rs = ps.executeQuery();
            
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Invalid department selected!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            int departmentId = rs.getInt("DepartmentID");
            
            // Insert service request
            String sql = "INSERT INTO servicerequest (CitizenID, DepartmentID, RequestType, Description, Status) " +
                        "VALUES (?, ?, ?, ?, 'Pending')";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, citizenId);
            ps.setInt(2, departmentId);
            ps.setString(3, requestType);
            ps.setString(4, description);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Service request submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                logCitizenAction("Service Request", "Submitted new request: " + requestType);
                return true;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error submitting request: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            DB.closeResources(null, ps, conn);
        }
        return false;
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
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(backgroundColor);
        
        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(textPrimaryColor);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Get citizen details
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        infoPanel.setBackground(backgroundColor);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT * FROM citizen WHERE CitizenID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, citizenId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                infoPanel.add(createInfoLabel("Full Name:"));
                infoPanel.add(createInfoValue(rs.getString("FullName")));
                
                infoPanel.add(createInfoLabel("National ID:"));
                infoPanel.add(createInfoValue(rs.getString("NationalID")));
                
                infoPanel.add(createInfoLabel("Address:"));
                infoPanel.add(createInfoValue(rs.getString("Address")));
                
                infoPanel.add(createInfoLabel("Contact:"));
                infoPanel.add(createInfoValue(rs.getString("Contact")));
                
                infoPanel.add(createInfoLabel("Registered Since:"));
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
                infoPanel.add(createInfoValue(sdf.format(rs.getTimestamp("CreatedAt"))));
            }
            
        } catch (SQLException e) {
            infoPanel.add(new JLabel("Error loading profile information: " + e.getMessage()));
            e.printStackTrace();
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
        
        JScrollPane scrollPane = new JScrollPane(infoPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(backgroundColor);
        
        // Edit profile button
        JButton editProfileBtn = createActionButton("Edit Profile", new Color(52, 152, 219));
        editProfileBtn.addActionListener(e -> showEditProfileDialog());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(editProfileBtn);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(textPrimaryColor);
        return label;
    }
    
    private JLabel createInfoValue(String value) {
        JLabel label = new JLabel(value != null ? value : "Not provided");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(darkMode ? new Color(180, 180, 180) : new Color(100, 100, 100));
        return label;
    }
    
    private void showEditProfileDialog() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT * FROM citizen WHERE CitizenID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, citizenId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                JTextField fullNameField = new JTextField(rs.getString("FullName"), 20);
                JTextField nationalIdField = new JTextField(rs.getString("NationalID"), 20);
                JTextField addressField = new JTextField(rs.getString("Address"), 20);
                JTextField contactField = new JTextField(rs.getString("Contact"), 20);
                
                JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
                panel.setBackground(cardBgColor);
                panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                panel.add(new JLabel("Full Name*:"));
                panel.add(fullNameField);
                panel.add(new JLabel("National ID*:"));
                panel.add(nationalIdField);
                panel.add(new JLabel("Address:"));
                panel.add(addressField);
                panel.add(new JLabel("Contact:"));
                panel.add(contactField);
                
                int result = JOptionPane.showConfirmDialog(this, panel, "Edit Profile",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                
                if (result == JOptionPane.OK_OPTION) {
                    updateCitizenProfile(fullNameField.getText(), nationalIdField.getText(), 
                                        addressField.getText(), contactField.getText());
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading profile: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private void updateCitizenProfile(String fullName, String nationalId, String address, String contact) {
        if (fullName.isEmpty() || nationalId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full Name and National ID are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DB.getConnection();
            String sql = "UPDATE citizen SET FullName = ?, NationalID = ?, Address = ?, Contact = ? WHERE CitizenID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, fullName);
            pstmt.setString(2, nationalId);
            pstmt.setString(3, address);
            pstmt.setString(4, contact);
            pstmt.setInt(5, citizenId);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                this.fullName = fullName;
                userMenuBtn.setText(fullName + " ▼");
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                logCitizenAction("Profile Update", "Updated citizen profile");
                showPanel("PROFILE");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating profile: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DB.closeResources(null, pstmt, conn);
        }
    }
    
    private JPanel createServicesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(backgroundColor);
        
        JLabel title = new JLabel("Available Government Services");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(textPrimaryColor);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Create services cards
        JPanel servicesPanel = new JPanel(new GridLayout(0, 1, 15, 15));
        servicesPanel.setBackground(backgroundColor);
        
        String[][] services = {
            {"🏥", "Health Insurance Registration", "Register for national health insurance coverage"},
            {"📜", "Birth Certificate", "Apply for official birth registration document"},
            {"🚗", "Driver License Renewal", "Renew your driver's license"},
            {"🎓", "School Admission Certificate", "Get official school admission letter"},
            {"💼", "Business Registration", "Register a new business or company"},
            {"🏠", "Land Title Transfer", "Transfer land ownership documents"},
            {"💰", "Tax Clearance Certificate", "Get certificate of tax compliance"},
            {"🏗️", "Building Permit", "Apply for construction permission"},
            {"📘", "Passport Application", "Apply for a new passport"},
            {"💍", "Marriage Certificate", "Register marriage officially"},
            {"🌾", "Farming Subsidy Request", "Apply for agricultural subsidies"},
            {"📋", "Trade License Application", "Apply for business trade license"}
        };
        
        for (String[] service : services) {
            servicesPanel.add(createServiceCard(service[0], service[1], service[2]));
        }
        
        JScrollPane scrollPane = new JScrollPane(servicesPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(backgroundColor);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createServiceCard(String icon, String title, String description) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(cardBgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(darkMode ? new Color(80, 80, 80) : new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        iconLabel.setForeground(primaryColor);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(cardBgColor);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(textPrimaryColor);
        
        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(darkMode ? new Color(180, 180, 180) : new Color(100, 100, 100));
        descLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(descLabel, BorderLayout.CENTER);
        
        JButton requestBtn = new JButton("Request Service");
        requestBtn.setBackground(primaryColor);
        requestBtn.setForeground(Color.WHITE);
        requestBtn.setFocusPainted(false);
        requestBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        requestBtn.addActionListener(e -> {
            showServiceRequestDialog(title);
        });
        
        card.add(iconLabel, BorderLayout.WEST);
        card.add(contentPanel, BorderLayout.CENTER);
        card.add(requestBtn, BorderLayout.EAST);
        
        return card;
    }
    
    private void showServiceRequestDialog(String serviceName) {
        // Reuse the existing dialog
        JDialog dialog = new JDialog(this, "Request Service: " + serviceName, true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(cardBgColor);
        
        JTextArea descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        
        formPanel.add(new JLabel("Service:"));
        formPanel.add(new JLabel(serviceName));
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descriptionScroll);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(cardBgColor);
        JButton submitBtn = createActionButton("Submit Request", new Color(46, 204, 113));
        JButton cancelBtn = createActionButton("Cancel", new Color(231, 76, 60));
        
        submitBtn.addActionListener(e -> {
            // Simplified submission - in real app, you would get department from service
            if (submitServiceRequest(serviceName, "General Services", descriptionArea.getText())) {
                dialog.dispose();
                showPanel("REQUESTS");
                setActiveButton(requestsBtn);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private JPanel createMyRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(backgroundColor);
        
        JLabel title = new JLabel("My Service Requests");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(textPrimaryColor);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Create requests table
        try {
            JTable requestsTable = createRequestsTable();
            JScrollPane scrollPane = new JScrollPane(requestsTable);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            panel.add(title, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);
        } catch (SQLException e) {
            panel.add(new JLabel("Error loading requests: " + e.getMessage()), BorderLayout.CENTER);
            System.err.println("Error creating requests table: " + e.getMessage());
        }
        
        // New request button
        JButton newRequestBtn = createActionButton("➕ New Service Request", new Color(46, 204, 113));
        newRequestBtn.addActionListener(e -> showNewRequestDialog());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(newRequestBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JTable createRequestsTable() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT sr.ServiceRequestID, sr.RequestType, d.Name as Department, " +
                        "sr.Status, sr.CreatedAt, sr.Description " +
                        "FROM servicerequest sr " +
                        "JOIN department d ON sr.DepartmentID = d.DepartmentID " +
                        "WHERE sr.CitizenID = ? " +
                        "ORDER BY sr.CreatedAt DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, citizenId);
            rs = pstmt.executeQuery();
            
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Request ID", "Service Type", "Department", "Status", "Date Submitted", "Description"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("ServiceRequestID"),
                    rs.getString("RequestType"),
                    rs.getString("Department"),
                    rs.getString("Status"),
                    sdf.format(rs.getTimestamp("CreatedAt")),
                    rs.getString("Description")
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
    
    private JPanel createMyDocumentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(backgroundColor);
        
        JLabel title = new JLabel("My Documents");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(textPrimaryColor);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Create documents list
        try {
            JTable documentsTable = createDocumentsTable();
            JScrollPane scrollPane = new JScrollPane(documentsTable);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            panel.add(title, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);
        } catch (SQLException e) {
            panel.add(new JLabel("Error loading documents: " + e.getMessage()), BorderLayout.CENTER);
            System.err.println("Error creating documents table: " + e.getMessage());
        }
        
        // Download button
        JButton downloadBtn = createActionButton("⬇️ Download Selected", new Color(52, 152, 219));
        downloadBtn.addActionListener(e -> downloadDocument());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(downloadBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JTable createDocumentsTable() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT d.DocumentID, d.FileName, d.FileType, ct.CaseType, " +
                        "d.CreatedAt, d.FilePath " +
                        "FROM document d " +
                        "LEFT JOIN casetable ct ON d.CaseID = ct.CaseID " +
                        "WHERE d.CitizenID = ? " +
                        "ORDER BY d.CreatedAt DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, citizenId);
            rs = pstmt.executeQuery();
            
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Document ID", "File Name", "Type", "Related Case", "Date Uploaded", "Status"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("DocumentID"),
                    rs.getString("FileName"),
                    rs.getString("FileType"),
                    rs.getString("CaseType"),
                    sdf.format(rs.getTimestamp("CreatedAt")),
                    "Available"
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
    
    private void downloadDocument() {
        JOptionPane.showMessageDialog(this, "Download functionality would be implemented here", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private JPanel createNotificationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(backgroundColor);
        
        JLabel title = new JLabel("Notifications");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(textPrimaryColor);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Create notifications list
        JTextArea notificationsArea = new JTextArea();
        notificationsArea.setEditable(false);
        notificationsArea.setBackground(cardBgColor);
        notificationsArea.setForeground(textPrimaryColor);
        notificationsArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        notificationsArea.setLineWrap(true);
        notificationsArea.setWrapStyleWord(true);
        
        // Get notifications
        String notifications = getCitizenNotifications();
        notificationsArea.setText(notifications);
        
        JScrollPane scrollPane = new JScrollPane(notificationsArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(darkMode ? Color.DARK_GRAY : Color.LIGHT_GRAY));
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        // Clear button
        JButton clearBtn = createActionButton("Clear All", new Color(231, 76, 60));
        clearBtn.addActionListener(e -> notificationsArea.setText(""));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(clearBtn);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private String getCitizenNotifications() {
        StringBuilder content = new StringBuilder();
        content.append("YOUR NOTIFICATIONS\n\n");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            
            // Get pending requests count
            int pendingRequests = getCitizenCount("SELECT COUNT(*) FROM servicerequest WHERE CitizenID = ? AND Status = 'Pending'", citizenId);
            if (pendingRequests > 0) {
                content.append("📋 PENDING REQUESTS: ").append(pendingRequests).append("\n\n");
                
                String pendingSql = "SELECT RequestType, CreatedAt FROM servicerequest " +
                                  "WHERE CitizenID = ? AND Status = 'Pending' ORDER BY CreatedAt DESC LIMIT 3";
                pstmt = conn.prepareStatement(pendingSql);
                pstmt.setInt(1, citizenId);
                rs = pstmt.executeQuery();
                
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
                while (rs.next()) {
                    content.append("• ").append(rs.getString("RequestType"))
                          .append(" (").append(sdf.format(rs.getTimestamp("CreatedAt"))).append(")\n");
                }
                content.append("\n");
            }
            
            // Get approved requests
            String approvedSql = "SELECT RequestType, CreatedAt FROM servicerequest " +
                               "WHERE CitizenID = ? AND Status = 'Approved' ORDER BY CreatedAt DESC LIMIT 3";
            pstmt = conn.prepareStatement(approvedSql);
            pstmt.setInt(1, citizenId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                content.append("✅ RECENTLY APPROVED:\n");
                do {
                    content.append("• ").append(rs.getString("RequestType")).append("\n");
                } while (rs.next());
                content.append("\n");
            }
            
            // System messages
            content.append("📢 SYSTEM MESSAGES:\n");
            content.append("• Your account is active and in good standing\n");
            content.append("• All government services are available\n");
            content.append("• Contact support for any assistance\n");
            
        } catch (SQLException e) {
            content.append("• System notifications loaded\n");
            content.append("• All services available\n");
            System.err.println("Error getting notifications: " + e.getMessage());
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
        
        content.append("\nLast updated: ").append(new SimpleDateFormat("MMM dd, yyyy HH:mm").format(new Date()));
        return content.toString();
    }
    
    private int getCitizenCount(String sql, int citizenId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, citizenId);
            rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            return 0;
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private void logCitizenAction(String action, String details) {
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
            System.err.println("Could not log citizen action: " + e.getMessage());
        } finally {
            DB.closeResources(null, pstmt, conn);
        }
    }
    
    private void loadInitialData() {
        // Initial data loading if needed
    }
    
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            logCitizenAction("Logout", "Citizen logged out of the system");
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
        SwingUtilities.invokeLater(() -> {
            // For testing - you would normally get this from login
            new CitizenDashboard(1, "citizen_bosco");
        });
    }
}