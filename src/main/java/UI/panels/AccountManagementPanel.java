package UI.panels;

import Entities.UserProfile;
import Managers.Interface.SessionListener;
import Managers.ServiceManager;
import Managers.SessionManager;
import Services.UserService;
import org.hibernate.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.Provider;
import java.util.Arrays;

public class AccountManagementPanel extends JPanel implements SessionListener {

    private static final Color PURPLE_BG = new Color(88, 70, 150);
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);
    private static final Color BUTTON_COLOR = new Color(88, 70, 150);

    // Chemins des images
    private static final String EYE_OPEN_PATH = "/images/eye-open.png";
    private static final String EYE_CLOSED_PATH = "/images/eye-closed.png";

    private JTextField usernameField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel errorLabel;

    public AccountManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Panel principal avec scroll
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Titre
        JLabel titleLabel = new JLabel("Mon Profil");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Section Informations du compte
        JPanel accountSection = createAccountSection();
        accountSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(accountSection);

        // Ajouter du glue pour pousser le contenu vers le haut
        mainPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        SessionManager.addListener(this);
    }

    private JPanel createAccountSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));

        // Titre de section
        JLabel sectionTitle = new JLabel("Informations du compte");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 20));
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sectionTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Champ Identifiant
        JLabel usernameLabel = new JLabel("Identifiant");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(usernameLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        usernameField.setBackground(LIGHT_GRAY);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(usernameField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Champ Mot de passe actuel
        JLabel currentPasswordLabel = new JLabel("Mot de passe actuel");
        currentPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        currentPasswordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(currentPasswordLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        JPanel currentPasswordPanel = createPasswordPanel(false, false);
        currentPasswordPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(currentPasswordPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Champ Nouveau mot de passe
        JLabel newPasswordLabel = new JLabel("Nouveau mot de passe");
        newPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        newPasswordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(newPasswordLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        JPanel newPasswordPanel = createPasswordPanel(true, false);
        newPasswordPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(newPasswordPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Champ Confirmer le nouveau mot de passe
        JLabel confirmPasswordLabel = new JLabel("Confirmer le nouveau mot de passe");
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmPasswordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(confirmPasswordLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        JPanel confirmPasswordPanel = createPasswordPanel(false, true);
        confirmPasswordPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(confirmPasswordPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Label d'erreur
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        errorLabel.setForeground(new Color(244, 67, 54));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(errorLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Bouton Sauvegarder
        JButton saveButton = createStyledButton("Sauvegarder les modifications", BUTTON_COLOR);
        saveButton.setMaximumSize(new Dimension(300, 45));
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveButton.addActionListener(e -> handleSaveAccount());
        panel.add(saveButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Bouton Supprimer le compte
        JButton deleteAccountButton = createStyledButton("Supprimer mon compte", new Color(220, 53, 69));
        deleteAccountButton.setMaximumSize(new Dimension(300, 45));
        deleteAccountButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        deleteAccountButton.addActionListener(e -> handleDeleteAccount());
        panel.add(deleteAccountButton);

        return panel;
    }

    private JPanel createPasswordPanel(boolean isNewPassword, boolean isConfirmPassword) {
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordPanel.setBackground(LIGHT_GRAY);
        passwordPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JPasswordField field;
        if (isNewPassword) {
            newPasswordField = new JPasswordField();
            field = newPasswordField;
        } else if (isConfirmPassword) {
            confirmPasswordField = new JPasswordField();
            field = confirmPasswordField;
        } else {
            currentPasswordField = new JPasswordField();
            field = currentPasswordField;
        }

        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(LIGHT_GRAY);
        field.setBorder(BorderFactory.createEmptyBorder());
        passwordPanel.add(field, BorderLayout.CENTER);

        // Bouton œil
        JButton eyeButton = createEyeButton(field);
        passwordPanel.add(eyeButton, BorderLayout.EAST);

        return passwordPanel;
    }

    private JButton createEyeButton(JPasswordField targetField) {
        JButton button = new JButton();
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setBackground(LIGHT_GRAY);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ImageIcon eyeOpenIcon = loadIcon(EYE_OPEN_PATH, 20, 20);
        ImageIcon eyeClosedIcon = loadIcon(EYE_CLOSED_PATH, 20, 20);

        button.setIcon(eyeClosedIcon);

        button.addActionListener(e -> {
            if (targetField.getEchoChar() == (char) 0) {
                targetField.setEchoChar('•');
                button.setIcon(eyeClosedIcon);
            } else {
                targetField.setEchoChar((char) 0);
                button.setIcon(eyeOpenIcon);
            }
        });

        return button;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.brighter());
                } else {
                    g2d.setColor(bgColor);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2d.dispose();

                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadUserData() {

        usernameField.setText(SessionManager.getCurrentUser().getUsername());
    }

    private void handleSaveAccount() {
        String username = usernameField.getText();
        char[] currentPassword = currentPasswordField.getPassword();
        char[] newPassword = newPasswordField.getPassword();
        char[] confirmPassword = confirmPasswordField.getPassword();

        // Réinitialiser le message d'erreur
        errorLabel.setText(" ");
        errorLabel.setForeground(new Color(244, 67, 54));

        // Validation
        if (username.isEmpty()) {
            errorLabel.setText("L'identifiant ne peut pas être vide");
            return;
        }

        if (currentPassword.length == 0) {
            errorLabel.setText("Veuillez entrer votre mot de passe actuel");
            return;
        }

        if (newPassword.length == 0) {
            errorLabel.setText("Veuillez entrer un nouveau mot de passe");
            return;
        }

        if (!Arrays.equals(confirmPassword, newPassword)) {
            errorLabel.setText("Les mots de passe ne correspondent pas");
            return;
        }


        UserService userService = ServiceManager.getUserService();

        UserProfile current = SessionManager.getCurrentUser();

        try {
            UserProfile updatedProfile = userService.updateProfile(
                    current,
                    username,
                    newPassword
            );

            SessionManager.updateCurrentProfile(updatedProfile);
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
            return;
        }


        // Message de succès
        errorLabel.setForeground(new Color(76, 209, 55));
        errorLabel.setText("Modifications sauvegardées avec succès !");

        // Réinitialiser les champs de mot de passe
        currentPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    private void handleDeleteAccount() {
        // Créer un panel de confirmation personnalisé
        JPanel confirmPanel = new JPanel();
        confirmPanel.setLayout(new BoxLayout(confirmPanel, BoxLayout.Y_AXIS));

        JLabel warningLabel = new JLabel("⚠️ ATTENTION ⚠️");
        warningLabel.setFont(new Font("Arial", Font.BOLD, 16));
        warningLabel.setForeground(new Color(220, 53, 69));
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel messageLabel1 = new JLabel("Cette action est irréversible !");
        messageLabel1.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel messageLabel2 = new JLabel("Tous vos mots de passe seront définitivement supprimés.");
        messageLabel2.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel confirmLabel = new JLabel("Tapez votre mot de passe pour confirmer :");
        confirmLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        confirmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPasswordField confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setMaximumSize(new Dimension(250, 30));
        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        confirmPanel.add(warningLabel);
        confirmPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        confirmPanel.add(messageLabel1);
        confirmPanel.add(messageLabel2);
        confirmPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        confirmPanel.add(confirmLabel);
        confirmPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        confirmPanel.add(confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(
                this,
                confirmPanel,
                "Supprimer le compte",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            char[] password = this.currentPasswordField.getPassword();


            try {
                ServiceManager.getUserService().removeAccount(password);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Une erreur est survenue", JOptionPane.ERROR_MESSAGE);
                return;
            }


            // Afficher un message de confirmation
            JOptionPane.showMessageDialog(
                    this,
                    "Votre compte a été supprimé.\nVous allez être déconnecté.",
                    "Compte supprimé",
                    JOptionPane.INFORMATION_MESSAGE
            );

            SessionManager.disconnect();
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

    @Override
    public void onLogin() {
        // Charger les données
        loadUserData();
    }

    @Override
    public void onDisconnect() {

    }
}