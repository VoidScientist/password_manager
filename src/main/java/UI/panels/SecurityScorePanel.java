package UI.panels;

import Entities.Profile;
import Entities.UserProfile;
import Managers.ServiceManager;
import Managers.SessionManager;
import Managers.Interface.SessionListener;
import Utilities.Security.Password.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SecurityScorePanel extends JPanel implements SessionListener {

    private static final Color PURPLE_BG = new Color(88, 70, 150);
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);
    private static final Color SCORE_EXCELLENT = new Color(76, 209, 55);  // Vert
    private static final Color SCORE_GOOD = new Color(156, 204, 101);     // Vert clair
    private static final Color SCORE_MEDIUM = new Color(255, 193, 7);     // Jaune/Orange
    private static final Color SCORE_POOR = new Color(255, 152, 0);       // Orange
    private static final Color SCORE_BAD = new Color(244, 67, 54);        // Rouge

    private int globalScore = 0;
    private List<SecurityWarning> warnings = new ArrayList<>();

    private UserProfile userProfile;  // Profil utilisateur contenant tous les profils
    private List<Profile> profiles = new ArrayList<>();  // Liste des profils à analyser

    private JPanel contentPanel;  // Panel qui affichera soit l'état initial, soit les résultats
    private CardLayout cardLayout;

    private JPanel resultsPanel;  // Panel avec les résultats d'analyse
    private boolean analysisPerformed = false;

    public SecurityScorePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Charger le dictionnaire des mots de passe faibles
        WeakPasswordDictionary.load();

        // Panel principal avec padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Titre
        JLabel titleLabel = new JLabel("Score de sécurité");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // CardLayout pour switcher entre l'état initial et les résultats
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        // État initial : pas d'analyse
        JPanel initialPanel = createInitialPanel();
        contentPanel.add(initialPanel, "initial");

        // Panel de résultats (vide au départ, sera rempli après l'analyse)
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, "results");

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        SessionManager.addListener(this);

        add(mainPanel);
    }

    /**
     * Crée le panel initial avec le bouton "Analyser"
     */
    private JPanel createInitialPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // Spacer pour centrer verticalement
        panel.add(Box.createVerticalGlue());

        // Icône ou cercle vide
        JPanel emptyCirclePanel = createEmptyCircle();
        emptyCirclePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(emptyCirclePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Message d'invitation
        JLabel messageLabel = new JLabel("Cliquez sur le bouton pour analyser la sécurité de vos mots de passe");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        messageLabel.setForeground(Color.GRAY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(messageLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Bouton "Analyser ma sécurité"
        JButton analyzeButton = createAnalyzeButton();
        analyzeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(analyzeButton);

        // Spacer pour centrer verticalement
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    /**
     * Crée un cercle vide (état initial)
     */
    private JPanel createEmptyCircle() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = 220;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                // Cercle gris avec un point d'interrogation
                g2d.setColor(LIGHT_GRAY);
                g2d.fillOval(x, y, size, size);

                // Point d'interrogation
                g2d.setColor(Color.GRAY);
                g2d.setFont(new Font("Arial", Font.BOLD, 100));
                String text = "?";
                FontMetrics fm = g2d.getFontMetrics();
                int textX = x + (size - fm.stringWidth(text)) / 2;
                int textY = y + (size - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(text, textX, textY);

                g2d.dispose();
            }
        };

        panel.setPreferredSize(new Dimension(400, 250));
        panel.setMaximumSize(new Dimension(400, 250));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    /**
     * Crée le bouton "Analyser ma sécurité"
     */
    private JButton createAnalyzeButton() {
        JButton button = new JButton(analysisPerformed ? "Recalculer le score" : "Analyser ma sécurité") {
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

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2d.dispose();

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(280, 50));
        button.setMaximumSize(new Dimension(280, 50));

        button.addActionListener(e -> performAnalysis());

        return button;
    }

    /**
     * Effectue l'analyse de sécurité
     */
    private void performAnalysis() {
        try {
            // 1. Charger les profils depuis le backend
            loadProfilesFromBackend();

            // 2. Calculer le score et les warnings
            calculateSecurityScore();

            // 3. Mettre à jour l'affichage
            updateResultsPanel();

            // 4. Passer à la vue des résultats
            cardLayout.show(contentPanel, "results");

            analysisPerformed = true;

        } catch (Exception e) {
            System.err.println("ERREUR lors de l'analyse de sécurité: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Met à jour le panel de résultats avec les données calculées
     */
    private void updateResultsPanel() {
        resultsPanel.removeAll();

        // Cercle de score
        JPanel scoreCirclePanel = createScoreCircle();
        scoreCirclePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultsPanel.add(scoreCirclePanel);
        resultsPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Bouton pour recalculer
        JButton recalculateButton = createAnalyzeButton();
        recalculateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultsPanel.add(recalculateButton);
        resultsPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Liste des warnings
        if (!warnings.isEmpty()) {
            JPanel warningsPanel = createWarningsPanel();
            warningsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            resultsPanel.add(warningsPanel);
        } else {
            JLabel noWarningsLabel = new JLabel("Bravo ! Votre sécurité est excellente.");
            noWarningsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            noWarningsLabel.setForeground(SCORE_EXCELLENT);
            noWarningsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            resultsPanel.add(noWarningsLabel);
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private void loadProfilesFromBackend() {
        try {
            profiles = ServiceManager.getDataService().getProfiles();
            System.out.println(profiles.size() + " profils chargés pour l'analyse");
        } catch (Exception e) {
            System.err.println("ERREUR lors du chargement des profils: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Impossible de charger les profils", e);
        }
    }

    private void calculateSecurityScore() {
        warnings.clear();
        int totalScore = 100;
        int passwordCount = profiles.size();

        if (passwordCount == 0) {
            globalScore = 0;
            warnings.add(new SecurityWarning(
                    SecurityLevel.WARNING,
                    "Aucun mot de passe à analyser",
                    "Ajoutez des comptes dans votre coffre-fort"
            ));
            return;
        }

        System.out.println("Analyse de " + passwordCount + " mots de passe...");

        // 1. Analyser la robustesse moyenne des mots de passe
        double totalStrength = 0;
        List<String> weakPasswords = new ArrayList<>();
        List<String> mediumPasswords = new ArrayList<>();

        for (Profile profile : profiles) {
            String password = profile.getPassword();
            if (password == null || password.isEmpty()) continue;

            try {
                PasswordStrength strength = new PasswordStrength(password);
                totalStrength += strength.getLevel();

                if (strength.getLevel() <= 2) {
                    weakPasswords.add(profile.getService());
                } else if (strength.getLevel() == 3) {
                    mediumPasswords.add(profile.getService());
                }
            } catch (Exception e) {
                System.err.println("Erreur analyse force pour " + profile.getService() + ": " + e.getMessage());
            }
        }

        double averageStrength = totalStrength / passwordCount;
        System.out.println("Force moyenne: " + averageStrength);

        // Pénalité pour robustesse faible
        if (averageStrength < 2.5) {
            totalScore -= 30;
        } else if (averageStrength < 3.5) {
            totalScore -= 15;
        }

        // 2. Détecter les mots de passe identiques (réutilisation)
        Map<String, List<String>> passwordMap = new HashMap<>();
        for (Profile profile : profiles) {
            String password = profile.getPassword();
            if (password == null || password.isEmpty()) continue;

            passwordMap.computeIfAbsent(password, k -> new ArrayList<>()).add(profile.getService());
        }

        for (Map.Entry<String, List<String>> entry : passwordMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                totalScore -= 15 * (entry.getValue().size() - 1);
                warnings.add(new SecurityWarning(
                        SecurityLevel.CRITICAL,
                        entry.getValue().size() + " comptes utilisent le même mot de passe !",
                        String.join(", ", entry.getValue())
                ));
            }
        }

        // 3. Ajouter les warnings pour mots de passe faibles
        if (!weakPasswords.isEmpty()) {
            totalScore -= 10 * weakPasswords.size();
            warnings.add(new SecurityWarning(
                    SecurityLevel.CRITICAL,
                    weakPasswords.size() + " mot(s) de passe très faible(s) détecté(s)",
                    String.join(", ", weakPasswords)
            ));
        }

        if (!mediumPasswords.isEmpty()) {
            warnings.add(new SecurityWarning(
                    SecurityLevel.WARNING,
                    mediumPasswords.size() + " mot(s) de passe de robustesse moyenne",
                    String.join(", ", mediumPasswords)
            ));
        }

        // 4. Vérifier les mots de passe dans le dictionnaire (mots courants)
        List<String> dictionaryPasswords = new ArrayList<>();
        for (Profile profile : profiles) {
            String password = profile.getPassword();
            if (password == null || password.isEmpty()) continue;

            try {
                if (WeakPasswordDictionary.isWeak(password)) {
                    dictionaryPasswords.add(profile.getService());
                }
            } catch (Exception e) {
                System.err.println("Erreur vérif dictionnaire pour " + profile.getService() + ": " + e.getMessage());
            }
        }

        if (!dictionaryPasswords.isEmpty()) {
            totalScore -= 20;
            warnings.add(new SecurityWarning(
                    SecurityLevel.CRITICAL,
                    "Mot(s) de passe courant(s) détecté(s)",
                    String.join(", ", dictionaryPasswords)
            ));
        }

        // 5. Détecter les patterns similaires entre mots de passe
        List<String> similarPatterns = detectSimilarPatterns();
        if (!similarPatterns.isEmpty()) {
            warnings.add(new SecurityWarning(
                    SecurityLevel.WARNING,
                    "Mots de passe suivant des schémas similaires détectés",
                    String.join(", ", similarPatterns)
            ));
        }

        // 6. Vérifier la longueur minimale (moins de 8 caractères)
        List<String> shortPasswords = new ArrayList<>();
        for (Profile profile : profiles) {
            String password = profile.getPassword();
            if (password == null) continue;

            if (password.length() < 8) {
                shortPasswords.add(profile.getService());
            }
        }

        if (!shortPasswords.isEmpty()) {
            totalScore -= 10;
            warnings.add(new SecurityWarning(
                    SecurityLevel.WARNING,
                    "Mot(s) de passe trop court(s) (moins de 8 caractères)",
                    String.join(", ", shortPasswords)
            ));
        }

        // Score final entre 0 et 100
        globalScore = Math.max(0, Math.min(100, totalScore));

        // Trier les warnings : CRITICAL en premier, puis WARNING
        warnings.sort((w1, w2) -> {
            if (w1.level == w2.level) return 0;
            return w1.level == SecurityLevel.CRITICAL ? -1 : 1;
        });

        System.out.println("Score calculé: " + globalScore + "/100");
        System.out.println(warnings.size() + " avertissements détectés");
    }

    private List<String> detectSimilarPatterns() {
        Map<String, List<String>> patternMap = new HashMap<>();

        for (Profile profile : profiles) {
            String password = profile.getPassword();
            if (password == null || password.isEmpty()) continue;

            String pattern = extractPattern(password);
            patternMap.computeIfAbsent(pattern, k -> new ArrayList<>()).add(profile.getService());
        }

        // Chercher les patterns utilisés 3 fois ou plus
        List<String> similarAccounts = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : patternMap.entrySet()) {
            if (entry.getValue().size() >= 3) {
                similarAccounts.addAll(entry.getValue());
            }
        }

        return similarAccounts;
    }

    private String extractPattern(String password) {
        StringBuilder pattern = new StringBuilder();

        if (password.isEmpty()) return "";

        char prevType = getCharType(password.charAt(0));
        int count = 1;

        for (int i = 1; i < password.length(); i++) {
            char currentType = getCharType(password.charAt(i));
            if (currentType == prevType) {
                count++;
            } else {
                pattern.append(prevType);
                prevType = currentType;
                count = 1;
            }
        }
        pattern.append(prevType);

        return pattern.toString();
    }

    private char getCharType(char c) {
        if (Character.isDigit(c)) return 'D';
        if (Character.isLowerCase(c)) return 'L';
        if (Character.isUpperCase(c)) return 'U';
        return 'S'; // Symbole
    }

    private JPanel createScoreCircle() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = 220;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                // Cercle coloré selon le score
                g2d.setColor(getScoreColor(globalScore));
                g2d.fillOval(x, y, size, size);

                // Score
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 80));
                String scoreText = String.valueOf(globalScore);
                FontMetrics fm = g2d.getFontMetrics();
                int textX = x + (size - fm.stringWidth(scoreText)) / 2;
                int textY = y + (size - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(scoreText, textX, textY);

                // Label "Score sécurité"
                g2d.setFont(new Font("Arial", Font.PLAIN, 14));
                String label = "Score sécurité";
                fm = g2d.getFontMetrics();
                textX = x + (size - fm.stringWidth(label)) / 2;
                textY = textY + 30;
                g2d.drawString(label, textX, textY);

                g2d.dispose();
            }
        };

        panel.setPreferredSize(new Dimension(400, 250));
        panel.setMaximumSize(new Dimension(400, 250));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    private Color getScoreColor(int score) {
        if (score >= 90) return SCORE_EXCELLENT;
        if (score >= 75) return SCORE_GOOD;
        if (score >= 60) return SCORE_MEDIUM;
        if (score >= 40) return SCORE_POOR;
        return SCORE_BAD;
    }

    private JPanel createWarningsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(800, Integer.MAX_VALUE));

        for (SecurityWarning warning : warnings) {
            JPanel warningCard = createWarningCard(warning);
            warningCard.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(warningCard);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return panel;
    }

    private JPanel createWarningCard(SecurityWarning warning) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(800, 70));

        // Icône de warning
        JLabel iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(40, 40));
        iconLabel.setOpaque(true);
        iconLabel.setBackground(warning.level == SecurityLevel.CRITICAL ? SCORE_BAD : new Color(255, 152, 0));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        iconLabel.setFont(new Font("Arial", Font.BOLD, 24));
        iconLabel.setText("!");
        iconLabel.setForeground(Color.WHITE);
        card.add(iconLabel, BorderLayout.WEST);

        // Message
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(Color.WHITE);

        JLabel messageLabel = new JLabel(warning.message);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel affectedLabel = new JLabel(warning.affectedAccounts);
        affectedLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        affectedLabel.setForeground(Color.GRAY);

        messagePanel.add(messageLabel);
        messagePanel.add(Box.createRigidArea(new Dimension(0, 3)));
        messagePanel.add(affectedLabel);
        card.add(messagePanel, BorderLayout.CENTER);

        return card;
    }

    @Override
    public void onLogin() {
        // Réinitialiser à l'état initial au login
        analysisPerformed = false;
        cardLayout.show(contentPanel, "initial");

        System.out.println("SecurityScorePanel prêt");
    }

    @Override
    public void onDisconnect() {
        // Clear les données
        profiles.clear();
        warnings.clear();
        globalScore = 0;
        analysisPerformed = false;
        cardLayout.show(contentPanel, "initial");
    }

    private static class SecurityWarning {
        SecurityLevel level;
        String message;
        String affectedAccounts;

        public SecurityWarning(SecurityLevel level, String message, String affectedAccounts) {
            this.level = level;
            this.message = message;
            this.affectedAccounts = affectedAccounts;
        }
    }

    private enum SecurityLevel {
        CRITICAL,  // Rouge - Problème grave
        WARNING    // Orange - Avertissement
    }
}