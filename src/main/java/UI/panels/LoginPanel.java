package UI.panels;

import Managers.ServiceManager;
import Managers.SessionManager;
import UI.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Classe permettant d'afficher le formulaire de login dans l'interface. Référée dans mainFrame par "login".
 */
public class LoginPanel extends JPanel {

    private static final Color PURPLE_BG = new Color(88, 70, 150);
    private static final Color BUTTON_COLOR = new Color(88, 70, 150);
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private MainFrame mainFrame;

    // Chemins des images
    private static final String ILLUSTRATION_PATH = "/images/logo.png";
    private static final String EYE_OPEN_PATH = "/images/eye-open.png";
    private static final String EYE_CLOSED_PATH = "/images/eye-closed.png";

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());

        // Panel gauche avec illustration
        JPanel leftPanel = createLeftPanel();

        // Panel droit avec formulaire
        JPanel rightPanel = createRightPanel();

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        errorLabel.setText(" ");
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond violet
                g2d.setColor(PURPLE_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                try {
                    ImageIcon icon = new ImageIcon(getClass().getResource(ILLUSTRATION_PATH));
                    Image img = icon.getImage();

                    // Centrer l'image
                    int imgWidth = img.getWidth(null);
                    int imgHeight = img.getHeight(null);
                    int x = (getWidth() - imgWidth) / 2;
                    int y = (getHeight() - imgHeight) / 2;

                    g2d.drawImage(img, x, y, this);
                } catch (Exception e) {
                    // Si l'image n'existe pas, afficher un texte par défaut
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, 16));
                    String text = "🔒 Security Illustration";
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = getHeight() / 2;
                    g2d.drawString(text, x, y);

                    g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                    String note = "(Image: " + ILLUSTRATION_PATH + ")";
                    x = (getWidth() - g2d.getFontMetrics().stringWidth(note)) / 2;
                    g2d.drawString(note, x, y + 30);
                }
            }
        };
        panel.setPreferredSize(new Dimension(550, 600));
        panel.setBackground(PURPLE_BG);
        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 40, 10, 40);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titre "LOGIN"
        JLabel titleLabel = new JLabel("LOGIN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(40, 40, 40, 40);
        panel.add(titleLabel, gbc);

        // Label "Identifiant"
        JLabel usernameLabel = new JLabel("Identifiant");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 40, 5, 40);
        panel.add(usernameLabel, gbc);

        // Champ Identifiant
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(300, 35));
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBackground(LIGHT_GRAY);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 40, 20, 40);
        panel.add(usernameField, gbc);

        // Label "Mot de passe"
        JLabel passwordLabel = new JLabel("Mot de passe");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 40, 5, 40);
        panel.add(passwordLabel, gbc);

        // Panel mot de passe avec oeil
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setBackground(LIGHT_GRAY);
        passwordPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBackground(LIGHT_GRAY);
        passwordField.setBorder(BorderFactory.createEmptyBorder());

        // Ajouter l'action "Enter" pour se connecter
        passwordField.addActionListener(e -> handleLogin());

        passwordPanel.add(passwordField, BorderLayout.CENTER);

        // Bouton oeil avec images
        JButton togglePasswordButton = createEyeButton();
        passwordPanel.add(togglePasswordButton, BorderLayout.EAST);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 40, 30, 40);
        panel.add(passwordPanel, gbc);

        // Label d'erreur (caché par défaut)
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        errorLabel.setForeground(new Color(244, 67, 54)); // Rouge
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 40, 10, 40);
        panel.add(errorLabel, gbc);

        // Bouton SE CONNECTER
        JButton loginButton = createStyledButton("SE CONNECTER", BUTTON_COLOR);
        loginButton.setPreferredSize(new Dimension(300, 45));
        loginButton.addActionListener(e -> handleLogin());
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 40, 20, 40);
        panel.add(loginButton, gbc);

        // Séparateur OU
        JLabel orLabel = new JLabel("OU", SwingConstants.CENTER);
        orLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        orLabel.setForeground(Color.GRAY);
        gbc.gridy = 7;
        gbc.insets = new Insets(20, 40, 20, 40);
        panel.add(orLabel, gbc);

        // Bouton Créer un compte
        JButton createAccountButton = createOutlineButton("Créer un compte");
        createAccountButton.setPreferredSize(new Dimension(300, 45));
        createAccountButton.addActionListener(e -> handleCreateAccount());
        gbc.gridy = 8;
        gbc.insets = new Insets(10, 40, 40, 40);
        panel.add(createAccountButton, gbc);

        return panel;
    }

    private JButton createEyeButton() {
        JButton button = new JButton();
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setBackground(LIGHT_GRAY);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Charger les images
        ImageIcon eyeOpenIcon = loadIcon(EYE_OPEN_PATH, 20, 20);
        ImageIcon eyeClosedIcon = loadIcon(EYE_CLOSED_PATH, 20, 20);

        // Utiliser directement les images
        button.setIcon(eyeClosedIcon);

        button.addActionListener(e -> {
            if (passwordField.getEchoChar() == (char) 0) {
                // Masquer le mot de passe
                passwordField.setEchoChar('•');
                button.setIcon(eyeClosedIcon);
            } else {
                // Afficher le mot de passe
                passwordField.setEchoChar((char) 0);
                button.setIcon(eyeOpenIcon);
            }
        });

        return button;
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
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createOutlineButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(new Color(88, 70, 150, 30));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(88, 70, 150, 15));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                }

                g2d.setColor(BUTTON_COLOR);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 25, 25);
                g2d.dispose();

                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setForeground(BUTTON_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Gère la connexion utilisateur en faisant appel à {@code UserService.login()}.
     * Affiche une erreur dans {@code errorLabel} s'il y en a une.
     */
    private void handleLogin() {
        String username = usernameField.getText();
        char[] password = passwordField.getPassword();

        // Réinitialiser le message d'erreur
        errorLabel.setText(" ");

        try {
            ServiceManager
                    .getUserService()
                    .login(username, password);
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }

    }

    private void handleCreateAccount() {
        errorLabel.setText(" ");
        mainFrame.showPage("register");
    }
}