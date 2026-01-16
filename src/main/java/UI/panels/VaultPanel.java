package UI.panels;

import Entities.Profile;
import Entities.Category;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.Provider;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import Managers.Interface.SessionListener;

import Managers.ServiceManager;
import Managers.SessionManager;
import Utilities.Security.Password.*;

/**
 * Panel principal de gestion du coffre-fort de mots de passe.
 * Permet de créer, modifier, supprimer et rechercher des profils.
 * Inclut des filtres (catégorie, date, nom) et un indicateur de force des mots de passe.
 *
 * @author ARCELON Louis, MARTEL Mathieu
 * @version v0.1
 */
public class VaultPanel extends JPanel implements SessionListener {

    private static final Color PURPLE_BG = new Color(88, 70, 150);
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);

    // Chemins des images pour le bouton oeil
    private static final String EYE_OPEN_PATH = "/images/eye-open.png";
    private static final String EYE_CLOSED_PATH = "/images/eye-closed.png";

    private JPanel accountListPanel;
    private JPanel detailPanel;
    private CardLayout detailCardLayout;

    private JComboBox<String> categoryFilter;
    private JComboBox<String> dateFilter;
    private JComboBox<String> nameFilter;
    private JTextField searchField;

    private List<Profile> profiles;  // Liste complète des profils
    private List<Category> categories;  // Liste des catégories depuis le backend

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
        detailPanel.setPreferredSize(new Dimension(400, 750));
        detailPanel.setVisible(false);

        JPanel emptyDetailPanel = new JPanel();
        emptyDetailPanel.setBackground(Color.WHITE);
        detailPanel.add(emptyDetailPanel, "empty");

        add(detailPanel, BorderLayout.EAST);

        SessionManager.addListener(this);

    }

    // Crée le panel principal avec les filtres et la liste des comptes
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

    // Crée le panel de filtrage (catégorie, date, nom, recherche)
    private JPanel createFilterPanel() {
        // Panel principal avec layout vertical
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        // Ligne 1 : Filtres (Catégorie, Date, Nom)
        JPanel filtersRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filtersRow.setBackground(Color.WHITE);

        JLabel categoryLabel = new JLabel("Catégorie");
        String[] categoryNames = getCategoryNames();
        categoryFilter = new JComboBox<>(categoryNames);
        categoryFilter.setPreferredSize(new Dimension(150, 30));
        categoryFilter.setBackground(LIGHT_GRAY);
        categoryFilter.addActionListener(this::onFilterChanged);

        JLabel dateLabel = new JLabel("Jusqu'à");
        dateFilter = new JComboBox<>(new String[]{"Aujourd'hui", "Cette semaine", "Ce mois", "Tous"});
        dateFilter.setPreferredSize(new Dimension(120, 30));
        dateFilter.setBackground(LIGHT_GRAY);
        dateFilter.addActionListener(this::onFilterChanged);

        JLabel nameLabel = new JLabel("Nom");
        nameFilter = new JComboBox<>(new String[]{"A-Z", "Z-A"});
        nameFilter.setPreferredSize(new Dimension(100, 30));
        nameFilter.setBackground(LIGHT_GRAY);
        nameFilter.addActionListener(this::onFilterChanged);

        filtersRow.add(categoryLabel);
        filtersRow.add(categoryFilter);
        filtersRow.add(dateLabel);
        filtersRow.add(dateFilter);
        filtersRow.add(nameLabel);
        filtersRow.add(nameFilter);

        // Ligne 2 : Barre de recherche
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchRow.setBackground(Color.WHITE);

        searchField = createPlaceholderTextField("Rechercher un compte...", 400, 35);
        searchField.addActionListener(this::onFilterChanged);

        searchRow.add(searchField);

        // Ajouter les deux lignes au panel principal
        mainPanel.add(filtersRow);
        mainPanel.add(searchRow);

        return mainPanel;
    }

    // Rafraîchit l'affichage quand un filtre change
    private void onFilterChanged(ActionEvent actionEvent) {
        displayAllProfiles();
    }

    /**
     * Crée un JTextField avec un placeholder
     */
    private JTextField createPlaceholderTextField(String placeholder, int width, int height) {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(width, height));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        textField.setFont(new Font("Arial", Font.PLAIN, 14));

        // Ajouter le placeholder
        textField.setForeground(Color.GRAY);
        textField.setText(placeholder);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });

        return textField;
    }

    /**
     * Charge les catégories depuis le backend
     */
    private void loadCategories() {
        try {
            categories = ServiceManager.getDataService().getCategories();

            String prevItem = (String) categoryFilter.getSelectedItem();

            categoryFilter.removeAllItems();
            categoryFilter.addItem("Toutes");

            for (Category cat : categories) {
                categoryFilter.addItem(cat.getName());
            }

            // Restaurer la sélection précédente si elle existe encore
            if (prevItem != null) {
                for (int i = 0; i < categoryFilter.getItemCount(); i++) {
                    if (categoryFilter.getItemAt(i).equals(prevItem)) {
                        categoryFilter.setSelectedItem(prevItem);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("ERREUR lors du chargement des catégories: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Impossible de charger les catégories: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Récupère les noms des catégories pour la liste déroulante
     */
    private String[] getCategoryNames() {

        if (categories == null || categories.isEmpty()) {
            return new String[]{"Toutes"};
        }

        String[] names = new String[categories.size() + 1];
        names[0] = "Toutes";
        for (int i = 0; i < categories.size(); i++) {
            names[i + 1] = categories.get(i).getName();
        }
        return names;
    }


    /**
     * Charge les profils depuis le backend
     */
    private void loadProfiles() {
        try {
            profiles = ServiceManager.getDataService().getProfiles();
        } catch (Exception e) {
            System.err.println("ERREUR lors du chargement des profils: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Impossible de charger les profils: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            profiles = new ArrayList<>(); // Liste vide pour éviter les NPE
        }
    }

    /**
     * Effectue la recherche et affiche les résultats
     */
    private List<Profile> performSearch(List<Profile> profilesToSearch) {
        String searchQuery = searchField.getText().trim();

        // Ignorer si c'est le placeholder
        if (searchQuery.isEmpty() || searchQuery.equals("Rechercher un compte...")) {
            return profilesToSearch;
        }

        List<Profile> filteredProfiles = new ArrayList<>();
        for (Profile profile : profilesToSearch) {
            // Recherche dans service, username, ou URL
            if (profile.getService().toLowerCase().contains(searchQuery.toLowerCase())) {
                filteredProfiles.add(profile);
            }
        }

        // retourne les profiles valides
        return filteredProfiles;
    }

    // Affiche tous les profils
    private void displayAllProfiles() {
        displayProfiles(profiles);
    }

    /**
     * Rafraîchit l'affichage avec une liste de profils donnée
     *
     * @param profilesToDisplay Liste des profils à afficher (tous ou filtrés)
     */
    private void displayProfiles(List<Profile> profilesToDisplay) {
        accountListPanel.removeAll();

        if (profilesToDisplay == null) {
            profilesToDisplay = new ArrayList<>();
        }

        LocalDateTime start;

        switch ((String) dateFilter.getSelectedItem()) {

            case "Aujourd'hui":
            {
                start = LocalDateTime.now().minusDays(1);
                break;
            }

            case "Cette semaine":
            {
                start = LocalDateTime.now().minusDays(7);
                break;
            }

            case "Ce mois": {
                start = LocalDateTime.now().minusDays(31);
                break;
            }

            default: {start = null; break;}

        }

        if (start != null) {
            profilesToDisplay = profilesToDisplay.stream()
                    .filter(profile -> profile.getCreationDate().isAfter(start))
                    .toList();
        }

        String selectedCategory = (String) categoryFilter.getSelectedItem();
        if (selectedCategory != null && !selectedCategory.equals("Toutes")) {
            profilesToDisplay = profilesToDisplay.stream()
                    .filter(profile -> {
                        // Si le profil n'a pas de catégorie, ne pas l'afficher
                        if (profile.getCategory() == null) return false;
                        // Sinon, vérifier si la catégorie correspond
                        return profile.getCategory().getName().equals(selectedCategory);
                    })
                    .toList();
        }
        // Si "Toutes" est sélectionné, on affiche TOUS les profils (pas de filtre)

        profilesToDisplay = profilesToDisplay.stream()
                .sorted(Comparator.comparing(Profile::getService, String.CASE_INSENSITIVE_ORDER))
                .toList();

        String nameOrdering = (String) nameFilter.getSelectedItem();

        if (nameOrdering.equals("Z-A")) {
            profilesToDisplay = profilesToDisplay.reversed();
        }

        profilesToDisplay = performSearch(profilesToDisplay);



        if (profilesToDisplay.isEmpty()) {
            // Message si aucun profil
            JLabel noResultLabel = new JLabel("Aucun compte trouvé");
            noResultLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            noResultLabel.setForeground(Color.GRAY);
            noResultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            accountListPanel.add(Box.createVerticalGlue());
            accountListPanel.add(noResultLabel);
            accountListPanel.add(Box.createVerticalGlue());
        } else {
            // Réutilisation de createAccountCard() pour chaque profil

            for (Profile profile : profilesToDisplay) {
                JPanel accountCard = createAccountCard(profile);
                accountListPanel.add(accountCard);
                accountListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        accountListPanel.revalidate();
        accountListPanel.repaint();
    }

    // Crée une card pour un profil (icône + infos + bouton oeil)
    private JPanel createAccountCard(Profile profile) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icône du service (toujours violet)
        JLabel iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(50, 50));
        iconLabel.setOpaque(true);
        iconLabel.setBackground(PURPLE_BG);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        iconLabel.setFont(new Font("Arial", Font.BOLD, 24));
        String iconText = profile.getService() != null && !profile.getService().isEmpty()
                ? profile.getService().substring(0, 1).toUpperCase()
                : "?";
        iconLabel.setText(iconText);
        iconLabel.setForeground(Color.WHITE);
        card.add(iconLabel, BorderLayout.WEST);

        // Panel d'informations
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        JLabel serviceLabel = new JLabel(profile.getService());
        serviceLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel emailLabel = new JLabel(profile.getUsername());
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        emailLabel.setForeground(Color.GRAY);

        infoPanel.add(serviceLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(emailLabel);
        card.add(infoPanel, BorderLayout.CENTER);

        // Panel des identifiants
        JPanel credentialsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        credentialsPanel.setBackground(Color.WHITE);

        JLabel loginLabel = new JLabel(profile.getUsername());
        loginLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel passwordLabel = new JLabel("......");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JButton eyeButton = new JButton();
        eyeButton.setBorder(BorderFactory.createEmptyBorder());
        eyeButton.setBackground(Color.WHITE);
        eyeButton.setFocusPainted(false);
        eyeButton.setContentAreaFilled(false);
        eyeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ImageIcon eyeOpenIcon = loadIcon(EYE_OPEN_PATH, 20, 20);
        ImageIcon eyeClosedIcon = loadIcon(EYE_CLOSED_PATH, 20, 20);

        eyeButton.setIcon(eyeClosedIcon);

        eyeButton.addActionListener(e -> {
            if (eyeButton.getIcon() == eyeClosedIcon) {
                String password = profile.getPassword();
                if (password != null) {
                    passwordLabel.setText(password);
                    eyeButton.setIcon(eyeOpenIcon);
                } else {
                    passwordLabel.setText("ERREUR");
                    JOptionPane.showMessageDialog(this,
                            "Impossible de décrypter le mot de passe",
                            "Erreur de décryptage",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                passwordLabel.setText("......");
                eyeButton.setIcon(eyeClosedIcon);
            }
        });

        credentialsPanel.add(loginLabel);
        credentialsPanel.add(passwordLabel);
        credentialsPanel.add(eyeButton);
        card.add(credentialsPanel, BorderLayout.EAST);

        // Listeners pour les interactions
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showAccountDetails(profile);
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

    // Crée le bouton "+" flottant en bas à droite
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

    // Affiche le panel de création d'un nouveau profil
    private void showNewAccountPanel() {
        String panelName = "new_profile";
        Profile newProfile = new Profile("", "", "", "");

        loadCategories();

        JPanel newAccountPanel = createDetailsPanel(newProfile, true);
        detailPanel.add(newAccountPanel, panelName);

        detailPanel.setVisible(true);
        detailCardLayout.show(detailPanel, panelName);

        revalidate();
        repaint();
    }

    // Affiche le panel de détails/modification d'un profil existant
    private void showAccountDetails(Profile profile) {
        String panelName = "details_" + profile.getId();

        loadCategories();

        JPanel detailsPanel = createDetailsPanel(profile, false);
        detailPanel.add(detailsPanel, panelName);

        detailPanel.setVisible(true);
        detailCardLayout.show(detailPanel, panelName);

        revalidate();
        repaint();
    }

    /**
     * Clone le profile avant de l'éditer pour permettre l'annulation
     */
    private Profile cloneProfile(Profile original) {
        Profile clone = new Profile(
                original.getService(),
                original.getUsername(),
                original.getPassword(), // getPassword() décrypte automatiquement
                original.getUrl()
        );
        clone.setEmail(original.getEmail());
        // Ne pas cloner l'ID, l'owner, la category car on ne veut pas créer un nouveau profil
        return clone;
    }

    // Crée le formulaire de création/modification d'un profil
    private JPanel createDetailsPanel(Profile profile, boolean isNew) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(LIGHT_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 25, 15, 25));

        // Header avec bouton fermer
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(LIGHT_GRAY);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JButton closeButton = new JButton("X");
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
        panel.add(Box.createRigidArea(new Dimension(0, 3)));

        // Icône du service
        JLabel serviceIcon = new JLabel();
        serviceIcon.setPreferredSize(new Dimension(70, 70));
        serviceIcon.setMaximumSize(new Dimension(70, 70));
        serviceIcon.setMinimumSize(new Dimension(70, 70));
        serviceIcon.setOpaque(true);
        serviceIcon.setBackground(PURPLE_BG);
        serviceIcon.setHorizontalAlignment(SwingConstants.CENTER);
        serviceIcon.setVerticalAlignment(SwingConstants.CENTER);
        serviceIcon.setFont(new Font("Arial", Font.BOLD, 36));
        String initialIconText = profile.getService() != null && !profile.getService().isEmpty()
                ? profile.getService().substring(0, 1).toUpperCase()
                : "?";
        serviceIcon.setText(initialIconText);
        serviceIcon.setForeground(Color.WHITE);
        serviceIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(serviceIcon);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Nom du service
        JTextField serviceNameField = new JTextField(profile.getService());
        serviceNameField.setFont(new Font("Arial", Font.BOLD, 20));
        serviceNameField.setHorizontalAlignment(JTextField.CENTER);
        serviceNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        serviceNameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        serviceNameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateIcon(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateIcon(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateIcon(); }

            private void updateIcon() {
                String text = serviceNameField.getText();
                if (!text.isEmpty()) {
                    serviceIcon.setText(text.substring(0, 1).toUpperCase());
                } else {
                    serviceIcon.setText("?");
                }
            }
        });

        panel.add(serviceNameField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Champ Login
        JTextField loginField = new JTextField(profile.getUsername());
        JPanel loginPanel = createDetailField("Login", loginField);
        panel.add(loginPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Champ Email
        JTextField emailField = new JTextField(profile.getEmail() != null ? profile.getEmail() : "");
        JPanel emailPanel = createDetailField("Email", emailField);
        panel.add(emailPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Barre de force du mot de passe
        JPanel strengthPanel = createStrengthBar(0);
        strengthPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String initialPassword = isNew ? "" : (profile.getPassword() != null ? profile.getPassword() : "");
        JPasswordField passwordField = new JPasswordField(initialPassword);
        JPanel passwordPanel = createDetailPasswordFieldWithStrengthUpdate("Mot de passe", passwordField, strengthPanel);
        panel.add(passwordPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));

        panel.add(strengthPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Champ URL
        String urlValue = profile.getUrl() != null ? profile.getUrl() : "";
        JTextField urlField = new JTextField(urlValue);
        JPanel urlPanel = createDetailField("URL", urlField);
        panel.add(urlPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Champ Catégorie
        String categoryName = profile.getCategory() != null ? profile.getCategory().getName() : "Autre";
        String[] categoryNames = getCategoryNames();
        String[] formCategoryNames = new String[categoryNames.length - 1];
        System.arraycopy(categoryNames, 1, formCategoryNames, 0, formCategoryNames.length);

        JComboBox<String> categoryCombo = new JComboBox<>(formCategoryNames);
        categoryCombo.setSelectedItem(categoryName);
        JPanel categoryPanel = createCategoryField("Catégorie", categoryCombo);
        panel.add(categoryPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Label d'erreur avec wrapping automatique
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        errorLabel.setForeground(new Color(244, 67, 54));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel.setVerticalAlignment(SwingConstants.TOP);
        errorLabel.setPreferredSize(new Dimension(340, 30));
        errorLabel.setMaximumSize(new Dimension(340, 30));
        panel.add(errorLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        buttonPanel.setBackground(LIGHT_GRAY);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JButton saveButton = createRoundedButton("Sauvegarder", new Color(40, 167, 69));
        saveButton.setPreferredSize(new Dimension(105, 32));
        saveButton.addActionListener(e -> {
            String newService = serviceNameField.getText().trim();
            String newLogin = loginField.getText().trim();
            String newEmail = emailField.getText().trim();
            String newPassword = new String(passwordField.getPassword());
            String newUrl = urlField.getText().trim();
            String newCategoryName = (String) categoryCombo.getSelectedItem();

            errorLabel.setText(" ");

            if (newService.isEmpty()) {
                errorLabel.setText("<html><center>Le nom du service ne peut pas être vide</center></html>");
                return;
            }

            if (newLogin.isEmpty()) {
                errorLabel.setText("<html><center>Le login ne peut pas être vide</center></html>");
                return;
            }

            if (newPassword.isEmpty()) {
                errorLabel.setText("<html><center>Le mot de passe ne peut pas être vide</center></html>");
                return;
            }

            try {
                // Récupérer l'objet Category correspondant
                Category selectedCategory = ServiceManager.getDataService().findCategoryByName(newCategoryName);

                if (isNew) {

                    Profile newProfile = ServiceManager.getDataService().createProfile(
                            newService,
                            newLogin,
                            newEmail,
                            newPassword,
                            newUrl
                    );

                    if (selectedCategory != null) {
                        ServiceManager.getDataService().attachProfileToCategory(newProfile, selectedCategory);
                        ServiceManager.getDataService().saveProfile(newProfile);
                    }

                } else {
                    ServiceManager.getDataService().attachProfileToCategory(profile, selectedCategory);

                    profile.setService(newService);
                    profile.setUsername(newLogin);
                    profile.setPassword(newPassword);
                    profile.setUrl(newUrl);
                    profile.setEmail(newEmail);

                    ServiceManager.getDataService().saveProfile(profile);
                }

                // Recharger et afficher
                loadProfiles();
                displayAllProfiles();

                // Fermer le panel
                detailPanel.setVisible(false);
                revalidate();
                repaint();

            } catch (Exception ex) {
                System.err.println("ERREUR lors de la sauvegarde: " + ex.getMessage());
                ex.printStackTrace();
                errorLabel.setText("<html><center>Erreur: " + ex.getMessage() + "</center></html>");
            }
        });
        buttonPanel.add(saveButton);

        if (!isNew) {
            JButton deleteButton = createRoundedButton("Supprimer", new Color(220, 53, 69));
            deleteButton.setPreferredSize(new Dimension(105, 32));
            deleteButton.addActionListener(e -> handleDeleteProfile(profile));
            buttonPanel.add(deleteButton);
        }

        JButton cancelButton = createRoundedButton("Annuler", new Color(108, 117, 125));
        cancelButton.setPreferredSize(new Dimension(105, 32));
        cancelButton.addActionListener(e -> {
            detailPanel.setVisible(false);
            revalidate();
            repaint();
        });
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel);

        return panel;
    }

    /**
     * Supprime un profil après confirmation.
     *
     * @param profile Le profil à supprimer
     */
    private void handleDeleteProfile(Profile profile) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Etes-vous sur de vouloir supprimer le compte \"" + profile.getService() + "\" ?\n" +
                        "Cette action est irreversible.",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ServiceManager.getDataService().removeProfile(profile);

                loadProfiles();
                displayAllProfiles();

                detailPanel.setVisible(false);
                revalidate();
                repaint();
            } catch (Exception e) {
                System.err.println("ERREUR lors de la suppression: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Impossible de supprimer le profil: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Crée un champ de catégorie avec dropdown
    private JPanel createCategoryField(String label, JComboBox<String> categoryCombo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(LIGHT_GRAY);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        fieldLabel.setForeground(Color.GRAY);
        fieldLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        categoryCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        categoryCombo.setBackground(Color.WHITE);
        categoryCombo.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(fieldLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(categoryCombo);

        return panel;
    }

    // Crée un champ texte avec label
    private JPanel createDetailField(String label, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(LIGHT_GRAY);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        fieldLabel.setForeground(Color.GRAY);
        fieldLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        textField.setFont(new Font("Arial", Font.PLAIN, 12));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        panel.add(fieldLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(textField);

        return panel;
    }

    // Crée un champ mot de passe avec oeil et barre de force
    private JPanel createDetailPasswordFieldWithStrengthUpdate(String label, JPasswordField passwordField, JPanel strengthBarPanel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(LIGHT_GRAY);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        fieldLabel.setForeground(Color.GRAY);
        fieldLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        passwordField.setFont(new Font("Arial", Font.PLAIN, 12));
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createEmptyBorder());

        SwingUtilities.invokeLater(() -> {
            String pwd = new String(passwordField.getPassword());
            if (!pwd.isEmpty()) {
                try {
                    PasswordStrength strength = new PasswordStrength(pwd);
                    updateStrengthBarDisplay(strengthBarPanel, strength.getLevel());
                } catch (Exception e) {
                    System.err.println("Erreur calcul force mot de passe: " + e.getMessage());
                }
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
                    try {
                        PasswordStrength strength = new PasswordStrength(pwd);
                        updateStrengthBarDisplay(strengthBarPanel, strength.getLevel());
                    } catch (Exception e) {
                        System.err.println("Erreur calcul force mot de passe: " + e.getMessage());
                        updateStrengthBarDisplay(strengthBarPanel, 0);
                    }
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

        ImageIcon eyeOpenIcon = loadIcon(EYE_OPEN_PATH, 18, 18);
        ImageIcon eyeClosedIcon = loadIcon(EYE_CLOSED_PATH, 18, 18);

        eyeButton.setIcon(eyeClosedIcon);

        eyeButton.addActionListener(e -> {
            if (passwordField.getEchoChar() == (char) 0) {
                passwordField.setEchoChar('\u2022');
                eyeButton.setIcon(eyeClosedIcon);
            } else {
                passwordField.setEchoChar((char) 0);
                eyeButton.setIcon(eyeOpenIcon);
            }
        });
        passwordPanel.add(eyeButton, BorderLayout.EAST);

        panel.add(fieldLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(passwordPanel);

        return panel;
    }

    // Met à jour l'affichage de la barre de force
    private void updateStrengthBarDisplay(JPanel strengthBarPanel, int level) {
        Component[] components = strengthBarPanel.getComponents();

        Color[] colors = {
                Color.LIGHT_GRAY,
                new Color(220, 53, 69),
                new Color(253, 126, 20),
                new Color(255, 193, 7),
                new Color(40, 167, 69),
                new Color(25, 135, 84)
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

    // Crée la barre de force vide
    private JPanel createStrengthBar(int strength) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        panel.setBackground(LIGHT_GRAY);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));

        for (int i = 0; i < 5; i++) {
            JLabel bar = new JLabel("    ");
            bar.setOpaque(true);
            bar.setBackground(Color.LIGHT_GRAY);
            bar.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            panel.add(bar);
        }

        return panel;
    }

    // Crée un bouton avec coins arrondis et couleur personnalisable
    private JButton createRoundedButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(105, 32));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    // Charge et redimensionne une icône
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

    @Override
    public void onLogin() {
        try {
            loadProfiles();
            loadCategories();
            displayAllProfiles();
        } catch (Exception e) {
            System.err.println("ERREUR lors du chargement initial: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des données: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onDisconnect() {
        // Clear les données à la déconnexion
        profiles = new ArrayList<>();
        categories = new ArrayList<>();
        displayAllProfiles();
        detailPanel.setVisible(false);
    }


}