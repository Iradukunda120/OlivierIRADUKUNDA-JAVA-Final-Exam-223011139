package GOVTECHFORM;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OfficerDashboard extends JFrame {
    private int officerId;
    private String username;
    private String officerName;
    private int departmentId;
    
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
    private JButton homeBtn, casesBtn, requestsBtn, citizensBtn, documentsBtn, tasksBtn, operationsBtn, logoutBtn;
    private JButton activeButton;
    
    // Top right corner components
    private JButton userMenuBtn;
    private JPopupMenu userPopupMenu;
    
    // Tables
    private JTable assignedCasesTable;
    private JTable pendingRequestsTable;
    private JTable assignedOperationsTable;
    
    // Document Management Components
    private JTable documentsTable;
    private JComboBox<String> caseFilterCombo;
    private JTextField searchField;
    
    // Operation definitions
    private Map<String, String[]> caseTypeOperations = new HashMap<>();
    
    public OfficerDashboard(int officerId, String username) {
        this.officerId = officerId;
        this.username = username;
        
        // Initialize operations mapping
        initializeOperations();
        
        // Get officer details
        getOfficerDetails();
        
        initializeTheme();
        setupUI();
        loadInitialData();
        
        setVisible(true);
    }
    
    private void initializeOperations() {
        // Define operations for each case type
        caseTypeOperations.put("Tax Collection", new String[]{
            "Review Tax Documents",
            "Calculate Tax Amount",
            "Issue Tax Notice",
            "Process Payment",
            "Generate Receipt",
            "Update Tax Record",
            "Handle Appeal"
        });
        
        caseTypeOperations.put("License Approval", new String[]{
            "Verify Applicant Details",
            "Check Documentation",
            "Conduct Background Check",
            "Approve License",
            "Issue License Certificate",
            "Update License Database",
            "Renewal Processing"
        });
        
        caseTypeOperations.put("Document Verification", new String[]{
            "Scan Documents",
            "Verify Authenticity",
            "Check Validity Period",
            "Cross-reference Database",
            "Approve Verification",
            "Issue Verification Certificate",
            "Archive Documents"
        });
        
        caseTypeOperations.put("Complaint Handling", new String[]{
            "Record Complaint Details",
            "Assign Severity Level",
            "Investigate Complaint",
            "Contact Involved Parties",
            "Propose Resolution",
            "Implement Solution",
            "Follow-up Check"
        });
        
        caseTypeOperations.put("Permit Issuance", new String[]{
            "Review Application",
            "Check Requirements",
            "Site Inspection",
            "Approve Permit",
            "Issue Permit Document",
            "Record in System",
            "Monitor Compliance"
        });
        
        caseTypeOperations.put("General Case", new String[]{
            "Initial Assessment",
            "Gather Information",
            "Analysis & Planning",
            "Take Action",
            "Document Progress",
            "Final Review",
            "Case Closure"
        });
    }
    
    private void getOfficerDetails() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT o.Name, o.DepartmentID, d.Name as DepartmentName " +
                        "FROM officer o " +
                        "JOIN department d ON o.DepartmentID = d.DepartmentID " +
                        "WHERE o.OfficerID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, officerId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                this.officerName = rs.getString("Name");
                this.departmentId = rs.getInt("DepartmentID");
            } else {
                this.officerName = "Officer";
                this.departmentId = 0;
            }
        } catch (SQLException e) {
            System.err.println("Error getting officer details: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
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
        setTitle("GovTech Solutions - Officer Dashboard");
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
        mainContentPanel.add(createCasesPanel(), "CASES");
        mainContentPanel.add(createRequestsPanel(), "REQUESTS");
        mainContentPanel.add(createCitizensPanel(), "CITIZENS");
        mainContentPanel.add(createDocumentsPanel(), "DOCUMENTS");
        mainContentPanel.add(createTasksPanel(), "TASKS");
        mainContentPanel.add(createOperationsPanel(), "OPERATIONS");
        
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
        
        JLabel titleLabel = new JLabel("Officer Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(200, 200, 200));
        
        leftPanel.add(logoLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        leftPanel.add(titleLabel);
        
        // Right side - Unified user controls
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(primaryColor);
        
        // Unified User Menu Button
        userMenuBtn = new JButton(officerName + " ▼");
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
        
        // Style the popup menu
        popupMenu.setBackground(darkMode ? cardBgColor : Color.WHITE);
        popupMenu.setBorder(BorderFactory.createLineBorder(darkMode ? Color.DARK_GRAY : Color.LIGHT_GRAY, 1));
        
        // User Info Section
        JMenuItem userInfoItem = new JMenuItem("<html><div style='text-align: center;'><b>" + officerName + "</b><br>" + 
                                              "<span style='font-size: 11px; color: #666;'>Government Officer</span></div></html>");
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
        JMenuItem casesItem = createMenuItem("📋 My Cases", e -> {
            showPanel("CASES");
            setActiveButton(casesBtn);
            userPopupMenu.setVisible(false);
        });
        JMenuItem operationsItem = createMenuItem("⚙️ My Operations", e -> {
            showPanel("OPERATIONS");
            setActiveButton(operationsBtn);
            userPopupMenu.setVisible(false);
        });
        
        popupMenu.add(dashboardItem);
        popupMenu.add(casesItem);
        popupMenu.add(operationsItem);
        popupMenu.addSeparator();
        
        // User Settings Section
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
        
        // Update popup menu
        userPopupMenu = createUserPopupMenu();
        
        // Log theme change
        logOfficerAction("Theme Change", "Switched to " + (darkMode ? "dark" : "light") + " theme");
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
            } else if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setForeground(textPrimaryColor);
                button.setBackground(cardBgColor);
            } else if (comp instanceof JComboBox) {
                JComboBox<?> combo = (JComboBox<?>) comp;
                combo.setBackground(cardBgColor);
                combo.setForeground(textPrimaryColor);
            } else if (comp instanceof JTextField) {
                JTextField field = (JTextField) comp;
                field.setBackground(cardBgColor);
                field.setForeground(textPrimaryColor);
            }
        }
    }
    
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(sidebarColor);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(25, 15, 25, 15));
        
        // Navigation section title
        JLabel navTitle = new JLabel("OFFICER NAVIGATION");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        navTitle.setForeground(new Color(170, 170, 170));
        navTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        navTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Navigation buttons
        homeBtn = createNavButton("Dashboard", "HOME");
        casesBtn = createNavButton("Assigned Cases", "CASES");
        requestsBtn = createNavButton("Service Requests", "REQUESTS");
        citizensBtn = createNavButton("Citizen Records", "CITIZENS");
        documentsBtn = createNavButton("Case Documents", "DOCUMENTS");
        tasksBtn = createNavButton("Daily Tasks", "TASKS");
        operationsBtn = createNavButton("Operations Management", "OPERATIONS");
        
        // Logout button at bottom
        logoutBtn = createNavButton("Logout", "LOGOUT");
        logoutBtn.setBackground(new Color(192, 57, 43));
        
        // Add components to sidebar
        sidebar.add(navTitle);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(homeBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(casesBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(requestsBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(citizensBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(documentsBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(tasksBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(operationsBtn);
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
        if (panelName.equals("DOCUMENTS")) {
            refreshDocumentsTable();
        }
    }
    
    private JPanel createHomePanel() {
        JPanel home = new JPanel(new BorderLayout());
        home.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        home.setBackground(backgroundColor);
        
        // Header with welcome message
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        
        JLabel welcomeLabel = new JLabel("<html><h1 style='margin: 0; font-size: 28px;'>Welcome, Officer " + officerName + "!</h1>" +
                                        "<p style='margin: 5px 0 0 0; font-size: 14px; color: #666;'>Manage cases, operations, and citizen requests</p></html>");
        welcomeLabel.setForeground(textPrimaryColor);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        home.add(headerPanel, BorderLayout.NORTH);
        
        // Stats and quick actions
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(backgroundColor);
        
        // Stats cards
        JPanel statsPanel = createOfficerStatsPanel();
        contentPanel.add(statsPanel, BorderLayout.NORTH);
        
        // Quick actions
        JPanel quickActionsPanel = createOfficerQuickActionsPanel();
        contentPanel.add(quickActionsPanel, BorderLayout.CENTER);
        
        home.add(contentPanel, BorderLayout.CENTER);
        
        return home;
    }
    
    private JPanel createOfficerStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 15, 15));
        panel.setBackground(backgroundColor);
        
        try {
            // Get officer-specific stats with error handling
            int assignedCases = getOfficerCount("SELECT COUNT(*) FROM officercase WHERE OfficerID = ?", officerId);
            int pendingRequests = getOfficerCount("SELECT COUNT(*) FROM servicerequest WHERE DepartmentID = ? AND Status = 'Pending'", departmentId);
            int highPriority = getOfficerCount("SELECT COUNT(*) FROM casetable ct " +
                                             "JOIN officercase oc ON ct.CaseID = oc.CaseID " +
                                             "WHERE oc.OfficerID = ? AND ct.Priority = 'High'", officerId);
            int assignedOperations = getOfficerCountWithFallback("SELECT COUNT(*) FROM assigned_operations WHERE OfficerID = ? AND Status = 'PENDING'", officerId);
            int completedToday = getOfficerCountWithFallback("SELECT COUNT(*) FROM assigned_operations WHERE OfficerID = ? AND Status = 'COMPLETED' AND DATE(CompletedDate) = CURDATE()", officerId);
            
            panel.add(createStatCard("Assigned Cases", String.valueOf(assignedCases), "📋", 
                e -> { showPanel("CASES"); setActiveButton(casesBtn); }));
            
            panel.add(createStatCard("Pending Requests", String.valueOf(pendingRequests), "📝", 
                e -> { showPanel("REQUESTS"); setActiveButton(requestsBtn); }));
            
            panel.add(createStatCard("High Priority", String.valueOf(highPriority), "⚠️", 
                e -> { showHighPriorityCases(); }));
            
            panel.add(createStatCard("Pending Operations", String.valueOf(assignedOperations), "⚙️", 
                e -> { showPanel("OPERATIONS"); setActiveButton(operationsBtn); }));
            
            panel.add(createStatCard("Completed Today", String.valueOf(completedToday), "✅", 
                e -> { showTodayCompleted(); }));
            
        } catch (SQLException e) {
            // Fallback values
            panel.add(createStatCard("Assigned Cases", "0", "📋", null));
            panel.add(createStatCard("Pending Requests", "0", "📝", null));
            panel.add(createStatCard("High Priority", "0", "⚠️", null));
            panel.add(createStatCard("Pending Operations", "0", "⚙️", null));
            panel.add(createStatCard("Completed Today", "0", "✅", null));
            System.err.println("Error loading officer stats: " + e.getMessage());
        }
        
        return panel;
    }
    
    private int getOfficerCount(String sql, int id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private int getOfficerCountWithFallback(String sql, int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            // If table doesn't exist or other error, return 0
            return 0;
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private JPanel createStatCard(String title, String value, String icon, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardBgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(darkMode ? new Color(80, 80, 80) : new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(action != null ? new Cursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        iconLabel.setForeground(primaryColor);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(textPrimaryColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(primaryColor);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(cardBgColor);
        contentPanel.add(iconLabel, BorderLayout.NORTH);
        contentPanel.add(titleLabel, BorderLayout.CENTER);
        contentPanel.add(valueLabel, BorderLayout.SOUTH);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        if (action != null) {
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
        }
        
        return card;
    }
    
    private JPanel createOfficerQuickActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        panel.add(createQuickActionCard("Review New Case", "Review and accept new case assignments", "🔍", 
            e -> showNewCaseReview()));
        
        panel.add(createQuickActionCard("Start Operation", "Begin operation on assigned case", "⚡", 
            e -> startOperationOnCase()));
        
        panel.add(createQuickActionCard("Update Case Status", "Update progress on assigned cases", "📊", 
            e -> showCaseStatusUpdate()));
        
        panel.add(createQuickActionCard("Generate Report", "Create daily activity report", "📈", 
            e -> generateDailyReport()));
        
        return panel;
    }
    
    private JPanel createQuickActionCard(String title, String description, String icon, ActionListener action) {
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
    
    private void showNewCaseReview() {
        // This would open a dialog to review new cases
        JOptionPane.showMessageDialog(this, "New case review functionality would be implemented here", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void startOperationOnCase() {
        // Get assigned cases
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT ct.CaseID, ct.CaseType, ct.Description, ct.Priority " +
                        "FROM casetable ct " +
                        "JOIN officercase oc ON ct.CaseID = oc.CaseID " +
                        "WHERE oc.OfficerID = ? " +
                        "ORDER BY ct.Priority DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, officerId);
            rs = pstmt.executeQuery();
            
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Case ID", "Case Type", "Description", "Priority"}, 0
            );
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("CaseID"),
                    rs.getString("CaseType"),
                    rs.getString("Description"),
                    rs.getString("Priority")
                });
            }
            
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No cases assigned to you.", "No Cases", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            JTable casesTable = new JTable(model);
            casesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(casesTable);
            
            int result = JOptionPane.showConfirmDialog(this, scrollPane, "Select Case for Operation", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                int selectedRow = casesTable.getSelectedRow();
                if (selectedRow != -1) {
                    int caseId = (int) model.getValueAt(selectedRow, 0);
                    String caseType = (String) model.getValueAt(selectedRow, 1);
                    showOperationSelectionDialog(caseId, caseType);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a case.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading cases: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private void showOperationSelectionDialog(int caseId, String caseType) {
        JDialog dialog = new JDialog(this, "Select Operation for Case #" + caseId, true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(cardBgColor);
        
        JLabel titleLabel = new JLabel("Select operation to perform on Case #" + caseId);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(textPrimaryColor);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Get available operations for this case type
        String[] operations = caseTypeOperations.getOrDefault(caseType, 
                            caseTypeOperations.get("General Case"));
        
        JList<String> operationsList = new JList<>(operations);
        operationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        operationsList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane listScroll = new JScrollPane(operationsList);
        
        mainPanel.add(listScroll, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(cardBgColor);
        
        JButton startBtn = createActionButton("Start Operation", new Color(46, 204, 113));
        JButton cancelBtn = createActionButton("Cancel", new Color(231, 76, 60));
        
        startBtn.addActionListener(e -> {
            if (operationsList.getSelectedIndex() != -1) {
                String selectedOperation = operationsList.getSelectedValue();
                executeOperation(caseId, caseType, selectedOperation);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select an operation.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(startBtn);
        buttonPanel.add(cancelBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void executeOperation(int caseId, String caseType, String operation) {
        // Create operation execution dialog
        JDialog execDialog = new JDialog(this, "Executing: " + operation, true);
        execDialog.setSize(600, 500);
        execDialog.setLocationRelativeTo(this);
        execDialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(cardBgColor);
        
        // Operation details
        JPanel detailsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        detailsPanel.setBackground(cardBgColor);
        
        JLabel caseIdLabel = new JLabel("Case ID:");
        caseIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel caseIdValue = new JLabel(String.valueOf(caseId));
        
        JLabel caseTypeLabel = new JLabel("Case Type:");
        caseTypeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel caseTypeValue = new JLabel(caseType);
        
        JLabel operationLabel = new JLabel("Operation:");
        operationLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel operationValue = new JLabel(operation);
        
        JLabel dateLabel = new JLabel("Date/Time:");
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel dateValue = new JLabel(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        detailsPanel.add(caseIdLabel);
        detailsPanel.add(caseIdValue);
        detailsPanel.add(caseTypeLabel);
        detailsPanel.add(caseTypeValue);
        detailsPanel.add(operationLabel);
        detailsPanel.add(operationValue);
        detailsPanel.add(dateLabel);
        detailsPanel.add(dateValue);
        
        mainPanel.add(detailsPanel, BorderLayout.NORTH);
        
        // Operation steps/form
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBackground(cardBgColor);
        formPanel.setBorder(BorderFactory.createTitledBorder("Operation Details"));
        
        JTextArea notesArea = new JTextArea(8, 40);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane notesScroll = new JScrollPane(notesArea);
        
        // Add operation-specific fields based on operation type
        JPanel specificFields = new JPanel(new GridLayout(0, 2, 10, 10));
        specificFields.setBackground(cardBgColor);
        
        if (operation.contains("Tax")) {
            specificFields.add(new JLabel("Tax Amount:"));
            specificFields.add(new JTextField());
            specificFields.add(new JLabel("Tax Period:"));
            specificFields.add(new JTextField());
            specificFields.add(new JLabel("Payment Method:"));
            specificFields.add(new JComboBox<>(new String[]{"Cash", "Bank Transfer", "Credit Card", "Online Payment"}));
        } else if (operation.contains("License")) {
            specificFields.add(new JLabel("License Number:"));
            specificFields.add(new JTextField());
            specificFields.add(new JLabel("Expiry Date:"));
            specificFields.add(new JTextField());
            specificFields.add(new JLabel("Applicant Verified:"));
            specificFields.add(new JCheckBox("Yes"));
        } else if (operation.contains("Document")) {
            specificFields.add(new JLabel("Document Type:"));
            specificFields.add(new JTextField());
            specificFields.add(new JLabel("Verification Status:"));
            specificFields.add(new JComboBox<>(new String[]{"Verified", "Pending", "Rejected"}));
            specificFields.add(new JLabel("Certificate Number:"));
            specificFields.add(new JTextField());
        }
        
        formPanel.add(specificFields, BorderLayout.NORTH);
        formPanel.add(new JLabel("Notes/Remarks:"), BorderLayout.CENTER);
        formPanel.add(notesScroll, BorderLayout.SOUTH);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Progress tracker
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBackground(cardBgColor);
        progressPanel.setBorder(BorderFactory.createTitledBorder("Progress"));
        
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        
        progressPanel.add(progressBar, BorderLayout.CENTER);
        mainPanel.add(progressPanel, BorderLayout.SOUTH);
        
        // Control buttons
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setBackground(cardBgColor);
        
        JButton saveBtn = createActionButton("💾 Save Progress", new Color(52, 152, 219));
        JButton completeBtn = createActionButton("✅ Complete Operation", new Color(46, 204, 113));
        JButton cancelBtn = createActionButton("❌ Cancel", new Color(231, 76, 60));
        
        saveBtn.addActionListener(e -> {
            progressBar.setValue(50);
            JOptionPane.showMessageDialog(execDialog, "Progress saved!", "Saved", JOptionPane.INFORMATION_MESSAGE);
        });
        
        completeBtn.addActionListener(e -> {
            progressBar.setValue(100);
            
            // Save operation to database
            if (saveOperationToDB(caseId, operation, notesArea.getText())) {
                JOptionPane.showMessageDialog(execDialog, "Operation completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                logOfficerAction("Operation Executed", "Case #" + caseId + ": " + operation);
                execDialog.dispose();
                refreshOperationsTable();
            }
        });
        
        cancelBtn.addActionListener(e -> execDialog.dispose());
        
        controlPanel.add(saveBtn);
        controlPanel.add(completeBtn);
        controlPanel.add(cancelBtn);
        
        execDialog.add(mainPanel, BorderLayout.CENTER);
        execDialog.add(controlPanel, BorderLayout.SOUTH);
        execDialog.setVisible(true);
    }
    
    private boolean saveOperationToDB(int caseId, String operation, String notes) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DB.getConnection();
            
            // First, check if assigned_operations table exists and create if not
            String checkTable = "CREATE TABLE IF NOT EXISTS case_operations_log (" +
                               "LogID INT AUTO_INCREMENT PRIMARY KEY, " +
                               "CaseID INT, " +
                               "OfficerID INT, " +
                               "OperationName VARCHAR(100), " +
                               "OperationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                               "Status VARCHAR(20) DEFAULT 'COMPLETED', " +
                               "Notes TEXT, " +
                               "FOREIGN KEY (CaseID) REFERENCES casetable(CaseID), " +
                               "FOREIGN KEY (OfficerID) REFERENCES officer(OfficerID))";
            Statement stmt = conn.createStatement();
            stmt.execute(checkTable);
            
            // Insert operation log
            String sql = "INSERT INTO case_operations_log (CaseID, OfficerID, OperationName, Notes) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, caseId);
            pstmt.setInt(2, officerId);
            pstmt.setString(3, operation);
            pstmt.setString(4, notes);
            
            int rows = pstmt.executeUpdate();
            
            // Also update system logs
            String logSql = "INSERT INTO system_logs (User, Action, Details) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(logSql);
            pstmt.setString(1, username);
            pstmt.setString(2, "Case Operation: " + operation);
            pstmt.setString(3, "Case ID: " + caseId + ", Notes: " + (notes.length() > 100 ? notes.substring(0, 100) + "..." : notes));
            pstmt.executeUpdate();
            
            return rows > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving operation: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            DB.closeResources(null, pstmt, conn);
        }
    }
    
    private void showPendingRequests() {
        showPanel("REQUESTS");
        setActiveButton(requestsBtn);
    }
    
    private void showCaseStatusUpdate() {
        showPanel("CASES");
        setActiveButton(casesBtn);
    }
    
    private void generateDailyReport() {
        // Generate daily activity report
        StringBuilder report = new StringBuilder();
        report.append("DAILY ACTIVITY REPORT\n");
        report.append("Officer: ").append(officerName).append("\n");
        report.append("Date: ").append(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).append("\n\n");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            
            // Get today's activities from system_logs or case_operations_log
            String sql = "SELECT 'System Log' as Type, Action, Details, Timestamp FROM system_logs " +
                        "WHERE User = ? AND DATE(Timestamp) = CURDATE() " +
                        "UNION ALL " +
                        "SELECT 'Operation' as Type, OperationName, Notes, OperationDate FROM case_operations_log " +
                        "WHERE OfficerID = ? AND DATE(OperationDate) = CURDATE() " +
                        "ORDER BY Timestamp DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setInt(2, officerId);
            rs = pstmt.executeQuery();
            
            report.append("Today's Activities:\n");
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            while (rs.next()) {
                report.append(sdf.format(rs.getTimestamp("Timestamp"))).append(" - ")
                     .append(rs.getString("Type")).append(": ")
                     .append(rs.getString("Action")).append("\n");
            }
            
        } catch (SQLException e) {
            report.append("Error generating report: ").append(e.getMessage());
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
        
        JTextArea reportArea = new JTextArea(report.toString());
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Daily Activity Report", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showHighPriorityCases() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT ct.CaseID, ct.CaseType, ct.Priority, ct.Description, ct.CreatedAt " +
                        "FROM casetable ct " +
                        "JOIN officercase oc ON ct.CaseID = oc.CaseID " +
                        "WHERE oc.OfficerID = ? AND ct.Priority = 'High' " +
                        "ORDER BY ct.CreatedAt DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, officerId);
            rs = pstmt.executeQuery();
            
            StringBuilder cases = new StringBuilder();
            cases.append("HIGH PRIORITY CASES\n\n");
            
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
            while (rs.next()) {
                cases.append("Case ID: ").append(rs.getInt("CaseID")).append("\n");
                cases.append("Type: ").append(rs.getString("CaseType")).append("\n");
                cases.append("Priority: ").append(rs.getString("Priority")).append("\n");
                cases.append("Description: ").append(rs.getString("Description")).append("\n");
                cases.append("Created: ").append(sdf.format(rs.getTimestamp("CreatedAt"))).append("\n");
                cases.append("------------------------------------\n");
            }
            
            if (cases.toString().equals("HIGH PRIORITY CASES\n\n")) {
                cases.append("No high priority cases assigned.\n");
            }
            
            JTextArea casesArea = new JTextArea(cases.toString());
            casesArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(casesArea);
            scrollPane.setPreferredSize(new Dimension(500, 300));
            
            JOptionPane.showMessageDialog(this, scrollPane, "High Priority Cases", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading high priority cases: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private void showTodayCompleted() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT OperationName, OperationDate, Notes FROM case_operations_log " +
                        "WHERE OfficerID = ? AND DATE(OperationDate) = CURDATE() " +
                        "ORDER BY OperationDate DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, officerId);
            rs = pstmt.executeQuery();
            
            StringBuilder completed = new StringBuilder();
            completed.append("TODAY'S COMPLETED OPERATIONS\n\n");
            
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            while (rs.next()) {
                completed.append(sdf.format(rs.getTimestamp("OperationDate"))).append(" - ")
                        .append(rs.getString("OperationName")).append("\n");
                if (rs.getString("Notes") != null) {
                    completed.append("   Notes: ").append(rs.getString("Notes")).append("\n");
                }
                completed.append("\n");
            }
            
            if (completed.toString().equals("TODAY'S COMPLETED OPERATIONS\n\n")) {
                completed.append("No operations completed today yet.\n");
            }
            
            JTextArea completedArea = new JTextArea(completed.toString());
            completedArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(completedArea);
            scrollPane.setPreferredSize(new Dimension(500, 300));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Today's Completed Operations", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading completed operations: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private JPanel createCasesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(backgroundColor);
        
        JLabel title = new JLabel("Assigned Cases");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(textPrimaryColor);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Create cases table
        try {
            assignedCasesTable = createAssignedCasesTable();
            JScrollPane scrollPane = new JScrollPane(assignedCasesTable);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            panel.add(title, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);
        } catch (SQLException e) {
            panel.add(new JLabel("Error loading assigned cases: " + e.getMessage()), BorderLayout.CENTER);
            System.err.println("Error creating cases table: " + e.getMessage());
        }
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(backgroundColor);
        
        JButton updateStatusBtn = createActionButton("🔄 Update Status", new Color(52, 152, 219));
        JButton viewDetailsBtn = createActionButton("👁️ View Details", new Color(155, 89, 182));
        JButton performOpBtn = createActionButton("⚡ Perform Operation", new Color(46, 204, 113));
        JButton addDocumentBtn = createActionButton("📎 Add Document", new Color(241, 196, 15));
        JButton refreshBtn = createActionButton("🔄 Refresh", new Color(231, 76, 60));
        
        updateStatusBtn.addActionListener(e -> updateCaseStatus());
        viewDetailsBtn.addActionListener(e -> viewCaseDetails());
        performOpBtn.addActionListener(e -> performCaseOperation());
        addDocumentBtn.addActionListener(e -> addCaseDocument());
        refreshBtn.addActionListener(e -> refreshCasesTable());
        
        buttonPanel.add(updateStatusBtn);
        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(performOpBtn);
        buttonPanel.add(addDocumentBtn);
        buttonPanel.add(refreshBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JTable createAssignedCasesTable() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT ct.CaseID, ct.CaseType, ct.Description, ct.Priority, " +
                        "ct.CreatedAt, oc.AssignedDate " +
                        "FROM casetable ct " +
                        "JOIN officercase oc ON ct.CaseID = oc.CaseID " +
                        "WHERE oc.OfficerID = ? " +
                        "ORDER BY ct.Priority DESC, ct.CreatedAt DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, officerId);
            rs = pstmt.executeQuery();
            
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Case ID", "Case Type", "Description", "Priority", "Created Date", "Assigned Date"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("CaseID"),
                    rs.getString("CaseType"),
                    rs.getString("Description"),
                    rs.getString("Priority"),
                    sdf.format(rs.getTimestamp("CreatedAt")),
                    sdf.format(rs.getTimestamp("AssignedDate"))
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
    
    private void updateCaseStatus() {
        int selectedRow = assignedCasesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a case to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) assignedCasesTable.getModel();
        int caseId = (int) model.getValueAt(selectedRow, 0);
        String caseType = (String) model.getValueAt(selectedRow, 1);

        JDialog dialog = new JDialog(this, "Update Case Status", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(cardBgColor);

        JComboBox<String> statusCombo = new JComboBox<>(new String[]{
            "Under Review", "In Progress", "Pending Information", "Awaiting Approval", "Completed", "Closed"
        });
        
        JTextArea notesArea = new JTextArea(4, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);

        formPanel.add(new JLabel("Case ID:"));
        formPanel.add(new JLabel(String.valueOf(caseId)));
        formPanel.add(new JLabel("Case Type:"));
        formPanel.add(new JLabel(caseType));
        formPanel.add(new JLabel("New Status:"));
        formPanel.add(statusCombo);
        formPanel.add(new JLabel("Notes:"));
        formPanel.add(notesScroll);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(cardBgColor);
        JButton updateBtn = createActionButton("Update", new Color(46, 204, 113));
        JButton cancelBtn = createActionButton("Cancel", new Color(231, 76, 60));
        
        updateBtn.addActionListener(e -> {
            String status = (String) statusCombo.getSelectedItem();
            String notes = notesArea.getText();
            
            if (updateCaseStatusInDB(caseId, status, notes)) {
                JOptionPane.showMessageDialog(this, "Case status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                logOfficerAction("Case Update", "Updated case " + caseId + " to status: " + status);
                dialog.dispose();
                refreshCasesTable();
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private boolean updateCaseStatusInDB(int caseId, String status, String notes) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DB.getConnection();
            String sql = "UPDATE casetable SET Status = ? WHERE CaseID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, caseId);
            
            int rows = pstmt.executeUpdate();
            
            // Log the action
            if (rows > 0) {
                String logSql = "INSERT INTO system_logs (User, Action, Details) VALUES (?, ?, ?)";
                pstmt = conn.prepareStatement(logSql);
                pstmt.setString(1, username);
                pstmt.setString(2, "Case Status Updated");
                pstmt.setString(3, "Case ID: " + caseId + ", Status: " + status + ", Notes: " + notes);
                pstmt.executeUpdate();
            }
            
            return rows > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating case status: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            DB.closeResources(null, pstmt, conn);
        }
    }
    
    private void viewCaseDetails() {
        int selectedRow = assignedCasesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a case to view details.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) assignedCasesTable.getModel();
        int caseId = (int) model.getValueAt(selectedRow, 0);
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT ct.*, c.FullName as CitizenName, c.Contact as CitizenContact " +
                        "FROM casetable ct " +
                        "LEFT JOIN document d ON ct.CaseID = d.CaseID " +
                        "LEFT JOIN citizen c ON d.CitizenID = c.CitizenID " +
                        "WHERE ct.CaseID = ? " +
                        "LIMIT 1";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, caseId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                StringBuilder details = new StringBuilder();
                details.append("CASE DETAILS\n\n");
                details.append("Case ID: ").append(rs.getInt("CaseID")).append("\n");
                details.append("Type: ").append(rs.getString("CaseType")).append("\n");
                details.append("Priority: ").append(rs.getString("Priority")).append("\n");
                details.append("Status: ").append(rs.getString("Status") != null ? rs.getString("Status") : "Unknown").append("\n");
                details.append("Description: ").append(rs.getString("Description")).append("\n");
                
                if (rs.getString("CitizenName") != null) {
                    details.append("Related Citizen: ").append(rs.getString("CitizenName")).append("\n");
                    details.append("Contact: ").append(rs.getString("CitizenContact")).append("\n");
                }
                
                details.append("Created: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(rs.getTimestamp("CreatedAt"))).append("\n");
                
                // Get operations history
                try {
                    String opsSql = "SELECT OperationName, OperationDate, Notes FROM case_operations_log " +
                                   "WHERE CaseID = ? ORDER BY OperationDate DESC LIMIT 5";
                    pstmt = conn.prepareStatement(opsSql);
                    pstmt.setInt(1, caseId);
                    ResultSet opsRs = pstmt.executeQuery();
                    
                    if (opsRs.next()) {
                        details.append("\nRECENT OPERATIONS:\n");
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm");
                        do {
                            details.append("• ").append(opsRs.getString("OperationName")).append(" - ")
                                   .append(sdf.format(opsRs.getTimestamp("OperationDate"))).append("\n");
                        } while (opsRs.next());
                    }
                    opsRs.close();
                } catch (SQLException e) {
                    // Table might not exist yet
                }
                
                JTextArea detailsArea = new JTextArea(details.toString());
                detailsArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(detailsArea);
                scrollPane.setPreferredSize(new Dimension(600, 400));
                
                JOptionPane.showMessageDialog(this, scrollPane, "Case Details - ID: " + caseId, JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Case details not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading case details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private void performCaseOperation() {
        int selectedRow = assignedCasesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a case to perform operation.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) assignedCasesTable.getModel();
        int caseId = (int) model.getValueAt(selectedRow, 0);
        String caseType = (String) model.getValueAt(selectedRow, 1);
        
        showOperationSelectionDialog(caseId, caseType);
    }
    
    private void addCaseDocument() {
        int selectedRow = assignedCasesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a case to add document.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) assignedCasesTable.getModel();
        int caseId = (int) model.getValueAt(selectedRow, 0);
        
        // Open document upload dialog
        openDocumentUploadDialog(caseId);
    }
    
    private void openDocumentUploadDialog(int caseId) {
        JDialog uploadDialog = new JDialog(this, "Upload Document for Case #" + caseId, true);
        uploadDialog.setSize(500, 400);
        uploadDialog.setLocationRelativeTo(this);
        uploadDialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(cardBgColor);
        
        // Form fields
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(cardBgColor);
        
        JTextField titleField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{
            "Application Form", "Identity Proof", "Address Proof", "Income Certificate",
            "Tax Document", "License Application", "Complaint Form", "Other"
        });
        
        JTextArea descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        
        formPanel.add(new JLabel("Document Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Document Type:"));
        formPanel.add(typeCombo);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descScroll);
        
        // File selection
        JPanel filePanel = new JPanel(new BorderLayout(10, 10));
        filePanel.setBackground(cardBgColor);
        filePanel.setBorder(BorderFactory.createTitledBorder("Select Document File"));
        
        JTextField filePathField = new JTextField();
        filePathField.setEditable(false);
        JButton browseBtn = createActionButton("Browse", new Color(52, 152, 219));
        
        JPanel fileSelectPanel = new JPanel(new BorderLayout(5, 0));
        fileSelectPanel.setBackground(cardBgColor);
        fileSelectPanel.add(filePathField, BorderLayout.CENTER);
        fileSelectPanel.add(browseBtn, BorderLayout.EAST);
        
        filePanel.add(fileSelectPanel, BorderLayout.NORTH);
        
        // Browse button action
        browseBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Document File");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            
            int result = fileChooser.showOpenDialog(uploadDialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                filePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(filePanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(cardBgColor);
        
        JButton uploadBtn = createActionButton("Upload Document", new Color(46, 204, 113));
        JButton cancelBtn = createActionButton("Cancel", new Color(231, 76, 60));
        
        uploadBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            String description = descriptionArea.getText().trim();
            String filePath = filePathField.getText();
            
            if (title.isEmpty() || filePath.isEmpty()) {
                JOptionPane.showMessageDialog(uploadDialog, "Please fill in all required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Simulate document upload
            uploadDocument(caseId, title, type, description, filePath);
            uploadDialog.dispose();
        });
        
        cancelBtn.addActionListener(e -> uploadDialog.dispose());
        
        buttonPanel.add(uploadBtn);
        buttonPanel.add(cancelBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        uploadDialog.add(mainPanel);
        uploadDialog.setVisible(true);
    }
    
    private void uploadDocument(int caseId, String title, String type, String description, String filePath) {
        // In a real application, this would handle actual file upload
        // For now, we'll just simulate it
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DB.getConnection();
            
            // Check if documents table exists
            String createTable = "CREATE TABLE IF NOT EXISTS officer_documents (" +
                               "DocumentID INT AUTO_INCREMENT PRIMARY KEY, " +
                               "CaseID INT, " +
                               "OfficerID INT, " +
                               "Title VARCHAR(200), " +
                               "DocumentType VARCHAR(100), " +
                               "Description TEXT, " +
                               "FileName VARCHAR(255), " +
                               "FilePath VARCHAR(500), " +
                               "UploadDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                               "Status VARCHAR(20) DEFAULT 'Active', " +
                               "FOREIGN KEY (CaseID) REFERENCES casetable(CaseID), " +
                               "FOREIGN KEY (OfficerID) REFERENCES officer(OfficerID))";
            Statement stmt = conn.createStatement();
            stmt.execute(createTable);
            
            // Insert document record
            String sql = "INSERT INTO officer_documents (CaseID, OfficerID, Title, DocumentType, Description, FileName, FilePath) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, caseId);
            pstmt.setInt(2, officerId);
            pstmt.setString(3, title);
            pstmt.setString(4, type);
            pstmt.setString(5, description);
            
            // Extract filename from path
            String fileName = new java.io.File(filePath).getName();
            pstmt.setString(6, fileName);
            pstmt.setString(7, filePath);
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Document uploaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                logOfficerAction("Document Uploaded", "Case #" + caseId + ": " + title);
                
                // If we're in the documents panel, refresh the table
                if (documentsTable != null) {
                    refreshDocumentsTable();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error uploading document: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            DB.closeResources(null, pstmt, conn);
        }
    }
    
    private void refreshCasesTable() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            DefaultTableModel model = (DefaultTableModel) assignedCasesTable.getModel();
            model.setRowCount(0);
            
            String sql = "SELECT ct.CaseID, ct.CaseType, ct.Description, ct.Priority, " +
                        "ct.CreatedAt, oc.AssignedDate " +
                        "FROM casetable ct " +
                        "JOIN officercase oc ON ct.CaseID = oc.CaseID " +
                        "WHERE oc.OfficerID = ? " +
                        "ORDER BY ct.Priority DESC, ct.CreatedAt DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, officerId);
            rs = pstmt.executeQuery();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("CaseID"),
                    rs.getString("CaseType"),
                    rs.getString("Description"),
                    rs.getString("Priority"),
                    sdf.format(rs.getTimestamp("CreatedAt")),
                    sdf.format(rs.getTimestamp("AssignedDate"))
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error refreshing cases: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private JPanel createDocumentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(backgroundColor);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(backgroundColor);
        
        JLabel title = new JLabel("Case Document Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(textPrimaryColor);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        headerPanel.add(title, BorderLayout.NORTH);
        
        // Toolbar with filters and search
        JPanel toolbarPanel = new JPanel(new BorderLayout(10, 0));
        toolbarPanel.setBackground(backgroundColor);
        toolbarPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setBackground(backgroundColor);
        
        filterPanel.add(new JLabel("Filter by Case:"));
        caseFilterCombo = new JComboBox<>();
        caseFilterCombo.addItem("All Cases");
        loadOfficerCasesToCombo();
        caseFilterCombo.setPreferredSize(new Dimension(200, 30));
        caseFilterCombo.addActionListener(e -> filterDocumentsByCase());
        
        filterPanel.add(caseFilterCombo);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(backgroundColor);
        
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        JButton searchBtn = createActionButton("🔍 Search", new Color(52, 152, 219));
        searchBtn.addActionListener(e -> searchDocuments());
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        
        toolbarPanel.add(filterPanel, BorderLayout.WEST);
        toolbarPanel.add(searchPanel, BorderLayout.EAST);
        
        headerPanel.add(toolbarPanel, BorderLayout.SOUTH);
        
        // Documents table
        try {
            documentsTable = createDocumentsTable();
            JScrollPane scrollPane = new JScrollPane(documentsTable);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            
            // Action buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            buttonPanel.setBackground(backgroundColor);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            
            JButton uploadBtn = createActionButton("📤 Upload Document", new Color(46, 204, 113));
            JButton viewBtn = createActionButton("👁️ View Details", new Color(52, 152, 219));
            JButton downloadBtn = createActionButton("📥 Download", new Color(155, 89, 182));
            JButton deleteBtn = createActionButton("🗑️ Delete", new Color(231, 76, 60));
            JButton refreshBtn = createActionButton("🔄 Refresh", new Color(241, 196, 15));
            
            uploadBtn.addActionListener(e -> openDocumentUploadDialog());
            viewBtn.addActionListener(e -> viewDocumentDetails());
            downloadBtn.addActionListener(e -> downloadDocument());
            deleteBtn.addActionListener(e -> deleteDocument());
            refreshBtn.addActionListener(e -> refreshDocumentsTable());
            
            buttonPanel.add(uploadBtn);
            buttonPanel.add(viewBtn);
            buttonPanel.add(downloadBtn);
            buttonPanel.add(deleteBtn);
            buttonPanel.add(refreshBtn);
            
            panel.add(headerPanel, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
        } catch (SQLException e) {
            panel.add(new JLabel("Error loading documents: " + e.getMessage()), BorderLayout.CENTER);
            System.err.println("Error creating documents table: " + e.getMessage());
        }
        
        return panel;
    }
    
    private void loadOfficerCasesToCombo() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT DISTINCT ct.CaseID, ct.CaseType, ct.Description " +
                        "FROM casetable ct " +
                        "JOIN officercase oc ON ct.CaseID = oc.CaseID " +
                        "WHERE oc.OfficerID = ? " +
                        "ORDER BY ct.CaseID";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, officerId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String displayText = "Case #" + rs.getInt("CaseID") + " - " + 
                                   rs.getString("CaseType") + " (" + 
                                   (rs.getString("Description").length() > 30 ? 
                                    rs.getString("Description").substring(0, 27) + "..." : 
                                    rs.getString("Description")) + ")";
                caseFilterCombo.addItem(displayText);
            }
        } catch (SQLException e) {
            System.err.println("Error loading officer cases: " + e.getMessage());
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private JTable createDocumentsTable() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            
            // Check if table exists
            String checkTable = "SHOW TABLES LIKE 'officer_documents'";
            Statement stmt = conn.createStatement();
            ResultSet tableCheck = stmt.executeQuery(checkTable);
            
            if (!tableCheck.next()) {
                // Create table if it doesn't exist
                String createTable = "CREATE TABLE IF NOT EXISTS officer_documents (" +
                                   "DocumentID INT AUTO_INCREMENT PRIMARY KEY, " +
                                   "CaseID INT, " +
                                   "OfficerID INT, " +
                                   "Title VARCHAR(200), " +
                                   "DocumentType VARCHAR(100), " +
                                   "Description TEXT, " +
                                   "FileName VARCHAR(255), " +
                                   "FilePath VARCHAR(500), " +
                                   "UploadDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                   "Status VARCHAR(20) DEFAULT 'Active', " +
                                   "FOREIGN KEY (CaseID) REFERENCES casetable(CaseID), " +
                                   "FOREIGN KEY (OfficerID) REFERENCES officer(OfficerID))";
                stmt.execute(createTable);
            }
            
            String sql = "SELECT d.DocumentID, d.Title, d.DocumentType, d.FileName, " +
                        "d.UploadDate, ct.CaseID, ct.CaseType, " +
                        "CASE WHEN d.Status = 'Active' THEN 'Active' ELSE 'Archived' END as StatusDisplay " +
                        "FROM officer_documents d " +
                        "JOIN casetable ct ON d.CaseID = ct.CaseID " +
                        "WHERE d.OfficerID = ? " +
                        "ORDER BY d.UploadDate DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, officerId);
            rs = pstmt.executeQuery();
            
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Document ID", "Title", "Type", "Case", "File Name", "Upload Date", "Status"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            while (rs.next()) {
                String caseInfo = "Case #" + rs.getInt("CaseID") + " - " + rs.getString("CaseType");
                model.addRow(new Object[]{
                    rs.getInt("DocumentID"),
                    rs.getString("Title"),
                    rs.getString("DocumentType"),
                    caseInfo,
                    rs.getString("FileName"),
                    sdf.format(rs.getTimestamp("UploadDate")),
                    rs.getString("StatusDisplay")
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
    
    private void openDocumentUploadDialog() {
        // Get cases for selection
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
                model.addRow(new Object[]{
                    rs.getInt("CaseID"),
                    rs.getString("CaseType"),
                    rs.getString("Description")
                });
            }
            
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No cases assigned to you. You need to have cases to upload documents.", "No Cases", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            JTable casesTable = new JTable(model);
            casesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(casesTable);
            
            int result = JOptionPane.showConfirmDialog(this, scrollPane, "Select Case for Document Upload", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                int selectedRow = casesTable.getSelectedRow();
                if (selectedRow != -1) {
                    int caseId = (int) model.getValueAt(selectedRow, 0);
                    openDocumentUploadDialog(caseId);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a case.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading cases: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private void filterDocumentsByCase() {
        String selected = (String) caseFilterCombo.getSelectedItem();
        if (selected == null || selected.equals("All Cases")) {
            refreshDocumentsTable();
            return;
        }
        
        // Extract case ID from the display text
        try {
            int startIndex = selected.indexOf("Case #") + 6;
            int endIndex = selected.indexOf(" - ");
            String caseIdStr = selected.substring(startIndex, endIndex);
            int caseId = Integer.parseInt(caseIdStr);
            
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            
            try {
                conn = DB.getConnection();
                DefaultTableModel model = (DefaultTableModel) documentsTable.getModel();
                model.setRowCount(0);
                
                String sql = "SELECT d.DocumentID, d.Title, d.DocumentType, d.FileName, " +
                            "d.UploadDate, ct.CaseID, ct.CaseType, " +
                            "CASE WHEN d.Status = 'Active' THEN 'Active' ELSE 'Archived' END as StatusDisplay " +
                            "FROM officer_documents d " +
                            "JOIN casetable ct ON d.CaseID = ct.CaseID " +
                            "WHERE d.OfficerID = ? AND d.CaseID = ? " +
                            "ORDER BY d.UploadDate DESC";
                
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, officerId);
                pstmt.setInt(2, caseId);
                rs = pstmt.executeQuery();
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                while (rs.next()) {
                    String caseInfo = "Case #" + rs.getInt("CaseID") + " - " + rs.getString("CaseType");
                    model.addRow(new Object[]{
                        rs.getInt("DocumentID"),
                        rs.getString("Title"),
                        rs.getString("DocumentType"),
                        caseInfo,
                        rs.getString("FileName"),
                        sdf.format(rs.getTimestamp("UploadDate")),
                        rs.getString("StatusDisplay")
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error filtering documents: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                DB.closeResources(rs, pstmt, conn);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error parsing case ID: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchDocuments() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            refreshDocumentsTable();
            return;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            DefaultTableModel model = (DefaultTableModel) documentsTable.getModel();
            model.setRowCount(0);
            
            String sql = "SELECT d.DocumentID, d.Title, d.DocumentType, d.FileName, " +
                        "d.UploadDate, ct.CaseID, ct.CaseType, " +
                        "CASE WHEN d.Status = 'Active' THEN 'Active' ELSE 'Archived' END as StatusDisplay " +
                        "FROM officer_documents d " +
                        "JOIN casetable ct ON d.CaseID = ct.CaseID " +
                        "WHERE d.OfficerID = ? AND (d.Title LIKE ? OR d.DocumentType LIKE ? OR d.FileName LIKE ?) " +
                        "ORDER BY d.UploadDate DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, officerId);
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            rs = pstmt.executeQuery();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            while (rs.next()) {
                String caseInfo = "Case #" + rs.getInt("CaseID") + " - " + rs.getString("CaseType");
                model.addRow(new Object[]{
                    rs.getInt("DocumentID"),
                    rs.getString("Title"),
                    rs.getString("DocumentType"),
                    caseInfo,
                    rs.getString("FileName"),
                    sdf.format(rs.getTimestamp("UploadDate")),
                    rs.getString("StatusDisplay")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching documents: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private void viewDocumentDetails() {
        int selectedRow = documentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a document to view details.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) documentsTable.getModel();
        int documentId = (int) model.getValueAt(selectedRow, 0);
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT d.*, ct.CaseType, ct.Description as CaseDescription, " +
                        "o.Name as OfficerName " +
                        "FROM officer_documents d " +
                        "JOIN casetable ct ON d.CaseID = ct.CaseID " +
                        "JOIN officer o ON d.OfficerID = o.OfficerID " +
                        "WHERE d.DocumentID = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, documentId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                StringBuilder details = new StringBuilder();
                details.append("DOCUMENT DETAILS\n\n");
                details.append("Document ID: ").append(rs.getInt("DocumentID")).append("\n");
                details.append("Title: ").append(rs.getString("Title")).append("\n");
                details.append("Type: ").append(rs.getString("DocumentType")).append("\n");
                details.append("File Name: ").append(rs.getString("FileName")).append("\n");
                details.append("Status: ").append(rs.getString("Status")).append("\n");
                
                details.append("\nRELATED CASE:\n");
                details.append("Case ID: ").append(rs.getInt("CaseID")).append("\n");
                details.append("Case Type: ").append(rs.getString("CaseType")).append("\n");
                details.append("Case Description: ").append(rs.getString("CaseDescription")).append("\n");
                
                if (rs.getString("Description") != null) {
                    details.append("\nDOCUMENT DESCRIPTION:\n");
                    details.append(rs.getString("Description")).append("\n");
                }
                
                details.append("\nUPLOAD INFORMATION:\n");
                details.append("Uploaded By: ").append(rs.getString("OfficerName")).append("\n");
                details.append("Upload Date: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("UploadDate"))).append("\n");
                
                if (rs.getString("FilePath") != null) {
                    details.append("File Path: ").append(rs.getString("FilePath")).append("\n");
                }
                
                JTextArea detailsArea = new JTextArea(details.toString());
                detailsArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(detailsArea);
                scrollPane.setPreferredSize(new Dimension(600, 400));
                
                JOptionPane.showMessageDialog(this, scrollPane, "Document Details - ID: " + documentId, JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Document details not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading document details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private void downloadDocument() {
        int selectedRow = documentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a document to download.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) documentsTable.getModel();
        int documentId = (int) model.getValueAt(selectedRow, 0);
        String fileName = (String) model.getValueAt(selectedRow, 4);
        
        // In a real application, this would handle actual file download
        // For now, we'll just show a message
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Document As");
        fileChooser.setSelectedFile(new java.io.File(fileName));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            
            // Simulate download
            try {
                // In real application, you would copy the file from source to destination
                JOptionPane.showMessageDialog(this, 
                    "Document '" + fileName + "' would be downloaded to:\n" + 
                    selectedFile.getAbsolutePath() + "\n\n" +
                    "(In a real application, this would copy the actual file)",
                    "Download Simulation", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                logOfficerAction("Document Download", "Downloaded document ID: " + documentId);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error downloading document: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteDocument() {
        int selectedRow = documentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a document to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) documentsTable.getModel();
        int documentId = (int) model.getValueAt(selectedRow, 0);
        String title = (String) model.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete document:\n\"" + title + "\"\n\n" +
            "Note: This will mark the document as archived, not permanently delete it.",
            "Confirm Document Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            PreparedStatement pstmt = null;
            
            try {
                conn = DB.getConnection();
                String sql = "UPDATE officer_documents SET Status = 'Archived' WHERE DocumentID = ? AND OfficerID = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, documentId);
                pstmt.setInt(2, officerId);
                
                int rows = pstmt.executeUpdate();
                
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Document marked as archived successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    logOfficerAction("Document Archived", "Document ID: " + documentId + " - " + title);
                    refreshDocumentsTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Document not found or you don't have permission to delete it.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting document: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                DB.closeResources(null, pstmt, conn);
            }
        }
    }
    
    private void refreshDocumentsTable() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            DefaultTableModel model = (DefaultTableModel) documentsTable.getModel();
            model.setRowCount(0);
            
            // Check if table exists
            String checkTable = "SHOW TABLES LIKE 'officer_documents'";
            Statement stmt = conn.createStatement();
            ResultSet tableCheck = stmt.executeQuery(checkTable);
            
            if (!tableCheck.next()) {
                // Table doesn't exist yet, create it
                String createTable = "CREATE TABLE IF NOT EXISTS officer_documents (" +
                                   "DocumentID INT AUTO_INCREMENT PRIMARY KEY, " +
                                   "CaseID INT, " +
                                   "OfficerID INT, " +
                                   "Title VARCHAR(200), " +
                                   "DocumentType VARCHAR(100), " +
                                   "Description TEXT, " +
                                   "FileName VARCHAR(255), " +
                                   "FilePath VARCHAR(500), " +
                                   "UploadDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                   "Status VARCHAR(20) DEFAULT 'Active', " +
                                   "FOREIGN KEY (CaseID) REFERENCES casetable(CaseID), " +
                                   "FOREIGN KEY (OfficerID) REFERENCES officer(OfficerID))";
                stmt.execute(createTable);
            }
            
            String sql = "SELECT d.DocumentID, d.Title, d.DocumentType, d.FileName, " +
                        "d.UploadDate, ct.CaseID, ct.CaseType, " +
                        "CASE WHEN d.Status = 'Active' THEN 'Active' ELSE 'Archived' END as StatusDisplay " +
                        "FROM officer_documents d " +
                        "JOIN casetable ct ON d.CaseID = ct.CaseID " +
                        "WHERE d.OfficerID = ? " +
                        "ORDER BY d.UploadDate DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, officerId);
            rs = pstmt.executeQuery();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            while (rs.next()) {
                String caseInfo = "Case #" + rs.getInt("CaseID") + " - " + rs.getString("CaseType");
                model.addRow(new Object[]{
                    rs.getInt("DocumentID"),
                    rs.getString("Title"),
                    rs.getString("DocumentType"),
                    caseInfo,
                    rs.getString("FileName"),
                    sdf.format(rs.getTimestamp("UploadDate")),
                    rs.getString("StatusDisplay")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error refreshing documents: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private JPanel createOperationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(backgroundColor);
        
        JLabel title = new JLabel("Operations Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(textPrimaryColor);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Create operations table
        try {
            assignedOperationsTable = createOperationsTable();
            JScrollPane scrollPane = new JScrollPane(assignedOperationsTable);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            panel.add(title, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);
        } catch (SQLException e) {
            panel.add(new JLabel("Error loading operations: " + e.getMessage()), BorderLayout.CENTER);
            System.err.println("Error creating operations table: " + e.getMessage());
        }
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(backgroundColor);
        
        JButton startOpBtn = createActionButton("⚡ Start Operation", new Color(46, 204, 113));
        JButton updateOpBtn = createActionButton("🔄 Update Progress", new Color(52, 152, 219));
        JButton completeOpBtn = createActionButton("✅ Complete", new Color(39, 174, 96));
        JButton viewDetailsBtn = createActionButton("👁️ View Details", new Color(155, 89, 182));
        JButton refreshBtn = createActionButton("🔄 Refresh", new Color(241, 196, 15));
        
        startOpBtn.addActionListener(e -> startSelectedOperation());
        updateOpBtn.addActionListener(e -> updateOperationProgress());
        completeOpBtn.addActionListener(e -> completeOperation());
        viewDetailsBtn.addActionListener(e -> viewOperationDetails());
        refreshBtn.addActionListener(e -> refreshOperationsTable());
        
        buttonPanel.add(startOpBtn);
        buttonPanel.add(updateOpBtn);
        buttonPanel.add(completeOpBtn);
        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(refreshBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JTable createOperationsTable() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            
            // Check if assigned_operations table exists
            String checkSql = "SHOW TABLES LIKE 'assigned_operations'";
            Statement stmt = conn.createStatement();
            ResultSet tableCheck = stmt.executeQuery(checkSql);
            
            if (!tableCheck.next()) {
                // Table doesn't exist, return empty table
                DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Operation ID", "Operation Name", "Case ID", "Priority", "Status", "Assigned Date", "Due Date"}, 0
                ) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                
                // Add a message row
                model.addRow(new Object[]{"-", "No operations table found", "-", "-", "-", "-", "Please create assigned_operations table"});
                
                JTable table = new JTable(model);
                table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                table.setRowHeight(30);
                table.setBackground(cardBgColor);
                table.setForeground(textPrimaryColor);
                table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
                return table;
            }
            
            // Table exists, load data
            String sql = "SELECT ao.OperationID, ao.OperationName, ao.Priority, ao.Status, " +
                        "ao.AssignedDate, ao.DueDate, ct.CaseID, ct.CaseType " +
                        "FROM assigned_operations ao " +
                        "LEFT JOIN casetable ct ON ao.CaseID = ct.CaseID " +
                        "WHERE ao.OfficerID = ? " +
                        "ORDER BY ao.Priority DESC, ao.AssignedDate DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, officerId);
            rs = pstmt.executeQuery();
            
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Operation ID", "Operation Name", "Case ID", "Case Type", "Priority", "Status", "Assigned Date", "Due Date"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                String dueDate = rs.getDate("DueDate") != null ? sdf.format(rs.getDate("DueDate")) : "N/A";
                model.addRow(new Object[]{
                    rs.getInt("OperationID"),
                    rs.getString("OperationName"),
                    rs.getInt("CaseID"),
                    rs.getString("CaseType"),
                    rs.getString("Priority"),
                    rs.getString("Status"),
                    sdf.format(rs.getTimestamp("AssignedDate")),
                    dueDate
                });
            }
            
            if (rowCount == 0) {
                model.addRow(new Object[]{"-", "No operations assigned", "-", "-", "-", "-", "-", "Contact admin to assign operations"});
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
    
    private void startSelectedOperation() {
        int selectedRow = assignedOperationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an operation to start.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) assignedOperationsTable.getModel();
        Object operationIdObj = model.getValueAt(selectedRow, 0);
        
        // Check if it's a placeholder row
        if (operationIdObj instanceof String && ((String)operationIdObj).equals("-")) {
            JOptionPane.showMessageDialog(this, "This is a placeholder row. No operations are available.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int operationId = (int) operationIdObj;
        String operationName = (String) model.getValueAt(selectedRow, 1);
        Object caseIdObj = model.getValueAt(selectedRow, 2);
        
        int caseId = 0;
        String caseType = "General Case";
        
        if (caseIdObj instanceof Integer) {
            caseId = (int) caseIdObj;
            caseType = (String) model.getValueAt(selectedRow, 3);
        }
        
        // Update status to IN_PROGRESS
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DB.getConnection();
            String sql = "UPDATE assigned_operations SET Status = 'IN_PROGRESS' WHERE OperationID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, operationId);
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Operation started: " + operationName, "Success", JOptionPane.INFORMATION_MESSAGE);
                logOfficerAction("Operation Started", operationName + " (ID: " + operationId + ")");
                refreshOperationsTable();
                
                // Execute the operation
                executeOperation(caseId, caseType, operationName);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error starting operation: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            DB.closeResources(null, pstmt, conn);
        }
    }
    
    private void updateOperationProgress() {
        int selectedRow = assignedOperationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an operation to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) assignedOperationsTable.getModel();
        Object operationIdObj = model.getValueAt(selectedRow, 0);
        
        // Check if it's a placeholder row
        if (operationIdObj instanceof String && ((String)operationIdObj).equals("-")) {
            JOptionPane.showMessageDialog(this, "Cannot update placeholder rows.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int operationId = (int) operationIdObj;
        String operationName = (String) model.getValueAt(selectedRow, 1);
        String currentStatus = (String) model.getValueAt(selectedRow, 5);

        JDialog dialog = new JDialog(this, "Update Operation Progress", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(cardBgColor);

        JComboBox<String> statusCombo = new JComboBox<>(new String[]{
            "PENDING", "IN_PROGRESS", "ON_HOLD", "COMPLETED"
        });
        statusCombo.setSelectedItem(currentStatus);
        
        JTextArea progressArea = new JTextArea(4, 20);
        progressArea.setLineWrap(true);
        progressArea.setWrapStyleWord(true);
        JScrollPane progressScroll = new JScrollPane(progressArea);

        formPanel.add(new JLabel("Operation:"));
        formPanel.add(new JLabel(operationName));
        formPanel.add(new JLabel("New Status:"));
        formPanel.add(statusCombo);
        formPanel.add(new JLabel("Progress Notes:"));
        formPanel.add(progressScroll);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(cardBgColor);
        JButton updateBtn = createActionButton("Update", new Color(46, 204, 113));
        JButton cancelBtn = createActionButton("Cancel", new Color(231, 76, 60));
        
        updateBtn.addActionListener(e -> {
            String newStatus = (String) statusCombo.getSelectedItem();
            String notes = progressArea.getText();
            
            if (updateOperationStatus(operationId, newStatus, notes)) {
                JOptionPane.showMessageDialog(this, "Operation progress updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshOperationsTable();
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private boolean updateOperationStatus(int operationId, String newStatus, String notes) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DB.getConnection();
            
            String sql;
            if (newStatus.equals("COMPLETED")) {
                sql = "UPDATE assigned_operations SET Status = ?, CompletedDate = CURDATE(), Comments = ? WHERE OperationID = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, newStatus);
                pstmt.setString(2, notes);
                pstmt.setInt(3, operationId);
            } else {
                sql = "UPDATE assigned_operations SET Status = ?, Comments = ? WHERE OperationID = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, newStatus);
                pstmt.setString(2, notes);
                pstmt.setInt(3, operationId);
            }
            
            int rows = pstmt.executeUpdate();
            
            // Log the action
            if (rows > 0) {
                String logSql = "INSERT INTO system_logs (User, Action, Details) VALUES (?, ?, ?)";
                pstmt = conn.prepareStatement(logSql);
                pstmt.setString(1, username);
                pstmt.setString(2, "Operation Status Updated");
                pstmt.setString(3, "Operation ID: " + operationId + ", Status: " + newStatus);
                pstmt.executeUpdate();
            }
            
            return rows > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating operation: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            DB.closeResources(null, pstmt, conn);
        }
    }
    
    private void completeOperation() {
        int selectedRow = assignedOperationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an operation to complete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) assignedOperationsTable.getModel();
        Object operationIdObj = model.getValueAt(selectedRow, 0);
        
        // Check if it's a placeholder row
        if (operationIdObj instanceof String && ((String)operationIdObj).equals("-")) {
            JOptionPane.showMessageDialog(this, "Cannot complete placeholder rows.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int operationId = (int) operationIdObj;
        String operationName = (String) model.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to mark '" + operationName + "' as completed?",
            "Confirm Completion", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (updateOperationStatus(operationId, "COMPLETED", "Operation completed by officer")) {
                JOptionPane.showMessageDialog(this, "Operation marked as completed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshOperationsTable();
            }
        }
    }
    
    private void viewOperationDetails() {
        int selectedRow = assignedOperationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an operation to view details.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) assignedOperationsTable.getModel();
        Object operationIdObj = model.getValueAt(selectedRow, 0);
        
        // Check if it's a placeholder row
        if (operationIdObj instanceof String && ((String)operationIdObj).equals("-")) {
            JOptionPane.showMessageDialog(this, "No details available for placeholder rows.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int operationId = (int) operationIdObj;
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT ao.*, o.Name as OfficerName, ct.CaseType, ct.Description as CaseDescription " +
                        "FROM assigned_operations ao " +
                        "JOIN officer o ON ao.OfficerID = o.OfficerID " +
                        "LEFT JOIN casetable ct ON ao.CaseID = ct.CaseID " +
                        "WHERE ao.OperationID = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, operationId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                StringBuilder details = new StringBuilder();
                details.append("OPERATION DETAILS\n\n");
                details.append("Operation ID: ").append(rs.getInt("OperationID")).append("\n");
                details.append("Name: ").append(rs.getString("OperationName")).append("\n");
                details.append("Priority: ").append(rs.getString("Priority")).append("\n");
                details.append("Status: ").append(rs.getString("Status")).append("\n");
                details.append("Assigned Date: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(rs.getTimestamp("AssignedDate"))).append("\n");
                
                if (rs.getDate("DueDate") != null) {
                    details.append("Due Date: ").append(new SimpleDateFormat("yyyy-MM-dd").format(rs.getDate("DueDate"))).append("\n");
                }
                
                if (rs.getString("Comments") != null) {
                    details.append("Comments: ").append(rs.getString("Comments")).append("\n");
                }
                
                if (rs.getInt("CaseID") != 0) {
                    details.append("\nRELATED CASE:\n");
                    details.append("Case ID: ").append(rs.getInt("CaseID")).append("\n");
                    details.append("Case Type: ").append(rs.getString("CaseType")).append("\n");
                    details.append("Description: ").append(rs.getString("CaseDescription")).append("\n");
                }
                
                details.append("\nASSIGNED OFFICER:\n");
                details.append("Name: ").append(rs.getString("OfficerName")).append("\n");
                
                JTextArea detailsArea = new JTextArea(details.toString());
                detailsArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(detailsArea);
                scrollPane.setPreferredSize(new Dimension(500, 400));
                
                JOptionPane.showMessageDialog(this, scrollPane, "Operation Details - ID: " + operationId, JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Operation details not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading operation details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private void refreshOperationsTable() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            DefaultTableModel model = (DefaultTableModel) assignedOperationsTable.getModel();
            model.setRowCount(0);
            
            // Check if table exists
            String checkSql = "SHOW TABLES LIKE 'assigned_operations'";
            Statement stmt = conn.createStatement();
            ResultSet tableCheck = stmt.executeQuery(checkSql);
            
            if (!tableCheck.next()) {
                // Table doesn't exist, add placeholder
                model.addRow(new Object[]{"-", "No operations table found", "-", "-", "-", "-", "-", "Please create assigned_operations table"});
                return;
            }
            
            // Table exists, load data
            String sql = "SELECT ao.OperationID, ao.OperationName, ao.Priority, ao.Status, " +
                        "ao.AssignedDate, ao.DueDate, ct.CaseID, ct.CaseType " +
                        "FROM assigned_operations ao " +
                        "LEFT JOIN casetable ct ON ao.CaseID = ct.CaseID " +
                        "WHERE ao.OfficerID = ? " +
                        "ORDER BY ao.Priority DESC, ao.AssignedDate DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, officerId);
            rs = pstmt.executeQuery();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                String dueDate = rs.getDate("DueDate") != null ? sdf.format(rs.getDate("DueDate")) : "N/A";
                model.addRow(new Object[]{
                    rs.getInt("OperationID"),
                    rs.getString("OperationName"),
                    rs.getInt("CaseID"),
                    rs.getString("CaseType"),
                    rs.getString("Priority"),
                    rs.getString("Status"),
                    sdf.format(rs.getTimestamp("AssignedDate")),
                    dueDate
                });
            }
            
            if (rowCount == 0) {
                model.addRow(new Object[]{"-", "No operations assigned", "-", "-", "-", "-", "-", "Contact admin to assign operations"});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error refreshing operations: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private JPanel createRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(backgroundColor);
        
        JLabel title = new JLabel("Service Requests - Department: " + getDepartmentName());
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(textPrimaryColor);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Create requests table
        try {
            pendingRequestsTable = createPendingRequestsTable();
            JScrollPane scrollPane = new JScrollPane(pendingRequestsTable);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            panel.add(title, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);
        } catch (SQLException e) {
            panel.add(new JLabel("Error loading service requests: " + e.getMessage()), BorderLayout.CENTER);
            System.err.println("Error creating requests table: " + e.getMessage());
        }
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(backgroundColor);
        
        JButton processBtn = createActionButton("⚡ Process Request", new Color(46, 204, 113));
        JButton viewCitizenBtn = createActionButton("👤 View Citizen", new Color(155, 89, 182));
        JButton refreshBtn = createActionButton("🔄 Refresh", new Color(241, 196, 15));
        
        processBtn.addActionListener(e -> processServiceRequest());
        viewCitizenBtn.addActionListener(e -> viewRequestingCitizen());
        refreshBtn.addActionListener(e -> refreshRequestsTable());
        
        buttonPanel.add(processBtn);
        buttonPanel.add(viewCitizenBtn);
        buttonPanel.add(refreshBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private String getDepartmentName() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT Name FROM department WHERE DepartmentID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, departmentId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("Name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
        return "Unknown Department";
    }
    
    private JTable createPendingRequestsTable() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT sr.ServiceRequestID, sr.RequestType, sr.Description, " +
                        "sr.Status, sr.CreatedAt, c.FullName as CitizenName " +
                        "FROM servicerequest sr " +
                        "JOIN citizen c ON sr.CitizenID = c.CitizenID " +
                        "WHERE sr.DepartmentID = ? " +
                        "ORDER BY sr.CreatedAt DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, departmentId);
            rs = pstmt.executeQuery();
            
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Request ID", "Request Type", "Citizen", "Status", "Date Submitted", "Description"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            while (rs.next()) {
                String description = rs.getString("Description");
                if (description != null && description.length() > 50) {
                    description = description.substring(0, 47) + "...";
                }
                
                model.addRow(new Object[]{
                    rs.getInt("ServiceRequestID"),
                    rs.getString("RequestType"),
                    rs.getString("CitizenName"),
                    rs.getString("Status"),
                    sdf.format(rs.getTimestamp("CreatedAt")),
                    description
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
    
    private void processServiceRequest() {
        int selectedRow = pendingRequestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to process.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) pendingRequestsTable.getModel();
        int requestId = (int) model.getValueAt(selectedRow, 0);
        String requestType = (String) model.getValueAt(selectedRow, 1);
        String citizenName = (String) model.getValueAt(selectedRow, 2);

        JDialog dialog = new JDialog(this, "Process Service Request", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(cardBgColor);

        JComboBox<String> actionCombo = new JComboBox<>(new String[]{
            "Approve", "Reject", "Request More Information", "Assign to Case", "Mark as Completed"
        });
        
        JTextArea commentsArea = new JTextArea(4, 20);
        commentsArea.setLineWrap(true);
        commentsArea.setWrapStyleWord(true);
        JScrollPane commentsScroll = new JScrollPane(commentsArea);

        formPanel.add(new JLabel("Request ID:"));
        formPanel.add(new JLabel(String.valueOf(requestId)));
        formPanel.add(new JLabel("Request Type:"));
        formPanel.add(new JLabel(requestType));
        formPanel.add(new JLabel("Citizen:"));
        formPanel.add(new JLabel(citizenName));
        formPanel.add(new JLabel("Action:"));
        formPanel.add(actionCombo);
        formPanel.add(new JLabel("Comments:"));
        formPanel.add(commentsScroll);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(cardBgColor);
        JButton processBtn = createActionButton("Process", new Color(46, 204, 113));
        JButton cancelBtn = createActionButton("Cancel", new Color(231, 76, 60));
        
        processBtn.addActionListener(e -> {
            String action = (String) actionCombo.getSelectedItem();
            String comments = commentsArea.getText();
            
            if (processRequestInDB(requestId, action, comments)) {
                JOptionPane.showMessageDialog(this, "Request processed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                logOfficerAction("Request Processed", action + " request ID: " + requestId);
                dialog.dispose();
                refreshRequestsTable();
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(processBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private boolean processRequestInDB(int requestId, String action, String comments) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DB.getConnection();
            
            // Update the request status
            String status = "Pending";
            if (action.equals("Approve")) status = "Approved";
            else if (action.equals("Reject")) status = "Rejected";
            else if (action.equals("Mark as Completed")) status = "Completed";
            
            String sql = "UPDATE servicerequest SET Status = ? WHERE ServiceRequestID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, requestId);
            
            int rows = pstmt.executeUpdate();
            
            // Log the action
            if (rows > 0) {
                String logSql = "INSERT INTO system_logs (User, Action, Details) VALUES (?, ?, ?)";
                pstmt = conn.prepareStatement(logSql);
                pstmt.setString(1, username);
                pstmt.setString(2, "Service Request " + action);
                pstmt.setString(3, "Request ID: " + requestId + ", Comments: " + comments);
                pstmt.executeUpdate();
            }
            
            return rows > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error processing request: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        } finally {
            DB.closeResources(null, pstmt, conn);
        }
    }
    
    private void viewRequestingCitizen() {
        int selectedRow = pendingRequestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to view citizen.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) pendingRequestsTable.getModel();
        String citizenName = (String) model.getValueAt(selectedRow, 2);
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT * FROM citizen WHERE FullName = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, citizenName);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                StringBuilder citizenInfo = new StringBuilder();
                citizenInfo.append("CITIZEN INFORMATION\n\n");
                citizenInfo.append("Name: ").append(rs.getString("FullName")).append("\n");
                citizenInfo.append("National ID: ").append(rs.getString("NationalID")).append("\n");
                citizenInfo.append("Address: ").append(rs.getString("Address")).append("\n");
                citizenInfo.append("Contact: ").append(rs.getString("Contact")).append("\n");
                citizenInfo.append("Registered: ").append(new SimpleDateFormat("yyyy-MM-dd").format(rs.getTimestamp("CreatedAt"))).append("\n");
                
                JTextArea infoArea = new JTextArea(citizenInfo.toString());
                infoArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(infoArea);
                scrollPane.setPreferredSize(new Dimension(400, 250));
                
                JOptionPane.showMessageDialog(this, scrollPane, "Citizen Information", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading citizen information: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private void refreshRequestsTable() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            DefaultTableModel model = (DefaultTableModel) pendingRequestsTable.getModel();
            model.setRowCount(0);
            
            String sql = "SELECT sr.ServiceRequestID, sr.RequestType, sr.Description, " +
                        "sr.Status, sr.CreatedAt, c.FullName as CitizenName " +
                        "FROM servicerequest sr " +
                        "JOIN citizen c ON sr.CitizenID = c.CitizenID " +
                        "WHERE sr.DepartmentID = ? " +
                        "ORDER BY sr.CreatedAt DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, departmentId);
            rs = pstmt.executeQuery();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            while (rs.next()) {
                String description = rs.getString("Description");
                if (description != null && description.length() > 50) {
                    description = description.substring(0, 47) + "...";
                }
                
                model.addRow(new Object[]{
                    rs.getInt("ServiceRequestID"),
                    rs.getString("RequestType"),
                    rs.getString("CitizenName"),
                    rs.getString("Status"),
                    sdf.format(rs.getTimestamp("CreatedAt")),
                    description
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error refreshing requests: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private JPanel createCitizensPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(backgroundColor);
        
        JLabel title = new JLabel("Citizen Records");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(textPrimaryColor);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Create citizens table
        try {
            JTable citizensTable = createCitizensTable();
            JScrollPane scrollPane = new JScrollPane(citizensTable);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            panel.add(title, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);
        } catch (SQLException e) {
            panel.add(new JLabel("Error loading citizen records: " + e.getMessage()), BorderLayout.CENTER);
            System.err.println("Error creating citizens table: " + e.getMessage());
        }
        
        // Search functionality
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(backgroundColor);
        
        JTextField searchField = new JTextField(20);
        JButton searchBtn = createActionButton("🔍 Search", new Color(52, 152, 219));
        
        searchBtn.addActionListener(e -> searchCitizens(searchField.getText()));
        
        searchPanel.add(new JLabel("Search by Name or ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        
        panel.add(searchPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JTable createCitizensTable() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT CitizenID, FullName, NationalID, Address, Contact, CreatedAt " +
                        "FROM citizen " +
                        "ORDER BY CreatedAt DESC LIMIT 50";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Citizen ID", "Full Name", "National ID", "Address", "Contact", "Registered"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("CitizenID"),
                    rs.getString("FullName"),
                    rs.getString("NationalID"),
                    rs.getString("Address"),
                    rs.getString("Contact"),
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
    
    private void searchCitizens(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.", "Search", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DB.getConnection();
            String sql = "SELECT CitizenID, FullName, NationalID, Address, Contact, CreatedAt " +
                        "FROM citizen " +
                        "WHERE FullName LIKE ? OR NationalID LIKE ? " +
                        "ORDER BY FullName";
            
            pstmt = conn.prepareStatement(sql);
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            rs = pstmt.executeQuery();
            
            StringBuilder results = new StringBuilder();
            results.append("SEARCH RESULTS FOR: ").append(searchTerm).append("\n\n");
            
            int count = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            while (rs.next()) {
                count++;
                results.append(count).append(". ").append(rs.getString("FullName")).append("\n");
                results.append("   ID: ").append(rs.getString("NationalID")).append("\n");
                results.append("   Contact: ").append(rs.getString("Contact")).append("\n");
                results.append("   Address: ").append(rs.getString("Address")).append("\n");
                results.append("   Registered: ").append(sdf.format(rs.getTimestamp("CreatedAt"))).append("\n\n");
            }
            
            if (count == 0) {
                results.append("No citizens found matching your search.\n");
            }
            
            JTextArea resultsArea = new JTextArea(results.toString());
            resultsArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(resultsArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Search Results (" + count + " found)", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching citizens: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            DB.closeResources(rs, pstmt, conn);
        }
    }
    
    private JPanel createTasksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(backgroundColor);
        
        JLabel title = new JLabel("Daily Tasks & Reminders");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(textPrimaryColor);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Create tasks list
        JTextArea tasksArea = new JTextArea();
        tasksArea.setEditable(false);
        tasksArea.setBackground(cardBgColor);
        tasksArea.setForeground(textPrimaryColor);
        tasksArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tasksArea.setLineWrap(true);
        tasksArea.setWrapStyleWord(true);
        
        // Get officer tasks
        String tasks = getOfficerTasks();
        tasksArea.setText(tasks);
        
        JScrollPane scrollPane = new JScrollPane(tasksArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(darkMode ? Color.DARK_GRAY : Color.LIGHT_GRAY));
        
        // Add task button
        JButton addTaskBtn = createActionButton("➕ Add Task", new Color(46, 204, 113));
        addTaskBtn.addActionListener(e -> addNewTask());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(addTaskBtn);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private String getOfficerTasks() {
        StringBuilder tasks = new StringBuilder();
        tasks.append("DAILY TASKS & REMINDERS\n");
        tasks.append("Officer: ").append(officerName).append("\n");
        tasks.append("Date: ").append(new SimpleDateFormat("MMMM dd, yyyy").format(new Date())).append("\n\n");
        
        tasks.append("📋 PRIORITY TASKS:\n");
        tasks.append("1. Review pending service requests\n");
        tasks.append("2. Start/complete assigned operations\n");
        tasks.append("3. Update status on assigned cases\n");
        tasks.append("4. Process citizen document requests\n");
        tasks.append("5. Follow up on high-priority cases\n\n");
        
        tasks.append("⚙️ OPERATIONS TO PERFORM:\n");
        tasks.append("• Check assigned operations in Operations Management\n");
        tasks.append("• Start operations on time\n");
        tasks.append("• Document operation progress\n");
        tasks.append("• Complete operations before deadlines\n\n");
        
        tasks.append("⏰ REMINDERS:\n");
        tasks.append("• Submit weekly report by Friday 5 PM\n");
        tasks.append("• Schedule case reviews for next week\n");
        tasks.append("• Check for new citizen applications\n");
        tasks.append("• Update case documentation\n");
        tasks.append("• Review operation status daily\n\n");
        
        tasks.append("📅 UPCOMING DEADLINES:\n");
        tasks.append("• Case #123 - Review due tomorrow\n");
        tasks.append("• Operation #456 - Complete by end of week\n");
        tasks.append("• Monthly report - Due end of month\n");
        tasks.append("• Team meeting - Every Wednesday 10 AM\n");
        
        return tasks.toString();
    }
    
    private void addNewTask() {
        JTextArea taskArea = new JTextArea(5, 30);
        taskArea.setLineWrap(true);
        taskArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(taskArea);
        
        int result = JOptionPane.showConfirmDialog(this, scrollPane, "Add New Task", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION && !taskArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Task added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            logOfficerAction("Task Added", "Added new daily task");
        }
    }
    
    private void logOfficerAction(String action, String details) {
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
            // Try to create the table if it doesn't exist
            try {
                Statement stmt = conn.createStatement();
                String createTable = "CREATE TABLE IF NOT EXISTS system_logs (" +
                                   "LogID INT AUTO_INCREMENT PRIMARY KEY, " +
                                   "User VARCHAR(100), " +
                                   "Action VARCHAR(200), " +
                                   "Details TEXT, " +
                                   "Timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
                stmt.execute(createTable);
                
                // Try again after creating table
                pstmt = conn.prepareStatement(action);
                pstmt.setString(1, username);
                pstmt.setString(2, action);
                pstmt.setString(3, details);
                pstmt.executeUpdate();
            } catch (SQLException ex) {
                System.err.println("Could not log officer action: " + ex.getMessage());
            }
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
            logOfficerAction("Logout", "Officer logged out of the system");
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
            new OfficerDashboard(1, "officer_jean");
        });
    }
}