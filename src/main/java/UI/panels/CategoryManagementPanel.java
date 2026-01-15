package UI.panels;

import Entities.Category;
import Entities.UserProfile;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CategoryManagementPanel extends JPanel {

    private static final Color PURPLE_BG = new Color(88, 70, 150);
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);

    private List<Category> categories;  // Utilisation directe de l'entité Category du backend
    private JPanel categoriesListPanel;
    private JPanel categoryDetailPanel;
    private CardLayout categoryDetailCardLayout;

    public CategoryManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Panel principal avec scroll
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Titre
        JLabel titleLabel = new JLabel("Gestion des catégories");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Description
        JLabel descriptionLabel = new JLabel("Gérez les catégories utilisées pour organiser vos mots de passe");
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionLabel.setForeground(Color.GRAY);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(descriptionLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Bouton d'ajout
        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        addButtonPanel.setBackground(Color.WHITE);
        addButtonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        addButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton addCategoryButton = createAddButton("+");
        addCategoryButton.addActionListener(e -> showCategoryDetailPanel(null));
        addButtonPanel.add(addCategoryButton);

        mainPanel.add(addButtonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Liste des catégories
        categoriesListPanel = new JPanel();
        categoriesListPanel.setLayout(new BoxLayout(categoriesListPanel, BoxLayout.Y_AXIS));
        categoriesListPanel.setBackground(Color.WHITE);
        categoriesListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        categoriesListPanel.setMaximumSize(new Dimension(700, Integer.MAX_VALUE));

        mainPanel.add(categoriesListPanel);

        // Ajouter du glue pour pousser le contenu vers le haut
        mainPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Panel de détails des catégories (à droite, caché par défaut)
        categoryDetailPanel = new JPanel();
        categoryDetailCardLayout = new CardLayout();
        categoryDetailPanel.setLayout(categoryDetailCardLayout);
        categoryDetailPanel.setPreferredSize(new Dimension(400, 600));
        categoryDetailPanel.setVisible(false);

        JPanel emptyCategoryDetailPanel = new JPanel();
        emptyCategoryDetailPanel.setBackground(Color.WHITE);
        categoryDetailPanel.add(emptyCategoryDetailPanel, "empty");

        add(categoryDetailPanel, BorderLayout.EAST);

        // Charger les données
        loadCategories();
    }

    /**
     * Charge les catégories depuis le backend
     * TODO: Appeler le service backend pour récupérer Set<Category>
     */
    private void loadCategories() {
        // TODO: Remplacer par l'appel backend
        // Temporaire
        categories = new ArrayList<>();
        Category c1 = new Category("Réseaux Sociaux", "Comptes Facebook, Instagram, Twitter, etc.");
        Category c2 = new Category("Autre", "Catégorie par défaut pour les comptes non classés");

        categories.add(c1);
        categories.add(c2);

        displayCategories();
    }

    private void displayCategories() {
        categoriesListPanel.removeAll();

        for (Category category : categories) {
            JPanel categoryCard = createCategoryCard(category);
            categoriesListPanel.add(categoryCard);
            categoriesListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        categoriesListPanel.revalidate();
        categoriesListPanel.repaint();
    }

    private JPanel createCategoryCard(Category category) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Icône de catégorie (toujours violet)
        JLabel iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(50, 50));
        iconLabel.setOpaque(true);
        iconLabel.setBackground(PURPLE_BG);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        iconLabel.setFont(new Font("Arial", Font.BOLD, 20));
        iconLabel.setText(category.getName().substring(0, 1));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.add(iconLabel, BorderLayout.WEST);

        // Panel central avec nom et description
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel nameLabel = new JLabel(category.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel descriptionLabel = new JLabel(category.getDesc());
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descriptionLabel.setForeground(Color.GRAY);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        infoPanel.add(descriptionLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        // Panel des boutons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);

        // Bouton Supprimer
        JButton deleteButton = createTextButton("Supprimer");
        deleteButton.setForeground(new Color(220, 53, 69));
        deleteButton.addActionListener(e -> handleDeleteCategory(category));
        buttonsPanel.add(deleteButton);

        card.add(buttonsPanel, BorderLayout.EAST);

        // MouseListener pour l'icône et le panel info
        MouseAdapter clickListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showCategoryDetailPanel(category);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(250, 250, 250));
                infoPanel.setBackground(new Color(250, 250, 250));
                buttonsPanel.setBackground(new Color(250, 250, 250));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                infoPanel.setBackground(Color.WHITE);
                buttonsPanel.setBackground(Color.WHITE);
            }
        };

        // Ajouter le listener sur l'icône et le panel info uniquement
        iconLabel.addMouseListener(clickListener);
        infoPanel.addMouseListener(clickListener);

        return card;
    }

    private void showCategoryDetailPanel(Category category) {
        boolean isNewCategory = (category == null);
        Category workingCategory = isNewCategory ? new Category("", "") : category;

        String panelName = isNewCategory ? "new_category" : "edit_category_" + category.getId();

        JPanel detailsPanel = createCategoryDetailPanel(workingCategory, isNewCategory);
        categoryDetailPanel.add(detailsPanel, panelName);

        categoryDetailPanel.setVisible(true);
        categoryDetailCardLayout.show(categoryDetailPanel, panelName);

        revalidate();
        repaint();
    }

    private JPanel createCategoryDetailPanel(Category category, boolean isNewCategory) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(LIGHT_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 25, 15, 25));

        // Header avec bouton fermer
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
            categoryDetailPanel.setVisible(false);
            revalidate();
            repaint();
        });
        headerPanel.add(closeButton, BorderLayout.EAST);

        panel.add(headerPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Icône de la catégorie (toujours violet)
        JLabel categoryIcon = new JLabel();
        categoryIcon.setPreferredSize(new Dimension(80, 80));
        categoryIcon.setMaximumSize(new Dimension(80, 80));
        categoryIcon.setMinimumSize(new Dimension(80, 80));
        categoryIcon.setOpaque(true);
        categoryIcon.setBackground(PURPLE_BG);
        categoryIcon.setHorizontalAlignment(SwingConstants.CENTER);
        categoryIcon.setVerticalAlignment(SwingConstants.CENTER);
        categoryIcon.setFont(new Font("Arial", Font.BOLD, 40));
        categoryIcon.setText(isNewCategory ? "?" : category.getName().substring(0, 1));
        categoryIcon.setForeground(Color.WHITE);
        categoryIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(categoryIcon);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Titre
        JLabel titleLabel = new JLabel(isNewCategory ? "Nouvelle catégorie" : "Modifier la catégorie");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Champ Nom
        JLabel nameLabel = new JLabel("Nom");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        nameLabel.setForeground(Color.GRAY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(nameLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        JTextField nameField = new JTextField(category.getName());
        nameField.setFont(new Font("Arial", Font.PLAIN, 13));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        nameField.setBackground(Color.WHITE);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        panel.add(nameField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Mettre à jour l'icône quand le nom change
        nameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateIcon(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateIcon(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateIcon(); }

            private void updateIcon() {
                String text = nameField.getText();
                if (!text.isEmpty()) {
                    categoryIcon.setText(text.substring(0, 1).toUpperCase());
                } else {
                    categoryIcon.setText("?");
                }
            }
        });

        // Champ Description
        JLabel descLabel = new JLabel("Description");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(descLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        JTextArea descArea = new JTextArea(category.getDesc());
        descArea.setFont(new Font("Arial", Font.PLAIN, 13));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setRows(3);
        descArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        JScrollPane descScrollPane = new JScrollPane(descArea);
        descScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        descScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(descScrollPane);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Label d'erreur
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(244, 67, 54));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(errorLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(Box.createVerticalGlue());

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(LIGHT_GRAY);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JButton saveButton = createRoundedButton("Sauvegarder", new Color(40, 167, 69));
        saveButton.addActionListener(e -> {
            String newName = nameField.getText().trim();
            String newDesc = descArea.getText().trim();

            errorLabel.setText(" ");

            if (newName.isEmpty()) {
                errorLabel.setText("Le nom ne peut pas être vide");
                return;
            }

            // Vérifier les doublons
            for (Category cat : categories) {
                if (cat != category && cat.getName().equals(newName)) {
                    errorLabel.setText("Cette catégorie existe déjà");
                    return;
                }
            }

            if (isNewCategory) {
                // TODO: Ajouter via backend
                System.out.println("Ajout catégorie: " + newName);
                Category newCategory = new Category(newName, newDesc);
                categories.add(newCategory);
            } else {
                // TODO: Modifier via backend
                System.out.println("Modification catégorie: " + category.getName() + " -> " + newName);
                category.setName(newName);
                category.setDesc(newDesc);
            }

            displayCategories();
            categoryDetailPanel.setVisible(false);
            revalidate();
            repaint();
        });

        JButton cancelButton = createRoundedButton("Annuler", new Color(220, 53, 69));
        cancelButton.addActionListener(e -> {
            categoryDetailPanel.setVisible(false);
            revalidate();
            repaint();
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        return panel;
    }

    private void handleDeleteCategory(Category category) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer la catégorie \"" + category.getName() + "\" ?\n" +
                        "Les comptes associés seront déplacés vers \"Autre\".",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: Supprimer la catégorie via le backend
            // categoryService.delete(category.getId());
            System.out.println("Suppression de la catégorie: " + category.getName());

            categories.remove(category);
            displayCategories();
        }
    }

    private JButton createAddButton(String text) {
        JButton button = new JButton(text) {
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

        button.setPreferredSize(new Dimension(50, 50));
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText("Ajouter une nouvelle catégorie");

        return button;
    }

    private JButton createTextButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 13));
        button.setForeground(PURPLE_BG);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setFont(new Font("Arial", Font.BOLD, 13));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setFont(new Font("Arial", Font.PLAIN, 13));
            }
        });

        return button;
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
}