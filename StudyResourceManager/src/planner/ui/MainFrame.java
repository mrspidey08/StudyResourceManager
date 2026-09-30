package planner.ui;

import planner.dao.CurriculumDAO;
import planner.exception.CurriculumException;
import planner.model.*;
import planner.service.DiagnosticService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Pattern;

public class MainFrame extends JFrame {
    private final CurriculumDAO dao;
    private final DiagnosticService diagnosticService;

    private CardLayout cardLayout;
    private JPanel mainContainer;
    private User currentUser;
    private boolean isUpdatingCombos = false;

    // Login Components
    private JTextField userField;
    private JPasswordField passField;

    // Dashboard Header Components
    private JLabel userStatusLabel;
    private JComboBox<Subject> subjectCombo;
    private JComboBox<CurriculumModule> moduleCombo;

    // Diagnostic Readiness Components
    private JProgressBar readinessBar;
    private JLabel riskBadgeLabel;
    private JLabel moduleSummaryLabel;

    // Topics Table & Filter
    private JTextField topicSearchField;
    private JTable topicTable;
    private DefaultTableModel topicModel;
    private TableRowSorter<DefaultTableModel> topicSorter;

    // Resources Table
    private JTable resourceTable;
    private DefaultTableModel resourceModel;
    private Vector<Resource> currentLoadedResources = new Vector<>();
    private JLabel selectedTopicTitleLabel;

    // Contributor Controls
    private JPanel adminToolbar;
    private JButton addSubjectBtn;
    private JButton addModuleBtn;
    private JButton addTopicBtn;
    private JButton deleteTopicBtn;
    private JButton addResourceBtn;
    private JButton deleteResourceBtn;

    public MainFrame() {
        super("Study Resource Manager & Exam Diagnostic Planner");
        this.dao = new CurriculumDAO();
        this.diagnosticService = new DiagnosticService(dao);
        initializeUI();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 740);
        setMinimumSize(new Dimension(940, 620));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        mainContainer.add(createLoginPanel(), "LOGIN");
        mainContainer.add(createDashboardPanel(), "DASHBOARD");

