package UI.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import Utilities.Security.Password.*;

public class VaultPanel extends JPanel {

    private static final Color PURPLE_BG = new Color(88, 70, 150);
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);

    // Chemins des images pour le bouton œil
    private static final String EYE_OPEN_PATH = "/images/eye-open.png";
    private static final String EYE_CLOSED_PATH = "/images/eye-closed.png";

    private JPanel accountListPanel;
    private JPanel detailPanel;
    private CardLayout detailCardLayout;

    private JComboBox<String> categoryFilter;
    private JComboBox<String> dateFilter;
    private JComboBox<String> nameFilter;
    private JTextField searchField;

    private List<Account> accounts;

    public VaultPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Charger le dictionnaire des mots de passe faibles
        WeakPasswordDictionary.load();

        // Créer un wrapper pour mainPanel et le bouton +
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.WHITE);

        JPanel mainPanel = createMainPanel();
        wrapperPanel.add(mainPanel, BorderLayout.CENTER);
        wrapperPanel.add(createAddButton(), BorderLayout.SOUTH);

        add(wrapperPanel, BorderLayout.CENTER);

        detailPanel = new JPanel();
        detailCardLayout = new CardLayout();
        detailPanel.setLayout(detailCardLayout);
        detailPanel.setPreferredSize(new Dimension(400, 600));
        detailPanel.setVisible(false);

        JPanel emptyDetailPanel = new JPanel();
        emptyDetailPanel.setBackground(Color.WHITE);
        detailPanel.add(emptyDetailPanel, "empty");

        add(detailPanel, BorderLayout.EAST);

        loadAccounts();
        displayAccounts();
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel filterPanel = createFilterPanel();
        panel.add(filterPanel, BorderLayout.NORTH);

        accountListPanel = new JPanel();
        accountListPanel.setLayout(new BoxLayout(accountListPanel, BoxLayout.Y_AXIS));
        accountListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(accountListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);

        JLabel categoryLabel = new JLabel("Catégorie");
        categoryFilter = new JComboBox<>(new String[]{"Réseaux Sociaux", "Banking", "Email", "Autre"});
        categoryFilter.setPreferredSize(new Dimension(150, 30));
        categoryFilter.setBackground(LIGHT_GRAY);

        JLabel dateLabel = new JLabel("Date");
        dateFilter = new JComboBox<>(new String[]{"Récent", "Plus ancien", "Cette semaine", "Ce mois"});
        dateFilter.setPreferredSize(new Dimension(120, 30));
        dateFilter.setBackground(LIGHT_GRAY);

        JLabel nameLabel = new JLabel("Nom");
        nameFilter = new JComboBox<>(new String[]{"A-Z", "Z-A"});
        nameFilter.setPreferredSize(new Dimension(100, 30));
        nameFilter.setBackground(LIGHT_GRAY);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton searchButton = new JButton("🔍");
        searchButton.setPreferredSize(new Dimension(40, 30));
        searchButton.setFocusPainted(false);

        panel.add(categoryLabel);
        panel.add(categoryFilter);
        panel.add(dateLabel);
        panel.add(dateFilter);
        panel.add(nameLabel);
        panel.add(nameFilter);
        panel.add(searchField);
        panel.add(searchButton);

        return panel;
    }

    private void loadAccounts() {
        accounts = new ArrayList<>();
        accounts.add(new Account("Instagram", "jean.jean@gmail.com", "jean.j", "••••••",
                "motdepasse123", "instagram_icon.png", "Réseaux Sociaux", 4));
        accounts.add(new Account("Facebook", "jean.jean@gmail.com", "jean.jean", "••••••",
                "facebook2024", "facebook_icon.png", "Réseaux Sociaux", 3));
        accounts.add(new Account("Gmail", "jean.jean@gmail.com", "jean.jean", "••••••",
                "gmail@secure", "gmail_icon.png", "Email", 5));
    }

    private void displayAccounts() {
        accountListPanel.removeAll();

        for (Account account : accounts) {
            JPanel accountCard = createAccountCard(account);
            accountListPanel.add(accountCard);
            accountListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        accountListPanel.revalidate();
        accountListPanel.repaint();
    }

    private JPanel createAccountCard(Account account) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(50, 50));
        iconLabel.setOpaque(true);
        iconLabel.setBackground(getColorForService(account.serviceName));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        iconLabel.setFont(new Font("Arial", Font.BOLD, 24));
        iconLabel.setText(account.serviceName.substring(0, 1));
        iconLabel.setForeground(Color.WHITE);
        card.add(iconLabel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        JLabel serviceLabel = new JLabel(account.serviceName);
        serviceLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel emailLabel = new JLabel(account.email);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        emailLabel.setForeground(Color.GRAY);

        infoPanel.add(serviceLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(emailLabel);
        card.add(infoPanel, BorderLayout.CENTER);

        JPanel credentialsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        credentialsPanel.setBackground(Color.WHITE);

        JLabel loginLabel = new JLabel(account.login);
        loginLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel passwordLabel = new JLabel(account.maskedPassword);
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JButton eyeButton = new JButton();
        eyeButton.setBorder(BorderFactory.createEmptyBorder());
        eyeButton.setBackground(Color.WHITE);
        eyeButton.setFocusPainted(false);
        eyeButton.setContentAreaFilled(false);
        eyeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Charger les images
        ImageIcon eyeOpenIcon = loadIcon(EYE_OPEN_PATH, 20, 20);
        ImageIcon eyeClosedIcon = loadIcon(EYE_CLOSED_PATH, 20, 20);

        // Utiliser directement les images (par défaut, mot de passe masqué = icône œil fermé)
        eyeButton.setIcon(eyeClosedIcon);

        eyeButton.addActionListener(e -> {
            if (eyeButton.getIcon() == eyeClosedIcon) {
                // Actuellement masqué, on affiche
                passwordLabel.setText(account.realPassword != null ? account.realPassword : account.maskedPassword);
                eyeButton.setIcon(eyeOpenIcon);
            } else {
                // Actuellement visible, on masque
                passwordLabel.setText("••••••");
                eyeButton.setIcon(eyeClosedIcon);
            }
        });

        credentialsPanel.add(loginLabel);
        credentialsPanel.add(passwordLabel);
        credentialsPanel.add(eyeButton);
        card.add(credentialsPanel, BorderLayout.EAST);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showAccountDetails(account);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(250, 250, 250));
                infoPanel.setBackground(new Color(250, 250, 250));
                credentialsPanel.setBackground(new Color(250, 250, 250));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                infoPanel.setBackground(Color.WHITE);
                credentialsPanel.setBackground(Color.WHITE);
            }
        });

        return card;
    }

    private JPanel createAddButton() {
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        buttonContainer.setBackground(Color.WHITE);

        JButton addButton = new JButton("+") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(PURPLE_BG.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(PURPLE_BG.brighter());
                } else {
                    g2d.setColor(PURPLE_BG);
                }
                g2d.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();

                super.paintComponent(g);
            }
        };

        addButton.setPreferredSize(new Dimension(50, 50));
        addButton.setFont(new Font("Arial", Font.BOLD, 24));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setContentAreaFilled(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.setToolTipText("Ajouter un nouveau compte");

        addButton.addActionListener(e -> showNewAccountPanel());

        buttonContainer.add(addButton);
        return buttonContainer;
    }

    private void showNewAccountPanel() {
        String panelName = "new_account";
        Account emptyAccount = new Account("Nouveau compte", "", "", "", "", "", "Autre", 0);
        JPanel newAccountPanel = createDetailsPanel(emptyAccount);
        detailPanel.add(newAccountPanel, panelName);

        detailPanel.setVisible(true);
        detailCardLayout.show(detailPanel, panelName);

        revalidate();
        repaint();
    }

    private void showAccountDetails(Account account) {
        String panelName = "details_" + account.serviceName;

        JPanel detailsPanel = createDetailsPanel(account);
        detailPanel.add(detailsPanel, panelName);

        detailPanel.setVisible(true);
        detailCardLayout.show(detailPanel, panelName);

        revalidate();
        repaint();
    }

    private JPanel createDetailsPanel(Account account) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(LIGHT_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 25, 15, 25));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(LIGHT_GRAY);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JButton closeButton = new JButton("✕");
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> {
            detailPanel.setVisible(false);
            revalidate();
            repaint();
        });
        headerPanel.add(closeButton, BorderLayout.EAST);

        panel.add(headerPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel serviceIcon = new JLabel();
        serviceIcon.setPreferredSize(new Dimension(80, 80));
        serviceIcon.setMaximumSize(new Dimension(80, 80));
        serviceIcon.setMinimumSize(new Dimension(80, 80));
        serviceIcon.setOpaque(true);
        serviceIcon.setBackground(getColorForService(account.serviceName));
        serviceIcon.setHorizontalAlignment(SwingConstants.CENTER);
        serviceIcon.setVerticalAlignment(SwingConstants.CENTER);
        serviceIcon.setFont(new Font("Arial", Font.BOLD, 40));
        serviceIcon.setText(account.serviceName.substring(0, 1));
        serviceIcon.setForeground(Color.WHITE);
        serviceIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(serviceIcon);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        JLabel serviceName = createEditableLabel(account.serviceName, 22, true);
        serviceName.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(serviceName);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        panel.add(createDetailField("Login", account.login));
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        panel.add(createDetailField("Email", account.email));
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel strengthPanel = createStrengthBar(0);
        strengthPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String initialPassword = account.email.isEmpty() ? "" : "motdepassedejean";
        panel.add(createDetailPasswordFieldWithStrengthUpdate("Mot de passe", initialPassword, strengthPanel));
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        panel.add(strengthPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        String urlValue = account.email.isEmpty() ? "" : "https://www.facebook.com/?locale=fr_FR";
        panel.add(createDetailField("URL", urlValue));
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        panel.add(createCategoryField("Catégorie", account.category));
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        panel.add(Box.createVerticalGlue());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(LIGHT_GRAY);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JButton saveButton = createRoundedButton("Sauvegarder", new Color(40, 167, 69));
        saveButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Modifications sauvegardées!");
        });

        JButton cancelButton = createRoundedButton("Annuler", new Color(220, 53, 69));
        cancelButton.addActionListener(e -> {
            detailPanel.setVisible(false);
            revalidate();
            repaint();
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        return panel;
    }

    private JLabel createEditableLabel(String text, int fontSize, boolean bold) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", bold ? Font.BOLD : Font.PLAIN, fontSize));
        label.setCursor(new Cursor(Cursor.TEXT_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Container parent = label.getParent();
                int index = getComponentIndex(parent, label);

                JTextField textField = new JTextField(label.getText());
                textField.setFont(label.getFont());
                textField.setHorizontalAlignment(JTextField.CENTER);
                textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
                textField.setAlignmentX(Component.CENTER_ALIGNMENT);

                parent.remove(label);
                parent.add(textField, index);
                parent.revalidate();
                parent.repaint();

                textField.requestFocus();
                textField.selectAll();

                textField.addActionListener(ae -> {
                    label.setText(textField.getText());
                    parent.remove(textField);
                    parent.add(label, index);
                    parent.revalidate();
                    parent.repaint();
                });

                textField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent ke) {
                        if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            parent.remove(textField);
                            parent.add(label, index);
                            parent.revalidate();
                            parent.repaint();
                        }
                    }
                });

                textField.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent fe) {
                        label.setText(textField.getText());
                        parent.remove(textField);
                        parent.add(label, index);
                        parent.revalidate();
                        parent.repaint();
                    }
                });
            }
        });

        return label;
    }

    private int getComponentIndex(Container parent, Component component) {
        Component[] components = parent.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] == component) {
                return i;
            }
        }
        return -1;
    }

    private JPanel createCategoryField(String label, String selectedCategory) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(LIGHT_GRAY);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        fieldLabel.setForeground(Color.GRAY);
        fieldLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] categories = {"Réseaux Sociaux", "Banking", "Email", "Shopping", "Travail", "Autre"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        categoryCombo.setSelectedItem(selectedCategory);
        categoryCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        categoryCombo.setBackground(Color.WHITE);
        categoryCombo.setFont(new Font("Arial", Font.PLAIN, 13));

        panel.add(fieldLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(categoryCombo);

        return panel;
    }

    private JPanel createDetailField(String label, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(LIGHT_GRAY);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        fieldLabel.setForeground(Color.GRAY);
        fieldLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField textField = new JTextField(value);
        textField.setFont(new Font("Arial", Font.PLAIN, 13));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        panel.add(fieldLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(textField);

        return panel;
    }

    private JPanel createDetailPasswordFieldWithStrengthUpdate(String label, String initialPassword, JPanel strengthBarPanel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(LIGHT_GRAY);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        fieldLabel.setForeground(Color.GRAY);
        fieldLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        JPasswordField passwordField = new JPasswordField(initialPassword);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 13));
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createEmptyBorder());

        // Calculer la force initiale
        SwingUtilities.invokeLater(() -> {
            String pwd = new String(passwordField.getPassword());
            if (!pwd.isEmpty()) {
                PasswordStrength strength = new PasswordStrength(pwd);
                updateStrengthBarDisplay(strengthBarPanel, strength.getLevel());
            }
        });

        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateStrength();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateStrength();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateStrength();
            }

            private void updateStrength() {
                String pwd = new String(passwordField.getPassword());
                if (pwd.isEmpty()) {
                    updateStrengthBarDisplay(strengthBarPanel, 0);
                } else {
                    PasswordStrength strength = new PasswordStrength(pwd);
                    updateStrengthBarDisplay(strengthBarPanel, strength.getLevel());
                }
            }
        });

        passwordPanel.add(passwordField, BorderLayout.CENTER);

        JButton eyeButton = new JButton();
        eyeButton.setBorder(BorderFactory.createEmptyBorder());
        eyeButton.setBackground(Color.WHITE);
        eyeButton.setFocusPainted(false);
        eyeButton.setContentAreaFilled(false);
        eyeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Charger les images
        ImageIcon eyeOpenIcon = loadIcon(EYE_OPEN_PATH, 20, 20);
        ImageIcon eyeClosedIcon = loadIcon(EYE_CLOSED_PATH, 20, 20);

        // Utiliser directement les images (par défaut, mot de passe masqué = icône œil fermé)
        eyeButton.setIcon(eyeClosedIcon);

        eyeButton.addActionListener(e -> {
            if (passwordField.getEchoChar() == (char) 0) {
                // Masquer le mot de passe
                passwordField.setEchoChar('•');
                eyeButton.setIcon(eyeClosedIcon);
            } else {
                // Afficher le mot de passe
                passwordField.setEchoChar((char) 0);
                eyeButton.setIcon(eyeOpenIcon);
            }
        });
        passwordPanel.add(eyeButton, BorderLayout.EAST);

        panel.add(fieldLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(passwordPanel);

        return panel;
    }

    private void updateStrengthBarDisplay(JPanel strengthBarPanel, int level) {
        Component[] components = strengthBarPanel.getComponents();

        Color[] colors = {
                Color.LIGHT_GRAY,
                new Color(220, 53, 69),   // Rouge
                new Color(253, 126, 20),  // Orange
                new Color(255, 193, 7),   // Jaune
                new Color(40, 167, 69),   // Vert
                new Color(25, 135, 84)    // Vert foncé
        };

        level = Math.max(0, Math.min(5, level));
        Color levelColor = (level > 0) ? colors[level] : Color.LIGHT_GRAY;

        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof JLabel) {
                JLabel bar = (JLabel) components[i];
                bar.setBackground(i < level ? levelColor : Color.LIGHT_GRAY);
            }
        }

        strengthBarPanel.revalidate();
        strengthBarPanel.repaint();
    }

    private JPanel createStrengthBar(int strength) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
        panel.setBackground(LIGHT_GRAY);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        for (int i = 0; i < 5; i++) {
            JLabel bar = new JLabel("     ");
            bar.setOpaque(true);
            bar.setBackground(Color.LIGHT_GRAY);
            bar.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            panel.add(bar);
        }

        return panel;
    }

    private JButton createRoundedButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(110, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private Color getColorForService(String serviceName) {
        switch (serviceName.toLowerCase()) {
            case "instagram": return new Color(225, 48, 108);
            case "facebook": return new Color(66, 103, 178);
            case "gmail": return new Color(234, 67, 53);
            case "twitter": return new Color(29, 161, 242);
            case "linkedin": return new Color(0, 119, 181);
            default: return PURPLE_BG;
        }
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            System.err.println("Impossible de charger l'image: " + path);
            return null;
        }
    }

    private static class Account {
        String serviceName;
        String email;
        String login;
        String maskedPassword;
        String realPassword;
        String iconPath;
        String category;
        int strength;

        public Account(String serviceName, String email, String login, String maskedPassword,
                       String iconPath, String category, int strength) {
            this.serviceName = serviceName;
            this.email = email;
            this.login = login;
            this.maskedPassword = maskedPassword;
            this.iconPath = iconPath;
            this.category = category;
            this.strength = strength;
        }

        public Account(String serviceName, String email, String login, String maskedPassword,
                       String realPassword, String iconPath, String category, int strength) {
            this.serviceName = serviceName;
            this.email = email;
            this.login = login;
            this.maskedPassword = maskedPassword;
            this.realPassword = realPassword;
            this.iconPath = iconPath;
            this.category = category;
            this.strength = strength;
        }
    }
}