        add(mainContainer);
        cardLayout.show(mainContainer, "LOGIN");
    }

    // =========================================================================
    // LOGIN SCREEN
    // =========================================================================
    private JPanel createLoginPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(241, 245, 249));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLbl = new JLabel("Academic Portal Authentication", JLabel.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(new Color(30, 41, 59));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(titleLbl, gbc);

        // Subtitle
        JLabel subtitleLbl = new JLabel("Study Resource Management & Diagnostic Exam Planner", JLabel.CENTER);
        subtitleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLbl.setForeground(new Color(100, 116, 139));
        gbc.gridy = 1;
        card.add(subtitleLbl, gbc);

        // Separator
        JSeparator sep = new JSeparator();
        gbc.gridy = 2;
        card.add(sep, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 3;
        gbc.gridx = 0;
        JLabel userLbl = new JLabel("Username:");
        userLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        card.add(userLbl, gbc);

        userField = new JTextField(18);
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        card.add(userField, gbc);

        // Password
        gbc.gridy = 4;
        gbc.gridx = 0;
        JLabel passLbl = new JLabel("Password:");
        passLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        card.add(passLbl, gbc);

        passField = new JPasswordField(18);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        card.add(passField, gbc);

        // Sign In Button
        JButton loginBtn = new JButton("Sign In");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setBackground(new Color(37, 99, 235));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setPreferredSize(new Dimension(200, 36));
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        card.add(loginBtn, gbc);

        // Demo credentials guide box
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        infoPanel.setBackground(new Color(248, 250, 252));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel leadInfo = new JLabel("• Contributor:  admin / admin123 (Curriculum management)");
        leadInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        leadInfo.setForeground(new Color(71, 85, 105));

        JLabel studInfo = new JLabel("• Student:  student / stud123 (Study & diagnostic audits)");
        studInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studInfo.setForeground(new Color(71, 85, 105));

        infoPanel.add(leadInfo);
        infoPanel.add(studInfo);

        gbc.gridy = 6;
        card.add(infoPanel, gbc);

        // Listeners
        loginBtn.addActionListener(e -> handleLogin());
        userField.addActionListener(e -> passField.requestFocusInWindow());
        passField.addActionListener(e -> handleLogin());

        wrapper.add(card);
        return wrapper;
    }

    // =========================================================================
    // DASHBOARD SCREEN
    // =========================================================================
    private JPanel createDashboardPanel() {
        JPanel dashboard = new JPanel(new BorderLayout(0, 0));

        // 1. TOP HEADER BAR
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setBackground(new Color(30, 41, 59));
        headerPanel.setBorder(new EmptyBorder(10, 16, 10, 16));

        JPanel navLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        navLeft.setOpaque(false);

        JLabel appTitle = new JLabel("Study Manager");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appTitle.setForeground(Color.WHITE);
        navLeft.add(appTitle);

        JLabel subjLbl = new JLabel("Subject:");
        subjLbl.setForeground(new Color(226, 232, 240));
        subjLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        navLeft.add(subjLbl);

        subjectCombo = new JComboBox<>();
        subjectCombo.setPreferredSize(new Dimension(240, 28));
        navLeft.add(subjectCombo);

        JLabel modLbl = new JLabel("Module:");
        modLbl.setForeground(new Color(226, 232, 240));
        modLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        navLeft.add(modLbl);

        moduleCombo = new JComboBox<>();
        moduleCombo.setPreferredSize(new Dimension(250, 28));
        navLeft.add(moduleCombo);

        JButton reloadBtn = new JButton("↻ Refresh");
        reloadBtn.setToolTipText("Reload subjects and modules");
        navLeft.add(reloadBtn);

        headerPanel.add(navLeft, BorderLayout.WEST);

        JPanel navRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        navRight.setOpaque(false);

        userStatusLabel = new JLabel("User: Guest");
        userStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userStatusLabel.setForeground(Color.WHITE);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFocusPainted(false);

        navRight.add(userStatusLabel);
        navRight.add(logoutBtn);
        headerPanel.add(navRight, BorderLayout.EAST);

        // 2. DIAGNOSTIC READINESS BANNER
        JPanel diagnosticBanner = new JPanel(new BorderLayout(12, 0));
        diagnosticBanner.setBackground(new Color(248, 250, 252));
        diagnosticBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(8, 16, 8, 16)
        ));

        JPanel bannerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        bannerLeft.setOpaque(false);

        moduleSummaryLabel = new JLabel("Module Readiness:");
        moduleSummaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bannerLeft.add(moduleSummaryLabel);

        readinessBar = new JProgressBar(0, 100);
        readinessBar.setStringPainted(true);
        readinessBar.setPreferredSize(new Dimension(190, 24));
        readinessBar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bannerLeft.add(readinessBar);

        riskBadgeLabel = new JLabel("[Status: N/A]");
        riskBadgeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bannerLeft.add(riskBadgeLabel);

        diagnosticBanner.add(bannerLeft, BorderLayout.WEST);

        JPanel bannerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bannerRight.setOpaque(false);

        JButton auditBtn = new JButton("Audit Readiness");
        auditBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton planBtn = new JButton("📅 Smart Study Planner & Export...");
        planBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        planBtn.setBackground(new Color(238, 242, 255));
        planBtn.setForeground(new Color(67, 56, 202));

        bannerRight.add(auditBtn);
        bannerRight.add(planBtn);
        diagnosticBanner.add(bannerRight, BorderLayout.EAST);

        // Combine Header & Banner in North
        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.add(headerPanel, BorderLayout.NORTH);
        northContainer.add(diagnosticBanner, BorderLayout.SOUTH);
        dashboard.add(northContainer, BorderLayout.NORTH);

        // 3. CENTER SPLIT PANE (Topics & Resources)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(6);

        // TOP CONTAINER: Topics Table
        JPanel topContainer = new JPanel(new BorderLayout(0, 6));
        topContainer.setBorder(new EmptyBorder(8, 12, 4, 12));

        JPanel topicHeaderBar = new JPanel(new BorderLayout());
        JLabel topicTitleLbl = new JLabel("Curriculum Topics & Exam Weightages");
        topicTitleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        topicHeaderBar.add(topicTitleLbl, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchPanel.add(new JLabel("Quick Filter:"));
        topicSearchField = new JTextField(16);
        topicSearchField.setToolTipText("Type to filter topics in real time");
        searchPanel.add(topicSearchField);
        topicHeaderBar.add(searchPanel, BorderLayout.EAST);

        topContainer.add(topicHeaderBar, BorderLayout.NORTH);

        topicModel = new DefaultTableModel(
                new String[] { "ID", "Topic Title", "Weightage (%)", "Notes Ready", "PYQ Ready" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        topicTable = new JTable(topicModel);
        topicTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        topicTable.setRowHeight(28);
        topicTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        topicTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Column formatting
        topicTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        topicTable.getColumnModel().getColumn(0).setMaxWidth(70);
        topicTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        topicTable.getColumnModel().getColumn(2).setMaxWidth(130);
        topicTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        topicTable.getColumnModel().getColumn(3).setMaxWidth(130);
        topicTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        topicTable.getColumnModel().getColumn(4).setMaxWidth(130);

        StatusBadgeRenderer badgeRenderer = new StatusBadgeRenderer();
        topicTable.getColumnModel().getColumn(3).setCellRenderer(badgeRenderer);
        topicTable.getColumnModel().getColumn(4).setCellRenderer(badgeRenderer);

        CenterRenderer centerRenderer = new CenterRenderer();
        topicTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        topicTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        topicSorter = new TableRowSorter<>(topicModel);
        topicTable.setRowSorter(topicSorter);

        topContainer.add(new JScrollPane(topicTable), BorderLayout.CENTER);
        splitPane.setTopComponent(topContainer);

        // BOTTOM CONTAINER: Resources Table
        JPanel bottomContainer = new JPanel(new BorderLayout(0, 6));
        bottomContainer.setBorder(new EmptyBorder(4, 12, 6, 12));

        JPanel resHeaderBar = new JPanel(new BorderLayout());
        selectedTopicTitleLabel = new JLabel("Study Materials & References (Select a topic above)");
        selectedTopicTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        resHeaderBar.add(selectedTopicTitleLabel, BorderLayout.WEST);

        JLabel hintLabel = new JLabel("Tip: Double-click any resource to open/launch");
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hintLabel.setForeground(new Color(100, 116, 139));
        resHeaderBar.add(hintLabel, BorderLayout.EAST);

        bottomContainer.add(resHeaderBar, BorderLayout.NORTH);

        resourceModel = new DefaultTableModel(
                new String[] { "ID", "Resource Title", "Type", "Pointer / Location" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resourceTable = new JTable(resourceModel);
        resourceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resourceTable.setRowHeight(28);
        resourceTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resourceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        resourceTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        resourceTable.getColumnModel().getColumn(0).setMaxWidth(70);
        resourceTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        resourceTable.getColumnModel().getColumn(2).setMaxWidth(140);
        resourceTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        resourceTable.getColumnModel().getColumn(2).setCellRenderer(new ResourceTypeRenderer());

        bottomContainer.add(new JScrollPane(resourceTable), BorderLayout.CENTER);

        JPanel resActionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        JButton launchBtn = new JButton("🚀 Launch Selected Resource");
        launchBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        resActionBar.add(launchBtn);
        bottomContainer.add(resActionBar, BorderLayout.SOUTH);

        splitPane.setBottomComponent(bottomContainer);
        dashboard.add(splitPane, BorderLayout.CENTER);

        // 4. CONTRIBUTOR TOOLBAR (SOUTH)
        adminToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        adminToolbar.setBackground(new Color(241, 245, 249));
        adminToolbar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(203, 213, 225)));

        JLabel roleLabel = new JLabel("Contributor Actions: ");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        roleLabel.setForeground(new Color(51, 65, 85));
        adminToolbar.add(roleLabel);

        addSubjectBtn = new JButton("+ Add Subject");
        addModuleBtn = new JButton("+ Add Module");
        addTopicBtn = new JButton("+ Add Topic");
        deleteTopicBtn = new JButton("- Delete Topic");
        addResourceBtn = new JButton("+ Add Resource");
        deleteResourceBtn = new JButton("- Delete Resource");

        adminToolbar.add(addSubjectBtn);
        adminToolbar.add(addModuleBtn);
        adminToolbar.add(addTopicBtn);
        adminToolbar.add(deleteTopicBtn);
        adminToolbar.add(addResourceBtn);
        adminToolbar.add(deleteResourceBtn);
        dashboard.add(adminToolbar, BorderLayout.SOUTH);

        // =====================================================================
        // EVENT LISTENERS
        // =====================================================================
        subjectCombo.addActionListener(e -> {
            if (!isUpdatingCombos) {
                loadModulesForSubject();
            }
        });

        moduleCombo.addActionListener(e -> {
            if (!isUpdatingCombos) {
                loadTopics();
            }
        });

        reloadBtn.addActionListener(e -> loadSubjects());

        topicTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadResourcesForSelectedTopic();
            }
        });

        // Double click to launch resource
        resourceTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && resourceTable.getSelectedRow() != -1) {
                    handleResourceLaunch();
                }
            }
        });

        launchBtn.addActionListener(e -> handleResourceLaunch());
        auditBtn.addActionListener(e -> handleReadinessAudit());
        planBtn.addActionListener(e -> showStudyPlannerDialog());

        logoutBtn.addActionListener(e -> {
            currentUser = null;
            passField.setText("");
            cardLayout.show(mainContainer, "LOGIN");
        });

        // Real-time topic filter
        topicSearchField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateFilter() {
                String text = topicSearchField.getText().trim();
                if (text.isEmpty()) {
                    topicSorter.setRowFilter(null);
                } else {
                    topicSorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 1));
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) { updateFilter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateFilter(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateFilter(); }
        });

        setupContributorActions();

        return dashboard;
    }

    // =========================================================================
    // AUTHENTICATION & PERMISSIONS
    // =========================================================================
    private void handleLogin() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = dao.authenticate(username, password);
        if (user != null) {
            currentUser = user;
            userStatusLabel.setText(user.getFullName() + " (" + user.getRole() + ")");
            applyRolePermissions();
            loadSubjects();
            cardLayout.show(mainContainer, "DASHBOARD");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials. Please verify username and password.",
                    "Authentication Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyRolePermissions() {
        boolean canEdit = currentUser != null && currentUser.canModifyCurriculum();
        adminToolbar.setVisible(canEdit);
        addSubjectBtn.setEnabled(canEdit);
        addModuleBtn.setEnabled(canEdit);
        addTopicBtn.setEnabled(canEdit);
        deleteTopicBtn.setEnabled(canEdit);
        addResourceBtn.setEnabled(canEdit);
        deleteResourceBtn.setEnabled(canEdit);
    }

    // =========================================================================
    // DATA LOADING & AUDIT
    // =========================================================================
    private void loadSubjects() {
        isUpdatingCombos = true;
        try {
            subjectCombo.removeAllItems();
            Vector<Subject> list = dao.getAllSubjects();
            for (Subject s : list) {
                subjectCombo.addItem(s);
            }
        } finally {
            isUpdatingCombos = false;
        }
        loadModulesForSubject();
    }

    private void loadModulesForSubject() {
        isUpdatingCombos = true;
        try {
            moduleCombo.removeAllItems();
            Subject s = (Subject) subjectCombo.getSelectedItem();
            if (s != null) {
                Vector<CurriculumModule> modules = dao.getModulesBySubject(s.getId());
                for (CurriculumModule m : modules) {
                    moduleCombo.addItem(m);
                }
            }
        } finally {
            isUpdatingCombos = false;
        }
        loadTopics();
    }

    private void loadTopics() {
        topicModel.setRowCount(0);
        resourceModel.setRowCount(0);
        currentLoadedResources.clear();
        selectedTopicTitleLabel.setText("Study Materials & References (Select a topic above)");

        CurriculumModule m = (CurriculumModule) moduleCombo.getSelectedItem();
        if (m != null) {
            Vector<Topic> topics = dao.getTopicsByModule(m.getId());
            for (Topic t : topics) {
                topicModel.addRow(new Object[] {
                        t.getId(),
                        t.getTitle(),
                        t.getExamWeightage(),
                        t.hasNotes() ? "✓ Yes" : "✗ No",
                        t.hasPyq() ? "✓ Yes" : "✗ No"
                });
            }
            updateReadinessVisuals(m.getId());
        } else {
            readinessBar.setValue(0);
            readinessBar.setString("0% Ready");
            riskBadgeLabel.setText("[Status: N/A]");
            riskBadgeLabel.setForeground(Color.DARK_GRAY);
        }
    }

    private void updateReadinessVisuals(int moduleId) {
        double score = diagnosticService.calculateReadiness(moduleId);
        String risk = diagnosticService.assessRisk(score);

        int intScore = (int) Math.round(score);
        readinessBar.setValue(intScore);
        readinessBar.setString(String.format("%.1f%% Ready", score));

        if ("HIGH RISK".equals(risk)) {
            readinessBar.setForeground(new Color(220, 38, 38));
            riskBadgeLabel.setForeground(new Color(220, 38, 38));
        } else if ("MODERATE RISK".equals(risk)) {
            readinessBar.setForeground(new Color(217, 119, 6));
            riskBadgeLabel.setForeground(new Color(217, 119, 6));
        } else {
            readinessBar.setForeground(new Color(22, 163, 74));
            riskBadgeLabel.setForeground(new Color(22, 163, 74));
        }
        riskBadgeLabel.setText("[" + risk + "]");
    }

    private void handleReadinessAudit() {
        CurriculumModule m = (CurriculumModule) moduleCombo.getSelectedItem();
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Please select a module to audit.", "Audit Notice",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        updateReadinessVisuals(m.getId());
        double score = diagnosticService.calculateReadiness(m.getId());
        String risk = diagnosticService.assessRisk(score);

        JOptionPane.showMessageDialog(this,
                String.format("Curriculum Module: %s\nReadiness Assessment: %.1f%%\nRisk Status: %s",
                        m.getName(), score, risk),
                "Diagnostic Audit Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadResourcesForSelectedTopic() {
        resourceModel.setRowCount(0);
        currentLoadedResources.clear();

        int viewRow = topicTable.getSelectedRow();
        if (viewRow != -1) {
            int modelRow = topicTable.convertRowIndexToModel(viewRow);
            int topicId = (int) topicModel.getValueAt(modelRow, 0);
            String title = (String) topicModel.getValueAt(modelRow, 1);
            selectedTopicTitleLabel.setText("Study Materials for: \"" + title + "\"");

            currentLoadedResources = dao.getResourcesByTopic(topicId);
            for (Resource r : currentLoadedResources) {
                resourceModel.addRow(new Object[] {
                        r.getId(),
                        r.getTitle(),
                        r.getResourceType(),
                        r.getUriPointer()
                });
            }
        } else {
            selectedTopicTitleLabel.setText("Study Materials & References (Select a topic above)");
        }
    }

    private void handleResourceLaunch() {
        int selectedRow = resourceTable.getSelectedRow();
        if (selectedRow != -1 && selectedRow < currentLoadedResources.size()) {
            Resource r = currentLoadedResources.get(selectedRow);
            try {
                r.launch();
            } catch (CurriculumException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Launch Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a valid resource from the table to launch.",
                    "Launch Notice", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // =========================================================================
    // SMART STUDY PLANNER & EXPORT DIALOG
    // =========================================================================
    private void showStudyPlannerDialog() {
        CurriculumModule m = (CurriculumModule) moduleCombo.getSelectedItem();
        Subject s = (Subject) subjectCombo.getSelectedItem();
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Please select a module first.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Smart Study Revision Planner & Report", true);
        dialog.setSize(720, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Inputs Panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("User Preparation Parameters"));

        JSpinner daysSpinner = new JSpinner(new SpinnerNumberModel(14, 1, 120, 1));
        JSpinner hoursSpinner = new JSpinner(new SpinnerNumberModel(3.0, 0.5, 16.0, 0.5));
        JButton generateBtn = new JButton("Generate Schedule");
        generateBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));

        inputPanel.add(new JLabel("Days until Exam:"));
        inputPanel.add(daysSpinner);
        inputPanel.add(new JLabel("Daily Study Hours:"));
        inputPanel.add(hoursSpinner);
        inputPanel.add(generateBtn);

        dialog.add(inputPanel, BorderLayout.NORTH);

        // Output Text Area
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        reportArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(reportArea);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Initial Report Generation
        Runnable generateReport = () -> {
            int days = (Integer) daysSpinner.getValue();
            double hours = ((Number) hoursSpinner.getValue()).doubleValue();
            String report = diagnosticService.generateStudyPlanReport(
                    m.getId(), m.getName(), s != null ? s.getName() : "Curriculum", days, hours);
            reportArea.setText(report);
            reportArea.setCaretPosition(0);
        };

        generateBtn.addActionListener(e -> generateReport.run());
        generateReport.run();

        // Bottom Action Bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton exportBtn = new JButton("💾 Save / Export Report to Plain Text File...");
        exportBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JButton closeBtn = new JButton("Close");

        exportBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Save Study Plan Report");
            fc.setSelectedFile(new File("StudyPlan_Module_" + m.getModuleNo() + ".txt"));
            int res = fc.showSaveDialog(dialog);
            if (res == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(reportArea.getText());
                    JOptionPane.showMessageDialog(dialog, "Report successfully saved to:\n" + file.getAbsolutePath(),
                            "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ioEx) {
                    JOptionPane.showMessageDialog(dialog, "Failed to save file: " + ioEx.getMessage(),
                            "Export Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        closeBtn.addActionListener(e -> dialog.dispose());

        bottomBar.add(exportBtn);
        bottomBar.add(closeBtn);
        dialog.add(bottomBar, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // =========================================================================
    // CONTRIBUTOR FORM DIALOGS
    // =========================================================================
    private void setupContributorActions() {
        addSubjectBtn.addActionListener(e -> showAddSubjectDialog());
        addModuleBtn.addActionListener(e -> showAddModuleDialog());
        addTopicBtn.addActionListener(e -> showAddTopicDialog());
        deleteTopicBtn.addActionListener(e -> handleDeleteTopic());
        addResourceBtn.addActionListener(e -> showAddResourceDialog());
        deleteResourceBtn.addActionListener(e -> handleDeleteResource());
    }

    private void showAddSubjectDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField codeField = new JTextField(12);
        JTextField nameField = new JTextField(24);

        g.gridx = 0; g.gridy = 0;
        panel.add(new JLabel("Subject Code (e.g. CS204):"), g);
        g.gridx = 1;
        panel.add(codeField, g);

        g.gridx = 0; g.gridy = 1;
        panel.add(new JLabel("Subject Title:"), g);
        g.gridx = 1;
        panel.add(nameField, g);

        int result = JOptionPane.showConfirmDialog(this, panel, "Create New Subject",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();
            if (code.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Both Code and Title are required.", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                dao.addSubject(code, name);
                loadSubjects();
                JOptionPane.showMessageDialog(this, "Subject '" + code + "' created successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (CurriculumException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddModuleDialog() {
        Subject s = (Subject) subjectCombo.getSelectedItem();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Please select or create a subject first.",
                    "Action Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JSpinner numSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        JTextField nameField = new JTextField(24);

        g.gridx = 0; g.gridy = 0;
        panel.add(new JLabel("Module Number:"), g);
        g.gridx = 1;
        panel.add(numSpinner, g);

        g.gridx = 0; g.gridy = 1;
        panel.add(new JLabel("Module Name:"), g);
        g.gridx = 1;
        panel.add(nameField, g);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Module to " + s.getCode(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Module name cannot be empty.", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modNo = (Integer) numSpinner.getValue();
            try {
                dao.addModule(s.getId(), modNo, name);
                loadModulesForSubject();
                JOptionPane.showMessageDialog(this, "Module " + modNo + " created successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (CurriculumException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddTopicDialog() {
        CurriculumModule m = (CurriculumModule) moduleCombo.getSelectedItem();
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Please select or create a module first.",
                    "Action Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField titleField = new JTextField(24);
        JSpinner weightSpinner = new JSpinner(new SpinnerNumberModel(15.0, 1.0, 100.0, 1.0));
        JCheckBox notesCheck = new JCheckBox("Verified Notes Available");
        JCheckBox pyqCheck = new JCheckBox("Solved PYQs Available");

        g.gridx = 0; g.gridy = 0;
        panel.add(new JLabel("Topic Title:"), g);
        g.gridx = 1;
        panel.add(titleField, g);

        g.gridx = 0; g.gridy = 1;
        panel.add(new JLabel("Exam Weightage (%):"), g);
        g.gridx = 1;
        panel.add(weightSpinner, g);

        g.gridx = 0; g.gridy = 2;
        panel.add(new JLabel("Study Notes:"), g);
        g.gridx = 1;
        panel.add(notesCheck, g);

        g.gridx = 0; g.gridy = 3;
        panel.add(new JLabel("Past Questions:"), g);
        g.gridx = 1;
        panel.add(pyqCheck, g);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Topic to Module " + m.getModuleNo(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Topic title cannot be empty.", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            double weight = ((Number) weightSpinner.getValue()).doubleValue();
            try {
                dao.addTopic(m.getId(), title, weight, notesCheck.isSelected(), pyqCheck.isSelected());
                loadTopics();
                JOptionPane.showMessageDialog(this, "Topic added successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (CurriculumException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeleteTopic() {
        int viewRow = topicTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a topic from the table to delete.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = topicTable.convertRowIndexToModel(viewRow);
        int topicId = (int) topicModel.getValueAt(modelRow, 0);
        String title = (String) topicModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete topic:\n\"" + title + "\" and all its associated resources?",
                "Confirm Delete Topic", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.deleteTopic(topicId);
                loadTopics();
                JOptionPane.showMessageDialog(this, "Topic deleted successfully.", "Deleted",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (CurriculumException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddResourceDialog() {
        int viewRow = topicTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a topic from the table first.",
                    "Action Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = topicTable.convertRowIndexToModel(viewRow);
        int topicId = (int) topicModel.getValueAt(modelRow, 0);
        String topicTitle = (String) topicModel.getValueAt(modelRow, 1);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField titleField = new JTextField(24);
        JComboBox<String> typeCombo = new JComboBox<>(new String[] { "WEB_URL", "LOCAL_FILE" });
        JTextField pointerField = new JTextField(20);
        JButton browseBtn = new JButton("Browse...");
        browseBtn.setEnabled(false);

        typeCombo.addActionListener(e -> {
            boolean isLocal = "LOCAL_FILE".equals(typeCombo.getSelectedItem());
            browseBtn.setEnabled(isLocal);
        });

        browseBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Select Study Resource File");
            int res = fc.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fc.getSelectedFile();
                pointerField.setText(selectedFile.getAbsolutePath());
            }
        });

        JPanel pointerPanel = new JPanel(new BorderLayout(6, 0));
        pointerPanel.add(pointerField, BorderLayout.CENTER);
        pointerPanel.add(browseBtn, BorderLayout.EAST);

        g.gridx = 0; g.gridy = 0;
        panel.add(new JLabel("Target Topic:"), g);
        g.gridx = 1;
        JLabel targetTopicLbl = new JLabel(topicTitle);
        targetTopicLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(targetTopicLbl, g);

        g.gridx = 0; g.gridy = 1;
        panel.add(new JLabel("Resource Title:"), g);
        g.gridx = 1;
        panel.add(titleField, g);

        g.gridx = 0; g.gridy = 2;
        panel.add(new JLabel("Resource Type:"), g);
        g.gridx = 1;
        panel.add(typeCombo, g);

        g.gridx = 0; g.gridy = 3;
        panel.add(new JLabel("URI / File Path:"), g);
        g.gridx = 1;
        panel.add(pointerPanel, g);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Study Resource",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            String pointer = pointerField.getText().trim();

            if (title.isEmpty() || pointer.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title and Path/URL cannot be empty.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                dao.addResource(topicId, title, type, pointer);
                loadResourcesForSelectedTopic();
                JOptionPane.showMessageDialog(this, "Resource added successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (CurriculumException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeleteResource() {
        int selectedRow = resourceTable.getSelectedRow();
        if (selectedRow == -1 || selectedRow >= currentLoadedResources.size()) {
            JOptionPane.showMessageDialog(this, "Please select a resource from the table to delete.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Resource r = currentLoadedResources.get(selectedRow);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete resource:\n\"" + r.getTitle() + "\"?",
                "Confirm Delete Resource", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.deleteResource(r.getId());
                loadResourcesForSelectedTopic();
                JOptionPane.showMessageDialog(this, "Resource deleted successfully.", "Deleted",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (CurriculumException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // CUSTOM TABLE RENDERERS
    // =========================================================================
    private static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            String text = value != null ? value.toString() : "";
            if (text.contains("Yes")) {
                label.setText("✓ Yes");
                label.setForeground(isSelected ? Color.WHITE : new Color(22, 101, 52));
                label.setFont(label.getFont().deriveFont(Font.BOLD));
            } else if (text.contains("No")) {
                label.setText("✗ No");
                label.setForeground(isSelected ? Color.WHITE : new Color(185, 28, 28));
                label.setFont(label.getFont().deriveFont(Font.PLAIN));
            }
            return label;
        }
    }

    private static class ResourceTypeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            String text = value != null ? value.toString() : "";
            if ("WEB_URL".equalsIgnoreCase(text)) {
                label.setText("🌐 WEB URL");
                label.setForeground(isSelected ? Color.WHITE : new Color(29, 78, 216));
            } else if ("LOCAL_FILE".equalsIgnoreCase(text)) {
                label.setText("📁 LOCAL FILE");
                label.setForeground(isSelected ? Color.WHITE : new Color(180, 83, 9));
            }
            return label;
        }
    }

    private static class CenterRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            return label;
        }
    }
}